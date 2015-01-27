package spagnola.alarm;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;


public class ReadThread implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ReadThread.class.getName());

    private volatile boolean running = true;
    
    private Thread t;
    private Socket alarmPanelSocket;
    private final String threadName = "ser2sock_reader";
    
    private AlarmApplication alarmApplication;
    
    ReadThread(AlarmApplication alarmApplication) {
        
        LOGGER.setLevel(AlarmProperties.getInstance().LOG_LEVEL);
        
        this.alarmApplication = alarmApplication;
        
        
        LOGGER.info("Creating " +  threadName );
    }
    
    public void setAlarmPanelSocket(Socket alarmPanelSocket) {
        this.alarmPanelSocket = alarmPanelSocket;
    }
    
    
    public void run() {
        LOGGER.info("Running " +  threadName );
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(alarmPanelSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            String userInput;
            while (running) {
                // TODO: add a string buffer for the input from the buffered reader.
                //LOGGER.info("relay: " + in.readLine());
                alarmApplication.broadcast("alarm panel", in.readLine());
            }
        } catch (IOException e) {
            LOGGER.severe("Couldn't get I/O for the connection to the alarm system.");
            System.exit(1);
        }
    }
    
    
    public void start() {
        LOGGER.info("Starting " +  threadName );
        if (t == null)
        {
            t = new Thread (this, threadName);
            t.start ();
        }
    }
    
    public void stop() {
        running = false;
    }
    
}
