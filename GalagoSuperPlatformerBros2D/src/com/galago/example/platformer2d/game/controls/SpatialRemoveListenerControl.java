/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class SpatialRemoveListenerControl extends AbstractControl {
    
    private Spatial listenSpatial;

    public SpatialRemoveListenerControl(Spatial listenSpatial) {
        this.listenSpatial = listenSpatial;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (listenSpatial != null && listenSpatial.getParent() == null) {
            spatial.removeFromParent();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
