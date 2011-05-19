
package de.iritgo.skillfull.component.motion;


import java.util.SortedMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;

import de.iritgo.skillfull.time.GameTimeManager;


public class StorePositionHistorySystem extends EntityProcessingSystem
{
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<PositionHistory> positionHistoryMapper;
	private GameTimeManager gameTime;

	public StorePositionHistorySystem (GameTimeManager gameTime)
	{
		super (PositionHistory.class, Position.class);
		this.gameTime = gameTime;
	}

	@Override
	public void initialize ()
	{
		positionMapper = new ComponentMapper<Position> (Position.class, world.getEntityManager ());
		positionHistoryMapper = new ComponentMapper<PositionHistory> (PositionHistory.class, world.getEntityManager ());
	}

	@Override
	protected void process (Entity e)
	{
		Position pos = positionMapper.get (e);
		PositionHistory posHis = positionHistoryMapper.get (e);
		if (posHis == null)
		{
			System.out.println ("*********** WAHT THE FUCK!*********");
		}
		else
		if (posHis.isExpandableTimeRange () && ! posHis.isInTimeRange (gameTime.getNetworkTime ()))
			posHis.addHistoryPos (gameTime.getNetworkTime (), new Position (pos));
	}
}
