
package de.iritgo.networkgame;


import java.sql.Driver;
import java.util.Random;

import com.artemis.utils.TrigLUT;

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
		super (bullet);
		this.director = director;
	}

	@Override
	protected void run () throws SuspendExecution
	{
		while (true)
		{
//			produce (waitTimer ().start (startTime).stop (startTime + 1500));
//			produce (position ().xy (60, 240));
//			produce (drive ().rotate (0).dontWait ());
//			int x = 0;
//			while (x++ < 50)
//				produce (drive ().speed (50.0f).rotate (0).time (200));

			produce (position ().xy (100 + r.nextInt (50), 250 + r.nextInt (80)));
			produce (drive ().rotate (0));
			produce (drive ().acceleration (1.5f).rotate (0).time (200));
			produce (drive ().withLastSpeed ().rotate (0).time (1500));
			produce (drive ().withLastSpeed ().acceleration (- 1.5f).rotate (0).time (200));
			produce (drive ().speed (300f).rotate (180).time (50));
			int q = 0;

			produce (waitTimer ().start (startTime).stop (startTime + r.nextInt (1500)));

			while (q < 4)
			{
				++q;
				float sin = (float) (TrigLUT.sin (++shotRad) * (float) 60 + (- 15 + r.nextInt (30)));
				produce (drive ().withLastSpeed ().rotate (180 + sin).time (800));
			}
			q = 0;
			produce (drive ().speed (300f).rotate (0).time (50));
			while (q < 5)
			{
				++q;
				produce (drive ().withLastSpeed ().rotate (r.nextInt (360)).time (1000));
			}

			produce (move ().to (100 + r.nextInt (50), 250 + r.nextInt (20)).time (2000));

			int wait = r.nextInt (500);
			shotRad = 0;

			produce (heading ().rotate (0).active ().dontWait ());
			if (r.nextBoolean () == true)
			{
				produce (drive ().rotate (90).dontWait ());
				produce (drive ().speed (150.0f + r.nextInt (50)).acceleration (- 0.2f).rotate (90).time (900));
			}
			else
			{
				produce (drive ().rotate (270).dontWait ());
				produce (drive ().speed (150.0f + r.nextInt (50)).acceleration (- 0.2f).rotate (270).time (900));
			}
			produce (heading ().unactive ().dontWait ());
			produce (drive ().rotate (0).dontWait ());
			produce (drive ().acceleration (2.0f).rotate (0).time (800));


			int rad = 260 - (++shotRad);
			produce (drive ().rotate (rad).time (50));
			produce (drive ().acceleration (2.0f).rotate (rad).time (1500));
		}
	}
}
