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
public class PlayerJoinedMessage implements Serializable {
    
    private String gameId;
    private String playerId;
    private String playerName;
    private int type;
    private Vector3f location;

    public PlayerJoinedMessage(String gameId, String playerId, int type) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.type = type;
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

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    
    
}
