package de.iritgo.skillfull;

import java.net.URL;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import de.iritgo.networkgame.NetworkGame;
import de.iritgo.skillfull.twl.TWLStateBasedGame;

public class Start extends TWLStateBasedGame {

	private static int TICKS_PER_SECOND = 25;
	private static int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
	private static int MAX_FRAMESKIP = 5;

	private static int loops = 0;
	private static long next_game_tick = 0l;
	private static boolean init = false;
	public static float interpolation;
	private String serverIp;
	private String serverPort;


	public Start(String name, String serverIp, String serverPort)
	{
		super(name);
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		// TODO Auto-generated constructor stub
	}

	public void initStatesList(GameContainer con) throws SlickException
	{
//		con.setTargetFrameRate(60);
//		con.setVSync(false);

		this.addState(new NetworkGame (2, serverIp, serverPort));
	}

	public static void main(String[] args) throws SlickException
	{
		String serverIp = "";
		String serverPort = "";
		
		
		if (args.length == 2)
		{
			serverIp = args[0];
			serverPort = args[1];
		}
		AppGameContainer app = new AppGameContainer(new Start ("Game", serverIp, serverPort));
/*
		{

			protected void updateAndRender(int delta) throws SlickException
			{
				if (!init)
				{
					init = true;
					next_game_tick = getTime ();
				}
				loops = 0;

				while( getTime () > next_game_tick && loops < MAX_FRAMESKIP)
				{
					input.poll(width, height);
					Music.poll(delta);
					game.update(this, 0);

					next_game_tick += SKIP_TICKS;
					loops++;
				}

				interpolation  = ((float) (getTime () + SKIP_TICKS - next_game_tick)) / ((float)( SKIP_TICKS ));
				if (hasFocus() || getAlwaysRender()) {
					if (clearEachFrame) {
						GL.glClear(SGL.GL_COLOR_BUFFER_BIT | SGL.GL_DEPTH_BUFFER_BIT);
					}

					GL.glLoadIdentity();
					Graphics graphics = getGraphics ();
					graphics.resetTransform();
					graphics.resetFont();
					graphics.resetLineWidth();
					graphics.setAntiAlias(false);
					try {
						game.render(this, graphics);
					} catch (Throwable e) {
						Log.error(e);
						throw new SlickException("Game.render() failure - check the game code.");
					}
					graphics.resetTransform();

					if (true) {
						getDefaultFont ().drawString(10, 10, "FPS: "+recordedFPS + "  - Inter: " + interpolation + " - Loop: "+ loops + " - delta: " + delta);
					}

					GL.flush();
				}

			}
*/
/*
				if (smoothDeltas) {
					if (getFPS() != 0) {
						delta = 1000 / getFPS();
					}
				}

				input.poll(width, height);

				Music.poll(delta);
				if (!paused) {
					storedDelta += delta;

					if (storedDelta >= minimumLogicInterval) {
						try {
							if (maximumLogicInterval != 0) {
								long cycles = storedDelta / maximumLogicInterval;
								for (int i=0;i<cycles;i++) {
									game.update(this, (int) maximumLogicInterval);
								}

								int remainder = (int) (delta % maximumLogicInterval);
								if (remainder > minimumLogicInterval) {
									game.update(this, (int) (delta % maximumLogicInterval));
									storedDelta = 0;
								} else {
									storedDelta = remainder;
								}
							} else {
								game.update(this, (int) storedDelta);
								storedDelta = 0;
							}

						} catch (Throwable e) {
							Log.error(e);
							throw new SlickException("Game.update() failure - check the game code.");
						}
					}
				} else {
					game.update(this, 0);
				}

				if (hasFocus() || getAlwaysRender()) {
					if (clearEachFrame) {
						GL.glClear(SGL.GL_COLOR_BUFFER_BIT | SGL.GL_DEPTH_BUFFER_BIT);
					}

					GL.glLoadIdentity();
					Graphics graphics = getGraphics ();
					graphics.resetTransform();
					graphics.resetFont();
					graphics.resetLineWidth();
					graphics.setAntiAlias(false);
					try {
						game.render(this, graphics);
					} catch (Throwable e) {
						Log.error(e);
						throw new SlickException("Game.render() failure - check the game code.");
					}
					graphics.resetTransform();

					if (true) {
						getDefaultFont ().drawString(10, 10, "FPS: "+recordedFPS);
					}

					GL.flush();
				}
				if (targetFPS != -1) {
					Display.sync(targetFPS);
				}
		};
 */
		app.setDisplayMode(1366, 768, true);
		app.start();

	}

	@Override
	protected URL getThemeURL ()
	{
		URL url = Thread.currentThread().getContextClassLoader()
		    .getResource("gui/chat.xml");
		return url;
	}
}
