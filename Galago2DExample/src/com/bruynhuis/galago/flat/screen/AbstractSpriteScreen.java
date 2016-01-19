/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.flat.screen;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.flat.MainApplication;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.EllipseCollisionShape;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.physics.joint.HingeJoint;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.PyramidCollisionShape;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.button.Spinner;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.panel.VPanel;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public abstract class AbstractSpriteScreen extends AbstractScreen implements PhysicsCollisionListener, PickListener {

    private static final String TYPE_WALL = "wall";
    private static final String TYPE_CRATE = "crate";
    private static final String TYPE_EGG = "egg";
    private static final String TYPE_BALL = "ball";
    private static final String TYPE_ROCK = "rock";
    private static final String TYPE_RAMP = "ramp";
    private static final String TYPE_TREE = "tree";
    private static final String TYPE_FLOOR = "floor";
    private static final String TYPE_FIRE = "fire";
    private static final String TYPE_CSAW = "csaw";
    
    private ColorRGBA dayColor = new ColorRGBA(137f / 255f, 196f / 255f, 244f / 255f, 1f);
    private ColorRGBA nightColor = new ColorRGBA(11f / 255f, 12f / 255f, 20f / 255f, 1f);
    private String selectedType = null;
    private ControlButton controlButton;
    private TouchButton stats;
    private Spinner timeButton;
    private String[] timeOptions = {"Night", "Day"};
    private VPanel toolsPanel;
    private TouchPickListener touchPickListener;
    protected Spatial selectedSpatial;
    protected MainApplication mainApplication;
    private TouchButtonAdapter toolsListener;
    private TouchButton pauseButton;
    private TouchButton playButton;
    private TouchButton selectButton;
    private TouchButton rotateButton;
    private TouchButton moveButton;
    private TouchButton delButton;
    private static final int ACTION_SELECT = 1;
    private static final int ACTION_MOVE = 2;
    private static final int ACTION_ROTATE = 3;
    private static final int ACTION_DELETE = 4;
    private int action = ACTION_SELECT;

    protected abstract String getHeading();

    protected abstract String getInstructions();

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        Label heading = new Label(hudPanel, getHeading(), 24, 400, 50);
        heading.setTextColor(ColorRGBA.Gray);
        heading.centerTop(0, 80);

        Label info = new Label(hudPanel, getInstructions(), 16, 500, 50);
        info.setTextColor(ColorRGBA.Orange);
        info.centerTop(0, 120);

        toolsPanel = new VPanel(hudPanel, null, 60, 520);
        toolsPanel.leftTop(0, 0);
        hudPanel.add(toolsPanel);

        toolsListener = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    selectedType = uid;
                }
            }
        };

        createToolButton(TYPE_BALL, "Ball", toolsPanel, toolsListener);
        createToolButton(TYPE_CRATE, "Crate", toolsPanel, toolsListener);
        createToolButton(TYPE_EGG, "Egg", toolsPanel, toolsListener);
        createToolButton(TYPE_WALL, "Wall", toolsPanel, toolsListener);
        createToolButton(TYPE_ROCK, "Rock", toolsPanel, toolsListener);
        createToolButton(TYPE_FLOOR, "Floor", toolsPanel, toolsListener);
        createToolButton(TYPE_RAMP, "Ramp", toolsPanel, toolsListener);
        createToolButton(TYPE_CSAW, "C-Saw", toolsPanel, toolsListener);
        createToolButton(TYPE_TREE, "Tree", toolsPanel, toolsListener);
        createToolButton(TYPE_FIRE, "Fire", toolsPanel, toolsListener);

        toolsPanel.layout();

        controlButton = new ControlButton(hudPanel, "controlbutton", window.getWidth(), window.getHeight());
        controlButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && touchX > 70f && touchX < window.getWidth() - 70f && touchY < window.getHeight()-70f && touchY > 70f) {

                    if (selectedType != null && action == ACTION_SELECT) {
                        Vector3f pos = getTouchWorldCoords();

                        if (selectedType.equals(TYPE_CRATE)) {
                            addCrate(pos);
                        } else if (selectedType.equals(TYPE_EGG)) {
                            addEgg(pos);
                        } else if (selectedType.equals(TYPE_WALL)) {
                            addWall(pos);
                        } else if (selectedType.equals(TYPE_BALL)) {
                            addBall(pos);
                        } else if (selectedType.equals(TYPE_ROCK)) {
                            addRock(pos);
                        } else if (selectedType.equals(TYPE_FLOOR)) {
                            addFloor(pos);
                        } else if (selectedType.equals(TYPE_RAMP)) {
                            addRamp(pos);                            
                        } else if (selectedType.equals(TYPE_CSAW)) {
                            addCSaw(pos);
                        } else if (selectedType.equals(TYPE_TREE)) {
                            addTree(pos);
                        } else if (selectedType.equals(TYPE_FIRE)) {
                            addFire(pos);
                        }
                    }

                }
            }
        });

        stats = new TouchButton(hudPanel, "stats", "Stats");
        stats.rightTop(5, 5);
        stats.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.showStats();
                }
            }
        });

        timeButton = new Spinner(hudPanel, "timeofdaybuitton", timeOptions);
        timeButton.rightTop(300, 5);
        timeButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    if (timeButton.getIndex() == 0) {
                        mainApplication.getViewPort().setBackgroundColor(dayColor);
                    } else if (timeButton.getIndex() == 1) {
                        mainApplication.getViewPort().setBackgroundColor(nightColor);
                    } else {
                        mainApplication.getViewPort().setBackgroundColor(dayColor);
                    }

                }
            }
        });

        pauseButton = createSmallButton("pause_physics", "Interface/icon-pause.png", hudPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && pauseButton.isVisible()) {
                    mainApplication.getDyn4jAppState().setEnabled(false);
                    playButton.setVisible(true);
                    pauseButton.setVisible(false);
                }
            }
        });
        pauseButton.rightTop(5, 100);

        playButton = createSmallButton("play_physics", "Interface/icon-play.png", hudPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && playButton.isVisible()) {
                    mainApplication.getDyn4jAppState().setEnabled(true);
                    playButton.setVisible(false);
                    pauseButton.setVisible(true);
                }
            }
        });
        playButton.rightTop(5, 100);
        
        selectButton = createSmallButton("select_physics", "Interface/icon-select.png", hudPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    //set select flag
                    action = ACTION_SELECT;
                }
            }
        });
        selectButton.rightTop(5, 160);
        
        moveButton = createSmallButton("move_physics", "Interface/icon-move.png", hudPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    //set move flag
                    action = ACTION_MOVE;
                }
            }
        });
        moveButton.rightTop(5, 220);
        
        rotateButton = createSmallButton("rotate_physics", "Interface/icon-rotate.png", hudPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    //TODO: set rotate flag
                    action = ACTION_ROTATE;
                }
            }
        });
        rotateButton.rightTop(5, 280);
        
        delButton = createSmallButton("del_physics", "Interface/icon-del.png", hudPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    action = ACTION_DELETE;
                }
            }
        });
        delButton.rightTop(5, 340);
    }
        
    protected void createToolButton(String id, String text, Panel parent, TouchButtonListener buttonListener) {
        TouchButton button = new TouchButton(parent, id, "Resources/smallbutton.png", 50, 50);
        button.setText(text);
        button.setTextColor(ColorRGBA.DarkGray);
        button.setFontSize(12);
        button.addEffect(new TouchEffect(button));
        button.addTouchButtonListener(buttonListener);

    }

    protected TouchButton createSmallButton(String id, String image, Panel parent, TouchButtonListener buttonListener) {
        TouchButton button = new TouchButton(parent, id, image, 50, 50);
        button.addEffect(new TouchEffect(button));
        button.addTouchButtonListener(buttonListener);
        return button;

    }

    @Override
    protected void load() {
        //Create some default stuff
        baseApplication.getViewPort().setBackgroundColor(dayColor);
        ((Base2DApplication) baseApplication).getDyn4jAppState().getPhysicsSpace().addPhysicsCollisionListener(this);

        //Init the picker listener
        touchPickListener = new TouchPickListener(baseApplication.getCamera(), rootNode);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);
    }

    protected void addFloor(Vector3f position) {
        float width = 16f;
        float height = 1f;

        Sprite sprite = new Sprite(TYPE_FLOOR, width, height);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/floor.j3m"));

        RigidBodyControl bodyControl = new RigidBodyControl(new BoxCollisionShape(width, height), 0);
        bodyControl.setRestitution(0);
        bodyControl.setFriction(0.8f);
        sprite.addControl(bodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
        bodyControl.setPhysicLocation(position);
        rootNode.attachChild(sprite);

    }

    protected void addWall(Vector3f position) {
        Sprite sprite = new Sprite(TYPE_WALL, 2, 1);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/wall.j3m"));

        RigidBodyControl bodyControl = new RigidBodyControl(new BoxCollisionShape(2, 1), 0);
        bodyControl.setRestitution(0);
        bodyControl.setFriction(0.8f);
        sprite.addControl(bodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
        bodyControl.setPhysicLocation(position);
        bodyControl.setPhysicRotation(FastMath.DEG_TO_RAD * 90f);
        rootNode.attachChild(sprite);

    }

    protected void addCrate(Vector3f position) {
        float width = 1.5f;
        float height = 1.5f;

        Sprite sprite = new Sprite(TYPE_CRATE, width, height);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/crate.j3m"));
        RigidBodyControl bodyControl = new RigidBodyControl(new BoxCollisionShape(width, height), 20f);
        bodyControl.setRestitution(0.1f);
        bodyControl.setFriction(0.1f);
        sprite.addControl(bodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
        bodyControl.setPhysicLocation(position);
        rootNode.attachChild(sprite);

    }

    protected void addBall(Vector3f position) {
        float radius = 0.5f;

        Sprite sprite = new Sprite("BALL", radius * 2f, radius * 2f);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/ball.j3m"));
        RigidBodyControl bodyControl = new RigidBodyControl(new CircleCollisionShape(radius), 2f);
        bodyControl.setRestitution(0.5f);
        bodyControl.setFriction(0.4f);
        sprite.addControl(bodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
        bodyControl.setPhysicLocation(position);
        rootNode.attachChild(sprite);
    }

    protected void addRock(Vector3f position) {
        float width = 1.8f;
        float height = 1.4f;

        Sprite sprite = new Sprite("BALL", width, height);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/rock.j3m"));
        RigidBodyControl bodyControl = new RigidBodyControl(new EllipseCollisionShape(width, height), 200);
        bodyControl.setRestitution(0f);
        bodyControl.setFriction(0.8f);
        sprite.addControl(bodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
        bodyControl.setPhysicLocation(position);
        rootNode.attachChild(sprite);
    }

    protected void addRamp(Vector3f position) {
        Sprite sprite = new Sprite("RAMP", 8, 1);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/triangle.j3m"));

        RigidBodyControl bodyControl = new RigidBodyControl(new PyramidCollisionShape(8, 1), 0);
        bodyControl.setRestitution(0);
        bodyControl.setFriction(0.8f);
        sprite.addControl(bodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
        bodyControl.setPhysicLocation(position);
        rootNode.attachChild(sprite);
    }
    
    protected void addCSaw(Vector3f position) {
        
        //Solid
        Sprite sprite = new Sprite(TYPE_CSAW, 2, 1);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/triangle.j3m"));
        RigidBodyControl bodyControl = new RigidBodyControl(new PyramidCollisionShape(2, 1), 0);
        bodyControl.setRestitution(0);
        bodyControl.setFriction(0.8f);
        sprite.addControl(bodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
        bodyControl.setPhysicLocation(position);
        rootNode.attachChild(sprite);
        
        //plank
        Sprite plankSprite = new Sprite(TYPE_CSAW, 8, 0.2f);
        plankSprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/floor.j3m"));
        RigidBodyControl plankbodyControl = new RigidBodyControl(new BoxCollisionShape(8, 0.2f), 1);
        plankbodyControl.setRestitution(0);
        plankbodyControl.setFriction(0.8f);
        plankSprite.addControl(plankbodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(plankSprite);
        plankbodyControl.setPhysicLocation(position.add(0, 0.6f, 0));
        rootNode.attachChild(plankSprite);
        
        HingeJoint hingeJoint = new HingeJoint(bodyControl, plankbodyControl, position.add(0, 0.6f, 0));
        mainApplication.getDyn4jAppState().getPhysicsSpace().addJoint(hingeJoint);
    }

    protected void addTree(Vector3f position) {
        float scale = 0.03f;
        int i = FastMath.nextRandomInt(0, 4);
        if (i == 0) {
            scale = 0.035f;
        } else if (i == 1) {
            scale = 0.04f;
        } else if (i == 2) {
            scale = 0.045f;
        } else if (i == 3) {
            scale = 0.05f;
        }
        
        float width = 54f;
        float height = 128f;
        Sprite sprite = new Sprite(TYPE_TREE, width * scale, height * scale);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/tree.j3m"));
        sprite.setLocalTranslation(position.x, position.y, -1f);
        rootNode.attachChild(sprite);
    }

    protected void addFire(Vector3f position) {
        AnimatedSprite flameSprite = new AnimatedSprite("flame", 3, 3, 4, 4, 5, true, false, true);
        flameSprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/flame.j3m"));
        flameSprite.setLocalTranslation(position);
        rootNode.attachChild(flameSprite);
        flameSprite.play();
    }

    protected void addEgg(Vector3f position) {
        float scale = 0.02f;
        float width = 50f * scale;
        float height = 68f * scale;

        Sprite sprite = new Sprite("EGG", width, height);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/egg.j3m"));
        RigidBodyControl bodyControl = new RigidBodyControl(new EllipseCollisionShape(width, height), 4f);
        bodyControl.setRestitution(0.2f);
        bodyControl.setFriction(0.7f);
        sprite.addControl(bodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
        bodyControl.setPhysicLocation(position);
        rootNode.attachChild(sprite);

    }

    @Override
    protected void show() {
        mainApplication.getDyn4jAppState().setEnabled(false);
        playButton.setVisible(true);
        pauseButton.setVisible(false);
    }

    @Override
    protected void exit() {
        touchPickListener.unregisterInput();
        rootNode.detachAllChildren();
        mainApplication.getDyn4jAppState().getPhysicsSpace().clear();

    }

    @Override
    protected void pause() {
    }

    public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB) {

    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            
            if (selectedSpatial != null && action == ACTION_DELETE) {                
                mainApplication.getDyn4jAppState().getPhysicsSpace().remove(selectedSpatial);
                selectedSpatial.removeFromParent();
                selectedSpatial = null;
            }
            
        }
    }

    public void picked(PickEvent pickEvent, float tpf) {
        if (isActive()) {
            if (pickEvent.isKeyDown() && pickEvent.getCursorPosition().x > 70f && pickEvent.getContactObject() != null
                    && pickEvent.getContactObject().getParent() != null && action != ACTION_SELECT) {
                
                selectedSpatial = pickEvent.getContactObject().getParent(); 

            } else {
                selectedSpatial = null;
            }

        }

    }

    protected void updateSelectedSpatialPosition(Vector3f pos) {
        if (selectedSpatial != null) {
//            log("pos: " + selectedSpatial.getName());
            RigidBodyControl rbc = selectedSpatial.getControl(RigidBodyControl.class);
            if (rbc != null) {
                rbc.setPhysicLocation(getTouchWorldCoords());
            } else {
                selectedSpatial.setLocalTranslation(pos);
            }
        }

    }
    
    protected void updateSelectedSpatialRotation(float radians) {
        if (selectedSpatial != null) {
            RigidBodyControl rbc = selectedSpatial.getControl(RigidBodyControl.class);
            if (rbc != null) {
                rbc.rotate(radians);
            } else {
                selectedSpatial.rotate(0, 0, radians);
            }
        }

    }

    public void drag(PickEvent pickEvent, float tpf) {
        if (isActive() && pickEvent.getCursorPosition().x > 70f) {
            if (selectedSpatial != null && pickEvent.isKeyDown()) {
                
                if (action == ACTION_MOVE) {
                    updateSelectedSpatialPosition(getTouchWorldCoords());
                }
                if (action == ACTION_ROTATE) {
                    if (pickEvent.isLeft()) {
                        updateSelectedSpatialRotation(tpf);
                    } else if (pickEvent.isRight()) {
                        updateSelectedSpatialRotation(-tpf);
                    }
                    
                }

            }
            
            //Drag the camera
            if (selectedSpatial == null && pickEvent.isKeyDown()) {
                log("drag mouse: " + pickEvent.getAnalogValue());
                
                if (pickEvent.isLeft()) {
                    camera.getLocation().addLocal(pickEvent.getAnalogValue()*10f, 0, 0);
                    camera.update();
                }
                if (pickEvent.isRight()) {
                    camera.getLocation().subtractLocal(pickEvent.getAnalogValue()*10f, 0, 0);
                    camera.update();
                }
                
            }

        }

    }
}
