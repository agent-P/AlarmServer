/*
 *
 */

package spagnola.alarm;

import java.io.*;
import java.util.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class AlarmProperties {
    private static final Logger LOGGER = Logger.getLogger(AlarmProperties.class.getName());

    private static AlarmProperties instance = null;
    /** the package name to support programatic logging configuration */
    private static final String PACKAGE_NAME = "spagnola.alarm";
    /** the full path to the configuration file */
    private static final String CONF_PATH = "/etc/alarm.conf";
    /** the full path to the log file */
    private static String LOG_PATH = null;
    /** the logging level */
    private static String LOG_LEVEL = null;
    /** the port the web socket listens on, the default is 8080 */
    public static int PORT = 8080;
    /** the path to the web app that is served by the websocket */
    public static String WEBAPP_PATH = "";
    
    /** Alarm host name, the name or IP address of the host running ser2sock */
    public static String ALARM_HOST = "";
    /** Alarm port, the port ser2sock is listening on, the default is 10000 */
    public static int ALARM_PORT = 10000;
    /** Alarm allowed list of devices that can access the alarm panel from the network */
    private static String allowed = "";
    /** List of allowed IP addresses in a Set collection */
    public static HashMap<String,String> ALLOWED = new HashMap<String, String>();
    
    protected AlarmProperties() {
        
        InputStream input = null;
        
        try {
            Properties properties = new Properties();
            /** Create the input stream for the configuration file and load the properties. */
            input = new FileInputStream(CONF_PATH);
            properties.load(input);
            
            /** Get the property values for the Log file path and the Logging Level. */
            LOG_PATH  = properties.getProperty("log-path");
            LOG_LEVEL = properties.getProperty("log-level");
            
            /** Create the log FileHandler. */
            FileHandler fh = createLogFileHandler(LOG_PATH, LOG_LEVEL);
            
            /** Disable console logging based on logging level. */
            disableConsoleLogging(LOG_LEVEL);
            
            /** Get the property value for the port that serves the websocket. */
            PORT = Integer.parseInt(properties.getProperty("websocket-port"));
            /** Get the property value for the path to the web app that is served by the websocket. */
            WEBAPP_PATH = properties.getProperty("webapp-path");
            /** Get the property value for the port ser2sock is listening on. */
            ALARM_PORT = Integer.parseInt(properties.getProperty("alarm-port"));
            /** Get the property value for the name or IP address of the host running ser2sock */
            ALARM_HOST = properties.getProperty("alarm-host");
            /** Get the property value for the list of allowed IP addresses. */
            allowed = properties.getProperty("allowed-list");
            /** If there is an allowed list, create a Set of the allowed list of IP addresses. */
            if(allowed != null) {
                String arr[] = allowed.split(",");
                for(int i=0; i<arr.length; i++) {
                    ALLOWED.put(arr[i], null);
                }
            }
            else {
                LOGGER.severe("No allowed devices specified, sever exiting.");
                System.exit(0);
            }
            
            // Test Code ******************************
            //LOGGER.info("properties file loaded...");
            //LOGGER.info("allowed-list = " + allowed);
            //Iterator iter = ALLOWED_LIST.iterator();
            //while (iter.hasNext()) {
            //    LOGGER.info(iter.next().toString());
            //}
            // End Test Code **************************

        }
        catch (IOException ioex) {
            /** Log the event and exit if an IO exception is caught while trying to open the conf file. */
            LOGGER.severe("Configuration file: [" + CONF_PATH + "] not found. Exiting.");
            System.exit(0);
        }
        finally {
            /** Finally, clean up the conf properties load by closing the conf file. */
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Returns the singleton instance of the class. Creates the instance if it doesn't exist.
     *
     * @return the instance of the singleton
     */
    static public AlarmProperties getInstance() {
        if(instance == null) {
            instance = new AlarmProperties();
        }
        return instance;
    }
    
    /**
     * Creates the log {@link FileHandler} and configures it using the loaded properties.
     * @param path  the full path to the log file
     * @param level  the logging level for the FileHandler
     * @return the log FileHandler. <code>null</code> if fails.
     */
    static private FileHandler createLogFileHandler(String path, String level) {
        
        FileHandler fh = null;
        
        /** Get the parent {@link Logger} for the package. */
        Logger logger = Logger.getLogger(PACKAGE_NAME);
        
        /** Create a {@link FileHandler}, and set its logging level.  */
        try {
            if(path != null) {
                fh = new FileHandler(path, true);
                
                /** Set the level as specified by argument, or to WARNING if value not recognized or specified. */
                if(level != null) {
                    if(level.equals("SEVERE")) {
                        fh.setLevel(Level.SEVERE);
                    }
                    else if(level.equals("INFO")) {
                        fh.setLevel(Level.INFO);
                    }
                    else if(level.equals("DEBUG")) {
                        fh.setLevel(Level.FINEST);
                    }
                    else if(level.equals("WARNING")) {
                        fh.setLevel(Level.WARNING);
                    }
                    else {
                        fh.setLevel(Level.WARNING);
                        LOGGER.warning("Log Level not recognized. Setting to WARNING.");
                    }
                }
                else {
                    fh.setLevel(Level.WARNING);
                    LOGGER.warning("No Log Level specified. Setting to WARNING.");
                }
                
                /** Add the {@link FileHandler} to the package parent {@link Logger} */
                logger.addHandler(fh);
                
                /** Create a {@link SimpleFormatter}, and set it as the formatter for the {@link FileHandler}. */
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
                
            }
        }
        catch (IOException ioex) {
            /** If an IO exception is caught, log it and set the FileHandler variable to null. */
            LOGGER.severe(ioex.toString());
            fh = null;
        }
        
        /** Return the FileHandler. It will be null if it can't be created. */
        return fh;
    }
    
    
    /**
     * Disable console logging if DEBUG logging level is not specified. The instance of the 
     * {@link ConsoleHandler} is removedfrom the root {@link Logger}.
     *
     * @param level  the logging level
     */
    static private void disableConsoleLogging(String level) {
        /** If the logging level does not equal DEBUG, disable the logging output to the console.
         Get the root Logger, and remove the instance of the ConsoleHandler if it is found. */
        if(!(level.equals("DEBUG"))) {
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            if(handlers[0] instanceof ConsoleHandler) {
                rootLogger.removeHandler(handlers[0]);
            }
        }
        
    }
}
