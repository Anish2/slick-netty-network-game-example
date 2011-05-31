package de.iritgo.skillfull.bullet.action;

import com.artemis.utils.Utils;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.BulletTimer;

public class MoveAction extends BulletAction
{
	private float x;
	private float y;
	private Bullet initBullet;

	public MoveAction (Bullet bullet)
	{
		initBullet = new Bullet (bullet);
		actionDone = false;
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{

		float posInTime = (float) getTime () / (float) getStopTime ();
		if (posInTime > 1)
		{
			posInTime = 1;
		}
		bullet.setX (Utils.lerp (initBullet.getX (), x, posInTime));
		bullet.setY (Utils.lerp (initBullet.getY (), y, posInTime));
		
		bullet.setRotation (Utils.lerpDegrees (bullet.getRotation (), Utils.angleInDegrees (initBullet.getX (), initBullet.getY (), x, y), posInTime));

//		System.out.println (posInTime + " : " + bullet.getX () + "/" + bullet.getY ());
		return actionDone;
	}

	public MoveAction to (float x, float y)
	{
		this.x = x;
		this.y = y;
		return this;
	}

	public MoveAction time (int time)
	{
		stopTime (time);
		return this;
	}

	public MoveAction dontWait ()
	{
		actionDone = true;
		return this;
	}
}
