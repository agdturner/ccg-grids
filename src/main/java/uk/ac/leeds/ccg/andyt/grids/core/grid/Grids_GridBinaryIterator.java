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

import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkBinary;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkBinaryIterator;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grids_GridBinary. The values are
 * returned chunk by chunk in row major order. The values within each chunk are
 * also returned in row major order.
 */
public class Grids_GridBinaryIterator extends Grids_AbstractGridIterator {

    protected Grids_GridBinaryIterator() {
    }

    /**
     * @param g The Grids_GridBinary to iterate over.
     */
    public Grids_GridBinaryIterator(
            Grids_GridBinary g) {
        super(g);
        GridIterator = g.chunkIDChunkMap.keySet().iterator();
        if (GridIterator.hasNext()) {
            ChunkID = (Grids_2D_ID_int) GridIterator.next();
            Chunk = (Grids_GridChunkBinary) g.chunkIDChunkMap.get(ChunkID);
            if (Chunk == null) {
                Grid.loadIntoCacheChunk(ChunkID);
                Chunk = (Grids_GridChunkBinary) g.chunkIDChunkMap.get(ChunkID);
            }
            initChunkIterator();
        }
    }

    /**
     * Initialises ChunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (Chunk instanceof Grids_GridChunkBinary) {
            ChunkIterator = new Grids_GridChunkBinaryIterator(
                    (Grids_GridChunkBinary) Chunk);
        }
    }

    /**
     * @param chunk
     * @return Grids_AbstractIterator to iterate over values in chunk.
     */
    @Override
    public Grids_AbstractIterator getChunkIterator(
            Grids_AbstractGridChunk chunk) {
        if (chunk instanceof Grids_GridChunkBinary) {
            return new Grids_GridChunkBinaryIterator(
                    (Grids_GridChunkBinary) chunk);
        } else {
            throw new Error("Unrecognised type of chunk "
                        + this.getClass().getName()
                        + ".getChunkIterator(Chunk(" + chunk.toString() + "))");
        }
    }

    @Override
    public Grids_GridBinary getGrid() {
        return (Grids_GridBinary) Grid;
    }
}
