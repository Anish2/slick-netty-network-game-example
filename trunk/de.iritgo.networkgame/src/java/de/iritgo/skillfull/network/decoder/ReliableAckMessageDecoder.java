package de.iritgo.skillfull.network.decoder;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.network.ReliableAckMessage;
import de.iritgo.skillfull.user.network.LoginRequestMessage;

public class ReliableAckMessageDecoder extends OneToOneDecoder
{
	@Override
	protected Object decode (ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception
	{
		if (msg instanceof ChannelBuffer)
		{
			ChannelBuffer buffer = (ChannelBuffer) msg;
			buffer.markReaderIndex ();
			int clientId = buffer.readByte ();
			byte opcode = buffer.readByte ();
			if (Opcode.fromByte (opcode) == Opcode.RELIABLE_ACK)
			{
				ReliableAckMessage ack = new ReliableAckMessage (buffer.readInt ());
				ack.setClientId ((byte)clientId);
				return ack;
			}
			buffer.resetReaderIndex ();
		}
		return msg;
	}
}
