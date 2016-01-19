/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ai;

/**
 * A heuristic that uses the tile that is closest to the target as the next best
 * tile.
 *
 * @author ndebruyn
 */
public class ClosestHeuristic implements AStarHeuristic {

    /**
     * @see AStarHeuristic#getCost(TileBasedMap, Mover, int, int, int, int)
     */
    public float getCost(TileBasedMap map, Mover mover, int x, int y, int tx, int ty) {
        float dx = tx - x;
        float dy = ty - y;

        float result = (float) (Math.sqrt((dx * dx) + (dy * dy)));

        return result;
    }
}