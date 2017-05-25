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
    
    private String terrainName;
    private String objectName;
    private int xPos;
    private int zPos;
    private int objectAngle = 0;
    private int terrainAngle = 0;
    private boolean walkable = false;
    private transient Spatial terrainSpatial;
    private transient Spatial objectSpatial;

    public Tile() {

    }

    public int getTerrainAngle() {
        return terrainAngle;
    }

    public void setTerrainAngle(int terrainAngle) {
        this.terrainAngle = terrainAngle;
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

    public int getObjectAngle() {
        return objectAngle;
    }

    public void setObjectAngle(int objectAngle) {
        this.objectAngle = objectAngle;
    }


    public String getTerrainName() {
        return terrainName;
    }

    public void setTerrainName(String terrainName) {
        this.terrainName = terrainName;
    }

    public Spatial getTerrainSpatial() {
        return terrainSpatial;
    }

    public void setTerrainSpatial(Spatial terrainSpatial) {
        this.terrainSpatial = terrainSpatial;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    @Override
    public String toString() {
        return "Tile{" + "terrainName=" + terrainName + ", objectName=" + objectName + ", xPos=" + xPos + ", zPos=" + zPos + ", terrainAngle=" + terrainAngle + ", objectAngle=" + objectAngle + ", walkable=" + walkable + '}';
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Spatial getObjectSpatial() {
        return objectSpatial;
    }

    public void setObjectSpatial(Spatial objectSpatial) {
        this.objectSpatial = objectSpatial;
    }

    
    
}
