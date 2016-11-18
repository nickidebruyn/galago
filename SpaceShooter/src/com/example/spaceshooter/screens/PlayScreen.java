/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.bruynhuis.galago.messages.MessageListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.TouchStick;
import com.bruynhuis.galago.ui.listener.TouchStickAdapter;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.util.Debug;
import com.bruynhuis.galago.util.Timer;
import com.example.spaceshooter.MainApplication;
import com.example.spaceshooter.enemies.EnemySpawnControl;
import com.example.spaceshooter.player.PlayerCollisionControl;
import com.example.spaceshooter.player.PlayerMovementControl;
import com.example.spaceshooter.player.PlayerShootControl;
import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class PlayScreen extends AbstractScreen implements MessageListener {
    
    private MainApplication mainApplication;
    private Sprite spaceship;
    private Material playerMaterial;
    private PlayerMovementControl playerMovementControl;
    private PlayerCollisionControl playerCollisionControl;
    private PlayerShootControl playerShootControl;
    private EnemySpawnControl enemySpawnControl;

    private int liveCount = 3;
    private int killCount = 0;
    private Label livesLabel;
    private Label killsLabel;
    private Label readyLabel;
    private boolean shoot = false;
    
    private TouchStick touchStick;

    @Override
    protected void init() {
        mainApplication = (MainApplication)baseApplication;
        
        livesLabel = new Label(hudPanel, "Lives: 3", 24, 180, 40);
        livesLabel.setAlignment(BitmapFont.Align.Left);
        livesLabel.leftTop(5, 5);
        
        killsLabel = new Label(hudPanel, "Kills: 0", 24, 180, 40);
        killsLabel.setAlignment(BitmapFont.Align.Right);
        killsLabel.rightTop(5, 5);
        
        readyLabel = new Label(hudPanel, "Get Ready", 44, 350, 60);
        readyLabel.setAlignment(BitmapFont.Align.Center);
        readyLabel.centerAt(0, 0);
        
        //Initialize the         
        playerMaterial = baseApplication.getModelManager().getMaterial("Materials/player.j3m");
        mainApplication.fixFlatTexture(playerMaterial.getParam("ColorMap"));
        
        if (baseApplication.isMobileApp()) {
            touchStick = new TouchStick(hudPanel, "touchStick", 200, 200);
            touchStick.centerBottom(0, 0);
            touchStick.addTouchStickListener(new TouchStickAdapter() {

                @Override
                public void doMove(float x, float y, float distance) {
                    
                                        
                }

                @Override
                public void doRelease(float x, float y) {
                    shoot = false;
                }

                @Override
                public void doPress(float x, float y) {
                    shoot = true;
                    
                }
                
            });
            
        } else {
            inputManager.addMapping("moveup", new KeyTrigger(KeyInput.KEY_UP));
            inputManager.addMapping("movedown", new KeyTrigger(KeyInput.KEY_DOWN));
            inputManager.addMapping("moveleft", new KeyTrigger(KeyInput.KEY_LEFT));
            inputManager.addMapping("moveright", new KeyTrigger(KeyInput.KEY_RIGHT));
            inputManager.addMapping("shoot", new KeyTrigger(KeyInput.KEY_SPACE));
        }

    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);
        
        liveCount = 3;
        killCount = 0;
        
        //Load the level
        Spatial spatial = baseApplication.getModelManager().getModel("Models/starfield.j3o");
        spatial.setLocalTranslation(0, 20, -1f);
        rootNode.attachChild(spatial);
        
        //Spawn player
        spawnPlayer();
        
        //Load the enemy spawner
        enemySpawnControl = new EnemySpawnControl(mainApplication, rootNode);
        
        updateUI();
        setWaitExit(150);
    }
    
    private void updateUI() {
        livesLabel.setText("Lives:" + liveCount);
        killsLabel.setText("Kills:" + killCount);
    }

    @Override
    protected void show() {
        setPreviousScreen("menu");
        rootNode.addControl(enemySpawnControl);
        mainApplication.getMessageManager().addMessageListener(this);
        
    }

    @Override
    protected void exit() {
        rootNode.removeControl(enemySpawnControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().clear();
        inputManager.removeListener(playerMovementControl);
        inputManager.removeListener(playerShootControl);
        baseApplication.getJoystickInputListener().removeJoystickListener(playerMovementControl);
        baseApplication.getJoystickInputListener().removeJoystickListener(playerShootControl);
        rootNode.detachAllChildren();
        mainApplication.getMessageManager().removeMessageListener(this);
    }

    @Override
    protected void pause() {
    }

    /**
     * This method will fire when a message is received.
     * 
     * @param message
     * @param object 
     */
    public void messageReceived(String message, Object object) {
        Debug.log("Message received: " + message);
        
        if (message.equals("gameover")) {
            shoot = false;
            liveCount --;
            updateUI();
            
            if (liveCount > 0) {
                mainApplication.getInputManager().removeListener(playerMovementControl);
                mainApplication.getInputManager().removeListener(playerShootControl);       
                baseApplication.getJoystickInputListener().removeJoystickListener(playerMovementControl);
                baseApplication.getJoystickInputListener().removeJoystickListener(playerShootControl);
                spawnPlayer();                
            } else {
                showScreen("gameover");
            }
            
        } else
        
        if (message.equals("kill")) {
            killCount ++;
            updateUI();
            
            if (killCount == enemySpawnControl.getEnemyCount()) {
                showScreen("gamecomplete");
            }
        }
        
    }
    
    private void spawnPlayer() {
        
        readyLabel.show();
        readyLabel.center();
        Tween.to(readyLabel, WidgetAccessor.POS_XY, 1f)
                .target(0f, -window.getHeight() * 2f)
                .delay(2f)
                .ease(Linear.INOUT)
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        readyLabel.hide();
                    }
                })
                .start(window.getApplication().getTweenManager());    
        
        //First we load the ship
        spaceship = new Sprite("player", 2.2f, 2.2f, 3, 3, 0);
        spaceship.setMaterial(playerMaterial);
        rootNode.attachChild(spaceship);
        
        RigidBodyControl spaceshipRigidBody = new RigidBodyControl(new BoxCollisionShape(2, 2), 1);
        spaceshipRigidBody.setSensor(true);
        spaceshipRigidBody.setActive(false);
        spaceshipRigidBody.setGravityScale(0f);
        spaceshipRigidBody.setPhysicLocation(0, -8f);
        spaceship.addControl(spaceshipRigidBody);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(spaceship);
        
        playerMovementControl = new PlayerMovementControl(mainApplication);
        spaceship.addControl(playerMovementControl);
        mainApplication.getInputManager().addListener(playerMovementControl, "moveup", "movedown", "moveleft", "moveright");
        baseApplication.getJoystickInputListener().addJoystickListener(playerMovementControl);
        
        
        playerCollisionControl = new PlayerCollisionControl(mainApplication);
        spaceship.addControl(playerCollisionControl);
        
        playerShootControl = new PlayerShootControl(mainApplication);
                
        spaceship.addControl(new AbstractControl() {
            
            private Timer respawnTimer = new Timer(15);
            private int respawnCounter = 14;

            @Override
            protected void controlUpdate(float tpf) {
                
                //Start the timer
                if (respawnCounter >= 14) {
                    respawnTimer.start();
                    respawnCounter --;
                    spatial.setCullHint(Spatial.CullHint.Never);
                }
                
                respawnTimer.update(tpf);
                if (respawnTimer.finished()) {
                    respawnCounter --;
                    
                    if (respawnCounter <= 0) {
                        //Activate the player ship
                        spatial.getControl(RigidBodyControl.class).setActive(true);
                        respawnTimer.stop();
                        spatial.setCullHint(Spatial.CullHint.Never);
                        spatial.addControl(playerShootControl);
                        mainApplication.getInputManager().addListener(playerShootControl, "shoot");
                        baseApplication.getJoystickInputListener().addJoystickListener(playerShootControl);
                        mainApplication.getSoundManager().playSound("player-shieldup");
                        
                    } else {
                        //Make the ship flicker
                        if (respawnCounter % 2 == 0) {
                            spatial.setCullHint(Spatial.CullHint.Always);
                        } else {
                            mainApplication.getSoundManager().playSound("timer");
                            spatial.setCullHint(Spatial.CullHint.Never);
                        }
                        
                        respawnTimer.reset();
                    }                    
                }                
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });
    }

    @Override
    public void update(float tpf) {
        if (shoot && playerShootControl != null) {
            playerShootControl.onAnalog("shoot", 1, tpf);
        }
    }
    
}
