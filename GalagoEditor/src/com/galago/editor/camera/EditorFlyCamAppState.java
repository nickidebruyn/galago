package com.galago.editor.camera;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

/**
 *
 * @author nicki
 */
public class EditorFlyCamAppState extends AbstractAppState {

    private Application app;
    private EditorFlyCamera flyCam;

    public EditorFlyCamAppState() {
    }

    /**
     * This is called by SimpleApplication during initialize().
     */
    void setCamera(EditorFlyCamera cam) {
        this.flyCam = cam;
    }

    public EditorFlyCamera getCamera() {
        return flyCam;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = app;

        if (app.getInputManager() != null) {

            if (flyCam == null) {
                flyCam = new EditorFlyCamera(app.getCamera());
            }

            flyCam.registerWithInput(app.getInputManager());
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        flyCam.setEnabled(enabled);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        if (app.getInputManager() != null) {
            flyCam.unregisterInput();
        }
    }
}
