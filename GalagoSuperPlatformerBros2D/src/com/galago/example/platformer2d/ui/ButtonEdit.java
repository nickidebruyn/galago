/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.ui;

import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.effect.WobbleEffect;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author Nidebruyn
 */
public class ButtonEdit extends TouchButton {

    public ButtonEdit(Panel panel, String uid, float scale) {
        super(panel, uid, "Interface/button-edit.png", 284*scale, 116*scale, true);
        setText("  EDIT");
        setFontSize(52*scale);
        setTextAlignment(TextAlign.LEFT);
        addEffect(new TouchEffect(this));
        addEffect(new WobbleEffect(this, 1.03f, 0.06f));

    }
    
}
