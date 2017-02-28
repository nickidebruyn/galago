/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.endless;

import com.bruynhuis.galago.app.Base3DApplication;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
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
import java.util.ArrayList;

/**
 *
 * @author nidebruyn
 */
public abstract class EndlessGame implements PhysicsCollisionListener, PhysicsTickListener {

    public static final String TYPE_PLAYER = "player";
    public static final String TYPE_OBSTACLE = "obstacle";
    public static final String TYPE_STATIC = "static";
    public static final String TYPE_PICKUP = "pickup";
    public static final String TYPE_VEGETATION = "vegetation";
    public static final String TYPE_START = "start";
    protected Base3DApplication baseApplication;
    protected Node rootNode;
    protected Node levelNode;
    protected Vector3f startPosition = Vector3f.ZERO;
    protected AmbientLight ambientLight;
    protected DirectionalLight sunLight;
    protected boolean started = false;
    protected boolean paused = false;
    protected boolean loading = false;
    protected EndlessPlayer player;
    protected EndlessGameListener gameListener;
    protected Spatial lastCollidedSpatial;
    protected Spatial lastColliderSpatial;
    protected Vector3f direction;
    protected int sectionCount = 0;
    protected ArrayList<EndlessSection> sectionList = new ArrayList<EndlessSection>();

    public EndlessGame(Base3DApplication baseApplication, Node rootNode, Vector3f direction) {
        this.baseApplication = baseApplication;
        this.rootNode = rootNode;
        this.direction = direction;

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

    public void start(EndlessPlayer physicsPlayer) {
        this.player = physicsPlayer;
        loading = false;
        started = true;
        paused = false;
        this.player.start();
        baseApplication.getBulletAppState().setEnabled(true);
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {

        if (player != null) {

            if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_STATIC)) {
                fireCollisionPlayerWithStaticListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_PICKUP)) {
                fireCollisionPlayerWithPickupListener(lastCollidedSpatial, lastColliderSpatial);

            } else if (checkCollisionWithType(event.getNodeA(), event.getNodeB(), TYPE_PLAYER, TYPE_OBSTACLE)) {
                fireCollisionPlayerWithObstacleListener(lastCollidedSpatial, lastColliderSpatial);

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
        fireGameOverListener();
    }

    public void addGameListener(EndlessGameListener gameListener) {
        this.gameListener = gameListener;
    }

    protected void fireGameOverListener() {
        if (gameListener != null) {
            gameListener.doGameOver();
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

    protected void fireCollisionPlayerWithObstacleListener(Spatial collided, Spatial collider) {
        if (gameListener != null) {
            gameListener.doCollisionPlayerWithObstacle(collided, collider);
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

    public EndlessPlayer getPlayer() {
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

        init();

        //Load first sections
        for (int i = 0; i < getNumberOfSections(); i++) {
            nextSection();
        }

        baseApplication.getBulletAppState().getPhysicsSpace().addTickListener(this);
        baseApplication.getBulletAppState().getPhysicsSpace().addCollisionListener(this);

        pause();
        loading = false;
    }


    public void fixTexture(MatParam mp) {
        if (mp != null) {
            MatParamTexture mpt = (MatParamTexture) mp;
            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }
    }


    /**
     * When this method is called a new section will be added to the furthest
     * point of the game and the very last section will be removed It will be
     * calculated by the distance of the player from the last section in the
     * negative position.
     */
    protected void nextSection() {
        Vector3f pos = direction.mult(sectionCount * getSectionSpacing());

        log(sectionCount + " = " + pos);

        EndlessSection section = getNextSection(pos.clone());
        section.load();

//        sectionStack.add(section);

        //Increase the section count
        sectionCount++;
    }

    /**
     * This method will return the spacing between sections.
     *
     * @return
     */
    protected abstract float getSectionSpacing();

    /**
     * This method will return the number of sections to be added into render
     * space
     *
     * @return
     */
    protected abstract int getNumberOfSections();

    /**
     * Must be implemented to add the next section
     *
     * @return
     */
    protected abstract EndlessSection getNextSection(Vector3f position);

    public Vector3f getDirection() {
        return direction;
    }

    public int getSectionCount() {
        return sectionCount;
    }
    
//    public void removeSection(EndlessSection endlessSection) {
//        sectionStack.remove(endlessSection);
//    }
}
