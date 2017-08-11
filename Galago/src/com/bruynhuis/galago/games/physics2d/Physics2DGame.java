/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.physics2d;

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
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.BatchNode;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author nidebruyn
 */
public abstract class Physics2DGame implements PhysicsCollisionListener {

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
    protected Physics2DPlayer player;
    protected Physics2DGameListener gameListener;
    protected Spatial lastCollidedSpatial;
    protected Spatial lastColliderSpatial;
    public static final String fileExtension = ".map";
    private TileMap tileMap;
    private boolean edit = false;
    private String saveFile;
    private File file;
    protected ArrayList<String> terrainList = new ArrayList<String>();
    protected ArrayList<String> enemyList = new ArrayList<String>();
    protected ArrayList<String> obstacleList = new ArrayList<String>();
    protected ArrayList<String> staticList = new ArrayList<String>();
    protected ArrayList<String> pickupList = new ArrayList<String>();
    protected ArrayList<String> vegetationList = new ArrayList<String>();
    protected ArrayList<String> skyList = new ArrayList<String>();
    protected ArrayList<String> frontLayer1List = new ArrayList<String>();
    protected ArrayList<String> frontLayer2List = new ArrayList<String>();
    protected ArrayList<String> backLayer1List = new ArrayList<String>();
    protected ArrayList<String> backLayer2List = new ArrayList<String>();
    protected ArrayList<String> startList = new ArrayList<String>();
    protected ArrayList<String> endList = new ArrayList<String>();

    public Physics2DGame(Base2DApplication baseApplication, Node rootNode) {
        this.baseApplication = baseApplication;
        this.rootNode = rootNode;

    }

    public abstract void init();

    protected abstract void initTerrainList(ArrayList<String> list);

    protected abstract void initEnemyList(ArrayList<String> list);

    protected abstract void initObstacleList(ArrayList<String> list);

    protected abstract void initStaticList(ArrayList<String> list);

    protected abstract void initPickupList(ArrayList<String> list);

    protected abstract void initVegetationList(ArrayList<String> list);

    protected abstract void initSkyList(ArrayList<String> list);

    protected abstract void initFrontLayer1List(ArrayList<String> list);

    protected abstract void initFrontLayer2List(ArrayList<String> list);

    protected abstract void initBackLayer1List(ArrayList<String> list);

    protected abstract void initBackLayer2List(ArrayList<String> list);

    protected abstract void initStartList(ArrayList<String> list);

    protected abstract void initEndList(ArrayList<String> list);

