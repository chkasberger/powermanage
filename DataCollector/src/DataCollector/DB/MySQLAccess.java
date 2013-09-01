/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
package DataCollector.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DataCollector.JUtil;
import DataCollector.XML.MYSQL;

public class MySQLAccess{
	private static Logger logger = Logger.getRootLogger();

	private Connection dbConnection;
	Context ctx = null;
	DataSource ds = null;

	private Statement statement = null;
	private PreparedStatement preparedStatementSet = null;
	private ResultSet resultSet = null;
	private String hostName;// = "localhost";
	private String dataBase;// = "logdata";
	private String dataBaseTable;// = "logdata.xamis";
	private String dataBaseTable3p;// = "logdata.xamis";
	private String user;// = "logdata.xamis";
	private String password;// = "logdata.xamis";

	public MySQLAccess(MYSQL mysql) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			logger.debug(mysql.getDatabase() 
					+ "\r\n" + mysql.getHostname()
					+ "\r\n" + mysql.getPassword() 
					+ "\r\n" + mysql.getTable()
					+ "\r\n" + mysql.getTable3p()
					+ "\r\n" + mysql.getUser() 
					+ "\r\n");
			hostName = mysql.getHostname();
			dataBase = mysql.getDatabase();// = "logdata";
			dataBaseTable = mysql.getTable();// = "logdata.xamis";
			dataBaseTable3p = mysql.getTable3p();// = "logdata.xamis";
			user = mysql.getUser();// = "logdata.xamis";
			password = mysql.getPassword();// = "logdata.xamis";

			Connect();

		} catch (Exception e) {
			logger.error("MySQLAccess: " + e.getMessage());
		}
	}

