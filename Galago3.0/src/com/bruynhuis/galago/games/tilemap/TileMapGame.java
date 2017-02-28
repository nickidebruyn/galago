/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.tilemap;

import com.bruynhuis.galago.app.Base3DApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import jme3tools.optimize.GeometryBatchFactory;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.games.platform.PlatformGame;
import com.bruynhuis.galago.save.GameSaves;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.shape.Quad;
import java.io.EOFException;

/**
 *
 * This game type does not make use of any physics. It will make use of ray
 * cating to check for collisions.
 *
 *
 * @author nidebruyn
 */
public abstract class TileMapGame implements PhysicsCollisionListener, PhysicsTickListener {

    public static final String fileExtension = ".map";
    private TileData tileData;
    private String saveFile;
    private File file;
    public static final String BLANK = "blank";
    public static final String TYPE_TERRAIN = "terrain";
    public static final String TYPE_PLAYER = "player";
    public static final String TYPE_ENEMY = "enemy";
    public static final String TYPE_OBSTACLE = "obstacle";
    public static final String TYPE_STATIC = "static";
    public static final String TYPE_PICKUP = "pickup";
    public static final String TYPE_VEGETATION = "vegetation";
    public static final String TYPE_BULLET = "bullet";
    public static final String TYPE = "type";
    protected Base3DApplication baseApplication;
    protected Node rootNode;
    protected BatchNode levelNode;
    protected Material surfaceMaterial;
    protected Vector3f startPosition = Vector3f.ZERO;
    protected Vector3f endPosition = Vector3f.ZERO;
    protected AmbientLight ambientLight;
    protected DirectionalLight sunLight;
    protected boolean started = false;
    protected boolean paused = false;
    protected boolean loading = false;
    protected TileMapPlayer player;
    protected TileMapGameListener gameListener;
    protected boolean optimize = false;
    protected Spatial lastCollidedSpatial;
    protected Spatial lastColliderSpatial;
    protected ArrayList<Spatial> terrainList = new ArrayList<>();
    protected ArrayList<Spatial> enemyList = new ArrayList<>();
    protected ArrayList<Spatial> obstacleList = new ArrayList<>();
    protected ArrayList<Spatial> staticList = new ArrayList<>();
    protected ArrayList<Spatial> pickupList = new ArrayList<>();
    protected ArrayList<Spatial> vegetationList = new ArrayList<>();
    protected ArrayList<Spatial> blankList = new ArrayList<>();
    protected Node mapPack;
    protected float tileSize = 2;
    protected int mapSize = 24;
    private boolean edit = false;
    private boolean diagonal = false;

    public TileMapGame(Base3DApplication baseApplication, Node rootNode, int mapSize, float tileSize, boolean diagonal) {
        this.baseApplication = baseApplication;
        this.rootNode = rootNode;
        this.mapSize = mapSize;
        this.tileSize = tileSize;
        this.diagonal = diagonal;

    }

    protected abstract boolean isPhysicsEnabled();

    /**
     * Initialize a default map
     */
    protected void initMap() {
        tileData.setMap(new Tile[mapSize][mapSize]);
        for (int i = 0; i < tileData.getMap().length; i++) {
            Tile[] row = tileData.getMap()[i];
            for (int j = 0; j < row.length; j++) {
                tileData.getMap()[i][j] = new Tile();
                tileData.getMap()[i][j].setItem(null);
                tileData.getMap()[i][j].setxPos(i);
                tileData.getMap()[i][j].setzPos(j);
                tileData.getMap()[i][j].setWalkable(false);
                tileData.getMap()[i][j].setItemAngle(0);

            }
        }
    }

    protected abstract void preInit();

    protected abstract void postInit();

    protected abstract Material initSurfaceMaterial();

    /**
     * This method must load the map pack model which will contain all models
     * needed for the level.
     *
     * @return
     */
    protected abstract Node initMapPack();

