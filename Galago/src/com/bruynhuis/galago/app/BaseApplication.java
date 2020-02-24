package com.bruynhuis.galago.app;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.bruynhuis.galago.control.tween.ColorAccessor;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.TouchInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import java.util.Iterator;
import java.util.Properties;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.control.tween.Vector3fAccessor;
import com.bruynhuis.galago.listener.JoystickInputListener;
import com.bruynhuis.galago.ui.listener.EscapeListener;
import com.bruynhuis.galago.ui.listener.FadeListener;
import com.bruynhuis.galago.ui.window.Fader;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.window.Window;
import com.bruynhuis.galago.listener.KeyboardInputListener;
import com.bruynhuis.galago.listener.LiveCameraListener;
import com.bruynhuis.galago.listener.PauseListener;
import com.bruynhuis.galago.listener.RemoteActionListener;
import com.bruynhuis.galago.listener.RewardAdListener;
import com.bruynhuis.galago.listener.SelectionActionListener;
import com.bruynhuis.galago.listener.SensorListener;
import com.bruynhuis.galago.messages.MessageManager;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.save.GameSaves;
import com.bruynhuis.galago.sound.DesktopMidiPlayer;
import com.bruynhuis.galago.sound.MidiPlayer;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.listener.GoogleAPIErrorListener;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.listener.SavedGameListener;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.util.ByteArrayInfo;
import com.bruynhuis.galago.util.SharedSystem;
import com.bruynhuis.galago.ttf.TrueTypeLoader;
import com.bruynhuis.galago.ui.field.InputType;
import com.bruynhuis.galago.ui.field.ProgressBar;
import com.bruynhuis.galago.util.Timer;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AndroidNativeImageLoader;
import java.io.IOException;
import java.util.HashMap;

/**
 * When using this Galago game library you need to extend the BaseApplication.
 * This class must be the main class of your game and all pre-loading of assets
 * and screens should happen here. There as 2 types of BaseApplications. -
 * Base2DApplication : Extend this class if you are going to create a 2D type
 * game. - Base3DApplication : Extend this class if you are going to create a 3D
 * game.
 *
 * @author nickidebruyn
 */
public abstract class BaseApplication extends SimpleApplication implements TouchListener, ActionListener, AnalogListener, FadeListener, PauseListener {

    public static final String KEYBOARD_ESCAPE_EVENT = "KEYBOARD_ESCAPE_EVENT";
    public static final String MOUSE_CLICK_EVENT = "MOUSE_CLICK_EVENT";
    public static final String MOUSE_MOVE_EVENT = "MOUSE_MOVE_EVENT";
    public static final String TOUCH_ESCAPE_EVENT = "TOUCH_ESCAPE_EVENT";
    public static final String TOUCH_EVENT = "TOUCH_EVENT";
    public static ColorRGBA BACKGROUND_COLOR = ColorRGBA.Black;
    public static ColorRGBA TEXT_COLOR = ColorRGBA.White;
    protected float SCREEN_WIDTH = 1280;
    protected float SCREEN_HEIGHT = 720;
    protected String gameSaveFileName = "defaultgame.save";
//    protected String GAME_FONT;
    protected String SPLASH_IMAGE;
    protected Node soundNode;
    protected SoundManager soundManager;
    protected EffectManager effectManager;
    protected ScreenManager screenManager;
    protected TextureManager textureManager;
    protected ModelManager modelManager;
    protected MessageManager messageManager;
    protected FontManager fontManager;
    private int loadingCounter = 0;
    private boolean loading = false;
    private int loadingTotalCount = 20;
    private Timer loadingTimer = new Timer(1f);
    private Timer soundLoadingTimer = new Timer(0.1f);
    protected GameSaves gameSaves;
    protected KeyboardInputListener keyboardInputListener;
    protected RemoteActionListener remoteActionListener;
    protected SelectionActionListener selectionActionListener;
    protected EscapeListener androidEscapeListener;
    protected GoogleAPIErrorListener googleAPIErrorListener;
    protected SavedGameListener savedGameListener;
    protected PauseListener pauseListener;
    protected SensorListener sensorListener;
    protected LiveCameraListener liveCameraListener;
    protected RewardAdListener rewardAdListener;
    public static final String TYPE = "TYPE";
    public static final String SHOW = "SHOW";
    public static final String ADMOB = "ADMOB";
    public static final String ADMOB_INTERSTITIALS = "ADMOB_INTER";
    public static final String ADMOB_REWARDS = "ADMOB_REWARDS";
    public static final String ACTION = "ACTION";
    public static final String ACTION_AD = "AD";
    public static final String ACTION_RATE = "RATE";
    public static final String ACTION_SHARE = "SHARE";
    public static final String ACTION_EMAIL = "EMAIL";
    public static final String ACTION_ALERT = "ALERT";
    public static final String ACTION_MORE = "MORE";
    public static final String ACTION_LINK = "LINK";
    public static final String ACTION_VIBRATE = "VIBRATE";
    public static final String ACTION_ADD_SCORE = "ADD_SCORE";
    public static final String ACTION_GET_SCORES = "GET_SCORES";
    public static final String ACTION_GET_ACHIEVEMENTS = "GET_ACHIEVEMENTS";
    public static final String ACTION_UNLOCK_ACHIEVEMENT = "UNLOCK_ACHIEVEMENT";
    public static final String ACTION_INCREMENT_ACHIEVEMENT = "INCREMENT_ACHIEVEMENT";
    public static final String ACTION_GOOGLE_SIGNIN = "GOOGLE_SIGNIN";
    public static final String ACTION_GOOGLE_SIGNOUT = "GOOGLE_SIGNOUT";
    public static final String ACTION_ANALYTICS = "ACTION_ANALYTICS";
    public static final String ACTION_OPEN_SAVED_GAME = "ACTION_OPEN_SAVED_GAME";
    public static final String ACTION_COMMIT_SAVED_GAME = "ACTION_COMMIT_SAVED_GAME";
    public static final String ACTION_SHOW_SAVED_GAME = "ACTION_SHOW_SAVED_GAME";
    public static final String ID = "ID";
    public static final String ACTION_INIT_ADS = "INIT_ADS";
    public static final String NAME = "NAME";
    public static final String SCORE = "SCORE";
    public static final String MESSAGE_TEXT = "MESSAGE_TEXT";
    public static final String URL = "URL";
    public static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";
    public static final String EMAIL_SUBJECT = "EMAIL_SUBJECT";
    public static final String EMAIL_CONTENT = "EMAIL_CONTENT";
    public static final String LEADERBOARD = "LEADERBOARD";
    public static final String ANALYTICS_CATAGORY = "ANALYTICS_CATAGORY";
    public static final String ANALYTICS_ACTION = "ANALYTICS_ACTION";
    public static final String ANALYTICS_LABEL = "ANALYTICS_LABEL";
    public static final String SAVED_GAME_NAME = "SAVED_GAME_NAME";
    public static final String SAVED_GAME_DESCRIPTION = "SAVED_GAME_DESCRIPTION";
    public static final String SAVED_GAME_DATA = "SAVED_GAME_DATA";
    public static final String SAVED_GAME_IMAGE = "SAVED_GAME_IMAGE";
    protected Window window;
    protected Panel splash;
    protected Label info;
    protected String splashInfoMessage = "GalagoFramework @ 2017";
    protected ProgressBar loadingBar;
    protected VideoRecorderAppState recorderAppState;
    protected boolean record = false;
    protected StatsAppState statsAppState;
    protected TweenManager tweenManager;
    protected TweenManager tweenManagerPhysics;
    protected MidiPlayer midiPlayer;
    protected AbstractScreen currentScreen;
    private boolean firePauseAction = false;
    private boolean fireResumeAction = false;
    private Texture2D cameraTexture;
    private AndroidNativeImageLoader androidImageLoader;
    private JoystickInputListener joystickInputListener;
    protected float secondCounter = 0.0f;
    protected int frameCounter = 0;
    protected int fps = 0;
    protected boolean rewardAdLoaded = false;
    protected boolean loadingBarVisible = true;
//    private boolean soundsLoaded = false;

