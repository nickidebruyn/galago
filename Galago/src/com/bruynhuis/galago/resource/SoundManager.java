/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.resource;

import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.scene.Node;
import java.util.HashMap;
import java.util.Map;
import com.bruynhuis.galago.app.BaseApplication;

/**
 * The SoundManager will manage SoundFX and Music in the game. It is also a
 * cache for sounds.
 *
 * @author nidebruyn
 */
public class SoundManager {

    protected BaseApplication application;
    protected Node soundNode;
    protected Map<String, AudioNode> music = new HashMap<String, AudioNode>();
    protected Map<String, AudioNode> soundFx = new HashMap<String, AudioNode>();
    protected Map<String, String> midiTracks = new HashMap<String, String>();
    protected boolean muteSound = false;
    protected boolean muteMusic = false;
    protected float musicVolume = 0.3f;
    protected float fxVolume = 0.7f;
    protected float musicSpeed = 0.5f;

    public SoundManager(BaseApplication simpleApplication, Node soundNode) {
        this.application = simpleApplication;
        this.soundNode = soundNode;
    }

    public void destroy() {
        stop();
        music.clear();
        soundFx.clear();
    }

    /**
     * Called when all music and sounds must be stopped
     */
    public void stopSound() {

        for (Map.Entry<String, AudioNode> entry : soundFx.entrySet()) {
            String string = entry.getKey();
            AudioNode audioNode = entry.getValue();
            audioNode.stop();
        }

    }

    public void stopMusic() {
        for (Map.Entry<String, AudioNode> entry : music.entrySet()) {
            String string = entry.getKey();
            AudioNode audioNode = entry.getValue();
            audioNode.stop();
        }

        if (application.hasMidiPlayer()) {
            application.getMidiPlayer().stop();
        }

    }

    /**
     * Called when all music and sounds must be stopped
     */
    public void stop() {
        stopMusic();
        stopSound();

    }

    /**
     * mute the sounds or not
     *
     * @param mute
     */
    public void muteSound(boolean mute) {
        if (this.muteSound != mute) {
            this.muteSound = mute;
            this.stopSound();
        }


    }

    public void muteMusic(boolean mute) {
        if (this.muteMusic != mute) {
            this.muteMusic = mute;
            this.stopMusic();
            if (application.hasMidiPlayer()) {
                application.getMidiPlayer().pause();
            }
        }

    }

    public void muteAll(boolean mute) {
        if (this.muteMusic != mute && this.muteSound != mute) {
            this.muteMusic = mute;
            this.muteSound = mute;
            this.stop();

            if (application.hasMidiPlayer()) {
                application.getMidiPlayer().stop();
            }
        }

    }

    /**
     * Must be called to cash music that wants to be loaded in the system.
     *
     * @param modelPath
     */
    public void loadMusic(String name, String musicPath) {
        //First we check if the music wasn't already loaded.
        if (music.get(name) != null) {
            return;
        }

        if (musicPath.endsWith(".mid") || musicPath.endsWith(".MID")) {
            midiTracks.put(name, musicPath);
            application.getMidiPlayer().setLooping(true);

        } else {
            AudioNode audioNode = new AudioNode(application.getAssetManager(), musicPath, false);
            audioNode.setPositional(false);
            audioNode.setLooping(true);  // activate continuous playing
            audioNode.setVolume(0);
//            audioNode.play();
            music.put(name, audioNode);
        }


    }

    /**
     * Play a music track
     *
     * @param name
     */
    public void playMusic(String name) {
        if (muteMusic) {
            return;
        }

        AudioNode audioNode = music.get(name);
        if (audioNode != null) {
            if (!audioNode.getStatus().equals(AudioSource.Status.Playing)) {
                audioNode.play(); // play continuously!
            } else if (audioNode.getStatus().equals(AudioSource.Status.Paused)) {
                audioNode.play(); // play continuously!
            }
        } else {
            String path = midiTracks.get(name);
//            System.out.println(path);
            if (path != null) {
                application.getMidiPlayer().open(path);
                application.getMidiPlayer().play();

            }
        }


    }

    public void stopMusic(String name) {
        AudioNode audioNode = music.get(name);
        if (audioNode != null) {
//            if (audioNode.getStatus().equals(AudioSource.Status.Playing)) {
            audioNode.stop();
//            }
        } else {
            String path = midiTracks.get(name);
            if (path != null) {
                application.getMidiPlayer().stop();
            }
        }
    }

