package de.iritgo.skillfull.chat.decoder;

import static org.jboss.netty.channel.Channels.fireMessageReceived;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import de.iritgo.skillfull.chat.ChatMessage;
import de.iritgo.skillfull.entity.network.EntityCreateMessage;
import de.iritgo.skillfull.entity.network.EntityDestroyMessage;
import de.iritgo.skillfull.entity.network.EntitySegmentMoveMessage;
import de.iritgo.skillfull.entity.network.EntityPositionMessage;
import de.iritgo.skillfull.entity.network.UserEntityCreateMessage;
import de.iritgo.skillfull.network.ClockMessage;
import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.user.network.LoginRequestMessage;

public class ChatMessageDecoder extends OneToOneDecoder
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
			if (Opcode.fromByte (opcode) == Opcode.CHAT_MESSAGE)
			{
				int messageSize = buffer.readInt ();
				byte userId = buffer.readByte ();
				String message = readString (buffer);

				ChatMessage chatMessage = new ChatMessage (userId, message);
				chatMessage.setClientId ((byte) clientId);
				return chatMessage;
			}
			buffer.resetReaderIndex ();
		}
		return msg;
	}

	private String readString (ChannelBuffer buffer)
	{
		int size = buffer.readInt ();
		String string = buffer.toString (buffer.readerIndex (), size, Charset.defaultCharset ());
		buffer.skipBytes (size);
		return string;
	}
}
