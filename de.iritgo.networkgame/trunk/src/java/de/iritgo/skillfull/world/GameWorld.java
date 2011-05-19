package de.iritgo.skillfull.world;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.World;

import de.iritgo.skillfull.client.Client;
import de.iritgo.skillfull.eventbus.EventBusManager;
import de.iritgo.skillfull.server.Server;
import de.iritgo.skillfull.time.GameTimeManager;
import de.iritgo.skillfull.time.TimerManager;

public class GameWorld
{
	private EventBusManager eventBusManager;

	private Client client;

	private Server server;

	private boolean clientOnly;

	private TimerManager timerManager;

	private GameTimeManager gameTimeManager;

	private TiledMap map;

	public EventBusManager getEventBusManager ()
	{
		return eventBusManager;
	}

	public void setEventBusManager (EventBusManager eventBusManager)
	{
		this.eventBusManager = eventBusManager;
	}

	public Client getClient ()
	{
		return client;
	}

	public void setClient (Client client)
	{
		this.client = client;
	}

	public Server getServer ()
	{
		return server;
	}

	public void setServer (Server server)
	{
		this.server = server;
	}

	public TimerManager getTimerManager ()
	{
		return timerManager;
	}

	public void init (GameContainer container, boolean clientOnly)
	{
		this.clientOnly = clientOnly;

		eventBusManager = new EventBusManager ();
		eventBusManager.init ();

		gameTimeManager = new GameTimeManager (false);
		gameTimeManager.setNetworkTimeShift ((float) 1.000);

		timerManager = new TimerManager (this);
		client = new Client (this, container);
		client.setGameTimeManager (gameTimeManager);
		server = new Server (this, container);
		GameTimeManager bla = new GameTimeManager (true);
		bla.setNetworkTimeShift ((float)1.0);
		bla.setStartNetworkTime (0);
		server.setGameTimeManager (bla);
	}

	public void update (int delta)
	{
		timerManager.update ();
		if (! clientOnly)
		{
			server.update (delta);
		}
		client.update (delta);
	}

	public long getNetworkTime ()
	{
		return gameTimeManager.getNetworkTime ();
	}

	public long getSystemTime ()
	{
		return gameTimeManager.getSystemTime ();
	}

	public void render (GameContainer container, StateBasedGame arg1, Graphics g)
	{
		client.render (container, arg1, g);
//		if (! clientOnly)
			server.render (container, arg1, g);
	}

	public TiledMap getMap ()
	{
		return map;
	}
	
	public void setMap (TiledMap map)
	{
		this.map = map;
	}
}
