/*
 * Copyright (C) 2017 geoagdt.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridChunkDoubleArrayOrMap 
        extends Grids_AbstractGridChunkDouble {
    
    protected Grids_AbstractGridChunkDoubleArrayOrMap() {
    }

    protected Grids_AbstractGridChunkDoubleArrayOrMap(Grids_GridDouble g, 
            Grids_2D_ID_int chunkID) {
        super(g, chunkID);
    }

    /**
     * Returns a Grids_GridChunkDoubleArrayOrMapIterator for iterating over the
     * cells in this.
     *
     * @return
     */
    @Override
    public abstract Grids_GridChunkDoubleArrayOrMapIterator iterator();
    
}
