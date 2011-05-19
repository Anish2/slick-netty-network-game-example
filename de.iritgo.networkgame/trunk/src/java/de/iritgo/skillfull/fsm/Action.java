package de.iritgo.skillfull.fsm;

public interface Action<E extends FsmEvent>
{
	public boolean execute (E event);
}
