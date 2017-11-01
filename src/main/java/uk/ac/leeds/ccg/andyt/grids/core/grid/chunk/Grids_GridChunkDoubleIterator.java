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
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellDoubleChunkArray
 * instance. The values are not returned in any particular order.
 */
public class Grids_GridChunkDoubleIterator extends Grids_AbstractIterator {

    protected double Value;
    protected int ChunkRow;
    protected int ChunkCol;
    protected int ChunkNRows;
    protected int ChunkNCols;

    protected Grids_GridChunkDoubleIterator() {
    }

    /**
     * Creates a new instance of Grid2DSquareDoubleIterator
     *
     * @param chunk The Grid2DSquareCellDoubleChunkArray to iterate over.
     */
    public Grids_GridChunkDoubleIterator(Grids_GridChunkDouble chunk) {
        init(chunk);
        Value = chunk.getValue();
    }

    /**
     *
     * @param chunk
     */
    protected final void init(Grids_AbstractGridChunkDouble chunk) {
        ChunkRow = 0;
        ChunkCol = 0;
        Grids_GridDouble g = chunk.getGrid();
        ChunkNRows = g.getChunkNRows(chunk.ChunkID, ge.HandleOutOfMemoryErrorFalse);
        ChunkNCols = g.getChunkNCols(chunk.ChunkID, ge.HandleOutOfMemoryErrorFalse);
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements. TODO: Try and
     * catch ArrayOutOfboundsException should be faster
     */
    @Override
    public boolean hasNext() {
        if (ChunkCol + 1 == ChunkNCols) {
            if (ChunkRow + 1 == ChunkNRows) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    @Override
    public Object next() {
        next0();
        return Value;
    }

    protected void next0() {
        if (ChunkCol + 1 == ChunkNCols) {
            if (ChunkRow + 1 == ChunkNRows) {
                //throw NoSuchElementException;
                //return null;
            } else {
                ChunkRow++;
                ChunkCol = 0;
            }
        } else {
            ChunkCol++;
        }
    }

    @Override
    public void remove() {
    }

}
