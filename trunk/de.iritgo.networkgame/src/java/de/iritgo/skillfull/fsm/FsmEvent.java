package de.iritgo.skillfull.fsm;

public interface FsmEvent<T>
{
	public int getFsmTypeId ();
	
	public T getMessage ();
}
