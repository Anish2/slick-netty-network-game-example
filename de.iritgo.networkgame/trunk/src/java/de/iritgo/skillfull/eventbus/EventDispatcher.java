
package de.iritgo.skillfull.eventbus;


import java.util.HashMap;

import com.artemis.utils.Bag;


/**
 * Events published to the event dispatcher are publish to each event bus. In
 * addition one may set veto handlers to intercept the dispatching process.
 * 
 * @author synopia
 */
public class EventDispatcher
{
	private final HashMap<Class<? extends Event>, HandlerBus> vetoHandlers = new HashMap<Class<? extends Event>, HandlerBus> ();
	private Bag<EventBus> eventBuses = new Bag<EventBus> (10);
	
	/**
	 * <p>
	 * Assigns a veto handler to events. Whenever an event of class
	 * <code>cls</code> is published, the vetohandler <code>handler</code>
	 * controls the dispatching process.
	 * </p>
	 * <p>
	 * First, <code>handler.isVeto</code> is called <i>from current thread</i>.
	 * If this call returns false, the event is dispatched as if no veto handler
	 * is assigned. Otherwise, <code>handler.handleVeto</code> is called using
	 * the specified eventbus (actually, an envelope event (
	 * <code>VetoEvent</code>) is published to <code>bus</code>).
	 * 
	 * @param bus
	 *            The event bus, to "run" the handleVeto method on
	 * @param cls
	 *            The event type to veto
	 * @param handler
	 *            handles the veto
	 * @param <T>
	 *            The event type to veto
	 */
	public synchronized <T extends Event> void subscribeVeto (EventBus bus, Class<T> cls, VetoHandler<T> handler)
	{
		vetoHandlers.put (cls, new HandlerBus (handler, bus));
	}
	
	public void addEventbus (EventBus eventBus)
	{
		eventBuses.add (eventBus);
	}

	@SuppressWarnings("unchecked")
	public boolean isVeto (Event event)
	{
		Class<? extends Event> cls = event.getClass ();

		if (vetoHandlers.containsKey (cls))
		{
			HandlerBus handlerBus = vetoHandlers.get (cls);

			return handlerBus.vetoHandler.isVeto (event);
		}

		return false;
	}

	public void publish (Event event)
	{
		publish (event, false);
	}

	public void publish (Event event, boolean ignoreVeto)
	{
		if (ignoreVeto || ! isVeto (event))
		{
			for (int i = 0; eventBuses.size () > i; ++i)
			{
				eventBuses.get (i).publish (event);
			}
			/*
			 * modelEventBus.publish(event); swingEventBus.publish(event);
			 * client.publish(event); server.publish(event);
			 */
		}
		else
		{
			HandlerBus handlerBus = vetoHandlers.get (event.getClass ());

			handlerBus.bus.publish (new VetoEvent (event, handlerBus.vetoHandler));
		}
	}

	private static class HandlerBus
	{
		public final EventBus bus;

		public final VetoHandler vetoHandler;

		private HandlerBus (VetoHandler<?> vetoHandler, EventBus bus)
		{
			this.vetoHandler = vetoHandler;
			this.bus = bus;
		}
	}
}
