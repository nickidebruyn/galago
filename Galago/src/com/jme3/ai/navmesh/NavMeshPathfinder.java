package com.jme3.ai.navmesh;

import com.jme3.ai.navmesh.Path.Waypoint;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.ai.navmesh.Cell.ClassifyResult;
import com.jme3.ai.navmesh.Cell.PathResult;
import com.jme3.ai.navmesh.Line2D.LineIntersect;
import java.util.ArrayList;
import java.util.List;

public class NavMeshPathfinder {

    private NavMesh navMesh;
    private Path path = new Path();
    private float entityRadius;
    private Vector2f currentPos = new Vector2f();
    private Vector3f currentPos3d = new Vector3f();
    private Cell currentCell;
    private Vector2f goalPos;
    private Vector3f goalPos3d;
    private Cell goalCell;
    private Waypoint nextWaypoint;
    /**
     * path finding data...
     */
    private volatile int sessionID = 0;
    private volatile Heap heap = new Heap();

    public NavMeshPathfinder(NavMesh navMesh) {
        this.navMesh = navMesh;
    }

    public Vector3f getPosition() {
        return currentPos3d;
    }

    public void setPosition(Vector3f position) {
        this.currentPos3d.set(position);
        this.currentPos.set(currentPos3d.x, currentPos3d.z);

        // should probably have this here:
        //currentCell = navMesh.findClosestCell(newPos2d);
    }

    public float getEntityRadius() {
        return entityRadius;
    }

    public void setEntityRadius(float entityRadius) {
        this.entityRadius = entityRadius;
    }

    /**
     * Warp into the scene on the nav mesh, finding the nearest cell.
     * The currentCell and position3d are updated and this new
     * position is returned. This updates the state of the path finder!
     * 
     * @return the new position in the nearest cell
     */
    public Vector3f warp(Vector3f newPos) {
        Vector3f newPos2d = new Vector3f(newPos.x, 0, newPos.z);
        currentCell = navMesh.findClosestCell(newPos2d);
        currentPos3d.set(navMesh.snapPointToCell(currentCell, newPos2d));
        currentPos3d.setY(newPos.getY());
        currentPos.set(currentPos3d.getX(), currentPos3d.getZ());
        return currentPos3d;
    }

    /**
     * Get the nearest cell to the supplied position
     * and place the returned position in that cell.
     * 
     * @param position to place in the nearest cell
     * @return the position in the cell
     */
    public Vector3f warpInside(Vector3f position) {
        Vector3f newPos2d = new Vector3f(position.x, 0, position.z);
        Cell cell = navMesh.findClosestCell(newPos2d);
        position.set(navMesh.snapPointToCell(cell, newPos2d));
        return position;
    }

    /**
     * Test if the position is inside a cell of the nav mesh.
     * @return false if it falls outside a cell, not in the navmesh.
     */
    /*public boolean isInsideNavMesh(Vector3f position) {
    Cell cell = navMesh.findClosestCell(position);
    return cell != null;
    }*/
    /**
     * Generate a new Path from the currentPos3d to the supplied goal 
     * position.
     * setPosition() must be called first for this to work. If the 
     * point is not in the mesh, false is returned. You should use
     * warp() in that case to place the point in the mesh.
     * 
     * @return fail if no path found or start location outside of a cell
     */
    public boolean computePath(Vector3f goal) {
        return computePath(goal, null);
    }

    /**
     * Generate a new Path from the currentPos3d to the supplied goal 
     * position.
     * setPosition() must be called first for this to work. If the 
     * point is not in the mesh, false is returned. You should use
     * warp() in that case to place the point in the mesh.
     * 
     * @return fail if no path found or start location outside of a cell
     */
    public boolean computePath(Vector3f goal, DebugInfo debugInfo) {
        // get the cell that this point is in
        Vector3f newPos2d = new Vector3f(currentPos3d.x, 0, currentPos3d.z);
        currentCell = navMesh.findClosestCell(newPos2d);
        if (currentCell == null) {
            return false;
        }

        goalPos3d = goal;
        goalPos = new Vector2f(goalPos3d.getX(), goalPos3d.getZ());
        Vector3f goalPos2d = new Vector3f(goalPos.getX(), 0, goalPos.getY());
        goalCell = navMesh.findClosestCell(goalPos2d);
        boolean result = buildNavigationPath(path, currentCell, currentPos3d, goalCell, goalPos3d, entityRadius, debugInfo);
        if (!result) {
            goalPos = null;
            goalCell = null;
            return false;
        }
        nextWaypoint = path.getFirst();
        return true;
    }

