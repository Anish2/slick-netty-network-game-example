
package de.iritgo.skillfull.entity.network;


import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;


public class EntityNetworkMessage extends Message
{
	protected int tick;

	protected int uniqueEntityId;

	public EntityNetworkMessage (Opcode opcode)
	{
		super (opcode);
	}

	public int getTick ()
	{
		return tick;
	}

	public void setTick (int tick)
	{
		this.tick = tick;
	}

	public int getUniqueEntityId ()
	{
		return uniqueEntityId;
	}

	public void setUniqueEntityId (int uniqueEntityId)
	{
		this.uniqueEntityId = uniqueEntityId;
	}

	@Override
	public void transfer ()
	{
		channelBuffer.writeByte (opcode.getObcode ());
		channelBuffer.writeInt (tick);
		channelBuffer.writeInt (uniqueEntityId);
	}

	@Override
	public boolean isSequenceMessage ()
	{
		return true;
	}

	@Override
	public Message cloneMessage (Message clone)
	{
		((EntityNetworkMessage) clone).setTick (tick);
		((EntityNetworkMessage) clone).setUniqueEntityId (uniqueEntityId);
		super.cloneMessage (clone);
		return clone;
	}

}
