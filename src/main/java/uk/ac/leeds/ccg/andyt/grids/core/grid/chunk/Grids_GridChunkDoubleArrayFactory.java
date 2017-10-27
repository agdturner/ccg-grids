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
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;

/**
 * A factory for constructing Grid2DSquareCellDoubleChunkArray instances.
 */
public class Grids_GridChunkDoubleArrayFactory
        extends Grids_AbstractGridChunkDoubleFactory {

    /**
     * Creates a new Grid2DSquareCellDoubleChunkArrayFactory.
     */
    public Grids_GridChunkDoubleArrayFactory() {
    }

    @Override
    public Grids_AbstractGridChunkDouble createGridChunkDouble() {
        return new Grids_GridChunkDoubleArray();
    }

    @Override
    public Grids_AbstractGridChunkDouble createGridChunkDouble(
            Grids_GridDouble grid2DSquareCellDouble,
            Grids_2D_ID_int chunkID) {
        return new Grids_GridChunkDoubleArray(
                grid2DSquareCellDouble,
                chunkID);
    }

    @Override
    public Grids_AbstractGridChunkDouble createGridChunkDouble(
            Grids_AbstractGridChunkDouble grid2DSquareCellDoubleChunk,
            Grids_2D_ID_int chunkID) {
        return new Grids_GridChunkDoubleArray(
                grid2DSquareCellDoubleChunk,
                chunkID);
    }

}
