/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author nidebruyn
 */
public class RigidbodyAccessor implements TweenAccessor<RigidBodyControl> {

    public static final int POS_XYZ = 1;
    public static final int ROTATION_X = 3;
    public static final int ROTATION_Y = 4;
    public static final int ROTATION_Z = 5;
    public static final int ROTATION_XYZ = 6;

    @Override
    public int getValues(RigidBodyControl target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POS_XYZ:
                returnValues[0] = target.getPhysicsLocation().x;
                returnValues[1] = target.getPhysicsLocation().y;
                returnValues[2] = target.getPhysicsLocation().z;
                return 3;
            case ROTATION_X:
                returnValues[0] = target.getPhysicsRotation().toAngles(null)[0] * FastMath.RAD_TO_DEG;
                return 1;
            case ROTATION_Y:
                returnValues[0] = target.getPhysicsRotation().toAngles(null)[1] * FastMath.RAD_TO_DEG;
                return 1;
            case ROTATION_Z:
                returnValues[0] = target.getPhysicsRotation().toAngles(null)[2] * FastMath.RAD_TO_DEG;
                return 1;
            case ROTATION_XYZ:
                returnValues[0] = target.getPhysicsRotation().toAngles(null)[0] * FastMath.RAD_TO_DEG;
                returnValues[1] = target.getPhysicsRotation().toAngles(null)[1] * FastMath.RAD_TO_DEG;
                returnValues[2] = target.getPhysicsRotation().toAngles(null)[2] * FastMath.RAD_TO_DEG;
                
                target.getPhysicsRotation().toAngles(returnValues);
                return 3;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(RigidBodyControl target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POS_XYZ:
                target.setPhysicsLocation(new Vector3f(newValues[0], newValues[1], newValues[2]));
                break;
            case ROTATION_X:
                Quaternion q = new Quaternion();
                q.fromAngleAxis(newValues[0] * FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
                target.setPhysicsRotation(q);
                break;
            case ROTATION_Y:
                q = new Quaternion();
                q.fromAngleAxis(newValues[0] * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
                target.setPhysicsRotation(q);
                break;
              case ROTATION_Z:
                q = new Quaternion();
                q.fromAngleAxis(newValues[0] * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
                target.setPhysicsRotation(q);
                break;
              case ROTATION_XYZ:                
                q = new Quaternion(new float[]{newValues[0] * FastMath.DEG_TO_RAD, newValues[1] * FastMath.DEG_TO_RAD, newValues[2] * FastMath.DEG_TO_RAD});
                target.setPhysicsRotation(q);
                break;
            default:
                assert false;
                break;
        }
    }
}
