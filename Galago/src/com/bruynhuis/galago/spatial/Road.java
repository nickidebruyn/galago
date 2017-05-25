/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.spatial;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a road mesh. It can be used for creation of a road like surface.
 * 
 * @author NideBruyn
 */
public class Road extends Mesh {

    private float width;
    private List<Vector3f> cp;

    /**
     * Serialization only. Do not use.
     */
    public Road() {
    }

    /**
     * Create a quad with the given width and height. The quad is always created
     * in the XY plane.
     *
     * @param width The X extent or width
     * @param controlPoint
     */
    public Road(float width, float height, List<Vector3f> controlPoint) {
        updateGeometry(width, height, controlPoint);
    }

    /**
     * Create a quad with the given width and height. The quad is always created
     * in the XY plane.
     *
     * @param width The X extent or width
     * @param controlPoint
     * @param flipCoords If true, the texture coordinates will be flipped along
     * the Y axis.
     */
    public Road(float width, float height, List<Vector3f> controlPoint, boolean flipCoords) {
        updateGeometry(width, height, controlPoint, flipCoords, false);
    }

    /**
     * Create a quad with the given width and height. The quad is always created
     * in the XY plane.
     *
     * @param width The X extent or width
     * @param height
     * @param controlPoint
     * @param flipCoords If true, the texture coordinates will be flipped along
     * the Y axis.
     * @param tessellation Set if to use Tesselation indexation
     */
    public Road(float width, float height, List<Vector3f> controlPoint, boolean flipCoords, boolean tessellation) {
        updateGeometry(width, height, controlPoint, flipCoords, tessellation);
    }

//    public float getHeight() {
//        return height;
//    }
    public float getWidth() {
        return width;
    }

    public void updateGeometry(float width, float height, List<Vector3f> controlPoint) {
        updateGeometry(width, height, controlPoint, false, false);
    }

    @Override
    public void clearBuffer(VertexBuffer.Type type) {
        super.clearBuffer(type); //To change body of generated methods, choose Tools | Templates.
    }

