package de.iritgo.skillfull.network;


public class ClockMessage extends Message
{
	private int startTimestamp;
	private int serverTimestamp;

	public ClockMessage ()
	{
		super (Opcode.CLOCK);
	}

	public ClockMessage (int startTime, int serverTime)
	{
		super (Opcode.CLOCK);
		this.startTimestamp = startTime;
		this.serverTimestamp = serverTime;
	}

	public void setStartTimestamp (int startTimestamp)
	{
		this.startTimestamp = startTimestamp;
	}

	public int getStartTimestamp ()
	{
		return startTimestamp;
	}

	public void setServerTimestamp (int serverTimestamp)
	{
		this.serverTimestamp = serverTimestamp;
	}

	public int getServerTimestamp ()
	{
		return serverTimestamp;
	}

	@Override
	public void transfer ()
	{
		channelBuffer.writeByte (Opcode.CLOCK.getObcode ());
		channelBuffer.writeInt (startTimestamp);
		channelBuffer.writeInt (serverTimestamp);
	}
}
