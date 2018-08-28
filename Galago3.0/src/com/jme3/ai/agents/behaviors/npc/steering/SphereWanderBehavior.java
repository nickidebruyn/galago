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
import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Random;

/**
 * Wander is a type of random steering. This idea can be implemented several
 * ways, but one that has produced good results is to constrain the steering
 * force to the surface of a sphere located slightly ahead of the character. To
 * produce the steering force for the next frame: a random displacement is added
 * to the previous value, and the sum is constrained again to the sphere's
 * surface. The sphere's radius determines the maximum wandering strength and
 * the magnitude of the random displacement determines the wander 'rate'.
 * <br><br>
 *
 * The steer force is contained in the XY plane.
 *
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class SphereWanderBehavior extends AbstractStrengthSteeringBehavior {

    private static final float OFFSET_DISTANCE = 0.01f;
    private static final float RANDOM_OFFSET = 0.01f;
    private static final float SIDE_REFERENCE_OFFSET = 0.0001f;
    private float sphereRadius = 0.75f;
    /**
     * Position of target.
     */
    protected Vector3f targetPosition;
    /**
     * Time interval durring which target position doesn't change.
     */
    protected float timeInterval;
    /**
     * Current time.
     */
    protected float time;
    private float randomFactor;
    private Vector2f randomDirection;
    private float maxRandom;
    private float rotationFactor;
    private BoundingSphere wanderSphere;

    /**
     * Constructor for wander behavior.
     *
     * @param agent to whom behavior belongs
     * @param timeInterval Sets the time interval for changing target position.
     * @param randomFactor Defines the maximum random value
     * @param rotationFactor Defines the maximum random variaton for each
     * iteration.
     *
     * @throws SteeringExceptions.NegativeValueException If timeInterval is
     * lower or equals to 0
     * @throws SteeringExceptions.IllegalIntervalException If randomFactor is
     * not contained in the [0,1] interval or if rotationFactor is not contained
     * in the [0,1] interval
     */
    public SphereWanderBehavior(Agent agent, float timeInterval, float randomFactor, float rotationFactor) {
        super(agent);
        this.construct(timeInterval, randomFactor, rotationFactor);
    }

    /**
     * @see SphereWanderBehavior#SphereWanderBehavior(com.jme3.ai.agents.Agent,
     * float, float, float)
     * @see
     * AbstractSteeringBehavior#AbstractSteeringBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Spatial)
     */
    public SphereWanderBehavior(Agent agent, float timeInterval, float randomFactor, float rotationFactor, Spatial spatial) {
        super(agent, spatial);
        this.construct(timeInterval, randomFactor, rotationFactor);
    }

    private void construct(float timeInterval, float randomFactor, float rotationFactor) {
        if (timeInterval <= 0) {
            throw new SteeringExceptions.NegativeValueException("The time interval must be possitive." + timeInterval);
        } else if (randomFactor < 0 || randomFactor > 1) {
            throw new SteeringExceptions.IllegalIntervalException("random", randomFactor);
        } else if (rotationFactor < 0 || rotationFactor > 1) {
            throw new SteeringExceptions.IllegalIntervalException("rotation", rotationFactor);
        }

        this.timeInterval = timeInterval;
        this.time = this.timeInterval;
        this.randomFactor = randomFactor;
        this.wanderSphere = new BoundingSphere(this.sphereRadius, Vector3f.ZERO);
        this.targetPosition = this.wanderSphere.getCenter();
        this.randomDirection = new Vector2f();
        this.maxRandom = this.sphereRadius - SphereWanderBehavior.RANDOM_OFFSET;
        this.rotationFactor = rotationFactor;
        this.maxRandom *= this.rotationFactor;
    }

    /**
     * Calculate steering vector.
     *
     * @return steering vector
     */
    @Override
    protected Vector3f calculateRawSteering() {
        changeTargetPosition(timePerFrame);
        return this.agent.offset(this.targetPosition).mult((0.5f / this.sphereRadius) * this.agent.getMoveSpeed());
    }

    /**
     * Metod for changing target position.
     *
     * @param tpf time per frame
     */
    protected void changeTargetPosition(float tpf) {
        time -= tpf;
        Vector3f forward;

        if (this.agent.getVelocity() != null) {
            forward = this.agent.getVelocity().normalize();
        } else {
            forward = this.agent.fordwardVector();
        }

        if (forward.equals(Vector3f.UNIT_Y)) {
            forward = forward.add(new Vector3f(0, 0, SphereWanderBehavior.SIDE_REFERENCE_OFFSET));
        }

        //Update sphere position  
        this.wanderSphere.setCenter(this.agent.getLocalTranslation().add(forward.mult(SphereWanderBehavior.OFFSET_DISTANCE + this.agent.getRadius() + this.sphereRadius)));

        if (time <= 0) {
            this.calculateNewRandomDir();
            time = timeInterval;
        }

        Vector3f sideVector = forward.cross(Vector3f.UNIT_Y).normalize();
        Vector3f rayDir = (this.agent.offset(wanderSphere.getCenter())).add(sideVector.mult(this.randomDirection.x));//.add(Vector3f.UNIT_Y.mult(this.randomDirection.y));       

        Ray ray = new Ray(this.agent.getLocalTranslation(), rayDir);
        CollisionResults results = new CollisionResults();
        this.wanderSphere.collideWith(ray, results);

        CollisionResult collisionResult = results.getCollision(1); //The collision with the second hemisphere
        this.targetPosition = collisionResult.getContactPoint();
    }

    protected void calculateNewRandomDir() {
        Random rand = new Random();

        float extraRandomSide = (rand.nextFloat() / 2) * this.sphereRadius * this.rotationFactor * this.randomFactor;
        //float extraRandomZ = (rand.nextFloat() / 2) * this.sphereRadius * this.rotationFactor * this.randomFactor ;

        if (rand.nextBoolean()) {
            extraRandomSide *= -1;
        }
        //if(rand.nextBoolean()) extraRandomZ *= -1;

        float exceededSideOffset;
        //float exceededZOffset;

        float predictecRandomSide = this.randomDirection.x + extraRandomSide;

        if (predictecRandomSide > this.maxRandom) {
            exceededSideOffset = this.maxRandom - predictecRandomSide;
        } else if (predictecRandomSide < -this.maxRandom) {
            exceededSideOffset = -predictecRandomSide + this.maxRandom;
        } else {
            exceededSideOffset = 0;
        }

        /*
         float predictedRandomUp = this.randomDirection.y + extraRandomZ;
        
         if(predictedRandomUp > this.maxRandom)
         exceededZOffset = this.maxRandom - predictedRandomUp;
         else if(predictedRandomUp < -this.maxRandom)
         exceededZOffset = -predictedRandomUp + this.maxRandom;
         else
         exceededZOffset = 0;
         */

        this.randomDirection = this.randomDirection.add(new Vector2f(
                extraRandomSide + exceededSideOffset,
                0 //extraRandomZ + exceededZOffset
                ));
    }

    /**
     * Get time interval for changing target position.
     *
     * @return
     */
    public float getTimeInterval() {
        return timeInterval;
    }

    /**
     * Setting time interval for changing target position.
     *
     * @param timeInterval
     */
    public void setTimeInterval(float timeInterval) {
        this.timeInterval = timeInterval;
    }

    /**
     * The sphere radius is 0.75 by default. Note that low radius values can
     * cause unexpected errors.
     *
     * @throws SteeringExceptions.NegativeValueException If sphereRadius is
     * lower or equals to 0
     */
    public void setSphereRadius(float sphereRadius) {
        if (sphereRadius <= 0) {
            throw new SteeringExceptions.NegativeValueException("The sphere radius must be possitive.", sphereRadius);
        }
        this.sphereRadius = sphereRadius;
    }
}
