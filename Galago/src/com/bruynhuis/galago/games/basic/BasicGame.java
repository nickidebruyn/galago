/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.basic;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.bruynhuis.galago.app.BaseApplication;

/**
 * 
 * This game type does not make use of any physics.
 * A very simple abstract game type
 * 
 *
 * @author nidebruyn
 */
public abstract class BasicGame {
    
    public static final String TYPE_PLAYER = "player";
    
    protected BaseApplication baseApplication;
    protected Node rootNode;
    protected Node levelNode;
    protected AmbientLight ambientLight;
    protected DirectionalLight sunLight;
    protected boolean started = false;
    protected boolean paused = false;
    protected boolean loading = false;
    protected BasicPlayer player;
    protected BasicGameListener gameListener;
    protected Vector3f startPosition = Vector3f.ZERO;

    public BasicGame(BaseApplication baseApplication, Node rootNode) {
        this.baseApplication = baseApplication;
        this.rootNode = rootNode;

    }

    public abstract void init();

    public void close() {
        loading = false;
        started = false;
        paused = false;

        if (sunLight != null) {
            levelNode.removeLight(sunLight);
        }

        if (ambientLight != null) {
            levelNode.removeLight(ambientLight);
        }

        if (player != null) {
            player.close();
        }

        levelNode.removeFromParent();
        rootNode.detachAllChildren();

        player = null;
        System.gc(); //Force memory to be released;

    }

    protected void log(String text) {
        System.out.println(text);
    }

    /**
     * Initialize the light of the scene.
     *
     * @param ambientColor
     * @param sunDirection
     */
    protected void initLight(ColorRGBA ambientColor, Vector3f sunDirection) {
//        ambientLight = new AmbientLight();
//        ambientLight.setColor(ambientColor);
//        levelNode.addLight(ambientLight);

        sunLight = new DirectionalLight();
        sunLight.setColor(ColorRGBA.White);
        sunLight.setDirection(sunDirection.normalizeLocal());
        levelNode.addLight(sunLight);
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void start(BasicPlayer physicsPlayer) {
        this.player = physicsPlayer;
        loading = false;
        started = true;
        paused = false;
        this.player.start();
    }

    public void doGameOver() {
        started = false;
        paused = true;
        fireGameOverListener();
    }
    
    public void doGameCompleted() {
        started = false;
        paused = true;
        fireGameCompletedListener();
    }

    public void addGameListener(BasicGameListener gameListener) {
        this.gameListener = gameListener;
    }

    protected void fireGameOverListener() {
        if (gameListener != null) {
            gameListener.doGameOver();
        }
    }

    protected void fireGameCompletedListener() {
        if (gameListener != null) {
            gameListener.doGameCompleted();
        }
    }
    
    public void fireScoreChangedListener(int score) {
        if (gameListener != null) {
            gameListener.doScoreChanged(score);
        }
    }

    public BaseApplication getBaseApplication() {
        return baseApplication;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public Node getLevelNode() {
        return levelNode;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isLoading() {
        return loading;
    }

    public BasicPlayer getPlayer() {
        return player;
    }

    protected String pad(int deep, String val) {
        String ret = "";
        for (int i = 0; i < deep; i++) {
            ret = ret + val;
        }
        return ret;
    }

    /**
     * Parse the level properties so that the spatials can be loaded.
     */
    public void load() {
        levelNode = new Node("LEVEL_NODE");
        rootNode.attachChild(levelNode);
        
        init();
        
        pause();
        loading = false;
        
    }

    public Vector3f getStartPosition() {
        return startPosition;
    }
    
}
