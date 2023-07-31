package com.jme3.ai.navmesh;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import java.io.IOException;
import java.util.Random;

/**
 * A Cell represents a single triangle within a NavigationMesh. It contains
 * functions for testing a path against the cell, and various ways to resolve
 * collisions with the cell walls. Portions of the A* path finding algorythm are
 * provided within this class as well, but the path finding process is managed
 * by the parent Navigation Mesh.
 * 
 * Portions Copyright (C) Greg Snook, 2000
 * 
 * @author TR
 * 
 */
public class Cell implements Savable {

    static final int VERT_A = 0;
    static final int VERT_B = 1;
    static final int VERT_C = 2;
    static final int SIDE_AB = 0;
    static final int SIDE_BC = 1;
    static final int SIDE_CA = 2;

    public enum PathResult {

        /**
         * The path does not cross this cell
         */
        NoRelationship,

        /**
         * The path ends in this cell
         */
        EndingCell,

        /**
         * The path exits this cell through side X
         */
        ExitingCell;
        
    };

    public class ClassifyResult {

        PathResult result = PathResult.NoRelationship;
        int side = 0;
        Cell cell = null;
        Vector2f intersection = new Vector2f();

        @Override
        public String toString() {
            return result.toString() + " " + cell;
        }
    }

    /**
     * A plane containing the cell triangle
     */
    private Plane cellPlane = new Plane();

    /**
     * pointers to the verticies of this triangle held in the
     * NavigationMesh's vertex pool
     */
    private Vector3f[] verticies = new Vector3f[3];

    /**
     * The center of the triangle
     */
    private Vector3f center = new Vector3f();

    /**
     * a 2D line representing each cell Side
     */
    private Line2D[] sides = new Line2D[3];

    /**
     * pointers to cells that attach to this cell. A null link denotes a solid
     * edge. Pathfinding Data...
     */
    private Cell[] links = new Cell[3];

    /**
     * an identifier for the current pathfinding session.
     */
    private volatile int sessionID;

    /**
     * total cost to use this cell as part of a path
     */
    private volatile float arrivalCost;

    /**
     * our estimated cost to the goal from here
     */
    private volatile float heuristic;

    /**
     * are we currently listed as an Open cell to revisit and test?
     */
    private volatile boolean open;

    /**
     * the side we arrived through.
     */
    private volatile int arrivalWall;

    /**
     * the pre-computed midpoint of each wall.
     */
    private Vector3f[] wallMidpoints = new Vector3f[3];

    /**
     * the distances between each wall midpoint of sides (0-1, 1-2, 2-0)
     */
    private float[] wallDistances = new float[3];

    void initialize(Vector3f pointA, Vector3f pointB, Vector3f pointC) {
        // guarantee ClockWise order
        if (isLeft(pointA, pointB, pointC)) {
            // CCW
            verticies[VERT_A] = pointA;
            verticies[VERT_B] = pointC;
            verticies[VERT_C] = pointB;
        } else {
            // CW
            verticies[VERT_A] = pointA;
            verticies[VERT_B] = pointB;
            verticies[VERT_C] = pointC;
        }

        // object must be re-linked
        links[SIDE_AB] = null;
        links[SIDE_BC] = null;
        links[SIDE_CA] = null;

        // now that the vertex pointers are set, compute additional data about
        // the Cell
        computeCellData();
    }

