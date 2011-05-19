package de.iritgo.skillfull.server.network;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;

import de.iritgo.skillfull.network.ClockMessage;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.MessageHandler;
import de.iritgo.skillfull.server.Server;


public class ClockMessageHandler extends MessageHandler
{
	private Server server;
	
	public ClockMessageHandler (Server server)
	{
		this.server = server;
	}

	public boolean messageReceived (ChannelHandlerContext ctx, MessageEvent e, String clientId) throws Exception
	{
		if (e.getMessage () instanceof ClockMessage)
		{
			ClockMessage message = (ClockMessage) e.getMessage ();
			long time = server.getGameTimeManager (message.getClientId ()).getNetworkTime ();
			message.setServerTimestamp ((int) time);
			e.getChannel ().write (new Message ().addMessage (message), e.getRemoteAddress ());
			return true;
		}
		return false;
	}

	@Override
	public void exceptionCaught (ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
	{
		e.getCause ().printStackTrace ();
	}
}
