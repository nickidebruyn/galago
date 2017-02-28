/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.simplecollision;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;

/**
 *
 * This class will be used for check the collision of spatials with other.
 * Collisions will be checked from the center of the originSpatial in a
 * direction to a specific distance.
 *
 * @author nidebruyn
 */
public class RayCaster {

    private Node scene;
    private Node originSpatial;
    private Vector3f direction;
    private Vector3f contactPoint;
    private Spatial contactObject;
    private CollisionResults results;
    private Ray ray;
    private float distance;
    private float collisionLength = 1f;
    private float zOffset = 0f;
    private Line line;

    public RayCaster(Node scene, Node originSpatial, Vector3f direction) {
        this.scene = scene;
        this.originSpatial = originSpatial;
        this.direction = direction;

        this.ray = new Ray(originSpatial.getWorldTranslation(), direction);
        this.results = new CollisionResults();
        
    }
    
    public void setDebuging(AssetManager assetManager) { 
        Vector3f endPos = direction.mult(collisionLength);
        line = new Line(Vector3f.ZERO, endPos);
        line.setLineWidth(1);
        Geometry geometry = new Geometry("line", line);
        Material mat = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
        geometry.setMaterial(mat);
        originSpatial.attachChild(geometry);
    }
    
    /**
     * This method will use the spatials bounds to determine the collision length
     * @return 
     */
    public boolean doCollisionCheck() {
        return doCollisionCheck(0f);
    }

    /**
     * This method must be called to detemine if a collision occured.
     *
     * @return
     */
    public boolean doCollisionCheck(float overrideCollisionLength) {

        results.clear();
        ray.setOrigin(originSpatial.getWorldTranslation().add(0, 0, zOffset));
        ray.setDirection(direction);
        scene.collideWith(ray, results);

        if (results.size() > 0) {
            // The closest collision point is what was truly hit:
            for (int i = 0; i < results.size(); i++) {
                CollisionResult col = results.getCollision(i);

                //Check that the ray doesn't hit the object itself
                if (!originSpatial.hasChild(col.getGeometry())) {
                                       
                    contactPoint = col.getContactPoint();
                    contactObject = null;
                    
                    //Find the highest level of object
                    if (col.getGeometry().getParent().getParent().equals(scene)) {
                        contactObject = col.getGeometry().getParent();
                        
                    } else if (col.getGeometry().getParent().getParent().getParent().equals(scene)) {
                        contactObject = col.getGeometry().getParent().getParent();
                        
                    } else if (col.getGeometry().getParent().getParent().getParent().getParent().equals(scene)) {
                        contactObject = col.getGeometry().getParent().getParent().getParent();
                        
                    } else if (col.getGeometry().getParent().getParent().getParent().getParent().getParent().equals(scene)) {
                        contactObject = col.getGeometry().getParent().getParent().getParent().getParent();
                        
                    } else if (col.getGeometry().getParent().getParent().getParent().getParent().getParent().getParent().equals(scene)) {
                        contactObject = col.getGeometry().getParent().getParent().getParent().getParent().getParent();
                        
                    } else {
                        throw new RuntimeException("RayCaster doesn't support that depth of children.");
                    }
                    
                    
                    distance = ray.getOrigin().distance(contactPoint);
                    collisionLength = 0f;

                    //Determine the collision length using the WorldBounds
                    if (originSpatial.getWorldBound() instanceof BoundingSphere) {
                        collisionLength = ((BoundingSphere) originSpatial.getWorldBound()).getRadius();
                        
                    } else {
                        BoundingBox bb = (BoundingBox) originSpatial.getWorldBound();
                        if (bb != null) {
                            //First check xAxis
                            if (direction.x != 0 && bb.getXExtent() > collisionLength) {
                                collisionLength = bb.getXExtent();
                            }
                            //Check yAxis
                            if (direction.y != 0 && bb.getYExtent() > collisionLength) {
                                collisionLength = bb.getYExtent();
                            }
                            //Check zAxis
                            if (direction.z != 0 && bb.getZExtent() > collisionLength) {
                                collisionLength = bb.getZExtent();
                            }
                        } else {
                            collisionLength = 0.5f;
                        }
                    }
                    
                    if (overrideCollisionLength != 0f) {
                        collisionLength = overrideCollisionLength;
                    }

                    //Finally check if collision happend with in the length
                    if (distance <= collisionLength) {
                        return true;
                    }
                    
                }

            }
        }
        return false;
    }
    
    protected void log(String text) {
        System.out.println(text);
    }

    public Vector3f getContactPoint() {
        return contactPoint;
    }

    public Spatial getContactObject() {
        return contactObject;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getDistance() {
        return distance;
    }

    public void setzOffset(float zOffset) {
        this.zOffset = zOffset;
    }

}
