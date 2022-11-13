package com.galago.editor;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.font.BitmapText;
import com.jme3.input.JoystickCompatibilityMappings;
import com.jme3.input.controls.ActionListener;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import markil3.controller.CalibrateInputScreen;
import markil3.controller.GUIUtils;
import markil3.controller.JoystickPreviewScreen;

/**
 *
 * @author ndebruyn
 */
public class JoystickSetup extends SimpleApplication implements ActionListener {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.
            getLogger(JoystickSetup.class);

    public static File GAME_FOLDER;
    public static File CALIBRATION_FILE;

    public static File getGameFolder() {
        return GAME_FOLDER;
    }

    private static void initializeJoystickMappings() {
        if (CALIBRATION_FILE == null) {
            CALIBRATION_FILE
                    = new File(GAME_FOLDER, "controllerCalibration.properties");
            URL mappingUrl;
            switch (JmeSystem.getPlatform()) {
                case Windows32:
                case Windows64:
                    mappingUrl = JoystickSetup.class.
                            getResource("/joystick-mapping.windows.properties");
                    break;
                case MacOSX32:
                case MacOSX64:
                case MacOSX_PPC32:
                case MacOSX_PPC64:
                    mappingUrl = JoystickSetup.class.
                            getResource("/joystick-mapping.osx.properties");
                    break;
                case Linux32:
                case Linux64:
                case Linux_ARM32:
                case Linux_ARM64:
                    mappingUrl = JoystickSetup.class.
                            getResource("/joystick-mapping.linux.properties");
                    break;
                case Android_ARM5:
                case Android_ARM6:
                case Android_ARM7:
                case Android_ARM8:
                case Android_X86:
                case Android_Other:
                    mappingUrl = JoystickSetup.class.
                            getResource("/joystick-mapping.android.properties");
                    break;
                case iOS_ARM:
                case iOS_X86:
                    mappingUrl = JoystickSetup.class.
                            getResource("/joystick-mapping.ios.properties");
                    break;
                default:
                    mappingUrl = null;
            }
            if (mappingUrl != null) {
                try {
                    JoystickCompatibilityMappings
                            .loadMappingProperties(mappingUrl);
                } catch (IOException e) {
                    logger.error("Unable to load joystick mappings for "
                            + mappingUrl, e);
                }
            }
            mappingUrl = JoystickSetup.class.
                    getResource("/joystick-mapping."
                            + JmeSystem.getPlatform().toString().toLowerCase()
                            + ".properties");
            if (mappingUrl != null) {
                try {
                    JoystickCompatibilityMappings
                            .loadMappingProperties(mappingUrl);
                } catch (IOException e) {
                    logger.error("Unable to load joystick mappings for "
                            + mappingUrl, e);
                }
            }
            if (CALIBRATION_FILE.isFile()) {
                try {
                    JoystickCompatibilityMappings.loadMappingProperties(
                            CALIBRATION_FILE.toURI().toURL());
                } catch (IOException e) {
                    logger.error("Unable to load joystick mappings.", e);
                }
            }
        }
    }

    public static void main(String[] args) {
        JoystickSetup app;
        AppSettings settings;
        app = new JoystickSetup();
        settings = new AppSettings(true);
        settings.setTitle("Joystick Preview");
        settings.setUseJoysticks(true);
        settings.setEmulateMouse(true);
        settings.setVSync(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        app.setSettings(settings);
        app.start();
    }

    private Node calibrateButton;

    public JoystickSetup() {
        super(new StatsAppState(), new DebugKeysAppState(),
                new JoystickPreviewScreen());
    }

    @Override
    public void initialize() {
        final File[] possibleGameDirs
                = new File[]{new File(System.getProperty("user.dir")),
                    new File(System.getProperty("user.home")),
                    JmeSystem.getStorageFolder(
                            JmeSystem.StorageFolderType.External)};
        int i, l;
        if (GAME_FOLDER == null) {
            for (i = 0, l = possibleGameDirs.length; i < l; i++) {
                GAME_FOLDER = possibleGameDirs[i];
                if (!GAME_FOLDER.isDirectory()) {
                    if (!GAME_FOLDER.mkdir()) {
                        logger.warn("Cannot make " + GAME_FOLDER);
                        continue;
                    }
                }
                if (!GAME_FOLDER.canWrite()) {
                    logger.warn("Cannot write to " + GAME_FOLDER);
                    continue;
                }
                break;
            }
            if (i == l) {
                throw new RuntimeException(
                        "Could not create game directory folder.");
            }
        }
        /*
         * Add custom joystick mappings before the input manager is loaded.
         */
        initializeJoystickMappings();
        super.initialize();
    }

    @Override
    public void simpleInitApp() {
        this.calibrateButton
                = GUIUtils.createButton(this.getAssetManager(), this.guiFont,
                        this.getContext().getTouchInput() != null, "calibrate",
                        "Calibrate Gamepad");
    }

    @Override
    public void simpleUpdate(float tpf) {
        JoystickPreviewScreen screen
                = this.getStateManager().getState(JoystickPreviewScreen.class);
        if (this.calibrateButton.getParent() == null && screen != null) {
            this.guiNode.attachChild(this.calibrateButton);
            this.calibrateButton.setLocalTranslation(
                    (this.getCamera().getWidth()
                    - ((BitmapText) this.calibrateButton.getChild(1))
                            .getLineWidth() - 10) / 2F,
                    this.getCamera().getHeight(), 0);
            this.inputManager.addListener(this, "calab");
            System.out.println("Add listern");
        } else if (this.calibrateButton.getParent() != null && screen == null) {
            this.guiNode.detachChild(this.calibrateButton);
            this.inputManager.removeListener(this);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        String buttonId;
        JoystickPreviewScreen screen
                = this.getStateManager().getState(JoystickPreviewScreen.class);
        System.out.println("Name = " + name);
        if (screen != null) {
            buttonId = GUIUtils.handleButtonPress(this.calibrateButton,
                    this.getInputManager().getCursorPosition(), isPressed);
            if (!isPressed && "calibrate".equals(buttonId)) {
                this.getStateManager().detach(screen);
                this.getStateManager()
                        .attach(new CalibrateInputScreen(CALIBRATION_FILE));
            }
        }
    }

    @Override
    public void restart() {
        super.restart();
        this.enqueue(() -> {
            JoystickPreviewScreen previewScreen = this.getStateManager()
                    .getState(JoystickPreviewScreen.class);
            CalibrateInputScreen calibrateScreen
                    = this.getStateManager().getState(CalibrateInputScreen.class);
            if (previewScreen != null) {
                previewScreen.resize();
            }
            if (calibrateScreen != null) {
                calibrateScreen.resize();
            }
            this.calibrateButton.setLocalTranslation(
                    (this.getCamera().getWidth()
                    - ((BitmapText) this.calibrateButton.getChild(1))
                            .getLineWidth() - 10) / 2F,
                    this.getCamera().getHeight(), 0);
        });
    }
}
