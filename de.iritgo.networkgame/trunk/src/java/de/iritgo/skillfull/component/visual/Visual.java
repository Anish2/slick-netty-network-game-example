package de.iritgo.skillfull.component.visual;

import com.artemis.Component;

public class Visual extends Component
{

	private Integer renderId;
	private int width;
	private int height;

	public Visual (Integer renderId, int width, int height)
	{
		this.renderId = renderId;
		this.width = width;
		this.height = height;
	}

	public Integer getRenderId ()
	{
		return renderId;
	}
	
	public int getWidth ()
	{
		return width;
	}
	
	public int getHeight ()
	{
		return height;
	}
}
