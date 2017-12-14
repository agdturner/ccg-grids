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

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;

/**
 * For iterating through the values in a Grids_GridChunkDoubleArray instance.
 * The values are not returned in any particular order.
 */
public class Grids_GridChunkDoubleArrayOrMapIterator
        extends Grids_AbstractGridChunkNumberRowMajorOrderIterator {

    protected double[][] Data;

    public Grids_GridChunkDoubleArrayOrMapIterator() {
    }

    public Grids_GridChunkDoubleArrayOrMapIterator(
            Grids_GridChunkDoubleArray chunk) {
        super(chunk);
        Data = chunk.getData();
    }

    public Grids_GridChunkDoubleArrayOrMapIterator(
            Grids_GridChunkDoubleMap chunk) {
        super(chunk);
        Data = chunk.to2DDoubleArray();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    @Override
    public Object next() {
        next0();
        return Data[Row][Col];
    }

    @Override
    public void remove() {
        Data[Row][Col] = ((Grids_GridDouble) Grid).getNoDataValue(ge.HOOME);
//        throw new UnsupportedOperationException();
    }
}
