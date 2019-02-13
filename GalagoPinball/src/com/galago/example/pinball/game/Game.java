/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.pinball.game;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.games.blender2d.BlenderPhysics2DGame;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author nicki
 */
public class Game extends BlenderPhysics2DGame {

    public Game(Base2DApplication baseApplication, Node rootNode, String sceneFile) {
        super(baseApplication, rootNode, sceneFile);
    }

    @Override
    public void init() {
        
    }

    @Override
    public void parse(Spatial spatial) {
        
    }
    
}
