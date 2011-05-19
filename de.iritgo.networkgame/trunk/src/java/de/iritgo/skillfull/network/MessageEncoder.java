package de.iritgo.skillfull.network;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class MessageEncoder extends OneToOneEncoder
{

	@Override
	synchronized protected Object encode (ChannelHandlerContext context, Channel channel, Object message) throws Exception
	{
		if (message instanceof Message)
		{
			Message sendMessage = (Message) message;
			ChannelBuffer channelBuffer = ChannelBuffers.buffer (512);
			sendMessage.transferToBuffer (channelBuffer);

//			dump (channel, message, channelBuffer);
			return channelBuffer;
		}
		return null;
	}

	private void dump (Channel channel, Object message, ChannelBuffer channelBuffer)
	{
		channelBuffer.markReaderIndex ();
		System.out.print ("Send from ");
		if (channel.getLocalAddress ().toString ().indexOf ("9001") >= 0)
		{
			System.out.println ("Client");
		}
		else
			System.out.println ("Server");

		((Message) message).dumpObcodes (); System.out.println ("");

		while (channelBuffer.readableBytes () > 0)
		{
			System.out.print (channelBuffer.readByte () + ":");
		}
		System.out.println ();
		channelBuffer.resetReaderIndex ();
	}
}
