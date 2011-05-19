package de.iritgo.skillfull.network;

import java.util.Date;


public class PingPongMessage extends Message
{
	private Long timestamp;
	
	
	public PingPongMessage ()
	{
		super(Opcode.PINGPONG);
	}
	
	public PingPongMessage (Long timestamp)
	{
		this.timestamp = timestamp;
	}

	@Override
	public void transfer ()
	{
		channelBuffer.writeByte (Opcode.PINGPONG.getObcode ());
		channelBuffer.writeLong (timestamp);
	}
	
	public Long getTimestamp ()
	{
		return timestamp;
	}
	
}
