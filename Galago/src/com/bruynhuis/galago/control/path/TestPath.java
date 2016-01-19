/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.path;

import com.bruynhuis.galago.app.BaseApplication;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public class TestPath extends PathTemplate {

    public TestPath(Node rootNode, BaseApplication baseApplication) {
        super(rootNode, baseApplication);
    }
    
    @Override
    protected void init(Spatial spatial) {

        path = new MotionPath();
        path.addWayPoint(new Vector3f(-500*widthFactor, 150*heightFactor, 0));
        path.addWayPoint(new Vector3f(-350*widthFactor, -120*heightFactor, 0));
        path.addWayPoint(new Vector3f(0, -180*heightFactor, 0));
        path.addWayPoint(new Vector3f(350*widthFactor, -120*heightFactor, 0));
        path.addWayPoint(new Vector3f(500*widthFactor, 150*heightFactor, 0));
        
        motionControl = createDefaultMotionEvent(spatial, path);
        addMotionPathListener(path);
        
//        path.enableDebugShape(baseApplication.getAssetManager(), rootNode);

    }
    
}
