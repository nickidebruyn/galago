/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d;

import com.jme3.scene.Spatial;
import java.io.Serializable;

/**
 *
 * @author NideBruyn
 */
public class Tile implements Serializable {
    
    private float xPos;
    private float yPos;
    private float zPos;
    private String uid;
    
    private transient Spatial spatial;

    public Tile() {
    }
    
    public Tile(float xPos, float yPos, float zPos, String uid) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.uid = uid;
    }

    public float getxPos() {
        return xPos;
    }

    public void setxPos(float xPos) {
        this.xPos = xPos;
    }

    public float getyPos() {
        return yPos;
    }

    public void setyPos(float yPos) {
        this.yPos = yPos;
    }

    public float getzPos() {
        return zPos;
    }

    public void setzPos(float zPos) {
        this.zPos = zPos;
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
}
