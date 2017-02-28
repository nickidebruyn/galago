/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.camera;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import com.bruynhuis.galago.util.Timer;

/**
 *
 * @author nidebruyn
 */
public class CameraShaker extends AbstractControl {

    private Vector3f shakeCenter = new Vector3f(0, 0, 0);
    private Timer timer = new Timer(100);
    
    static public final float SMALL_AMOUNT = 0.005f;
    static public final float DEFAULT_AMOUNT = 0.01f;
    static public final float LARGE_AMOUNT = 0.05f;
    
    private float ampAmount = DEFAULT_AMOUNT;
    
    private Vector3f[] points = {Vector3f.ZERO, Vector3f.UNIT_X, Vector3f.UNIT_X.negate(),
    Vector3f.UNIT_Z, Vector3f.UNIT_Z.negate(), new Vector3f(1, 0, 1), new Vector3f(-1, 0, 1), new Vector3f(1, 0, -1)};    
    
    private Camera cam;
    private Spatial target;
    private CameraShakeListener cameraShakeListener;

    public CameraShaker(Camera cam, Spatial target) {
        this.cam = cam;
        this.target = target;
        setEnabled(false);
        target.addControl(this);
        
    }

    public CameraShakeListener getCameraShakeListener() {
        return cameraShakeListener;
    }

    public void setCameraShakeListener(CameraShakeListener cameraShakeListener) {
        this.cameraShakeListener = cameraShakeListener;
    }
    
    protected void fireCameraShakeDoneListener() {
        if (cameraShakeListener != null) {
            cameraShakeListener.done();
        }
    }


    public void update(final float tpf) {
        if (!enabled) {
            return;
        }
        
        timer.update(tpf);

        if (!timer.finished()) {
            cam.setLocation(shakeCenter.add(points[FastMath.nextRandomInt(0, points.length-1)].mult(ampAmount)));            
        } else {
            timer.stop();
            cam.setLocation(shakeCenter.clone());
            setEnabled(false);
            fireCameraShakeDoneListener();
        }
    }

    public void shake() {
        shake(DEFAULT_AMOUNT, 100);
    }

    /**
     * Shake the camera for a specified duration and a specified amount of time.
     * @param amount
     * @param duration 
     */
    public void shake(float amount, float duration) {
        shakeCenter = cam.getLocation().clone();
        ampAmount = amount;
        timer.setMaxTime(duration);
        timer.start();
        setEnabled(true);

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public Control cloneForSpatial(Spatial spatial) {
        CameraShaker control = new CameraShaker(this.cam, this.target);
        //TODO: copy parameters to new Control
        control.setSpatial(spatial);
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat(“name”, defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, “name”, defaultValue);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //throw new UnsupportedOperationException(“Not supported yet.”);
    }
}