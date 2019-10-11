/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes.test;

import java.util.List;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import com.cubes.*;
import com.cubes.shapes.*;

/**
 *
 * @author Carl
 */
public class CubesTestAssets{
    
    public static CubesSettings getSettings(Application application){
        CubesSettings settings = new CubesSettings(application);
        settings.setDefaultBlockMaterial("Textures/cubes/terrain.png");
        return settings;
    }
    
    public static final Block BLOCK_GRASS = new Block(new BlockSkin[]{
            new BlockSkin(new BlockSkin_TextureLocation(0, 0), false),
            new BlockSkin(new BlockSkin_TextureLocation(1, 0), false),
            new BlockSkin(new BlockSkin_TextureLocation(2, 0), false)
        }){

        @Override
        protected int getSkinIndex(BlockChunkControl chunk, Vector3Int location, Block.Face face){
            if(chunk.isBlockOnSurface(location)){
                switch(face){
                    case Top:
                        return 0;

                    case Bottom:
                        return 2;
                }
                return 1;
            }
            return 2;
        }
    };
    private static final BlockSkin[] SKINS_WOOD = new BlockSkin[]{
        new BlockSkin(new BlockSkin_TextureLocation(4, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(4, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false),
        new BlockSkin(new BlockSkin_TextureLocation(3, 0), false)
    };
    public static Block BLOCK_WOOD = new Block(SKINS_WOOD);
    public static Block BLOCK_WOOD_FLAT = new Block(SKINS_WOOD){{
        setShapes(new BlockShape_Cuboid(new float[]{0, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f}));
    }};
    public static Block BLOCK_BRICK = new Block(new BlockSkin(new BlockSkin_TextureLocation(7, 0), false));
    public static Block BLOCK_CONNECTOR_ROD = new Block(new BlockSkin(new BlockSkin_TextureLocation(7, 0), false)){{
            setShapes(
                new BlockShape_Cuboid(new float[]{0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f}),
                new BlockShape_Cuboid(new float[]{0.5f, 0.5f, 0.2f, 0.2f, 0.2f, 0.2f}),
                new BlockShape_Cuboid(new float[]{0.2f, 0.2f, 0.5f, 0.5f, 0.2f, 0.2f}),
                new BlockShape_Cuboid(new float[]{0.2f, 0.2f, 0.2f, 0.2f, 0.5f, 0.5f})
            );
        }

        @Override
        protected int getShapeIndex(BlockChunkControl chunk, Vector3Int location){
            if((chunk.getNeighborBlock_Global(location, Block.Face.Top) !=  null) && (chunk.getNeighborBlock_Global(location, Block.Face.Bottom) !=  null)){
                return 1;
            }
            else if((chunk.getNeighborBlock_Global(location, Block.Face.Left) !=  null) && (chunk.getNeighborBlock_Global(location, Block.Face.Right) !=  null)){
                return 2;
            }
            else if((chunk.getNeighborBlock_Global(location, Block.Face.Front) !=  null) && (chunk.getNeighborBlock_Global(location, Block.Face.Back) !=  null)){
                return 3;
            }
            return 0;
    }};
    public static Block BLOCK_GLASS = new Block(new BlockSkin(new BlockSkin_TextureLocation(8, 0), true));
    public static Block BLOCK_STONE = new Block(new BlockSkin(new BlockSkin_TextureLocation(9, 0), false));
    public static Block BLOCK_STONE_PILLAR = new Block(new BlockSkin(new BlockSkin_TextureLocation(9, 0), false)){{
            setShapes(new BlockShape_Cube(), new BlockShape_Pyramid());
        }

        @Override
        protected int getShapeIndex(BlockChunkControl chunk, Vector3Int location){
            return (chunk.isBlockOnSurface(location)?1:0);
        }
    };
    public static Block BLOCK_WATER = new Block(new BlockSkin(new BlockSkin_TextureLocation(0, 1), true));
    
    public static void registerBlocks(){
        BlockManager.register(BLOCK_GRASS);
        BlockManager.register(BLOCK_WOOD);
        BlockManager.register(BLOCK_WOOD_FLAT);
        BlockManager.register(BLOCK_BRICK);
        BlockManager.register(BLOCK_CONNECTOR_ROD);
        BlockManager.register(BLOCK_GLASS);
        BlockManager.register(BLOCK_STONE);
        BlockManager.register(BLOCK_STONE_PILLAR);
        BlockManager.register(BLOCK_WATER);
    }
    
    private static final Vector3f lightDirection = new Vector3f(-0.8f, -1, -0.8f).normalizeLocal();
    
    public static void initializeEnvironment(SimpleApplication simpleApplication){
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setDirection(lightDirection);
        directionalLight.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        simpleApplication.getRootNode().addLight(directionalLight);
        simpleApplication.getRootNode().attachChild(SkyFactory.createSky(simpleApplication.getAssetManager(), "Textures/cubes/sky.jpg", true));
        
        DirectionalLightShadowRenderer directionalLightShadowRenderer = new DirectionalLightShadowRenderer(simpleApplication.getAssetManager(), 2048, 3);
        directionalLightShadowRenderer.setLight(directionalLight);
        directionalLightShadowRenderer.setShadowIntensity(0.3f);
        simpleApplication.getViewPort().addProcessor(directionalLightShadowRenderer);
    }
    
    public static void initializeWater(SimpleApplication simpleApplication){
        WaterFilter waterFilter = new WaterFilter(simpleApplication.getRootNode(), lightDirection);
        getFilterPostProcessor(simpleApplication).addFilter(waterFilter);
    }
    
    private static FilterPostProcessor getFilterPostProcessor(SimpleApplication simpleApplication){
        List<SceneProcessor> sceneProcessors = simpleApplication.getViewPort().getProcessors();
        for(int i=0;i<sceneProcessors.size();i++){
            SceneProcessor sceneProcessor = sceneProcessors.get(i);
            if(sceneProcessor instanceof FilterPostProcessor){
                return (FilterPostProcessor) sceneProcessor;
            }
        }
        FilterPostProcessor filterPostProcessor = new FilterPostProcessor(simpleApplication.getAssetManager());
        simpleApplication.getViewPort().addProcessor(filterPostProcessor);
        return filterPostProcessor;
    }
}