    /**
     * Your the main java class in your game should call this constructor to
     * create a basic game setup
     *
     * @param title This is the title of your game in the Window
     * @param width This will be the native/default width at which your game is
     * designed for. Anything different from this at run time will be scaled.
     * @param height This is the height of the game when it is designed.
     * @param gameSaveFileName A file for saving player data.
     * @param gameFont When a different game font wants to be used specific for
     * the theme of this game put it her.
     * @param splashImage This is the path to the splash image you want to use
     * when the game starts up.
     * @param resizable It you want the game to be resizable you can always set
     * this to true. Normally for an android game this will be false.
     */
    public BaseApplication(String title, float width, float height, String gameSaveFileName, String gameFont, String splashImage, boolean resizable, float widthSample, float heightSample) {
        this.SCREEN_WIDTH = width;
        this.SCREEN_HEIGHT = height;
        this.gameSaveFileName = gameSaveFileName;
//        this.GAME_FONT = gameFont;

        if (gameFont == null) {
            gameFont = "Fonts/OpenSans.fnt";
        }

        FontManager.DEFAULT_FONT = gameFont;

        this.SPLASH_IMAGE = splashImage;

        AppSettings settings = new AppSettings(true);
        settings.setTitle(title);
        if (widthSample == 0 || heightSample == 0) {
            settings.setWidth((int) SCREEN_WIDTH);
            settings.setHeight((int) SCREEN_HEIGHT);
        } else {
            settings.setWidth((int) widthSample);
            settings.setHeight((int) heightSample);
        }

        settings.setVSync(true);
        settings.setUseJoysticks(true);
        settings.setSettingsDialogImage(null);
        settings.setGammaCorrection(false);
        settings.setSettingsDialogImage("Resources/jme-logo.png");

//        settings.setSettingsDialogImage(splashImage);
        setSettings(settings);
        setPauseOnLostFocus(false);

        if (resizable) {
            start();
        } else {
            start(JmeContext.Type.Display);
        }
    }

    public BaseApplication(String title, float width, float height, String gameSaveFileName, String gameFont, String splashImage, boolean resizable) {
        this(title, width, height, gameSaveFileName, gameFont, splashImage, resizable, 0, 0);
    }

    /**
     * If you wish to record video when you run your game you can set this to
     * true.
     *
     * @param record
     */
    public void setRecordVideo(boolean record) {
        this.record = record;
    }

    /**
     * Called when the camera gets initialized.
     */
    protected void initCamera() {
        flyCam.setEnabled(false);
        viewPort.setBackgroundColor(BACKGROUND_COLOR);
    }

