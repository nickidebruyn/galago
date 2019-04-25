/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.util.SpatialUtils;
import com.galago.tests.MainApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public class PhysicsJointScreen extends AbstractEditorScreen {

    public static final String NAME = "joints";

    private Spatial floor;
    private Spatial hook;
    private Spatial ball;

    @Override
    protected void load() {
        setPreviousScreen(MenuScreen.NAME);
        super.load(); //To change body of generated methods, choose Tools | Templates.

//        floor = SpatialUtils.addBox(sceneNode, 10, 0.1f, 10);
//        SpatialUtils.addColor(floor, ColorRGBA.Green, false);
//        SpatialUtils.addMass(floor, 0);
//        floor.setShadowMode(RenderQueue.ShadowMode.Receive);

        //Create the hook object
        hook = SpatialUtils.addBox(sceneNode, 0.2f, 0.2f, 0.2f);
        SpatialUtils.addColor(hook, ColorRGBA.Brown, false);
        RigidBodyControl hookRbc = SpatialUtils.addMass(hook, 0);
        hook.setShadowMode(RenderQueue.ShadowMode.Cast);
        SpatialUtils.translate(hook, 0, 5, 0);

        //Create the ball
        ball = SpatialUtils.addSphere(sceneNode, 20, 20, 0.25f);
        SpatialUtils.addCartoonColor(ball, "Textures/mat-cap-copper2.jpg", ColorRGBA.White, ColorRGBA.Black, 0.0f, true, false);
        RigidBodyControl ballRbc = SpatialUtils.addMass(ball, 1);
        ball.setShadowMode(RenderQueue.ShadowMode.Cast);
        SpatialUtils.translate(ball, 0, 3, 0);

        HingeJoint joint = new HingeJoint(hookRbc, // A
                ballRbc, // B
                new Vector3f(0f, 0f, 0f), // pivot point local to A
                new Vector3f(0f, -2f, 0f), // pivot point local to B
                Vector3f.UNIT_Z, // DoF Axis of A (Z axis)
                Vector3f.UNIT_Z);        // DoF Axis of B (Z axis)
//        joint.setLimit(2, 3, 0.1f, 1, 1);
        
        ((MainApplication)baseApplication).getBulletAppState().getPhysicsSpace().add(joint);
    }

}
