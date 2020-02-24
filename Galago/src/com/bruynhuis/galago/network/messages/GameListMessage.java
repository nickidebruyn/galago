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
public class GameListMessage extends AbstractMessage {
    
    private NetworkGameMessage[] games;    

    public GameListMessage() {
    }

    public NetworkGameMessage[] getGames() {
        return games;
    }

    public void setGames(NetworkGameMessage[] games) {
        this.games = games;
    }


    
}
