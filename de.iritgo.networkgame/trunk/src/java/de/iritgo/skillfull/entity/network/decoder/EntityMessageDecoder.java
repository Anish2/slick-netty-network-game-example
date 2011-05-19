package de.iritgo.skillfull.entity.network.decoder;

import static org.jboss.netty.channel.Channels.fireMessageReceived;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import de.iritgo.skillfull.entity.network.EntityCreateMessage;
import de.iritgo.skillfull.entity.network.EntityDestroyMessage;
import de.iritgo.skillfull.entity.network.EntitySegmentMoveMessage;
import de.iritgo.skillfull.entity.network.EntityPositionMessage;
import de.iritgo.skillfull.entity.network.UserEntityCreateMessage;
import de.iritgo.skillfull.network.ClockMessage;
import de.iritgo.skillfull.network.Opcode;

public class EntityMessageDecoder extends OneToOneDecoder
{
	@Override
	protected Object decode (ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception
	{
		if (msg instanceof ChannelBuffer)
		{
			ChannelBuffer buffer = (ChannelBuffer) msg;
			buffer.markReaderIndex ();
			byte clientId = buffer.readByte ();
			byte opcode = buffer.readByte ();
			if (Opcode.fromByte (opcode) == Opcode.ENTITY_POSITION)
			{
				int tick = buffer.readInt ();
				int entityId = buffer.readInt ();
				int x = buffer.readInt ();
				int y = buffer.readInt ();
				EntityPositionMessage createPositionMessage = new EntityPositionMessage (x, y);
				createPositionMessage.setTick (tick);
				createPositionMessage.setUniqueEntityId (entityId);
				return createPositionMessage;
			}
			else if (Opcode.fromByte (opcode) == Opcode.ENTITY_CREATE)
			{
				int tick = buffer.readInt ();
				int entityId = buffer.readInt ();
				byte templateId = buffer.readByte ();
				int lifeTime = buffer.readInt ();
				EntityCreateMessage createEntityMessage = new EntityCreateMessage (templateId);
				createEntityMessage.setTick (tick);
				createEntityMessage.setLifeTime (lifeTime);
				createEntityMessage.setUniqueEntityId (entityId);
				
				return createEntityMessage;
			}
			else if (Opcode.fromByte (opcode) == Opcode.USER_ENTITY_CREATE)
			{
				int tick = buffer.readInt ();
				int entityId = buffer.readInt ();
				byte templateId = buffer.readByte ();
				UserEntityCreateMessage userCreateEntityMessage = new UserEntityCreateMessage (templateId);
				userCreateEntityMessage.setTick (tick);
				userCreateEntityMessage.setUniqueEntityId (entityId);
				
				return userCreateEntityMessage;
			}
			else if (Opcode.fromByte (opcode) == Opcode.ENTITY_MOVEWAY)
			{
				int tick = buffer.readInt ();
				int entityId = buffer.readInt ();

				int startX = buffer.readInt ();
				int startY = buffer.readInt ();
				int endX = buffer.readInt ();
				int endY = buffer.readInt ();
				int destinationTick = buffer.readInt ();
				
				EntitySegmentMoveMessage moveWayMessage = new EntitySegmentMoveMessage ();
				moveWayMessage.setTick (tick);
				moveWayMessage.setUniqueEntityId (entityId);
				moveWayMessage.setStartX (startX);
				moveWayMessage.setStartY (startY);
				moveWayMessage.setEndX (endX);
				moveWayMessage.setEndY (endY);
				moveWayMessage.setDestinationTick (destinationTick);
				return moveWayMessage;
			}
			else if (Opcode.fromByte (opcode) == Opcode.ENTITY_DESTROY)
			{
				int tick = buffer.readInt ();
				int entityId = buffer.readInt ();
				byte fromId = buffer.readByte ();
				EntityDestroyMessage entityDestroyMessage = new EntityDestroyMessage (entityId, fromId);
				entityDestroyMessage.setTick (tick);
				return entityDestroyMessage;
			}
			buffer.resetReaderIndex ();
		}
		return msg;
	}
}
