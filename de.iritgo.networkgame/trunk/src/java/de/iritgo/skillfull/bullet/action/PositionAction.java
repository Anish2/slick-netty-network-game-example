package de.iritgo.skillfull.bullet.action;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;

public class PositionAction extends BulletAction
{
	private int x;
	private int y;

	public PositionAction ()
	{
		actionDone = true;
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		bullet.setLocation (x, y);
		inactive ();
		return true;
	}
	
	public PositionAction xy (int x, int y)
	{
		this.x = x;
		this.y = y;
		return this;
	}
}
