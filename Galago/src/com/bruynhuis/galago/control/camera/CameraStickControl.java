/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.camera;

import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author nidebruyn
 */
public class CameraStickControl extends AbstractControl {
    
    private Camera camera;

    public CameraStickControl(Camera camera1) {
        this.camera = camera1;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if (camera != null) {
            spatial.setLocalTranslation(camera.getLocation().x, camera.getLocation().y, camera.getLocation().z);
        }

    }


    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public Control cloneForSpatial(Spatial spatial) {
        return this;
    }
    
}
