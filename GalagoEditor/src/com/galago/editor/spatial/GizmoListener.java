package com.galago.editor.spatial;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author ndebruyn
 */
public interface GizmoListener {
    
    public void gizmoUpdate(Vector3f position, Quaternion rotations, Vector3f scale);
    
}
