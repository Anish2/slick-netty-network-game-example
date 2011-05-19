
package de.iritgo.skillfull.component.common;


import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;

import de.iritgo.skillfull.world.CommonWorld;


public class ExpirationSystem extends EntityProcessingSystem
{

	private ComponentMapper<Expires> expiresMapper;
	private CommonWorld commonWorld;

	public ExpirationSystem (CommonWorld commonWorld)
	{
		super (Expires.class);
		this.commonWorld = commonWorld;
	}

	@Override
	public void initialize ()
	{
		expiresMapper = new ComponentMapper<Expires> (Expires.class, world.getEntityManager ());
	}

	@Override
	protected void process (Entity e)
	{
		Expires expires = expiresMapper.get (e);
		expires.reduceLifeTime (world.getDelta ());

		if (expires.isExpired ())
		{
			if (commonWorld.getSkillFullEntityManager ().removeEntityFromServerEntityCache (e) != null)
				world.deleteEntity (e);
		}

	}
}
