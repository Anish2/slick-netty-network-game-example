
package de.iritgo.skillfull.component.motion;


import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.artemis.Component;


public class PositionHistory extends Component
{
	private TreeMap<Long, Position> posHistory = new TreeMap<Long, Position> ();

	private long firstTime;
	private long lastTime;

	private boolean expandableTimeRange;

	public void addHistoryPos (long time, Position position)
	{
		if (firstTime > time || firstTime == 0)
		{
			firstTime = time;
		}

		if (lastTime < time)
		{
			lastTime = time;
		}
		posHistory.put (time, position);
		if (posHistory.size () > 2000)
		{
			posHistory.remove (posHistory.firstKey ());
			firstTime = posHistory.firstEntry ().getKey ();
		}
	}

	public Entry<Long, Position> getHistoryPos (long time)
	{
		Entry<Long, Position> entry = posHistory.higherEntry (time);
		return entry;
	}

	public SortedMap<Long, Position> getHistoryEntries (long startTime, long endTime)
	{
		return posHistory.subMap (startTime, endTime);
	}

	public Position getLastPosition ()
	{
		Entry<Long, Position> lastPos = posHistory.lastEntry ();
		return lastPos != null ? lastPos.getValue () : null;
	}

	public boolean isInTimeRange (long time)
	{
		return time >= firstTime && time <= lastTime;
	}

	public void setExpandableTimeRange (boolean felixbleTimeRange)
	{
		this.expandableTimeRange = felixbleTimeRange;
	}

	public boolean isExpandableTimeRange ()
	{
		return expandableTimeRange;
	}
}
