/*
 * Copyright (c) 2009-2014 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.bruynhuis.galago.sprite.physics;

import com.bruynhuis.galago.sprite.physics.joint.PhysicsJoint;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.sprite.physics.vehicle.Vehicle;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.*;
import org.dyn4j.world.listener.CollisionListenerAdapter;
import org.dyn4j.world.listener.StepListenerAdapter;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author nidebruyn
 */
public class PhysicsSpace {

    private static final Logger logger = Logger.getLogger(PhysicsSpace.class.getName());
    private static final float DEFAULT_SPEED = 1f;

    private World physicsWorld;
    protected float speed = DEFAULT_SPEED;

    protected CollisionListenerAdapter collisionAdapter;

    protected StepListenerAdapter stepAdapter;
    protected ArrayList<PhysicsCollisionListener> collisionListeners = new ArrayList<>();
    protected ArrayList<PhysicsTickListener> tickListeners = new ArrayList<>();
    protected ArrayList<PhysicsJoint> physicsJoints = new ArrayList<>();
    protected ArrayList<Vehicle> vehicles = new ArrayList<>();
    protected Integer initialCapacity;

    protected Integer initialJointCapacity;
    protected Bounds bounds;

    public PhysicsSpace() {
        init();

    }

    public PhysicsSpace(final Integer initialCapacity, final Integer initialJointCapacity, final Bounds bounds) {
        this.initialCapacity = initialCapacity;
        this.initialJointCapacity = initialJointCapacity;
        this.bounds = bounds;

        init();

    }

    private void init() {
        if (initialCapacity != null && initialJointCapacity != null) {
            this.physicsWorld = new World<>(initialCapacity, initialJointCapacity);
        } else {
            this.physicsWorld = new World<>();
        }
        if (bounds != null) {
            this.physicsWorld.setBounds(bounds);
        }

        collisionAdapter = new CollisionListenerAdapter() {
            @Override
            public boolean collision(BroadphaseCollisionData collision) {
                return super.collision(collision);
            }

            @Override
            public boolean collision(NarrowphaseCollisionData collision) {
                return super.collision(collision);
            }

            @Override
            public boolean collision(ManifoldCollisionData collision) {
                if (collision.getBody1() != null && collision.getBody2() != null) {
                    Vector3f collisionPoint = null;
                    Manifold manifold = collision.getManifold();

                    if (manifold.getPoints() != null && manifold.getPoints().size() > 0) {
                        ManifoldPoint mp = manifold.getPoints().get(0);
                        collisionPoint = new Vector3f((float) mp.getPoint().x, (float) mp.getPoint().y, (float) mp.getDepth());
                    }
                    fireCollisionListenerEvent(collision.getBody1(), collision.getFixture1(), collision.getBody2(), collision.getFixture2(), collisionPoint);
                    return true;

                } else {
                    return false;
                }
            }

//      @Override
//      public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Manifold manifold) {
//        if (body1 != null && body2 != null) {
//          Vector3f collisionPoint = null;
//          if (manifold.getPoints() != null && manifold.getPoints().size() > 0) {
//            ManifoldPoint mp = manifold.getPoints().get(0);
//            collisionPoint = new Vector3f((float) mp.getPoint().x, (float) mp.getPoint().y, (float) mp.getDepth());
//          }
//          fireCollisionListenerEvent(body1, fixture1, body2, fixture2, collisionPoint);
//          return true;
//
//        } else {
//          return false;
//        }
//      }
        };

        physicsWorld.addCollisionListener(collisionAdapter);

        stepAdapter = new StepListenerAdapter() {

            @Override
            public void begin(TimeStep step, PhysicsWorld world) {
                firePreTickListenerEvent(step);
            }

            @Override
            public void updatePerformed(TimeStep step, PhysicsWorld world) {
                super.updatePerformed(step, world);
            }

            @Override
            public void postSolve(TimeStep step, PhysicsWorld world) {
                super.postSolve(step, world);
            }

            @Override
            public void end(TimeStep step, PhysicsWorld world) {
                fireTickListenerEvent(step);
            }

        };

        physicsWorld.addStepListener(stepAdapter);

//    physicsWorld.getSettings().setContinuousDetectionMode(ContinuousDetectionMode.NONE);
//        physicsWorld.getSettings().setSleepTime(speed);
    }

    public int getBodyCount() {
        return this.physicsWorld.getBodyCount();
    }

