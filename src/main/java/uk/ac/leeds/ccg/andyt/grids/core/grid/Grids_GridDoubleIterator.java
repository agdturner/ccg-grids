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

import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArrayOrMapIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkNumberRowMajorOrderIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleIterator;

/**
 * For iterating through the values in a Grids_GridDouble. The values are
 * returned chunk by chunk in row major order. The values within each chunk are
 * also returned in row major order.
 */
public class Grids_GridDoubleIterator
        extends Grids_AbstractGridNumberIterator {

    protected Grids_GridDoubleIterator() {
    }

    /**
     * @param g The Grids_GridDouble to iterate over.
     */
    public Grids_GridDoubleIterator(
            Grids_GridDouble g) {
        super(g);
        GridIterator = g.getChunkIDChunkMap().values().iterator();
        if (GridIterator.hasNext()) {
            Chunk = (Grids_AbstractGridChunkDouble) GridIterator.next();
            initChunkIterator();
        }
    }

    /**
     * Initialises ChunkIterator.
     */
    @Override
    protected final void initChunkIterator() {
        if (Chunk instanceof Grids_GridChunkDoubleArray || Chunk instanceof Grids_GridChunkDoubleMap) {
            ChunkIterator = new Grids_GridChunkDoubleArrayOrMapIterator(
                    (Grids_GridChunkDoubleArray) Chunk);
        } else {
            ChunkIterator = new Grids_GridChunkDoubleIterator(
                    (Grids_GridChunkDouble) Chunk);
        }
    }

    /**
     * @param chunk
     * @return Grids_AbstractIterator to iterate over values in chunk.
     */
    @Override
    public Grids_AbstractGridChunkNumberRowMajorOrderIterator getChunkIterator(
            Grids_AbstractGridChunkNumber chunk) {
        if (chunk instanceof Grids_GridChunkDoubleArray || chunk instanceof Grids_GridChunkDoubleMap) {
            return new Grids_GridChunkDoubleArrayOrMapIterator(
                    (Grids_GridChunkDoubleArray) chunk);
        } else {
            return new Grids_GridChunkDoubleIterator(
                    (Grids_GridChunkDouble) chunk);
        }
    }

    @Override
    public Grids_GridDouble getGrid() {
        return (Grids_GridDouble) Grid;
    }
}
