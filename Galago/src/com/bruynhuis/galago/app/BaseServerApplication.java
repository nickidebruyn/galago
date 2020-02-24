/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.app;

import com.bruynhuis.galago.network.game.NetworkGame;
import com.bruynhuis.galago.network.game.NetworkObject;
import com.bruynhuis.galago.network.game.NetworkPlayer;
import com.bruynhuis.galago.network.messages.AddObjectMessage;
import com.bruynhuis.galago.network.messages.NetworkGameMessage;
import com.bruynhuis.galago.network.messages.CreateGameMessage;
import com.bruynhuis.galago.network.messages.ExitGameMessage;
import com.bruynhuis.galago.network.messages.GameClosedMessage;
import com.bruynhuis.galago.network.messages.GameCreatedMessage;
import com.bruynhuis.galago.network.messages.GameListMessage;
import com.bruynhuis.galago.network.messages.GameNotAvailableMessage;
import com.bruynhuis.galago.network.messages.GamePlayerLimitReachedMessage;
import com.bruynhuis.galago.network.messages.JoinGameMessage;
import com.bruynhuis.galago.network.messages.ObjectCollisionMessage;
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
import com.bruynhuis.galago.network.messages.PlayerStateMessage;
import com.bruynhuis.galago.network.messages.PlayerWalkMessage;
import com.bruynhuis.galago.network.messages.PlayerWithPlayerCollisionMessage;
import com.bruynhuis.galago.network.messages.RequestGameListMessage;
import com.bruynhuis.galago.util.Timer;
import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NideBruyn
 */
public abstract class BaseServerApplication extends SimpleApplication implements ConnectionListener, MessageListener<HostedConnection> {

    protected Server server;
    protected String title;
    protected int portTcp;
    protected int portUdp;
    protected int version;

    protected Map<String, NetworkGame> games = new HashMap<>();

    public BaseServerApplication(String title, int portTcp, int portUdp, int version) {
        this.title = title;
        this.portTcp = portTcp;
        this.portUdp = portUdp;
        this.version = version;
        start(JmeContext.Type.Headless);
    }

    protected void registerMessages() {
        Serializer.registerClasses(CreateGameMessage.class,
                JoinGameMessage.class,
                GameListMessage.class,
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
                PlayerRespawnMessage.class,
                ObjectForceMessage.class,
                ObjectLifeTimerMessage.class,
                ObjectCollisionMessage.class,
                PlayerCollisionMessage.class,
                PlayerWithPlayerCollisionMessage.class,
                ObjectDestroyMessage.class,
                PlayerDisableMessage.class);
        log("Registering messages");
    }

    protected void createServer() throws IOException {
        server = Network.createServer(title, version, portTcp, portUdp);
        server.start();
        log("Started and waiting clients");

    }

    protected void registerListeners() {
        if (server != null && server.isRunning()) {
            server.addConnectionListener(this);
            server.addMessageListener(this,
                    CreateGameMessage.class,
                    JoinGameMessage.class,
                    GameListMessage.class,
                    NetworkGameMessage.class,
                    RequestGameListMessage.class,
                    ExitGameMessage.class,
                    PlayerMoveMessage.class,
                    AddObjectMessage.class,
                    ObjectStateMessage.class,
                    PlayerForceMessage.class,
                    PlayerJumpMessage.class,
                    PlayerWalkMessage.class,
                    PlayerDamageMessage.class,
                    PlayerRespawnMessage.class,
                    ObjectForceMessage.class,
                    ObjectLifeTimerMessage.class,
                    PlayerWithPlayerCollisionMessage.class,
                    ObjectDestroyMessage.class);
            log("Listeners registered");
        } else {
            System.err.println("Failed to reigster listeners");
        }
    }

    protected abstract void initMessages();

    protected abstract void initListeners();

