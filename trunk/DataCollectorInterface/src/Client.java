import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;

//import org.apache.commons.dbutils.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import DataCollector.JUtil;
import DataCollector.DB.MySQLAccess;
import DataCollector.XML.*;

public class Client {

	static Logger logger = Logger.getRootLogger();
	//static Socket socket;
	static BufferedReader in;
	static PrintWriter out;	
	static XmlConfigParser config = new XmlConfigParser();
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args){
		
		JUtil.setupLogger();
		logger.setLevel(Level.DEBUG);
		logger.debug("app startet");
		
		
		config(args);
		
		//java.util.Date now = new java.util.Date();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //System.out.println(format.format(now));

		MySQLAccess mySQLAccess = new MySQLAccess(config.getMysql());
		
		int count = 0;		
		while(new com.fastcgi.FCGIInterface().FCGIaccept()>= 0) {
			count ++;
			logger.debug("ina di loop" + count);
		   
			//String input = System.getProperty("FCGI_STDIN");
			//System.out.println("Content-type: text/html\n\n");
			System.out.println("Content-type: application/json\r\n");
			System.out.println("<html>");
			System.out.println("<head><TITLE>FastCGI-Hello Java stdio</TITLE></head>");
			System.out.println("<body>");
			System.out.println("<H3>FastCGI-JSON</H3>");
		   	//System.out.println("request number " + count + " running on host " + System.getProperty("SERVER_NAME"));
			
			Object[] input = null; 
			JSONObject jObject = null;
			
			try {
				//System.out.println(input);
				//System.getProperties().list(System.out);
				String queryString = System.getProperty("QUERY_STRING");
				input = queryString.split("[ _T]");
				//foo = mySQLAccess.Get(new Object[]{"2013:02:10", "12:00:00", "2013:02:10", "13:00:59", "15"}).toString();
				jObject = mySQLAccess.Get(input);
				//logger.debug("Array: ");
		   		//jArray = mySQLAccess.GetJSONArray(input);
				
				//System.out.println(jObject);
		   		//logger.debug("Array: " + jArray);
		   		//System.out.println("Array: " + jArray);
				//System.out.flush();
		   		System.out.println(jObject);
				System.out.flush();
				logger.debug(input[0] + " " + input[1] + " " + input[2] + " " + input[3]);
				logger.debug(jObject);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
					
				//e1.printStackTrace();
			}
		   	System.out.println("</body>");
		   	System.out.println("</html>");		 
	   }        	    		
	}
	
	private static void config(String[] args) {
		if(args.length > 0){
			config.getConfig(args[0]);	
		}
	}
}