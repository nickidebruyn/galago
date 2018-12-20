/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens;

import com.bruynhuis.galago.games.platform2d.Platform2DGameListener;
import com.bruynhuis.galago.listener.JoystickEvent;
import com.bruynhuis.galago.listener.JoystickInputListener;
import com.bruynhuis.galago.listener.JoystickListener;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.SensorListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.galago.example.platformer2d.MainApplication;
import com.galago.example.platformer2d.game.Game;
import com.galago.example.platformer2d.game.LevelDefinition;
import com.galago.example.platformer2d.game.SuperBrosPlayer;
import com.galago.example.platformer2d.game.controls.MoverControl;
import com.galago.example.platformer2d.game.controls.MushroomControl;
import com.galago.example.platformer2d.game.controls.PortalControl;
import com.galago.example.platformer2d.game.enemies.EnemyControl;
import com.galago.example.platformer2d.game.terrain.CrateControl;
import com.galago.example.platformer2d.game.terrain.GlassControl;
import com.galago.example.platformer2d.ui.ScoreHeaderPanel;
import com.galago.example.platformer2d.ui.ShowListener;
import com.galago.example.platformer2d.ui.TipDialog;

/**
 *
 * @author Nidebruyn
 */
public class PlayScreen extends AbstractScreen implements Platform2DGameListener, KeyboardControlListener, JoystickListener, SensorListener, PickListener {

    public static final String DEAD = "dead";
    private MainApplication mainApplication;
    private LevelDefinition levelDefinition;
    private Label bodiesLabel;
    private Game game;
    private SuperBrosPlayer player;
    private JoystickInputListener joystickInputListener;
    private TouchPickListener touchPickListener;
    private KeyboardControlInputListener keyboardControlInputListener;
    private boolean test = false;
    private String editFile;
    private boolean tipDialogWasShow = false;

    public static float camHeight = 1f;
    public static float camLeft = 1f;
    public static float cameraMoveHeight = 4f;
    public static float levelXMin = 0;
    public static float levelXMax = 20;
    
    private boolean gameOver = false;
    private float tiltAngle;
    private float maxTiltAngle = 70f;
    private float startTiltAngle = 5f;
    private boolean left = false;
    private boolean right = false;
    private float movementAmount = 0;
    public static final float maximumMovement = 5f;
    private ScoreHeaderPanel scoreHeaderPanel;
    private TipDialog tipDialog;
    private Timer playTimer = new Timer(100);
    private int time = 0;
    private Spatial pickup;

    public void setTest(boolean test) {
        this.test = test;
    }

    public void setEditFile(String editFile) {
        this.editFile = editFile;
    }

    public void setLevelDefinition(LevelDefinition levelDefinition) {
        this.levelDefinition = levelDefinition;
    }

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;
        mainApplication.addSensorListener(this);

        scoreHeaderPanel = new ScoreHeaderPanel(hudPanel);
        scoreHeaderPanel.centerTop(0, 0);

        bodiesLabel = new Label(hudPanel, "Bodies: ", 18, 300, 30);
        bodiesLabel.setAlignment(TextAlign.LEFT);
        bodiesLabel.leftBottom(2, 2);

