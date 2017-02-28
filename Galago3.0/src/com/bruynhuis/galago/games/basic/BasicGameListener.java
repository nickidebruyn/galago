/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.basic;

/**
 *
 * @author nidebruyn
 */
public interface BasicGameListener {
    
    public void doGameOver();
    
    public void doGameCompleted();
    
    public void doScoreChanged(int score);    
      
}
