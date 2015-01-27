/*
 *
 */

package spagnola.alarm;

import java.io.*;
import java.net.*;
import java.util.*;
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
        logger.setLevel(AlarmProperties.getInstance().LOG_LEVEL);
        return new AlarmWebSocket(handler, request, listeners);
    }

    /**
     * Method is called, when {@link AlarmWebSocket} receives a {@link Frame}. Commands to the alarm panel
     * are written to the PrintWriter of the alarm panel Socket.
     * @param websocket {@link AlarmWebSocket}
     * @param data {@link Frame}
     *
     * @throws IOException
     */
    @Override
    public void onMessage(WebSocket websocket, String data) {
        broadcast(((AlarmWebSocket)websocket).getUser(), data);
        out.println(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnect(WebSocket websocket) {
        
        String ipAddress = ((AlarmWebSocket)websocket).getPeerIPAddress();
        logger.info("Connection attempt from IP Address: " + ipAddress);
        
        if(isAllowed(ipAddress)) {
            members.add(websocket);
        
            ((AlarmWebSocket)websocket).setUser(ipAddress);
            broadcast("system", ((AlarmWebSocket)websocket).getUser() + " has connected.");
        }
        else {
            logger.warning("IP Address: " + ipAddress + " not authorized.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClose(WebSocket websocket, DataFrame frame) {
        members.remove(websocket);
        broadcast("system", ((AlarmWebSocket)websocket).getUser() + " disconnected.");
    }

    /**
     * Broadcasts the message from the alarm controller and alarm panels.
     *
     * @param device the device identifier
     * @param text the text message
     */
    public void broadcast(String device, String text) {
        logger.log(Level.INFO, "Broadcasting: {0} from: {1}", new Object[]{text, device});
        final String jsonMessage = toJsonp(device, text);
        logger.log(Level.INFO, jsonMessage);
        
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

    
    private static String toJsonp(String name, String message) {
        return "window.parent.app.update({ name: \"" + escape(name) +
                "\", message: \"" + escape(message) + "\" });\n";
    }

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
                    buffer.append("\\\"");
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
