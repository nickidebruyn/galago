/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.flat.screen;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.flat.MainApplication;
import com.jme3.scene.LineBatchNode;
import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.panel.VPanel;
import com.jme3.bounding.BoundingBox;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import java.util.ArrayList;

/**
 *
 * @author nidebruyn
 */
public class LineShapeScreen extends AbstractScreen implements PhysicsCollisionListener, PickListener {

    private static final String BACKGROUND = "background";
    private TouchButton stats;
    private TouchButton clear;
    private TouchPickListener touchPickListener;
    private MainApplication mainApplication;
    private Sprite backgroundSprite;
    private Label bodiesLabel;
    private Material lineMaterial;
    private Material markerMaterial;
    private LineBatchNode lineNode;
    private LineData lineData;
    private ArrayList<LineData> lines = new ArrayList<LineData>();
    private Geometry lineGeometry;
    private float markerSize = 0.2f;
    private float lineSize = 0.4f;
    private float lineWidth = 20f;
    private float physicsLineWidth = 0.18f;
    private float angleBatchSize = 3f;
    private String selectedType = null;
    private TouchButtonAdapter toolsListener;
    private VPanel toolsPanel;
    private static final String TYPE_DRAW = "draw";
    private static final String TYPE_DEL = "del";
    private static final String TYPE_SMALL_BOX = "smallbox";
    private static final String TYPE_LARGE_BOX = "largebox";
    private static final String TYPE_BALL = "ball";

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        Label heading = new Label(hudPanel, "Line physics", 24, 400, 50);
        heading.setTextColor(ColorRGBA.Gray);
        heading.centerTop(0, 10);

        bodiesLabel = new Label(hudPanel, "Bodies: ", 18, 300, 30);
        bodiesLabel.setAlignment(BitmapFont.Align.Left);
        bodiesLabel.leftBottom(2, 2);

        Image i = new Image(hudPanel, "Resources/smallbutton.png", 15, 15);
        i.center();

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

