package de.iritgo.skillfull.user.fsm;

import de.iritgo.skillfull.fsm.FsmEventBus;
import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.user.FsmEventTypes;
import de.iritgo.skillfull.user.network.UserMessage;

public class IdleMessage extends UserMessage
{

	public IdleMessage ()
	{
		super (null);
	}

	@Override
	public int getFsmTypeId ()
	{
		return FsmEventTypes.IDLE.id ();
	}

	@Override
	public UserMessage getMessage ()
	{
		return this;
	}

}
