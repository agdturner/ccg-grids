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
package uk.ac.leeds.ccg.grids.d2.chunk.br;

import ch.obermuhlner.math.big.BigRational;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.grids.d2.grid.br.Grids_GridBR;

/**
 * For iterating through the values in a Grids_GridChunkBRArray instance. The
 * values are not returned in any particular order.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkBRIteratorArrayOrMap
        extends Grids_ChunkRowMajorOrderIterator {

    private static final long serialVersionUID = 1L;

    /**
     * The data.
     */
    protected BigRational[][] data;

    /**
     * Create a new instance.
     *
     * @param c The chunk to iterate over.
     */
    public Grids_ChunkBRIteratorArrayOrMap(Grids_ChunkBRArrayOrMap c) {
        super(c);
        if (c instanceof Grids_ChunkBRArray) {
            data = ((Grids_ChunkBRArray) c).getData();
        } else {
            data = ((Grids_ChunkBRMap) c).to2DBRArray();
        }
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    public BigRational next() {
        next0();
        return data[row][col];
    }

    /**
     * Sets a value to NoData.
     */
    public void remove() {
        data[row][col] = ((Grids_GridBR) grid).getNoDataValue();
    }
}
