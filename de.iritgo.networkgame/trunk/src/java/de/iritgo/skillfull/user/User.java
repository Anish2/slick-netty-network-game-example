package de.iritgo.skillfull.user;

public class User
{
	private int id;
	
	private String networkId;
	
	private String name;
	
	private String password;
	
	private byte clientId;

	private long entityId;

	
	public void setId (int id)
	{
		this.id = id;
	}
	
	public int getId ()
	{
		return id;
	}
	
	public String getNetworkId ()
	{
		return networkId;
	}

	public void setNetworkId (String networkId)
	{
		this.networkId = networkId;
	}
	
	public String getName ()
	{
		return name;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	public String getPassword ()
	{
		return password;
	}

	public void setPassword (String password)
	{
		this.password = password;
	}
	
	public byte getClientId ()
	{
		return clientId;
	}
	
	public void setClientId (byte userId)
	{
		this.clientId = userId;
	}

	public long getEntityId ()
	{
		return entityId;
	}
	
	public void setEntityId (long entityId)
	{
		this.entityId = entityId;
	}
}
