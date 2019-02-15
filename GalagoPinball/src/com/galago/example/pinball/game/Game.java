/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.pinball.game;

import aurelienribon.tweenengine.Tween;
import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.control.tween.Rigidbody2DAccessor;
import com.bruynhuis.galago.games.blender2d.BlenderPhysics2DGame;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.util.SpriteUtils;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author nicki
 */
public class Game extends BlenderPhysics2DGame {

    private RigidBodyControl leftFlipper;

    public Game(Base2DApplication baseApplication, Node rootNode, String sceneFile) {
        super(baseApplication, rootNode, sceneFile);
    }

    @Override
    public void init() {

        //LOAD THE LEFT FLIPPER
        Node flipperNode = new Node(TYPE_STATIC);
        levelNode.attachChild(flipperNode);
        flipperNode.move(0, 0, 1);

        SpriteUtils.addSprite(flipperNode, "Textures/circle.png", 0.2f, 0.2f, 0f, 0f, 0.1f);
        SpriteUtils.addSprite(flipperNode, "Textures/flipper.png", 2f, 0.5f, 0.7f, 0f, 0f);

//        BoxCollisionShape shape = new BoxCollisionShape(2f, 0.4f);
//        shape.setLocation(0.7f, 0);
        FlipperCollisionShape shape = new FlipperCollisionShape(2f, 0.4f, 0.1f);
        RigidBodyControl rbc = new RigidBodyControl(shape, 0);
        
        rbc.setPhysicLocation(-2.3f, -6.3f);
        rbc.setPhysicRotation(FastMath.DEG_TO_RAD * -25);

        flipperNode.addControl(rbc);
        baseApplication.getDyn4jAppState().getPhysicsSpace().add(flipperNode);
        
        rbc.setFriction(0);
        rbc.setRestitution(0);
//        rbc.setGravityScale(0.6f);
        
        leftFlipper = rbc;

    }

    @Override
    public void parse(Spatial spatial) {

//        if (spatial.getUserData("flipper") != null && spatial.getUserData("flipper").equals("left")) {
////            leftFlipper = spatial;
//            Geometry geom = (Geometry) ((Node) spatial).getChild(0);
//            float angle = -30;
//
//            log("Got left flipper: " + geom);
//            leftFlipper = new Node("left-flipper");
//            leftFlipper.setLocalTranslation(spatial.getWorldTranslation());
//            SpatialUtils.rotate(leftFlipper, 0, 0, angle);
//
//            Sprite sp = new Sprite("sprite", 2.2f, 2.2f / 4);
//            sp.setImage("Textures/flipper.png");
//            sp.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);
//            sp.move(1, 0, 0);
//            ((Node) leftFlipper).attachChild(sp);
//
//            RigidBodyControl rbc = new RigidBodyControl(0);
//            SpriteUtils.addCollisionShapeBasedOfGeometry(rbc, geom);
//            rbc.setPhysicLocation(spatial.getWorldTranslation());
//            rbc.setPhysicRotation(FastMath.DEG_TO_RAD*angle);
//            
//            leftFlipper.setName(TYPE_STATIC);
//            levelNode.attachChild(leftFlipper);
//            leftFlipper.addControl(rbc);
//            baseApplication.getDyn4jAppState().getPhysicsSpace().add(leftFlipper);
//            
//
//            //Finally remove the placeholder in the blender file            
//            spatial.removeFromParent();
//        }
    }

    public void doLeftFlip() {
        if (leftFlipper != null) {
            if (isStarted() && !isPaused() && !isGameOver()) {

                Tween.to(leftFlipper, Rigidbody2DAccessor.ROTATION, 0.08f)
                        .target(40)
                        .start(baseApplication.getTweenManager());

            }

        }
    }
    public void doLeftStop() {
        if (leftFlipper != null) {
            if (isStarted() && !isPaused() && !isGameOver()) {

                Tween.to(leftFlipper, Rigidbody2DAccessor.ROTATION, 0.08f)
                        .target(-25)
                        .start(baseApplication.getTweenManager());

            }

        }
    }
}
