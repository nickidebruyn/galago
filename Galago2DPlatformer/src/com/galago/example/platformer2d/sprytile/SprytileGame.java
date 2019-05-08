/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.sprytile;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.games.blender2d.BlenderPhysics2DGame;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class SprytileGame extends BlenderPhysics2DGame {

    public SprytileGame(Base2DApplication baseApplication, Node rootNode, String sceneFile) {
        super(baseApplication, rootNode, sceneFile);
    }

    @Override
    public void init() {
    }

    @Override
    public void parse(Spatial spatial) {
        
        //First update the transparency of the spatial
        if (spatial instanceof Geometry) {
            Geometry geom = (Geometry) spatial;
            SpatialUtils.updateSpatialTransparency(geom, true, 1f);
            
        }
        
    }
    
}
