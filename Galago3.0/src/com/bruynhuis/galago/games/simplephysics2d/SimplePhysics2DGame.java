/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.simplephysics2d;

import com.bruynhuis.galago.app.Base2DApplication;

import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.BatchNode;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nidebruyn
 */
public abstract class SimplePhysics2DGame implements PhysicsCollisionListener {

    public static final String TYPE_SKY = "sky";
    public static final String TYPE_FRONT_LAYER1 = "frontlayer1";
    public static final String TYPE_FRONT_LAYER2 = "frontlayer2";
    public static final String TYPE_BACK_LAYER1 = "backlayer1";
    public static final String TYPE_BACK_LAYER2 = "backlayer2";
    public static final String TYPE_PLAYER = "player";
    public static final String TYPE_TERRAIN = "terrain";
    public static final String TYPE_ENEMY = "enemy";
    public static final String TYPE_OBSTACLE = "obstacle";
    public static final String TYPE_STATIC = "static";
    public static final String TYPE_PICKUP = "pickup";
    public static final String TYPE_START = "start";
    public static final String TYPE_END = "end";
    public static final String TYPE_VEGETATION = "vegetation";
    public static final String TYPE_BULLET = "bullet";
    public static final String SHAPE = "shape";
    
    protected Base2DApplication baseApplication;
    protected Node rootNode;
    protected Node levelNode;
    protected Node terrainNode;
    protected Node vegetationNode;
    protected Vector3f startPosition = Vector3f.ZERO;
    protected Vector3f endPosition = Vector3f.ZERO;
    protected boolean started = false;
    protected boolean paused = false;
    protected boolean gameOver = false;
    protected boolean loading = false;
    protected SimplePhysics2DPlayer player;
    protected SimplePhysics2DGameListener gameListener;
    protected Spatial lastCollidedSpatial;
    protected Spatial lastColliderSpatial;

    public SimplePhysics2DGame(Base2DApplication baseApplication, Node rootNode) {
        this.baseApplication = baseApplication;
        this.rootNode = rootNode;

    }

    public abstract void init();

    public void close() {
        loading = false;
        started = false;
        paused = false;
        gameOver = false;

        baseApplication.getDyn4jAppState().getPhysicsSpace().removePhysicsCollisionListener(this);

        if (player != null) {
            player.close();
        }

        levelNode.removeFromParent();
        rootNode.detachAllChildren();

        baseApplication.getDyn4jAppState().getPhysicsSpace().clear();
        baseApplication.getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(baseApplication);
        player = null;
        System.gc(); //Force memory to be released;

    }

    protected void log(String text) {
        System.out.println(text);
    }

    public void pause() {
        paused = true;
        baseApplication.getDyn4jAppState().setEnabled(false);
    }

    public void resume() {
        paused = false;
        baseApplication.getDyn4jAppState().setEnabled(true);
    }

    public void start(SimplePhysics2DPlayer physicsPlayer) {
        this.player = physicsPlayer;
        loading = false;
        started = true;
        paused = false;
        gameOver = false;
        this.player.start();
        baseApplication.getDyn4jAppState().setEnabled(true);
    }

