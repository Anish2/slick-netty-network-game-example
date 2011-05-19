package de.iritgo.skillfull.entity.network.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import de.iritgo.skillfull.entity.network.EntityNetworkMessage;
import de.iritgo.skillfull.network.CommonNetwork;


public class EntityMessageHandler extends SimpleChannelUpstreamHandler
{
	private CommonNetwork network;
	
	public EntityMessageHandler (CommonNetwork network)
	{
		this.network = network;
	}

	@Override
	public void messageReceived (ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		if (e.getMessage () instanceof EntityNetworkMessage)
		{
			network.receivedMessage ((EntityNetworkMessage) e.getMessage ());
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
