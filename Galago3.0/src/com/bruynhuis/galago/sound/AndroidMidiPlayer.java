/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This is the implementation for an Android MidiPlayer.
 * @author NideBruyn
 */
public class AndroidMidiPlayer implements MidiPlayer {
    
    private MediaPlayer mediaPlayer;
    private Context context;
    private boolean looping;
    private float volume;
    
    public AndroidMidiPlayer(Context context) {
        this.context = context;
        this.mediaPlayer = new MediaPlayer();

        this.looping = true;
        this.volume = 0.5f;
    }

    @Override
    public void open(String fileName) {
        reset();

        try {
            AssetFileDescriptor afd = context.getAssets().openFd(fileName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mediaPlayer.prepare();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log(e.getMessage());
            
        } catch (IOException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }
    
    private void log(String text) {
        System.err.println(text);
    }
    
    //TODO: This should probably be replaced with something better.
    //I had to reset the player to avoid error when
    //opening a second midi file.
    private void reset() {
        mediaPlayer.reset();
        mediaPlayer.setLooping(looping);
        setVolume(volume);
    }

    @Override
    public boolean isLooping() {
        return mediaPlayer.isLooping();
    }

    @Override
    public void setLooping(boolean loop) {
        mediaPlayer.setLooping(loop);
    }

    @Override
    public void play() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void release() {
        mediaPlayer.release();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume, volume);
    }
    
}
