/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.blender2d;

import com.bruynhuis.galago.app.Base2DApplication;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.EllipseCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.TriCollisionShape;
import com.jme3.bounding.BoundingBox;
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
public abstract class BlenderPhysics2DGame implements PhysicsCollisionListener {

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
    public static final String SHAPE_TRIANGLE = "triangle";
    public static final String SHAPE_BOX = "box";
    public static final String SHAPE_CIRCLE = "circle";
    public static final String SHAPE_ELLIPSE = "ellipse";
    
    public static final String MASS = "mass";
    public static final String SENSOR = "sensor";
    
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
    protected BlenderPhysics2DPlayer player;
    protected Blender2DGameListener gameListener;
    protected Spatial lastCollidedSpatial;
    protected Spatial lastColliderSpatial;

    public BlenderPhysics2DGame(Base2DApplication baseApplication, Node rootNode, String sceneFile) {
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
        baseApplication.getDyn4jAppState().setEnabled(false);

        levelNode = (Node)baseApplication.getAssetManager().loadModel(sceneFile);
        rootNode.attachChild(levelNode);
        
        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            public void visit(Spatial spatial) {

                if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_TERRAIN)) {
                    
                    log("##### ADDING TERRAIN ################################################");
                    spatial.setName(TYPE_TERRAIN);
                    add2DRigidbody(spatial);       
                    
                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_STATIC)) {
                    
                    log("##### ADDING STATIC ################################################");
                    spatial.setName(TYPE_STATIC);
                    add2DRigidbody(spatial);       
                    
                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_OBSTACLE)) {
                    
                    log("##### ADDING OBSTACLE ################################################");
                    spatial.setName(TYPE_OBSTACLE);
                    add2DRigidbody(spatial);       
                    
                } else if (spatial.getUserData(TYPE) != null
                        && spatial.getUserData(TYPE).equals(TYPE_ENEMY)) {
                    
                    log("##### ADDING ENEMY ################################################");
                    spatial.setName(TYPE_ENEMY);
                    add2DRigidbody(spatial);       

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
                    add2DRigidbody(spatial);
                    

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

    }

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

    public void start(BlenderPhysics2DPlayer physicsPlayer) {
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

            } else if (checkCollisionWithType(spatialA, spatialB, TYPE_PLAYER, TYPE_END)) {
                doLevelCompleted();
                spatialA.setName(null);
                spatialB.setName(null);

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

    public BlenderPhysics2DPlayer getPlayer() {
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
    private void add2DRigidbody(Spatial spatial) {
        Float massVal = spatial.getUserData(MASS);
        float mass = 0;
        if (massVal != null) {
            mass = massVal.floatValue();
        }
        RigidBodyControl rigidBodyControl = new RigidBodyControl(mass);
        
        log("\t World Bounds: " + spatial.getWorldBound());

        if (spatial instanceof Node) {
            //We need to create the rbc here
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) {
                Geometry geometry = (Geometry) node.getChild(i);
                addCollisionShape(rigidBodyControl, geometry);

            }

        } else {
            Geometry geometry = (Geometry) spatial;
            addCollisionShape(rigidBodyControl, geometry);

        }

        spatial.addControl(rigidBodyControl);
        baseApplication.getDyn4jAppState().getPhysicsSpace().add(spatial);
        rigidBodyControl.setMass(mass);
        
        boolean sensor = false;
        if (spatial.getUserData(SENSOR) != null) {
            sensor = true;
        }
        rigidBodyControl.setSensor(sensor);
        
        log("\t World position: " + spatial.getWorldTranslation());
        
        float radians = spatial.getWorldRotation().toAngles(null)[2];
        log("\t World rotation: " + (radians* FastMath.RAD_TO_DEG));
        rigidBodyControl.setPhysicLocation(spatial.getWorldTranslation().x, spatial.getWorldTranslation().y);
        rigidBodyControl.setPhysicRotation(radians);

    }
    
    private void addCollisionShape(RigidBodyControl rigidBodyControl, Geometry geometry) {
        String type = null;
        if (geometry.getUserData(SHAPE) != null) {
            type = geometry.getUserData(SHAPE);
        } else if (geometry.getParent().getUserData(SHAPE) != null) {
            type = geometry.getParent().getUserData(SHAPE);
        }
        
        if (SHAPE_BOX.equals(type)) {
            addBoxCollisionShape(rigidBodyControl, geometry);
        } else if (SHAPE_CIRCLE.equals(type)) {
            addCircleCollisionShape(rigidBodyControl, geometry);
        } else if (SHAPE_ELLIPSE.equals(type)) {
            addEllipseCollisionShape(rigidBodyControl, geometry);
        } else {
            addTriangleCollisionShape(rigidBodyControl, geometry);
        }
        
    }

    private void addTriangleCollisionShape(RigidBodyControl rigidBodyControl, Geometry geometry) {
        int triCount = geometry.getMesh().getTriangleCount();
        log("\t Add collision shapes: -TriCount: " + triCount);        
        log("\t World Bounds: " + geometry.getWorldBound());
        
        Vector3f vec1 = new Vector3f(0, 0, 0);
        Vector3f vec2 = new Vector3f(0, 0, 0);
        Vector3f vec3 = new Vector3f(0, 0, 0);
                
        for (int t = 0; t < triCount; t++) {
            geometry.getMesh().getTriangle(t, vec1, vec2, vec3);
            TriCollisionShape triCollisionShape = new TriCollisionShape(vec1.clone(), vec2.clone(), vec3.clone());
            rigidBodyControl.addCollisionShape(triCollisionShape);
        }

    }
    
    private void addBoxCollisionShape(RigidBodyControl rigidBodyControl, Geometry geometry) {
        BoundingBox bb = (BoundingBox)geometry.getWorldBound();
        float width = bb.getXExtent()*2f;
        float height = bb.getYExtent()*2f;
        BoxCollisionShape boxCollisionShape = new BoxCollisionShape(width, height);
        rigidBodyControl.addCollisionShape(boxCollisionShape);

    }
    
    private void addCircleCollisionShape(RigidBodyControl rigidBodyControl, Geometry geometry) {
        BoundingBox bb = (BoundingBox)geometry.getWorldBound();
//        float width = bb.getXExtent()*2f;
        float height = bb.getYExtent();
        CircleCollisionShape collisionShape = new CircleCollisionShape(height);
        rigidBodyControl.addCollisionShape(collisionShape);

    }
    
    private void addEllipseCollisionShape(RigidBodyControl rigidBodyControl, Geometry geometry) {
        BoundingBox bb = (BoundingBox)geometry.getWorldBound();
        float width = bb.getXExtent()*2f;
        float height = bb.getYExtent()*2f;
        EllipseCollisionShape collisionShape = new EllipseCollisionShape(width, height);
        rigidBodyControl.addCollisionShape(collisionShape);

    }


//    /**
//     * Add an enemy type object. If the player collides with this it dies.
//     *
//     * @param bodyControl The body to control
//     */
//    public Spatial addEnemy(RigidBodyControl bodyControl) {
//        bodyControl.getSpatial().setName(TYPE_ENEMY);
//        if (!edit) {
//            baseApplication.getDyn4jAppState().getPhysicsSpace().add(bodyControl);
//        }
//        levelNode.attachChild(bodyControl.getSpatial());
//        return bodyControl.getSpatial();
//
//    }
//
//    /**
//     * Add an obstacle type object. If the player collides with this it dies.
//     *
//     * @param bodyControl The body to control
//     */
//    public Spatial addObstacle(Spatial spatial) {
//        spatial.setName(TYPE_OBSTACLE);
//        if (!edit && spatial.getControl(RigidBodyControl.class) != null) {
//            baseApplication.getDyn4jAppState().getPhysicsSpace().add(spatial.getControl(RigidBodyControl.class));
//        }
//
//        levelNode.attachChild(spatial);
//        return spatial;
//
//    }
//
//    /**
//     * Add a vegetation type model. If this model is added the player can move
//     * through it
//     */
//    public Spatial addVegetation(Sprite sprite) {
//        sprite.setName(TYPE_VEGETATION);
//        vegetationNode.attachChild(sprite);
//        return sprite;
//
//    }

    public void fixTexture(MatParam mp) {
        if (mp != null) {
            MatParamTexture mpt = (MatParamTexture) mp;
            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }
    }

//    /**
//     * Add an static type object.
//     *
//     * @param bodyControl The body to control
//     */
//    public Spatial addStatic(RigidBodyControl bodyControl) {
//        bodyControl.getSpatial().setName(TYPE_STATIC);
//        if (!edit) {
//            baseApplication.getDyn4jAppState().getPhysicsSpace().add(bodyControl);
//        }
//        levelNode.attachChild(bodyControl.getSpatial());
//        return bodyControl.getSpatial();
//
//    }
//
//
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

//
//    /**
//     * This method will add the end marker.
//     *
//     * @param sprite
//     * @return
//     */
//    public Spatial addEnd(Sprite sprite) {
//        sprite.setName(TYPE_END);
//        levelNode.attachChild(sprite);
//        endPosition = sprite.getWorldTranslation().clone();
//
//        sprite.addControl(new AbstractControl() {
//            @Override
//            protected void controlUpdate(float tpf) {
//                if (isStarted() && !isGameOver() && !isPaused()) {
//                    
//                    float distance = new Vector2f(player.getPosition().x, player.getPosition().y).distance(new Vector2f(spatial.getLocalTranslation().x, spatial.getLocalTranslation().y));
//
//                    if (distance < player.getSize() * 0.5f) {
//                        doLevelCompleted();
//                    }
//
//                }
//            }
//
//            @Override
//            protected void controlRender(RenderManager rm, ViewPort vp) {
//            }
//        });
//        return sprite;
//
//    }
//
//    /**
//     * Add a bullet type object. If the player collides with this it dies.
//     *
//     * @param bodyControl The body to control
//     */
//    public Spatial addBullet(RigidBodyControl bodyControl) {
//        bodyControl.getSpatial().setName(TYPE_BULLET);
//        if (!edit) {
//            baseApplication.getDyn4jAppState().getPhysicsSpace().add(bodyControl);
//        }
//        levelNode.attachChild(bodyControl.getSpatial());
//        return bodyControl.getSpatial();
//
//    }


}
