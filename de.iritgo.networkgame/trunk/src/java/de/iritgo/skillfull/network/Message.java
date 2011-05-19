package de.iritgo.skillfull.network;

import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

import de.iritgo.skillfull.eventbus.Event;

public class Message implements Event
{
	protected int messageSize;

	protected Opcode opcode;

	protected List<Message> messages = new LinkedList<Message> ();

	protected ChannelBuffer channelBuffer;

	protected byte clientId;

	private String networkId;

	private boolean keepForSnapshotDelta;

	protected int sequenceId;

	protected int subSequenceId;


	private long receivedAt;

	public Message ()
	{
	}

	public Message (Opcode opcode)
	{
		this.opcode = opcode;
	}

	public int getMessageSize ()
	{
		if (opcode != null)
			return messageSize + opcode.getSize ();
		return messageSize;
	}

	public void setMessageSize (int messageSize)
	{
		this.messageSize = messageSize;
	}

	public Opcode getOpcode ()
	{
		return opcode;
	}

	public void setOpcode (Opcode opcode)
	{
		this.opcode = opcode;
	}

	public byte getClientId ()
	{
		return clientId;
	}

	public void setClientId (byte clientId)
	{
		this.clientId = clientId;
	}

	public Message withClientId (byte clientId)
	{
		this.clientId = clientId;
		return this;
	}

	public void transferToBuffer (ChannelBuffer channelBuffer)
	{
		this.channelBuffer = channelBuffer;
		messageSize = 0;
		for (Message message : messages)
		{
			message.init (channelBuffer);
			message.transfer ();
		}
	}

	public void transfer ()
	{
	}

	public void init (ChannelBuffer channelBuffer)
	{
		this.channelBuffer = channelBuffer;
		channelBuffer.writeByte (clientId);
	}

	public Message addMessage (Message message)
	{
		messageSize += message.getMessageSize ();
		messages.add (message);
		return this;
	}

	public Message andMessage (Message message)
	{
		messageSize += message.getMessageSize ();
		messages.add (message);
		return this;
	}

	public Message getFirstMessage ()
	{
		return messages.get (0);
	}

	public void setNetworkId (String clientUniqueId)
	{
		this.networkId = clientUniqueId;
	}

	public String getNetworkId ()
	{
		return networkId;
	}

	public void dumpObcodes ()
	{
		for (Message message : messages)
		{
			System.out.print (message.getOpcode () != null ? message.getOpcode ().toString () : "");
			System.out.print ("->");
			message.dumpObcodes ();
		}
	}

	public void setReceivedAt (long receivedAt)
	{
		this.receivedAt = receivedAt;
	}

	public long getReceivedAt ()
	{
		return receivedAt;
	}

	public int getNumberOfMessages ()
	{
		int size = 0;
		for (Message message : messages)
		{
			size += message.getNumberOfMessages ();
		}
		return messages.size () + size;
	}

	public void setKeepForSnapshotDelta (boolean keepForSnapshotDelta)
	{
		this.keepForSnapshotDelta = keepForSnapshotDelta;
	}

	public boolean isKeepForSnapshotDelta ()
	{
		return keepForSnapshotDelta;
	}

	public void setSequenceId (int sequenceId)
	{
		this.sequenceId = sequenceId;
	}

	public int getSequenceId ()
	{
		return sequenceId;
	}

	public boolean isSequenceMessage ()
	{
		return false;
	}

	public boolean isReliableMessage ()
	{
		return false;
	}

	public int getSubSequenceId ()
	{
		return subSequenceId;
	}

	public void setSubSequenceId (int subSequenceId)
	{
		this.subSequenceId = subSequenceId;
	}

	public Message cloneMessage ()
	{
		return this;
	}

	public Message cloneMessage (Message clone)
	{
		clone.setClientId (clientId);
		clone.setKeepForSnapshotDelta (clone.isKeepForSnapshotDelta ());
		clone.setSequenceId (sequenceId);
		clone.setSubSequenceId (subSequenceId);
		clone.setNetworkId (networkId);
		clone.setMessageSize (messageSize);
		clone.setOpcode (opcode);
		clone.setReceivedAt (receivedAt);
		return clone;
	}
}
