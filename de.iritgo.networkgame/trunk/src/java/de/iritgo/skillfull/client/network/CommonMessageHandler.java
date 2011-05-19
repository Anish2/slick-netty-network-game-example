
package de.iritgo.skillfull.client.network;


import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import de.iritgo.skillfull.chat.ChatMessage;
import de.iritgo.skillfull.network.ClockMessage;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.user.network.LoginResponseMessage;
import de.iritgo.skillfull.user.network.NewUserMessage;


public class CommonMessageHandler extends SimpleChannelUpstreamHandler
{
	private Network network;

	private int sequence = 0;

	public CommonMessageHandler (Network network)
	{
		this.network = network;
	}

	@Override
	public void messageReceived (ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		if (e.getMessage () instanceof LoginResponseMessage)
		{
			network.receivedMessage ((Message) e.getMessage ());
		}
		if (e.getMessage () instanceof ChatMessage)
		{
			network.receivedMessage ((ChatMessage) e.getMessage ());
		}
		if (e.getMessage () instanceof NewUserMessage)
		{
			network.receivedMessage ((NewUserMessage) e.getMessage ());
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
