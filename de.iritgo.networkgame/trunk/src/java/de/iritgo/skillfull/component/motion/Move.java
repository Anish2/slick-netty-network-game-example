
package de.iritgo.skillfull.component.motion;


import java.util.TreeMap;
import java.util.Map.Entry;

import com.artemis.Component;


public class Move extends Component
{
	private TreeMap<Long, Position> posInterpolates = new TreeMap<Long, Position> ();
	private float rotation;
	private float speedX;
	private float speedY;
	private Object[] cubicHistory;
	private long cubicHistoryStartTime;

	public Move ()
	{
	}

	public void addInterpolationPos (long time, Position position)
	{
		posInterpolates.put (time, position);
		if (posInterpolates.size () > 1000)
			posInterpolates.clear ();
	}

	public Entry<Long, Position> getInterpolationPos (long time)
	{
		Entry<Long, Position> entry = posInterpolates.higherEntry (time - 30);

		if (entry != null)
		{
			if ((entry.getKey () - time) > 100)
			{
				return null;
			}
			posInterpolates.remove (entry.getKey ());
		}
		return entry;
	}

	public void setLastRotation (float rotation)
	{
		this.rotation = rotation;
	}

	public float getLastRotation ()
	{
		return rotation;
	}

	public void setCurrentSpeedX (float vx)
	{
		this.speedX = vx;
	}

	public void setCurrentSpeedY (float vy)
	{
		this.speedY = vy;
	}

	public float getCurrentSpeedX ()
	{
		return speedX;
	}

	public float getCurrentSpeedY ()
	{
		return speedY;
	}

	public void resetInterpolationTree ()
	{
		posInterpolates.clear ();
	}

	public void setCubicHistory (Object[] array)
	{
		cubicHistory = array;
	}

	public Object[] getLastPositionHistory ()
	{
		return cubicHistory;
	}

	public void setCubicHistoryStartTime (long startTime)
	{
		this.cubicHistoryStartTime = startTime;
	}

	public long getCubicHistoryStartTime ()
	{
		return cubicHistoryStartTime;
	}
}
