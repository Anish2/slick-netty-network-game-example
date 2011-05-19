
package de.iritgo.skillfull.server.network;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

import de.iritgo.skillfull.chat.decoder.ChatMessageDecoder;
import de.iritgo.skillfull.network.CommonNetwork;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.MessageEncoder;
import de.iritgo.skillfull.network.Options;
import de.iritgo.skillfull.network.ReliableAckMessage;
import de.iritgo.skillfull.network.ReliableMessage;
import de.iritgo.skillfull.network.SequenceAckMessage;
import de.iritgo.skillfull.network.SequenceMessage;
import de.iritgo.skillfull.network.decoder.ClockMessageDecoder;
import de.iritgo.skillfull.network.decoder.MessageFrameDecoder;
import de.iritgo.skillfull.network.decoder.PingPongMessageDecoder;
import de.iritgo.skillfull.network.decoder.ReliableAckMessageDecoder;
import de.iritgo.skillfull.network.decoder.ReliableMessageDecoder;
import de.iritgo.skillfull.network.decoder.SequenceAckMessageDecoder;
import de.iritgo.skillfull.network.decoder.SequenceMessageDecoder;
import de.iritgo.skillfull.network.handler.PingPongMessageHandler;
import de.iritgo.skillfull.server.Server;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.user.network.LoginRequestMessage;
import de.iritgo.skillfull.user.network.decoder.UserMessageDecoder;


public class Network implements CommonNetwork
{
	private HashMap<User, BlockingQueue<Message>> reliableMessages;

	private ConcurrentHashMap<String, SocketAddress> clientIdRemoteAddressMappings = new ConcurrentHashMap<String, SocketAddress> ();

	private BlockingQueue<Message> receivedMessages = new ArrayBlockingQueue<Message> (500, true);

	private HashMap<User, ArrayList<Message>> userMessagesPerTick = new HashMap<User, ArrayList<Message>> ();

	private NioDatagramChannelFactory factory;

	private ConnectionlessBootstrap bootstrap;

	private DatagramChannel channel;

	private InetSocketAddress dstAddress;

	private ConcurrentHashMap<User, Integer> userReliableIds = new ConcurrentHashMap<User, Integer> ();

	private ConcurrentHashMap<User, Integer> userSequenceIds = new ConcurrentHashMap<User, Integer> ();

	private volatile ConcurrentHashMap<User, ArrayList<Message>> snapshotDeltaCache = new ConcurrentHashMap<User, ArrayList<Message>> ();

	private Server server;

	private SequenceMessage compressedMessagePerTick;

	private Object allCollectedMessages;

	public static int sendMessages;

	public void init (final Server server, String receiveIPAddress, int receivePort)
	{
		reliableMessages = new HashMap<User, BlockingQueue<Message>> ();
		this.server = server;
		factory = new NioDatagramChannelFactory (Executors.newCachedThreadPool ());
		bootstrap = new ConnectionlessBootstrap (factory);
		final Network network = this;
		ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory ()
		{

			@Override
			public ChannelPipeline getPipeline () throws Exception
			{
				ChannelPipeline pipeline = Channels.pipeline ();
				pipeline.addLast ("encoder", new MessageEncoder ());
				pipeline.addLast ("decoder-message", new MessageFrameDecoder ());
				pipeline.addLast ("decoder-sequence", new SequenceMessageDecoder ());
				pipeline.addLast ("decoder-reliable", new ReliableMessageDecoder ());
				pipeline.addLast ("decoder-reliable-ack", new ReliableAckMessageDecoder ());
				pipeline.addLast ("decoder-sequence-ack", new SequenceAckMessageDecoder ());
				pipeline.addLast ("decoder-login", new UserMessageDecoder ());
				pipeline.addLast ("decoder-clock", new ClockMessageDecoder ());
				pipeline.addLast ("decoder-pingpong", new PingPongMessageDecoder ());
				pipeline.addLast ("decoder-chatmessage", new ChatMessageDecoder ());
				pipeline.addLast ("handler-clock", new ClockMessageHandler (server));
				pipeline.addLast ("handler-reliable-ack", new ReliableAckMessageHandler (network));
				pipeline.addLast ("handler-pingpong", new PingPongMessageHandler (null, true));
				pipeline.addLast ("handler-reliable", new ReliableMessageHandler (network));
				pipeline.addLast ("handler-sequence-ack", new SequenceAckMessageHandler (network));
				pipeline.addLast ("handler-sequence", new SequenceMessageHandler (network));
				pipeline.addLast ("handler-message", new CommonMessageHandler (network));
				return pipeline;
			}

		};

		bootstrap.setPipelineFactory (pipelineFactory);
		bootstrap.setOption ("broadcast", "false");
		bootstrap.setOption ("receiveBufferSizePredictorFactory", new FixedReceiveBufferSizePredictorFactory (
						Options.DEFAULT_UDP_PAYLOAD_SIZE));
		channel = (DatagramChannel) bootstrap.bind (new InetSocketAddress (receiveIPAddress, receivePort));
	}

