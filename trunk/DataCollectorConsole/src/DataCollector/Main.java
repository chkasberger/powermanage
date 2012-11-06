package DataCollector;

import gnu.io.PortInUseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import DataCollector.DB.MySQLAccess;
import DataCollector.IO.ComPort;
import DataCollector.IO.ComPortEvent;
import DataCollector.IO.ComPortEventListener;

public class Main {
	final static Logger logger = Logger.getRootLogger();

	enum Hardware {AMIS, FROELING}
	static MySQLAccess DB;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Level logLevel = Level.DEBUG;
		// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		try {
			SimpleLayout layout = new SimpleLayout();
			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			logger.setLevel(logLevel);
			logger.addAppender(consoleAppender);
		} catch (Exception ex) {
			logger.debug(ex + "@" + JUtil.getMethodName(1));
		}

		DB = new MySQLAccess();
		//DB.Set();

		ComPort Port = new ComPort();
		String[] singleOption;
		ArrayList<String> portList = ComPort.portList();
		String[][] portListArray = new String[portList.size()][2];

		System.out.print("available serial ports:\r\n");
		for (int i = 0; i < portList.size(); i++) {
			portListArray[i][0] = String.valueOf(i);
			portListArray[i][1] = portList.get(i);

			System.out.println("\t" + portListArray[i][0] + "=" + portListArray[i][1]);
		}

		if(portList.size() > 0)
		{
			if(args != null)
			{
				singleOption = args;
				logger.debug("command line arguments found");
			}
			else
			{
				System.out.print("usage:\t[port] + [options]\r\n" +
						"options:\r\n" +
						"\t-b [baud{300(default)|600|1200|2400|4800|9600|19200|...}]\r\n" +
						"\t-p [parity{0=none(default),1=odd,2=even}]\r\n" +
						"\t-d [data{7(default)|8}]\r\n" +
						"\t-s [stop{1(default)}|2|3(=1.5)]\r\n" +
						"\t-t [type{0=AMIS(default)|1=Froeling}]\r\n" +
						"\t-c [cycle{time in seconds}]\r\n" +
						"example:\r\t0 -b 19200 -p 0 -s 2 -t 0 -c 60");

				Scanner in = new Scanner(System.in);
				String options = in.nextLine();
				singleOption = options.split(" -");
			}

			//String[] singleOption = new String[]{"0"};
			System.out.print("enter port + options:\r\n\t");

			Object portName = 0;
			Object baudRate = 300;
			Object parityBit = ComPort.Parity.EVEN;
			Object dataBits = ComPort.DataBits.SEVEN;
			Object stopBits = ComPort.StopBits.ONE;
			Object hardwareType = Hardware.AMIS;
			Object interval = 60;

			boolean valid = true;

			for (String string : singleOption) {
				logger.debug(string);

				if (string.matches("[0-9]{1,3}")) // port index
				{
					portName = portListArray[Integer.parseInt(string)][1];
					logger.debug("selected port: " + portName);
				}
				else if(string.matches("[0-9]?[a-zA-Z]{1}[ ]{1}[0-9]+"))
				{
					if(string.startsWith("b")) // port index
					{
						int baud = Integer.parseInt(string.substring(2));
						if(baud % 300 == 0)
						{
							baudRate = baud;
							logger.debug("baudrate is set to " + baudRate.toString());
						}
						else
							logger.debug("baudrate is not multiple of 300!");
					}

					if(string.startsWith("p")) //parity
					{
						switch(Integer.parseInt(string.substring(2))){
						case 0: parityBit = ComPort.Parity.NONE; break;
						case 1: parityBit = ComPort.Parity.ODD; break;
						case 2: parityBit = ComPort.Parity.EVEN; break;
						}

						logger.debug("parity is set to " + parityBit.toString());
						logger.debug("parity is set to " + parityBit.toString());
					}
					if(string.startsWith("d")) // data bits
					{
						switch(Integer.parseInt(string.substring(2))){
						case 7: dataBits = ComPort.DataBits.SEVEN; break;
						case 8: dataBits = ComPort.DataBits.EIGHT; break;
						}
						logger.debug("databit is set to " + dataBits.toString());
					}
					if(string.startsWith("s")) // stop bits
					{
						switch(Integer.parseInt(string.substring(2))){
						case 1: stopBits = ComPort.StopBits.ONE; break;
						case 3: stopBits = ComPort.StopBits.ONEPOINTFIVE; break;
						case 2: stopBits = ComPort.StopBits.TWO; break;
						}
						logger.debug("stopbit is set to " + stopBits.toString());
					}
					if(string.startsWith("t")) // hardware type
					{
						switch(Integer.parseInt(string.substring(2))){
						case 0: hardwareType = Hardware.AMIS; break;
						case 1: hardwareType = Hardware.FROELING; break;
						}
						logger.debug("hardware is set to " + hardwareType.toString());
					}
					if(string.startsWith("c")) // interval
					{
						try{
							interval = Integer.parseInt(string.substring(2));
							logger.debug("interval is set to " + interval.toString());
						}
						catch(NumberFormatException nfe){
							logger.info("option must be an integer value; used default value: " + interval.toString());
						}
					}
				}
				else
				{
					logger.info("Wrong or rather unknown option: " + string);
					valid = false;
				}
			}

			if(valid)
			{
				try {
					//connect to serial port
					Port.connect(portName, baudRate, parityBit, dataBits, stopBits);

					//create timer schedule
					Timer timer = new Timer();
					long delay = (Integer)interval*1000;
					timer.schedule(new Task(Port, portName, baudRate, parityBit, dataBits, stopBits), 0, delay);
				} catch (PortInUseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				final Object[] DBData = new Object[]{"0","0","0","0","00:00:00","0000-00-00","0","0","0","0"};
				Port.addComPortEventListener(new ComPortEventListener() {

					@Override
					public void comPortEventFired(ComPortEvent evt) {
						// TODO Auto-generated method stub
						File file =new File("AMISLOG.csv");

						//if file doesnt exists, then create it
						if(!file.exists()){
							try {
								file.createNewFile();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						//true = append file
						FileWriter fileWritter = null;
						try {
							fileWritter = new FileWriter(file.getName(),true);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

						String str = evt.getSource().toString();
						String data = "";
						String rawData = "";
						String sepperator = ";";
						boolean writeToFile = true;
						boolean writeToDB = false;

						if(
								str.contains("(")|
								str.contains(")")|
								str.contains("!\r\n"))
						{
							if(!str.contains("!\r\n"))
								rawData = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
							else
								rawData = str;

							if(rawData.contains("*"))
								data = rawData.substring(0, rawData.indexOf("*"));
							else
								data = rawData.trim();

							if(str.contains("F.F("))
							{
								DBData[0] = data;
							}
							else if(str.contains("0.0.0("))
							{
								DBData[1] = data;
							}
							else if(str.contains("1.8.0("))
							{
								DBData[2] = data;
							}
							else if(str.contains("2.8.0("))
							{
								DBData[3] = data;
							}
							else if(str.contains("0.9.1("))
							{
								DBData[4] = data;
							}
							else if(str.contains("0.9.2("))
							{
								DBData[5] = data;
							}
							else if(str.contains("1.7.0("))
							{
								DBData[6] = data;
							}
							else if(str.contains("2.7.0("))
							{
								DBData[7] = data;
							}
							else if(str.contains("3.7.0("))
							{
								DBData[8] = data;
							}
							else if(str.contains("4.7.0("))
							{
								DBData[9] = data;
							}
							else if(str.startsWith("!\r\n"))
							{
								data = "";
								sepperator = "\r\n";
								System.out.print("\r\nfinished cycle\r\n");
								writeToDB = true;
							}
							else
							{
								writeToFile = false;
							}

							if(writeToFile)
							{
								logger.debug(data);
								try {
									bufferWritter.write(data + sepperator);
									bufferWritter.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							if(writeToDB)
							{
								logger.debug("try to send data to MySQLAccess CLASS");
								DB.Set(DBData);
								/*for (Object object : DBData) {
									logger.debug("-->" + (String)object);
								}
								 */
							}
						}

					}
				});
			}
			else{
				System.out.println("No valid input found!");
			}
		}
		else
		{
			System.out.println("No valid serial port found!");
		}
	}
}
