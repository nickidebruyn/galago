/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper3d.ui;

import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author nicki
 */
public class PlayButton extends ControlButton {

    public PlayButton(Panel parent) {
        super(parent, "playbutton", 480, 300);
        setText("TAP TO PLAY");
        setFontSize(28);
        addEffect(new TouchEffect(this));

    }

}