    @Override
    public void simpleInitApp() {
        try {
            registerMessages();
            initMessages();
            createServer();
            registerListeners();
            initListeners();

        } catch (IOException ex) {
            Logger.getLogger(BaseServerApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
//
//    @Override
//    public void update() {
//        super.update();
//        
//    }

    protected void log(String text) {
        System.out.println(text);
    }

    @Override
    public void connectionAdded(Server server, HostedConnection conn) {
        log("New connection added: " + conn.getAddress());

    }

    @Override
    public void connectionRemoved(Server server, HostedConnection conn) {
        log("Connection removed for client " + conn.getId());
        removeCreatorGame(conn.getId());

    }

    @Override
    public void messageReceived(HostedConnection source, Message m) {
//        log("Message received, " + m + " from source, " + source.getAddress());

        if (m instanceof RequestGameListMessage) {
            source.send(assembleGameListMessage());

        } else if (m instanceof CreateGameMessage) {
            CreateGameMessage gameMessage = (CreateGameMessage) m;
            log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            log("Creating game with name: " + gameMessage.getGameName() + " by player, " + source.getId());

            enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    //Add the network game
                    NetworkGame networkGame = new NetworkGame(BaseServerApplication.this, UUID.randomUUID().toString(), gameMessage.getGameName(), source.getId());
                    networkGame.setPhysicsEnabled(gameMessage.isPhysicsEnabled());
                    networkGame.setRandomSpawnPoint(gameMessage.isRandomSpawnPoint());
                    networkGame.setSpawnPoints(gameMessage.getSpawnPoints());
                    networkGame.setGravity(gameMessage.getGravity());
                    networkGame.load();

                    games.put(networkGame.getGameId(), networkGame);
                    source.send(new GameCreatedMessage(networkGame.getGameId(), networkGame.getGameName()));

                    return null;
                }
            });

            //Assemble the network game list
            server.broadcast(assembleGameListMessage());

        } else if (m instanceof ExitGameMessage) {
            ExitGameMessage exitGameMessage = (ExitGameMessage) m;

            removeCreatorGame(source.getId());
            removePlayerFromGame(games.get(exitGameMessage.getGameId()), source.getId());

        } else if (m instanceof JoinGameMessage) {
            JoinGameMessage joinGameMessage = (JoinGameMessage) m;

            NetworkGame game = games.get(joinGameMessage.getGameId());
            if (game == null) {
                source.send(new GameNotAvailableMessage(joinGameMessage.getGameId()));
            }

            if (game.getPlayers().size() >= game.getSpawnPoints().size()) {
                source.send(new GamePlayerLimitReachedMessage(joinGameMessage.getGameId()));
                return;
            }

            //If game is available we can now try to join the game
            //1. Pick a random spawnpoint for the player
            enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Vector3f randomPosition = null;
                    if (game.isRandomSpawnPoint()) {
                        randomPosition = game.getSpawnPoints().get(FastMath.nextRandomInt(0, game.getSpawnPoints().size() - 1)).clone();
                        
                    } else {
                        randomPosition = game.getSpawnPoints().get(game.getPlayers().size()).clone();
                        
                    }
                    
                    Quaternion rotation = Quaternion.IDENTITY.clone();

                    //2. Add the player to the network game
                    NetworkPlayer networkPlayer = new NetworkPlayer(game, source.getId(), joinGameMessage.getPlayerName(), randomPosition, rotation);
                    networkPlayer.setPlayerType(joinGameMessage.getPlayerType());
                    networkPlayer.setCollisionType(joinGameMessage.getCollisionType());
                    networkPlayer.setHalfExtends(joinGameMessage.getHalfExtends());
                    networkPlayer.setMass(joinGameMessage.getMass());
                    networkPlayer.setRadius(joinGameMessage.getRadius());
                    networkPlayer.setFriction(joinGameMessage.getFriction());
                    networkPlayer.setRestitution(joinGameMessage.getRestitution());
                    networkPlayer.setPositionLock(joinGameMessage.getPositionLock());
                    networkPlayer.setRotationLock(joinGameMessage.getRotationLock());
                    networkPlayer.setInitialForce(joinGameMessage.getInitialForce());
                    networkPlayer.setInitialGravity(joinGameMessage.getInitialGravity());
                    networkPlayer.setHealth(joinGameMessage.getHealth());
                    networkPlayer.setScore(joinGameMessage.getScore());
                    networkPlayer.setLoot(joinGameMessage.getLoot());
                    game.addPlayer(networkPlayer);

                    return null;
                }
            });

            //3. Broadcast all current player states to all players
