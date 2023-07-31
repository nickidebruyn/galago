/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.ai.steering;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import java.util.ArrayList;
import java.util.List;
import com.jme3.ai.steering.behaviour.Evade;
import com.jme3.ai.steering.behaviour.Flee;
import com.jme3.ai.steering.behaviour.ObstacleAvoid;
import com.jme3.ai.steering.behaviour.Persuit;
import com.jme3.ai.steering.behaviour.Seek;
import com.jme3.ai.steering.behaviour.Separation;
import com.jme3.ai.steering.utilities.SimpleObstacle;

/**
 *
 * @author Brent Owens
 */
public class TestSteering extends SimpleApplication {

    private Vehicle target;
    private Vehicle vehicle1;
    private Node obstacleNode;
    private Node friendNode;

    public static void main(String[] args) {
        TestSteering app = new TestSteering();
        app.start();
    }

    @Override
    public void start() {
        showSettings = false;
        super.start();
    }
    
    @Override
    public void simpleInitApp() {
        getCamera().setLocation(new Vector3f(0,20,0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(50);
        
        // create target
        target = new Vehicle(ColorRGBA.Red);
        target.velocity = new Vector3f(-1, 0, 0); // move down -X
        target.setLocalTranslation(10, 0, 10);
        Quaternion YAW180 = new Quaternion().fromAngleAxis(FastMath.PI, new Vector3f(0,1,0));
        target.setLocalRotation(YAW180); // point down -X
        target.addControl(new MoveStraightControl());
        rootNode.attachChild(target);
        
        // create the vehicle
        vehicle1 = new Vehicle(ColorRGBA.Blue);
        //vehicle1.addControl(new SeekControl());
        //vehicle1.addControl(new FleeControl());
        //vehicle1.addControl(new PersuitControl());
        //vehicle1.addControl(new EvadeControl());
        //vehicle1.addControl(new PersuitAndAvoidControl());
        //vehicle1.addControl(new PersuitSeparationControl());
        vehicle1.addControl(new PersuitAvoidSeparationControl());
        rootNode.attachChild(vehicle1);
        
        
        obstacleNode = new Node("obstacleNode");
        rootNode.attachChild(obstacleNode);
        friendNode = new Node("friendNode");
        rootNode.attachChild(friendNode);
        
        int amount = 10;
        
        // create obstacles
        for (int i=0; i<amount; i++) {
            Vehicle obstacle  = new Vehicle(ColorRGBA.Yellow);
            obstacle.setLocalTranslation(((float)Math.random())*10f, 0, ((float)Math.random())*10f);
            obstacleNode.attachChild(obstacle);
        }
        
        amount = 4;
        // create neighbours
        for (int i=0; i<amount; i++) {
            Vehicle neighbour  = new Vehicle(ColorRGBA.Blue);
            neighbour.setLocalTranslation(((float)Math.random())*5f, 0, ((float)Math.random())*5f);
            neighbour.addControl(new PersuitAvoidSeparationControl());
            friendNode.attachChild(neighbour);
        }
    }
    
    /**
     * Get all obsticals in the scene
     */
    private void fillObstacals(List<Obstacle> obstacles) {
        for (Spatial s : obstacleNode.getChildren()) {
            if (s instanceof Vehicle) {
                Vehicle v = (Vehicle)s;
                obstacles.add(v.toObstacle());
            }
        }
    }
    
    /**
     * find all neighbours in the scene, within the radius
     */
    private void fillNeighbours(Vehicle source, List<Obstacle> neighbours, float radius) {
        float r2 = radius*radius;
        for (Spatial s : friendNode.getChildren()) {
            if (s instanceof Vehicle && !s.equals(source)) {
                Vehicle v = (Vehicle)s;
                float d = source.getWorldTranslation().subtract(v.getWorldTranslation()).lengthSquared();
                if (d<r2) // if it is within the radius
                    neighbours.add(v.toObstacle());
            }
        }
    }
        
    /**
     * A Vehicle's "forward" is along the local +X axis
     */
    protected class Vehicle extends Node {
        float speed = 1.5f; // worldUnits/second
        float maxSpeed = 1.5f; // worldUnits/second
        float maxTurnForce = 2f; // max steering force per second (perpendicular to velocity)
                                // if speed is 1 and turn force is 1, then it will turn 45 degrees in a second
        float mass = 1.0f; // the higher, the slower it turns
        float collisionRadius = 0.1f;
        Vector3f velocity = Vector3f.UNIT_Z;
        
        // debug geoms
        Geometry collisionLine;
        Geometry velocityLine;
        
        protected Vehicle(ColorRGBA color) {
            Box b = new Box(Vector3f.ZERO, 0.1f, 0.1f, 0.2f);
            Geometry geom = new Geometry("Box", b);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", color);
            geom.setMaterial(mat);
            this.attachChild(geom);
        }
        
        /**
         * Take the steering influence and apply the vehicle's mass, max speed,
         * speed, and maxTurnForce to determine the new velocity.
         */
        protected void updateVelocity(Vector3f steeringInfluence, float scale) {
            //showVelocity(velocity.length(), true);
            //showCollisionRange(speed/maxTurnForce, true);
            //if (true) return;
            Vector3f steeringForce = truncate(steeringInfluence, maxTurnForce * scale);
            Vector3f acceleration = steeringForce.divide(mass);
            Vector3f vel = truncate(velocity.add(acceleration), maxSpeed);
            velocity = vel;
            
            setLocalTranslation(getLocalTranslation().add(velocity.mult(scale)));
            
            // rotate to face
            Quaternion rotTo = getLocalRotation().clone();
            rotTo.lookAt(velocity.normalize(), Vector3f.UNIT_Y);
            
            setLocalRotation(rotTo);
            
            //showVelocity(velocity.length(), true);
            showCollisionRange(speed/maxTurnForce, true);
        }
        
        /**
         * truncate the length of the vector to the given limit
         */
        private Vector3f truncate(Vector3f source, float limit) {
            if (source.lengthSquared() <= limit*limit) {
                return source;
            } else {
                return source.normalize().scaleAdd(limit, Vector3f.ZERO);
            }
        }
        
        /**
         * Gets the predicted position for this 'frame', 
         * taking into account current position and velocity.
         * @param tpf time per fram
         */
        protected Vector3f getFuturePosition(float tpf) {
            return getWorldTranslation().add(velocity);
        }
        
        protected void showVelocity(float length, boolean show) {
            if (velocityLine == null) {
                // create it if it doesn't exist
                //Vector3f end = this.velocity.normalize().mult(length);
                Line line = new Line(Vector3f.ZERO, new Vector3f(0, 0, length));
                Geometry geom = new Geometry("cylinder", line);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Cyan);
                geom.setMaterial(mat);
                velocityLine = geom;
            }
            
            // modify its direction and length
            Line line = (Line) velocityLine.getMesh();
            line.updatePoints(Vector3f.ZERO, new Vector3f(0, 0, length));
            
            // attach/detach it
            if (show) {
                if (velocityLine.getParent() == null)
                    this.attachChild(velocityLine);
            } else {
                if (velocityLine.getParent() != null)
                    velocityLine.removeFromParent();
            }
        }
        
        protected void showCollisionRange(float length, boolean show) {
            if (collisionLine == null) {
                // create it if it doesn't exist
                //Vector3f end = this.velocity.normalize().mult(length);
                Line line = new Line(Vector3f.ZERO, new Vector3f(0, 0, length));
                Geometry geom = new Geometry("cylinder", line);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Yellow);
                geom.setMaterial(mat);
                collisionLine = geom;
            }
            
            // modify its direction and length
            Line line = (Line) collisionLine.getMesh();
            line.updatePoints(Vector3f.ZERO, new Vector3f(0, 0, length));
            
            // attach/detach it
            if (show) {
                if (collisionLine.getParent() == null)
                    this.attachChild(collisionLine);
            } else {
                if (collisionLine.getParent() != null)
                    collisionLine.removeFromParent();
            }
        }
        
        protected void setCollideWarning(boolean warn) {
            if (collisionLine != null) {
                if (warn) {
                    collisionLine.getMaterial().setColor("Color", ColorRGBA.Red);
                } else {
                    collisionLine.getMaterial().setColor("Color", ColorRGBA.Cyan);
                }
            }
        }

        protected Obstacle toObstacle() {
            return new SimpleObstacle(getWorldTranslation(), collisionRadius, velocity);
        }
    }
    
