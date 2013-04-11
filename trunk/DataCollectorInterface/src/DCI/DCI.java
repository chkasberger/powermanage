package DCI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import DataCollector.JUtil;
import DataCollector.DB.MySQLAccess;
import DataCollector.XML.XmlConfigParser;

public class DCI {

	static Logger logger = Logger.getRootLogger();
	// static Socket socket;
	static BufferedReader in;
	static PrintWriter out;
	static XmlConfigParser config = new XmlConfigParser();

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) {

		JUtil.setupLogger(args[1]);

		logger.debug("app startet");

		config.getConfig(args[0]);

		MySQLAccess mySQLAccess = new MySQLAccess(config.getMysql());

		while (new com.fastcgi.FCGIInterface().FCGIaccept() >= 0) {

			System.out.println("Content-type: application/json\r\n");
			// System.out.println("Content-type: text/plain\r\n");

			/*
			 * System.out.println( "<!DOCTYPE html>" + "<html lang=\"en\">" +
			 * "<head><TITLE>db query</TITLE></head>" + "<body>" );
			 */
			Object[] input = null;
			JSONObject jObject = null;

			try {

				String queryString = System.getProperty("QUERY_STRING");
				input = queryString.split("[ _T]");

				jObject = mySQLAccess.Get(input);

				System.out.println(jObject);
				System.out.flush();
				//logger.debug(input[0] + " " + input[1] + " " + input[2] + " "
				//		+ input[3]);
				logger.debug(jObject);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				//logger.error(e.getStackTrace());
				// e1.printStackTrace();
			}
			/*
			 * System.out.println( "</body>" + "</html>");
			 */
		}
	}
}