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
public class VignetteFilter extends Filter {
    
    private float reduction = 1f;
    private float boost = 1f;

    /**
     * creates a BarrelBlurFilter
     */
    public VignetteFilter() {
        super("VignetteFilter");

    }

    @Override
    protected Material getMaterial() {
        material.setFloat("Reduction", reduction);
        material.setFloat("Boost", boost);
        return material;
    }
    
    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Resources/filters/vignette.j3md");
    }
    
    public float getReduction() {
        return reduction;
    }

    public void setReduction(float reduction) {
        this.reduction = reduction;
        if (material != null) {
            material.setFloat("Reduction", reduction);
        }
    }
    
    public float getBoost() {
        return boost;
    }

    public void setBoost(float boost) {
        this.boost = boost;
        if (material != null) {
            material.setFloat("Boost", boost);
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