//            broadcastAllPlayerStates(game, false);
//            broadcastAllObjectStates(game, false);
        } else if (m instanceof AddObjectMessage) {
            log("Received add object");
            AddObjectMessage addObjectMessage = (AddObjectMessage) m;
            NetworkGame game = games.get(addObjectMessage.getGameId());
            if (game == null) {
                source.send(new GameNotAvailableMessage(addObjectMessage.getGameId()));
            }

            //Add the object to the network game
            enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    NetworkObject networkObject = new NetworkObject(game, UUID.randomUUID().toString(), addObjectMessage.getObjectName(), addObjectMessage.getPosition(), addObjectMessage.getRotation());
                    networkObject.setType(addObjectMessage.getObjectType());
                    networkObject.setCollisionType(addObjectMessage.getCollisionType());
                    networkObject.setHalfExtends(addObjectMessage.getHalfExtends());
                    networkObject.setMass(addObjectMessage.getMass());
                    networkObject.setRadius(addObjectMessage.getRadius());
                    networkObject.setPositionLock(addObjectMessage.getPositionLock());
                    networkObject.setRotationLock(addObjectMessage.getRotationLock());
                    networkObject.setInitialForce(addObjectMessage.getInitialForce());
                    networkObject.setInitialGravity(addObjectMessage.getInitialGravity());
                    networkObject.setFriction(addObjectMessage.getFriction());
                    networkObject.setRestitution(addObjectMessage.getRestitution());
                    game.addObject(networkObject);
                    return null;
                }
            });

            //3. Broadcast all current player states to all players
//            broadcastAllObjectStates(game, false);
        } else if (m instanceof PlayerMoveMessage) {

            PlayerMoveMessage playerMoveMessage = (PlayerMoveMessage) m;

            NetworkGame networkGame = games.get(playerMoveMessage.getGameId());
            if (networkGame != null) {
                NetworkPlayer networkPlayer = networkGame.getPlayers().get(playerMoveMessage.getPlayerId());
                if (networkPlayer != null) {

                    enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            if (networkPlayer.getPhysicsRigidBody() == null) {
                                networkPlayer.setPosition(playerMoveMessage.getPosition());
                                networkPlayer.setRotation(playerMoveMessage.getRotation());

                            } else if (networkPlayer.getPhysicsRigidBody() != null) {
                                networkPlayer.getPhysicsRigidBody().setLinearVelocity(new Vector3f(0, 0, 0));
                                networkPlayer.getPhysicsRigidBody().setAngularVelocity(new Vector3f(0, 0, 0));
                                networkPlayer.getPhysicsRigidBody().clearForces();
                                networkPlayer.getPhysicsRigidBody().setPhysicsLocation(playerMoveMessage.getPosition().clone());
                                networkPlayer.getPhysicsRigidBody().setPhysicsRotation(playerMoveMessage.getRotation().clone());

                            } else if (networkPlayer.getNetworkCharacterControl() != null) {
//                        networkPlayer.getPhysicsRigidBody().setLinearVelocity(new Vector3f(0, 0, 0));
//                        networkPlayer.getPhysicsRigidBody().setAngularVelocity(new Vector3f(0, 0, 0));
//                        networkPlayer.getPhysicsRigidBody().clearForces();
                                networkPlayer.getNetworkCharacterControl().warp(playerMoveMessage.getPosition().clone());

                            }

                            return null;
                        }
                    });

                    //TODO: Possibly need to skip the broadcast for the source client
                    //NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN BBBBBBBBBBBBBBBBBBBBBB
