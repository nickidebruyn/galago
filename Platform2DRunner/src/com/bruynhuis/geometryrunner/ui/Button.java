/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.ImageSwapEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class Button extends TouchButton {
    
    private static float scale = 1.5f;

    public Button(Panel panel, String id, String text) {
        super(panel, id, "Interface/blue_button01.png", 190*scale, 49*scale, true);
        setText(text);
        setFontSize(22*scale);
        setTextColor(ColorRGBA.White);
        addEffect(new ImageSwapEffect("Interface/blue_button01.png", "Interface/green_button01.png", this));

    }
    
}