    void computeCellData() {
        // create 2D versions of our verticies
        Vector2f point1 = new Vector2f(verticies[VERT_A].x, verticies[VERT_A].z);
        Vector2f point2 = new Vector2f(verticies[VERT_B].x, verticies[VERT_B].z);
        Vector2f point3 = new Vector2f(verticies[VERT_C].x, verticies[VERT_C].z);

        // innitialize our sides
        sides[SIDE_AB] = new Line2D(point1, point2); // line AB
        sides[SIDE_BC] = new Line2D(point2, point3); // line BC
        sides[SIDE_CA] = new Line2D(point3, point1); // line CA

        cellPlane.setPlanePoints(verticies[VERT_A], verticies[VERT_B],
                verticies[VERT_C]);

        // compute midpoint as centroid of polygon
        center.x = ((verticies[VERT_A].x + verticies[VERT_B].x + verticies[VERT_C].x) / 3);
        center.y = ((verticies[VERT_A].y + verticies[VERT_B].y + verticies[VERT_C].y) / 3);
        center.z = ((verticies[VERT_A].z + verticies[VERT_B].z + verticies[VERT_C].z) / 3);

        // compute the midpoint of each cell wall
        wallMidpoints[0] = new Vector3f(
                (verticies[VERT_A].x + verticies[VERT_B].x) / 2.0f,
                (verticies[VERT_A].y + verticies[VERT_B].y) / 2.0f,
                (verticies[VERT_A].z + verticies[VERT_B].z) / 2.0f);
        wallMidpoints[1] = new Vector3f(
                (verticies[VERT_C].x + verticies[VERT_B].x) / 2.0f,
                (verticies[VERT_C].y + verticies[VERT_B].y) / 2.0f,
                (verticies[VERT_C].z + verticies[VERT_B].z) / 2.0f);

        wallMidpoints[2] = new Vector3f(
                (verticies[VERT_C].x + verticies[VERT_A].x) / 2.0f,
                (verticies[VERT_C].y + verticies[VERT_A].y) / 2.0f,
                (verticies[VERT_C].z + verticies[VERT_A].z) / 2.0f);

        // compute the distances between the wall midpoints
        Vector3f wallVector;
        wallVector = wallMidpoints[0].subtract(wallMidpoints[1]);
        wallDistances[0] = wallVector.length();

        wallVector = wallMidpoints[1].subtract(wallMidpoints[2]);
        wallDistances[1] = wallVector.length();

        wallVector = wallMidpoints[2].subtract(wallMidpoints[0]);
        wallDistances[2] = wallVector.length();

    }
    
    public Vector3f[] getTriangle() {
        return verticies;
    }
    
    /**
     * Check if C is left of the line AB
     */
    public boolean isLeft(Vector3f a, Vector3f b, Vector3f c){
        return ((b.x - a.x)*(c.z - a.z) - (b.z - a.z)*(c.x - a.x)) > 0;
    }

    /**
     * Navigation Mesh is created as a pool of raw cells. The cells are then
     * compared against each other to find common edges and create links.
     * This routine is called from a potentially adjacent cell to test if
     * a link should exist between the two.
     *
     * @param pointA
     * @param pointB
     * @param caller
     * @return
     */
    boolean requestLink(Vector3f pointA, Vector3f pointB, Cell caller, float epsilon) {
        // return true if we share the two provided verticies with the calling
        // cell.
        if (verticies[VERT_A].distanceSquared(pointA) <= epsilon) {
            if (verticies[VERT_B].distanceSquared(pointB) <= epsilon) {
                links[SIDE_AB] = caller;
                return true;
            } else if (verticies[VERT_C].distanceSquared(pointB) <= epsilon) {
                links[SIDE_CA] = caller;
                return true;
            }
        } else if (verticies[VERT_B].distanceSquared(pointA) <= epsilon) {
            if (verticies[VERT_A].equals(pointB)) {
                links[SIDE_AB] = caller;
                return true;
            } else if (verticies[VERT_C].distanceSquared(pointB) <= epsilon) {
                links[SIDE_BC] = caller;
                return true;
            }
        } else if (verticies[VERT_C].distanceSquared(pointA) <= epsilon) {
            if (verticies[VERT_A].distanceSquared(pointB) <= epsilon) {
                links[SIDE_CA] = caller;
                return true;
            } else if (verticies[VERT_B].distanceSquared(pointB) <= epsilon) {
                links[SIDE_BC] = caller;
                return true;
            }
        }

        // we are not adjacent to the calling cell
        return false;
    }

    /**
     * Sets a link to the calling cell on the enumerated edge.
     *
     * @param Side
     * @param Caller
     */
    private void setLink(int Side, Cell Caller) {
        links[Side] = Caller;
    }

    /**
     * Uses the X and Z information of the vector to calculate Y on the cell plane
     * @param point
     */
    public float getHeightOnCell(Vector3f point){
        return cellPlane.solveForY(point.x, point.z);
    }

