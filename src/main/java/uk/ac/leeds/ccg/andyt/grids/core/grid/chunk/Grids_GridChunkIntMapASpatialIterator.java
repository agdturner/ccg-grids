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
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntMap.OffsetBitSet;

/**
 * For iterating through the values in a Grids_GridChunkIntMap instance. The
 * values are returned in no particular spatial order.
 */
public class Grids_GridChunkIntMapASpatialIterator extends Grids_GridChunkNumberMapASpatialIterator {

    private int DefaultValue;
    private Grids_GridChunkIntMap.GridChunkIntMapData Data;
    private TreeMap<Integer, OffsetBitSet> DataMapBitSet;
    private Iterator<Integer> DataMapBitSetIte;
    private int DataMapBitSetValue;
    private TreeMap<Integer, HashSet<Grids_2D_ID_int>> DataMapHashSet;
    private Iterator<Integer> DataMapHashSetIte;
    private int DataMapHashSetValue;

    protected Grids_GridChunkIntMapASpatialIterator() {
    }

    public Grids_GridChunkIntMapASpatialIterator(
            Grids_GridChunkIntMap chunk) {
        super(chunk);
        Data = chunk.getData();
        DataMapBitSet = Data.DataMapBitSet;
        DataMapHashSet = Data.DataMapHashSet;
        DataMapBitSetNumberOfValues = 0;
        DataMapBitSetIte = DataMapBitSet.keySet().iterator();
        if (DataMapBitSetIte.hasNext()) {
            ValuesLeft = true;
            DataMapBitSetValue = DataMapBitSetIte.next();
            DataMapBitSetNumberOfValues += DataMapBitSet.get(DataMapBitSetValue)._BitSet.cardinality();
        }
        NumberOfNoDataValues -= DataMapBitSetNumberOfValues;
        DataMapBitSetIte = DataMapBitSet.keySet().iterator();
        DataMapBitSetIndex = 0;
        DataMapHashSetNumberOfValues = 0;
        DataMapHashSetIte = DataMapHashSet.keySet().iterator();
        if (DataMapHashSetIte.hasNext()) {
            ValuesLeft = true;
            DataMapHashSetValue = DataMapHashSetIte.next();
            DataMapHashSetNumberOfValues += DataMapHashSet.get(DataMapHashSetValue).size();
        }
        NumberOfNoDataValues -= DataMapHashSetNumberOfValues;
        DataMapHashSetIte = DataMapHashSet.keySet().iterator();
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

}
