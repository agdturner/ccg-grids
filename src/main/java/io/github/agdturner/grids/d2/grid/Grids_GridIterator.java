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
package io.github.agdturner.grids.d2.grid;

import java.util.Iterator;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.core.Grids_Object;
import io.github.agdturner.grids.d2.chunk.Grids_Chunk;
import io.github.agdturner.grids.d2.chunk.Grids_ChunkIterator;

/**
 * For iterating through the values in a Grid. The values are returned chunk by
 * chunk in row major order. The values within each chunk are also returned in
 * row major order.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_GridIterator extends Grids_Object {

    private static final long serialVersionUID = 1L;

    protected Grids_Grid Grid;
    protected Grids_Chunk Chunk;
    protected Grids_2D_ID_int ChunkID;
    protected Iterator<Grids_2D_ID_int> GridIterator;
    protected Grids_ChunkIterator ChunkIterator;

    public Grids_GridIterator(Grids_Grid grid) {
        super(grid.env);
        Grid = grid;
    }

    protected abstract void initChunkIterator();

    public abstract Grids_Grid getGrid();

    public Iterator<Grids_2D_ID_int> getGridIterator() {
        return GridIterator;
    }

    public abstract Grids_ChunkIterator getChunkIterator();

    /**
     * @param c The chunk for which the iterator is returned.
     * @return An appropriate iterator for iterating over the values in
     * {@code c}.
     * @throws java.lang.Exception If the type of chunk is not recognised.
     */
    public abstract Grids_ChunkIterator getChunkIterator(Grids_Chunk c)
            throws Exception;

    /**
     * @return {@code true} if the iterator has more elements.
     */
    public boolean hasNext() {
        if (ChunkIterator.hasNext()) {
            return true;
        } else {
            if (GridIterator.hasNext()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return {@link ChunkID}
     */
    public Grids_2D_ID_int getChunkID() {
        return ChunkID;
    }

}
