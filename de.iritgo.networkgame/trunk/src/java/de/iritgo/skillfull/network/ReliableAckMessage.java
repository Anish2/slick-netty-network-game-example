package de.iritgo.skillfull.network;

import org.jboss.netty.buffer.ChannelBuffer;


public class ReliableAckMessage extends Message
{
	private int reliableId;

	public ReliableAckMessage ()
	{
		super (Opcode.RELIABLE_ACK);
	}

	public ReliableAckMessage (int relialbeId)
	{
		super (Opcode.RELIABLE_ACK);
		this.reliableId = relialbeId;
	}

	public void setReliableId (int reliableId)
	{
		this.reliableId = reliableId;
	}

	@Override
	public int getMessageSize ()
	{
		return 5;
	}

	@Override
	public void transfer ()
	{
		channelBuffer.writeByte (Opcode.RELIABLE_ACK.getObcode ());
		channelBuffer.writeInt (reliableId);
	}

	public int getReliableId ()
	{
		return reliableId;
	}
}
