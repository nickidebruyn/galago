package com.bruynhuis.galago.flat;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.flat.screen.InfoScreen;
import com.bruynhuis.galago.flat.screen.LineShapeScreen;
import com.bruynhuis.galago.flat.screen.MenuScreen;
import com.bruynhuis.galago.flat.screen.PhysicsScreen;
import com.bruynhuis.galago.flat.screen.RotatingCameraScreen;
import com.bruynhuis.galago.flat.screen.VehicleScreen;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.sprite.physics.Dyn4jAppState;
import com.bruynhuis.galago.sprite.physics.ThreadingType;

/**
 * test
 * @author normenhansen
 */
public class MainApplication extends Base2DApplication {

    public MainApplication() {
        super("Galago 2D", 1280, 720, "galago2d.save", null, null, false);
    }

    public static void main(String[] args) {
        new MainApplication();
    }

    @Override
    protected void preInitApp() {
        frustumSize = 6f;
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
        dyn4jAppState = new Dyn4jAppState(ThreadingType.PARALLEL);
        stateManager.attach(dyn4jAppState);
        dyn4jAppState.getPhysicsSpace().setGravity(0, -15f);
        dyn4jAppState.getPhysicsSpace().setSpeed(100f);

    }

    @Override
    protected void postInitApp() {       
        showScreen("info");
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen("info", new InfoScreen());
        screenManager.loadScreen("menu", new MenuScreen());
        screenManager.loadScreen("physics", new PhysicsScreen());
        screenManager.loadScreen("vehicle", new VehicleScreen());
        screenManager.loadScreen("line", new LineShapeScreen());
        screenManager.loadScreen("camrotation", new RotatingCameraScreen());
    }

    @Override
    protected void initSound(SoundManager soundManager) {
        
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
        AnimatedSprite explosionSprite = new AnimatedSprite("explosion", 2.4f, 2f, 3, 4, 5);
        explosionSprite.setMaterial(getAssetManager().loadMaterial("Materials/explosion.j3m"));
        effectManager.loadEffect("explosion", explosionSprite);
        
    }

    @Override
    protected void initTextures(TextureManager textureManager) {
        
    }

    @Override
    public void initModelManager(ModelManager modelManager) {
        modelManager.loadMaterial("Materials/background.j3m");
        modelManager.loadMaterial("Materials/line.j3m");
    }

}
