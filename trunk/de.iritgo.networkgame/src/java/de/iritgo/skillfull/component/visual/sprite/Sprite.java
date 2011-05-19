package de.iritgo.skillfull.component.visual.sprite;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import com.artemis.Component;

import de.iritgo.skillfull.render.RenderIds;

public class Sprite extends Component 
{
	private Image sprite;
	
	private boolean active;
	private String imageFile;

	public Sprite (String imageFile)
	{
		this.imageFile = imageFile;
		try
		{
			sprite = new Image (imageFile);
		}
		catch (SlickException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setImage (Image sprite)
	{
		this.sprite = sprite;
	}
	
	public Image getImage ()
	{
		return sprite;
	}

	public boolean isActive ()
	{
		return active;
	}
	
	public void active ()
	{
		active = true;
	}
	
	public void inActive ()
	{
		active = false;
	}
	
	public Integer getRenderId ()
	{
		return RenderIds.SPRITE.getId ();
	}
}
