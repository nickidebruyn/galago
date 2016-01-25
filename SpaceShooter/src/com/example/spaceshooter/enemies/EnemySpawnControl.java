/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.enemies;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.util.Timer;
import com.example.spaceshooter.MainApplication;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Nidebruyn
 */
public class EnemySpawnControl extends AbstractControl {
    
    public static final int TYPE_STRAIT = 0;
    public static final int TYPE_ZIGZAG = 1;
    public static final int TYPE_ZIGZAG_AGRESSIVE = 2;
    public static final int TYPE_MOTHER_SHIP = 3;
    
    private MainApplication mainApplication;
    private Node rootNode;
    private Timer timer = new Timer(200);
    private Material enemyMaterial;
    private int enemyCount = 0;
    private int[] level = {0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 2, 1, 1, 2, 2, 2, 2, 1, 0, 3};

    public EnemySpawnControl(MainApplication mainApplication, Node node) {
        this.mainApplication = mainApplication;
        this.rootNode = node;
        this.enemyMaterial = mainApplication.getModelManager().getMaterial("Materials/enemies.j3m");
        this.timer.start();
    }

    @Override
    protected void controlUpdate(float tpf) {
        
        timer.update(tpf);
        if (timer.finished()) {
            //add an enemy to the system
            int enemy = level[enemyCount];
            addEnemy(enemy);
            enemyCount ++;
            
            //reset the timer
            timer.reset();
            
            if (enemyCount >= level.length) {
                timer.stop();
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
        
    private void addEnemy(int type) {
        Vector3f position = new Vector3f(FastMath.nextRandomInt(-mainApplication.getLevelWidth()+2, mainApplication.getLevelWidth()-2), mainApplication.getLevelHeight() + 2f, 0);
        
        int indexOfShip = FastMath.nextRandomInt(0, 8);
        
        float size = 2f;
        float hSpeed = 0f;
        float vSpeed = 50f;
        int shield = 1;
        
        if (type == TYPE_MOTHER_SHIP) {
            size = 5f;
            vSpeed = FastMath.nextRandomInt(4, 4);
            hSpeed = FastMath.nextRandomInt(10, 16);
            shield = 10;
            
        } else if (type == TYPE_ZIGZAG) {
            size = 3.0f;
            vSpeed = FastMath.nextRandomInt(10, 14);
            hSpeed = FastMath.nextRandomInt(20, 26);
            shield = 3;
            
        } else if (type == TYPE_ZIGZAG_AGRESSIVE) {
            size = 2.5f;
            vSpeed = FastMath.nextRandomInt(12, 16);
            hSpeed = FastMath.nextRandomInt(20, 26);
            shield = 2;
            
        } else {
            vSpeed = FastMath.nextRandomInt(40, 50);
            
        }
        
        //First we load the ship
        Sprite spaceship = new Sprite("enemy", size, size, 3, 3, indexOfShip);
        spaceship.setMaterial(enemyMaterial);
        rootNode.attachChild(spaceship);
        
        RigidBodyControl spaceshipRigidBody = new RigidBodyControl(new CircleCollisionShape(size*0.5f), 1f);
        spaceshipRigidBody.setSensor(true);
        spaceshipRigidBody.setGravityScale(0f);
        spaceshipRigidBody.setPhysicLocation(position);
        spaceship.addControl(spaceshipRigidBody);
        mainApplication.getDyn4jAppState().getPhysicsSpace().add(spaceship);
        
        spaceship.addControl(new EnemyMovementControl(mainApplication, vSpeed, hSpeed));
        spaceship.addControl(new EnemyCollisionControl(mainApplication, shield));
        
        if (type == TYPE_ZIGZAG_AGRESSIVE) {
            spaceship.addControl(new EnemyShootControl(mainApplication, 100));
        }
        
        if (type == TYPE_MOTHER_SHIP) {
            spaceship.addControl(new EnemyShootControl(mainApplication, 40));
        }
        
        
    }
    
    public int getEnemyCount() {
        return level.length;
    }
}
