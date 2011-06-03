package de.iritgo.skillfull.bullet;


public class Bullet
{
	private BulletData bulletData;
	private int bulletDataIndex;

	public Bullet ()
	{
	}

	public int getId ()
	{
		return bulletDataIndex;
	}

	public void setBulletData (BulletData bulletData, int bulletDataIndex)
	{
		this.bulletData = bulletData;
		this.bulletDataIndex = bulletDataIndex;
	}

	public void setAcceleration (float acceleration)
	{
		bulletData.acceleration[bulletDataIndex] = acceleration;
	}

	public float getAcceleration ()
	{
		return bulletData.acceleration[bulletDataIndex];
	}

	public float getSpeed ()
	{
		return bulletData.speed[bulletDataIndex];
	}

	public void setSpeed (float speed)
	{
		bulletData.speed[bulletDataIndex] = speed;
	}

	public void setHeading (float heading)
	{
		bulletData.heading[bulletDataIndex] = heading;
	}

	public float getHeading ()
	{
		return bulletData.heading[bulletDataIndex];
	}

	public boolean isHeadingActive ()
	{
		return 	bulletData.headingActive[bulletDataIndex];
	}

	public void setHeadingActive (boolean active)
	{
		bulletData.headingActive[bulletDataIndex] = active;
	}

	public float getX ()
	{
		return bulletData.x[bulletDataIndex];
	}

	public void setX (float x)
	{
		bulletData.x[bulletDataIndex] = x;
	}

	public float getY ()
	{
		return bulletData.y[bulletDataIndex];
	}

	public void setY (float y)
	{
		bulletData.y[bulletDataIndex] = y;
	}

	public float getRotation ()
	{
		return bulletData.rotation[bulletDataIndex];
	}

	public void setRotation (float rotation)
	{
		bulletData.rotation[bulletDataIndex] = rotation;
	}
}
