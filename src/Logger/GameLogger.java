package Logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameLogger {

	private static Logger	logger = Logger.getLogger("war");
    private static String	LOG_FILES_PATH = "logs/";
    
    
	public static void removeConsoleHandler() {
		logger.setUseParentHandlers(false);
	}
	
    public static void addFileHandler(Object fillerBy, String logFileName){
        FileHandler handler = null;
        try {
            handler = new FileHandler(LOG_FILES_PATH + logFileName+".log");
            handler.setFilter(new ObjectFilter(fillerBy));
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "could not create file: " + logFileName);
        }
    }

    public static void log(Object filter,Level level, String message){
        logger.log(level, message, filter);
    }

}