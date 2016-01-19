/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.window;

import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bruynhuis.galago.ui.optimization.TextureAtlas;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.ui.field.TextArea;
import com.bruynhuis.galago.ui.field.TextField;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

/**
 *
 * A window is the main container per screen. Each Screen contains one window.
 * The window is only for Interal use and shouldn't be called.
 *
 * @author nidebruyn
 */
public class Window {

    private float width = 1280f;
    private float height = 720f;
    protected BaseApplication application;
    protected Node guiNode;
    protected Node windowNode;
    protected TextureAtlas textureAtlas;
    protected ArrayList<Picture> optimizationPictures = new ArrayList<Picture>();
    protected BitmapFont bitmapFont;
    protected Fader fader;
    protected ArrayList<Panel> panels = new ArrayList<Panel>();
    protected CollisionResults results;
    protected Ray ray;

    /**
     *
     * @param simpleApplication
     * @param guiNode
     */
    public Window(BaseApplication simpleApplication, Node guiNode) {
        this(simpleApplication, guiNode, 1280f, 720f, simpleApplication.getAssetManager().loadFont("Interface/Fonts/Default.fnt"));
    }

    /**
     *
     * @param simpleApplication
     * @param guiNode
     * @param font
     */
    public Window(BaseApplication simpleApplication, Node guiNode, BitmapFont font) {
        this(simpleApplication, guiNode, 1280f, 720f, font);
    }

    /**
     *
     * @param simpleApplication
     * @param guiNode
     * @param width
     * @param height
     */
    public Window(BaseApplication simpleApplication, Node guiNode, float width, float height) {
        this(simpleApplication, guiNode, width, height, simpleApplication.getAssetManager().loadFont("Interface/Fonts/Default.fnt"));
    }

    /**
     *
     * @param simpleApplication
     * @param guiNode
     * @param width
     * @param height
     * @param font
     */
    public Window(BaseApplication simpleApplication, Node guiNode, float width, float height, BitmapFont font) {
        this.application = simpleApplication;
        this.guiNode = guiNode;
        this.width = width;
        this.height = height;
        this.bitmapFont = font;

        this.results = new CollisionResults();
        this.ray = new Ray(simpleApplication.getCamera().getLocation(), simpleApplication.getCamera().getDirection());

        textureAtlas = new TextureAtlas(1024, 1024);

        windowNode = new Node("MAIN_WINDOW_NODE");
        windowNode.setLocalTranslation((width / 2f) * getScaleFactorWidth(), (height / 2f) * getScaleFactorHeight(), 0);
        guiNode.attachChild(windowNode);



    }

//    /**
//     * Public helper method for creating a Unshaded gui material for Pictures.
//     * @param texture
//     * @return 
//     */
//    public Material makeGuiMaterial(Texture texture) {
//        Material material = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        material.setColor("Color", ColorRGBA.White);
//        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//        material.setTexture("ColorMap", texture);
//        return material;
//    }
//    
//    /**
//     * This is a helper method which must be called by all image widgets with pictures on them.
//     * @param picture 
//     */
//    public void addPictureForOptimization(Picture picture) {
//        optimizationPictures.add(picture);
//        textureAtlas.addTexture(picture.getMaterial().getTextureParam("ColorMap").getTextureValue(), "ColorMap");
//    }
    /**
     * Helper method which we use to optimize the ui with TextureAtlas and
     * batching
     */
    public void optimize() {
//        writeAtlas(textureAtlas.getAtlasTexture("ColorMap"));

//        Material m = makeGuiMaterial(textureAtlas.getAtlasTexture("ColorMap"));
//        for (int i = 0; i < optimizationPictures.size(); i++) {
//            Picture picture = optimizationPictures.get(i);
//            textureAtlas.applyCoords(picture);
//            picture.setMaterial(m);
//        }
//        
//        windowNode.batch();
        windowNode.setCullHint(Spatial.CullHint.Always);
    }

