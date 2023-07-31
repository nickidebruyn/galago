package com.jme3.ai.navmesh;

/**
 * A Plane in 3D Space represented in point-normal form (Ax + By + Cz + D = 0).
 * 
 * The convention for the distance constant D is:
 * 
 * D = -(A, B, C) dot (X, Y, Z)
 * 
 * Portions Copyright (C) Greg Snook, 2000
 * 
 * @author TR
 * 
 */
public class Plane extends com.jme3.math.Plane {

    /**
     * Given Z and Y, Solve for X on the plane
     *
     * @param y
     * @param z
     * @return
     */
    public float solveForX(float y, float z) {
        // Ax + By + Cz + D = 0
        // Ax = -(By + Cz + D)
        // x = -(By + Cz + D)/A

        // if (m_Normal[x] != null)
        // {
        // using jme plane we have to flip the sign as the constant is
        // calculated different
        // return ( -(normal.y*Y + normal.z*Z + constant) / normal.x );
        return (-(normal.y * y + normal.z * z - constant) / normal.x);
        // }
        //
        // return (0.0f);
    }

    /**
     * Given X and Z, Solve for Y on the plane
     *
     * @param x
     * @param z
     * @return
     */
    public float solveForY(float x, float z) {
        // Ax + By + Cz + D = 0
        // By = -(Ax + Cz + D)
        // y = -(Ax + Cz + D)/B

        // if (m_Normal[1])
        // {
        // using jme plane we have to flip the sign as the constant is
        // calculated different
        // return ( -(normal.x*X + normal.z*Z + constant) / normal.y );
        return (-(normal.x * x + normal.z * z - constant) / normal.y);
        // }
        //
        // return (0.0f);
    }

    /**
     * Given X and Y, Solve for Z on the plane
     *
     * @param x
     * @param y
     * @return
     */
    public float solveForZ(float x, float y) {
        // Ax + By + Cz + D = 0
        // Cz = -(Ax + By + D)
        // z = -(Ax + By + D)/C

        // if (m_Normal[2])
        // {
        // using jme plane we have to flip the sign as the constant is
        // calculated different
        // return ( -(normal.x*X + normal.y*Y + constant) / normal.z );
        return (-(normal.x * x + normal.y * y - constant) / normal.z);
        // }
        //
        // return (0.0f);
    }
}
