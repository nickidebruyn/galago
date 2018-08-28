/**
 * Copyright (c) 2014, jMonkeyEngine All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.ai.agents.behaviors.npc.steering;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.steering.SteeringExceptions.NegativeValueException;

import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * With this class it will be possible to increase or decrease the steering
 * behavior force.
 * <br> <br>
 *
 * It can be changed the length of this vector itself, multiplying it by a
 * scalar or modifying the force on a especific axis (x, y, z) <br> <br>
 *
 * This class also allows you force the steering to stay within a plane.
 * <br><br>
 *
 * You need to call setupStrengthControl( ... ), otherwhise this class will work
 * the same as AstractSteeringBehavior.
 *
 * @see SteerStrengthType
 * @see AbstractSteeringBehavior
 *
 * @author Jesús Martín Berlanga
 * @author Tihomir Radosavljević
 * @version 2.1.1
 */
public abstract class AbstractStrengthSteeringBehavior extends AbstractSteeringBehavior {

    /**
     * Defines how the "strength" will be applied to a steer force.
     */
    private static enum SteerStrengthType {

        NO_STRENGTH,
        SCALAR,
        AXIS,
        PLANE
    }
    /**
     * Type of steer force that is applied to steering behavior.
     */
    private SteerStrengthType type = SteerStrengthType.NO_STRENGTH;
    /**
     * Used if the steer type is scalar.
     */
    private float scalar;
    /**
     * Used if the steer type is axis.
     */
    private float x, y, z;
    /**
     * Used if the steer type is plane.
     */
    private Plane plane;

    /**
     * @see
     * AbstractSteeringBehavior#AbstractSteeringBehavior(com.jme3.ai.agents.Agent)
     */
    public AbstractStrengthSteeringBehavior(Agent agent) {
        super(agent);
    }

    /**
     * @see
     * AbstractSteeringBehavior#AbstractSteeringBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Spatial)
     */
    public AbstractStrengthSteeringBehavior(Agent agent, Spatial spatial) {
        super(agent, spatial);
    }

    /**
     * If you call this function you will be able to increase or decrease the
     * steering behavior force multiplying it by a scalar.
     *
     * @param scalar Scalar that will multiply the raw steer force.
     *
     * @throws NegativeValueException If scalar is lower than 0
     */
    public void setupStrengthControl(float scalar) {
        this.validateScalar(scalar);
        this.scalar = scalar;
        this.type = SteerStrengthType.SCALAR;
    }

    /**
     * If you call this function you will be able to modify the raw steering
     * force on the specific axis (x, y, z).
     *
     * @param x X axis multiplier
     * @param y Y axis multiplier
     * @param z Z axis multiplier
     *
     * @throws NegativeValueException If any axis multiplier is lower than 0
     */
    public void setupStrengthControl(float x, float y, float z) {
        this.validateScalar(x);
        this.validateScalar(y);
        this.validateScalar(z);
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = SteerStrengthType.AXIS;
    }

    /**
     * If you call this function you will be able to modify the raw steering
     * force on the specific axis (x, y, z).
     *
     * @param x X axis multiplier
     * @param y Y axis multiplier
     * @param z Z axis multiplier
     *
     * @throws NegativeValueException If any axis multiplier is lower than 0
     */
    public void setupStrengthControl(Vector3f vector) {
        validateScalar(vector.getX());
        validateScalar(vector.getY());
        validateScalar(vector.getZ());
        x = vector.getX();
        y = vector.getY();
        z = vector.getZ();
        type = SteerStrengthType.AXIS;
    }

    /**
     * Forces the steer to stay inside a plane.
     *
     * @param Plane plane where the steer will be
     */
    public void setupStrengthControl(Plane plane) {
        this.scalar = 1.0f;
        this.plane = plane;
        this.type = SteerStrengthType.PLANE;
    }

    /**
     * @see AbstractStrengthSteeringBehavior#setupStrengthControl(float)
     * @see
     * AbstractStrengthSteeringBehavior#setupStrengthControl(com.jme3.math.Plane)
     */
    public void setupStrengthControl(Plane plane, float scalar) {
        this.validateScalar(scalar);
        this.scalar = scalar;
        this.plane = plane;
        this.type = SteerStrengthType.PLANE;
    }

    private void validateScalar(float scalar) {
        if (scalar < 0) {
            throw new NegativeValueException("The scalar multiplier must be positive.", scalar);
        }
    }

    /**
     * If this function is called, this class work as AbstractSteeringBehavior.
     *
     * @see AbstractSteeringBehavior
     */
    public void turnOffStrengthControl() {
        this.type = SteerStrengthType.NO_STRENGTH;
    }

    /**
     * Calculates the steering force with the specified strength. <br><br>
     *
     * If the strength was not setted up it the return calculateSteering(), the
     * unmodified force.
     *
     * @return The steering force with the specified strength.
     */
    @Override
    protected Vector3f calculateSteering() {

        Vector3f strengthSteeringForce = calculateRawSteering();

        switch (this.type) {
            case SCALAR:
                strengthSteeringForce = strengthSteeringForce.mult(this.scalar);
                break;

            case AXIS:
                strengthSteeringForce.setX(strengthSteeringForce.getX() * this.x);
                strengthSteeringForce.setY(strengthSteeringForce.getY() * this.y);
                strengthSteeringForce.setZ(strengthSteeringForce.getZ() * this.z);
                break;

            case PLANE:
                strengthSteeringForce = this.plane.getClosestPoint(strengthSteeringForce).mult(this.scalar);
                break;

        }
        
        //if there is no steering force, than the steering vector is zero
        if (strengthSteeringForce.equals(Vector3f.NAN)) {
            strengthSteeringForce = Vector3f.ZERO.clone();
        }
        
        return strengthSteeringForce;
    }

    /**
     * If a behavior class extend from CompoundSteeringBehaviour instead of
     * AbstractSteeringBehavior, it must implement this method instead of
     * calculateSteering().
     *
     * @see AbstractSteeringBehavior#calculateSteering()
     * @return
     */
    protected abstract Vector3f calculateRawSteering();
}