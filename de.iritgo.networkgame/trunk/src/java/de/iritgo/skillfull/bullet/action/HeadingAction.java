package de.iritgo.skillfull.bullet.action;

import com.artemis.utils.Utils;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.BulletTimer;

public class HeadingAction extends BulletAction
{
	private int timer;
	private BulletTimer bulletTimer;
	private float x;
	private float y;
	private Bullet initBullet;
	private float rotation;
	private float fromRotation;
	private Bullet bullet;

	public HeadingAction (Bullet bullet)
	{
		bulletTimer = new BulletTimer ();
		initBullet = new Bullet (bullet);
		this.bullet = bullet;
		actionDone = false;
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		if (! bulletTimer.isValid ())
		{
			inactive ();
			return actionDone = true;
		}
		
		bulletTimer.update (delta);

		float posInTime = (float) bulletTimer.getTime () / (float) bulletTimer.getStopTime ();
		if (posInTime > 1)
		{
			posInTime = 1;
		}
		
		bullet.setHeading (Utils.lerpDegrees (fromRotation, rotation, posInTime));
		return actionDone;
	}

	public HeadingAction to (float x, float y)
	{
		this.x = x;
		this.y = y;
		return this;
	}
	
	public HeadingAction rotate (float rotation)
	{
		this.rotation = rotation;
		return this;
	}
	
	public HeadingAction fromRotation ()
	{
		this.fromRotation = initBullet.getRotation ();
		return this;
	}

	public HeadingAction fromHeading ()
	{
		this.fromRotation = initBullet.getHeading ();
		return this;
	}

	public HeadingAction time (int time)
	{
		bulletTimer.setStopTime (time);
		return this;
	}

	public HeadingAction dontWait ()
	{
		actionDone = true;
		return this;
	}
	
	public HeadingAction active ()
	{
		bullet.setHeadingActive (true);
		return this;
	}

	public HeadingAction unactive ()
	{
		bullet.setHeadingActive (false);
		return this;
	}
}
