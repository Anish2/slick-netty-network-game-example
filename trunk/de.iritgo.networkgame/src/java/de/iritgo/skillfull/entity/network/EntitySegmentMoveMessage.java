
package de.iritgo.skillfull.entity.network;

import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;


public class EntitySegmentMoveMessage extends EntityNetworkMessage
{
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private int destinationTick;

	public EntitySegmentMoveMessage ()
	{
		super (Opcode.ENTITY_MOVEWAY);
	}

	@Override
	public void transfer ()
	{
		super.transfer ();
		channelBuffer.writeInt (startX);
		channelBuffer.writeInt (startY);
		channelBuffer.writeInt (endX);
		channelBuffer.writeInt (endY);
		channelBuffer.writeInt (destinationTick);
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

	public int getStartX ()
	{
		return startX;
	}

	public void setStartX (int startX)
	{
		this.startX = startX;
	}

	public int getStartY ()
	{
		return startY;
	}

	public void setStartY (int startY)
	{
		this.startY = startY;
	}

	public int getEndX ()
	{
		return endX;
	}

	public void setEndX (int endX)
	{
		this.endX = endX;
	}

	public int getEndY ()
	{
		return endY;
	}

	public void setEndY (int endY)
	{
		this.endY = endY;
	}

	public int getDestinationTick ()
	{
		return destinationTick;
	}

	public void setDestinationTick (int destinationTick)
	{
		this.destinationTick = destinationTick;
	}

	@Override
	public Message cloneMessage ()
	{
		EntitySegmentMoveMessage clone = new EntitySegmentMoveMessage ();
		clone.setDestinationTick (destinationTick);
		clone.setEndX (endX);
		clone.setEndY (endY);
		clone.setStartX (startX);
		clone.setStartY (startY);
		super.cloneMessage (clone);
		return clone;
	}

}
