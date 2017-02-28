/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.filters;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.io.IOException;

/**
 *
 * @author NideBruyn
 */
public class ShockwaveFilter extends Filter {

    /*
     * This is the point on screen between 0 and 1 that starts the shackwave
     */
    private Vector2f distortionPoint = new Vector2f(0.5f, 0.5f);
    /*
     * Amplitude, Refraction, Width  e.g. 10.0, 0.8, 0.1
     */
    private Vector3f shockParams = new Vector3f(10.0f, 0.8f, 0.1f);
    private float distortionTime = 2f;
    private float speed = 1f;

    /**
     * creates a ShockwaveFilter
     */
    public ShockwaveFilter() {
        super("ShockwaveFilter");

    }

    @Override
    protected Material getMaterial() {
        material.setVector2("DistortionPoint", distortionPoint);
        material.setVector3("ShockParams", shockParams);
        material.setFloat("DistortionTime", distortionTime);
        return material;
    }

    @Override
    protected void preFrame(float tpf) {
        if (material != null && distortionTime < 2) {
                        
            distortionTime += (tpf*speed);
            
            setDistortionTime(distortionTime);
        }
    }

    /**
     * 
     * Apply the shockwave at the distortion point on the screen
     *
     * @param count
     */
    public void doEffect(float speed, Vector2f distortionPoint) {
        this.speed = speed;
        Vector2f point = new Vector2f(distortionPoint.x/getRenderFrameBuffer().getWidth(), distortionPoint.y/getRenderFrameBuffer().getHeight());
        setDistortionPoint(point);
        setDistortionTime(0);

    }

    public Vector2f getDistortionPoint() {
        return distortionPoint;
    }

    /**
     * This is the point on screen between 0 and 1 that starts the shackwave
     * @param distortionPoint 
     */
    public void setDistortionPoint(Vector2f distortionPoint) {
        this.distortionPoint = distortionPoint;
        if (material != null) {
            material.setVector2("DistortionPoint", distortionPoint);
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Vector3f getShockParams() {
        return shockParams;
    }

    /**
     * Amplitude, Refraction, Width  (default 10.0, 0.8, 0.1)
     * @param shockParams 
     */
    public void setShockParams(Vector3f shockParams) {
        this.shockParams = shockParams;
        if (material != null) {
            material.setVector3("ShockParams", shockParams);
        }
    }

    public float getDistortionTime() {
        return distortionTime;
    }

    public void setDistortionTime(float distortionTime) {
        this.distortionTime = distortionTime;
        if (material != null) {
            material.setFloat("DistortionTime", distortionTime);
        }
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Resources/filters/shockwave.j3md");
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
//        oc.write(distortionOffsets, "distortionOffsets", Vector3f.ZERO);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
//        distortionOffsets = (Vector3f) ic.readSavable("distortionOffsets", Vector3f.ZERO);
    }
}
