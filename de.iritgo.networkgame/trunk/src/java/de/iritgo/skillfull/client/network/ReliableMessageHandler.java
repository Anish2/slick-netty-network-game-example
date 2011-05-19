
package de.iritgo.skillfull.client.network;


import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.ReliableAckMessage;
import de.iritgo.skillfull.network.ReliableMessage;


public class ReliableMessageHandler extends SimpleChannelUpstreamHandler
{
	private Network network;

	private int reliableId = 0;

	public ReliableMessageHandler (Network network)
	{
		this.network = network;
	}

	@Override
	public void messageReceived (ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		if (e.getMessage () instanceof ReliableMessage)
		{

			ReliableMessage message = (ReliableMessage) e.getMessage ();
			if (message.getReliableId () <= reliableId)
			{
				e.getChannel ().write (
								new Message ().addMessage (new ReliableAckMessage (message.getReliableId ())
												.withClientId (message.getClientId ())), e.getRemoteAddress ());
			}
			// else if ((message.getReliableId () >= reliableId + 2))
			// {
			// // Wow future message, we ignore comlete
			// }
			else if ((message.getReliableId () > reliableId))
			{
				// New message we work on this
				Channels.fireMessageReceived (e.getChannel (), message.getMessageBuffer (), e.getRemoteAddress ());
				reliableId = message.getReliableId ();
				e.getChannel ().write (
								new Message ().addMessage (new ReliableAckMessage (message.getReliableId ())
												.withClientId (message.getClientId ())), e.getRemoteAddress ());
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
