
package de.iritgo.skillfull.component.motion;


import com.artemis.Component;


public class SegmentMove extends Component
{
	private Position startPoint;

	private Position endPoint;

	private long startTick;

	private long destinationTick;

	private boolean started;

	private float speedY;

	private float speedX;

	private float delay;

	public SegmentMove ()
	{
	}

	public Position getStartPoint ()
	{
		return startPoint;
	}

	public void setStartPoint (Position startPoint)
	{
		this.startPoint = startPoint;
	}

	public Position getEndPoint ()
	{
		return endPoint;
	}

	public void setEndPoint (Position endPoint)
	{
		this.endPoint = endPoint;
	}

	public long getStartTick ()
	{
		return startTick;
	}

	public void setStartTick (long startTick)
	{
		this.startTick = startTick;
	}

	public void setDestinationTick (long destinationTick)
	{
		this.destinationTick = destinationTick;
	}

	public long getDestinationTick ()
	{
		return destinationTick;
	}

	public boolean isStarted ()
	{
		return started;
	}

	public void setStarted (boolean started)
	{
		this.started = started;
	}

	public void setSpeedX (float v)
	{
		this.speedX = v;
	}

	public float getSpeedX ()
	{
		return speedX;
	}

	public void setSpeedY (float vy)
	{
		this.speedY = vy;
	}

	public float getSpeedY ()
	{
		return speedY;
	}

	public void setDelay (float delay)
	{
		this.delay = delay;
	}

	public float getDelay ()
	{
		return delay;
	}
}
