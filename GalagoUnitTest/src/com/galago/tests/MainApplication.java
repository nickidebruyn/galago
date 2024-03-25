/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.util.ColorUtils;
import com.galago.tests.screens.BatchGUIScreen;
import com.galago.tests.screens.ButtonLayoutsScreen;
import com.galago.tests.screens.CombatScreen;
import com.galago.tests.screens.EggScreen;
import com.galago.tests.screens.FireScreen;
import com.galago.tests.screens.GrassScreen;
import com.galago.tests.screens.GridPanelScreen;
import com.galago.tests.screens.InputGuiScreen;
import com.galago.tests.screens.JoystickRawScreen;
import com.galago.tests.screens.JoystickScreen;
import com.galago.tests.screens.LightningSky;
import com.galago.tests.screens.MenuScreen;
import com.galago.tests.screens.MotionBlurScreen;
import com.galago.tests.screens.PagerPanelScreen;
import com.galago.tests.screens.ParticleEditor;
import com.galago.tests.screens.PhysicsJointScreen;
import com.galago.tests.screens.PhysicsScreen;
import com.galago.tests.screens.PostShaderScreen;
import com.galago.tests.screens.RagdollScreen;
import com.galago.tests.screens.RoadMeshScreen;
import com.galago.tests.screens.SplatMarkerScreen;
import com.galago.tests.screens.TextWriterScreen;
import com.galago.tests.screens.TextureMaskingScreen;
import com.galago.tests.screens.WorldEditorScreen;
import com.galago.tests.screens.TrailRenderScreen;
import com.galago.tests.screens.WaterMovementScreen;
import com.galago.tests.screens.WaterWaveScreen;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import java.io.IOException;

/**
 *
 * @author NideBruyn
 */
public class MainApplication extends Base3DApplication {

    public static void main(String[] args) {
        new MainApplication();
    }

    public MainApplication() {
        super("Galago Showcase", 1600, 900, "galagoshowcase.save", null, null, false);
    }

    @Override
    protected void preInitApp() {
        BACKGROUND_COLOR = ColorUtils.rgb(44, 62, 80);
    }

    @Override
    protected void postInitApp() {
        registerInputMappings("left_arrow", new KeyTrigger(keyInput.KEY_LEFT));
        registerInputMappings("right_arrow", new KeyTrigger(keyInput.KEY_RIGHT));        
        
        showScreen(MenuScreen.NAME);
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

//    @Override
//    protected void initPhysics() {
//        //Don't load if it already exist
//        if (bulletAppState != null) {
//            return;
//        }
//        /**
//         * Set up Physics
//         */
//        bulletAppState = new BulletAppState(new Vector3f(-100, 0, -100), new Vector3f(100, 100, 100));
//        stateManager.attach(bulletAppState);
////        bulletAppState.getPhysicsSpace().setAccuracy(1f/80f);
////        bulletAppState.getPhysicsSpace().setMaxSubSteps(2);
//    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen(MenuScreen.NAME, new MenuScreen());
        screenManager.loadScreen("buttons", new ButtonLayoutsScreen());
        screenManager.loadScreen("pager", new PagerPanelScreen());
        screenManager.loadScreen("batch", new BatchGUIScreen());
        screenManager.loadScreen("grid", new GridPanelScreen());
        screenManager.loadScreen("input", new InputGuiScreen());
        screenManager.loadScreen("physics", new PhysicsScreen());
        screenManager.loadScreen(PhysicsJointScreen.NAME, new PhysicsJointScreen());
        screenManager.loadScreen("fire", new FireScreen());
        screenManager.loadScreen("joystick", new JoystickScreen());
        screenManager.loadScreen("rawjoystick", new JoystickRawScreen());
        screenManager.loadScreen("watermovement", new WaterMovementScreen());
        screenManager.loadScreen("waterwave", new WaterWaveScreen());
        screenManager.loadScreen("textwrite", new TextWriterScreen());
        screenManager.loadScreen("postshader", new PostShaderScreen());
        screenManager.loadScreen("roadmesh", new RoadMeshScreen());
        screenManager.loadScreen("lightning", new LightningSky());
        screenManager.loadScreen("motionblur", new MotionBlurScreen());
        screenManager.loadScreen("trailrender", new TrailRenderScreen());
        screenManager.loadScreen(WorldEditorScreen.NAME, new WorldEditorScreen());
        screenManager.loadScreen(TextureMaskingScreen.NAME, new TextureMaskingScreen());
        screenManager.loadScreen(EggScreen.NAME, new EggScreen());
        screenManager.loadScreen(RagdollScreen.NAME, new RagdollScreen());
        screenManager.loadScreen(CombatScreen.NAME, new CombatScreen());
        screenManager.loadScreen(SplatMarkerScreen.NAME, new SplatMarkerScreen());
        screenManager.loadScreen(ParticleEditor.NAME, new ParticleEditor());
        screenManager.loadScreen(GrassScreen.NAME, new GrassScreen());

    }

    @Override
    public void initModelManager(ModelManager modelManager) {
    }

    @Override
    protected void initSound(SoundManager soundManager) {
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
    }

    @Override
    protected void initTextures(TextureManager textureManager) {
    }

    @Override
    protected void initFonts(FontManager fontManager) {
    }
    @Override
    protected Object[] getIconList() throws IOException {
        return null;
    }
}
