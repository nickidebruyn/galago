/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.Effect;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.util.Debug;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author Nidebruyn
 */
public class Button extends TouchButton {

    public Button(Panel panel, String id, String text) {
        super(panel, id, "Interface/buttonBlue.png", 260, 50, true);
        setTransparency(0.5f);
        setText(text);
        setTextColor(ColorRGBA.White);
        setFontSize(26);
        addEffect(new TouchEffect(this));
        addEffect(new Effect(this) {
            @Override
            protected void doShow() {
            }
            
            @Override
            protected void doHide() {
            }
            
            @Override
            protected void doTouchDown() {
            }
            
            @Override
            protected void doTouchUp() {
            }
            
            @Override
            protected void doEnabled(boolean enabled) {
            }
            
            @Override
            protected void doSelected() {
                Debug.log("id = " + uid);
                setScale(1.1f);
                window.getApplication().getSoundManager().playSound("button");
            }
            
            @Override
            protected void doUnselected() {
                setScale(1.0f);
            }
            
            @Override
            protected void controlUpdate(float tpf) {
            }
        });
    }
    
}