    /**
     * Filter if this spatial must be used as an enemy.
     *
     * @param spatial
     * @return
     */
    protected abstract Spatial filterEnemy(Spatial spatial);

    /**
     * Filter to get all terrain models.
     *
     * @param spatial
     * @return
     */
    protected abstract Spatial filterTerrain(Spatial spatial);

    /**
     * Filter if this spatial must be used as an obstacle.
     *
     * @param spatial
     * @return
     */
    protected abstract Spatial filterObstacle(Spatial spatial);

    /**
     * Filter if this spatial must be used as a static.
     *
     * @param spatial
     * @return
     */
    protected abstract Spatial filterStatic(Spatial spatial);

    /**
     * Filter if this spatial must be used as a pickup.
     *
     * @param spatial
     * @return
     */
    protected abstract Spatial filterPickup(Spatial spatial);

    /**
     * Filter if this spatial must be used as a vegetation.
     *
     * @param spatial
     * @return
     */
    protected abstract Spatial filterVegetation(Spatial spatial);

    /**
     * Filter if this spatial must be used as a dynamic.
     *
     * @param spatial
     * @return
     */
    protected abstract Spatial filterDynamic(Spatial spatial);

    public void close() {
        loading = false;
        started = false;
        paused = false;

        preClose();

        if (sunLight != null) {
            levelNode.removeLight(sunLight);
        }

        if (ambientLight != null) {
            levelNode.removeLight(ambientLight);
        }

        if (isPhysicsEnabled()) {
            baseApplication.getBulletAppState().getPhysicsSpace().removeCollisionListener(this);
            baseApplication.getBulletAppState().getPhysicsSpace().removeTickListener(this);
            baseApplication.getBulletAppState().setSpeed(1);
        }


        if (player != null) {
            player.close();
        }

        levelNode.removeFromParent();
        rootNode.detachAllChildren();

        if (isPhysicsEnabled()) {
            baseApplication.getBulletAppState().getPhysicsSpace().destroy();
            baseApplication.getBulletAppState().getPhysicsSpace().create();
        }

        player = null;
        System.gc(); //Force memory to be released;

    }

