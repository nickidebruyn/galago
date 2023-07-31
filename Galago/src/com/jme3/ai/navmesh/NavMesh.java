package com.jme3.ai.navmesh;

import java.io.IOException;
import java.util.ArrayList;

import com.jme3.ai.navmesh.Cell.ClassifyResult;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;

/**
 * A NavigationMesh is a collection of NavigationCells used to control object
 * movement while also providing path finding line-of-sight testing. It serves
 * as a parent to all the Actor objects which exist upon it.
 * 
 * Portions Copyright (C) Greg Snook, 2000
 * 
 * @author TR
 * 
 */
public class NavMesh implements Savable {

    /**
     * the cells that make up this mesh
     */
    private ArrayList<Cell> cellList = new ArrayList<Cell>();

    public NavMesh() {
    }

    public NavMesh(Mesh mesh) {
        loadFromMesh(mesh);
    }

    public void clear() {
        cellList.clear();
    }

    /**
     * Add a new cell, defined by the three vertices in clockwise order, to this
     * mesh.
     *
     * @param pointA
     * @param PointB
     * @param PointC
     */
    public void addCell(Vector3f pointA, Vector3f PointB, Vector3f PointC) {
        Cell newCell = new Cell();
        newCell.initialize(pointA.clone(), PointB.clone(), PointC.clone());
        cellList.add(newCell);
    }

    /**
     * Does noting at this point. Stubbed for future use in animating the mesh
     * @param elapsedTime
     */
    void Update(float elapsedTime) {
    }

    public int getNumCells() {
        return cellList.size();
    }

    public Cell getCell(int index) {
        return (cellList.get(index));
    }

    /**
     * Force a point to be inside the cell
     */
    public Vector3f snapPointToCell(Cell cell, Vector3f point) {
        if (!cell.contains(point)) {
            cell.forcePointToCellColumn(point);
        }

        cell.computeHeightOnCell(point);
        return point;
    }

    /**
     * Force a point to be inside the nearest cell on the mesh
     */
    Vector3f snapPointToMesh(Vector3f point) {
        return snapPointToCell(findClosestCell(point), point);
    }

    /**
     * Find the closest cell on the mesh to the given point
     * AVOID CALLING! not a fast routine!
     */
    public Cell findClosestCell(Vector3f point) {
        float closestDistance = 3.4E+38f;
        float closestHeight = 3.4E+38f;
        boolean foundHomeCell = false;
        float thisDistance;
        Cell closestCell = null;

        // oh dear this is not fast
        for (Cell cell : cellList) {
            if (cell.contains(point)) {
                thisDistance = Math.abs(cell.getHeightOnCell(point) - point.y);

                if (foundHomeCell) {
                    if (thisDistance < closestHeight) {
                        closestCell = cell;
                        closestHeight = thisDistance;
                    }
                } else {
                    closestCell = cell;
                    closestHeight = thisDistance;
                    foundHomeCell = true;
                }
            }

            if (!foundHomeCell) {
                Vector2f start = new Vector2f(cell.getCenter().x, cell.getCenter().z);
                Vector2f end = new Vector2f(point.x, point.z);
                Line2D motionPath = new Line2D(start, end);

                ClassifyResult Result = cell.classifyPathToCell(motionPath);

                if (Result.result == Cell.PathResult.ExitingCell) {
                    Vector3f ClosestPoint3D = new Vector3f(
                            Result.intersection.x, 0.0f, Result.intersection.y);
                    cell.computeHeightOnCell(ClosestPoint3D);

                    ClosestPoint3D = ClosestPoint3D.subtract(point);

                    thisDistance = ClosestPoint3D.length();

                    if (thisDistance < closestDistance) {
                        closestDistance = thisDistance;
                        closestCell = cell;
                    }
                }
            }
        }

        return closestCell;
    }

    /**
     * Test to see if two points on the mesh can view each other
     * FIXME: EndCell is the last visible cell?
     *
     * @param StartCell
     * @param StartPos
     * @param EndPos
     * @return
     */
    boolean isInLineOfSight(Cell StartCell, Vector3f StartPos, Vector3f EndPos) {
        return isInLineOfSight(StartCell, StartPos, EndPos, null);
    }

