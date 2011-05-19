
package de.iritgo.skillfull.fsm;


import java.util.List;

import com.artemis.utils.Bag;

import de.iritgo.skillfull.eventbus.Event;
import de.iritgo.skillfull.eventbus.EventBus;
import de.iritgo.skillfull.eventbus.EventHandler;


public class FsmEventBus extends EventBus
{
	private Bag<Fsm> fsms = new Bag<Fsm> (50);

	public void addFsm (Fsm fsm)
	{
		fsms.add (fsm);
	}

	@Override
	public void publish (Event event)
	{
		handleEvent (event);
	}

	protected synchronized void getCallbacks (Event event, List<EventHandler> resultCallbacks)
	{
		Class cls = event.getClass ();
		if (event instanceof FsmBusEvent)
		{
			FsmBusEvent fsmBusEvent = (FsmBusEvent) event;
			for (Class clazz : handlers.keySet ())
			{
				if (clazz.getSimpleName ().equals (fsmBusEvent.getFsmSimpleBaseName ()))
				{
					List<EventHandler<?>> callbackList = handlers.get (clazz);
					
					for (EventHandler callback : callbackList)
					{
						resultCallbacks.add (callback);
					}
					break;
				}
			}
		}
	}
}
