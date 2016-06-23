var portraitDivElement;

var portraitScreen;


var init_PortraitScreen = function() {
    
    /**
     * Create the Screen containers.
     */
    portraitDivElement = document.createElement("div");

    portraitDivElement.style.position = "absolute";
    portraitDivElement.style.width = "705px";
    portraitDivElement.style.height = "1300px";
    portraitDivElement.style.top = "5px";
    portraitDivElement.style.left = "0px";
    portraitDivElement.style.webkitTransform =  'scale(0.45) translateX(-430px) translateY(-770px)';
//    portraitDivElement.style.webkitTransform =  'scale(0.45) translateX(-440px) translateY(-575px)';
    //portraitDivElement.style.webkitTransform =  'scale(0.58) translateX(-250px) translateY(-470px)';
    //portraitDivElement.style.webkitTransform =  'translateX(10px) translateY(50px)';
    
    portraitScreen = new LCARSBlankScreen('_portrait', '', "100%", "100%");
    
//    portraitDivElement.style.border = "1px solid blue";

    portraitDivElement.appendChild(portraitScreen.element);

    document.body.appendChild(portraitDivElement);

    /**
     * Create the Screen components.
     */
    ulc = new LCARSCorner("ULC", "", 5, 5, 475, 1, ES_SHAPE_NW | EC_ORANGE | ES_STATIC);
    portraitScreen.addComponent(ulc);
    
    text_title = new LCARSText("text_title", "SECURITY SYSTEM", 485, 34 , EC_ORANGE);
    text_title.setTextFontSize(34);
    portraitScreen.addComponent(text_title);
    
    ulc_end_cap = new LCARSRectangle("ulc_end_cap", "", 680, 5, 20, 30, ES_RECT_RND_E | EC_ORANGE);
    portraitScreen.addComponent(ulc_end_cap);
    
    indicatorReady = new LCARSRectangle("ready", "READY", 175, 60, 180, 75, EF_SUBTITLE | ES_RECT_RND_W | ES_LABEL_W | EC_BLUE);
    portraitScreen.addComponent(indicatorReady);
    
    indicatorArmed = new LCARSRectangle("armed", "ARMED", 360, 60, 290, 75, EF_SUBTITLE | ES_RECT_RND_E | ES_LABEL_E | EC_RED);
    portraitScreen.addComponent(indicatorArmed);
    
    llc = new LCARSCorner("LLC", "", 5, 102, 350, 1, ES_SHAPE_SW | EC_ORANGE | ES_STATIC);
    portraitScreen.addComponent(llc);
    
    urc = new LCARSCorner("URC", "", 360, 164, 340, 1, ES_SHAPE_NE | EC_ORANGE | ES_STATIC);
    portraitScreen.addComponent(urc);
    
    mode_1 = new LCARSButton("mode_1", "MODE 1", 550, 260, 2, EF_SUBTITLE | EC_ORANGE);
    mode_1.addEventListener("click", updateMode);
    portraitScreen.addComponent(mode_1);
    
    rectConnected = new LCARSRectangle("rect_c", "CONNECTED", 550, 390, 150, 45, ES_LABEL_C | EC_BLUE);
    portraitScreen.addComponent(rectConnected);
    
    rectNotConnected = new LCARSRectangle("rect_nc", "NOT CONNECTED", 550, 390, 150, 45, ES_LABEL_C | EC_RED);
    portraitScreen.addComponent(rectNotConnected);
    
    mode_2 = new LCARSButton("mode_2", "MODE 2", 550, 440, 2, EF_SUBTITLE | EC_ORANGE);
    mode_2.addEventListener("click", updateMode);
    portraitScreen.addComponent(mode_2);
    
    lrc = new LCARSCorner("LRC", "", 360, 570, 340, 1, ES_SHAPE_SE | EC_ORANGE | ES_STATIC);
    portraitScreen.addComponent(lrc);
    
    ulc_messages = new LCARSCorner("ULC_MESSAGES", "MESSAGES", 5, 632, 350, 1, ES_SHAPE_NW | EC_ORANGE | ES_STATIC);
    portraitScreen.addComponent(ulc_messages);
    
    llc_messages = new LCARSCorner("LLC_MESSAGES", "", 5, 905, 670, 1, ES_SHAPE_SW | EC_ORANGE | ES_STATIC);
    portraitScreen.addComponent(llc_messages);
    
    ulc_end_cap = new LCARSRectangle("ulc_end_cap", "", 680, 967, 20, 30, ES_RECT_RND_E | EC_ORANGE);
    portraitScreen.addComponent(ulc_end_cap);
    
    clear_button = new LCARSButton("clear_button", "", 5, 729, 1, EC_ORANGE | ES_STATIC);
    portraitScreen.addComponent(clear_button);
    
    rect_messages = new LCARSRectangle("rect_messages", "", 5, 794, 150, 41, ES_LABEL_C | EC_ORANGE);
    portraitScreen.addComponent(rect_messages);
    
    blank_button = new LCARSButton("blank_button", "", 5, 840, 1, EC_ORANGE | ES_STATIC);
    portraitScreen.addComponent(blank_button);
    
    
    keypad = new LCARSKeypad("keypad_1", 20, 230, EF_SUBTITLE | ES_RECT_RND | EC_ORANGE, EKP_AUX_KEYS | ES_LABEL_SW | EF_BUTTON | EC_BLUE);
    keypad.addEventListener("click", key_pressed);
    keypad.setAuxText("OFF", "AWAY", "STAY", "MAX", "TEST", "BYPASS", "INSTANT", "CODE", "CHIME", " ", "READY", " ");
    portraitScreen.addComponent(keypad);
    
    keypad.setVisible(false);
    
    armButton = new LCARSRectangle("arm", "ARM", 30, 260, 475, 125, EF_TITLE | EC_RED | ES_RECT_RND | ES_LABEL_C);
    armButton.addEventListener("click", arm_pressed);
    armButton.static = 0;
    armButton.setComponentDynamics();
    portraitScreen.addComponent(armButton);
    
    disarmButton = new LCARSRectangle("disarm", "DISARM", 30, 440, 475, 125, EF_TITLE | EC_BLUE | ES_RECT_RND | ES_LABEL_C);
    disarmButton.addEventListener("click", disarm_pressed);
    disarmButton.static = 0;
    disarmButton.setComponentDynamics();
    portraitScreen.addComponent(disarmButton);
    
    //rectTest = new LCARSRectangle("rectTest", "", 175, 680, 500, 5, ES_LABEL_C | EC_BLUE);
    //portraitScreen.addComponent(rectTest);
    
    messagesArea = new LCARSTextArea("textArea", "", 175, 665, 490, 6, EC_ORANGE);
    messagesArea.setLineSpacing(1.5);
    messagesArea.setTextFontSize(30);
    portraitScreen.addComponent(messagesArea);
    
    
    /**
     * Set up the callbacks so the display will update based on data updates.
     */
    setUpdateMessageCallback(updateMessageArea);
    setUpdateDisplayCallback(updateDisplay);

}