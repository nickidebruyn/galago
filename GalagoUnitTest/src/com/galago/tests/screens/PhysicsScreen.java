/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.field.TextArea;
import com.bruynhuis.galago.util.SpatialUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nidebruyn
 */
public class PhysicsScreen extends AbstractScreen {
    
    private float size = 10;
    private Timer timer = new Timer(200);
    private TextArea textArea;

    @Override
    protected void init() {
        
        textArea = new TextArea(hudPanel, 500, 200);
        textArea.rightBottom(0, 0);
        textArea.setFontSize(18);
        textArea.setMaxLines(10);
        textArea.setTextColor(ColorRGBA.LightGray);
        
        
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.DarkGray);
        
        SpatialUtils.addSunLight(rootNode, ColorRGBA.White);
        
        Spatial floor = SpatialUtils.addBox(rootNode, size, 0.1f, size);
        SpatialUtils.addColor(floor, ColorRGBA.Green, false);
        SpatialUtils.addMass(floor, 0);
        
        Spatial s = SpatialUtils.addCameraNode(rootNode, camera, 20, 10, 30);
        s.addControl(new RotationControl(10));
        
        rootNode.addControl(new AbstractControl() {

            @Override
            protected void controlUpdate(float tpf) {
                
                timer.update(tpf);
                if (timer.finished()) {
                    
                    textArea.append("Adding a ball");
                    
                    //Add a object
                    Spatial s = SpatialUtils.addSphere(rootNode, 20, 20, FastMath.nextRandomInt(2, 5) * 0.1f);
                    SpatialUtils.addColor(s, ColorRGBA.randomColor(), true);
                    float xPos = FastMath.nextRandomInt(-((int)size-2), (int)size-2);
                    float zPos = FastMath.nextRandomInt(-((int)size-2), (int)size-2);
                    SpatialUtils.translate(s, xPos, 5, zPos);
                    SpatialUtils.addMass(s, 1);
                    
                    timer.reset();
                }
                
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });
        
        
    }

    @Override
    protected void show() {
        timer.start();
        
    }

    @Override
    protected void exit() {
        ((Base3DApplication)baseApplication).getBulletAppState().getPhysicsSpace().destroy();
        ((Base3DApplication)baseApplication).getBulletAppState().getPhysicsSpace().create();
        rootNode.getLocalLightList().clear();
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
        
    }
    
}
