/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.editor.spatial;

import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.spatial.Polygon;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.Debug;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author NideBruyn
 */
public class Gizmo11 extends Node implements PickListener {

    private float radius = 1f;

    private Camera camera;
    private TouchPickListener touchPickListener;

    private Spatial ringXAxis;
    private Spatial ringYAxis;
    private Spatial ringZAxis;

    private Spatial moveXAxisPos;
//    private Spatial moveXAxisNeg;

    private Spatial moveYAxisPos;
//    private Spatial moveYAxisNeg;

    private Spatial moveZAxisPos;
//    private Spatial moveZAxisNeg;

    private Node draggingPlane;

    private GizmoListener gizmoListener;

    private Spatial selectedSpatial;
    private boolean wasDragging = false;
    private Vector3f startPosition;
    private Vector3f lastPosition;

    private int transformationType = 0;

    public static final int LOCAL = 0;
    public static final int GLOBAL = 1;
    public static final int CAMERA = 2;

    private int snapMode = 0;
    public static final int SNAP_TO_SCENE = 1;
    public static final int SNAP_TO_GRID = 2;

    public static final Quaternion PLANE_XY = new Quaternion().fromAngleAxis(0, new Vector3f(1, 0, 0));
    public static final Quaternion PLANE_YZ = new Quaternion().fromAngleAxis(-FastMath.PI / 2, new Vector3f(0, 1, 0));//YAW090
    public static final Quaternion PLANE_XZ = new Quaternion().fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0)); //PITCH090    

    private Quaternion originRotation;
    private Node rootNode;
    private Vector3f constraints = new Vector3f(0, 0, 0);

    public Gizmo11(String name, Camera camera, InputManager inputManager, Node rootNode) {
        super(name);
        this.camera = camera;
        this.rootNode = rootNode;
        init();

        touchPickListener = new TouchPickListener(name, camera, this);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);

    }

    protected void init() {

        //Load rings.
        ringXAxis = loadRing("xAxis", ColorRGBA.Blue);
        SpatialUtils.rotateTo(ringXAxis, 0, 0, 90);

        ringYAxis = loadRing("yAxis", ColorRGBA.Green);
        SpatialUtils.rotateTo(ringYAxis, -90, 0, 0);

        ringZAxis = loadRing("zAxis", ColorRGBA.Red);
        SpatialUtils.rotateTo(ringZAxis, 0, 90, 0);

        //Load the movement arrows
        float arrowDis = radius + (radius * 0.5f * 0.5f) + 0.1f;

        //Z Axis---------------
        moveZAxisPos = loadArrow("zArrowPos", ColorRGBA.Blue);
        SpatialUtils.move(moveZAxisPos, 0, 0, arrowDis);

//        moveZAxisNeg = loadArrow("zArrowNeg", ColorRGBA.Blue);
//        SpatialUtils.move(moveZAxisNeg, 0, 0, -arrowDis);
//        SpatialUtils.rotateTo(moveZAxisNeg, 0, 180, 0);
        //X Axis ---------------
        moveXAxisPos = loadArrow("xArrowPos", ColorRGBA.Red);
        SpatialUtils.rotateTo(moveXAxisPos, 0, 90, 0);
        SpatialUtils.move(moveXAxisPos, arrowDis, 0, 0);

//        moveXAxisNeg = loadArrow("xArrowNeg", ColorRGBA.Red);
//        SpatialUtils.move(moveXAxisNeg, -arrowDis, 0, 0);
//        SpatialUtils.rotateTo(moveXAxisNeg, 0, -90, 0);
        //Y Axis ----------------
        moveYAxisPos = loadArrow("yArrowPos", ColorRGBA.Green);
        SpatialUtils.rotateTo(moveYAxisPos, -90, 0, 0);
        SpatialUtils.move(moveYAxisPos, 0, arrowDis, 0);

//        moveYAxisNeg = loadArrow("yArrowNeg", ColorRGBA.Green);
//        SpatialUtils.move(moveYAxisNeg, 0, -arrowDis, 0);
//        SpatialUtils.rotateTo(moveYAxisNeg, 90, 0, 0);
        float size = 1000;
        Geometry g = new Geometry("plane", new Quad(size, size));
        SpatialUtils.addColor(g, ColorUtils.rgb(100, 0, 0, 50), true);
        g.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        g.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        g.setLocalTranslation(-size / 2, -size / 2, 0);
        draggingPlane = new Node();
        draggingPlane.attachChild(g);
        attachChild(draggingPlane);

        this.setQueueBucket(RenderQueue.Bucket.Translucent);
    }

    private Spatial loadRing(String gName, ColorRGBA colorRGBA) {
        Polygon pYXis = new Polygon(30, radius, 0.05f);
        Geometry geom = new Geometry(name + gName, pYXis);
        Material m = SpatialUtils.addColor(geom, colorRGBA, true);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);
        attachChild(geom);
//        SpatialUtils.updateSpatialTransparency(geom, true, 0.3f);

        return geom;

    }

    private Spatial loadArrow(String gName, ColorRGBA colorRGBA) {
        Spatial geom = SpatialUtils.addCone(this, 20, radius * 0.2f, radius * 0.5f);
        geom.setName(name + gName);
        Material m = SpatialUtils.addColor(geom, colorRGBA, true);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);