    boolean isInLineOfSight(Cell StartCell, Vector3f StartPos, Vector3f EndPos, DebugInfo debugInfo) {
        Line2D MotionPath = new Line2D(new Vector2f(StartPos.x, StartPos.z),
                new Vector2f(EndPos.x, EndPos.z));

        Cell testCell = StartCell;
        Cell.ClassifyResult result = testCell.classifyPathToCell(MotionPath);
        Cell.ClassifyResult prevResult = result;

        while (result.result == Cell.PathResult.ExitingCell) {
            if (result.cell == null)// hit a wall, so the point is not visible
            {
                if (debugInfo != null) {
                    debugInfo.setFailedCell(prevResult.cell);
                }
                return false;
            }
            if (debugInfo != null) {
                debugInfo.addPassedCell(prevResult.cell);
            }
            prevResult = result;
            result = result.cell.classifyPathToCell(MotionPath);
        }
        if (debugInfo != null) {
            debugInfo.setEndingCell(prevResult.cell);
        }
        return (result.result == Cell.PathResult.EndingCell || result.result == Cell.PathResult.ExitingCell); //This is messing up the result, I think because of shared borders
    }

    /**
     * Link all the cells that are in our pool
     */
    public void linkCells() {
//        for (int i = 0; i < cellList.size(); i++){
//            for (int j = i+1; j < cellList.size(); j++){
//                cellList.get(i).checkAndLink(cellList.get(j));
//            }
//        }
        for (Cell pCellA : cellList) {
            for (Cell pCellB : cellList) {
                if (pCellA != pCellB) {
                    pCellA.checkAndLink(pCellB, 0.001f);
                }
            }
        }
    }

    private void addFace(Vector3f vertA, Vector3f vertB, Vector3f vertC) {
        // some art programs can create linear polygons which have two or more
        // identical vertices. This creates a poly with no surface area,
        // which will wreak havok on our navigation mesh algorithms.
        // We only except polygons with unique vertices.
        if ((!vertA.equals(vertB)) && (!vertB.equals(vertC)) && (!vertC.equals(vertA))) {
            addCell(vertA, vertB, vertC);
        } else {
            System.out.println("Warning, Face winding incorrect");
        }
    }

    public void loadFromData(Vector3f[] positions, short[][] indices) {
        Plane up = new Plane();
        up.setPlanePoints(Vector3f.UNIT_X, Vector3f.ZERO, Vector3f.UNIT_Z);
        up.getNormal();

        for (int i = 0; i < indices.length / 3; i++) {
            Vector3f vertA = positions[indices[i][0]];
            Vector3f vertB = positions[indices[i][1]];
            Vector3f vertC = positions[indices[i][2]];

            Plane p = new Plane();
            p.setPlanePoints(vertA, vertB, vertC);
            if (up.pseudoDistance(p.getNormal()) <= 0.0f) {
                System.out.println("Warning, normal of the plane faces downward!!!");
                continue;
            }

            addFace(vertA, vertB, vertC);
        }

        linkCells();
    }

    public void loadFromMesh(Mesh mesh) {
        clear();

        Vector3f a = new Vector3f();
        Vector3f b = new Vector3f();
        Vector3f c = new Vector3f();

        Plane up = new Plane();
        up.setPlanePoints(Vector3f.UNIT_X, Vector3f.ZERO, Vector3f.UNIT_Z);
        up.getNormal();

        IndexBuffer ib = mesh.getIndexBuffer();
        FloatBuffer pb = mesh.getFloatBuffer(Type.Position);
        pb.clear();
        for (int i = 0; i < mesh.getTriangleCount() * 3; i += 3) {
            int i1 = ib.get(i + 0);
            int i2 = ib.get(i + 1);
            int i3 = ib.get(i + 2);
            BufferUtils.populateFromBuffer(a, pb, i1);
            BufferUtils.populateFromBuffer(b, pb, i2);
            BufferUtils.populateFromBuffer(c, pb, i3);

            Plane p = new Plane();
            p.setPlanePoints(a, b, c);
            if (up.pseudoDistance(p.getNormal()) <= 0.0f) {
                System.out.println("Warning, normal of the plane faces downward!!!");
                continue;
            }

            addFace(a, b, c);
        }

        linkCells();
    }

    public void write(JmeExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.writeSavableArrayList(cellList, "cellarray", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JmeImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        cellList = (ArrayList<Cell>) capsule.readSavableArrayList("cellarray", new ArrayList<Cell>());
    }
}
