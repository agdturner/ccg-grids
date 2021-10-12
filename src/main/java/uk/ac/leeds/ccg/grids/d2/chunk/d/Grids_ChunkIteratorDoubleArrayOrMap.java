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

import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;

/**
 * For iterating through the values in a Grids_GridChunkDoubleArray instance.
 * The values are not returned in any particular order.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIteratorDoubleArrayOrMap
        extends Grids_ChunkRowMajorOrderIterator {

    private static final long serialVersionUID = 1L;

    /**
     * A reference to or copy of the data to iterate over.
     */
    protected double[][] data;

    /**
     * Create a new instance.
     * 
     * @param c What {@link #data} is set from.
     */
    public Grids_ChunkIteratorDoubleArrayOrMap(Grids_ChunkDoubleArrayOrMap c) {
        super(c);
        if (c instanceof Grids_ChunkDoubleArray) {
            data = ((Grids_ChunkDoubleArray) c).getData();
        } else {
            data = ((Grids_ChunkDoubleMap) c).to2DDoubleArray();
        }
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    public Double next() {
        next0();
        return data[row][col];
    }

    /**
     * Set the value to NoDataValue.
     */
    public void remove() {
        data[row][col] = ((Grids_GridDouble) grid).getNoDataValue();
    }
}
