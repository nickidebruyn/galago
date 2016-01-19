/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sound;

/**
 * The interface the represents a MidiPlayer.
 * 
 * @author NideBruyn
 */
public interface MidiPlayer {

    public void open(String fileName);

    public boolean isLooping();

    public void setLooping(boolean loop);

    public void play();

    public void pause();

    public void stop();

    public void release();

    public boolean isPlaying();

    public void setVolume(float volume);
}