	public void sendMessages (User user)
	{
		Integer sequenceId = userSequenceIds.get (user);
		if (sequenceId == null)
		{
			userSequenceIds.put (user, 1);
			sequenceId = 1;
		}
		ArrayList<Message> messages = userMessagesPerTick.get (user);
		if (messages == null)
			return;

		BlockingQueue<Message> reliableQueue = reliableMessages.get (user);
		if (reliableQueue == null)
		{
			reliableQueue = new ArrayBlockingQueue<Message> (150, true);
			reliableMessages.put (user, reliableQueue);
		}

		Integer currentReliableId = userReliableIds.get (user);
		if (currentReliableId == null)
		{
			userReliableIds.put (user, 1);
			currentReliableId = 1;
		}

		Message networkMessage = new Message ();
		networkMessage.setClientId (user.getClientId ());
		SequenceMessage sequenceMessage = null;

		HashMap<Integer, HashMap<Integer, SequenceMessage>> seqMessages = new HashMap<Integer, HashMap<Integer, SequenceMessage>> ();
		ArrayList<Message> newSequenceMessages = new ArrayList<Message> ();

		int messageCount = 0;
		int subSequenceId = 0;
		int messageAddedCount = 0;
		int messageSize = messages.size ();
		boolean forceSend = false;

		// System.out.println ("Started sending sequences..." + user.getId () +
		// "/" + messageSize);
		while (messages.size () > 0)
		{
			Message message = messages.remove (0);
			if (message.getMessageSize () + getMessageSize (reliableQueue.peek (), newSequenceMessages, seqMessages) >= CommonNetwork.MAX_PACKET_SIZE)
			{
				messages.add (0, message);
				forceSend = true;
			}
			else
			{
				// System.out.println ("Send Message: " + message.getOpcode () +
				// " / " + user.getId ());
				if (message.isSequenceMessage ())
				{
					if (message.getSequenceId () != 0)
					{
						HashMap<Integer, SequenceMessage> subSeqMessages = seqMessages.get (message.getSequenceId ());
						if (subSeqMessages == null)
						{
							subSeqMessages = new HashMap<Integer, SequenceMessage> ();
							seqMessages.put (message.getSequenceId (), subSeqMessages);
						}

						SequenceMessage seqMessage = subSeqMessages.get (message.getSubSequenceId ());
						if (seqMessage == null)
						{
							seqMessage = (SequenceMessage) new SequenceMessage ().withClientId (user.getClientId ());
							seqMessage.setSequenceId (message.getSequenceId ());
							seqMessage.setSubSequenceId (message.getSubSequenceId ());
							subSeqMessages.put (message.getSubSequenceId (), seqMessage);
							++messageCount;
						}
						if (message.getSubSequenceId () != 0)
						{
							int count = 0;
							for (int q = 0; q < messages.size (); ++q)
							{
								Message subSeqMessage = messages.get (q);

								if ((subSeqMessage.getSubSequenceId () == message.getSubSequenceId ())
												&& (subSeqMessage.getSequenceId () == message.getSequenceId ()))
								{
									count += subSeqMessage.getMessageSize ();
								}
							}
							if (count + getMessageSize (reliableQueue.peek (), newSequenceMessages, seqMessages) >= CommonNetwork.MAX_PACKET_SIZE)
							{
								messages.add (0, message);
								forceSend = true;
							}
							else
							{
								// SubSequence block sended, we must stay
								// together
								ArrayList<Message> tmpBag = new ArrayList<Message> ();
								tmpBag.addAll (messages);

								seqMessage.addMessage (message);
								++messageCount;
								++messageAddedCount;
								count = 0;
								for (int q = 0; q < tmpBag.size (); ++q)
								{
									Message subSeqMessage = tmpBag.get (q);
									if ((subSeqMessage.getSubSequenceId () == message.getSubSequenceId ())
													&& (subSeqMessage.getSequenceId () == message.getSequenceId ()))
									{
										seqMessage.addMessage (subSeqMessage);
										messages.remove (subSeqMessage);
										++messageAddedCount;
										++count;
									}
								}
								messageCount += count;
							}
						}
					}
					else
					{
						if (sequenceMessage == null)// || sequenceMessage.getNumberOfMessages () >= 10)
						{
							++messageCount;
							++subSequenceId;
							sequenceMessage = new SequenceMessage ();
							sequenceMessage.setClientId (user.getClientId ());
							sequenceMessage.setSequenceId (sequenceId);
							sequenceMessage.setSubSequenceId (subSequenceId);
							newSequenceMessages.add (sequenceMessage);
						}
						message.setSequenceId (sequenceId);
						message.setSubSequenceId (subSequenceId);

						sequenceMessage.addMessage (message);
						++messageCount;
						++messageAddedCount;

						if (message.isKeepForSnapshotDelta ())
						{
							ArrayList<Message> userSeqMes = snapshotDeltaCache.get (user);
							if (userSeqMes == null)
							{
								userSeqMes = new ArrayList<Message> ();
								snapshotDeltaCache.put (user, userSeqMes);
							}
							++sendMessages;
							synchronized (userSeqMes)
							{
								userSeqMes.add (message);
							}
						}
					}
				}
				else if (message.isReliableMessage ())
				{
					Message reliableMessage = new ReliableMessage (currentReliableId)
									.withClientId (user.getClientId ());
					reliableMessage.addMessage (message);
					userReliableIds.put (user, ++currentReliableId);

					reliableQueue.add (reliableMessage);
					++messageCount;
					++messageAddedCount;
				}
				else
				{
					networkMessage.addMessage (message);
					++messageCount;
					++messageAddedCount;
				}
			}
			if (forceSend)
			{
				forceSend = false;
				messageCount = 0;
				sequenceMessage = null;
				sendCollectedMessages (user, reliableQueue, networkMessage, newSequenceMessages, seqMessages);

				newSequenceMessages.clear ();
				networkMessage = new Message ();
				networkMessage.setClientId (user.getClientId ());
			}
		}

		if (messageCount > 0)
		{
			sendCollectedMessages (user, reliableQueue, networkMessage, newSequenceMessages, seqMessages);
		}
		userSequenceIds.put (user, ++sequenceId);
	}

