var indicatorReady;
var indicatorArmed;
var l_indicatorReady;
var l_indicatorArmed;

var context;

var mode = 1;
var _ready;
var _armed;
var alarm_state;

/**
 * Display update functions ************************************************
 */

var updateMessageArea = function(text) {
    messagesArea.appendLine(text);
}

var updateDisplay = function(data) {
    
    /**
     * Update the ready indicator.
     */
    if(data[0] == '1') {
        _ready = true;
        indicatorReady.setEnabled(true);
        l_indicatorReady.setEnabled(true);
        if(mode == 1) {
            armButton.setEnabled(true);
            l_armButton.setEnabled(true);
        }
    }
    else {
        _ready = false;
        indicatorReady.setEnabled(false);
        l_indicatorReady.setEnabled(false);
        if(mode == 1) {
            armButton.setEnabled(false);
            l_armButton.setEnabled(false);
        }
    }
    
    /**
     * Update the armed indicator.
     */
    if(data[2] == '1' || data[1] == '1') {
        /** If an Armed state is detected. Enable the 'Armed' indicator. */
        _armed = true;
        indicatorArmed.setEnabled(true);
        l_indicatorArmed.setEnabled(true);
        if(mode == 1) {
            disarmButton.setEnabled(true);
            l_disarmButton.setEnabled(true);
        }
        
        if(data[2] == '1' && data[12] == '0') {
            /** If Armed - Stay is detected. Also show the 'Stay' indicator. */
            showArmedStay();
        }
        else if(data[1] == '1' && data[12] == '0') {
            /** Else if Armed - Away is detected. Also show the 'Away' indicator. */
            showArmedAway();
        }
        else if(data[1] == '1' && data[12] == '1') {
            /** Else if Armed - Max is detected. Also show the 'Max' indicator. */
            showArmedMax();
        }
        else if(data[2] == '1' && data[12] == '1') {
            /** Else if Armed - Instant is detected. Also show the 'Instant' indicator. */
            showArmedInstant();
        }
        else {
            /** Else no specific Arm state detected. Show none. */
            showArmedNone();
        }
    }
    else {
        _armed = false;
        indicatorArmed.setEnabled(false);
        l_indicatorArmed.setEnabled(false);
        if(mode == 1) {
            disarmButton.setEnabled(false);
            l_disarmButton.setEnabled(false);
        }
        
        showArmedNone();
    }
    
}


var showArmedAway = function() {
    hideArmedStay();
    hideArmedMax();
    hideArmedInstant();
    hideArmedSpacer();
    
    l_rect_upper_away.setVisible(true);
    l_rectSpacer_upper_away.setVisible(true);
}

var showArmedStay = function() {
    hideArmedAway();
    hideArmedMax();
    hideArmedInstant();
    hideArmedSpacer();
    
    l_rect_upper_stay.setVisible(true);
    l_rectSpacer_upper_stay.setVisible(true);
}

var showArmedMax = function() {
    hideArmedStay();
    hideArmedAway();
    hideArmedInstant();
    hideArmedSpacer();
    
    l_rect_upper_max.setVisible(true);
    l_rectSpacer_upper_m_i.setVisible(true);
}

var showArmedInstant = function() {
    hideArmedStay();
    hideArmedAway();
    hideArmedMax();
    hideArmedSpacer();
    
    l_rect_upper_instant.setVisible(true);
    l_rectSpacer_upper_m_i.setVisible(true);
}

var showArmedNone = function() {
    hideArmedStay();
    hideArmedAway();
    hideArmedMax();
    hideArmedInstant();
    
    l_rectSpacer_upper.setVisible(true);
}

var hideArmedAway = function() {
    l_rect_upper_away.setVisible(false);
    l_rectSpacer_upper_away.setVisible(false);
}

var hideArmedStay = function() {
    l_rect_upper_stay.setVisible(false);
    l_rectSpacer_upper_stay.setVisible(false);
}

var hideArmedMax = function() {
    l_rect_upper_max.setVisible(false);
    l_rectSpacer_upper_m_i.setVisible(false);
}

