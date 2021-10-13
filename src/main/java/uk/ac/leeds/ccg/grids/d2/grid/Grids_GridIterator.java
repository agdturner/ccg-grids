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
package uk.ac.leeds.ccg.grids.d2.grid;

import java.util.Iterator;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.core.Grids_Object;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkIterator;

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

    /**
     * grid
     */
    protected Grids_Grid grid;

    /**
     * chunk
     */
    protected Grids_Chunk chunk;

    /**
     * chunkID
     */
    protected Grids_2D_ID_int chunkID;

    /**
     * gridIterator
     */
    protected Iterator<Grids_2D_ID_int> gridIterator;

    /**
     * chunkIterator
     */
    protected Grids_ChunkIterator chunkIterator;

    /**
     * Create a new instance.
     * @param grid The grid.
     */
    public Grids_GridIterator(Grids_Grid grid) {
        super(grid.env);
        this.grid = grid;
    }

    /**
     * For initialising.
     */
    protected abstract void initChunkIterator();

    /**
     * @return The grid.
     */
    public abstract Grids_Grid getGrid();

    /**
     * @return A grid iterator. 
     */
    public Iterator<Grids_2D_ID_int> getGridIterator() {
        return gridIterator;
    }

    /**
     * @return A chunk iterator. 
     */
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
        if (chunkIterator.hasNext()) {
            return true;
        } else {
            if (gridIterator.hasNext()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return {@link #chunkID}
     */
    public Grids_2D_ID_int getChunkID() {
        return chunkID;
    }

}
