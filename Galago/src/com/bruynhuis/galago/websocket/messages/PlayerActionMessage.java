/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.websocket.messages;

import java.io.Serializable;

/**
 *
 * @author NideBruyn
 */
public class PlayerActionMessage implements Serializable {

    private String playerId;
    private String gameId;
    private int action;

    public PlayerActionMessage(String playerId, String gameId, int action) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.action = action;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    

}
