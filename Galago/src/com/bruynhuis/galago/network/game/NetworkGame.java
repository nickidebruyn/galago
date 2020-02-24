/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network.game;

import com.bruynhuis.galago.app.BaseServerApplication;
import com.bruynhuis.galago.util.Timer;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author NideBruyn
 */
public class NetworkGame {

    private BaseServerApplication application;
    private String gameId;
    private String gameName;
    private boolean active;
    private boolean physicsEnabled;
    private boolean randomSpawnPoint;
    private int gameCreatorId;
    private Map<Integer, NetworkPlayer> players = new HashMap<>();
    private Map<String, NetworkObject> objects = new HashMap<>();
    private List<Vector3f> spawnPoints = new ArrayList<>();
    protected BulletAppState bulletAppState;
    private Vector3f gravity;
    protected AbstractControl gameControl;
    protected Node gameNode;
    private Timer broadcastTimer = new Timer(2.5f);
    private Timer statsTimer = new Timer(1000f);

    public NetworkGame(BaseServerApplication application, String gameId, String gameName, int creatorId) {
        this.application = application;
        this.gameId = gameId;
        this.gameName = gameName;
        this.gameCreatorId = creatorId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public boolean isPhysicsEnabled() {
        return physicsEnabled;
    }

    public void setPhysicsEnabled(boolean physicsEnabled) {
        this.physicsEnabled = physicsEnabled;
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void setGravity(Vector3f gravity) {
        this.gravity = gravity;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getGameCreatorId() {
        return gameCreatorId;
    }

    public void setGameCreatorId(int gameCreatorId) {
        this.gameCreatorId = gameCreatorId;
    }

    public Map<Integer, NetworkPlayer> getPlayers() {
        return players;
    }

    public List<Vector3f> getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(List<Vector3f> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public void addPlayer(NetworkPlayer networkPlayer) {
        players.put(networkPlayer.getPlayerId(), networkPlayer);

        log("Loaded player physics: " + networkPlayer.getCollisionType());
        networkPlayer.load();

    }

    public void addObject(NetworkObject networkObject) {
        objects.put(networkObject.getId(), networkObject);

        networkObject.load();

    }

    public void load() {
        //TODO: Loading the game
        if (isPhysicsEnabled()) {
            initPhysics();
            log("Physics loaded for game " + gameId);

        }

        gameNode = new Node(gameId);
        application.getRootNode().attachChild(gameNode);

        gameControl = new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                broadcastTimer.update(tpf);

                if (broadcastTimer.finished()) {

                    application.broadcastAllPlayerStates(NetworkGame.this, false);
                    application.broadcastAllObjectStates(NetworkGame.this, false);

                    broadcastTimer.reset();

                }

                statsTimer.update(tpf);

                if (statsTimer.finished()) {

                    log("\n\n");
                    log("##### NETWORK GAME STATS #######");
                    log("# PLAYER: " + players.size());
                    log("# OBJECTS: " + objects.size());
                    log("# ");
                    log("##### END ######################");
                    log("\n\n");

                    statsTimer.reset();

                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        };

        gameNode.addControl(gameControl);
        broadcastTimer.start();
        statsTimer.start();
    }

    public void close() {
        log("Closing the game " + gameId);

        application.getRootNode().detachChild(gameNode);

        if (bulletAppState != null) {
            application.getStateManager().detach(bulletAppState);
        }

    }

    protected void initPhysics() {
        //Don't load if it already exist
        if (bulletAppState != null) {
            return;
        }
        /**
         * Set up Physics
         */
        bulletAppState = new BulletAppState();
        application.getStateManager().attach(bulletAppState);
        if (gravity != null) {
            bulletAppState.getPhysicsSpace().setGravity(gravity);
        }

//        bulletAppState.getPhysicsSpace().setAccuracy(1f/80f);
//        bulletAppState.getPhysicsSpace().setMaxSubSteps(2);
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    protected void log(String text) {
        System.out.println(text);
    }

    public Map<String, NetworkObject> getObjects() {
        return objects;
    }

    public Node getGameNode() {
        return gameNode;
    }

    public BaseServerApplication getApplication() {
        return application;
    }

    public boolean isRandomSpawnPoint() {
        return randomSpawnPoint;
    }

    public void setRandomSpawnPoint(boolean randomSpawnPoint) {
        this.randomSpawnPoint = randomSpawnPoint;
    }

}
