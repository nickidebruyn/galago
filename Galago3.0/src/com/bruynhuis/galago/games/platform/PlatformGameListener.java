/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform;

import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public interface PlatformGameListener {
    
    public void doGameOver();
    
    public void doGameCompleted();
        
    public void doCollisionPlayerWithStatic(Spatial collider);
    
    public void doCollisionPlayerWithDynamic(Spatial collider);
    
    public void doCollisionPlayerWithPickup(Spatial collider);
    
    public void doCollisionPlayerWithEnemy(Spatial collider);
    
    public void doCollisionEnemyWithEnemy(Spatial collider);
    
    public void doCollisionPlayerWithObstacle(Spatial collider);
    
    public void doCollisionEnemyWithObstacle(Spatial collider);
    
}
