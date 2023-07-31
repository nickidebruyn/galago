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

/**
 * Implementation of a binary min heap
 *
 * @author Ron Weiss (ronw@ee.columbia.edu)
 */
public class MinHeap extends Heap
{
    /**
     * Creates an empty MinHeap.
     */
    public MinHeap()
    {
        super();
    }

    /**
     *  Use given Comparator for all comparisons between elements in
     *  this MinHeap.  Otherwise rely on compareTo methods and
     *  Comparable Objects.
     */
    public MinHeap(Comparator c)
    {
        super(c);
    }

    /**
     * Creates an empty MinHeap with the given capacity.
     */
    public MinHeap(int capacity)
    {
        super(capacity);
    }

    /**
     * Create a new MinHeap containing the elements of the given
     * Collection.
     */
    public MinHeap(Collection c)
    {
        super(c);
    }

    /**
     * Delete the smallest element of this MinHeap.
     */
    public Object deleteMin()
    {
        return remove(0);
    }

    /**
     * Perform heap sort on the data stored in this heap.  After
     * calling sort, a call to this objects iterator() method will
     * iterate through the data stored in the heap in sorted order.
     * This is not a stable sort.
     */
    public void sort()
    {
        super.sort();

        // this just so happens to maintain the min-heap property
        isHeap = true;
    }
}
