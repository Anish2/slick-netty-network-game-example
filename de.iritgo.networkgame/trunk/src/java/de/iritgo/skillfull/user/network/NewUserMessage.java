package de.iritgo.skillfull.user.network;

import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.user.FsmEventTypes;


public class NewUserMessage extends UserMessage
{
	private String name;
	private byte userId;
	
	
	public NewUserMessage ()
	{
		super (Opcode.NEW_USER);
	}
	
	public NewUserMessage (byte userId, String name)
	{
		this ();
		this.userId = userId;
		this.name = name;
	}


	@Override
	public void transfer ()
	{
		channelBuffer.writeByte (Opcode.NEW_USER.getObcode ());
		channelBuffer.writeInt (5 + name.length ());
		channelBuffer.writeByte (userId);
		channelBuffer.writeInt (name.length ());
		channelBuffer.writeBytes (name.getBytes ());
	}

	public int getMessageSize ()
	{
		messageSize += opcode.getSize () + 5;
		messageSize += name.length ();
		return messageSize;
	}
	
	public void setName (String name)
	{
		this.name = name;
	}
	
	public byte getUserId ()
	{
		return userId;
	}
	
	public void setUserId (byte userId)
	{
		this.userId = userId;
	}

	@Override
	public int getFsmTypeId ()
	{
		return FsmEventTypes.NEW_USER.id ();
	}

	@Override
	public NewUserMessage getMessage ()
	{
		return this;
	}

	public String getName ()
	{
		return name;
	}
	

	@Override
	public boolean isSequenceMessage ()
	{
		return true;
	}
	
	@Override
	public boolean isKeepForSnapshotDelta ()
	{
		return true;
	}
	
	@Override
	public Message cloneMessage ()
	{
		NewUserMessage newUserMessage = new NewUserMessage ();
		newUserMessage.setUserId (userId);
		newUserMessage.setName (name);
		super.cloneMessage (newUserMessage);
		return newUserMessage;
		
	}
}
