package de.iritgo.skillfull.server;

import java.util.HashMap;
import java.util.Random;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.Utils;

import de.iritgo.simplelife.data.Tuple2;
import de.iritgo.skillfull.component.common.CollisionSystem;
import de.iritgo.skillfull.component.common.Expires;
import de.iritgo.skillfull.component.common.Owner;
import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.component.motion.PositionHistory;
import de.iritgo.skillfull.component.motion.PositionHistorySystem;
import de.iritgo.skillfull.component.motion.SegmentMove;
import de.iritgo.skillfull.component.motion.SegmentMoveSystem;
import de.iritgo.skillfull.entity.EntityManager;
import de.iritgo.skillfull.entity.network.EntityCreateMessage;
import de.iritgo.skillfull.entity.network.EntitySegmentMoveMessage;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.world.CommonWorld;

public class BotTest
{
	
	private HashMap<Entity, Tuple2<Float, Float>> bots = new HashMap<Entity, Tuple2<Float, Float>> ();
	private Server server;
	private CommonWorld commonWorld;
	private Random random = new Random ();
	private int shootTimer;
	
	public BotTest (Server server, CommonWorld commonWorld)
	{
		this.server = server;
		this.commonWorld = commonWorld;
	}
	
	private int getRandom (int length)
	{
		int r = 400 + random.nextInt ((int) length);
		if (r > length - 400)
			r = (int) length - 400;
		return r;
	}
	
	
	
	public Entity createBot ()
	{
		Entity bot = commonWorld.getSkillFullEntityManager ().createAndroidEntity (-1);
		bot.getComponent (Position.class).setX (getRandom ((int) commonWorld.getPlayfield ().getWidth ()));
		bot.getComponent (Position.class).setY (getRandom ((int) commonWorld.getPlayfield ().getHeight ()));
		bot.getComponent (PositionHistory.class).setExpandableTimeRange (true);
		bot.addComponent (new Owner ((int) bot.getUniqueId ()));
		bot.getComponent (PositionHistory.class).addHistoryPos (commonWorld.getGameTimeManager ().getNetworkTime (), new Position (bot.getComponent (Position.class)));
		bot.addComponent (new Owner ((int) bot.getUniqueId ()));
		bot.refresh ();
		bots.put (bot, new Tuple2 ((float) 1.0, (float) 1.0));
		return bot;
	}
	
