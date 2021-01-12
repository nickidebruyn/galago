/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.game;

import com.bruynhuis.galago.network.messages.CollisionType;
import com.bruynhuis.galago.util.Timer;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class NetworkObject implements PhysicsTickListener, PhysicsCollisionListener {

    private String id;
    private int type;
    private String name;
    private Vector3f position;
    private Quaternion rotation;
    private int collisionType;
    private float mass;
    private float radius;
    private Vector3f initialForce;
    private Vector3f initialGravity;
    private Vector3f halfExtends;
    private NetworkGame networkGame;
    private Node objectNode;
    private RigidBodyControl rigidBodyControl;
    private GhostControl ghostControl;
    private Vector3f positionLock;
    private Vector3f rotationLock;
    private Timer lifeTimeTimer;
    private boolean destroyed;
    private boolean sensor;
    private float friction = 0.5f;
    private float restitution = 0.5f;
    private int collisionGroup = PhysicsCollisionObject.COLLISION_GROUP_01;
    private int collideWithGroups = PhysicsCollisionObject.COLLISION_GROUP_01;
    private int health = 1;

    public NetworkObject(NetworkGame networkGame, String id, String name, Vector3f position, Quaternion rotation) {
        this.networkGame = networkGame;
        this.id = id;
        this.name = name;
        this.position = position;
        this.rotation = rotation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector3f getPosition() {
        if (rigidBodyControl != null) {
            return rigidBodyControl.getPhysicsLocation();

        } else if (ghostControl != null) {
            return ghostControl.getPhysicsLocation();
        }

        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternion getRotation() {
        if (rigidBodyControl != null) {
            return rigidBodyControl.getPhysicsRotation();

        } else if (ghostControl != null) {
            return ghostControl.getPhysicsRotation();
        }

        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public NetworkGame getNetworkGame() {
        return networkGame;
    }

    public RigidBodyControl getRigidBodyControl() {
        return rigidBodyControl;
    }

    public void setRigidBodyControl(RigidBodyControl rigidBodyControl) {
        this.rigidBodyControl = rigidBodyControl;
    }

    public void load() {
        objectNode = new Node(id);
        networkGame.getGameNode().attachChild(objectNode);

        if (networkGame.isPhysicsEnabled()) {

            RigidBodyControl rbc = null;
            GhostControl gc = null; //TODO: Have to code this as ghost control when sensor is set

            if (isSensor()) {
                if (getCollisionType() == CollisionType.TYPE_SPHERE) {
                    gc = new GhostControl(new SphereCollisionShape(getRadius()));

                } else if (getCollisionType() == CollisionType.TYPE_BOX) {
                    gc = new GhostControl(new BoxCollisionShape(getHalfExtends().clone()));

                }

            } else if (getCollisionType() == CollisionType.TYPE_SPHERE) {
                rbc = new RigidBodyControl(new SphereCollisionShape(getRadius()),
                        getMass());

            } else if (getCollisionType() == CollisionType.TYPE_BOX) {
                rbc = new RigidBodyControl(new BoxCollisionShape(getHalfExtends().clone()),
                        getMass());

            }

            if (rbc != null) {
                objectNode.addControl(rbc);
                networkGame.getBulletAppState().getPhysicsSpace().add(rbc);
                networkGame.getBulletAppState().getPhysicsSpace().addTickListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().addCollisionListener(this);
                rbc.setPhysicsLocation(getPosition().clone());
                rbc.setPhysicsRotation(getRotation().clone());
                setRigidBodyControl(rbc);

                if (initialForce != null) {
                    rbc.setLinearVelocity(initialForce);
                }
                if (initialGravity != null) {
                    rbc.setGravity(initialGravity);

                }

                rbc.setFriction(friction);
                rbc.setRestitution(restitution);
                rbc.setCollisionGroup(collisionGroup);
                rbc.setCollideWithGroups(collideWithGroups);

            } else if (gc != null) {
                objectNode.addControl(gc);
                networkGame.getBulletAppState().getPhysicsSpace().add(gc);
                networkGame.getBulletAppState().getPhysicsSpace().addTickListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().addCollisionListener(this);
                gc.setPhysicsLocation(getPosition().clone());
                gc.setPhysicsRotation(getRotation().clone());
                gc.setCollisionGroup(collisionGroup);
                gc.setCollideWithGroups(collideWithGroups);
                setGhostControl(gc);

            }

        }

        objectNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (lifeTimeTimer != null) {
                    lifeTimeTimer.update(tpf);

                    if (lifeTimeTimer.finished()) {
                        destroyed = true;
                        lifeTimeTimer.stop();
                    }
                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });

    }

    public GhostControl getGhostControl() {
        return ghostControl;
    }

    public void setGhostControl(GhostControl ghostControl) {
        this.ghostControl = ghostControl;
    }

    public void close() {
        //TODO: Handle closing 
        objectNode.removeFromParent();

        if (networkGame.isPhysicsEnabled()) {

            if (rigidBodyControl != null) {
                networkGame.getBulletAppState().getPhysicsSpace().removeTickListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().removeCollisionListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().remove(rigidBodyControl);

            } else if (ghostControl != null) {
                networkGame.getBulletAppState().getPhysicsSpace().removeTickListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().removeCollisionListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().remove(ghostControl);

            }

        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCollisionType() {
        return collisionType;
    }

    public void setCollisionType(int collisionType) {
        this.collisionType = collisionType;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector3f getHalfExtends() {
        return halfExtends;
    }

    public void setHalfExtends(Vector3f halfExtends) {
        this.halfExtends = halfExtends;
    }

    public Vector3f getPositionLock() {
        return positionLock;
    }

    public void setPositionLock(Vector3f positionLock) {
        this.positionLock = positionLock;
    }

    public Vector3f getRotationLock() {
        return rotationLock;
    }

    public void setRotationLock(Vector3f rotationLock) {
        this.rotationLock = rotationLock;
    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {

        if (rigidBodyControl != null) {

            if (positionLock != null) {
                rigidBodyControl.setPhysicsLocation(rigidBodyControl.getPhysicsLocation().multLocal(positionLock));
            }

            if (rotationLock != null) {
                Quaternion q = rigidBodyControl.getPhysicsRotation();
                float angles[] = q.toAngles(null);
                angles[0] = angles[0] * rotationLock.x;
                angles[1] = angles[1] * rotationLock.y;
                angles[2] = angles[2] * rotationLock.z;

                q.fromAngles(angles);

                rigidBodyControl.setPhysicsRotation(q);

            }
        }

    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }

    public Node getObjectNode() {
        return objectNode;
    }

    public void setObjectNode(Node objectNode) {
        this.objectNode = objectNode;
    }

    public Vector3f getInitialForce() {
        return initialForce;
    }

    public void setInitialForce(Vector3f initialForce) {
        this.initialForce = initialForce;
    }

    public Vector3f getInitialGravity() {
        return initialGravity;
    }

    public void setInitialGravity(Vector3f initialGravity) {
        this.initialGravity = initialGravity;
    }

    public Timer getLifeTimeTimer() {
        return lifeTimeTimer;
    }

    public void setLifeTimeTimer(Timer lifeTimeTimer) {
        this.lifeTimeTimer = lifeTimeTimer;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() != null && event.getNodeA().getName().equals(id)) {
//            System.out.println("Found collision with object B: " + event.getNodeB().getName());
            NetworkObject object = networkGame.getObjects().get(event.getNodeB().getName());
            if (object != null) {
                networkGame.getApplication().broadcastObjectCollision(networkGame, this, object);
            }

        } else if (event.getNodeB() != null && event.getNodeB().getName().equals(id)) {
//            System.out.println("Found collision with object A: " + event.getNodeA().getName());
            NetworkObject object = networkGame.getObjects().get(event.getNodeA().getName());
            if (object != null) {
                networkGame.getApplication().broadcastObjectCollision(networkGame, this, object);
            }

        }
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    public boolean isSensor() {
        return sensor;
    }

    public void setSensor(boolean sensor) {
        this.sensor = sensor;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void addDamage(int damage) {
        this.health = health - damage;

        if (this.health <= 0) {
            this.doKill();

        }
    }

    public void doKill() {
        this.health = 0;
        this.destroyed = true;

        if (rigidBodyControl != null) {
            ((RigidBodyControl) rigidBodyControl).setEnabled(false);

        }

        if (ghostControl != null) {
            ghostControl.setEnabled(false);

        }
    }

    public int getCollisionGroup() {
        return collisionGroup;
    }

    public void setCollisionGroup(int collisionGroup) {
        this.collisionGroup = collisionGroup;
    }

    public int getCollideWithGroups() {
        return collideWithGroups;
    }

    public void setCollideWithGroups(int collideWithGroups) {
        this.collideWithGroups = collideWithGroups;
    }
    
    
}
