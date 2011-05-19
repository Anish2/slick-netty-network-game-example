package de.iritgo.skillfull.network;

import org.jboss.netty.buffer.ChannelBuffer;


public class SequenceAckMessage extends Message
{
	private int sequenceId;

	public SequenceAckMessage ()
	{
		super (Opcode.SEQUENCE_ACK);
	}

	public SequenceAckMessage (int sequenceId, int subSequenceId)
	{
		super (Opcode.SEQUENCE_ACK);
		this.sequenceId = sequenceId;
		this.subSequenceId = subSequenceId;
	}

	public void setSequenceId (int sequenceId)
	{
		this.sequenceId = sequenceId;
	}

	@Override
	public int getMessageSize ()
	{
		return 5;
	}

	@Override
	public void transfer ()
	{
		channelBuffer.writeByte (Opcode.SEQUENCE_ACK.getObcode ());
		channelBuffer.writeInt (sequenceId);
		channelBuffer.writeInt (subSequenceId);
	}

	public int getSequenceId ()
	{
		return sequenceId;
	}
}
