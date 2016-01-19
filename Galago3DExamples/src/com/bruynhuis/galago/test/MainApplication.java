package com.bruynhuis.galago.test;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.test.screens.BatchGUIScreen;
import com.bruynhuis.galago.test.screens.BlockBuilderScreen;
import com.bruynhuis.galago.test.screens.ButtonLayoutsScreen;
import com.bruynhuis.galago.test.screens.FireScreen;
import com.bruynhuis.galago.test.screens.GridPanelScreen;
import com.bruynhuis.galago.test.screens.InputGuiScreen;
import com.bruynhuis.galago.test.screens.JoystickRawScreen;
import com.bruynhuis.galago.test.screens.JoystickScreen;
import com.bruynhuis.galago.test.screens.MenuScreen;
import com.bruynhuis.galago.test.screens.PagerPanelScreen;
import com.bruynhuis.galago.test.screens.PhysicsScreen;
import com.bruynhuis.galago.test.screens.RainbowScreen;
import com.bruynhuis.galago.test.screens.ShadedTerrainScreen;
import com.bruynhuis.galago.test.screens.TextWriterScreen;
import com.bruynhuis.galago.test.screens.UnshadedTerrainScreen;
import com.bruynhuis.galago.test.screens.WaterMovementScreen;
import com.bruynhuis.galago.test.screens.WaterWaveScreen;
import com.jme3.math.ColorRGBA;

/**
 * 
 * @author nicki de Bruyn
 */
public class MainApplication extends Base3DApplication {

    public MainApplication() {
        super("Galago Examples", 1280, 720, "galagoexamples.save", null, "Interface/splash.png", false);
    }    

    public static void main(String[] args) {
        new MainApplication();
    }

    @Override
    protected void preInitApp() {
        BACKGROUND_COLOR = ColorRGBA.White;                
        
    }

    @Override
    protected void postInitApp() {
        showScreen("textwrite");
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen("menu", new MenuScreen());
        screenManager.loadScreen("buttons", new ButtonLayoutsScreen());
        screenManager.loadScreen("terrain-unshaded", new UnshadedTerrainScreen());
        screenManager.loadScreen("terrain-shaded", new ShadedTerrainScreen());
        screenManager.loadScreen("pager", new PagerPanelScreen());
        screenManager.loadScreen("batch", new BatchGUIScreen());
        screenManager.loadScreen("grid", new GridPanelScreen());
        screenManager.loadScreen("input", new InputGuiScreen());
        screenManager.loadScreen("physics", new PhysicsScreen());
        screenManager.loadScreen("blockbuilder", new BlockBuilderScreen());
        screenManager.loadScreen("fire", new FireScreen());
        screenManager.loadScreen("joystick", new JoystickScreen());
        screenManager.loadScreen("rawjoystick", new JoystickRawScreen());
        screenManager.loadScreen("watermovement", new WaterMovementScreen());
        screenManager.loadScreen("waterwave", new WaterWaveScreen());
        screenManager.loadScreen("rainbow", new RainbowScreen());
        screenManager.loadScreen("textwrite", new TextWriterScreen());
    }

    @Override
    protected void initSound(SoundManager soundManager) {
        
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
        
        
    }

    @Override
    public void initModelManager(ModelManager modelManager) {
        
    }

    @Override
    protected void initTextures(TextureManager textureManager) {

    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

}
