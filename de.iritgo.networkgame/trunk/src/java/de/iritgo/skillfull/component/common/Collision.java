
package de.iritgo.skillfull.component.common;


import com.artemis.Component;


public class Collision extends Component
{
	private int collisionOriginator;
	
	private boolean collisionable;

	private boolean collision;

	public Collision ()
	{
		collisionable = true;
		collision = false;
		collisionOriginator = -1;
	}

	public int getCollisionOriginator ()
	{
		return collisionOriginator;
	}

	public void setCollisionOriginator (int collisionOriginator)
	{
		this.collisionOriginator = collisionOriginator;
	}
	
	public boolean isCollisionable ()
	{
		return collisionable;
	}
	
	public void setCollisionable (boolean collisionable)
	{
		this.collisionable = collisionable;
	}
	
	public boolean isCollision ()
	{
		return collision;
	}
	
	public void setCollision (boolean collision)
	{
		this.collision = collision;
	}
}
