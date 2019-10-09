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

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridChunkRowMajorOrderIterator 
        extends Grids_AbstractGridChunkIterator {

    protected int Row;
    protected int Col;
    protected int NRows;
    protected int NCols;

    protected Grids_AbstractGridChunkRowMajorOrderIterator() {
    }

    protected Grids_AbstractGridChunkRowMajorOrderIterator(
            Grids_AbstractGridChunk chunk) {
        super(chunk);
        Row = 0;
        Col = 0;
        NRows = Grid.getChunkNRows(chunk.ChunkID);
        NCols = Grid.getChunkNCols(chunk.ChunkID);
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
        if (Col + 1 == NCols) {
            if (Row + 1 == NRows) {
                return false;
            }
        }
        return true;
    }

    protected void next0() {
        if (Col + 1 == NCols) {
            if (Row + 1 == NRows) {
                throw new NoSuchElementException();
            } else {
                Row++;
                Col = 0;
            }
        } else {
            Col++;
        }
    }

}
