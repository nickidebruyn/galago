/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.control.AnimationControl;
import com.bruynhuis.galago.util.Debug;
import com.bruynhuis.galago.util.SpatialUtils;
import com.galago.tests.MainApplication;
import com.galago.tests.controls.MyRagdoll;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public class RagdollScreen extends AbstractEditorScreen {

    public static final String NAME = "ragdoll";
    private Spatial bot;
    private AnimationControl animationControl;
    private AnimControl animControl;
    private MyRagdoll ragdoll;
    private Spatial ragdollSpatial;
    private Bone leftHand;
    private Spatial rock;

    @Override
    protected void load() {
        setPreviousScreen(MenuScreen.NAME);
        super.load(); //To change body of generated methods, choose Tools | Templates.

        bot = baseApplication.getAssetManager().loadModel("Models/xbot/bot.j3o");

        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
//                    Debug.log("Spatial: " + spatial.getName());
                if (spatial.getControl(AnimControl.class) != null && spatial.getUserData("animation") != null) {
                    Debug.log("Found Anim Control on " + spatial.getName());
                    ragdollSpatial = spatial;
                    animControl = ragdollSpatial.getControl(AnimControl.class);

                    animationControl = new AnimationControl();
                    ragdollSpatial.addControl(animationControl);

                    ragdoll = new MyRagdoll(10.0f);
                    setupBones(ragdoll, animControl.getSkeleton().getRoots());
//                    ragdoll.addCollisionListener(this);

                }
            }
        };
        bot.depthFirstTraversal(sgv);
        bot.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        bot.move(0, 0, 0);

        sceneNode.attachChild(bot);

        rock = SpatialUtils.addSphere(sceneNode, 10, 10, 0.3f);
        SpatialUtils.addColor(rock, ColorRGBA.Green, true);
    }

    /**
     * Recursive method that will setup the bones.
     *
     * @param ragdollControl
     * @param bones
     */
    private void setupBones(KinematicRagdollControl ragdollControl, Bone[] bones) {
        log("Found bones = " + bones.length);
        if (bones != null && bones.length > 0) {
            for (int i = 0; i < bones.length; i++) {
                Bone bone = bones[i];
                log("Bone: " + bone.getName());

                if (bone.getName() != null && bone.getName().length() > 0) {
                    ragdollControl.addBoneName(bone.getName());
                    
                    if (bone.getName().endsWith("LeftHand")) {
                        leftHand = bone;                        
                    }
                }

                //Map the child bones
                if (bone.getChildren() != null && bone.getChildren().size() > 0) {
                    Bone[] children = new Bone[bone.getChildren().size()];
                    bone.getChildren().toArray(children);
                    setupBones(ragdollControl, children);

                }
            }
        }
    }

    @Override
    protected void show() {
        super.show(); //To change body of generated methods, choose Tools | Templates.
        baseApplication.showDebuging();
        
        log("Bone list = " + ragdoll.getBoneList());
        log("Left hand = " + leftHand);

//        ragdollSpatial.addControl(ragdoll);
//        ((MainApplication) baseApplication).getBulletAppState().getPhysicsSpace().add(ragdoll);

        animationControl.play("dance", true, false, 1);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf); //To change body of generated methods, choose Tools | Templates.
        
        if (leftHand != null) {
//            log("Hand pos = " + leftHand.getModelSpacePosition());
            rock.setLocalTranslation(leftHand.getModelBindInversePosition());
        }        
        
    }

    
}
