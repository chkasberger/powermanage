package DataCollector;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import DataCollector.DB.MySQLAccess;

public class Task extends TimerTask {

	final static Logger logger = Logger.getRootLogger();
	static MySQLAccess DB;
	Object[] DBData;

	/**
	 * Constructs the object, sets the string to be output in function run()
	 * @param str
	 */
	Task() {
		DB = new MySQLAccess();
	}

	public Task(Object[] dBData) {
		// TODO Auto-generated constructor stub
		DBData = dBData;
	}

	/**
	 * When the timer executes, this code is run.
	 */
	@Override
	public void run() {
		logger.debug("try to send data to MySQLAccess CLASS");
		DB.Set(DBData);
	}
}