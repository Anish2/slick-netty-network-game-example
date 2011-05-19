
package de.iritgo.skillfull.user.network.decoder;


import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.user.network.LoginRequestMessage;
import de.iritgo.skillfull.user.network.LoginResponseMessage;
import de.iritgo.skillfull.user.network.NewUserMessage;
import de.iritgo.skillfull.user.network.UserInputMessage;


public class UserMessageDecoder extends OneToOneDecoder
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
			if (Opcode.fromByte (opcode) == Opcode.LOGIN)
			{
				int messageSize = buffer.readInt ();
				String name = readString (buffer);
				String password = readString (buffer);
				String gameName = readString (buffer);
				LoginRequestMessage loginMessage = new LoginRequestMessage ();
				loginMessage.setName (name);
				loginMessage.setPassword (password);
				loginMessage.setGameName (gameName);
				loginMessage.setClientId ((byte) clientId);
				return loginMessage;
			}
			else if (Opcode.fromByte (opcode) == Opcode.NEW_USER)
			{
				int messageSize = buffer.readInt ();
				byte userId = buffer.readByte ();
				String name = readString (buffer);
				NewUserMessage newUserMessage = new NewUserMessage (userId, name);
				newUserMessage.setClientId ((byte) clientId);
				return newUserMessage;
			}
			else if (Opcode.fromByte (opcode) == Opcode.LOGIN_RESPONSE)
			{
				LoginResponseMessage loginResponseMessage = new LoginResponseMessage (buffer.readByte (),
								buffer.readByte ());
				return loginResponseMessage;
			}
			else if (Opcode.fromByte (opcode) == Opcode.USER_INPUT)
			{
				int tick = buffer.readInt ();
				int pressedKey = buffer.readInt ();
				int mouseX = buffer.readInt ();
				int mouseY = buffer.readInt ();
				int posX = buffer.readInt ();
				int posY = buffer.readInt ();
				byte mouseClicked = buffer.readByte ();
				UserInputMessage userInputMessage = new UserInputMessage ();
				userInputMessage.setTick (tick);
				userInputMessage.setPressedKey (pressedKey);
				userInputMessage.setMouseX (mouseX);
				userInputMessage.setMouseY (mouseY);
				userInputMessage.setPosX (posX);
				userInputMessage.setPosY (posY);
				userInputMessage.setMouseClicked (mouseClicked);
				userInputMessage.setClientId ((byte) clientId);
				return userInputMessage;
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