    public void pauseMusic(String name) {
        AudioNode audioNode = music.get(name);
        if (audioNode != null) {
//            if (audioNode.getStatus().equals(AudioSource.Status.Playing)) {
            audioNode.pause();
//            }
        } else {
            String path = midiTracks.get(name);
            if (path != null && application.getMidiPlayer().isPlaying()) {
                application.getMidiPlayer().pause();
            }
        }
    }

    public void resumeMusic(String name) {
        AudioNode audioNode = music.get(name);
        if (audioNode != null) {
//            if (audioNode.getStatus().equals(AudioSource.Status.Paused)) {
            audioNode.play();
//            }
        } else {
            String path = midiTracks.get(name);
            if (path != null && !application.getMidiPlayer().isPlaying()) {
                application.getMidiPlayer().play();
            }
        }
    }

    /**
     * Set the musics volume softer or harder
     *
     * @param name
     * @param volume
     */
    public void setMusicVolume(String name, float volume) {
        AudioNode audioNode = music.get(name);
        if (audioNode != null) {
            if (!muteMusic) {
                audioNode.setVolume(volume);
            }
        } else {
            String path = midiTracks.get(name);
            if (path != null) {
                application.getMidiPlayer().setVolume(volume);
            }
        }

    }

    public void setSoundVolume(String name, float volume) {
        AudioNode audioNode = soundFx.get(name);
        if (audioNode != null && !muteSound) {
            audioNode.setVolume(volume);
        }
    }

    /**
     * Resets the music volume
     *
     * @param name
     */
    public void resetMusicVolume(String name) {
        AudioNode audioNode = music.get(name);
        if (audioNode != null) {
            if (!muteMusic) {
                audioNode.setVolume(musicVolume);
            }
        } else {
            String path = midiTracks.get(name);
            if (path != null) {
                application.getMidiPlayer().setVolume(musicVolume);
            }
        }
    }

    /**
     * Set the musics volume softer or harder
     *
     * @param name
     * @param volume
     */
    public void setMusicSpeed(String name, float speed) {
        AudioNode audioNode = music.get(name);
        if (audioNode != null && !muteMusic) {
            audioNode.setPitch(speed);
        }
    }

    /**
     * Resets the music volume
     *
     * @param name
     */
    public void resetMusicSpeed(String name) {
        AudioNode audioNode = music.get(name);
        if (audioNode != null && !muteMusic) {
            audioNode.setPitch(musicSpeed);
        }
    }

    /**
     * Must be called to cash soundfx that wants to be loaded in the system.
     *
     * @param modelPath
     */
    public void loadSoundFx(String name, String soundPath) {
        //First we check if the sound wasn't already loaded.
        if (soundFx.get(name) != null) {
            return;
        }

        AudioNode audioNode = new AudioNode(application.getAssetManager(), soundPath, false);
        audioNode.setPositional(false);
        audioNode.setLooping(false);
        audioNode.setVolume(fxVolume);
        soundFx.put(name, audioNode);

    }

    /**
     * Play a sound track
     *
     * @param name
     */
    public void playSound(String name) {
        if (muteSound) {
            return;
        }

        AudioNode audioNode = soundFx.get(name);
        audioNode.playInstance(); // play once!

    }

    public boolean isPlaying(String name) {
        AudioNode audioNode = soundFx.get(name);
        if (audioNode != null) {
            return audioNode.getStatus().equals(AudioSource.Status.Playing);
        } else {
            return false;
        }
    }

    public boolean isMusicPlaying(String name) {
        AudioNode audioNode = music.get(name);
        if (audioNode != null) {
            return audioNode.getStatus().equals(AudioSource.Status.Playing);
        } else {
            return false;
        }
    }

    /**
     * Stop the sound fx
     *
     * @param name
     */
    public void stopSound(String name) {
        AudioNode audioNode = soundFx.get(name);
        audioNode.stop();
    }

    public void setSoundPitch(String name, float pitch) {
        AudioNode audioNode = soundFx.get(name);
        audioNode.setPitch(pitch);
    }

    public void setMusicPitch(String name, float pitch) {
        AudioNode audioNode = music.get(name);
        audioNode.setPitch(pitch);
    }

    public AudioNode getSoundFXNode(String name) {
        if (name == null) {
            return null;
        }
        AudioNode audioNode = soundFx.get(name);
        return audioNode.clone();
    }
}
