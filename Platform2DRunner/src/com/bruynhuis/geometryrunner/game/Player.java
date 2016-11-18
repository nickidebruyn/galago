/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.game;

import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.games.platform2d.Platform2DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.DefaultParticleInfluencer;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nidebruyn
 */
public class Player extends Platform2DPlayer implements PhysicsCollisionListener {

    private Sprite sprite;
    private RigidBodyControl rigidBodyControl;
    private boolean right;
    private float moveSpeed = 6f;
    private float jumpForce = 18f;
    private boolean onGround = false;

    private int pickups = 0;
    private Spatial dust;
    private ParticleEmitter dustEmitter;
    private Vector3f forwardDust = new Vector3f(0.1f, 6, 0);

    public Player(Platform2DGame platform2DGame) {
        super(platform2DGame);
    }

    @Override
    protected void init() {
        lives = 0;

        sprite = new Sprite(Platform2DGame.TYPE_PLAYER, getSize(), getSize(), 2, 5, 8);
        sprite.setMaterial(game.getBaseApplication().getAssetManager().loadMaterial("Materials/player.j3m"));

        rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(getSize(), getSize()), 1f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(1f);
        playerNode.addControl(rigidBodyControl);
        playerNode.attachChild(sprite);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().add(playerNode);
        rigidBodyControl.setPhysicLocation(new Vector3f(startPosition.x, startPosition.y, 0f));
        rigidBodyControl.setGravityScale(2.5f);

        initEffects();

        playerNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                
                if (dust != null) {
                    dust.setLocalTranslation(getPosition().x-0.5f, getPosition().y-getSize()*0.5f, -0.1f);
                }

                if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

                    if (right) {
                        emitDust(onGround);
                        rigidBodyControl.move(tpf * moveSpeed, 0);

                    }

                    if (getPosition().y < -5f) {
                        game.doGameOver();
                    }

                }

                if (rigidBodyControl != null) {
                    //Correct the rotation so that the player don;t twist
//                    rigidBodyControl.getBody().setAngularVelocity(0);
                    rigidBodyControl.getBody().setAngularDamping(0);
//                    rigidBodyControl.getBody().getTransform().setRotation(0);
                    if (onGround) {
                        rigidBodyControl.getBody().setAngularVelocity(0);
                    } else {
                        rigidBodyControl.getBody().setAngularVelocity(-7f);
                    }
                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

        //Add the collision listener
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsCollisionListener(this);

    }

    protected void initEffects() {
        dust = game.getBaseApplication().getModelManager().getModel("Models/dust.j3o");
        dust.setCullHint(Spatial.CullHint.Always);
        dust.setLocalTranslation(-getSize() * 0.5f, -getSize() * 0.5f, 0);
        game.getRootNode().attachChild(dust);

        dustEmitter = ((ParticleEmitter) ((Node) dust).getChild(0));
        dustEmitter.preload(game.getBaseApplication().getRenderManager(), game.getBaseApplication().getViewPort());

    }

    protected void emitDust(boolean emit) {
        if (emit) {
            dust.setCullHint(Spatial.CullHint.Never);
            ((DefaultParticleInfluencer) dustEmitter.getParticleInfluencer()).setInitialVelocity(forwardDust);

        } else {
            dustEmitter.killAllParticles();
            dust.setCullHint(Spatial.CullHint.Always);
        }

    }

    @Override
    public Vector3f getPosition() {
        return rigidBodyControl.getPhysicLocation().clone();
    }

    @Override
    public void doDie() {
        emitDust(false);
        game.getBaseApplication().getEffectManager().doEffect("die", getPosition());
        sprite.removeFromParent();
    }

    public void jump(float extrajumpForcePer) {

        if (onGround) {
            rigidBodyControl.clearForces();
            rigidBodyControl.applyImpulse(0, jumpForce * extrajumpForcePer);
            onGround = false;
        }

    }


    public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB) {
        if (spatialA != null && spatialA.equals(playerNode) && spatialB != null) {
//            log("collision: A");
//            if (spatialB.getControl(RigidBodyControl.class).hasMultipleBodies()) {
//                doOnGroundCheck(spatialB, collisionShapeB.getLocation());
//            } else {
//                doOnGroundCheck(spatialB, spatialB.getControl(RigidBodyControl.class).getPhysicLocation());
//            }
            onGround = true;

        } else if (spatialB != null && spatialB.equals(playerNode) && spatialA != null) {
//            log("collision: B");
//            if (spatialA.getControl(RigidBodyControl.class).hasMultipleBodies()) {
//                doOnGroundCheck(spatialA, collisionShapeA.getLocation());
//            } else {
//                doOnGroundCheck(spatialA, spatialA.getControl(RigidBodyControl.class).getPhysicLocation());
//            }
            onGround = true;

        }

    }

    public boolean isOnGround() {
        return rigidBodyControl.getLinearVelocity().y < 0.01f && rigidBodyControl.getLinearVelocity().y > -0.01f;
    }

    @Override
    public void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        right = true;
    }

    public boolean isFacingForward() {
        return !sprite.isHorizontalFlipped();
    }

    public int getLives() {
        return lives;
    }

    @Override
    protected float getSize() {
        return 1f;
    }

    public int getPickups() {
        return pickups;
    }

    public void addPickup() {
        this.pickups++;
    }
}
