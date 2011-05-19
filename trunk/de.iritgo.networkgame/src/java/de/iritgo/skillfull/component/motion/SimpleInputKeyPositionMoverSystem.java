package de.iritgo.skillfull.component.motion;



import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;

import de.iritgo.skillfull.component.input.InputKeyFocus;


public class SimpleInputKeyPositionMoverSystem extends EntityProcessingSystem
{
	private ComponentMapper<InputKeyFocus> inputKeyFocusMapper;
	private ComponentMapper<Position> positionMapper;

	public SimpleInputKeyPositionMoverSystem ()
	{
		super (InputKeyFocus.class, Position.class);
	}

	@Override
	public void initialize ()
	{
		inputKeyFocusMapper = new ComponentMapper<InputKeyFocus> (InputKeyFocus.class, world.getEntityManager ());
		positionMapper = new ComponentMapper<Position> (Position.class, world.getEntityManager ());
	}

	@Override
	protected void process (Entity e)
	{
		InputKeyFocus focus = inputKeyFocusMapper.get (e);
		Position position = positionMapper.get (e);
/*
		if (focus.isPressed (Input.KEY_LEFT))
		{
			position.addX (- 1.0f);
		}
		if (focus.isPressed (Input.KEY_RIGHT))
		{
			position.addX (1.0f);
		}
*/
	}
}
