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
		produce (position ().xy (100 + r.nextInt (50), 200));
		produce (drive (bullet).rotate (90).time (2000).block ());
		

		float clickOut = r.nextFloat () * r.nextInt (2);
		int wait = r.nextInt (500);
		
		if (r.nextBoolean () == true)
		{
			produce (drive (bullet).rotate (90).time (wait / 2).block ());
			produce (drive (bullet).speed (8.0f + clickOut).acceleration (-1.0f).rotate (90).time (700).block ());
		}
		else
		{
			produce (drive (bullet).rotate (270).time (wait / 2).block ());
			produce (drive (bullet).speed (8.0f - clickOut).acceleration (-1.0f).rotate (270).time (700).block ());
		}	
		produce (drive (bullet).rotate (0).time (wait).block ());
		produce (drive (bullet).acceleration (1.0f).rotate (0).time (900).block ());
		
		produce (waitTimer ().start (director.getTimer ()).stop (startTime + 5000));
		int rad = 260 - (++shotRad);
		produce (drive (bullet).rotate (rad).time (50).block ());		
		produce (drive (bullet).acceleration (0.1f).rotate (rad).time (2900).block ());
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