	public void update (int delta)
	{
		for (Entity entity : bots.keySet ())
		{
			Tuple2<Float, Float> tuple = bots.get (entity);
			Position pos = entity.getComponent (Position.class);
			if (pos.getX () > commonWorld.getPlayfield ().getWidth () || pos.getX () < 1)
			{
				tuple.set1 (tuple.get1 () * (float) -1.0);
			}
			if (pos.getY () > commonWorld.getPlayfield ().getHeight () || pos.getY () < 1)
			{
				tuple.set2 (tuple.get2 () * (float) -1.0);
			}

			pos.addX (tuple.get1 ());
			pos.addY (tuple.get2 ());
		}

		shootTimer += delta;
		if (shootTimer > 500)
		{
			shootTimer -= 500;
			for (Entity bot : bots.keySet ())
			{
				Position botPos = bot.getComponent (Position.class);
				ImmutableBag<Entity> users = commonWorld.getGroupManager ().getEntities ("USERS");
				for (int i = 0; i < users.size (); ++i)
				{
					Entity user = users.get (i);
					if (user == bot)
						continue;
					
					Position userPos = user.getComponent (Position.class);
					if (botPos.getDistanceTo (userPos) <= 250)
					{
						botFire (bot.getUniqueId (), botPos, userPos);
					}
				}
			}
			
			
			
		}
	}
	// TODO: Reduant code with usermanager
	private void botFire (long uniqueId, Position botPos, Position dstPos)
	{
		EntityManager entityManager = commonWorld.getSkillFullEntityManager ();
		Entity entity = entityManager.createByTemplateId ((byte) 2, 0, (int) uniqueId, -1);
		entity.getComponent (Position.class).setLocation (botPos.getX (), botPos.getY ());
		entity.getComponent (PositionHistory.class).setExpandableTimeRange (false);
		entity.refresh ();
		
		EntityCreateMessage entityCreateMessage = new EntityCreateMessage ((byte) 2);
		entityCreateMessage.setUniqueEntityId ((int) entity.getUniqueId ());
		int tick = (int) commonWorld.getGameTimeManager ().getNetworkTime ();
		entityCreateMessage.setTick (tick);

		EntitySegmentMoveMessage entitySegmentMoveMessage = new EntitySegmentMoveMessage ();
		entitySegmentMoveMessage.setTick (tick);
		entitySegmentMoveMessage.setStartX ((int) botPos.getX ());
		entitySegmentMoveMessage.setStartY ((int) botPos.getY ());
		entitySegmentMoveMessage.setEndX ((int) dstPos.getX ());
		entitySegmentMoveMessage.setEndY ((int) dstPos.getY ());
		
		
		// Default shot length...
		float x = Utils.getXAtEndOfRotatedLineByOrigin (botPos.getX (), 250, Utils.angleInDegrees 
						(botPos.getX (), botPos.getY (), dstPos.getX (), dstPos.getY ()));
		float y = Utils.getYAtEndOfRotatedLineByOrigin (botPos.getY (), 250, Utils.angleInDegrees
						(botPos.getX (), botPos.getY (), dstPos.getX (), dstPos.getY ()));
		

		entitySegmentMoveMessage.setEndX ((int) x);
		entitySegmentMoveMessage.setEndY ((int) y);

		//									3 Pixel / Sek. v = weg/zeit
		// v = Pixel/t
		// v = 1 Pixel/1000ms
		float dist = Utils.distance (entitySegmentMoveMessage.getStartX (), entitySegmentMoveMessage.getStartY (),
						entitySegmentMoveMessage.getEndX (), entitySegmentMoveMessage.getEndY ());
		float t = (dist) / (float) (0.02 * 15);
		

		entitySegmentMoveMessage.setDestinationTick (tick + (int) t);
		entitySegmentMoveMessage.setUniqueEntityId ((int) entity.getUniqueId ());

		Expires entityExpires = entity.getComponent (Expires.class);
		entityExpires.setLifeTime ((int) t + 1000);
		entityCreateMessage.setLifeTime ((int) t);

		SegmentMove moveWayComp = entity.getComponent (SegmentMove.class);
		moveWayComp.setStartPoint (new Position (entitySegmentMoveMessage.getStartX (), entitySegmentMoveMessage.getStartY ()));
		moveWayComp.setEndPoint (new Position (entitySegmentMoveMessage.getEndX (), entitySegmentMoveMessage.getEndY ()));
		moveWayComp.setStartTick (entitySegmentMoveMessage.getTick ());
		moveWayComp.setDestinationTick (entitySegmentMoveMessage.getDestinationTick ());
		SegmentMoveSystem segmentMoveSystem = commonWorld.getSystemManager ().getSystem (
						SegmentMoveSystem.class);

		for (long simulatedTime = tick; simulatedTime < moveWayComp
						.getDestinationTick (); simulatedTime += commonWorld.getDelta ())
		{
			segmentMoveSystem.calcMovement (entity, simulatedTime, true);
		}
		
		PositionHistorySystem posHistorySystem = commonWorld.getSystemManager ().getSystem (PositionHistorySystem.class);
		posHistorySystem.forward ();
		posHistorySystem.process ();


		for (User dstUser : commonWorld.getUserManager ().getUsers ())
		{
			commonWorld.getNetwork ().sendMessage (dstUser, entityCreateMessage);
			commonWorld.getNetwork ().sendMessage (dstUser, entitySegmentMoveMessage);
		}
	}

	public void sendBots (User user)
	{
		for (Entity entity : bots.keySet ())
		{
			EntityCreateMessage entityCreateMessage = new EntityCreateMessage ((byte)1);
			entityCreateMessage.setUniqueEntityId ((int) entity.getUniqueId ());
			commonWorld.getNetwork ().sendMessage (user, entityCreateMessage);
		}
	}

	public void createBots (int bots)
	{
		for (int i = 0; i < bots ; ++i)
		{
			Entity bot = createBot ();
			for (User user : commonWorld.getUserManager ().getUsers ())
			{
				EntityCreateMessage entityCreateMessage = new EntityCreateMessage ((byte)1);
				entityCreateMessage.setUniqueEntityId ((int) bot.getUniqueId ());
				commonWorld.getNetwork ().sendMessage (user, entityCreateMessage);
			}
		}
	}
}
