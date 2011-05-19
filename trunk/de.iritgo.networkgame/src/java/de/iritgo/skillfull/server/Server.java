package de.iritgo.skillfull.server;

import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.SystemManager;
import com.artemis.World;

import de.iritgo.simplelife.data.Tuple2;
import de.iritgo.skillfull.chat.ChatService;
import de.iritgo.skillfull.component.common.CollisionSystem;
import de.iritgo.skillfull.component.common.ExpirationSystem;
import de.iritgo.skillfull.component.common.Owner;
import de.iritgo.skillfull.component.input.InputKeyFocus;
import de.iritgo.skillfull.component.motion.Move;
import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.component.motion.PositionHistory;
import de.iritgo.skillfull.component.motion.PositionHistorySystem;
import de.iritgo.skillfull.component.motion.SegmentMoveSystem;
import de.iritgo.skillfull.component.motion.StorePositionHistorySystem;
import de.iritgo.skillfull.entity.EntityManager;
import de.iritgo.skillfull.network.CommonNetwork;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.render.RenderSystem;
import de.iritgo.skillfull.render.SpriteRender;
import de.iritgo.skillfull.server.network.Network;
import de.iritgo.skillfull.time.GameTimeManager;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.world.CommonWorld;
import de.iritgo.skillfull.world.CommonWorldExtension;
import de.iritgo.skillfull.world.GameWorld;

public class Server
{
	private Network network;

	private GameWorld gameWorld;

	private RenderSystem renderSystem;

	private EntitySystem entitySendNetworkSystem;

	private GameContainer container;

	private HashMap<Entity, Tuple2<Float, Float>> testBla = new HashMap<Entity, Tuple2<Float, Float>> ();

	private CommonWorld commonWorld;

	private GameTimeManager gameTimeManager;

	private int worldSimulationTimer;

	private int serverNetworkTimer;

	protected EntitySystem segmentMoveSystem;

	protected EntitySystem expirationSystem;

	protected CollisionSystem collisionSystem;

	protected PositionHistorySystem positionHistorySystem;

	protected StorePositionHistorySystem storePositionHistorySystem;

	private boolean initialized = false;

	private BotTest botTest;

	
	public Server (GameWorld gameWorld, GameContainer container)
	{
		network = new Network ();
		this.gameWorld = gameWorld;
		this.container = container;
	}

	public void init (String receiveIPAddress, int receivePort)
	{
		network.init (this, receiveIPAddress, receivePort);

		commonWorld = new CommonWorld ();

		CommonWorldExtension extension = new CommonWorldExtension ()
		{

			@Override
			public void registerSystems (SystemManager systemManager, GameTimeManager gameTimeManager)
			{
				renderSystem = (RenderSystem) systemManager.setSystem (new RenderSystem (container, commonWorld));
				renderSystem.setMap (null);
				renderSystem.addRender (new SpriteRender ((World) commonWorld));

				entitySendNetworkSystem = systemManager.setSystem (new EntityPositionNetworkTransferSystem (commonWorld));
				segmentMoveSystem = systemManager.setSystem (new SegmentMoveSystem (gameTimeManager, commonWorld));
				expirationSystem = systemManager.setSystem (new ExpirationSystem (commonWorld));
				collisionSystem = (CollisionSystem) systemManager.setSystem (new CollisionSystem (commonWorld));
				positionHistorySystem = (PositionHistorySystem) systemManager.setSystem (new PositionHistorySystem ());
				storePositionHistorySystem = (StorePositionHistorySystem) systemManager.setSystem (new StorePositionHistorySystem (gameTimeManager));
			}

			@Override
			public CommonNetwork getNetwork ()
			{
				return network;
			}
		};

		commonWorld.init (container, extension, gameTimeManager);

		commonWorld.setPlayfield (new Rectangle (0, 0, gameWorld.getMap ().getWidth () * gameWorld.getMap ().getTileWidth (),
						gameWorld.getMap ().getHeight () * gameWorld.getMap ().getTileHeight ()));


		botTest = new BotTest (this, commonWorld);
		commonWorld.setBotTest (botTest);

		EntityManager entityFactory = commonWorld.getSkillFullEntityManager ();

		ChatService chatService = new ChatService (commonWorld);
		chatService.registerServerMessageListener ();

		commonWorld.getGroupManager ().set ("camera", entityFactory.createCamera ());

		network.clearUserCompressedMessagePerTick ();
		initialized = true;
	}

	public void update (int delta)
	{
		if (! initialized)
			return;
		
		commonWorld.loopStart ();
		commonWorld.setDelta (delta);
		commonWorld.getGameTimeManager ().update (delta);


		if (worldSimulationTimer > GameTimeManager.SERVER_WORLD_SIMULATION_INTERVAL)
		{
			worldSimulationTimer -= GameTimeManager.SERVER_WORLD_SIMULATION_INTERVAL;
			
			botTest.update (delta);

			for (Message message = network.getNewMessage (); message != null; message = network.getNewMessage ())
			{
				commonWorld.getEventBusManager ().publish (message);
			}
			storePositionHistorySystem.process ();
			expirationSystem.process ();

			positionHistorySystem.reward ();
			collisionSystem.setRewindMode ();
			collisionSystem.setTime (commonWorld.getGameTimeManager ().getNetworkTime ());
			collisionSystem.process ();
			positionHistorySystem.forward ();
			positionHistorySystem.process ();

		}
		worldSimulationTimer += delta;



		serverNetworkTimer += delta;

		if (serverNetworkTimer > GameTimeManager.SERVER_NETWORK_INTERVAL)
		{
			serverNetworkTimer -= GameTimeManager.SERVER_WORLD_SIMULATION_INTERVAL;
			entitySendNetworkSystem.process ();
			for (User user : commonWorld.getUserManager ().getUsers ())
			{
				network.sendMessages (user);
			}
			network.clearUserCompressedMessagePerTick ();
		}
	}

	public void setGameTimeManager (GameTimeManager gameTimeManager)
	{
		this.gameTimeManager = gameTimeManager;
	}

	public GameTimeManager getGameTimeManager (byte clientId)
	{
		return gameTimeManager;
	}

	public void sendMessage (User user, Message message)
	{
		network.sendMessage (user, message);
	}

	public void sendReliableMessage (User user, Message message)
	{
		network.sendReliableMessage (user, message);
	}

	public User getUserByNetworkId (String networkId)
	{
		return commonWorld.getUserManager ().findByNetworkId (networkId);
	}

	public void render (GameContainer container2, StateBasedGame arg1, Graphics g)
	{
//		if (initialized)
//			renderSystem.process ();
//		g.drawString ("Server-Entities: " + commonWorld.getEntityManager ().getTotalCreated (), 400, 200);
	}
}
