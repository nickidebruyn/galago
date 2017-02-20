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
public class OldVideoFilter extends Filter {

    /**
     * creates a OldVideoFilter
     */
    public OldVideoFilter() {
        super("OldVideoFilter");

    }

    @Override
    protected Material getMaterial() {
        return material;
    }
    
    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Resources/filters/old-video.j3md");
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
