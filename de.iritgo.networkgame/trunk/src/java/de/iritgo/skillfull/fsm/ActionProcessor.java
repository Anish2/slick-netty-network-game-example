package de.iritgo.skillfull.fsm;

public interface ActionProcessor
{
	public void execute (Action action, FsmEvent event);
}
