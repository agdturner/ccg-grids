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

import java.util.NoSuchElementException;

/**
 *
*
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_AbstractGridChunkRowMajorOrderIterator 
        extends Grids_AbstractChunkIterator {

    protected int Row;
    protected int Col;
    protected int NRows;
    protected int NCols;

    protected Grids_AbstractGridChunkRowMajorOrderIterator(
            Grids_AbstractGridChunk chunk) {
        super(chunk);
        Row = 0;
        Col = 0;
        NRows = Grid.getChunkNRows(chunk.ChunkID);
        NCols = Grid.getChunkNCols(chunk.ChunkID);
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements. TODO: Try and
     * catch ArrayOutOfboundsException should be faster
     */
    public boolean hasNext() {
        if (Col + 1 == NCols) {
            if (Row + 1 == NRows) {
                return false;
            }
        }
        return true;
    }

    protected void next0() {
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
    }

}
