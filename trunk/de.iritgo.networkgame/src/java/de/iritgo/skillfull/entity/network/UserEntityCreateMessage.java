
package de.iritgo.skillfull.entity.network;

import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;


public class UserEntityCreateMessage extends EntityNetworkMessage
{
	private byte templateId;

	public UserEntityCreateMessage (byte templateId)
	{
		super (Opcode.USER_ENTITY_CREATE);
		this.templateId = templateId;
	}

	@Override
	public void transfer ()
	{
		super.transfer ();
		channelBuffer.writeByte (templateId);
	}

	public byte getTemplateId ()
	{
		return templateId;
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
		UserEntityCreateMessage clone = new UserEntityCreateMessage (templateId);
		super.cloneMessage (clone);
		return clone;
	}

}
