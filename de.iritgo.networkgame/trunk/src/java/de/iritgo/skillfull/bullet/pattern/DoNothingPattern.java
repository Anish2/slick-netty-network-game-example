package de.iritgo.skillfull.bullet.pattern;


public class DoNothingPattern implements BulletPattern
{
	public DoNothingPattern ()
	{
	}

	@Override
	public float getOffsetAcceleration ()
	{
		return 0;
	}

	@Override
	public float getOffsetRotation ()
	{
		return 0;
	}

	@Override
	public float getOffsetSpeed ()
	{
		return 0;
	}

	@Override
	public float getOffsetX ()
	{
		return 0;
	}

	@Override
	public float getOffsetY ()
	{
		return 0;
	}
}
