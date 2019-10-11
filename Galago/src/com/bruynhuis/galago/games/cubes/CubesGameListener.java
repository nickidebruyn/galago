/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.cubes;

import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public interface CubesGameListener {

    public void doGameOver();

    public void doGameCompleted();

    public void doScoreChanged(int score);

    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider);

    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider);

    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider);

    public void doCollisionPlayerWithBullet(Spatial collided, Spatial collider);

    public void doCollisionObstacleWithBullet(Spatial collided, Spatial collider);

    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider);

    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider);

    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider);

    public void doCollisionEnemyWithObstacle(Spatial collided, Spatial collider);

}
