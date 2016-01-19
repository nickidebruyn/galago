/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sound;

import com.jme3.system.JmeSystem;
import java.io.InputStream;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

/**
 * This is the implementation for a Desktop MidiPlayer.
 * @author NideBruyn
 */
public class DesktopMidiPlayer implements MidiPlayer {

    private Sequence sequence;
    private Sequencer sequencer;

    public DesktopMidiPlayer() {

        try {
            sequencer = MidiSystem.getSequencer();
        } catch (MidiUnavailableException e) {
            log("Error opening midi device, " + e.getMessage());
        }

    }

    private void log(String text) {
        System.err.println(text);
    }

    @Override
    public void open(String fileName) {

        InputStream is = null;
        try {
            is = JmeSystem.getResourceAsStream("/" + fileName);
            sequence = MidiSystem.getSequence(is);
            sequencer.open();
            sequencer.setSequence(sequence);

        } catch (Exception e) {
            log("Error opening midi: " + fileName + "." + e.getMessage());
        }
    }

    @Override
    public boolean isLooping() {
        if (sequencer != null) {
            return sequencer.getLoopCount() != 0;
        }
        return false;
    }

    @Override
    public void setLooping(boolean loop) {
        if (sequencer != null) {
            if (!loop) {
                sequencer.setLoopCount(0);
                return;
            }
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        }
    }

    @Override
    public void play() {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.start();
        }
    }

    @Override
    public void pause() {
        stop();
    }

    @Override
    public void stop() {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.stop();
        }
    }

    @Override
    public void release() {
        if (sequencer != null) {
            sequencer.close();
        }
    }

    @Override
    public boolean isPlaying() {
        return sequencer.isRunning();
    }

    @Override
    public void setVolume(float volume) {
        //Not implemented
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            ShortMessage volumeMessage = new ShortMessage();

            for (int i = 0; i < 16; i++) {
                volumeMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, (int)volume*10);
                MidiSystem.getReceiver().send(volumeMessage, -1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
