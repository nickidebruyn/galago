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
public class ButtonWide extends TouchButton {
    
    private static float scale = 0.6f;

    public ButtonWide(Panel panel, String id, String text) {
        super(panel, id, "Resources/largebutton.png", 340*scale, 100*scale, true);
        setText(text);
        setFontSize(42*scale);
        addEffect(new TouchEffect(this));

    }
    
}
