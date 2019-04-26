/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.controls;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.control.AnimationControl;
import com.bruynhuis.galago.listener.AnimationListener;
import com.bruynhuis.galago.util.Debug;
import com.bruynhuis.galago.util.SharedSystem;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class CharacterCombatController extends AbstractControl implements AnimationListener, PhysicsCollisionListener {

    private Spatial mainModel;
    private AnimationControl animationControl;
    private String[] anims = {"idle", "kick", "hit", "die", "boxing", "jump"};
    private float moveSpeed = 200f;
    private Quaternion rotator = new Quaternion();
    private Vector3f lookDirection = new Vector3f(1, 0, 0);
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private BetterCharacterControl characterControl;
    private String itemA, itemB;
    private boolean walking = false;
    private boolean jumping = false;

    public CharacterCombatController(Spatial mainModel, Vector3f lookAt) {
        this.mainModel = mainModel;
        this.lookDirection = lookAt;

    }

    @Override
    protected void controlUpdate(float tpf) {

        if (animationControl == null) {
            findAnimationControl();

        }

        if (characterControl == null) {
            characterControl = new BetterCharacterControl(0.3f, 1.8f, 1f); // construct character. (If your character bounces, try increasing height and weight.)
            mainModel.addControl(characterControl); // attach to wrapper

            // set basic physical properties:
            characterControl.setJumpForce(new Vector3f(0, 3f, 0));
            characterControl.setGravity(new Vector3f(0, 1f, 0));
            characterControl.warp(mainModel.getWorldTranslation().clone()); // warp character into landscape at particular location
            spatial.lookAt(lookDirection.mult(-1), Vector3f.UNIT_Y);
            characterControl.setViewDirection(lookDirection);

            Base3DApplication base3DApplication = (Base3DApplication) SharedSystem.getInstance().getBaseApplication();

            // add to physics state
            base3DApplication.getBulletAppState().getPhysicsSpace().add(characterControl);
            base3DApplication.getBulletAppState().getPhysicsSpace().addAll(mainModel);            

        }

        if (animationControl != null) {
            
            if (!jumping && characterControl.isOnGround()) {
                
                if (walking) {
                    animationControl.play("run", true, false, 1);
                } else {
                    animationControl.play("idle", true, false, 1);
                }
                
            }            

            //Calculation to move a character to a target position
            rotator.lookAt(lookDirection, Vector3f.UNIT_Y);
            spatial.getLocalRotation().slerp(rotator, 0.08f);
            characterControl.setViewDirection(spatial.getLocalRotation().getRotationColumn(2));
            characterControl.setWalkDirection(walkDirection.mult(tpf * moveSpeed));

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private void findAnimationControl() {
        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
//                    Debug.log("Spatial: " + spatial.getName());
                if (spatial.getControl(AnimationControl.class) != null) {
                    animationControl = spatial.getControl(AnimationControl.class);
                    animationControl.addAnimationListener(CharacterCombatController.this);
                }
            }
        };

        mainModel.depthFirstTraversal(sgv);
    }

    @Override
    public void doAnimationDone(String animationName) {

        if (animationName.equals("jump")) {
            jumping = false;
        }

    }

    private boolean isInAnimationGroup(String[] group, String anim) {
        boolean isThere = false;

        for (int i = 0; i < group.length; i++) {
            String str = group[i];
            if (str.equalsIgnoreCase(anim)) {
                isThere = true;
                break;
            }
        }

        return isThere;

    }

    @Override
    public void collision(PhysicsCollisionEvent event) {

        itemA = event.getNodeA().getName();
        itemB = event.getNodeB().getName();
        Debug.log("Item A = " + itemA);
        Debug.log("Item B = " + itemB);

//        if (itemA.equals("xbot") && itemB.equals("xbot")) {
//            pickNewStandingMovement();
//            
//        } else if (itemA.equals("xbot") && itemB.equals("crate")) {
//            doJumpAnimation();
//            
//        } else if (itemB.equals("xbot") && itemA.equals("crate")) {
//            doJumpAnimation();
//        }                
    }
    
    public void setDirection(float dir) {
        this.lookDirection.setX(dir);
        
    }
    
    public void walk(boolean move) {
        this.walking = move;
        
        if (move) {
            this.walkDirection.setX(lookDirection.x);
            
        } else {
            this.walkDirection.setX(0);
            
        }
        
        
    }
    
    public void jump() {
        if (characterControl.isOnGround() && !jumping) {  
            jumping = true;
            characterControl.jump();
            animationControl.play("jump", true, false, 1);
        }
        
    }
}
