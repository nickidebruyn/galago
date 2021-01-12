/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.websocket.messages;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.io.Serializable;

/**
 *
 * @author NideBruyn
 */
public class EntityStateMessage implements Serializable {

    private String gameId;
    private String id;
    private Vector3f location;
    private Quaternion rotation;
    private boolean killed;
    private boolean active;
    private int state;
    private int type;
    private int health;

    public EntityStateMessage(String gameId, String id) {
        this.gameId = gameId;
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
