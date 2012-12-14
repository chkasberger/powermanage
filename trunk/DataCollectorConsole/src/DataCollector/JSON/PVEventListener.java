/**
 * 
 */
package DataCollector.JSON;

import java.util.EventListener;


/**
 * @author Kasberger.Christian
 *
 */
public interface PVEventListener extends EventListener{
	public void PVEventFired(PVEvent evt);
}
