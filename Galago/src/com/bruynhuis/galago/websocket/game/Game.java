package com.bruynhuis.galago.websocket.game;

import com.bruynhuis.galago.util.Timer;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.JmeContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game extends SimpleApplication {

    private String id;
    private String name;
    private List<Vector3f> spawnPoints = new ArrayList<>();
    private boolean terminated;
    private boolean loading;
    private boolean started;
    private boolean gameover;
    private String winnerId;
    private Map<String, Player> players = new HashMap<>();
    private Map<String, Entity> entities = new HashMap<>();
    private long startTime;
    private GameListener gameListener;
    private Timer gameTimer = new Timer(10);
    public float gameUpdateRate = 10f;
    public float entityUpdateRate = 5f;
    public float playerUpdateRate = 5f;
    

    public Game(String id, String name) {
        this.id = id;
        this.name = name;
        started = false;
        gameover = false;
        loading = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Vector3f> getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(List<Vector3f> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isGameover() {
        return gameover;
    }

    public void setGameover(boolean gameover) {
        this.gameover = gameover;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getId(), player);
        player.load();
    }

    public boolean hasPlayer(String playerId) {
        return players.containsKey(playerId);

    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void removePlayer(Player player) {
        player.close();
        this.players.remove(player.getId(), player);
    }

    public void addEntity(Entity entity) {
        this.entities.put(entity.getId(), entity);
        entity.load();
    }

    public boolean hasEntity(String entityId) {
        return entities.containsKey(entityId);

    }

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public void removeEntity(Entity entity) {
        entity.close();
        this.entities.remove(entity.getId(), entity);
    }

    @Override
    public void simpleInitApp() {
        System.out.println("Init game loop");

    }

    @Override
    public void simpleUpdate(float tpf) {
//        System.out.println("Update game");

        if (started && !gameover && !loading && !terminated) {
            gameTimer.update(tpf);
            if (gameTimer.finished()) {
                gameListener.broadcastState(this);
                gameTimer.reset();

            }
        }

    }

    public void startGame() {
        start(JmeContext.Type.Headless);
        started = true;
        gameover = false;
        loading = false;
        terminated = false;
        this.startTime = System.currentTimeMillis();
        gameTimer.setMaxTime(gameUpdateRate);
        gameTimer.start();
    }

    public void stopGame() {
        if (started) {
            stop();
        }
        gameTimer.stop();
        started = false;
        gameover = true;
        loading = false;
        terminated = false;

    }

    public void terminateGame() {
        if (started) {
            stop();
        }
        gameTimer.stop();

        started = false;
        gameover = false;
        loading = false;
        terminated = true;

    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public GameListener getGameListener() {
        return gameListener;
    }

    public void setGameListener(GameListener gameListener) {
        this.gameListener = gameListener;
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
