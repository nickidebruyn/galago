/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.control.AnimationControl;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.util.Debug;
import com.bruynhuis.galago.util.SpatialUtils;
import com.galago.tests.controls.CharacterCombatController;
import com.jme3.animation.AnimControl;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class CombatScreen extends AbstractScreen implements KeyboardControlListener {

    public static final String NAME = "CombatScreen";
    private Label title;

    private DirectionalLight sun;
    private Spatial island;
    private CharacterCombatController player1;
    private CharacterCombatController player2;
    private KeyboardControlInputListener keyboardControlInputListener;
    private boolean player1Left = false;
    private boolean player1Right = false;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Player1 vs Player2");
        title.centerTop(0, 0);

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);
        
    }

    @Override
    protected void load() {
        
        sun = new DirectionalLight(new Vector3f(0, -1, -1));
        rootNode.addLight(sun);

        island = baseApplication.getAssetManager().loadModel("Models/terrain/island1.j3o");
        rootNode.attachChild(island);
        SpatialUtils.addMass(island, 0);
        
        player1 = loadPlayer("player1", ColorRGBA.Pink, -6, 1);
        
        player2 = loadPlayer("player2", ColorRGBA.Yellow, 6, 1);
        
        //Load camera
        camera.setLocation(new Vector3f(0, 6, 15));
        camera.lookAt(new Vector3f(0, 2, 0), Vector3f.UNIT_Y);

    }
    
    private CharacterCombatController loadPlayer(String name, ColorRGBA color, float x, float y) {
        Spatial s = baseApplication.getAssetManager().loadModel("Models/xbot/bot.j3o");
        s.setName(name);
        rootNode.attachChild(s);

        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
//                    Debug.log("Spatial: " + spatial.getName());
                if (spatial.getControl(AnimControl.class) != null && spatial.getUserData("animation") != null) {
                    Debug.log("Found Anim Control on " + spatial.getName());
                    AnimationControl ac = new AnimationControl();
                    spatial.addControl(ac);
                }
            }
        };
        s.depthFirstTraversal(sgv);
        
        Vector3f dir = new Vector3f(1, 0, 0);
        if (x > 0) {
            dir.setX(-1);
        }
        
        CharacterCombatController ccc = new CharacterCombatController(s, dir);
        s.addControl(ccc);
        SpatialUtils.updateSpatialColor(s, color);
        SpatialUtils.translate(s, x, y, 0);
        
        return ccc;
    }

    @Override
    protected void show() {
        keyboardControlInputListener.registerWithInput(inputManager);
    }

    @Override
    protected void exit() {
        keyboardControlInputListener.unregisterInput();
        rootNode.detachAllChildren();
        rootNode.removeLight(sun);
        
    }

    @Override
    protected void pause() {
    }

    @Override
    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {
        
        if (keyboardControlEvent.isLeft()) {
            player1Left = keyboardControlEvent.isKeyDown();
        }
        
        if (keyboardControlEvent.isRight()) {
            player1Right = keyboardControlEvent.isKeyDown();
        }
        
        if (keyboardControlEvent.isUp() && keyboardControlEvent.isKeyDown()) {
            player1.jump();
        }
        
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            
            if (player1Left) {
                player1.setDirection(-1);
                player1.walk(true);
                
            } else if (player1Right) {
                player1.setDirection(1);
                player1.walk(true);
                
            } else {
                player1.walk(false);
            }
            
        }
    }

}
