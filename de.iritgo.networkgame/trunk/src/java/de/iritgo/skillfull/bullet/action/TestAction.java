package de.iritgo.skillfull.bullet.action;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;

public class TestAction extends BulletAction
{
	private String text;

	public TestAction (String text)
	{
		this.text = text;
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		System.out.println (text);
		inactive ();
		
		return true;
	}
}
