package de.iritgo.skillfull.bullet;

import de.iritgo.skillfull.component.motion.Position;

public class Bullet extends Position
{
	private float acceleration;
	private float movedWay;
	private float lastSpeed;

	public Bullet ()
	{
	}

	public Bullet (Bullet bullet)
	{
		super (bullet);
	}

	public void setAcceleration (float acceleration)
	{
		this.acceleration = acceleration;
	}

	public float getAcceleration ()
	{
		return acceleration;
	}

	public void setMovedWay (float movedWay)
	{
		this.movedWay = movedWay;
	}

	public float getMovedWay ()
	{
		return movedWay;
	}

	public float getLastSpeed ()
	{
		return lastSpeed;
	}

	public void setLastSpeed (float lastSpeed)
	{
		this.lastSpeed = lastSpeed;
	}
}
