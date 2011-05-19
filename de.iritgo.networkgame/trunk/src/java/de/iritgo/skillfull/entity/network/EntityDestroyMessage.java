
package de.iritgo.skillfull.entity.network;

import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;


public class EntityDestroyMessage extends EntityNetworkMessage
{
	private byte fromId;

	public EntityDestroyMessage (int entityId, byte fromId)
	{
		super (Opcode.ENTITY_DESTROY);
		this.fromId = fromId;
		this.uniqueEntityId = entityId;
	}

	@Override
	public void transfer ()
	{
		super.transfer ();
		channelBuffer.writeByte (fromId);
	}

	public byte getFromId ()
	{
		return fromId;
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

	public void setFromId (byte fromId)
	{
		this.fromId = fromId;
	}

	@Override
	public Message cloneMessage ()
	{
		EntityDestroyMessage clone = new EntityDestroyMessage (uniqueEntityId, fromId);
		super.cloneMessage (clone);
		return clone;
	}
}
