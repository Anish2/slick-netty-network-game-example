package de.iritgo.skillfull.bullet;

import de.iritgo.skillfull.bullet.action.DriveAction;
import de.iritgo.skillfull.bullet.action.PositionAction;
import de.iritgo.skillfull.bullet.pattern.BulletPattern;

public class BulletActionFactory
{
	private ActionData actionData;

	public BulletActionFactory (int bullets)
	{
		actionData = new ActionData (bullets);
	}

	public PositionAction createPositionAction (BulletPattern pattern)
	{
		PositionAction posAction = new PositionAction ();
		posAction.setActionData (actionData);
		posAction.setPattern (pattern);
		return posAction;
	}

	public DriveAction createDriveAction (BulletPattern pattern)
	{
		DriveAction driveAction = new DriveAction ();
		driveAction.setActionData (actionData);
		driveAction.setPattern (pattern);
		return driveAction;
	}
}
