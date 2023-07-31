/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.ai.steering.behaviour;

import com.jme3.math.Plane;
import com.jme3.math.Plane.Side;
import com.jme3.math.Vector3f;
import java.util.List;
import com.jme3.ai.steering.Obstacle;

/**
 * Obstacle avoidance behavior gives a character the ability to maneuver 
 * in a cluttered environment by dodging around obstacles. There is an 
 * important distinction between obstacle avoidance and flee behavior. 
 * Flee will always cause a character to steer away from a given location, 
 * whereas obstacle avoidance takes action only when a nearby obstacle 
 * lies directly in front of the character.
 * 
 * The implementation of obstacle avoidance behavior here will make a 
 * simplifying assumption that both the character and obstacle can be 
 * reasonably approximated as spheres.
 * 
 * Keep in mind that this relates to obstacle avoidance not necessarily to 
 * collision detection.
 * 
 * The goal of the behavior is to keep an imaginary cylinder of free space 
 * in front of the character. The cylinder lies along the character’s forward 
 * axis, has a diameter equal to the character’s bounding sphere, and extends 
 * from the character’s center for a distance based on the character’s speed 
 * and agility. An obstacle further than this distance away is not an immediate 
 * threat.
 * 
 * @author Brent Owens
 */
public class ObstacleAvoid implements Behaviour {
    
    public Vector3f calculateForce(Vector3f location, Vector3f velocity, float collisionRadius,
                                    float speed, float turnSpeed, float tpf,
                                    List<Obstacle> obstacles) {
        
        // a turn force less than the speed will increase the range of the
        // collision cylinder. If the turn force is larger than the speed, 
        // then the cylinder. This is just a rough, linear, approximation
        // of the distance needed to avoid a collision.
        float cautionRange = speed/turnSpeed * tpf;
        
        Plane plane = new Plane(velocity, 1);
        
        float r1 = cautionRange + collisionRadius;
        // assuming obsticals are ordered from closest to farthest
        for (Obstacle obstical : obstacles) {
            Vector3f loc = obstical.getLocation().subtract(location);
            
            if (plane.whichSide(loc) != Side.Positive)
                continue; // if it is behind, ignore it
            
            // if it is at least in the radius of the collision cylinder
            if (loc.lengthSquared() < (r1+obstical.getRadius())*(r1+obstical.getRadius()) ) {
                // check cylinder collision
                
                // Project onto the back-plane(defined using the velocity vector as the normal for the plane)
                // and test the radius width intersection
                Vector3f projPoint = plane.getClosestPoint(loc);
                if (projPoint.lengthSquared() < (collisionRadius+obstical.getRadius())*(collisionRadius+obstical.getRadius())) {
                    // we have a collision.
                    // negate the side-up projection we used to check the collision
                    // and use that for steering
                    return loc.negate();
                }
            }
        }
        
        return Vector3f.ZERO; // no collision
    }
    
}
