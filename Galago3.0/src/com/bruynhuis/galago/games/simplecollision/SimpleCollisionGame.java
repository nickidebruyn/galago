/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.simplecollision;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import jme3tools.optimize.GeometryBatchFactory;
import com.bruynhuis.galago.app.BaseApplication;

/**
 * 
 * This game type does not make use of any physics. It will make use of ray cating to check for collisions.
 * 
 *
 * @author nidebruyn
 */
public abstract class SimpleCollisionGame {

    public static final String TYPE_PLAYER = "player";
    public static final String TYPE_ENEMY = "enemy";
    public static final String TYPE_OBSTACLE = "obstacle";
    public static final String TYPE_STATIC = "static";
    public static final String TYPE_PICKUP = "pickup";
    public static final String TYPE_VEGETATION = "vegetation";
    public static final String TYPE_BULLET = "bullet";
    
    protected BaseApplication baseApplication;
    protected Node rootNode;
    protected Node levelNode;
    protected Node staticNode;
    protected Vector3f startPosition = Vector3f.ZERO;
    protected Vector3f endPosition = Vector3f.ZERO;
    protected AmbientLight ambientLight;
    protected DirectionalLight sunLight;
    protected boolean started = false;
    protected boolean paused = false;
    protected boolean loading = false;
    protected SimpleCollisionPlayer player;
    protected SimpleCollisionGameListener gameListener;
    protected boolean optimize = false;

    public SimpleCollisionGame(BaseApplication baseApplication, Node rootNode) {
        this.baseApplication = baseApplication;
        this.rootNode = rootNode;

    }

    public abstract void init();

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

        if (player != null) {
            player.close();
        }

        levelNode.removeFromParent();
        rootNode.detachAllChildren();

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

        sunLight = new DirectionalLight();
        sunLight.setColor(ColorRGBA.White);
        sunLight.setDirection(sunDirection.normalizeLocal());
        levelNode.addLight(sunLight);
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void start(SimpleCollisionPlayer physicsPlayer) {
        this.player = physicsPlayer;
        loading = false;
        started = true;
        paused = false;
        this.player.start();
    }

    /**
     * This method is for internal use only.
     * It will fire the appropriate even.
     */
    public void fireCollisionEvent(Spatial spatialA, Spatial spatialB) {
        
        if (player != null) {
            
//            log("Collision: " + spatialA.getName() + " with " + spatialB.getName());

            if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_STATIC)) {                
                fireCollisionPlayerWithStaticListener(spatialA, spatialB);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_PICKUP)) {
                fireCollisionPlayerWithPickupListener(spatialA, spatialB);
                
            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_ENEMY, TYPE_ENEMY)) {
                fireCollisionEnemyWithEnemyListener(spatialA, spatialB);
                
            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_ENEMY, TYPE_STATIC)) {                
                fireCollisionEnemyWithStaticListener(spatialA, spatialB);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_ENEMY)) {
                fireCollisionPlayerWithEnemyListener(spatialA, spatialB);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_OBSTACLE)) {
                fireCollisionPlayerWithObstacleListener(spatialA, spatialB);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_ENEMY, TYPE_OBSTACLE)) {
                fireCollisionEnemyWithObstacleListener(spatialA, spatialB);

            }


        }
    }

    /**
     * Determine if a collision between object was found.
     *
     * @param sA
     * @param sB
     * @param point
     * @return
     */
    protected boolean checkCollisionWithType(Spatial sA, Spatial sB, String collider, String type) {
        boolean collision = sA.getName() != null && sB.getName() != null
                && sA.getName().startsWith(collider) && sB.getName().startsWith(type);

        return collision;
    }

    public void doGameOver() {
        started = false;
        paused = true;
        fireGameOverListener();
    }

    public void addGameListener(SimpleCollisionGameListener gameListener) {
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
    
    public void fireScoreChangedListener(int score) {
        if (gameListener != null) {
            gameListener.doScoreChanged(score);
        }
    }

    protected void fireCollisionPlayerWithStaticListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithStatic(collided, collider);
        }
    }
    
    protected void fireCollisionPlayerWithPickupListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithPickup(collided, collider);
        }
    }

    protected void fireCollisionPlayerWithEnemyListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithEnemy(collided, collider);
        }
    }

    protected void fireCollisionPlayerWithObstacleListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithObstacle(collided, collider);
        }
    }

    protected void fireCollisionEnemyWithObstacleListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionEnemyWithObstacle(collided, collider);
        }
    }

    protected void fireCollisionEnemyWithEnemyListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionEnemyWithEnemy(collided, collider);
        }
    }
    
    protected void fireCollisionEnemyWithStaticListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionEnemyWithStatic(collided, collider);
        }
    }

    public BaseApplication getBaseApplication() {
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

    public SimpleCollisionPlayer getPlayer() {
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
//            log("GEOM" + pad(deep, "-") + ">" + s.getName());

        } else if (s instanceof Node) {
//            log("NODE" + pad(deep, "-") + ">" + s.getName());
            Node node = (Node) s;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                printDebug(child, deep + 1);

            }

        }

    }

    /**
     * Parse the level properties so that the spatials can be loaded.
     */
    public void load() {
        levelNode = new Node("LEVEL_NODE");
        rootNode.attachChild(levelNode);
        
        staticNode = new Node(TYPE_STATIC);
        levelNode.attachChild(staticNode);

        init();
        
        if (optimize) {

            if (staticNode != null) {
                optimize(staticNode, TYPE_STATIC);
            }
        }

        pause();
        loading = false;
        
    }

    public boolean isOptimize() {
        return optimize;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }       

    /**
     * Add an enemy type block. If the player collides with this it dies.
     *
     * @param position
     * @param file
     * @param name
     */
    public Spatial createEnemy(Spatial model, Vector3f collisionAxis) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_ENEMY);
//        baseApplication.getRenderManager().preloadScene(model);
        levelNode.attachChild(model);
        model.addControl(new RayColliderControl(this, collisionAxis));

        return model;

    }
    
    public Spatial createObstacle(Spatial model) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_OBSTACLE);
//        baseApplication.getRenderManager().preloadScene(model);
        levelNode.attachChild(model);
//        model.addControl(new RayColliderControl(this));

        return model;

    }

    /**
     * Add a vegetation type model. If this model is added the player can move
     * through it
     */
    public Spatial createVegetation(Spatial model) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_VEGETATION);
//        baseApplication.getRenderManager().preloadScene(model);
        levelNode.attachChild(model);

        return model;

    }

    public void fixTexture(MatParam mp) {
        if (mp != null) {
            MatParamTexture mpt = (MatParamTexture) mp;
            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }
    }

    /**
     * Create static
     * @param model
     * @param mass
     * @return 
     */
    public Spatial createStatic(Spatial model) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_STATIC);
        staticNode.attachChild(model);
//        model.addControl(new RayColliderControl(this));

        return model;

    }

    /**
     * This will add a pickup model to the scene. Like GOLD, etc.
     *
     * @param position
     * @param model
     * @param name
     */
    public Spatial createPickup(Spatial model) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_PICKUP);
        levelNode.attachChild(model);
//        model.addControl(new RayColliderControl(this));

        return model;

    }

    
    /**
     * Create a bullet in 3d and physics space
     * @param model
     * @return 
     */
    public Spatial createBullet(Spatial model) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_BULLET);
        levelNode.attachChild(model);
//        model.addControl(new RayColliderControl(this));

        return model;
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
}
