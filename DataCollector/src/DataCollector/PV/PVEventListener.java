/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
/**
 * 
 */
package DataCollector.PV;

import java.util.EventListener;


/**
 * @author Kasberger.Christian
 *
 */
public interface PVEventListener extends EventListener{
	public void PVEventFired(PVEvent evt);
}
