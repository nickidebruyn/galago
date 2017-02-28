/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.service;

/**
 *
 * @author NideBruyn
 */
public interface RemoteServiceCallback {
    
    public void onSuccess(String response);
    
    public void onError(String error);
    
}
