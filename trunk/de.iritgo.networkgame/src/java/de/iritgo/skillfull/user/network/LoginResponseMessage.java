package de.iritgo.skillfull.user.network;

import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.user.FsmEventTypes;


public class LoginResponseMessage extends UserMessage
{
	private Byte loginState;
	private Byte userId;
	
	
	public LoginResponseMessage ()
	{
		super (Opcode.LOGIN_RESPONSE);
	}

	public LoginResponseMessage (byte userId, byte loginState)
	{
		this ();
		this.userId = userId;
		this.loginState = loginState;
	}


	@Override
	public int getFsmTypeId ()
	{
		return FsmEventTypes.LOGIN_RESPONSE.id ();
	}

	@Override
	public UserMessage getMessage ()
	{
		return this;
	}

	@Override
	public void transfer ()
	{
		channelBuffer.writeByte (Opcode.LOGIN_RESPONSE.getObcode ());
		channelBuffer.writeByte (userId);
		channelBuffer.writeByte (loginState);
	}

	public Byte getLoginState ()
	{
		return loginState;
	}
	
	public Byte getUserId ()
	{
		return userId;
	}
	
	@Override
	public boolean isReliableMessage ()
	{
		return true;
	}
	
	@Override
	public Message cloneMessage ()
	{
		LoginResponseMessage clone = new LoginResponseMessage (userId, loginState);
		super.cloneMessage (clone);
		return clone;
	}

}
