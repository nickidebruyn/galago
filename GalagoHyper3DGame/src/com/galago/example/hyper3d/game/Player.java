/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper3d.game;

import com.bruynhuis.galago.control.effects.LineControl;
import com.bruynhuis.galago.control.effects.TrailControl;
import com.bruynhuis.galago.games.physics.PhysicsGame;
import com.bruynhuis.galago.games.physics.PhysicsPlayer;
import com.bruynhuis.galago.util.SpatialUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nicki
 */
public class Player extends PhysicsPlayer implements PhysicsTickListener {

    private Spatial model;
    private RigidBodyControl rbc;

    //Player settings
    private float killPosition = 10f;
    private float playerRadius = 0.35f;
    private float speed = 0;
    private float speedMax = 220f;
    private float accel = 100;
    private float jumpForce = 0;
    private float jumpForceMax = 10;
    private float jumpForceAccel = 100;
    private Vector3f nextPosition = new Vector3f(0, 0, 0);
    private Vector3f nextLinearVel = new Vector3f(0, 0, 0);
    private Vector3f nextAngularVel = new Vector3f(0, 0, 0);

    //Trail settings
    private float width = 0.1f;
    private float endWidth = 0.05f;
    private float lifetime = 0.4f;
    private float widthFactor = 1f;

    //Onground indicator
    private boolean onGround = false;
    private boolean prepareJump = false;
    private boolean jump = false;
    private Timer jumpDelayTimer = new Timer(20);

    public Player(PhysicsGame physicsGame) {
        super(physicsGame);
    }

    @Override
    protected void init() {

        lives = 0;

        model = SpatialUtils.addSphere(playerNode, 28, 28, playerRadius);
        SpatialUtils.addMaterial(model, game.getBaseApplication().getModelManager().getMaterial("Materials/ball.j3m"));
        

        rbc = new RigidBodyControl(new SphereCollisionShape(playerRadius), 10);
        game.getBaseApplication().getBulletAppState().getPhysicsSpace().add(rbc);
        playerNode.addControl(rbc);

        SpatialUtils.move(playerNode, 0, 1, 0);

        game.getBaseApplication().getBulletAppState().getPhysicsSpace().addTickListener(this);

        playerNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (game.isStarted() && !game.isPaused() && !game.isGameover()) {

                    if (getPosition().y < -killPosition || getPosition().y > killPosition) {
                        doDamage(10);
                    }

                    jumpDelayTimer.update(tpf);

                    //When the jump is prepared
                    if (prepareJump) {
                        if (jumpForce < jumpForceMax) {
                            jumpForce += tpf * jumpForceAccel;
                        }
                    }

//                    log("vel=" + rbc.getLinearVelocity());
                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });
    }

    @Override
    public Vector3f getPosition() {
        return rbc.getPhysicsLocation().clone();
    }

    @Override
    public void doDie() {
        //TODO
    }

    @Override
    public void start() {

        rbc.setFriction(0.0f);
        rbc.setRestitution(0.0f);
//        rbc.setKinematicSpatial(false);
        rbc.setGravity(new Vector3f(0, -16, 0));
        rbc.setLinearSleepingThreshold(0f);
        rbc.setAngularSleepingThreshold(0f);
//        rbc.setAngularDamping(0f);
//        rbc.setLinearDamping(0f);
//        rbc.setLinearVelocity(new Vector3f(speed, 0, 0));

//        loadTrail();
//        loadParticleTrail();
        jumpDelayTimer.start();
    }

    @Override
    public void close() {
        game.getBaseApplication().getBulletAppState().getPhysicsSpace().removeTickListener(this);
        super.close();
    }

    public void prepareJump() {
        prepareJump = true;

    }

    public void jump() {
//        log("Jump");
        jump = true;

    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
//        this.rbc.setLinearVelocity(new Vector3f(speed, rbc.getLinearVelocity().y, 0));
        
//        rbc.setFriction(0.0f);
//        rbc.setRestitution(0.0f);
        
//        log("Speed = " + speed);
        //Accelerate the player speed
        if (this.speed < this.speedMax) {
            this.speed += tpf * accel;
        }

        //Check for jump actions
        if (jump) {
            if (isOnGround()) {
                setOnGround(false);
//                this.rbc.clearForces();
                log("Jump with force: " + jumpForce);
                this.rbc.setLinearVelocity(new Vector3f(this.rbc.getLinearVelocity().x, jumpForce, 0));
//                this.rbc.applyImpulse(new Vector3f(0, jumpForce, 0), new Vector3f(0, 0, 0));
                this.jumpDelayTimer.start();

            }

            jumpForce = 0;
            prepareJump = false;
            jump = false;
        }
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
//        SpatialUtils.move(playerNode, tpf * speed, 0, 0);
//        log("Velocity y= " + rbc.getLinearVelocity().y);

//        nextPosition.set(rbc.getPhysicsLocation().x + (speed * tpf), rbc.getPhysicsLocation().y, 0);
//        rbc.setPhysicsLocation(nextPosition);

        nextAngularVel.set(0, 0, -tpf*speed*2);
        nextLinearVel.set(tpf*speed, rbc.getLinearVelocity().y, 0);

        rbc.setAngularVelocity(nextAngularVel);        
        rbc.setLinearVelocity(nextLinearVel);

    }

    private void loadParticleTrail() {
        Spatial trail = game.getBaseApplication().getAssetManager().loadModel("Models/particle-trail.j3o");
        playerNode.attachChild(trail);
    }

    private void loadTrail() {
        Material trailMat = game.getBaseApplication().getAssetManager().loadMaterial("Materials/trail.j3m");

        Geometry trailGeometry = new Geometry();
//        trailMat.getAdditionalRenderState().setAlphaTest(true);
//        trailMat.getAdditionalRenderState().setAlphaFallOff(0.5f);
        trailGeometry.setMaterial(trailMat);
        //rootNode.attachChild(trail);  // either attach the trail geometry node to the root…
        trailGeometry.setIgnoreTransform(true); // or set ignore transform to true. this should be most useful when attaching nodes in the editor
        //trailGeometry.setQueueBucket(RenderQueue.Bucket.Translucent);

        LineControl line = new LineControl(new LineControl.Algo2CamPosBBNormalized(), true);
        trailGeometry.addControl(line);
        TrailControl trailControl = new TrailControl(line);
        playerNode.addControl(trailControl);
        trailControl.setStartWidth(this.endWidth * this.widthFactor);
        trailControl.setEndWidth(this.width * this.widthFactor);
        trailControl.setLifeSpan(this.lifetime);
//        trailControl.setSegmentLength(0.2f);

        playerNode.attachChild(trailGeometry);
        trailGeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        if (jumpDelayTimer.finished()) {
            this.onGround = onGround;
        }

    }

}
