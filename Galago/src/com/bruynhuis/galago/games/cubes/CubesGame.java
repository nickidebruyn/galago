/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.cubes;

import com.bruynhuis.galago.app.Base3DApplication;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

/**
 *
 * @author nidebruyn
 */
public abstract class CubesGame implements PhysicsCollisionListener, PhysicsTickListener {

    public static final String TYPE_PLAYER = "player";
    public static final String TYPE_ENEMY = "enemy";
    public static final String TYPE_OBSTACLE = "obstacle";
    public static final String TYPE_STATIC = "static";
    public static final String TYPE_PICKUP = "pickup";
    public static final String TYPE_START = "start";
    public static final String TYPE_END = "end";
    public static final String TYPE_VEGETATION = "vegetation";
    public static final String TYPE_BULLET = "bullet";
    
    protected Base3DApplication baseApplication;
    protected Node rootNode;
    protected Node levelNode;
    protected Node cubesNode;
    protected BlockTerrainControl blockTerrainControl;    
    protected AbstractCubesTheme cubesTheme;
    
    protected Vector3f startPosition = Vector3f.ZERO;
    protected Vector3f endPosition = Vector3f.ZERO;
    protected AmbientLight ambientLight;
    protected DirectionalLight sunLight;
    protected boolean started = false;
    protected boolean gameover = false;
    protected boolean paused = false;
    protected boolean loading = false;
    protected CubesPlayer player;
    protected CubesGameListener gameListener;
    protected Spatial lastCollidedSpatial;
    protected Spatial lastColliderSpatial;

    public CubesGame(Base3DApplication baseApplication, Node rootNode, AbstractCubesTheme abstractCubesTheme) {
        this.baseApplication = baseApplication;
        this.rootNode = rootNode;
        this.cubesTheme = abstractCubesTheme;

    }

    public abstract void init();

    public void close() {
        loading = false;
        started = false;
        gameover = false;
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
        rootNode.detachAllChildren();

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

    public void start(CubesPlayer physicsPlayer) {
        this.player = physicsPlayer;
        loading = false;
        started = true;
        gameover = false;
        paused = false;
        this.player.start();
        baseApplication.getBulletAppState().setEnabled(true);
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
//        log("Collision: " + player);
        
        if (player != null && event.getNodeA() != null && event.getNodeB() != null) {
            
//            log("Collision: " + event.getNodeA().getName() + " with " + event.getNodeB().getName());

            if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_STATIC)) {
                fireCollisionPlayerWithStaticListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_PICKUP)) {
                fireCollisionPlayerWithPickupListener(lastCollidedSpatial, lastColliderSpatial);
                
            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_BULLET)) {
                fireCollisionPlayerWithBulletListener(lastCollidedSpatial, lastColliderSpatial);
                
            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_OBSTACLE, TYPE_BULLET)) {
                fireCollisionObstacleWithBulletListener(lastCollidedSpatial, lastColliderSpatial);
                
            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_ENEMY, TYPE_BULLET)) {
                fireCollisionEnemyWithBulletListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_ENEMY)) {
                fireCollisionPlayerWithEnemyListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_OBSTACLE)) {
                fireCollisionPlayerWithObstacleListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_ENEMY, TYPE_ENEMY)) {
                fireCollisionEnemyWithEnemyListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_ENEMY, TYPE_OBSTACLE)) {
                fireCollisionPlayerWithEnemyListener(lastCollidedSpatial, lastColliderSpatial);

            }


        }
    }

    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        //TODO
    }

    public void physicsTick(PhysicsSpace space, float tpf) {
        //TODO
//        log("gamelistener: " + gameListener);
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
            lastColliderSpatial = sA;
            return true;


        } else if (collision && sA.getName().startsWith(type)) {
            lastCollidedSpatial = sA;
            lastColliderSpatial = sB;
            return true;

        }

        lastCollidedSpatial = null;
        lastColliderSpatial = null;
        return false;
    }

    public void doGameOver() {
        started = false;
        this.gameover = true;
        fireGameOverListener();
    }
    
    public void doGameComplete() {
        started = false;
        this.gameover = true;
        fireGameCompletedListener();
    }

    public boolean isGameover() {
        return gameover;
    }

    public void addGameListener(CubesGameListener gameListener) {
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
    
    protected void fireCollisionPlayerWithBulletListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithBullet(collided, collider);
        }
    }
    
    protected void fireCollisionObstacleWithBulletListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionObstacleWithBullet(collided, collider);
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
    
    protected void fireCollisionEnemyWithBulletListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionEnemyWithBullet(collided, collider);
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

    public CubesPlayer getPlayer() {
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
     * Parse the level properties so that the spatials can be loaded.
     */
    public void load() {
        baseApplication.getBulletAppState().setEnabled(false);

        levelNode = new Node("LEVEL_NODE");
        rootNode.attachChild(levelNode);
        
        cubesNode = new Node("cubes");
        levelNode.attachChild(cubesNode);
        cubesNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        
        blockTerrainControl = new BlockTerrainControl(cubesTheme.getCubesSettings(), new Vector3Int(cubesTheme.getChunkCountX(), 
                cubesTheme.getChunkCountY(), 
                cubesTheme.getChunkCountZ()));
        cubesNode.addControl(blockTerrainControl);

        init();

        baseApplication.getBulletAppState().getPhysicsSpace().addTickListener(this);
        baseApplication.getBulletAppState().getPhysicsSpace().addCollisionListener(this);

        pause();
        loading = false;
    }

    /**
     * Add an enemy type block. If the player collides with this it dies.
     *
     * @param position
     * @param file
     * @param name
     */
    protected Spatial createEnemy(Spatial model, float mass) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_ENEMY);
        baseApplication.getRenderManager().preloadScene(model);
        levelNode.attachChild(model);