    @Override
    public void simpleInitApp() {

        Tween.registerAccessor(Widget.class, new WidgetAccessor());
        Tween.registerAccessor(Spatial.class, new SpatialAccessor());
        Tween.registerAccessor(ColorRGBA.class, new ColorAccessor());
        Tween.registerAccessor(Vector3f.class, new Vector3fAccessor());

        tweenManager = new TweenManager();
        tweenManagerPhysics = new TweenManager();
        messageManager = new MessageManager(this);

        addPauseListener(this);

        assetManager.registerLoader(TrueTypeLoader.class, "ttf");
        if (isMobileApp()) {
//            assetManager.registerLoader(AndroidImageLoader.class, "jpg", "bmp", "gif", "png", "jpeg");            
        }

        preInitApp();

        initCamera();

        //Needs to happen here because the splash image uses it.
        textureManager = new TextureManager(this);
        initTextures(textureManager);

        //Splash uses a label so this must happen before splash
        fontManager = new FontManager(this);
        fontManager.loadFont(new FontStyle(14));
        fontManager.loadFont(new FontStyle(16));
        fontManager.loadFont(new FontStyle(18));
        fontManager.loadFont(new FontStyle(20));
        fontManager.loadFont(new FontStyle(22));

        initSplash();

        loading = true;
        loadingTimer.start();

        if (record) {
            recorderAppState = new VideoRecorderAppState();
            stateManager.attach(recorderAppState);
        }

        if (stateManager.getState(StatsAppState.class) != null) {
            StatsAppState statsAppState = stateManager.getState(StatsAppState.class);
            stateManager.detach(statsAppState);

        }
    }

    /**
     * PreInitApp() gets called just before anything in the game startup is
     * loaded. Here you can do some pre loading coding.
     */
    protected abstract void preInitApp();

    /**
     * postInitApp() will always be called after all loading of the game was
     * done and you are ready to show the first screen. This is also where you
     * normally call to showScreen("menu").
     */
    protected abstract void postInitApp();

    @Override
    public void doPause(boolean pause) {
        if (pause) {
            firePauseAction = true;
        } else {
            fireResumeAction = true;
        }
    }

    /**
     * Load the game saves from the file system.
     */
    protected void initGameSaves() {
        gameSaves = new GameSaves(this.gameSaveFileName);
        gameSaves.read();
    }

    private void updateProgress(String text, int progress) {
        info.setText(text);
        loadingBar.setProgress((float) progress / (float) loadingTotalCount);
    }

    @Override
    public void simpleUpdate(float tpf) {
        tweenManager.update(tpf);

        //Update preloading sound
//        if (getSoundManager() != null && !soundsLoaded) {
//            soundLoadingTimer.update(tpf);
//            if (soundLoadingTimer.finished()) {
//                int count = getSoundManager().getCompletedPreloadedSoundFXCount();
//                if (count < getSoundManager().getSoundFx().size()) {
//                    getSoundManager().preloadNextSoundFX();
//                    log("preloading sounds: " + count);
//                    soundLoadingTimer.reset();
//
//                } else {
//                    soundsLoaded = true;
//                    soundLoadingTimer.stop();
//                }
//            }
//
//        }
        if (loading) {

            loadingTimer.update(tpf);
            if (loadingTimer.finished()) {
                loadingCounter++;
                loadingTimer.reset();
            }

            if (loadingCounter == 1) {
                updateProgress("Loading save data...", loadingCounter);
                initGameSaves();
                SharedSystem.getInstance().setBaseApplication(this);
            }

            if (loadingCounter == 2) {
                updateProgress("Loading input...", loadingCounter);
                initInput();
            }

            if (loadingCounter == 3) {
                updateProgress("Loading models...", loadingCounter);
                modelManager = new ModelManager(this);
                initModelManager(modelManager);
            }

            if (loadingCounter == 4) {
                updateProgress("Loading fonts...", loadingCounter);
                //Load any fonts to be used                
                initFonts(fontManager);

            }

            if (loadingCounter == 5) {
                updateProgress("Loading fx...", loadingCounter);
                effectManager = new EffectManager(this);
                initEffect(effectManager);

            }

            if (loadingCounter == 6) {
                if (isPhysicsEnabled()) {
                    updateProgress("Loading physics...", loadingCounter);
                    initPhysics();
                }
            }

            if (loadingCounter == 7) {
                updateProgress("Loading screens...", loadingCounter);
                screenManager = new ScreenManager(this);
                initScreens(screenManager);
            }

            if (loadingCounter == 8) {
                updateProgress("Loading sounds...", loadingCounter);
                soundNode = new Node("Sound Node");
                rootNode.attachChild(soundNode);
                soundManager = new SoundManager(this, soundNode);
                initSound(soundManager);

                if (getGameSaves() != null && getGameSaves().getGameData() != null) {
                    soundManager.muteMusic(!getGameSaves().getGameData().isMusicOn());
                    soundManager.muteSound(!getGameSaves().getGameData().isSoundOn());
                } else {
                    info.setText("");
                }

                loadingTotalCount = 8 + getSoundManager().getSoundFx().size();
                soundLoadingTimer.start();

            }

            if (loadingCounter > 8) {
                updateProgress("Loading music...", loadingCounter);
                loadingTimer.stop();

                soundLoadingTimer.update(tpf);

                if (soundLoadingTimer.finished()) {

                    int count = getSoundManager().getCompletedPreloadedSoundFXCount();
                    if (count < getSoundManager().getSoundFx().size()) {
                        getSoundManager().preloadNextSoundFX();
                        loadingCounter++;
                        soundLoadingTimer.reset();

                    } else {
                        updateProgress("Loading done...", loadingTotalCount);
                        loading = false;
                        soundLoadingTimer.stop();
                        window.getFader().setVisible(true);
                        window.getFader().fadeOut();

                    }
                }

            }

//            if (loadingCounter == 9) {
//                updateProgress("Loading music...", loadingCounter);
//                loadingTimer.stop();
//                soundLoadingTimer.start();
//                loading = false;
//                window.getFader().setVisible(true);
//                window.getFader().fadeOut();
//
//            }
        } else {
            //Set the font size for the stats
            if (statsAppState != null) {
                statsAppState.getFpsText().setSize(18f * getApplicationHeightScaleFactor());
//                statsAppState.getFpsText().setBox(new Rectangle(600f*0.5f, 50f*0.5f, 600f, 50f*0.5f));
//                statsAppState.getFpsText().setVerticalAlignment(BitmapFont.VAlign.Bottom);
            }

            if (firePauseAction) {
                if (screenManager != null && currentScreen != null) {
                    currentScreen.firePauseAction();
                }
                firePauseAction = false;
            }

            if (fireResumeAction) {
                if (screenManager != null && currentScreen != null) {
                    currentScreen.fireResumeAction();
                }
                fireResumeAction = false;
            }

        }

    }

//    @Override
//    protected BitmapFont loadGuiFont() {
//        if (GAME_FONT != null) {
//            return assetManager.loadFont(GAME_FONT);
//        } else {
//            return super.loadGuiFont();
//        }
//
//    }
    /**
     * Helper method that will get the game save data.
     *
     * @return
     */
    public GameSaves getGameSaves() {
        return gameSaves;
    }

