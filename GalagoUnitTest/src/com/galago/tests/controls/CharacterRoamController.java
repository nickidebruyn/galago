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
import com.bruynhuis.galago.util.Timer;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
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
public class CharacterRoamController extends AbstractControl implements AnimationListener, PhysicsCollisionListener {

    private Spatial mainModel;
    private AnimationControl animationControl;
    private Timer startMovement = new Timer(50);
    private String[] anims = {"idle", "kick", "hit", "die", "boxing", "jump"};
    private Timer changeTimer = new Timer(5);
    private float moveSpeed = 2f;
    private Vector3f targetPosition;
//    private Vector3f direction;
    private Quaternion rotator = new Quaternion();
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private BetterCharacterControl characterControl;
    private String itemA, itemB;

    public CharacterRoamController(Spatial mainModel) {
        this.mainModel = mainModel;

    }

    @Override
    protected void controlUpdate(float tpf) {

        if (animationControl == null) {
            findAnimationControl();
            startMovement.start();

        }

        if (characterControl == null) {
            characterControl = new BetterCharacterControl(0.6f, 1.8f, 1f); // construct character. (If your character bounces, try increasing height and weight.)
            mainModel.addControl(characterControl); // attach to wrapper
            
            // set basic physical properties:
            characterControl.setJumpForce(new Vector3f(0, 2f, 0));
            characterControl.setGravity(new Vector3f(0, 1f, 0));
            characterControl.warp(mainModel.getWorldTranslation().clone()); // warp character into landscape at particular location
            
            Base3DApplication base3DApplication = (Base3DApplication) SharedSystem.getInstance().getBaseApplication();
            
            // add to physics state
            base3DApplication.getBulletAppState().getPhysicsSpace().add(characterControl);
            base3DApplication.getBulletAppState().getPhysicsSpace().addAll(mainModel);
//            base3DApplication.getBulletAppState().getPhysicsSpace().addCollisionListener(this);

        }

        if (animationControl != null) {

            //A timer for starting the characters movement
            startMovement.update(tpf);
            if (startMovement.finished()) {
                pickNewStandingMovement();
                startMovement.stop();
            }

            //Calculation to move a character to a target position
            if (targetPosition != null) {
                changeTimer.update(tpf);

                rotator.lookAt(targetPosition.subtract(spatial.getWorldTranslation()).normalize(), Vector3f.UNIT_Y);
                spatial.getLocalRotation().slerp(rotator, 0.08f);

                characterControl.setViewDirection(spatial.getLocalRotation().getRotationColumn(2));
                
                
                walkDirection = spatial.getLocalRotation().mult(Vector3f.UNIT_Z).normalize();                
//                Debug.log("Direction = " + walkDirection);
                characterControl.setWalkDirection(walkDirection.mult(tpf*moveSpeed));
//                spatial.move(direction.x * tpf * moveSpeed, 0, direction.z * tpf * moveSpeed);

                if (changeTimer.finished()) {
                    if (spatial.getWorldTranslation().distance(targetPosition) <= 1f) {
                        pickNewStandingMovement();
                    }
                }
            } else {
                walkDirection.set(0, 0, 0);
                characterControl.setWalkDirection(walkDirection);
            }

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private void pickRandomAnimation() {
        int index = FastMath.nextRandomInt(0, anims.length - 1);
        String animName = anims[index];
        animationControl.play(animName, true, false, 1);
    }

    private void findAnimationControl() {
        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
//                    Debug.log("Spatial: " + spatial.getName());
                if (spatial.getControl(AnimationControl.class) != null) {
                    animationControl = spatial.getControl(AnimationControl.class);
                    animationControl.addAnimationListener(CharacterRoamController.this);
                }
            }
        };

        mainModel.depthFirstTraversal(sgv);
    }

    private void pickNewStandingMovement() {
        targetPosition = null;
        changeTimer.stop();
        pickRandomAnimation();

    }

    private void pickNewTargetPosition() {
        Debug.log("Change Target");

        changeTimer.reset();

        targetPosition = spatial.getWorldTranslation().add(FastMath.nextRandomInt(-5, 5), 0, FastMath.nextRandomInt(-5, 5));

        if (FastMath.nextRandomInt(0, 1) == 0) {
            //Run
            moveSpeed = 200f;
            animationControl.play("run", true, false, 1);

        } else {
            //Walk
            moveSpeed = 100f;
            animationControl.play("walk", true, false, 1);
        }

    }

    @Override
    public void doAnimationDone(String animationName) {

        if (isInAnimationGroup(anims, animationName)) {
            pickNewTargetPosition();

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
    
    private void doJumpAnimation() {
        if (characterControl.isOnGround()) {
            animationControl.play("jump", true, false, 1);
            characterControl.jump();
        }
        
    }
}
