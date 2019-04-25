package com.galago.tests;

import com.bruynhuis.galago.emitter.Emitter;
import com.bruynhuis.galago.emitter.EmitterMesh;
import com.bruynhuis.galago.emitter.Interpolation;
import com.bruynhuis.galago.emitter.influencers.AlphaInfluencer;
import com.bruynhuis.galago.emitter.influencers.ColorInfluencer;
import com.bruynhuis.galago.emitter.influencers.DestinationInfluencer;
import com.bruynhuis.galago.emitter.influencers.GravityInfluencer;
import com.bruynhuis.galago.emitter.influencers.RotationInfluencer;
import com.bruynhuis.galago.emitter.influencers.SizeInfluencer;
import com.bruynhuis.galago.emitter.influencers.SpriteInfluencer;
import com.jme3.animation.AnimChannel;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;

/**
 * test
 *
 * @author normenhansen
 */
public class AlphaTest01 extends SimpleApplication implements ActionListener {

    VideoRecorderAppState vrAppState;
    Sphere s;
    Emitter effectring;
    Emitter e1, e2, e3, e4;
    Node ring;
    AnimChannel c;

    public static void main(String[] args) {
        AlphaTest01 app = new AlphaTest01();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        vrAppState = new VideoRecorderAppState();
        vrAppState.setQuality(0.35f);

        this.setPauseOnLostFocus(true);

        flyCam.setDragToRotate(true);
        flyCam.setMoveSpeed(15f);
        inputManager.setCursorVisible(true);

        e1 = new Emitter();
        e1.setName("e1");
        e1.setMaxParticles(6);
        e1.addInfluencers(
                new GravityInfluencer(),
                new ColorInfluencer(),
                new AlphaInfluencer(),
                new SizeInfluencer(),
                new RotationInfluencer()
        );
        e1.setShapeSimpleEmitter();
        //	e1.setDirectionType(DirectionType.Normal);
        //	e1.initParticles(ParticleDataTriMesh.class, null);
        e1.setEmitterTestMode(false, false);
        e1.setSprite("Textures/halo.png");
        e1.setEmissionsPerSecond(2);
        e1.setParticlesPerEmission(1);
        e1.setForce(0.01f);
        e1.setLifeMinMax(1.25f, 1.75f);
        e1.setBillboardMode(Emitter.BillboardMode.Velocity);
        e1.setUseRandomEmissionPoint(true);
        e1.setUseVelocityStretching(false);

        e1.getInfluencer(GravityInfluencer.class).setGravity(new Vector3f(0f, 0f, 0f));

        e1.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(1f, 0.75f, 0.25f, 1f));
        e1.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(1f, 1f, 0.75f, 1f));
        e1.getInfluencer(ColorInfluencer.class).setEnabled(true);

        e1.getInfluencer(AlphaInfluencer.class).addAlpha(0, Interpolation.exp5Out);
        e1.getInfluencer(AlphaInfluencer.class).addAlpha(2.8f, Interpolation.exp5In);
        e1.getInfluencer(AlphaInfluencer.class).addAlpha(0, Interpolation.exp5In);

        e1.getInfluencer(SizeInfluencer.class).addSize(new Vector3f(.05f, .05f, .05f));
        e1.getInfluencer(SizeInfluencer.class).addSize(new Vector3f(4f, 4f, .4f));
        e1.getInfluencer(SizeInfluencer.class).setEnabled(true);

        e1.getInfluencer(RotationInfluencer.class).setUseRandomStartRotation(false, false, true);
        e1.getInfluencer(RotationInfluencer.class).addRotationSpeed(new Vector3f(0, 0, 0.25f));
        e1.getInfluencer(RotationInfluencer.class).setUseRandomDirection(true);
        e1.setLocalScale(0.5f);

        e1.setLocalRotation(e1.getLocalRotation().fromAngleAxis(90 * FastMath.DEG_TO_RAD, Vector3f.UNIT_X));
        e1.setLocalTranslation(0, 0.01f, 0);
        e1.setLocalScale(0.05f);

        e1.initialize(assetManager);

        //	rootNode.addControl(e1);
        //	e1.setEnabled(true);        
        Mesh cylmesh = new Sphere(10, 10, 1);

        e2 = new Emitter();
        e2.setName("e2");
        e2.setMaxParticles(130);
        e2.addInfluencers(
                new ColorInfluencer(),
                new SizeInfluencer(),
                new DestinationInfluencer(),
                new SpriteInfluencer()
        );
        e2.setShape(cylmesh);
        e2.setDirectionType(EmitterMesh.DirectionType.Normal);

        //	e2.initParticles(ParticleDataTriMesh.class, null);
        e2.setSprite("Textures/glow.png");
        e2.setEmissionsPerSecond(60);
        e2.setParticlesPerEmission(1);
        e2.setForceMinMax(1.2f, 2.2f);
        e2.setLifeMinMax(1.75f, 2.75f);
        e2.setBillboardMode(Emitter.BillboardMode.Velocity_Z_Up_Y_Left);
        e2.setEmitterTestMode(false, false);
        e2.setUseVelocityStretching(true);
        e2.setVelocityStretchFactor(1.45f);
        e2.setUseSequentialEmissionFace(true);

        e2.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(1f, 0f, 0f, 0.35f));
        e2.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(1f, 1f, 0f, 0.1f));

        e2.getInfluencer(SizeInfluencer.class).addSize(0f, Interpolation.exp10Out);
        e2.getInfluencer(SizeInfluencer.class).addSize(.4f, Interpolation.linear);
        e2.getInfluencer(SizeInfluencer.class).addSize(.075f, Interpolation.exp5In);
        e2.getInfluencer(SizeInfluencer.class).addSize(.025f);
        e2.getInfluencer(SizeInfluencer.class).setEnabled(true);

        e2.getInfluencer(DestinationInfluencer.class).addDestination(new Vector3f(0, -2f, 0), .1f, Interpolation.exp10In);
        e2.getInfluencer(DestinationInfluencer.class).addDestination(new Vector3f(0, 10f, 0), .1f, Interpolation.exp10Out);

        e2.getInfluencer(SpriteInfluencer.class).setUseRandomStartImage(true);
        e2.getInfluencer(SpriteInfluencer.class).setAnimate(false);

        e2.getParticleNode().getLocalRotation().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X);

        e2.setLocalTranslation(0, 0, 0);
        e2.setLocalScale(0.25f);

        e2.initialize(assetManager);

        //	rootNode.addControl(e2);
        //	e2.setEnabled(true);
        e3 = new Emitter();
        e3.setName("e3");
        e3.setMaxParticles(130);
        e3.addInfluencers(
                new ColorInfluencer(),
                new SizeInfluencer(),
                new DestinationInfluencer(),
                new SpriteInfluencer()
        );
        e3.setShape(cylmesh);
        e3.setDirectionType(EmitterMesh.DirectionType.Normal);

        //	e3.initParticles(ParticleDataTriMesh.class, null);
        e3.setSprite("Textures/smoke2x2.png", 2, 2);
        e3.setEmissionsPerSecond(60);
        e3.setParticlesPerEmission(1);
        e3.setForceMinMax(2.2f, 2.2f);
        e3.setLifeMinMax(1.75f, 1.75f);
        e3.setBillboardMode(Emitter.BillboardMode.Velocity_Z_Up_Y_Left);
        e3.setEmitterTestMode(false, false);
        e3.setUseVelocityStretching(true);
        e3.setVelocityStretchFactor(1.45f);

        e3.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(1f, 0.0f, 0f, 1f));
        e3.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(1f, 1f, 0f, 1f));

        e3.getInfluencer(SizeInfluencer.class).addSize(0f, Interpolation.exp10Out);
        e3.getInfluencer(SizeInfluencer.class).addSize(.4f, Interpolation.linear);
        e3.getInfluencer(SizeInfluencer.class).addSize(.075f, Interpolation.exp5In);
        e3.getInfluencer(SizeInfluencer.class).addSize(.025f);
        e3.getInfluencer(SizeInfluencer.class).setEnabled(true);

        e3.getInfluencer(DestinationInfluencer.class).addDestination(new Vector3f(0, -2f, 0), .1f, Interpolation.exp10In);
        e3.getInfluencer(DestinationInfluencer.class).addDestination(new Vector3f(0, 10f, 0), .1f, Interpolation.exp10Out);

        e3.getInfluencer(SpriteInfluencer.class).setUseRandomStartImage(true);
        e3.getInfluencer(SpriteInfluencer.class).setAnimate(false);

        e3.getParticleNode().getLocalRotation().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X);

        e3.setLocalTranslation(0, 0, 0);
        e3.setLocalScale(0.25f);

        e3.initialize(assetManager);

        //	rootNode.addControl(e3);
        //	e3.setEnabled(true);
        e4 = new Emitter();
        e4.setName("e4");
        e4.setMaxParticles(130);
        e4.addInfluencers(
                new ColorInfluencer(),
                new AlphaInfluencer(),
                new SizeInfluencer(),
                new SpriteInfluencer()
        );
        e4.setShape(cylmesh);
        e4.setDirectionType(EmitterMesh.DirectionType.Normal);

        //	e4.initParticles(ParticleDataTriMesh.class, null);
        e4.setSprite("Textures/bolts3x3.png", 3, 3);
        e4.setEmissionsPerSecond(7);
        e4.setParticlesPerEmission(3);
        e4.setForceMinMax(0.001f, 0.001f);
        e4.setLifeMinMax(1f, 1f);
        e4.setBillboardMode(Emitter.BillboardMode.Velocity_Z_Up_Y_Left);
        e4.setEmitterTestMode(false, false);
        e4.setUseVelocityStretching(false);
        e4.setVelocityStretchFactor(1.45f);

        e4.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(0.5f, 0.5f, 1f, 1f));
        e4.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(0.5f, 0.5f, 1f, 1f));

        e4.getInfluencer(SizeInfluencer.class).addSize(new Vector3f(0.25f, 1, 0f));
        e4.getInfluencer(SizeInfluencer.class).setEnabled(false);

        e4.getInfluencer(AlphaInfluencer.class).addAlpha(0, Interpolation.exp10Out);
        e4.getInfluencer(AlphaInfluencer.class).addAlpha(1, Interpolation.exp5In);
        e4.getInfluencer(AlphaInfluencer.class).addAlpha(0, Interpolation.linear);

        e4.getInfluencer(SpriteInfluencer.class).setUseRandomStartImage(true);
        e4.getInfluencer(SpriteInfluencer.class).setAnimate(true);
        e4.getInfluencer(SpriteInfluencer.class).setFixedDuration(1f / 9f);

        e4.getParticleNode().getLocalRotation().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X);

        e4.setLocalTranslation(0, 0, 0);
        e4.setLocalScale(0.75f);

        e4.initialize(assetManager);
        
                    rootNode.addControl(e1);
                    rootNode.addControl(e2);
                    rootNode.addControl(e3);
                    rootNode.addControl(e4);
                    e1.setEnabled(true);
                    e2.setEnabled(true);
                    e3.setEnabled(true);
                    e4.setEnabled(true);

