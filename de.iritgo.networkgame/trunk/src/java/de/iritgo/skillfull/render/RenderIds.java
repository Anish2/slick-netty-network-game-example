package de.iritgo.skillfull.render;

public enum RenderIds
{
	SPRITE (1),
	ANIM (2);
	
	
	/** Render id field*/
	private Integer id;

	RenderIds (Integer id)
	{
		this.id = id;
	}
	
	public int getId ()
	{
		return id;
	}
}
