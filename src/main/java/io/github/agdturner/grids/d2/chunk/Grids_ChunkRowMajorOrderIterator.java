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
package io.github.agdturner.grids.d2.chunk;

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

    protected int Row;
    protected int Col;
    protected int NRows;
    protected int NCols;

    protected Grids_ChunkRowMajorOrderIterator(Grids_Chunk chunk) {
        super(chunk);
        Row = 0;
        Col = 0;
        NRows = Grid.getChunkNRows(chunk.ChunkID);
        NCols = Grid.getChunkNCols(chunk.ChunkID);
    }

    /**
     * @return {@code true} if the iterator has more elements.
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
