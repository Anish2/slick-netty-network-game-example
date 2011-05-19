
package de.iritgo.skillfull.entity.network;

import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;


public class EntityCreateMessage extends EntityNetworkMessage
{
	private byte templateId;
	private int lifeTime;

	public EntityCreateMessage (byte templateId)
	{
		super (Opcode.ENTITY_CREATE);
		this.templateId = templateId;
	}

	@Override
	public void transfer ()
	{
		super.transfer ();
		channelBuffer.writeByte (templateId);
		channelBuffer.writeInt (lifeTime);
	}

	public byte getTemplateId ()
	{
		return templateId;
	}

	public int getLifeTime ()
	{
		return lifeTime;
	}

	public void setLifeTime (int lifeTime)
	{
		this.lifeTime = lifeTime;
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

	@Override
	public Message cloneMessage ()
	{
		EntityCreateMessage clone = new EntityCreateMessage (templateId);
		clone.setLifeTime (lifeTime);
		super.cloneMessage (clone);
		return clone;
	}
}
