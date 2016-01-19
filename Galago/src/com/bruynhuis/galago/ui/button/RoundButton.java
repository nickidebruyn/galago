/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author nidebruyn
 */
public class RoundButton extends TouchButton {

    public RoundButton(Panel panel, String id, float width, float height) {
        super(panel, id, "Resources/button_round.png", width, height, true);
        
    }
    
}
