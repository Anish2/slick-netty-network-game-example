
package de.iritgo.skillfull.component.common;


import com.artemis.Component;


public class Owner extends Component
{
	private int ownerId;

	public Owner ()
	{
	}

	public Owner (int ownerId)
	{
		this.ownerId = ownerId;
	}

	public int getOwnerId ()
	{
		return ownerId;
	}

	public void setOwnerId (int ownerId)
	{
		this.ownerId = ownerId;
	}
}
