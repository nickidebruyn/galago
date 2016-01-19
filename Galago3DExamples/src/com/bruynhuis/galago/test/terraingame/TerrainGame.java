/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.terraingame;

import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.games.basic.BasicGame;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.water.SimpleWaterProcessor;

/**
 *
 * @author nidebruyn
 */
public class TerrainGame extends BasicGame {

    private Node scene;
    private DirectionalLightShadowRenderer dlsr;
    private SimpleWaterProcessor simpleWaterProcessor;
    private FilterPostProcessor oceanFilterPostProcessor;
    private Vector3f lightPos =  new Vector3f(33,12,-29);
    private boolean unshaded = false;

    public TerrainGame(BaseApplication baseApplication, Node rootNode, boolean unshaded) {
        super(baseApplication, rootNode);
        this.unshaded = unshaded;
    }

    @Override
    public void init() {
        scene = (Node) baseApplication.getAssetManager().loadModel("Scenes/terrain1.j3o");

        //fix the terrain.
        for (int i = 0; i < scene.getChildren().size(); i++) {
            Spatial spatial = scene.getChildren().get(i);
            if (spatial instanceof TerrainQuad) {
                TerrainQuad terrain = (TerrainQuad) spatial;
                TerrainLodControl control = new TerrainLodControl(terrain, baseApplication.getCamera());
                control.setLodCalculator(new DistanceLodCalculator(33, 2.7f)); // patch size, and a multiplier
                if (unshaded) {
                    SpatialUtils.makeTerrainUnshaded(terrain);
                }
                
            }
        }

//        SpatialUtils.makeUnshaded(scene);
        
        levelNode.attachChild(scene);
        
        SpatialUtils.addSkySphere(levelNode, 1);
        
        if (!unshaded) {
            initLight(ColorRGBA.White, new Vector3f(.35f, -1f, 1f).normalizeLocal());
            loadFilter();            
        }
        
//        SpatialUtils.addSimpleWater(levelNode, 128, 1f, 0.0f, 0f);
        if (unshaded) {
            simpleWaterProcessor = SpatialUtils.addSimpleWater(levelNode, 128, 1f, 0.01f, 10f);
        } else {
            oceanFilterPostProcessor = SpatialUtils.addOceanWater(levelNode, sunLight.getDirection().clone(), 2f);
        }        

    }

    /**
     * load some effects
     */
    private void loadFilter() {
        dlsr = new DirectionalLightShadowRenderer(baseApplication.getAssetManager(), 512, 1);
        dlsr.setLight(sunLight);
//        dlsr.setShadowIntensity(0.4f);
        baseApplication.getViewPort().addProcessor(dlsr);
        log("Loaded the shadows");
    }

    @Override
    public void close() {
        if (simpleWaterProcessor != null) {
            getBaseApplication().getViewPort().removeProcessor(simpleWaterProcessor);
        }
        
        if (oceanFilterPostProcessor != null) {
            getBaseApplication().getViewPort().removeProcessor(oceanFilterPostProcessor);
        }
        
        super.close(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