    /**
     * Uses the X and Z information of the vector to calculate Y on the cell plane
     * @param point
     */
    public void computeHeightOnCell(Vector3f point) {
        point.y = getHeightOnCell(point);
    }

    /**
     * Test to see if a 2D point is within the cell. There are probably better
     * ways to do this, but this seems plenty fast for the time being.
     *
     * @param point
     * @return
     */
    public boolean contains(Vector2f point) {
        // we are "in" the cell if we are on the right hand side of all edge
        // lines of the cell
        int InteriorCount = 0;

        for (int i = 0; i < 3; i++) {
            Line2D.PointSide SideResult = sides[i].getSide(
                    point, 1.0e-6f);

            if (SideResult != Line2D.PointSide.Left) {
                InteriorCount++;
            }
        }
        // if(InteriorCount == 3)
        // System.out.println("Point "+TestPoint+" is in Cell:"+this);
        // else
        // System.out.println("Point "+TestPoint+" is NOT in Cell:"+this);
        return (InteriorCount == 3);
    }

    /**
     * Test to see if a 3D point is within the cell by projecting it down to 2D
     * and calling the above method.
     * @param point
     * @return
     */
    public boolean contains(Vector3f point) {
        return (contains(new Vector2f(point.x, point.z)));
    }

    public Vector3f getVertex(int Vert) {
        return (verticies[Vert]);
    }

    public Vector3f getCenter() {
        return (center);
    }

    Cell getLink(int side) {
        return (links[side]);
    }

    Line2D getWall(int side){
        return sides[side];
    }

    float getArrivalCost() {
        return (arrivalCost);
    }

    float getHeuristic() {
        return (heuristic);
    }

    float getTotalCost() {
        return (arrivalCost + heuristic);
    }

    int getArrivalWall() {
        return (arrivalWall);
    }

    public float getWallLength(int side){
        return wallDistances[side];
    }

    public Vector3f getWallMidpoint(int side) {
        return (wallMidpoints[side]);
    }

    /**
     * Classifies a Path in relationship to this cell. A path is represented by
     * a 2D line where Point A is the start of the path and Point B is the
     * desired position.
     *
     * If the path exits this cell on a side which is linked to another cell,
     * that cell index is returned in the NextCell parameter and SideHit
     * contains the side number of the wall exited through.
     *
     * If the path collides with a side of the cell which has no link (a solid
     * edge), SideHit contains the side number (0-2) of the colliding wall.
     *
     * In either case PointOfIntersection will contain the point where the path
     * intersected with the wall of the cell if it is provided by the caller.
     */
    public ClassifyResult classifyPathToCell(Line2D MotionPath) {
        // System.out.println("Cell:"+m_Vertex[0].toString()+" "+m_Vertex[1].toString()+" "+m_Vertex[2].toString());
        // System.out.println("     Path:"+MotionPath);
        int interiorCount = 0;
        ClassifyResult result = new ClassifyResult();

        // Check our MotionPath against each of the three cell walls
        for (int i = 0; i < 3; ++i) {
            // Classify the MotionPath endpoints as being either ON_LINE,
            // or to its LEFT_SIDE or RIGHT_SIDE.
            // Since our triangle vertices are in clockwise order,
            // we know that points to the right of each line are inside the
            // cell.
            // Points to the left are outside.
            // We do this test using the ClassifyPoint function of Line2D

            // If the destination endpoint of the MotionPath
            // is Not on the right side of this wall...
            Line2D.PointSide end = sides[i].getSide(
                    MotionPath.getPointB(), 0.0f);
            if (end == Line2D.PointSide.Left){//(end != Line2D.PointSide.Right) {
//					&& end != Line2D.POINT_CLASSIFICATION.ON_LINE) {
                // ..and the starting endpoint of the MotionPath
                // is Not on the left side of this wall...
                if (sides[i].getSide(MotionPath.getPointA(), 0.0f) != Line2D.PointSide.Left) {
                    // Check to see if we intersect the wall
                    // using the Intersection function of Line2D
                    Line2D.LineIntersect IntersectResult = MotionPath.intersect(sides[i], result.intersection);

                    if (IntersectResult == Line2D.LineIntersect.SegmentsIntersect || IntersectResult == Line2D.LineIntersect.ABisectsB) {
                        // record the link to the next adjacent cell
                        // (or NULL if no attachement exists)
                        // and the enumerated ID of the side we hit.
                        result.cell = links[i];
                        result.side = i;
                        result.result = PathResult.ExitingCell;
                        // System.out.println("exits this cell");
                        return result;

                        // pNextCell = m_Link[i];
                        // Side = i;
                        // return (PATH_RESULT.EXITING_CELL);
                    }
                }
            } else {
                // The destination endpoint of the MotionPath is on the right
                // side.
                // Increment our InteriorCount so we'll know how many walls we
                // were
                // to the right of.
                interiorCount++;
            }
        }

        // An InteriorCount of 3 means the destination endpoint of the
        // MotionPath
        // was on the right side of all walls in the cell.
        // That means it is located within this triangle, and this is our ending
        // cell.
        if (interiorCount == 3) {
            // System.out.println(" ends within this cell");
            result.result = PathResult.EndingCell;
            return result;
            // return (PATH_RESULT.ENDING_CELL);
        }
        // System.out.println("No intersection with this cell at all");
        // We only reach here is if the MotionPath does not intersect the cell
        // at all.
        return result;
        // return (PATH_RESULT.NO_RELATIONSHIP);
    }

