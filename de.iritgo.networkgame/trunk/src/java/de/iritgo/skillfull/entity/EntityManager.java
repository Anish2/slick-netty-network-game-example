
package de.iritgo.skillfull.entity;


import java.util.HashMap;

import javax.print.DocFlavor.INPUT_STREAM;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

import com.artemis.Entity;
import com.artemis.World;

import de.iritgo.skillfull.client.Client;
import de.iritgo.skillfull.component.camera.Camera;
import de.iritgo.skillfull.component.common.Collision;
import de.iritgo.skillfull.component.common.Expires;
import de.iritgo.skillfull.component.common.Owner;
import de.iritgo.skillfull.component.input.InputKeyFocus;
import de.iritgo.skillfull.component.motion.PositionHistory;
import de.iritgo.skillfull.component.motion.Move;
import de.iritgo.skillfull.component.motion.SegmentMove;
import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.component.visual.Visual;
import de.iritgo.skillfull.component.visual.sprite.Sprite;
import de.iritgo.skillfull.entity.network.EntityCreateMessage;
import de.iritgo.skillfull.entity.network.EntityDestroyMessage;
import de.iritgo.skillfull.entity.network.EntitySegmentMoveMessage;
import de.iritgo.skillfull.entity.network.EntityPositionMessage;
import de.iritgo.skillfull.entity.network.UserEntityCreateMessage;
import de.iritgo.skillfull.eventbus.Event;
import de.iritgo.skillfull.eventbus.EventHandler;
import de.iritgo.skillfull.fsm.FsmEventBus;
import de.iritgo.skillfull.render.RenderIds;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.user.UserFsmBusEventHandler;
import de.iritgo.skillfull.user.network.LoginRequestMessage;
import de.iritgo.skillfull.user.network.UserMessage;
import de.iritgo.skillfull.world.CommonWorld;


public class EntityManager
{
	private CommonWorld world;

	private GameContainer container;

	private HashMap<Long, Entity> serverEntityIdMapping = new HashMap<Long, Entity> ();

	public static int blaCount = 0;

	public static Sprite apple = new Sprite ("data/apple.png");
	public static Sprite android = new Sprite ("data/android.png");

	public void init (final CommonWorld world, GameContainer container)
	{
		this.world = world;
		this.container = container;

		world.getEventBusManager ().subscribeOnSimpleEventBus (EntityCreateMessage.class,
						new EventHandler<EntityCreateMessage> ()
						{
							@Override
							public void handleEvent (EntityCreateMessage event)
							{
								int lifeTime = (int) (event.getTick () - world.getGameTimeManager ().getNetworkTime () + event.getLifeTime ());
								//TODO: Need we the ownerId on the client?
								createByTemplateId (event.getTemplateId (), lifeTime, -1, event.getUniqueEntityId ());
							}
						});

		world.getEventBusManager ().subscribeOnSimpleEventBus (UserEntityCreateMessage.class,
						new EventHandler<UserEntityCreateMessage> ()
						{
							@Override
							public void handleEvent (UserEntityCreateMessage event)
							{
								User user = world.getUserManager ().getAppUser ();
								Entity entity = world.getSkillFullEntityManager ().createNewUserEntity (user, event.getUniqueEntityId ());
								// TODO: Ok, we have in this game only one camera
								Entity camera = world.getGroupManager ().getEntities ("camera").get (0);
								camera.getComponent (Camera.class).setFollowEntityUniqueId (event.getUniqueEntityId ());
								camera.getComponent (Camera.class).setFollowEntity (true);

							}
						});

		world.getEventBusManager ().subscribeOnSimpleEventBus (EntityPositionMessage.class,
						new EventHandler<EntityPositionMessage> ()
						{
							@Override
							public void handleEvent (EntityPositionMessage event)
							{
								Entity entity = serverEntityIdMapping.get ((long) event.getUniqueEntityId ());
								User user = world.getUserManager ().getAppUser ();
								if (entity == null || user == null)
								{
									if (event.getUniqueEntityId () == -10000)
									{
										Entity userEntity = serverEntityIdMapping.get ((long) user.getEntityId ());
										userEntity.getComponent (Position.class).setLocation (event.getX (), event.getY ());
									}
									return;
								}
								if (user.getEntityId () == entity.getCustomId ())
									return;

								Move mov = entity.getComponent (Move.class);
								if (mov != null)
								{
									mov.addInterpolationPos (event.getTick (), new Position (event.getX (), event.getY ()));
								}
							}
						});
		world.getEventBusManager ().subscribeOnSimpleEventBus (EntitySegmentMoveMessage.class,
						new EventHandler<EntitySegmentMoveMessage> ()
						{
							@Override
							public void handleEvent (EntitySegmentMoveMessage event)
							{
								Entity entity = serverEntityIdMapping.get ((long) event.getUniqueEntityId ());
								if (entity == null)
								{
									return;
								}

								if (entity.getCustomId () != event.getUniqueEntityId ())
								{
									System.out.println ("Reuse error!!!!!" + entity.getCustomId () +"/"+ event.getUniqueEntityId ());
								}

								SegmentMove mov = entity.getComponent (SegmentMove.class);
								if (mov == null)
									return;
								mov.setStarted (false);
								mov.setStartPoint (new Position (event.getStartX (), event.getStartY ()));
								mov.setEndPoint (new Position (event.getEndX (), event.getEndY ()));
								mov.setStartTick (event.getTick ());
								mov.setDestinationTick (event.getDestinationTick ());
							}
						});
		world.getEventBusManager ().subscribeOnSimpleEventBus (EntityDestroyMessage.class,
						new EventHandler<EntityDestroyMessage> ()
						{
							@Override
							public void handleEvent (EntityDestroyMessage event)
							{
								Entity entity = serverEntityIdMapping.get ((long) event.getUniqueEntityId ());
								if (entity == null)
								{
									return;
								}
								if (entity.getCustomId () == event.getUniqueEntityId ())
								{
									serverEntityIdMapping.remove (event.getUniqueEntityId ());
									world.deleteEntity (entity);
								}
							}
						});
	}

