
package de.iritgo.skillfull.pid;


public class Pid
{
	// Gains
	private double kp;

	private double ki;

	private double kd;

	// Running Values
	private long lastUpdate;

	private double lastPV;

	private double errSum;

	// Reading/Writing Values
	private PidProcessor readPV;

	private PidProcessor readSP;

	private PidProcessor writeOV;

	// Max/Min Calculation
	private double pvMax;

	private double pvMin;

	private double outMax;

	private double outMin;

	// Threading and Timing
	private double computeHz = 1.0f;

	private Thread runThread;

	public Pid (double pG, double iG, double dG, double pMax, double pMin, double oMax, double oMin,
					PidProcessor pvFunc, PidProcessor spFunc, PidProcessor outFunc)
	{
		kp = pG;
		ki = iG;
		kd = dG;
		pvMax = pMax;
		pvMin = pMin;
		outMax = oMax;
		outMin = oMin;
		readPV = pvFunc;
		readSP = spFunc;
		writeOV = outFunc;
	}

	// #endregion

	// #region Public Methods

	public void enable ()
	{
		if (runThread != null)
			return;

		reset ();

		runThread = new Thread(new Runnable()
		{
			@Override
			public void run ()
			{
				runPid ();
			}
		}
		);
//		runThread.IsBackground = true;
//		runThread.Name = "PID Processor";
		runThread.start ();
	}

	public void disable ()
	{
		if (runThread == null)
			return;

		readPV = null;
		readSP = null;
		writeOV = null;

		// runThread.Abort();
		runThread = null;
	}

	public void reset ()
	{
		errSum = 0.0f;
		lastUpdate = System.currentTimeMillis ();
	}

	// #endregion

	// #region Private Methods

	private double scaleValue (double value, double valuemin, double valuemax, double scalemin, double scalemax)
	{
		double vPerc = (value - valuemin) / (valuemax - valuemin);
		double bigSpan = vPerc * (scalemax - scalemin);

		double retVal = scalemin + bigSpan;

		return retVal;
	}

	private double clamp (double value, double min, double max)
	{
		if (value > max)
			return max;
		if (value < min)
			return min;
		return value;
	}

	public void compute ()
	{
		if (readPV == null || readSP == null || writeOV == null)
			return;

		double pv = readPV.read ();
		double sp = readSP.read ();

		// We need to scale the pv to +/- 100%, but first clamp it
//		pv = clamp (pv, pvMin, pvMax);
//		pv = scaleValue (pv, pvMin, pvMax, - 1.0f, 1.0f);

		// We also need to scale the setpoint
//		sp = clamp (sp, pvMin, pvMax);
//		sp = scaleValue (sp, pvMin, pvMax, - 1.0f, 1.0f);

		// Now the error is in percent...
		double err = sp - pv;

		double pTerm = err * kp;
		double iTerm = 0.0f;
		double dTerm = 0.0f;

		double partialSum = 0.0f;
		long nowTime = System.currentTimeMillis ();

//		if (lastUpdate != 0)
		{
			// double dT = (nowTime - lastUpdate).TotalSeconds;
//			double dT = (nowTime - lastUpdate) / 1000;
			double dT = 0.070;

			// Compute the integral if we have to...
			if (pv >= pvMin && pv <= pvMax)
			{
				partialSum = errSum + dT * err;
				iTerm = ki * partialSum;
			}

			if (dT != 0.0f)
				dTerm = kd * (pv - lastPV) / dT;
		}

		lastUpdate = nowTime;
		errSum = partialSum;
		lastPV = pv;

		// Now we have to scale the output value to match the requested scale
		double outReal = pTerm + iTerm + dTerm;

//		outReal = clamp (outReal, - 1.0f, 1.0f);
//		outReal = scaleValue (outReal, - 1.0f, 1.0f, outMin, outMax);

		// Write it out to the world
		writeOV.write (outReal);
	}

	// #endregion

	// #region Threading

	private void runPid ()
	{

		while (true)
		{
			try
			{
				int sleepTime = (int) (1000 / computeHz);
				Thread.sleep (sleepTime);
				compute ();
			}
			catch (Exception e)
			{

			}
		}

	}

	// #endregion

}
