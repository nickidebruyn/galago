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
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Settings;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickidebruyn
 */
public class Dyn4jAppState extends AbstractAppState {

  private static final long TIME_STEP_IN_MICROSECONDS = (long) (Settings.DEFAULT_STEP_FREQUENCY * 1000);
  /**
   * See {@link Application} for details.
   */
  protected Application app = null;
  protected AppStateManager stateManager = null;
  protected Integer initialCapacity;

  protected Integer initialJointCapacity;
  protected Bounds bounds = null;
  protected PhysicsSpace physicsSpace = null;
  protected float tpf = 0;
  protected float tpfSum = 0;
  // MultiTreading Fields
  protected ThreadingType threadingType = null;
  protected ScheduledThreadPoolExecutor executor;
  /**
   * (non-javadoc)
   */
  private final Runnable parallelPhysicsUpdate = () -> {
    if (!isEnabled()) {
      return;
    }

    Dyn4jAppState.this.physicsSpace.updateFixed(Dyn4jAppState.this.tpfSum);
    Dyn4jAppState.this.tpfSum = 0;
  };

  /**
   * Instantiates a new <code>Dyn4jAppState</code> object with the default values
   */
  public Dyn4jAppState() {
    this(null, null, ThreadingType.PARALLEL);
  }

  /**
   * Instantiates a new <code>Dyn4jAppState</code> object with the default values but specified bounds
   *
   * @param bounds The limit of the world.
   */
  public Dyn4jAppState(final Bounds bounds) {
    this(null, null, bounds, ThreadingType.PARALLEL);
  }

  /**
   * Instantiates a new <code>Dyn4jAppState</code> object with the default values but specified capacities
   *
   * @param initialCapacity      initial capacity (bodies in the world).
   * @param initialJointCapacity initial capacity of the joints.
   */
  public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity) {
    this(initialCapacity, initialJointCapacity, null, ThreadingType.PARALLEL);
  }

  /**
   * Instantiates a new <code>Dyn4jAppState</code> object with the specified capacities and bounds
   *
   * @param initialCapacity      initial capacity (bodies in the world).
   * @param initialJointCapacity initial capacity of the joints.
   * @param bounds               The limit of the world.
   */
  public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity, final Bounds bounds) {
    this(initialCapacity, initialJointCapacity, bounds, ThreadingType.PARALLEL);
  }

  /**
   * Instantiate a new <code>Dyn4jAppState</code> object and initialize it with the default values.
   *
   * @param threadingType ThreadingType
   */
  public Dyn4jAppState(final ThreadingType threadingType) {
    this(null, null, threadingType);
  }

  /**
   * Instantiate a new <code>Dyn4jAppState</code> object and initialize it with the default values.
   *
   * @param bounds        The limit of the world.
   * @param threadingType ThreadingType
   */
  public Dyn4jAppState(final Bounds bounds, final ThreadingType threadingType) {
    this(null, null, bounds, threadingType);
  }

  /**
   * Instantiate a new <code>Dyn4jAppState</code> object and initialize it with the default values.
   *
   * @param initialCapacity      initial capacity (bodies in the world).
   * @param initialJointCapacity initial capacity of the joints.
   * @param threadingType        ThreadingType
   */
  public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity, final ThreadingType threadingType) {
    this(initialCapacity, initialJointCapacity, null, threadingType);
  }

  /**
   * Instantiate a new <code>Dyn4jAppState</code> object and initialize it with the values specified.
   *
   * @param initialCapacity      initial capacity (bodies in the world).
   * @param initialJointCapacity initial capacity of the joints.
   * @param bounds               The limit of the world.
   * @param threadingType        ThreadingType
   */
  public Dyn4jAppState(final Integer initialCapacity, final Integer initialJointCapacity, final Bounds bounds, final ThreadingType threadingType) {
    this.threadingType = threadingType;
    this.initialCapacity = initialCapacity;
    this.initialJointCapacity = initialJointCapacity;
    this.bounds = bounds;
  }

  @Override
  public void initialize(final AppStateManager stateManager, final Application app) {
    this.app = app;
    this.stateManager = stateManager;

    // Start physic related objects.
    startPhysics();

    super.initialize(stateManager, app);
  }

  private void startPhysics() {
    if (this.initialized) {
      return;
    }

    if (this.threadingType == ThreadingType.PARALLEL) {
      startPhysicsOnExecutor();
    } else {
      this.physicsSpace = new PhysicsSpace(initialCapacity, initialJointCapacity, bounds);
    }

    this.initialized = true;
  }

  /**
   * Initializes the physics so that it runs in parallel with the engine
   */
  private void startPhysicsOnExecutor() {
    if (this.executor != null) {
      this.executor.shutdown();
    }
    this.executor = new ScheduledThreadPoolExecutor(1);

    final Callable<Boolean> call = () -> {
      Dyn4jAppState.this.physicsSpace = new PhysicsSpace(Dyn4jAppState.this.initialCapacity, Dyn4jAppState.this.initialJointCapacity,
              Dyn4jAppState.this.bounds);
      return true;
    };

    try {
      this.executor.submit(call).get();
    } catch (final InterruptedException | ExecutionException ex) {
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

    } else {

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
}