    /**
     * ProjectPathOnCellWall projects a path intersecting the wall with the wall
     * itself. This can be used to convert a path colliding with a cell wall to
     * a resulting path moving along the wall. The input parameter MotionPath
     * MUST contain a starting point (EndPointA) which is the point of
     * intersection with the path and cell wall number [SideNumber] and an
     * ending point (EndPointB) which resides outside of the cell.
     */
    void projectPathOnCellWall(int sideNumber, Line2D motionPath) {
        // compute the normalized vector of the cell wall in question
        Vector2f WallNormal = sides[sideNumber].getPointB().subtract(
                sides[sideNumber].getPointA());
        WallNormal = WallNormal.normalize();

        // determine the vector of our current movement
        Vector2f MotionVector = motionPath.getPointB().subtract(
                motionPath.getPointA());

        // compute dot product of our MotionVector and the normalized cell wall
        // this gives us the magnatude of our motion along the wall

        float DotResult = MotionVector.dot(WallNormal);

        // our projected vector is then the normalized wall vector times our new
        // found magnatude
        MotionVector = WallNormal.mult(DotResult);

        // redirect our motion path along the new reflected direction
        motionPath.setPointB(motionPath.getPointA().add(MotionVector));

        //
        // Make sure starting point of motion path is within the cell
        //
        Vector2f NewPoint = motionPath.getPointA();
        forcePointToCellColumn(NewPoint);
        motionPath.setPointA(NewPoint);

        //
        // Make sure destination point does not intersect this wall again
        //
        NewPoint = motionPath.getPointB();
        forcePointToWallInterior(sideNumber, NewPoint);
        motionPath.setPointB(NewPoint);

    }

    /**
     * Force a 2D point to the interior side of the specified wall.
     *
     * @param sideNumber
     * @param point
     * @return
     */
    boolean forcePointToWallInterior(int sideNumber, Vector2f point) {
        float Distance = sides[sideNumber].signedDistance(point);
        float Epsilon = 0.001f;

        if (Distance <= Epsilon) {
            if (Distance <= 0.0f) {
                Distance -= Epsilon;
            }

            Distance = Math.abs(Distance);
            Distance = (Epsilon > Distance ? Epsilon : Distance);

            // this point needs adjustment
            Vector2f Normal = sides[sideNumber].getNormal();
            Normal = Normal.mult(Distance);
            point.x += Normal.x;
            point.y += Normal.y;
            return (true);
        }
        return (false);
    }

    /**
     * Force a 3D point to the interior side of the specified wall.
     *
     * @param sideNumber
     * @param point
     * @return
     */
    boolean forcePointToWallInterior(int sideNumber, Vector3f point) {
        Vector2f TestPoint2D = new Vector2f(point.x, point.z);
        boolean PointAltered = forcePointToWallInterior(sideNumber, TestPoint2D);

        if (PointAltered) {
            point.x = TestPoint2D.x;
            point.z = TestPoint2D.y;
        }

        return (PointAltered);
    }

