/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.util.SpriteUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 *
 * @author NideBruyn
 */
public class Game extends SimplePhysics2DGame {

    public Game(Base2DApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);
    }

    @Override
    public void init() {
        
        //Load the ground
        loadTerrain(36, 1, 0, -10);
        
        //Load the roof
        loadTerrain(36, 1, 0, 10);
        
        //Load the left wall
        loadTerrain(1, 20, -18, 0);
        
        //Load the right wall
        loadTerrain(1, 20, 18, 0);
        
        //load a platform
        loadTerrain(24, 0.5f, 0, -6);
        loadTerrain(5, 0.5f, -6, -2);
        loadTerrain(5, 0.5f, 6, -2);        
        loadTerrain(8, 0.5f, 0, 1);        
        
        loadTerrain(1, 1, 0, 4);        
        
    }
    
    private void loadTerrain(float width, float height, float x, float y) {
        
        Sprite sprite = new Sprite("terrain", width, height);
        SpriteUtils.addColor(sprite, ColorRGBA.Brown, true);
        
        RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(width, height), 0);
        sprite.addControl(rbc);
        
        addTerrain(sprite);
        
        SpriteUtils.move(sprite, x, y, 0);
        
    }
    
}
