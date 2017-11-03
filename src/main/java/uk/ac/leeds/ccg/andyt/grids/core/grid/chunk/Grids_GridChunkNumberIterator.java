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

import java.util.NoSuchElementException;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_GridChunkNumberIterator extends Grids_AbstractIterator {
    
    protected int ChunkRow;
    protected int ChunkCol;
    protected int ChunkNRows;
    protected int ChunkNCols;

    protected Grids_GridChunkNumberIterator() {
    }

    /**
     *
     * @param chunk
     */
    protected final void init(Grids_AbstractGridChunkNumber chunk) {
        ChunkRow = 0;
        ChunkCol = 0;
        Grids_AbstractGridNumber g = chunk.getGrid();
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

    protected void next0() {
        if (ChunkCol + 1 == ChunkNCols) {
            if (ChunkRow + 1 == ChunkNRows) {
                throw new NoSuchElementException();
            } else {
                ChunkRow++;
                ChunkCol = 0;
            }
        } else {
            ChunkCol++;
        }
    }
    
}
