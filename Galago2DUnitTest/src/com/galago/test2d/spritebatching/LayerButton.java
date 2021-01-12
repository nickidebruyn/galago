/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.test2d.spritebatching;

import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.effect.WobbleEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author NideBruyn
 */
public class LayerButton extends Panel {

    private ControlButton button;
    private Label label;
    private float borderSize = 2;
    private String uid;
    private WobbleEffect wobbleEffect;

    public LayerButton(Panel parent, String uid, String text, float size, float borderSize) {
        super(parent, "Interface/button-toolbar.png", size, size, true);
        this.uid = uid;
        this.borderSize = borderSize;

        label = new Label(this, text, 24, size, size);
        label.setAlignment(TextAlign.CENTER);
        label.centerAt(0, 0);

        button = new ControlButton(this, uid, size, size, true);
        button.addEffect(new TouchEffect(this));

        wobbleEffect = new WobbleEffect(this, 1.1f, 0.6f);
        wobbleEffect.setEnabled(false);
        this.addEffect(wobbleEffect);

        parent.add(this);
    }

    public void select() {
        updatePicture("Interface/button-toolbar-selected.png");
        wobbleEffect.setEnabled(true);
    }

    public void unselect() {
        updatePicture("Interface/button-toolbar.png");
        wobbleEffect.setEnabled(false);
        this.setScale(1);
    }

    public void addTouchButtonListener(TouchButtonListener listener) {
        button.addTouchButtonListener(listener);

    }

    public String getUid() {
        return uid;
    }

}
