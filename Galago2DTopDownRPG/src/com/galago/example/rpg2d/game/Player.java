/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.rpg2d.game;

import com.bruynhuis.galago.games.basic.BasicGame;
import com.bruynhuis.galago.games.basic.BasicPlayer;
import com.bruynhuis.galago.util.SpriteUtils;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class Player extends BasicPlayer {
    
    private float minWalkDistance = 0.1f;
    private float distanceBetweenCharacterAndTarget = 0;
    private float moveSpeed = 2.5f;
    private Quaternion targetRotation = Quaternion.IDENTITY;
    private Vector3f targetPosition = new Vector3f();

    public Player(BasicGame basicGame) {
        super(basicGame);
    }

    @Override
    protected void init() {

        SpriteUtils.addSprite(playerNode, "Textures/player.png", false, 0.01f, -1, 0, 0);

        playerNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (game.isStarted() && !game.isPaused() && !game.isGameOver() && !game.isGameComplete()) {

                    //Calculate the character movement
                    distanceBetweenCharacterAndTarget = playerNode.getWorldTranslation().distance(targetPosition);
                    if (distanceBetweenCharacterAndTarget > minWalkDistance) {

                        //Move the character towards the target
                        //1. start rotating character towards target each frame
//                        targetRotation.lookAt(targetPosition.subtract(getPosition()).normalize(), Vector3f.UNIT_Y);
//                        playerNode.getLocalRotation().slerp(targetRotation, 0.01f);

                        //2. move in the direction the character is facing
                        playerNode.move(targetPosition.subtract(getPosition()).normalize().mult(moveSpeed * tpf));

                    }

                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });
    }

    @Override
    public Vector3f getPosition() {
        return playerNode.getWorldTranslation();
    }

    @Override
    public void doDie() {
    }

    public void setTargetPosition(Vector3f targetPosition) {
        this.targetPosition = targetPosition;
    }

    
}