    /**
     * This method will add a tile type
     *
     * @param tile
     */
    public void addTile(Tile tile) {
        //Add the tile to the tile map
        if (!tileMap.getTiles().contains(tile)) {
            tileMap.getTiles().add(tile);
        }

        if (tile.getSpatial() == null) {
            Spatial spatial = getItem(tile.getUid());
            if (spatial != null) {
                tile.setSpatial(spatial);
            } else {
                throw new RuntimeException("ERROR: Tile spatial may not be null, check the getItem() method implementation. Missing sprite returned.");
            }
            
        }

        //Now add the tile to the levelnode
        //We have to determine to what type this tile exist
        //Step 1: Update the position
        if (tile.getSpatial().getControl(RigidBodyControl.class) != null && !edit) {
            if (tile.getSpatial().getUserData(SHAPE) != null) {
                CollisionShape collisionShape = (CollisionShape) tile.getSpatial().getUserData(SHAPE);
                collisionShape.setLocation(tile.getxPos(), tile.getyPos());
                tile.getSpatial().setLocalTranslation(new Vector3f(tile.getxPos(), tile.getyPos(), tile.getzPos()));
            } else {
                tile.getSpatial().getControl(RigidBodyControl.class).setPhysicLocation(new Vector3f(tile.getxPos(), tile.getyPos(), tile.getzPos()));
            }

        } else if (tile.getSpatial().getUserData(SHAPE) != null && !edit) {
            CollisionShape collisionShape = (CollisionShape) tile.getSpatial().getUserData(SHAPE);
            collisionShape.setLocation(tile.getxPos(), tile.getyPos());
            tile.getSpatial().setLocalTranslation(new Vector3f(tile.getxPos(), tile.getyPos(), tile.getzPos()));

        } else {
            tile.getSpatial().setLocalTranslation(new Vector3f(tile.getxPos(), tile.getyPos(), tile.getzPos()));
        }

        //Add to the world
        String uid = tile.getUid();
        if (terrainList.contains(uid)) {
            addTerrain(tile.getSpatial());

        } else if (enemyList.contains(uid)) {
            addEnemy(tile.getSpatial().getControl(RigidBodyControl.class));

        } else if (obstacleList.contains(uid)) {
            addObstacle(tile.getSpatial());

        } else if (staticList.contains(uid)) {
            addStatic(tile.getSpatial().getControl(RigidBodyControl.class));

        } else if (pickupList.contains(uid)) {
            addPickup((Sprite) tile.getSpatial());

        } else if (vegetationList.contains(uid)) {
            addVegetation((Sprite) tile.getSpatial());

        } else if (skyList.contains(uid)) {
            addSky((Sprite) tile.getSpatial(), 1f);

        } else if (frontLayer1List.contains(uid)) {
            addSky((Sprite) tile.getSpatial(), -0.94f);

        } else if (frontLayer2List.contains(uid)) {
            addSky((Sprite) tile.getSpatial(), -0.98f);

        } else if (backLayer1List.contains(uid)) {
            addSky((Sprite) tile.getSpatial(), 0.94f);

        } else if (backLayer2List.contains(uid)) {
            addSky((Sprite) tile.getSpatial(), 0.98f);

        } else if (startList.contains(uid)) {
            addStart((Sprite) tile.getSpatial());

        } else if (endList.contains(uid)) {
            addEnd((Sprite) tile.getSpatial());

        }
    }

    private String getTileAsText(Tile tile) {
        return "Tile{" + "xPos=" + tile.getxPos() + ", yPos=" + tile.getyPos() + ", zPos=" + tile.getzPos() + ", uid=" + tile.getUid() + ", spatial=" + tile.getSpatial() + '}';
    }

    /**
     * This will remove a selected sprite from the level.
     *
     * @param sprite
     */
    protected void removeTile(Sprite sprite) {
        Tile selectedTile = null;
        for (int i = 0; i < tileMap.getTiles().size(); i++) {
            Tile tile = tileMap.getTiles().get(i);
            if (tile.getSpatial() != null && tile.getSpatial().equals(sprite)) {
                if (tile.getSpatial().getControl(RigidBodyControl.class) != null) {
                    baseApplication.getDyn4jAppState().getPhysicsSpace().remove(sprite);
                }
                sprite.removeFromParent();
                selectedTile = tile;
            }
        }

        if (selectedTile != null) {
            tileMap.getTiles().remove(selectedTile);
        }
    }

    protected void removeTileAtPosition(Vector3f position) {
        Tile selectedTile = null;
        for (int i = 0; i < tileMap.getTiles().size(); i++) {
            Tile tile = tileMap.getTiles().get(i);
            if (((int) tile.getxPos() == (int) position.getX())
                    && ((int) tile.getyPos() == (int) position.getY())) {
                selectedTile = tile;
            }
        }

        if (selectedTile != null) {
            if (selectedTile.getSpatial().getControl(RigidBodyControl.class) != null) {
                baseApplication.getDyn4jAppState().getPhysicsSpace().remove(selectedTile.getSpatial());
            }
            if (selectedTile.getSpatial() != null) {
                selectedTile.getSpatial().removeFromParent();
            }
            tileMap.getTiles().remove(selectedTile);
        }
    }

    /**
     * This method must be implemented to load the different sprite object. The
     * item parameter must match one of the different items defined in the init
     * lists.
     *
     * @param item
     * @return
     */
    public abstract Sprite getItem(String item);

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

