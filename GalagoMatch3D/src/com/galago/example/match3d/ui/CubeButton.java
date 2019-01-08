/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.example.match3d.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;

/**
 * An implementation of a button.
 
 * @author nicki
 */
public class CubeButton extends TouchButton {
	
    public CubeButton(Panel panel, String id) {
        super(panel, id, "Interface/button-cube.png", 60, 60, true);
        this.setText("");
        this.setBackgroundColor(ColorRGBA.White);
        this.addEffect(new TouchEffect(this));
    }	
    
    
}
