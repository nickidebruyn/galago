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
public class ScoreButton extends TouchButton {
    
    private static float scale = 0.5f;

    public ScoreButton(Panel panel) {
        super(panel, "score-button", "Interface/button-score-off.png", 256*scale, 256*scale, true);
        addEffect(new ImageSwapEffect("Interface/button-score-off.png", "Interface/button-score-on.png", this));

    }
    
}
