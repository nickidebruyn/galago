/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.editor.spatial;

import com.bruynhuis.galago.input.Input;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.spatial.Polygon;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.Debug;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author NideBruyn
 */
public class Gizmo extends Node implements PickListener {

    private float radius = 1f;

    private Camera camera;
    private TouchPickListener touchPickListener;

    private Spatial ringXAxis;
    private Spatial ringYAxis;
    private Spatial ringZAxis;

    private Spatial moveXAxisPos;
    private Spatial scaleXAxisNeg;

    private Spatial moveYAxisPos;
    private Spatial scaleYAxisNeg;

    private Spatial moveZAxisPos;
    private Spatial scaleZAxisNeg;

    private Spatial selectedGizmo;
    private Vector3f downClickPos;
    private Vector3f mainGizmoPos;
    private Vector3f startScale;
    private float dragDistance;

    private Line transformLine;
    private Geometry transformLineGeometry;

    private GizmoListener gizmoListener;

    public static final Quaternion PLANE_XY = new Quaternion().fromAngleAxis(0, new Vector3f(1, 0, 0));
    public static final Quaternion PLANE_YZ = new Quaternion().fromAngleAxis(-FastMath.PI / 2, new Vector3f(0, 1, 0));//YAW090
    public static final Quaternion PLANE_XZ = new Quaternion().fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0)); //PITCH090   

    public static final int LOCAL = 0;
    public static final int GLOBAL = 1;
    public static final int CAMERA = 2;

    private boolean dragging = false;
    private boolean ctrlDown = false;
    private Node draggingPlane;
    private Vector3f constraints = new Vector3f(0, 0, 0);

    private Spatial target;

    public Gizmo(String name, Camera camera, InputManager inputManager) {
        super(name);
        this.camera = camera;
        init();
        setShadowMode(RenderQueue.ShadowMode.Off);

        touchPickListener = new TouchPickListener(name, camera, this);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);

    }

    protected void init() {

        //Load rings.
        ringXAxis = loadRotateTool("xAxis", ColorRGBA.Blue);
        SpatialUtils.rotateTo(ringXAxis, 0, 90, 0);

        ringYAxis = loadRotateTool("yAxis", ColorRGBA.Green);
        SpatialUtils.rotateTo(ringYAxis, -90, 0, 0);

        ringZAxis = loadRotateTool("zAxis", ColorRGBA.Red);
        SpatialUtils.rotateTo(ringZAxis, 0, 0, 90);

        //Load the movement arrows
        float arrowDis = radius + (radius * 0.5f * 0.5f) + 0.2f;
        float cubeDis = radius + (radius * 0.5f * 0.5f) + 1f;

        //Z Axis---------------
        moveZAxisPos = loadMoveTool("zArrowPos", ColorRGBA.Blue);
        SpatialUtils.move(moveZAxisPos, 0, 0, arrowDis);

        scaleZAxisNeg = loadScaleTool("zScalePos", ColorRGBA.Blue);
        SpatialUtils.move(scaleZAxisNeg, 0, 0, cubeDis);

        //X Axis ---------------
        moveXAxisPos = loadMoveTool("xArrowPos", ColorRGBA.Red);
        SpatialUtils.rotateTo(moveXAxisPos, 0, 90, 0);
        SpatialUtils.move(moveXAxisPos, arrowDis, 0, 0);

        scaleXAxisNeg = loadScaleTool("xScalePos", ColorRGBA.Red);
        SpatialUtils.move(scaleXAxisNeg, cubeDis, 0, 0);

        //Y Axis ----------------
        moveYAxisPos = loadMoveTool("yArrowPos", ColorRGBA.Green);
        SpatialUtils.rotateTo(moveYAxisPos, -90, 0, 0);
        SpatialUtils.move(moveYAxisPos, 0, arrowDis, 0);

        scaleYAxisNeg = loadScaleTool("yScalePos", ColorRGBA.Green);
        SpatialUtils.move(scaleYAxisNeg, 0, cubeDis, 0);

        float size = 1000;
        Geometry g = new Geometry("plane", new Quad(size, size));
        SpatialUtils.addColor(g, ColorUtils.rgb(255, 255, 255, 10), true);
        g.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        g.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        g.setLocalTranslation(-size / 2, -size / 2, 0);
        draggingPlane = new Node();
        draggingPlane.attachChild(g);
        attachChild(draggingPlane);

        transformLineGeometry = (Geometry) SpatialUtils.addLine(this, new Vector3f(0, 0, 0), new Vector3f(10, 0, 0), ColorRGBA.Blue, 5);
        transformLine = (Line) transformLineGeometry.getMesh();
        transformLineGeometry.setQueueBucket(RenderQueue.Bucket.Translucent);
        transformLineGeometry.removeFromParent();

        this.setQueueBucket(RenderQueue.Bucket.Translucent);
        
        this.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float f) {
                
//                System.out.println("Cntrl down = " + Input.get("ctrl"));
                
                if (Input.get("ctrl") == 1) {
                    ctrlDown = true;                    
                } else if (Input.get("ctrl") == -1) {
                    ctrlDown = false;
                    
                }
                
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
                
            }
            
        
        });
    }

    private Spatial loadRotateTool(String gName, ColorRGBA colorRGBA) {
        Polygon pYXis = new Polygon(30, radius, 0.075f);
        Geometry geom = new Geometry(name + gName, pYXis);
        Material m = SpatialUtils.addColor(geom, colorRGBA, true);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);
        attachChild(geom);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        geom.setShadowMode(RenderQueue.ShadowMode.Off);

        return geom;

    }

    private Spatial loadMoveTool(String gName, ColorRGBA colorRGBA) {
        Spatial geom = SpatialUtils.addCone(this, 20, radius * 0.16f, radius * 0.5f);
        geom.setName(name + gName);
        Material m = SpatialUtils.addColor(geom, colorRGBA, true);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        geom.setShadowMode(RenderQueue.ShadowMode.Off);

        return geom;

    }

    private Spatial loadScaleTool(String gName, ColorRGBA colorRGBA) {
        float extend = radius * 0.15f;
        Spatial geom = SpatialUtils.addSphere(this, 20, 20, extend);
//        Spatial geom = SpatialUtils.addBox(this, extend, extend, extend);
        geom.setName(name + gName);
        Material m = SpatialUtils.addColor(geom, colorRGBA, true);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        geom.setShadowMode(RenderQueue.ShadowMode.Off);

        return geom;

    }

    public Spatial getTarget() {
        return target;
    }

    public void setTarget(Spatial target) {
        this.target = target;
    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {

        if (pickEvent.isKeyDown() && pickEvent.isLeftButton() && pickEvent.getContactObject() != null) {
            Debug.log("You clicked on " + pickEvent.getContactObject().getName());

            if (pickEvent.getContactObject().equals(moveXAxisPos)) {
                constraints = Vector3f.UNIT_X; // move only X
                initiatePick(pickEvent, PLANE_XY, GLOBAL);
                transformLine.updatePoints(new Vector3f(-1000, 0, 0), new Vector3f(1000, 0, 0));
                transformLineGeometry.getMaterial().setColor("Color", ColorRGBA.Red);

            } else if (pickEvent.getContactObject().equals(moveYAxisPos)) {
                constraints = Vector3f.UNIT_Y; // move only X
                initiatePick(pickEvent, PLANE_YZ, GLOBAL);
                transformLine.updatePoints(new Vector3f(0, -1000, 0), new Vector3f(0, 1000, 0));
                transformLineGeometry.getMaterial().setColor("Color", ColorRGBA.Green);

            } else if (pickEvent.getContactObject().equals(moveZAxisPos)) {
                constraints = Vector3f.UNIT_Z; // move only X
                initiatePick(pickEvent, PLANE_XZ, GLOBAL);
                transformLine.updatePoints(new Vector3f(0, 0, -1000), new Vector3f(0, 0, 1000));
                transformLineGeometry.getMaterial().setColor("Color", ColorRGBA.Blue);

            } else if (pickEvent.getContactObject().equals(scaleXAxisNeg)) {
                constraints = Vector3f.UNIT_X; // move only X
                initiatePick(pickEvent, PLANE_XY, GLOBAL);
                transformLine.updatePoints(new Vector3f(-1000, 0, 0), new Vector3f(1000, 0, 0));
                transformLineGeometry.getMaterial().setColor("Color", ColorRGBA.Red);

            } else if (pickEvent.getContactObject().equals(scaleYAxisNeg)) {
                constraints = Vector3f.UNIT_Y; // move only X
                initiatePick(pickEvent, PLANE_YZ, GLOBAL);
                transformLine.updatePoints(new Vector3f(0, -1000, 0), new Vector3f(0, 1000, 0));
                transformLineGeometry.getMaterial().setColor("Color", ColorRGBA.Green);

            } else if (pickEvent.getContactObject().equals(scaleZAxisNeg)) {
                constraints = Vector3f.UNIT_Z; // move only X
                initiatePick(pickEvent, PLANE_XZ, GLOBAL);
                transformLine.updatePoints(new Vector3f(0, 0, -1000), new Vector3f(0, 0, 1000));
                transformLineGeometry.getMaterial().setColor("Color", ColorRGBA.Blue);

            } else if (pickEvent.getContactObject().equals(ringXAxis)) {
                System.out.println("Ring clicked: ");
                constraints = Vector3f.UNIT_X; // move only X
                initiatePick(pickEvent, PLANE_YZ, GLOBAL);

            } else if (pickEvent.getContactObject().equals(ringYAxis)) {
                System.out.println("Ring Y clicked: ");
                constraints = Vector3f.UNIT_Y;
                initiatePick(pickEvent, PLANE_XZ, GLOBAL);

            } else if (pickEvent.getContactObject().equals(ringZAxis)) {
                System.out.println("Ring clicked: ");
                constraints = Vector3f.UNIT_Z; // move only X
                initiatePick(pickEvent, PLANE_XY, GLOBAL);

            }

        } else {
            stopPick();

        }

    }

    public void initiatePick(PickEvent pickEvent, Quaternion planeRotation, int type) {
        if (target == null) {
            return;
            
        }
        startScale = target.getLocalScale().clone();
        selectedGizmo = pickEvent.getContactObject();
        downClickPos = selectedGizmo.getWorldTranslation().clone();
        mainGizmoPos = this.getLocalTranslation().clone();
        dragging = true;
        attachChild(draggingPlane);
        draggingPlane.setLocalRotation(planeRotation);
        attachChild(transformLineGeometry);

    }

    public void stopPick() {
        draggingPlane.removeFromParent();
        transformLineGeometry.removeFromParent();
        selectedGizmo = null;
        downClickPos = null;
        mainGizmoPos = null;
        dragging = false;
        startScale = null;
    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {

        if (pickEvent.isLeftButton() && dragging && pickEvent.getContactPoint() != null && target != null) {

//            float dis = pickEvent.getContactPoint().distance(downClickPos);
//            Debug.log("Distance = " + dis);
            Vector3f pos = pickEvent.getContactPoint().subtract(downClickPos);
            float angles[] = target.getWorldRotation().toAngles(null);

            if (selectedGizmo.equals(moveXAxisPos)) {
                target.setLocalTranslation(mainGizmoPos.x + pos.x, mainGizmoPos.y, mainGizmoPos.z);

            } else if (selectedGizmo.equals(moveYAxisPos)) {
                target.setLocalTranslation(mainGizmoPos.x, mainGizmoPos.y + pos.y, mainGizmoPos.z);

            } else if (selectedGizmo.equals(moveZAxisPos)) {
                target.setLocalTranslation(mainGizmoPos.x, mainGizmoPos.y, mainGizmoPos.z + pos.z);
                
            } else if (ctrlDown && (selectedGizmo.equals(scaleXAxisNeg) || selectedGizmo.equals(scaleYAxisNeg) || selectedGizmo.equals(scaleZAxisNeg))) {
                target.setLocalScale(startScale.x + pos.x, startScale.y + pos.x, startScale.z + pos.x);

            } else if (selectedGizmo.equals(scaleXAxisNeg)) {
                target.setLocalScale(startScale.x + pos.x, startScale.y, startScale.z);

            } else if (selectedGizmo.equals(scaleYAxisNeg)) {
                target.setLocalScale(startScale.x, startScale.y + pos.y, startScale.z);

            } else if (selectedGizmo.equals(scaleZAxisNeg)) {
                target.setLocalScale(startScale.x, startScale.y, startScale.z + pos.z);

            } else if (selectedGizmo.equals(ringXAxis)) {
                float AngleRad = FastMath.HALF_PI + FastMath.atan2(downClickPos.z - pickEvent.getContactPoint().z, downClickPos.y - pickEvent.getContactPoint().y);
                System.out.println("Rotate on X angle: " + AngleRad);
                SpatialUtils.rotateTo(target, AngleRad * FastMath.RAD_TO_DEG, angles[1] * FastMath.RAD_TO_DEG, angles[2] * FastMath.RAD_TO_DEG);

            } else if (selectedGizmo.equals(ringYAxis)) {
                float AngleRad = FastMath.HALF_PI + FastMath.atan2(downClickPos.x - pickEvent.getContactPoint().x, downClickPos.z - pickEvent.getContactPoint().z);
                System.out.println("Rotate on angle: " + AngleRad);
                SpatialUtils.rotateTo(target, angles[0] * FastMath.RAD_TO_DEG, AngleRad * FastMath.RAD_TO_DEG, angles[2] * FastMath.RAD_TO_DEG);

            } else if (selectedGizmo.equals(ringZAxis)) {
                float AngleRad = FastMath.PI + FastMath.atan2(downClickPos.y - pickEvent.getContactPoint().y, downClickPos.x - pickEvent.getContactPoint().x);
                System.out.println("Rotate on Z angle: " + AngleRad);
                SpatialUtils.rotateTo(target, angles[0] * FastMath.RAD_TO_DEG, angles[1] * FastMath.RAD_TO_DEG, AngleRad * FastMath.RAD_TO_DEG);

            }

            this.setLocalTranslation(target.getLocalTranslation().clone());

            gizmoListener.gizmoUpdate(getWorldTranslation(), getWorldRotation(), getWorldScale());
        }

    }

    public void setGizmoListener(GizmoListener gizmoListener) {
        this.gizmoListener = gizmoListener;

    }

}
