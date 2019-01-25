/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.match3d.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nicki
 */
public class Button extends TouchButton {

    public Button(Panel parent, String id, String text) {
        super(parent, id, "Interface/button.png", 160, 50);
        setText(text);
        setFontSize(24);
        setTextColor(ColorRGBA.DarkGray);
        addEffect(new TouchEffect(this));

    }

}
