/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.control;

import com.bruynhuis.galago.util.Timer;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author nidebruyn
 */
public class FlameLightControl extends AbstractControl {
    
    private PointLight pointLight;
    private Timer offTimer = new Timer(1);
    private boolean off = false;

    public FlameLightControl() {
    }    

    @Override
    protected void controlUpdate(float tpf) {
        if (pointLight == null) {
            Light light = spatial.getLocalLightList().get(0);
            if (light != null && light instanceof PointLight) {
                pointLight = (PointLight)light;
//                pointLight.setRadius(3);
                turnLightOff();
                
            }
        }
        
        if (pointLight != null) {
            offTimer.update(tpf);
            if (offTimer.finished()) {
                if (off) {
                    turnLightOn();
                } else {
                    turnLightOff();
                }
            }
            
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    protected void turnLightOff() {
        off = true;
        pointLight.setColor(ColorRGBA.LightGray);
        offTimer.setMaxTime(FastMath.nextRandomFloat()*20);
        offTimer.reset();
    }
    
    protected void turnLightOn() {
        off = false;
        pointLight.setColor(ColorRGBA.Orange.mult(0.5f));
        offTimer.setMaxTime(FastMath.nextRandomFloat()*30);
        offTimer.reset();
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        return new FlameLightControl();

    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
}
