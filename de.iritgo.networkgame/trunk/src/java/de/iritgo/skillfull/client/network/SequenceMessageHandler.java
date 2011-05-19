
package de.iritgo.skillfull.client.network;


import java.util.HashMap;
import java.util.LinkedList;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import de.iritgo.skillfull.client.Client;
import de.iritgo.skillfull.network.SequenceAckMessage;
import de.iritgo.skillfull.network.SequenceMessage;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.ReliableAckMessage;
import de.iritgo.skillfull.network.ReliableMessage;


public class SequenceMessageHandler extends SimpleChannelUpstreamHandler
{
	private Network network;

	private LinkedList<Integer> receivedSequences = new LinkedList<Integer> ();
//	private HashMap<Integer, LinkedList<Integer>> receivedSubSequences = new HashMap<Integer, LinkedList<Integer>> ();
	private HashMap<Integer, Integer> receivedSubSequences = new HashMap<Integer, Integer> ();

	public SequenceMessageHandler (Network network)
	{
		this.network = network;
		receivedSequences = new LinkedList<Integer> ();
	}

	@Override
	public void messageReceived (ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{

		if (e.getMessage () instanceof SequenceMessage)
		{
			SequenceMessage message = (SequenceMessage) e.getMessage ();
			synchronized (message)
			{
//			LinkedList<Integer> subSequences = receivedSubSequences.get (message.getSequenceId ());
//			if (subSequences == null)
//			{
//				subSequences = new LinkedList<Integer> ();
//				receivedSubSequences.put (message.getSequenceId (), subSequences);
//			}
			Integer subSequence = receivedSubSequences.get (message.getSequenceId ());
			if (subSequence == null)
			{
				subSequence = 0;
				receivedSubSequences.put (message.getSequenceId (), 0);
			}

//			System.out.print ("Locale SeqNum: " + subSequence + "Sequence: " + message.getSequenceId () + "/" + message.getSubSequenceId ());

//			if (receivedSequences.contains (message.getSequenceId ()) && subSequences.contains (message.getSubSequenceId ()))
			if (receivedSequences.contains (message.getSequenceId ()) && subSequence >= message.getSubSequenceId ())
			{
//				System.out.println ("...exists.");
//				System.out.println ("Alt: "+ message.getSequenceId () + ": "+ message.getSubSequenceId ());
				e.getChannel ().write (
								new Message ().addMessage (new SequenceAckMessage (message.getSequenceId (), message.getSubSequenceId ())
												.withClientId (message.getClientId ())), e.getRemoteAddress ());
			}
			else if (subSequence + 1 == message.getSubSequenceId ())
			{
//				System.out.println ("...new.");
				LinkedList<Message> removeM = new LinkedList<Message> ();
//				System.out.println ("Client-Empfangen: " + message.getSequenceId () + "/" + message.getSubSequenceId () + " Size: "+ ((SequenceMessage)message).getNumOfMessages ());

				receivedSubSequences.put (message.getSequenceId (), message.getSubSequenceId ());

//				subSequences.add (message.getSubSequenceId ());
//				if (subSequences.size () > 5000)
//				{
//					subSequences.removeFirst ();
//				}

				receivedSequences.add (message.getSequenceId ());
//				System.out.println ("Neu: "+ message.getSequenceId () + ": "+ message.getSubSequenceId ());
				if (receivedSequences.size () > 5000)
				{
					receivedSubSequences.remove (receivedSequences.removeFirst ());
				}
				// New message we work on this
				e.getChannel ().write (
								new Message ().addMessage (new SequenceAckMessage (message.getSequenceId (), message.getSubSequenceId ())
								.withClientId (message.getClientId ())), e.getRemoteAddress ());

				Channels.fireMessageReceived (e.getChannel (), message.getMessageBuffer (), e.getRemoteAddress ());
			}
//			else
			{
//				System.out.println (" subsequence number to high...");
			}
			}
		}
		else
		{
			super.messageReceived (ctx, e);
		}
	}

	@Override
	public void exceptionCaught (ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
	{
		e.getCause ().printStackTrace ();
	}
}
