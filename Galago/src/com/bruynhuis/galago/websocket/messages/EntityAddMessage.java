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
public class EntityAddMessage implements Serializable {

    private String gameId;
    private String id;
    private int type;
    private Vector3f location;

    public EntityAddMessage(String gameId, String id, int type) {
        this.gameId = gameId;
        this.id = id;
        this.type = type;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
