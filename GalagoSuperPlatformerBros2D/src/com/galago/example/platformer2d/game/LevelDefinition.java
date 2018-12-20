/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.game;

/**
 *
 * @author Nidebruyn
 */
public class LevelDefinition {
    
    private int uid;
    private String levelName;
    private String levelFile;
    private String description;
    private int time;

    public LevelDefinition(int uid, String levelName, String levelFile, String description, int time) {
        this.uid = uid;
        this.levelName = levelName;
        this.levelFile = levelFile;
        this.description = description;
        this.time = time;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLevelFile() {
        return levelFile;
    }

    public void setLevelFile(String levelFile) {
        this.levelFile = levelFile;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

        
}
