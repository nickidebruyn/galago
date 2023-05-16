/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.screen;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.ui.listener.EscapeListener;
import com.bruynhuis.galago.ui.listener.FadeListener;
import com.bruynhuis.galago.ui.window.Fader;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * The abstract screen represents a logical screen in the game. If you wish to
 * create a screen you need to extend this class and implement all abstract
 * methods. Also for you to use this screen you have to initialize it in the
 * BaseApplication initScreens() method.
 *
 * @author nidebruyn
 */
public abstract class AbstractScreen extends AbstractAppState implements EscapeListener, FadeListener {

    protected BaseApplication baseApplication;
    protected AssetManager assetManager;
    protected InputManager inputManager;
    protected Camera camera;
    protected Node rootNode;
    protected Window window;
    protected Panel loadingPanel;
    protected Panel hudPanel;
    protected Fader fader;
    protected boolean active = false;
    protected String nextScreen;
    protected String previousScreen;
    protected boolean setPrevious = true;
    protected boolean loading = false;
    protected String screenName;

    /**
     * This method must contain all initialization of widgets for this screen.
     * Each screen has a hudPanel. Widgets can be added to this hud panel. This
     * init method will be called when the game starts up and you create an
     * instance of the screen.
     */
    protected abstract void init();

    /**
     * This method is called just before the screen is shown. Here you will
     * normally load the level or game.
     */
    protected abstract void load();

    /**
     * This method is called after the screen was loaded and it is set to be
     * visible.
     */
    protected abstract void show();

    /**
     * This method is called when you call to showScreen(). Called when the
     * screen exits. Here you will normally detach all spatials from the
     * rootNode and remove postprocessor filters, lights, etc.
     */
    protected abstract void exit();

    /**
     * This method will be called when it is an android device and the screen
     * dims or locks, etc.
     */
    protected abstract void pause();

    /**
     * This method will be called on a mobile device when the application is
     * resumed.
     */
    protected void resume() {
    }

    /**
     * For internal use only.
     */
    public void firePauseAction() {
        pause();
    }

