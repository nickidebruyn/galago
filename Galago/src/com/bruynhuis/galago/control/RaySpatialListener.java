/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 *
 * @author NideBruyn
 */
public interface RaySpatialListener {
    
    public void doAction(Vector3f contactPoint, Geometry contactObject, boolean hasCollision);
    
}
