
package de.iritgo.skillfull.render;


import org.newdawn.slick.Graphics;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;

import de.iritgo.skillfull.component.visual.sprite.Sprite;


public class SpriteRender extends Render
{
	private ComponentMapper<Sprite> spriteMapper;

	public SpriteRender (World world)
	{
		super (world);
	}

	public void initialize ()
	{
		spriteMapper = new ComponentMapper<Sprite> (Sprite.class, world.getEntityManager ());
	}

	@Override
	public int getRenderId ()
	{
		return RenderIds.SPRITE.getId ();
	}

	@Override
	public void render (float x, float y, Entity entity, Graphics g, boolean you)
	{
		Sprite sprite = spriteMapper.get (entity);
		g.drawImage (sprite.getImage (), x, y);
		if (you)
		{
			g.drawString ("Y", x + 10, y + 20);
		}
	}
}
