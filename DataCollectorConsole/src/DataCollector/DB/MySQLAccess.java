package DataCollector.DB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

import org.apache.log4j.Logger;

public class MySQLAccess {
	private static Logger logger = Logger.getRootLogger();

	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private PreparedStatement preparedStatementSet = null;
	private ResultSet resultSet = null;
	private final String hostName = "localhost";
	private final String dataBase = "logdata";
	private final String dataBaseTable = "logdata.amis";
	//private final String dataTable = "amis";

	public MySQLAccess()
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			logger.error(e.toString());
		}
	}

	public void Set(Object[] data)
	{


		logger.debug("try to write data to: " + dataBaseTable);
		try {
			connect = DriverManager.getConnection("jdbc:mysql://" + hostName + "/" + dataBase + "?"
					+ "user=DataCollector&password=collect");
			logger.debug("connected to MYSQL DB.TABLE: " + dataBaseTable);

			preparedStatementSet = connect.prepareStatement("INSERT INTO  " + dataBaseTable + " VALUES (default, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?)");

			logger.debug(Double.parseDouble((String) data[0]));		//Fehler;						F.F;	99999999;	Zählerereignisse
			logger.debug(Double.parseDouble((String) data[1]));		//Serialnummer;					0.0.0;	999999999;			In Zähler laden
			logger.debug(Double.parseDouble((String) data[2]));		//Energie A+ Tariflos;			1.8.0;	999999.999;	kWh;	1.8.1 bis 1.8.6 summieren
			logger.debug(Double.parseDouble((String) data[3]));		//Energie A- Tariflos;			2.8.0;	999999.999;	kWh;	2.8.1 bis 2.8.6 summieren
			logger.debug(Time.valueOf((String)data[4]));					//aktuelle Uhrzeit;				0.9.1;	23:59:59;			Uhrenbaustein
			logger.debug(Date.valueOf("20" + (String)data[5]));					//aktuelles Datum;				0.9.2;	99-12-31;			Uhrenbaustein
			logger.debug(Double.parseDouble((String) data[6]));		//momentane Wirkleistung P+;	1.7.0;	99.999;		kW;		Messsystem
			logger.debug(Double.parseDouble((String) data[7]));		//momentane Wirkleistung P-;	2.7.0;	99.999;		kW;		Messsystem
			logger.debug(Double.parseDouble((String) data[8]));		//momentane Blindleistung Q+;	3.7.0;	99.999;		kvAr;	Messsystem
			logger.debug(Double.parseDouble((String) data[9]));		//momentane Blindleistung Q-;	4.7.0;	99.999;		kvAr;	Messsystem

			preparedStatementSet.setDouble(1, Double.parseDouble((String) data[0]));		//Fehler;						F.F;	99999999;	Zählerereignisse
			preparedStatementSet.setDouble(2, Double.parseDouble((String) data[1]));		//Serialnummer;					0.0.0;	999999999;			In Zähler laden
			preparedStatementSet.setDouble(3, Double.parseDouble((String) data[2]));		//Energie A+ Tariflos;			1.8.0;	999999.999;	kWh;	1.8.1 bis 1.8.6 summieren
			preparedStatementSet.setDouble(4, Double.parseDouble((String) data[3]));		//Energie A- Tariflos;			2.8.0;	999999.999;	kWh;	2.8.1 bis 2.8.6 summieren
			preparedStatementSet.setTime(5, Time.valueOf((String)data[4]));					//aktuelle Uhrzeit;				0.9.1;	23:59:59;			Uhrenbaustein
			preparedStatementSet.setDate(6, Date.valueOf("20" + (String)data[5]));					//aktuelles Datum;				0.9.2;	99-12-31;			Uhrenbaustein
			preparedStatementSet.setDouble(7, Double.parseDouble((String) data[6]));		//momentane Wirkleistung P+;	1.7.0;	99.999;		kW;		Messsystem
			preparedStatementSet.setDouble(8, Double.parseDouble((String) data[7]));		//momentane Wirkleistung P-;	2.7.0;	99.999;		kW;		Messsystem
			preparedStatementSet.setDouble(9, Double.parseDouble((String) data[8]));		//momentane Blindleistung Q+;	3.7.0;	99.999;		kvAr;	Messsystem
			preparedStatementSet.setDouble(10,Double.parseDouble((String) data[9]));		//momentane Blindleistung Q-;	4.7.0;	99.999;		kvAr;	Messsystem


			preparedStatementSet.executeUpdate();
			logger.debug("executed SQL write command");
		} catch (SQLException e) {
			//logger.error(e.toString());
			e.printStackTrace();
		}
		finally
		{
			close();
		}
	}

	public void Set()
	{
		//this.data = data;
		Object[] data = new Object[10];
		data[0] = "0000000";
		data[1] = "003030396";
		data[2] = "984.138";
		data[3] = "2544.458";
		data[4] = "14:07:29";
		data[5] = "12-11-05";
		data[6] = "0.000";
		data[7] = "0.330";
		data[8] = "0.000";
		data[9] = "0.288";

		logger.debug("try to write data to: " + dataBaseTable);
		try {
			connect = DriverManager.getConnection("jdbc:mysql://" + hostName + "/" + dataBase + "?"
					+ "user=DataCollector&password=collect");
			logger.debug("connected to MYSQL DB.TABLE: " + dataBaseTable);

			preparedStatementSet = connect.prepareStatement("INSERT INTO  " + dataBaseTable + " VALUES (default, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?)");

			logger.debug(Double.parseDouble((String) data[0]));		//Fehler;						F.F;	99999999;	Zählerereignisse
			logger.debug(Double.parseDouble((String) data[1]));		//Serialnummer;					0.0.0;	999999999;			In Zähler laden
			logger.debug(Double.parseDouble((String) data[2]));		//Energie A+ Tariflos;			1.8.0;	999999.999;	kWh;	1.8.1 bis 1.8.6 summieren
			logger.debug(Double.parseDouble((String) data[3]));		//Energie A- Tariflos;			2.8.0;	999999.999;	kWh;	2.8.1 bis 2.8.6 summieren
			logger.debug(Time.valueOf((String)data[4]));					//aktuelle Uhrzeit;				0.9.1;	23:59:59;			Uhrenbaustein
			logger.debug(Date.valueOf("20" + (String)data[5]));					//aktuelles Datum;				0.9.2;	99-12-31;			Uhrenbaustein
			logger.debug(Double.parseDouble((String) data[6]));		//momentane Wirkleistung P+;	1.7.0;	99.999;		kW;		Messsystem
			logger.debug(Double.parseDouble((String) data[7]));		//momentane Wirkleistung P-;	2.7.0;	99.999;		kW;		Messsystem
			logger.debug(Double.parseDouble((String) data[8]));		//momentane Blindleistung Q+;	3.7.0;	99.999;		kvAr;	Messsystem
			logger.debug(Double.parseDouble((String) data[9]));		//momentane Blindleistung Q-;	4.7.0;	99.999;		kvAr;	Messsystem

			preparedStatementSet.setDouble(1, Double.parseDouble((String) data[0]));		//Fehler;						F.F;	99999999;	Zählerereignisse
			preparedStatementSet.setDouble(2, Double.parseDouble((String) data[1]));		//Serialnummer;					0.0.0;	999999999;			In Zähler laden
			preparedStatementSet.setDouble(3, Double.parseDouble((String) data[2]));		//Energie A+ Tariflos;			1.8.0;	999999.999;	kWh;	1.8.1 bis 1.8.6 summieren
			preparedStatementSet.setDouble(4, Double.parseDouble((String) data[3]));		//Energie A- Tariflos;			2.8.0;	999999.999;	kWh;	2.8.1 bis 2.8.6 summieren
			preparedStatementSet.setTime(5, Time.valueOf((String)data[4]));					//aktuelle Uhrzeit;				0.9.1;	23:59:59;			Uhrenbaustein
			preparedStatementSet.setDate(6, Date.valueOf("20" + (String)data[5]));					//aktuelles Datum;				0.9.2;	99-12-31;			Uhrenbaustein
			preparedStatementSet.setDouble(7, Double.parseDouble((String) data[6]));		//momentane Wirkleistung P+;	1.7.0;	99.999;		kW;		Messsystem
			preparedStatementSet.setDouble(8, Double.parseDouble((String) data[7]));		//momentane Wirkleistung P-;	2.7.0;	99.999;		kW;		Messsystem
			preparedStatementSet.setDouble(9, Double.parseDouble((String) data[8]));		//momentane Blindleistung Q+;	3.7.0;	99.999;		kvAr;	Messsystem
			preparedStatementSet.setDouble(10,Double.parseDouble((String) data[9]));		//momentane Blindleistung Q-;	4.7.0;	99.999;		kvAr;	Messsystem


			preparedStatementSet.executeUpdate();
			logger.debug("executed SQL write command");
		} catch (SQLException e) {
			//logger.error(e.toString());
			e.printStackTrace();
		}
		finally
		{
			close();
		}
	}

	public void readDataBase() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + hostName + "/" + dataBase + "?"
					+ "user=DataCollector&password=collect");

			statement = connect.createStatement();	// Statements allow to issue SQL queries to the database
			resultSet = statement.executeQuery("SELECT * FROM " + dataBaseTable);	// Result set get the result of the SQL query
			writeResultSet(resultSet);

			// PreparedStatements can use variables and are more efficient
			preparedStatement = connect.prepareStatement("INSERT INTO  " + dataBaseTable + " VALUES (default, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?)");
			// "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
			// Parameters start with 1
			preparedStatement.setString(1, "Test");
			preparedStatement.setString(2, "TestEmail");
			preparedStatement.setString(3, "TestWebpage");
			//preparedStatement.setDate(4, (java.sql.Date)("2009, 12, 11"));
			preparedStatement.setString(5, "TestSummary");
			preparedStatement.setString(6, "TestComment");
			preparedStatement.executeUpdate();

			preparedStatement = connect.prepareStatement("SELECT myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
			resultSet = preparedStatement.executeQuery();
			writeResultSet(resultSet);

			// Remove again the insert comment
			preparedStatement = connect.prepareStatement("delete from FEEDBACK.COMMENTS where myuser= ? ; ");
			preparedStatement.setString(1, "Test");
			preparedStatement.executeUpdate();

			resultSet = statement.executeQuery("select * from " + dataBaseTable);
			writeMetaData(resultSet);

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

	}

	private void writeMetaData(ResultSet resultSet) throws SQLException {
		//   Now get some metadata from the database
		// Result set get the result of the SQL query

		System.out.println("The columns in the table are: ");

		System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
		for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
			System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
		}
	}

	private void writeResultSet(ResultSet resultSet) throws SQLException {
		// ResultSet is initially before the first data set
		while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g. resultSet.getSTring(2);
			String user = resultSet.getString("myuser");
			String website = resultSet.getString("webpage");
			String summery = resultSet.getString("summery");
			//Date date = resultSet.getDate("datum");
			String comment = resultSet.getString("comments");
			System.out.println("User: " + user);
			System.out.println("Website: " + website);
			System.out.println("Summery: " + summery);
			//System.out.println("Date: " + date);
			System.out.println("Comment: " + comment);
		}
	}

	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

}