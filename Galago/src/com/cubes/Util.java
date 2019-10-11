/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cubes;

import java.util.Map;
import com.jme3.math.Vector3f;

/**
 *
 * @author Carl
 */
public class Util{
    
    private static final float MAX_FLOAT_ROUNDING_DIFFERENCE = 0.0001f;
    
    public static boolean isValidIndex(byte[][][] array, Vector3Int index){
        return ((index.getX() >= 0) && (index.getX() < array.length)
             && (index.getY() >= 0) && (index.getY() < array[0].length)
             && (index.getZ() >= 0) && (index.getZ() < array[0][0].length));
    }
    
    public static boolean isValidIndex(Object[][][] array, Vector3Int index){
        return ((index.getX() >= 0) && (index.getX() < array.length)
             && (index.getY() >= 0) && (index.getY() < array[0].length)
             && (index.getZ() >= 0) && (index.getZ() < array[0][0].length));
    }

    public static Vector3f compensateFloatRoundingErrors(Vector3f vector){
        return new Vector3f(compensateFloatRoundingErrors(vector.getX()),
                            compensateFloatRoundingErrors(vector.getY()),
                            compensateFloatRoundingErrors(vector.getZ()));
    }
    
    public static float compensateFloatRoundingErrors(float number){
        float remainder = (number % 1);
        if ((remainder < MAX_FLOAT_ROUNDING_DIFFERENCE) || (remainder > (1 - MAX_FLOAT_ROUNDING_DIFFERENCE))){
            number = Math.round(number);
        }
        return number;
    }
    
    public static <T, E> T getHashKeyByValue(Map<T, E> map, E value){
        for(Map.Entry<T, E> entry : map.entrySet()){
            if(value.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return null;
    }
}
