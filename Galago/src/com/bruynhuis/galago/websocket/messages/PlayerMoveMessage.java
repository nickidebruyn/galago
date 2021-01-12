/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.websocket.messages;

import com.jme3.math.Vector3f;
import java.io.Serializable;

/**
 *
 * @author NideBruyn
 */
public class PlayerMoveMessage implements Serializable {

    private String gameId;
    private String playerId;
    private Vector3f direction;
    private float speed = 1;

    public PlayerMoveMessage(String gameId, String playerId, Vector3f direction) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.direction = direction;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

}
