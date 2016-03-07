var landscapeDivElement;

var landscapeScreen;

var init_LandscapeScreen = function() {
    
    landscapeDivElement = document.createElement("div");
    
    landscapeDivElement.style.position = "absolute";
    landscapeDivElement.style.width = "970px";
    landscapeDivElement.style.height = "530px";
    landscapeDivElement.style.top = "5px";
    landscapeDivElement.style.left = "5px";
    landscapeDivElement.style.webkitTransform = 'scale(0.75) translateX(-155px) translateY(-90px)';
    
    document.body.appendChild(landscapeDivElement);
    
    landscapeScreen = new LCARSBlankScreen('_landscape', '', "100%", "100%");
    
    landscapeDivElement.appendChild(landscapeScreen.element);
    
    
    /**
     * Lanscape screen controls area
     */
    l_urc_end_cap = new LCARSRectangle("l_urc_end_cap", "", 5, 15, 20, 30, ES_RECT_RND_W | EC_ORANGE);
    landscapeScreen.addComponent(l_urc_end_cap);
    
    l_text_title = new LCARSText("l_text_title", "SECURITY SYSTEM", 30, 44, EC_ORANGE);
    l_text_title.setTextFontSize(34);
    landscapeScreen.addComponent(l_text_title);
    
    l_urc = new LCARSCorner("L_URC", "", 225, 15, 475, 1, ES_SHAPE_NE | EC_ORANGE | ES_STATIC);
    landscapeScreen.addComponent(l_urc);
    
    l_mode_1 = new LCARSButton("mode_1_l", "MODE 1", 550, 111, 2, EF_SUBTITLE | EC_ORANGE);
    l_mode_1.addEventListener("click", updateMode);
    landscapeScreen.addComponent(l_mode_1);
    
    l_armButton = new LCARSRectangle("arm_l", "ARM", 30, 111, 475, 125, EF_TITLE | EC_RED | ES_RECT_RND | ES_LABEL_C);
    l_armButton.addEventListener("click", arm_pressed);
    l_armButton.static = 0;
    l_armButton.setComponentDynamics();
    landscapeScreen.addComponent(l_armButton);
    
    l_rectConnected = new LCARSRectangle("l_rect_c", "CONNECTED", 550, 241, 150, 45, ES_LABEL_C | EC_BLUE);
    landscapeScreen.addComponent(l_rectConnected);
    
    l_rectNotConnected = new LCARSRectangle("l_rect_nc", "NOT CONNECTED", 550, 241, 150, 45, ES_LABEL_C | EC_RED);
    landscapeScreen.addComponent(l_rectNotConnected);
    
    l_mode_2 = new LCARSButton("mode_2_l", "MODE 2", 550, 291, 2, EF_SUBTITLE | EC_ORANGE);
    l_mode_2.addEventListener("click", updateMode);
    landscapeScreen.addComponent(l_mode_2);
    
    l_disarmButton = new LCARSRectangle("disarm_l", "DISARM", 30, 291, 475, 125, EF_TITLE | EC_BLUE | ES_RECT_RND | ES_LABEL_C);
    l_disarmButton.addEventListener("click", disarm_pressed);
    l_disarmButton.static = 0;
    l_disarmButton.setComponentDynamics();
    landscapeScreen.addComponent(l_disarmButton);
    
    l_keypad = new LCARSKeypad("l_keypad_1", 20, 81, EF_SUBTITLE | ES_RECT_RND | EC_ORANGE, EKP_AUX_KEYS | ES_LABEL_SW | EF_BUTTON | EC_BLUE);
    l_keypad.addEventListener("click", key_pressed);
    l_keypad.setAuxText("OFF", "AWAY", "STAY", "MAX", "TEST", "BYPASS", "INSTANT", "CODE", "CHIME", " ", "READY", " ");
    landscapeScreen.addComponent(l_keypad);
    
    l_keypad.setVisible(false);
    
    l_lrc = new LCARSCorner("L_LRC", "", 30, 421, 670, 1, ES_SHAPE_SE | EC_ORANGE | ES_STATIC);
    landscapeScreen.addComponent(l_lrc);
    
    l_lrc_end_cap = new LCARSRectangle("l_lrc_end_cap", "", 5, 483, 20, 30, ES_RECT_RND_W | EC_ORANGE);
    landscapeScreen.addComponent(l_lrc_end_cap);
    
    /**
     * Lanscape screen indicator area
     */
    l_ulc = new LCARSCorner("L_ULC", "", 705, 15, 235, 1, ES_SHAPE_NW | EC_ORANGE | ES_STATIC);
    landscapeScreen.addComponent(l_ulc);
    
    l_ulc_end_cap = new LCARSRectangle("l_ulc_end_cap", "", 945, 15, 20, 30, ES_RECT_RND_E | EC_ORANGE);
    landscapeScreen.addComponent(l_ulc_end_cap);
    
    l_rect_upper_away = new LCARSRectangle("l_rect_upper_away", "AWAY", 705, 111, 150, 45, ES_LABEL_C | EF_BUTTON | EC_RED);
    landscapeScreen.addComponent(l_rect_upper_away);
    
    l_rectSpacer_upper_away = new LCARSRectangle("l_rectSpacer_upper_away", "", 705, 160, 150, 76, EF_SUBTITLE | EC_ORANGE);
    landscapeScreen.addComponent(l_rectSpacer_upper_away);
    
    l_rect_upper_stay = new LCARSRectangle("l_rect_upper_stay", "STAY", 705, 150, 150, 45, ES_LABEL_C | EF_BUTTON | EC_YELLOW);
    landscapeScreen.addComponent(l_rect_upper_stay);
    
    l_rectSpacer_upper_stay = new LCARSRectangle("l_rectSpacer_upper_stay", "", 705, 111, 150, 35, EF_SUBTITLE | EC_ORANGE);
    landscapeScreen.addComponent(l_rectSpacer_upper_stay);
    
    l_rectSpacer_lower_stay = new LCARSRectangle("l_rectSpacer_lower_stay", "", 705, 200, 150, 35, EF_SUBTITLE | EC_ORANGE);
    landscapeScreen.addComponent(l_rectSpacer_lower_stay);
    
    l_rect_upper_max = new LCARSRectangle("l_rect_upper_max", "MAX", 705, 191, 150, 45, ES_LABEL_C | EF_BUTTON | EC_RED);
    landscapeScreen.addComponent(l_rect_upper_max);
    
    l_rect_upper_instant = new LCARSRectangle("l_rect_upper_instant", "INSTANT", 705, 191, 150, 45, ES_LABEL_C | EF_BUTTON | EC_RED);
    landscapeScreen.addComponent(l_rect_upper_instant);
    
    l_rectSpacer_upper_m_i = new LCARSRectangle("l_rectSpacer_upper_m_i", "", 705, 111, 150, 76, EF_SUBTITLE | EC_ORANGE);
    landscapeScreen.addComponent(l_rectSpacer_upper_m_i);
    
    l_rectSpacer_upper = new LCARSRectangle("l_rectSpacer_upper", "", 705, 111, 150, 125, EF_SUBTITLE | EC_ORANGE);
    landscapeScreen.addComponent(l_rectSpacer_upper);
    
    l_indicatorReady = new LCARSRectangle("l_indicator_ready", "READY", 860, 291, 105, 125, EF_BUTTON | ES_RECT_RND_E | ES_LABEL_E | EC_BLUE);
    landscapeScreen.addComponent(l_indicatorReady);
    
    l_rectSpacer = new LCARSRectangle("l_rect_spacer", "STATUS", 705, 241, 150, 45, EF_BUTTON | EC_ORANGE);
    landscapeScreen.addComponent(l_rectSpacer);
    
    l_indicatorArmed = new LCARSRectangle("l_indicator_armed", "ARMED", 860, 111, 105, 125, EF_BUTTON | ES_RECT_RND_E | ES_LABEL_E | EC_RED);
    landscapeScreen.addComponent(l_indicatorArmed);
    
    l_rectSpacer_lower = new LCARSRectangle("l_rectSpacer_lower", "", 705, 291, 150, 125, EF_SUBTITLE | EC_ORANGE);
    landscapeScreen.addComponent(l_rectSpacer_lower);
    
    l_llc = new LCARSCorner("L_LLC", "", 705, 421, 235, 1, ES_SHAPE_SW | EC_ORANGE | ES_STATIC);
    landscapeScreen.addComponent(l_llc);
    
    l_llc_end_cap = new LCARSRectangle("l_llc_end_cap", "", 945, 483, 20, 30, ES_RECT_RND_E | EC_ORANGE);
    landscapeScreen.addComponent(l_llc_end_cap);
    
}
