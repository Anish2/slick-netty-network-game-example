package de.iritgo.skillfull.network;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class ReliableMessage extends Message
{
	public int reliableId;
	private ChannelBuffer messageBuffer;

	public ReliableMessage ()
	{
		super (Opcode.RELIABLE);
	}

	public ReliableMessage (int relaibleId)
	{
		super (Opcode.RELIABLE);
		this.reliableId = relaibleId;
	}

	public int getReliableId ()
	{
		return reliableId;
	}

	public void setReliableId (int compressedMessageSize)
	{
		this.reliableId = compressedMessageSize;
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

		channelBuffer.writeByte (Opcode.RELIABLE.getObcode ());
		channelBuffer.writeInt (compressedBuffer.readableBytes ());
		channelBuffer.writeInt (reliableId);
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

	public Message withReliableId (int reliableId)
	{
		this.reliableId = reliableId;
		return this;
	}
}
