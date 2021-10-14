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
package uk.ac.leeds.ccg.grids.d2.chunk.b;

import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.grid.b.Grids_GridBinary;

/**
 * A factory for constructing {@link Grids_ChunkBinaryArray} instances.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkBinaryFactoryArray extends Grids_ChunkBinaryFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     */
    public Grids_ChunkBinaryFactoryArray() {
    }

    @Override
    public Grids_ChunkBinaryArray create(Grids_GridBinary g, Grids_2D_ID_int i) {
        return new Grids_ChunkBinaryArray(g, i);
    }

    @Override
    public Grids_ChunkBinaryArray create(Grids_ChunkBinary chunk, Grids_2D_ID_int i) {
        return new Grids_ChunkBinaryArray(chunk, i);
    }

}
