/*
 *
 */

package spagnola.alarm;

import java.io.*;
import java.util.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class AlarmProperties {
    private static final Logger LOGGER = Logger.getLogger(AlarmProperties.class.getName());

    static private AlarmProperties instance = null;
    /** the full path to the configuration file */
    private static final String CONF_PATH = "/etc/alarm.conf";
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
    public static Set<String> ALLOWED_LIST = null;
    
    
    public static Level LOG_LEVEL;
    
    //FileHandler fh;
    
    protected AlarmProperties() {
        
        InputStream input = null;
        
        try {
            Properties properties = new Properties();
            /** Create the input stream for the configuration file and load the properties. */
            input = new FileInputStream(CONF_PATH);
            properties.load(input);
            
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
            /** Create a Set pf the allowed list of IP addresses. */
            ALLOWED_LIST = new HashSet<String>(Arrays.asList(allowed.split(",")));
            
            /** Get the property value for the Logging Level. Set the level as specified, or to WARNING if
             value not recognized or specified. */
            String logLevel = properties.getProperty("log-level");
            if(logLevel != null) {
                if(logLevel.equals("SEVERE")) {
                    LOGGER.setLevel(Level.SEVERE);
                }
                else if(logLevel.equals("INFO")) {
                    LOGGER.setLevel(Level.INFO);
                }
                else if(logLevel.equals("DEBUG")) {
                    LOGGER.setLevel(Level.FINEST);
                }
                else if(logLevel.equals("WARNING")) {
                    LOGGER.setLevel(Level.WARNING);
                }
                else {
                    LOGGER.setLevel(Level.WARNING);
                    LOGGER.warning("Log Level not recognized. Setting to WARNING.");
                }
            }
            else {
                LOGGER.setLevel(Level.WARNING);
                LOGGER.warning("No Log Level specified. Setting to WARNING.");
            }
            
            LOG_LEVEL = LOGGER.getLevel();
            
            
            //LOGGER.info("properties file loaded...");
            LOGGER.info("allowed-list = " + allowed);
            Iterator iter = ALLOWED_LIST.iterator();
            while (iter.hasNext()) {
                LOGGER.info(iter.next().toString());
            }

            
            
//            fh = new FileHandler("/Users/spagnola/test.log");
//            LOGGER.addHandler(fh);
//            SimpleFormatter formatter = new SimpleFormatter();
//            fh.setFormatter(formatter);
            
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
    
    static public AlarmProperties getInstance() {
        if(instance == null) {
            instance = new AlarmProperties();
        }
        return instance;
    }
}
