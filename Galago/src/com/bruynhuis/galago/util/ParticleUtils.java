/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.util;

import com.bruynhuis.galago.emitter.Emitter;
import com.bruynhuis.galago.emitter.EmitterMesh;
import com.bruynhuis.galago.emitter.Interpolation;
import com.bruynhuis.galago.emitter.influencers.AlphaInfluencer;
import com.bruynhuis.galago.emitter.influencers.ColorInfluencer;
import com.bruynhuis.galago.emitter.influencers.DestinationInfluencer;
import com.bruynhuis.galago.emitter.influencers.GravityInfluencer;
import com.bruynhuis.galago.emitter.influencers.ImpulseInfluencer;
import com.bruynhuis.galago.emitter.influencers.RotationInfluencer;
import com.bruynhuis.galago.emitter.influencers.SizeInfluencer;
import com.bruynhuis.galago.emitter.influencers.SpriteInfluencer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class ParticleUtils {

    public static Spatial addFire(Node parent, Mesh mesh) {
        
        Node particleNode = new Node("fire-particle-node");
        parent.attachChild(particleNode);

//        Node campfire = (Node) assetManager.loadModel("Models/Campfire.j3o");
//        Mesh campfiremesh = ((Geometry) campfire.getChild(0)).getMesh();
        Emitter e1 = new Emitter();
        e1.setName("e1");
        e1.setMaxParticles(100);
        e1.addInfluencers(
                new GravityInfluencer(),
                new ColorInfluencer(),
                new AlphaInfluencer(),
                new SizeInfluencer(),
                new RotationInfluencer(),
                new SpriteInfluencer(),
                new ImpulseInfluencer(),
                new DestinationInfluencer()
        );
        e1.setShape(mesh);
        //	e1.initParticles(ParticleDataTriMesh.class, null);
        e1.setSprite("Resources/textures/fire2x2.png", 2, 2);

        e1.setBillboardMode(Emitter.BillboardMode.Camera);
        e1.setForceMinMax(2.1f, 3.1f);
        e1.setLifeMinMax(0.45f, 1f);
        e1.setEmissionsPerSecond(100);
        e1.setParticlesPerEmission(1);
        e1.setEmitterTestMode(false, false);
        e1.setUseRandomEmissionPoint(true);
        e1.setUseVelocityStretching(false);
        //	e1.setVelocityStretchFactor(0.2f);

        e1.getInfluencer(GravityInfluencer.class).setGravity(0, -2, 0);

        e1.getInfluencer(DestinationInfluencer.class).removeAll();
        e1.getInfluencer(DestinationInfluencer.class).addDestination(new Vector3f(0, 4, 0), .52f, Interpolation.exp5Out);
        e1.getInfluencer(DestinationInfluencer.class).addDestination(new Vector3f(0, 8, 0), .42f, Interpolation.exp5Out);
        //	e1.getInfluencer(DestinationInfluencer.class).setUseInterpolation(true);

        e1.getInfluencer(AlphaInfluencer.class).addAlpha(.15f, Interpolation.exp10Out);
        e1.getInfluencer(AlphaInfluencer.class).addAlpha(.6f);
        e1.getInfluencer(AlphaInfluencer.class).addAlpha(.3f, Interpolation.exp5In);
        e1.getInfluencer(AlphaInfluencer.class).addAlpha(0f);

        e1.getInfluencer(SizeInfluencer.class).addSize(.3f, Interpolation.exp10Out);
        e1.getInfluencer(SizeInfluencer.class).addSize(.5f);
        e1.getInfluencer(SizeInfluencer.class).addSize(new Vector3f(.1f, .1f, 1f), Interpolation.exp5In);
        e1.getInfluencer(SizeInfluencer.class).addSize(0f);

        e1.getInfluencer(ColorInfluencer.class).addColor(ColorRGBA.Orange);
        e1.getInfluencer(ColorInfluencer.class).addColor(ColorRGBA.Yellow);
        e1.getInfluencer(ColorInfluencer.class).addColor(ColorRGBA.Orange);
        e1.getInfluencer(ColorInfluencer.class).addColor(ColorRGBA.White);

        e1.getInfluencer(RotationInfluencer.class).addRotationSpeed(new Vector3f(0, 0, 0));
        e1.getInfluencer(RotationInfluencer.class).setUseRandomStartRotation(false, false, true);
        e1.getInfluencer(RotationInfluencer.class).setUseRandomDirection(true);
        e1.getInfluencer(RotationInfluencer.class).setUseRandomSpeed(true);

        e1.getInfluencer(SpriteInfluencer.class).setUseRandomStartImage(true);
        e1.getInfluencer(SpriteInfluencer.class).setAnimate(true);
        e1.getInfluencer(SpriteInfluencer.class).setFixedDuration(1f / 8f);

        e1.setLocalScale(.35f);
        e1.initialize(SharedSystem.getInstance().getBaseApplication().getAssetManager());
        particleNode.addControl(e1);
        e1.setEnabled(true);

        Emitter e1a = new Emitter();
        e1a.setName("e1a");
        e1a.setMaxParticles(75);
        e1a.addInfluencers(
                new GravityInfluencer(),
                new ColorInfluencer(),
                new AlphaInfluencer(),
                new SizeInfluencer(),
                new RotationInfluencer(),
                new SpriteInfluencer(),
                new ImpulseInfluencer(),
                new DestinationInfluencer()
        );
        e1a.setShape(mesh);
        //	e1a.initParticles(ParticleDataTriMesh.class, null);
        e1a.setSprite("Resources/textures/flame4x4.jpg", 4, 4);

        e1a.setBillboardMode(Emitter.BillboardMode.Velocity_Z_Up);
        e1a.setForceMinMax(3.1f, 4.1f);
        e1a.setLifeMinMax(0.45f, 1f);
        e1a.setEmissionsPerSecond(75);
        e1a.setParticlesPerEmission(1);
        e1a.setEmitterTestMode(false, false);
        e1a.setUseRandomEmissionPoint(true);
        e1a.setUseVelocityStretching(true);
        e1a.setVelocityStretchFactor(0.275f);

        e1a.getInfluencer(GravityInfluencer.class).setGravity(0, -1.6f, 0);

        e1a.getInfluencer(DestinationInfluencer.class).removeAll();
        e1a.getInfluencer(DestinationInfluencer.class).addDestination(new Vector3f(0, 4, 0), .42f, Interpolation.exp5Out);
        e1a.getInfluencer(DestinationInfluencer.class).addDestination(new Vector3f(0, 8, 0), .32f, Interpolation.exp5Out);
        //	e1a.getInfluencer(DestinationInfluencer.class).setUseInterpolation(true);

        e1a.getInfluencer(AlphaInfluencer.class).addAlpha(.15f, Interpolation.exp10Out);
        e1a.getInfluencer(AlphaInfluencer.class).addAlpha(.4f);
        e1a.getInfluencer(AlphaInfluencer.class).addAlpha(.3f, Interpolation.exp5In);
        e1a.getInfluencer(AlphaInfluencer.class).addAlpha(0f);

        e1a.getInfluencer(SizeInfluencer.class).addSize(.0f, Interpolation.exp10Out);
        e1a.getInfluencer(SizeInfluencer.class).addSize(.5f);
        e1a.getInfluencer(SizeInfluencer.class).addSize(new Vector3f(.4f, .4f, 1f), Interpolation.exp5In);
        e1a.getInfluencer(SizeInfluencer.class).addSize(0f);

        e1a.getInfluencer(ColorInfluencer.class).addColor(ColorRGBA.Orange);
        e1a.getInfluencer(ColorInfluencer.class).addColor(ColorRGBA.Yellow);
        e1a.getInfluencer(ColorInfluencer.class).addColor(ColorRGBA.Orange);
        e1a.getInfluencer(ColorInfluencer.class).addColor(ColorRGBA.White);

        e1a.getInfluencer(RotationInfluencer.class).addRotationSpeed(new Vector3f(0, 0, 0));
        e1a.getInfluencer(RotationInfluencer.class).setUseRandomStartRotation(false, false, true);
        e1a.getInfluencer(RotationInfluencer.class).setUseRandomDirection(true);
        e1a.getInfluencer(RotationInfluencer.class).setUseRandomSpeed(true);

        e1a.getInfluencer(SpriteInfluencer.class).setUseRandomStartImage(true);
        e1a.getInfluencer(SpriteInfluencer.class).setAnimate(true);
        e1a.getInfluencer(SpriteInfluencer.class).setFixedDuration(1f / 8f);

        e1a.initialize(SharedSystem.getInstance().getBaseApplication().getAssetManager());
        e1a.setLocalScale(.135f);
        particleNode.addControl(e1a);
        e1a.setEnabled(true);

        Emitter e2 = new Emitter();
        e2.setName("e2");
        e2.setMaxParticles(400);
        e2.addInfluencers(
                new GravityInfluencer(),
                new ColorInfluencer(),
                new AlphaInfluencer(),
                new SizeInfluencer(),
                new RotationInfluencer(),
                new SpriteInfluencer(),
                new ImpulseInfluencer()
        );
        e2.setShapeSimpleEmitter();
        e2.setDirectionType(EmitterMesh.DirectionType.RandomNormalAligned);
        //	e2.initParticles(ParticleDataTriMesh.class, null);
        e2.setSprite("Resources/textures/fire2x4.png", 4, 2);

        e2.setBillboardMode(Emitter.BillboardMode.Camera);
        e2.setForceMinMax(1.1f, 1.81f);
        e2.setLifeMinMax(2.f, 2.5f);
        e2.setEmissionsPerSecond(100);
        e2.setParticlesPerEmission(1);
        e2.setEmitterTestMode(false, false);
        e2.setUseRandomEmissionPoint(true);
        e2.setUseVelocityStretching(false);
        //	e2.setVelocityStretchFactor(0.02f);

        e2.getInfluencer(GravityInfluencer.class).setGravity(0, -16, 0);

        e2.getInfluencer(AlphaInfluencer.class).addAlpha(.1f, Interpolation.exp10Out);
        e2.getInfluencer(AlphaInfluencer.class).addAlpha(.2f);
        e2.getInfluencer(AlphaInfluencer.class).addAlpha(.1f, Interpolation.exp5In);
        e2.getInfluencer(AlphaInfluencer.class).addAlpha(0f);

        e2.getInfluencer(SizeInfluencer.class).addSize(.6f, Interpolation.exp10Out);
        e2.getInfluencer(SizeInfluencer.class).addSize(1.8f);
        e2.getInfluencer(SizeInfluencer.class).addSize(new Vector3f(2.7f, 2.7f, 1f), Interpolation.exp5In);
        e2.getInfluencer(SizeInfluencer.class).addSize(.2f);

        e2.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(.4f, .4f, .4f, 0.2f));
        e2.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(.8f, .8f, .8f, 0.15f));
        e2.getInfluencer(ColorInfluencer.class).addColor(new ColorRGBA(.2f, .2f, .2f, 0.1f));

        e2.getInfluencer(RotationInfluencer.class).addRotationSpeed(new Vector3f(0, 0, 0));
        e2.getInfluencer(RotationInfluencer.class).setUseRandomStartRotation(false, false, true);
        e2.getInfluencer(RotationInfluencer.class).setUseRandomDirection(true);
        e2.getInfluencer(RotationInfluencer.class).setUseRandomSpeed(true);

        e2.getInfluencer(SpriteInfluencer.class).setUseRandomStartImage(true);
        e2.getInfluencer(SpriteInfluencer.class).setAnimate(false);
        //	e2.getInfluencer(SpriteInfluencer.class).setFixedDuration(1/8);

        e2.initialize(SharedSystem.getInstance().getBaseApplication().getAssetManager());
        e2.setLocalScale(.35f);
        particleNode.addControl(e2);
        e2.setEnabled(true);
        
        return particleNode;
    }

}
