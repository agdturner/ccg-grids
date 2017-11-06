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

import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkNumberRowMajorOrderIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArrayOrMapIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntMap;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellInt instance. The
 * values are returned chunk by chunk, but the order within each chunk is
 * determined by the chunks types. If some
 */
public class Grids_GridIntIterator
        extends Grids_AbstractGridNumberIterator {

    protected Grids_GridIntIterator() {
    }

    /**
     *
     * @param g The Grids_GridInt to iterate over.
     */
    public Grids_GridIntIterator(
            Grids_GridInt g) {
        GridIterator = g.getChunkIDChunkMap().values().iterator();
        if (GridIterator.hasNext()) {
            Chunk = (Grids_AbstractGridChunkInt) GridIterator.next();
            initChunkIterator();
        }
    }

    /**
     * Initialises ChunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (Chunk instanceof Grids_GridChunkIntArray || Chunk instanceof Grids_GridChunkIntMap) {
            ChunkIterator = new Grids_GridChunkIntArrayOrMapIterator(
                    (Grids_GridChunkIntArray) Chunk);
        } else {
            ChunkIterator = new Grids_GridChunkIntIterator(
                    (Grids_GridChunkInt) Chunk);
        }
    }

    /**
     * @return Grids_AbstractIterator to iterate over values in chunk.
     */
    @Override
    public Grids_AbstractGridChunkNumberRowMajorOrderIterator getChunkIterator() {
        return ChunkIterator;
    }

    @Override
    public Grids_AbstractIterator getChunkIterator(Grids_AbstractGridChunk chunk) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
