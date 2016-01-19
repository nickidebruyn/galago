/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.flat.screen;

import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Line;

/**
 *
 * @author nidebruyn
 */
public class LineData {
    
    private Line line;
    private Vector3f start;
    private Vector3f end;

    public LineData(Line line, Vector3f start, Vector3f end) {
        this.line = line;
        this.start = start;
        this.end = end;
    }

    public Line getLine() {
        return line;
    }

    public Vector3f getStart() {
        return start;
    }

    public Vector3f getEnd() {
        return end;
    }
    
    public void update(Vector3f start, Vector3f end) {
        this.start = start;
        this.end = end;
        line.updatePoints(start, end);
    }
    
}
