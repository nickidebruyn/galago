/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics;

import com.jme3.scene.control.Control;
import org.dyn4j.dynamics.Body;

/**
 *
 * @author nidebruyn
 */
public interface PhysicsControl extends Control {

    public void setPhysicsSpace(PhysicsSpace space);

    public PhysicsSpace getPhysicsSpace();

    public void setEnabled(boolean state);
    
    public Body getBody();
    
    public void setBody(Body body);
}