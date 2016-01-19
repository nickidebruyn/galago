/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.path;

import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.control.path.PathTemplate;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Nidebruyn
 */
public class RainbowPath extends PathTemplate {

    public RainbowPath(Node rootNode, BaseApplication baseApplication) {
        super(rootNode, baseApplication);
    }
    
    @Override
    protected void init(Spatial spatial) {
        
        path = new MotionPath();
        path.addWayPoint(new Vector3f(-20, 0, 0));
        path.addWayPoint(new Vector3f(-15, 4, 0));
        path.addWayPoint(new Vector3f(0, 10, 0));
        path.addWayPoint(new Vector3f(15, 4, 0));
        path.addWayPoint(new Vector3f(20, 0, 0));
        
        motionControl = createDefaultMotionEvent(spatial, path);
//        motionControl.setDirectionType(MotionEvent.Direction.Rotation);
//        motionControl.setLoopMode(LoopMode.Cycle);
        addMotionPathListener(path);
        
    }
    
}
