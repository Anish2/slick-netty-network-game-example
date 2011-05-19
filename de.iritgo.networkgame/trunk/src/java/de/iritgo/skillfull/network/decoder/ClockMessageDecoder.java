package de.iritgo.skillfull.network.decoder;

import static org.jboss.netty.channel.Channels.fireMessageReceived;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import de.iritgo.skillfull.network.ClockMessage;
import de.iritgo.skillfull.network.Opcode;

public class ClockMessageDecoder extends OneToOneDecoder
{
/*
	@Override
	protected Object decode (ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception
	{
		buffer.markReaderIndex ();

		if (buffer.readableBytes () < 1)
		{
			return null;
		}
		byte opcode = buffer.readByte ();
		if (Opcode.fromByte (opcode) == Opcode.CLOCK)
		{
			int clock = buffer.readInt ();
			ClockMessage clockMessage = new ClockMessage ();
			clockMessage.setClock (clock);
			return clockMessage;
		}

		buffer.resetReaderIndex ();
		return buffer;
	}
*/
	@Override
	protected Object decode (ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception
	{
		if (msg instanceof ChannelBuffer)
		{
			ChannelBuffer buffer = (ChannelBuffer) msg;
			buffer.markReaderIndex ();
			byte clientId = buffer.readByte ();
			byte opcode = buffer.readByte ();
			if (Opcode.fromByte (opcode) == Opcode.CLOCK)
			{
				int startTimestamp = buffer.readInt ();
				int serverTimestamp = buffer.readInt ();
				ClockMessage clockMessage = new ClockMessage ();
				clockMessage.setStartTimestamp (startTimestamp);
				clockMessage.setServerTimestamp (serverTimestamp);
				clockMessage.setClientId ((byte)clientId);
				return clockMessage;
			}
			buffer.resetReaderIndex ();
		}
		return msg;
	}
}