	private int getMessageSize (Message reliableMessage, ArrayList<Message> newSequenceMessages,
					HashMap<Integer, HashMap<Integer, SequenceMessage>> seqMessages)
	{
		int messageSize = 0;
		if (newSequenceMessages != null)
			for (Message message : newSequenceMessages)
				if (message.getNumberOfMessages () != 0)
					messageSize += message.getMessageSize ();

		for (HashMap<Integer, SequenceMessage> oldSeqMessages : seqMessages.values ())
			for (SequenceMessage oldSeqMessage : oldSeqMessages.values ())
				if (oldSeqMessage.getNumberOfMessages () != 0)
					messageSize += oldSeqMessage.getMessageSize ();

		if (reliableMessage != null)
			messageSize += reliableMessage.getMessageSize ();
		return messageSize + 20;
	}

	private void sendCollectedMessages (User user, BlockingQueue<Message> reliableQueue, Message networkMessage,
					ArrayList<Message> newSequenceMessages,
					HashMap<Integer, HashMap<Integer, SequenceMessage>> seqMessages)
	{
		if (reliableQueue.size () > 0)
		{
			networkMessage.addMessage (reliableQueue.peek ());
		}
		for (HashMap<Integer, SequenceMessage> oldSeqMessages : seqMessages.values ())
		{
			for (SequenceMessage oldSeqMessage : oldSeqMessages.values ())
			{
				if (oldSeqMessage.getNumberOfMessages () != 0)
				{
					networkMessage.addMessage (oldSeqMessage);
				}
			}
		}

		for (Message sequenceMessage : newSequenceMessages)
		{
			if (sequenceMessage.getNumberOfMessages () != 0)
			{
				networkMessage.addMessage (sequenceMessage);
			}
		}

		sendMessage (networkMessage, clientIdRemoteAddressMappings.get (user.getNetworkId ()));

		seqMessages.clear ();
	}

