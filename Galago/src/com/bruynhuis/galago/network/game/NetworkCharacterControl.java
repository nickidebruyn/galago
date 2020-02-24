/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.game;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.objects.PhysicsRigidBody;

/**
 *
 * @author nidebruyn
 */
public class NetworkCharacterControl extends BetterCharacterControl {

    public NetworkCharacterControl() {
    }

    public NetworkCharacterControl(float radius, float height, float mass) {        
        super(radius, height, mass);
    }

    public PhysicsRigidBody getPhysicsRigidBody() {
        return rigidBody;
    }

}
