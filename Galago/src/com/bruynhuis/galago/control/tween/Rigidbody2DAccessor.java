/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.math.FastMath;


/**
 *
 * @author nidebruyn
 */
public class Rigidbody2DAccessor implements TweenAccessor<RigidBodyControl> {

    public static final int POS = 1;
    public static final int ROTATION = 2;

    @Override
    public int getValues(RigidBodyControl target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POS:
                returnValues[0] = target.getPhysicsLocation().x;
                returnValues[1] = target.getPhysicsLocation().y;
                return 2;
            case ROTATION:
                returnValues[0] = target.getPhysicsRotation() * FastMath.RAD_TO_DEG;                
                return 1;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(RigidBodyControl target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POS:
                target.setPhysicsLocation(newValues[0], newValues[1]);
                break;
              case ROTATION:
                target.setPhysicsRotation(newValues[0] * FastMath.DEG_TO_RAD);
                break;
            default:
                assert false;
                break;
        }
    }
}
