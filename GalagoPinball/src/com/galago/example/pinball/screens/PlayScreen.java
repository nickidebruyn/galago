/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.example.pinball.screens;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.games.blender2d.Blender2DGameListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.galago.example.pinball.game.Game;
import com.galago.example.pinball.game.Player;
import com.jme3.scene.Spatial;

/**
 *
 * @author nicki
 */
public class PlayScreen extends AbstractScreen implements Blender2DGameListener {
	
    public static final String NAME = "PlayScreen";
    private Label title;
    private ControlButton leftButton;
    private ControlButton rightButton;
    
    private Game game;
    private Player player;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Screen Title");
        title.centerTop(0, 0);
        
        leftButton = new ControlButton(hudPanel, "left-button", 240, 400);
        leftButton.leftBottom(0, 0);
        leftButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    game.doLeftFlip();
                }
            }

            @Override
            public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    game.doLeftStop();
                }
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    game.doLeftStop();
                }
            }
            
        });
        
    }

    @Override
    protected void load() {
        game = new Game((Base2DApplication)baseApplication, rootNode, "Models/board.j3o");
        game.load();
        
        player = new Player(game);
        player.load();
        
        game.addGameListener(this);
        
    }

    @Override
    protected void show() {
        setPreviousScreen(null);
        game.start(player);
        
    }

    @Override
    protected void exit() {        
        game.close();
    }

    @Override
    protected void pause() {
    }

    @Override
    public void doGameOver() {
        log("Exit");
        exitScreen();
    }

    @Override
    public void doGameCompleted() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doScoreChanged(int score) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithTerrain(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
        
    }

    @Override
    public void doCollisionEnemyWithStatic(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithTerrain(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithBullet(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionObstacleWithBullet(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionEnemyWithObstacle(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionTerrainWithBullet(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}