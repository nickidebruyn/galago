/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.bruynhuis.galago.control;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author NideBruyn
 */
public class RotationControl extends AbstractControl {
    
    protected Vector3f rotator = null;
    protected float rotationSpeed = 0.5f;
    
    public RotationControl() {
        
    }
    
    public RotationControl(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public RotationControl(Vector3f v) {
        this.rotator = v;
    }    
    

    @Override
    protected void controlUpdate(float tpf) {
        if (rotator == null) {
            spatial.rotate(0, FastMath.DEG_TO_RAD*rotationSpeed*tpf, 0);
        } else {
            spatial.rotate(FastMath.DEG_TO_RAD*rotator.x*tpf, FastMath.DEG_TO_RAD*rotator.y*tpf, FastMath.DEG_TO_RAD*rotator.z*tpf);
        }
        

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public Control cloneForSpatial(Spatial spatial) {
        return new RotationControl(rotationSpeed);

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
