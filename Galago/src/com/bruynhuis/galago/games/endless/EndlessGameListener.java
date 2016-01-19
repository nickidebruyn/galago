/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.endless;

import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public interface EndlessGameListener {
    
    public void doGameOver();
        
    public void doScoreChanged(int score);
    
    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider);
    
    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider);
    
    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider);
        
}
