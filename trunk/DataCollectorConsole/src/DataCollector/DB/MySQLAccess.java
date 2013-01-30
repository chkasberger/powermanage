package DataCollector.DB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import DataCollector.XML.MYSQL;

public class MySQLAccess {
	private static Logger logger = Logger.getRootLogger();

	private Connection dbConnection = null;
	private Statement statement = null;
	//private final PreparedStatement preparedStatement = null;
	private PreparedStatement preparedStatementSet = null;
	private ResultSet resultSet = null;
	private String hostName;// = "localhost";
	private String dataBase;// = "logdata";
	private String dataBaseTable;// = "logdata.xamis";
	private String user;// = "logdata.xamis";
	private String password;// = "logdata.xamis";
	//private final String dataTable = "amis";

	public MySQLAccess(MYSQL mysql)
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");

			hostName = mysql.getHostname();
			dataBase = mysql.getDatabase();// = "logdata";
			dataBaseTable = mysql.getTable();// = "logdata.xamis";
			user = mysql.getUser();// = "logdata.xamis";
			password = mysql.getPassword();// = "logdata.xamis";

		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
	}

	public void Set(Object[] data)
	{
		logger.debug("try to write data to: " + dataBaseTable);
		try {
			dbConnection = DriverManager.getConnection("jdbc:mysql://" + hostName + "/" + dataBase + "?"
					+ "user=" + user + "&password=" + password);
			logger.debug("connected to MYSQL DB.TABLE: " + dataBaseTable);

			preparedStatementSet = prepareStatementSet(data);
			preparedStatementSet.executeUpdate();
			logger.debug("executed SQL write command");

			preparedStatementSet.close();
			logger.debug("closed prepared statement set");

		} catch (Exception e) {
			logger.error(e.toString());
			//e.printStackTrace();
		}
		finally
		{
			close();
		}
	}

	private PreparedStatement prepareStatementSet(Object[] data) {
		logger.debug("DB values: ");
		logger.debug(Time.valueOf((String)data[0]) 			+ "\t\tcurrent time D0 interface");	//aktuelle Uhrzeit;				0.9.1;	23:59:59;			Uhrenbaustein
		logger.debug(Date.valueOf("20" + (String)data[1])	+ "\t\tcurrent date D0 interface");	//aktuelles Datum;				0.9.2;	99-12-31;			Uhrenbaustein
		logger.debug(data[2] 	+ "\t\terror counter D0 interface");	//Fehler;						F.F;	99999999;	Zählerereignisse
		logger.debug (data[3] 	+ "\t\tserial number D0 interface");	//Serialnummer;					0.0.0;	999999999;			In Zähler laden
		logger.debug(data[4] 	+ "\t\tconsumed energy D0 interface");	//Energie A+ Tariflos;			1.8.0;	999999.999;	kWh;	1.8.1 bis 1.8.6 summieren
		logger.debug(data[5] 	+ "\t\tsupplied energy D0 interface");	//Energie A- Tariflos;			2.8.0;	999999.999;	kWh;	2.8.1 bis 2.8.6 summieren
		logger.debug(data[6]	+ "\t\tcurrent consumed effective power D0 interface");	//momentane Wirkleistung P+;	1.7.0;	99.999;		kW;		Messsystem
		logger.debug(data[7]	+ "\t\tcurrent supplied effective power D0 interface");	//momentane Wirkleistung P-;	2.7.0;	99.999;		kW;		Messsystem
		logger.debug(data[8]	+ "\t\tcurrent consumed reactive power D0 interface");	//momentane Blindleistung Q+;	3.7.0;	99.999;		kvAr;	Messsystem
		logger.debug(data[9]	+ "\t\tcurrent supplied reactive power D0 interface");	//momentane Blindleistung Q-;	4.7.0;	99.999;		kvAr;	Messsystem
		logger.debug(data[10]	+ "\t\tcurrent produced power JSON interface");	//momentane AC Power der PV-Anlage
		logger.debug(data[11]	+ "\t\tcurrent produced total energy JSON interface");	//produzierte Gesamtenergie der PV-Anlage
		logger.debug(data[12]	+ "\t\tcurrent produced year energy JSON interface");	//produzierte Jahresenergie der PV-Anlage
		logger.debug(data[13]	+ "\t\tcurrent produced day energy JSON interface");	//produzierte Tagesenergie der PV-Anlage
		logger.debug(data[14]	+ "\t\tcurrent consumed power S0 interface");		//produzierte Tagesenergie der PV-Anlage
		logger.debug(data[15]	+ "\t\tcurrent consumed energy S0 interface");		//produzierte Tagesenergie der PV-Anlage

		PreparedStatement pss = null;
		try {
			pss = dbConnection.prepareStatement("INSERT INTO  " + dataBaseTable + " VALUES (default, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pss.setTime(1, Time.valueOf((String)data[0]));					//aktuelle Uhrzeit;				0.9.1;	23:59:59;			Uhrenbaustein
			pss.setDate(2, Date.valueOf("20" + (String)data[1]));					//aktuelles Datum;				0.9.2;	99-12-31;			Uhrenbaustein
			pss.setDouble(3, (double) data[2]);		//Fehler;						F.F;	99999999;	Zählerereignisse
			pss.setDouble(4, (double) data[3]);		//Serialnummer;					0.0.0;	999999999;			In Zähler laden
			pss.setDouble(5, (double) data[4]);		//Energie A+ Tariflos;			1.8.0;	999999.999;	kWh;	1.8.1 bis 1.8.6 summieren
			pss.setDouble(6, (double) data[5]);		//Energie A- Tariflos;			2.8.0;	999999.999;	kWh;	2.8.1 bis 2.8.6 summieren
			pss.setDouble(7, (double) data[6]);		//momentane Wirkleistung P+;	1.7.0;	99.999;		kW;		Messsystem
			pss.setDouble(8, (double) data[7]);		//momentane Wirkleistung P-;	2.7.0;	99.999;		kW;		Messsystem
			pss.setDouble(9, (double) data[8]);		//momentane Blindleistung Q+;	3.7.0;	99.999;		kvAr;	Messsystem
			pss.setDouble(10,(double) data[9]);		//momentane Blindleistung Q-;	4.7.0;	99.999;		kvAr;	Messsystem
			pss.setDouble(11,(double) data[10]);		//momentane AC Power der PV-Anlage
			pss.setDouble(12,(double) data[11]);		//produzierte Gesamtenergie der PV-Anlage
			pss.setDouble(13,(double) data[12]);		//produzierte Jahresenergie der PV-Anlage
			pss.setDouble(14,(double) data[13]);		//produzierte Tagesenergie der PV-Anlage
			pss.setDouble(15,(double) data[14]);		//produzierte Tagesenergie der PV-Anlage
			pss.setDouble(16,(double) data[15]);		//produzierte Tagesenergie der PV-Anlage
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return pss;
	}

	public JSONObject readDataBase(Object[] input) throws Exception {
		JSONObject jObject = null;
		String[] selectedInterval = new String[5];

		{
			if(input.length > 0 && input[0].toString().matches("[0-9]{4}[/:-][0-9]{2}[/:-][0-9]{2}"))
				selectedInterval[0] = input[0].toString();
			else
				selectedInterval[0] = "2013-01-11";

			if(input.length > 1 && input[1].toString().matches("[0-9]{2}[/:-][0-9]{2}[/:-][0-9]{2}"))
				selectedInterval[1] = input[1].toString();
			else
				selectedInterval[1] = "12:00:00";

			if(input.length > 2 && input[2].toString().matches("[0-9]{4}[/:-][0-9]{2}[/:-][0-9]{2}"))
				selectedInterval[2] = input[2].toString();
			else
				selectedInterval[2] = "2013-01-11";

			if(input.length > 3 && input[3].toString().matches("[0-9]{2}[/:-][0-9]{2}[/:-][0-9]{2}"))
				selectedInterval[3] = input[3].toString();
			else
				selectedInterval[3] = "13:00:59";

			if(input.length > 4 && input[4].toString().matches("[0-9]{1,2}"))
				selectedInterval[4] = input[4].toString();
			else
				selectedInterval[4] = "1";
		}

		String[] intervalMask = new String[]{"00", "00", "00", "00"};

		switch (selectedInterval[4]) {
		case "60":
			intervalMask = new String[]{"00", "00", "00", "00"};
			break;
		case "30":
			intervalMask = new String[]{"00", "00", "30", "00"};
			break;
		case "15":
			intervalMask = new String[]{"00", "15", "30", "45"};
			break;
		case "10":
			intervalMask = new String[]{"0", "00", "00", "00"};
			break;
		case "5":
			intervalMask = new String[]{"0", "5", "00", "00"};
			break;
		case "1":
			intervalMask = new String[]{"", "00", "00", "00"};
			break;

		default:
			intervalMask = new String[]{"00", "15", "30", "45"};
			break;
		}

		try {

			Class.forName("com.mysql.jdbc.Driver");
			dbConnection = DriverManager.getConnection("jdbc:mysql://"
					+ hostName + "/" + dataBase + "?" + "user=" + user
					+ "&password=" + password);

			statement = dbConnection.createStatement();
			resultSet = statement.executeQuery("select * from sg where (EM_0_9_2>='" + selectedInterval[0] + "' and EM_0_9_1>='" + selectedInterval[1] + "') and (EM_0_9_2<='" + selectedInterval[2] + "' and EM_0_9_1<='" + selectedInterval[3] + "') and (EM_0_9_1 like '%:%" + intervalMask[0] + ":%' or EM_0_9_1 like '%:%" + intervalMask[1] + ":%'or EM_0_9_1 like '%:%" + intervalMask[2] + ":%'or EM_0_9_1 like '%:%" + intervalMask[3] + ":%');");
			jObject = createJSONObject(resultSet);

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

		return jObject;
	}

	private JSONObject createJSONObject(ResultSet resultSet) throws JSONException, SQLException{
		JSONObject jObject = new JSONObject();

		java.util.Date dateTime = new java.util.Date();
		SimpleDateFormat dateTimeFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		Object[] columnNames = getColumnNames(resultSet);
		Object[][] columnValues = getColumnValues(resultSet, columnNames);

		Object[][] dbMirror = new Object[columnValues.length + 1][columnNames.length];

		System.arraycopy(columnNames, 0, dbMirror[0], 0, dbMirror[0].length);

		for(int i=0; i<columnValues.length; i++)
			for(int j=0; j<columnValues[i].length; j++)
				dbMirror[i + 1][j]=columnValues[i][j];

		jObject.put("timestamp", dateTimeFormater.format(dateTime));
		Object[] tmp = new Object[dbMirror[0].length - 5];
		System.arraycopy(dbMirror[0], 5, tmp, 0, tmp.length);
		jObject.put("units", tmp);

		for (int i = 0; i < dbMirror.length; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < dbMirror[i].length; j++) {
				int length = dbMirror[i][j].toString().length();
				if(dbMirror[i][j].toString().length() >= 13)
					sb.append(dbMirror[i][j].toString().substring(0, 11) + " ");
				else
					sb.append(dbMirror[i][j].toString());

				while(length < 12)
				{
					sb.append(" ");
					length++;
				}
				Object[] tmpArray = new Object[dbMirror[i].length - 5];
				System.arraycopy(dbMirror[i], 5, tmpArray, 0, tmpArray.length);
				jObject.put(dbMirror[i][2].toString() + "T" + dbMirror[i][1].toString(), tmpArray);
			}
			logger.debug(sb.toString());
		}

		jObject.length();
		return jObject;
	}

	private Object[] getColumnNames(ResultSet resultSet) throws SQLException {
		Object[] columns = new Object[resultSet.getMetaData().getColumnCount()];

		for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
			columns[i] = resultSet.getMetaData().getColumnName(i + 1);
		}
		return columns;
	}

	private Object[][] getColumnValues(ResultSet resultSet, Object[] columnNames) throws SQLException {

		Object[][] columnValues = null;

		if (resultSet.last()) {
			columnValues = new Object[resultSet.getRow()][columnNames.length];
			resultSet.beforeFirst();
		}

		while (resultSet.next()) {

			if(resultSet.getRow() > 0)
			{

				for (int i = 0; i < columnNames.length; i++) {
					columnValues[resultSet.getRow() - 1][i] = resultSet.getObject((String)columnNames[i]);
				}
			}
		}
		return columnValues;
	}

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
				logger.debug("closed result set");
			}

			if (statement != null) {
				statement.close();
				logger.debug("closed statement");
			}

			if (preparedStatementSet != null) {
				preparedStatementSet.close();
				logger.debug("closed prepared statement set");
			}

			if (dbConnection != null) {
				dbConnection.close();
				logger.debug("closed SQL connection");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}