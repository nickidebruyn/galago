/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.websocket.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NideBruyn
 */
public class GameListMessage implements Serializable {

    private List<GameStateMessage> games = new ArrayList<>();

    public List<GameStateMessage> getGames() {
        return games;
    }

    public void setGames(List<GameStateMessage> games) {
        this.games = games;
    }

}
