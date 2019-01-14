/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.survival.screens;

import com.bruynhuis.galago.games.blender3d.Blender3DGameListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.galago.example.survival.MainApplication;
import com.galago.example.survival.game.Game;
import com.galago.example.survival.game.Player;
import com.jme3.app.FlyCamAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author nicki
 */
public class PlayScreen extends AbstractScreen implements Blender3DGameListener {

    public static final String NAME = "PlayScreen";
    private Label title;
    private MainApplication mainApplication;

    private Game game;
    private Player player;
    private FlyCamAppState flyCamAppState;
    private FlyByCamera flyCamera;
    private float cameraHeight = 2f;
    private float cameraMultiplyer = -1f;
    private float cameraHeightMin = 1.8f;
    private float cameraHeightMax = 2f;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        title = new Label(hudPanel, "Survival");
        title.centerTop(0, 0);

    }

    @Override
    protected void load() {
        
        cameraMultiplyer = -1f;

        game = new Game(mainApplication, rootNode, "Scenes/level1.j3o");
        game.load();

        player = new Player(game);
        player.load();

        game.addGameListener(this);

        //Load the camera
//        camera.setLocation(new Vector3f(-50, 50, 50));
//        camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        //Load the fly cam
        flyCamAppState = new FlyCamAppState();
        baseApplication.getStateManager().attach(flyCamAppState);
        
        
    }

    @Override
    protected void show() {
        game.start(player);
        flyCamera = flyCamAppState.getCamera();
        flyCamera.setMoveSpeed(6);
        flyCamera.setRotationSpeed(2);
    }

    @Override
    protected void exit() {
    }

    @Override
    protected void pause() {
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {
                
                cameraHeight = game.getTerrain().getHeight(new Vector2f(camera.getLocation().x, camera.getLocation().z)) + cameraHeightMax;
                camera.setLocation(new Vector3f(camera.getLocation().x, cameraHeight, camera.getLocation().z));
                
//                cameraHeight += tpf*cameraMultiplyer;
//                
//                if (cameraHeight <= cameraHeightMin) {
//                    cameraMultiplyer = 1f;
//                } else if (cameraHeight > cameraHeightMax) {
//                    cameraMultiplyer = -1f;
//                }
                
            }
            
        }
    }

    @Override
    public void doGameOver() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
