/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.ai.navmesh;

import com.jme3.math.Vector3f;
import com.jme3.ai.navmesh.Path.Waypoint;
import java.util.ArrayList;
import java.util.List;

/**
 * Debug nformation from a pathfinding search.
 * 
 * @author sploreg
 */
public class DebugInfo {
    private List<Vector3f> preOptWaypoints = new ArrayList<Vector3f>();
    private List<Cell> plannedCells = new ArrayList<Cell>();
    private List<Vector3f> wpPositions;
    private Vector3f startPos, endPos;
    private Waypoint failedVisibleWaypoint;
    private Waypoint farthestTestedWaypoint;
    private Cell failedCell;
    private List<Cell> passedCells = new ArrayList<Cell>();
    private Cell endingCell;
    private Vector3f startLocation;

    public void reset() {
        if (preOptWaypoints != null)
            preOptWaypoints.clear();
        if (plannedCells != null)
            plannedCells.clear();
        if (wpPositions != null)
            wpPositions.clear();
        startPos = null;
        endPos = null;
        failedVisibleWaypoint = null;
        farthestTestedWaypoint = null;
        failedCell = null;
        if (passedCells != null)
            passedCells.clear();
        endingCell = null;
        startLocation = null;
    }
    
    public void setWaypointPositions(List<Vector3f> wpPositions) {
        this.wpPositions = wpPositions;
    }

    public List<Vector3f> getWpPositions() {
        return wpPositions;
    }

    public Vector3f getEndPos() {
        return endPos;
    }

    public void setEndPos(Vector3f endPos) {
        this.endPos = endPos;
    }

    public Vector3f getStartPos() {
        return startPos;
    }

    public void setStartPos(Vector3f startPos) {
        this.startPos = startPos;
    }
    
    public void setFailedVisibleWaypoint(Waypoint testPoint) {
        this.failedVisibleWaypoint = testPoint;
    }

    public Waypoint getFailedVisibleWaypoint() {
        return failedVisibleWaypoint;
    }

    public void setFarthestTestedWaypoint(Waypoint farthest) {
        this.farthestTestedWaypoint = farthest;
    }

    public Waypoint getFarthestTestedWaypoint() {
        return farthestTestedWaypoint;
    }
    
    void setFailedCell(Cell failed) {
        this.failedCell = failed;
    }

    void addPassedCell(Cell passed) {
        this.passedCells.add(passed);
    }

    void setEndingCell(Cell ending) {
        this.endingCell = ending;
    }

    public Cell getEndingCell() {
        return endingCell;
    }

    public Cell getFailedCell() {
        return failedCell;
    }

    public List<Cell> getPassedCells() {
        return passedCells;
    }

    public void setStartLocation(Vector3f loc) {
        this.startLocation = loc;
    }

    public Vector3f getStartLocation() {
        return startLocation;
    }

    void addPlannedCell(Cell cell) {
        plannedCells.add(cell);
    }

    public List<Cell> getPlannedCells() {
        return plannedCells;
    }

    void addPreOptWaypoints(Vector3f wp) {
        preOptWaypoints.add(wp);
    }

    public List<Vector3f> getPreOptWaypoints() {
        return preOptWaypoints;
    }
    
}
