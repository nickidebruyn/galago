/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.screens;

import com.bruynhuis.galago.games.platform2d.Platform2DGameListener;
import com.bruynhuis.galago.listener.JoystickEvent;
import com.bruynhuis.galago.listener.JoystickInputListener;
import com.bruynhuis.galago.listener.JoystickListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.bruynhuis.geometryrunner.MainApplication;
import com.bruynhuis.geometryrunner.game.Game;
import com.bruynhuis.geometryrunner.game.LevelDefinition;
import com.bruynhuis.geometryrunner.game.Player;

/**
 *
 * @author nidebruyn
 */
public class PlayScreen extends AbstractScreen implements Platform2DGameListener, ActionListener, JoystickListener {

    private static final String KEYBOARD_ENTER = "KEY-ENTER";
    private static final String KEYBOARD_SPACE = "KEY-SPACE";
    private MainApplication mainApplication;
    private LevelDefinition levelDefinition;
    private Game game;
    private Player player;
    private JoystickInputListener joystickInputListener;
    private boolean test = false;
    private String editFile;
    public static float camHeight = 3f;
    public static float camLeft = 3f;
    private boolean gameOver = false;
    private ControlButton jumpButton;

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

        jumpButton = new ControlButton(hudPanel, "jumpbutton", window.getWidth(), window.getHeight());
        jumpButton.center();
        jumpButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game.isStarted() && !game.isPaused()) {
                    log("Jump");
                    player.jump(1);
                }
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
            }
        });


    }

    protected void doPlayGameAction() {
        if (isActive()) {
            baseApplication.getSoundManager().playSound("button");
            baseApplication.getSoundManager().playMusic("music");
            jumpButton.show();
            game.start(player);

        }
    }

    protected void doResumeGameAction() {
        if (isActive()) {
            mainApplication.getSoundManager().playSound("button");
            jumpButton.show();
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
//                showScreen("level");
            }

        }
    }

    protected void doNextGameAction() {
        if (isActive()) {
            mainApplication.getSoundManager().playSound("button");
            if (test) {
                showScreen("edit");
            } else {
                showScreen("level");
            }
        }
    }

    protected void doPauseGameAction() {
        if (isActive() && !game.isPaused()) {
            game.pause();
            jumpButton.hide();
            inputManager.setCursorVisible(true);
        }
    }

    @Override
    protected void load() {
        setWaitExit(100);
        mainApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);

        gameOver = false;

        //Now we load the level.
        game = new Game(mainApplication, rootNode);
        if (test) {
            game.test(editFile);
        } else {
            game.play(levelDefinition.getLevelFile());
        }

        game.load();

        player = new Player(game);
        player.load();

        if (!baseApplication.isMobileApp()) {
            initInput();
        }

        camera.setLocation(new Vector3f(game.getStartPosition().x+camLeft, game.getStartPosition().y + camHeight, camera.getLocation().z));

        joystickInputListener = new JoystickInputListener();
        joystickInputListener.addJoystickListener(this);
        joystickInputListener.registerWithInput(inputManager);

        game.addGameListener(this);
    }

    /**
     * Load some input controls
     */
    protected void initInput() {
        if (!inputManager.hasMapping(KEYBOARD_ENTER)) {

            inputManager.addMapping(KEYBOARD_ENTER, new KeyTrigger(KeyInput.KEY_RETURN));
            inputManager.addMapping(KEYBOARD_SPACE, new KeyTrigger(KeyInput.KEY_SPACE));

            inputManager.addListener(this, new String[]{KEYBOARD_ENTER, KEYBOARD_SPACE});
        }

    }

    @Override
    protected void show() {
        setPreviousScreen("menu");
        jumpButton.show();
        
        game.start(player);
        baseApplication.getSoundManager().playMusic("music");
//
//        infoLabel.setText("Tiles: " + game.getTileMap().getTiles().size() + ";   Bodies: " + game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().getBodyCount()
//                + "; After Terrain Count: " + game.getTerrainBodies() + "; Before Terrain Count: " + game.getOriginalTerrainBodies());
    }

    @Override
    protected void exit() {
        baseApplication.getSoundManager().stopMusic("music");
        joystickInputListener.unregisterInput();
        game.close();

    }

    @Override
    protected void pause() {
    }

    public void doGameOver() {

        showScreen("gameover");
        baseApplication.getSoundManager().stopMusic("music");
    }

    public void doGameCompleted() {
        showScreen("gamecomplete");

//        baseApplication.getSoundManager().playSound("win");
//
//        if (test) {
//            gameOverDialog.show(0, player.getScore(), true);
//
//        } else {
//            int score = player.getScore();
//            String oldScoreStr = mainApplication.getGameSaves().getGameData().getProperties().getProperty("level_" + levelDefinition.getUid());
//            int oldScore = 0;
//            if (oldScoreStr != null) {
//                oldScore = Integer.parseInt(oldScoreStr);
//            }
//
//            //Update the game data of games the player played
//            baseApplication.getGameSaves().getGameData().setGamesPlayed(baseApplication.getGameSaves().getGameData().getGamesPlayed() + 1);
//
//            int highestLevel = mainApplication.getGameSaves().getGameData().getCompletedLevel();
//            int currentLevel = levelDefinition.getUid();
//
//            if (currentLevel >= highestLevel) {
//                mainApplication.getGameSaves().getGameData().setCompletedLevel(currentLevel + 1);
//            }
//
//            if (score > oldScore) {
//                mainApplication.getGameSaves().getGameData().getProperties().setProperty("level_" + currentLevel, score + "");
//            }
//
//            mainApplication.getGameSaves().save();
//
//            gameOverDialog.show(oldScore, score, true);
//
//        }

        baseApplication.getSoundManager().stopMusic("music");
    }

    public void doScoreChanged(int score) {

    }

    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
    }

    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider) {

        if (collided != null && collided.getUserData("type") != null) {
            String type = collided.getUserData("type");
            log("Found pickup: " + type);


        }
    }

    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider) {
    }

    public void doCollisionPlayerWithBullet(Spatial collided, Spatial collider) {
    }

    public void doCollisionObstacleWithBullet(Spatial collided, Spatial collider) {
    }

    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider) {
        log("enemy hit: " + collided);

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
    }

    public void doCollisionEnemyWithTerrain(Spatial collided, Spatial collider) {
    }

    public void doCollisionEnemyWithStatic(Spatial collided, Spatial collider) {
    }

    public void doCollisionTerrainWithBullet(Spatial collided, Spatial collider) {
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (isActive()) {

            if (name != null) {

                if (game != null && game.isStarted() && player != null && !game.isPaused()) {

                    if (KEYBOARD_SPACE.equals(name)) {
                        if (isPressed) {
                            player.jump(1);
                        }

                    }

                }

            }
        }
    }

    @Override
    public void update(float tpf) {
        //Update camera position to follow the player
        if (game != null && game.isStarted() && player != null && !game.isPaused()) {

            camera.setLocation(camera.getLocation().interpolate(camera.getLocation().clone().setX(player.getPosition().x+camLeft).setY(camHeight), 0.1f));

            if (gameOver) {
                player.doDamage(1);
                gameOver = false;
            }

        }



    }

    public void picked(PickEvent pickEvent, float tpf) {
        if (isActive() && game.isStarted() && !game.isPaused()) {

            if (pickEvent.isRightButton() && pickEvent.isKeyDown()) {
                baseApplication.getSoundManager().playSound("jump");
                player.jump(1);
            }

        }
    }

    public void drag(PickEvent pickEvent, float tpf) {

        if (isActive() && game.isStarted() && !game.isPaused()) {
        }
    }

    public void stick(JoystickEvent joystickEvent, float fps) {
        if (isActive() && game.isStarted() && !game.isPaused()) {

            //prepare the jump
            if (joystickEvent.isButton3() && joystickEvent.isKeyDown()) {
                baseApplication.getSoundManager().playSound("jump");
                player.jump(1);
            }

        }

    }

    public void doCollisionEnemyWithPickup(Spatial collided, Spatial collider) {
    }
}
