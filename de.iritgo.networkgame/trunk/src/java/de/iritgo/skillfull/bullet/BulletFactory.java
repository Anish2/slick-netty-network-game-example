package de.iritgo.skillfull.bullet;

public class BulletFactory
{
	private BulletData bulletData;
	private int bulletCount;

	public BulletFactory (int bullets)
	{
		bulletCount = 0;
		bulletData = new BulletData (bullets);
	}

	public Bullet createBullet ()
	{
		Bullet bullet = new Bullet ();
		bullet.setBulletData (bulletData, bulletCount++);
		return bullet;
	}
}