    public void clearPath() {
        path.clear();
        goalPos = null;
        goalCell = null;
        nextWaypoint = null;
    }

    public Vector3f getWaypointPosition() {
        return nextWaypoint.getPosition();
    }

    public Vector3f getDirectionToWaypoint() {
        Vector3f waypt = nextWaypoint.getPosition();
        return waypt.subtract(currentPos3d).normalizeLocal();
    }

    public float getDistanceToWaypoint() {
        return currentPos3d.distance(nextWaypoint.getPosition());
    }

    public Vector3f onMove(Vector3f moveVec) {
        if (moveVec.equals(Vector3f.ZERO)) {
            return currentPos3d;
        }

        Vector3f newPos2d = new Vector3f(currentPos3d);
        newPos2d.addLocal(moveVec);
        newPos2d.setY(0);

        Vector3f currentPos2d = new Vector3f(currentPos3d);
        currentPos2d.setY(0);

        //Cell nextCell = navMesh.resolveMotionOnMesh(currentPos2d, currentCell, newPos2d, newPos2d);
        //currentCell = nextCell;
        newPos2d.setY(currentPos3d.getY());
        return newPos2d;
    }

    public boolean isAtGoalWaypoint() {
        return path.getWaypoints().size() >= 0 && nextWaypoint == path.getLast();
    }

    public Waypoint getNextWaypoint() {
        return nextWaypoint;
    }

    public void goToNextWaypoint() {
        goToNextWaypoint(null);
    }

    public void goToNextWaypoint(DebugInfo debugInfo) {
        int from = getPath().getWaypoints().indexOf(nextWaypoint);
        //Fix for waypoints that could be empty
        if (getPath().getWaypoints().isEmpty()) {
            return;            
        }
        
        nextWaypoint = getPath().getWaypoints().get(from + 1);
        //nextWaypoint = path.getFurthestVisibleWayPoint(nextWaypoint, debugInfo);//path.getOptimalVisibleWayPoint(nextWaypoint);
        int to = getPath().getWaypoints().indexOf(nextWaypoint);
        //Vector3f waypt = nextWaypoint.getPosition();
        //currentPos3d.setX(waypt.getX());
        //currentPos3d.setZ(waypt.getZ());
        //currentPos.set(waypt.getX(), waypt.getZ());
        currentCell = nextWaypoint.getCell();
        //System.out.println("Going from WP idx "+from+" to "+to);
    }

    public Path getPath() {
        return path;
    }

