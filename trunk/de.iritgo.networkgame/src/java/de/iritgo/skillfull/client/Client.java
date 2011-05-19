package de.iritgo.skillfull.client;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import com.artemis.EntitySystem;
import com.artemis.SystemManager;
import com.artemis.World;

import de.iritgo.skillfull.chat.ChatService;
import de.iritgo.skillfull.client.network.Network;
import de.iritgo.skillfull.component.camera.CameraDirector;
import de.iritgo.skillfull.component.common.ExpirationSystem;
import de.iritgo.skillfull.component.input.InputKeySystem;
import de.iritgo.skillfull.component.motion.MoveSystem;
import de.iritgo.skillfull.component.motion.SegmentMoveSystem;
import de.iritgo.skillfull.eventbus.EventBusManager;
import de.iritgo.skillfull.fsm.Fsm;
import de.iritgo.skillfull.network.ClockMessage;
import de.iritgo.skillfull.network.CommonNetwork;
import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.render.RenderSystem;
import de.iritgo.skillfull.render.SpriteRender;
import de.iritgo.skillfull.time.GameTimeManager;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.user.UserInputNetworkSystem;
import de.iritgo.skillfull.user.fsm.ClientFsm;
import de.iritgo.skillfull.user.network.LoginRequestMessage;
import de.iritgo.skillfull.world.CommonWorld;
import de.iritgo.skillfull.world.CommonWorldExtension;
import de.iritgo.skillfull.world.GameWorld;


public class Client
{
	private Network network;

	private GameWorld gameWorld;

	private GameTimeManager gameTimeManager;

	private EventBusManager eventBusManager;

	private GameContainer container;

	private RenderSystem renderSystem;

	private EntitySystem inputKeySystem;

	private UserInputNetworkSystem userInputNetworkSystem;

	private EntitySystem moveSystem;

	private CommonWorld commonWorld;

	private int clientNetworkTimer;

	protected EntitySystem segmentMoveSystem;

	protected EntitySystem expirationSystem;

	protected EntitySystem cameraDirector;

	private boolean initialized = false;

	private ChatService chatService;

	private int worldSimulationTimer;

	public Client (GameWorld gameWorld, final GameContainer container)
	{
		this.gameWorld = gameWorld;
		this.container = container;

		network = new Network ();
		commonWorld = new CommonWorld ();
	}

	public void init (String userName, String receiveIPAddress, int receivePort, String dstIPAddress, int dstPort)
	{

		CommonWorldExtension extension = new CommonWorldExtension ()
		{
			@Override
			public void registerSystems (SystemManager systemManager, GameTimeManager gameTimeManager)
			{
				renderSystem = (RenderSystem) systemManager.setSystem (new RenderSystem (container, commonWorld));
				renderSystem.setMap (gameWorld.getMap ());
				renderSystem.addRender (new SpriteRender ((World) commonWorld));

				inputKeySystem = systemManager.setSystem (new InputKeySystem (commonWorld));
				userInputNetworkSystem = (UserInputNetworkSystem) systemManager.setSystem (new UserInputNetworkSystem (commonWorld, container));
				moveSystem = systemManager.setSystem (new MoveSystem (gameTimeManager));
				segmentMoveSystem = systemManager.setSystem (new SegmentMoveSystem (gameTimeManager, commonWorld));
				expirationSystem = systemManager.setSystem (new ExpirationSystem (commonWorld));
				cameraDirector = systemManager.setSystem (new CameraDirector (container, commonWorld, gameWorld.getMap ()));
			}

			@Override
			public CommonNetwork getNetwork ()
			{
				return network;
			}
		};


		commonWorld.setPlayfield (new Rectangle (0, 0, gameWorld.getMap ().getWidth () * gameWorld.getMap ().getTileWidth (),
						gameWorld.getMap ().getHeight () * gameWorld.getMap ().getTileHeight ()));
		commonWorld.init (container, extension, gameTimeManager);

		commonWorld.getGroupManager ().set ("camera", commonWorld.getSkillFullEntityManager ().createCamera ());

		network.init (this, receiveIPAddress, receivePort, dstIPAddress, dstPort, new Random ().nextInt (128));

		ClientFsm clientFsm = new ClientFsm ();
		clientFsm.init (commonWorld);

		network.newReliableMessagePerTick ();

		User user = commonWorld.getUserManager ().registerUser (userName, "password");
		commonWorld.getUserManager ().setAppUser (user);
		initialized = true;
		chatService = new ChatService (commonWorld);
		chatService.registerClientMessageListener ();

		login (user, "default");
	}

	public void login (User user, String gameName)
	{
		network.sendReliableMessage (new LoginRequestMessage (user.getName (), user.getPassword (), gameName));
	}

	public void sendMessage (Message message)
	{
		network.sendMessage (message);
	}

	public void sendReliableMessage (Message message)
	{
		network.sendReliableMessage (message);
	}

	public void setGameTimeManager (GameTimeManager gameTimeManager)
	{
		this.gameTimeManager = gameTimeManager;
	}

	public GameTimeManager getGameTimeManager ()
	{
		return gameTimeManager;
	}

	public EventBusManager getEventBusManager ()
	{
		return eventBusManager;
	}

	public void update (int delta)
	{
		if (! initialized)
			return;

		gameTimeManager.startLoop ();
		gameTimeManager.update (delta);
		commonWorld.setDelta (delta);
		commonWorld.loopStart ();

		inputKeySystem.process ();
		userInputNetworkSystem.process ();
		expirationSystem.process ();
		cameraDirector.process ();

		worldSimulationTimer += delta;
		if (worldSimulationTimer >= GameTimeManager.CLIENT_WORLD_SIMULATION_INTERVAL)
		{
			worldSimulationTimer -= GameTimeManager.CLIENT_WORLD_SIMULATION_INTERVAL;
			moveSystem.process ();
			segmentMoveSystem.process ();
			for (Message message = network.getNewMessage (); message != null; message = network.getNewMessage ())
			{
				commonWorld.getEventBusManager ().publish (message);
			}
		}

		clientNetworkTimer += delta;
//		if (clientNetworkTimer > (100 + gameTimeManager.getServerClientPing ()) * gameTimeManager.getNetworkTimeShift ())
		if (clientNetworkTimer >= GameTimeManager.CLIENT_NETWORK_INTERVAL)
		{
			clientNetworkTimer -= GameTimeManager.CLIENT_NETWORK_INTERVAL;
			sendMessage (new ClockMessage ((int) gameTimeManager.getSystemTime (), 0));
			network.commitReliableMessagePerTick ();
			network.sendMessage (null);
			network.newReliableMessagePerTick ();
		}
		gameTimeManager.endLoop ();
	}

	public void render (GameContainer container, StateBasedGame arg1, Graphics g)
	{
		if (! initialized)
			return;
		renderSystem.process ();
//		System.out.println ("Client-Entities: " + commonWorld.getEntityManager ().getTotalCreated ());
		g.drawString ("Ping/Lag: " + commonWorld.getGameTimeManager ().getServerClientPing () + "/" + commonWorld.getGameTimeManager ().getLag (), 600, 10);
	}

	public CommonWorld getCommonWorld ()
	{
		return commonWorld;
	}

	public ChatService getChatService ()
	{
		return chatService;
	}
}
