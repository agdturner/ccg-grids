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
package uk.ac.leeds.ccg.grids.d2.chunk;

import java.util.NoSuchElementException;

/**
 * For iterating over the values in a chunk in row major order.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_ChunkRowMajorOrderIterator
        extends Grids_ChunkIterator {

    private static final long serialVersionUID = 1L;

    /**
     * row
     */
    protected int row;

    /**
     * col
     */
    protected int col;

    /**
     * nRows
     */
    protected int nRows;

    /**
     * nCols
     */
    protected int nCols;


    /**
     * Create a new instance.
     * 
     * @param chunk used to create this.
     */
    protected Grids_ChunkRowMajorOrderIterator(Grids_Chunk chunk) {
        super(chunk);
        row = 0;
        col = 0;
        nRows = grid.getChunkNRows(chunk.id);
        nCols = grid.getChunkNCols(chunk.id);
    }

    /**
     * @return {@code true} if the iterator has more elements.
     */
    @Override
    public boolean hasNext() {
        if (col + 1 == nCols) {
            if (row + 1 == nRows) {
                return false;
            }
        }
        return true;
    }

    protected void next0() {
        if (col + 1 == nCols) {
            if (row + 1 == nRows) {
                throw new NoSuchElementException();
            } else {
                row++;
                col = 0;
            }
        } else {
            col++;
        }
    }

}
