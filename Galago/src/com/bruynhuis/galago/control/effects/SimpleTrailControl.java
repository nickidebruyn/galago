/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.effects;

import com.bruynhuis.galago.control.SpatialLifeControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.LineBatchNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Line;

/**
 *
 * @author nicki
 */
public class SimpleTrailControl extends AbstractControl {

    private Spatial target;
    private float segmentLength;
    private float lifeSpan;
    private float lineWidth = 6f;
    private LineBatchNode lineBatchNode;
    private Vector3f lastSpawnPos;
    private Material material;

    public SimpleTrailControl(Spatial target, float segmentLength, float lifeSpan) {
        this.target = target;
        this.segmentLength = segmentLength;
        this.lifeSpan = lifeSpan;

    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (material != null) {
            if (lastSpawnPos == null) {
                lastSpawnPos = target.getWorldTranslation().clone();

            } else if (lastSpawnPos.distance(target.getWorldTranslation()) >= segmentLength) {

                //Initialize the line for the first time
                if (lineBatchNode == null) {
                    lineBatchNode = new LineBatchNode("linetrail");
                    lineBatchNode.setLineWidth(lineWidth);
                    ((Node) spatial).attachChild(lineBatchNode);

                }

                //Add a line
                addLine(lastSpawnPos, target.getWorldTranslation().clone());

                //Set the last spawn position to the position of the target object
                lastSpawnPos = target.getWorldTranslation().clone();
            }

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    protected void addLine(Vector3f startPos, Vector3f endPos) {
        Line line = new Line(startPos, endPos);
        Geometry lineGeometry = new Geometry("line_geom", line);
        lineGeometry.setMaterial(material);
        lineGeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        lineBatchNode.attachChild(lineGeometry);
        lineGeometry.addControl(new SpatialLifeControl(lifeSpan));
        
        lineBatchNode.batch();
    }
    
    public void setLineWidth(float width) {
        this.lineWidth = width;
        if (material != null) {
            material.getAdditionalRenderState().setLineWidth(width);
        }
        
    }
    
    
}
