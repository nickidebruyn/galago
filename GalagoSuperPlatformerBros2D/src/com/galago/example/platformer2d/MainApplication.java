package com.galago.example.platformer2d;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.sprite.physics.Dyn4jAppState;
import com.bruynhuis.galago.sprite.physics.ThreadingType;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.util.ColorUtils;
import com.galago.example.platformer2d.game.LevelManager;
import com.galago.example.platformer2d.screens.editor.EditScreen;
import com.galago.example.platformer2d.screens.PlayScreen;
import com.galago.example.platformer2d.screens.editor.LevelFileSelectionScreen;

/**
 * Bounce is a game where the player controls a bouncing ball by tilting the device.
 * Get from point A to point B
 * 
 * @author nickidebruyn
 */
public class MainApplication extends Base2DApplication {
    
    public static final String LEADERBOARD_UID = "xxx";
    
    private LevelManager levelManager;
    
    public MainApplication() {
        super("Super Platformer Bros", 1280, 800, "platformer2d.save", "Interface/Fonts/Roofrunnersactive.fnt", null, false);
    }    

    public static void main(String[] args) {
        new MainApplication();
    }

    @Override
    protected void preInitApp() {
        frustumSize = 11.6f;
        
        splashInfoMessage = "Loading Game";
        
        BACKGROUND_COLOR = ColorUtils.rgb(60, 70, 80);

        levelManager = new LevelManager(this);


    }
    
    @Override
    protected void postInitApp() {
        showScreen("edit");

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
        dyn4jAppState.getPhysicsSpace().setGravity(0, -10f);
        dyn4jAppState.getPhysicsSpace().setSpeed(1f);

    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
//        screenManager.loadScreen("menu", new MenuScreen());
//        screenManager.loadScreen("levels", new LevelSelectScreen());
        screenManager.loadScreen("edit", new EditScreen());
        screenManager.loadScreen("fileselect", new LevelFileSelectionScreen());
        screenManager.loadScreen("play", new PlayScreen());
//        screenManager.loadScreen("options", new OptionsScreen());
//        screenManager.loadScreen("about", new AboutScreen());
//        screenManager.loadScreen("gameover", new GameoverScreen());
//        screenManager.loadScreen("gamecomplete", new GameCompleteScreen());
    }

    @Override
    public void initModelManager(ModelManager modelManager) {

    }

    @Override
    protected void initSound(SoundManager soundManager) {
        soundManager.loadSoundFx("button", "Sounds/button.ogg");
        soundManager.loadSoundFx("die", "Sounds/die.ogg");        
        soundManager.loadSoundFx("jump", "Sounds/pop.ogg");       
        soundManager.loadSoundFx("glass", "Sounds/glass.ogg");       
        soundManager.loadSoundFx("glass-break", "Sounds/glass-break.ogg");      
        soundManager.loadSoundFx("crate-break", "Sounds/crate-break.ogg");      
        soundManager.loadSoundFx("pickup", "Sounds/pickup.ogg");
        soundManager.loadSoundFx("turn", "Sounds/turn.ogg");
        soundManager.loadSoundFx("timer", "Sounds/timer.ogg");
        soundManager.loadSoundFx("win", "Sounds/win.ogg");
        soundManager.loadSoundFx("bounce", "Sounds/bounce.ogg");
        soundManager.loadSoundFx("portal", "Sounds/portal.ogg");

//        soundManager.loadMusic("music", "Sounds/dreambells.ogg");
//        soundManager.setMusicVolume("music", 0.2f);
        
        
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
        effectManager.loadEffect("die", "Models/effects/die.j3o");
        effectManager.loadEffect("pickup", "Models/effects/pickup.j3o");
        effectManager.loadEffect("glass-break", "Models/effects/glass-break.j3o");
        effectManager.loadEffect("crate-break", "Models/effects/crate-break.j3o");

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
    
    public EditScreen getEditScreen() {
        return (EditScreen) getScreenManager().getScreen("edit");
    }

    @Override
    protected void initFonts(FontManager fontManager) {
        fontManager.loadFont(new FontStyle(74));
        fontManager.loadFont(new FontStyle(42));
        fontManager.loadFont(new FontStyle(36));
        fontManager.loadFont(new FontStyle(24));
    }
}
