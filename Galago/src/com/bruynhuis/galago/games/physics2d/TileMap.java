/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.physics2d;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author NideBruyn
 */
public class TileMap implements Serializable {
   
    private ArrayList<Tile> tiles = new ArrayList<Tile>();
    private String uid;
    private String name;
    private String description;
    private boolean saved = false;
    private String theme;
    private Properties properties;
    private int time = 0;
    private boolean night = false;

    public TileMap() {
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(ArrayList<Tile> tiles) {
        this.tiles = tiles;
    }
    
    public boolean isSaved() {
        return saved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isNight() {
        return night;
    }

    public void setNight(boolean night) {
        this.night = night;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
}
