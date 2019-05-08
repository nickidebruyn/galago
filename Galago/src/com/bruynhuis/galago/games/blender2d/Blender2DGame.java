/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.blender2d;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.control.AbstractControl;
import com.jme3.texture.Texture;

/**
 *
 * @author nidebruyn
 */
public abstract class Blender2DGame {

    public static final String TYPE = "type";
    public static final String TYPE_PLAYER = "player";
    public static final String TYPE_TERRAIN = "terrain";
    public static final String TYPE_ENEMY = "enemy";
    public static final String TYPE_OBSTACLE = "obstacle";
    public static final String TYPE_STATIC = "static";
    public static final String TYPE_PICKUP = "pickup";
    public static final String TYPE_START = "start";
    public static final String TYPE_END = "end";
    public static final String TYPE_BULLET = "bullet";
    
    protected Base2DApplication baseApplication;
    protected String sceneFile;
    protected Node rootNode;
    protected Node levelNode;
    protected float startAngle;
    protected Vector3f startPosition = Vector3f.ZERO;
    protected Vector3f endPosition = Vector3f.ZERO;
    protected boolean started = false;
    protected boolean paused = false;
    protected boolean gameOver = false;
    protected boolean loading = false;
    protected Blender2DPlayer player;
    protected Blender2DGameListener gameListener;

    public Blender2DGame(Base2DApplication baseApplication, Node rootNode, String sceneFile) {
        this.baseApplication = baseApplication;
        this.rootNode = rootNode;
        this.sceneFile = sceneFile;

    }

    public abstract void init();
    
    public abstract void parse(Spatial spatial);

    /**
     * Parse the level properties so that the spatials can be loaded.
     */
    public void load() {

        levelNode = (Node)baseApplication.getAssetManager().loadModel(sceneFile);
        rootNode.attachChild(levelNode);
        
        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            public void visit(Spatial spatial) {

                if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_TERRAIN)) {
                    
                    log("##### ADDING TERRAIN ################################################");
                    spatial.setName(TYPE_TERRAIN);
                    
                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_STATIC)) {
                    
                    log("##### ADDING STATIC ################################################");
                    spatial.setName(TYPE_STATIC);
                    
                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_OBSTACLE)) {
                    
                    log("##### ADDING OBSTACLE ################################################");
                    spatial.setName(TYPE_OBSTACLE);
                    
                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_ENEMY)) {
                    
                    log("##### ADDING ENEMY ################################################");
                    spatial.setName(TYPE_ENEMY);

                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_START)) {
                    
                    log("##### ADDING START ################################################");
                    spatial.setName(TYPE_START);
                    startPosition = spatial.getWorldTranslation().clone();
                    startAngle = spatial.getWorldRotation().toAngleAxis(Vector3f.UNIT_Z)*FastMath.RAD_TO_DEG;
                    log("\t World position: " + startPosition);
                    log("\t World rotation: " + startAngle);

                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_END)) {
                    
                    log("##### ADDING END ################################################");
                    spatial.setName(TYPE_END);      
                    endPosition = spatial.getWorldTranslation().clone();        
                    log("\t World position: " + endPosition);
                    addEnd(spatial);

                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_PICKUP)) {
                    
                    log("##### ADDING PICKUP ################################################");
                    spatial.setName(TYPE_PICKUP);
                    log("\t World position: " + spatial.getWorldTranslation());
                    addPickup(spatial);

                }
                
                parse(spatial);
            }
        };
        
        levelNode.depthFirstTraversal(sgv);

        init();

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

    }

    public void close() {
        loading = false;
        started = false;
        paused = false;
        gameOver = false;

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

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void start(Blender2DPlayer player) {
        this.player = player;
        loading = false;
        started = true;
        paused = false;
        gameOver = false;
        this.player.start();
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

    public void addGameListener(Blender2DGameListener gameListener) {
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

    public float getStartAngle() {
        return startAngle;
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

    public Blender2DPlayer getPlayer() {
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

    public void fixTexture(MatParam mp) {
        if (mp != null) {
            MatParamTexture mpt = (MatParamTexture) mp;
            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }
    }
    
    /**
     * Add a type pickup. If the player collides with this it gains something.
     *
     * @param bodyControl The body to control
     */
    public void addPickup(Spatial spatial) {
        spatial.setName(TYPE_PICKUP);
        spatial.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (isStarted() && !isGameOver() && !isPaused()) {

                    float distance = new Vector2f(player.getPosition().x, player.getPosition().y).distance(new Vector2f(spatial.getLocalTranslation().x, spatial.getLocalTranslation().y));

                    if (distance < player.getSize()) {
                        fireCollisionPlayerWithPickupListener(spatial, player.getPlayerNode());
                    }

                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

    }
    
    public void addEnd(Spatial spatial) {
        spatial.setName(TYPE_END);
        spatial.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (isStarted() && !isGameOver() && !isPaused()) {

                    float distance = new Vector2f(player.getPosition().x, player.getPosition().y).distance(new Vector2f(spatial.getLocalTranslation().x, spatial.getLocalTranslation().y));

                    if (distance < player.getSize()) {
                        doLevelCompleted();
                    }

                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

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
}
