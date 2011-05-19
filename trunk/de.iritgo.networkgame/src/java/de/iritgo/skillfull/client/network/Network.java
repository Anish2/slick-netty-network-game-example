package de.iritgo.skillfull.client.network;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
import de.iritgo.skillfull.client.Client;
import de.iritgo.skillfull.entity.network.decoder.EntityMessageDecoder;
import de.iritgo.skillfull.entity.network.handler.EntityMessageHandler;
import de.iritgo.skillfull.network.CommonNetwork;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.MessageEncoder;
import de.iritgo.skillfull.network.Options;
import de.iritgo.skillfull.network.ReliableAckMessage;
import de.iritgo.skillfull.network.ReliableMessage;
import de.iritgo.skillfull.network.SequenceAckMessage;
import de.iritgo.skillfull.network.decoder.ClockMessageDecoder;
import de.iritgo.skillfull.network.decoder.MessageFrameDecoder;
import de.iritgo.skillfull.network.decoder.PingPongMessageDecoder;
import de.iritgo.skillfull.network.decoder.ReliableAckMessageDecoder;
import de.iritgo.skillfull.network.decoder.ReliableMessageDecoder;
import de.iritgo.skillfull.network.decoder.SequenceAckMessageDecoder;
import de.iritgo.skillfull.network.decoder.SequenceMessageDecoder;
import de.iritgo.skillfull.network.handler.PingPongMessageHandler;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.user.network.decoder.UserMessageDecoder;

public class Network implements CommonNetwork
{
	private BlockingQueue<Message> reliableMessages = new LinkedBlockingQueue<Message> ();

	private BlockingQueue<Message> receivedMessages = new ArrayBlockingQueue<Message> (25000, true);

	private volatile int currentSequenceId = 0;

	private volatile int currentReliableId = 0;

	private NioDatagramChannelFactory factory;

	private ConnectionlessBootstrap bootstrap;

	private DatagramChannel channel;

	private InetSocketAddress dstAddress;

	private byte clientId;

	private int sequenceId;

	private Client client;

	private ReliableMessage reliableMessagesPerTick;

	public int getCurrentSequenceId ()
	{
		return currentSequenceId;
	}

	public void init (final Client client, String receiveIPAddress, int receivePort, String dstIPAddress, int dstPort, int clientId)
	{
		this.dstAddress = new InetSocketAddress (dstIPAddress, dstPort);
		this.clientId = (byte) clientId;
		this.client = client;

		factory = new NioDatagramChannelFactory (Executors.newCachedThreadPool ());
		bootstrap = new ConnectionlessBootstrap (factory);
		final Network network = this;
		bootstrap.setPipelineFactory (new ChannelPipelineFactory ()
		{
			public ChannelPipeline getPipeline () throws Exception
			{
				return Channels.pipeline (new MessageEncoder (),
								new MessageFrameDecoder (),
								new PingPongMessageDecoder (),
								new PingPongMessageHandler (null, false),
								new ClockMessageDecoder (),
								new ClockMessageHandler (client),
								new SequenceAckMessageDecoder (),
								new SequenceAckMessageHandler (network),
								new ReliableAckMessageDecoder (),
								new ReliableAckMessageHandler (network),
								new ReliableMessageDecoder (),
								new ReliableMessageHandler (network),
								new SequenceMessageDecoder (),
								new SequenceMessageHandler (network),
								new EntityMessageDecoder (),
								new UserMessageDecoder (),
								new ChatMessageDecoder (),
								new EntityMessageHandler (network),
								new CommonMessageHandler (network)
				);
			}
		});
		bootstrap.setOption ("broadcast", "false");
		bootstrap.setOption ("receiveBufferSizePredictorFactory", new FixedReceiveBufferSizePredictorFactory (Options.DEFAULT_UDP_PAYLOAD_SIZE));
		channel = (DatagramChannel) bootstrap.bind (new InetSocketAddress (receiveIPAddress, receivePort));
	}

	public void sendMessage (Message message)
	{
		if (message == null && reliableMessages.size () != 0)
		{
			Message sendMessage = new Message ().withClientId (clientId);
			int relCounter = 0;
			for (Message reliableMessage : reliableMessages)
			{
				if (reliableMessage.getMessageSize () + sendMessage.getMessageSize () >= CommonNetwork.MAX_PACKET_SIZE)
					break;
				
				sendMessage.addMessage (reliableMessage);
				relCounter += reliableMessage.getNumberOfMessages () + 1;
			}
			channel.write (sendMessage, dstAddress);
			return;
		}

		if (message == null)
		{
			return;
		}
		message.setClientId (clientId);
		if (reliableMessages.size () != 0)
		{
			message.addMessage (reliableMessages.peek ());
		}
		channel.write (new Message ().withClientId (clientId).andMessage (message), dstAddress);
	}

	public void sendReliableMessage (Message message)
	{
		message.setClientId (clientId);
		reliableMessagesPerTick.addMessage (message);
	}

	public void commitReliableMessagePerTick ()
	{
		if (reliableMessagesPerTick.getNumberOfMessages () > 0)
		{
			reliableMessages.add (reliableMessagesPerTick.withReliableId (++currentReliableId));
		}
	}

	public void newReliableMessagePerTick ()
	{
		reliableMessagesPerTick = (ReliableMessage) new ReliableMessage ().withClientId (clientId);
	}

	public void receivedMessage (Message message)
	{
		receivedMessages.add (message);
	}

	public void reliableAckMessageReceived (ReliableAckMessage ack)
	{
		Message message = reliableMessages.peek ();
		if (message != null && ((ReliableMessage)message).getReliableId () == ack.getReliableId ())
		{
			reliableMessages.remove ();
		}
	}

	public Message getNewMessage ()
	{
		return receivedMessages.poll ();
	}

	@Override
	public void sendReliableMessage (User user, Message message)
	{
		// for server
	}

	@Override
	public void sendCompressedMessage (Message message)
	{
	}

	@Override
	public void sendCompressedMessage (User user, Message entityPos)
	{
		// for server
	}

	public void sequenceAckMessageReceived (SequenceAckMessage message)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessage (User user, Message message)
	{
		// TODO Auto-generated method stub

	}
}
