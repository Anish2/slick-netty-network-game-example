package de.iritgo.skillfull.user.network;

import de.iritgo.skillfull.eventbus.Event;
import de.iritgo.skillfull.fsm.FsmBusEvent;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.user.FsmEventTypes;


public class LoginRequestMessage extends UserMessage
{
	private String name;
	
	private String password;
	
	private String gameName;
	
	public LoginRequestMessage ()
	{
		super (Opcode.LOGIN);
	}
	
	public LoginRequestMessage (String name, String password, String gameName)
	{
		super (Opcode.LOGIN);
		this.name = name;
		this.password = password;
		this.gameName = gameName;
	}

	@Override
	public int getFsmTypeId ()
	{
		return FsmEventTypes.LOGIN.id ();
	}

	@Override
	public UserMessage getMessage ()
	{
		return this;
	}

	@Override
	public void transfer ()
	{
		channelBuffer.writeByte (Opcode.LOGIN.getObcode ());
		channelBuffer.writeInt (4 + name.length ()
						+ 4 + password.length ()
						+ 4 + gameName.length ()
		);
		channelBuffer.writeInt (name.length ());
		channelBuffer.writeBytes (name.getBytes ());
		channelBuffer.writeInt (password.length ());
		channelBuffer.writeBytes (password.getBytes ());
		channelBuffer.writeInt (gameName.length ());
		channelBuffer.writeBytes (gameName.getBytes ());
	}
	
	public int getMessageSize ()
	{
		messageSize += opcode.getSize () + 12;
		messageSize += name.length ();
		messageSize += password.length ();
		messageSize += gameName.length ();
		return messageSize;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	public void setPassword (String password)
	{
		this.password = password;
	}

	public void setGameName (String gameName)
	{
		this.gameName = gameName;
	}

	public String getName ()
	{
		return name;
	}

	public String getPassword ()
	{
		return password;
	}

	public String getGameName ()
	{
		return gameName;
	}
	
	@Override
	public boolean isReliableMessage ()
	{
		return true;
	}
}
