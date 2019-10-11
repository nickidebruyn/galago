/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.camera;

import com.jme3.math.Vector3f;
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
    private Vector3f offset;

    public CameraStickControl(Camera camera1) {
        this.camera = camera1;
        offset = new Vector3f(0, 0, 0);
    }

    public CameraStickControl(Camera camera, Vector3f offset) {
        this.camera = camera;
        this.offset = offset;
        if (this.offset == null) {
            this.offset = new Vector3f(0, 0, 0);
        }
    }    
    
    @Override
    protected void controlUpdate(float tpf) {
        if (camera != null) {
            spatial.setLocalTranslation(camera.getLocation().x + offset.x, camera.getLocation().y + offset.y, camera.getLocation().z + offset.z);
        }

    }


    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public Control cloneForSpatial(Spatial spatial) {
        return this;
    }
    
}
