/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.effect;

import com.bruynhuis.galago.ui.ImageWidget;
import com.bruynhuis.galago.ui.Widget;

/**
 *
 * @author Nidebruyn
 */
public class ImageSwapEffect extends Effect {

    private String offImage;
    private String onImage;

    public ImageSwapEffect(String offImage, String onImage, Widget widget) {
        super(widget);
        this.offImage = offImage;
        this.onImage = onImage;
    }

    @Override
    protected void doShow() {
    }

    @Override
    protected void doHide() {
    }

    @Override
    protected void doTouchDown() {
        ImageWidget image = (ImageWidget) widget;
        image.updatePicture(onImage);
//        image.setTransparency(1);
    }

    @Override
    protected void doTouchUp() {
        ImageWidget image = (ImageWidget) widget;
        image.updatePicture(offImage);
//        image.setTransparency(1);
    }

    @Override
    protected void doEnabled(boolean enabled) {
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void doSelected() {
        ImageWidget button = (ImageWidget) widget;
        button.updatePicture(onImage);
//        button.setTransparency(1);
    }

    @Override
    protected void doUnselected() {
        ImageWidget button = (ImageWidget) widget;
        button.updatePicture(offImage);
//        button.setTransparency(1);
    }

    @Override
    protected void doHoverOver() {

    }

    @Override
    protected void doHoverOff() {

    }

}
