/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.path;

import com.bruynhuis.galago.app.BaseApplication;
import com.jme3.animation.LoopMode;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public abstract class PathTemplate {

    protected Node rootNode;
    protected BaseApplication baseApplication;

    protected MotionPath path;
    protected MotionEvent motionControl;
    
    protected float widthFactor = 1f;
    protected float heightFactor = 1f;
    
    protected PathListener previewListener;

    public PathTemplate(Node rootNode, BaseApplication baseApplication) {
        this.rootNode = rootNode;
        this.baseApplication = baseApplication;
        this.widthFactor = baseApplication.getApplicationWidthScaleFactor();
        this.heightFactor = baseApplication.getApplicationHeightScaleFactor();
    }
    
    protected abstract void init(Spatial spatial);

    public void start(Spatial spatial, float speed) {

        init(spatial);

        if (path != null && motionControl != null) {
            motionControl.setSpeed(speed);
            motionControl.play();
        }
        
        fireMoveListenerStart();
        fireMoveListenerBusy();
    }

    protected void addMotionPathListener(final MotionPath motionPath) {
        motionPath.addListener(new MotionPathListener() {
            public void onWayPointReach(MotionEvent control, int wayPointIndex) {
                if (motionPath.getNbWayPoints() == wayPointIndex + 1) {                    
//                    control.getSpatial().removeControl(MotionEvent.class);
                    
                    fireMoveListenerDone();

                } else {
                    fireMoveListenerBusy();
                }
            }
        });
    }

    protected MotionEvent createDefaultMotionEvent(final Spatial spatial, final MotionPath motionPath) {
        MotionEvent motionControl = new MotionEvent(spatial, motionPath);
        motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));
        motionControl.setInitialDuration(10f);
        motionControl.setLoopMode(LoopMode.Cycle);
        return motionControl;
    }
        
    public void addPreviewListener(PathListener previewListener1) {
        this.previewListener = previewListener1;
    }
    
    protected void fireMoveListenerStart() {
        if (previewListener != null) {
            previewListener.moveStarted();
        }
    }
    
    protected void fireMoveListenerBusy() {
        if (previewListener != null) {
            previewListener.moveBusy();
        }
    }
    
    protected void fireMoveListenerDone() {
        if (previewListener != null) {
            previewListener.moveDone();
        }
    }
    
}
