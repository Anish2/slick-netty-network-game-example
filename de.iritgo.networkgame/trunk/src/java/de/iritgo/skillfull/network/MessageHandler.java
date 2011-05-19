package de.iritgo.skillfull.network;

import java.net.SocketAddress;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public abstract class MessageHandler extends SimpleChannelUpstreamHandler
{
	public MessageHandler ()
	{
	}
	
	@Override
	public void messageReceived (ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		if (e.getMessage () instanceof Message)
		{
			((Message) e.getMessage ()).setNetworkId (getClientId (e.getRemoteAddress (), (Message) e.getMessage ()));
			if (! messageReceived (ctx, e, getClientId (e.getRemoteAddress (), (Message) e.getMessage ())))
			{
				super.messageReceived (ctx,e);
			}
		}
		else
		{
			super.messageReceived (ctx,e);
		}
	}

	public abstract boolean messageReceived (ChannelHandlerContext ctx, MessageEvent e, String clientId) throws Exception;
	
	protected String getClientId (SocketAddress remoteAddress, Message message)
	{
		return remoteAddress.hashCode () + "/" + message.getClientId ();
	}

}
