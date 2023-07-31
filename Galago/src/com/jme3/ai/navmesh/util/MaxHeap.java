/*
 *  Copyright 2006-2007 Columbia University.
 *
 *  This file is part of MEAPsoft.
 *
 *  MEAPsoft is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation.
 *
 *  MEAPsoft is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MEAPsoft; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA
 *
 *  See the file "COPYING" for the text of the license.
 */

package com.jme3.ai.navmesh.util;

import java.util.Collection;
import java.util.Comparator;

// I can't believe that the huge Java API doesn't already include such
// a basic data structure

/**
 * Implementation of a binary max heap.
 *
 * @author Ron Weiss (ronw@ee.columbia.edu)
 */
public class MaxHeap extends Heap
{
    /**
     * Creates an empty MaxHeap.
     */
    public MaxHeap()
    {
        super();
    }

    /**
     *  Use given Comparator for all comparisons between elements in
     *  this MaxHeap.  Otherwise rely on compareTo methods and
     *  Comparable Objects.
     */
    public MaxHeap(Comparator c)
    {
        super(c);
    }

    /**
     * Creates an empty MaxHeap with the given capacity.
     */
    public MaxHeap(int capacity)
    {
        super(capacity);
    }

    /**
     * Create a new MaxHeap containing the elements of the given
     * Collection.
     */
    public MaxHeap(Collection c)
    {
        super(c);
    }

    /**
     * Delete the largest element of this MaxHeap.
     */
    public Object deleteMax()
    {
        return remove(0);
    }

    /**
     * Compare two Objects in this heap - wrapper around
     * compareTo/Comparator.compare
     */
    protected int cmp(int node1, int node2)
    {
        return -super.cmp(node1, node2);
    }
}
