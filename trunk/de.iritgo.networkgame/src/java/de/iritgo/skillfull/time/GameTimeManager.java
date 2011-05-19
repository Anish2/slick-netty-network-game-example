
package de.iritgo.skillfull.time;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.iritgo.skillfull.network.ClockMessage;
import de.iritgo.skillfull.pid.Pid;
import de.iritgo.skillfull.pid.PidProcessor;


public class GameTimeManager
{
	private static final int CLIENT_INTERPOLATION_TIME = 100; //ms
	public static final int CLIENT_NETWORK_INTERVAL = 33; // ms
	public static final int SERVER_NETWORK_INTERVAL = 50; // ms
	public static final int SERVER_WORLD_SIMULATION_INTERVAL = 33; //ms
	public static final int CLIENT_WORLD_SIMULATION_INTERVAL = 25; //ms
	public static final int CLIENT_INPUT_SAMPLE_INTERVAL = 33; //ms

	private static final int MAXIMUM_COLLECTED_PINGS = 32;
	private static final int MINIMUM_COLLECTED_PINGS = 16;

	private long networkTime;

	private float networkTimeShift;

	private LinkedList<Integer> pings = new LinkedList<Integer> ();

	public GameTimeManager (boolean server)
	{
	}

	PidProcessor measuredValue = new PidProcessor ()
	{
		double value;

		@Override
		public double write (double outReal)
		{
			value = outReal;
			return value;
		}

		@Override
		public double read ()
		{
			return value;
		}
	};

	PidProcessor dstValue = new PidProcessor ()
	{
		double value;

		@Override
		public double write (double outReal)
		{
			value = outReal;
			return value;
		}

		@Override
		public double read ()
		{
			return 100.0;
		}
	};

	PidProcessor outValue = new PidProcessor ()
	{
		double value;

		@Override
		public double write (double outReal)
		{
			value = outReal;
			return value;
		}

		@Override
		public double read ()
		{
			return value;
		}
	};

	Pid pid = new Pid (0.01, 0.00, 0.00, 10, 0, 10, 0.0, measuredValue, dstValue, outValue);

	private long lastServer;

	private long lastNetwork;

	private int delta;

	private long startLoop;

	private long lastLoopDelta;

	private int serverClientPing;

	private int serverClientPingAdded;

	public void update (int delta)
	{
		this.delta = delta;
		this.networkTime += (delta * networkTimeShift);
	}
	
	public int getDelta ()
	{
		return delta;
	}

	public long getNetworkTime ()
	{
		return networkTime;
	}

	public long getSystemTime ()
	{
		return System.currentTimeMillis ();
	}

	public void setNetworkTimeShift (float timeShift)
	{
		this.networkTimeShift = timeShift;
	}

	public float getNetworkTimeShift ()
	{
		return networkTimeShift;
	}

	public void updateNetworkClock (ClockMessage message)
	{
		long start = message.getStartTimestamp ();
		long server = message.getServerTimestamp ();
		int roundPing = (int) (getSystemTime () - start);

		if (pings.size () > MAXIMUM_COLLECTED_PINGS)
		{
			pings.remove (pings.getFirst ());
		}

		pings.add (roundPing);

		long serverTime = server + (roundPing / 2);
		
		if (pings.size () > MINIMUM_COLLECTED_PINGS)
		{
			// check clock
			int averagePing = 0;
			averagePing = calculateAveragePing (averagePing);

			if (roundPing-30 <= averagePing)
			{
				adjustTimeMultiplactor (server, roundPing, averagePing);
			}
		}
		else
		{
			networkTime = serverTime;
			networkTimeShift = 1.0f;
		}
	}

	private void adjustTimeMultiplactor (long serverTime, int roundPing, int averagePing)
	{
		serverClientPingAdded += averagePing / 2;
		serverClientPing = averagePing / 2;

		if ((serverTime / 30) == (networkTime / 30))
			networkTime = serverTime;

		float toleranz = ((float) (100.0 / (float) serverTime) * (float) networkTime);

		if (toleranz < 99.8 || toleranz > 100)
		{
			networkTime = serverTime;
		}
		else
		{
			measuredValue.write (toleranz);
			pid.compute ();
			networkTimeShift += (float) outValue.read ();
		}
	}

	private int calculateAveragePing (int averagePing)
	{
		int count = 0;
		List<Integer> tmpPings = new LinkedList<Integer> (pings);
		Collections.sort (tmpPings);
		for (Integer lowPings : tmpPings)
		{
			++count;
			averagePing += lowPings;
			if (count >= MINIMUM_COLLECTED_PINGS)
				break;
		}
		averagePing = averagePing / count;
		return averagePing;
	}

	public void setStartNetworkTime (int startTime)
	{
		this.networkTime = startTime;
	}

	public void startLoop ()
	{
		startLoop = System.currentTimeMillis ();
	}

	public void endLoop ()
	{
		lastLoopDelta = startLoop - System.currentTimeMillis ();
	}
	
	public long lastLoopDelta ()
	{
		return lastLoopDelta;
	}

	public int getServerClientPing ()
	{
		return serverClientPing;
	}

	public int getInterpolation ()
	{
		return CLIENT_INTERPOLATION_TIME;
	}
	
	public int getLag ()
	{
		return getInterpolation () + serverClientPing + delta;
	}
}
