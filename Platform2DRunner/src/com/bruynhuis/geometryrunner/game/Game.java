/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.game;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.Animation;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author nidebruyn
 */
public class Game extends Platform2DGame {

    public Game(Base2DApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);

    }

    @Override
    public void init() {
    }

    @Override
    protected void initTerrainList(ArrayList<String> list) {
        list.add("terrain-dirt");
        list.add("terrain-grass");
        list.add("terrain-planet");
        list.add("terrain-sand");
        list.add("terrain-snow");
        list.add("terrain-stone");
    }

    @Override
    protected void initEnemyList(ArrayList<String> list) {
    }

    @Override
    protected void initObstacleList(ArrayList<String> list) {
        list.add("obstacle-spike1");
        list.add("obstacle-spike2");
        list.add("obstacle-spike3");
        list.add("obstacle-spike4");
        list.add("obstacle-spike5");
        list.add("obstacle-spike6");
        list.add("obstacle-spike-long");

    }

    @Override
    protected void initStaticList(ArrayList<String> list) {
        for (int i = 0; i < 9; i++) {
            list.add("static_" + i);
        }
    }

    @Override
    protected void initPickupList(ArrayList<String> list) {
    }

    @Override
    protected void initVegetationList(ArrayList<String> list) {
        for (int i = 0; i < 12; i++) {
            list.add("vegetation_" + i);
        }
    }

    @Override
    protected void initSkyList(ArrayList<String> list) {
        list.add("sky_blue_desert");
        list.add("sky_blue_grass");
        list.add("sky_blue_land");
        list.add("sky_blue_shroom");

        list.add("sky_colored_desert");
        list.add("sky_colored_grass");
        list.add("sky_colored_land");
        list.add("sky_colored_shroom");
    }

    @Override
    protected void initFrontLayer1List(ArrayList<String> list) {
    }

    @Override
    protected void initFrontLayer2List(ArrayList<String> list) {
    }

    @Override
    protected void initBackLayer1List(ArrayList<String> list) {
        for (int i = 0; i < 4; i++) {
            list.add("layer1_" + i);
        }
    }

    @Override
    protected void initBackLayer2List(ArrayList<String> list) {
        for (int i = 0; i < 4; i++) {
            list.add("layer2_" + i);
        }
    }

    @Override
    protected void initStartList(ArrayList<String> list) {
        list.add("start");
    }

    @Override
    protected void initEndList(ArrayList<String> list) {
        list.add("end");
    }

    @Override
    public Sprite getItem(String item) {
        Sprite sprite = null;

        //Get the sky from method
        if (item.startsWith("sky")) {
            sprite = getSky(item);
        }

        if (item.startsWith("terrain")) {
            sprite = getTerrain(item);
        }

        if (item.startsWith("start")) {
            sprite = getStart(item);

        }

        if (item.startsWith("end")) {
            sprite = getEnd(item);

        }

        if (item.startsWith("vegetation")) {
            sprite = getVegetation(item);

        }

        if (item.startsWith("layer1")) {
            sprite = getLayer1(item);

        }
        
        if (item.startsWith("layer2")) {
            sprite = getLayer2(item);

        }

        if (item.startsWith("static")) {
            sprite = getStatics(item);

        }

        if (item.startsWith("obstacle")) {
            sprite = getObstacles(item);

        }

        return sprite;
    }

    /**
     * Returns a sky sprite for the game.
     *
     * @param name
     * @return
     */
    private Sprite getSky(String item) {
        Sprite s = new Sprite("sky", 22, 22);
        String name = item.replaceFirst("sky_", "");
        s.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/Backgrounds/" + name + ".j3m"));
        s.setLocalTranslation(0, 0, -10);
        return s;
    }

    /**
     * Get a terrain tile
     *
     * @param item
     * @return
     */
    private Sprite getTerrain(String item) {
        Sprite sprite = null;
        float scale = 20f;

        if (item.endsWith("dirt")) {
            sprite = new Sprite(item, 9*scale, 3, 1, 6, 0);

        } else if (item.endsWith("grass")) {
            sprite = new Sprite(item, 9*scale, 3, 1, 6, 1);

        } else if (item.endsWith("planet")) {
            sprite = new Sprite(item, 9*scale, 3, 1, 6, 2);

        } else if (item.endsWith("sand")) {
            sprite = new Sprite(item, 9*scale, 3, 1, 6, 3);

        } else if (item.endsWith("snow")) {
            sprite = new Sprite(item, 9*scale, 3, 1, 6, 4);

        } else if (item.endsWith("stone")) {
            sprite = new Sprite(item, 9*scale, 3, 1, 6, 5);

        }
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/terrains.j3m"));
        sprite.scaleTextureCoords(new Vector2f(scale, 1));

        RigidBodyControl terrainBody = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0);
        terrainBody.setRestitution(0);
        terrainBody.setFriction(1f);
        terrainBody.setPhysicLocation(new Vector3f(0, 0, 0));
        sprite.addControl(terrainBody);

        return sprite;
    }

    private Sprite getStatics(String item) {
        String iStr = item.replaceFirst("static_", "");
        int i = Integer.parseInt(iStr);
        Sprite sprite = new Sprite(item, 1f, 1f, 3, 3, i);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/statics.j3m"));

        RigidBodyControl terrainBody = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0);
        terrainBody.setRestitution(0);
        terrainBody.setFriction(1f);
        terrainBody.setPhysicLocation(new Vector3f(0, 0, 0));
        sprite.addControl(terrainBody);

        return sprite;
    }

    private Sprite getObstacles(String item) {

        Sprite sprite = null;

        if (item.endsWith("spike1")) {
            sprite = new Sprite(item, 1f, 1f, 3, 3, 0);

        } else if (item.endsWith("spike2")) {
            sprite = new Sprite(item, 1f, 1f, 3, 3, 1);

        } else if (item.endsWith("spike3")) {
            sprite = new Sprite(item, 1f, 1f, 3, 3, 2);

        } else if (item.endsWith("spike4")) {
            sprite = new Sprite(item, 1f, 1f, 3, 3, 6);

        } else if (item.endsWith("spike5")) {
            sprite = new Sprite(item, 1f, 1f, 3, 3, 7);

        } else if (item.endsWith("spike6")) {
            sprite = new Sprite(item, 1f, 1f, 3, 3, 8);

        } else if (item.endsWith("spike-long")) {
            sprite = new Sprite(item, 3f, 1f, 1, 3, 1);

        }

        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/obstacles.j3m"));

        RigidBodyControl terrainBody = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), 0.25f), 0);
        terrainBody.setRestitution(0);
        terrainBody.setFriction(1f);
        terrainBody.setPhysicLocation(new Vector3f(0, 0, 0));
        sprite.addControl(terrainBody);

        return sprite;
    }

    /**
     * Returns the start sprite
     *
     * @param item
     * @return
     */
    private Sprite getStart(String item) {
        AnimatedSprite sprite = new AnimatedSprite(item, 1f, 1f, 6, 2, 0);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/items.j3m"));
        sprite.setLocalTranslation(0, 0, -0.01f);
        sprite.addAnimation(new Animation("flag", 0, 1, 20));
        sprite.play("flag", true, false, true);
        return sprite;
    }

    /**
     * Returns the end sprite
     *
     * @param item
     * @return
     */
    private Sprite getEnd(String item) {
        Sprite sprite = new Sprite(item, 1f, 1f, 2, 6, 3);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/items.j3m"));
        sprite.setLocalTranslation(0, 0, -0.01f);
        return sprite;
    }

    /**
     * Load the vegetation
     *
     * @param item
     * @return
     */
    private Sprite getVegetation(String item) {
        String iStr = item.replaceFirst("vegetation_", "");
        int i = Integer.parseInt(iStr);
        Sprite sprite = new Sprite(item, 1f, 1f, 2, 6, i);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/items.j3m"));
        sprite.setLocalTranslation(0, 0, -0.05f);
        return sprite;
    }

    private Sprite getLayer1(String item) {
        float scale = 10f;
        String iStr = item.replaceFirst("layer1_", "");
        int i = Integer.parseInt(iStr);
        Sprite sprite = new Sprite(item, 20f*scale, 5f, 1, 4, i);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/layers.j3m"));
        sprite.setLocalTranslation(0, 0, -5f);
        sprite.scaleTextureCoords(new Vector2f(scale, 1));
        return sprite;
    }
    
    private Sprite getLayer2(String item) {
        float scale = 10f;
        String iStr = item.replaceFirst("layer2_", "");
        int i = Integer.parseInt(iStr);
        Sprite sprite = new Sprite(item, 20f*scale, 5f, 1, 4, i);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/layers.j3m"));
        sprite.setLocalTranslation(0, 0, -7f);
        sprite.scaleTextureCoords(new Vector2f(scale, 1));
        return sprite;
    }
}
