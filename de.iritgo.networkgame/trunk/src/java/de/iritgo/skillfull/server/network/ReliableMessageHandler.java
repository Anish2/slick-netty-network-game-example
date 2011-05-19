package de.iritgo.skillfull.server.network;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.util.internal.ConcurrentHashMap;

import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.ReliableAckMessage;
import de.iritgo.skillfull.network.ReliableMessage;


public class ReliableMessageHandler extends SimpleChannelUpstreamHandler
{
	private Network network;
	private Map<String, Integer> reliableClientIds;

	public ReliableMessageHandler (Network network)
	{
		this.network = network;
		reliableClientIds = new ConcurrentHashMap<String, Integer> ();
	}

	@Override
	public void messageReceived (ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		if (e.getMessage () instanceof ReliableMessage)
		{
			ReliableMessage message = (ReliableMessage) e.getMessage ();
			String reliableClientIdKey = e.getRemoteAddress ().hashCode () + "/" + message.getClientId ();
			Integer reliableIdTmp = reliableClientIds.get (reliableClientIdKey);
			if (reliableIdTmp == null)
			{
				reliableClientIds.put (reliableClientIdKey, 1);
				reliableIdTmp = 1;
			}
			int reliableId = reliableIdTmp; 
			if (message.getReliableId () < reliableId)
			{
				e.getChannel ().write (new Message ().addMessage (new ReliableAckMessage (message.getReliableId ())), e.getRemoteAddress ());
			}
			else if ((message.getReliableId () > reliableId + 1))
			{
				// Wow future message, we ignore that complete
			}
			else if ((message.getReliableId () == reliableId))
			{
				// New message we work on this
				Channels.fireMessageReceived (e.getChannel (),
								message.getMessageBuffer (), e.getRemoteAddress ());
				
				reliableClientIds.put (reliableClientIdKey, message.getReliableId () + 1);
				e.getChannel ().write (new Message ().andMessage 
								(new ReliableAckMessage (message.getReliableId ()).withClientId (message.getClientId ())), e.getRemoteAddress ());
			}
		}
		else
		{
			super.messageReceived (ctx,e);
		}
	}

	@Override
	public void exceptionCaught (ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
	{
		e.getCause ().printStackTrace ();
	}
}
