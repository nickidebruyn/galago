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
public class RetryButton extends ControlButton {

    public RetryButton(Panel parent) {
        super(parent, "restartbutton", 480, 300);
        setText("TAP TO RESTART");
        setFontSize(28);
        addEffect(new TouchEffect(this));

    }

}
