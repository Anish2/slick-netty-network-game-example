package de.iritgo.skillfull.fsm;

/*
 * State.java
 *
 * Created on September 26, 2003, 4:48 PM
 */

/**
 *
 * @author  cyphre
 */

import java.util.Vector;

public class State
{
	public static int ON_ENTER = 0;
    public static int ON_DO = 1;
    public static int ON_EXIT = 2;
    public static int ON_EVENT = 3;

    private static final int MAX_SPECIFICATIONS = 10;

    public String name = "";
    public int numTransitions = 0;
    public int numEvents = 0;
    public int maxEvents = 6;

    public Vector specifications;
    public Vector transitions;
	private ActionProcessor actionProcessor;

    /** Creates a new instance of State */
    public State ()
    {
        specifications = new Vector();
        transitions = new Vector();
        for(int i = 0;i < MAX_SPECIFICATIONS ;++i){
            specifications.addElement(new Specification());
        }
    }
    
    public State (ActionProcessor actionProcessor)
    {
    	this();
    	this.actionProcessor = actionProcessor;
    }

    public void setName (String n)
    {
        this.name = n;
    }

    public void addTransition (FsmEvent e, State s)
    {
    	if (numTransitions < maxEvents)
    	{
            transitions.add (new Transition (e,s));
            numTransitions++;
        }
    }

    public void addAction (int actionElementNumber, String actionName, FsmEvent fsmEvent, Action action)
    {
    	if (actionElementNumber < ON_EVENT)
    	{
            ((Specification)specifications.elementAt(actionElementNumber)).name = actionName;
            ((Specification)specifications.elementAt(actionElementNumber)).action = action;
        }
    	else
        {
            if (numEvents < maxEvents)
            {
                ((Specification)specifications.elementAt(actionElementNumber + numEvents)).name = actionName;
                ((Specification)specifications.elementAt(actionElementNumber + numEvents)).event = fsmEvent;
                ((Specification)specifications.elementAt(actionElementNumber + numEvents)).action = action;
                numEvents++;
            }
        }
    }

    public boolean incoming (FsmEvent fsmEvent, Fsm f)
    {
    	if (numEvents > 0)
    	{
            Specification spec = (Specification)specifications.elementAt (ON_ENTER);
            if (spec.action != null)
            {
            	actionProcessor.execute (spec.action, fsmEvent);
            }

    		for(int i = 0;i < numEvents;++i)
            {
                spec = (Specification)specifications.elementAt (ON_EVENT + i);
                if (spec.event.getFsmTypeId () == fsmEvent.getFsmTypeId ())
                {
                	actionProcessor.execute (spec.action, fsmEvent);
                    return true;
                }
            }
        }
    	else
    	{
            for(int i = 0;i < ON_DO;++i)
            {
                Specification spec = (Specification)specifications.elementAt(i);
                if (spec.action != null)
                {
                	actionProcessor.execute (spec.action, fsmEvent);
                }
            }
        }
    	return false;
    }

    public State outgoing (FsmEvent fsmEvent, Fsm f)
    {
    	Specification spec = (Specification)specifications.elementAt (ON_EXIT);
        for(int i = 0;i < numTransitions;++i)
        {
            Transition tran = (Transition)transitions.elementAt(i);
            if (tran.event.getFsmTypeId () == fsmEvent.getFsmTypeId ())
            {
            	if (spec.action != null)
            	{
                	actionProcessor.execute (spec.action, fsmEvent);
                }
            	return tran.state;
            }
        }

        for(int i = 0;i < numEvents;++i)
        {
            spec = (Specification)specifications.elementAt (ON_EVENT + i);
            if (spec.event.getFsmTypeId () == fsmEvent.getFsmTypeId ())
            {
            	actionProcessor.execute (spec.action, fsmEvent);
                return null;
            }
        }
        return null;
    }

    class Transition
    {
        FsmEvent event = null;
        State state = null;

        public Transition(FsmEvent e, State s)
        {
        	event = e;
        	state = s;
        }
    }

    class Specification
    {
        String name = null;
        FsmEvent event = null;
        Action action = null;
    }
}
