/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.controls;

import com.jme3.bullet.control.KinematicRagdollControl;

/**
 *
 * @author NideBruyn
 */
public class MyRagdoll extends KinematicRagdollControl {

    public MyRagdoll(float weightThreshold) {
        super(weightThreshold);
    }
    
    public int getBoneList() {
        return boneList.size();
    }    
    
}
