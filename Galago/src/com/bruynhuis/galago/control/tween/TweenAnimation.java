/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.tween;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NideBruyn
 */
public class TweenAnimation {
    
    private String name;
    private TweenManager tweenManager;
    private List<Tween> tweenList = new ArrayList<>();

    public TweenAnimation(String name, TweenManager tweenManager) {
        this.name = name;
        this.tweenManager = tweenManager;
    }
    
    public void addTween(Tween tween) {
        if (tween != null) {
            tweenList.add(tween);
        }
        
    }
    
    public void play() {
        if (tweenList != null) {
            for (int i = 0; i < tweenList.size(); i++) {
                Tween tw = tweenList.get(i);
                tw.start(tweenManager);

            }            
        }
    }
    
    public void stop() {
        if (tweenList != null) {
            for (int i = 0; i < tweenList.size(); i++) {
                Tween tw = tweenList.get(i);
                tw.free();
            }            
        }
    }
    
    public void pause() {
        if (tweenList != null) {
            for (int i = 0; i < tweenList.size(); i++) {
                Tween tw = tweenList.get(i);
                tw.pause();
            }            
        }
    }
    
    public void resume() {
        if (tweenList != null) {
            for (int i = 0; i < tweenList.size(); i++) {
                Tween tw = tweenList.get(i);
                tw.resume();
            }            
        }
    }
    
}