//        ButtonAdapter b = new ButtonAdapter(screen, new Vector2f(0, 0)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
//                if (rootNode.getControl(Emitter.class) == null) {
//                    rootNode.addControl(e1);
//                    rootNode.addControl(e2);
//                    rootNode.addControl(e3);
//                    rootNode.addControl(e4);
//                    e1.setEnabled(true);
//                    e2.setEnabled(true);
//                    e3.setEnabled(true);
//                    e4.setEnabled(true);
//                    setText("Remove");
//                } else {
//                    e1.setEnabled(false);
//                    e2.setEnabled(false);
//                    e3.setEnabled(false);
//                    e4.setEnabled(false);
//                    rootNode.removeControl(e1);
//                    rootNode.removeControl(e2);
//                    rootNode.removeControl(e3);
//                    rootNode.removeControl(e4);
//                    e1.killAllParticles();
//                    e2.killAllParticles();
//                    e3.killAllParticles();
//                    e4.killAllParticles();
//                    setText("Add");
//                }
//            }
//        };
//        b.setText("Add");
//        screen.addElement(b);
//
//        ButtonAdapter b2 = new ButtonAdapter(screen, new Vector2f(0, b.getHeight())) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
//                if (!e1.isEnabled()) {
//                    e1.setEnabled(true);
//                    e2.setEnabled(true);
//                    e3.setEnabled(true);
//                    e4.setEnabled(true);
//                    setText("Pause");
//                } else {
//                    e1.setEnabled(false);
//                    e2.setEnabled(false);
//                    e3.setEnabled(false);
//                    e4.setEnabled(false);
//                    setText("Resume");
//                }
//            }
//        };
//        b2.setText("Pause");
//        screen.addElement(b2);
    }

    private void setupKeys() {
        inputManager.addMapping("F9", new KeyTrigger(KeyInput.KEY_F9));
        inputManager.addListener(this, "F9");
    }

    float rot = 0;
    boolean dir = true;

    @Override
    public void simpleUpdate(float tpf) {

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("F9")) {
            if (!isPressed) {
                if (stateManager.hasState(vrAppState)) {
                    System.out.println("Stopping video recorder");
                    stateManager.detach(vrAppState);
                } else {
                    System.out.println("Starting video recorder");
                    stateManager.attach(vrAppState);
                }
            }
        }
    }
}
