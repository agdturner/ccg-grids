/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArrayIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleMapIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDouble64CellMapIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDouble64CellMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellDouble instance. The
 * values are not returned in any particular order. The chunk order depends on
 * the order in which an iterator returns
 * Grid2DSquareCellDouble._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.values()
 *
 */
public class Grids_GridDoubleIterator
        extends Grids_AbstractGridIterator {

    /**
     * Creates a new instance of Grid2DSquareDoubleIterator
     */
    public Grids_GridDoubleIterator() {
    }

    /**
     * Creates a new instance of Grid2DSquareDoubleIterator
     *
     * @param g The Grid2DSquareCellDouble to iterate
     * over.
     */
    public Grids_GridDoubleIterator(
            Grids_GridDouble g) {
        GridIterator                = g.getChunkIDChunkMap().values().iterator();
        if (GridIterator.hasNext()) {
            Chunk                    = (Grids_AbstractGridChunkDouble) GridIterator.next();
            initChunkIterator();
        }
    }

    /**
     * Initialises ChunkIterator
     */
    @Override
    protected final void initChunkIterator() {
        if (Chunk instanceof Grids_GridChunkDouble64CellMap) {
            ChunkIterator                    = new Grids_GridChunkDouble64CellMapIterator(
                            (Grids_GridChunkDouble64CellMap) Chunk);
            return;
        }
        if (Chunk instanceof Grids_GridChunkDoubleArray) {
            ChunkIterator                    = new Grids_GridChunkDoubleArrayIterator(
                            (Grids_GridChunkDoubleArray) Chunk);
            return;
        }
        ChunkIterator                = new Grids_GridChunkDoubleMapIterator(
                        (Grids_GridChunkDoubleMap) Chunk);
    }

    /**
     * @param chunk
     * @return Grids_AbstractIterator to iterate over values in
     * a_Grid2DSquareCellDoubleChunk
     */
    @Override
    public Grids_AbstractIterator getChunkIterator(
            Grids_AbstractGridChunk chunk) {
        if (chunk instanceof Grids_GridChunkDouble64CellMap) {
            return new Grids_GridChunkDouble64CellMapIterator(
                    (Grids_GridChunkDouble64CellMap) chunk);
        }
        if (chunk instanceof Grids_GridChunkDoubleArray) {
            return new Grids_GridChunkDoubleArrayIterator(
                    (Grids_GridChunkDoubleArray) chunk);
        }
        return new Grids_GridChunkDoubleMapIterator(
                (Grids_GridChunkDoubleMap) chunk);
    }
}
