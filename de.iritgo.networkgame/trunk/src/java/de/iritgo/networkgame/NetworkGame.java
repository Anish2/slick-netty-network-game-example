
package de.iritgo.networkgame;


import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.EntitySystem;

import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.simplelife.string.StringTools;
import de.iritgo.skillfull.bullet.Bullet;
import de.iritgo.skillfull.bullet.BulletDirector;
import de.iritgo.skillfull.bullet.SequenceDirectorIterator;
import de.iritgo.skillfull.client.Client;
import de.iritgo.skillfull.component.camera.Camera;
import de.iritgo.skillfull.component.visual.sprite.Sprite;
import de.iritgo.skillfull.render.RenderSystem;
import de.iritgo.skillfull.server.Server;
import de.iritgo.skillfull.twl.BasicTWLGameState;
import de.iritgo.skillfull.world.CommonWorld;
import de.iritgo.skillfull.world.GameWorld;


/**
 * The main for the slick beer game.
 *
 * @author held
 *
 */
public class NetworkGame extends BasicTWLGameState
{
	private TiledMap map;

	private GameWorld world;

	private Client client;

	private Server server;

	private ChatFrame chatFrame;

	private boolean chatTabToggle = true;

	private String serverIp;

	private String serverPort;

	private BulletDirector bulletDirector;

	private int blaTimer;

	private float xpos;

	private float blub;

	private float superBlub;

	public static Sprite apple = new Sprite ("data/apple.png");


	public NetworkGame (int state, String serverIp, String serverPort)
	{
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}

	/*
	 * @see org.newdawn.slick.BasicGame#init(org.newdawn.slick.GameContainer)
	 */
	public void init (GameContainer container, StateBasedGame s) throws SlickException
	{


		bulletDirector = new BulletDirector ()
		{
			public SequenceDirectorIterator createIterator (Bullet bullet)
			{
				return new BulletSeq (bullet, bulletDirector);
			}
		};

		bulletDirector.createBullets (200);













		container.setAlwaysRender (true);
		container.setShowFPS (true);
		container.setVSync (true);
		// Zeitscheibe auf 20ms
//		container.setMinimumLogicUpdateInterval (15);
		container.setSmoothDeltas (false);
		map = new TiledMap ("maps/BeerMap.tmx");

		world = new GameWorld ();
		boolean clientOnly = false;
		world.init (container, clientOnly);
		world.setMap (map);
		server = world.getServer ();
		client = world.getClient ();
		if (StringTools.isNotTrimEmpty (serverIp))
		{
			server.init (serverIp, NumberTools.toInt (serverPort, 9000));
		}

        chatFrame = ChatFrame.getInstance (this);
        getRootPane ().add(chatFrame);

        chatFrame.setSize(600, 400);
        chatFrame.setPosition(10, 250);
	}

	/**
	 * @see org.newdawn.slick.state.GameState#render(org.newdawn.slick.GameContainer,
	 *      org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void update (GameContainer container, StateBasedGame arg1, int delta) throws SlickException
	{
		world.update (delta);
		bulletDirector.update (delta);

		blaTimer += delta;
		blub = (( (0.005f * 1000) * blaTimer) / 1000);
		xpos = blub + superBlub;
		if (blaTimer > 1500)
		{
			superBlub += (( (0.005f * 1000) * blaTimer) / 1000);
			blaTimer = blaTimer - 1500;

		}

		if (container.getInput ().isKeyPressed (Input.KEY_TAB))
		{
			chatTabToggle = ! chatTabToggle;
			chatFrame.setVisible (chatTabToggle);
		}
	}

	@Override
	public void keyPressed (int key, char c)
	{
		super.keyPressed (key, c);
	}

	@Override
	public void keyReleased (int key, char c)
	{
		super.keyReleased (key, c);
	}

	/**
	 * @see org.newdawn.slick.state.GameState#render(org.newdawn.slick.GameContainer,
	 *      org.newdawn.slick.state.StateBasedGame, org.newdawn.slick.Graphics)
	 */
	public void render (GameContainer container, StateBasedGame arg1, Graphics g) throws SlickException
	{
		world.render (container, arg1, g);
		for (int i = 0 ; i < bulletDirector.getBullets ().size (); ++i)
		{
			Bullet bullet = bulletDirector.getBullets ().get (i);
			apple.getImage ().setRotation (bullet.getRotation ());
			g.drawImage (apple.getImage (), (int)bullet.getX (), (int)bullet.getY ());
		}
		g.drawImage (apple.getImage (), (int)100 + xpos, (int)50);


		g.drawString ("GameTime: " + world.getNetworkTime (), 100, 10);
		g.drawString ("TimeShift: " + client.getGameTimeManager ().getNetworkTimeShift (), 280, 10);
	}

	public int getID ()
	{
		return 2;
	}

	@Override
	public CommonWorld getCommonWorld ()
	{
		return client.getCommonWorld ();
	}

	public void localChatText (String text)
	{
		String [] commands = text.split (" ");
		if (commands[0].equals ("client"))
		{
			if (commands.length == 2)
			{
				client.init (commands[1], "localhost", 9001, "localhost", 9000);
				chatFrame.appendRow ("default", "");
			}
			else if (commands.length == 6)
			{
				client.init (commands[1], commands[2], NumberTools.toInt (commands[3], 9005), commands[4], NumberTools.toInt (commands[5], 9000));
			}
			else
			{
				chatFrame.appendRow ("default", "To start the client enter: client <username> <localip> <localport> <serverip> <serverport>");
				return;
			}
			chatFrame.appendRow ("default", ".");
			chatFrame.appendRow ("default", "Try to connect...can take a while...");
		}
		else if (commands[0].equals ("server"))
		{
			if (commands.length == 1)
			{
				server.init ("localhost", 9000);
			}
			else if (commands.length == 3)
			{
				server.init (commands[1], NumberTools.toInt (commands[2], 9000));
			}
			else
			{
				chatFrame.appendRow ("default", "To start the client enter: client <localip> <serverip> <serverport>");
				return;
			}
			chatFrame.appendRow ("default", ".");
			chatFrame.appendRow ("default", "Server created...");
		}
		else
		{
			if (client.getChatService () != null)
				client.getChatService ().sendMessage (client.getCommonWorld ().getUserManager ().getAppUser (), text);
		}
	}
}
