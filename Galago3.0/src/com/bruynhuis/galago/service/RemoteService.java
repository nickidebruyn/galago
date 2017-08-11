/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.service;

import java.util.Properties;

/**
 *
 * @author NideBruyn
 */
public interface RemoteService {
    
    public void doGet(String url, Properties parameters, RemoteServiceCallback remoteServiceCallback) throws Exception;
    
    public void doPost(String url, Properties parameters, RemoteServiceCallback remoteServiceCallback) throws Exception;
    
}
