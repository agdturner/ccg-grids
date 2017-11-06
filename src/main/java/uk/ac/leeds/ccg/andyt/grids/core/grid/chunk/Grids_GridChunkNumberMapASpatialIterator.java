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

/**
 * For iterating through the values in a Grids_GridChunkNumberMap instance. The
 * values are returned in no particular spatial order.
 */
public abstract class Grids_GridChunkNumberMapASpatialIterator extends Grids_AbstractGridChunkNumberIterator {

    protected int NumberOfCells;
    protected int NumberOfDefaultValues;
    protected int DefaultValueIndex;
    protected int NumberOfNoDataValues;
    protected int NoDataValueIndex;
    protected int DataMapBitSetIndex;
    protected int DataMapBitSetNumberOfValues;
    protected int DataMapHashSetNumberOfValues;
    protected int DataMapHashSetIndex;
    protected boolean ValuesLeft;

    protected Grids_GridChunkNumberMapASpatialIterator() {
    }

    public Grids_GridChunkNumberMapASpatialIterator(
            Grids_GridChunkIntMap chunk) {
                NumberOfCells = chunk.ChunkNRows * chunk.ChunkNCols;
        NumberOfCells = chunk.ChunkNRows * chunk.ChunkNCols;
        NumberOfDefaultValues = chunk.getNumberOfDefaultValues(NumberOfCells);
        DefaultValueIndex = 0;
        ValuesLeft = true;
        NumberOfNoDataValues = NumberOfCells - NumberOfDefaultValues;
        DataMapBitSetIndex = 0;
        DataMapHashSetIndex = 0;
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
        return ValuesLeft;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
