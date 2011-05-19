package de.iritgo.skillfull.server.network;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;

import de.iritgo.skillfull.chat.ChatMessage;
import de.iritgo.skillfull.network.MessageHandler;
import de.iritgo.skillfull.user.network.LoginRequestMessage;
import de.iritgo.skillfull.user.network.UserInputMessage;


public class CommonMessageHandler extends MessageHandler
{
	private Network network;

	public CommonMessageHandler (Network network)
	{
		this.network = network;
	}

	public boolean messageReceived (ChannelHandlerContext ctx, MessageEvent e, String clientUniqueId) throws Exception
	{
		if (e.getMessage () instanceof LoginRequestMessage)
		{
			LoginRequestMessage login = (LoginRequestMessage) e.getMessage ();
			login.setRemoteAddress (e.getRemoteAddress ());
			network.addClientIdRemoteAddressMapping (e.getRemoteAddress (), login);
			network.receivedMessage (login);
			return true;
		}
		if (e.getMessage () instanceof UserInputMessage)
		{
			UserInputMessage userInputMessage = (UserInputMessage) e.getMessage ();
			network.receivedMessage (userInputMessage);
			return true;
		}
		if (e.getMessage () instanceof ChatMessage)
		{
			network.receivedMessage ((ChatMessage) e.getMessage ());
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