    @Override
    public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB) {
        if (player != null) {

//            log("Collision: " + spatialA.getName() + " with " + spatialB.getName());

            if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_STATIC)) {
                fireCollisionPlayerWithStaticListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_TERRAIN)) {
                fireCollisionPlayerWithTerrainListener(lastCollidedSpatial, lastColliderSpatial);
                
            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_PLAYER)) {
                fireCollisionPlayerWithPlayerListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_PICKUP)) {
                fireCollisionPlayerWithPickupListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_ENEMY, TYPE_STATIC)) {
                fireCollisionEnemyWithStaticListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_ENEMY, TYPE_TERRAIN)) {
                fireCollisionEnemyWithTerrainListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_BULLET)) {
                fireCollisionPlayerWithBulletListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_OBSTACLE, TYPE_BULLET)) {
                fireCollisionObstacleWithBulletListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_ENEMY, TYPE_BULLET)) {
                fireCollisionEnemyWithBulletListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_TERRAIN, TYPE_BULLET)) {
                fireCollisionTerrainWithBulletListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_ENEMY)) {
                fireCollisionPlayerWithEnemyListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_OBSTACLE)) {
                fireCollisionPlayerWithObstacleListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_ENEMY, TYPE_ENEMY)) {
                fireCollisionEnemyWithEnemyListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_ENEMY, TYPE_OBSTACLE)) {
                fireCollisionEnemyWithObstacleListener(lastCollidedSpatial, lastColliderSpatial);

            }
        }
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
        paused = true;
        gameOver = true;
        fireGameOverListener();
    }

    public void doLevelCompleted() {
        started = false;
        paused = true;
        gameOver = true;
        fireGameCompletedListener();
    }

    public void addGameListener(SimplePhysics2DGameListener gameListener) {
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

    protected void fireCollisionPlayerWithTerrainListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithTerrain(collided, collider);
        }
    }
    
    protected void fireCollisionPlayerWithPlayerListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithPlayer(collided, collider);
        }
    }

    protected void fireCollisionEnemyWithStaticListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionEnemyWithStatic(collided, collider);
        }
    }

    protected void fireCollisionEnemyWithTerrainListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionEnemyWithTerrain(collided, collider);
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

    protected void fireCollisionTerrainWithBulletListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionTerrainWithBullet(collided, collider);
        }
    }

    public Base2DApplication getBaseApplication() {
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

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isLoading() {
        return loading;
    }

    public SimplePhysics2DPlayer getPlayer() {
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
        baseApplication.getDyn4jAppState().setEnabled(false);

        levelNode = new Node("LEVEL_NODE");
        rootNode.attachChild(levelNode);

        terrainNode = new BatchNode("TERRAIN_NODE");
        levelNode.attachChild(terrainNode);

        vegetationNode = new BatchNode("VEGETATION_NODE");
        levelNode.attachChild(vegetationNode);

        init();

        baseApplication.getDyn4jAppState().getPhysicsSpace().addPhysicsCollisionListener(this);

        pause();
        loading = false;

        //Optimize
        optimize();
    }

    public void optimize() {
        //Optimize
        if (baseApplication.isMobileApp()) {
            SpatialUtils.makeUnshaded(rootNode);
        }
        ((BatchNode) terrainNode).batch();
        ((BatchNode) vegetationNode).batch();
    }

    /**
     * Add an enemy type object. If the player collides with this it dies.
     *
     * @param bodyControl The body to control
     */
    public Spatial addEnemy(RigidBodyControl bodyControl) {
        bodyControl.getSpatial().setName(TYPE_ENEMY);
        baseApplication.getDyn4jAppState().getPhysicsSpace().add(bodyControl);

        levelNode.attachChild(bodyControl.getSpatial());
        return bodyControl.getSpatial();

    }

    /**
     * Add an obstacle type object. If the player collides with this it dies.
     *
     * @param bodyControl The body to control
     */
    public Spatial addObstacle(Spatial spatial) {
        spatial.setName(TYPE_OBSTACLE);
        if (spatial.getControl(RigidBodyControl.class) != null) {
            baseApplication.getDyn4jAppState().getPhysicsSpace().add(spatial.getControl(RigidBodyControl.class));
        }

        levelNode.attachChild(spatial);
        return spatial;

    }

    /**
     * Add a vegetation type model. If this model is added the player can move
     * through it
     */
    public Spatial addVegetation(Sprite sprite) {
        sprite.setName(TYPE_VEGETATION);
        vegetationNode.attachChild(sprite);
        return sprite;

    }

    public void fixTexture(MatParam mp) {
        if (mp != null) {
            MatParamTexture mpt = (MatParamTexture) mp;
            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }
    }

    /**
     * Add an static type object.
     *
     * @param bodyControl The body to control
     */
    public Spatial addStatic(RigidBodyControl bodyControl) {
        bodyControl.getSpatial().setName(TYPE_STATIC);
        baseApplication.getDyn4jAppState().getPhysicsSpace().add(bodyControl);

        levelNode.attachChild(bodyControl.getSpatial());
        return bodyControl.getSpatial();

    }

    /**
     * Add an terrain type object.
     *
     * @param bodyControl The body to control
     */
    public Spatial addTerrain(Spatial spatial) {
        spatial.setName(TYPE_TERRAIN);
        if (spatial.getControl(RigidBodyControl.class) != null) {
            baseApplication.getDyn4jAppState().getPhysicsSpace().add(spatial.getControl(RigidBodyControl.class));
        }

        terrainNode.attachChild(spatial);
        return spatial;

    }

    /**
     * Add a type pickup. If the player collides with this it gains something.
     *
     * @param bodyControl The body to control
     */
    public Spatial addPickup(Sprite sprite) {
        sprite.setName(TYPE_PICKUP);
        levelNode.attachChild(sprite);
        sprite.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (isStarted() && !isGameOver() && !isPaused()) {

                    float distance = new Vector2f(player.getPosition().x, player.getPosition().y).distance(new Vector2f(spatial.getLocalTranslation().x, spatial.getLocalTranslation().y));

                    //Check if the player is in range
//                    log("player=" + player.getPosition());
//                    log("pickup=" + spatial.getLocalTranslation());
//                    log("distance=" + distance);
                    if (distance < player.getSize()) {
                        fireCollisionPlayerWithPickupListener(spatial, player.getPlayerNode());
                    }

                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });
        return sprite;

    }

    /**
     * Adds the start position to the level
     *
     * @param sprite
     * @return
     */
    public Spatial addStart(Sprite sprite) {
        sprite.setName(TYPE_START);
        levelNode.attachChild(sprite);
        startPosition = sprite.getWorldTranslation().clone();
        return sprite;

    }

    /**
     * This method will add the end marker.
     *
     * @param sprite
     * @return
     */
    public Spatial addEnd(Sprite sprite) {
        sprite.setName(TYPE_END);
        levelNode.attachChild(sprite);
        endPosition = sprite.getWorldTranslation().clone();

        sprite.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (isStarted() && !isGameOver() && !isPaused()) {
                    
                    float distance = new Vector2f(player.getPosition().x, player.getPosition().y).distance(new Vector2f(spatial.getLocalTranslation().x, spatial.getLocalTranslation().y));

                    if (distance < player.getSize() * 0.5f) {
                        doLevelCompleted();
                    }

                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });
        return sprite;

    }

    /**
     * Add a bullet type object. If the player collides with this it dies.
     *
     * @param bodyControl The body to control
     */
    public Spatial addBullet(RigidBodyControl bodyControl) {
        bodyControl.getSpatial().setName(TYPE_BULLET);
        baseApplication.getDyn4jAppState().getPhysicsSpace().add(bodyControl);

        levelNode.attachChild(bodyControl.getSpatial());
        return bodyControl.getSpatial();

    }

    /**
     * Create background sky
     */
    public void addSky(Sprite sprite, final float parallaxEffectSpeed) {

        sprite.setQueueBucket(RenderQueue.Bucket.Opaque);
        addVegetation(sprite);
        sprite.setName(TYPE_SKY);

        sprite.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                //Make the background stick to the camera
                spatial.setLocalTranslation(baseApplication.getCamera().getLocation().x * parallaxEffectSpeed, baseApplication.getCamera().getLocation().y * parallaxEffectSpeed, spatial.getLocalTranslation().z);
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

    }

    public Node getTerrainNode() {
        return terrainNode;
    }

    /**
     * A public helper method that can be called when the map or level needs to
     * be cleared.
     */
    public void clear() {

        levelNode.removeFromParent();
        baseApplication.getDyn4jAppState().getPhysicsSpace().clear();
        baseApplication.getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(baseApplication);

        levelNode = new Node("LEVEL_NODE");
        rootNode.attachChild(levelNode);

        terrainNode = new BatchNode("TERRAIN_NODE");
        levelNode.attachChild(terrainNode);

        vegetationNode = new BatchNode("VEGETATION_NODE");
        levelNode.attachChild(vegetationNode);

    }

}
