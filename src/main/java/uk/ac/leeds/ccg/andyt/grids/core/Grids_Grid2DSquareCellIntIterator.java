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
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grid2DSquareCellInt instance. The 
 * values are returned chunk by chunk, but the order within each chunk is
 * determined by the chunks types. If some
 */
public class Grids_Grid2DSquareCellIntIterator
        extends Grids_AbstractIterator {

    private Iterator<Grids_AbstractGridChunk> _Grid2DSquareCellIntHashMapIterator;
    private Grids_AbstractGrid2DSquareCellIntChunk _Grid2DSquareCellIntChunk;
    private Grids_AbstractIterator _Grid2DSquareCellIntChunkIterator;

    /** Creates a new instance of Grid2DSquareIntIterator */
    public Grids_Grid2DSquareCellIntIterator() {
    }

    /**
     * Creates a new instance of Grid2DSquareIntIterator
     * @param a_Grid2DSquareCellInt The Grid2DSquareCellInt to iterate over.
     */
    public Grids_Grid2DSquareCellIntIterator(
            Grids_Grid2DSquareCellInt a_Grid2DSquareCellInt) {
        this._Grid2DSquareCellIntHashMapIterator =
                a_Grid2DSquareCellInt._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.values().iterator();
        if (_Grid2DSquareCellIntHashMapIterator.hasNext()) {
            this._Grid2DSquareCellIntChunk =
                    (Grids_AbstractGrid2DSquareCellIntChunk) this._Grid2DSquareCellIntHashMapIterator.next();
            init_Grid2DSquareCellIntChunkIterator();
        }
    }

    /**
     * Initialises _Grid2DSquareCellIntChunkIterator
     */
    private void init_Grid2DSquareCellIntChunkIterator() {
        if (this._Grid2DSquareCellIntChunk instanceof Grids_Grid2DSquareCellIntChunk64CellMap) {
            this._Grid2DSquareCellIntChunkIterator =
                    new Grids_Grid2DSquareCellIntChunk64CellMapIterator(
                    (Grids_Grid2DSquareCellIntChunk64CellMap) this._Grid2DSquareCellIntChunk);
            return;
        }
        if (this._Grid2DSquareCellIntChunk instanceof Grids_Grid2DSquareCellIntChunkArray) {
            this._Grid2DSquareCellIntChunkIterator =
                    new Grids_Grid2DSquareCellIntChunkArrayIterator(
                    (Grids_Grid2DSquareCellIntChunkArray) this._Grid2DSquareCellIntChunk);
            return;
        }
        if (this._Grid2DSquareCellIntChunk instanceof Grids_Grid2DSquareCellIntChunkJAI) {
            this._Grid2DSquareCellIntChunkIterator =
                    new Grids_Grid2DSquareCellIntChunkJAIIterator(
                    (Grids_Grid2DSquareCellIntChunkJAI) this._Grid2DSquareCellIntChunk);
            return;
        }
        this._Grid2DSquareCellIntChunkIterator =
                new Grids_Grid2DSquareCellIntChunkMapIterator(
                (Grids_Grid2DSquareCellIntChunkMap) this._Grid2DSquareCellIntChunk);
    }

    /**
     * @param a_Grid2DSquareCellIntChunk
     * @return Grids_AbstractIterator to iterate over values in a_Grid2DSquareCellDoubleChunk
     */
    public static Grids_AbstractIterator getGrid2DSquareCellIntChunkIterator(
            Grids_AbstractGrid2DSquareCellIntChunk a_Grid2DSquareCellIntChunk) {
        if (a_Grid2DSquareCellIntChunk instanceof Grids_Grid2DSquareCellIntChunk64CellMap) {
            return new Grids_Grid2DSquareCellIntChunk64CellMapIterator(
                    (Grids_Grid2DSquareCellIntChunk64CellMap) a_Grid2DSquareCellIntChunk);
        }
        if (a_Grid2DSquareCellIntChunk instanceof Grids_Grid2DSquareCellIntChunkArray) {
            return new Grids_Grid2DSquareCellIntChunkArrayIterator(
                    (Grids_Grid2DSquareCellIntChunkArray) a_Grid2DSquareCellIntChunk);
        }
        if (a_Grid2DSquareCellIntChunk instanceof Grids_Grid2DSquareCellIntChunkJAI) {
            return new Grids_Grid2DSquareCellIntChunkJAIIterator(
                    (Grids_Grid2DSquareCellIntChunkJAI) a_Grid2DSquareCellIntChunk);
        }
        return new Grids_Grid2DSquareCellIntChunkMapIterator(
                (Grids_Grid2DSquareCellIntChunkMap) a_Grid2DSquareCellIntChunk);
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
                Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk =
                        (Grids_AbstractGrid2DSquareCellIntChunk) this._Grid2DSquareCellIntHashMapIterator.next();
                Grids_AbstractIterator grid2DSquareCellIntChunkIterator =
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
                            (Grids_AbstractGrid2DSquareCellIntChunk) this._Grid2DSquareCellIntHashMapIterator.next();
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
