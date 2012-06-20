import java.io.IOException;

import org.apache.log4j.*;

public class PMLogger {
	private static Logger logger = Logger.getRootLogger();
	public Level logLevel = Level.ALL;
	
    PMLogger(){
        SimpleLayout layout = new SimpleLayout();
        ConsoleAppender consoleAppender = new ConsoleAppender( layout );
        logger.addAppender( consoleAppender );
        FileAppender fileAppender = null;
		try {
			fileAppender = new FileAppender( layout, "logs/MeineLogDatei.log", false );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        logger.addAppender( fileAppender );
        // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
        logger.setLevel( logLevel );
    }
}
