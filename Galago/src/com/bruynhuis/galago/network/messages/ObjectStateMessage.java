/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.messages;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author NideBruyn
 */
@Serializable
public class ObjectStateMessage extends AbstractMessage {

    private int objectType;
    private String objectId;
    private String objectName;
    private String gameId;
    private Vector3f position;
    private Quaternion rotation;
    private boolean destroyed;
    private int health = 1;
    private Vector3f halfExtends = Vector3f.ZERO;
    private float radius;

    public ObjectStateMessage() {
    }

    public ObjectStateMessage(String objectId, String objectName, String gameId) {
        this.objectId = objectId;
        this.objectName = objectName;
        this.gameId = gameId;
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public String toString() {
        return "ObjectStateMessage{" + "objectType=" + objectType + ", objectId=" + objectId + ", objectName=" + objectName + ", gameId=" + gameId + ", position=" + position + ", rotation=" + rotation + '}';
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Vector3f getHalfExtends() {
        return halfExtends;
    }

    public void setHalfExtends(Vector3f halfExtends) {
        this.halfExtends = halfExtends;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

}
