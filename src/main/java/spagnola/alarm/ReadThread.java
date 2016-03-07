package spagnola.alarm;

import java.io.*;
import java.net.*;
import java.util.Observable;
import java.util.Scanner;
import java.util.logging.Logger;


public class ReadThread extends Observable implements Runnable {
    private static final Logger logger = Logger.getLogger(ReadThread.class.getName());

    private volatile boolean running = true;
    
    private Thread t;
    private Socket alarmPanelSocket;
    private final String threadName = "ser2sock_reader";
    
    private AlarmApplication alarmApplication;
    
    private static final String[] alarmStateStrings = {
        "ARMED_STAY",
        "ARMED_AWAY",
        "ARMED_MAX",
        "ARMED_INSTANT",
        "DISARMED",
        "DISARMED_CHIME",
        "DISARMED_TEST"
    };
    
    /** The alarm panel state constants */
    private static final int ARMED_STAY     = 0;
    private static final int ARMED_AWAY     = 1;
    private static final int ARMED_MAX      = 2;
    private static final int ARMED_INSTANT  = 3;
    private static final int DISARMED       = 4;
    private static final int DISARMED_CHIME = 5;
    private static final int DISARMED_TEST  = 6;
    
    private int alarmState = DISARMED;
    
    
    /**
     *
     */
    ReadThread(AlarmApplication alarmApplication) {
        
        this.alarmApplication = alarmApplication;
        
        logger.info("Creating " +  threadName );
    }
    
    public void setAlarmPanelSocket(Socket alarmPanelSocket) {
        this.alarmPanelSocket = alarmPanelSocket;
    }
    
    
    
    /**
     *
     */
    public void run() {
        logger.info("Running " +  threadName );
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(alarmPanelSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            String userInput;
            while (running) {
                // TODO: add a string buffer for the input from the buffered reader.
                //logger.info("relay: " + in.readLine());
                String panelMessage = in.readLine();
                logger.info(panelMessage);
                setAlarmPanelDataFrame("alarm panel", panelMessage);
                
            }
        } catch (IOException e) {
            logger.severe("Couldn't get I/O for the connection to the alarm system.");
            System.exit(1);
        }
    }
    
    
    /**
     *
     */
    public void start() {
        logger.info("Starting " +  threadName );
        if (t == null)
        {
            t = new Thread (this, threadName);
            t.start ();
        }
    }
    
    public void stop() {
        running = false;
    }

    
    /**
     * Sets and processes the messages from the alarm alarm panel. Calls the
     * notify method to send the data frame to the websocket listeners.
     *
     * @param device the device identifier
     * @param text the data frame from the alarm panel
     */
    public void setAlarmPanelDataFrame(String device, String text) {
        
        int lastAlarmState = alarmState;
        
        if(text.charAt(0) == '!') {
            logger.info("panel message: " + text);
        }
        else {
            /** Get the status characters from the data frame. */
            String delimiters = "[\\[\\]]";
            String tokens[] = text.split(delimiters);
            String status = tokens[1];
            
            logger.info("Setting: " + status);
            
            
            /** Derive the alarm panel state from the status characters. */
            if(status.charAt(0) == '1') {
                /** Found a disarmed state. Find the sub state. */
                if(status.charAt(3) == '0' && status.charAt(5) == '1') {
                    alarmState = DISARMED_TEST;
                }
                else if(status.charAt(3) == '1' && status.charAt(5) == '1' && status.charAt(8) == '1') {
                    alarmState = DISARMED_CHIME;
                }
                else {
                    alarmState = DISARMED;
                }
            }
            else {
                /** Found an armed state. Find the sub state. */
                if(status.charAt(2) == '1' && status.charAt(12) == '0') {
                    alarmState = ARMED_STAY;
                }
                else if(status.charAt(1) == '1' && status.charAt(12) == '0') {
                    alarmState = ARMED_AWAY;
                }
                else if(status.charAt(2) == '1' && status.charAt(12) == '1') {
                    alarmState = ARMED_INSTANT;
                }
                else if(status.charAt(1) == '1' && status.charAt(12) == '1') {
                    alarmState = ARMED_MAX;
                }
            }
        }
        
        /** Detect an alarm state change. */
        if(alarmState != lastAlarmState) {
            /** Log the alarm state change. */
            logger.warning("Alarm state changed to: " + alarmStateStrings[alarmState]);
            
            /** Set the Observable state to "changed", and notify the Observers of the state change. */
            setChanged();
            notifyObservers("Alarm State Change");
        }
        
        /** Notify the observers that there is a new data string from the alarm panel.
         * Set the Observable state to "changed", and notify the Observers. */
        setChanged();
        notifyObservers(text);
    }

}

