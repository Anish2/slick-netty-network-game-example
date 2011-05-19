package de.iritgo.skillfull.network.decoder;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.network.ReliableMessage;

public class ReliableMessageDecoder extends OneToOneDecoder
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
			if (Opcode.fromByte (opcode) == Opcode.RELIABLE)
			{
				int messageSize = buffer.readInt ();
				int reliableId = buffer.readInt ();
				int messages = buffer.readByte ();
				ReliableMessage reliableMessage = new ReliableMessage  ();
				reliableMessage.setReliableId (reliableId);
				reliableMessage.setMessageSize (messageSize);
				reliableMessage.setMessageBuffer (buffer);
				reliableMessage.setClientId ((byte)clientId);
				
				return reliableMessage;
			}
			buffer.resetReaderIndex ();
		}
		return msg;
	}
}
