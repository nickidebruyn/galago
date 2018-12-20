/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.ImageSwapEffect;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author Nidebruyn
 */
public class HomeButton extends TouchButton {
    
    private static float scale = 0.5f;

    public HomeButton(Panel panel) {
        super(panel, "home-button", "Interface/button-home-off.png", 256*scale, 256*scale, true);
        addEffect(new ImageSwapEffect("Interface/button-home-off.png", "Interface/button-home-on.png", this));

    }
    
}
