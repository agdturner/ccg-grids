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

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_Grid2DSquareCellInt;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellIntChunkArray instance. 
 * The values are not returned in any particular order.
 */
public class Grids_Grid2DSquareCellIntChunkArrayIterator
        extends Grids_AbstractIterator {

    private int[][] data;
    private int chunkRowIndex;
    private int chunkColIndex;
    private int chunkNrows;
    private int chunkNcols;

    /** Creates a new instance of Grid2DSquareIntIterator */
    public Grids_Grid2DSquareCellIntChunkArrayIterator() {
    }

    /**
     * Creates a new instance of Grid2DSquareIntIterator
     * @param grid2DSquareCellIntChunkArray The Grid2DSquareCellIntChunkArray to
     *   iterate over.
     */
    public Grids_Grid2DSquareCellIntChunkArrayIterator(
            Grids_Grid2DSquareCellIntChunkArray grid2DSquareCellIntChunkArray) {
        this.data = grid2DSquareCellIntChunkArray.getData();
        this.chunkRowIndex = 0;
        this.chunkColIndex = 0;
        Grids_Grid2DSquareCellInt grid2DSquareCellInt =
                grid2DSquareCellIntChunkArray.getGrid2DSquareCellInt();
        this.chunkNrows = grid2DSquareCellInt.getChunkNRows(grid2DSquareCellIntChunkArray.ChunkID,
                ge.HandleOutOfMemoryErrorFalse);
        this.chunkNcols = grid2DSquareCellInt.getChunkNCols(grid2DSquareCellIntChunkArray.ChunkID,
                ge.HandleOutOfMemoryErrorFalse);
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    @Override
    public boolean hasNext() {
        if (chunkColIndex + 1 == this.chunkNcols) {
            if (chunkRowIndex + 1 == this.chunkNrows) {
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
        if (chunkColIndex + 1 == this.chunkNcols) {
            if (chunkRowIndex + 1 == this.chunkNrows) {
                //throw NoSuchElementException;
                //return null;
            } else {
                this.chunkRowIndex++;
                this.chunkColIndex = 0;
            }
        } else {
            this.chunkColIndex++;
        }
        return new Integer(
                this.data[this.chunkRowIndex][this.chunkColIndex]);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
