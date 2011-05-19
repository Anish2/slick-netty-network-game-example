package de.iritgo.skillfull.world;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

import com.artemis.Entity;
import com.artemis.SystemManager;
import com.artemis.World;

import de.iritgo.simplelife.data.Tuple2;
import de.iritgo.skillfull.component.input.InputKeyFocus;
import de.iritgo.skillfull.component.input.InputKeySystem;
import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.component.motion.SimpleInputKeyPositionMoverSystem;
import de.iritgo.skillfull.entity.EntityManager;
import de.iritgo.skillfull.eventbus.EventBusManager;
import de.iritgo.skillfull.fsm.FsmEventBus;
import de.iritgo.skillfull.network.CommonNetwork;
import de.iritgo.skillfull.render.RenderSystem;
import de.iritgo.skillfull.render.SpriteRender;
import de.iritgo.skillfull.server.BotTest;
import de.iritgo.skillfull.server.Server;
import de.iritgo.skillfull.time.GameTimeManager;
import de.iritgo.skillfull.user.UserManager;

public class CommonWorld extends World
{
	private EventBusManager eventBusManager;
	private UserManager userManager;
	private EntityManager skillFullEntityManager;
	private CommonWorldExtension extension;
	private GameTimeManager gameTimeManager;
	private List<InputKeySystem> keyListener;
	private List<InputKeySystem> mouseListener;
	private Rectangle playfield;
	private BotTest botTest;

	public void init (GameContainer container, CommonWorldExtension extension, GameTimeManager gameTimeManager)
	{
		this.extension = extension;
		this.gameTimeManager = gameTimeManager;

		keyListener = new ArrayList<InputKeySystem> ();
		mouseListener = new ArrayList<InputKeySystem> ();

		eventBusManager = new EventBusManager ();
		eventBusManager.init ();
		userManager = new UserManager ();
		userManager.init (this);

		skillFullEntityManager = new EntityManager ();
		skillFullEntityManager.init (this, container);

		SystemManager systemManager = getSystemManager ();

		extension.registerSystems (systemManager, gameTimeManager);

		systemManager.initializeAll ();
	}

	public EventBusManager getEventBusManager ()
	{
		return eventBusManager;
	}

	public CommonNetwork getNetwork ()
	{
		return extension.getNetwork ();
	}

	public EntityManager getSkillFullEntityManager ()
	{
		return skillFullEntityManager;
	}

	public GameTimeManager getGameTimeManager ()
	{
		return gameTimeManager;
	}

	public UserManager getUserManager ()
	{
		return userManager;
	}

	public void addKeyListener (InputKeySystem inputKeySystem)
	{
		keyListener.add (inputKeySystem);
	}

	public void addMouseListener (InputKeySystem inputKeySystem)
	{
		mouseListener.add (inputKeySystem);
	}

	public void fireKeyPressed (int keyCode, char keyChar)
	{
		if (keyListener == null)
			return;
		
		for (InputKeySystem system : keyListener)
		{
			system.keyPressed (keyCode, keyChar);
		}
	}

	public void fireKeyReleased (int keyCode, char keyChar)
	{
		if (keyListener == null)
			return;
		
		for (InputKeySystem system : keyListener)
		{
			system.keyReleased (keyCode, keyChar);
		}
	}

	public void fireMousePressed (int mouseButton, int mouseX, int mouseY)
	{
		if (keyListener == null)
			return;
		for (InputKeySystem system : keyListener)
		{
			system.mousePressed (mouseButton, mouseX, mouseY);
		}
	}

	public void fireMouseReleased (int mouseButton, int mouseX, int mouseY)
	{
		if (keyListener == null)
			return;
		
		for (InputKeySystem system : keyListener)
		{
			system.mouseReleased (mouseButton, mouseX, mouseY);
		}
	}

	public Entity getEntityByUniqueId (long uniqueEntityId)
	{
		return skillFullEntityManager.getEntityByUniqueId (uniqueEntityId);
	}
	
	public Rectangle getPlayfield ()
	{
		return playfield;
	}
	
	public void setPlayfield (Rectangle playfield)
	{
		this.playfield = playfield;
	}

	public BotTest getBotTest ()
	{
		// TODO Auto-generated method stub
		return botTest;
	}

	public void setBotTest (BotTest botTest)
	{
		this.botTest = botTest;
	}
}
