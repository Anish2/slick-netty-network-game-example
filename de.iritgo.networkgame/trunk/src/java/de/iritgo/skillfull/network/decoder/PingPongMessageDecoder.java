package de.iritgo.skillfull.network.decoder;

import static org.jboss.netty.channel.Channels.fireMessageReceived;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import de.iritgo.skillfull.network.ClockMessage;
import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.network.PingPongMessage;

public class PingPongMessageDecoder extends OneToOneDecoder
{
	@Override
	protected Object decode (ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception
	{
		if (msg instanceof ChannelBuffer)
		{
			ChannelBuffer buffer = (ChannelBuffer) msg;
			buffer.markReaderIndex ();
			byte clientId = buffer.readByte ();
			byte opcode = buffer.readByte ();
			if (Opcode.fromByte (opcode) == Opcode.PINGPONG)
			{
				long timestamp = buffer.readLong ();
				PingPongMessage pingpongMessage = new PingPongMessage (timestamp);
				pingpongMessage.setClientId ((byte)clientId);
				return pingpongMessage;
			}
			buffer.resetReaderIndex ();
		}
		return msg;
	}
}
