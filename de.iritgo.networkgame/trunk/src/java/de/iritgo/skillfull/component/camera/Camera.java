package de.iritgo.skillfull.component.camera;

import org.newdawn.slick.geom.Rectangle;

import com.artemis.Component;

public class Camera extends Component 
{
	private Rectangle viewPort;
	private boolean active;
	private float offsetX;
	private float offsetY;
	private float x;
	private float y;
	
	private boolean followEntity;
	
	private long followEntityUniqueId;

	public void setViewPort (Rectangle viewPort)
	{
		this.viewPort = viewPort;
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
	
	public Rectangle getViewPort ()
	{
		return viewPort;
	}

	public float getOffsetX ()
	{
		return offsetX;
	}

	public float getOffsetY ()
	{
		return offsetY;
	}

	public float getX ()
	{
		return x;
	}

	public float getY ()
	{
		return y;
	}
	
	public void setX (float x)
	{
		this.x = x;
	}
	
	public void setY (float y)
	{
		this.y = y;
	}

	public boolean isFollowEntity ()
	{
		return followEntity;
	}

	public void setFollowEntity (boolean followEntity)
	{
		this.followEntity = followEntity;
	}

	public long getFollowEntityUniqueId ()
	{
		return followEntityUniqueId;
	}

	public void setFollowEntityUniqueId (long uniqueEntityId)
	{
		this.followEntityUniqueId = uniqueEntityId;
	}

	public void setActive (boolean active)
	{
		this.active = active;
	}
}
