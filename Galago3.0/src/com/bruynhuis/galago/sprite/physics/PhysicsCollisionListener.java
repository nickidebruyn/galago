/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics;

import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public interface PhysicsCollisionListener {
    
//    public void collision(Spatial spatialA, Spatial spatialB);
    
    public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB, Vector3f collisionPoint);
    
}
