package de.iritgo.skillfull.bullet;

import de.iritgo.skillfull.bullet.action.DriveAction;
import de.iritgo.skillfull.bullet.action.HeadingAction;
import de.iritgo.skillfull.bullet.action.MoveAction;
import de.iritgo.skillfull.bullet.action.PositionAction;
import de.iritgo.skillfull.bullet.action.TestAction;
import de.iritgo.skillfull.bullet.action.WaitTimerAction;
import de.matthiasmann.continuations.SuspendExecution;


public class SequenceDirectorIterator extends de.matthiasmann.continuations.CoIterator<BulletAction>
{
	private static final long serialVersionUID = 1L;
	private Bullet bullet;
	
	public SequenceDirectorIterator (Bullet bullet)
	{
		this.bullet = bullet;
	}
	
	@Override
	protected void run () throws SuspendExecution
	{
	}

	private BulletAction createBullets (int i)
	{
		return new BulletAction ();
	}
	
	protected BulletAction test (String text)
	{
		return new TestAction (text);
	}

	protected MoveAction move ()
	{
		return new MoveAction (bullet);
	}

	protected DriveAction drive ()
	{
		return new DriveAction (bullet);
	}

	protected PositionAction position ()
	{
		return new PositionAction ();
	}
	
	protected WaitTimerAction waitTimer ()
	{
		return new WaitTimerAction ();
	}

	protected HeadingAction heading ()
	{
		return new HeadingAction (bullet);
	}
}
