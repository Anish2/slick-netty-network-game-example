
package de.iritgo.skillfull.bullet.action;


import com.artemis.utils.Utils;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.BulletTimer;


public class DriveAction extends BulletAction
{
	private int timer;

	private BulletTimer bulletTimer;

	private float x;

	private float y;

	private Bullet initBullet;

	private boolean wait;

	private float acceleration;

	private float rotation;

	private float movedWay;

	private float speed;

	private int lastCallTime;

	private float lastMovedWay;

	private float lastNextPosX;

	private float lastNextPosY;

	private float lastMoved;

	public DriveAction (Bullet bullet)
	{
		bulletTimer = new BulletTimer ();
		initBullet = new Bullet (bullet);
		rotation = -1;
		acceleration = 0.0f;
		wait = false;
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		bulletTimer.update (delta);
		int time = bulletTimer.getTime ();

		if (! bulletTimer.isStartTimeReached ())
			return ! wait;

		if (! bulletTimer.isValid ())
		{
			inactive ();
			bullet.setMovedWay (movedWay);
			bullet.setAcceleration (acceleration);
			if (rotation != -1)
				bullet.setRotation (rotation);

			bullet.setLastSpeed (((acceleration) * (time)) + (speed));
			return true;
		}

		float posInTime = (float) bulletTimer.getTime () / (float) bulletTimer.getStopTime ();
		if (posInTime > 1)
		{
			posInTime = 1;
		}
		else
		{
			if (rotation != -1)
				bullet.setRotation (Utils.lerpDegrees (initBullet.getRotation (), rotation, posInTime));
		}

		movedWay = (0.5f * (acceleration / 1000) * ((time) * (time))) + ((speed / 1000) * (time));
		
		float nextPosX = Utils.getXAtEndOfRotatedLineByOrigin (0, movedWay - lastMoved, bullet.getRotation ());
		float nextPosY = Utils.getYAtEndOfRotatedLineByOrigin (0, movedWay - lastMoved, bullet.getRotation ());
		
		lastMoved = movedWay;

		bullet.setX (bullet.getX () + nextPosX);
		bullet.setY (bullet.getY () + nextPosY);
		return ! wait;
	}

	public DriveAction acceleration (float acceleration)
	{
		this.acceleration = acceleration;
		return this;
	}

	public DriveAction movedWay (float movedWay)
	{
		this.movedWay = movedWay;
		return this;
	}

	public DriveAction rotate (float rotation)
	{
		this.rotation = rotation;
		return this;
	}

	public DriveAction time (int time)
	{
		bulletTimer.setStopTime (time);
		return this;
	}

	public DriveAction block ()
	{
		wait = true;
		return this;
	}

	public DriveAction time (int startTime, int stopTime)
	{
		bulletTimer.setStartTime (startTime);
		bulletTimer.setStopTime (startTime + stopTime);
		return this;
	}

	public DriveAction speed (float speed)
	{
		this.speed = speed;
		return this;
	}
}
