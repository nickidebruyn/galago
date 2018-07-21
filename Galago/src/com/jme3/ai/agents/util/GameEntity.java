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
package com.jme3.ai.agents.util;

import com.jme3.ai.agents.behaviors.npc.steering.ObstacleAvoidanceBehavior;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.monkeystuff.systems.HitPoints;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Base class for game objects that are interacting in game, and in general can
 * move and can be destroyed. Not to be used for terrain except for things that
 * can be destroyed. For automaticaly updating them, add them to to MonkeyBrainsAppState
 * with addAgent().
 *
 * @see MonkeyBrainsAppState#addAgent(com.jme3.ai.agents.Agent)
 * @see MonkeyBrainsAppState#addAgent(com.jme3.ai.agents.Agent, com.jme3.math.Vector3f)
 * @see MonkeyBrainsAppState#addAgent(com.jme3.ai.agents.Agent, float, float, float) For
 * other GameEntity use:
 * @see MonkeyBrainsAppState#addGameObject(com.jme3.ai.agents.util.GameEntity)
 *
 * @author Tihomir Radosavljević
 * @author Jesús Martín Berlanga
 * @version 1.3.2
 */
public abstract class GameEntity extends AbstractControl {

    /**
     * Container for the velocity of the game object.
     */
    protected Vector3f velocity = Vector3f.UNIT_X.clone();
    /**
     * Mass of GameEntity.
     */
    protected float mass;
    /**
     * GameEntity acceleration speed.
     */
    protected Vector3f acceleration;
    /**
     * Maximum move speed of GameEntity.
     */
    protected float maxMoveSpeed;
    /**
     * Maximum force that can be applied to this GameEntity.
     */
    protected float maxForce;
    /**
     * HitPoint System that will agent use.
     */
    protected HitPoints hitPoints;
    /**
     * Rotation speed of GameEntity.
     */
    protected float rotationSpeed;
    /**
     * Radius of GameEntity. It is needed for object that will be added in list
     * of objects that agent should avoid durring game, like mines etc.
     *
     * @see ObstacleAvoidanceBehavior
     */
    protected float radius = 0;
    /**
     * Unique id of gameEntity. Used internaly in framework. Changing may cause
     * unexpecting results.
     */
    protected int id;

    /**
     * @return The predicted position for this 'frame', taking into account
     * current position and velocity.
     */
    public Vector3f getPredictedPosition() {
        Vector3f predictedPos = new Vector3f();
        if (velocity != null) {
            predictedPos = getLocalTranslation().add(velocity);
        }
        return predictedPos;
    }

    /**
     * @param gameEntity Other game entitys
     * @return The offset relative to another game entity
     */
    public Vector3f offset(GameEntity gameEntity) {
        return gameEntity.getLocalTranslation().subtract(getLocalTranslation());
    }

    /**
     * @param positionVector
     * @return The offset relative to an position vector
     */
    public Vector3f offset(Vector3f positionVector) {
        return positionVector.subtract(getLocalTranslation());
    }

    /**
     * @return The agent forward direction
     */
    public Vector3f fordwardVector() {
        return getLocalRotation().mult(new Vector3f(0, 0, 1)).normalize();
    }

    /**
     * Calculates the forwardness in relation with another game entity. That is
     * how "forward" is the direction to the quarry (1 means dead ahead, 0 is
     * directly to the side, -1 is straight back)
     *
     * @param gameEntity Other game entity
     * @return The forwardness in relation with another agent
     */
    public float forwardness(GameEntity gameEntity) {
        Vector3f agentLooks = fordwardVector();
        float radiansAngleBetwen = agentLooks.angleBetween(offset(gameEntity).normalize());
        return FastMath.cos(radiansAngleBetwen);
    }

    /**
     * @param positionVector Offset vector.
     * @return The forwardness in relation with a position vector
     */
    public float forwardness(Vector3f offsetVector) {
        Vector3f agentLooks = getLocalRotation().mult(new Vector3f(0, 0, 1)).normalize();
        float radiansAngleBetwen = agentLooks.angleBetween(offsetVector.normalize());
        return FastMath.cos(radiansAngleBetwen);
    }

    /**
     * @param gameEntity Other agent
     * @return Distance relative to another game entity
     */
    public float distanceRelativeToGameEntity(GameEntity gameEntity) {
        return offset(gameEntity).length();
    }

    /**
     * @param gameEntity Other agent
     * @return Distance from a position
     */
    public float distanceSquaredRelativeToGameEntity(GameEntity gameEntity) {
        return offset(gameEntity).lengthSquared();
    }

    /**
     * @param position Position
     * @return Distance from a position
     */
    public float distanceFromPosition(Vector3f position) {
        return offset(position).length();
    }

    /**
     * @param position Position
     * @return Distance squared Distance from a position
     */
    public float distanceSquaredFromPosition(Vector3f position) {
        return offset(position).lengthSquared();
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public Vector3f getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector3f acceleration) {
        this.acceleration = acceleration;
    }

    public float getMoveSpeed() {
        return velocity.length();
    }

    public void setMoveSpeed(float moveSpeed) {
        if (maxMoveSpeed < moveSpeed) {
            this.maxMoveSpeed = moveSpeed;
        }
        velocity.normalizeLocal().multLocal(moveSpeed);
    }

    public float getMaxForce() {
        return maxForce;
    }

    public void setMaxForce(float maxForce) {
        this.maxForce = maxForce;
    }

    public Quaternion getLocalRotation() {
        return spatial.getLocalRotation();
    }

    public void setLocalRotation(Quaternion rotation) {
        try {
            spatial.setLocalRotation(rotation);
        } catch (NullPointerException e) {
            throw new GameEntityExceptions.GameEntityAttributeNotFound(this, "spatial");
        }

    }

    /**
     *
     * @return local translation of agent
     */
    public Vector3f getLocalTranslation() {
        try {
            return spatial.getLocalTranslation();
        } catch (NullPointerException e) {
            throw new GameEntityExceptions.GameEntityAttributeNotFound(this, "spatial");
        }
    }

    /**
     *
     * @param position local translation of agent
     */
    public void setLocalTranslation(Vector3f position) {
        this.spatial.setLocalTranslation(position);
    }

    /**
     * Setting local translation of agent
     *
     * @param x x translation
     * @param y y translation
     * @param z z translation
     */
    public void setLocalTranslation(float x, float y, float z) {
        this.spatial.setLocalTranslation(x, y, z);
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public float getMaxMoveSpeed() {
        return maxMoveSpeed;
    }

    public void setMaxMoveSpeed(float maxMoveSpeed) {
        this.maxMoveSpeed = maxMoveSpeed;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public Vector3f getVelocity() {
        return this.velocity;
    }

    public void setRadius(float radius) {
        this.validateRadius(radius);
        this.radius = radius;
    }

    public float getRadius() {
        return this.radius;
    }

    public HitPoints getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(HitPoints hitPoints) {
        this.hitPoints = hitPoints;
    }

    protected void validateRadius(float radius) {
        if (radius < 0) {
            throw new GameEntityExceptions.NegativeRadiusException("A GameObject can't have a negative radius. You tried to construct the agent with a " + radius + " radius.");
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GameEntity{" + id + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameEntity other = (GameEntity) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
}