package de.iritgo.skillfull.bullet.pattern;

import java.util.Random;

public class RandomPattern implements BulletPattern
{
	private Random random;
	private int range;

	public RandomPattern (int range)
	{
		this.random = new Random ();
		this.range = range;
	}

	@Override
	public float getOffsetAcceleration ()
	{
		return random.nextInt (range);
	}

	@Override
	public float getOffsetRotation ()
	{
		return random.nextInt (range);
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
