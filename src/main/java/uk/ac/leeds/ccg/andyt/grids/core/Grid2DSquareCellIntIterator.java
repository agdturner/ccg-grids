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
package uk.ac.leeds.ccg.andyt.grids.core;

import java.util.Iterator;
import java.util.NoSuchElementException;
import uk.ac.leeds.ccg.andyt.grids.utilities.AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellInt instance. The 
 * values are returned chunk by chunk, but the order within each chunk is
 * determined by the chunks types. If some
 */
public class Grid2DSquareCellIntIterator
        extends AbstractIterator {

    private Iterator<AbstractGrid2DSquareCellChunk> _Grid2DSquareCellIntHashMapIterator;
    private AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk;
    private AbstractIterator _Grid2DSquareCellIntChunkIterator;

    /** Creates a new instance of Grid2DSquareIntIterator */
    public Grid2DSquareCellIntIterator() {
    }

    /**
     * Creates a new instance of Grid2DSquareIntIterator
     * @param a_Grid2DSquareCellInt The Grid2DSquareCellInt to iterate over.
     */
    public Grid2DSquareCellIntIterator(
            Grid2DSquareCellInt a_Grid2DSquareCellInt) {
        this._Grid2DSquareCellIntHashMapIterator =
                a_Grid2DSquareCellInt._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.values().iterator();
        if (_Grid2DSquareCellIntHashMapIterator.hasNext()) {
            this._Grid2DSquareCellIntChunk =
                    (AbstractGrid2DSquareCellIntChunk) this._Grid2DSquareCellIntHashMapIterator.next();
            init_Grid2DSquareCellIntChunkIterator();
        }
    }

    /**
     * Initialises _Grid2DSquareCellIntChunkIterator
     */
    private void init_Grid2DSquareCellIntChunkIterator() {
        if (this._Grid2DSquareCellIntChunk instanceof Grid2DSquareCellIntChunk64CellMap) {
            this._Grid2DSquareCellIntChunkIterator =
                    new Grid2DSquareCellIntChunk64CellMapIterator(
                    (Grid2DSquareCellIntChunk64CellMap) this._Grid2DSquareCellIntChunk);
            return;
        }
        if (this._Grid2DSquareCellIntChunk instanceof Grid2DSquareCellIntChunkArray) {
            this._Grid2DSquareCellIntChunkIterator =
                    new Grid2DSquareCellIntChunkArrayIterator(
                    (Grid2DSquareCellIntChunkArray) this._Grid2DSquareCellIntChunk);
            return;
        }
        if (this._Grid2DSquareCellIntChunk instanceof Grid2DSquareCellIntChunkJAI) {
            this._Grid2DSquareCellIntChunkIterator =
                    new Grid2DSquareCellIntChunkJAIIterator(
                    (Grid2DSquareCellIntChunkJAI) this._Grid2DSquareCellIntChunk);
            return;
        }
        this._Grid2DSquareCellIntChunkIterator =
                new Grid2DSquareCellIntChunkMapIterator(
                (Grid2DSquareCellIntChunkMap) this._Grid2DSquareCellIntChunk);
    }

    /**
     * @param a_Grid2DSquareCellIntChunk
     * @return AbstractIterator to iterate over values in a_Grid2DSquareCellDoubleChunk
     */
    public static AbstractIterator getGrid2DSquareCellIntChunkIterator(
            AbstractGrid2DSquareCellIntChunk a_Grid2DSquareCellIntChunk) {
        if (a_Grid2DSquareCellIntChunk instanceof Grid2DSquareCellIntChunk64CellMap) {
            return new Grid2DSquareCellIntChunk64CellMapIterator(
                    (Grid2DSquareCellIntChunk64CellMap) a_Grid2DSquareCellIntChunk);
        }
        if (a_Grid2DSquareCellIntChunk instanceof Grid2DSquareCellIntChunkArray) {
            return new Grid2DSquareCellIntChunkArrayIterator(
                    (Grid2DSquareCellIntChunkArray) a_Grid2DSquareCellIntChunk);
        }
        if (a_Grid2DSquareCellIntChunk instanceof Grid2DSquareCellIntChunkJAI) {
            return new Grid2DSquareCellIntChunkJAIIterator(
                    (Grid2DSquareCellIntChunkJAI) a_Grid2DSquareCellIntChunk);
        }
        return new Grid2DSquareCellIntChunkMapIterator(
                (Grid2DSquareCellIntChunkMap) a_Grid2DSquareCellIntChunk);
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
        if (this._Grid2DSquareCellIntChunkIterator.hasNext()) {
            return true;
        } else {
            while (_Grid2DSquareCellIntHashMapIterator.hasNext()) {
                AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk =
                        (AbstractGrid2DSquareCellIntChunk) this._Grid2DSquareCellIntHashMapIterator.next();
                AbstractIterator grid2DSquareCellIntChunkIterator =
                        getGrid2DSquareCellIntChunkIterator(grid2DSquareCellIntChunk);
                if (grid2DSquareCellIntChunkIterator.hasNext()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    @Override
    public Object next() {
        try {
            if (this._Grid2DSquareCellIntChunkIterator.hasNext()) {
                return this._Grid2DSquareCellIntChunkIterator.next();
            } else {
                while (_Grid2DSquareCellIntHashMapIterator.hasNext()) {
                    this._Grid2DSquareCellIntChunk =
                            (AbstractGrid2DSquareCellIntChunk) this._Grid2DSquareCellIntHashMapIterator.next();
                    this._Grid2DSquareCellIntChunkIterator =
                            getGrid2DSquareCellIntChunkIterator(_Grid2DSquareCellIntChunk);
                    if (this._Grid2DSquareCellIntChunkIterator.hasNext()) {
                        return this._Grid2DSquareCellIntChunkIterator.next();
                    }
                }
            }
        } catch (NoSuchElementException nsee0) {
            nsee0.printStackTrace();
            System.out.println(nsee0.toString());
        }
        return null;
    }

    /**
     * throw new UnsupportedOperationException();
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
