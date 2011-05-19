
package de.iritgo.skillfull.render;


import org.newdawn.slick.Graphics;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;


public abstract class Render
{
	protected World world;

	protected Entity owner;

	public Render (World world)
	{
		this.world = world;
	}

	public abstract void initialize ();

	public abstract int getRenderId ();

	public abstract void render (float x, float y, Entity entity, Graphics g, boolean you);
}
