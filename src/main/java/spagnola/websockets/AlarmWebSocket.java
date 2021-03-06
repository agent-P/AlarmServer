/*
 *
 */

package spagnola.websockets;

import java.util.logging.Logger;

import java.util.regex.*;

import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketListener;

import spagnola.alarm.*;


/**
 * Customize {@link WebSocket} implementation, which contains chat application
 * specific properties and logic.
 *
 * @author Perry Spagnola
 */
public class AlarmWebSocket extends DefaultWebSocket {
    private static final Logger logger = Logger.getLogger(AlarmWebSocket.class.getName());
    
    /** The protcol handler for the socket. Needed to get connection related information. */
    private ProtocolHandler protocolHandler;
    
    /** The http request for the connection. Needed to get http header information to deal with reverse proxies. */
    private HttpRequestPacket request;
    
    /** Remote user identifier */
    private volatile String user;

    public AlarmWebSocket(ProtocolHandler protocolHandler, HttpRequestPacket request, WebSocketListener... listeners) {
        super(protocolHandler, request, listeners);
        
        /** Set the protocol handler. */
        this.protocolHandler = protocolHandler;
        
        /** Set the hhtp request. */
        this.request = request;
    }
    
    
    /**
     * Get the address of the peer that made the connection.
     *
     * @return the address string which includes the originating port
     */
    public String getPeerAddress() {
        return protocolHandler.getConnection().getPeerAddress().toString();
    }
    
    /**
     * Get the address of the real ip address of the peer that made the connection. Needed
     * to handle reverse proxy implementations.
     *
     * @return the address string which includes the originating port
     */
    public String getRealIpAddress() {
        return this.request.getHeader("x-real-ip");

    }
    
    /**
     * Get the IP address of the peer that made the connection.
     *
     * @return the IP address string of the device making the connection
    */
    public String getPeerIPAddress() {
        final String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        String realIp = getRealIpAddress();
        if(realIp != null) {
            Matcher matcher = pattern.matcher(getRealIpAddress());
            if(matcher.find()) {
                return matcher.group();
            }
        }
        else {
            Matcher matcher = pattern.matcher(getPeerAddress());
            if(matcher.find()) {
                return matcher.group();
            }
            else {
                return "0.0.0.0";
            }
        }
        
        return "0.0.0.0";
    }
    
    
    /**
     * Get the user name
     * @return the user name
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the user name
     * @param user the user name
     */
    public void setUser(String user) {
        this.user = user;
    }
}
