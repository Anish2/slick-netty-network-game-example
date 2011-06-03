package de.iritgo.skillfull.bullet.action;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.pattern.BulletPattern;

public class PositionAction extends BulletAction
{
	private int x;
	private int y;

	@Override
	protected void actionInit ()
	{
		setActionDone (true);
	}


	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		inactive ();
		return true;
	}

	@Override
	public void performDone (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		bullet.setX (x + pattern.getOffsetX ());
		bullet.setY (y + pattern.getOffsetY ());
	}

	public PositionAction xy (int x, int y)
	{
		this.x = x;
		this.y = y;
		return this;
	}
}