//                    broadcastAllPlayerStates(networkGame, false);
                }

            }

        } else if (m instanceof PlayerJumpMessage) {

            PlayerJumpMessage playerJumpMessage = (PlayerJumpMessage) m;

            NetworkGame networkGame = games.get(playerJumpMessage.getGameId());
            if (networkGame != null) {
                NetworkPlayer networkPlayer = networkGame.getPlayers().get(playerJumpMessage.getPlayerId());
                if (networkPlayer != null) {

                    enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            if (networkPlayer.getNetworkCharacterControl() != null) {
                                networkPlayer.getNetworkCharacterControl().setJumpForce(playerJumpMessage.getForce().clone());
                                networkPlayer.getNetworkCharacterControl().jump();
                            }
                            return null;
                        }
                    });

                }

            }

        } else if (m instanceof PlayerWalkMessage) {

            PlayerWalkMessage playerWalkMessage = (PlayerWalkMessage) m;

            NetworkGame networkGame = games.get(playerWalkMessage.getGameId());
            if (networkGame != null) {
                NetworkPlayer networkPlayer = networkGame.getPlayers().get(playerWalkMessage.getPlayerId());
                if (networkPlayer != null && networkPlayer.getNetworkCharacterControl() != null) {

                    enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            networkPlayer.getNetworkCharacterControl().setWalkDirection(playerWalkMessage.getWalkDirection().clone());
                            networkPlayer.getNetworkCharacterControl().setViewDirection(playerWalkMessage.getViewDirection().clone());
                            return null;
                        }
                    });

                    //TODO: Possibly need to skip the broadcast for the source client
                    //NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN BBBBBBBBBBBBBBBBBBBBBB
//                    broadcastAllPlayerStates(networkGame, false);
                }

            }

        } else if (m instanceof PlayerForceMessage) {

            PlayerForceMessage playerForceMessage = (PlayerForceMessage) m;

            NetworkGame networkGame = games.get(playerForceMessage.getGameId());
            if (networkGame != null) {
                NetworkPlayer networkPlayer = networkGame.getPlayers().get(playerForceMessage.getPlayerId());
                if (networkPlayer != null) {

                    enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            if (networkPlayer.getPhysicsRigidBody() != null) {
                                networkPlayer.getPhysicsRigidBody().clearForces();
                                networkPlayer.getPhysicsRigidBody().setLinearVelocity(playerForceMessage.getForce());

                            } else if (networkPlayer.getNetworkCharacterControl() != null) {
                                networkPlayer.getNetworkCharacterControl().getPhysicsRigidBody().clearForces();
                                networkPlayer.getNetworkCharacterControl().getPhysicsRigidBody().setLinearVelocity(playerForceMessage.getForce());

                            }
                            return null;
                        }
                    });

                    //TODO: Possibly need to skip the broadcast for the source client
                    //NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN BBBBBBBBBBBBBBBBBBBBBB
