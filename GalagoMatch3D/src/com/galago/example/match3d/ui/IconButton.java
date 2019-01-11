/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.example.match3d.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 * An implementation of a button.
 
 * @author NideBruyn
 */
public class IconButton extends TouchButton {
	
    public IconButton(Panel panel, String id, String image) {
        super(panel, id, image, 50, 50, true);
        this.setText(" ");
        this.addEffect(new TouchEffect(this));
    }	
    
    
}
