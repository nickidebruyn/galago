/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.ai.steering.behaviour;

import com.jme3.math.Vector3f;
import java.util.List;
import com.jme3.ai.steering.Obstacle;

/**
 * Separation steering behavior gives a character the ability to 
 * maintain a certain separation distance from others nearby. This 
 * can be used to prevent characters from crowding together.
 * 
 * For each nearby character, a repulsive force is computed by 
 * subtracting the positions of our character and the nearby character, 
 * normalizing, and then applying a 1/r weighting. (That is, the position 
 * offset vector is scaled by 1/r^2.) Note that 1/r is just a setting 
 * that has worked well, not a fundamental value. These repulsive forces 
 * for each nearby character are summed together to produce the overall 
 * steering force.
 * 
 * The supplied neighbours should only be the nearby neighbours in
 * the field of view of the character that is steering. It is good to
 * ignore anything behind the character.
 * 
 * @author Brent Owens
 */
public class Separation implements Behaviour {
    
    public Vector3f calculateForce(Vector3f location, 
                                    Vector3f velocity, 
                                    List<Obstacle> neighbours) {
        
        
        Vector3f steering = new Vector3f();
        for (Obstacle o : neighbours) {
            Vector3f loc = o.getLocation().subtract(location);
            float len2 = loc.lengthSquared();
            loc.normalizeLocal();
            steering.addLocal(loc.negate().mult(1f/len2));
        }
        
        return steering;
    }
}
