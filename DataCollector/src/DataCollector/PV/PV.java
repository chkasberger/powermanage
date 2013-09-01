/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
package DataCollector.PV;

import java.io.BufferedInputStream;
//import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
//import java.io.PrintWriter;
//import java.net.Socket;
import java.net.URL;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

//import org.apache.log4j.Level;
import org.apache.log4j.Logger;
//import org.apache.log4j.lf5.LogLevel;
//import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

//import com.sun.jmx.snmp.Enumerated;

public class PV {
	final static Logger logger = Logger.getRootLogger();
	//static Socket socket;
	//static BufferedReader in;
	//static PrintWriter out;
	InputStream in;
	BufferedInputStream bIn;
	//static int deviceAddress;
	public enum SCOPE {
	    SYSTEM, THREEPHASE 
	}
	SCOPE scope;
	
	protected static EventListenerList listenerList = new EventListenerList();
	
	// static String url;
	URL url;

	public PV(URL url, SCOPE scope) {
		this.url = url;
		this.scope = scope;
	}

	public void addPVEventListener(PVEventListener listener) {
		listenerList.add(PVEventListener.class, listener);
	}

	public void removePVEventListener(PVEventListener listener) {
		listenerList.remove(PVEventListener.class, listener);
	}

	static void firePVEvent(PVEvent evt) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == PVEventListener.class) {
				((PVEventListener) listeners[i + 1]).PVEventFired(evt);
			}
		}
	}

	public void readValues() {

		try {
			in = url.openStream();
			bIn = new BufferedInputStream(in);
			javaJsonLib(bIn);
			
			in.close();
			bIn.close();
			
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private void javaJsonLib(BufferedInputStream bIn) {
		try {
			//org.json.JSONTokener jTok = new JSONTokener(bIn);
			JSONObject jObjTokens = new JSONObject(new JSONTokener(bIn));
		
			JSONObject jObj = jObjTokens.getJSONObject("Body").getJSONObject(
					"Data");
			
			double[] inverterValues = null;
			double[] values = new double[5];
			double[] values3p = new double[7];
			
			switch (scope) {
			case SYSTEM:
				values[0] = -1;
				
				JSONObject TOTAL_ENERGY = jObj.getJSONObject("TOTAL_ENERGY");
				JSONObject DAY_ENERGY = jObj.getJSONObject("YEAR_ENERGY");
				JSONObject YEAR_ENERGY = jObj.getJSONObject("DAY_ENERGY");
				JSONObject PAC = jObj.getJSONObject("PAC");

				values[1] = getValues(PAC, "PAC");
				values[2] = getValues(TOTAL_ENERGY, "TOTAL_ENERGY");
				values[3] = getValues(YEAR_ENERGY, "YEAR_ENERGY");
				values[4] = getValues(DAY_ENERGY, "DAY_ENERGY");
				inverterValues = values;
				
				break;
			case THREEPHASE:
				values3p[0] = 1;
				
				JSONObject UAC_L1 = jObj.getJSONObject("UAC_L1");
				JSONObject UAC_L2 = jObj.getJSONObject("UAC_L2");
				JSONObject UAC_L3 = jObj.getJSONObject("UAC_L3");
				JSONObject IAC_L1 = jObj.getJSONObject("IAC_L1");
				JSONObject IAC_L2 = jObj.getJSONObject("IAC_L2");
				JSONObject IAC_L3 = jObj.getJSONObject("IAC_L3");

				values3p[1] = getValues(UAC_L1, "UAC_L1");
				values3p[2] = getValues(UAC_L2, "UAC_L2");
				values3p[3] = getValues(UAC_L3, "UAC_L3");
				values3p[4] = getValues(IAC_L1, "IAC_L1");
				values3p[5] = getValues(IAC_L2, "IAC_L2");
				values3p[6] = getValues(IAC_L3, "IAC_L3");
				inverterValues = values3p;
				
				break;

			default:
				break;
			}

			/*for (double d : inverterValues) {
				logger.debug("Scope: " + scope + " " + d);
			}
			*/
			//PVEvent pvEventValues = ;
			firePVEvent(new PVEvent(inverterValues));

			for (int i = 0; i < inverterValues.length; i++) {
				inverterValues[i] = -1;
			}
			
			//bIn.reset();
			/*jObjTokens = null;
			jObj = null;
			inverterValues = null;
			values = null;
			values3p = null;
			*/
		} catch (JSONException e) {
			logger.error("Scope: " + scope + "\r\n" + e.getMessage());
		}
	}
	
	private double getValues(JSONObject refObj, String name) {
		JSONObject Values = null;
		Object unit = null;
		Iterator<?> vKey = null;
		double allValues = 0.0;
		double total = 0;
		
		switch (scope) {
		case SYSTEM:
			try {
				
				Values = refObj.getJSONObject("Values");
				
				unit = refObj.getString("Unit");
				vKey = Values.keys();
			} catch (JSONException e) {
				logger.error("getValues(): " + e.getMessage());
			}

			while (vKey.hasNext()) {

				double value;
				Object element = vKey.next();
				try {
					value = Values.getInt(element.toString());
					allValues += value;
					// System.out.print("\r\n\t\t" + element + "\t" + value + "\t" +
					// unit);
				} catch (JSONException e) {
					logger.error(e.getMessage());
				}
			}
			total = Math.round(allValues);
			total = total / 1000;

			break;
		case THREEPHASE:
			try {
				total = refObj.getDouble("Value");
				unit = refObj.getString("Unit");
			} catch (JSONException e) {
				logger.error("getValues(): " + e.getMessage());
			}

			break;
		default:
			break;
		}
						
		logger.debug("PV-System\t" + name + ": "+ total + " " + unit);
/*		Values = null;
		unit = null;
		vKey = null;
	*/	
		return 0.0 + total;
	}
}