    private class SeekControl extends SimpleControl {

        Seek seek = new Seek();
        
        @Override
        protected void controlUpdate(float tpf) {
            Vehicle vehicle = (Vehicle) getSpatial();
            if (vehicle == null)
                return;
            
            // calculate the steering force from the Seek routine
            Vector3f steering = seek.calculateForce(vehicle.getWorldTranslation(), 
                    vehicle.velocity, vehicle.speed, 
                    target.getWorldTranslation());
            
            // add the force to the velicity
            vehicle.updateVelocity(steering, tpf);
        }
    }
    
    private class FleeControl extends SimpleControl {

        Flee flee = new Flee();
        
        @Override
        protected void controlUpdate(float tpf) {
            Vehicle vehicle = (Vehicle) getSpatial();
            if (vehicle == null)
                return;
            
            // calculate the steering force from the Flee routine
            Vector3f steering = flee.calculateForce(vehicle.getWorldTranslation(), 
                    vehicle.velocity, vehicle.speed, 
                    target.getWorldTranslation());
            
            // add the force to the velicity
            vehicle.updateVelocity(steering, tpf);
        }
    }
    
    private class PersuitControl extends SimpleControl {

        Persuit persuit = new Persuit();
        
        @Override
        protected void controlUpdate(float tpf) {
            Vehicle vehicle = (Vehicle) getSpatial();
            if (vehicle == null)
                return;
            
            // calculate the steering force from the Persuit routine
            Vector3f steering = persuit.calculateForce(vehicle.getWorldTranslation(),
                    vehicle.velocity,
                    vehicle.speed, 
                    target.speed, 
                    tpf,
                    target.velocity,
                    target.getFuturePosition(tpf));
            
            // add the force to the velicity
            vehicle.updateVelocity(steering, tpf);
        }
    }
    
