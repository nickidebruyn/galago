/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics;

import org.dyn4j.dynamics.Body;

/**
 *
 * @author NideBruyn
 */
public interface PhysicsSpaceListener {

    void bodyAdded(Body body);

    void bodyRemoved(Body body);

}
