import java.io.*;
//import java.lang.reflect.Array;
import java.util.*;

//import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
//import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import gnu.io.*;

public class ComPort {
	static Enumeration<?>		portList;
	static CommPortIdentifier	portId;
	static String				messageString = "Hello, world!";
	static SerialPort			serialPort;
	static OutputStream			outputStream;
	static boolean				outputBufferEmptyFlag = false;
    /**
     * Method declaration
     *
     *
     * @param string
     *
     * @see
     */
	public static ArrayList<String> listPorts(){
		
        ArrayList<String> portList = new ArrayList<String>();
        
        @SuppressWarnings("unchecked")
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        
        while ( portEnum.hasMoreElements() ) 
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();            
            //portList.add(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) + "\n\r");
            portList.add(portIdentifier.getName());
        }   
        
        for(String s : portList){
        	System.out.println(s);
        }

        return portList;
    }       

	private static String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
	
    public static void open(String[] port) {
	boolean portFound = false;
	//String  defaultPort = "/dev/term/a";
	String  defaultPort = "COM1";

	if (port.length > 0) {
	    defaultPort = port[0];
	} 

	portList = CommPortIdentifier.getPortIdentifiers();

	while (portList.hasMoreElements()) {
	    portId = (CommPortIdentifier) portList.nextElement();

	    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

		if (portId.getName().equals(defaultPort)) {
		    System.out.println("Found port " + defaultPort);

		    portFound = true;

		    try {
			serialPort = 
			    (SerialPort) portId.open("SimpleWrite", 2000);
		    } catch (PortInUseException e) {
			System.out.println("Port in use.");

			continue;
		    } 

		    try {
			outputStream = serialPort.getOutputStream();
		    } catch (IOException e) {}

		    try {
			serialPort.setSerialPortParams(9600, 
						       SerialPort.DATABITS_8, 
						       SerialPort.STOPBITS_1, 
						       SerialPort.PARITY_NONE);
		    } catch (UnsupportedCommOperationException e) {}
	

		    try {
		    	serialPort.notifyOnOutputEmpty(true);
		    } catch (Exception e) {
			System.out.println("Error setting event notification");
			System.out.println(e.toString());
			System.exit(-1);
		    }
		    
		    

		} 
	    } 
	} 

	if (!portFound) {
	    System.out.println("port " + defaultPort + " not found.");
	} 
    } 
    
    public static void close(){
    	try {
		       Thread.sleep(2000);  // Be sure data is xferred before closing
		    } catch (Exception e) {}
		    serialPort.close();
		    System.exit(1);
    }
    
    public static void write(String messageString) {
    	
	    System.out.println(
		    	"Writing \""+messageString+"\" to "
			+serialPort.getName());

		    try {
			outputStream.write(messageString.getBytes());
		    } catch (IOException e) {		   		    	
		    }
    }

    public static void write(List<String> messageList){
    	for(String s : messageList){
    		write(s);    		
    	}
    }
}
