/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.service;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet; 
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
//import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.entity.ContentInputStream;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

/**
 *
 * @author NideBruyn
 */
public class DesktopAndroidRemoteServiceImpl implements RemoteService {

    protected final static Logger logger = Logger.getLogger(DesktopAndroidRemoteServiceImpl.class.getName());

    protected void log(String text) {
//        System.out.println(text);
    }

    @Override
    public void doGet(final String url, final Properties parameters, final RemoteServiceCallback remoteServiceCallback) throws Exception {
        //CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        // Trust standard CAs and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                        return true;
                    }
                })
                .build();
        // Allow TLSv1 protocol only
        SSLIOSessionStrategy sslSessionStrategy = new SSLIOSessionStrategy(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                SSLIOSessionStrategy.getDefaultHostnameVerifier());
        final CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                .setSSLStrategy(sslSessionStrategy)
                .build();

        log("Created httpClient for GET");

        try {
            // Start the client
            httpclient.start();
            log("httpClient started");

            // One most likely would want to use a callback for operation result
//            final CountDownLatch latch = new CountDownLatch(1);
            final HttpGet request = new HttpGet(url);
            log("httpClient request " + url);

            httpclient.execute(request, new FutureCallback<HttpResponse>() {

                @Override
                public void completed(final HttpResponse response) {
                    log("SUCCESS: " + response.toString());
//                    latch.countDown();

                    try {
                        ContentInputStream cis = (ContentInputStream) response.getEntity().getContent();
                        byte[] data = new byte[cis.available()];
                        cis.read(data);

                        httpclient.close();
                        remoteServiceCallback.onSuccess(new String(data));

                    } catch (IOException ex) {
                        Logger.getLogger(DesktopAndroidRemoteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalStateException ex) {
                        Logger.getLogger(DesktopAndroidRemoteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {

                    }

                }

                @Override
                public void failed(final Exception ex) {
                    log("ERROR: " + ex);
                    try {
                        //                    latch.countDown();
                        httpclient.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(DesktopAndroidRemoteServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    remoteServiceCallback.onError(ex.getMessage());

                }

                @Override
                public void cancelled() {
                    log("CANCELLED");
                    try {
                        //                    latch.countDown();
                        httpclient.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopAndroidRemoteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            });

//            latch.await();
        } finally {
//            log("httpClient close");
//            httpclient.close();
        }

    }

    @Override
    public void doPost(final String url, final Properties parameters, final RemoteServiceCallback remoteServiceCallback) throws Exception {
        // Trust standard CAs and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                        return true;
                    }
                })
                .build();
        // Allow TLSv1 protocol only
        SSLIOSessionStrategy sslSessionStrategy = new SSLIOSessionStrategy(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                SSLIOSessionStrategy.getDefaultHostnameVerifier());
        final CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                .setSSLStrategy(sslSessionStrategy)
                .build();

        log("Created httpClient for POST");

        try {
            // Start the client
            httpclient.start();
            log("httpClient started");

            // One most likely would want to use a callback for operation result
//            final CountDownLatch latch = new CountDownLatch(1);
            final HttpPost request = new HttpPost(url);
            log("httpClient request " + url);

            httpclient.execute(request, new FutureCallback<HttpResponse>() {

                @Override
                public void completed(final HttpResponse response) {
                    log("SUCCESS: " + response.toString());
//                    latch.countDown();

                    try {
                        ContentInputStream cis = (ContentInputStream) response.getEntity().getContent();
                        byte[] data = new byte[cis.available()];
                        cis.read(data);
                        
                        httpclient.close();
                        remoteServiceCallback.onSuccess(new String(data));

                    } catch (IOException ex) {
                        Logger.getLogger(DesktopAndroidRemoteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalStateException ex) {
                        Logger.getLogger(DesktopAndroidRemoteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                @Override
                public void failed(final Exception ex) {
                    log("ERROR: " + ex);
                    try {
                        //                    latch.countDown();
                        httpclient.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(DesktopAndroidRemoteServiceImpl.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    remoteServiceCallback.onError(ex.getMessage());

                }

                @Override
                public void cancelled() {
                    log("CANCELLED");
                    try {
                        //                    latch.countDown();
                        httpclient.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopAndroidRemoteServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            });

//            latch.await();
        } finally {
//            log("httpClient close");
//            httpclient.close();
        }
    }
}
