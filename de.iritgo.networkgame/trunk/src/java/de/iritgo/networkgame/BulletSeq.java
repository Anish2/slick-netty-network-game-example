package de.iritgo.networkgame;

import java.sql.Driver;
import java.util.Random;

import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletAction;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.SequenceDirectorIterator;
import de.matthiasmann.continuations.SuspendExecution;

public class BulletSeq extends SequenceDirectorIterator
{
	private Bullet bullet;

	private Random r = new Random ();

	private long startTime;

	private BulletDirector director;

	private static int shotRad = 0;

	public BulletSeq (Bullet bullet, BulletDirector director)
	{
		this.bullet = bullet;
		this.director = director;
	}


	@Override
	protected void run () throws SuspendExecution
	{
		while (true)
		{
			startTime = director.getTimer ();
			produce (waitTimer ().start (startTime).stop (startTime + 1500));

		produce (position ().xy (100 + r.nextInt (50), 250 + r.nextInt (20)));
		produce (drive (bullet).rotate (0).time (500).block ());
		produce (drive (bullet).acceleration (0.5f).rotate (0).time (500).block ());
//		produce (test ("->" + bullet.getLastSpeed ()));
		produce (drive (bullet).speed (bullet.getLastSpeed ()).rotate (0).time (1500).block ());
//		produce (test ("->" + bullet.getLastSpeed ()));
		produce (drive (bullet).speed (bullet.getLastSpeed ()).acceleration (-0.5f).rotate (0).time (1000).block ());
//		produce (test ("<-" + bullet.getLastSpeed ()));
//		produce (test ("Move around: " + bullet.getLastSpeed ()));
		int q = 0;
		while (q < 5)
		{
			++q;
			produce (drive (bullet).speed (bullet.getLastSpeed () * 0.8f).rotate (r.nextInt (360)).time (1000).block ());
//			produce (test ("<->" + bullet.getLastSpeed ()));
		}
//		produce (test ("<->" + bullet.getLastSpeed ()));
//		produce (test ("DONE"));

		produce (move (bullet).to (100 + r.nextInt (50), 250 + r.nextInt (20)).time (2000).block ());
		produce (drive (bullet).rotate (90).time (500).block ());


		float clickOut = r.nextFloat () * r.nextInt (2);
		int wait = r.nextInt (500);

		if (r.nextBoolean () == true)
		{
			produce (drive (bullet).rotate (90).time (wait / 2).block ());
			produce (drive (bullet).speed (150.0f + r.nextInt (50)).acceleration (-0.2f).rotate (90).time (900).block ());
		}
		else
		{
			produce (drive (bullet).rotate (270).time (wait / 2).block ());
			produce (drive (bullet).speed (150.0f + r.nextInt (50)).acceleration (-0.2f).rotate (270).time (900).block ());
		}
		produce (drive (bullet).rotate (0).time (500+wait).block ());
		produce (drive (bullet).acceleration (2.0f).rotate (0).time (800).block ());

		startTime = director.getTimer ();

		produce (waitTimer ().start (director.getTimer ()).stop (startTime + 1000));
		int rad = 260 - (++shotRad);
		produce (drive (bullet).rotate (rad).time (50).block ());
		produce (drive (bullet).acceleration (2.0f).rotate (rad).time (1500).block ());
		}

//		produce (drive (bullet).rotate (180).time (1000));
//		while (true)
//		{
//		produce (drive (bullet).speed (1.0f).rotate (0).time (1000).block ());
//		produce (drive (bullet).speed (1.0f).rotate (0).time (1000).block ());
//		produce (drive (bullet).speed (1.0f).rotate (0).time (1000).block ());
//		produce (drive (bullet).speed (1.0f).rotate (0).time (3000).block ());
//		produce (drive (bullet).speed (1.0f).rotate (270).time (1000).block ());
//		produce (drive (bullet).speed (1.0f).rotate (270).time (1000).block ());
//		produce (drive (bullet).speed (1.0f).rotate (270).time (1000).block ());
//		produce (drive (bullet).speed (1.0f).rotate (180).time (3000).block ());
//		}
	}
}
