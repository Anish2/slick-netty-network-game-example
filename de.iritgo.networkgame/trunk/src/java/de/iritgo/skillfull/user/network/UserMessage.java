package de.iritgo.skillfull.user.network;

import java.net.SocketAddress;

import de.iritgo.skillfull.fsm.FsmBusEvent;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.user.FsmEventTypes;

public abstract class UserMessage extends Message implements FsmBusEvent<UserMessage, FsmEventTypes>
{
	private SocketAddress remoteAddress;

	public UserMessage (Opcode opcode)
	{
		super (opcode);
	}

	public void setRemoteAddress (SocketAddress address)
	{
		this.remoteAddress = address;
	}
	
	public SocketAddress getRemoteAddress ()
	{
		return remoteAddress;
	}
	
	@Override
	public String getFsmSimpleBaseName ()
	{
		return "UserMessage";
	}
}
