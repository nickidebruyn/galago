package com.example.spaceshooter;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.example.spaceshooter.screens.AboutScreen;
import com.example.spaceshooter.screens.GameCompleteScreen;
import com.example.spaceshooter.screens.GameoverScreen;
import com.example.spaceshooter.screens.MenuScreen;
import com.example.spaceshooter.screens.OptionsScreen;
import com.example.spaceshooter.screens.PlayScreen;

/**
 * A simple space shooter game
 *
 * @author Nicki de Bruyn
 */
public class MainApplication extends Base2DApplication {
    
    private int levelWidth = 16;
    private int levelHeight = 9;
    private float levelSpeed = 0.2f;

    public MainApplication() {
        super("Space Shooter", 1280, 720, "spaceshooter.save", "Interface/Fonts/KenVectorFuture.fnt", null, false);
    }

    public static void main(String[] args) {
        new MainApplication();
    }

    @Override
    protected void preInitApp() {
    }

    @Override
    protected void postInitApp() {
        showScreen("menu");
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    protected void initScreens(ScreenManager sm) {
        sm.loadScreen("menu", new MenuScreen());
        sm.loadScreen("play", new PlayScreen());
        sm.loadScreen("options", new OptionsScreen());
        sm.loadScreen("about", new AboutScreen());
        sm.loadScreen("gameover", new GameoverScreen());
        sm.loadScreen("gamecomplete", new GameCompleteScreen());
    }

    @Override
    public void initModelManager(ModelManager mm) {
        mm.loadMaterial("Materials/enemies.j3m");
        mm.loadMaterial("Materials/player.j3m");
        mm.loadMaterial("Materials/meteors.j3m");
        mm.loadMaterial("Materials/bullets.j3m");
        
        mm.loadModel("Models/starfield.j3o");
        
    }

    @Override
    protected void initSound(SoundManager sm) {
        sm.loadSoundFx("player-die", "Sounds/sfx_lose.ogg");
        sm.setSoundVolume("player-die", 1f);
        
        sm.loadSoundFx("enemy-die", "Sounds/explode.ogg");
        sm.setSoundVolume("enemy-die", 0.1f);
        
        sm.loadSoundFx("bullet-die", "Sounds/sfx_zap.ogg");
        sm.setSoundVolume("bullet-die", 1f);
        
        sm.loadSoundFx("shield-down", "Sounds/sfx_shieldDown.ogg");
        sm.setSoundVolume("shield-down", 1f);
        
        sm.loadSoundFx("player-laser", "Sounds/sfx_laser1.ogg");
        sm.setSoundVolume("player-laser", 1f);
        
        sm.loadSoundFx("player-shieldup", "Sounds/sfx_shieldUp.ogg");
        sm.setSoundVolume("player-shieldup", 1f);
        
        sm.loadSoundFx("timer", "Sounds/count.ogg");
        sm.setSoundVolume("timer", 0.2f);
        
        sm.loadSoundFx("button", "Sounds/sfx_twoTone.ogg");
        sm.setSoundVolume("button", 0.2f);
    }

    @Override
    protected void initEffect(EffectManager em) {
        em.loadEffect("player-explode", "Models/effects/player-explode.j3o");
        
        em.loadEffect("enemy-explode", "Models/effects/enemy-explode.j3o");
        
        em.loadEffect("bullet-explode", "Models/effects/bullet-explode.j3o");
    }

    @Override
    protected void initTextures(TextureManager tm) {
    }

    public int getLevelWidth() {
        return levelWidth;
    }

    public int getLevelHeight() {
        return levelHeight;
    }

    public float getLevelSpeed() {
        return levelSpeed;
    }

    public void setLevelSpeed(float levelSpeed) {
        this.levelSpeed = levelSpeed;
    }
    
    
}
