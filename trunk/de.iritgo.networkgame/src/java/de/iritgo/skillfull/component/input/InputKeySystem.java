
package de.iritgo.skillfull.component.input;


import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.MouseListener;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.artemis.IntervalEntitySystem;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

import de.iritgo.simplelife.data.Tuple2;
import de.iritgo.skillfull.time.GameTimeManager;
import de.iritgo.skillfull.world.CommonWorld;


public class InputKeySystem extends IntervalEntitySystem implements KeyListener, MouseListener
{
	private CommonWorld commonWorld;

	private ComponentMapper<InputKeyFocus> inputKeyFocusMapper;

	private Bag<Tuple2<Integer, Boolean>> keyHistory;

	public InputKeySystem (CommonWorld commonWorld)
	{
		super (GameTimeManager.CLIENT_INPUT_SAMPLE_INTERVAL, InputKeyFocus.class);
//		super (0, InputKeyFocus.class);
		this.commonWorld = commonWorld;
	}

	@Override
	public void initialize ()
	{
		inputKeyFocusMapper = new ComponentMapper<InputKeyFocus> (InputKeyFocus.class, world.getEntityManager ());
		commonWorld.addKeyListener (this);
		commonWorld.addMouseListener (this);
		keyHistory = new Bag<Tuple2<Integer, Boolean>> (15);
	}

	@Override
	protected void processEntities (ImmutableBag<Entity> entities)
	{
		for (int j = 0; j < entities.size (); ++j)
		{
			Entity e = entities.get (j);
			InputKeyFocus focus = inputKeyFocusMapper.get (e);
			for (int i = 0; keyHistory.size () > i; i++)
			{
				Tuple2<Integer, Boolean> key = keyHistory.get (i);
				if (focus.isKey (key.get1 ()))
				{
					focus.pressedKey (key.get1 (), key.get2 ());
				}
			}
		}
	}

	@Override
	public void keyPressed (int key, char c)
	{
		keyHistory.add (new Tuple2<Integer, Boolean> (key, true));
	}

	@Override
	public void keyReleased (int key, char c)
	{
		keyHistory.add (new Tuple2<Integer, Boolean> (key, false));
	}



	@Override
	public void inputEnded ()
	{
	}

	@Override
	public void inputStarted ()
	{
	}

	@Override
	public boolean isAcceptingInput ()
	{
		return true;
	}

	@Override
	public void setInput (Input input)
	{
	}

	@Override
	public void mouseClicked (int mouseButtonId, int x, int y, int doubleClicked)
	{
	}

	@Override
	public void mouseDragged (int arg0, int arg1, int arg2, int arg3)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved (int arg0, int arg1, int arg2, int arg3)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed (int mouseButtonId, int x, int y)
	{
		keyHistory.add (new Tuple2<Integer, Boolean> (Input.MOUSE_LEFT_BUTTON, true));
	}

	@Override
	public void mouseReleased (int mouseButtonId, int x, int y)
	{
		keyHistory.add (new Tuple2<Integer, Boolean> (Input.MOUSE_LEFT_BUTTON, false));
	}

	@Override
	public void mouseWheelMoved (int arg0)
	{
		// TODO Auto-generated method stub

	}

	public void clearHistory ()
	{
		keyHistory.fastClear ();
	}
}
