/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.test.terraingame.TerrainGame;
import com.bruynhuis.galago.test.terraingame.TerrainGamePlayer;
import com.jme3.input.FlyByCamera;

/**
 *
 * @author nidebruyn
 */
public class UnshadedTerrainScreen extends AbstractScreen {

    private TerrainGame game;
    private TerrainGamePlayer player;
    private FlyByCamera flyByCamera;

    @Override
    protected void init() {
    }

    @Override
    protected void load() {

        game = new TerrainGame(baseApplication, rootNode, true);
        game.load();

        player = new TerrainGamePlayer(game);
        player.load();

        //Init the picker listener
        flyByCamera = new FlyByCamera(camera);
        flyByCamera.registerWithInput(inputManager);
        flyByCamera.setMoveSpeed(15f);

    }

    @Override
    protected void show() {
        game.start(player);
    }

    @Override
    protected void exit() {
        flyByCamera.unregisterInput();
        game.close();
    }

    @Override
    protected void pause() {
        
    }

}
