/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.ui;

import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.ImageSwapEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.geometryrunner.game.LevelDefinition;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class LevelButton extends Panel {
    
    private TouchButton touchButton;
    private LevelDefinition levelDefinition;
    private static float scale = 1.6f;

    public LevelButton(Widget parent, LevelDefinition levelDefinition) {
        super(parent, null, 190*scale, 49*scale, true);
        this.levelDefinition = levelDefinition;
        
        touchButton = new TouchButton(this, levelDefinition.getUid()+"", "Interface/blue_button13.png", 190*scale, 49*scale, true);
        touchButton.setText(levelDefinition.getLevelName());
        touchButton.setFontSize(18*scale);
        touchButton.setTextColor(ColorRGBA.White);
        touchButton.addEffect(new ImageSwapEffect("Interface/blue_button13.png", "Interface/green_button13.png", touchButton));
        
        ((Panel)parent).add(this);
    }
    
    public void addTouchButtonListener(TouchButtonListener listener) {
        touchButton.addTouchButtonListener(listener);
    }

    public LevelDefinition getLevelDefinition() {
        return levelDefinition;
    }
    
    public void setEnabled(boolean enabled) {
        touchButton.setEnabled(enabled);
    }
}
