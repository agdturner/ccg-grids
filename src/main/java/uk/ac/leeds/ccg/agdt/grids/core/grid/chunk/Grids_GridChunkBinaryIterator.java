/*
 * Copyright 2019 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.agdt.grids.core.grid.chunk;

import java.util.NoSuchElementException;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridBinary;
import uk.ac.leeds.ccg.agdt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grids_GridChunkBinary instance. The
 * values are not returned in any particular order.
*
 * @author Andy Turner
 * @version 1.0.0
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
        super(chunk.env);
        Data = chunk.getData();
        Row = 0;
        Col = 0;
        Grids_GridBinary g = chunk.getGrid();
        NRows = g.getChunkNRows(chunk.ChunkID);
        NCols = g.getChunkNCols(chunk.ChunkID);
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
