/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.survival.game;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.games.blender3d.Blender3DGame;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;

/**
 *
 * @author nicki
 */
public class Game extends Blender3DGame {
    
    private Terrain terrain;

    public Game(Base3DApplication baseApplication, Node rootNode, String sceneFile) {
        super(baseApplication, rootNode, sceneFile);
    }    

    @Override
    public void init() {
        
        
    }

    @Override
    public void parse(Spatial spatial) {
        
        if (spatial instanceof Terrain) {
            log("Found terrain");
            terrain = (Terrain) spatial;
        }
        
    }

    public Terrain getTerrain() {
        return terrain;
    }
    
    
}
