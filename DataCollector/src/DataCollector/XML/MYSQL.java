/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
package DataCollector.XML;

public class MYSQL {

	private String hostname;
	private String database;
	private String table;
	private String table3p;
	private int port;
	private String user;
	private String password;
	private int interval;

	public MYSQL() {

	}

	public synchronized String getHostname() {
		return hostname;
	}

	public synchronized void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public synchronized String getDatabase() {
		return database;
	}

	public synchronized void setDatabase(String database) {
		this.database = database;
	}

	public synchronized String getTable() {
		return table;
	}

	public synchronized void setTable(String table) {
		this.table = table;
	}

	public synchronized String getTable3p() {
		return table3p;
	}

	public synchronized void setTable3p(String table3p) {
		this.table3p = table3p;
	}

	public synchronized int getPort() {
		return port;
	}

	public synchronized void setPort(int port) {
		this.port = port;
	}

	public synchronized String getUser() {
		return user;
	}

	public synchronized void setUser(String user) {
		this.user = user;
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
