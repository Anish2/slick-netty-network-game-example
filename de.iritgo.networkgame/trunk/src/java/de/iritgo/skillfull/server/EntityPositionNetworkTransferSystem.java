
package de.iritgo.skillfull.server;


import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;

import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.entity.network.EntityPositionMessage;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.world.CommonWorld;


public class EntityPositionNetworkTransferSystem extends EntityProcessingSystem
{
	private ComponentMapper<Position> positionMapper;

	private CommonWorld commonWorld;

	public EntityPositionNetworkTransferSystem (CommonWorld commonWorld)
	{
		super (Position.class);
		this.commonWorld = commonWorld;
	}

	@Override
	public void initialize ()
	{
		positionMapper = new ComponentMapper<Position> (Position.class, world.getEntityManager ());
	}

	@Override
	protected void process (Entity e)
	{
		Position pos = positionMapper.get (e);
		EntityPositionMessage entityPos = new EntityPositionMessage ((int) pos.getX (), (int) pos.getY ());
		entityPos.setUniqueEntityId ((int) e.getUniqueId ());
		entityPos.setTick ((int) commonWorld.getGameTimeManager ().getNetworkTime ());
		for (User user : commonWorld.getUserManager ().getUsers ())
		{
			commonWorld.getNetwork ().sendMessage (user, entityPos);
		}
	}
}
