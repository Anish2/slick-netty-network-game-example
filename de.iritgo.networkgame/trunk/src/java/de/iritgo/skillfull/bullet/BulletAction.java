package de.iritgo.skillfull.bullet;


public class BulletAction
{
	private boolean active = true;

	protected boolean actionDone;
	
	private int stopTime;

	private int time;

	private int startTime;

	private BulletTimer bulletTimer;

	
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		return true;
	}

	public boolean isInTime ()
	{
		return active && bulletTimer.getTime () >= startTime && ((bulletTimer.getTime () - startTime) + bulletTimer.getOverlapTime () <= stopTime);
	}
	
	protected void inactive ()
	{
		active = false;
	}

	public boolean isActionDone ()
	{
		return actionDone;
	}
	
	public void setTime (int time)
	{
		this.time = time;
	}
	
	public int getTime ()
	{
		return (bulletTimer.getTime () - startTime) + bulletTimer.getOverlapTime ();
	}
	
	public void startTime (int startTime)
	{
		this.startTime = startTime;
	}

	public void stopTime (int stopTime)
	{
		this.stopTime = stopTime;
	}
	
	public int getStopTime ()
	{
		return stopTime;
	}

	public void setBulletTimer (BulletTimer bulletTimer)
	{
		this.bulletTimer = bulletTimer;
	}

	public void updateTime ()
	{
		startTime = bulletTimer.getTime () + startTime;
	}

	public void performDone (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		// TODO Auto-generated method stub
		
	}

	public void updateOverlapTime ()
	{
		if (stopTime != 0)
			bulletTimer.setOverlapTime ((bulletTimer.getTime () - startTime)  + bulletTimer.getOverlapTime () - stopTime);
		System.out.println (bulletTimer.getOverlapTime ());
	}
}
