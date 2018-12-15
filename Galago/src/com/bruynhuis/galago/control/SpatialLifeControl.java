/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.control;

import com.bruynhuis.galago.util.Timer;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class SpatialLifeControl extends AbstractControl {

    private Timer timer;

    public SpatialLifeControl(float lifetime) {
        timer = new Timer(lifetime);
        timer.start();
    }

    @Override
    protected void controlUpdate(float tpf) {
        timer.update(tpf);
        if (timer.finished()) {
            spatial.removeFromParent();
            timer.stop();
        }
    }

    public void start() {
        timer.start();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
