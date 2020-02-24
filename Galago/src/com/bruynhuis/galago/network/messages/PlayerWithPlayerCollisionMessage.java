/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author NideBruyn
 */
@Serializable
public class PlayerWithPlayerCollisionMessage extends AbstractMessage {

    private int playerId;
    private String gameId;
    private int collisionPlayerId;

    public PlayerWithPlayerCollisionMessage() {
    }

    public PlayerWithPlayerCollisionMessage(int playerId, String gameId, int collisionPlayerId) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.collisionPlayerId = collisionPlayerId;

    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getCollisionPlayerId() {
        return collisionPlayerId;
    }


    public void setCollisionPlayerId(int collisionPlayerId) {
        this.collisionPlayerId = collisionPlayerId;
    }

}
