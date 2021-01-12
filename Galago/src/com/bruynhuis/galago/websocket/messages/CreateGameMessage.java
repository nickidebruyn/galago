package com.bruynhuis.galago.websocket.messages;

import com.jme3.math.Vector3f;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateGameMessage implements Serializable {

    private String gameName;
    private String playerName;
    private int playerType;
    private List<Vector3f> spawnPoints = new ArrayList<>();
    public float gameUpdateRate = 5f;
    public float entityUpdateRate = 5f;
    public float playerUpdateRate = 5f;

    public CreateGameMessage(String gameName, String playerName, int playerType) {
        this.gameName = gameName;
        this.playerName = playerName;
        this.playerType = playerType;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public List<Vector3f> getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(List<Vector3f> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerType() {
        return playerType;
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }

    public float getGameUpdateRate() {
        return gameUpdateRate;
    }

    public void setGameUpdateRate(float gameUpdateRate) {
        this.gameUpdateRate = gameUpdateRate;
    }

    public float getEntityUpdateRate() {
        return entityUpdateRate;
    }

    public void setEntityUpdateRate(float entityUpdateRate) {
        this.entityUpdateRate = entityUpdateRate;
    }

    public float getPlayerUpdateRate() {
        return playerUpdateRate;
    }

    public void setPlayerUpdateRate(float playerUpdateRate) {
        this.playerUpdateRate = playerUpdateRate;
    }

}
