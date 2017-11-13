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

import java.util.NoSuchElementException;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridBinary;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grids_GridChunkBinary instance. The
 * values are not returned in any particular order.
 */
public class Grids_GridChunkBinaryIterator extends Grids_AbstractIterator {

    protected boolean[][] Data;
    protected int Row;
    protected int Col;
    protected int NRows;
    protected int NCols;

    protected Grids_GridChunkBinaryIterator() {
    }

    public Grids_GridChunkBinaryIterator(Grids_GridChunkBinary chunk) {
        super(chunk.ge);
        Data = chunk.getData();
        Row = 0;
        Col = 0;
        Grids_GridBinary g = chunk.getGrid();
        NRows = g.getChunkNRows(chunk.ChunkID, ge.HandleOutOfMemoryError);
        NCols = g.getChunkNCols(chunk.ChunkID, ge.HandleOutOfMemoryError);
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

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     */
    @Override
    public Object next() {
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
        return Data[Row][Col];
    }

    /**
     *
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation). This method can be called only once per
     * call to <tt>next</tt>. The behavior of an iterator is unspecified if the
     * underlying collection is modified while the iteration is in progress in
     * any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     * operation is not supported by this Iterator.
     *
     * @exception IllegalStateException if the <tt>next</tt> method has not yet
     * been called, or the <tt>remove</tt> method has already been called after
     * the last call to the <tt>next</tt>
     * method.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
