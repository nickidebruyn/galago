/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.tilemap;

import com.jme3.scene.Spatial;
import java.io.Serializable;

/**
 * A tile represents a quad on the surface. This will be used for navigation and object placement.
 *
 * @author nidebruyn
 */
public class Tile implements Serializable {
    
    private String item;
    private String enemyItem;
    private int xPos;
    private int zPos;
    private int itemAngle = 0;
    private boolean walkable = false;
    private transient Spatial itemSpatial;
    private transient Spatial enemySpatial;

    public Tile() {

    }

    public int getxPos() {
        return xPos;
    }

    public int getzPos() {
        return zPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setzPos(int zPos) {
        this.zPos = zPos;
    }

    public int getItemAngle() {
        return itemAngle;
    }

    public void setItemAngle(int itemAngle) {
        this.itemAngle = itemAngle;
    }


    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Spatial getItemSpatial() {
        return itemSpatial;
    }

    public void setItemSpatial(Spatial itemSpatial) {
        this.itemSpatial = itemSpatial;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    @Override
    public String toString() {
        return "Tile{" + "item=" + item + ", xPos=" + xPos + ", zPos=" + zPos + ", itemAngle=" + itemAngle + ", walkable=" + walkable + '}';
    }

    public String getEnemyItem() {
        return enemyItem;
    }

    public void setEnemyItem(String enemyItem) {
        this.enemyItem = enemyItem;
    }

    public Spatial getEnemySpatial() {
        return enemySpatial;
    }

    public void setEnemySpatial(Spatial enemySpatial) {
        this.enemySpatial = enemySpatial;
    }

    
    
}
