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
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import java.util.Iterator;
import java.util.NoSuchElementException;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkNumberRowMajorOrderIterator;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grid. The values are returned chunk by
 * chunk. The order of values within each chunk is determined by each chunk's
 * type.
 */
public abstract class Grids_AbstractGridNumberIterator
        extends Grids_AbstractIterator {

    protected Grids_AbstractGridNumber Grid;
    protected Grids_AbstractGridChunkNumber Chunk;
    protected Iterator<Grids_AbstractGridChunkNumber> GridIterator;
    protected Grids_AbstractGridChunkNumberRowMajorOrderIterator ChunkIterator;

    protected Grids_AbstractGridNumberIterator() {
    }

    public Grids_AbstractGridNumberIterator(Grids_AbstractGridNumber grid) {
        super(grid.ge);
        Grid = grid;
    }

    protected abstract void initChunkIterator();

    public Grids_AbstractGridChunkNumberRowMajorOrderIterator getChunkIterator() {
        return ChunkIterator;
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
        if (ChunkIterator.hasNext()) {
            return true;
        } else {
            if (GridIterator.hasNext()) {
                return true;
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
        if (ChunkIterator.hasNext()) {
            return ChunkIterator.next();
        } else {
            while (GridIterator.hasNext()) {
                ge.removeFromNotToSwapData(Grid, Chunk.getChunkID(ge.HandleOutOfMemoryError));
                Chunk = (Grids_AbstractGridChunkNumber) GridIterator.next();
                ge.addToNotToSwapData(Grid, Chunk.getChunkID(ge.HandleOutOfMemoryError));
                ChunkIterator = getChunkIterator();
                if (ChunkIterator.hasNext()) {
                    return ChunkIterator.next();
                }
            }
        }
        throw new NoSuchElementException();
    }

    /**
     *
     * @return Chunk.ChunkID
     */
    public Grids_2D_ID_int getChunkID() {
        return Chunk.getChunkID(ge.HandleOutOfMemoryError);
    }

    /**
     *
     * @return Chunk.ChunkID
     */
    public Grids_2D_ID_int getCellID() {
        return Chunk.getChunkID(ge.HandleOutOfMemoryError);
    }

    /**
     * throw new UnsupportedOperationException();
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
