/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class ColorAccessor implements TweenAccessor<ColorRGBA> {

    public static final int COLOR_RGBA = 1;

    @Override
    public int getValues(ColorRGBA target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case COLOR_RGBA:
                returnValues[0] = target.getRed();
                returnValues[1] = target.getGreen();
                returnValues[2] = target.getBlue();
                return 3;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(ColorRGBA target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case COLOR_RGBA:
                target.set(newValues[0], newValues[1], newValues[2], 1f);
                break;

            default:
                assert false;
                break;
        }
    }
}
