
package de.iritgo.skillfull.network;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;


public class SequenceMessage extends Message
{
	public int compressedMessageSize;

	private ChannelBuffer messageBuffer;

	private int numberOfMessages;

	public SequenceMessage ()
	{
		super (Opcode.SEQUENCE);
	}

	public int getCompressedMessageSize ()
	{
		return compressedMessageSize;
	}

	public void setCompressedMessageSize (int compressedMessageSize)
	{
		this.compressedMessageSize = compressedMessageSize;
	}

	@Override
	public void init (ChannelBuffer channelBuffer)
	{
		this.channelBuffer = channelBuffer;
		channelBuffer.writeByte (clientId);
	}

	@Override
	public void transfer ()
	{
		ChannelBuffer compressedBuffer = ChannelBuffers.dynamicBuffer ();

		for (Message message : messages)
		{
			message.init (compressedBuffer);
			message.transfer ();
		}

		channelBuffer.writeByte (Opcode.SEQUENCE.getObcode ());
		channelBuffer.writeInt (compressedBuffer.readableBytes ());
		channelBuffer.writeInt (sequenceId);
		channelBuffer.writeInt (subSequenceId);
		channelBuffer.writeByte ((byte) messages.size ());
		compressedBuffer.getBytes (0, channelBuffer, compressedBuffer.readableBytes ());
	}
	
	public void setMessageBuffer (ChannelBuffer buffer)
	{
		this.messageBuffer = buffer;
	}

	public ChannelBuffer getMessageBuffer ()
	{
		return messageBuffer;
	}

	public void setNumOfMessages (int numberOfMessages)
	{
		this.numberOfMessages = numberOfMessages;
	}

	public int getNumOfMessages ()
	{
		return numberOfMessages;
	}
}
