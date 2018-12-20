/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.control.effects.FlowControl;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import com.galago.example.platformer2d.game.controls.BladeControl;
import com.galago.example.platformer2d.game.controls.BladeHorizontalControl;
import com.galago.example.platformer2d.game.controls.MoverControl;
import com.galago.example.platformer2d.game.controls.MushroomControl;
import com.galago.example.platformer2d.game.controls.PlatformHorizontalControl;
import com.galago.example.platformer2d.game.controls.PlatformVerticalControl;
import com.galago.example.platformer2d.game.controls.PortalControl;
import com.galago.example.platformer2d.game.terrain.CrateControl;
import com.galago.example.platformer2d.game.terrain.GlassControl;

/**
 *
 * @author Nidebruyn
 */
public class Game extends Platform2DGame {

    public static final float TILE_SIZE = 1f;
    private RigidBodyControl terrainRigidBodyControl;
    private float backgroundScale = 0.026f;
    private FilterPostProcessor fpp;
    
    public Game(Base2DApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);

    }

    @Override
    public void init() {

        if (!isEdit() && terrainRigidBodyControl != null) {
            terrainRigidBodyControl.optimize();
        }

        fpp = new FilterPostProcessor(baseApplication.getAssetManager());
        baseApplication.getViewPort().addProcessor(fpp);

    }

    @Override
    protected void initTerrainList(ArrayList<String> list) {
        list.add("terrain-ground");
        list.add("terrain-plate");
        list.add("terrain-metal");
        list.add("terrain-stone");
        list.add("terrain-brick");
        list.add("terrain-grass");
        list.add("terrain-snow");
        list.add("terrain-snow-ground");

    }

    @Override
    protected void initEnemyList(ArrayList<String> list) {
    }

    @Override
    protected void initObstacleList(ArrayList<String> list) {
        list.add("obstacle-spike-up");
        list.add("obstacle-spike-down");
        list.add("obstacle-spike-left");
        list.add("obstacle-spike-right");
        list.add("obstacle-blade-horizontal");
        list.add("obstacle-blade-vertical");
        list.add("obstacle-spike-ball");

    }

    @Override
    protected void initStaticList(ArrayList<String> list) {
        list.add("static-glass");
        list.add("static-ice");
        list.add("static-crate");
        list.add("static-platform-horizontal");
        list.add("static-platform-vertical");
        list.add("static-mushroom");
        list.add("static-portal-blue-in");
        list.add("static-portal-blue-out");
        list.add("static-portal-yellow-in");
        list.add("static-portal-yellow-out");
        list.add("static-portal-purple-in");
        list.add("static-portal-purple-out");
        
        list.add("static-mover-up");
        list.add("static-mover-down");
        list.add("static-mover-left");
        list.add("static-mover-right");
        
    }

    @Override
    protected void initPickupList(ArrayList<String> list) {
        list.add("pickup-star");
    }

    @Override
    protected void initVegetationList(ArrayList<String> list) {

    }

    @Override
    protected void initSkyList(ArrayList<String> list) {
        list.add("sky-default");
        list.add("sky-blue");
        list.add("sky-orange");
        list.add("sky-green");
        list.add("sky-red");
        list.add("sky-purple");
    }

    @Override
    protected void initFrontLayer1List(ArrayList<String> list) {
    }

    @Override
    protected void initFrontLayer2List(ArrayList<String> list) {
    }

    @Override
    protected void initBackLayer1List(ArrayList<String> list) {
    }

    @Override
    protected void initBackLayer2List(ArrayList<String> list) {
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

        if (item.startsWith("enemy")) {
            sprite = getEnemy(item);

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

        if (item.startsWith("pickup")) {
            sprite = getPickup(item);

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
     * Get a terrain tile
     *
     * @param item
     * @return
     */
    private Sprite getTerrain(String item) {
        Sprite sprite = null;
        float size = TILE_SIZE;

        sprite = new Sprite(item, size, size);
        sprite.setImage("Textures/terrain/" + item + ".png");
        sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);

        BoxCollisionShape collisionShape = new BoxCollisionShape(sprite.getWidth(), sprite.getHeight());

        sprite.setUserData(SHAPE, collisionShape);

        if (terrainRigidBodyControl == null) {
            terrainRigidBodyControl = new RigidBodyControl(collisionShape, 0);
//            terrainRigidBodyControl.setAutoSleepingEnabled(true);
            terrainRigidBodyControl.setRestitution(0);
            terrainRigidBodyControl.setFriction(1f);
            terrainRigidBodyControl.setDensity(10);
            sprite.addControl(terrainRigidBodyControl);
        } else {
            terrainRigidBodyControl.addCollisionShape(collisionShape);
        }

        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);

        return sprite;
    }

    private Sprite getEnemy(String item) {
       
        return null;
    }

    private Sprite getStatics(String item) {

        Sprite sprite = null;

        if (item.endsWith("glass")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/terrain/terrain-glass.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
//            sprite.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.PremultAlphas); 
            RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0);
            rigidBodyControl.setRestitution(0);
            rigidBodyControl.setFriction(1f);
            sprite.addControl(rigidBodyControl);
            sprite.addControl(new GlassControl(this));

        }
        
        if (item.endsWith("ice")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/terrain/terrain-ice.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
//            sprite.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.PremultAlphas); 
            RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0);
            rigidBodyControl.setRestitution(0);
            rigidBodyControl.setFriction(1f);
            sprite.addControl(rigidBodyControl);
            sprite.addControl(new GlassControl(this));

        }
        
        if (item.endsWith("crate")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/terrain/terrain-crate2.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0);
            rigidBodyControl.setRestitution(0);
            rigidBodyControl.setFriction(1f);
            sprite.addControl(rigidBodyControl);
            sprite.addControl(new CrateControl(this));

        }
        
        if (item.endsWith("platform-horizontal")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/static/metal-platform.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth()*0.95f, sprite.getHeight()*0.95f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.addControl(new PlatformHorizontalControl(this));            

        }
        
        if (item.endsWith("platform-vertical")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/static/metal-platform.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth()*0.95f, sprite.getHeight()*0.95f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.addControl(new PlatformVerticalControl(this));

        }
        
        if (item.endsWith("mushroom")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/static/mushroom.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()*0.4f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.addControl(new MushroomControl(this));

        }
        
        if (item.endsWith("portal-blue-in")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setUserData("target", "blue-out");
            sprite.setImage("Textures/static/portal-blue.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new CircleCollisionShape(TILE_SIZE*0.4f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setSensor(true);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.addControl(new PortalControl(this));
            sprite.addControl(new RotationControl(new Vector3f(0, 0, 70)));
            Spatial sonar = baseApplication.getAssetManager().loadModel("Models/effects/portal-in.j3o");
            sonar.move(0, 0, 0.1f);
            sprite.attachChild(sonar);

        }
        
        if (item.endsWith("portal-blue-out")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setUserData("type", "blue-out");
            sprite.setImage("Textures/static/portal-blue.png");
            sprite.setLocalTranslation(0, 0, 0.01f);
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new CircleCollisionShape(TILE_SIZE*0.4f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setSensor(true);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.addControl(new RotationControl(new Vector3f(0, 0, 70)));
            Spatial sonar = baseApplication.getAssetManager().loadModel("Models/effects/portal-out.j3o");
            sonar.move(0, 0, 0.1f);
            sprite.attachChild(sonar);

        }
        
        if (item.endsWith("portal-yellow-in")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setUserData("target", "yellow-out");
            sprite.setImage("Textures/static/portal-yellow.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new CircleCollisionShape(TILE_SIZE*0.4f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setSensor(true);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.addControl(new PortalControl(this));
            sprite.addControl(new RotationControl(new Vector3f(0, 0, 70)));
            Spatial sonar = baseApplication.getAssetManager().loadModel("Models/effects/portal-in.j3o");
            sonar.move(0, 0, 0.1f);
            sprite.attachChild(sonar);

        }
        
        if (item.endsWith("portal-yellow-out")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setUserData("type", "yellow-out");
            sprite.setImage("Textures/static/portal-yellow.png");
            sprite.setLocalTranslation(0, 0, 0.01f);
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new CircleCollisionShape(TILE_SIZE*0.4f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setSensor(true);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.addControl(new RotationControl(new Vector3f(0, 0, 70)));
            Spatial sonar = baseApplication.getAssetManager().loadModel("Models/effects/portal-out.j3o");
            sonar.move(0, 0, 0.1f);
            sprite.attachChild(sonar);

        }
        
        if (item.endsWith("portal-purple-in")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setUserData("target", "purple-out");
            sprite.setImage("Textures/static/portal-purple.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new CircleCollisionShape(TILE_SIZE*0.4f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setSensor(true);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.addControl(new PortalControl(this));
            sprite.addControl(new RotationControl(new Vector3f(0, 0, 70)));
            Spatial sonar = baseApplication.getAssetManager().loadModel("Models/effects/portal-in.j3o");
            sonar.move(0, 0, 0.1f);
            sprite.attachChild(sonar);

        }
        
        if (item.endsWith("portal-purple-out")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setUserData("type", "purple-out");
            sprite.setImage("Textures/static/portal-purple.png");
            sprite.setLocalTranslation(0, 0, 0.01f);
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new CircleCollisionShape(TILE_SIZE*0.4f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setSensor(true);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.addControl(new RotationControl(new Vector3f(0, 0, 70)));
            Spatial sonar = baseApplication.getAssetManager().loadModel("Models/effects/portal-out.j3o");
            sonar.move(0, 0, 0.1f);
            sprite.attachChild(sonar);

        }
        
        if (item.endsWith("mover-right")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/static/mover.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            
            sprite.addControl(new MoverControl(this, new Vector3f(1, 0, 0)));
            
            Sprite flower = new Sprite("mover-right", TILE_SIZE*0.8f, TILE_SIZE*0.8f);
            flower.setImage("Textures/static/arrow.png");
            FlowControl flowControl = new FlowControl("Textures/static/arrow.png", -2, 0);
            flower.addControl(flowControl);
            sprite.attachChild(flower);
            flower.move(0, 0, -0.01f);

        }
        
        if (item.endsWith("mover-left")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/static/mover.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);            
            sprite.addControl(new MoverControl(this, new Vector3f(-1, 0, 0)));
            sprite.rotate(0, 0, 180*FastMath.DEG_TO_RAD);
            
            Sprite flower = new Sprite("mover-left", TILE_SIZE*0.8f, TILE_SIZE*0.8f);
            flower.setImage("Textures/static/arrow.png");
            FlowControl flowControl = new FlowControl("Textures/static/arrow.png", -2, 0);
            flower.addControl(flowControl);
            sprite.attachChild(flower);
            flower.move(0, 0, -0.01f);

        }
        
        if (item.endsWith("mover-up")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/static/mover.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);            
            sprite.addControl(new MoverControl(this, new Vector3f(0, 1, 0)));
            sprite.rotate(0, 0, 90*FastMath.DEG_TO_RAD);
            
            Sprite flower = new Sprite("mover-up", TILE_SIZE*0.8f, TILE_SIZE*0.8f);
            flower.setImage("Textures/static/arrow.png");
            FlowControl flowControl = new FlowControl("Textures/static/arrow.png", -2, 0);
            flower.addControl(flowControl);
            sprite.attachChild(flower);
            flower.move(0, 0, -0.01f);

        }
        
        if (item.endsWith("mover-down")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/static/mover.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);            
            sprite.addControl(new MoverControl(this, new Vector3f(0, -1, 0)));
            sprite.rotate(0, 0, 270*FastMath.DEG_TO_RAD);
            
            Sprite flower = new Sprite("mover-down", TILE_SIZE*0.8f, TILE_SIZE*0.8f);
            flower.setImage("Textures/static/arrow.png");
            FlowControl flowControl = new FlowControl("Textures/static/arrow.png", -2, 0);
            flower.addControl(flowControl);
            sprite.attachChild(flower);
            flower.move(0, 0, -0.01f);

        }

        if (sprite != null) {
            sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        }

        return sprite;

    }

    private Sprite getObstacles(String item) {

        Sprite sprite = null;

        if (item.endsWith("spike-up")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/obstacle/spikes.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl terrainBody = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), TILE_SIZE*0.15f), 0);
            terrainBody.setRestitution(0);
            terrainBody.setFriction(1f);
            terrainBody.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(terrainBody);

        }

        if (item.endsWith("spike-down")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/obstacle/spikes.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl terrainBody = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), TILE_SIZE*0.15f), 0);
            terrainBody.setRestitution(0);
            terrainBody.setFriction(1f);
            terrainBody.setPhysicLocation(new Vector3f(0, 0, 0));
            terrainBody.setPhysicRotation(180 * FastMath.DEG_TO_RAD);
            sprite.rotate(0, 0, 180 * FastMath.DEG_TO_RAD);
            sprite.addControl(terrainBody);

        }

        if (item.endsWith("spike-left")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/obstacle/spikes.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl terrainBody = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), TILE_SIZE*0.15f), 0);
            terrainBody.setRestitution(0);
            terrainBody.setFriction(1f);
            terrainBody.setPhysicLocation(new Vector3f(0, 0, 0));
            terrainBody.setPhysicRotation(90 * FastMath.DEG_TO_RAD);
            sprite.rotate(0, 0, 90 * FastMath.DEG_TO_RAD);
            sprite.addControl(terrainBody);

        }

        if (item.endsWith("spike-right")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/obstacle/spikes.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl terrainBody = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), TILE_SIZE*0.15f), 0);
            terrainBody.setRestitution(0);
            terrainBody.setFriction(1f);
            terrainBody.setPhysicLocation(new Vector3f(0, 0, 0));
            terrainBody.setPhysicRotation(270 * FastMath.DEG_TO_RAD);
            sprite.rotate(0, 0, 270 * FastMath.DEG_TO_RAD);
            sprite.addControl(terrainBody);

        }

        if (item.endsWith("blade-vertical")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/obstacle/blade.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new CircleCollisionShape(sprite.getWidth() * 0.5f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.getChild(0).addControl(new RotationControl(new Vector3f(0, 0, -300)));
            sprite.addControl(new BladeControl(this));

        }
        
        if (item.endsWith("blade-horizontal")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/obstacle/blade.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new CircleCollisionShape(sprite.getWidth() * 0.5f), 0);
            body.setRestitution(0);
            body.setFriction(1f);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            body.setPhysicRotation(90 * FastMath.DEG_TO_RAD);
            sprite.rotate(0, 0, 90 * FastMath.DEG_TO_RAD);
            sprite.addControl(body);
            sprite.getChild(0).addControl(new RotationControl(new Vector3f(0, 0, -300)));
            sprite.addControl(new BladeHorizontalControl(this));

        }
        
        if (item.endsWith("spike-ball")) {
            sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
            sprite.setImage("Textures/obstacle/spike-ball.png");
            sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
            RigidBodyControl body = new RigidBodyControl(new CircleCollisionShape(sprite.getWidth() * 0.5f), 1);
            body.setRestitution(0.2f);
            body.setFriction(0.5f);
            body.setGravityScale(0);
            body.setPhysicLocation(new Vector3f(0, 0, 0));
            sprite.addControl(body);
            sprite.addControl(new AbstractControl() {

                @Override
                protected void controlUpdate(float tpf) {
                    if (isStarted() && !isGameOver() && !isPaused()) {
                        
                        if (FastMath.abs(player.getPosition().x - spatial.getControl(RigidBodyControl.class).getPhysicLocation().x) < 2 &&
                                FastMath.abs(spatial.getControl(RigidBodyControl.class).getPhysicLocation().y - player.getPosition().y) < 2) {
                            spatial.getControl(RigidBodyControl.class).setGravityScale(1.2f);
                        }
                        
                    }
                }

                @Override
                protected void controlRender(RenderManager rm, ViewPort vp) {
                }
            });

        }

        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);

        return sprite;
    }

    /**
     * Returns the start sprite
     *
     * @param item
     * @return
     */
    private Sprite getStart(String item) {
        Sprite sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/other/start.j3m"));
        sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
        sprite.setLocalTranslation(0, 0, 0.01f);
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        return sprite;
    }

    /**
     * Returns the end sprite
     *
     * @param item
     * @return
     */
    private Sprite getEnd(String item) {
        Sprite sprite = new Sprite(item, TILE_SIZE, TILE_SIZE);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/other/end.j3m"));
        sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
        sprite.setLocalTranslation(0, 0, 0.01f);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(new CircleCollisionShape(TILE_SIZE*0.4f), 0);
        rigidBodyControl.setRestitution(0);
        rigidBodyControl.setFriction(0f);
        rigidBodyControl.setGravityScale(0);
        rigidBodyControl.setSensor(true);
        rigidBodyControl.setPhysicLocation(new Vector3f(0, 0, 0));
        sprite.addControl(rigidBodyControl);
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);

        if (!isEdit()) {
            Spatial sonar = baseApplication.getAssetManager().loadModel("Models/effects/end.j3o");
            sonar.move(-0.04f, 0.25f, -0.1f);
            sprite.attachChild(sonar);
        }
        return sprite;
    }

    /**
     * Load the vegetation
     *
     * @param item
     * @return
     */
    private Sprite getVegetation(String item) {
        Sprite sprite = null;
        


        return sprite;
    }

    private Sprite getPickup(String item) {
        Sprite sprite = new Sprite(item, TILE_SIZE*0.8f, TILE_SIZE*0.8f);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/pickup/star.j3m"));
        sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
        sprite.setLocalTranslation(0, 0, 0.01f);
//        RigidBodyControl rigidBodyControl = new RigidBodyControl(new CircleCollisionShape(0.4f), 0);
//        rigidBodyControl.setRestitution(0);
//        rigidBodyControl.setFriction(0f);
//        rigidBodyControl.setGravityScale(0);
//        rigidBodyControl.setSensor(true);
//        rigidBodyControl.setPhysicLocation(new Vector3f(0, 0, 0));
//        sprite.addControl(rigidBodyControl);
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        return sprite;
    }

    /**
     * Returns a sky sprite for the game.
     *
     * @param name
     * @return
     */
    public Sprite getSky(String item) {

        Sprite sprite = null;
        float width = 1280;
        float height = 800;

        String matStr = item.replaceAll("sky-", "");

        sprite = new Sprite("sky", width * backgroundScale, height * backgroundScale);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/background/" + matStr + ".j3m"));

        sprite.setLocalTranslation(0, 0, -100);
        sprite.setQueueBucket(RenderQueue.Bucket.Translucent);

        return sprite;
    }

    private Sprite getLayer1(String item) {

        Sprite sprite = null;
       

        return sprite;
    }

    private Sprite getLayer2(String item) {
        Sprite sprite = null;
        

        return sprite;
    }
}
