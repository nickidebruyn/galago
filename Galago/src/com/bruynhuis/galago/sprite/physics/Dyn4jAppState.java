/*
 * Copyright (c) 2009-2014 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.bruynhuis.galago.sprite.physics;

import com.bruynhuis.galago.spatial.CenteredQuad;
import com.bruynhuis.galago.spatial.JmeMesh;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Capacity;
import org.dyn4j.dynamics.Settings;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.material.Materials;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author nickidebruyn
 */
public class Dyn4jAppState extends AbstractAppState implements PhysicsSpaceListener {

    private static final long TIME_STEP_IN_MICROSECONDS = (long) (Settings.DEFAULT_STEP_FREQUENCY * 1000);
    /**
     * See {@link Application} for details.
     */
    protected Application app = null;
    protected AppStateManager stateManager = null;
    protected Capacity initialCapacity = null;
    protected Bounds bounds = null;
    protected PhysicsSpace physicsSpace = null;
    protected float tpf = 0;
    protected float tpfSum = 0;
    // MultiTreading Fields
    protected ThreadingType threadingType = null;
    protected ScheduledThreadPoolExecutor executor;
    private boolean debugEnabled = false;
    private Material debugShapeMaterial;
    private final Map<Convex, Geometry> debugShapes = new HashMap<>();
    private final Node debugNode = new Node("Dyn4J Debug Node");

    private final Runnable parallelPhysicsUpdate = new Runnable() {
        @Override
        public void run() {
            if (!isEnabled()) {
                return;
            }

            Dyn4jAppState.this.physicsSpace.updateFixed(Dyn4jAppState.this.tpfSum);
            Dyn4jAppState.this.tpfSum = 0;
        }
    };

    public Dyn4jAppState() {
        this(null, null, ThreadingType.PARALLEL);
    }

    public Dyn4jAppState(final Bounds bounds) {
        this(null, bounds, ThreadingType.PARALLEL);
    }

    public Dyn4jAppState(final Capacity initialCapacity) {
        this(initialCapacity, null, ThreadingType.PARALLEL);
    }

    public Dyn4jAppState(final Capacity initialCapacity, final Bounds bounds) {
        this(initialCapacity, bounds, ThreadingType.PARALLEL);
    }

    public Dyn4jAppState(final ThreadingType threadingType) {
        this(null, null, threadingType);
    }

    public Dyn4jAppState(final Bounds bounds, final ThreadingType threadingType) {
        this(null, bounds, threadingType);
    }

    public Dyn4jAppState(final Capacity initialCapacity, final ThreadingType threadingType) {
        this(initialCapacity, null, threadingType);
    }

    public Dyn4jAppState(final Capacity initialCapacity, final Bounds bounds, final ThreadingType threadingType) {
        this.threadingType = threadingType;
        this.initialCapacity = initialCapacity;
        this.bounds = bounds;
    }

    @Override
    public void initialize(final AppStateManager stateManager, final Application app) {
        this.app = app;
        this.stateManager = stateManager;

        // Start physic related objects.
        startPhysics();

        if (debugShapeMaterial == null) {
            debugShapeMaterial = new Material(app.getAssetManager(), Materials.UNSHADED);
            debugShapeMaterial.getAdditionalRenderState().setWireframe(true);
        }

        super.initialize(stateManager, app);
    }

    private void startPhysics() {
        if (this.initialized) {
            return;
        }

        if (this.threadingType == ThreadingType.PARALLEL) {
            startPhysicsOnExecutor();
        } else {
            this.physicsSpace = new PhysicsSpace(this.initialCapacity, this.bounds);
            physicsSpace.addPhysicsSpaceListener(this);
        }

        this.initialized = true;
    }

