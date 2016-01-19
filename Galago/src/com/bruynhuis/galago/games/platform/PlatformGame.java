/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform;

import com.bruynhuis.galago.app.Base3DApplication;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Quad;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;
import jme3tools.optimize.GeometryBatchFactory;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.control.camera.CameraStickControl;
import com.bruynhuis.galago.control.RaySpatialCollisionControl;
import com.bruynhuis.galago.control.RaySpatialListener;

/**
 *
 * @author nidebruyn
 */
public abstract class PlatformGame implements PhysicsCollisionListener, PhysicsTickListener {

    public static final String materialExtension = ".j3m";
    public static final String modelExtension = ".j3o";
    public static final String levelExtension = ".lvl";
    public static final String propertiesExtension = ".properties";
    public static final String LEVELS = "levels";
    public static final String LEVEL_ICON = "level-icon";
    public static final String LEVEL_SOUND = "level-sound";
    public static final String BACKGROUND_COLOR = "background-color";
    public static final String BACKGROUND_IMAGE = "background-image";
    public static final String BACKGROUND_LAYER1 = "background-layer1";
    public static final String BACKGROUND_LAYER2 = "background-layer2";
    public static final String BACKGROUND_LAYER3 = "background-layer3";
    public static final String BACKGROUND = "background";
    public static final String WEATHER = "weather";
    public static final String PACK = "pack";
    private String levelName;
    private String levelFile;
    private String iconFile;
    private String propertiesFile;
    private Properties levelPackProperties;
    private Properties levelProperties;
    private Properties blockProperties;
    private HashMap<String, Spatial> objects = new HashMap<String, Spatial>();
    public static float blockSize = 1.0f;
    private Node weatherNode;
    private Node backgroundNode;
    private Node enemyNode;
    private Node obstacleNode;
    private Node vegetationNode;
    private Node dynamicNode;
    private Node pickupNode;
    private Node staticNode;
    private int gold = 0;
    private float diePoint = -5f;
    protected boolean optimize = false;
    private File file;
    public static final String TYPE_PLAYER = "player";
    public static final String TYPE_ENEMY = "enemy";
    public static final String TYPE_OBSTACLE = "obstacle";
    public static final String TYPE_STATIC = "static";
    public static final String TYPE_PICKUP = "pickup";
    public static final String TYPE_START = "start";
    public static final String TYPE_END = "end";
    public static final String TYPE_VEGETATION = "vegetation";
    public static final String TYPE_DYNAMIC = "dynamic";
    protected Base3DApplication baseApplication;
    protected Node rootNode;
    protected Node levelNode;
    protected Vector3f startPosition = Vector3f.ZERO;
    protected AmbientLight ambientLight;
    protected DirectionalLight sunLight;
    protected boolean started = false;
    protected boolean paused = false;
    protected boolean loading = false;
    protected PlatformPlayer player;
    protected PlatformGameListener gameListener;
    protected Spatial lastCollidedSpatial;
    protected AbstractControl controller;

    public PlatformGame(Base3DApplication baseApplication, Node rootNode) {
        this.baseApplication = baseApplication;
        this.rootNode = rootNode;
    }

//    public void load(Vector3f startPosition) {
//
//    }
    protected void init() {
        initModel();
        initPhysics();
    }

    public void close() {
        loading = false;
        started = false;
        paused = false;

        if (sunLight != null) {
            levelNode.removeLight(sunLight);
        }

        if (ambientLight != null) {
            levelNode.removeLight(ambientLight);
        }

        baseApplication.getBulletAppState().getPhysicsSpace().removeCollisionListener(this);
        baseApplication.getBulletAppState().getPhysicsSpace().removeTickListener(this);

        baseApplication.getBulletAppState().setSpeed(1);

        if (player != null) {
            player.close();
        }

        levelNode.removeFromParent();
//        rootNode.detachAllChildren();

        baseApplication.getBulletAppState().getPhysicsSpace().destroy();
        baseApplication.getBulletAppState().getPhysicsSpace().create();
        player = null;
        System.gc(); //Force memory to be released;

    }

    protected void log(String text) {
        System.out.println(text);
    }

    /**
     * Initialize the light of the scene.
     *
     * @param ambientColor
     * @param sunDirection
     */
    protected void initLight(ColorRGBA ambientColor, Vector3f sunDirection) {
        ambientLight = new AmbientLight();
        ambientLight.setColor(ambientColor);
        levelNode.addLight(ambientLight);
//
        sunLight = new DirectionalLight();
        sunLight.setColor(ColorRGBA.White);
        sunLight.setDirection(sunDirection.normalizeLocal());
        levelNode.addLight(sunLight);
    }

    public void pause() {
        paused = true;
        baseApplication.getBulletAppState().setEnabled(false);
    }

    public void resume() {
        paused = false;
        baseApplication.getBulletAppState().setEnabled(true);
    }

    public void start(PlatformPlayer physicsPlayer) {
        this.player = physicsPlayer;
        loading = false;
        started = true;
        paused = false;
        this.player.start();
        baseApplication.getBulletAppState().setEnabled(true);
    }

