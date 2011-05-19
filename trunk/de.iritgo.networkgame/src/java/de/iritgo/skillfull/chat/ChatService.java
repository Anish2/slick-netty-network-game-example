package de.iritgo.skillfull.chat;

import de.iritgo.networkgame.ChatFrame;
import de.iritgo.simplelife.math.NumberTools;
import de.iritgo.skillfull.client.Client;
import de.iritgo.skillfull.entity.network.EntityCreateMessage;
import de.iritgo.skillfull.eventbus.EventHandler;
import de.iritgo.skillfull.user.User;
import de.iritgo.skillfull.world.CommonWorld;

public class ChatService
{
	
	private CommonWorld commonWorld;


	public ChatService (CommonWorld commonWorld)
	{
		this.commonWorld = commonWorld;
	}
	
	
	public void sendMessage (User user, String message)
	{
		ChatMessage chatMessage = new ChatMessage ((byte)user.getId (), message);
		commonWorld.getNetwork ().sendReliableMessage (chatMessage);
	}
	
	public void sendBroadcastMessage (User user, String message)
	{
		ChatMessage chatMessage = new ChatMessage ((byte)user.getId (), message);
		for (User toUser : commonWorld.getUserManager ().getUsers ())
		{
			commonWorld.getNetwork ().sendMessage (toUser, chatMessage);
		}
	}
	
	public void registerClientMessageListener ()
	{
		commonWorld.getEventBusManager ().subscribeOnSimpleEventBus (ChatMessage.class,
						new EventHandler<ChatMessage> ()
						{
							@Override
							public void handleEvent (ChatMessage event)
							{
								// Very bad hack!!!!! ->
								ChatFrame chatFrame = ChatFrame.getInstance (null);

								User user = commonWorld.getUserManager ().getUser ((int) event.getUserId ());
								if (user != null && user.getId () == commonWorld.getUserManager ().getAppUser ().getId ())
									return;
								
								if (user != null)
								{
									chatFrame.appendRow ("default", user.getName () + " >" + event.getMessage ());
								}
								else
								{
									chatFrame.appendRow ("default", "Unknown >" + event.getMessage ());
								}
							}
						});
	}

	public void registerServerMessageListener ()
	{
		commonWorld.getEventBusManager ().subscribeOnSimpleEventBus (ChatMessage.class,
						new EventHandler<ChatMessage> ()
						{
							@Override
							public void handleEvent (ChatMessage event)
							{
								String[] commands = event.getMessage ().split (" ");
								if (commands.length == 2)
								{
									if (commands[0].equals ("bots"))
									{
										commonWorld.getBotTest ().createBots (NumberTools.toInt (commands[1], 1));
									}
								}
								User user = commonWorld.getUserManager ().getUser ((int) event.getUserId ());
								sendBroadcastMessage (user, event.getMessage ());
							}
						});
	}
}
