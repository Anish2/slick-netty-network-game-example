
package de.iritgo.skillfull.bullet.action;


import com.artemis.utils.Utils;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.BulletTimer;
import de.iritgo.skillfull.bullet.pattern.BulletPatternContext;


public class HeadingAction extends BulletAction
{
	private float rotation;

	private float fromRotation;

	public HeadingAction ()
	{
		// this.bullet = bullet;
		// actionDone = false;
	}

	@Override
	protected void actionInit ()
	{
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		// float posInTime = (float) bulletTimer.getTime () / (float)
		// bulletTimer.getStopTime ();
		// if (posInTime > 1)
		// {
		// posInTime = 1;
		// }

		// bullet.setHeading (Utils.lerpDegrees (fromRotation, rotation,
		// posInTime));
		return false;
	}

	public HeadingAction rotate (float rotation)
	{
		this.rotation = rotation;
		return this;
	}

	public HeadingAction fromRotation ()
	{
		// this.fromRotation = startRotation;
		return this;
	}

	public HeadingAction fromHeading ()
	{
		// this.fromRotation = startHeading;
		return this;
	}

	public HeadingAction time (int time)
	{
		// bulletTimer.setStopTime (time);
		return this;
	}

	public HeadingAction dontWait ()
	{
		// actionDone = true;
		return this;
	}

	public HeadingAction active ()
	{
		// bullet.setHeadingActive (true);
		return this;
	}

	public HeadingAction unactive ()
	{
		// bullet.setHeadingActive (false);
		return this;
	}
}