    /**
     * Force a 2D point to the interior cell by forcing it to the interior of
     * each wall.
     *
     * @param point
     * @return
     */
    boolean forcePointToCellColumn(Vector2f point) {
        // create a motion path from the center of the cell to our point
        Line2D TestPath = new Line2D(new Vector2f(center.x,
                center.z), point);

        ClassifyResult result = classifyPathToCell(TestPath);
        // compare this path to the cell.

        if (result.result == PathResult.ExitingCell) {
            Vector2f PathDirection = new Vector2f(result.intersection.x
                    - center.x, result.intersection.y - center.z);

            PathDirection = PathDirection.mult(0.9f);

            point.x = center.x + PathDirection.x;
            point.y = center.z + PathDirection.y;
            return true;
        } else if (result.result == PathResult.NoRelationship) {
            point.x = center.x;
            point.y = center.z;
            return true;
        }

        return false;
    }

    /**
     * Force a 3D point to the interior cell by forcing it to the interior of
     * each wall
     * @param point
     * @return
     */
    boolean forcePointToCellColumn(Vector3f point) {
        Vector2f TestPoint2D = new Vector2f(point.x, point.z);
        boolean PointAltered = forcePointToCellColumn(TestPoint2D);

        if (PointAltered) {
            point.x = TestPoint2D.x;
            point.z = TestPoint2D.y;
        }
        return (PointAltered);
    }