    /**
     * Build a navigation path using the provided points and the A* method
     */
    private boolean buildNavigationPath(Path navPath,
            Cell startCell, Vector3f startPos,
            Cell endCell, Vector3f endPos,
            float entityRadius, DebugInfo debugInfo) {

        // Increment our path finding session ID
        // This Identifies each pathfinding session
        // so we do not need to clear out old data
        // in the cells from previous sessions.
        sessionID++;

        // load our data into the Heap object
        // to prepare it for use.
        heap.initialize(sessionID, startPos);

        // We are doing a reverse search, from EndCell to StartCell.
        // Push our EndCell onto the Heap at the first cell to be processed
        endCell.queryForPath(heap, null, 0.0f);

        // process the heap until empty, or a path is found
        boolean foundPath = false;
        while (heap.isNotEmpty() && !foundPath) {

            // pop the top cell (the open cell with the lowest cost) off the
            // Heap
            Node currentNode = heap.getTop();

            // if this cell is our StartCell, we are done
            if (currentNode.cell.equals(startCell)) {
                foundPath = true;
            } else {
                // Process the Cell, Adding it's neighbors to the Heap as needed
                currentNode.cell.processCell(heap);
            }
        }

        Vector2f intersectionPoint = new Vector2f();

        // if we found a path, build a waypoint list
        // out of the cells on the path
        if (!foundPath) {
            return false;
        }

        // Setup the Path object, clearing out any old data
        navPath.initialize(navMesh, startPos, startCell, endPos, endCell);

        Vector3f lastWayPoint = startPos;

        // Step through each cell linked by our A* algorithm
        // from StartCell to EndCell
        Cell currentCell = startCell;
        while (currentCell != null && currentCell != endCell) {

            if (debugInfo != null) {
                debugInfo.addPlannedCell(currentCell);
            }

            // add the link point of the cell as a way point (the exit
            // wall's center)
            int linkWall = currentCell.getArrivalWall();
            Vector3f newWayPoint = currentCell.getWallMidpoint(linkWall).clone();

            Line2D wall = currentCell.getWall(linkWall);
            float length = wall.length();
            float distBlend = entityRadius / length;

            Line2D lineToGoal = new Line2D(new Vector2f(lastWayPoint.x, lastWayPoint.z),
                    new Vector2f(endPos.x, endPos.z));
            LineIntersect result = lineToGoal.intersect(wall, intersectionPoint);
            switch (result) {
                case SegmentsIntersect:
                    float d1 = wall.getPointA().distance(intersectionPoint);
                    float d2 = wall.getPointB().distance(intersectionPoint);
                    if (d1 > entityRadius && d2 > entityRadius) {
                        // we can fit through the wall if we go
                        // directly to the goal.
                        newWayPoint = new Vector3f(intersectionPoint.x, 0, intersectionPoint.y);
                    } else {
                        // cannot fit directly.
                        // try to find point where we can
                        if (d1 < d2) {
                            intersectionPoint.interpolateLocal(wall.getPointA(), wall.getPointB(), distBlend);
                            newWayPoint = new Vector3f(intersectionPoint.x, 0, intersectionPoint.y);
                        } else {
                            intersectionPoint.interpolateLocal(wall.getPointB(), wall.getPointA(), distBlend);
                            newWayPoint = new Vector3f(intersectionPoint.x, 0, intersectionPoint.y);
                        }
                    }
                    currentCell.computeHeightOnCell(newWayPoint);
                    break;
                case LinesIntersect:
                case ABisectsB:
                case BBisectsA:
                    Vector2f lastPt2d = new Vector2f(lastWayPoint.x, lastWayPoint.z);
                    Vector2f endPos2d = new Vector2f(endPos.x, endPos.z);

                    Vector2f normalEnd = endPos2d.subtract(lastPt2d).normalizeLocal();
                    Vector2f normalA = wall.getPointA().subtract(lastPt2d).normalizeLocal();
                    Vector2f normalB = wall.getPointB().subtract(lastPt2d).normalizeLocal();
                    if (normalA.dot(normalEnd) < normalB.dot(normalEnd)) {
                        // choose point b
                        intersectionPoint.interpolateLocal(wall.getPointB(), wall.getPointA(), distBlend);
                        newWayPoint = new Vector3f(intersectionPoint.x, 0, intersectionPoint.y);
                    } else {
                        // choose point a
                        intersectionPoint.interpolateLocal(wall.getPointA(), wall.getPointB(), distBlend);
                        newWayPoint = new Vector3f(intersectionPoint.x, 0, intersectionPoint.y);
                    }
                    currentCell.computeHeightOnCell(newWayPoint);

                    break;
                case CoLinear:
                case Parallel:
                    System.out.println("## colinear or parallel");
                    break;
            }


            if (debugInfo != null) {
                debugInfo.addPreOptWaypoints(newWayPoint.clone());
            }
//                newWayPoint = snapPointToCell(currentCell, newWayPoint);
            lastWayPoint = newWayPoint.clone();

            navPath.addWaypoint(newWayPoint, currentCell);

            // get the next cell
            currentCell = currentCell.getLink(linkWall);
        }

        // cap the end of the path.
        navPath.finishPath();

        //remove optimization so it can be done as the actor moves
        // further: optimize the path
        List<Waypoint> newPath = new ArrayList<Waypoint>();
        Waypoint curWayPoint = navPath.getFirst();
        newPath.add(curWayPoint);
        while (curWayPoint != navPath.getLast()) {
            curWayPoint = navPath.getFurthestVisibleWayPoint(curWayPoint);
            newPath.add(curWayPoint);
        }

        navPath.initialize(navMesh, startPos, startCell, endPos, endCell);
        for (Waypoint newWayPoint : newPath) {
            navPath.addWaypoint(newWayPoint.getPosition(), newWayPoint.getCell());
        }
        navPath.finishPath();

        return true;
    }

