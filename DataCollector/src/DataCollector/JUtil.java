/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
package DataCollector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * @author User
 *
 */
public class JUtil {
	
	//Logger logger = Logger.getRootLogger();
	
	public static String getMethodName(int stack) {
		String functionName;
		functionName = (new Exception().getStackTrace()[stack].getClassName()
				+ "." + new Exception().getStackTrace()[stack].getMethodName());
		return functionName;
	}

	public static void setupLogger(String _logLevel) {
		Logger logger = Logger.getRootLogger();	
		Level logLevel;
		if(_logLevel != null)
		{
			switch (_logLevel.toUpperCase()) {
			case "ALL":
				logLevel = Level.ALL;
				break;
			case "DEBUG":
				logLevel = Level.DEBUG;
				break;
			case "INFO":
				logLevel = Level.INFO;
				break;
			case "WARN":
				logLevel = Level.WARN;
				break;
			case "ERROR":
				logLevel = Level.ERROR;
				break;
			case "FATAL":
				logLevel = Level.FATAL;
				break;
			case "OFF":
				logLevel = Level.OFF;
				break;
			default:
				logLevel = Level.ERROR;			
				break;
			}
		}
		else{
			logLevel = Level.OFF;						
		}
		//logLevel = Level.INFO;
		//logLevel = Level.DEBUG;
		// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		try {
			SimpleLayout layout = new SimpleLayout();
			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			logger.setLevel(logLevel);
			logger.addAppender(consoleAppender);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static String getTimeStamp(){
		SimpleDateFormat dateTimeFormater = new SimpleDateFormat("ss:SSS");
		return (dateTimeFormater.format(new java.util.Date().getTime()));		
	}
	public class BufferedFileWriter
	{
		private final File file;
		private FileWriter fileWritter;
		private final BufferedWriter bufferWritter;

		public BufferedFileWriter(String fileName) throws IOException
		{
			file = new File(fileName);

			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				fileWritter = new FileWriter(file.getName(),true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			bufferWritter = new BufferedWriter(fileWritter);
		}

		public void write(String value) throws IOException
		{
			bufferWritter.write(value);
		}

		public void writeln(String value) throws IOException
		{
			bufferWritter.write(value + "\r\n");
		}
	}
}
//http://www.mayor.de/lian98/doc.de/html/g_iec62056_struct.htm