    public void updateGeometry(float width, float height, List<Vector3f> controlPoint, boolean flipCoords, boolean tessellation) {
        this.width = width;
        if (controlPoint.get(0).z > controlPoint.get(3).z) {
            cp = new ArrayList();
            cp.add(controlPoint.get(3));
            cp.add(controlPoint.get(2));
            cp.add(controlPoint.get(1));
            cp.add(controlPoint.get(0));
        } else {
            cp = controlPoint;
        }

        float lenght = FastMath.getBezierP1toP2Length(cp.get(0), cp.get(1), cp.get(2), cp.get(3));

        int modulo = (int) (lenght % height);
        int nbSection = (int) ((lenght - modulo) / height);
        //nbSection = (int) ((lenght) / width)+1;
        List<Vector3f> computePosition = new ArrayList();
        for (float i = 0; i <= nbSection; i++) {
            if (i < nbSection) {
                Vector3f v1 = FastMath.interpolateBezier(i / nbSection, cp.get(0), cp.get(1), cp.get(2), cp.get(3));
                Vector3f v2 = FastMath.interpolateBezier((i + 1) / nbSection, cp.get(0), cp.get(1), cp.get(2), cp.get(3));

                Vector3f vv1 = v2.subtract(v1);
                float anglex = FastMath.acos(vv1.x / height);
                float anglez = FastMath.asin(vv1.z / height);

                float angle1 = anglex - FastMath.HALF_PI;
                float angle2 = anglex + FastMath.HALF_PI;

                float angle3 = anglez - FastMath.HALF_PI;
                float angle4 = anglez + FastMath.HALF_PI;
                
                computePosition.add( v1.add(new Vector3f(FastMath.cos(angle1) * width , 1, FastMath.sin(angle1) * width )));
                computePosition.add( v1.add(new Vector3f(FastMath.cos(angle2) * width , 1, FastMath.sin(angle2) * width )));
            } 
            else {
                Vector3f v1 = FastMath.interpolateBezier((i) / nbSection, cp.get(0), cp.get(1), cp.get(2), cp.get(3));
                Vector3f v2 = FastMath.interpolateBezier((i+1) / nbSection, cp.get(0), cp.get(1), cp.get(2), cp.get(3));
                
                Vector3f vv1 = v2.subtract(v1);
                float anglex = FastMath.acos(vv1.x / height);
                float anglez = FastMath.asin(vv1.z / height);

                float angle1 = anglex - FastMath.HALF_PI;
                float angle2 = anglex + FastMath.HALF_PI;

                float angle3 = anglez - FastMath.HALF_PI;
                float angle4 = anglez + FastMath.HALF_PI;
                
                computePosition.add( v1.add(new Vector3f(FastMath.cos(angle1) * width , 1, FastMath.sin(angle1) * width )));
                computePosition.add( v1.add(new Vector3f(FastMath.cos(angle2) * width , 1, FastMath.sin(angle2) * width )));
//                computePosition.add(v1);
//                computePosition.add(v1);
            }
        }

//        Vector3f v1 = FastMath.interpolateBezier(0 / nbSection, cp.get(0), cp.get(1), cp.get(2), cp.get(3));
//        Vector3f v2 = FastMath.interpolateBezier(0.1f / nbSection, cp.get(0), cp.get(1), cp.get(2), cp.get(3));
//
//        Vector3f vv1 = v2.subtract(v1);
//        float anglex = FastMath.acos(vv1.x / width);
//        float anglez = FastMath.asin(vv1.z / width);
//
//        float angle1 = anglex - FastMath.HALF_PI;
//        float angle2 = anglex + FastMath.HALF_PI;
//
//        float angle3 = anglez - FastMath.HALF_PI;
//        float angle4 = anglez + FastMath.HALF_PI;
//
//        List<Vector3f> bezier1 = new ArrayList();
//        bezier1.add(cp.get(0).add(new Vector3f(FastMath.cos(angle1) * (width/2), 1, FastMath.sin(angle3) * (width/2))));
//        bezier1.add(cp.get(1).add(new Vector3f(FastMath.cos(angle1) * (width/2), 1, FastMath.sin(angle3) * (width/2))));
//        bezier1.add(cp.get(2).add(new Vector3f(FastMath.cos(angle1) * (width/2), 1, FastMath.sin(angle3) * (width/2))));
//        bezier1.add(cp.get(3).add(new Vector3f(FastMath.cos(angle1) * (width/2), 1, FastMath.sin(angle3) * (width/2))));
//
//        List<Vector3f> bezier2 = new ArrayList();
//        bezier2.add(cp.get(0).add(new Vector3f(FastMath.cos(angle2) * (width/2), 1, FastMath.sin(angle4) * (width/2))));
//        bezier2.add(cp.get(1).add(new Vector3f(FastMath.cos(angle2) * (width/2), 1, FastMath.sin(angle4) * (width/2))));
//        bezier2.add(cp.get(2).add(new Vector3f(FastMath.cos(angle2) * (width/2), 1, FastMath.sin(angle4) * (width/2))));
//        bezier2.add(cp.get(3).add(new Vector3f(FastMath.cos(angle2) * (width/2), 1, FastMath.sin(angle4) * (width/2))));
//
//        for (float i = 0; i <= nbSection; i++) {
//            if(1 < nbSection){
//            computePosition.add(FastMath.interpolateBezier(i / nbSection, bezier1.get(0), bezier1.get(1), bezier1.get(2), bezier1.get(3)));
//            computePosition.add(FastMath.interpolateBezier(i / nbSection, bezier2.get(0), bezier2.get(1), bezier2.get(2), bezier2.get(3)));
//            } else {
//                
//            }
//        }

        float[] vertexPosition = new float[(nbSection + 1) * 2 * 3];
        float[] vertexTexCoord = new float[nbSection * 4 * 2];
        float[] vertexNormalCoord = new float[nbSection * 4 * 3];
        int[] vertexIndex = new int[(nbSection) * 2 * 3];

        int i = 0;
        while (i <= nbSection) {
            int inPos = i * 2;
            int pos = i * 6;
            if (i % 2 == 0) {
                vertexPosition[pos + 0] = computePosition.get(inPos).x;
                vertexPosition[pos + 1] = computePosition.get(inPos).y;//Height <---- Will use the 3D Interpolation of the Start and End Height
                vertexPosition[pos + 2] = computePosition.get(inPos).z;

                vertexPosition[pos + 3] = computePosition.get(inPos + 1).x;
                vertexPosition[pos + 4] = computePosition.get(inPos + 1).y;//Height <---- Will use the 3D Interpolation of the Start and End Height
                vertexPosition[pos + 5] = computePosition.get(inPos + 1).z;

            } else {
                vertexPosition[pos + 3] = computePosition.get(inPos).x;
                vertexPosition[pos + 4] = computePosition.get(inPos).y;//Height <---- Will use the 3D Interpolation of the Start and End Height
                vertexPosition[pos + 5] = computePosition.get(inPos).z;

                vertexPosition[pos + 0] = computePosition.get(inPos + 1).x;
                vertexPosition[pos + 1] = computePosition.get(inPos + 1).y;//Height <---- Will use the 3D Interpolation of the Start and End Height
                vertexPosition[pos + 2] = computePosition.get(inPos + 1).z;

            }
            i++;
        }
        System.out.println(i);
        i = 0;
        while (i <= nbSection) {

            int pos = i * 4;
            if (i % 2 == 0) {
                vertexTexCoord[pos + 0] = 0;
                vertexTexCoord[pos + 1] = 0;

                vertexTexCoord[pos + 2] = 1;
                vertexTexCoord[pos + 3] = 0;
            } else {
                vertexTexCoord[pos + 0] = 1;
                vertexTexCoord[pos + 1] = 1;

                vertexTexCoord[pos + 2] = 0;
                vertexTexCoord[pos + 3] = 1;
            }

            i++;
        }
        i = 0;
        while (i < nbSection) {

            int pos = i * 6;
            vertexNormalCoord[pos + 0] = 0;
            vertexNormalCoord[pos + 1] = 0;
            vertexNormalCoord[pos + 2] = 1;

            vertexNormalCoord[pos + 3] = 0;
            vertexNormalCoord[pos + 4] = 0;
            vertexNormalCoord[pos + 5] = 1;
            i++;
        }
        i = 0;
        if (tessellation) {
            while (i < nbSection) {
                int inPos = i * 2;
                int pos = i * 4;
                if (i % 2 == 0) {

                    vertexIndex[pos + 0] = inPos + 0;
                    vertexIndex[pos + 1] = inPos + 1;
                    vertexIndex[pos + 2] = inPos + 2;
                    vertexIndex[pos + 3] = inPos + 3;

                } else {

                    vertexIndex[pos + 0] = inPos + 1;
                    vertexIndex[pos + 1] = inPos + 0;
                    vertexIndex[pos + 2] = inPos + 3;
                    vertexIndex[pos + 3] = inPos + 2;

                }

                i++;
            }
            setBuffer(VertexBuffer.Type.Index, 4, vertexIndex);
            setMode(Mesh.Mode.Patch);
            setPatchVertexCount(4);
        } else {
            while (i < nbSection) {
                int inPos = i * 2;
                int pos = i * 6;
                if (i % 2 == 0) {
                    vertexIndex[pos + 0] = inPos + 0;
                    vertexIndex[pos + 1] = inPos + 1;
                    vertexIndex[pos + 2] = inPos + 2;

                    vertexIndex[pos + 3] = inPos + 0;
                    vertexIndex[pos + 4] = inPos + 2;
                    vertexIndex[pos + 5] = inPos + 3;
                } else {
                    vertexIndex[pos + 0] = inPos + 0;
                    vertexIndex[pos + 1] = inPos + 2;
                    vertexIndex[pos + 2] = inPos + 1;

                    vertexIndex[pos + 3] = inPos + 0;
                    vertexIndex[pos + 4] = inPos + 3;
                    vertexIndex[pos + 5] = inPos + 2;
                }

                i++;
            }
            setBuffer(VertexBuffer.Type.Index, 3, vertexIndex);
        }

        setBuffer(VertexBuffer.Type.Position, 3, vertexPosition);
        setBuffer(VertexBuffer.Type.TexCoord, 2, vertexTexCoord);
        setBuffer(VertexBuffer.Type.Normal, 3, vertexNormalCoord);

        updateBound();
        setStatic();
    }

}