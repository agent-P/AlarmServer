/*
 *
 */

package spagnola.alarm;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.utils.DataStructures;
import org.glassfish.grizzly.websockets.Broadcaster;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.OptimizedBroadcaster;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketListener;

import spagnola.websockets.*;

/**
 * Websockets based alarm monitoring and control application.
 * This {@link WebSocketApplication} customizes default {@link WebSocket}
 * with {@link AlarmWebSocket}, which includes some alarm specific properties and
 * logic.
 *
 * @author Perry Spagnola
 */
public class AlarmApplication extends WebSocketApplication {
    private static final Logger logger = Logger.getLogger(AlarmApplication.class.getName());

    /** Logged in members */
    private Set<WebSocket> members = Collections.newSetFromMap(DataStructures.<WebSocket, Boolean>getConcurrentMap());

    /** Initialize optimized broadcaster. */
    private final Broadcaster broadcaster = new OptimizedBroadcaster();
    
    /** Socket for talking to the alarm panel. */
    private Socket alarmPanelSocket;
    
    /** PrintWriter for writing to the alarm panel Socket. */
    private PrintWriter out;
    
    /** Last message received from alarm panel. To broadcast immediately on connect. Initialized to null. */
    private String lastMessage = null;
    
    /**
     * Sets the socket for talking to the alram panel, and creates the PrintWriter
     * object for writing to it.
     *
     * @param alarmPanelSocket  the Socket that connects to the alarm panel through ser2sockß
     */
    public void setAlarmPanelSocket(Socket alarmPanelSocket) {
        this.alarmPanelSocket = alarmPanelSocket;
        
        try {
            /** Try to create the PrintWriter object. */
            this.out = new PrintWriter(alarmPanelSocket.getOutputStream(), true);
        }
        catch (IOException exception) {
            /** Catch any IO exceptions, and log them as severe. */
            logger.severe("Could not create PrintWriter for talking to alarm panel.");
        }
    }

    /**
     * Creates a customized {@link WebSocket} implementation.
     * 
     * @return customized {@link WebSocket} implementation - {@link AlarmWebSocket}
     */
    @Override
    public WebSocket createSocket(ProtocolHandler handler, HttpRequestPacket request, WebSocketListener... listeners) {
        
        return new AlarmWebSocket(handler, request, listeners);
    }

    /**
     * Method is called, when {@link AlarmWebSocket} receives a {@link Frame}. Commands to the alarm panel
     * are written to the PrintWriter of the alarm panel Socket. A conditional response is broadcast to 
     * all connected devices.
     * @param websocket {@link AlarmWebSocket}
     * @param data {@link Frame}
     *
     * @throws IOException
     */
    @Override
    public void onMessage(WebSocket websocket, String data) {
        
        StringBuffer response = new StringBuffer("");
        String user = ((AlarmWebSocket)websocket).getUser();
        
        if(data.length() == 1) {
            switch(data.charAt(0)) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '*':
                case '#':
                    response.append("Key press...");
                    break;
                default:
                    response.append("unknown key...");
                    logger.warning("Unexpected data: " + data + " from user: " + user);
                    break;
            }
            broadcast(user, response.toString());
            out.println(data);
        }
        else {
            logger.warning("Data: " + data + " rejected from user: " + user);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnect(WebSocket websocket) {
        
        String ipAddress = ((AlarmWebSocket)websocket).getPeerIPAddress();
        logger.warning("Connection attempt from IP Address: " + ipAddress);
        
        if(isAllowed(ipAddress)) {
            members.add(websocket);
            
            /** If the last message from the alarm panel is not null, send it to the connecting
             *  client to give fast current status. DOn't want to wait until nect broadcast.
             */
            if(lastMessage != null) {
                websocket.send(lastMessage);
            }
        
            ((AlarmWebSocket)websocket).setUser(ipAddress);
            broadcast("system", ((AlarmWebSocket)websocket).getUser() + " has connected.");
        }
        else {
            websocket.close();
            ((AlarmWebSocket)websocket).setUser(ipAddress);
            broadcast("system", "Unauthorized connection attempt from: " + ((AlarmWebSocket)websocket).getUser());
            logger.warning("IP Address: " + ipAddress + " not authorized.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClose(WebSocket websocket, DataFrame frame) {

        String ipAddress = ((AlarmWebSocket)websocket).getPeerIPAddress();
        logger.warning("IP Address: " + ipAddress + " has disconnected.");

        members.remove(websocket);
        broadcast("system", ((AlarmWebSocket)websocket).getUser() + " disconnected.");
    }

    /**
     * Broadcasts the messages from the alarm controller and alarm panels.
     *
     * @param device the device identifier
     * @param text the text message
     */
    public void broadcast(String device, String text) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        final String timestamp = dateFormat.format(date);
        
        logger.log(Level.INFO, "Broadcasting: ", new Object[]{text, device});
        final String jsonMessage = toJsonp(device, text, timestamp);
        logger.log(Level.INFO, jsonMessage);
        
        lastMessage = jsonMessage;
        
        broadcaster.broadcast(members, jsonMessage);
    }
    
    
    /**
     * Method to determine if the IP address argument is allowed to access the alarm application.
     * @param ipAddress  the IP address to test for allowed access
     *
     * @return <code>true</code> if allowed, <code>false</code> if not.
     */
    private static boolean isAllowed(String ipAddress) {
        Set<String> ALLOWED_LIST = AlarmProperties.getInstance().ALLOWED_LIST;
        
        Iterator iter = ALLOWED_LIST.iterator();
        while (iter.hasNext()) {
            if(iter.next().toString().equals(ipAddress)) {
                return true;
            }
        }
        
        return false;
    }

    
    /**
     * Create the JSON string to broadcast to the keypad clients.
     *
     * @param name  the device that generated the message
     * @param message  the message
     * @param timestamp  the date and time of the message
     * @return the formed JSON message as a String object
     */
    private static String toJsonp(String name, String message, String timestamp) {
        return "{ \"name\": \"" + name +
                "\", \"message\": \"" + escape(message) + "\", \"timestamp\": \"" + timestamp + "\"}\n";
    }

    
    /**
     * Handle special characters in the input argument String object.
     *
     * @param orig  the String object to be processed for special characters.
     * @return the corrected String object
     */
    private static String escape(String orig) {
        StringBuilder buffer = new StringBuilder(orig.length());

        for (int i = 0; i < orig.length(); i++) {
            char c = orig.charAt(i);
            switch (c) {
                case '\b':
                    buffer.append("\\b");
                    break;
                case '\f':
                    buffer.append("\\f");
                    break;
                case '\n':
                    buffer.append("<br />");
                    break;
                case '\r':
                    // ignore
                    break;
                case '\t':
                    buffer.append("\\t");
                    break;
                case '\'':
                    buffer.append("\\'");
                    break;
                case '\"':
                    buffer.append("/");
                    break;
                case '\\':
                    buffer.append("\\\\");
                    break;
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                default:
                    buffer.append(c);
            }
        }

        return buffer.toString();
    }    
}
