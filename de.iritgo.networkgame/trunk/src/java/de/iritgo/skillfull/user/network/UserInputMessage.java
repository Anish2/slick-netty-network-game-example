
package de.iritgo.skillfull.user.network;


import de.iritgo.skillfull.network.Message;
import de.iritgo.skillfull.network.Opcode;


public class UserInputMessage extends Message
{
	private int tick;

	private int pressedKey;

	private int mouseX;

	private int mouseY;

	private byte mouseClicked;

	private int posX;

	private int posY;

	public UserInputMessage ()
	{
		super (Opcode.USER_INPUT);
	}

	@Override
	public void transfer ()
	{
		channelBuffer.writeByte (Opcode.USER_INPUT.getObcode ());
		channelBuffer.writeInt (tick);
		channelBuffer.writeInt (pressedKey);
		channelBuffer.writeInt (mouseX);
		channelBuffer.writeInt (mouseY);
		channelBuffer.writeInt (posX);
		channelBuffer.writeInt (posY);
		channelBuffer.writeByte (mouseClicked);
	}

	public int getTick ()
	{
		return tick;
	}

	public void setTick (int tick)
	{
		this.tick = tick;
	}

	public int getPressedKey ()
	{
		return pressedKey;
	}

	public void setPressedKey (int pressedKey)
	{
		this.pressedKey = pressedKey;
	}

	public int getMouseX ()
	{
		return mouseX;
	}

	public void setMouseX (int mouseX)
	{
		this.mouseX = mouseX;
	}

	public int getMouseY ()
	{
		return mouseY;
	}

	public void setMouseY (int mouseY)
	{
		this.mouseY = mouseY;
	}

	public byte getMouseClicked ()
	{
		return mouseClicked;
	}

	public void setMouseClicked (byte mouseClicked)
	{
		this.mouseClicked = mouseClicked;
	}

	public void setPosX (int x)
	{
		this.posX = x;
	}

	public int getPosX ()
	{
		return posX;
	}

	public void setPosY (int posY)
	{
		this.posY = posY;
	}

	public int getPosY ()
	{
		return posY;
	}

}
