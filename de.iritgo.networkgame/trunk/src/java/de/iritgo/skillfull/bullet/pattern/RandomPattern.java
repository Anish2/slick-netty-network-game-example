package de.iritgo.skillfull.bullet.pattern;

import java.util.Random;

public class RandomPattern implements BulletPattern
{
	private Random random;
	private int range;
	private int start;

	public RandomPattern (int range)
	{
		this.random = new Random ();
		this.range = range;
	}

	public RandomPattern (int start, int range)
	{
		this.random = new Random ();
		this.range = range;
		this.start = start;
	}

	@Override
	public float getOffsetAcceleration ()
	{
		return random.nextInt (range);
	}

	@Override
	public float getOffsetRotation ()
	{
		return start + random.nextInt (range);
	}

	@Override
	public float getOffsetSpeed ()
	{
		return random.nextInt (range);
	}

	@Override
	public float getOffsetX ()
	{
		return random.nextInt (range);
	}

	@Override
	public float getOffsetY ()
	{
		return random.nextInt (range);
	}
}
