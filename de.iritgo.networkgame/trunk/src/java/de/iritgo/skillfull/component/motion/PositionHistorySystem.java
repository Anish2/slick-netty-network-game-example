
package de.iritgo.skillfull.component.motion;


import java.util.SortedMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;


public class PositionHistorySystem extends EntityProcessingSystem
{
	private ComponentMapper<Position> positionMapper;
	private ComponentMapper<PositionHistory> historyPositionMapper;
	private long currentTime;
	private long delta;
	private boolean rewind;

	public PositionHistorySystem ()
	{
		super (PositionHistory.class, Position.class);
	}

	@Override
	public void initialize ()
	{
		positionMapper = new ComponentMapper<Position> (Position.class, world.getEntityManager ());
		historyPositionMapper = new ComponentMapper<PositionHistory> (PositionHistory.class, world.getEntityManager ());
	}

	public void setCurrentTime (long time)
	{
		this.currentTime = time;
	}

	public void setDelta (long delta)
	{
		this.delta = delta;
	}

	@Override
	protected void process (Entity e)
	{
		warpInTime (e, currentTime, delta);
	}

	public void warpInTime (Entity e, long currentTime, long delta)
	{
		Position position = positionMapper.get (e);
		PositionHistory historyPosition = historyPositionMapper.get (e);

		if (rewind)
		{
			if (historyPosition != null)
			{
				SortedMap<Long, Position> positions = null;
				long dynamicDelta = delta;
				while (positions == null && historyPosition.isInTimeRange (currentTime))
				{
					positions = historyPosition.getHistoryEntries (currentTime - delta, currentTime + dynamicDelta);
					dynamicDelta += delta;
					if ((currentTime + dynamicDelta) > (currentTime + 500))
					{
						// To far away...
						break;
					}
				}
				if (positions != null && positions.size () > 0)
				{
					Position pos = positions.get (positions.firstKey ());
					position.setLocation (pos.getX (), pos.getY ());
				}
				else
				{
					position.setLocation (-1000, -1000);
				}
			}
		}
		else
		{
			Position lastPos = historyPosition.getLastPosition ();
			if (lastPos != null)
			{
				position.setLocation (lastPos.getX (), lastPos.getY ());
			}
			else
			{
				System.out.println ("No last position!?" + e.getUniqueId ());
			}
		}
	}

	public void rewind (long time, long delta)
	{
		this.currentTime = time;
		this.delta = delta;
		rewind = true;
	}

	public void forward ()
	{
		rewind = false;
	}

	public void reward ()
	{
		rewind = true;
	}
}
