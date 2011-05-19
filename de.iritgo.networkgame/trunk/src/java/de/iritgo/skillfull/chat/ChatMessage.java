package de.iritgo.skillfull.chat;

import de.iritgo.skillfull.eventbus.Event;
import de.iritgo.skillfull.fsm.FsmBusEvent;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;
import de.iritgo.skillfull.user.FsmEventTypes;


public class ChatMessage extends Message
{
	private byte userId;
	
	private String message;
	
	public ChatMessage ()
	{
		super (Opcode.CHAT_MESSAGE);
	}
	
	public ChatMessage (byte userId, String message)
	{
		super (Opcode.CHAT_MESSAGE);
		this.userId = userId;
		this.message = message;
	}

	@Override
	public void transfer ()
	{
		channelBuffer.writeByte (Opcode.CHAT_MESSAGE.getObcode ());
		channelBuffer.writeInt (5 + message.length ());
		channelBuffer.writeByte (userId);
		channelBuffer.writeInt (message.length ());
		channelBuffer.writeBytes (message.getBytes ());
	}

	@Override
	public boolean isReliableMessage ()
	{
		return false;
	}
	
	@Override
	public boolean isKeepForSnapshotDelta ()
	{
		return true;
	}
	
	@Override
	public boolean isSequenceMessage ()
	{
		return true;
	}

	public byte getUserId ()
	{
		return userId;
	}

	public void setUserId (byte userId)
	{
		this.userId = userId;
	}

	public String getMessage ()
	{
		return message;
	}

	public void setMessage (String message)
	{
		this.message = message;
	}
	
	public int getMessageSize ()
	{
		messageSize += opcode.getSize () + 5;
		messageSize += message.length ();
		return messageSize;
	}
}
