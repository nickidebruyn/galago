/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.websocket.game;

import com.bruynhuis.galago.util.Timer;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class Player {

    private Game game;
    private String id;
    private String name;
    private Vector3f location = new Vector3f(0, 0, 0);
    private Quaternion rotation = new Quaternion();
    private boolean owner;
    private boolean killed;
    private boolean active;
    private int state;
    private int type;
    private int health;
    private int score;
    private Node node;
    private PlayerListener playerListener;
    private Timer playerTimer = new Timer(2);
    private Vector3f walkDirection;
    private float walkSpeed;

    public Player(Game game, String id, String name, int type) {
        this.game = game;
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public void load() {
        this.node = new Node(id);
        this.node.setLocalTranslation(location);
        this.node.setLocalRotation(rotation);
        this.game.getRootNode().attachChild(node);
        this.node.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (game.isStarted() && !game.isLoading() && !game.isGameover() && !game.isTerminated()) {

                    //Check if player can move
                    if (walkDirection != null) {
                        node.move(tpf * walkDirection.x * walkSpeed, tpf * walkDirection.y * walkSpeed, tpf * walkDirection.z * walkSpeed);

                    }

                    playerTimer.update(tpf);
                    if (playerTimer.finished()) {
//                        if (walkDirection != null && (walkDirection.x != 0 || walkDirection.y != 0 || walkDirection.z != 0)) {
                        playerListener.broadcastState(Player.this);
                        playerTimer.reset();
//                        }

                    }

                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });
        
        playerTimer.setMaxTime(game.getPlayerUpdateRate());
        playerTimer.start();

    }

    public void setInitialLocation(Vector3f location) {
        this.location = location;
    }

    public void setInitialRotation(Quaternion quaternion) {
        this.rotation = quaternion;

    }

    public void close() {
        playerTimer.stop();
        node.removeFromParent();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public PlayerListener getPlayerListener() {
        return playerListener;
    }

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
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

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Vector3f getLocation() {
        return node.getLocalTranslation();
    }

    public void setLocation(Vector3f location) {
        this.node.setLocalTranslation(location);
    }

    public Quaternion getRotation() {
        return node.getLocalRotation();
    }

    public void setRotation(Quaternion rotation) {
        this.node.setLocalRotation(rotation);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public void setWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addDamage(int damage) {
        this.health = health - damage;

        if (this.health <= 0) {
            this.doKill();

        }
    }

    public void doKill() {
        this.health = 0;
        this.killed = true;
        this.active = false;

    }

    public void respawn() {
        this.killed = false;
        this.active = true;
        this.node.setLocalTranslation(location);
        this.node.setLocalRotation(rotation);

    }
}
