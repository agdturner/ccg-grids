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
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkRowMajorOrderIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArrayOrMapIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntMap;

/**
 * For iterating through the values in a Grids_GridInt. The values are returned
 * chunk by chunk in row major order. The values within each chunk are also
 * returned in row major order.
 */
public class Grids_GridIntIterator
        extends Grids_AbstractGridIterator {

    protected Grids_GridIntIterator() {
    }

    /**
     *
     * @param g The Grids_GridInt to iterate over.
     */
    public Grids_GridIntIterator(
            Grids_GridInt g) {
        super(g);
        GridIterator = g.ChunkIDChunkMap.keySet().iterator();
        if (GridIterator.hasNext()) {
            ChunkID = (Grids_2D_ID_int) GridIterator.next();
            Chunk = (Grids_AbstractGridChunkInt) g.ChunkIDChunkMap.get(ChunkID);
            if (Chunk == null) {
                Grid.loadIntoCacheChunk(ChunkID);
                Chunk = (Grids_AbstractGridChunkInt) g.ChunkIDChunkMap.get(ChunkID);
            }
            initChunkIterator();
        }
    }

    /**
     * Initialises ChunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (Chunk instanceof Grids_GridChunkIntArray) {
            ChunkIterator = new Grids_GridChunkIntArrayOrMapIterator(
                    (Grids_GridChunkIntArray) Chunk);
        } else if (Chunk instanceof Grids_GridChunkIntMap) {
            ChunkIterator = new Grids_GridChunkIntArrayOrMapIterator(
                    (Grids_GridChunkIntMap) Chunk);
        } else {
            ChunkIterator = new Grids_GridChunkIntIterator(
                    (Grids_GridChunkInt) Chunk);
        }
    }

    /**
     * @param chunk
     * @return Grids_AbstractIterator to iterate over values in chunk.
     */
    @Override
    public Grids_AbstractGridChunkRowMajorOrderIterator getChunkIterator(
            Grids_AbstractGridChunk chunk) {
        if (chunk instanceof Grids_GridChunkIntArray) {
            return new Grids_GridChunkIntArrayOrMapIterator(
                    (Grids_GridChunkIntArray) chunk);
        } else if (chunk instanceof Grids_GridChunkIntMap) {
            return new Grids_GridChunkIntArrayOrMapIterator(
                    (Grids_GridChunkIntMap) chunk);
        } else if (chunk instanceof Grids_GridChunkInt) {
            return new Grids_GridChunkIntIterator(
                    (Grids_GridChunkInt) chunk);
        } else {
            throw new Error("Unrecognised type of chunk "
                        + this.getClass().getName()
                        + ".getChunkIterator(Chunk(" + chunk.toString() + "))");
        }
    }

    @Override
    public Grids_GridInt getGrid() {
        return (Grids_GridInt) Grid;
    }

}
