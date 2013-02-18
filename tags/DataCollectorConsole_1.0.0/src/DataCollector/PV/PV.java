package DataCollector.PV;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

//import com.sun.jmx.snmp.Enumerated;


public class PV{
	static Socket socket;
	static BufferedReader in;
	static PrintWriter out;
	//static String url;
	URL url;

	public PV(URL url) {
		//url = new URL("http://wilma-pt2-12/solar_api/v1/GetInverterRealtimeData.cgi?Scope=System");
		//url = new URL("http://10.0.0.3/solar_api/GetInverterRealtimeData.cgi?Scope=System");
		this.url = url;
	}

	/**
	 * @description create listener components
	 */
	protected static EventListenerList listenerList = new EventListenerList();
	public void addPVEventListener(PVEventListener listener){
		listenerList.add(PVEventListener.class, listener);
	}

	public void removePVEventListener(PVEventListener listener){
		listenerList.remove(PVEventListener.class, listener);
	}

	static void firePVEvent(PVEvent evt){
		Object[] listeners = listenerList.getListenerList();
		for(int i=0; i<listeners.length; i+=2){
			if(listeners[i]==PVEventListener.class){
				((PVEventListener)listeners[i+1]).PVEventFired(evt);
			}
		}
	}

	public void readValues()
	{

		InputStream in;

		try {
			in = url.openStream();
			BufferedInputStream bIn = new BufferedInputStream(in);
			javaJsonLib(bIn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void javaJsonLib(BufferedInputStream bIn) {
		org.json.JSONTokener jTok = null;
		JSONObject jObj = null;
		JSONObject TOTAL_ENERGY = null;
		JSONObject DAY_ENERGY = null;
		JSONObject YEAR_ENERGY = null;
		JSONObject PAC = null;
		double[] values = new double[4];
		try {
			jTok = new JSONTokener(bIn);
			jObj = new JSONObject(jTok).getJSONObject("Body").getJSONObject("Data");
			//System.out.println("current keys: " + jObj.names());

			PAC = jObj.getJSONObject("PAC");
			TOTAL_ENERGY = jObj.getJSONObject("TOTAL_ENERGY");
			YEAR_ENERGY = jObj.getJSONObject("YEAR_ENERGY");
			DAY_ENERGY = jObj.getJSONObject("DAY_ENERGY");

			values[0] = getValues(PAC, "PAC");
			values[1] = getValues(TOTAL_ENERGY, "TOTAL_ENERGY");
			values[2] = getValues(YEAR_ENERGY, "YEAR_ENERGY");
			values[3] = getValues(DAY_ENERGY, "DAY_ENERGY");
			System.out.println();
			PVEvent pvEventValues = new PVEvent(values);
			firePVEvent(pvEventValues);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private double getValues(JSONObject refObj, String name) {
		JSONObject Values = null;
		Object unit = null;
		Iterator<?> vKey = null;

		try {
			Values = refObj.getJSONObject("Values");
			unit = refObj.getString("Unit");
			vKey = Values.keys();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		//System.out.print(vKey + "\r\n\t" + name + ":");
		double allValues = 0.0;
		while(vKey.hasNext()) {

			double value;
			Object element = vKey.next();
			try {
				value = Values.getInt(element.toString());
				allValues += value;
				//System.out.print("\r\n\t\t" + element + "\t" + value + "\t" + unit);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		//System.out.print("\r\n\t\t" + "ALL" + "\t" + allValues + "\t" + unit);

		double total = Math.round(allValues);
		total = total / 1000;

		System.out.print("\r\n\t\t" + "ALL" + "\t" + total + "\t" + unit);
		//System.out.print("\r\n\t\t" + "ALL" + "\t" + foo/1000 + "\t" + unit);


		return 0.0 + total;
		//System.out.println();
	}
}