    /**
     * Resolve a movement vector on the mesh
     *
     * @param startPos
     * @param startCell
     * @param endPos
     * @return
     */
    private Cell resolveMotionOnMesh(Vector3f startPos, Cell startCell, Vector3f endPos, Vector3f modifiedEndPos) {
        int i = 0;
        // create a 2D motion path from our Start and End positions, tossing out
        // their Y values to project them
        // down to the XZ plane.
        Line2D motionLine = new Line2D(new Vector2f(startPos.x, startPos.z),
                new Vector2f(endPos.x, endPos.z));

        // these three will hold the results of our tests against the cell walls
        ClassifyResult result = null;

        // TestCell is the cell we are currently examining.
        Cell currentCell = startCell;

        do {
            i++;
            // use NavigationCell to determine how our path and cell interact
            // if(TestCell.IsPointInCellCollumn(MotionPath.EndPointA()))
            // System.out.println("Start is in cell");
            // else
            // System.out.println("Start is NOT in cell");
            // if(TestCell.IsPointInCellCollumn(MotionPath.EndPointB()))
            // System.out.println("End is in cell");
            // else
            // System.out.println("End is NOT in cell");
            result = currentCell.classifyPathToCell(motionLine);

            // if exiting the cell...
            if (result.result == PathResult.ExitingCell) {
                // Set if we are moving to an adjacent cell or we have hit a
                // solid (unlinked) edge
                if (result.cell != null) {
                    // moving on. Set our motion origin to the point of
                    // intersection with this cell
                    // and continue, using the new cell as our test cell.
                    motionLine.setPointA(result.intersection);
                    currentCell = result.cell;
                } else {
                    // we have hit a solid wall. Resolve the collision and
                    // correct our path.
                    motionLine.setPointA(result.intersection);
                    currentCell.projectPathOnCellWall(result.side, motionLine);

                    // add some friction to the new MotionPath since we are
                    // scraping against a wall.
                    // we do this by reducing the magnatude of our motion by 10%
                    Vector2f Direction = motionLine.getPointB().subtract(
                            motionLine.getPointA()).mult(0.9f);
                    // Direction.mult(0.9f);
                    motionLine.setPointB(motionLine.getPointA().add(
                            Direction));
                }
            } else if (result.result == Cell.PathResult.NoRelationship) {
                // Although theoretically we should never encounter this case,
                // we do sometimes find ourselves standing directly on a vertex
                // of the cell.
                // This can be viewed by some routines as being outside the
                // cell.
                // To accomodate this rare case, we can force our starting point
                // to be within
                // the current cell by nudging it back so we may continue.
                Vector2f NewOrigin = motionLine.getPointA();
                // NewOrigin.x -= 0.01f;
                currentCell.forcePointToCellColumn(NewOrigin);
                motionLine.setPointA(NewOrigin);
            }
        }//
        // Keep testing until we find our ending cell or stop moving due to
        // friction
        //
        while ((result.result != Cell.PathResult.EndingCell)
                && (motionLine.getPointA().x != motionLine.getPointB().x && motionLine.getPointA().y != motionLine.getPointB().y) && i < 5000);
        //
        if (i >= 5000) {
            System.out.println("Loop detected in ResolveMotionOnMesh");
        }
        // we now have our new host cell

        // Update the new control point position,
        // solving for Y using the Plane member of the NavigationCell
        modifiedEndPos.x = motionLine.getPointB().x;
        modifiedEndPos.y = 0.0f;
        modifiedEndPos.z = motionLine.getPointB().y;
        currentCell.computeHeightOnCell(modifiedEndPos);

        return currentCell;
    }
}
