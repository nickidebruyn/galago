/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author Nidebruyn
 */
public class ButtonPlay extends TouchButton {
    
    private static float scale = 1f;

    public ButtonPlay(Panel panel, String id) {
        super(panel, id, "Resources/button_test.png", 184*scale, 52*scale, true);
        setText("");
        addEffect(new TouchEffect(this));

    }
    
}