var hideArmedInstant = function() {
    l_rect_upper_instant.setVisible(false);
    l_rectSpacer_upper_m_i.setVisible(false);
}

var hideArmedSpacer = function() {
    l_rectSpacer_upper.setVisible(false);
}

var updateConnectionIndicator = function(status) {
    if(status == 'connected') {
        rectConnected.setVisible(true);
        rectNotConnected.setVisible(false);
        l_rectConnected.setVisible(true);
        l_rectNotConnected.setVisible(false);
    }
    else {
        rectConnected.setVisible(false);
        rectNotConnected.setVisible(true);
        l_rectConnected.setVisible(false);
        l_rectNotConnected.setVisible(true);
        indicatorReady.setEnabled(false);
        indicatorArmed.setEnabled(false);
        l_indicatorReady.setEnabled(false);
        l_indicatorArmed.setEnabled(false);
        if(mode == 1) {
            disarmButton.setEnabled(false);
            armButton.setEnabled(false);
            l_disarmButton.setEnabled(false);
            l_armButton.setEnabled(false);
        }
    }
}


var setMode = function(_mode) {
    mode = _mode;
}


var updateMode = function(event) {
    if(event.srcElement.id[5] == '1') {
        if(pin == true) {
            mode = 1;
        }
    }
    else if(event.srcElement.id[5] == '2') {
        mode = 2;
    }
    
    updateModeDisplay();
}


var updateModeDisplay = function() {
    if(mode == 1) {
        keypad.setVisible(false);
        l_keypad.setVisible(false);
        armButton.setVisible(true);
        disarmButton.setVisible(true);
        l_armButton.setVisible(true);
        l_disarmButton.setVisible(true);
        if(_ready) {
            armButton.setEnabled(true);
            disarmButton.setEnabled(false);
            l_armButton.setEnabled(true);
            l_disarmButton.setEnabled(false);
        }
        else if(_armed) {
            armButton.setEnabled(false);
            disarmButton.setEnabled(true);
            l_armButton.setEnabled(false);
            l_disarmButton.setEnabled(true);
        }
        else {
            armButton.setEnabled(false);
            disarmButton.setEnabled(false);
            l_armButton.setEnabled(false);
            l_disarmButton.setEnabled(false);
        }
    }
    else if(mode == 2) {
        keypad.setVisible(true);
        l_keypad.setVisible(true);
        disarmButton.setVisible(false);
        armButton.setVisible(false);
        l_disarmButton.setVisible(false);
        l_armButton.setVisible(false);
    }
}

/**
 * Update the display based on the orientation of the device. Note the 180 degree case
 * is not detected in iOS 8.x.
 *    0: portrait screen is visible, landscape screen is hidden
 *   90: portrait screen is hidden, landscape screen is visible
 *  -90: portrait screen is hidden, landscape screen is visible
 *  180: no screen change, last screen remains
 */
var updateOrientation = function() {
    //            alert(window.screen.width + ", " + window.screen.height);
    //            alert(window.orientation);
    switch(window.orientation) {
        case 0:
            landscapeDivElement.style.visibility = 'hidden';
            //document.body.removeChild(landscapeDivElement);
            portraitScreen.element.style.visibility = 'visible';
            break;
        case -90:
            portraitScreen.element.style.visibility = 'hidden';
            //document.body.appendChild(landscapeDivElement);
            landscapeDivElement.style.visibility = 'visible';
            break;
        case 90:
            portraitScreen.element.style.visibility = 'hidden';
            //document.body.appendChild(landscapeDivElement);
            landscapeDivElement.style.visibility = 'visible';
            break;
        case 180:
            landscapeDivElement.style.visibility = 'hidden';
            //document.body.removeChild(landscapeDivElement);
            portraitScreen.element.style.visibility = 'visible';
            break;

        default:
            break;
    }
}


/**
 * Manage the display when the websocket open event occurs.
 */
var onOpen = function(host, message) {
    
    updateConnectionIndicator('connected');
    updateMessageArea("connected to: " + host);
    
    if(message !== undefined)
        updateMessageArea(message);

}
