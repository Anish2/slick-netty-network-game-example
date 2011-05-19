package de.iritgo.skillfull.fsm;

public class SimpleActionProcessor implements ActionProcessor
{

	@Override
	public void execute (Action action, FsmEvent event)
	{
		action.execute (event);
	}
}
