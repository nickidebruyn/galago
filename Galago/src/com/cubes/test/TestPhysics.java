package com.cubes.test;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.cubes.*;

public class TestPhysics extends SimpleApplication implements ActionListener{

    public static void main(String[] args){
        Logger.getLogger("").setLevel(Level.SEVERE);
        TestPhysics app = new TestPhysics();
        app.start();
    }

    public TestPhysics(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Cubes Demo - Physics");
        settings.setFrameRate(60);
    }
    private final Vector3Int terrainSize = new Vector3Int(100, 30, 100);
    private BulletAppState bulletAppState;
    private CharacterControl playerControl;
    private Vector3f walkDirection = new Vector3f();
    private boolean[] arrowKeys = new boolean[4];
    private CubesSettings cubesSettings;
    private BlockTerrainControl blockTerrain;
    private Node terrainNode = new Node();

    @Override
    public void simpleInitApp(){
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        initControls();
        initBlockTerrain();
        initPlayer();
        cam.lookAtDirection(new Vector3f(1, 0, 1), Vector3f.UNIT_Y);
    }

    private void initControls(){
        inputManager.addMapping("move_left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("move_right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("move_up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("move_down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "move_left");
        inputManager.addListener(this, "move_right");
        inputManager.addListener(this, "move_up");
        inputManager.addListener(this, "move_down");
        inputManager.addListener(this, "jump");
    }
    
    private void initBlockTerrain(){
        CubesTestAssets.registerBlocks();
        CubesTestAssets.initializeEnvironment(this);
        
        cubesSettings = CubesTestAssets.getSettings(this);
        blockTerrain = new BlockTerrainControl(cubesSettings, new Vector3Int(7, 1, 7));
        blockTerrain.setBlocksFromNoise(new Vector3Int(), terrainSize, 0.8f, CubesTestAssets.BLOCK_GRASS);
        blockTerrain.addChunkListener(new BlockChunkListener(){

            @Override
            public void onSpatialUpdated(BlockChunkControl blockChunk){
                Geometry optimizedGeometry = blockChunk.getOptimizedGeometry_Opaque();
                RigidBodyControl rigidBodyControl = optimizedGeometry.getControl(RigidBodyControl.class);
                if(rigidBodyControl == null){
                    rigidBodyControl = new RigidBodyControl(0);
                    optimizedGeometry.addControl(rigidBodyControl);
                    bulletAppState.getPhysicsSpace().add(rigidBodyControl);
                }
                rigidBodyControl.setCollisionShape(new MeshCollisionShape(optimizedGeometry.getMesh()));
            }
        });
        terrainNode.addControl(blockTerrain);
        terrainNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(terrainNode);
    }

    private void initPlayer(){
        playerControl = new CharacterControl(new CapsuleCollisionShape((cubesSettings.getBlockSize() / 2), cubesSettings.getBlockSize() * 2), 0.05f);
        playerControl.setJumpSpeed(20);
        playerControl.setFallSpeed(20);
        playerControl.setGravity(80);
        playerControl.setPhysicsLocation(new Vector3f(5, terrainSize.getY() + 5, 5).mult(cubesSettings.getBlockSize()));
        bulletAppState.getPhysicsSpace().add(playerControl);
    }

    @Override
    public void simpleUpdate(float lastTimePerFrame){
        float playerMoveSpeed = ((cubesSettings.getBlockSize() * 6.5f) * lastTimePerFrame);
        Vector3f camDir = cam.getDirection().mult(playerMoveSpeed);
        Vector3f camLeft = cam.getLeft().mult(playerMoveSpeed);
        walkDirection.set(0, 0, 0);
        if(arrowKeys[0]){ walkDirection.addLocal(camDir); }
        if(arrowKeys[1]){ walkDirection.addLocal(camLeft.negate()); }
        if(arrowKeys[2]){ walkDirection.addLocal(camDir.negate()); }
        if(arrowKeys[3]){ walkDirection.addLocal(camLeft); }
        walkDirection.setY(0);
        playerControl.setWalkDirection(walkDirection);
        cam.setLocation(playerControl.getPhysicsLocation());
    }

    @Override
    public void onAction(String actionName, boolean value, float lastTimePerFrame){
        if(actionName.equals("move_up")){
            arrowKeys[0] = value;
        }
        else if(actionName.equals("move_right")){
            arrowKeys[1] = value;
        }
        else if(actionName.equals("move_left")){
            arrowKeys[3] = value;
        }
        else if(actionName.equals("move_down")){
            arrowKeys[2] = value;
        }
        else if(actionName.equals("jump")){
            playerControl.jump();
        }
    }
}
