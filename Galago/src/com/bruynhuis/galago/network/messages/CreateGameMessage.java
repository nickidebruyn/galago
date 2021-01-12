/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NideBruyn
 */
@Serializable
public class CreateGameMessage extends AbstractMessage {

    private String gameName;
    private List<Vector3f> spawnPoints = new ArrayList<>();
    private boolean physicsEnabled;
    private Vector3f gravity;
    private boolean randomSpawnPoint;
    private boolean keepOpen;

    public CreateGameMessage() {
    }

    public CreateGameMessage(String gameName) {
        this.gameName = gameName;

    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public List<Vector3f> getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(List<Vector3f> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public boolean isPhysicsEnabled() {
        return physicsEnabled;
    }

    public void setPhysicsEnabled(boolean physicsEnabled) {
        this.physicsEnabled = physicsEnabled;
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void setGravity(Vector3f gravity) {
        this.gravity = gravity;
    }

    public boolean isRandomSpawnPoint() {
        return randomSpawnPoint;
    }

    public void setRandomSpawnPoint(boolean randomSpawnPoint) {
        this.randomSpawnPoint = randomSpawnPoint;
    }

    public boolean isKeepOpen() {
        return keepOpen;
    }

    public void setKeepOpen(boolean keepOpen) {
        this.keepOpen = keepOpen;
    }

}
