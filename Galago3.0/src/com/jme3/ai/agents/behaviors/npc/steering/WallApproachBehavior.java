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
import com.jme3.ai.agents.behaviors.npc.steering.SteeringExceptions.WallApproachWithoutWallException;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * "Approach a 'wall' (or other surface or path) and then maintain a certain
 * offset from it" <br><br>
 *
 * Keep in mind that this relates to wall approach not necessarily to collision
 * detection.
 *
 * @author Jesús Martín Berlanga
 * @version 1.1
 */
public class WallApproachBehavior extends AbstractStrengthSteeringBehavior {

    //14 tests in total
    private static enum RayTests {

        RAY_TEST_X(Vector3f.UNIT_X),
        RAY_TEST_Y(Vector3f.UNIT_Y),
        RAY_TEST_Z(Vector3f.UNIT_Z),
        RAY_TEST_NEGX(Vector3f.UNIT_X.negate()),
        RAY_TEST_NEGY(Vector3f.UNIT_Y.negate()),
        RAY_TEST_NEGZ(Vector3f.UNIT_Z.negate()),
        RAY_TEST_XZ_UP(new Vector3f(1, 1, 1)),
        RAY_TEST_NEGX_Z_UP(new Vector3f(-1, 1, 1)),
        RAY_TEST_X_NEGZ_UP(new Vector3f(1, 1, -1)),
        RAY_TEST_NEGXZ_UP(new Vector3f(-1, 1, -1)),
        RAY_TEST_XZ_DOWN(new Vector3f(1, -1, 1)),
        RAY_TEST_NEGX_Z_DOWN(new Vector3f(-1, -1, 1)),
        RAY_TEST_X_NEGZ_DOWN(new Vector3f(1, -1, -1)),
        RAY_TEST_NEGXZ_DOWN(new Vector3f(-1, -1, -1));
        private Vector3f direction;

        private RayTests(Vector3f direction) {
            this.direction = direction;
        }

        private Vector3f getDirection() {
            return this.direction;
        }
    }
    private Node wall;
    private float offsetToMaintain;
    private float rayTestOffset;
    private static final float MIN_RAY_TEST_OFFSET = 0.001f;

    /**
     * @throws SteeringExceptions.NegativeValueException If offsetToMaintain is
     * negative
     */
    public void setOffsetToMaintain(float offsetToMaintain) {
        WallApproachBehavior.validateOffsetToMaintain(offsetToMaintain);
        this.offsetToMaintain = offsetToMaintain;
    }

    /**
     * @param wall Surface or path where the agent will maintain a certain
     * offset
     * @paaram offsetToMaintain Offset from the surface that the agent will have
     * to maintain
     *
     * @throws WallApproachWithoutWallException If the wall is a null pointer
     * @throws SteeringExceptions.NegativeValueException If offsetToMaintain is
     * negative
     *
     * @see
     * AbstractStrengthSteeringBehavior#AbstractStrengthSteeringBehavior(com.jme3.ai.agents.Agent)
     */
    public WallApproachBehavior(Agent agent, Node wall, float offsetToMaintain) {
        super(agent);
        WallApproachBehavior.validateConstruction(wall, offsetToMaintain);
        this.wall = wall;
        this.offsetToMaintain = offsetToMaintain;

        if (offsetToMaintain != 0) {
            this.rayTestOffset = (offsetToMaintain + agent.getRadius()) * 4;
        } else {
            this.rayTestOffset = WallApproachBehavior.MIN_RAY_TEST_OFFSET;
        }
    }

    /**
     * @see WallApproach#WallApproach(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Node, float)
     * @see
     * AbstractStrengthSteeringBehavior#AbstractStrengthSteeringBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Spatial)
     */
    public WallApproachBehavior(Agent agent, Node wall, float offsetToMaintain, Spatial spatial) {
        super(agent, spatial);
        WallApproachBehavior.validateConstruction(wall, offsetToMaintain);
        this.wall = wall;
        this.offsetToMaintain = offsetToMaintain;
        
        if (offsetToMaintain != 0) {
            this.rayTestOffset = (offsetToMaintain + agent.getRadius()) * 4;
        } else {
            this.rayTestOffset = WallApproachBehavior.MIN_RAY_TEST_OFFSET;
        }
    }

