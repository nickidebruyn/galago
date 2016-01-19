/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.blockbuilder;

import com.bruynhuis.galago.games.basic.BasicGame;
import com.bruynhuis.galago.games.basic.BasicPlayer;
import com.jme3.math.Vector3f;

/**
 *
 * @author nidebruyn
 */
public class BlockBuilderGamePlayer extends BasicPlayer {

    public BlockBuilderGamePlayer(BasicGame basicGame) {
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
