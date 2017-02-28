/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author nidebruyn
 */
public abstract class PlatformPlayer {
    
    protected PlatformGame game;
    protected Node playerNode;
    protected Vector3f startPosition;

    public PlatformPlayer(PlatformGame physicsGame) {
        this.game = physicsGame;
    }
    
    public void load() {
        this.startPosition = game.getStartPosition();
        
        //Load the player models
        playerNode = new Node(PlatformGame.TYPE_PLAYER);
        playerNode.setLocalTranslation(startPosition);
        game.getLevelNode().attachChild(playerNode);

        init();
    }
    
    protected abstract void init();
    
    public void start() {
        
    }
    
    public void log(String text) {
        System.out.println(text);
    }
    
    public void close() {
        playerNode.removeFromParent();
    }

    public PlatformGame getGame() {
        return game;
    }

    public Node getPlayerNode() {
        return playerNode;
    }
    
    public abstract Vector3f getPosition();
}
