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

import de.iritgo.skillfull.client.Client;
import de.iritgo.skillfull.network.ClockMessage;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.ReliableAckMessage;
import de.iritgo.skillfull.network.ReliableMessage;


public class ClockMessageHandler extends SimpleChannelUpstreamHandler
{
	private Client client;
	private int sequence = 0;

	public ClockMessageHandler (Client client)
	{
		this.client = client;
	}

	@Override
	public void messageReceived (ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		if (e.getMessage () instanceof ClockMessage)
		{
			ClockMessage message = (ClockMessage) e.getMessage ();
			client.getGameTimeManager ().updateNetworkClock (message);
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
