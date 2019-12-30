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
package uk.ac.leeds.ccg.grids.d2.chunk.i;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntMap.OffsetBitSet;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkNumberMapASpatialIterator;

/**
 * For iterating through the values in a Grids_GridChunkIntMap instance. The
 * values are returned in no particular spatial order.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIteratorIntMapASpatial
        extends Grids_ChunkNumberMapASpatialIterator {

    private static final long serialVersionUID = 1L;

    protected int NumberOfDefaultValues;
    protected int NumberOfNoDataValues;

    protected int DefaultValue;
    protected Grids_ChunkIntMap.GridChunkIntMapData Data;
    protected TreeMap<Integer, OffsetBitSet> DataMapBitSet;
    protected Iterator<Integer> DataMapBitSetIte;
    protected int DataMapBitSetValue;
    protected TreeMap<Integer, HashSet<Grids_2D_ID_int>> DataMapHashSet;
    protected Iterator<Integer> DataMapHashSetIte;
    protected int DataMapHashSetValue;

    public Grids_ChunkIteratorIntMapASpatial(
            Grids_ChunkIntMap chunk) {
        super(chunk);
        Data = chunk.getData();
        DataMapBitSet = Data.DataMapBitSet;
        DataMapHashSet = Data.DataMapHashSet;
        dataMapBitSetNumberOfValues = 0;
        DataMapBitSetIte = DataMapBitSet.keySet().iterator();
        if (DataMapBitSetIte.hasNext()) {
            hasNext = true;
            DataMapBitSetValue = DataMapBitSetIte.next();
            dataMapBitSetNumberOfValues += DataMapBitSet.get(DataMapBitSetValue).bitSet.cardinality();
        }
        NumberOfNoDataValues -= dataMapBitSetNumberOfValues;
        DataMapBitSetIte = DataMapBitSet.keySet().iterator();
        dataMapBitSetIndex = 0;
        dataMapHashSetNumberOfValues = 0;
        DataMapHashSetIte = DataMapHashSet.keySet().iterator();
        if (DataMapHashSetIte.hasNext()) {
            hasNext = true;
            DataMapHashSetValue = DataMapHashSetIte.next();
            dataMapHashSetNumberOfValues += DataMapHashSet.get(DataMapHashSetValue).size();
        }
        NumberOfNoDataValues -= dataMapHashSetNumberOfValues;
        DataMapHashSetIte = DataMapHashSet.keySet().iterator();
        dataMapHashSetIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    /**
     * Returns the next element in the iteration. First all the default values
     * are returned then all the values in DataMapBitSet, then all the values in
     * DataMapHashSet.
     *
     * @return the next element in the iteration or null.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public Integer next() {
        if (hasNext) {
            if (defaultValueIndex == NumberOfDefaultValues - 1) {
                if (dataMapBitSetIndex == dataMapBitSetNumberOfValues - 1) {
                    if (DataMapBitSetIte.hasNext()) {
                        DataMapBitSetValue = DataMapBitSetIte.next();
                        dataMapBitSetNumberOfValues = DataMapBitSet
                                .get(DataMapBitSetValue).bitSet.cardinality();
                        dataMapBitSetIndex = 0;
                        return DataMapBitSetValue;
                    } else {
                        if (dataMapHashSetIndex == dataMapHashSetNumberOfValues - 1) {
                            if (DataMapHashSetIte.hasNext()) {
                                DataMapHashSetValue = DataMapHashSetIte.next();
                                dataMapHashSetNumberOfValues = DataMapHashSet
                                        .get(DataMapHashSetValue).size();
                                dataMapHashSetIndex = 0;
                                return DataMapHashSetValue;
                            } else {
                                hasNext = false;
                                return null;
                            }
                        } else {
                            dataMapHashSetIndex++;
                            return DataMapHashSetValue;
                        }
                    }
                } else {
                    dataMapBitSetIndex++;
                    return DataMapBitSetValue;
                }
            } else {
                defaultValueIndex++;
                return DefaultValue;
            }
        } else {
            return null;
        }
    }

}