    /**
     * Generic way of handling the input events.
     */
    protected void initInput() {
        //Clear all input options that was created by default        
        inputManager.clearMappings();

        //Initialize the inputs
        if (isMobileApp()) {
            //Touch events
            inputManager.addMapping(TOUCH_ESCAPE_EVENT, new TouchTrigger(TouchInput.KEYCODE_BACK));
            inputManager.addListener(this, new String[]{TOUCH_ESCAPE_EVENT});

            inputManager.addMapping(TOUCH_EVENT, new TouchTrigger(TouchInput.ALL));
            inputManager.addListener(this, new String[]{TOUCH_EVENT});

        } else {
            //PC events
            inputManager.addMapping(KEYBOARD_ESCAPE_EVENT, new KeyTrigger(KeyInput.KEY_ESCAPE));
            inputManager.addListener(this, new String[]{KEYBOARD_ESCAPE_EVENT});

            inputManager.addMapping(MOUSE_CLICK_EVENT, new MouseButtonTrigger(0));
            inputManager.addListener(this, MOUSE_CLICK_EVENT);

            inputManager.addMapping(MOUSE_MOVE_EVENT,
                    new MouseAxisTrigger(MouseInput.AXIS_X, false),
                    new MouseAxisTrigger(MouseInput.AXIS_Y, false),
                    new MouseAxisTrigger(MouseInput.AXIS_X, true),
                    new MouseAxisTrigger(MouseInput.AXIS_Y, true));
            inputManager.addListener(this, MOUSE_MOVE_EVENT);
        }

        joystickInputListener = new JoystickInputListener();
        joystickInputListener.registerWithInput(inputManager);

    }

    protected void setLoadingScreenVisible(boolean visible) {
        loadingBarVisible = visible;
    }

    protected void initSplash() {
        window = new Window(this, getGuiNode(), SCREEN_WIDTH, SCREEN_HEIGHT, getBitmapFont());

        //Load the splash
        splash = new Panel(window, SPLASH_IMAGE, true);
        window.add(splash);
        splash.center();

        info = new Label(splash, splashInfoMessage, 14, 500, 40);
        info.centerAt(0, -100);
        info.setTextColor(ColorRGBA.DarkGray);

        loadingBar = new ProgressBar(splash, "Resources/progressbar-border.png", "Resources/progressbar.png", 256, 10);
        loadingBar.centerAt(0, -150);

        splash.add(loadingBar);

        //Add the fade over the other gui's
        Fader fader = new Fader(window, ColorRGBA.Black, 100f, 100f, 1f, 1f);
        fader.addFadeListener(this);
        window.setFader(fader);
        fader.setVisible(false);

        info.setVisible(loadingBarVisible);
        loadingBar.setVisible(loadingBarVisible);
    }

//    private void updateProgress() {
//        int completed = 0;
//        
//        for (Iterator<AudioNode> it = getSoundManager().getSoundFx().values().iterator(); it.hasNext();) {
//            AudioNode an = it.next();
//
//            if (an.getUserData("preloaded") != null) {
//                completed ++;
//            }
//
//        }
//        float progress = (float)completed/(float)getSoundManager().getSoundFx().size();
//        loadingBar.setProgress(progress);
//
//    }
    protected abstract void initPhysics();

    public abstract void showDebuging();

    protected abstract boolean isPhysicsEnabled();

    protected abstract void initScreens(ScreenManager screenManager);

    /**
     * Initialize the map of models used.
     *
     * @param modelManager
     */
    public abstract void initModelManager(ModelManager modelManager);

    /**
     * Init the sound system.
     */
    protected abstract void initSound(SoundManager soundManager);

    /**
     * Init the effects to be used.
     */
    protected abstract void initEffect(EffectManager effectManager);

    /**
     * Init the textures to be used
     *
     * @param textureManager
     */
    protected abstract void initTextures(TextureManager textureManager);

    /**
     * Init the fonts to be used
     *
     * @param fontManager
     */
    protected abstract void initFonts(FontManager fontManager);

    /**
     * This method returns the modelManager class.
     *
     * @return ModelManager
     */
    public ModelManager getModelManager() {
        return modelManager;
    }

