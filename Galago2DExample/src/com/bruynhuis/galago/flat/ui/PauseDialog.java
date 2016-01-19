/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.flat.ui;

import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class PauseDialog extends PopupDialog {
    
    protected Label info;
    protected TouchButton closeButton;

    public PauseDialog(Window window) {
        super(window);
        
        setTitle("Paused");
//        setBackgroundColor(ColorRGBA.Orange);
        
        info = new Label(this, "The game is paused, please close this popup to continue playing!", 18, 500, 120);
        info.setTextColor(ColorRGBA.Gray);
        info.centerAt(0, 20);
        
        closeButton = new TouchButton(this, "close pause", "Close");
        closeButton.centerBottom(0, 10);
        closeButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                PauseDialog.this.hide();
            }
            
        });
        
    }
    
}
