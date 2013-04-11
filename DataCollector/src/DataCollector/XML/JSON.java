/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
package DataCollector.XML;

import java.net.MalformedURLException;
import java.net.URL;

public class JSON {
	private URL url;
	private String password;
	private int interval;

	public JSON() {

	}

	public synchronized URL getUrl() {
		return url;
	}

	public synchronized void setUrl(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized String getPassword() {
		return password;
	}

	public synchronized void setPassword(String password) {
		this.password = password;
	}

	public synchronized int getInterval() {
		return interval;
	}

	public synchronized void setInterval(int interval) {
		this.interval = interval;
	}
}
