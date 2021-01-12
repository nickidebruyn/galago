/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.websocket.game;

/**
 *
 * @author NideBruyn
 */
public interface PlayerListener {
    
    public void broadcastState(Player player);
    
}
