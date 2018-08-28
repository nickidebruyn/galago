/**
 * Copyright (c) 2014, jMonkeyEngine All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.ai.agents.util.control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.AgentExceptions;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.ai.agents.util.GameEntityExceptions;
import com.jme3.ai.monkeystuff.weapon.AbstractWeapon;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.LinkedList;
import java.util.List;

/**
 * Class with information about agents and consequences of their behaviors in
 * game. It is not necessary to use it but it enables easier game status
 * updates. Contains agents and game entities and provides generic ai control.
 *
 * @author Tihomir Radosavljević
 * @version 2.1.1
 */
public class MonkeyBrainsAppState extends AbstractAppState {

    /**
     * Status of game with agents.
     */
    private boolean inProgress = false;
    /**
     * Indicator if the agent in same team can damage each other.
     */
    protected boolean friendlyFire = true;
    /**
     * Application to which this game is attached.
     */
    protected Application app;
    /**
     * Controls for game.
     */
    protected GameControl gameControl;
    /**
     * Hit point controls for game.
     */
    protected HitPointsControl hitPointsControl;
    /**
     * List of all agents that are active in game.
     */
    protected List<Agent> agents;
    /**
     * List of all GameEntities in game except for agents.
     */
    protected List<GameEntity> gameEntities;
    /**
     * Used internaly for difference between game entities.
     */
    private int idCounter;
    /**
     * Used internaly for difference between agents.
     */
    private int idCounterAgent;
    /**
     * Maximum number of agents supported by framework.
     */
    public static final int MAX_NUMBER_OF_AGENTS = 1000;

    protected MonkeyBrainsAppState() {
        agents = new LinkedList<Agent>();
        gameEntities = new LinkedList<GameEntity>();
        idCounter = MAX_NUMBER_OF_AGENTS + 1;
    }

    /**
     * Method for getting new available id for game entities.
     *
     * @return unique id
     */
    private int setIdCouterToGameEntity() {
        if (gameEntities.size() >= Integer.MAX_VALUE - MAX_NUMBER_OF_AGENTS - 1) {
            throw new GameEntityExceptions.MaxGameEntitiesException();
        }
        while (!isAvailable(gameEntities, idCounter)) {
            if (idCounter < Integer.MAX_VALUE) {
                idCounter++;
            } else {
                idCounter = MAX_NUMBER_OF_AGENTS;
            }
        }
        return idCounter;
    }

    /**
     * Method for getting new available id for agents.
     *
     * @return unique id
     */
    private int setIdCounterToAgent() {
        if (agents.size() >= MAX_NUMBER_OF_AGENTS - 1) {
            throw new AgentExceptions.MaxAgentsException();
        }
        while (!isAvailable(agents, idCounterAgent)) {
            if (idCounterAgent < MAX_NUMBER_OF_AGENTS - 1) {
                idCounterAgent++;
            } else {
                idCounterAgent = 0;
            }
        }
        return idCounterAgent;
    }

