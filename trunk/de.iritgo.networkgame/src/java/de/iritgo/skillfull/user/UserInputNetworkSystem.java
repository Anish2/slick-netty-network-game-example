
package de.iritgo.skillfull.user;


import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;

import de.iritgo.skillfull.component.input.InputKeyFocus;
import de.iritgo.skillfull.component.input.InputKeySystem;
import de.iritgo.skillfull.component.motion.Position;
import de.iritgo.skillfull.time.GameTimeManager;
import de.iritgo.skillfull.user.network.UserInputMessage;
import de.iritgo.skillfull.world.CommonWorld;


public class UserInputNetworkSystem extends IntervalEntitySystem
{
	private ComponentMapper<InputKeyFocus> inputKeyFocusMapper;

	private ComponentMapper<Position> positionMapper;

	private CommonWorld commonWorld;

	private float lastPosX;

	private float lastPosY;

	private GameContainer container;

	private int mouseClickCoolDown;

	private boolean mouseWasClicked;

	private int mousePosX;

	private int mousePosY;

	private int lagTimer;

	public UserInputNetworkSystem (CommonWorld commonWorld, GameContainer container)
	{
		super (GameTimeManager.CLIENT_INPUT_SAMPLE_INTERVAL, InputKeyFocus.class, Position.class);
//		super (0, InputKeyFocus.class, Position.class);
		this.commonWorld = commonWorld;
		this.container = container;
	}

	@Override
	public void initialize ()
	{
		inputKeyFocusMapper = new ComponentMapper<InputKeyFocus> (InputKeyFocus.class, world.getEntityManager ());
		positionMapper = new ComponentMapper<Position> (Position.class, world.getEntityManager ());
	}

	@Override
	protected void processEntities (ImmutableBag<Entity> entities)
	{
		for (int i = 0; i < entities.size (); ++i)
		{
			process (entities.get (i));
		}
	}


	protected void process (Entity e)
	{
		InputKeyFocus focus = inputKeyFocusMapper.get (e);
		Position position = positionMapper.get (e);
		InputKeySystem inputKeySystem = commonWorld.getSystemManager ().getSystem (InputKeySystem.class);

		//TODO: Only one camera
		Entity cameraEntity = commonWorld.getGroupManager ().getEntities ("camera").get (0);
		Position cameraPosition = cameraEntity.getComponent (Position.class);

		float speed = 0.5f * commonWorld.getDelta ();
		if (focus.isPressed (Input.KEY_A))
		{
			position.addX (-0.5f * speed);
//			UserInputMessage userInput = new UserInputMessage ();
//			userInput.setPressedKey (Input.KEY_LEFT);
//			commonWorld.getNetwork ().sendReliableMessage (userInput);
		}
		if (focus.isPressed (Input.KEY_D))
		{
			position.addX (0.5f  * speed);
//			UserInputMessage userInput = new UserInputMessage ();
//			userInput.setPressedKey (Input.KEY_RIGHT);
//			commonWorld.getNetwork ().sendReliableMessage (userInput);
		}
		if (focus.isPressed (Input.KEY_W))
		{
			position.addY (-0.5f  * speed);

//			UserInputMessage userInput = new UserInputMessage ();
//			userInput.setPressedKey (Input.KEY_UP);
//			commonWorld.getNetwork ().sendReliableMessage (userInput);
		}
		if (focus.isPressed (Input.KEY_S))
		{
			position.addY (0.5f  * speed);
//			UserInputMessage userInput = new UserInputMessage ();
//			userInput.setPressedKey (Input.KEY_DOWN);
//			commonWorld.getNetwork ().sendReliableMessage (userInput);
		}
		UserInputMessage userInput = new UserInputMessage ();

		if (focus.isPressed (Input.MOUSE_LEFT_BUTTON))
		{
			userInput.setMouseClicked ((byte) 0x1);
			userInput.setMouseX (container.getInput ().getMouseX ());
			userInput.setMouseY (container.getInput ().getMouseY ());
		}

		mouseClickCoolDown += commonWorld.getDelta ();
		if (mouseClickCoolDown > 200)
		{
			mouseClickCoolDown -= 200;
			if (mouseWasClicked)
			{
				userInput.setMouseClicked ((byte)0x1);
				userInput.setMouseX (mousePosX);
				userInput.setMouseY (mousePosY);
				mouseWasClicked = false;
			}
		}
		else
		{
			if (userInput.getMouseClicked () != 0)
			{
				mousePosX = (int) (userInput.getMouseX () + cameraPosition.getX ());
				mousePosY = (int) (userInput.getMouseY () + cameraPosition.getY ());
				mouseWasClicked = true;
			}
			userInput.setMouseClicked ((byte)0);
		}

		inputKeySystem.clearHistory ();

		if (userInput.getMouseClicked () == 0 && lastPosX == position.getX () && lastPosY == position.getY ())
			return;
		lastPosX = position.getX ();
		lastPosY = position.getY ();

		userInput.setPosX ((int) position.getX ());
		userInput.setPosY ((int) position.getY ());
		userInput.setTick ((int) commonWorld.getGameTimeManager ().getNetworkTime ());

		commonWorld.getNetwork ().sendReliableMessage (userInput);

	}
}
