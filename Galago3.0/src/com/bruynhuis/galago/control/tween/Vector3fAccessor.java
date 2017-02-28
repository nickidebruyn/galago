/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.jme3.math.Vector3f;

/**
 *
 * @author nidebruyn
 */
public class Vector3fAccessor implements TweenAccessor<Vector3f> {

    public static final int VECTOR3F = 1;

    @Override
    public int getValues(Vector3f target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case VECTOR3F:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                returnValues[2] = target.getZ();
                return 3;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Vector3f target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case VECTOR3F:
                target.set(newValues[0], newValues[1], newValues[2]);
                break;

            default:
                assert false;
                break;
        }
    }
}
