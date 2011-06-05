
package de.iritgo.networkgame;


import java.util.Random;

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

	private int delta;

	public static Sprite rocket = new Sprite ("data/rocket.png");


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
			public SequenceDirectorIterator createIterator ()
			{
				return new BulletSeq ();
			}
		};

		bulletDirector.createBullets (50);

//		for (int i = 0; i < 2000 ; i += 11)
//		{
//			int fuck = 10 + new Random ().nextInt (10);
//			blaTimer += fuck;
//			if (blaTimer >= 200)
//			{
//				superBlub += (( 50f/ 1000f) * (float)blaTimer) ;
//				blaTimer = blaTimer - 200;
//			}
//			blub = (( 50f/ 1000f) * (float)blaTimer) ;
//			xpos = blub + superBlub;
//			System.out.println ("Correct: " + (60 + xpos) + ":::" + blaTimer + " weg; " + blub);
//			bulletDirector.update (fuck);
//		}
//		System.exit (0);

		container.setAlwaysRender (true);
		container.setShowFPS (true);
		container.setVSync (true);
		// Zeitscheibe auf 20ms
//		container.setMinimumLogicUpdateInterval (60);
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
		this.delta = delta;
		world.update (delta);

		blaTimer += delta;
		if (blaTimer >= 200)
		{
			superBlub += (( 50f/ 1000f) * (float)blaTimer) ;
			blaTimer = blaTimer - 200;
		}
		blub = (( 50f/ 1000f) * (float)blaTimer) ;
		xpos = blub + superBlub;

//		System.out.println ("Correct: " + xpos + ":::" + blaTimer);

		bulletDirector.update (delta);

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
			if (bullet.isHeadingActive ())
				rocket.getImage ().setRotation (bullet.getHeading () + 90);
			else
				rocket.getImage ().setRotation (bullet.getRotation () + 90);

			g.drawImage (rocket.getImage (), (int)bullet.getX (), (int)bullet.getY ());
		}
		g.drawImage (rocket.getImage (), (int)60 + xpos, (int)250);


		g.drawString ("GameTime: " + world.getNetworkTime (), 100, 10);
		g.drawString ("TimeShift: " + client.getGameTimeManager ().getNetworkTimeShift (), 280, 10);
		g.drawString ("Delta: " + delta, 450, 10);
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
