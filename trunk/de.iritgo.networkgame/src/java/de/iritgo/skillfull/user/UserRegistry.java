package de.iritgo.skillfull.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class UserRegistry
{
	private int userCount;
	
	private HashMap<Integer, User> users;
	
	public UserRegistry ()
	{
		users = new HashMap<Integer, User> ();
		userCount = 0;
	}
	
	public void registerUser (User user)
	{
		user.setId (++userCount);
		users.put (user.getId (), user);
	}
	
	public void addUser (User user)
	{
		users.put (user.getId (), user);
	}

	public User getUser (Integer id)
	{
		return users.get (id);
	}

	public Collection<User> getUsers ()
	{
		return users.values ();
	}

	public void remove (User user)
	{
		users.remove (user.getId ());
	}
}
