
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

	public DriveAction (Bullet bullet)
	{
		bulletTimer = new BulletTimer ();
		initBullet = new Bullet (bullet);
		wait = false;
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		bulletTimer.update (delta);
		int time = (int) (bulletTimer.getTime ());

		if (! bulletTimer.isStartTimeReached ())
			return ! wait;

		if (! bulletTimer.isValid ())
		{
			inactive ();
			bullet.setMovedWay (movedWay);
			bullet.setAcceleration (acceleration);
			bullet.setRotation (rotation);
			if (speed == 0)
			{
				bullet.setLastSpeed (movedWay / time);
			}
			return true;
		}

		float posInTime = bulletTimer.getTime () / bulletTimer.getStopTime ();
		if (posInTime > 1)
		{
			posInTime = 1;
			bullet.setRotation (rotation);
		}
		else
		{
			Utils.shouldRotateCounterClockwise (initBullet.getRotation (), rotation);
			bullet.setRotation (Utils.lerp (initBullet.getRotation (), rotation, posInTime));
		}
		movedWay = (0.5f * acceleration * ((time + 1000) * (time + 1000))) + (speed * (time + 1000));
		// System.out.println (time + "/" + delta + " L:" + lastCallTime +
		// " Dif:" + (time - lastCallTime) + " LastWay" +
		// (movedWay-lastMovedWay)
		// + "WWay: " + movedWay + "X:" + bullet.getX ());
		lastCallTime = time;
		lastMovedWay = movedWay;
		float nextPosX = Utils.getXAtEndOfRotatedLineByOrigin (0, movedWay, bullet.getRotation ());
		float nextPosY = Utils.getYAtEndOfRotatedLineByOrigin (0, movedWay, bullet.getRotation ());

		bullet.addX (nextPosX);
		bullet.addY (nextPosY);

		return ! wait;
	}

	public DriveAction acceleration (float acceleration)
	{
		this.acceleration = acceleration / 100000;
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
		this.speed = speed / 1000;
		return this;
	}
}