    public void collision(PhysicsCollisionEvent event) {
        if (player != null) {

            if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_STATIC)) {
                fireCollisionPlayerWithStaticListener(lastCollidedSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_DYNAMIC)) {
                fireCollisionPlayerWithDynamicListener(lastCollidedSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_PICKUP)) {
                fireCollisionPlayerWithPickupListener(lastCollidedSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_ENEMY)) {
                fireCollisionPlayerWithEnemyListener(lastCollidedSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_OBSTACLE)) {
                fireCollisionPlayerWithObstacleListener(lastCollidedSpatial);
                
            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_END)) {
                fireGameCompletedListener();

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_ENEMY, TYPE_ENEMY)) {
                fireCollisionEnemyWithEnemyListener(lastCollidedSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_ENEMY, TYPE_OBSTACLE)) {
                fireCollisionPlayerWithEnemyListener(lastCollidedSpatial);

            }


        }
    }

    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        //TODO
    }

    public void physicsTick(PhysicsSpace space, float tpf) {
        //TODO
    }

    /**
     * Determine if the bullet hit something specific
     *
     * @param sA
     * @param sB
     * @param point
     * @return
     */
    protected boolean checkCollisionWithType(Spatial sA, Spatial sB, String collider, String type) {
        boolean collision = sA.getName() != null && sB.getName() != null
                && ((sA.getName().startsWith(collider) && sB.getName().startsWith(type))
                || (sA.getName().startsWith(type) && sB.getName().startsWith(collider)));


        if (collision && sB.getName().startsWith(type)) {
            lastCollidedSpatial = sB;
            return true;


        } else if (collision && sA.getName().startsWith(type)) {
            lastCollidedSpatial = sA;
            return true;

        }

        lastCollidedSpatial = null;
        return false;
    }

    public void doGameOver() {
        started = false;
        fireGameOverListener();
    }

    public void addGameListener(PlatformGameListener gameListener) {
        this.gameListener = gameListener;
    }

    protected void fireGameOverListener() {
        if (gameListener != null) {
            gameListener.doGameOver();
        }
    }

    protected void fireGameCompletedListener() {
        if (gameListener != null) {
            gameListener.doGameCompleted();
        }
    }

    protected void fireCollisionPlayerWithStaticListener(Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithStatic(collider);
        }
    }

    protected void fireCollisionPlayerWithDynamicListener(Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithDynamic(collider);
        }
    }

    protected void fireCollisionPlayerWithPickupListener(Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithPickup(collider);
            removeObject(collider);
        }
    }

    protected void fireCollisionPlayerWithEnemyListener(Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithEnemy(collider);
        }
    }

    protected void fireCollisionPlayerWithObstacleListener(Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithObstacle(collider);
        }
    }

    protected void fireCollisionEnemyWithObstacleListener(Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionEnemyWithObstacle(collider);
        }
    }

    protected void fireCollisionEnemyWithEnemyListener(Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionEnemyWithEnemy(collider);
        }
    }

    public Base3DApplication getBaseApplication() {
        return baseApplication;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public Node getLevelNode() {
        return levelNode;
    }

    public Vector3f getStartPosition() {
        return startPosition;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isLoading() {
        return loading;
    }

    public PlatformPlayer getPlayer() {
        return player;
    }

    protected String pad(int deep, String val) {
        String ret = "";
        for (int i = 0; i < deep; i++) {
            ret = ret + val;
        }
        return ret;
    }

    /**
     * Print a trance of the spatial
     *
     * @param s
     */
    protected void printDebug(Spatial s, int deep) {

        if (s instanceof Geometry) {
            log("GEOM" + pad(deep, "-") + ">" + s.getName());

        } else if (s instanceof Node) {
            log("NODE" + pad(deep, "-") + ">" + s.getName());
            Node node = (Node) s;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                printDebug(child, deep + 1);

            }

        }

    }

    /**
     * This is a helper method used to optimize the scene.
     *
     * @param node
     * @param batchName
     */
    protected void optimize(Node node, String batchName) {
        log("============== FOR (" + batchName + ") =====================");
        log("Nodes before = " + node.getQuantity());
        log("---------------------------------------------");
        printDebug(node, 1);

        //1. Optimize to batch node
        GeometryBatchFactory.optimize(node);

        //2. Loop over batch and remove unused nodes
        fixEmptyNode(node);
        fixEmptyNode(node);
        fixEmptyNode(node);
        fixEmptyNode(node);
        fixEmptyNode(node);

        //3. Set name of batch
        fixLevelNames(node, batchName);


        log("Nodes after = " + node.getQuantity());
        log("---------------------------------------------");
        printDebug(node, 1);
    }

    /**
     * Initialize the physics
     */
    protected void initPhysics() {

        if (optimize) {

            if (staticNode != null) {
                RigidBodyControl blockRigidBodyControl = new RigidBodyControl(0);
                staticNode.addControl(blockRigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(blockRigidBodyControl);
                blockRigidBodyControl.setFriction(0.2f);
                blockRigidBodyControl.setRestitution(0);

            }

//        if (obstacleNode != null) {
//            optimize(obstacleNode, OBSTACLE);
//            
//            RigidBodyControl blockRigidBodyControl = new RigidBodyControl(0);
//            obstacleNode.addControl(blockRigidBodyControl);
//            bulletAppState.getPhysicsSpace().add(blockRigidBodyControl);
//            blockRigidBodyControl.setFriction(0.2f);
//            blockRigidBodyControl.setRestitution(0);
//            
//            obstacleNode.setQueueBucket(RenderQueue.Bucket.Transparent);
//        }

            //Temp taken out
//            if (vegetationNode != null) {
//                optimize(vegetationNode, VEGETATION);
////                vegetationNode.setQueueBucket(RenderQueue.Bucket.Transparent);
//            }
        }

    }

    /**
     * This method must be called before we start creating or loading a level.
     *
     * @param levelpack
     */
    protected void loadLevelPack(String levelpackprop) {
        if (levelpackprop == null) {
            levelpackprop = "pack1.properties";
        }
        this.propertiesFile = levelpackprop;

        try {

            if (!propertiesFile.endsWith(propertiesExtension)) {
                throw new RuntimeException("Invalid level pack properties file format.");
            }

            InputStream propertiesInputStream = null;

            try {
                Platform platform = JmeSystem.getPlatform();

                if (platform.compareTo(Platform.Android_ARM5) == 0 || platform.compareTo(Platform.Android_ARM6) == 0 || platform.compareTo(Platform.Android_ARM7) == 0) {
                    propertiesInputStream = JmeSystem.getResourceAsStream("/assets/Levels/" + propertiesFile);

                } else {
                    propertiesInputStream = JmeSystem.getResourceAsStream("/Levels/" + propertiesFile);
                }
                
            } catch (UnsupportedOperationException e) {
                Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.INFO, null, e);
                //Load the default
                propertiesInputStream = JmeSystem.getResourceAsStream("/assets/Levels/" + propertiesFile);
                
            }


            //Load the level pack properties
            levelPackProperties = new Properties();
            log("Loading properties file");
            levelPackProperties.load(propertiesInputStream);

        } catch (IOException ex) {
            Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

        }

    }

    /**
     * Pre load the level before we load the actual level.
     */
    public void preload(String levelFile) {
        this.levelFile = levelFile;
        baseApplication.getBulletAppState().setEnabled(false);
        gold = 0;

//        close();

        try {

            if (!levelFile.endsWith(levelExtension)) {
                throw new RuntimeException("Invalid level file format.");
            }

            InputStream levelInputStream = null;

            Platform platform = JmeSystem.getPlatform();

            if (platform.compareTo(Platform.Android_ARM5) == 0 || platform.compareTo(Platform.Android_ARM6) == 0 || platform.compareTo(Platform.Android_ARM7) == 0) {
                levelInputStream = JmeSystem.getResourceAsStream("/assets/Levels/" + levelFile);

            } else {
                levelInputStream = JmeSystem.getResourceAsStream("/Levels/" + levelFile);
            }

            if (levelInputStream == null) {
                //Load a default
                log("Loading default properties");
                levelProperties = new Properties();

            } else {
                //Load the level.
                levelProperties = new Properties();
                log("Loading properties from file");
                levelProperties.load(levelInputStream);
                iconFile = levelProperties.getProperty(LEVEL_ICON);
                log("###################### " + iconFile);

            }

        } catch (IOException ex) {
            Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

        }
    }

    /**
     * This method must be called when you wish to load an existing level.
     *
     * @param levelFile
     */
    public void loadLevel() {

        if (levelProperties != null) {
            //Finally load the level.
            init();
        }

    }

    /**
     * Prepare everything so that a new level can be created.
     */
    public void editLevel(String levelFile, String packFile) {
        //Unload if previous level exist or was loaded
        this.levelFile = levelFile;
        baseApplication.getBulletAppState().setEnabled(false);
        gold = 0;

//        close();

        //Now we load the level data from the file system for editing
        readLevelData(levelFile, packFile);

        if (levelProperties == null) {
            levelProperties = new Properties();
            objects = new HashMap<String, Spatial>();
        }
        levelProperties.setProperty(PACK, packFile);

        //Last we need to load the level.
        initModel();
    }

    /**
     * Prepare everything so that a new level can be created.
     */
    public void testLevel(String levelFile) {
        //Unload if previous level exist or was loaded
        this.levelFile = levelFile;
        baseApplication.getBulletAppState().setEnabled(false);
        gold = 0;

//        close();

        //Now we load the level data from the file system for editing
        readLevelData(levelFile, null);

        //Last we need to load the level.
        initModel();
        initPhysics();
    }

    /**
     * Reset the level for editing
     */
    public void resetLevel(String levelFile, String packFile) {

        //Unload if previous level exist or was loaded
        this.levelFile = levelFile;
        baseApplication.getBulletAppState().setEnabled(false);
        gold = 0;

        //Remove all previous level data
        close();

        //Now we load the level data from the file system for editing
        levelProperties = new Properties();
        objects = new HashMap<String, Spatial>();
        levelProperties.setProperty(PACK, packFile);

        //Last we need to load the level.
        initModel();

    }

    /**
     * Parse the level properties so that the spatials can be loaded.
     */
    protected void initModel() {
        if (levelProperties != null) {
            loading = true;
            started = false;
            paused = false;

            //Get the level pack
            String pack = levelProperties.getProperty(PACK);
            if (pack == null || !pack.endsWith(".properties")) {
                pack = "pack1.properties";
                levelProperties.put(PACK, pack);
            }
            loadLevelPack(pack);

            levelNode = new Node("LEVEL_NODE");
            rootNode.attachChild(levelNode);

            backgroundNode = new Node("BACKGROUND");
            levelNode.attachChild(backgroundNode);

            staticNode = new Node(TYPE_STATIC);
            levelNode.attachChild(staticNode);

            enemyNode = new Node(TYPE_ENEMY);
            levelNode.attachChild(enemyNode);

            obstacleNode = new Node(TYPE_OBSTACLE);
            levelNode.attachChild(obstacleNode);

            vegetationNode = new Node(TYPE_VEGETATION);
            levelNode.attachChild(vegetationNode);

            dynamicNode = new Node(TYPE_DYNAMIC);
            levelNode.attachChild(dynamicNode);
            
            pickupNode = new Node(TYPE_PICKUP);
            levelNode.attachChild(pickupNode);

            boolean weatherLoaded = false;

            //We need to loop over all the properties of the level and load the level.
            for (Iterator<Object> it = levelProperties.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();

                if (key.equalsIgnoreCase(WEATHER)) {
                    parseWeather(levelProperties.getProperty(key));
                    weatherLoaded = true;

                } else if (key.equalsIgnoreCase(BACKGROUND)) {
                    //Load the background.
                    changeBackground(levelProperties.getProperty(key));

                } else if (key.equalsIgnoreCase(PACK) || key.equalsIgnoreCase(LEVEL_ICON)) {// || key.equalsIgnoreCase(ABILITY)) {
                    //DO nothing just ignore.
                    //For sciping keys.
                } else {
                    //Try and parse the level block positions
                    String[] positionStr = key.split(",");
                    float x = positionStr.length > 0 ? Float.parseFloat(positionStr[0].trim()) : 0;
                    float y = positionStr.length > 1 ? Float.parseFloat(positionStr[1].trim()) : 0;
                    float z = positionStr.length > 2 ? Float.parseFloat(positionStr[2].trim()) : 0;
                    Vector3f position = new Vector3f(x, y, z);

                    String value = levelProperties.getProperty(key);

                    addNewObject(value, position);

                }

            }

            //If there was no weather loaded we need to load the lights manually
            if (!weatherLoaded) {
                parseWeather("day");
            }

            controller = new AbstractControl() {
                @Override
                protected void controlUpdate(float tpf) {
                    if (started && !paused) {
                        if (player.getPosition().y <= diePoint) {
                            doGameOver();
                        }
                    }
                }

                @Override
                protected void controlRender(RenderManager rm, ViewPort vp) {
                }
            };

            levelNode.addControl(controller);

            baseApplication.getBulletAppState().getPhysicsSpace().addTickListener(this);
            baseApplication.getBulletAppState().getPhysicsSpace().addCollisionListener(this);

            pause();
            loading = false;
        }

    }

    /**
     * Add an enemy type block. If the player collides with this it dies.
     *
     * @param position
     * @param file
     * @param name
     */
    protected Spatial createEnemy(Vector3f position, String model, String name) {
        if (model == null) {
            return null;
        }

        String values[] = model.split(",");
        String modelFile = model;

        float scale = 1f;
        if (values.length >= 2) {
            modelFile = values[0].trim();
            scale = Float.parseFloat(values[1].trim());
        }

        if (modelFile.endsWith(modelExtension)) {
            Spatial modelSpatial = baseApplication.getAssetManager().loadModel(modelFile);
            modelSpatial.setLocalTranslation(position);
            modelSpatial.setLocalScale(scale);
//            fixLevelNames(modelSpatial, name);
            modelSpatial.setName(name);
            baseApplication.getRenderManager().preloadScene(modelSpatial);

            if (modelSpatial.getControl(RigidBodyControl.class) == null) {
                RigidBodyControl blockRigidBodyControl = new RigidBodyControl(0);
                modelSpatial.addControl(blockRigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(blockRigidBodyControl);
                blockRigidBodyControl.setFriction(1f);
                blockRigidBodyControl.setRestitution(0f);
            }

            return modelSpatial;
        }

        return null;
    }

    /**
     * Add an obstacle type block. If the player collides with this it dies.
     *
     * @param position
     * @param file
     * @param name
     */
    protected Spatial createObstacle(Vector3f position, String model, String name) {
        if (model == null) {
            return null;
        }

        String values[] = model.split(",");
        String modelFile = model;
        float scale = 1f;
        if (values.length >= 2) {
            modelFile = values[0].trim();
            scale = Float.parseFloat(values[1].trim());
        }

        if (modelFile.endsWith(".j3o")) {
            Spatial modelSpatial = baseApplication.getAssetManager().loadModel(modelFile);
            modelSpatial.setName(name);
            modelSpatial.setLocalTranslation(position);
            modelSpatial.setLocalScale(scale);
            baseApplication.getRenderManager().preloadScene(modelSpatial);
            String massStr = modelSpatial.getUserData("mass");
            float mass = 0;
            if (massStr != null) {
                mass = Float.parseFloat(massStr);
            }

            if (modelSpatial.getControl(RigidBodyControl.class) == null) {
                RigidBodyControl blockRigidBodyControl = new RigidBodyControl(mass);
                modelSpatial.addControl(blockRigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(blockRigidBodyControl);

            }
            return modelSpatial;

        }

        return null;
    }

    /**
     * Add a vegetation type model. If this model is added the player can move
     * through it
     *
     * @param position
     * @param model
     * @param name
     */
    protected Spatial createVegetation(Vector3f position, String model, String name) {
        if (model == null) {
            return null;
        }

        String values[] = model.split(",");
        String modelFile = model;
        float scale = 1f;
        float zOffset = 0; //FastMath.nextRandomFloat()*0.2f;
        if (values.length >= 2) {
            modelFile = values[0].trim();
            scale = Float.parseFloat(values[1].trim());
        }
        if (values.length >= 3) {
            zOffset = Float.parseFloat(values[2].trim());
        }
        if (modelFile.endsWith(modelExtension)) {
            Spatial modelSpatial = baseApplication.getAssetManager().loadModel(modelFile);
            modelSpatial.setLocalTranslation(position.add(0, 0, zOffset));
            modelSpatial.setLocalScale(scale);
            modelSpatial.setName(name);
            baseApplication.getRenderManager().preloadScene(modelSpatial);
            return modelSpatial;
        }
        return null;
    }

    public void fixTexture(MatParam mp) {
        if (mp != null) {
            MatParamTexture mpt = (MatParamTexture) mp;
            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }
    }

    //Add a model that can be collided against to the optimized node.
    protected Spatial createStatic(Vector3f position, String model, String name) {
        if (model == null) {
            return null;
        }

        String values[] = model.split(",");
        String modelFile = model;

        Vector3f scale = Vector3f.UNIT_XYZ;
        if (values.length == 2) {
            modelFile = values[0].trim();
            float sf = Float.parseFloat(values[1].trim());
            scale = new Vector3f(sf, sf, sf);

        } else if (values.length >= 4) {
            modelFile = values[0].trim();
            float sx = Float.parseFloat(values[1].trim());
            float sy = Float.parseFloat(values[2].trim());
            float sz = Float.parseFloat(values[3].trim());
            scale = new Vector3f(sx, sy, sz);
        }

        if (modelFile.endsWith(modelExtension)) {
            Spatial modelSpatial = baseApplication.getAssetManager().loadModel(modelFile);
            modelSpatial.setLocalTranslation(position);
            modelSpatial.setLocalScale(scale);
            fixLevelNames(modelSpatial, name);
            baseApplication.getRenderManager().preloadScene(modelSpatial);

            if (!optimize) {
                RigidBodyControl blockRigidBodyControl = new RigidBodyControl(0);
                modelSpatial.addControl(blockRigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(blockRigidBodyControl);
                blockRigidBodyControl.setFriction(0.2f);
                blockRigidBodyControl.setRestitution(0);
            }

            return modelSpatial;
        }

        return null;

    }

    //Add a model that can be collided against to the optimized node.
    protected Spatial createDynamic(Vector3f position, String model, String name) {
        if (model == null) {
            return null;
        }

        String values[] = model.split(",");
        String modelFile = model;

        float scale = 1f;
        if (values.length >= 2) {
            modelFile = values[0].trim();
            scale = Float.parseFloat(values[1].trim());
        }

        if (modelFile.endsWith(modelExtension)) {
            Spatial modelSpatial = baseApplication.getAssetManager().loadModel(modelFile);
            modelSpatial.setLocalTranslation(position);
            modelSpatial.setLocalScale(scale);
            fixLevelNames(modelSpatial, name);
            baseApplication.getRenderManager().preloadScene(modelSpatial);

            if (modelSpatial.getControl(RigidBodyControl.class) == null) {
                RigidBodyControl blockRigidBodyControl = new RigidBodyControl(0);
                modelSpatial.addControl(blockRigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(blockRigidBodyControl);
                blockRigidBodyControl.setFriction(1f);
                blockRigidBodyControl.setRestitution(0f);
            }

            return modelSpatial;
        }

        return null;

    }

    protected void fixLevelNames(Spatial sp, String name) {
        if (sp instanceof Geometry) {
            sp.setName(name);
        }

        if (sp instanceof Node) {
            Node parentNode = (Node) sp;
            parentNode.setName(name);
            for (int i = 0; i < parentNode.getQuantity(); i++) {
                fixLevelNames(parentNode.getChild(i), name);
            }
        }
    }

    protected void fixEmptyNode(Spatial sp) {
        if (sp instanceof Node) {
            Node parentNode = (Node) sp;
            if (parentNode.getQuantity() <= 0) {
                parentNode.removeFromParent();

            } else {
                for (int i = 0; i < parentNode.getQuantity(); i++) {
                    fixEmptyNode(parentNode.getChild(i));
                }
            }
        }
    }

    /**
     * This will add a pickup model to the scene. Like GOLD, etc.
     *
     * @param position
     * @param model
     * @param name
     */
    protected Spatial createPickupModel(Vector3f position, String model, String name) {
        if (model == null) {
            return null;
        }

        String values[] = model.split(",");
        String modelFile = model;
        float scale = 1f;
        if (values.length >= 2) {
            modelFile = values[0].trim();
            scale = Float.parseFloat(values[1].trim());
        }

        if (modelFile.endsWith(".j3o")) {
            Spatial modelSpatial = baseApplication.getAssetManager().loadModel(modelFile);
            modelSpatial.setName(name);
            modelSpatial.setLocalTranslation(position);
            modelSpatial.setLocalScale(scale);
            baseApplication.getRenderManager().preloadScene(modelSpatial);
            String massStr = modelSpatial.getUserData("mass");
            float mass = 0;
            if (massStr != null) {
                mass = Float.parseFloat(massStr);
            }

            if (modelSpatial.getControl(RigidBodyControl.class) == null) {
                RigidBodyControl blockRigidBodyControl = new RigidBodyControl(mass);
                modelSpatial.addControl(blockRigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(blockRigidBodyControl);

            }
            return modelSpatial;

        }

        return null;
    }

    /**
     * Adds the start point of the game.
     *
     * @param position
     * @param materialFile
     */
    protected Spatial createStartPoint(Vector3f position, String materialFile) {
        startPosition = position;

        if (materialFile.endsWith(modelExtension)) {
            Spatial modelSpatial = baseApplication.getAssetManager().loadModel(materialFile);
            modelSpatial.setLocalTranslation(position);
            modelSpatial.setName(TYPE_START);
            baseApplication.getRenderManager().preloadScene(modelSpatial);

            if (modelSpatial.getControl(RigidBodyControl.class) == null) {
                RigidBodyControl blockRigidBodyControl = new RigidBodyControl(0);
                modelSpatial.addControl(blockRigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(blockRigidBodyControl);
                blockRigidBodyControl.setFriction(0.2f);
                blockRigidBodyControl.setRestitution(0);
            }

            return modelSpatial;
        }

        return null;

    }

    /**
     * Add the end point to the scene. If the player gets close to this it ends
     * the game successfully
     *
     * @param position
     * @param materialFile
     */
    protected Spatial createEndPoint(Vector3f position, String materialFile) {

        if (materialFile.endsWith(modelExtension)) {
            Spatial modelSpatial = baseApplication.getAssetManager().loadModel(materialFile);

            String types[] = {TYPE_PLAYER};

            RaySpatialCollisionControl raySpatialCollisionControl = new RaySpatialCollisionControl(rootNode, (Node) modelSpatial, Vector3f.UNIT_Y, 3, types);
            raySpatialCollisionControl.addRaySpatialListener(new RaySpatialListener() {
                public void doAction(Vector3f contactPoint, Geometry contactObject, boolean hasCollision) {

                    if (isStarted() && !isPaused()) {
                        if (contactObject != null) {
                            fireGameCompletedListener();
                        }
                    }

                }
            });

            modelSpatial.addControl(raySpatialCollisionControl);

            modelSpatial.setLocalTranslation(position);
            modelSpatial.setName(TYPE_END);
            baseApplication.getRenderManager().preloadScene(modelSpatial);

            if (modelSpatial.getControl(RigidBodyControl.class) == null) {
                RigidBodyControl blockRigidBodyControl = new RigidBodyControl(0);
                modelSpatial.addControl(blockRigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(blockRigidBodyControl);
                blockRigidBodyControl.setFriction(0.2f);
                blockRigidBodyControl.setRestitution(0);
            }

            return modelSpatial;
        }

        return null;
    }

    public float getBlockSize() {
        return blockSize;
    }

    /**
     * Parse the background color if any.
     *
     * @param properties
     */
    protected void parseBackgroundColor(String val) {
        String[] strArr = val.split(",");
        if (strArr.length == 3) {
            float red = Float.parseFloat(strArr[0].trim());
            float green = Float.parseFloat(strArr[1].trim());
            float blue = Float.parseFloat(strArr[2].trim());
            ColorRGBA colorRGBA = new ColorRGBA(red / 255f, green / 255f, blue / 255f, 0.5f);
            baseApplication.getViewPort().setBackgroundColor(colorRGBA);
        } else {
            baseApplication.getViewPort().setBackgroundColor(BaseApplication.BACKGROUND_COLOR);
        }
    }

    /**
     * Parse the background image if any. this will be the sky
     *
     * @param properties
     */
    public void parseBackgroundImage(Node parentNode, String materialFile) {
        if (materialFile != null) {
            float depth = 200f;
            float per = 0.024f;
            Material material = baseApplication.getAssetManager().loadMaterial(materialFile);
            material.preload(baseApplication.getRenderManager());
            float width = 1280 * per;
            float height = 720 * per;
            Spatial layer = loadLayer(material, width, height, new Vector3f(-width * 0.5f, -height * 0.5f, -depth), 1f);
            layer.setQueueBucket(RenderQueue.Bucket.Sky);
            Node node = new Node("SKY BACKGROUND");
            node.attachChild(layer);
            node.addControl(new BillboardControl());
            node.addControl(new CameraStickControl(baseApplication.getCamera()));
            parentNode.attachChild(node);
        }
    }

    protected void parseSky(Node parentNode, String skyFile) {
        if (skyFile != null) {
            Texture texture = baseApplication.getAssetManager().loadTexture(skyFile);
            Spatial sky = SkyFactory.createSky(baseApplication.getAssetManager(), texture, texture, texture, texture, texture, texture);
//            Spatial sky = SkyFactory.createSky(assetManager, texture, true);
            parentNode.attachChild(sky);
        }
    }

    /**
     * Parse layer 1
     *
     * @param properties
     */
    public Spatial parseBackgroundLayer1(Node node, String materialFile) {
        if (materialFile != null) {
            float depth = 400f;
            float per = 1.2f;
            Material material = baseApplication.getAssetManager().loadMaterial(materialFile);
            material.preload(baseApplication.getRenderManager());
            Spatial layer = loadLayer(material, 1280 * per, 720 * per, new Vector3f(-depth * 0.8f, -(720 * per * 0.5f), -depth), 1f);
            layer.setQueueBucket(RenderQueue.Bucket.Transparent);
//            layer.addControl(new BillboardControl());
            node.attachChild(layer);
            return layer;
        }
        return null;
    }

    /**
     * Helper to load a layer
     *
     * @param material
     * @param width
     * @param height
     * @param position
     * @return
     */
    private Spatial loadLayer(Material material, float width, float height, Vector3f position, float textureScale) {
        Quad quad = new Quad(width, height);
        Geometry geometry = new Geometry(BACKGROUND_LAYER1, quad);
        geometry.setLocalTranslation(position);
        geometry.setMaterial(material);
        quad.scaleTextureCoordinates(new Vector2f(textureScale, textureScale));
        return geometry;

    }

    private Spatial loadOcean(String waterMaterialFile) {
        String foamMaterialFile = "Materials/reserved/water/oceanflow.j3m";
        float width = 500f;
        float height = 200f;

        Node ocean = new Node("OCEAN");

        Quad quad = new Quad(width, height);
        quad.scaleTextureCoordinates(new Vector2f(width * 0.2f, height * 0.2f));
        Geometry gWater = new Geometry("WATER", quad);
        gWater.setUserData("ySpeed", "0.04");
        gWater.setUserData("xSpeed", "0.01");
        gWater.rotate(FastMath.DEG_TO_RAD * -90, 0, 0);
        gWater.move(-width * 0.2f, 0, height * 0.5f);
        Material material = baseApplication.getAssetManager().loadMaterial(waterMaterialFile);
        material.preload(baseApplication.getRenderManager());
        gWater.setMaterial(material);
        ocean.attachChild(gWater);
//        gWater.addControl(new FlowControl());

        quad = new Quad(width, height);
        quad.scaleTextureCoordinates(new Vector2f(width * 0.05f, height * 0.05f));
        Geometry gfoam = new Geometry("FOAM", quad);
        gfoam.setQueueBucket(RenderQueue.Bucket.Transparent);
        gfoam.setUserData("ySpeed", "0.02");
        gfoam.setUserData("xSpeed", "0.02");
        gfoam.rotate(FastMath.DEG_TO_RAD * -90, 0, 0);
        gfoam.move(-width * 0.2f, 0.02f, height * 0.5f);  //Lift the foam just a little bit above the water.
        material = baseApplication.getAssetManager().loadMaterial(foamMaterialFile);
        material.preload(baseApplication.getRenderManager());
        gfoam.setMaterial(material);
        ocean.attachChild(gfoam);
//        gfoam.addControl(new FlowControl());

        return ocean;
    }

    /**
     * Parse the weather. Pre defined weather
     *
     * @param properties
     */
    protected void parseWeather(String type) {
        weatherNode = new Node("weather node");
        levelNode.attachChild(weatherNode);

        log("Loading weather = " + type);
        if (type != null) {

            if ("day".equalsIgnoreCase(type)) {
                initLight(ColorRGBA.White, new Vector3f(-.3f, -1f, -0.8f));
                parseBackgroundImage(weatherNode, "Materials/sky/day.j3m");
            }

        } else {
            initLight(ColorRGBA.White, new Vector3f(-.3f, -1f, -0.8f));
        }
    }

    /**
     * This is a helper method that will be used to swap the weather system.
     */
    public void changeWeather() {
        String currentWeather = levelProperties.getProperty(WEATHER);

        if (currentWeather == null) {
            parseWeather("day");
            levelProperties.put(WEATHER, "day");
        } else {
            //release the light
            if (sunLight != null) {
                levelNode.removeLight(sunLight);
            }

            if (ambientLight != null) {
                levelNode.removeLight(ambientLight);
            }

            levelProperties.remove(WEATHER);
            weatherNode.removeFromParent();

            //Parse the weather
            String weather = "day";
            if (currentWeather.equals("day")) {
                weather = "ocean";
            } else if (currentWeather.equals("ocean")) {
                weather = "night";
            } else if (currentWeather.equals("night")) {
                weather = "rain";
            } else if (currentWeather.equals("rain")) {
                weather = "snow";
            } else if (currentWeather.equals("snow")) {
                weather = "cloudy";
            } else if (currentWeather.equals("cloudy")) {
                weather = "dusk";
            } else if (currentWeather.equals("dusk")) {
                weather = "day";
            }

            //Set the weather
            parseWeather(weather);
            levelProperties.put(WEATHER, weather);
        }
    }

    /**
     * Add an object to the level from the map pack.
     *
     * @param key
     * @param x
     */
    public Spatial getObject(String key, Vector3f position) {
        Spatial spatial = null;

        //Load the ENEMIES
        if (key.startsWith(TYPE_ENEMY)) {
            spatial = createEnemy(position, levelPackProperties.getProperty(key), key);
        }

        //Load the OBSTACLEs
        if (key.startsWith(TYPE_OBSTACLE)) {
            spatial = createObstacle(position, levelPackProperties.getProperty(key), key);
        }

        //Load the VEGETATIONS
        if (key.startsWith(TYPE_VEGETATION)) {
            spatial = createVegetation(position, levelPackProperties.getProperty(key), key);
        }

        //Load the static models
        if (key.startsWith(TYPE_STATIC)) {
            spatial = createStatic(position, levelPackProperties.getProperty(key), key);

        }

        //Load the DYNAMIC
        if (key.startsWith(TYPE_DYNAMIC)) {
            spatial = createDynamic(position, levelPackProperties.getProperty(key), key);

        }
        
        //Load the pickup
        if (key.startsWith(TYPE_PICKUP)) {
            spatial = createPickupModel(position, levelPackProperties.getProperty(key), key);
        }


        //Load the start
        if (key.equals(TYPE_START)) {
            spatial = createStartPoint(position, levelPackProperties.getProperty(TYPE_START));
        }

        //Load the end
        if (key.equals(TYPE_END)) {
            spatial = createEndPoint(position, levelPackProperties.getProperty(TYPE_END));
        }

        return spatial;
    }

    /**
     * Public helper method that will add new objects to the level.
     *
     * @param key
     * @param position
     */
    public void addNewObject(String key, Vector3f position) {
        log("Adding =" + key + ", at = " + position);
        int x = (int) position.x;
        int y = (int) position.y;
        int z = (int) position.z;

        String k = x + "," + y + "," + z;
        if (levelProperties.containsKey(k)) {
            levelProperties.setProperty(k, key);
            //Remove from map
            if (objects.containsKey(k)) {
                objects.get(k).removeFromParent();
                objects.remove(k);
            }
        } else {
            levelProperties.put(k, key);
        }
        Spatial obj = getObject(key, position);
        if (obj != null) {
            if (key.startsWith(TYPE_VEGETATION)) {
                vegetationNode.attachChild(obj);

            } else if (key.startsWith(TYPE_ENEMY)) {
                enemyNode.attachChild(obj);

            } else if (key.startsWith(TYPE_OBSTACLE)) {
                obstacleNode.attachChild(obj);

            } else if (key.startsWith(TYPE_STATIC)) {
                staticNode.attachChild(obj);

            } else if (key.startsWith(TYPE_DYNAMIC)) {
                dynamicNode.attachChild(obj);
                
            } else if (key.startsWith(TYPE_PICKUP)) {
                pickupNode.attachChild(obj);                

            } else {
                levelNode.attachChild(obj);
            }

            objects.put(k, obj);
            if (position.y < diePoint) {
                diePoint = position.y - 4f;
            }
        }

    }

    /**
     * Remove the object at a given position.
     *
     * @param position
     */
    public void removeObject(Vector3f position) {
        int x = (int) position.x;
        int y = (int) position.y;
        int z = (int) position.z;
        String k = x + "," + y + "," + z;
        if (levelProperties.containsKey(k)) {
            levelProperties.remove(k);
            //Remove from map
            if (objects.containsKey(k)) {
                Spatial spatial = objects.get(k);
                RigidBodyControl bodyControl = spatial.getControl(RigidBodyControl.class);
                if (bodyControl != null) {
                    baseApplication.getBulletAppState().getPhysicsSpace().remove(spatial);
                }
                spatial.removeFromParent();
                objects.remove(k);
            }

        }

    }

    /**
     * Return all keys of the static object
     *
     * @return
     */
    public String[] getStaticKeys() {
        String[] allObjects = null;

        if (levelPackProperties != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (Iterator it = levelPackProperties.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (key.startsWith(TYPE_STATIC)) {
                    list.add(key);
                }
            }

            Collections.reverse(list);
            
            allObjects = new String[list.size()];
            list.toArray(allObjects);

            Collections.sort(list);

        }

        return allObjects;
    }

    /**
     * Return all keys of the map pack
     *
     * @return
     */
    public String[] getVegKeys() {
        String[] allObjects = null;

        if (levelPackProperties != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (Iterator it = levelPackProperties.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (key.startsWith(TYPE_VEGETATION)) {
                    list.add(key);
                }
            }
            
            Collections.reverse(list);

            allObjects = new String[list.size()];
            list.toArray(allObjects);

            Collections.sort(list);

        }

        return allObjects;
    }

    /**
     * Return all keys of the enemy pack
     *
     * @return
     */
    public String[] getEnemyKeys() {
        String[] allObjects = null;

        if (levelPackProperties != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (Iterator it = levelPackProperties.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (key.startsWith(TYPE_ENEMY)) {
                    list.add(key);
                }
            }
            
            Collections.reverse(list);

            allObjects = new String[list.size()];
            list.toArray(allObjects);

            Collections.sort(list);

        }

        return allObjects;
    }

    /**
     * Return all keys of the map pack
     *
     * @return
     */
    public String[] getObstacleKeys() {
        String[] allObjects = null;

        if (levelPackProperties != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (Iterator it = levelPackProperties.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (key.startsWith(TYPE_OBSTACLE)) {
                    list.add(key);
                }
            }
            
            Collections.reverse(list);

            allObjects = new String[list.size()];
            list.toArray(allObjects);

            Collections.sort(list);

        }

        return allObjects;
    }

    /**
     * Return all dynamic keys of the map pack
     *
     * @return
     */
    public String[] getDynamicKeys() {
        String[] allObjects = null;

        if (levelPackProperties != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (Iterator it = levelPackProperties.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (key.startsWith(TYPE_DYNAMIC)) {
                    list.add(key);
                }
            }
            
            Collections.reverse(list);

            allObjects = new String[list.size()];
            list.toArray(allObjects);

            Collections.sort(list);

        }

        return allObjects;
    }

    /**
     * Return all pickup keys of the map pack
     *
     * @return
     */
    public String[] getPickupKeys() {
        String[] allObjects = null;

        if (levelPackProperties != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (Iterator it = levelPackProperties.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (key.startsWith(TYPE_PICKUP)) {
                    list.add(key);
                }
            }
            
            Collections.reverse(list);

            allObjects = new String[list.size()];
            list.toArray(allObjects);

            Collections.sort(list);

        }

        return allObjects;
    }

    /**
     * Return all keys of the background map pack
     *
     * @return
     */
    public String[] getBackgroundKeys() {
        String[] allObjects = null;

        if (levelPackProperties != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (Iterator it = levelPackProperties.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (key.startsWith(BACKGROUND) && !key.equals(BACKGROUND_COLOR)) {
                    list.add(key);
                }
            }
            Collections.reverse(list);
            

            allObjects = new String[list.size()];
            list.toArray(allObjects);

//            Collections.sort(list);

        }

        return allObjects;
    }

    public void changeBackground(String key) {
        backgroundNode.detachAllChildren();
        if (levelProperties.containsKey(key)) {
            levelProperties.setProperty(BACKGROUND, key);
        } else {
            levelProperties.put(BACKGROUND, key);
        }
        String file = levelPackProperties.getProperty(key);
        parseBackgroundLayer1(backgroundNode, file);
    }

    /**
     * This is a helper method that can be used to save the new level.
     */
    public void saveLevelData() {
        if (levelProperties != null) {
            File folder = JmeSystem.getStorageFolder();

            if (folder != null && folder.exists()) {
                if (file != null) {
                    PrintWriter printWriter = null;
                    try {
                        printWriter = new PrintWriter(file);
                        levelProperties.store(printWriter, "");

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } finally {
                        printWriter.close();
                    }
                }
            }
        }

    }

    /**
     * Read the current level that is designed.
     */
    public void readLevelData(String levelfile, String levelPack) {
        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists()) {
            try {
                file = new File(folder.getAbsolutePath() + File.separator + levelfile);
                if (file.exists()) {
                    FileReader fileReader = new FileReader(file);
                    levelProperties = new Properties();
                    levelProperties.load(fileReader);
                    objects = new HashMap<String, Spatial>();

                } else {
                    file.createNewFile();
                    levelProperties = new Properties();
                    objects = new HashMap<String, Spatial>();
                    //If the level pack was set we will load it as default
                    if (levelPack != null && levelPack.endsWith(".properties")) {
                        levelProperties.put(PACK, levelPack);
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }

    public String getLevelIcon() {
        return iconFile;
    }

    public Properties getLevelData() {
        return levelProperties;
    }

    public String getLevelSound() {
        return levelPackProperties.getProperty(LEVEL_SOUND);
    }

    public Node getDynamicNode() {
        return dynamicNode;
    }

    public int getObjectCount() {
        return levelProperties.size();
    }
    
    public void removeObject(Spatial spatial) {
        if (spatial != null) {
            RigidBodyControl rbc = spatial.getControl(RigidBodyControl.class);
            if (rbc != null) {
                baseApplication.getBulletAppState().getPhysicsSpace().remove(rbc);
            }
            
            spatial.removeFromParent();
            
        }
    }

    public float getDiePoint() {
        return diePoint;
    }
    
}
