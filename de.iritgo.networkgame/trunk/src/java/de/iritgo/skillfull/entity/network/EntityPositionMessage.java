
package de.iritgo.skillfull.entity.network;

import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;


public class EntityPositionMessage extends EntityNetworkMessage
{
	private int x;
	private int y;

	public EntityPositionMessage (int x, int y)
	{
		super (Opcode.ENTITY_POSITION);
		this.x = x;
		this.y = y;
	}

	@Override
	public void transfer ()
	{
		super.transfer ();
		channelBuffer.writeInt (x);
		channelBuffer.writeInt (y);
	}

	public int getX ()
	{
		return x;
	}

	public int getY ()
	{
		return y;
	}

	@Override
	public boolean isSequenceMessage ()
	{
		return true;
	}

	@Override
	public Message cloneMessage ()
	{
		EntityPositionMessage clone = new EntityPositionMessage (x, y);
		super.cloneMessage (clone);
		return clone;
	}

}
