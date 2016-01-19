/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.ui.panel.Panel;

/**
 * This is an invisible button on the screen.
 * It can be used to do screen touch controls such as swipe left or swipe right, etc.
 * 
 * @author NideBruyn
 */
public class ControlButton extends TouchButton {

    public ControlButton(Panel panel, String uid, float width, float height) {
        super(panel, uid, "Resources/blank.png", width, height);
        setText(" ");
    }
    
    public ControlButton(Panel panel, String uid, String image, float width, float height) {
        super(panel, uid, image, width, height, true);
        setText(" ");
    }
}