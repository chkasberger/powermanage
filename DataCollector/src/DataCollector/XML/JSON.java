/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non commercial use!
 *******************************************************************************/
package DataCollector.XML;

//import java.net.MalformedURLException;
import java.net.URL;

public class JSON {
	private URL url;
	private String password;
	private int interval;
	private String host;
	private String deviceId;
	private String collection;
	
	public JSON() {

	}

	public synchronized URL getUrl() {
		return url;
	}

	public synchronized void setUrl(String url) {
		try {
            this.url = new URL(url);
		} catch (Exception e) {
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

	public synchronized void setHost(String host) {
		this.host = host;		
	}
	
	public synchronized String getHost() {
		return host;
	}

	public synchronized void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public synchronized String getDeviceId() {
		return deviceId;
	}

	public synchronized void setCollection(String collection) {
		this.collection = collection;
	}

	public synchronized String getCollection() {
		return collection;
	}
}
