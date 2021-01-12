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
public class PlayerScoreMessage implements Serializable {

    private String playerId;
    private String gameId;
    private int score;

    public PlayerScoreMessage(String playerId, String gameId, int score) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.score = score;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    
}
