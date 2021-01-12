/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.jme3.font.BitmapText;

/**
 *
 * @author nidebruyn
 */
public class BitmapTextAccessor implements TweenAccessor<BitmapText> {

    public static final int OPACITY = 80;

    @Override
    public int getValues(BitmapText target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case OPACITY:
                returnValues[0] = target.getAlpha();
                return 1;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(BitmapText target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case OPACITY:
                target.setAlpha(newValues[0]);
                break;
            default:
                assert false;
                break;
        }
    }
}
