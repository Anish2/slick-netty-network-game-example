
package de.iritgo.skillfull.bullet.action;


import com.artemis.utils.Utils;

import de.iritgo.skillfull.bullet.ActionData;
import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.pattern.BulletPattern;
import de.iritgo.skillfull.bullet.pattern.BulletPatternContext;


public class DriveAction extends BulletAction
{
	private BulletPattern patternContext;

	@Override
	protected void actionInit ()
	{
		setRotation (-1);
		setAcceleration (0.0f);
		setActionDone (false);
		setMovedWay (0);
		activate ();
	}

	@Override
	public boolean perform (int time, BulletDirector bulletDirector, Bullet bullet)
	{
		float posInTime = (float) time / (float) getStopTime ();
		if (posInTime > 1)
		{
			posInTime = 1;
		}

		if (getRotation () != - 1)
			bullet.setRotation (Utils.lerpDegrees (getStartRotation (), getRotation (), posInTime));

		if (getSpeed () == 0 && getAcceleration () == 0)
			return false;

		float way = (0.5f * (getAcceleration () / 1000) * ((time) * (time))) + ((getSpeed () / 1000) * (time));

		float rotation = bullet.getRotation ();
		float currentX = getCurrentX ();
		float currentY = getCurrentY ();
		float movedWay = getMovedWay ();
		currentX += Utils.getXAtEndOfRotatedLineByOrigin (0, way - movedWay, rotation);
		currentY += Utils.getYAtEndOfRotatedLineByOrigin (0, way - movedWay, rotation);

		setCurrentX (currentX);
		setCurrentY (currentY);

		setMovedWay (way);

		bullet.setX (getStartX () + currentX);
		bullet.setY (getStartY () + currentY);
//		System.out.println ("Mist: " + bullet.getX () + ">" + time + " weg: " + (way));
		return false;
	}

	@Override
	public void performDone (int time, BulletDirector bulletDirector, Bullet bullet)
	{
		perform (time, bulletDirector, bullet);
		if (getRotation () != - 1)
			bullet.setRotation (getRotation ());
	}

	public DriveAction acceleration (float acceleration)
	{
		setAcceleration (acceleration);
		return this;
	}

	public DriveAction rotate (float rotation)
	{
		setRotation (rotation);
		return this;
	}

	public DriveAction time (int time)
	{
		stopTime (time);
		return this;
	}

	public DriveAction dontWait ()
	{
		setActionDone (true);
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
		setSpeed (speed);
		return this;
	}
}