    /**
     * Helper function for checking if some specific id is available.
     *
     * @param list
     * @param id
     * @return
     */
    private boolean isAvailable(List list, int id) {
        for (int i = 0; i < list.size(); i++) {
            if (((GameEntity) list.get(i)).getId() == id) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adding agent to game. It will be automatically updated when game is
     * updated, and agent's position will be one set into Spatial.
     *
     * @param agent agent which is added to game
     */
    public void addAgent(Agent agent) {
        agents.add(agent);
        agent.setId(setIdCounterToAgent());
        if (inProgress) {
            agent.start();
        }
        //rootNode.attachChild(agent.getSpatial());
    }

    /**
     * Adding agent to game. It will be automatically updated when game is
     * updated.
     *
     * @param agent agent which is added to game
     * @param position position where spatial should be added
     */
    public void addAgent(Agent agent, Vector3f position) {
        agent.setLocalTranslation(position);
        agents.add(agent);
        agent.setId(setIdCounterToAgent());
        if (inProgress) {
            agent.start();
        }
//        rootNode.attachChild(agent.getSpatial());
    }

    /**
     * Adding agent to game. It will be automatically updated when game is
     * updated.
     *
     * @param agent agent which is added to game
     * @param x X coordinate where spatial should be added
     * @param y Y coordinate where spatial should be added
     * @param z Z coordinate where spatial should be added
     */
    public void addAgent(Agent agent, float x, float y, float z) {
        agent.setLocalTranslation(x, y, z);
        agents.add(agent);
        agent.setId(setIdCounterToAgent());
        if (inProgress) {
            agent.start();
        }
//        rootNode.attachChild(agent.getSpatial());
    }

    /**
     * Removing agent from list of agents to be updated and its spatial from
     * game.
     *
     * @param agent agent who should be removed
     */
    public void removeAgent(Agent agent) {
        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).equals(agent)) {
                agents.get(i).stop();
                agents.get(i).getSpatial().removeFromParent();
                agents.remove(i);
                break;
            }
        }
    }

    /**
     * Disabling agent. It means from agent will be dead and won't updated.
     *
     * @param agent
     */
    public void disableAgent(Agent agent) {
        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).equals(agent)) {
                agents.get(i).stop();
                break;
            }
        }
    }

    /**
     * Method that will update all alive agents and fired bullets while active.
     */
    @Override
    public void update(float tpf) {
        if (!inProgress) {
            return;
        }
        for (int i = 0; i < agents.size(); i++) {
            agents.get(i).update(tpf);
        }
        for (int i = 0; i < gameEntities.size(); i++) {
            gameEntities.get(i).update(tpf);
        }
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public List<GameEntity> getGameEntities() {
        return gameEntities;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    /**
     * Decrease hit points of target.
     *
     * @see
     * HitPointsControl#decreaseHitPoints(com.jme3.ai.agents.util.GameEntity,
     * float)
     * @param target game entity who is being attacked
     * @param weapon weapon with which is target being attacked
     */
    public void decreaseHitPoints(GameEntity target, AbstractWeapon weapon) {
        decreaseHitPoints(target, weapon.getAttackDamage());
    }

    /**
     * Game entities can be boulders, bulets etc. This method enables destroying
     * one bullet with another...
     *
     * @param target
     * @param damage
     */
    public void decreaseHitPoints(GameEntity target, float damage) {
        try {
            hitPointsControl.decreaseHitPoints(target, damage);
        } catch (NullPointerException e) {
            throw new NullPointerException("HitPointsControl is not set.");
        }
    }

    public void addGameEntity(GameEntity gameEntity) {
        gameEntities.add(gameEntity);
        gameEntity.setId(setIdCouterToGameEntity());
    }

    public void removeGameEntity(GameEntity gameEntity) {
        gameEntity.getSpatial().removeFromParent();
        gameEntities.remove(gameEntity);
    }

    public static MonkeyBrainsAppState getInstance() {
        return GameHolder.INSTANCE;
    }

    public GameControl getGameControl() {
        return gameControl;
    }

    public void setGameControl(GameControl gameControl) {
        this.gameControl = gameControl;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    private static class GameHolder {

        private static final MonkeyBrainsAppState INSTANCE = new MonkeyBrainsAppState();
    }

    public void start() {
        inProgress = true;
        for (Agent agent : agents) {
            agent.start();
        }
    }

    public void stop() {
        inProgress = false;
        for (Agent agent : agents) {
            agent.stop();
        }
    }

    public Application getApp() {
        return app;
    }

    public void setApp(Application app) {
        this.app = app;
    }

    public HitPointsControl getHitPointsControl() {
        return hitPointsControl;
    }

    public void setHitPointsControl(HitPointsControl hitPointsControl) {
        this.hitPointsControl = hitPointsControl;
    }
}
