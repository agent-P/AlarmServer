var webSocket;
var messages = document.getElementById("messages");

var dataFrame;


function get_appropriate_ws_url() {
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
    
    return pcol + u[0];
}



function openSocket(host) {
    // Ensures only one connection is open at a time
    if(webSocket != undefined && webSocket.readyState != WebSocket.CLOSED){
        writeResponse("WebSocket is already opened.");
        return;
    }
    // Create a new instance of the websocket
    webSocket = new WebSocket("ws://" + host + ":8181/alarm-panel-decoder/panel");
    
    /**
     * Bind functions to the listeners for the websocket.
     */
    webSocket.onopen = function(event) {
        updateConnectionIndicator('connected');
        writeResponse("connected to: " + host);
        
        // For reasons I can't determine, onopen gets called twice
        // and the first time event.data is undefined.
        // Leave a comment if you know the answer.
        if(event.data == undefined)
            return;
        
        writeResponse(event.data);
    };
    
    webSocket.onmessage = function(event) {
        
        var messageText = "error parsing message...";
        
        JSONobject = JSON.parse(event.data);
        //alert(JSONobject.name);
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
 * Sends the value of the text input to the server
 */
function send() {
    var text = document.getElementById("messageinput").value;
    webSocket.send(text);
}

function closeSocket() {
    webSocket.close();
}

function writeResponse(text){
    messagesArea.appendLine(text);
}

function key_pressed(event) {
    //messagesArea.appendLine(event.srcElement.id[0]);
    webSocket.send(event.srcElement.id[0]);
}

