/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author NideBruyn
 */
public class RaySpatialCollisionControl extends AbstractControl {

    public static final String TYPE= "TYPE";
    
    private Vector3f contactPoint;
    private Geometry contactObject;
    private CollisionResults results;
    private Ray ray;
    private Vector3f direction = null;
    private Node scene;
    private Node originObject;
    private float collisionLength = 1f;
    private RaySpatialListener raySpatialListener;
    private String[] collisionTypes;
    

    public RaySpatialCollisionControl(Node scene, Node originObject, Vector3f direction, float collisionLength, String[] collisionTypes) {
        this.scene = scene;
        this.originObject = originObject;
        this.direction = direction;
        this.collisionLength = collisionLength;
        this.collisionTypes = collisionTypes;
        
        if (direction == null) {
            direction = originObject.getWorldRotation().getRotationColumn(2);
        }
        
        this.ray = new Ray(originObject.getWorldTranslation(), direction);
        this.results = new CollisionResults();
    }

    @Override
    protected void controlUpdate(float tpf) {
        callControl(tpf);
    }
    
    /**
     * This is an external all for direct usage.
     * @param tpf 
     */
    public void callControl(float tpf) {        
        results.clear();
        ray.setOrigin(originObject.getWorldTranslation());
        ray.setDirection(direction);
        scene.collideWith(ray, results);

        if (results.size() > 0) {
            // The closest collision point is what was truly hit:
            for (int i = 0; i < results.size(); i++) {
                CollisionResult col = results.getCollision(i);

                if (collisionTypes != null && !originObject.hasChild(col.getGeometry())) {
                    for (int j = 0; j < collisionTypes.length; j++) {
                        String typeStr = collisionTypes[j];
                        String type = col.getGeometry().getUserData(TYPE);
                        if (type != null && type.equals(typeStr)) {
                            contactPoint = col.getContactPoint();
                            contactObject = col.getGeometry();
                            float distance = ray.getOrigin().distance(contactPoint);
                            if (distance > collisionLength) {
                                fireSpatialListener(contactPoint, contactObject, false);
                            } else {
                                fireSpatialListener(contactPoint, contactObject, true);
                            }
                            return;
                        }
                    }
                }

            }            
        }
    }

    public void addRaySpatialListener(RaySpatialListener raySpatialListener1) {
        this.raySpatialListener = raySpatialListener1;
    }

    public void fireSpatialListener(Vector3f contactPoint, Geometry contactObject, boolean hasCollision) {
        if (raySpatialListener != null) {
            raySpatialListener.doAction(contactPoint, contactObject, hasCollision);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial spatial) {
        return this;

    }
}
