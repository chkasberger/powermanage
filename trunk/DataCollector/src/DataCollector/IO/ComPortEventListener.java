/**
 * 
 */
package DataCollector.IO;

import java.util.EventListener;


/**
 * @author Kasberger.Christian
 *
 */
public interface ComPortEventListener extends EventListener{
	public void comPortEventFired(ComPortEvent evt);
}
