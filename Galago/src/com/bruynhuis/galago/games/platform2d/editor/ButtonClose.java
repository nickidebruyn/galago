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
public class ButtonClose extends TouchButton {
    
    private static float scale = 0.5f;

    public ButtonClose(Panel panel, String id) {
        super(panel, id, "Resources/button-delete.png", 100*scale, 100*scale, true);
        setText("");
        addEffect(new TouchEffect(this));

    }
    
}