    /**
     * Returns the font manager
     *
     * @return
     */
    public FontManager getFontManager() {
        return fontManager;
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param fadeOut
     */
    public void fadeDone(boolean fadeOut) {
        if (fadeOut) {
            postInitApp();
            window.setVisible(false);
            doInitAds();

        }
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param name
     * @param isPressed
     * @param tpf
     */
    public void onAction(String name, boolean isPressed, float tpf) {

        if (KEYBOARD_ESCAPE_EVENT.equals(name)) {
//            System.out.println("^^^^^^^^^^^^^^^^^^^^ KEYBOARD ESCAPE ^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            if (isPressed) {
                fireAllEscapeListeners(false);
            }

        }
        if (MOUSE_CLICK_EVENT.equals(name)) {

            if (getCurrentScreen() != null) {

                if (isPressed) {
                    getCurrentScreen().getWindow().fireButtonCollision(true, false,
                            inputManager.getCursorPosition().x,
                            inputManager.getCursorPosition().y, tpf);

                } else {
                    getCurrentScreen().getWindow().fireButtonCollision(false, false,
                            inputManager.getCursorPosition().x,
                            inputManager.getCursorPosition().y, tpf);

                }
            }
        }

    }

    @Override
    public void onAnalog(String name, float value, float tpf) {

        if (MOUSE_MOVE_EVENT.equals(name)) {
            if (getCurrentScreen() != null) {
                getCurrentScreen().getWindow().fireButtonCollision(false, true,
                        inputManager.getCursorPosition().x,
                        inputManager.getCursorPosition().y, tpf);
            }
        }

    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param name
     * @param event
     * @param tpf
     */
    public void onTouch(String name, TouchEvent event, float tpf) {
        if (event == null) {
            return;
        }

//        doAlert("Clicked on touch: " + name + "; type=" + event.getType().name());
        if (name.equals(TOUCH_ESCAPE_EVENT)) {
            fireAllEscapeListeners(true);
        }

        if (name.equals(TOUCH_EVENT)) {
            if (getCurrentScreen() != null) {

                if (TouchEvent.Type.DOWN.equals(event.getType())) {
                    getCurrentScreen().getWindow().fireButtonCollision(true, false,
                            event.getX(), event.getY(), tpf);

                }
                if (TouchEvent.Type.UP.equals(event.getType())) {
                    getCurrentScreen().getWindow().fireButtonCollision(false, false,
                            event.getX(), event.getY(), tpf);

                }
                if (TouchEvent.Type.MOVE.equals(event.getType())) {
                    getCurrentScreen().getWindow().fireButtonCollision(false, true,
                            event.getX(), event.getY(), tpf);

                }
            }
        }
    }

    /**
     * This method is for internal use and should not be called. All screen
     * states must be called here.
     *
     * @param touch
     */
    protected void doEscapeAction(boolean touch) {
        //Loop over all screens and fire the escape action
        if (screenManager != null && !screenManager.getScreens().isEmpty()) {
            for (Iterator<AbstractScreen> it = screenManager.getScreens().values().iterator(); it.hasNext();) {
                AbstractScreen abstractScreenState = it.next();
                abstractScreenState.doEscape(touch);
            }
        }

    }

    /**
     * Call al the escape listeners
     */
    public void fireAllEscapeListeners(boolean touch) {
        doEscapeAction(touch);
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @return
     */
    public Node getSoundNode() {
        return soundNode;
    }

    /**
     * Get this screens with this method.
     *
     * @return ScreenManager
     */
    public ScreenManager getScreenManager() {
        return screenManager;
    }

    /**
     * This method will return the sound manager and the soundmanager must be
     * usedd to play music or sounds.
     *
     * @return SoundManager
     */
    public SoundManager getSoundManager() {
        return soundManager;
    }

    /**
     * A helper manager for showing different effects in the game. This is
     * normally particle effects.
     *
     * @return EffectManager
     */
    public EffectManager getEffectManager() {
        return effectManager;
    }

    /**
     * This method will return an instance of the message manager.
     *
     * @return
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @return BitmapFont
     */
    public BitmapFont getBitmapFont() {
        return guiFont;
    }

    @Override
    public void destroy() {

        if (joystickInputListener != null) {
            joystickInputListener.unregisterInput();
//            joystickInputListener = null;
        }

        if (midiPlayer != null) {
            midiPlayer.stop();
            midiPlayer.release();
        }
        if (screenManager != null) {
            screenManager.destroy();
        }
        if (soundManager != null) {
            soundManager.destroy();
        }
        if (effectManager != null) {
            effectManager.destroy();
        }

        if (modelManager != null) {
            modelManager.destroy();
        }

        if (messageManager != null) {
            messageManager.destroy();
        }

        if (fontManager != null) {
            fontManager.destroy();
        }

        super.destroy();
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param remoteActionListener1
     */
    public void addRemoteActionListener(RemoteActionListener remoteActionListener1) {
        this.remoteActionListener = remoteActionListener1;
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param properties
     */
    protected void fireRemoteActionListener(Properties properties) {
        if (remoteActionListener != null) {
            remoteActionListener.doAction(properties);
        }
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param remoteActionListener1
     */
    public void addSelectionActionListener(SelectionActionListener selectionActionListener1) {
        this.selectionActionListener = selectionActionListener1;
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param properties
     */
    protected void fireSelectionActionListener(HashMap<Integer, String> options) {
        if (selectionActionListener != null) {
            selectionActionListener.doSelectionOption(options);
        }
    }

    public void setDropdownSelectedIndex(int selectedIndex) {
        if (getCurrentScreen() != null) {
            getCurrentScreen().getWindow().setValueForDropdown(selectedIndex);
        }
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param pauseListener
     */
    public void addPauseListener(PauseListener pauseListener) {
        this.pauseListener = pauseListener;
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param paused
     */
    protected void firePauseListener(boolean paused) {
        if (pauseListener != null) {
            pauseListener.doPause(paused);
        }
    }

    /**
     * This method is for internal use and should not be called.
     */
    public void doPauseGame() {
        firePauseListener(true);
    }

    /**
     * For internal use by the android activity
     */
    public void doResumeGame() {
        firePauseListener(false);
    }

    public void addSensorListener(SensorListener sensorListener) {
        this.sensorListener = sensorListener;
    }

    public void addLiveCameraListener(LiveCameraListener liveCameraListener) {
        this.liveCameraListener = liveCameraListener;
    }

    public void addRewardAdListener(RewardAdListener rewardAdListener) {
        this.rewardAdListener = rewardAdListener;
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param paused
     */
    public void fireSensorListener(float fisting, float tilting, float twisting) {
        if (sensorListener != null) {
            sensorListener.doSensorAction(fisting, tilting, twisting);
        }
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param
     */
    public void fireRewardAdRewardListener(int amount, String type) {
        if (rewardAdListener != null) {
            rewardAdListener.doAdRewarded(amount, type);
        }
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param
     */
    public void fireRewardAdClosedListener() {
        if (rewardAdListener != null) {
            rewardAdListener.doAdClosed();
        }
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param
     */
    public void fireRewardAdLoadedListener() {
        if (rewardAdListener != null) {
            rewardAdListener.doAdLoaded();
        }
    }

    /**
     * This method is not for external use. It will be called from the mobile
     * device.
     *
     * @param format
     * @param width
     * @param height
     * @param data
     */
    public void fireLiveCameraListener(int format, int width, int height, byte[] data) {
        if (liveCameraListener != null && isMobileApp()) {

            //Do this if not done before
            if (androidImageLoader == null) {
                androidImageLoader = new AndroidNativeImageLoader();
                cameraTexture = new Texture2D();
            }

            try {
                Image image = (Image) androidImageLoader.load(new ByteArrayInfo(assetManager, data));
                cameraTexture.setImage(image);
                liveCameraListener.setTexture(cameraTexture);

            } catch (IOException e) {
                System.out.println("IMAGE LOAD FAILED");
            }

        }
    }

    /**
     * If you are using android and you are using Admob you will call this
     * method to show some ads. Usage is: code:
     * baseApplication.showAds(BaseApplication.ADMOB_INTERSTITIALS, true);
     *
     * @param addType
     * @param show
     */
    public void showAds(String adType, boolean show) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_AD);
        properties.put(BaseApplication.TYPE, adType);
        properties.put(BaseApplication.SHOW, show + "");
        fireRemoteActionListener(properties);
    }

    /**
     * If you are using android you can call this method from the game to take
     * the player to the rate section on the playstore.
     */
    public void doRateApplication() {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_RATE);
        fireRemoteActionListener(properties);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the user share his application via xxx
     */
    public void doShareApplication() {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_SHARE);
        fireRemoteActionListener(properties);
    }

    /**
     * Email a specific address
     *
     * @param email
     * @param subject
     * @param content
     */
    public void doEmail(String email, String subject, String content) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_EMAIL);
        properties.put(EMAIL_ADDRESS, email);
        properties.put(EMAIL_SUBJECT, subject);
        properties.put(EMAIL_CONTENT, content);
        fireRemoteActionListener(properties);
    }

    /**
     * Show an alert message on the mobile device
     *
     * @param text
     */
    public void doAlert(String text) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_ALERT);
        properties.put(MESSAGE_TEXT, text);
        fireRemoteActionListener(properties);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the user see more of your applications.
     */
    public void doMoreApplications() {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_MORE);
        fireRemoteActionListener(properties);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the user link to a url
     *
     * @param url
     */
    public void doLinkToUrl(String url) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_LINK);
        properties.put(URL, url);
        fireRemoteActionListener(properties);
    }

    /**
     * This method should be called to show a selection.
     *
     * @param items
     */
    public void doShowSelection(HashMap<Integer, String> items) {
        fireSelectionActionListener(items);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the device vibrate.
     */
    public void doVibrate() {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_VIBRATE);
        fireRemoteActionListener(properties);
    }

    /**
     * This method is for internal use and should not be called.
     *
     */
    private void doInitAds() {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_INIT_ADS);
        fireRemoteActionListener(properties);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the user add his score to the leaderboard.
     *
     * @param leaderboardID
     * @param score
     */
    public void doAddHighscore(String leaderboardID, int score) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_ADD_SCORE);
        properties.put(SCORE, score + "");
        properties.put(LEADERBOARD, leaderboardID);
        fireRemoteActionListener(properties);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the user add his score to the leaderboard.
     *
     * @param leaderboardID
     * @param score
     */
    public void doAddHighscore(String leaderboardID, float score) {
        String scoreStr = String.format("%.2f", score);
        scoreStr = scoreStr.replace(".", "");
        scoreStr = scoreStr.replace(",", "");

//        log("Score formated = " + scoreStr);
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_ADD_SCORE);
        properties.put(SCORE, scoreStr);
        properties.put(LEADERBOARD, leaderboardID);
        fireRemoteActionListener(properties);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the user see the leaderboard.
     *
     * @param leaderboardID
     * @param score
     */
    public void doShowHighscores(String leaderboardID, int score) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_GET_SCORES);
        properties.put(SCORE, score + "");
        if (leaderboardID != null) {
            properties.put(LEADERBOARD, leaderboardID);
        }
        fireRemoteActionListener(properties);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the user see achievments.
     */
    public void doShowAchievements() {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_GET_ACHIEVEMENTS);
        fireRemoteActionListener(properties);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the user unlock an achievement.
     *
     * @param achievementID
     */
    public void doUnlockAchievement(String achievementID) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_UNLOCK_ACHIEVEMENT);
        properties.put(ID, achievementID);
        fireRemoteActionListener(properties);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the user increment an achievement.
     *
     * @param achievementID
     */
    public void doIncrementAchievement(String achievementID) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_INCREMENT_ACHIEVEMENT);
        properties.put(ID, achievementID);
        fireRemoteActionListener(properties);
    }