        tipDialog = new TipDialog(window);
        tipDialog.addTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    tipDialogWasShow = true;
                    tipDialog.hide();

                }
            }
        });

        tipDialog.addShowListener(new ShowListener() {
            public void shown() {
            }

            public void hidden() {
                doPlayGameAction();
            }
        });

        touchPickListener = new TouchPickListener(camera, rootNode);
        touchPickListener.setPickListener(this);

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);
    }

    protected void doPlayGameAction() {
        if (isActive()) {
//            baseApplication.getSoundManager().playMusic("music");
            touchPickListener.registerWithInput(inputManager);
            game.start(player);
            playTimer.start();

        }
    }

    protected void doResumeGameAction() {
        if (isActive()) {
            mainApplication.getSoundManager().playSound("button");
            game.resume();
        }
    }

    protected void doRestartGameAction() {
        if (isActive()) {
            mainApplication.getSoundManager().playSound("button");
            showScreen("play");
        }
    }

    protected void doExitGameAction() {
        if (isActive()) {
            mainApplication.getSoundManager().playSound("button");
            if (test) {
                showScreen("edit");
            } else {
                showScreen("levels");
            }

        }
    }

    protected void doNextGameAction() {
        if (isActive()) {
            mainApplication.getSoundManager().playSound("button");
            if (test) {
                showScreen("edit");
            } else {
                showScreen("levels");
            }
        }
    }

    protected void doPauseGameAction() {
        if (isActive() && !game.isPaused()) {
            game.pause();
            inputManager.setCursorVisible(true);
        }
    }

    @Override
    protected void load() {
        setWaitExit(50);
//        mainApplication.getViewPort().setBackgroundColor(MainApplication.BACKGROUND_COLOR);

        time = 60;
        movementAmount = 0;
        left = false;
        right = false;

        scoreHeaderPanel.setTime(time);
        scoreHeaderPanel.setScore(0);
        gameOver = false;

        //Now we load the level.
        game = new Game(mainApplication, rootNode);
        if (test) {
            game.test(editFile);
        } else {
            game.play(levelDefinition.getLevelFile());
            time = levelDefinition.getTime();
        }

        game.load();

        player = new SuperBrosPlayer(game);
        player.load();

        camera.setLocation(new Vector3f(0, 0.9f, 10));

        if (mainApplication.isMobileApp()) {
            mainApplication.setCameraDistanceFrustrum(8.5f);
        } else {
            mainApplication.setCameraDistanceFrustrum(9f);
        }

        joystickInputListener = new JoystickInputListener();
        joystickInputListener.addJoystickListener(this);
        joystickInputListener.registerWithInput(inputManager);

        game.addGameListener(this);

        scoreHeaderPanel.setTime(time);
    }

    @Override
    protected void show() {
        setWaitExit(30f);
        setPreviousScreen("edit");
        bodiesLabel.setText("Bodies: " + mainApplication.getDyn4jAppState().getPhysicsSpace().getBodyCount());

        if (!baseApplication.isMobileApp()) {
            keyboardControlInputListener.registerWithInput(inputManager);
        }

        if (tipDialogWasShow) {
            doPlayGameAction();
        } else {
            tipDialog.show();
        }

    }

    @Override
    protected void exit() {
        playTimer.stop();
//        baseApplication.getSoundManager().stopMusic("music");

        if (!baseApplication.isMobileApp()) {
            keyboardControlInputListener.unregisterInput();
        }

        touchPickListener.unregisterInput();
        joystickInputListener.unregisterInput();
        game.close();

    }

    @Override
    protected void pause() {
    }

    public void doGameOver() {
        playTimer.stop();
        baseApplication.getSoundManager().playSound("die");

        showScreen("play");
    }

    public void doGameCompleted() {
        playTimer.stop();
        baseApplication.getSoundManager().playSound("win");

        player.doLevelCompleteAction();

        //Update the game data of games the player played
        baseApplication.getGameSaves().getGameData().setGamesPlayed(baseApplication.getGameSaves().getGameData().getGamesPlayed() + 1);

        if (levelDefinition != null) {
            int highestLevel = mainApplication.getGameSaves().getGameData().getCompletedLevel();
            int currentLevel = levelDefinition.getUid();

            if (currentLevel >= highestLevel) {
                mainApplication.getGameSaves().getGameData().setCompletedLevel(currentLevel + 1);
            }

        }

        mainApplication.getGameSaves().save();

        showScreen("edit");

    }

    public void doScoreChanged(int score) {
        scoreHeaderPanel.setScore(score);
    }

    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
        //No need for player collision logic
        if (collided.getControl(GlassControl.class) != null) {
            if (player.isOnGround()) {
                collided.getControl(GlassControl.class).doHit();
                player.jump(1f);
            }
        } else if (collided.getControl(CrateControl.class) != null) {
            if (player.isOnGround()) {
                collided.getControl(CrateControl.class).doHit();
                player.jump(1);
            }
        } else if (collided.getControl(MushroomControl.class) != null) {
            if (player.isOnGround()) {
                collided.getControl(MushroomControl.class).doHit();
                player.jump(1.5f);
            }
        } else if (collided.getControl(PortalControl.class) != null) {
            collided.getControl(PortalControl.class).doHit();

        } else if (collided.getControl(MoverControl.class) != null) {
            //Do nothing as it is handled in the moverControl
//            Debug.log("mover collision");
            collided.getControl(MoverControl.class).doHit();

        } else if (!collided.getControl(RigidBodyControl.class).isSensor()) {
            log("name: " + collided.getName());
            player.jump(1f);
        }

    }

    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider) {
        //Only remove this if it is not dead
        if (collided.getUserData(DEAD) == null) {
            pickup = collided;
            collided.setUserData(DEAD, "true");

        }

    }

    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider) {

        float dif = collider.getLocalTranslation().y - collided.getLocalTranslation().y;
        log("dist: " + dif);

        if (collider.getLocalTranslation().y > collided.getLocalTranslation().y && dif > 0.8f) {
            collided.getControl(EnemyControl.class).doHit();
//            player.bounce();

        } else {
            collided.getControl(EnemyControl.class).doEat();
            if (!gameOver) {
                gameOver = true;
            }
        }

    }

    public void doCollisionPlayerWithBullet(Spatial collided, Spatial collider) {
        //No need for player collision logic
    }

    public void doCollisionObstacleWithBullet(Spatial collided, Spatial collider) {
        //No need for this because no bullets in this game
    }

    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider) {
        //No need for this because no bullets in this game
    }

    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider) {
    }

    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider) {
        if (!gameOver) {
            gameOver = true;
        }
    }

    public void doCollisionEnemyWithObstacle(Spatial collided, Spatial collider) {
    }

    public void doCollisionPlayerWithTerrain(Spatial collided, Spatial collider) {

//        if (isOnTop(collided)) {
//            player.jump(1);
//        }
    }

    public void doCollisionEnemyWithTerrain(Spatial collided, Spatial collider) {
    }

    public void doCollisionEnemyWithStatic(Spatial collided, Spatial collider) {
    }

    public void doCollisionEnemyWithPickup(Spatial collided, Spatial collider) {
    }

    public void doCollisionTerrainWithBullet(Spatial collided, Spatial collider) {
        //No need for this because no bullets in this game
    }

    @Override
    public void update(float tpf) {
        //Update camera position to follow the player
        if (game != null && game.isStarted() && player != null && !game.isGameOver() && !game.isPaused()) {

            float targetX = FastMath.clamp(player.getPosition().x, levelXMin, levelXMax);
            
            //update the camera
            if (player.getPosition().y > cameraMoveHeight) {
                camera.setLocation(camera.getLocation().interpolateLocal(camera.getLocation().clone().setX(targetX).setY(player.getPosition().y + PlayScreen.camHeight), 0.025f));
            } else {
                camera.setLocation(camera.getLocation().interpolateLocal(camera.getLocation().clone().setX(targetX).setY(PlayScreen.camHeight), 0.025f));
            }
            
//            bodiesLabel.setText("Bodies: " + mainApplication.getDyn4jAppState().getPhysicsSpace().getBodyCount());

            if (left) {
                player.moveLeft(movementAmount);
            } else if (right) {
                player.moveRight(movementAmount);
            } else {
                player.moveCancel();
            }

            if (gameOver) {
                player.doDamage(1);
                gameOver = false;
            }

            //Pickup detected
            if (pickup != null) {
                Vector3f pos = pickup.getLocalTranslation().clone();
                pickup.removeFromParent();
                pickup = null;
                baseApplication.getEffectManager().doEffect("pickup", pos);
                baseApplication.getSoundManager().playSound("pickup");
//                player.doSmileFace();
                player.addScore(1);

            }

            playTimer.update(tpf);
            if (playTimer.finished()) {
                time--;
                playTimer.reset();
                scoreHeaderPanel.setTime(time);
                if (time <= 0) {
                    game.doGameOver();
                } else if (time <= 5) {
                    baseApplication.getSoundManager().playSound("timer");
                }

            }

        }

    }

    public void picked(PickEvent pickEvent, float tpf) {
        if (isActive() && game.isStarted() && !game.isPaused()) {

//            if (pickEvent.isKeyDown()) {
            player.setMoverInactive();
//            }

        }
    }

    public void drag(PickEvent pickEvent, float tpf) {

        if (isActive() && game.isStarted() && !game.isPaused()) {
        }
    }

    public void stick(JoystickEvent joystickEvent, float fps) {
        if (isActive() && game.isStarted() && !game.isPaused()) {

            //prepare the jump
            if (joystickEvent.isButton3() && joystickEvent.isButtonDown()) {
            }

        }

    }

    public void doSensorAction(float fisting, float tilting, float twisting) {
        if (isActive() && game != null && game.isStarted() && !game.isPaused()) {

//            sensorLabel.setText("Fisting: " + fisting + "\nTilting: " + tilting + "\nTwisting: " + twisting);
            tiltAngle = FastMath.RAD_TO_DEG * tilting;

            if (tiltAngle < -maxTiltAngle) {
                tiltAngle = -maxTiltAngle;

            } else if (tiltAngle > maxTiltAngle) {
                tiltAngle = maxTiltAngle;
            }

            //Detect movement
            movementAmount = maximumMovement * (1f - (maxTiltAngle - FastMath.abs(tiltAngle)) / maxTiltAngle);

            if (tiltAngle < -startTiltAngle) {
                left = true;
                right = false;

            } else if (tiltAngle > startTiltAngle) {
                left = false;
                right = true;

            } else {
                left = false;
                right = false;
                movementAmount = 0;
//                player.setPowerPercentage(0);
            }

        }
    }

    @Override
    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {
                if (game != null && game.isStarted() && player != null && !game.isPaused()) {

                    if (keyboardControlEvent.isUp()) {
                        if (keyboardControlEvent.isKeyDown()) {
                            player.jump(1);
                        }
                    }

                    if (keyboardControlEvent.isLeft()) {
                        if (keyboardControlEvent.isKeyDown()) {
                            left = true;
                            movementAmount = maximumMovement;
                        } else {
                            left = false;
                        }
                    }

                    if (keyboardControlEvent.isRight()) {
                        if (keyboardControlEvent.isKeyDown()) {
                            right = true;
                            movementAmount = maximumMovement;
                        } else {
                            right = false;
                        }
                    }

                    if (keyboardControlEvent.isButton1()) {
                        if (keyboardControlEvent.isKeyDown()) {
                            player.setMoverInactive();
                        }                        
                    }

                } else if (game != null && !game.isStarted() && tipDialog.isVisible()) {
                    if (keyboardControlEvent.isButton1()) {
                        if (keyboardControlEvent.isKeyDown()) {
                            tipDialog.hide();
                        }                        
                    }

                }
    }
}