        clear = new TouchButton(hudPanel, "clear", "Clear");
        clear.leftTop(5, 5);
        clear.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showScreen("line");
                }
            }
        });


        //Add the tools to the scenen
        toolsPanel = new VPanel(hudPanel, null, 65, 400);
        toolsPanel.leftBottom(0, 50);
        hudPanel.add(toolsPanel);

        toolsListener = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    selectedType = uid;
                }
            }
        };

        createToolButton(TYPE_DRAW, "Draw", toolsPanel, toolsListener);
        createToolButton(TYPE_DEL, "Del", toolsPanel, toolsListener);
        createToolButton(TYPE_SMALL_BOX, "Small Box", toolsPanel, toolsListener);
        createToolButton(TYPE_LARGE_BOX, "Large Box", toolsPanel, toolsListener);
        createToolButton(TYPE_BALL, "Ball", toolsPanel, toolsListener);


        toolsPanel.layout();
    }

    protected void createToolButton(String id, String text, Panel parent, TouchButtonListener buttonListener) {
        TouchButton button = new TouchButton(parent, id, "Resources/smallbutton.png", 62, 62);
        button.setText(text);
        button.setTextColor(ColorRGBA.DarkGray);
        button.setFontSize(12);
        button.addEffect(new TouchEffect(button));
        button.addTouchButtonListener(buttonListener);

    }

    @Override
    protected void load() {
        //Create some default stuff
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.White);
        ((Base2DApplication) baseApplication).getDyn4jAppState().getPhysicsSpace().addPhysicsCollisionListener(this);

        backgroundSprite = new Sprite(BACKGROUND, 36, 28);
        backgroundSprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/background.j3m"));
        rootNode.attachChild(backgroundSprite);

        lineMaterial = baseApplication.getModelManager().getMaterial("Materials/line.j3m");
        markerMaterial = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
        
        addBox(new Vector3f(0, -7f, 0), 24, 3);
        

        //Init the picker listener
        touchPickListener = new TouchPickListener(baseApplication.getCamera(), rootNode);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);

        selectedType = TYPE_DRAW;
    }

    @Override
    protected void show() {
        mainApplication.getDyn4jAppState().setEnabled(false);
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
            bodiesLabel.setText("Bodies: " + mainApplication.getDyn4jAppState().getPhysicsSpace().getBodyCount());
        }
    }

    public void picked(PickEvent pickEvent, float tpf) {
        if (isActive() && pickEvent.getContactObject() != null && pickEvent.getCursorPosition().x > 62f) {

            if (TYPE_DRAW.equals(selectedType) && pickEvent.getContactObject().getName().startsWith(BACKGROUND)) {
                if (pickEvent.isKeyDown()) {
                    startDrawing(pickEvent.getContactPoint().clone().multLocal(1, 1, 0));
//                    mainApplication.getDyn4jAppState().setEnabled(false);
                } else {
                    endDrawing(pickEvent.getContactPoint().clone().multLocal(1, 1, 0));
                    mainApplication.getDyn4jAppState().setEnabled(true);
                }
                
            } else if (TYPE_DEL.equals(selectedType)) {
                if (pickEvent.isKeyDown()) {
                    //Delete
                    Spatial s = pickEvent.getContactObject().getParent();
                    if (s instanceof Sprite) {
                        mainApplication.getDyn4jAppState().getPhysicsSpace().remove(s);
                        s.removeFromParent();
                    }
                }
                               
            } else {
                if (pickEvent.isKeyDown() && pickEvent.getContactObject().getName().startsWith(BACKGROUND)) {
                    //TODO: Add static types
                    float x = (int)pickEvent.getContactPoint().x;
                    float y = (int)pickEvent.getContactPoint().y;
                    Vector3f pos = new Vector3f(x, y, 0);
                    
                    if (TYPE_SMALL_BOX.equals(selectedType)) {
                        addBox(pos, 1, 1);
                    } else 
                    if (TYPE_LARGE_BOX.equals(selectedType)) {
                        addBox(pos, 3, 3);
                    } else 
                    if (TYPE_BALL.equals(selectedType)) {
                        addBall(pos, 0.5f);
                    }
                }
            }


        }

    }

    public void drag(PickEvent pickEvent, float tpf) {
        if (isActive() && pickEvent.getContactObject() != null && pickEvent.getCursorPosition().x > 62f && pickEvent.getContactObject().getName().startsWith(BACKGROUND)) {

            if (pickEvent.isKeyDown() && lineNode != null) {
                updateLine(pickEvent.getContactPoint().clone().multLocal(1, 1, 0));
            }

        }
    }

    protected void addBall(Vector3f position, float radius) {
        Sprite sprite = new Sprite("BALL", radius * 2f, radius * 2f);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/smileyf.j3m"));
        RigidBodyControl bodyControl = new RigidBodyControl(new CircleCollisionShape(radius), 0.2f);
        bodyControl.setRestitution(0.001f);
        bodyControl.setFriction(0.1f);
        bodyControl.setDensity(1f);
        bodyControl.setMass(1f);
        sprite.addControl(bodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
        bodyControl.setPhysicLocation(position);
        rootNode.attachChild(sprite);
    }

    protected void addBox(Vector3f position, float width, float height) {
        Sprite sprite = new Sprite("floor", width, height);
        sprite.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/terrain.j3m"));

        RigidBodyControl bodyControl = new RigidBodyControl(new BoxCollisionShape(width, height), 0);
        bodyControl.setRestitution(0);
        bodyControl.setFriction(1f);
        bodyControl.setDensity(10);
        sprite.addControl(bodyControl);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
        bodyControl.setPhysicLocation(position);
        rootNode.attachChild(sprite);

    }

    protected void startDrawing(Vector3f startPos) {
//        addBall(startPos);

        lineNode = new LineBatchNode("line");
        addLine(startPos);
        rootNode.attachChild(lineNode);
    }

    protected void addLine(Vector3f startPos) {
        Line line = new Line(startPos, startPos);
        line.setLineWidth(lineWidth);

        lineGeometry = new Geometry("line_geom", line);
        lineGeometry.setMaterial(lineMaterial);
        lineNode.attachChild(lineGeometry);

        lineData = new LineData(line, startPos, startPos);
        lines.add(lineData);
    }

    protected void updateLine(Vector3f endPos) {
        if (lineData != null && lineNode != null) {
            if (lineData.getStart().distance(endPos) >= lineSize) {
                //Update line for the last time
                endLine(endPos);
                addLine(endPos);
            } else {
                lineData.update(lineData.getStart(), endPos);
            }
        }
    }

    protected void endLine(Vector3f endPos) {
        //NB: The start and end is not updated
        lineData.update(lineData.getStart(), endPos);

    }

    protected void endDrawing(Vector3f endPos) {

        if (lineNode != null) {
            endLine(endPos);
//            lineNode.batch();

            addDrawingToPhysics();

            lineData = null;
            lineNode = null;
            lineGeometry = null;
            lines.clear();

        }
    }

    protected void addDrawingToPhysics() {

        BoundingBox bb = (BoundingBox) lineNode.getWorldBound();
//        log("\n\nBB = " + bb);
        float shiftX = bb.getCenter().x;
        float shiftY = bb.getCenter().y;

        RigidBodyControl rigidBodyControl = null;

        Vector3f start = null;
        Vector3f end = null;
        float oldAngle = 0f;

        for (int i = 0; i < lines.size(); i++) {

            LineData ld = lines.get(i);
            ld.update(ld.getStart().subtract(shiftX, shiftY, 0), ld.getEnd().subtract(shiftX, shiftY, 0));

            float angle = getAngle(ld.getStart(), ld.getEnd());
            log("Angle = " + angle);

            if (start == null) {
                start = ld.getStart();
            }

            end = ld.getEnd();

            //Check if the end point can be reset or not
            if (FastMath.abs(oldAngle - angle) < angleBatchSize) {
                log("RESET");
                end = null;
            }

            oldAngle = angle;

            //Only add if start and end is near
            if (end != null) {

                Vector3f midPoint = FastMath.interpolateLinear(0.5f, start, end);
                float size = start.distance(end);

                //Only add it if there is a size bigger than 0;
                if (size > 0) {
                    BoxCollisionShape collisionShape = new BoxCollisionShape(size, physicsLineWidth);
                    collisionShape.setLocation(midPoint.x, midPoint.y);
                    collisionShape.setRotation(FastMath.DEG_TO_RAD * angle);

                    if (rigidBodyControl == null) {
                        rigidBodyControl = new RigidBodyControl(collisionShape, 1f);
                        lineNode.addControl(rigidBodyControl);

                    } else {
                        rigidBodyControl.addCollisionShape(collisionShape);
                    }

                    rigidBodyControl.setRestitution(0.01f);
                    rigidBodyControl.setFriction(0.4f);
                    rigidBodyControl.setDensity(50);
                    rigidBodyControl.setMass(1);

                }
                start = null;
                end = null;

            }

        }

        if (rigidBodyControl != null) {
            log("Line parts: " + lines.size());
            log("Physics parts: " + rigidBodyControl.getBody().getFixtureCount());
            rigidBodyControl.setPhysicLocation(bb.getCenter().clone());

            mainApplication.getDyn4jAppState().getPhysicsSpace().add(lineNode);

//            addLine(lineNode, new Vector3f(-markerSize, 0, 0), new Vector3f(markerSize, 0, 0));
//            addLine(lineNode, new Vector3f(0, markerSize, 0), new Vector3f(0, -markerSize, 0));
        }

        lineNode.batch();
    }

    protected void addLine(Node parent, Vector3f start, Vector3f end) {

        Line line = new Line(start, end);
        line.setLineWidth(2);

        Geometry geometry = new Geometry("line_geom", line);
        geometry.setMaterial(markerMaterial);
        parent.attachChild(geometry);
    }

    public float getAngle(Vector3f vector1, Vector3f vector2) {
        float angle = (float) Math.toDegrees(Math.atan2(vector2.y - vector1.y, vector2.x - vector1.x));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }
}
