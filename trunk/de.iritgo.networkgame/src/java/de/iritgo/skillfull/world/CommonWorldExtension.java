package de.iritgo.skillfull.world;

import com.artemis.SystemManager;

import de.iritgo.skillfull.network.CommonNetwork;
import de.iritgo.skillfull.time.GameTimeManager;

public interface CommonWorldExtension
{
	public void registerSystems (SystemManager systemManager, GameTimeManager gameTimeManager);

	public CommonNetwork getNetwork ();
}
