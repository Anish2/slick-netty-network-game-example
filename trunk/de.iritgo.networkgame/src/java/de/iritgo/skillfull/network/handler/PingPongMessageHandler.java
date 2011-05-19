package de.iritgo.skillfull.network.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.CommonNetwork;
import de.iritgo.skillfull.network.PingPongMessage;


public class PingPongMessageHandler extends SimpleChannelUpstreamHandler
{
	private boolean server;
	private CommonNetwork network;
	
	public PingPongMessageHandler (CommonNetwork network, boolean server)
	{
		this.server = server;
		this.network = network;
	}

	@Override
	public void messageReceived (ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		if (e.getMessage () instanceof PingPongMessage)
		{
			PingPongMessage message = (PingPongMessage) e.getMessage ();
			if (server)
			{
				System.out.println ("Server pong");
				e.getChannel ().write (new Message ().andMessage 
								(message), e.getRemoteAddress ());

			}
			else
			{
				System.out.println ("Ping (ms): " + (System.currentTimeMillis () - message.getTimestamp ()));
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
