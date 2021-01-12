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
public class ObjectDamageMessage extends AbstractMessage {

    private String objectId;
    private String gameId;
    private int damage;

    public ObjectDamageMessage() {
    }

    public ObjectDamageMessage(String objectId, String gameId, int damage) {
        this.objectId = objectId;
        this.gameId = gameId;
        this.damage = damage;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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
