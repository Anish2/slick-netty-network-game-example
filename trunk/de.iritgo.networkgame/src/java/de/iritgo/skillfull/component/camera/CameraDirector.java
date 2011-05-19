
package de.iritgo.skillfull.component.camera;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;

import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.world.CommonWorld;


public class CameraDirector extends EntityProcessingSystem
{
	private ComponentMapper<Camera> cameraMapper;
	private ComponentMapper<Position> positionMapper;
	private CommonWorld commonWorld;
	private GameContainer container;
	private TiledMap map;


	public CameraDirector (GameContainer container, CommonWorld commonWorld, TiledMap map)
	{
		super (Camera.class, Position.class);

		this.commonWorld = commonWorld;
		this.container = container;
		this.map = map;
	}

	@Override
	public void initialize ()
	{
		cameraMapper = new ComponentMapper<Camera>(Camera.class, world.getEntityManager());
		positionMapper = new ComponentMapper<Position>(Position.class, world.getEntityManager());
	}

	@Override
	protected void process (Entity e)
	{
		Camera camera = cameraMapper.get (e);
		Position cameraPosition = positionMapper.get (e);
		if (camera.isActive ())
		{
			if (camera.isFollowEntity ())
			{
				Entity entity = commonWorld.getEntityByUniqueId (camera.getFollowEntityUniqueId ());
				Position followPos = entity.getComponent (Position.class);
				float screenWidth = container.getWidth ();
				float screenHeight = container.getHeight ();

				float cameraX = followPos.getX () - screenWidth / 2;
				float cameraY = followPos.getY () - screenHeight / 2;
				float mapWidth = map.getWidth () * map.getTileWidth ();
				float mapHeight = map.getHeight () * map.getTileHeight ();
				//if the camera reaches the left or right edge of the screen, lock it
				if(cameraX < 0) cameraX = 0;
				if(cameraX > mapWidth - screenWidth) cameraX = mapWidth - screenWidth;

				//if the camera reaches the top or bottom edge of the screen, lock it
				if(cameraY < 0) cameraY = 0;
				if(cameraY > mapHeight - screenHeight) cameraY = mapHeight - screenHeight;
				cameraPosition.setX (cameraX);
				cameraPosition.setY (cameraY);
			}
		}
	}
}
