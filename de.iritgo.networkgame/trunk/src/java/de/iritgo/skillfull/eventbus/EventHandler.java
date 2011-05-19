
package de.iritgo.skillfull.eventbus;


/**
 * @author synopia
 */
public interface EventHandler<E extends Event>
{
	void handleEvent (E event);
}
