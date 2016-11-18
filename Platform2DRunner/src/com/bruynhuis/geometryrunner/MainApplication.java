package com.bruynhuis.geometryrunner;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.sprite.physics.Dyn4jAppState;
import com.bruynhuis.galago.sprite.physics.ThreadingType;
import com.bruynhuis.geometryrunner.game.LevelManager;
import com.bruynhuis.geometryrunner.screens.AboutScreen;
import com.bruynhuis.geometryrunner.screens.EditScreen;
import com.bruynhuis.geometryrunner.screens.GameCompleteScreen;
import com.bruynhuis.geometryrunner.screens.GameoverScreen;
import com.bruynhuis.geometryrunner.screens.LevelSelectScreen;
import com.bruynhuis.geometryrunner.screens.MenuScreen;
import com.bruynhuis.geometryrunner.screens.OptionsScreen;
import com.bruynhuis.geometryrunner.screens.PlayScreen;
import com.jme3.math.ColorRGBA;

/**
 * test
 *
 * @author nicki de bruyn
 */
public class MainApplication extends Base2DApplication {
    
    private LevelManager levelManager;

    public MainApplication() {
        super("Geometry Runner", 1280, 720, "geometryrunner.save", "Interface/Fonts/SnapITC.fnt", null, false);
    }

    public static void main(String[] args) {
        new MainApplication();
    }

    @Override
    protected void preInitApp() {
        if (isMobileApp()) {
            frustumSize = 5.8f;
        } else {
            frustumSize = 6f;
        }
        
        splashInfoMessage = "Loading Game";
        
        BACKGROUND_COLOR = ColorRGBA.White;

        levelManager = new LevelManager(this);


    }
    
    @Override
    protected void postInitApp() {
        showScreen("menu");

    }
    
    @Override
    protected void initPhysics() {        
        //Don't load if it already exist
        if (dyn4jAppState != null) {
            return;
        }
        /**
         * Set up Physics
         */
        dyn4jAppState = new Dyn4jAppState(ThreadingType.SEQUENTIAL);
        stateManager.attach(dyn4jAppState);
        dyn4jAppState.getPhysicsSpace().setGravity(0, -30f);
        dyn4jAppState.getPhysicsSpace().setSpeed(1f);

    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen("menu", new MenuScreen());
        screenManager.loadScreen("levels", new LevelSelectScreen());
        screenManager.loadScreen("edit", new EditScreen());
        screenManager.loadScreen("play", new PlayScreen());
        screenManager.loadScreen("options", new OptionsScreen());
        screenManager.loadScreen("about", new AboutScreen());
        screenManager.loadScreen("gameover", new GameoverScreen());
        screenManager.loadScreen("gamecomplete", new GameCompleteScreen());
    }

    @Override
    public void initModelManager(ModelManager modelManager) {
        modelManager.loadModel("Models/dust.j3o");

    }

    @Override
    protected void initSound(SoundManager soundManager) {
        soundManager.loadSoundFx("button", "Sounds/switch2.ogg");

        soundManager.loadMusic("music", "Sounds/Factory Birth - Light Layer - 60ms Overlap.ogg");
        soundManager.setMusicVolume("music", 0.7f);
        
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
        effectManager.loadEffect("die", "Models/die.j3o");
    }

    @Override
    protected void initTextures(TextureManager textureManager) {
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public PlayScreen getPlayScreen() {
        return (PlayScreen) getScreenManager().getScreen("play");
    }
}