    /**
     * If you are you are using android you can call this method from the game
     * to let the user sign into google play services.
     */
    public void doGoogleSignIn() {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_GOOGLE_SIGNIN);
        fireRemoteActionListener(properties);
    }

    /**
     * Sign out of google
     */
    public void doGoogleSignOut() {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_GOOGLE_SIGNOUT);
        fireRemoteActionListener(properties);
    }

    /**
     * Send a message to the analytics tracking system.
     *
     * @param catagory
     * @param action
     * @param label
     */
    public void doAnalyticsAction(String catagory, String action, String label) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_ANALYTICS);
        properties.put(ANALYTICS_CATAGORY, catagory);
        properties.put(ANALYTICS_ACTION, action);
        properties.put(ANALYTICS_LABEL, label);
        fireRemoteActionListener(properties);
    }

    /**
     * This method can be called to open a saved game.
     *
     * @param name
     */
    public void doOpenSavedGameAction(String name) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_OPEN_SAVED_GAME);
        properties.put(SAVED_GAME_NAME, name);
        fireRemoteActionListener(properties);
    }

    /**
     * This method can be called to commit a saved game.
     *
     * @param name
     */
    public void doCommitSavedGameAction(String name, String description, String data) {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_COMMIT_SAVED_GAME);
        properties.put(SAVED_GAME_NAME, name);
        properties.put(SAVED_GAME_DESCRIPTION, description);
        properties.put(SAVED_GAME_DATA, data);

        fireRemoteActionListener(properties);
    }

    /**
     * This will open up a saved game selection dialog.
     */
    public void doShowSavedGameAction() {
        Properties properties = new Properties();
        properties.put(ACTION, ACTION_SHOW_SAVED_GAME);
        fireRemoteActionListener(properties);
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param keyboardInputListener1
     */
    public void addKeyboardInputListener(KeyboardInputListener keyboardInputListener1) {
        this.keyboardInputListener = keyboardInputListener1;
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param properties
     */
    public void fireKeyboardInputListener(Properties properties, InputType inputType) {
        if (keyboardInputListener != null) {
            keyboardInputListener.doInput(properties, inputType);
        }
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param escapeListener
     */
    public void addAndroidEscapeListener(EscapeListener escapeListener) {
        this.androidEscapeListener = escapeListener;
    }

    /**
     * This method is for internal use and should not be called.
     */
    public void fireAndroidEscapeListener() {
        if (androidEscapeListener != null) {
            androidEscapeListener.doEscape(true);
        }
    }

    /**
     * Set this to listen to errors from google.
     *
     * @param googleAPIErrorListener
     */
    public void setGoogleAPIErrorListener(GoogleAPIErrorListener googleAPIErrorListener) {
        this.googleAPIErrorListener = googleAPIErrorListener;
    }

    public void setSavedGameListener(SavedGameListener savedGameListener) {
        this.savedGameListener = savedGameListener;
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param errorMessage
     */
    public void fireGoogleAPIErrorListener(String errorMessage) {
        if (googleAPIErrorListener != null) {
            googleAPIErrorListener.onGoogleAPIError(errorMessage);
        }
    }

    /**
     * If a saved game error has occured
     *
     * @param errorMessage
     */
    public void fireSavedGameErrorListener(String errorMessage) {
        if (savedGameListener != null) {
            savedGameListener.onSavedGameError(errorMessage);
        }
    }

    /**
     * If a saved game was opened
     *
     * @param errorMessage
     */
    public void fireSavedGameOpenListener(String name, String data) {
        if (savedGameListener != null) {
            savedGameListener.onSavedGameOpened(name, data);
        }
    }

    public void fireSavedGameSavedListener() {
        if (savedGameListener != null) {
            savedGameListener.onSavedGameSaved();
        }
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param message
     */
    public void fireGoogleAPIConnectedListener(String message) {
        if (googleAPIErrorListener != null) {
            googleAPIErrorListener.onGoogleAPIConnected(message);
        }
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param message
     */
    public void fireGoogleAPIDisconnectedListener(String message) {
        if (googleAPIErrorListener != null) {
            googleAPIErrorListener.onGoogleAPIDisconnected(message);
        }
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param errorMessage
     */
    public void doGoogleAPIError(String errorMessage) {
        fireGoogleAPIErrorListener(errorMessage);
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param message
     */
    public void doGoogleAPIConnected(String message) {
        fireGoogleAPIConnectedListener(message);
    }

    /**
     * This method is for internal use and should not be called.
     *
     * @param message
     */
    public void doGoogleAPIDisconnected(String message) {
        fireGoogleAPIDisconnectedListener(message);
    }

    /**
     * This method can be called from the game to determine if it is a mobile
     * app or not.
     *
     * @return boolean
     */
    public boolean isMobileApp() {
        try {
            Platform platform = JmeSystem.getPlatform();
            return platform.compareTo(Platform.Android_ARM5) == 0
                    || platform.compareTo(Platform.Android_ARM6) == 0
                    || platform.compareTo(Platform.Android_ARM7) == 0
                    || platform.compareTo(Platform.Android_ARM8) == 0
                    || platform.compareTo(Platform.Android_Other) == 0
                    || platform.compareTo(Platform.Android_X86) == 0;

        } catch (UnsupportedOperationException e) {
            return true;
        }

    }

    /**
     * This method can be called to show game statistics such as FPS, obj count,
     * etc.
     */
    public void showStats() {
        if (statsAppState == null) {
            statsAppState = new StatsAppState(guiNode, guiFont);
            stateManager.attach(statsAppState);
        } else {
            stateManager.attach(statsAppState);
        }
    }

    /**
     * Hide the game stats.
     */
    public void hideStats() {
        if (stateManager.getState(StatsAppState.class) != null) {
            StatsAppState statsAppState = stateManager.getState(StatsAppState.class);
            stateManager.detach(statsAppState);

        } else if (statsAppState != null) {
            stateManager.detach(statsAppState);
        }
    }

    public float getSCREEN_WIDTH() {
        return SCREEN_WIDTH;
    }

    public float getSCREEN_HEIGHT() {
        return SCREEN_HEIGHT;
    }

    /**
     * This method returns the scale ratio of the game when it is running. If
     * the native resolution at design time was set to 800 x 480 and you are
     * running the game on a device with screen res of 800 x 480 then the scale
     * factor for the Width will be 1.0f
     *
     * @return scalefactor
     */
    public float getApplicationWidthScaleFactor() {
        return settings.getWidth() / SCREEN_WIDTH;
    }

    /**
     * This method returns the scale ratio of the game when it is running. If
     * the native resolution at design time was set to 800 x 480 and you are
     * running the game on a device with screen res of 800 x 480 then the scale
     * factor for the height will be 1.0f
     *
     * @return scalefactor
     */
    public float getApplicationHeightScaleFactor() {
        return settings.getHeight() / SCREEN_HEIGHT;
    }

    /**
     * Helper method that will print and log memory usage to the console
     */
    public void logMemoryUsage() {
        long heapMax = Runtime.getRuntime().maxMemory(); // max capable
        long heapTotal = Runtime.getRuntime().totalMemory(); // current heap size max, can grow as necessary up to maxMemory
        long heapFree = Runtime.getRuntime().freeMemory(); // current free heap (totalMemory-usedMemory)
        System.out.println("*************************** MEMORY AT STARTUP ************************");
        System.out.println("Heap Max : " + heapMax);
        System.out.println("Heap Total : " + heapTotal);
        System.out.println("Heap Free : " + heapFree);
    }

    /**
     * Helper method that will log the message that is send to it.
     *
     * @param text
     */
    public void log(String text) {
        System.out.println(text);
    }

    /**
     * Returns the tween manager if some nice tween effects is needed.
     *
     * @return
     */
    public TweenManager getTweenManager() {
        return tweenManager;
    }

    /**
     * Returns the tween manager which will run in the physics loop
     *
     * @return
     */
    public TweenManager getTweenManagerPhysics() {
        return tweenManagerPhysics;
    }

    /**
     * This method will enable and show a specific screen.
     *
     * @param screenName
     */
    public AbstractScreen showScreen(String screenName) {
        AbstractScreen screen = screenManager.getScreen(screenName);

        if (screen == null) {
            throw new RuntimeException("Screen " + screenName + " does not exist!");
        }

        screen.setEnabled(true);

        return screen;
    }

    /**
     * Only for interal use and it should not be called.
     *
     * @return
     */
    public MidiPlayer getMidiPlayer() {
        //When we want to make use of midi player we must first check if it doesn't already exist
        //If it exist it would probably have been set or created by android system
        if (midiPlayer == null) {
            midiPlayer = new DesktopMidiPlayer();
        }
        return midiPlayer;
    }

    /**
     * Only for interal use and it should not be called.
     *
     * @param midiPlayer
     */
    public void setMidiPlayer(MidiPlayer midiPlayer) {
        this.midiPlayer = midiPlayer;
    }

    /**
     * Only for interal use and it should not be called.
     *
     * @return
     */
    public boolean hasMidiPlayer() {
        return midiPlayer != null;
    }

    /**
     * Can be called in the game to get the textureManager
     *
     * @return TextureManager
     */
    public TextureManager getTextureManager() {
        return textureManager;
    }

    public AbstractScreen getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentScreen(AbstractScreen currentScreen) {
        this.currentScreen = currentScreen;
    }

    public JoystickInputListener getJoystickInputListener() {
        return joystickInputListener;
    }

    public int getFPS() {
        secondCounter += getTimer().getTimePerFrame();
        frameCounter++;
        if (secondCounter >= 1.0f) {
            fps = (int) (frameCounter / secondCounter);
            secondCounter = 0.0f;
            frameCounter = 0;
        }

        return fps;
    }

    public boolean isRewardAdLoaded() {
        return rewardAdLoaded;
    }

    public void setRewardAdLoaded(boolean rewardAdLoaded) {
        this.rewardAdLoaded = rewardAdLoaded;
    }
}