    public void start(Physics2DPlayer physicsPlayer) {
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

    public void addGameListener(Physics2DGameListener gameListener) {
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

    public Physics2DPlayer getPlayer() {
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

        initStartList(startList);
        initEndList(endList);
        initSkyList(skyList);
        initBackLayer1List(backLayer1List);
        initBackLayer2List(backLayer2List);
        initEnemyList(enemyList);
        initFrontLayer1List(frontLayer1List);
        initFrontLayer2List(frontLayer2List);
        initObstacleList(obstacleList);
        initPickupList(pickupList);
        initStaticList(staticList);
        initTerrainList(terrainList);
        initVegetationList(vegetationList);

        levelNode = new Node("LEVEL_NODE");
        rootNode.attachChild(levelNode);

        terrainNode = new BatchNode("TERRAIN_NODE");
        levelNode.attachChild(terrainNode);

        vegetationNode = new BatchNode("VEGETATION_NODE");
        levelNode.attachChild(vegetationNode);

        //If no tile map exist
        if (tileMap == null) {
            tileMap = new TileMap();
        }
        //Load the existing terrain tiles
        for (int i = 0; i < tileMap.getTiles().size(); i++) {
            Tile tile = tileMap.getTiles().get(i);
            Sprite sprite = getItem(tile.getUid());
            if (sprite != null) {
                tile.setSpatial(sprite);
                addTile(tile);
            }

        }

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
//            SpatialUtils.makeUnshaded(rootNode);
        }
        if (!edit) {
            ((BatchNode) terrainNode).batch();
//            ((BatchNode) vegetationNode).batch();
        }
    }

    /**
     * Add an enemy type object. If the player collides with this it dies.
     *
     * @param bodyControl The body to control
     */
    public Spatial addEnemy(RigidBodyControl bodyControl) {
        bodyControl.getSpatial().setName(TYPE_ENEMY);
        if (!edit) {
            baseApplication.getDyn4jAppState().getPhysicsSpace().add(bodyControl);
        }
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
        if (!edit && spatial.getControl(RigidBodyControl.class) != null) {
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
        if (!edit) {
            baseApplication.getDyn4jAppState().getPhysicsSpace().add(bodyControl);
        }
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
        if (!edit && spatial.getControl(RigidBodyControl.class) != null) {
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
        if (!edit) {
            baseApplication.getDyn4jAppState().getPhysicsSpace().add(bodyControl);
        }
        levelNode.attachChild(bodyControl.getSpatial());
        return bodyControl.getSpatial();

    }

    /**
     * Create background sky
     */
    protected void addSky(Sprite sprite, final float parallaxEffectSpeed) {
        log("Add sky");
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

        tileMap.getTiles().clear();

        levelNode = new Node("LEVEL_NODE");
        rootNode.attachChild(levelNode);

        terrainNode = new BatchNode("TERRAIN_NODE");
        levelNode.attachChild(terrainNode);

        vegetationNode = new BatchNode("VEGETATION_NODE");
        levelNode.attachChild(vegetationNode);

    }

    public ArrayList<String> getTypeList(String type) {
        ArrayList<String> list = null;

        if (type.equals(TYPE_ENEMY)) {
            list = enemyList;

        } else if (type.equals(TYPE_OBSTACLE)) {
            list = obstacleList;

        } else if (type.equals(TYPE_PICKUP)) {
            list = pickupList;

        } else if (type.equals(TYPE_STATIC)) {
            list = staticList;

        } else if (type.equals(TYPE_TERRAIN)) {
            list = terrainList;

        } else if (type.equals(TYPE_VEGETATION)) {
            list = vegetationList;

        } else if (type.equals(TYPE_BACK_LAYER1)) {
            list = backLayer1List;

        } else if (type.equals(TYPE_BACK_LAYER2)) {
            list = backLayer2List;

        } else if (type.equals(TYPE_END)) {
            list = endList;

        } else if (type.equals(TYPE_FRONT_LAYER1)) {
            list = frontLayer1List;

        } else if (type.equals(TYPE_FRONT_LAYER2)) {
            list = frontLayer2List;

        } else if (type.equals(TYPE_SKY)) {
            list = skyList;

        } else if (type.equals(TYPE_START)) {
            list = startList;

        }

        //If nothing found we return te selector
        if (list == null || list.size() == 0) {
            list = new ArrayList<String>();

        }

        return list;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    /**
     *
     *
     * ################ ALL LEVEL PERSISTANCE GOES HERE
     *
     *
     *
     */
    public void edit(String levelfile) {
//        setOptimize(false);
        edit = true;
        readEditFile(levelfile);
    }

    public void test(String levelfile) {
//        setOptimize(true);
        edit = false;
        readEditFile(levelfile);
    }

    public void play(String levelfile) {
//        setOptimize(true);
        edit = false;
        readAssetFile(levelfile);
    }

    /**
     * This method must be called when reading a level file and not when editing
     * it.
     *
     * @param levelfile
     */
    protected void readAssetFile(String levelfile) {

        //Read from the assets folder
        this.saveFile = levelfile;

        InputStream levelInputStream = null;

        try {
            Platform platform = JmeSystem.getPlatform();

            if (platform.compareTo(Platform.Android_ARM5) == 0 || platform.compareTo(Platform.Android_ARM6) == 0 || platform.compareTo(Platform.Android_ARM7) == 0) {
                levelInputStream = JmeSystem.getResourceAsStream("/assets/Levels/" + levelfile);

            } else {
                levelInputStream = JmeSystem.getResourceAsStream("/Levels/" + levelfile);
            }
        } catch (UnsupportedOperationException e) {
            Logger.getLogger(Physics2DGame.class.getName()).log(java.util.logging.Level.INFO, null, e);
            //Load the default
            levelInputStream = JmeSystem.getResourceAsStream("/assets/Levels/" + levelfile);

        }

        try {
            if (levelInputStream == null) {
                //Load a default
                log("Loading level");
                tileMap = new TileMap();

            } else {
                //Load the level.
//                tileData = new TileData();
                log("Loading tile data from file");
                ObjectInputStream in = new ObjectInputStream(levelInputStream);
                try {
                    tileMap = (TileMap) in.readObject();

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                }

            }

        } catch (IOException ex) {
            Logger.getLogger(Physics2DGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }

    /**
     * Read the current level that is designed.
     */
    protected void readEditFile(String levelfile) {

        this.saveFile = levelfile;
        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists()) {
            try {
                file = new File(folder.getAbsolutePath() + File.separator + levelfile);
                if (file.exists()) {
                    FileInputStream fileIn = new FileInputStream(file);

                    try {
                        ObjectInputStream in = new ObjectInputStream(fileIn);
                        tileMap = (TileMap) in.readObject();

                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } catch (EOFException eofe) {
                        Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.WARNING, null, eofe);
                        file.createNewFile();
                        tileMap = new TileMap();

                    }

                } else {
                    file.createNewFile();
                    tileMap = new TileMap();

                }

            } catch (IOException ex) {
                Logger.getLogger(Physics2DGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This is a helper method that can be used to save the new level.
     */
    public void save() {
        if (tileMap != null) {
            File folder = JmeSystem.getStorageFolder();

            if (folder != null && folder.exists()) {
                if (file != null) {
                    FileOutputStream fileOut = null;
                    ObjectOutputStream out = null;
                    try {
                        fileOut = new FileOutputStream(file);
                        out = new ObjectOutputStream(fileOut);
                        tileMap.setSaved(true);
                        out.writeObject(tileMap);

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } finally {
                        if (fileOut != null) {
                            try {
                                fileOut.close();
                            } catch (IOException ex) {
                                Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                            }
                        }


                    }

                }
            }
        }

    }

    public boolean isEdit() {
        return edit;
    }
}
