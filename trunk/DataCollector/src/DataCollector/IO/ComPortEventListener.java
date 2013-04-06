/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
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
