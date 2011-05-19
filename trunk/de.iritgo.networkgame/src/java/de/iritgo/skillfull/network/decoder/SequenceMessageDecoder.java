package de.iritgo.skillfull.network.decoder;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import de.iritgo.skillfull.network.SequenceMessage;
import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.network.ReliableMessage;

public class SequenceMessageDecoder extends OneToOneDecoder
{
/*
	@Override
	protected Object decode (ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception
	{
		return null;
	}
*/

	protected Object decode (ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception
	{
		if (msg instanceof ChannelBuffer)
		{
			ChannelBuffer buffer = (ChannelBuffer) msg;
			buffer.markReaderIndex ();
			byte clientId = buffer.readByte ();
			byte opcode = buffer.readByte ();
			if (Opcode.fromByte (opcode) == Opcode.SEQUENCE)
			{
				int compressedMessageSize = buffer.readInt ();
				int sequenceId = buffer.readInt ();
				int subSequenceId = buffer.readInt ();
				int messages = buffer.readByte ();

				SequenceMessage seqMessage = new SequenceMessage  ();
				seqMessage.setNumOfMessages (messages);
				seqMessage.setSequenceId (sequenceId);
				seqMessage.setMessageSize (compressedMessageSize);
				seqMessage.setMessageBuffer (buffer);
				seqMessage.setClientId ((byte)clientId);
				seqMessage.setSubSequenceId (subSequenceId);

				return seqMessage;
			}
			buffer.resetReaderIndex ();
		}
		return msg;
	}

}