//        log("box: " + model.getWorldBound());
        BoundingBox bb = (BoundingBox) model.getWorldBound();
        
        RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(bb.getXExtent(), bb.getYExtent(), bb.getZExtent())), mass);
        model.addControl(rigidBodyControl);

        baseApplication.getBulletAppState().getPhysicsSpace().add(rigidBodyControl);         

        return model;

    }
    
    protected Spatial createEnemy(Spatial model, float mass, boolean sphere) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_ENEMY);
        baseApplication.getRenderManager().preloadScene(model);
        levelNode.attachChild(model);
//        log("box: " + model.getWorldBound());
        BoundingBox bb = (BoundingBox) model.getWorldBound();
        CollisionShape collisionShape = null;
        if (sphere) {
            collisionShape = new SphereCollisionShape(bb.getYExtent());
        } else {
            collisionShape = new BoxCollisionShape(new Vector3f(bb.getXExtent(), bb.getYExtent(), bb.getZExtent()));
        }
        
        RigidBodyControl rigidBodyControl = new RigidBodyControl(collisionShape, mass);
        model.addControl(rigidBodyControl);

        baseApplication.getBulletAppState().getPhysicsSpace().add(rigidBodyControl);         

        return model;

    }

    /**
     * Add an obstacle type spatial. If the player collides with this it dies.
     * 
     */
    protected Spatial createObstacle(Spatial model, float mass) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_OBSTACLE);
        baseApplication.getRenderManager().preloadScene(model);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(mass);
        model.addControl(rigidBodyControl);

        baseApplication.getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
        
        levelNode.attachChild(model);

        return model;

    }

    /**
     * Add a vegetation type model. If this model is added the player can move
     * through it
     */
    protected Spatial createVegetation(Spatial model) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_VEGETATION);
        baseApplication.getRenderManager().preloadScene(model);
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
    protected Spatial createStatic(Spatial model, float mass) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_STATIC);
        baseApplication.getRenderManager().preloadScene(model);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(mass);
        model.addControl(rigidBodyControl);

        baseApplication.getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
        
        levelNode.attachChild(model);

        return model;

    }

    /**
     * This will add a pickup model to the scene. Like GOLD, etc.
     *
     * @param position
     * @param model
     * @param name
     */
    protected Spatial createPickup(Spatial model) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_PICKUP);
        baseApplication.getRenderManager().preloadScene(model);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(0);
        model.addControl(rigidBodyControl);

        baseApplication.getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
        
        levelNode.attachChild(model);

        return model;

    }

    /**
     * Adds the start point of the game.
     *
     * @param position
     */
    protected Spatial createStart(Spatial model) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_START);
        baseApplication.getRenderManager().preloadScene(model);
        levelNode.attachChild(model);
        
        startPosition = model.getLocalTranslation();

        return model;

    }

    /**
     * Adds the end point of the game.
     *
     * @param position
     */
    protected Spatial createEnd(Spatial model) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_END);
        baseApplication.getRenderManager().preloadScene(model);
        levelNode.attachChild(model);
        
        endPosition = model.getLocalTranslation();

        return model;

    }
    
    /**
     * Create a bullet in 3d and physics space
     * @param model
     * @return 
     */
    protected Spatial createBullet(Spatial model) {
        if (model == null) {
            return null;
        }
        model.setName(TYPE_BULLET);
        baseApplication.getRenderManager().preloadScene(model);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(0);
        model.addControl(rigidBodyControl);

        baseApplication.getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
        
        levelNode.attachChild(model);

        return model;

    }

    public BlockTerrainControl getBlockTerrainControl() {
        return blockTerrainControl;
    }

    public AbstractCubesTheme getCubesTheme() {
        return cubesTheme;
    }

}
