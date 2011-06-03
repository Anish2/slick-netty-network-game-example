
package de.iritgo.skillfull.bullet;


import com.artemis.utils.Bag;

import de.iritgo.networkgame.BulletSeq;


public class BulletDirector
{
	private static boolean ACTION_DONE = true;

	private Bag<Bullet> bullets;

	private Bag<Bag<BulletAction>> bulletActionBags;

	private BulletTimer bulletTimer;

	private int bulletDone;

	private Bag<BulletAction> actions = null;


	SequenceDirectorIterator seqDirector;

	public BulletDirector ()
	{
		bulletTimer = new BulletTimer ();
		actions = new Bag<BulletAction> (10);
	}

	public void update (int delta)
	{
		bulletTimer.update (delta);

		if (actions.size () == 0)
		{
			actions = nextActions (actions);
		}
		for (int i = 0; i < bullets.size (); ++i)
		{
			Bullet bullet = bullets.get (i);
			if (bullet != null)
			{
				performActions (delta, bullet, actions);
			}
		}
		for (int i = 0 ; i < actions.size () ; ++i)
		{
			BulletAction action = actions.get (i);
			if (! action.isInTime ())
			{
				actions.remove (i--);
			}
		}
	}

	private boolean performActions (int delta, Bullet bullet, Bag<BulletAction> actions)
	{
		for (int i = 0 ; i < actions.size () ; ++i)
		{
			BulletAction action = actions.get (i);
			action.init (bullet);
			if (action.isInTime ())
				action.perform (this, bullet);
			else
			{
				action.updateOverlapTime ();
				action.performDone (this, bullet);
				action.setInit (false);
			}
		}
		return false;
	}

	private Bag<BulletAction> nextActions (Bag<BulletAction> actions)
	{
		actions.clear ();
		while (seqDirector.hasNext ())
		{
			BulletAction action = seqDirector.next ();
			action.setBulletTimer (bulletTimer);
			action.updateTime (0);
			actions.add (action);
			if (action.getStopTime () == 0)
				continue;
			return actions;
		}
		return null;
	}

	public void reset ()
	{
	}

	public void createBullets (int numOfBullets)
	{
		seqDirector = createIterator ();
		BulletFactory bulletFactory = new BulletFactory (numOfBullets);
		BulletActionFactory bulletActionFactory = new BulletActionFactory (numOfBullets);
		seqDirector.setBulletFactory (bulletActionFactory);

		bullets = new Bag<Bullet> (numOfBullets);
		bulletActionBags = new Bag<Bag<BulletAction>> (numOfBullets);

		for (int i = 0; i < numOfBullets; ++i)
		{
			Bullet bullet = bulletFactory.createBullet ();
			bullets.add (bullet);
			bulletActionBags.add (new Bag<BulletAction> (10));
		}
	}

	public SequenceDirectorIterator createIterator ()
	{
		return null;
	}

	public Bag<Bullet> getBullets ()
	{
		return bullets;
	}
}