/*	protected void finalize() throws Throwable {
		logger.error("Finalize SQL connection");
		// do finalization here
		super.finalize(); // not necessary if extending Object.
		dbConnection.close();
	}
*/
	private boolean Connect() {
		int retryCount = 3;
		do{
			try {
				logger.debug("Try to connect to MYSQL DB.TABLE: " + dataBaseTable);
				dbConnection =  DriverManager.getConnection("jdbc:mysql://"
						+ hostName + "/" + dataBase + "?" + "user=" + user
						+ "&password=" + password);
	
				logger.debug("connected to MYSQL DB.TABLE: " + dataBaseTable);
				return true;
			} catch (SQLException e) {
				logger.error("Connect: "+ e.getSQLState() + " " + e.getMessage());// e.printStackTrace();
				if ("08S01".equals(e.getSQLState())) {
	                retryCount--;
	            } else {
	                retryCount = 0;
	            }
				//return false;
			}
		}while(retryCount > 0);
		
		return false;

	}

	public void Set(double[] data) {
		logger.debug("try to write data to: " + dataBaseTable);
		for (int i = 0; i < data.length; i++) {
			logger.debug(data[i]);
		}
		
		try {
			if(dbConnection == null)
//			if (!dbConnection.isValid(0)){
	//			logger.debug("Set: connection querry");
				Connect();
		//	}
			logger.debug("Set: start prepare");
			preparedStatementSet = prepareStatementSet(data);
			preparedStatementSet.executeUpdate();
			logger.debug("executed SQL write command");

			preparedStatementSet.close();
			logger.debug("closed prepared statement set");

		} catch (Exception e) {
			logger.error("Set: " + e.toString());
			// e.printStackTrace();
		}
	}

	private PreparedStatement prepareStatementSet(double[] data) {
		logger.debug(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")
				.format(new java.util.Date())); 

		String replacementString = "default,?";
		for (int i = 0; i < data.length; i++) {
			replacementString += ",?";
		}
		
		//logger.debug(replacementString);

		PreparedStatement pss = null;
		String tmpDBTable;
		
		try {
			if(data.length > 4)
				tmpDBTable = dataBaseTable3p;									
			else
				tmpDBTable = dataBaseTable;
			
			pss = dbConnection
					.prepareStatement("INSERT INTO  "
							+ tmpDBTable
							+ " VALUES (" + replacementString + ")");
			//logger.debug("INSERT INTO  " + tmpDBTable + " VALUES (" + replacementString + ")");
			
			pss.setObject(1, new java.util.Date()); // aktuelle Uhrzeit; 0.9.1;

			for (int i = 0; i < data.length; i++) {
				pss.setDouble(i + 2, (double) data[i]);				
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		
		return pss;
	}

	public JSONObject Get(JSONObject jObj, boolean overload) throws Exception {

		JSONObject jObject = null;
		// JSONArray jArray = null;

		try {
			if (!dbConnection.isValid(0))
				Connect();

			statement = dbConnection.createStatement();

			if (overload == false) {
				resultSet = statement.executeQuery("SELECT * FROM "
						+ dataBaseTable + " ORDER BY id DESC LIMIT 1;");

			} else {

				String[] selectedInterval = getInterval(jObj.getInt("interval"), true);

				resultSet = statement.executeQuery(
						"SELECT * FROM "
						+ dataBaseTable + " WHERE TIMESTAMP BETWEEN '"
						+ jObj.get("startDate") + " " + jObj.get("startTime")
						+ "' AND '" 
						+ jObj.get("endDate") + " " + jObj.get("endTime")
						+ "' and (Time(TIMESTAMP) like '%:%"
						+ selectedInterval[0] + ":%'or Time(TIMESTAMP) like '%:%"
						+ selectedInterval[1] + ":%'or Time(TIMESTAMP) like '%:%"
						+ selectedInterval[2] + ":%'or Time(TIMESTAMP) like '%:%"
						+ selectedInterval[3] + ":%');"
						);
			}

			logger.debug("executed DB query");
				
			jObject = createJSONObject(resultSet);			
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			//logger.error(e.getStackTrace());
		} finally {
			statement.close();
			//statement = null;
			resultSet.close();
			//resultSet = null;
		}
		return jObject;
		// return jArray;
	}
	

	private JSONObject createJSONObject(ResultSet resultSet)
			throws JSONException, SQLException {
		JSONObject jObject = new JSONObject();
		JSONArray jArray = new JSONArray();

		logger.debug(JUtil.getTimeStamp());

		jObject.put("timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.format(new java.util.Date()));

		// jObject

		Object[] column = new Object[resultSet.getMetaData().getColumnCount()];
		for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
			column[i] = resultSet.getMetaData().getColumnName(i + 1);
		}
		jObject.put("units", new JSONArray().put(column));
		column = null;

		logger.debug(jObject);

		resultSet.last();
		logger.debug("RowCount: " + resultSet.getRow());
		resultSet.beforeFirst();

		logger.debug("ColumnCount: " + resultSet.getMetaData().getColumnCount());

		int count = 0;
		ArrayList<ResultSet> al = new ArrayList<ResultSet>();
		while (resultSet.next()) {
			try {
				logger.debug("Ina di resultSet loop: " + count);

				column = new Object[resultSet.getMetaData().getColumnCount()];

				al.add(resultSet);
				if (resultSet.getRow() > 0) {
					for (int i = 0; i < column.length; i++) {
						column[i] = resultSet.getObject(i + 1);
					}

					jArray.put(column);
					column = null;
				}

				count++;
			}

			catch (Exception e) {
				logger.error(e.getMessage());		
			}
		}
		jObject.put("values", jArray);

		al = null;
		
		return jObject;
	}

	private String[] getInterval(int interval, boolean overload) {
		
		String[] intervalMask;// = new String[] { "00", "00", "00", "00" };

		switch (interval) {
		case 60:
			intervalMask = new String[] { "00", "00", "00", "00" };
			break;
		case 30:
			intervalMask = new String[] { "00", "00", "30", "00" };
			break;
		case 15:
			intervalMask = new String[] { "00", "15", "30", "45" };
			break;
		case 10:
			intervalMask = new String[] { "0", "00", "00", "00" };
			break;
		case 5:
			intervalMask = new String[] { "0", "5", "00", "00" };
			break;
		case 1:
			intervalMask = new String[] { "", "00", "00", "00" };
			break;

		default:
			intervalMask = new String[] { "00", "15", "30", "45" };
			break;
		}
		
		return intervalMask;
	}

}