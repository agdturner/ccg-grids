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
package uk.ac.leeds.ccg.grids.d2.chunk.d;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkNumberMapASpatialIterator;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_OffsetBitSet;

/**
 * For iterating through the values in a Grids_GridChunkDoubleMap instance. The
 * values are not returned in any particular spatial order.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIteratorDoubleMapASpatial
        extends Grids_ChunkNumberMapASpatialIterator {

    private static final long serialVersionUID = 1L;

    /**
     * numberOfDefaultValues
     */
    protected int numberOfDefaultValues;

    /**
     * numberOfNoDataValues
     */
    protected int numberOfNoDataValues;

    /**
     * defaultValue
     */
    protected double defaultValue;

    /**
     * data
     */
    protected Grids_ChunkDoubleMap.GridChunkDoubleMapData data;

    /**
     * dataMapBitSet
     */
    protected TreeMap<Double, Grids_OffsetBitSet> dataMapBitSet;

    /**
     * dataMapBitSetIte
     */
    protected Iterator<Double> dataMapBitSetIte;

    /**
     * dataMapBitSetValue
     */
    protected double dataMapBitSetValue;

    /**
     * dataMapHashSet
     */
    protected TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;

    /**
     * dataMapHashSetIte
     */
    protected Iterator<Double> dataMapHashSetIte;

    /**
     * dataMapHashSetValue
     */
    protected double dataMapHashSetValue;

    /**
     * Create a new instance.
     *
     * @param chunk Chunk
     */
    public Grids_ChunkIteratorDoubleMapASpatial(
            Grids_ChunkDoubleMap chunk) {
        super(chunk);
        data = chunk.getData();
        dataMapBitSet = data.dataMapBitSet;
        dataMapHashSet = data.dataMapHashSet;
        dataMapBitSetNumberOfValues = 0;
        dataMapBitSetIte = dataMapBitSet.keySet().iterator();
        if (dataMapBitSetIte.hasNext()) {
            hasNext = true;
            dataMapBitSetValue = dataMapBitSetIte.next();
            dataMapBitSetNumberOfValues += dataMapBitSet.get(dataMapBitSetValue).bitSet.cardinality();
        }
        numberOfNoDataValues -= dataMapBitSetNumberOfValues;
        dataMapBitSetIte = dataMapBitSet.keySet().iterator();
        dataMapHashSetNumberOfValues = 0;
        dataMapHashSetIte = dataMapHashSet.keySet().iterator();
        if (dataMapHashSetIte.hasNext()) {
            hasNext = true;
            dataMapHashSetValue = dataMapHashSetIte.next();
            dataMapHashSetNumberOfValues += dataMapHashSet.get(dataMapHashSetValue).size();
        }
        numberOfNoDataValues -= dataMapHashSetNumberOfValues;
        dataMapHashSetIte = dataMapHashSet.keySet().iterator();
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
    public Double next() {
        if (hasNext) {
            if (defaultValueIndex == numberOfDefaultValues - 1) {
                if (dataMapBitSetIndex == dataMapBitSetNumberOfValues - 1) {
                    if (dataMapBitSetIte.hasNext()) {
                        dataMapBitSetValue = dataMapBitSetIte.next();
                        dataMapBitSetNumberOfValues = dataMapBitSet.get(dataMapBitSetValue).bitSet.cardinality();
                        dataMapBitSetIndex = 0;
                        return dataMapBitSetValue;
                    } else {
                        if (dataMapHashSetIndex == dataMapHashSetNumberOfValues - 1) {
                            if (dataMapHashSetIte.hasNext()) {
                                dataMapHashSetValue = dataMapHashSetIte.next();
                                dataMapHashSetNumberOfValues = dataMapHashSet.get(dataMapHashSetValue).size();
                                dataMapHashSetIndex = 0;
                                return dataMapHashSetValue;
                            } else {
                                hasNext = false;
                                return null;
                            }
                        } else {
                            dataMapHashSetIndex++;
                            return dataMapHashSetValue;
                        }
                    }
                } else {
                    dataMapBitSetIndex++;
                    return dataMapBitSetValue;
                }
            } else {
                defaultValueIndex++;
                return defaultValue;
            }
        } else {
            return null;
        }
    }
}
