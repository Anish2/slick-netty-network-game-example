package de.iritgo.skillfull.eventbus;


public class EventBusManager
{
	private EventDispatcher eventDispatcher;
	private SimpleEventBus simpleEventBus = new SimpleEventBus ();

	
	public void init ()
	{
		eventDispatcher = new EventDispatcher ();
		eventDispatcher.addEventbus (simpleEventBus);
	}
	
    public void publish(Event event)
    {
    	eventDispatcher.publish (event, false);
    }
    
    public void addEventBus (EventBus eventBus)
    {
    	eventDispatcher.addEventbus (eventBus);
    }

	public void subscribeOnSimpleEventBus (Class klass, EventHandler eventHandler)
	{
		simpleEventBus.subscribe (klass, eventHandler);
	}
}
