package de.iritgo.skillfull.fsm;

import de.iritgo.skillfull.eventbus.Event;

public interface FsmBusEvent<T, t> extends FsmEvent<T>, Event
{
	public String getFsmSimpleBaseName ();
}
