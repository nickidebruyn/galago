/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public class SpatialAccessor implements TweenAccessor<Spatial> {

    public static final int POS_XYZ = 1;
    public static final int SCALE_XYZ = 2;
    public static final int ROTATION_X = 3;
    public static final int ROTATION_Y = 4;
    public static final int ROTATION_Z = 5;
    public static final int ROTATION_XYZ = 6;
    public static final int POS_Y = 7;

    @Override
    public int getValues(Spatial target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POS_XYZ:
                returnValues[0] = target.getLocalTranslation().x;
                returnValues[1] = target.getLocalTranslation().y;
                returnValues[2] = target.getLocalTranslation().z;
                return 3;
            case SCALE_XYZ:
                returnValues[0] = target.getLocalScale().x;
                returnValues[1] = target.getLocalScale().y;
                returnValues[2] = target.getLocalScale().z;
                return 3;
            case ROTATION_X:
                returnValues[0] = target.getLocalRotation().toAngles(null)[0] * FastMath.RAD_TO_DEG;
                return 1;
            case ROTATION_Y:
                returnValues[0] = target.getLocalRotation().toAngles(null)[1] * FastMath.RAD_TO_DEG;
                return 1;
            case ROTATION_Z:
                returnValues[0] = target.getLocalRotation().toAngles(null)[2] * FastMath.RAD_TO_DEG;
                return 1;
            case ROTATION_XYZ:
                returnValues[0] = target.getLocalRotation().toAngles(null)[0] * FastMath.RAD_TO_DEG;
                returnValues[1] = target.getLocalRotation().toAngles(null)[1] * FastMath.RAD_TO_DEG;
                returnValues[2] = target.getLocalRotation().toAngles(null)[2] * FastMath.RAD_TO_DEG;

                target.getLocalRotation().toAngles(returnValues);
                return 3;                
            case POS_Y:
                returnValues[0] = target.getLocalTranslation().y;
                return 1;

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Spatial target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POS_XYZ:
                target.setLocalTranslation(newValues[0], newValues[1], newValues[2]);
                break;
            case SCALE_XYZ:
                target.setLocalScale(newValues[0], newValues[1], newValues[2]);
                break;
            case ROTATION_X:
                Quaternion q = new Quaternion();
                q.fromAngleAxis(newValues[0] * FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
                target.setLocalRotation(q);
                break;
            case ROTATION_Y:
                q = new Quaternion();
                q.fromAngleAxis(newValues[0] * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
                target.setLocalRotation(q);
                break;
            case ROTATION_Z:
                q = new Quaternion();
                q.fromAngleAxis(newValues[0] * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
                target.setLocalRotation(q);
                break;
            case ROTATION_XYZ:
                q = new Quaternion(new float[]{newValues[0] * FastMath.DEG_TO_RAD, newValues[1] * FastMath.DEG_TO_RAD, newValues[2] * FastMath.DEG_TO_RAD});
                target.setLocalRotation(q);
                break;
            case POS_Y:
                target.setLocalTranslation(target.getLocalTranslation().x, newValues[0], target.getLocalTranslation().z);
                break;
            default:
                assert false;
                break;
        }
    }
}