	private void sendMessage (Message message, SocketAddress socketAddress)
	{
		channel.write (message, socketAddress);
	}

	public void sendReliableMessage (User user, Message message)
	{
	}

	public void receivedMessage (Message message)
	{
		receivedMessages.add (message);
	}

	public Message getNewMessage ()
	{
		return receivedMessages.poll ();
	}

	public void reliableAckMessageReceived (ReliableAckMessage ack)
	{
		User user = server.getUserByNetworkId (ack.getNetworkId ());

		BlockingQueue<Message> reliableQueue = reliableMessages.get (user);
		if (reliableQueue != null && reliableQueue.size () > 0)
		{
			Message message = reliableQueue.peek ();

			if (message != null && ((ReliableMessage) message).getReliableId () == ack.getReliableId ())
			{
				reliableQueue.remove ();
				System.out.println ("Server-Nachricht am Client angekommen!");
			}
		}
	}

	public void addClientIdRemoteAddressMapping (SocketAddress remoteAddress, LoginRequestMessage login)
	{
		clientIdRemoteAddressMappings.put (getClientId (remoteAddress, login), remoteAddress);
	}

	public String getClientId (SocketAddress remoteAddress, Message message)
	{
		return remoteAddress.hashCode () + "/" + message.getClientId ();
	}

	@Override
	public void sendReliableMessage (Message message)
	{
		// for the client
	}

	@Override
	public void sendCompressedMessage (Message message)
	{
		// for the client
	}

	public void sendMessage (User user, Message message)
	{
		ArrayList<Message> messages = userMessagesPerTick.get (user);
		if (messages == null)
		{
			messages = new ArrayList<Message> ();
			userMessagesPerTick.put (user, messages);
		}

		messages.add (message.cloneMessage ());
	}

	public void sequenceAckMessageReceived (SequenceAckMessage message)
	{
		String networkId = message.getNetworkId ();
		User user = server.getUserByNetworkId (networkId);
		ArrayList<Message> seqMessages = snapshotDeltaCache.get (user);
		for (Message seq : new ArrayList<Message> (seqMessages))
		{
			if ((seq.getSequenceId () == message.getSequenceId ())
							&& (seq.getSubSequenceId () == message.getSubSequenceId ()))
			{
				synchronized (seqMessages)
				{
					seqMessages.remove (seq);
				}
			}
		}
	}

	public void clearUserCompressedMessagePerTick ()
	{
		userMessagesPerTick.clear ();
		for (User user : snapshotDeltaCache.keySet ())
		{
			ArrayList<Message> messages = snapshotDeltaCache.get (user);
			synchronized (messages)
			{
				for (Message message : messages)
				{
					sendMessage (user, message);
				}
			}
		}
	}

	@Override
	public void sendCompressedMessage (User user, Message entityPos)
	{
	}
}
