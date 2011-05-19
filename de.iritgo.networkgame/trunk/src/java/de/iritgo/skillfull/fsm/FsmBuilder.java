package de.iritgo.skillfull.fsm;

import de.iritgo.skillfull.user.FsmEventTypes;
import de.iritgo.skillfull.user.network.LoginRequestMessage;


public class FsmBuilder
{
	State[] st = null;
	private int currentState;
	
	public FsmBuilder (int size, ActionProcessor actionProcessor)
	{
		st = new State[size];
		for(int i = 0;i < st.length;++i)
		{
            st[i] = new State (actionProcessor);
        }
	}
	
	public FsmBuilder state (int state)
	{
		this.currentState = state;
		return this;
	}
	
	public <T extends FsmEvent> FsmBuilder addTrans (T event)
	{
		st[currentState].setName (event.getClass ().getSimpleName ());
		st[currentState].addTransition (event, st[event.getFsmTypeId ()]);
		return this;
	}
	
	public <T extends Action, t extends FsmEvent> FsmBuilder addAction (int baseTypeId, t event, T action)
	{
		st[currentState].addAction (baseTypeId, action.getClass ().getSimpleName (), event, action);
		return this;
	}

	public FsmBuilder name (String name)
	{
		st[currentState].setName (name);
		return this;
	}
	
	public State[] getStates ()
	{
		return st;
	}
}
