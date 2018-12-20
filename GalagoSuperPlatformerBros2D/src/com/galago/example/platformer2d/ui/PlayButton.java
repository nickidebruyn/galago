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
public class PlayButton extends TouchButton {
    
    private static float scale = 0.75f;

    public PlayButton(Panel panel) {
        super(panel, "play-button", "Interface/button-play-off.png", 256*scale, 256*scale, true);
        addEffect(new ImageSwapEffect("Interface/button-play-off.png", "Interface/button-play-on.png", this));

    }
    
}