    private void startPhysicsOnExecutor() {
        if (this.executor != null) {
            this.executor.shutdown();
        }
        this.executor = new ScheduledThreadPoolExecutor(1);

        final Callable<Boolean> call = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Dyn4jAppState.this.physicsSpace = new PhysicsSpace(Dyn4jAppState.this.initialCapacity,
                        Dyn4jAppState.this.bounds);

                //NB This is going to be called on another thread....
                physicsSpace.addPhysicsSpaceListener(Dyn4jAppState.this);

                return true;
            }
        };

        try {
            this.executor.submit(call).get();
        } catch (final Exception ex) {
            Logger.getLogger(Dyn4jAppState.class.getName()).log(Level.SEVERE, null, ex);
        }

        schedulePhysicsCalculationTask();
    }

    private void schedulePhysicsCalculationTask() {
        if (this.executor != null) {
            this.executor.scheduleAtFixedRate(this.parallelPhysicsUpdate, 0l, TIME_STEP_IN_MICROSECONDS,
                    TimeUnit.MICROSECONDS);
        }
    }

    // FIXME Remove this method but check how to implement tests
    @Override
    public void stateAttached(final AppStateManager stateManager) {
        // Start physic related objects if appState is not initialized.
        if (!this.initialized) {
            startPhysics();
        }

        super.stateAttached(stateManager);
    }

    /**
     * See {@link AppStateManager#update(float)}. Note: update method is not
     * called if enabled = false.
     */
    @Override
    public void update(final float tpf) {
        if (!isEnabled()) {
            return;
        }

        this.tpf = tpf;
        this.tpfSum += tpf;

        if (debugEnabled) {
            renderDebugs();
        }

    }

    /**
     * See {@link AppStateManager#render(RenderManager)}. Note: render method is
     * not called if enabled = false.
     */
    @Override
    public void render(final RenderManager rm) {

        if (threadingType == ThreadingType.PARALLEL) {
            executor.submit(parallelPhysicsUpdate);

        } else if (threadingType == ThreadingType.SEQUENTIAL) {
            final float timeStep = isEnabled() ? this.tpf * this.physicsSpace.getSpeed() : 0;
            this.physicsSpace.updateFixed(timeStep);

        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (enabled) {
            schedulePhysicsCalculationTask();

        } else if (this.executor != null) {
            this.executor.remove(this.parallelPhysicsUpdate);
        }
        super.setEnabled(enabled);
    }

    @Override
    public void cleanup() {
        if (this.executor != null) {
            this.executor.shutdown();
            this.executor = null;
        }

        this.physicsSpace.clear();

        super.cleanup();
    }

    public PhysicsSpace getPhysicsSpace() {
        return this.physicsSpace;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;

        if (enabled) {

            // first add all the ones that don't exist yet (query the world)
            // then rely on the add/remove to mediate the visuals.
            List<Body> bodies = physicsSpace.getPhysicsWorld().getBodies();

            for (Body body : bodies) {

                List<BodyFixture> fixtures = body.getFixtures();

                for (BodyFixture fixture : fixtures) {

                    Convex shape = fixture.getShape();

                    Geometry geometry = debugShapes.get(shape);

                    if (geometry == null) {

                        if (shape instanceof Rectangle) {

                            Rectangle rectangle = (Rectangle) shape;

                            Mesh mesh = new Quad((float) rectangle.getWidth(), (float) rectangle.getHeight());
                            geometry = new Geometry("Box Collision Shape", mesh);
                            geometry.setMaterial(debugShapeMaterial);
                            debugShapes.put(shape, geometry);
                            debugNode.attachChild(geometry);

                        }
                    }

                    if (geometry != null) {
                        geometry.setLocalTranslation(
                                (float) shape.getCenter().x,
                                (float) shape.getCenter().y,
                                0.1f);
                    }

                }

            }

        } else {
            debugShapes.clear();
            debugNode.detachAllChildren();
            debugNode.removeFromParent();

            debugShapeMaterial = null;
        }
    }

    private void renderDebugs() {

        if (debugNode.getParent() == null) {
            ((SimpleApplication) app).getRootNode().attachChild(debugNode);
        }

        List<Body> bodies = physicsSpace.getPhysicsWorld().getBodies();

        for (Body body : bodies) {

            List<BodyFixture> fixtures = body.getFixtures();

            for (BodyFixture fixture : fixtures) {

                Convex shape = fixture.getShape();

                Geometry geometry = debugShapes.get(shape);

                if (geometry != null) {

                    geometry.setLocalTranslation(
                            (float) body.getTransform().getTranslation().x,
                            (float) body.getTransform().getTranslation().y,
                            0.001f // juuuust slightly in front of the terrain.
                    );

                    // one-dimensional rotation in 2D
                    Quaternion quaternion = new Quaternion()
                            .fromAngles(
                                    0,
                                    0,
                                    (float) body.getTransform().getRotation());

                    geometry.setLocalRotation(quaternion);

                }
            }
        }

    }

    @Override
    public void bodyAdded(Body body) {

        List<BodyFixture> fixtures = body.getFixtures();

        for (BodyFixture fixture : fixtures) {

            Convex shape = fixture.getShape();

            Geometry geometry = debugShapes.get(shape);

            if (geometry == null) {

                // shapes are created from the center in Box2D.
                // JME Quads are not, and probably some other stuff. Just be aware.
                if (shape instanceof Rectangle) {

                    Rectangle rectangle = (Rectangle) shape;

//                    float halfWidth = (float) rectangle.getWidth() * 0.5f;
//                    float halfHeight = (float) rectangle.getHeight() * 0.5f;
//
//                    float z = 0.001f; // juuust slightly in front of 0
//
//                    JmeMesh mesh = new JmeMesh();
//
//                    Vector3f[] verts = { // bl, br, tl, tr
//                            new Vector3f(-halfWidth, -halfHeight, z),
//                            new Vector3f(halfWidth, -halfHeight, z),
//                            new Vector3f(-halfWidth, halfHeight, z),
//                            new Vector3f(halfWidth, halfHeight, z)
//                    };
//
//                    mesh.set(VertexBuffer.Type.Position, verts);
                    Mesh mesh = new CenteredQuad((float) rectangle.getWidth(), (float) rectangle.getHeight());

                    geometry = new Geometry("Box Collision Shape", mesh);
                    geometry.setMaterial(debugShapeMaterial);
                    debugShapes.put(shape, geometry);
                    debugNode.attachChild(geometry);

                } else if (shape instanceof Polygon) {

                    Polygon polygon = (Polygon) shape;

                    Vector2[] vertices = polygon.getVertices();
                    Vector3f[] meshVerts = new Vector3f[vertices.length];

                    for (int i = 0; i < meshVerts.length; i++) {
                        meshVerts[i] = new Vector3f((float) vertices[i].x, (float) vertices[i].y, 0.01f);
                    }

                    Integer[] indices = {0, 1, 2, 0, 2, 3};

                    JmeMesh mesh = new JmeMesh();
                    mesh.set(VertexBuffer.Type.Position, meshVerts);
                    mesh.set(VertexBuffer.Type.Index, indices);

                    geometry = new Geometry("Polygon Shape", mesh);
                    geometry.setMaterial(debugShapeMaterial);

                    geometry.setLocalTranslation(
                            (float) body.getTransform().getTranslationX(),
                            (float) body.getTransform().getTranslationY(),
                            0);

                    debugShapes.put(shape, geometry);
                    debugNode.attachChild(geometry);

                }

            }

        }
    }

    @Override
    public void bodyRemoved(Body body) {
        List<BodyFixture> fixtures = body.getFixtures();

        for (BodyFixture fixture : fixtures) {

            Convex shape = fixture.getShape();

            Geometry geometry = debugShapes.get(shape);

            if (geometry != null) {
                debugShapes.remove(shape);
                geometry.removeFromParent();
            }

        }
    }

}
