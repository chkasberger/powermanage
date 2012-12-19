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

	private Connection dbConnection = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private PreparedStatement preparedStatementSet = null;
	private ResultSet resultSet = null;
	private final String hostName = "localhost";
	private final String dataBase = "logdata";
	private final String dataBaseTable = "logdata.xamis";
	//private final String dataTable = "amis";

	public MySQLAccess()
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
	}

	public void Set(Object[] data)
	{
		logger.debug("try to write data to: " + dataBaseTable);
		try {
			dbConnection = DriverManager.getConnection("jdbc:mysql://" + hostName + "/" + dataBase + "?"
					+ "user=DataCollector&password=collect");
			logger.debug("connected to MYSQL DB.TABLE: " + dataBaseTable);

			preparedStatementSet = prepareStatementSet(data);
			preparedStatementSet.executeUpdate();
			logger.debug("executed SQL write command");

			preparedStatementSet.close();
			logger.debug("closed prepared statement set");

		} catch (SQLException e) {
			logger.error(e.toString());
			//e.printStackTrace();
		}
		finally
		{
			close();

		}
	}

	private PreparedStatement prepareStatementSet(Object[] data) {
		logger.debug("DB values: "
				+ (Time.valueOf((String)data[0]) 			+ "\t\tcurrent time D0 interface\r\n")	//aktuelle Uhrzeit;				0.9.1;	23:59:59;			Uhrenbaustein
				+ (Date.valueOf("20" + (String)data[1])	+ "\t\tcurrent date D0 interface\r\n")	//aktuelles Datum;				0.9.2;	99-12-31;			Uhrenbaustein
				+ (data[2] 	+ "\t\terror counter D0 interface\r\n")	//Fehler;						F.F;	99999999;	Zählerereignisse
				+ (data[3] 	+ "\t\tserial number D0 interface\r\n")	//Serialnummer;					0.0.0;	999999999;			In Zähler laden
				+ (data[4] 	+ "\t\tconsumed energy D0 interface\r\n")	//Energie A+ Tariflos;			1.8.0;	999999.999;	kWh;	1.8.1 bis 1.8.6 summieren
				+ (data[5] 	+ "\t\tsupplied energy D0 interface\r\n")	//Energie A- Tariflos;			2.8.0;	999999.999;	kWh;	2.8.1 bis 2.8.6 summieren
				+ (data[6]	+ "\t\tcurrent consumed effective power D0 interface\r\n")	//momentane Wirkleistung P+;	1.7.0;	99.999;		kW;		Messsystem
				+ (data[7]	+ "\t\tcurrent supplied effective power D0 interface\r\n")	//momentane Wirkleistung P-;	2.7.0;	99.999;		kW;		Messsystem
				+ (data[8]	+ "\t\tcurrent consumed reactive power D0 interface\r\n")	//momentane Blindleistung Q+;	3.7.0;	99.999;		kvAr;	Messsystem
				+ (data[9]	+ "\t\tcurrent supplied reactive power D0 interface\r\n")	//momentane Blindleistung Q-;	4.7.0;	99.999;		kvAr;	Messsystem
				+ (data[10]	+ "\t\tcurrent produced power JSON interface\r\n")	//momentane AC Power der PV-Anlage
				+ (data[11]	+ "\t\tcurrent produced total energy JSON interface\r\n")	//produzierte Gesamtenergie der PV-Anlage
				+ (data[12]	+ "\t\tcurrent produced year energy JSON interface\r\n")	//produzierte Jahresenergie der PV-Anlage
				+ (data[13]	+ "\t\tcurrent produced day energy JSON interface\r\n")	//produzierte Tagesenergie der PV-Anlage
				+ (data[14]	+ "\t\tcurrent consumed power S0 interface\r\n")		//produzierte Tagesenergie der PV-Anlage
				+ (data[15]	+ "\t\tcurrent consumed energy S0 interface"));		//produzierte Tagesenergie der PV-Anlage

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

	public void readDataBase() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			dbConnection = DriverManager.getConnection("jdbc:mysql://" + hostName + "/" + dataBase + "?"
					+ "user=DataCollector&password=collect");

			statement = dbConnection.createStatement();	// Statements allow to issue SQL queries to the database
			resultSet = statement.executeQuery("SELECT * FROM " + dataBaseTable);	// Result set get the result of the SQL query
			writeResultSet(resultSet);

			// PreparedStatements can use variables and are more efficient
			preparedStatement = dbConnection.prepareStatement("INSERT INTO  " + dataBaseTable + " VALUES (default, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?)");
			// "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
			// Parameters start with 1
			preparedStatement.setString(1, "Test");
			preparedStatement.setString(2, "TestEmail");
			preparedStatement.setString(3, "TestWebpage");
			//preparedStatement.setDate(4, (java.sql.Date)("2009, 12, 11"));
			preparedStatement.setString(5, "TestSummary");
			preparedStatement.setString(6, "TestComment");
			preparedStatement.executeUpdate();

			preparedStatement = dbConnection.prepareStatement("SELECT myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
			resultSet = preparedStatement.executeQuery();
			writeResultSet(resultSet);

			// Remove again the insert comment
			preparedStatement = dbConnection.prepareStatement("delete from FEEDBACK.COMMENTS where myuser= ? ; ");
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