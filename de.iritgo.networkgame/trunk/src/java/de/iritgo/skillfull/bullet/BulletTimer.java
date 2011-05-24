package de.iritgo.skillfull.bullet;

public class BulletTimer
{
	private int currentTime;
	private int stopTime;
	private int startTime;

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
		currentTime += delta;
	}
	
	public boolean isValid ()
	{
		return currentTime < stopTime;
	}

	public void setStopTime (int time)
	{
		stopTime = time;
	}

	public float getTime ()
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
}
