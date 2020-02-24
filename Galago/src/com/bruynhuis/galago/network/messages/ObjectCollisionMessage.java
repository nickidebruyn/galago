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
public class ObjectCollisionMessage extends AbstractMessage {

    private String objectId;
    private String gameId;
    private String collisionObjectId;
    private int collisionObjectType;

    public ObjectCollisionMessage() {
    }

    public ObjectCollisionMessage(String objectId, String gameId, String collisionObjectId, int collisionObjectType) {
        this.objectId = objectId;
        this.gameId = gameId;
        this.collisionObjectId = collisionObjectId;
        this.collisionObjectType = collisionObjectType;
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

    public String getCollisionObjectId() {
        return collisionObjectId;
    }

    public void setCollisionObjectId(String collisionObjectId) {
        this.collisionObjectId = collisionObjectId;
    }

    public int getCollisionObjectType() {
        return collisionObjectType;
    }

    public void setCollisionObjectType(int collisionObjectType) {
        this.collisionObjectType = collisionObjectType;
    }



}
