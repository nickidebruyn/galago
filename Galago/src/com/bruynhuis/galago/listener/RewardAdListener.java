/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.listener;

/**
 *
 * @author NideBruyn
 */
public interface RewardAdListener {
    
    public void doAdRewarded(int amount, String type);
    
    public void doAdClosed();
    
    public void doAdLoaded();
    
}
