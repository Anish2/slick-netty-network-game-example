package de.iritgo.skillfull.bullet.action;

import com.artemis.utils.Utils;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.BulletTimer;

public class MoveAction extends BulletAction
{
	private int timer;
	private BulletTimer bulletTimer;
	private float x;
	private float y;
	private Bullet initBullet;
	private boolean wait;

	public MoveAction (Bullet bullet)
	{
		bulletTimer = new BulletTimer ();
		initBullet = new Bullet (bullet);
		wait = false;
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		bulletTimer.update (delta);
		if (! bulletTimer.isValid ())
		{
			bullet.setX (x);
			bullet.setY (y);
			inactive ();
			return true;
		}
		float posInTime = (float) bulletTimer.getTime () / (float) bulletTimer.getStopTime ();
		if (posInTime > 1)
		{
			posInTime = 1;
		}
		bullet.setX (Utils.lerp (initBullet.getX (), x, posInTime));
		bullet.setY (Utils.lerp (initBullet.getY (), y, posInTime));

//		System.out.println (posInTime + " : " + bullet.getX () + "/" + bullet.getY ());
		return ! wait;
	}

	public MoveAction to (float x, float y)
	{
		this.x = x;
		this.y = y;
		return this;
	}

	public MoveAction time (int time)
	{
		bulletTimer.setStopTime (time);
		return this;
	}

	public MoveAction block ()
	{
		wait = true;
		return this;
	}
}
