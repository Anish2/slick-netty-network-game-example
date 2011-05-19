package de.iritgo.skillfull.user;

import de.iritgo.skillfull.eventbus.EventHandler;
import de.iritgo.skillfull.fsm.Fsm;
import de.iritgo.skillfull.fsm.FsmEventBusHandler;
import de.iritgo.skillfull.user.network.UserMessage;

public class UserFsmBusEventHandler implements FsmEventBusHandler<FsmEventTypes>, EventHandler<UserMessage>
{
	private Fsm fsm;

	public UserFsmBusEventHandler (Fsm fsm)
	{
		this.fsm = fsm;
	}

	@Override
	public boolean validFor (FsmEventTypes type)
	{
		if (type.getClass () == FsmEventTypes.class)
		{
			return true;
		}

		for (FsmEventTypes type1 : FsmEventTypes.values ())
		{
			if (type1 == type)
				return true;
		}
		return false;
	}

	@Override
	public void handleEvent (UserMessage event)
	{
		fsm.onEvent (event);
	}
}
