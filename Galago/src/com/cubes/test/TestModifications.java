package com.cubes.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.cubes.*;

public class TestModifications extends SimpleApplication{

    public static void main(String[] args){
        Logger.getLogger("").setLevel(Level.SEVERE);
        TestModifications app = new TestModifications();
        app.start();
    }

    public TestModifications(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Cubes Demo - Modifications");
    }
    private BlockTerrainControl blockTerrain;
    private long lastModificationTimestamp;
    private Vector3Int lastModificationLocation = new Vector3Int(0, 4, 15);

    @Override
    public void simpleInitApp(){
        CubesTestAssets.registerBlocks();
        
        blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(2, 1, 2));
        for(int x=0;x<32;x++){
            for(int z=0;z<16;z++){
                int groundHeight = (int) ((Math.random() * 4) + 8);
                for(int y=0;y<groundHeight;y++){
                    if((z != 15) || (y != 4)){
                        blockTerrain.setBlock(x, y, z, CubesTestAssets.BLOCK_STONE);
                    }
                }
                int additionalHeight = (int) (Math.random() * 4);
                for(int y=0;y<additionalHeight;y++){
                    Block block = ((y > 0)?CubesTestAssets.BLOCK_GRASS:CubesTestAssets.BLOCK_WOOD);
                    blockTerrain.setBlock(x, (groundHeight + y), z, block);
                }
            }
        }
        Node terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
        
        cam.setLocation(new Vector3f(-29, 32, 96));
        cam.lookAtDirection(new Vector3f(0.68f, -0.175f, -0.71f), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(250);
    }

    @Override
    public void simpleUpdate(float lastTimePerFrame){
        if((System.currentTimeMillis() - lastModificationTimestamp) > 50){
            blockTerrain.removeBlock(lastModificationLocation);
            lastModificationLocation.addLocal(1, 0, 0);
            if(lastModificationLocation.getX() > 31){
                lastModificationLocation.setX(0);
            }
            blockTerrain.setBlock(lastModificationLocation, CubesTestAssets.BLOCK_GRASS);
            lastModificationTimestamp = System.currentTimeMillis();
        }
    }
}
