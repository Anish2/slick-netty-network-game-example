package de.iritgo.skillfull.fsm;

public class Fsm
{
    public FsmEvent event = null;
    public String response = "Idle";
    public State currState = null;
    public State state = null;
    public State[] states;

    public void addStates (State[] st, int initSt)
    {
        states = st;
        currState = states[initSt];
    }

    public boolean inEvent (FsmEvent fsmEvent)
    {
        if (currState != null)
        {
            return currState.incoming (fsmEvent, this);
        }
        return false;
    }

    public boolean onEvent (FsmEvent fsmEvent)
    {
        if ((states != null) && (currState != null))
        {
        	if (event != null && (fsmEvent.getFsmTypeId () == event.getFsmTypeId ()))
        	{
        		return false;
        	}
        	
            event = fsmEvent;
            State temp = currState.outgoing (fsmEvent, this);
            if (temp != null)
            {
                currState = temp;
                currState.incoming (fsmEvent, this);
                return true;
            }
        }
        event = null;
        return false;
    }

    public Fsm()
    {
    }
}