    private class EvadeControl extends SimpleControl {

        Evade evade = new Evade();
        
        @Override
        protected void controlUpdate(float tpf) {
            Vehicle vehicle = (Vehicle) getSpatial();
            if (vehicle == null)
                return;
            
            // calculate the steering force from the Persuit routine
            Vector3f steering = evade.calculateForce(vehicle.getWorldTranslation(),
                    vehicle.velocity,
                    vehicle.speed, 
                    target.speed, 
                    tpf,
                    target.velocity,
                    target.getFuturePosition(tpf));
            
            // add the force to the velicity
            vehicle.updateVelocity(steering, tpf);
        }
    }
    
    private class PersuitAndAvoidControl extends SimpleControl {

        Persuit persuit = new Persuit();
        ObstacleAvoid avoid = new ObstacleAvoid();
        
        List<Obstacle> obstacles = new ArrayList<Obstacle>();
        
        @Override
        protected void controlUpdate(float tpf) {
            Vehicle vehicle = (Vehicle) getSpatial();
            if (vehicle == null)
                return;
            
            if (obstacles.isEmpty())
                fillObstacals(obstacles);
            
            // calculate the steering force from the Persuit routine
            Vector3f steering = persuit.calculateForce(vehicle.getWorldTranslation(),
                    vehicle.velocity,
                    vehicle.speed, 
                    target.speed, 
                    tpf,
                    target.velocity,
                    target.getFuturePosition(tpf));
            
            Vector3f avoidance = avoid.calculateForce(vehicle.getWorldTranslation(), 
                    vehicle.velocity, 
                    vehicle.speed, 
                    vehicle.collisionRadius, 
                    vehicle.maxTurnForce, 
                    tpf, 
                    obstacles);
            
            // add the force to the velocity
            vehicle.updateVelocity(steering.add(avoidance), tpf);
        }
    }
    
