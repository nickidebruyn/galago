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
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.io.IOException;

/**
 *
 * @author NideBruyn
 */
public class NoiseFilter extends Filter {
    
    private float amount = 0.6f;
//    private float time = 0f;
    private float speed = 0.2f;

    /**
     * creates a NoiseFilter
     */
    public NoiseFilter() {
        super("NoiseFilter");

    }

    @Override
    protected Material getMaterial() {
        material.setFloat("Amount", amount);
        material.setFloat("Speed", speed);
//        material.setFloat("Time", time);
        return material;
    }
    
    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Resources/filters/noise.j3md");
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
        if (material != null) {
            material.setFloat("Amount", amount);
        }
    }
//
//    public float getTime() {
//        return time;
//    }
//
//    @Override
//    protected void preFrame(float tpf) {
//        time += tpf;
//        if (time >= 1000) {
//            time = 0;
//        }
//        setTime(time);
//    }
//
//    public void setTime(float time) {
//        this.time = time;
//        if (material != null) {
//            material.setFloat("Time", time);
//        }
//    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        if (material != null) {
            material.setFloat("Speed", speed);
        }
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
