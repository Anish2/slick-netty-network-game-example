
package de.iritgo.skillfull.bullet.action;


import com.artemis.utils.Utils;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.BulletTimer;


public class DriveAction extends BulletAction
{
	private float x;

	private float y;

	private Bullet initBullet;

	private float acceleration;

	private float rotation;

	private float movedWay;

	private float speed;

	private float lastMovedWay;

	private float startX;

	private float startY;

	private float startRot;

	public DriveAction (Bullet bullet)
	{
		initBullet = new Bullet (bullet);
		startX = initBullet.getX ();
		startY = initBullet.getY ();
		startRot = initBullet.getRotation ();
		rotation = - 1;
		acceleration = 0.0f;
		actionDone = false;
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		int time = getTime ();

		float posInTime = (float) time / (float) getStopTime ();
		if (posInTime > 1)
		{
			posInTime = 1;
		}

		if (rotation != - 1)
			bullet.setRotation (Utils.lerpDegrees (startRot, rotation, posInTime));

		movedWay = (0.5f * (acceleration / 1000) * ((time) * (time))) + ((speed / 1000) * (time));

		if (speed == 0 && acceleration == 0)
			return actionDone;

		float rotation = bullet.getRotation ();
		x += Utils.getXAtEndOfRotatedLineByOrigin (0, movedWay - lastMovedWay, rotation);
		y += Utils.getYAtEndOfRotatedLineByOrigin (0, movedWay - lastMovedWay, rotation);
		lastMovedWay = movedWay;

		bullet.setX (startX + x);
		bullet.setY (startY + y);
//		System.out.println ("Mist: " + bullet.getX () + ">" + time + " weg: " + movedWay);
		return actionDone;
	}

	@Override
	public void performDone (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		perform (delta, bulletDirector, bullet);
		startX = bullet.getX ();
		startY = bullet.getY ();
		startRot = bullet.getRotation ();
		int time = getTime ();

		inactive ();
		bullet.setMovedWay (movedWay);
		bullet.setAcceleration (acceleration);
		if (rotation != -1)
			bullet.setRotation (rotation);

		bullet.setLastSpeed (((acceleration) * (time)) + (speed));
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
		stopTime (time);
		return this;
	}

	public DriveAction dontWait ()
	{
		actionDone = true;
		return this;
	}

	public DriveAction time (int startTime, int stopTime)
	{
		startTime (startTime);
		stopTime (startTime + stopTime);
		return this;
	}

	public DriveAction speed (float speed)
	{
		this.speed = speed;
		return this;
	}

	public DriveAction withLastSpeed ()
	{
		speed = initBullet != null ? initBullet.getLastSpeed () : speed;
		return this;
	}
}
