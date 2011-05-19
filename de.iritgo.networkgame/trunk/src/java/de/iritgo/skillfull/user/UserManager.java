
package de.iritgo.skillfull.user;


import java.util.Collection;

import com.artemis.Entity;
import com.artemis.utils.Utils;

import de.iritgo.skillfull.component.common.CollisionSystem;
import de.iritgo.skillfull.component.common.Expires;
import de.iritgo.skillfull.component.motion.PositionHistory;
import de.iritgo.skillfull.component.motion.PositionHistorySystem;
import de.iritgo.skillfull.component.motion.Move;
import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.component.motion.SegmentMove;
import de.iritgo.skillfull.component.motion.SegmentMoveSystem;
import de.iritgo.skillfull.entity.EntityManager;
import de.iritgo.skillfull.entity.network.EntityCreateMessage;
import de.iritgo.skillfull.entity.network.EntitySegmentMoveMessage;
import de.iritgo.skillfull.eventbus.EventHandler;
import de.iritgo.skillfull.user.fsm.ServerFsm;
import de.iritgo.skillfull.user.network.UserInputMessage;
import de.iritgo.skillfull.world.CommonWorld;


public class UserManager
{
	private UserRegistry userRegistry;

	private User appUser;

	private CommonWorld commonWorld;

	public void init (final CommonWorld commonWorld)
	{
		this.commonWorld = commonWorld;
		userRegistry = new UserRegistry ();
		ServerFsm serverFsm = new ServerFsm ();
		serverFsm.init (commonWorld, this);

		commonWorld.getEventBusManager ().subscribeOnSimpleEventBus (UserInputMessage.class,
						new EventHandler<UserInputMessage> ()
						{
							@Override
							public void handleEvent (UserInputMessage event)
							{
								String networkId = event.getNetworkId ();
								User user = commonWorld.getUserManager ().findByNetworkId (networkId);

								Entity userEntity = commonWorld.getEntityByUniqueId (user.getEntityId ());

								Position pos = userEntity.getComponent (Position.class);

								pos.setLocation (event.getPosX (), event.getPosY ());

								if (event.getMouseClicked () > 0)
								{
									Position userPos = userEntity.getComponent (Position.class);

									EntityManager entityManager = commonWorld.getSkillFullEntityManager ();
									Entity entity = entityManager.createByTemplateId ((byte) 2, 0, (int) user.getEntityId (), -1);
									entity.getComponent (Position.class).setLocation (userPos.getX (), userPos.getY ());
									entity.getComponent (PositionHistory.class).setExpandableTimeRange (false);
									entity.refresh ();
									EntityCreateMessage entityCreateMessage = new EntityCreateMessage ((byte) 2);
									entityCreateMessage.setUniqueEntityId ((int) entity.getUniqueId ());
									entityCreateMessage.setTick (event.getTick ());

									EntitySegmentMoveMessage entitySegmentMoveMessage = new EntitySegmentMoveMessage ();
									entitySegmentMoveMessage.setTick (event.getTick ());
									entitySegmentMoveMessage.setStartX ((int) userPos.getX ());
									entitySegmentMoveMessage.setStartY ((int) userPos.getY ());
									entitySegmentMoveMessage.setEndX ((int) event.getMouseX ());
									entitySegmentMoveMessage.setEndY ((int) event.getMouseY ());
									
									
									// Default shot length...
									float x = Utils.getXAtEndOfRotatedLineByOrigin (userPos.getX (), 250, Utils.angleInDegrees 
													(userPos.getX (), userPos.getY (), event.getMouseX (), event.getMouseY ()));
									float y = Utils.getYAtEndOfRotatedLineByOrigin (userPos.getY (), 250, Utils.angleInDegrees
													(userPos.getX (), userPos.getY (), event.getMouseX (), event.getMouseY ()));
									

									entitySegmentMoveMessage.setEndX ((int) x);
									entitySegmentMoveMessage.setEndY ((int) y);

									//									3 Pixel / Sek. v = weg/zeit
									// v = Pixel/t
									// v = 1 Pixel/1000ms
									float dist = Utils.distance (entitySegmentMoveMessage.getStartX (), entitySegmentMoveMessage.getStartY (),
													entitySegmentMoveMessage.getEndX (), entitySegmentMoveMessage.getEndY ());
									float t = (dist) / (float) (0.02 * 15);
									

									entitySegmentMoveMessage.setDestinationTick (event.getTick () + (int) t);
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

									for (long simulatedTime = event.getTick (); simulatedTime < moveWayComp
													.getDestinationTick (); simulatedTime += commonWorld.getDelta ())
									{
										segmentMoveSystem.calcMovement (entity, simulatedTime, true);
									}

									for (User dstUser : getUsers ())
									{
										commonWorld.getNetwork ().sendMessage (dstUser, entityCreateMessage);
										commonWorld.getNetwork ().sendMessage (dstUser, entitySegmentMoveMessage);
									}
									// Check Collision

									CollisionSystem collisionSystem = commonWorld.getSystemManager ().getSystem (
													CollisionSystem.class);
									PositionHistorySystem posHistorySystem = commonWorld.getSystemManager ().getSystem (PositionHistorySystem.class);

									posHistorySystem.reward ();
									collisionSystem.setRewindMode ();
									long delta = commonWorld.getDelta ();
									for (long backInTime = event.getTick (); backInTime < commonWorld.getGameTimeManager ().getNetworkTime (); backInTime += delta)
									{
										// Check Collision
										collisionSystem.setTime (backInTime);
										collisionSystem.process ();
									}
									collisionSystem.setForwardMode ();
									posHistorySystem.forward ();
									posHistorySystem.process ();

								}

								// if (event.getPressedKey () == Input.KEY_LEFT)
								// {
								// pos.addX (-1 * 115/commonWorld.getDelta ());
								// }
								// if (event.getPressedKey () ==
								// Input.KEY_RIGHT)
								// {
								// pos.addX (1 * 115/commonWorld.getDelta ());
								// }
								// if (event.getPressedKey () == Input.KEY_UP)
								// {
								// pos.addY (-1 * 115/commonWorld.getDelta ());
								// }
								// if (event.getPressedKey () == Input.KEY_DOWN)
								// {
								// pos.addY (1 * 115/commonWorld.getDelta ());
								// }
							}
						});

	}

	public User registerUser (String name, String password)
	{
		User user = new User ();
		user.setName (name);
		user.setPassword (password);

		userRegistry.registerUser (user);
		return user;
	}

	public User addUser (User user)
	{
		userRegistry.addUser (user);
		return user;
	}

	public User findByNetworkId (String networkId)
	{
		for (User user : userRegistry.getUsers ())
		{
			if (networkId.equals (user.getNetworkId ()))
				return user;
		}
		return null;
	}

	public User getUser (Integer id)
	{
		return userRegistry.getUser (id);
	}

	public Collection<User> getUsers ()
	{
		return userRegistry.getUsers ();
	}

	public User getAppUser ()
	{
		return appUser;
	}

	public void setAppUser (User appUser)
	{
		this.appUser = appUser;
	}

	public void removeUser (User user)
	{
		userRegistry.remove (user);
	}
}