    public void add(Object obj) {
        if (obj instanceof PhysicsControl) {
            ((PhysicsControl) obj).setPhysicsSpace(this);
            //Only add if it doesn't already contain the body
            if (!this.physicsWorld.containsBody(((PhysicsControl) obj).getBody())) {
                this.physicsWorld.addBody(((PhysicsControl) obj).getBody());
            }

        } else if (obj instanceof Spatial) {
            Spatial node = (Spatial) obj;
            PhysicsControl control = node.getControl(PhysicsControl.class);
            //Only add if it doesn't already contain the body
            if (!this.physicsWorld.containsBody(control.getBody())) {
                control.setPhysicsSpace(this);
                this.physicsWorld.addBody(control.getBody());
            }

        } else {
            throw (new UnsupportedOperationException("Cannot add this kind of object to the physics space."));
        }
    }

    public void remove(Object obj) {
        if (obj instanceof PhysicsControl) {
            this.physicsWorld.removeBody(((PhysicsControl) obj).getBody());
            ((PhysicsControl) obj).setPhysicsSpace(null);

        } else if (obj instanceof Spatial) {
            Spatial node = (Spatial) obj;
            PhysicsControl control = node.getControl(PhysicsControl.class);
            this.physicsWorld.removeBody(control.getBody());
            control.setPhysicsSpace(null);

        } else {
            throw (new UnsupportedOperationException("Cannot remove this kind of object from the physics space."));
        }
    }

    public void addJoint(final PhysicsJoint joint) {
        this.physicsWorld.addJoint(joint.getJoint());
        this.physicsJoints.add(joint);
    }

    public boolean removeJoint(final PhysicsJoint joint) {
        this.physicsJoints.remove(joint);
        return this.physicsWorld.removeJoint(joint.getJoint());
    }

    public void addVehicle(final Vehicle vehicle) {
        this.physicsWorld.addJoint(vehicle.getFrontWheelJoint());
        this.physicsWorld.addJoint(vehicle.getRearWheelJoint());
        this.vehicles.add(vehicle);

    }

    public void updateFixed(final float elapsedTime) {
        this.physicsWorld.update(elapsedTime);
    }

    public void clear() {
        Vector2 gravity = this.physicsWorld.getGravity().copy();
        this.physicsWorld = null;
        this.collisionListeners.clear();
        this.tickListeners.clear();
        this.init();
        this.setGravity(gravity.x, gravity.y);
    }

    public void setGravity(final Vector2f gravity) {
        this.physicsWorld.setGravity(new Vector2(gravity.x, gravity.y));
    }

    public void setGravity(final double x, final double y) {
        this.physicsWorld.setGravity(new Vector2(x, y));
    }

    public Vector3f getGravity() {
        return Converter.toVector3f(this.physicsWorld.getGravity());
    }

    public void setSpeed(final float speed) {
        this.speed = speed;
        this.physicsWorld.getSettings().setMinimumAtRestTime(Settings.DEFAULT_STEP_FREQUENCY * speed);
    }

    public float getSpeed() {
        return this.speed;
    }

    public World getPhysicsWorld() {
        return this.physicsWorld;
    }

    public void addPhysicsCollisionListener(PhysicsCollisionListener collisionListener) {
        this.collisionListeners.add(collisionListener);
    }

    public void removePhysicsCollisionListener(PhysicsCollisionListener collisionListener) {
        this.collisionListeners.add(collisionListener);
    }

    public void addPhysicsTickListener(PhysicsTickListener tickListener) {
        this.tickListeners.add(tickListener);
    }

    public void removePhysicsTickListener(PhysicsTickListener tickListener) {
        this.tickListeners.add(tickListener);
    }

    protected void fireCollisionListenerEvent(CollisionBody body1, Fixture fixture1, CollisionBody body2, Fixture fixture2, Vector3f collisionPoint) {
        for (int i = 0; i < collisionListeners.size(); i++) {
            PhysicsCollisionListener physicsCollisionListener = collisionListeners.get(i);
            Spatial spatialA = (Spatial) body1.getUserData();
            Spatial spatialB = (Spatial) body2.getUserData();

            if (spatialA != null && spatialB != null && fixture1 != null && fixture2 != null) {
                physicsCollisionListener.collision(spatialA, (CollisionShape) fixture1.getUserData(), spatialB, (CollisionShape) fixture2.getUserData(), collisionPoint);
            }
        }
    }

    protected void firePreTickListenerEvent(TimeStep step) {
        for (int i = 0; i < tickListeners.size(); i++) {
            PhysicsTickListener physicsTickListener = tickListeners.get(i);
            physicsTickListener.prePhysicsTick(this, (float) step.getDeltaTime());
        }
    }

    protected void fireTickListenerEvent(TimeStep step) {
        for (int i = 0; i < tickListeners.size(); i++) {
            PhysicsTickListener physicsTickListener = tickListeners.get(i);
            physicsTickListener.physicsTick(this, (float) step.getDeltaTime());
        }
    }
}
