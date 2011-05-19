
package de.iritgo.skillfull.component.input;


import com.artemis.Component;


public class InputKeyFocus extends Component
{
	private int[] keys = new int[65];

	private boolean[] pressed = new boolean[65];

	private int index = 0;

	public InputKeyFocus ()
	{
		init ();
	}

	public InputKeyFocus (int... keys)
	{
		init ();
		for (int i = 0; keys.length > i && 64 > i; i++)
		{
			index++;
			this.keys[i] = keys[i];
		}
	}

	private void init ()
	{
		for (int i = 0; keys.length > i; i++)
		{
			keys[i] = - 1;
			pressed[i] = false;
		}
	}

	public void addKey (int key)
	{
		if (index <= 64)
			keys[++index] = key;
	}

	public boolean isKey (int key)
	{
		for (int i = 0; keys.length > i; i++)
		{
			if (keys[i] == key)
			{
				return true;
			}
			if (keys[i] == - 1)
			{
				return false;
			}
		}
		return false;
	}

	public void pressedKey (int key, boolean pressed)
	{
		for (int i = 0; keys.length > i; i++)
		{
			if (keys[i] == key)
			{
				this.pressed[i] = pressed;
				return;
			}
			if (keys[i] == -1)
				return;
		}
	}

	public boolean isPressed (int key)
	{
		for (int i = 0; keys.length > i; i++)
		{
			if (keys[i] == key)
			{
				return pressed[i];
			}
			if (keys[i] == -1)
				return false;
		}
		return false;
	}

}
