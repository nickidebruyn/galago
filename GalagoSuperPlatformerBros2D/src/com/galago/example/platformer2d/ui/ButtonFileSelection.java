/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.ui;

import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author NideBruyn
 */
public class ButtonFileSelection extends Panel {
    
    private static float scale = 0.6f;
    private ControlButton controlButton;
    private ButtonClose buttonClose;

    public ButtonFileSelection(Panel parent, String uid, String text) {
        super(parent, "Interface/button-xtrawide.png", 512*scale, 128*scale, true);
        
        controlButton = new ControlButton(this, uid, 512*scale, 128*scale, true);
        controlButton.setText(text);
        controlButton.setFontSize(42*scale);
        controlButton.addEffect(new TouchEffect(this));
        controlButton.setTextAlignment(TextAlign.LEFT);
        
        buttonClose = new ButtonClose(this, uid);
        buttonClose.rightCenter(-60, 0);
        
        parent.add(this);
        
    }
    
    public void addOkButtonListener(TouchButtonListener touchButtonListener) {
        controlButton.addTouchButtonListener(touchButtonListener);
    }
    
    public void addDeleteButtonListener(TouchButtonListener buttonListener) {
        buttonClose.addTouchButtonListener(buttonListener);
    }
}
