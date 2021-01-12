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
public class ChangePlayerStateMessage extends AbstractMessage {

    private int playerId;
    private String gameId;
    private String state;

    public ChangePlayerStateMessage() {
    }

    public ChangePlayerStateMessage(int playerId, String gameId, String state) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }



}
