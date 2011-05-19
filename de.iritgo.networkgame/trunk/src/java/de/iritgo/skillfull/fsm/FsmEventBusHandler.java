package de.iritgo.skillfull.fsm;


public interface FsmEventBusHandler<Z>
{
	public abstract boolean validFor (Z type);
}
