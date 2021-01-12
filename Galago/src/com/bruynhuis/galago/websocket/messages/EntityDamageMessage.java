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
public class EntityDamageMessage implements Serializable {

    private String id;
    private String gameId;
    private int damage;

    public EntityDamageMessage(String id, String gameId, int damage) {
        this.id = id;
        this.gameId = gameId;
        this.damage = damage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

}
