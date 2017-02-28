/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.simplephysics2d;

import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author nidebruyn
 */
public abstract class SimplePhysics2DPlayer {
    
    protected SimplePhysics2DGame game;
    protected Node playerNode;
    protected Vector3f startPosition;
    protected int lives = 0;
    protected int score = 0;

    public SimplePhysics2DPlayer(SimplePhysics2DGame physicsGame) {
        this.game = physicsGame;
    }
    
    public void load() {
        this.startPosition = game.getStartPosition().mult(new Vector3f(1, 1, 0));
        
        //Load the player models
        playerNode = new Node(Platform2DGame.TYPE_PLAYER);
        playerNode.setLocalTranslation(this.startPosition);
        game.getLevelNode().attachChild(playerNode);

        init();
    }
    
    protected abstract void init();
    
    protected abstract float getSize();
    
    public void start() {
        
    }
    
    public void log(String text) {
        System.out.println(text);
    }
    
    public void close() {
        playerNode.removeFromParent();
    }

    public SimplePhysics2DGame getGame() {
        return game;
    }

    public Node getPlayerNode() {
        return playerNode;
    }
    
    public abstract Vector3f getPosition();
    
    public void doDamage(int hits) {
        if (lives > 0) {
            lives -= hits;
        } else {
            game.doGameOver();
            doDie();
        }
    }    

    public int getLives() {
        return lives;
    }
    
    public boolean addLife() {
        if (lives < 3) {
            lives ++;
            return true;
        }        
        return false;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
        game.fireScoreChangedListener(this.score);
    }
    
    public abstract void doDie();
}
