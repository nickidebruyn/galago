/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.spatial;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author nidebruyn
 */
public class PickMarker {
    
    private Spatial spatial;
    private Material material;
    private float size = 6f;
    
    public PickMarker(Material material) {
        this.material = material;
    }    
    
    public void load(Node rootNode) {
        Node node = new Node();

        Quad quad = new Quad(size, size);
        Geometry geometry = new Geometry("MARKER", quad);
        geometry.rotate(FastMath.DEG_TO_RAD*(-90.0f), 0.0f, 0.0f);
        geometry.setLocalTranslation(-size*0.5f, 0.005f, size*0.5f);
        geometry.setMaterial(material);
        
        node.attachChild(geometry);        
        spatial = node;
        rootNode.attachChild(node);
        spatial.addControl(new MarkerController());
        spatial.setQueueBucket(Bucket.Transparent);
    }
    
    public void close() {
        spatial.removeFromParent();
    }
    
    public void doMark(Vector3f position) {
        spatial.setLocalScale(1f);
        spatial.setLocalTranslation(position);
    }
    
    public class MarkerController extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {
            if (spatial.getLocalScale().x > 0f) {
                spatial.setLocalScale(spatial.getLocalScale().x - (tpf*3f));
            }
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
        }

        public Control cloneForSpatial(Spatial spatial) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
