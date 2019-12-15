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
package io.github.agdturner.grids.d2.chunk.b;

import java.util.NoSuchElementException;
import io.github.agdturner.grids.d2.chunk.Grids_ChunkIterator;
import io.github.agdturner.grids.d2.grid.b.Grids_GridBoolean;

/**
 * For iterating through the values in a Grids_GridChunkBinary instance. The
 * values are not returned in any particular order.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIteratorBoolean extends Grids_ChunkIterator {

    private static final long serialVersionUID = 1L;

    protected Boolean[][] Data;
    protected int Row;
    protected int Col;
    protected int NRows;
    protected int NCols;

    public Grids_ChunkIteratorBoolean(Grids_ChunkBoolean chunk) {
        super(chunk);
        Data = chunk.getData();
        Row = 0;
        Col = 0;
        Grids_GridBoolean g = chunk.getGrid();
        NRows = g.getChunkNRows(chunk.getChunkID());
        NCols = g.getChunkNCols(chunk.getChunkID());
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements. TODO: Try and
     * catch ArrayOutOfboundsException should be faster
     */
    @Override
    public boolean hasNext() {
        if (Col + 1 == NCols) {
            if (Row + 1 == NRows) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    public Boolean next() {
        if (Col + 1 == NCols) {
            if (Row + 1 == NRows) {
                throw new NoSuchElementException();
            } else {
                Row++;
                Col = 0;
            }
        } else {
            Col++;
        }
        return Data[Row][Col];
    }
}
