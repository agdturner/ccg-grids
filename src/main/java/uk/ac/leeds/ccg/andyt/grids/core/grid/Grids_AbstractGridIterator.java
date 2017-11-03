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
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * For iterating through the values in a Grid. The values are returned chunk by
 * chunk. The order of values within each chunk is determined by each chunk's
 * type.
 */
public abstract class Grids_AbstractGridIterator
        extends Grids_AbstractIterator {

    protected Iterator<Grids_AbstractGridChunk> GridIterator;
    protected Grids_AbstractGridChunk Chunk;
    protected Grids_AbstractIterator ChunkIterator;

    protected Grids_AbstractGridIterator() {
    }

    protected abstract void initChunkIterator();

    public abstract Grids_AbstractIterator getChunkIterator(Grids_AbstractGridChunk chunk);

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
            while (GridIterator.hasNext()) {
                Grids_AbstractGridChunkInt chunk
                        = (Grids_AbstractGridChunkInt) GridIterator.next();
                Grids_AbstractIterator chunkIterator
                        = getChunkIterator(chunk);
                if (chunkIterator.hasNext()) {
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
            if (ChunkIterator.hasNext()) {
                return ChunkIterator.next();
            } else {
                while (GridIterator.hasNext()) {
                    Chunk = GridIterator.next();
                    ChunkIterator = getChunkIterator(Chunk);
                    if (ChunkIterator.hasNext()) {
                        return ChunkIterator.next();
                    }
                }
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