    protected abstract void preClose();

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
        sunLight.setDirection(sunDirection);
        levelNode.addLight(sunLight);
    }

    /**
     * Creates a surface spatial. This is by default a quad but it could be a
     * terrain.
     *
     * @return
     */
    protected Spatial createSurface() {
        Quad quad = new Quad(mapSize * tileSize, mapSize * tileSize);
        Geometry geometry = new Geometry(BLANK, quad);
        quad.scaleTextureCoordinates(new Vector2f(mapSize, mapSize));
        geometry.setMaterial(surfaceMaterial);
        geometry.rotate(FastMath.DEG_TO_RAD * 90, 0, 0);
        geometry.setShadowMode(RenderQueue.ShadowMode.Receive);
        return geometry;
    }

    public void pause() {
        paused = true;
        if (isPhysicsEnabled()) {
            baseApplication.getBulletAppState().setEnabled(false);
        }

    }

    public void resume() {
        paused = false;
        if (isPhysicsEnabled()) {
            baseApplication.getBulletAppState().setEnabled(true);
        }

    }

    public void start(TileMapPlayer tileMapPlayer) {
        this.player = tileMapPlayer;
        loading = false;
        started = true;
        paused = false;
        this.player.start();
        if (isPhysicsEnabled()) {
            baseApplication.getBulletAppState().setEnabled(true);
        }

    }

    /**
     * This will only get called if the tile game has physics. If not it will
     * use raycasting to detect collisions.
     *
     * @param event
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
//        log("Collision: " + player);

        if (player != null) {

//            log("Collision: " + event.getNodeA().getName() + " with " + event.getNodeB().getName());

            if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_STATIC)) {
                fireCollisionPlayerWithStaticListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_TERRAIN)) {
                fireCollisionPlayerWithTerrainListener(lastCollidedSpatial, lastColliderSpatial);

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
                fireCollisionEnemyWithObstacleListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_ENEMY, TYPE_TERRAIN)) {
                fireCollisionEnemyWithTerrainListener(lastCollidedSpatial, lastColliderSpatial);

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

    /**
     * This method is for internal use only. It will fire the appropriate even.
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
    protected boolean checkCollisionWithNonPhysicsType(Spatial sA, Spatial sB, String collider, String type) {
        boolean collision = sA.getName() != null && sB.getName() != null
                && sA.getName().startsWith(collider) && sB.getName().startsWith(type);

        return collision;
    }

    public void doGameOver() {
        started = false;
        paused = true;
        fireGameOverListener();
    }

    public void addGameListener(TileMapGameListener gameListener) {
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

    protected void fireCollisionPlayerWithTerrainListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithTerrain(collided, collider);
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

    protected void fireCollisionEnemyWithTerrainListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionEnemyWithTerrain(collided, collider);
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

    protected void fireCollisionEnemyWithBulletListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionEnemyWithBullet(collided, collider);
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

    public TileMapPlayer getPlayer() {
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

        if (isPhysicsEnabled()) {
            baseApplication.getBulletAppState().setEnabled(false);
        }


        preInit();

        //Load the map pack models 
        mapPack = initMapPack();
        surfaceMaterial = initSurfaceMaterial();

        List<Spatial> children = mapPack.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Spatial child = children.get(i);
//            TangentBinormalGenerator.generate(child);

            //Filter enemies
            Spatial filtered = filterEnemy(child);
            if (filtered != null) {
                filtered.setName(TYPE_ENEMY + "_" + filtered.getName());
                enemyList.add(filtered);
            }

            //Filter obstacle
            filtered = filterObstacle(child);
            if (filtered != null) {
                filtered.setName(TYPE_OBSTACLE + "_" + filtered.getName());
                obstacleList.add(filtered);
            }

            //Filter enemies
            filtered = filterPickup(child);
            if (filtered != null) {
                filtered.setName(TYPE_PICKUP + "_" + filtered.getName());
                pickupList.add(filtered);
            }

            //Filter static
            filtered = filterStatic(child);
            if (filtered != null) {
                filtered.setName(TYPE_STATIC + "_" + filtered.getName());
                staticList.add(filtered);
            }

            //Filter vegetation
            filtered = filterVegetation(child);
            if (filtered != null) {
                filtered.setName(TYPE_VEGETATION + "_" + filtered.getName());
                vegetationList.add(filtered);
            }

            //Filter terrain
            filtered = filterTerrain(child);
            if (filtered != null) {
                filtered.setName(TYPE_TERRAIN + "_" + filtered.getName());
                terrainList.add(filtered);
            }


        }

        //create t
        levelNode = new BatchNode("LEVEL_NODE");
        rootNode.attachChild(levelNode);

        //initialize the map
        loadSurface();

        //Load the static level models into the scene
        for (int r = 0; r < tileData.getMap().length; r++) {
            Tile[] row = tileData.getMap()[r];
            for (int c = 0; c < row.length; c++) {
                Tile tile = row[c];
                updateTile(tile);
            }
        }

        if (optimize) {
            //TODO:
        }

        postInit();

        pause();
        loading = false;

    }

    protected void loadSurface() {
        Spatial surface = createSurface();
        surface.move(-tileSize * 0.5f, -0.005f, -tileSize * 0.5f);
        surface.setShadowMode(RenderQueue.ShadowMode.Receive);
        rootNode.attachChild(surface);
    }

    /**
     * Add an enemy type block. If the player collides with this it dies.
     *
     * @param model
     * @param mass
     * @param sphere
     * @return
     */
    protected Spatial createEnemy(Spatial model, float mass, boolean sphere) {
        if (model == null) {
            return null;
        }

        if (!edit) {
            BoundingBox bb = (BoundingBox) model.getWorldBound();
            CollisionShape collisionShape = null;
            if (sphere) {
                collisionShape = new SphereCollisionShape(bb.getYExtent());
            } else {
                collisionShape = new BoxCollisionShape(new Vector3f(bb.getXExtent(), bb.getYExtent(), bb.getZExtent()));
            }

            if (isPhysicsEnabled()) {
                RigidBodyControl rigidBodyControl = new RigidBodyControl(collisionShape, mass);
                model.addControl(rigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
            } else {
                model.addControl(new RayColliderControl(this, new Vector3f(1, 1, 1)));
            }
        }

        levelNode.attachChild(model);

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

        if (!edit) {

            if (isPhysicsEnabled()) {
                RigidBodyControl rigidBodyControl = new RigidBodyControl(mass);
                model.addControl(rigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
            }
        }


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
        levelNode.attachChild(model);

        return model;

    }

    protected Spatial createTerrain(Spatial model) {
        if (model == null) {
            return null;
        }

        if (!edit) {

            if (isPhysicsEnabled()) {
                RigidBodyControl rigidBodyControl = new RigidBodyControl(0);
                model.addControl(rigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
            }
        }

        levelNode.attachChild(model);

        return model;

    }

    /**
     * Create static
     *
     * @param model
     * @param mass
     * @return
     */
    protected Spatial createStatic(Spatial model) {
        if (model == null) {
            return null;
        }
        if (!edit) {
            if (isPhysicsEnabled()) {
                RigidBodyControl rigidBodyControl = new RigidBodyControl(0);
                model.addControl(rigidBodyControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
            }
        }


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

        if (!edit) {
            if (isPhysicsEnabled()) {
                BoundingBox bb = (BoundingBox) model.getWorldBound();
                CollisionShape collisionShape = new SphereCollisionShape(bb.getYExtent());
                GhostControl ghostControl = new GhostControl(collisionShape);
                model.addControl(ghostControl);
                baseApplication.getBulletAppState().getPhysicsSpace().add(ghostControl);
            }
        }


        levelNode.attachChild(model);

        return model;

    }

    /**
     *
     * HELPER METHODS
     *
     *
     *
     * @return
     */
    /**
     * Returns a tile at a specific position
     *
     * @param x
     * @param z
     * @return
     */
    public Tile getTile(int x, int z) {
        return tileData.getMap()[x][z];
    }

    /**
     * Get the closest tile to the x and z position
     *
     * @param x
     * @param z
     * @return
     */
    public Tile getClosestTerrainTile(float x, float z) {
        Tile selectedTile = null;
        Vector3f clickPos = new Vector3f(x, 0, z);
        float distance = 1000f;

        for (int r = 0; r < tileData.getMap().length; r++) {
            Tile[] row = tileData.getMap()[r];
            for (int c = 0; c < row.length; c++) {
                Tile tile = row[c];

                Vector3f tilePos = new Vector3f(tile.getxPos() * tileSize, 0, tile.getzPos() * tileSize);
                float spatialToTileDistance = tilePos.distance(clickPos);

                if (spatialToTileDistance < distance) {
                    distance = spatialToTileDistance;
                    selectedTile = tile;
                }

            }
        }

        //If not a tile was selected we clear selection
        if (selectedTile != null && !selectedTile.isWalkable()) {
            selectedTile = null;
        }

        return selectedTile;
    }

    /**
     * Helper method to determine the closest tile to the selected point
     *
     * @param x
     * @param z
     * @return
     */
    public Tile getTileFromContactPoint(float x, float z) {
        Tile selectedTile = null;
        Vector3f clickPos = new Vector3f(x, 0, z);
        float distance = 1000f;

        for (int r = 0; r < tileData.getMap().length; r++) {
            Tile[] row = tileData.getMap()[r];
            for (int c = 0; c < row.length; c++) {
                Tile tile = row[c];
                Vector3f tilePos = new Vector3f(tile.getxPos() * tileSize, 0, tile.getzPos() * tileSize);
                float spatialToTileDistance = tilePos.distance(clickPos);

                if (spatialToTileDistance < distance) {
                    distance = spatialToTileDistance;
                    selectedTile = tile;
                }

            }
        }

        return selectedTile;
    }

    /**
     * Try to fnd the child
     *
     * @param name
     * @return
     */
    protected Spatial getChild(String name) {
        Spatial s = null;

        if (name != null) {
            String type = BLANK;
            if (name.startsWith(TYPE_TERRAIN)) {
                type = TYPE_TERRAIN;

            } else if (name.startsWith(TYPE_OBSTACLE)) {
                type = TYPE_OBSTACLE;

            } else if (name.startsWith(TYPE_PICKUP)) {
                type = TYPE_PICKUP;

            } else if (name.startsWith(TYPE_STATIC)) {
                type = TYPE_STATIC;

            } else if (name.startsWith(TYPE_VEGETATION)) {
                type = TYPE_VEGETATION;

            } else if (name.startsWith(TYPE_ENEMY)) {
                type = TYPE_ENEMY;

            }

            if (type != null) {
                ArrayList<Spatial> list = getTileListForType(type);
                for (int i = 0; i < list.size(); i++) {
                    Spatial spatial = list.get(i);
                    if (spatial.getName().equals(name)) {
                        s = spatial;
                        break;
                    }
                }
            }
        }

        return s;
    }

    /**
     * Update a selected tile at a position
     *
     * @param x
     * @param z
     * @param model
     */
    public Tile updateTile(Tile tile) {
//        Tile tile = map[x][z];

        float xPos = tile.getxPos() * tileSize;
        float zPos = tile.getzPos() * tileSize;

        //First we remove the exiting spatials
        if (tile.getItemSpatial() != null) {
            tile.getItemSpatial().removeFromParent();
        }

        if (tile.getEnemySpatial() != null) {
            tile.getEnemySpatial().removeFromParent();
        }

        //Update the spatial item if any
        Spatial modelItem = getChild(tile.getItem());
        if (modelItem != null) {
            modelItem = modelItem.clone();
            modelItem.setName(tile.getItem());
            modelItem.setLocalTranslation(xPos, 0, zPos);
            modelItem.setLocalRotation(new Quaternion().fromAngleAxis(tile.getItemAngle() * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));

            tile.setItemSpatial(modelItem);

            //Here we load the models;
            if (tile.getItem().startsWith(TYPE_OBSTACLE)) {
                tile.setWalkable(true);
                createObstacle(modelItem, 0);

            } else if (tile.getItem().startsWith(TYPE_PICKUP)) {
                tile.setWalkable(true);
                createPickup(modelItem);

            } else if (tile.getItem().startsWith(TYPE_STATIC)) {
                tile.setWalkable(false);
                createStatic(modelItem);

            } else if (tile.getItem().startsWith(TYPE_TERRAIN)) {
                tile.setWalkable(true);
                createTerrain(modelItem);

            } else if (tile.getItem().startsWith(TYPE_VEGETATION)) {
                tile.setWalkable(true);
                createVegetation(modelItem);

            }


        }

        //Update the enemy spatial item if any
        Spatial enemySpatial = getChild(tile.getEnemyItem());
        if (enemySpatial != null) {
            enemySpatial = enemySpatial.clone();
            enemySpatial.setName(tile.getEnemyItem());
            enemySpatial.setLocalTranslation(xPos, 0, zPos);
            enemySpatial.setLocalRotation(new Quaternion().fromAngleAxis(tile.getItemAngle() * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));

            tile.setEnemySpatial(enemySpatial);
            initEnemy(tile);
            enemySpatial.setLocalTranslation(xPos, 0.25f, zPos);
            createEnemy(enemySpatial, 100, true);

        }

//        levelNode.batch();

        return tile;
    }

    /**
     * This method will be called just before an enemy is added.
     *
     * @param spatial
     */
    protected abstract void initEnemy(Tile tile);

    /**
     * This method can be called to get a random adjacent tile.
     *
     * @param currentTile
     * @return
     */
    public Tile getNextAdjacentTile(Tile currentTile, Tile fromTile) {
        Tile tile = null;

        ArrayList<Tile> adjacentTiles = getAllAdjacentTile(currentTile, fromTile);

        //Get a random option
        if (adjacentTiles.size() > 0) {
            tile = adjacentTiles.get(FastMath.nextRandomInt(0, adjacentTiles.size() - 1));

        } else {
            //Stay on same tile
            tile = currentTile;

        }

        return tile;
    }

    /**
     * Returns all tiles adjacent to this tile
     *
     * @param currentTile
     * @param fromTile
     * @return
     */
    public ArrayList<Tile> getAllAdjacentTile(Tile currentTile, Tile fromTile) {

        ArrayList<Tile> adjacentTiles = new ArrayList<Tile>();
        if (hasTerrain(0, -1, currentTile)) {
            adjacentTiles.add(tileData.getMap()[currentTile.getxPos()][currentTile.getzPos() - 1]);
        }
        if (hasTerrain(0, 1, currentTile)) {
            adjacentTiles.add(tileData.getMap()[currentTile.getxPos()][currentTile.getzPos() + 1]);
        }
        if (hasTerrain(1, 0, currentTile)) {
            adjacentTiles.add(tileData.getMap()[currentTile.getxPos() + 1][currentTile.getzPos()]);
        }
        if (hasTerrain(-1, 0, currentTile)) {
            adjacentTiles.add(tileData.getMap()[currentTile.getxPos() - 1][currentTile.getzPos()]);
        }

        //Only do this if diagonal is true
        if (diagonal) {
            if (hasTerrain(-1, -1, currentTile)) {
                adjacentTiles.add(tileData.getMap()[currentTile.getxPos() - 1][currentTile.getzPos() - 1]);
            }
            if (hasTerrain(-1, 1, currentTile)) {
                adjacentTiles.add(tileData.getMap()[currentTile.getxPos() - 1][currentTile.getzPos() + 1]);
            }
            if (hasTerrain(1, -1, currentTile)) {
                adjacentTiles.add(tileData.getMap()[currentTile.getxPos() + 1][currentTile.getzPos() - 1]);
            }
            if (hasTerrain(1, 1, currentTile)) {
                adjacentTiles.add(tileData.getMap()[currentTile.getxPos() + 1][currentTile.getzPos() + 1]);
            }
        }

        //This statement removes the previous position as an option when there are other options
        if (adjacentTiles.size() > 1 && fromTile != null && adjacentTiles.contains(fromTile)) {
            adjacentTiles.remove(fromTile);
        }

        return adjacentTiles;
    }

    /**
     * Check method if any floor tile exist with those parameters
     *
     * @param xOffset
     * @param zOffset
     * @param tile
     * @return
     */
    protected boolean hasTerrain(int xOffset, int zOffset, Tile tile) {
        return (tile.getxPos() + xOffset) < mapSize
                && (tile.getxPos() + xOffset) > 0
                && (tile.getzPos() + zOffset) < mapSize
                && (tile.getzPos() + zOffset) > 0
                && tileData.getMap()[tile.getxPos() + xOffset][tile.getzPos() + zOffset].isWalkable();
    }

    /**
     * A public helper method that can be called when the map or level needs to
     * be cleared.
     */
    public void clear() {

        levelNode.detachAllChildren();

        initMap();

        //Load the static level models into the scene
        for (int r = 0; r < tileData.getMap().length; r++) {
            Tile[] row = tileData.getMap()[r];
            for (int c = 0; c < row.length; c++) {
                Tile tile = row[c];
                updateTile(tile);
            }
        }

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
            Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.INFO, null, e);
            //Load the default
            levelInputStream = JmeSystem.getResourceAsStream("/assets/Levels/" + levelfile);

        }

        try {
            if (levelInputStream == null) {
                //Load a default
                log("Loading level");
                tileData = new TileData();
                initMap();

            } else {
                //Load the level.
//                tileData = new TileData();
                log("Loading tile data from file");
                ObjectInputStream in = new ObjectInputStream(levelInputStream);
                try {
                    tileData = (TileData) in.readObject();

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(TileData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                }

            }

        } catch (IOException ex) {
            Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                        tileData = (TileData) in.readObject();
//                        map = tileData.getMap();

                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(TileData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } catch (EOFException eofe) {
                        Logger.getLogger(TileData.class.getName()).log(java.util.logging.Level.WARNING, null, eofe);
                        file.createNewFile();
                        tileData = new TileData();
                        initMap();
                    }

                } else {
                    file.createNewFile();
                    tileData = new TileData();
                    initMap();

                }

            } catch (IOException ex) {
                Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This is a helper method that can be used to save the new level.
     */
    public void save() {
        if (tileData != null) {
            File folder = JmeSystem.getStorageFolder();

            if (folder != null && folder.exists()) {
                if (file != null) {
                    FileOutputStream fileOut = null;
                    ObjectOutputStream out = null;
                    try {
                        fileOut = new FileOutputStream(file);
                        out = new ObjectOutputStream(fileOut);
//                        tileData.setMap(map);
                        out.writeObject(tileData);

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } finally {
                        if (fileOut != null) {
                            try {
                                fileOut.close();
                            } catch (IOException ex) {
                                Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                            }
                        }


                    }

                }
            }
        }

    }

//    /**
//     * A helper method that will be used to parse the properties into the Tile
//     * array.
//     */
//    protected void loadFromProperties() {
//        if (levelProperties != null) {
//            //We need to loop over all the properties of the level and load the level.
//            for (Iterator<Object> it = levelProperties.keySet().iterator(); it.hasNext();) {
//                String key = (String) it.next();
//
//                //Try and parse the level properties
//                String[] positionStr = key.split(",");
//                int x = positionStr.length > 0 ? Integer.parseInt(positionStr[0].trim()) : 0;
//                int z = positionStr.length > 1 ? Integer.parseInt(positionStr[1].trim()) : 0;
//                int angle = positionStr.length > 2 ? Integer.parseInt(positionStr[2].trim()) : 0;
//
//                String value = levelProperties.getProperty(key);
//                Tile tile = map[x][z];
//                tile.setAngle(angle);
//                tile.setName(value);
//            }
//        }
//    }
//    /**
//     * This method will translate the tile array to the properties file.
//     */
//    protected void updateProperties() {
//        if (levelProperties != null) {
//            levelProperties.clear();
//
//            //We need to loop over all the tiles of the level and set the properties.
//            for (int r = 0; r < map.length; r++) {
//                Tile[] row = map[r];
//                for (int c = 0; c < row.length; c++) {
//                    Tile tile = row[c];
//                    levelProperties.put(r + "," + c + "," + tile.getAngle(), tile.getName());
//                }
//            }
//        }
//    }
    public boolean isOptimize() {
        return optimize;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
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

    public float getTileSize() {
        return tileSize;
    }

    public int getMapSize() {
        return mapSize;
    }

    /**
     * This method must be called by the editor to get the selected types list.
     *
     * @param type
     * @return
     */
    public ArrayList<Spatial> getTileListForType(String type) {
        ArrayList<Spatial> list = null;

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

        }

        //If nothing found we return te selector
        if (list == null || list.size() == 0) {
            list = blankList;

        }

        return list;
    }
}
