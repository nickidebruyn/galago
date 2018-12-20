/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.ui;

import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class TipDialog extends AbstractGameDialog {
    
    private Label instructions;
    private ControlButton controlButton;

    public TipDialog(Window window) {
        super(window, "TAP TO START");
        
        instructions = new Label(this, "Tilt your device left or right \nand make the bouncing ball move.", 28, 400, 200);
        instructions.setTextColor(ColorRGBA.White);
        instructions.centerAt(0, 0);
        instructions.setAlignment(TextAlign.CENTER);
        
        controlButton = new ControlButton(this, "dialog control button", window.getWidth(), window.getHeight());
        controlButton.center();
    }
    
    public void addTouchListener(TouchButtonListener buttonListener) {
        controlButton.addTouchButtonListener(buttonListener);
    }
    
}
