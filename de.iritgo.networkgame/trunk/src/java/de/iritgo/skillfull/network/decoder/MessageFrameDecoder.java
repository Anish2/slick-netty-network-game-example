package de.iritgo.skillfull.network.decoder;

import java.nio.charset.Charset;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.FailedChannelFuture;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.network.Options;

public class MessageFrameDecoder extends FrameDecoder
{
	private HashMap<String, Integer> clientSequenceMap = new HashMap<String, Integer> ();

	public MessageFrameDecoder ()
	{
		super (true);
	}

	@Override
	protected Object decode (ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception
	{
//		System.out.print ("Side: " + channel.getLocalAddress () + " it is: ");
//		if (channel.getLocalAddress ().toString ().indexOf ("9001") >= 0)
//		{
//			System.out.println ("Client");
//		}
//		else
//			System.out.println ("Server");
			
		buffer.markReaderIndex ();
		if (buffer.readableBytes () < 1)
		{
			return null;
		}
		// Dummy read or for late use...
		byte clientId = buffer.readByte ();

		byte opcodeByte = buffer.readByte ();
		Opcode opcode = null;
		try
		{
			opcode = Opcode.fromByte (opcodeByte);
		}
		catch (Exception x)
		{
			System.out.print ("Can't decode message on " );
			if (channel.getLocalAddress ().toString ().indexOf ("9001") >= 0)
			{
				System.out.println ("Client");
			}
			else
				System.out.println ("Server");

			System.out.print (clientId + ":" + opcodeByte + ":");
			while (buffer.readableBytes () > 0)
			{
				System.out.print (buffer.readByte () + ":");
			}
			System.out.println ();

			return null;
		}
		int messageSize = opcode.getSize ();
		int readed = 2;
		if ((opcodeByte & Options.DYNAMIC_LENGTH_MESSAGE) == Options.DYNAMIC_LENGTH_MESSAGE )
		{
			messageSize += buffer.readInt ();
			readed = 6;
		}

		if (buffer.readableBytes () < messageSize - readed)
		{
			buffer.resetReaderIndex ();
			return null;
		}
		ChannelBuffer frame = buffer.copy (buffer.readerIndex () - readed, messageSize);
		buffer.skipBytes (messageSize - readed);
		return frame;

/*		
		else if (opcode == Opcode.COMPRESSED)
		{
			if (buffer.readableBytes () < opcode.getSize () - 1)
			{
				buffer.resetReaderIndex ();
				return null;
			}
			int sequenceId = buffer.readInt ();
			int messages = buffer.readByte ();
			int compressedMessageSize = buffer.readInt ();
			
			int currentFrame = 0;
			Object[] frames = new Object[messages];
			int dataPos = buffer.readerIndex ();
			while (buffer.readerIndex () - dataPos < compressedMessageSize)
			{
				clientId = buffer.readByte ();
				opcodeByte = buffer.readByte ();
				opcode = Opcode.fromByte (opcodeByte);
				if (opcode != Opcode.COMPRESSED)
				{
					int messageSize = opcode.getSize ();
					int readed = 2;
					if ((opcodeByte & Options.DYNAMIC_LENGTH_MESSAGE) == Options.DYNAMIC_LENGTH_MESSAGE )
					{
						messageSize += buffer.readInt ();
						readed = 6;
					}
					
					if (buffer.readableBytes () < messageSize - readed)
					{
						buffer.resetReaderIndex ();
						return null;
					}
					frames[currentFrame] = ChannelBuffers.directBuffer (messageSize + 4);
					((ChannelBuffer)frames[currentFrame]).writeBytes (buffer, buffer.readerIndex () - readed, messageSize); 
					((ChannelBuffer)frames[currentFrame]).writeInt (sequenceId);
					currentFrame++;
					buffer.skipBytes (messageSize - readed);
				}
			}
			return frames;
		}
		buffer.resetReaderIndex ();
		return null;
*/		
	}
}
