package de.iritgo.skillfull.bullet.pattern;

public class BulletPatternContext implements BulletPattern
{
	private BulletPattern currentPattern;


	public void setPattern (BulletPattern bulletPattern)
	{
		this.currentPattern = bulletPattern;
	}

	@Override
	public float getOffsetAcceleration ()
	{
		return currentPattern.getOffsetAcceleration ();
	}

	@Override
	public float getOffsetRotation ()
	{
		return currentPattern.getOffsetRotation ();
	}

	@Override
	public float getOffsetSpeed ()
	{
		return currentPattern.getOffsetSpeed ();
	}

	@Override
	public float getOffsetX ()
	{
		return currentPattern.getOffsetX ();
	}

	@Override
	public float getOffsetY ()
	{
		return currentPattern.getOffsetY ();
	}
}
