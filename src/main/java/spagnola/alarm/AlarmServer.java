/*
 *
 */

package spagnola.alarm;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketEngine;

/**
 * Java websocket alarm control panel server implementation.
 * Server expects to get the path to webapp from a properties .conf file in /etc.
 *
 * @author Perry Spagnola
 */
public class AlarmServer {
    private static final Logger LOGGER = Logger.getLogger(AlarmServer.class.getName());
    
    /** the full path to the configuration file */
    public static String CONF_PATH = "/etc/alarm.conf";
    /** the port the web socket listens on, the default is 8080 */
    public static int PORT = 8181;
    /** the path to the web app that is served by the websocket */
    public static String WEBAPP_PATH = "";
    
    /** Alarm host name, the name or IP address of the host running ser2sock */
    public static String ALARM_HOST = "";
    /** Alarm port, the port ser2sock is listening on, the default is 10000 */
    public static int ALARM_PORT = 10000;
    
    public static void main(String[] args) throws Exception {
        AlarmProperties properties = AlarmProperties.getInstance();
        
        WEBAPP_PATH = properties.WEBAPP_PATH;
        ALARM_HOST = properties.ALARM_HOST;
        ALARM_PORT = properties.ALARM_PORT;
        
        LOGGER.setLevel(properties.LOG_LEVEL);
        
        
        /** Create the socket to talk to the alarm panel. */
        Socket alarmPanelSocket = new Socket(ALARM_HOST, ALARM_PORT);
        
        /** Create a Grizzly HttpServer to serve static resources for the web app, on PORT. */
        final HttpServer server = HttpServer.createSimpleServer(WEBAPP_PATH, PORT);

        /** Register the WebSockets add-on with the HttpServer. */
        server.getListener("grizzly").registerAddOn(new WebSocketAddOn());

        /** Create and initialize the websocket alarm application. */
        final WebSocketApplication alarmApplication = new AlarmApplication();
        
        /** Set its alarm panel socket. So, the alarm application can command the alarm panel. */
        ((AlarmApplication)alarmApplication).setAlarmPanelSocket(alarmPanelSocket);

        /** Register the application with the websocket engine. */
        WebSocketEngine.getEngine().register("/alarm-panel-decoder", "/panel", alarmApplication);
        
        /** Create the Read thread. */
        final ReadThread readThread = new ReadThread((AlarmApplication)alarmApplication);
        
        /** Set the ReadThread socket to the alarm panel. */
        readThread.setAlarmPanelSocket(alarmPanelSocket);
        

        try {
            /** Try to start the server and the read thread. */
            server.start();
            LOGGER.info("Press any key to stop the server...");
            System.out.println("Enter any keyboard input to kill the server...");
            
            readThread.start();
            
            /** Block the method from falling through to exit until user keyboard input. */
            System.in.read();
        } finally {
            /** Finally, stop the server and the read thread. */
            server.shutdownNow();
            readThread.stop();
        }
    }
}
