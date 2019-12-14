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
package uk.ac.leeds.ccg.agdt.grids.core.chunk.i;

import uk.ac.leeds.ccg.agdt.grids.core.chunk.Grids_ChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.agdt.grids.core.grid.i.Grids_GridInt;

/**
 * For iterating through the values in a Grid2DSquareCellIntChunkArray instance.
 * The values are not returned in any particular order.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIteratorIntArrayOrMap
        extends Grids_ChunkRowMajorOrderIterator {

    private static final long serialVersionUID = 1L;

    protected int[][] Data;

    public Grids_ChunkIteratorIntArrayOrMap(
            Grids_ChunkIntArray chunk) {
        super(chunk);
        Data = chunk.getData();
    }
    
    public Grids_ChunkIteratorIntArrayOrMap(
            Grids_ChunkIntMap chunk) {
        super(chunk);
        Data = chunk.to2DIntArray();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    public Integer next() {
        next0();
        return Data[Row][Col];
    }

    public void remove() {
        Data[Row][Col] = ((Grids_GridInt) Grid).getNoDataValue();
    }
}
