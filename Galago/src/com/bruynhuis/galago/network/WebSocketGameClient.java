/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network;

import com.bruynhuis.galago.app.BaseApplication;
import java.net.URI;
import java.nio.ByteBuffer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.bruynhuis.galago.util.SerializationUtils;
import java.io.Serializable;

/**
 *
 * @author NideBruyn
 */
public class WebSocketGameClient extends WebSocketClient {

    public static String TYPE_CONNECT = "CONNECT";
    public static String TYPE_DISCONNECT = "DISCONNECT";
    public static String TYPE_MESSAGE = "MESSAGE";
    public static String TYPE_DATA = "DATA";
    public static String TYPE_ERROR = "ERROR";

    private BaseApplication baseApplication;

    public WebSocketGameClient(BaseApplication baseApplication, URI serverUri) {
        super(serverUri);
        this.baseApplication = baseApplication;
    }

    @Override
    public void onOpen(ServerHandshake sh) {
        baseApplication.getMessageManager().sendMessage(TYPE_CONNECT, null);

    }

    @Override
    public void onMessage(String string) {
        baseApplication.getMessageManager().sendMessage(TYPE_MESSAGE, string);

    }

    @Override
    public void onClose(int i, String string, boolean bln) {
        baseApplication.getMessageManager().sendMessage(TYPE_DISCONNECT, string);

    }

    @Override
    public void onError(Exception excptn) {
        baseApplication.getMessageManager().sendMessage(TYPE_ERROR, excptn.getMessage());

    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        if (bytes != null) {
            baseApplication.getMessageManager().sendMessage(TYPE_DATA, SerializationUtils.deserialize(bytes.array()));
        }
        
    }

    public void send(Serializable serializable) {
        this.send(SerializationUtils.serialize(serializable));
        
    }
}
