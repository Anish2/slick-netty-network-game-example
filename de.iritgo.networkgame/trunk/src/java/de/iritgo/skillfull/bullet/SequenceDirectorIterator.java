package de.iritgo.skillfull.bullet;

import de.iritgo.skillfull.bullet.action.DriveAction;
import de.iritgo.skillfull.bullet.action.HeadingAction;
import de.iritgo.skillfull.bullet.action.MoveAction;
import de.iritgo.skillfull.bullet.action.PositionAction;
import de.iritgo.skillfull.bullet.action.TestAction;
import de.iritgo.skillfull.bullet.action.WaitTimerAction;
import de.iritgo.skillfull.bullet.pattern.BulletPatternContext;
import de.iritgo.skillfull.bullet.pattern.DoNothingPattern;
import de.matthiasmann.continuations.SuspendExecution;


public class SequenceDirectorIterator extends de.matthiasmann.continuations.CoIterator<BulletAction>
{
	private static final long serialVersionUID = 1L;

	protected BulletPatternContext pattern;

	private BulletActionFactory actionFactory;

	public SequenceDirectorIterator ()
	{
		pattern = new BulletPatternContext ();
		pattern.setPattern (new DoNothingPattern ());
	}

	public void setBulletFactory (BulletActionFactory actionFactory)
	{
		this.actionFactory = actionFactory;
	}

	@Override
	protected void run () throws SuspendExecution
	{
	}

	private BulletAction createBullets (int i)
	{
		return null;
	}

	protected BulletAction test (String text)
	{
		return new TestAction (text);
	}

	protected MoveAction move ()
	{
		return new MoveAction ();
	}

	protected DriveAction drive ()
	{
		return actionFactory.createDriveAction (pattern);
	}

	protected PositionAction position ()
	{
		return actionFactory.createPositionAction (pattern);
	}

	protected WaitTimerAction waitTimer ()
	{
		return new WaitTimerAction ();
	}

	protected HeadingAction heading ()
	{
		return new HeadingAction ();
	}
}