    protected void writeAtlas(Texture texture) {

        Date date = new Date();

        File file = new File("screenshot-window-atlas-" + date.getSeconds() + ".png").getAbsoluteFile();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            log("size = " + texture.getImage().getData().size());
            JmeSystem.writeImageFile(outStream, "png", texture.getImage().getData(0), 1024, 1024);
        } catch (IOException ex) {
            log("Error while saving screenshot" + ex);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException ex) {
                    log("Error while saving screenshot" + ex);
                }
            }
        }
    }

    public void log(String text) {
        System.out.println(text);
    }

    public void setVisible(boolean visible) {
        for (Iterator<Panel> it = panels.iterator(); it.hasNext();) {
            Panel panel = it.next();
            if (panel instanceof PopupDialog) {

                if (!visible) {
                    panel.setVisible(visible);
                }

            } else {
                panel.setVisible(visible);

            }
        }

        if (visible) {
            windowNode.setCullHint(Spatial.CullHint.Never);
        } else {
            windowNode.setCullHint(Spatial.CullHint.Always);
        }
    }

    /**
     * This is for internal use only.
     *
     * @param widgetNode
     * @return
     */
    public void fireButtonCollision(boolean down, boolean move, float cursorPointX, float cursorPointY, float tpf) {
        results.clear();

        // 1. calc direction
        Vector3f origin = new Vector3f(cursorPointX, cursorPointY, 10f);
        Vector3f direction = new Vector3f(0, 0, -10f);

        // 2. Aim the ray from cam loc to cam direction.        
        ray.setOrigin(origin);
        ray.setDirection(direction);

        // 3. Collect intersections between Ray and Shootables in results list.
        getWindowNode().collideWith(ray, results);

        // 5. Use the results (we mark the hit object)
        if (results.size() > 0) {

            for (int i = 0; i < results.size(); i++) {
                CollisionResult cr = results.getCollision(i);
                fireCollisionOnButtons(cr, down, move, cursorPointX, cursorPointY, tpf);

            }
        }
    }

    /**
     * Must be called by the picker.
     *
     * @param cr
     * @param down
     * @param move
     * @param cursorPointX
     * @param cursorPointY
     * @param tpf
     */
    private void fireCollisionOnButtons(CollisionResult cr, boolean down, boolean move, float cursorPointX, float cursorPointY, float tpf) {
        TouchButton touchButton = null;
        if (cr.getGeometry().getParent() != null
                && cr.getGeometry().getParent().getUserData(TouchButton.TYPE_TOUCH_BUTTON) != null
                && cr.getGeometry().getParent().getUserData(TouchButton.TYPE_TOUCH_BUTTON) instanceof TouchButton) {


            //Now we check what to do
            touchButton = (TouchButton) cr.getGeometry().getParent().getUserData(TouchButton.TYPE_TOUCH_BUTTON);
           
            if (touchButton.isEnabled() && touchButton.isVisible()) {
                if (move) {
                    touchButton.fireTouchMove(cursorPointX, cursorPointY, tpf);

                } else {
                    if (down) {
                        touchButton.fireTouchDown(cursorPointX, cursorPointY, tpf);
                    } else {
                        touchButton.fireTouchUp(cursorPointX, cursorPointY, tpf);
                    }

                }
            }

        }
    }

    public float getScaleFactorWidth() {
        return getAppSettings().getWidth() / getWidth();
    }

    public float getScaleFactorHeight() {
        return getAppSettings().getHeight() / getHeight();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public BaseApplication getApplication() {
        return application;
    }

    public Node getGuiNode() {
        return guiNode;
    }

    public BitmapFont getBitmapFont() {
        return bitmapFont;
    }

    public AssetManager getAssetManager() {
        return application.getAssetManager();
    }

    public AppSettings getAppSettings() {
        return application.getContext().getSettings();
    }

    public void add(Panel panel) {
        panels.add(panel);
        panel.add(windowNode);

    }

    public InputManager getInputManager() {
        return application.getInputManager();
    }

    public Fader getFader() {
        return fader;
    }

    public void setFader(Fader fader) {
        this.fader = fader;
        fader.setVisible(false);
        fader.add(windowNode);
    }

    public void removeFocusFromFields() {
        for (Iterator<Panel> it = panels.iterator(); it.hasNext();) {
            Panel panel = it.next();
            removeFocusFromFieldOnPanel(panel);

        }
    }

    protected void removeFocusFromFieldOnPanel(Panel panel) {
        for (Iterator<Widget> it1 = panel.getWidgets().iterator(); it1.hasNext();) {
            Widget widget = it1.next();
            if (widget instanceof TextField) {
                ((TextField) widget).blur();

            } else if (widget instanceof TextArea) {
                ((TextArea) widget).blur();

            } else if (widget instanceof Panel) {
                removeFocusFromFieldOnPanel((Panel) widget);
            }

        }
    }

    public Node getWindowNode() {
        return windowNode;
    }
}
