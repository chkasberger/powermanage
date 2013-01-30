import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
//import java.text.SimpleDateFormat;

public class Client {

	static Socket socket;
	static BufferedReader in;
	static PrintWriter out;
	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws InterruptedException, MalformedURLException, IOException {
		
		//Thread DBThread = null;

		//java.util.Date now = new java.util.Date();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //System.out.println(format.format(now));

        String host;
        host= "localhost";
		//host = "raspberrypi";
		//host = "10.0.0.3";
        //host = "ziontrain.no-ip.org";
        //host = "194.166.134.191";
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            
        	//echoSocket = new Socket(host, 4444);
        	echoSocket = new Socket(host, 56665);
        	//echoSocket.setSoTimeout(5000);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host + "!");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: " + host + "!");
            System.out.println(e.getMessage());
            System.exit(1);
        }

		BufferedReader stdIn = new BufferedReader(
	                                   new InputStreamReader(System.in));
		String userInput;
	
		if(args.length > 0){
			StringBuilder sb = new StringBuilder();
			for (String str : args) {
				sb.append(str + " ");
			}
			String argument = sb.toString();
			out.println(argument);
		    System.out.println(in.readLine());		    
		    System.exit(0);
		}
			 
		while ((userInput = stdIn.readLine()) != null) {
			if(userInput.equals("exit")){
				System.exit(0);
				//System.out.println("exiting 2");
			}
		    out.println(userInput);
		    System.out.println(in.readLine());
		}
	
		out.close();
		in.close();
		stdIn.close();
		echoSocket.close();			    		
	}
}