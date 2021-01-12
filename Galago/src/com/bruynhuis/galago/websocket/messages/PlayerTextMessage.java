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
public class PlayerTextMessage implements Serializable {

    private String playerId;
    private String gameId;
    private String text;

    public PlayerTextMessage(String playerId, String gameId, String text) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    

}