	public Entity createAndroidEntity (long uniqueEntityId)
	{
		Entity user = world.createEntity ();
		user.addComponent (new Move ());
		user.addComponent (new Position (100, 100));
		user.addComponent (new PositionHistory ());
		user.addComponent (android);
		user.addComponent (new Visual (RenderIds.SPRITE.getId (), 128, 64));
		user.addComponent (new Collision ());
		world.getGroupManager ().set ("USERS", user);
		user.refresh ();

		if (uniqueEntityId != -1)
		{
			serverEntityIdMapping.put (uniqueEntityId, user);
			user.setCustomId ((int) uniqueEntityId);
		}
		else
		{
			serverEntityIdMapping.put (user.getUniqueId (), user);
			user.setCustomId ((int) user.getUniqueId ());
		}
		return user;
	}

	public Entity createBulletEntity (int lifeTime, int ownerId, long uniqueEntityId)
	{
		Entity bullet = world.createEntity ();
		bullet.addComponent (new SegmentMove ());
		bullet.addComponent (new Expires (lifeTime));
		bullet.addComponent (new Position (-100, -100));
		bullet.addComponent (new PositionHistory ());
		bullet.addComponent (new Owner (ownerId));
		bullet.addComponent (apple);
		bullet.addComponent (new Visual (RenderIds.SPRITE.getId (), 128, 64));
		bullet.addComponent (new Collision ());
		world.getGroupManager ().set ("BULLETS", bullet);
		bullet.refresh ();

		if (uniqueEntityId != -1)
		{
			serverEntityIdMapping.put (uniqueEntityId, bullet);
			bullet.setCustomId ((int) uniqueEntityId);
		}
		else
		{
			serverEntityIdMapping.put (bullet.getUniqueId (), bullet);
			bullet.setCustomId ((int) bullet.getUniqueId ());
		}
		return bullet;
	}

	public Entity createCamera ()
	{
		Entity camera = world.createEntity ();
		Camera cameraView1 = new Camera ();
		cameraView1.setX (0.0f);
		cameraView1.setY (0.0f);
		// cameraView1.setViewPort (new Rectangle (0, 0, container.getWidth ()
		// /2, container.getHeight ()));
		cameraView1.setViewPort (new Rectangle (0, 0, container.getWidth (), container.getHeight ()));
		cameraView1.setActive (true);
		camera.addComponent (cameraView1);
		// camera1.addComponent (new InputKeyFocus (Input.KEY_LEFT,
		// Input.KEY_RIGHT));
		camera.addComponent (new Position (50, 0));
		camera.refresh ();
		return camera;
	}

	public Entity createByTemplateId (byte templateId)
	{
		return createByTemplateId (templateId, 0, -1, -1);
	}

	public Entity createByTemplateId (byte templateId, int lifeTime, int ownerId, long uniqueEntityId)
	{
		switch (templateId)
		{
			case 1:
			{
				return createAndroidEntity (uniqueEntityId);
			}
			case 2:
			{
				return createBulletEntity (lifeTime, ownerId, uniqueEntityId);
			}
		}
		return null;
	}

	//TODO: Move to UserManager?
	public Entity createNewUserEntity (User user, int uniqueEntityId)
	{
		Entity entity = createAndroidEntity (uniqueEntityId);
		entity.addComponent (new InputKeyFocus (
						Input.KEY_A, Input.KEY_D, Input.KEY_W, Input.KEY_S,
						Input.MOUSE_LEFT_BUTTON));
		entity.addComponent (new Owner ((int) entity.getUniqueId ()));
		entity.refresh ();
		entity.setCustomId (uniqueEntityId);
		user.setEntityId (uniqueEntityId);
		return entity;
	}

	public Entity getEntityByUniqueId (long uniqueEntityId)
	{
		return serverEntityIdMapping.get (uniqueEntityId);
	}

	public Entity removeEntityFromServerEntityCache (Entity entity)
	{
		Entity removedEntity = serverEntityIdMapping.remove ((long) entity.getCustomId ());

		return removedEntity;
	}
}
