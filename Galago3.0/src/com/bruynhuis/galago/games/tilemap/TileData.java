/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.tilemap;

import java.io.Serializable;

/**
 *
 * @author nidebruyn
 */
public class TileData implements Serializable {

    private Tile map[][];

    public Tile[][] getMap() {
        return map;
    }

    public void setMap(Tile[][] map) {
        this.map = map;
    }
    
}