    public void fireResumeAction() {
        resume();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        baseApplication = (BaseApplication) app;
        assetManager = baseApplication.getAssetManager();
        inputManager = baseApplication.getInputManager();
        camera = baseApplication.getCamera();

        window = new Window(baseApplication, baseApplication.getGuiNode(), baseApplication.getSCREEN_WIDTH(), baseApplication.getSCREEN_HEIGHT(), baseApplication.getBitmapFont());

        loadingPanel = new Panel(window, "Resources/fade.png");
        window.add(loadingPanel);
        loadingPanel.center();
        loadingPanel.setName("Loading panel");

        hudPanel = new Panel(window, null);
        hudPanel.setName("Hud panel");
        window.add(hudPanel);
        hudPanel.center();

        rootNode = new Node("Root Node");
        rootNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                //Controls where this screen must go
                if (loading) {
                    load();
                    loading = false;
                    window.getFader().fadeIn();

                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

        init();

        //Add the fade over the other gui's
        fader = new Fader(window, ColorRGBA.Black, 5f, 5f, 1f, 1f);
        fader.setName("Fader screens");
        fader.addFadeListener(this);
        window.setFader(fader);
        window.getFader().setVisible(false);

        window.setVisible(false);

        window.optimize();

        //Do this only at the end!
        super.initialize(stateManager, app);
    }

    /**
     * Set the exit fader wait time.
     *
     * @param wait
     */
    protected void setWaitExit(float wait) {
        fader.setOutWait(wait);
    }

    /**
     * set the fader screen enter wait time.
     *
     * @param wait
     */
    protected void setWaitEnter(float wait) {
        fader.setInWait(wait);
    }
    
    protected void setSpeedExit(float speed) {
        fader.setOutspeed(speed);
        
    }
    
    protected void setSpeedEnter(float speed) {
        fader.setInspeed(speed);
        
    }

    /**
     * Log some text to the console
     *
     * @param text
     */
    protected void log(String text) {
        System.out.println(text);
    }

    /**
     * Can be called to determine if the screen is active. Do this in the button
     * events or in game loops.
     *
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Helper method to return 3d world coords for the cursor position.
     *
     * @return
     */
    public Vector3f getTouchWorldCoords() {
        return camera.getWorldCoordinates(inputManager.getCursorPosition(), 0f).multLocal(1, 1, 0);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        // unregister all my listeners, detach all my nodes, etc...
        window.setVisible(false);

        rootNode.detachAllChildren();
        rootNode.removeFromParent();
        baseApplication.getViewPort().clearProcessors();

    }

    @Override
    public void setEnabled(boolean enabled) {
        // Pause and unpause
        super.setEnabled(enabled);
        if (baseApplication == null) {
            return;
        }

        if (enabled) {
            active = false;
            window.setVisible(true);
            loadingPanel.setVisible(true);
            hudPanel.setVisible(false);
            baseApplication.getRootNode().attachChild(rootNode);

            loading = true;
            log("Showing screen...," + screenName);

        } else {
            active = false;
//            loadingPanel.setVisible(true);
//            hudPanel.setVisible(false);
            window.getFader().fadeOut();

        }
    }

    /**
     * for internal use only.
     *
     * @param fadeOut
     */
    public void fadeDone(boolean fadeOut) {
        if (fadeOut) {
            window.setVisible(false);
            rootNode.removeFromParent();
            baseApplication.getViewPort().clearProcessors();

            if (nextScreen != null) {
                exit();
                AbstractScreen screen = baseApplication.showScreen(nextScreen);
                nextScreen = null;

                if (setPrevious) {
                    screenName = null;
                    //Determine this screens name
                    for (Iterator it = baseApplication.getScreenManager().getScreens().keySet().iterator(); it.hasNext();) {
                        String key = (String) it.next();
                        AbstractScreen abstractScreenState = baseApplication.getScreenManager().getScreens().get(key);
                        if (abstractScreenState.equals(this)) {
                            screenName = key;
                            break;
                        }
                    }

                    screen.setPreviousScreen(screenName);
                }

            } else {
                //Exit the application
                baseApplication.stop();
                System.exit(0);
            }

        } else {
            baseApplication.setCurrentScreen(this);
            loadingPanel.setVisible(false);
            hudPanel.setVisible(true);
            active = true;
            show();

        }
    }

    public Window getWindow() {
        return window;
    }

    @Override
    public void update(float tpf) {
    }

    /**
     * Help us to move to a specific screen.
     *
     * @param nextScreen
     */
    public void showScreen(String nextScreen) {
        this.nextScreen = nextScreen;
        this.setPrevious = true;
        this.setEnabled(false);
        window.closeAllDialogs();
    }

    /**
     * Exit the system. This can be called when the user wants to exit the
     * screen.
     *
     */
    protected void exitScreen() {
        this.setPreviousScreen(null);
        this.setEnabled(false);
        this.nextScreen = null;
    }

    /**
     * For internal use.
     *
     * @param touchEvent
     */
    public void doEscape(boolean touchEvent) {
        if (isActive() && isEnabled() && isInitialized()) {

            //Exit the application if no previuos screen was set.
            if (previousScreen == null) {
                baseApplication.fireAndroidEscapeListener();
                baseApplication.stop();
                System.exit(0);

            } else {
                //Else you goto the previous screen
                showPreviousScreen();

            }

        }

    }

    /**
     * This method can be called if you want to move to the previous screen.
     */
    protected void showPreviousScreen() {
        this.nextScreen = previousScreen;
        this.setPrevious = false;
        this.previousScreen = null;
        this.setEnabled(false);
    }

    /**
     * Should not be called directy
     *
     * @param previousScreen
     */
    public void setPreviousScreen(String previousScreen) {
        this.previousScreen = previousScreen;
    }

    /**
     * Sets the image of the cursor.
     *
     * @param imageStr
     */
    protected void setImageCursor(String imageStr) {
        Texture cursorTexture = assetManager.loadTexture(imageStr);

        Image image = cursorTexture.getImage();
        ByteBuffer imgByteBuff = (ByteBuffer) image.getData(0).rewind();
        IntBuffer curIntBuff = BufferUtils.createIntBuffer(image.getHeight() * image.getWidth());

        while (imgByteBuff.hasRemaining()) {
            int rgba = imgByteBuff.getInt();
            int argb = ((rgba & 255) << 24) | (rgba >> 8);
            curIntBuff.put(argb);
        }

        JmeCursor c = new JmeCursor();
        c.setHeight(image.getHeight());
        c.setWidth(image.getWidth());
        c.setNumImages(1);
        c.setyHotSpot(image.getHeight() - 3);
        c.setxHotSpot(3);
        c.setImagesData((IntBuffer) curIntBuff.rewind());

        inputManager.setMouseCursor(c);
    }

    protected void resetCursor() {
    }

    public Node getRootNode() {
        return rootNode;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}