    /**
     * Process this cells neighbors using A*
     *
     * @param heap
     * @return
     */
    boolean processCell(Heap heap) {
        if (sessionID == heap.getSessionID()) {
            // once we have been processed, we are closed
            open = false;

            // querry all our neigbors to see if they need to be added to the
            // Open heap
            for (int i = 0; i < 3; ++i) {
                if (links[i] != null) {
                    // abs(i-m_ArrivalWall) is a formula to determine which
                    // distance measurement to use.
                    // The Distance measurements between the wall midpoints of
                    // this cell
                    // are held in the order ABtoBC, BCtoCA and CAtoAB.
                    // We add this distance to our known m_ArrivalCost to
                    // compute
                    // the total cost to reach the next adjacent cell.
                    links[i].queryForPath(heap, this, arrivalCost
                            + wallDistances[Math.abs(i - arrivalWall)]);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Process this cell using the A* heuristic
     *
     * @param heap
     * @param caller
     * @param arrivalCost
     * @return
     */
    boolean queryForPath(Heap heap, Cell caller, float arrivalCost) {
        if (sessionID != heap.getSessionID()) {
            // this is a new session, reset our internal data
            sessionID = heap.getSessionID();

            if (caller != null) {
                open = true;
                computeHeuristic(heap.getGoal());
                this.arrivalCost = arrivalCost;

                // remember the side this caller is entering from
                if (caller.equals(links[0])) {
                    arrivalWall = 0;
                } else if (caller.equals(links[1])) {
                    arrivalWall = 1;
                } else if (caller.equals(links[2])) {
                    arrivalWall = 2;
                }
            } else {
                // we are the cell that contains the starting location
                // of the A* search.
                open = false;
                this.arrivalCost = 0;
                heuristic = 0;
                arrivalWall = 0;
            }
            // add this cell to the Open heap
            heap.addCell(this);
            return true;
        } else if (open) {
            // m_Open means we are already in the Open Heap.
            // If this new caller provides a better path, adjust our data
            // Then tell the Heap to resort our position in the list.
            if ((arrivalCost + heuristic) < (this.arrivalCost + heuristic)) {
                this.arrivalCost = arrivalCost;

                // remember the side this caller is entering from
                if (caller.equals(links[0])) {
                    arrivalWall = 0;
                } else if (caller.equals(links[1])) {
                    arrivalWall = 1;
                } else if (caller.equals(links[2])) {
                    arrivalWall = 2;
                }
                // ask the heap to resort our position in the priority heap
                heap.adjustCell(this);
                return true;
            }
        }
        // this cell is closed
        return false;
    }


    /**
     * Compute the A* Heuristic for this cell given a Goal point
     * @param goal
     */
    void computeHeuristic(Vector3f goal) {
        // our heuristic is the estimated distance (using the longest axis
        // delta) between our
        // cell center and the goal location

//        float XDelta = Math.abs(goal.x - center.x);
//        float YDelta = Math.abs(goal.y - center.y);
//        float ZDelta = Math.abs(goal.z - center.z);

//        heuristic = Math.max(Math.max(XDelta, YDelta), ZDelta);
        heuristic = goal.distance(center);
    }

    @Override
    public String toString() {
        return "Cell: " + center.x + "," + center.z;
    }

    public Vector3f getNormal() {
        return this.cellPlane.getNormal();
    }

    public Vector3f getRandomPoint() {
        Random rand = new Random();
        Vector2f ret =
                this.sides[0].getPointA().add(this.sides[0].getDirection().mult(rand.nextFloat()).add(
                this.sides[1].getDirection().mult(rand.nextFloat())));
        forcePointToCellColumn(ret);
        Vector3f vec = new Vector3f(ret.x, 0, ret.y);
        computeHeightOnCell(vec);
        return vec;
    }

    public void write(JmeExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
//        capsule.write(terrain, "terrain", null);
//        capsule.write(cellPlane, "cellPlane", null);
        capsule.write(verticies, "verticies", null);
//        capsule.write(center, "center", null);
//        capsule.write(sides, "sides", null);
        capsule.write(links, "links", null);
//        capsule.write(wallMidpoints, "midpoints", null);
//        capsule.write(wallDistances, "distances", null);
    }

    public void read(JmeImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);

        Savable[] verts = capsule.readSavableArray("verticies", null);
        for (int i = 0; i < verts.length; i++){
            verticies[i] = (Vector3f) verts[i];
        }

        Savable[] savLinks = capsule.readSavableArray("links", null);
        for (int i = 0; i < savLinks.length; i++){
            links[i] = (Cell) savLinks[i];
        }

        computeCellData();
        
//        cellPlane = (Plane) capsule.readSavable("cellPlane", new Plane());
//        center = (Vector3f) capsule.readSavable("center", new Vector3f());
//        sides = (Line2D[]) capsule.readSavableArray("sides", new Line2D[3]);
//        wallMidpoints = (Vector3f[]) capsule.readSavableArray("midpoints", new Vector3f[3]);
//        wallDistances = capsule.readFloatArray("distances", new float[3]);

        
    }

    void checkAndLink(Cell other, float epsilon) {
        if (getLink(Cell.SIDE_AB) == null
                && other.requestLink(getVertex(0), getVertex(1), this, epsilon)) {
            setLink(Cell.SIDE_AB, other);
        } else if (getLink(Cell.SIDE_BC) == null
                && other.requestLink(getVertex(1), getVertex(2), this, epsilon)) {
            setLink(Cell.SIDE_BC, other);
        } else if (getLink(Cell.SIDE_CA) == null
                && other.requestLink(getVertex(2), getVertex(0), this, epsilon)) {
            setLink(Cell.SIDE_CA, other);
        }
    }

    void unLink(Cell c) {
        if (c == links[0]) {
            links[0] = null;
            c.unLink(this);
        } else if (c == links[1]) {
            links[1] = null;
            c.unLink(this);
        } else if (c == links[2]) {
            links[2] = null;
            c.unLink(this);
        }
    }
    
    /**
     * Return a mesh representation of this polygon (triangle)
     */
    public Mesh getDebugMesh() {
        Mesh m = new Mesh();//(verticies[0], verticies[1], verticies[2]);
        m.setBuffer(Type.Position, 3, new float[]{  verticies[0].x, verticies[0].y, verticies[0].z,
                                                    verticies[1].x, verticies[1].y, verticies[1].z,
                                                    verticies[2].x, verticies[2].y, verticies[2].z});
        
        // the tex coords are wrong, but we render wire/unshaded so it doesn't matter
        m.setBuffer(Type.TexCoord, 2, new float[]{  0,0,
                                                    0.5f,1,
                                                    1,1});
        m.setBuffer(Type.Normal, 3, new float[]{0, 0, 1,
                                                0, 0, 1,
                                                0, 0, 1});
        m.setBuffer(Type.Index, 3, new short[]{0, 1, 2});
        m.updateBound();
        return m;
    }
}
