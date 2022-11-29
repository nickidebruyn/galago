/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.editor.spatial;

import com.bruynhuis.galago.spatial.Polygon;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author NideBruyn
 */
public class PaintGizmo extends Node {
    
    private float radius = 5;
    private ColorRGBA color = ColorRGBA.Blue;
    private Polygon polygon;

    public PaintGizmo(String name, Camera camera, InputManager inputManager) {
        super(name);

        init();

    }

    protected void init() {
        
        polygon = new Polygon(30, radius, 0.25f);
        Geometry geom = new Geometry("PAINT-GIZMO", polygon);
        Material m = SpatialUtils.addColor(geom, color, true);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);
        attachChild(geom);
//        SpatialUtils.rotateTo(geom, -90, 0, 0);

        this.setQueueBucket(RenderQueue.Bucket.Translucent);
    }

    public void setRadius(float paintRadius) {        
        radius = FastMath.abs(paintRadius);
        polygon.setOuterRadius(radius);
        
    }

}
