/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class FileButton extends Panel {

    private ControlButton controlButton;
    private ButtonClose buttonClose;
    private ButtonShare buttonShare;

    public FileButton(Panel parent, String uid, String text) {
        super(parent, "Resources/largebutton.png", 400, 50, true);

        controlButton = new ControlButton(this, uid, 290, 50, true);
        controlButton.setText(text);
        controlButton.setTextColor(ColorRGBA.LightGray);
        controlButton.setFontSize(20);
        controlButton.addEffect(new TouchEffect(this));
        controlButton.setTextAlignment(TextAlign.LEFT);
        controlButton.leftCenter(0, 0);

        buttonClose = new ButtonClose(this, uid);
        buttonClose.rightCenter(0, 0);
        
        buttonShare = new ButtonShare(this, uid);
        buttonShare.rightCenter(54, 0);

        parent.add(this);

    }

    public void addOkButtonListener(TouchButtonListener touchButtonListener) {
        controlButton.addTouchButtonListener(touchButtonListener);
    }

    public void addDeleteButtonListener(TouchButtonListener buttonListener) {
        buttonClose.addTouchButtonListener(buttonListener);
    }
    
    public void addShareButtonListener(TouchButtonListener buttonListener) {
        buttonShare.addTouchButtonListener(buttonListener);
    }
}
