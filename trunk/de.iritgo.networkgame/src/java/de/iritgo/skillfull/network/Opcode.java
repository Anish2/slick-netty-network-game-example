
package de.iritgo.skillfull.network;


public enum Opcode
{
	// Without any data your length is 2
	CLOCK((byte) 0x01, 10),
	SEQUENCE((byte)(Options.DYNAMIC_LENGTH_MESSAGE | (byte)0x02), 15),
	RELIABLE((byte)(Options.DYNAMIC_LENGTH_MESSAGE | (byte)0x03), 11),
	LOGIN ((byte)(Options.DYNAMIC_LENGTH_MESSAGE | (byte)0x04), 6),
	RELIABLE_ACK ((byte) 0x05, 6),
	PINGPONG ((byte) 0x06, 10), 
	LOGIN_RESPONSE ((byte) 0x07, 4), 
	// Entity default length is 10
	ENTITY_CREATE ((byte) 0x08, 15), 
	ENTITY_POSITION ((byte) 0x9, 18), 
	ENTITY_MOVEWAY((byte) 0x10, 30),
	ENTITY_DESTROY((byte) 0x11, 11),
	USER_ENTITY_CREATE ((byte)0x12, 11),
	USER_INPUT ((byte) 0x13, 27), 
	SEQUENCE_ACK((byte) 0x14, 10),
	CHAT_MESSAGE((byte)(Options.DYNAMIC_LENGTH_MESSAGE | 0x15), 6), 
	NEW_USER((byte)(Options.DYNAMIC_LENGTH_MESSAGE | 0x16), 6);

	private byte opcode;
	private int  size;

	private Opcode (byte opcode, int size)
	{
		this.opcode = opcode;
		this.size = size;
	}

	public static Opcode fromByte (byte b)
	{
		for (Opcode opcode : values ())
		{
			if (opcode.opcode == b)
			{
				return opcode;
			}
		}
		throw new IllegalArgumentException ("No Opcode for byte: " + b);
	}

	public byte getObcode ()
	{
		return opcode;
	}

	public int getSize ()
	{
		return size;
	}
}
