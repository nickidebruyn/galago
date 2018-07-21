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
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.input.FlyByCamera;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * Base interface for game controls used in game.
 *
 * @author Tihomir Radosavljević
 * @version 1.0.1
 */
public interface GameControl {

    /**
     * Add all inputManagerMapping that will player use.
     */
    public void setInputManagerMapping();
    /**
     * Add all camera settings that will be used in game.
     * @param cam 
     */
    public void setCameraSettings(Camera cam);
    /**
     * Add all fly camera settings that will be used in game.
     * @param flyCam 
     */
    public void setFlyCameraSettings(FlyByCamera flyCam);
    /**
     * Method for marking the end of game. Should also stop the game.
     * @see Game#stop() 
     * @return 
     */
    public boolean finish();
    /**
     * Calculating if the agent won the game.
     * @param agent
     * @return 
     */
    public boolean win(Agent agent);
    /**
     * Restarting all game parameters.
     */
    public void restart();   
    /**
     * Method for creating entities in given area.
     * @param gameEntity entity that should be created
     * @param area where entity will be created
     */
    public void spawn(GameEntity gameEntity, Vector3f... area);
}
