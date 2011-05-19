
package de.iritgo.skillfull.component.common;


import java.util.Random;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.component.motion.PositionHistory;
import de.iritgo.skillfull.component.motion.PositionHistorySystem;
import de.iritgo.skillfull.entity.network.EntityDestroyMessage;
import de.iritgo.skillfull.entity.network.EntityPositionMessage;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.world.CommonWorld;


public class CollisionSystem extends EntitySystem
{
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<Owner> ownerMapper;
	private ComponentMapper<Collision> collisionMapper;
	private PositionHistorySystem posHistorySystem;
	private boolean rewindMode;
	private long time;
	private ComponentMapper<PositionHistory> positionHistoryMapper;
	private CommonWorld commonWorld;

	public CollisionSystem (CommonWorld commonWorld)
	{
		super (Position.class, Owner.class, Collision.class);
		this.commonWorld = commonWorld;
	}

	@Override
	public void initialize ()
	{
		positionMapper = new ComponentMapper<Position> (Position.class, world.getEntityManager ());
		positionHistoryMapper = new ComponentMapper<PositionHistory> (PositionHistory.class, world.getEntityManager ());
		ownerMapper = new ComponentMapper<Owner> (Owner.class, world.getEntityManager ());
		collisionMapper = new ComponentMapper<Collision> (Collision.class, world.getEntityManager ());
		posHistorySystem = world.getSystemManager ().getSystem (PositionHistorySystem.class);
	}

	@Override
	protected void processEntities (ImmutableBag<Entity> entities)
	{
		ImmutableBag<Entity> bullets = world.getGroupManager ().getEntities ("BULLETS");
		ImmutableBag<Entity> users = world.getGroupManager ().getEntities ("USERS");
		if (bullets == null || users == null)
		{
			return;
		}

		if (rewindMode)
			posHistorySystem.rewind (time, 30);

		for (int a = 0; users.size () > a; a++)
		{
			Entity userEntity = users.get (a);
			Position userPos = positionMapper.get (userEntity);
			Owner userOwner = ownerMapper.get (userEntity);
			Collision userCollision = collisionMapper.get (userEntity);

			if (rewindMode)
				posHistorySystem.warpInTime (userEntity, time, 30);

			if (userPos.getX () < 0 || userPos.getY () < 0 || userCollision.isCollision () || ! userCollision.isCollisionable ())
			{
				continue;
			}

			for (int b = 0; bullets.size () > b; b++)
			{
				Entity bullet = bullets.get (b);
				Position pos = positionMapper.get (bullet);
				Owner bulletOwner = ownerMapper.get (bullet);
				Collision bulletCollision = collisionMapper.get (bullet);

				if (bulletOwner.getOwnerId () == userOwner.getOwnerId () || bulletCollision.isCollision () || ! bulletCollision.isCollisionable ())
				{
					continue;
				}

				if (rewindMode)
					posHistorySystem.warpInTime (bullet, time, 30);

				if (pos.getX () < 0 || pos.getY () < 0)
					continue;

//				System.out.println ("Collision-Check: " + time + " U:" + ship.getId () + " d:" + distance (ship, bullet));
//				System.out.println (shipPos.getX () + "/" + shipPos.getY () + " : " + pos.getX () + "/" + pos.getY ());
				if (collisionExists (bullet, userEntity))
				{
//					System.out.println ("Treffer!" + userEntity.getUniqueId () + "<->" +bullet.getUniqueId () + " in rewind mode->" + rewindMode);
					world.deleteEntity (bullet);
					userPos.setLocation (200 + new Random ().nextInt ((int) commonWorld.getPlayfield ().getWidth ()) -200, 200 +  new Random ().nextInt ((int) commonWorld.getPlayfield ().getHeight ())-200);
					PositionHistory posHis = positionHistoryMapper.get (userEntity);
					posHis.getLastPosition ().setLocation (userPos.getX (), userPos.getY ());
					bulletCollision.setCollision (true);
					
					EntityDestroyMessage destroyEntity = new EntityDestroyMessage ((int) bullet.getUniqueId (), (byte) userOwner.getOwnerId ());
					User killedUser = null;
					for (User user : commonWorld.getUserManager ().getUsers ())
					{
						commonWorld.getNetwork ().sendMessage (user, destroyEntity);
						if (user.getEntityId () == userEntity.getUniqueId ())
						{
							killedUser = user;
						}
					}

					//TODO: A new message!!!
					EntityPositionMessage positionMessage = new EntityPositionMessage ((int) userPos.getX (), (int) userPos.getY ());
					// hack!
					positionMessage.setUniqueEntityId ((int) -10000);
					positionMessage.setTick ((int) time);
					
					commonWorld.getNetwork ().sendMessage (killedUser, positionMessage);
					
//
//					world.deleteEntity (ship);
					break;
				}
			}
		}

		/*
		 * for(int a = 0; relevantEntities.size() > a; a++) { Entity entityFirst
		 * = relevantEntities.get(a); for(int b = 0; relevantEntities.size() >
		 * b; b++) { Entity entitySecond = relevantEntities.get(b);
		 *
		 * if(collisionExists(entityFirst, entitySecond)) {
		 * System.out.println("KABOOM"); } } }
		 */
	}

	private boolean collisionExists (Entity e1, Entity e2)
	{
		Position t1 = positionMapper.get (e1);
		Position t2 = positionMapper.get (e2);
		return t1.getDistanceTo (t2) < 30;
	}

	private float distance (Entity e1, Entity e2)
	{
		Position t1 = positionMapper.get (e1);
		Position t2 = positionMapper.get (e2);
		return t1.getDistanceTo (t2);
	}

	@Override
	protected boolean checkProcessing ()
	{
		return true;
	}

	public void setTime (long time)
	{
		this.time = time;
	}

	public void setRewindMode ()
	{
		rewindMode = true;
	}

	public void setForwardMode ()
	{
		rewindMode = false;
	}
}
