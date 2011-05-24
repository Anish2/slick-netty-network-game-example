package de.iritgo.skillfull.bullet;


public class BulletAction
{
	private boolean active = true;

	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		return true;
	}

	public boolean isActive ()
	{
		return active;
	}
	
	protected void inactive ()
	{
		active = false;
	}
}
