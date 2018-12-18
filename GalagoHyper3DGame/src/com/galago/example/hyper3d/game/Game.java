/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.hyper3d.game;

import aurelienribon.tweenengine.Tween;
import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.control.effects.SimpleTrailControl;
import com.bruynhuis.galago.control.tween.RigidbodyAccessor;
import com.bruynhuis.galago.games.physics.PhysicsGame;
import com.bruynhuis.galago.games.physics.PhysicsPlayer;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nicki
 */
public class Game extends PhysicsGame {

    private Material skyMaterial;
    private Spatial skySpatial;
    private float blockSize = 1f;
    private int blocksPerSection = 12;
    private float distanceBetweenSections;
    private float lastSectionPos;
    private Vector3f sunDirection = new Vector3f(-0.2f, -0.3f, -0.4f);

    private int SECTION_COUNT = 6;

    public Game(Base3DApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);

        distanceBetweenSections = blocksPerSection * blockSize;
    }

    @Override
    public void init() {

        baseApplication.getBulletAppState().getPhysicsSpace().setGravity(new Vector3f(0, -30f, 0));
        baseApplication.getBulletAppState().setSpeed(2.5f);
//        baseApplication.getBulletAppState().setBroadphaseType(PhysicsSpace.BroadphaseType.SIMPLE);
//        baseApplication.getBulletAppState().setThreadingType(BulletAppState.ThreadingType.PARALLEL);

        skyMaterial = baseApplication.getModelManager().getMaterial("Materials/sky.j3m");
        skySpatial = SpatialUtils.addSkySphere(levelNode, 0, baseApplication.getCamera());
        skySpatial.setMaterial(skyMaterial);

        loadSection(1, -distanceBetweenSections / 2f);
        loadSection(FastMath.nextRandomInt(1, SECTION_COUNT), -distanceBetweenSections / 2f + distanceBetweenSections);

        initLight(ColorRGBA.White, sunDirection);

    }

    private void loadNextSection() {
        loadSection(FastMath.nextRandomInt(1, SECTION_COUNT), lastSectionPos + distanceBetweenSections);

    }

    private void loadSection(int index, float x) {

        lastSectionPos = x;

        //This loads the marker between sections
        loadSectionMarker(x);

        //DEFINE THE DIFFERENT TYPE OF SECTIONS HERE
        if (index == 1) {
            for (int i = 1; i < blocksPerSection-1; i++) {
                loadBlock(x + i, 0f);
            }

        } else if (index == 2) {
            loadBlock( x + 2, 0f, 1f, x + 1, 0f);
            loadBlock(x + 3, 0f, 1f, x + 2, 0f);
            loadBlock(x + 4, 0f, 1f, x + 3, 0f);
            loadBlock(x + 5, 0f);
            loadBlock(x + 6, 0f);
            loadBlock(x + 7, 0f);
            loadBlock(x + 8, 0f);

        } else if (index == 3) {
            loadBlock(x + 1, 0f, 1, x + 1, 2f);
            loadBlock(x + 2, 0f, 1.2f, x + 2, 2f);
            loadBlock(x + 3, 0f, 1.4f, x + 3, 2f);
//            loadBlock(x + 4, 0f);
//            loadBlock(x + 5, 0f);
            loadBlock(x + 6, 2f);
            loadBlock(x + 7, 2f);
            loadBlock(x + 8, 2f);

        } else if (index == 4) {
            loadBlock(x + 1, 1f);
            loadBlock(x + 2, 1f);
            loadBlock(x + 3, 1f);
//            loadBlock(x + 4, 1f);
            loadBlock(x + 6, 5f, 1f, x + 6, 1f);
            loadBlock(x + 7, 5f, 1f, x + 7, 1f);
            loadBlock(x + 8, 5f, 1f, x + 8, 1f);
//            loadBlock(x + 8, 3f);

        } else if (index == 5) {

            loadBlock(x + 2, 1f);
            loadBlock(x + 3, 1f);
            loadBlock(x + 4, 1f);
            loadBlock(x + 5, 1f);
            
            loadBlock(x + 6, -12f, 1f, x + 6, 1f);
            loadBlock(x + 7, -12f, 1f, x + 7, 1f);
            loadBlock(x + 8, -12f, 1f, x + 8, 1f);


        } else if (index == 6) {

            for (int r = -1; r < 3; r++) {
                for (int c = 0; c < 10; c++) {
                    
                    if (r > 0) {
                        loadBlock(x + c, r, c/10f, x + c, r + 3);  
                        
                    } else {
                        loadBlock(x + c, r, c/10f, x + c, r - 1);
                    }                    
                    
                }
                
            }

        }

    }

    private void loadBlock(float x, float y) {
        loadBlock(x, y, 0, 0, 0);

    }

    private void loadBlock(float x, float y, float delay, float targetX, float targetY) {
        Spatial block = SpatialUtils.addBox(levelNode, blockSize * 0.46f, blockSize * 0.46f, blockSize * 0.46f);
        block.setName(TYPE_STATIC);
        SpatialUtils.addColor(block, ColorRGBA.Brown, false);
        BoxCollisionShape collisionShape = new BoxCollisionShape(new Vector3f(blockSize * 0.5f, blockSize * 0.5f, blockSize * 0.5f));
        RigidBodyControl rbc = new RigidBodyControl(collisionShape, 0f);
        block.addControl(rbc);
        getBaseApplication().getBulletAppState().getPhysicsSpace().add(rbc);        
        rbc.setFriction(0.0f);
        rbc.setRestitution(0.0f);
        rbc.setLinearSleepingThreshold(0f);
        rbc.setAngularSleepingThreshold(0f);
        SpatialUtils.move(block, x, y, 0);
        block.addControl(new BlockDestroyControl(this));

        if (delay == 0 && targetX == 0 && targetY == 0) {

        } else {
            Tween.to(rbc, RigidbodyAccessor.POS_XYZ, 0.4f)
                    .target(targetX, targetY, 0)
                    .delay(delay)
                    .start(baseApplication.getTweenManager());
        }

    }

    private void loadSectionMarker(float x) {
        Node marker = new Node("section");
        levelNode.attachChild(marker);
//        Spatial marker = SpatialUtils.addBox(levelNode, blockSize * 0.05f, blockSize * 2f, blockSize * 0.05f);
//        marker.setCullHint(Spatial.CullHint.Always);
//        SpatialUtils.addColor(marker, ColorRGBA.Green, true);
        SpatialUtils.move(marker, x, 0, 0);
        marker.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (isStarted() && !isPaused()) {

                    if (player.getPosition().x > spatial.getWorldTranslation().x
                            && new Vector3f(player.getPosition().x, 0, 0).distance(spatial.getWorldTranslation()) >= distanceBetweenSections) {

                        spatial.removeFromParent();
                        player.addScore(1);

                        //TODO: Choose the next random valid section
                        loadNextSection();

                    }

                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });
    }

    public float getDistanceBetweenSections() {
        return distanceBetweenSections;
    }

    @Override
    public void start(PhysicsPlayer physicsPlayer) {
        super.start(physicsPlayer);

        SimpleTrailControl trailControl = new SimpleTrailControl(player.getPlayerNode(), 0.2f, 50);
        trailControl.setMaterial(baseApplication.getAssetManager().loadMaterial("Common/Materials/WhiteColor.j3m"));
        trailControl.setLineWidth(6f);
        levelNode.addControl(trailControl);

    }

}
