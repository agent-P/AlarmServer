var webSocket;
var messages = document.getElementById("messages");

var authenticated = false;
var pin = false;
var dataFrame;


function get_appropriate_ws_protocol() {
    var pcol;
    var u = document.URL;
    
    /*
     * We open the websocket encrypted if this page came on an
     * https:// url itself, otherwise unencrypted
     */
    
    if (u.substring(0, 5) == "https") {
        pcol = "wss://";
        u = u.substr(8);
    } else {
        pcol = "ws://";
        if (u.substring(0, 4) == "http")
            u = u.substr(7);
    }
    
    u = u.split('/');
    
    return pcol;
}



function openSocket(host) {
    /** Ensures only one connection is open at a time. */
    if(webSocket != undefined && webSocket.readyState != WebSocket.CLOSED){
        writeResponse("WebSocket is already opened.");
        return;
    }
    /** Create a new instance of the websocket. */
    protocol = get_appropriate_ws_protocol();
    webSocket = new WebSocket(protocol + host + "/alarm-panel-decoder/panel");
    
    /**
     * Bind functions to the listeners for the websocket.
     */
    webSocket.onopen = function(event) {
        
        onOpen(host, event.data);
    };
    
    webSocket.onmessage = function(event) {
        
        var messageText = "error parsing message...";
        
        JSONobject = JSON.parse(event.data);
        if(JSONobject.name == "alarm panel") {
            if(JSONobject.message == "!Sending..done") {
                return;  // Don't write anything to screen.
            }
            
            /**
             * Parse out the panel text message.
             */
            var tempString = JSONobject.message.split("/", 2);
            messageText = tempString[1];
            
            /**
             * Parse out the panel control data frame.
             */
            dataFrame = JSONobject.message.split("[", 2);
            //writeResponse(dataFrame[1]);
            updateDisplay(dataFrame[1]);
        }
        else if(JSONobject.name == "authentication") {
            if(JSONobject.message == "access granted") {
                authenticated = true;
                messageText = "User authenticated.";
            }
            else if(JSONobject.message == "access denied") {
                authenticated = false;
                messageText = "User not authenticated.";
            }
            else if(JSONobject.message == "PIN") {
                pin = true;
                setMode(1);
                updateModeDisplay();
                return;  // Don't write anything to screen.
            }
            else if(JSONobject.message == "NO PIN") {
                pin = false;
                setMode(2);
                updateModeDisplay();
                return;  // Don't write anything to screen.
            }
        }
        else {
            messageText = JSONobject.message;
        }
        writeResponse(messageText);
    };
    
    webSocket.onclose = function(event) {
        updateConnectionIndicator('not connected');
        writeResponse("Connection closed");
    };
}

/**
 * Determines if the user has been authenticated.
 */
function isAuthenticated() {
    return authenticated;
}

/**
 * Determines if the user has a valid PIN.
 */
function hasPIN() {
    return pin;
}

/**
 * Sends the value of the text input to the server
 */
function send(text) {
    webSocket.send(text);
}

function closeSocket() {
    webSocket.close();
}

function writeResponse(text){
    updateMessage(text);
}

function key_pressed(event) {
    webSocket.send(event.srcElement.id[0]);
}

function arm_pressed(event) {
    webSocket.send("ARM");
}

function disarm_pressed(event) {
    webSocket.send("DISARM");
}

var onOpen;

function setOnOpenCallback(callback) {
    onOpen = callback;
}

var updateMessage;

function setUpdateMessageCallback(callback) {
    updateMessage = callback;
}

var updateDisplay;

function setUpdateDisplayCallback(callback) {
    updateDisplay = callback;
}

function setModeCallback(callback) {
    setMode = callback;
}


