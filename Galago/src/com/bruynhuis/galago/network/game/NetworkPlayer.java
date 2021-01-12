/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.game;

import com.bruynhuis.galago.network.messages.CollisionType;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author NideBruyn
 */
public class NetworkPlayer implements PhysicsTickListener, PhysicsCollisionListener {

    private boolean networkClient;
    private int playerId;
    private int playerType;
    private String playerName;
    private Vector3f position;
    private Quaternion rotation;
    private int collisionType;
    private float mass;
    private float radius;
    private float friction = 0f;
    private float restitution = 0.5f;
    private Vector3f halfExtends;
    private Vector3f initialPosition;
    private Vector3f initialForce;
    private Vector3f initialViewDirection;
    private Vector3f initialGravity;
    private NetworkGame networkGame;
    private Node playerNode;
    private PhysicsRigidBody physicsRigidBody;
    private NetworkCharacterControl networkCharacterControl;
    private Vector3f positionLock;
    private Vector3f rotationLock;
    private int health = 1;
    private int score;
    private int loot;
    private boolean killed;
    private boolean active;
    private int collisionGroup = PhysicsCollisionObject.COLLISION_GROUP_01;
    private int collideWithGroups = PhysicsCollisionObject.COLLISION_GROUP_01;
    private String state;

    public NetworkPlayer(NetworkGame networkGame, int playerId, String playerName, Vector3f position, Quaternion rotation) {
        this.networkGame = networkGame;
        this.playerId = playerId;
        this.playerName = playerName;
        this.position = position;
        this.rotation = rotation;
        this.initialPosition = position.clone();
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public Vector3f getInitialPosition() {
        return initialPosition;
    }

    public int getPlayerType() {
        return playerType;
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }

    public NetworkGame getNetworkGame() {
        return networkGame;
    }

    public PhysicsRigidBody getPhysicsRigidBody() {
        return physicsRigidBody;
    }

    public void setPhysicsRigidBody(PhysicsRigidBody physicsRigidBody) {
        this.physicsRigidBody = physicsRigidBody;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void load() {
        playerNode = new Node(playerId + "");
        networkGame.getGameNode().attachChild(playerNode);

        if (networkGame.isPhysicsEnabled()) {

            RigidBodyControl rigidBody = null;
            if (collisionType == CollisionType.TYPE_SPHERE) {
                rigidBody = new RigidBodyControl(new SphereCollisionShape(getRadius()),
                        getMass());

            } else if (getCollisionType() == CollisionType.TYPE_BOX) {
                rigidBody = new RigidBodyControl(new BoxCollisionShape(getHalfExtends()),
                        getMass());

            } else if (getCollisionType() == CollisionType.TYPE_CHARACTER) {
                NetworkCharacterControl characterControl = new NetworkCharacterControl(getHalfExtends().x, getHalfExtends().y, getMass());
                setNetworkCharacterControl(characterControl);
//                rigidBody = characterControl.getPhysicsRigidBody();
                playerNode.addControl(characterControl);
                networkGame.getBulletAppState().getPhysicsSpace().addCollisionListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().addTickListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().add(characterControl);
                characterControl.warp(getPosition().clone());
                characterControl.setViewDirection(initialViewDirection);

                if (initialForce != null) {
                    characterControl.getPhysicsRigidBody().setLinearVelocity(initialForce.clone());
                }
                if (initialGravity != null) {
                    characterControl.getPhysicsRigidBody().setGravity(initialGravity.clone());

                }

                characterControl.getPhysicsRigidBody().setFriction(friction);
                characterControl.getPhysicsRigidBody().setRestitution(restitution);
                characterControl.getPhysicsRigidBody().setCollisionGroup(collisionGroup);
                characterControl.getPhysicsRigidBody().setCollideWithGroups(collideWithGroups);

            }

            if (rigidBody != null) {
                playerNode.addControl(rigidBody);
                networkGame.getBulletAppState().getPhysicsSpace().addCollisionListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().add(rigidBody);
                networkGame.getBulletAppState().getPhysicsSpace().addTickListener(this);
                rigidBody.setPhysicsLocation(getPosition().clone());
                rigidBody.setPhysicsRotation(getRotation().clone());
                setPhysicsRigidBody(rigidBody);

                if (initialForce != null) {
                    rigidBody.setLinearVelocity(initialForce.clone());
                }
                if (initialGravity != null) {
                    rigidBody.setGravity(initialGravity.clone());

                }

                rigidBody.setFriction(friction);
                rigidBody.setRestitution(restitution);
                rigidBody.setCollisionGroup(collisionGroup);
                rigidBody.setCollideWithGroups(collideWithGroups);
            }

            networkGame.log("Loaded player physics");
        }

        active = true;
    }

    public void close() {
        //TODO: Handle closing 
        playerNode.removeFromParent();

        if (networkGame.isPhysicsEnabled()) {
            if (physicsRigidBody != null) {
                networkGame.getBulletAppState().getPhysicsSpace().remove(physicsRigidBody);
                networkGame.getBulletAppState().getPhysicsSpace().removeTickListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().removeCollisionListener(this);

            } else if (networkCharacterControl != null) {
                networkGame.getGameNode().removeControl(networkCharacterControl);
                networkGame.getBulletAppState().getPhysicsSpace().remove(networkCharacterControl);
                networkGame.getBulletAppState().getPhysicsSpace().removeTickListener(this);
                networkGame.getBulletAppState().getPhysicsSpace().removeCollisionListener(this);

            }

        }

        active = false;

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
        if (positionLock != null) {
            if (physicsRigidBody != null) {
                physicsRigidBody.setPhysicsLocation(physicsRigidBody.getPhysicsLocation().multLocal(positionLock));

            } else if (networkCharacterControl != null) {
                networkCharacterControl.getPhysicsRigidBody().setPhysicsLocation(networkCharacterControl.getPhysicsRigidBody().getPhysicsLocation().multLocal(positionLock));
            }
        }

        if (rotationLock != null) {

            if (physicsRigidBody != null) {
                Quaternion q = physicsRigidBody.getPhysicsRotation();
                float angles[] = q.toAngles(null);
                angles[0] = angles[0] * rotationLock.x;
                angles[1] = angles[1] * rotationLock.y;
                angles[2] = angles[2] * rotationLock.z;

                q.fromAngles(angles);

                physicsRigidBody.setPhysicsRotation(q);

            } else if (networkCharacterControl != null) {
                Quaternion q = networkCharacterControl.getPhysicsRigidBody().getPhysicsRotation();
                float angles[] = q.toAngles(null);
                angles[0] = angles[0] * rotationLock.x;
                angles[1] = angles[1] * rotationLock.y;
                angles[2] = angles[2] * rotationLock.z;

                q.fromAngles(angles);

                networkCharacterControl.getPhysicsRigidBody().setPhysicsRotation(q);

            }

        }
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {

    }

    public NetworkCharacterControl getNetworkCharacterControl() {
        return networkCharacterControl;
    }

    public void setNetworkCharacterControl(NetworkCharacterControl networkCharacterControl) {
        this.networkCharacterControl = networkCharacterControl;
    }

    public Node getPlayerNode() {
        return playerNode;
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {

        if (event.getNodeA() != null && event.getNodeA().getName().equals(playerId + "")) {
//            System.out.println("Found collision with object B: " + event.getNodeB().getName());
            NetworkObject object = networkGame.getObjects().get(event.getNodeB().getName());
            if (object != null) {
                networkGame.getApplication().broadcastPlayerCollision(networkGame, this, object);
            } else if (event.getNodeB().getName().length() < 4) {
                NetworkPlayer player = networkGame.getPlayers().get(Integer.parseInt(event.getNodeB().getName()));
                if (player != null) {
                    networkGame.getApplication().broadcastPlayerWithPlayerCollision(networkGame, this, player);
                }
            }

        } else if (event.getNodeB() != null && event.getNodeB().getName().equals(playerId + "")) {
//            System.out.println("Found collision with object A: " + event.getNodeA().getName());
            NetworkObject object = networkGame.getObjects().get(event.getNodeA().getName());
            if (object != null) {
                networkGame.getApplication().broadcastPlayerCollision(networkGame, this, object);

            } else if (event.getNodeA().getName().length() < 4) {
                NetworkPlayer player = networkGame.getPlayers().get(Integer.parseInt(event.getNodeA().getName()));
                if (player != null) {
                    networkGame.getApplication().broadcastPlayerWithPlayerCollision(networkGame, this, player);
                }
            }

        }
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
    
    public void addScore(int s) {
        this.score = score + s;
    }

    public void doKill() {
        this.health = 0;
        this.killed = true;

        if (physicsRigidBody != null) {
            //TODO
            ((RigidBodyControl) physicsRigidBody).setEnabled(false);

        } else if (networkCharacterControl != null) {
            networkCharacterControl.setEnabled(false);
        }
    }

    public void respawn() {
        this.killed = false;
        this.active = true;

        if (physicsRigidBody != null) {
            physicsRigidBody.clearForces();
            ((RigidBodyControl) physicsRigidBody).setEnabled(true);
            physicsRigidBody.setPhysicsLocation(getPosition().clone());
            physicsRigidBody.setPhysicsRotation(getRotation().clone());

        } else if (networkCharacterControl != null) {
            networkCharacterControl.setEnabled(true);
            networkCharacterControl.getPhysicsRigidBody().clearForces();
            networkCharacterControl.getPhysicsRigidBody().setPhysicsLocation(getPosition().clone());
            networkCharacterControl.getPhysicsRigidBody().setPhysicsRotation(getRotation().clone());
            networkCharacterControl.setViewDirection(new Vector3f(0, 0, 0));
            networkCharacterControl.setWalkDirection(new Vector3f(0, 0, 0));
        }
    }

    public int getLoot() {
        return loot;
    }

    public void setLoot(int loot) {
        this.loot = loot;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public boolean isNetworkClient() {
        return networkClient;
    }

    public void setNetworkClient(boolean networkClient) {
        this.networkClient = networkClient;
    }

    public Vector3f getInitialViewDirection() {
        return initialViewDirection;
    }

    public void setInitialViewDirection(Vector3f initialViewDirection) {
        this.initialViewDirection = initialViewDirection;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
