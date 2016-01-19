/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.save;

import java.io.Serializable;
import java.util.Properties;

/**
 * This class will be serialized when the level is saved.
 *
 * @author NideBruyn
 */
public class LevelData implements Serializable {

    private String uid;
    private String name;
    private String fileName;
    private byte[] snapshot;
    private int score = 0;
    private int time = 0;
    private boolean completed = false;
    private Properties properties = new Properties();
    private boolean saved = false;

    public LevelData() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public byte[] getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(byte[] snapshot) {
        this.snapshot = snapshot;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
}
