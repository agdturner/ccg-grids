/*
 * Copyright 2019 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.agdt.grids.core.grid.chunk;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkIntMap.OffsetBitSet;

/**
 * For iterating through the values in a Grids_GridChunkIntMap instance. The
 * values are returned in no particular spatial order.
*
 * @author Andy Turner
 * @version 1.0.0
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
