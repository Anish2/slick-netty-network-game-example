package de.iritgo.skillfull.bullet;

import de.iritgo.skillfull.bullet.pattern.BulletPattern;


public abstract class BulletAction
{
	private ActionData actionData;
	private int actionDataIndex;
	private BulletTimer bulletTimer;
	private int startTime;
	private int stopTime;
	protected BulletPattern pattern;

	public void init (Bullet bullet)
	{

		actionDataIndex = bullet.getId ();

		if (! actionData.init[actionDataIndex])
		{
			actionData.init[actionDataIndex] = true;
			actionData.active[actionDataIndex] = true;
			actionData.startX[actionDataIndex] = bullet.getX ();
			actionData.startY[actionDataIndex] = bullet.getY ();
			actionData.startRotation[actionDataIndex] = bullet.getRotation ();
			actionData.startHeading[actionDataIndex] = bullet.getHeading ();
			actionData.startAcceleration[actionDataIndex] = bullet.getAcceleration ();
			actionData.startSpeed[actionDataIndex] = bullet.getSpeed ();
			actionData.currentX[actionDataIndex] = 0;
			actionData.currentY[actionDataIndex] = 0;
			actionInit ();
		}
	}

	public void setInit (boolean init)
	{
		actionData.init[actionDataIndex] = init;
	}

	protected abstract void actionInit ();

	public void setActionData (ActionData actionData)
	{
		this.actionData = actionData;
	}

	public void setPattern (BulletPattern pattern)
	{
		this.pattern = pattern;
	}

	public boolean perform (BulletDirector bulletDirector, Bullet bullet)
	{
		return perform (getTime (), bulletDirector, bullet);
	}

	protected boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		return true;
	}

	public void performDone (BulletDirector bulletDirector, Bullet bullet)
	{
		performDone (getTime (), bulletDirector, bullet);
	}

	public void performDone (int time, BulletDirector bulletDirector, Bullet bullet)
	{
	}

	public int getTime ()
	{
		return (bulletTimer.getTime () - startTime);
	}

	public boolean isInTime ()
	{
		return actionData.active[actionDataIndex] &&
		bulletTimer.getTime () >= startTime && ((bulletTimer.getTime () - startTime) < stopTime);
	}

	protected void inactive ()
	{
		actionData.active[actionDataIndex] = false;
	}

	public boolean isActionDone ()
	{
		return actionData.actionDone[actionDataIndex];
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

	public void updateTime (int delta)
	{
		startTime = bulletTimer.getTime () + startTime - (bulletTimer.getFirstDelta () + bulletTimer.getOverlapTime ());
	}


	public void updateOverlapTime ()
	{
		if (stopTime != 0)
			bulletTimer.setOverlapTime (bulletTimer.getTime () - (startTime + stopTime));
	}

	public void activate ()
	{
		actionData.active[actionDataIndex] = true;
		actionData.actionDone[actionDataIndex] = false;
	}

	protected void setActionDone (boolean actionDone)
	{
		actionData.active[actionDataIndex] = actionDone;
	}

	protected float getStartRotation ()
	{
		return actionData.startRotation[actionDataIndex];
	}

	protected float getRotation ()
	{
		return actionData.currentRotation[actionDataIndex];
	}

	protected void setRotation (float rotation)
	{
		for (int i = 0 ; i < actionData.currentSpeed.length; ++i)
			actionData.currentRotation[i] = rotation + pattern.getOffsetRotation ();
	}

	protected float getAcceleration ()
	{
		return actionData.currentAcceleration[actionDataIndex];
	}

	protected void setAcceleration (float acceleration)
	{
		for (int i = 0 ; i < actionData.currentAcceleration.length; ++i)
			actionData.currentAcceleration[i] = acceleration;
	}

	protected float getSpeed ()
	{
		return actionData.currentSpeed[actionDataIndex];
	}

	protected void setSpeed (float speed)
	{
		for (int i = 0 ; i < actionData.currentSpeed.length; ++i)
			actionData.currentSpeed[i] = speed  + pattern.getOffsetSpeed ();
	}

	protected float getCurrentX ()
	{
		return actionData.currentX[actionDataIndex];
	}

	protected void setCurrentX (float x)
	{
		actionData.currentX[actionDataIndex] = x;
	}

	protected float getCurrentY ()
	{
		return actionData.currentY[actionDataIndex];
	}

	protected void setCurrentY (float y)
	{
		actionData.currentY[actionDataIndex] = y;
	}

	protected float getMovedWay ()
	{
		return actionData.movedWay[actionDataIndex];
	}

	protected void setMovedWay (float movedWay)
	{
		actionData.movedWay[actionDataIndex] = movedWay;
	}

	protected float getStartX ()
	{
		return actionData.startX[actionDataIndex];
	}

	protected void setStartX (float x)
	{
		actionData.startX[actionDataIndex] = x;
	}

	protected float getStartY ()
	{
		return actionData.startY[actionDataIndex];
	}

	protected void setStartY (float y)
	{
		actionData.startY[actionDataIndex] = y;
	}
}
