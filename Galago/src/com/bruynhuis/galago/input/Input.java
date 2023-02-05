package com.bruynhuis.galago.input;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ndebruyn
 */
public class Input {

    private static Map<String, Float> inputMaps = new HashMap<>();
    private static Map<String, String> inputGroups = new HashMap<>();

    public static void registerInput(String name) {
        inputMaps.put(name, Float.valueOf(0));

    }

    public static boolean hasMapping(String name) {
        return inputMaps.containsKey(name);

    }

    public static void set(String name, float val) {
        inputMaps.replace(name, val);

    }
    
    public static float get(String name) {
        return inputMaps.get(name);
    }
    
    public static void consume(String name) {
        inputMaps.replace(name, 0f);

    }

}
