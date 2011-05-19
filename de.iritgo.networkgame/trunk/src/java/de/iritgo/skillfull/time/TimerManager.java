package de.iritgo.skillfull.time;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import de.iritgo.skillfull.world.GameWorld;

public class TimerManager
{
	private TreeMap<Long, TimerAction> networkTimers;
	private TreeMap<Long, TimerAction> systemTimers;
	private GameWorld world;
	
	public TimerManager (GameWorld world)
	{
		this.world = world;
		networkTimers = new TreeMap<Long, TimerAction> (new Comparator<Long>()
		{
			@Override
			public int compare (Long o1, Long o2)
			{
				return (int) (o1.longValue () - o2.longValue ());
			}
		});

		systemTimers = new TreeMap<Long, TimerAction> (new Comparator<Long>()
						{
							@Override
							public int compare (Long o1, Long o2)
							{
								return (int) (o1.longValue () - o2.longValue ());
							}
						});
}
	
	public void addNetworkTimer (long time, TimerAction action)
	{
		networkTimers.put (time + world.getNetworkTime (), action);
	}
	public void addSystemTimer (long time, TimerAction action)
	{
		systemTimers.put (time + world.getSystemTime (), action);
	}
	
	public void update ()
	{
		if (networkTimers.size () > 0 && world.getNetworkTime () > networkTimers.firstKey ())
		{
			networkTimers.remove (networkTimers.firstKey ()).execute ();
		}
		if (systemTimers.size () > 0 && world.getSystemTime () > systemTimers.firstKey ())
		{
			systemTimers.remove (systemTimers.firstKey ()).execute ();
		}
	}
}
