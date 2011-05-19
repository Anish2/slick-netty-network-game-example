
package de.iritgo.skillfull.render;


import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.GroupManager;
import com.artemis.utils.ImmutableBag;

import de.iritgo.skillfull.component.camera.Camera;
import de.iritgo.skillfull.component.input.InputKeyFocus;
import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.component.visual.Visual;
import de.iritgo.skillfull.world.CommonWorld;


public class RenderSystem extends EntitySystem
{
	private Graphics graphics;
	private GameContainer container;

	private GroupManager groupManager;

	private TiledMap map;

	private HashMap<Integer, Render> renderList;

	private ComponentMapper<Visual> visualMapper;
	private ComponentMapper<Position> positionMapper;

	private CommonWorld commonWorld;

	public RenderSystem (GameContainer container, CommonWorld commonWorld)
	{
		super (Visual.class);
		this.container = container;
		this.graphics = container.getGraphics ();
		renderList = new HashMap<Integer, Render> ();
		this.commonWorld =commonWorld;
	}

	public void initialize ()
	{
		groupManager = world.getGroupManager ();
		visualMapper = new ComponentMapper<Visual> (Visual.class, world.getEntityManager ());
		positionMapper = new ComponentMapper<Position> (Position.class, world.getEntityManager ());
	}

	public void addRender (Render render)
	{
		render.initialize ();
		renderList.put (render.getRenderId (), render);
	}

	@Override
	protected void processEntities (ImmutableBag<Entity> entities)
	{
		ImmutableBag<Entity> cameras = groupManager.getEntities ("camera");
		for (int c = 0; c < cameras.size (); ++c)
		{
			Camera camera = cameras.get (c).getComponent (Camera.class);
			Position cameraPosition = cameras.get (c).getComponent (Position.class);
			Rectangle viewPort = camera.getViewPort ();
			graphics.setClip (viewPort);
			float cameraX = cameraPosition.getX ();
			float cameraY = cameraPosition.getY ();

			if (map != null)
			{
				int tileOffsetX = (int) - (cameraX % map.getTileWidth ());
				int tileOffsetY = (int) - (cameraY % map.getTileHeight ());

				// calculate the index of the leftmost tile that is being displayed
				int tileIndexX = (int) (cameraX / map.getTileWidth ());
				int tileIndexY = (int) (cameraY / map.getTileHeight ());
				map.render ((int) viewPort.getX () - map.getTileWidth () + tileOffsetX,
								(int) viewPort.getY () - map.getTileHeight () + tileOffsetY, tileIndexX, tileIndexY,
								(int) viewPort.getWidth () + map.getTileWidth (),
								(int) viewPort.getHeight () + map.getTileHeight ());
			}
			graphics.translate (-cameraX, -cameraY);
			for (int i = 0; i < entities.size (); ++i)
			{
				Entity entity = entities.get (i);
				Visual visual = visualMapper.get (entity);
				if (visual == null)
					continue;

				Position position = positionMapper.get (entity);
				float x = position.getX ();
				float y = position.getY ();
				if (x + visual.getWidth () >= cameraX && x < (cameraX + viewPort.getWidth ())
				&& (y + visual.getHeight () >= cameraY && y < (cameraY + viewPort.getHeight ())))
				{
					renderList.get (visual.getRenderId ()).render (
									position.getX () + viewPort.getX (),
									position.getY () + viewPort.getY (),
									entity,
									graphics,
									// TODO: HACK!
									entity.getComponent (InputKeyFocus.class) != null);
				}
			}

			graphics.clearClip ();
			graphics.translate (cameraX, cameraY);
		}
	}

	@Override
	protected boolean checkProcessing ()
	{
		return true;
	}

	public void setMap (TiledMap map)
	{
		this.map = map;
	}

}
