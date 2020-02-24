/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.rpg2d.game;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Expo;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.games.basic.BasicGame;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.util.SpriteUtils;
import com.jme3.scene.Node;

/**
 *
 * @author NideBruyn
 */
public class Game extends BasicGame {

    public Game(BaseApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);
    }

    @Override
    public void init() {        
        SpriteUtils.addSprite(levelNode, "Textures/grass-land.png", 0.028f, 0, 0, 0);
        SpriteUtils.addSprite(levelNode, "Textures/rock1.png", 0.022f, 1, 0, 0);
        Sprite tree =  SpriteUtils.addSprite(levelNode, "Textures/tree1.png", 0.022f, 0, 0, 0);
        tree.setOffset(0, 3, 0);
        SpriteUtils.rotateTo(tree, -5);
        Tween anim = Tween.to(tree, SpatialAccessor.ROTATION_Z, 2)
                .target(3)
                .ease(Expo.INOUT)
                .repeatYoyo(Tween.INFINITY, 0)
                .start(baseApplication.getTweenManager());
        
        
        SpriteUtils.addSprite(levelNode, "Textures/rock1.png", 0.022f, 5, 0, 0);
    }

}
