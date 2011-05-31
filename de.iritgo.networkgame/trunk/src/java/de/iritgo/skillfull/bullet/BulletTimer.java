package de.iritgo.skillfull.bullet;

public class BulletTimer
{
	private int currentTime;
	private int stopTime;
	private int startTime;
	private int overlapTime;
	private int firstDelta;

	public BulletTimer (int stopTime)
	{
		this.stopTime = stopTime;
		currentTime = 0;
	}

	public BulletTimer ()
	{
		currentTime = 0;
	}

	public void update (int delta)
	{
		if (currentTime == 0)
		{
			firstDelta = delta;
		}
		else
			firstDelta = 0;
		
		currentTime += delta;
	}

	public boolean isValid ()
	{
		return currentTime <= stopTime;
	}

	public void setStopTime (int time)
	{
		stopTime = time;
	}

	public int getTime ()
	{
		return currentTime;
	}

	public int getStopTime ()
	{
		return stopTime;
	}

	public void setStartTime (int startTime)
	{
		this.startTime = startTime;
	}

	public boolean isStartTimeReached ()
	{
		return currentTime >= startTime;
	}

	public void setOverlapTime (int overlapTime)
	{
		this.overlapTime = overlapTime;
	}
	
	public int getOverlapTime ()
	{
		return overlapTime;
	}

	public int getFirstDelta ()
	{
		return firstDelta;
	}
}
