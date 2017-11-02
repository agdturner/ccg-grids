/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleMap.OffsetBitSet;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellDoubleChunkMap
 * instance. The values are not returned in any particular order.
 */
public class Grids_GridChunkDoubleMapIterator extends Grids_AbstractIterator {

    private int NumberOfCells;
    private double DefaultValue;
    private int NumberOfDefaultValues;
    private int DefaultValueIndex;
    private Grids_GridChunkDoubleMap.GridChunkDoubleMapData Data;
    private TreeMap<Double, OffsetBitSet> DataMapBitSet;
    private Iterator<Double> DataMapBitSetIte;
    private int DataMapBitSetIndex;
    private int DataMapBitSetNumberOfValues;
    private double DataMapBitSetValue;
    private TreeMap<Double, HashSet<Grids_2D_ID_int>> DataMapHashSet;
    private Iterator<Double> DataMapHashSetIte;
    private int DataMapHashSetNumberOfValues;
    private int DataMapHashSetIndex;
    private double DataMapHashSetValue;
    private boolean ValuesLeft;

    protected Grids_GridChunkDoubleMapIterator() {
    }

    public Grids_GridChunkDoubleMapIterator(
            Grids_GridChunkDoubleMap chunk) {
        Data = chunk.getData();
        DataMapBitSet = Data.DataMapBitSet;
        DataMapBitSetIte = DataMapBitSet.keySet().iterator();
        DataMapHashSet = Data.DataMapHashSet;
        DataMapHashSetIte = DataMapHashSet.keySet().iterator();
        NumberOfCells = chunk.ChunkNRows * chunk.ChunkNCols;
        DefaultValue = chunk.DefaultValue;
        NumberOfDefaultValues = chunk.getNumberOfDefaultValues(NumberOfCells);
        DefaultValueIndex = 0;
        ValuesLeft = false;
        if (NumberOfDefaultValues > 0) {
            ValuesLeft = true;
        }
        if (DataMapBitSetIte.hasNext()) {
            ValuesLeft = true;
            DataMapBitSetValue = DataMapBitSetIte.next();
            DataMapBitSetNumberOfValues = DataMapBitSet.get(DataMapBitSetValue)._BitSet.cardinality();
        }
        DataMapBitSetIndex = 0;
        if (DataMapHashSetIte.hasNext()) {
            ValuesLeft = true;
            DataMapHashSetValue = DataMapHashSetIte.next();
            DataMapHashSetNumberOfValues = DataMapHashSet.get(DataMapHashSetValue).size();
        }
        DataMapHashSetIndex = 0;
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    @Override
    public boolean hasNext() {
        return ValuesLeft;
    }

    /**
     * Returns the next element in the iteration. First all the default values
     * are returned then all the values in DataMapBitSet, then all the values in
     * DataMapHashSet. 
     *
     * @return the next element in the iteration or null.
     * @exception NoSuchElementException iteration has no more elements.
     */
    @Override
    public Object next() {
        if (ValuesLeft) {
        if (DefaultValueIndex == NumberOfDefaultValues - 1) {
            if (DataMapBitSetIndex == DataMapBitSetNumberOfValues - 1) {
                if (DataMapBitSetIte.hasNext()) {
                    DataMapBitSetValue = DataMapBitSetIte.next();
                    DataMapBitSetNumberOfValues = DataMapBitSet.get(DataMapBitSetValue)._BitSet.cardinality();
                    DataMapBitSetIndex = 0;
                    return DataMapBitSetValue;
                } else {
                    if (DataMapHashSetIndex == DataMapHashSetNumberOfValues - 1) {
                        if (DataMapHashSetIte.hasNext()) {
                            DataMapHashSetValue = DataMapHashSetIte.next();
                            DataMapHashSetNumberOfValues = DataMapHashSet.get(DataMapHashSetValue).size();
                            DataMapHashSetIndex = 0;
                            return DataMapHashSetValue;
                        } else {
                            ValuesLeft = false;
                            return null;
                        }
                    } else {
                        DataMapHashSetIndex++;
                        return DataMapHashSetValue;
                    }
                }
            } else {
                DataMapBitSetIndex++;
                return DataMapBitSetValue;
            }
        } else {
            DefaultValueIndex++;
            return DefaultValue;
        }
        } else {
            return null;
        }
    }

    /**
     *
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation). This method can be called only once per
     * call to <tt>next</tt>. The behaviour of an iterator is unspecified if the
     * underlying collection is modified while the iteration is in progress in
     * any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     * operation is not supported by this Iterator.
     *
     * @exception IllegalStateException if the <tt>next</tt> method has not yet
     * been called, or the <tt>remove</tt> method has already been called after
     * the last call to the <tt>next</tt>
     * method.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
