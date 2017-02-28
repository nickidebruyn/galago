/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.blender3d;

import com.bruynhuis.galago.app.Base3DApplication;
import com.jme3.bounding.BoundingBox;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nidebruyn
 */
public abstract class Blender3DGame implements PhysicsCollisionListener {

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
    public static final String SHAPE = "shape";
    public static final String SHAPE_BOX = "box";
    public static final String SHAPE_SPHERE = "sphere";
    public static final String SHAPE_CAPSULE = "capsule";
    public static final String MASS = "mass";
    public static final String SENSOR = "sensor";
    protected Base3DApplication baseApplication;
    protected String sceneFile;
    protected Node rootNode;
    protected Node levelNode;
    protected Vector3f startPosition = Vector3f.ZERO;
    protected Vector3f endPosition = Vector3f.ZERO;
    protected boolean started = false;
    protected boolean paused = false;
    protected boolean gameOver = false;
    protected boolean loading = false;
    protected Blender3DPlayer player;
    protected Blender3DGameListener gameListener;
    protected Spatial lastCollidedSpatial;
    protected Spatial lastColliderSpatial;

    public Blender3DGame(Base3DApplication baseApplication, Node rootNode, String sceneFile) {
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
        baseApplication.getBulletAppState().setEnabled(false);

        levelNode = (Node) baseApplication.getAssetManager().loadModel(sceneFile);
        rootNode.attachChild(levelNode);

        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            public void visit(Spatial spatial) {

                if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_TERRAIN)) {

                    log("##### ADDING TERRAIN ################################################");
                    spatial.setName(TYPE_TERRAIN);
                    add3DRigidbody(spatial);

                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_STATIC)) {

                    log("##### ADDING STATIC ################################################");
                    spatial.setName(TYPE_STATIC);
                    add3DRigidbody(spatial);

                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_OBSTACLE)) {

                    log("##### ADDING OBSTACLE ################################################");
                    spatial.setName(TYPE_OBSTACLE);
                    add3DRigidbody(spatial);

                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_ENEMY)) {

                    log("##### ADDING ENEMY ################################################");
                    spatial.setName(TYPE_ENEMY);
                    add3DRigidbody(spatial);

                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_START)) {

                    log("##### ADDING START ################################################");
                    spatial.setName(TYPE_START);
                    startPosition = spatial.getWorldTranslation().clone();
                    log("\t World position: " + startPosition);

                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_END)) {

                    log("##### ADDING END ################################################");
                    spatial.setName(TYPE_END);
                    endPosition = spatial.getWorldTranslation().clone();
                    log("\t World position: " + endPosition);
                    add3DRigidbody(spatial);


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

        baseApplication.getBulletAppState().getPhysicsSpace().addCollisionListener(this);

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

        baseApplication.getBulletAppState().getPhysicsSpace().removeCollisionListener(this);

        if (player != null) {
            player.close();
        }

        levelNode.removeFromParent();
        rootNode.detachAllChildren();

        baseApplication.getBulletAppState().getPhysicsSpace().destroy();
        player = null;
        System.gc(); //Force memory to be released;

    }

    protected void log(String text) {
        System.out.println(text);
    }

    public void pause() {
        paused = true;
        baseApplication.getBulletAppState().setEnabled(false);
    }

    public void resume() {
        paused = false;
        baseApplication.getBulletAppState().setEnabled(true);
    }

    public void start(Blender3DPlayer physicsPlayer) {
        this.player = physicsPlayer;
        loading = false;
        started = true;
        paused = false;
        gameOver = false;
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

    public void addGameListener(Blender3DGameListener gameListener) {
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

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isLoading() {
        return loading;
    }

    public Blender3DPlayer getPlayer() {
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
     * This will parse and add the physics to the spatial.
     *
     * @param spatial
     */
    private void add3DRigidbody(Spatial spatial) {
        Float massVal = spatial.getUserData(MASS);
        float mass = 0;
        if (massVal != null) {
            mass = massVal.floatValue();
        }

        String shape = null;
        if (spatial.getUserData(SHAPE) != null) {
            shape = spatial.getUserData(SHAPE);
        }

        CollisionShape collisionShape = null;
        BoundingBox bb = (BoundingBox) spatial.getWorldBound();
        log("\t World Bounds: " + bb);

        if (SHAPE_BOX.equals(shape)) {
            collisionShape = new BoxCollisionShape(bb.getExtent(null));

        } else if (SHAPE_SPHERE.equals(shape)) {
            collisionShape = new SphereCollisionShape(bb.getYExtent());

        } else if (SHAPE_CAPSULE.equals(shape)) {
            collisionShape = new CapsuleCollisionShape(bb.getXExtent(), bb.getYExtent());
        }

        //Check what type
        log("\t World position: " + spatial.getWorldTranslation());

        if (spatial.getUserData(SENSOR) != null) {
            GhostControl ghostControl = null;
            if (collisionShape == null) {
                ghostControl = new GhostControl();                
            } else {
                ghostControl = new GhostControl(collisionShape);
            }

            spatial.addControl(ghostControl);
            baseApplication.getBulletAppState().getPhysicsSpace().add(spatial);

        } else {
            RigidBodyControl rigidBodyControl = null;
            if (collisionShape == null) {
                rigidBodyControl = new RigidBodyControl(mass);
            } else {
                rigidBodyControl = new RigidBodyControl(collisionShape, mass);
            }
            spatial.addControl(rigidBodyControl);
            baseApplication.getBulletAppState().getPhysicsSpace().add(spatial);
//            rigidBodyControl.setFriction(0.01f);
//            rigidBodyControl.setRestitution(0.5f);

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

                    float distance = player.getPosition().distance(spatial.getWorldTranslation());

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
}
