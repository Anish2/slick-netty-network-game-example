package de.iritgo.skillfull.eventbus;

public class SimpleEventBus extends EventBus
{
	@Override
	public void publish (Event event)
	{
        handleEvent( event );
	}
}