//        SpatialUtils.updateSpatialTransparency(geom, true, 0.3f);

        return geom;

    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {

        if (pickEvent.isKeyDown() && pickEvent.isLeftButton() && pickEvent.getContactObject() != null) {
            Debug.log("You clicked on " + pickEvent.getContactObject().getName());

            if (pickEvent.getContactObject().equals(moveXAxisPos)) {
                constraints = Vector3f.UNIT_X; // move only X
                initiatePick(pickEvent, PLANE_XY, GLOBAL);

            } else if (pickEvent.getContactObject().equals(moveYAxisPos)) {
                constraints = Vector3f.UNIT_Y; // move only X
                initiatePick(pickEvent, PLANE_YZ, GLOBAL);

            } else if (pickEvent.getContactObject().equals(moveZAxisPos)) {
                constraints = Vector3f.UNIT_Z; // move only X
                initiatePick(pickEvent, PLANE_XZ, GLOBAL);

            }

        } else {
            stopPick();

        }

    }

    public void initiatePick(PickEvent pickEvent, Quaternion planeRotation, int type) {
        selectedSpatial = pickEvent.getContactObject();
        startPosition = pickEvent.getContactObject().getWorldTranslation().clone();
        wasDragging = true;

        attachChild(draggingPlane);
        setTransformation(planeRotation, type, camera);

    }

    public void stopPick() {
        draggingPlane.removeFromParent();
        selectedSpatial = null;
        wasDragging = false;
    }

    public void setTransformation(Quaternion planeRotation, int type, Camera camera) {
        Quaternion rot = new Quaternion();
        transformationType = type;
        switch (transformationType) {
            case LOCAL:
                rot.set(selectedSpatial.getWorldRotation());
                rot.multLocal(planeRotation);
                originRotation = selectedSpatial.getWorldRotation().clone();
                break;
            case GLOBAL:
                rot.set(planeRotation);
                originRotation = new Quaternion(Quaternion.IDENTITY);
                break;
            case CAMERA:
                rot.set(camera.getRotation());
                originRotation = camera.getRotation();
                break;
            default:
                break;
        }
        draggingPlane.setLocalRotation(rot);
    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {

        if (wasDragging) {

            if (pickEvent.getContactPoint() != null) {

                //Drag pos
                Vector3f diff = startPosition.add(pickEvent.getContactPoint()).subtract(selectedSpatial.getLocalTranslation()).mult(constraints);
                updateSelectedTranslation(diff, constraints);


//                Vector3f diff = Vector3f.ZERO;
////                if (pickedMarker.equals(QUAD_XY) || pickedMarker.equals(QUAD_XZ) || pickedMarker.equals(QUAD_YZ)) {
////                    diff = pickManager.getTranslation();
////
////                } else 
//                if (selectedSpatial.equals(moveXAxisPos) || selectedSpatial.equals(moveYAxisPos) || selectedSpatial.equals(moveZAxisPos)) {
//                    diff = getTranslation(constraints);
//                }
//                Vector3f position;
//                Spatial parent = toolController.getSelectedSpatial().getParent();
//                if (parent != null) {
//                    position = startPosition.add(parent.getWorldRotation().inverse().mult(diff));
//                } else {
//                    position = startPosition.add(diff);
//                }
//                lastPosition = position;
//                toolController.updateSelectedTranslation(position, pickedMarker);

                gizmoListener.gizmoUpdate(getWorldTranslation().clone(), getWorldRotation().clone(), getWorldScale().clone());
            }

        }

    }

    public void updateSelectedTranslation(final Vector3f translation, final Vector3f cons) {

        if (translation == null) {
            return;

        }

        if (snapMode == SNAP_TO_SCENE) {
            translation.set(snapToScene(translation));
        }
        if (snapMode == SNAP_TO_GRID) {
            if (cons.x != 0f) {
                translation.setX((int) translation.x);
            }
            if (cons.y != 0f) {
                translation.setY((int) translation.y);
            }
            if (cons.z != 0f) {
                translation.setZ((int) translation.z);
            }
        }
        setLocalTranslation(translation);
    }

    private Vector3f snapToScene(final Vector3f position) {
        final Ray ray = new Ray(position, Vector3f.UNIT_Y.negate());
        final CollisionResults collisionResults = new CollisionResults();
        rootNode.collideWith(ray, collisionResults);
        for (CollisionResult r : collisionResults) {
            if (!r.getGeometry().equals(selectedSpatial)) {
                position.y = r.getContactPoint().y;
                break;
            }
        }
        return position;
    }

    public void setGizmoListener(GizmoListener gizmoListener) {
        this.gizmoListener = gizmoListener;

    }

    public void setSnapMode(int snapMode) {
        this.snapMode = snapMode;
    }

//    /**
//     *
//     * @param axisConstraint
//     * @return
//     */
//    public Vector3f getTranslation(Vector3f axisConstraint) {
//        Vector3f localConstraint = (originRotation.mult(axisConstraint)).normalize(); // according to the "plane" rotation
//        Vector3f constrainedTranslation = localConstraint.mult(getTranslation().dot(localConstraint));
//        return constrainedTranslation;
//    }
//    
//    /**
//     *
//     * @return the translation in WorldSpace
//     */
//    public Vector3f getTranslation() {
//        return finalPickLoc.subtract(startPickLoc);
//    }    
}
