/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.bruynhuis.galago.ui.Widget;

/**
 *
 * @author nidebruyn
 */
public class WidgetAccessor implements TweenAccessor<Widget> {

    public static final int POS_XY = 1;
    public static final int SCALE_XY = 2;
    public static final int ROTATION = 3;
    public static final int ROTATION_Y = 5;
    public static final int OPACITY = 4;

    @Override
    public int getValues(Widget target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POS_XY:
                returnValues[0] = target.getPosition().x;
                returnValues[1] = target.getPosition().y;
                return 2;
            case SCALE_XY:
                returnValues[0] = target.getScales().x;
                returnValues[1] = target.getScales().y;
                return 2;
            case ROTATION:
                returnValues[0] = target.getRotation();
                return 1;
                
            case ROTATION_Y:
                returnValues[0] = target.getRotationY();
                return 1;
                
            case OPACITY:
                returnValues[0] = target.getTransparency();
                return 1;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Widget target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POS_XY:
                target.setPosition(newValues[0], newValues[1]);
                break;
            case SCALE_XY:
                target.setScales(newValues[0], newValues[1]);
                break;
            case ROTATION:
                target.setRotation(newValues[0]);
                break;
            case ROTATION_Y:
                target.setRotationY(newValues[0]);
                break;
            case OPACITY:
                target.setTransparency(newValues[0]);
                break;
            default:
                assert false;
                break;
        }
    }
}