    private static void validateConstruction(Node wall, float offsetToMaintain) {
        if (wall == null) {
            throw new WallApproachWithoutWallException("You can not instantiate a new wall approach behaviour without a wall.");
        } else {
            WallApproachBehavior.validateOffsetToMaintain(offsetToMaintain);
        }
    }

    private static void validateOffsetToMaintain(float offsetToMaintain) {
        if (offsetToMaintain < 0) {
            throw new SteeringExceptions.NegativeValueException("The superficial offset to maintain cannot be negative.", offsetToMaintain);
        }
    }

    /**
     * @see AbstractStrengthSteeringBehavior#calculateRawSteering()
     */
    @Override
    protected Vector3f calculateRawSteering() {
        Vector3f steer = new Vector3f();

        Vector3f aproximatedSurfaceLocationDir = this.approximateSurfaceLocation();

        if (aproximatedSurfaceLocationDir != null) {
            Vector3f surfaceLocation = this.surfaceLocation(aproximatedSurfaceLocationDir);

            if (surfaceLocation != null) {
                Vector3f extraOffset = this.agent.offset(surfaceLocation).negate().normalize().mult(this.offsetToMaintain);

                SeekBehavior seek = new SeekBehavior(this.agent, surfaceLocation.add(extraOffset));
                steer = seek.calculateRawSteering();
            }
        }

        return steer;
    }

    /**
     * Check for intersections with the wall - Ray test
     */
    private Vector3f approximateSurfaceLocation() {
        class LowerDistances {

            private Vector3f[] lowerDistances = new Vector3f[]{
                Vector3f.POSITIVE_INFINITY,
                Vector3f.POSITIVE_INFINITY,
                Vector3f.POSITIVE_INFINITY,};

            private void addDistance(Vector3f distance) {
                float distanceLength = distance.length();

                int lowerPos = -1;
                float currentLength = Float.MAX_VALUE;

                for (int i = 0; i < lowerDistances.length; i++) {
                    float length = lowerDistances[i].length();

                    if (length > currentLength) {
                        currentLength = length;
                        lowerPos = i;
                    }
                }

                if (lowerPos != -1 && lowerDistances[lowerPos].length() > distanceLength) {
                    lowerDistances[lowerPos] = distance;
                }
            }

            private Vector3f distanceSum() {
                Vector3f sum = new Vector3f();
                boolean almostOne = false;

                for (int i = 0; i < lowerDistances.length; i++) {
                    if (lowerDistances[i] != null && lowerDistances[i].length() < rayTestOffset) {
                        sum = sum.add(lowerDistances[i]);
                        almostOne = true;
                    }
                }

                if (almostOne) {
                    return sum;
                } else {
                    return null;
                }
            }
        }

        LowerDistances distances = new LowerDistances();

        for (RayTests rayTest : WallApproachBehavior.RayTests.values()) {
            Vector3f rayTestSurfaceLocation = this.surfaceLocation(rayTest.getDirection());

            if (rayTestSurfaceLocation != null) {
                distances.addDistance(agent.offset(rayTestSurfaceLocation));
            }
        }

        return distances.distanceSum();
    }

    private Vector3f surfaceLocation(Vector3f direction) {
        Vector3f surfaceLocation = null;

        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(this.agent.getLocalTranslation(), direction);
        this.wall.collideWith(ray, results);

        CollisionResult collisionResult = results.getClosestCollision();

        if (collisionResult != null && !Float.isNaN(collisionResult.getDistance()) && !Float.isInfinite(collisionResult.getDistance())) {
            surfaceLocation = results.getClosestCollision().getContactPoint();
        }
        return surfaceLocation;
    }
}