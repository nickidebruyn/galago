/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.ui.effect.ImageSwapEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author NideBruyn
 */
public class OverlayImageButton extends Panel {

    protected TouchButton button;

    public OverlayImageButton(Panel parent, String id, String image, String imageSelected, String imageOverlay, float scale, float width, float height) {
        super(parent, image, width, height, true);

        if (imageOverlay == null) {
            imageOverlay = "Resources/blank.png";
        }

        button = new TouchButton(this, id, imageOverlay, width * scale, height * scale, true);
        button.center();
        button.setText("");
        button.addEffect(new ImageSwapEffect(image, imageSelected, this));

        parent.add(this);
    }

    public void addTouchButtonListener(TouchButtonListener buttonListener) {
        button.addTouchButtonListener(buttonListener);

    }
}
