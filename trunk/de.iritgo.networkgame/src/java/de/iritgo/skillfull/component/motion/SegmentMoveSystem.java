
package de.iritgo.skillfull.component.motion;


import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;

import de.iritgo.skillfull.time.GameTimeManager;
import de.iritgo.skillfull.world.CommonWorld;


public class SegmentMoveSystem extends EntityProcessingSystem
{
	private ComponentMapper<SegmentMove> segmentMoveMapper;

	private ComponentMapper<Position> positionMapper;

	private ComponentMapper<PositionHistory> positionHistoryMapper;

	private GameTimeManager gameTimeManager;

	private CommonWorld commonWorld;

	public SegmentMoveSystem (GameTimeManager gameTimeManager, CommonWorld commonWorld)
	{
		super (SegmentMove.class, Position.class, PositionHistory.class);
		this.gameTimeManager = gameTimeManager;
		this.commonWorld = commonWorld;
	}

	@Override
	public void initialize ()
	{
		segmentMoveMapper = new ComponentMapper<SegmentMove> (SegmentMove.class,
						world.getEntityManager ());
		positionMapper = new ComponentMapper<Position> (Position.class, world.getEntityManager ());
		positionHistoryMapper = new ComponentMapper<PositionHistory> (PositionHistory.class, world.getEntityManager ());
	}

	@Override
	protected void process (Entity e)
	{
		long currentTime = gameTimeManager.getNetworkTime ();
		calcMovement (e, currentTime, false);
	}

	public void calcMovement (Entity e, long currentTime, boolean simulate)
	{
		SegmentMove move = segmentMoveMapper.get (e);
		Position position = positionMapper.get (e);
		PositionHistory positionHistory = positionHistoryMapper.get (e);
		if (move == null)
		{
			System.out.println ("************** ERROR:" + e.getId () + " CustomId: " + e.getCustomId () + "Client-EntityUniqueId: " + e.getUniqueId ());
			if (commonWorld.getSkillFullEntityManager ().getEntityByUniqueId (e.getCustomId ()) != null)
			{
				System.out.println ("ServerChache: " + commonWorld.getSkillFullEntityManager ().getEntityByUniqueId (e.getCustomId ()).getUniqueId ());
			}
			else
			{
				System.out.println ("Cache ist leer!");
			}

		}

		if (move.getStartPoint () == null || move.getEndPoint () == null)
			return;

		float startX = move.getStartPoint ().getX ();
		float startY = move.getStartPoint ().getY ();
		float endX = move.getEndPoint ().getX ();
		float endY = move.getEndPoint ().getY ();
		float delay = move.getDelay ();
		long startTime = move.getStartTick ();
		long dstTime = move.getDestinationTick ();
		if (startTime < currentTime && dstTime > currentTime)
		{
			if (! move.isStarted ())
			{
				move.setStarted (true);
				delay = currentTime - startTime;
				move.setDelay (delay);
				position.setX (startX);
				position.setY (startY);
				float vx = (endX - startX) / (dstTime - currentTime);
				move.setSpeedX (vx);
				float vy = (endY - startY) / (dstTime - currentTime);
				move.setSpeedY (vy);
			}
			float posX = move.getSpeedX () * (currentTime - (startTime + delay));
			float posY = move.getSpeedY () * (currentTime - (startTime + delay));

//			System.out.println (startX + ":" + startY + " : " + endX + ":" + endY + ":C: " + position.getX () + "/"
//							+ position.getY () + " TIME: " + (currentTime - (startTime + delay)));
			position.setX (startX + posX);
			position.setY (startY + posY);
		}
		else if (move.isStarted ())
		{
			position.setX (endX);
			position.setY (endY);
			move.setStarted (false);
		}
		if (simulate)
		{
			positionHistory.addHistoryPos (currentTime, new Position (position));
		}
	}
}
