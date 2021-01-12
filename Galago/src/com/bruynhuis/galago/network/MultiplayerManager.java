/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.network;

import com.bruynhuis.galago.network.messages.AddObjectMessage;
import com.bruynhuis.galago.network.messages.ChangePlayerStateMessage;
import com.bruynhuis.galago.network.messages.JoinGameMessage;
import com.bruynhuis.galago.network.messages.CreateGameMessage;
import com.bruynhuis.galago.network.messages.ExitGameMessage;
import com.bruynhuis.galago.network.messages.GameClosedMessage;
import com.bruynhuis.galago.network.messages.GameCreatedMessage;
import com.bruynhuis.galago.network.messages.GameListMessage;
import com.bruynhuis.galago.network.messages.GameNotAvailableMessage;
import com.bruynhuis.galago.network.messages.GamePlayerLimitReachedMessage;
import com.bruynhuis.galago.network.messages.GameStateMessage;
import com.bruynhuis.galago.network.messages.NetworkGameMessage;
import com.bruynhuis.galago.network.messages.ObjectCollisionMessage;
import com.bruynhuis.galago.network.messages.ObjectDamageMessage;
import com.bruynhuis.galago.network.messages.ObjectDestroyMessage;
import com.bruynhuis.galago.network.messages.ObjectForceMessage;
import com.bruynhuis.galago.network.messages.ObjectLifeTimerMessage;
import com.bruynhuis.galago.network.messages.ObjectStateMessage;
import com.bruynhuis.galago.network.messages.PlayerCollisionMessage;
import com.bruynhuis.galago.network.messages.PlayerDamageMessage;
import com.bruynhuis.galago.network.messages.PlayerDisableMessage;
import com.bruynhuis.galago.network.messages.PlayerForceMessage;
import com.bruynhuis.galago.network.messages.PlayerJumpMessage;
import com.bruynhuis.galago.network.messages.PlayerLeftGameMessage;
import com.bruynhuis.galago.network.messages.PlayerMoveMessage;
import com.bruynhuis.galago.network.messages.PlayerRespawnMessage;
import com.bruynhuis.galago.network.messages.PlayerScoreMessage;
import com.bruynhuis.galago.network.messages.PlayerStateMessage;
import com.bruynhuis.galago.network.messages.PlayerWalkMessage;
import com.bruynhuis.galago.network.messages.PlayerWithPlayerCollisionMessage;
import com.bruynhuis.galago.network.messages.RequestGameListMessage;
import com.bruynhuis.galago.network.messages.StartGameMessage;
import com.bruynhuis.galago.network.messages.StopGameMessage;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NideBruyn
 */
public class MultiplayerManager implements MessageListener<Client>, ClientStateListener {

    private String serverAddress;
    private String gameName;
    private int tcpPort;
    private int udpPort;
    private int version;
    private Client client;
    private boolean local;

    public MultiplayerManager(String gameName, String ipAddress, int tcpPort, int udpPort, int version, boolean local) {
        this.gameName = gameName;
        this.serverAddress = ipAddress;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.version = version;
        this.local = local;
    }

    public void load() {
        try {
            if (!local) {
                registerMessages();
            }

            createClient();
            registerListeners();

        } catch (IOException ex) {
            log("Exception connecting");
            Logger.getLogger(MultiplayerManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    protected void registerMessages() {
        try {
            Serializer.registerClasses(CreateGameMessage.class,
                    JoinGameMessage.class,
                    GameListMessage.class,
                    GameStateMessage.class,
                    StopGameMessage.class,
                    StartGameMessage.class,
                    RequestGameListMessage.class,
                    NetworkGameMessage.class,
                    GameClosedMessage.class,
                    GameCreatedMessage.class,
                    ExitGameMessage.class,
                    GameNotAvailableMessage.class,
                    PlayerStateMessage.class,
                    PlayerLeftGameMessage.class,
                    PlayerMoveMessage.class,
                    GamePlayerLimitReachedMessage.class,
                    AddObjectMessage.class,
                    ObjectStateMessage.class,
                    PlayerForceMessage.class,
                    PlayerJumpMessage.class,
                    PlayerWalkMessage.class,
                    PlayerDamageMessage.class,
                    PlayerScoreMessage.class,
                    PlayerRespawnMessage.class,
                    ObjectForceMessage.class,
                    ObjectLifeTimerMessage.class,
                    ObjectCollisionMessage.class,
                    PlayerCollisionMessage.class,
                    PlayerWithPlayerCollisionMessage.class,
                    ObjectDestroyMessage.class,
                    ObjectDamageMessage.class,
                    PlayerDisableMessage.class,
                    ChangePlayerStateMessage.class);
            log("Registering messages");
        } catch (Exception e) {
            log("Message already registered on client: " + e.getMessage());
        }

    }

    protected void createClient() throws IOException {
        client = Network.connectToServer(gameName, version, serverAddress, tcpPort, udpPort);
        client.start();

    }

    protected void registerListeners() {
        if (client != null && client.isStarted()) {

            client.addClientStateListener(this);

            client.addMessageListener(this,
                    CreateGameMessage.class,
                    GameStateMessage.class,
                    JoinGameMessage.class,
                    StartGameMessage.class,
                    StopGameMessage.class,
                    GameListMessage.class,
                    NetworkGameMessage.class,
                    RequestGameListMessage.class,
                    AddObjectMessage.class,
                    //                    ChangePlayerStateMessage.class,
                    ObjectStateMessage.class,
                    ObjectForceMessage.class,
                    ObjectCollisionMessage.class,
                    PlayerCollisionMessage.class,
                    PlayerWithPlayerCollisionMessage.class);

            log("Listeners registered");
        } else {
            System.err.println("Failed to register listeners");
        }
    }

    @Override
    public void messageReceived(Client source, Message m) {
//        log("Message received, " + m);

    }

    protected void log(String text) {
        System.out.println("CLIENT: " + text);

    }

    @Override
    public void clientConnected(Client c) {
        log("Client successfully connected");
    }

    @Override
    public void clientDisconnected(Client c, DisconnectInfo info) {
        log("Client discconnected");
//        System.exit(0);
    }

    public void disposeConnection() {
        if (client != null) {
            client.close();

        }
    }

    public Client getClient() {
        return client;
    }

    public boolean isClientConnected() {
        return client != null && client.isConnected();
    }
}
