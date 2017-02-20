/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics;

/**
 *
 * @author nidebruyn
 */
public interface PhysicsTickListener {
    
    public void prePhysicsTick(PhysicsSpace space, float tpf);

    public void physicsTick(PhysicsSpace space, float tpf);
    
}
