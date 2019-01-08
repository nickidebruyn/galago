/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.match3d.game;

import com.bruynhuis.galago.games.basic.BasicGame;
import com.bruynhuis.galago.games.basic.BasicPlayer;
import com.jme3.math.Vector3f;

/**
 *
 * @author nicki
 */
public class Player extends BasicPlayer {

    public Player(BasicGame basicGame) {
        super(basicGame);
    }

    @Override
    protected void init() {

    }

    @Override
    public Vector3f getPosition() {
        return playerNode.getWorldTranslation();
    }

    @Override
    public void doDie() {

    }

}
