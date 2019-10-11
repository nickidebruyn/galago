package com.cubes.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.cubes.*;

public class TestHeightmap extends SimpleApplication{

    public static void main(String[] args){
        Logger.getLogger("").setLevel(Level.SEVERE);
        TestHeightmap app = new TestHeightmap();
        app.start();
    }

    public TestHeightmap(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Cubes Demo - Heightmap");
    }

    @Override
    public void simpleInitApp(){
        CubesTestAssets.registerBlocks();
        
        BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(4, 1, 4));
        blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(64, 1, 64), CubesTestAssets.BLOCK_STONE);
        blockTerrain.setBlocksFromHeightmap(new Vector3Int(0, 1, 0), "Textures/cubes/heightmap.jpg", 20, CubesTestAssets.BLOCK_GRASS);
        Node terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
        
        cam.setLocation(new Vector3f(-3, 88, 300));
        cam.lookAtDirection(new Vector3f(0.44f, -0.35f, -0.83f), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(300);
    }
}
