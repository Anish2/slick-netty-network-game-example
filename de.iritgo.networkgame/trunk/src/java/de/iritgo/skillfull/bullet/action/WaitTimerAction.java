package de.iritgo.skillfull.bullet.action;

import com.artemis.utils.Utils;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.BulletTimer;

public class WaitTimerAction extends BulletAction
{
	
	
	private long stop;
	private long start;

	public WaitTimerAction ()
	{
	}

	@Override
	public boolean perform (int delta, BulletDirector bulletDirector, Bullet bullet)
	{
		start += delta;
		if (start > stop)
		{
			inactive ();
			return true;
		}
		return false;
	}
	
	public WaitTimerAction stop (long time)
	{
		stop = time;
		return this;
	}

	public WaitTimerAction start (long time)
	{
		start = time;
		return this;
	}
}
