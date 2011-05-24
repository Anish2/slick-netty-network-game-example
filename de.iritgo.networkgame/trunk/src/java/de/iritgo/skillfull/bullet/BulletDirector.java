package de.iritgo.skillfull.bullet;

import com.artemis.utils.Bag;

import de.iritgo.networkgame.BulletSeq;

public class BulletDirector
{
	private Bag<Bullet> bullets;
	private Bag<SequenceDirectorIterator> directors;
	private Bag<Bag<BulletAction>> bulletActionBags;
	private long timer;
	private int blaTimer;
	private int bulletDone;

	public void update (int delta)
	{
		timer += delta;

		for (int i = 0 ; i < bullets.size () ; ++i)
		{
			Bullet bullet = bullets.get (i);
			if (bullet != null)
			{
				Bag<BulletAction> bulletActions = bulletActionBags.get (i);
				if (bulletActions == null)
				{
					bulletActionBags.set (i, new Bag<BulletAction> (10));
				}

				boolean performDirector = true;

				for (int j = 0; j < bulletActions.size (); ++j)
				{
					BulletAction action = bulletActions.get (j);
					if (action.isActive ())
					{
						if (! action.perform (delta, this, bullet))
						{
							performDirector = false;
						}
					}
				}
				if (performDirector)
				{
					SequenceDirectorIterator seqDirector = directors.get (i);
					if (seqDirector.hasNext ())
					{
						BulletAction action = seqDirector.next ();
						bulletActions.add (action);
//						action.perform (delta, this, bullet);
					}
					else
					{
						++bulletDone;
						if (bulletDone == bullets.size ())
						{
							System.out.println ("Done!");
						}
					}
				}
			}
		}
	}

	public long getTimer ()
	{
		return timer;
	}

	public void reset ()
	{
	}

	public void createBullets (int numOfBullets)
	{
		bullets = new Bag<Bullet> (numOfBullets);
		directors = new Bag<SequenceDirectorIterator> (numOfBullets);
		bulletActionBags = new Bag<Bag<BulletAction>> (numOfBullets);

		for (int i = 0 ; i < numOfBullets ; ++i)
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
