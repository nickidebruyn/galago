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
import com.jme3.math.ColorRGBA;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.io.IOException;

/**
 *
 * @author NideBruyn
 */
public class FogFilter extends Filter {
    
    private float fogStartDistance = 100.0f;
    private float fogMaxDistance = 800f;
    private float fogDensity = 0.8f;
    private ColorRGBA fogColor = ColorRGBA.White;

    /**
     * creates a FogFilter
     */
    public FogFilter() {
        super("FogFilter");

    }

    @Override
    protected Material getMaterial() {
        material.setFloat("FogStartDistance", fogStartDistance);
        material.setFloat("FogMaxDistance", fogMaxDistance);
        material.setFloat("FogDensity", fogDensity);
        material.setColor("FogColor", fogColor);
        return material;
    }
    
    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Resources/filters/fog.j3md");
    }

    public float getFogStartDistance() {
        return fogStartDistance;
    }

    public void setFogStartDistance(float fogStartDistance) {
        this.fogStartDistance = fogStartDistance;
        if (material != null) {
            material.setFloat("FogStartDistance", fogStartDistance);
        }
    }
    
    public float getFogMaxDistance() {
        return fogMaxDistance;
    }

    public void setFogMaxDistance(float fogMaxDistance) {
        this.fogMaxDistance = fogMaxDistance;
        if (material != null) {
            material.setFloat("FogMaxDistance", fogMaxDistance);
        }
    }
    
    public float getFogDensity() {
        return fogDensity;
    }

    public void setFogDensity(float fogDensity) {
        this.fogDensity = fogDensity;
        if (material != null) {
            material.setFloat("FogDensity", fogDensity);
        }
    }
    
    public ColorRGBA getFogColor() {
        return fogColor;
    }

    public void setFogColor(ColorRGBA color) {
        this.fogColor = color;
        if (material != null) {
            material.setColor("FogColor", fogColor);
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
