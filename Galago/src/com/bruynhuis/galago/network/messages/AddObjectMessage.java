/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.messages;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author NideBruyn
 */
@Serializable
public class AddObjectMessage extends AbstractMessage {

    private String objectName;
    private String gameId;
    private int objectType;
    private Vector3f position;
    private Quaternion rotation;
    private int collisionType;
    private float mass;
    private float radius;
    private boolean sensor;
    private Vector3f initialForce = new Vector3f(0, 0, 0);
    private Vector3f initialGravity = new Vector3f(0, -10, 0);
    private Vector3f halfExtends = Vector3f.ZERO;
    private Vector3f positionLock = new Vector3f(1, 1, 1);
    private Vector3f rotationLock = new Vector3f(1, 1, 1);
    private float friction = 0.5f;
    private float restitution = 0.5f;
    private int health = 0;
    private int collisionGroup = PhysicsCollisionObject.COLLISION_GROUP_01;
    private int collideWithGroups = PhysicsCollisionObject.COLLISION_GROUP_01;

    public AddObjectMessage() {
    }

    public AddObjectMessage(String objectName, String gameId, int collisionType, float radius, float mass) {
        this.objectName = objectName;
        this.gameId = gameId;
        this.collisionType = collisionType;
        this.radius = radius;
        this.mass = mass;
        
    }
    
    public AddObjectMessage(String objectName, String gameId, int collisionType, Vector3f halfExt, float mass) {
        this.objectName = objectName;
        this.gameId = gameId;
        this.collisionType = collisionType;
        this.halfExtends = halfExt;
        this.mass = mass;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
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
