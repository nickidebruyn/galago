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
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.io.IOException;

/**
 *
 * @author NideBruyn
 */
public class CircularFadingFilter extends Filter {

    private Vector3f circleCenter = new Vector3f(500f, 500f, 0);
    private float circleRadius = 0.2f;

    /**
     * creates a CircularFadingFilter
     */
    public CircularFadingFilter() {
        super("CircularFadingFilter");

    }

    @Override
    protected Material getMaterial() {
        material.setVector3("CircleCenter", circleCenter);
        material.setFloat("CircleRadius", circleRadius);
        return material;
    }

    public Vector3f getCircleCenter() {
        return circleCenter;
    }

    public void setCircleCenter(Vector3f circleCenter) {
        this.circleCenter = circleCenter;
        if (material != null) {
            material.setVector3("CircleCenter", circleCenter);
        }
    }

    public float getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
        if (material != null) {
            material.setFloat("CircleRadius", circleRadius);
        }
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Resources/filters/circular-fading.j3md");
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
