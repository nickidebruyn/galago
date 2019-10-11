package com.cubes.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.cubes.*;

public class TestNoise extends SimpleApplication{

    public static void main(String[] args){
        Logger.getLogger("").setLevel(Level.SEVERE);
        TestNoise app = new TestNoise();
        app.start();
    }

    public TestNoise(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Cubes Demo - Noise");
    }

    @Override
    public void simpleInitApp(){
        CubesTestAssets.registerBlocks();
        CubesTestAssets.initializeEnvironment(this);
        
        BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(4, 1, 4));
        blockTerrain.setBlocksFromNoise(new Vector3Int(0, 0, 0), new Vector3Int(64, 50, 64), 0.3f, CubesTestAssets.BLOCK_GRASS);
        Node terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        terrainNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(terrainNode);
        
        cam.setLocation(new Vector3f(-64, 187, -55));
        cam.lookAtDirection(new Vector3f(0.64f, -0.45f, 0.6f), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(300);
    }    
}
