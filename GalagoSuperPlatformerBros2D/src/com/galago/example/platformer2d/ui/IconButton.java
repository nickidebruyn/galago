/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author Nidebruyn
 */
public class IconButton extends TouchButton {

    public IconButton(Panel panel, String id, String pictureFile) {
        super(panel, id, pictureFile, 100, 100, true);
        setText("");
        
    }
    
}
