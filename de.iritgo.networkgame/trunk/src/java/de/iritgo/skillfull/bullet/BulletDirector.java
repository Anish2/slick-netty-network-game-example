
package de.iritgo.skillfull.bullet;


import com.artemis.utils.Bag;

import de.iritgo.networkgame.BulletSeq;


public class BulletDirector
{
	private static boolean ACTION_DONE = true;

	private Bag<Bullet> bullets;

	private Bag<SequenceDirectorIterator> directors;

	private Bag<Bag<BulletAction>> bulletActionBags;

	private BulletTimer bulletTimer;

	private int bulletDone;

	public BulletDirector ()
	{
		bulletTimer = new BulletTimer ();
	}

	public void update (int delta)
	{
		bulletTimer.update (delta);

		for (int i = 0; i < bullets.size (); ++i)
		{
			Bullet bullet = bullets.get (i);
			if (bullet != null)
			{
				Bag<BulletAction> bulletActions = bulletActionBags.get (i);
				if (bulletActions == null)
				{
					bulletActionBags.set (i, new Bag<BulletAction> (10));
				}

				boolean doMoreAction = true;
				while (doMoreAction)
				{
					addActions (i, bulletActions);
					doMoreAction = performActions (delta, bullet, bulletActions);
				}
			}
		}
	}

	private boolean performActions (int delta, Bullet bullet, Bag<BulletAction> bulletActions)
	{
		for (int j = 0; j < bulletActions.size (); ++j)
		{
			BulletAction action = bulletActions.get (j);
			if (action.isInTime ())
			{
				action.perform (delta, this, bullet);
			}
			else
			{
				action.performDone (delta, this, bullet);
				action.updateOverlapTime ();
				bulletActions.remove (j--);
				if (bulletActions.size () == 0)
				{
					return true;
				}
			}
		}
		return false;
	}

	private void addActions (int i, Bag<BulletAction> bulletActions)
	{
		if (bulletActions.size () == 0)
		{
			SequenceDirectorIterator seqDirector = directors.get (i);
			if (seqDirector.hasNext ())
			{
				BulletAction action = seqDirector.next ();
				action.setBulletTimer (bulletTimer);
				action.updateTime (0);
				bulletActions.add (action);
			}
		}
	}

	public void reset ()
	{
	}

	public void createBullets (int numOfBullets)
	{
		bullets = new Bag<Bullet> (numOfBullets);
		directors = new Bag<SequenceDirectorIterator> (numOfBullets);
		bulletActionBags = new Bag<Bag<BulletAction>> (numOfBullets);

		for (int i = 0; i < numOfBullets; ++i)
		{
			Bullet bullet = new Bullet ();
			bullets.add (bullet);
			bulletActionBags.add (new Bag<BulletAction> (10));
			directors.add (createIterator (bullet));
		}
	}

	public SequenceDirectorIterator createIterator (Bullet bullet)
	{
		return null;
	}

	public Bag<Bullet> getBullets ()
	{
		return bullets;
	}
}
