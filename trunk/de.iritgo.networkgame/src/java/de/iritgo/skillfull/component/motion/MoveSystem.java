
package de.iritgo.skillfull.component.motion;


import java.util.SortedMap;
import java.util.Map.Entry;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.utils.Utils;

import de.iritgo.skillfull.time.GameTimeManager;


public class MoveSystem extends EntityProcessingSystem
{
	private ComponentMapper<Move> moveInterpolateMapper;

	private ComponentMapper<Position> positionMapper;

	private ComponentMapper<PositionHistory> historyPositionMapper;

	private GameTimeManager gameTimeManager;

	public MoveSystem (GameTimeManager gameTimeManager)
	{
		super (Move.class, Position.class, PositionHistory.class);
		this.gameTimeManager = gameTimeManager;
	}

	@Override
	public void initialize ()
	{
		moveInterpolateMapper = new ComponentMapper<Move> (Move.class, world.getEntityManager ());
		positionMapper = new ComponentMapper<Position> (Position.class, world.getEntityManager ());
		historyPositionMapper = new ComponentMapper<PositionHistory> (PositionHistory.class, world.getEntityManager ());
	}

	@Override
	protected void process (Entity e)
	{
		Move move = moveInterpolateMapper.get (e);
		Position position = positionMapper.get (e);
		PositionHistory historyPosition = historyPositionMapper.get (e);

		float currentTime = gameTimeManager.getNetworkTime ();
		currentTime = currentTime - (gameTimeManager.getLag ());

		Entry<Long, Position> entry = null;
		entry = move.getInterpolationPos ((long) currentTime);
		Position lastPosition = null;
		if (entry == null && move.getLastPositionHistory () != null)
		{
			lastPosition = (Position) move.getLastPositionHistory ()[move.getLastPositionHistory ().length - 1];
		}
		else
		{
			lastPosition = null;
		}

		float cx = position.getX ();
		float cy = position.getY ();

		if (entry != null)
		{
			historyPosition.addHistoryPos (entry.getKey (), entry.getValue ());
			float x = entry.getValue ().getX ();
			float y = entry.getValue ().getY ();

			if (currentTime <= entry.getKey ())
			{
				if (lastPosition == null)
				{
					float rotation = Utils.angleInDegrees (cx, cy, x, y);

					move.setLastRotation (rotation);
					move.setCurrentSpeedX ((Math.abs (x - cx) / (entry.getKey () - currentTime))
									* (gameTimeManager.getDelta () + gameTimeManager.lastLoopDelta ()));
					move.setCurrentSpeedY ((Math.abs (y - cy) / (entry.getKey () - currentTime))
									* (gameTimeManager.getDelta () + gameTimeManager.lastLoopDelta ()));

					SortedMap<Long, Position> history = historyPosition.getHistoryEntries (
									(long) currentTime - (gameTimeManager.getLag () + 500), (long) currentTime);
					if (history.size () >= 3)
					{
						long startTime = entry.getKey ();
						move.setCubicHistory (history.values ().toArray ());
						move.setCubicHistoryStartTime (startTime);
					}
				}

				if (position.getX () == Float.NaN || position.getX () < -10000)
				{
					System.out.println ("Defekt");
					position.setX (0);
					position.setY (0);
				}
				if (Math.abs ((entry.getKey () - currentTime)) < 10)
				{
					position.setX (x);
					position.setY (y);
					return;
				}
				if (cx == x && cy == y)
				{
					position.setX (x);
					position.setY (y);
					return;
				}
				position.setX (Utils.lerp (cx, x, (float) (entry.getKey () - currentTime) / 1000));
				position.setY (Utils.lerp (cy, y, (float) (entry.getKey () - currentTime) / 1000));
			}
		}
		else
		{
			if (lastPosition == null)
				return;
			float x = position.getX ();
			float y = position.getY ();
			float rotation = move.getLastRotation ();
			float nextPosX = Utils.getXAtEndOfRotatedLineByOrigin (x, 1, rotation);
			float nextPosY = Utils.getYAtEndOfRotatedLineByOrigin (y, 1, rotation);

			position.setX (nextPosX);
			position.setY (nextPosY);
		}
	}
}
