
package de.iritgo.skillfull.bullet.action;


import com.artemis.utils.Utils;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.BulletTimer;
import de.iritgo.skillfull.bullet.pattern.BulletPatternContext;


public class MoveAction extends BulletAction
{
	public MoveAction ()
	{

		// actionDone = false;
	}

	@Override
	protected void actionInit ()
	{
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{

		// float posInTime = (float) getTime () / (float) getStopTime ();
		// if (posInTime > 1)
		// {
		// posInTime = 1;
		// }
		// bullet.setX (Utils.lerp (startX, x, posInTime));
		// bullet.setY (Utils.lerp (startY, y, posInTime));
		//
		// bullet.setRotation (Utils.lerpDegrees (bullet.getRotation (),
		// Utils.angleInDegrees (startX, startY, x, y), posInTime));

		// System.out.println (posInTime + " : " + bullet.getX () + "/" +
		// bullet.getY ());
		return false;
	}

	public MoveAction to (float x, float y)
	{
		// this.x = x;
		// this.y = y;
		return this;
	}

	public MoveAction time (int time)
	{
		stopTime (time);
		return this;
	}

	public MoveAction dontWait ()
	{
		// actionDone = true;
		return this;
	}
}
