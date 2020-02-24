/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author NideBruyn
 */
@Serializable
public class JoinGameMessage extends AbstractMessage {

    private String playerName;
    private String gameId;
    private int playerType;
    private int collisionType;
    private float mass;
    private float radius;
    private int health;
    private int score;
    private int loot;
    private Vector3f halfExtends = Vector3f.ZERO;
    private Vector3f positionLock = new Vector3f(1, 1, 1);
    private Vector3f rotationLock = new Vector3f(1, 1, 1);
    private Vector3f initialForce = new Vector3f(0, 0, 0);
    private Vector3f initialGravity = new Vector3f(0, -10, 0);
    private float friction = 0.0f;
    private float restitution = 0.5f;

    public JoinGameMessage() {
    }

    public JoinGameMessage(String playerName, String gameId, int playerType) {
        this.playerName = playerName;
        this.gameId = gameId;
        this.playerType = playerType;
    }

    public JoinGameMessage(String playerName, String gameId, int playerType, int collisionType, float radius, float mass) {
        this.playerName = playerName;
        this.gameId = gameId;
        this.playerType = playerType;
        this.collisionType = collisionType;
        this.radius = radius;
        this.mass = mass;
    }
    
    public JoinGameMessage(String playerName, String gameId, int playerType, int collisionType, Vector3f halfExt, float mass) {
        this.playerName = playerName;
        this.gameId = gameId;
        this.playerType = playerType;
        this.collisionType = collisionType;
        this.halfExtends = halfExt;
        this.mass = mass;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getPlayerType() {
        return playerType;
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLoot() {
        return loot;
    }

    public void setLoot(int loot) {
        this.loot = loot;
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

}