    private class PersuitSeparationControl extends SimpleControl {

        Persuit persuit = new Persuit();
        Separation separation = new Separation();
        
        List<Obstacle> neighbours = new ArrayList<Obstacle>();
        
        @Override
        protected void controlUpdate(float tpf) {
            Vehicle vehicle = (Vehicle) getSpatial();
            if (vehicle == null)
                return;
            
            neighbours.clear(); // re-calculate every time
            fillNeighbours(vehicle, neighbours, 5f);
            
            // calculate the steering force from the Persuit routine
            Vector3f steering = persuit.calculateForce(vehicle.getWorldTranslation(),
                    vehicle.velocity,
                    vehicle.speed, 
                    target.speed, 
                    tpf,
                    target.velocity,
                    target.getFuturePosition(tpf));
            
            Vector3f separate = separation.calculateForce(vehicle.getWorldTranslation(), 
                    vehicle.velocity, 
                    neighbours);
            
            // add the force to the velocity
            vehicle.updateVelocity(steering.add(separate), tpf);
        }
    }
    
    private class PersuitAvoidSeparationControl extends SimpleControl {

        Persuit persuit = new Persuit();
        ObstacleAvoid avoid = new ObstacleAvoid();
        Separation separation = new Separation();
        
        List<Obstacle> obstacles = new ArrayList<Obstacle>();
        List<Obstacle> neighbours = new ArrayList<Obstacle>();
        
        @Override
        protected void controlUpdate(float tpf) {
            Vehicle vehicle = (Vehicle) getSpatial();
            if (vehicle == null)
                return;
            
            // fill once
            if (obstacles.isEmpty())
                fillObstacals(obstacles);
            
            neighbours.clear(); // re-calculate every time
            fillNeighbours(vehicle, neighbours, 5f);
            
            // calculate the steering force from the Persuit routine
            Vector3f steering = persuit.calculateForce(vehicle.getWorldTranslation(),
                    vehicle.velocity,
                    vehicle.speed, 
                    target.speed, 
                    tpf,
                    target.velocity,
                    target.getFuturePosition(tpf));
            
            Vector3f avoidance = avoid.calculateForce(vehicle.getWorldTranslation(), 
                    vehicle.velocity, 
                    vehicle.speed, 
                    vehicle.collisionRadius, 
                    vehicle.maxTurnForce, 
                    tpf, 
                    obstacles);
            
            Vector3f separate = separation.calculateForce(vehicle.getWorldTranslation(), 
                    vehicle.velocity, 
                    neighbours);
            
            // color the vector
            vehicle.setCollideWarning(avoidance.lengthSquared() > 0);
                   
            // add the force to the velocity
            vehicle.updateVelocity(steering.add(avoidance).add(separate), tpf);
        }
    }
    
    private class MoveStraightControl extends SimpleControl {

        @Override
        protected void controlUpdate(float tpf) {
            Vehicle vehicle = (Vehicle) getSpatial();
            if (vehicle == null)
                return;
            vehicle.updateVelocity(Vector3f.ZERO, tpf);
        }
    }
    
    /**
     * Just implements some of the required methods to make this test case easier.
     */
    private abstract class SimpleControl extends AbstractControl {
        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
        }

        @Override
        public Control cloneForSpatial(Spatial spatial) {
            return this;
        }
    }
}
