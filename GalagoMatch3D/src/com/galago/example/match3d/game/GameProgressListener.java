/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.match3d.game;

/**
 *
 * @author NideBruyn
 */
public interface GameProgressListener {
    
    public void doLevelUp(int level);
    
    public void doScoreBooster(int score);
    
}
