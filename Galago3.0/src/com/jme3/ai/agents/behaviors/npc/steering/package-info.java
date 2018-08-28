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
/**
 * Steer behaviors. 
 * 
 * Basic steering behavior ai structure provided by Tihomir Radosavljević. 
 * Incorporate full steer behavior project into MonkeyBrains by Jesús Martín Berlanga. <br> <br>
 * 
 * References: <br>
 *  - Craig W. Reynolds: "Steering Behaviors For Autonomous Characters" <br>
 *  - OpenSteer <br> 
 * <br>
 * 
 * For more information visit: http://jmesteer.bdevel.org/
 * <br><br>
 * 
 * If you find any bug please contact with me (Jesús) and I will be glad to fix it.
 * You will find my email at the website mentioned before.
 * 
 * @version 3.1.0, 26/10/2014
 * <br><br><br>
 * 
 * 
 * 
 * ////////////////////////// <br>
 * ////////Changelog///////// <br>
 * ////////////////////////// <br> 
 * <br>
 * - 3.1.0, 26/10/2014, Jesús Martín Berlanga: <br>
 * Wander behavior fixes <br><br>
 * 
 * - 3.0.2, 9/10/2014, Tihomir Radosavljević: <br>
 * Some steers accepts game entities instead of agents <br><br>
 * Official integration with MonkeyBrains. Naming and style conventions to 
 * follow the same structure. <br><br>
 * 
 * - 3.0.1, 20/8/2014, Jesús Martín Berlanga: <br>
 * Small javadoc fixes <br><br>
 * 
 * - 3.0, 17/8/2014, Jesús Martín Berlanga: <br>
 * First official release <br><br>
 * 
 * ...
 * <br><br><br>
 * 
 * 
 * 
 * @author Jesús Martin Berlanga
 * @author Tihomir Radosavljević
 */
package com.jme3.ai.agents.behaviors.npc.steering;