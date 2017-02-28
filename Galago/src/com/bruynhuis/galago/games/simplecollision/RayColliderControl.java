/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.simplecollision;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;

/**
 * This class will handle all types of object collisions by using ray casting.
 * 
 * @author nidebruyn
 */
public class RayColliderControl extends AbstractControl {
    
    protected SimpleCollisionGame basicGame;
    protected ArrayList<RayCaster> casters = null;
    protected Vector3f directions;

    public RayColliderControl(SimpleCollisionGame basicGame, Vector3f directions) {
        this.basicGame = basicGame;
        this.directions = directions;
        
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        //Lets build up the casters
        if (casters == null) {
            //Only test collisions in these directions
            casters = new ArrayList<RayCaster>();
            if (directions.x > 0) {
                casters.add(new RayCaster(basicGame.getLevelNode(), (Node)spatial, Vector3f.UNIT_X));
                casters.add(new RayCaster(basicGame.getLevelNode(), (Node)spatial, Vector3f.UNIT_X.negate()));
            }
            
            if (directions.y > 0) {
                casters.add(new RayCaster(basicGame.getLevelNode(), (Node)spatial, Vector3f.UNIT_Y));
                casters.add(new RayCaster(basicGame.getLevelNode(), (Node)spatial, Vector3f.UNIT_Y.negate()));
            }

            if (directions.z > 0) {
                casters.add(new RayCaster(basicGame.getLevelNode(), (Node)spatial, Vector3f.UNIT_Z));
                casters.add(new RayCaster(basicGame.getLevelNode(), (Node)spatial, Vector3f.UNIT_Z.negate()));
            }

            
        }

               
        //We check here if the game is running or not        
        if (basicGame != null && basicGame.isStarted() && !basicGame.isPaused()) {
            
            //Here will will go through all ray casts and check for collisions.
            //Once a collision occurred we call back to the basic game and informing the gamelistener that something collided.
            
            //Loop over all casters and check for collisions
            for (int i = 0; i < casters.size(); i++) {
                RayCaster rayCaster = casters.get(i);
                if (rayCaster.doCollisionCheck()) {
                    //Call back to the basic game
                    basicGame.fireCollisionEvent(spatial, rayCaster.getContactObject());
                    
                }
            }
            
        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }
    
}
