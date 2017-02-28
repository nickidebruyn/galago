/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.service;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author NideBruyn
 */
public class AndroidRemoteServiceImpl implements RemoteService {
    
    protected final static Logger logger = Logger.getLogger(AndroidRemoteServiceImpl.class.getName());
    
    protected void log(String text) {
        System.out.println(text);
    }

    @Override
    public void doGet(final String url, final Properties properties, final RemoteServiceCallback remoteServiceCallback) {
        AsyncHttpClient client = new AsyncHttpClient();
                
        String URI = new String(url);        
        log("GET URL = " + URI);
       
        client.get(URI, new AsyncHttpResponseHandler(){
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                log(response);
                remoteServiceCallback.onSuccess(response);

            }
            // When the response returned by REST has Http response code other than '200'

            @Override
            public void onFailure(int statusCode, Throwable error,
                    String content) {
                log("Error = " + statusCode);
                
                 // When Http response code is '404'
                 if(statusCode == 404){
                     remoteServiceCallback.onError("Requested resource not found");
                 } 
                 // When Http response code is '500'
                 else if(statusCode == 500){
                     remoteServiceCallback.onError("Something went wrong at server end");
                 } 
                 // When Http response code other than 404, 500
                 else{
                     remoteServiceCallback.onError("Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]");
                 }

            }
            
        });

    }
    
    @Override
    public void doPost(final String url, final Properties parameters, final RemoteServiceCallback remoteServiceCallback) {
        AsyncHttpClient client = new AsyncHttpClient();
        
        String URI = new String(url);        
        log("POST URL = " + URI);
       
        client.post(url, new AsyncHttpResponseHandler(){
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                remoteServiceCallback.onSuccess(response);

            }
            // When the response returned by REST has Http response code other than '200'

            @Override
            public void onFailure(int statusCode, Throwable error,
                    String content) {
                
                 // When Http response code is '404'
                 if(statusCode == 404){
                     remoteServiceCallback.onError("Requested resource not found");
                 } 
                 // When Http response code is '500'
                 else if(statusCode == 500){
                     remoteServiceCallback.onError("Something went wrong at server end");
                 } 
                 // When Http response code other than 404, 500
                 else{
                     remoteServiceCallback.onError("Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]");
                 }

            }
        });

    }
}
