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

import de.iritgo.skillfull.network.SequenceAckMessage;
import de.iritgo.skillfull.network.SequenceMessage;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.ReliableAckMessage;
import de.iritgo.skillfull.network.ReliableMessage;


public class SequenceMessageHandler extends SimpleChannelUpstreamHandler
{
	private Network network;
	private Map<String, Integer> reliableClientIds;

	public SequenceMessageHandler (Network network)
	{
		this.network = network;
		reliableClientIds = new ConcurrentHashMap<String, Integer> ();
	}

	@Override
	public void messageReceived (ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		if (e.getMessage () instanceof SequenceMessage)
		{
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
