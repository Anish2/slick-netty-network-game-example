package de.iritgo.skillfull.network;

import de.iritgo.skillfull.user.User;

public interface CommonNetwork 
{
	int MAX_PACKET_SIZE = 500;

	public void receivedMessage (Message message);

	public void sendReliableMessage (User user, Message message);

	public void sendReliableMessage (Message message);

	public void sendCompressedMessage (Message message);

	public void sendCompressedMessage (User user, Message entityPos);

	public void sendMessage (User user, Message message);
}
