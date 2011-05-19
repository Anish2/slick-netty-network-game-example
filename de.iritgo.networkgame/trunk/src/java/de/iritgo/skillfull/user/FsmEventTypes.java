package de.iritgo.skillfull.user;

public enum FsmEventTypes
{
	IDLE (0),
	LOGIN (1),
	LOGOFF (2), 
	LOGIN_RESPONSE (3), 
	USER_INPUT (4), 
	NEW_USER (5);
	
	
	private int eventType;
	
	private FsmEventTypes (int eventType)
	{
		this.eventType = eventType;
	}
	
	public int id ()
	{
		return eventType;
	}
}