//                    broadcastAllPlayerStates(networkGame, false);
                }

            }

        } else if (m instanceof PlayerDamageMessage) {

            PlayerDamageMessage playerDamageMessage = (PlayerDamageMessage) m;

            NetworkGame networkGame = games.get(playerDamageMessage.getGameId());
            if (networkGame != null) {
                NetworkPlayer networkPlayer = networkGame.getPlayers().get(playerDamageMessage.getPlayerId());
                if (networkPlayer != null) {

                    enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            networkPlayer.addDamage(playerDamageMessage.getDamage());
//                    broadcastAllPlayerStates(networkGame, false);
                            return null;
                        }
                    });

                }

            }

        } else if (m instanceof PlayerRespawnMessage) {

            PlayerRespawnMessage playerRespawnMessage = (PlayerRespawnMessage) m;

            NetworkGame networkGame = games.get(playerRespawnMessage.getGameId());
            if (networkGame != null) {
                NetworkPlayer networkPlayer = networkGame.getPlayers().get(playerRespawnMessage.getPlayerId());
                if (networkPlayer != null) {
                    log(">>>> Player Respawn");
                    enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Vector3f randomPosition = networkGame.getSpawnPoints().get(FastMath.nextRandomInt(0, networkGame.getSpawnPoints().size() - 1)).clone();
                            Quaternion rotation = Quaternion.IDENTITY.clone();

                            networkPlayer.setPosition(randomPosition);
                            networkPlayer.setRotation(rotation);
                            networkPlayer.setHealth(playerRespawnMessage.getHealth());

                            networkPlayer.respawn();
//                    broadcastAllPlayerStates(networkGame, false);

                            return null;
                        }
                    });

                }

            }

        } else if (m instanceof ObjectForceMessage) {

            ObjectForceMessage objectForceMessage = (ObjectForceMessage) m;

            NetworkGame networkGame = games.get(objectForceMessage.getGameId());
            if (networkGame != null) {
                NetworkObject networkObject = networkGame.getObjects().get(objectForceMessage.getObjectId());
                if (networkObject != null) {

                    if (networkObject.getRigidBodyControl() != null) {
                        enqueue(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                log("object force message on server " + objectForceMessage.getForce());
                                networkObject.getRigidBodyControl().clearForces();
                                networkObject.getRigidBodyControl().setLinearVelocity(objectForceMessage.getForce());
                                return null;
                            }
                        });

                    }

                    //TODO: Possibly need to skip the broadcast for the source client
                    //NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN BBBBBBBBBBBBBBBBBBBBBB
//                    broadcastAllObjectStates(networkGame, false);
                }

            }

        } else if (m instanceof ObjectLifeTimerMessage) {

            ObjectLifeTimerMessage objectLifeTimerMessage = (ObjectLifeTimerMessage) m;

            NetworkGame networkGame = games.get(objectLifeTimerMessage.getGameId());

            if (networkGame != null) {
                NetworkObject networkObject = networkGame.getObjects().get(objectLifeTimerMessage.getObjectId());
                if (networkObject != null) {

                    enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Timer timer = new Timer(objectLifeTimerMessage.getTimeToLive());
                            networkObject.setLifeTimeTimer(timer);
                            timer.start();
                            return null;
                        }
                    });

                }

            }

        } else if (m instanceof ObjectDestroyMessage) {

            ObjectDestroyMessage objectDestroyMessage = (ObjectDestroyMessage) m;
            NetworkGame networkGame = games.get(objectDestroyMessage.getGameId());

            if (networkGame != null) {
                NetworkObject networkObject = networkGame.getObjects().get(objectDestroyMessage.getObjectId());
                if (networkObject != null) {
                    enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            networkObject.setDestroyed(true);
                            return null;
                        }
                    });

                }

            }
        }

    }

    public void broadcastAllPlayerStates(NetworkGame networkGame, boolean reliable) {

        enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                for (Iterator<NetworkPlayer> iterator = networkGame.getPlayers().values().iterator(); iterator.hasNext();) {
                    NetworkPlayer player = iterator.next();

                    if (player.isActive()) {
                        PlayerStateMessage playerStateMessage = new PlayerStateMessage(player.getPlayerId(), player.getPlayerName(), networkGame.getGameId());
                        playerStateMessage.setPlayerType(player.getPlayerType());

                        if (player.getNetworkCharacterControl() != null) {
                            playerStateMessage.setPosition(player.getNetworkCharacterControl().getPhysicsRigidBody().getPhysicsLocation().clone());
                            playerStateMessage.setRotation(player.getPlayerNode().getWorldRotation().clone());
                            playerStateMessage.setOnGround(player.getNetworkCharacterControl().isOnGround());
//                        log("player rotation: " + playerStateMessage.getRotation());

                        } else {
                            playerStateMessage.setPosition(player.getPhysicsRigidBody() == null ? player.getPosition() : player.getPhysicsRigidBody().getPhysicsLocation().clone());
                            playerStateMessage.setRotation(player.getPhysicsRigidBody() == null ? player.getRotation() : player.getPhysicsRigidBody().getPhysicsRotation().clone());
                        }

                        playerStateMessage.setHealth(player.getHealth());
                        playerStateMessage.setKilled(player.isKilled());
                        playerStateMessage.setScore(player.getScore());
                        playerStateMessage.setLoot(player.getLoot());

                        playerStateMessage.setReliable(reliable);
                        
                        if (player.isKilled()) {
                            player.setActive(false);
                            playerStateMessage.setReliable(true);
                        }
                        server.broadcast(Filters.in(getGameConnections(networkGame)), playerStateMessage);
                    }

                }
                return null;
            }
        });

    }

    public void broadcastAllObjectStates(NetworkGame networkGame, boolean reliable) {
        enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                List<String> objectsToRemove = null;
                for (Iterator<String> iterator = networkGame.getObjects().keySet().iterator(); iterator.hasNext();) {
                    String key = iterator.next();
                    NetworkObject object = networkGame.getObjects().get(key);
                    ObjectStateMessage objectStateMessage = new ObjectStateMessage(object.getId(), object.getName(), networkGame.getGameId());
                    objectStateMessage.setObjectType(object.getType());
                    objectStateMessage.setPosition(object.getRigidBodyControl() == null ? object.getPosition() : object.getRigidBodyControl().getPhysicsLocation().clone());
                    objectStateMessage.setRotation(object.getRigidBodyControl() == null ? object.getRotation() : object.getRigidBodyControl().getPhysicsRotation().clone());
                    objectStateMessage.setReliable(reliable);

                    if (object.isDestroyed()) {
                        log("Destroy object on server " + object.getId());
                        if (objectsToRemove == null) {
                            objectsToRemove = new ArrayList<>();
                        }
                        objectsToRemove.add(key);
                        objectStateMessage.setDestroyed(true);
                        objectStateMessage.setReliable(true);
                        object.close();

                    }
                    server.broadcast(Filters.in(getGameConnections(networkGame)), objectStateMessage);
                }

                if (objectsToRemove != null) {
                    networkGame.getObjects().keySet().removeAll(objectsToRemove);

                }
                return null;
            }
        });

    }

    public void broadcastObjectCollision(NetworkGame networkGame, NetworkObject networkObject, NetworkObject networkCollisionObject) {
        ObjectCollisionMessage colObj = new ObjectCollisionMessage(networkObject.getId(), networkGame.getGameId(), networkCollisionObject.getId(), networkCollisionObject.getType());
        server.broadcast(colObj);

    }

    public void broadcastPlayerCollision(NetworkGame networkGame, NetworkPlayer networkPlayer, NetworkObject networkCollisionObject) {
        server.broadcast(new PlayerCollisionMessage(networkPlayer.getPlayerId(), networkGame.getGameId(), networkCollisionObject.getId(), networkCollisionObject.getType()));

    }

    public void broadcastPlayerWithPlayerCollision(NetworkGame networkGame, NetworkPlayer networkPlayer, NetworkPlayer networkCollisionPlayer) {
        server.broadcast(new PlayerWithPlayerCollisionMessage(networkPlayer.getPlayerId(), networkGame.getGameId(), networkCollisionPlayer.getPlayerId()));

    }

    protected GameListMessage assembleGameListMessage() {
        GameListMessage gameListMessage = new GameListMessage();

        if (games != null && games.size() > 0) {
            NetworkGameMessage[] messages = new NetworkGameMessage[games.size()];

            int i = 0;
            for (Iterator<NetworkGame> iterator = games.values().iterator(); iterator.hasNext();) {
                NetworkGame game = iterator.next();
                messages[i] = new NetworkGameMessage(game.getGameId(), game.getGameName());
                i++;
            }

            gameListMessage.setGames(messages);

        }

        return gameListMessage;
    }

    private void removeCreatorGame(int creatorId) {
        
        if (games != null && games.size() > 0) {
            log("Remove game that was created by player, " + creatorId);
            enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (Iterator<NetworkGame> iterator = games.values().iterator(); iterator.hasNext();) {
                        NetworkGame game = iterator.next();
                        if (game.getGameCreatorId() == creatorId) {
                            game.close();
                            games.remove(game.getGameId());
                            server.broadcast(Filters.in(getGameConnections(game)), new GameClosedMessage(game.getGameId()));
                            break;
                        }
                    }
                    return null;
                }
            });
        }

    }

    private Collection<HostedConnection> getGameConnections(NetworkGame game) {
        List<HostedConnection> conns = new ArrayList<>();
        for (Iterator<NetworkPlayer> iterator = game.getPlayers().values().iterator(); iterator.hasNext();) {
            NetworkPlayer player = iterator.next();
            HostedConnection con = server.getConnection(player.getPlayerId());
            if (con != null) {
                conns.add(con);

            }
        }
        return conns;
    }

    private void removePlayerFromGame(NetworkGame networkGame, int playerId) {
        if (networkGame != null) {

            enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    NetworkPlayer player = networkGame.getPlayers().get(playerId);
                    if (player != null) {
                        networkGame.getPlayers().remove(playerId);
                        player.close();
                        server.broadcast(Filters.in(getGameConnections(networkGame)), new PlayerLeftGameMessage(playerId, player.getPlayerName(), networkGame.getGameName()));
                    }
                    return null;
                }
            });

        }

    }

    @Override
    public void destroy() {
        if (server != null) {
            server.close();
        }
        super.destroy();
    }

}
