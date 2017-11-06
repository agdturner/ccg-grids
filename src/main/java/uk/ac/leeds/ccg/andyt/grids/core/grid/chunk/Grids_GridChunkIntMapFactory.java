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
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;

/**
 * A factory for constructing Grids_GridChunkIntMap instances.
 */
public class Grids_GridChunkIntMapFactory
        extends Grids_AbstractGridChunkIntFactory {

    public Grids_GridChunkIntMapFactory() {
    }

    @Override
    public Grids_GridChunkIntMap createGridChunkInt(
            Grids_GridInt g,
            Grids_2D_ID_int chunkID) {
        return new Grids_GridChunkIntMap(
                g,
                chunkID);
    }

    @Override
    public Grids_GridChunkIntMap createGridChunkInt(
            Grids_AbstractGridChunkInt chunk,
            Grids_2D_ID_int chunkID) {
        return new Grids_GridChunkIntMap(
                chunk,
                chunkID,
                chunk.getGrid().getNoDataValue(false));
    }

    public Grids_GridChunkIntMap createGridChunkInt(
            Grids_AbstractGridChunkInt chunk,
            Grids_2D_ID_int chunkID,
            int defaultValue) {
        return new Grids_GridChunkIntMap(
                chunk,
                chunkID,
                defaultValue);
    }

}
