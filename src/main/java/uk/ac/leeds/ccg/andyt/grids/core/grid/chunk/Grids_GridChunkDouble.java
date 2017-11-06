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

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * Grids_AbstractGridChunkDouble extension for which all values are the same.
 */
public class Grids_GridChunkDouble
        extends Grids_AbstractGridChunkDouble
        implements Serializable {

    /**
     * For storing the value of every cell in this grid.
     */
    public double Value;

    protected Grids_GridChunkDouble() {
    }

    /**
     * Creates a new Grids_GridChunkDouble with value set to value.
     *
     * @param g The Grids_GridDouble Grids is set to.
     * @param chunkID The Grids_2D_ID_int ChunkID is set to.
     * @param value To be the value of all cells in this chunk.
     */
    public Grids_GridChunkDouble(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID,
            double value) {
        super(g, chunkID);
        Value = value;
    }

    @Override
    protected final void initData() {
    }

    protected double getValue() {
        return Value;
    }

    @Override
    protected void clearData() {
    }

    @Override
    protected double getCell(
            int chunkRow,
            int chunkCol,
            double noDataValue) {
        return Value;
    }

    @Override
    protected double getCell(
            int chunkRow,
            int chunkCol,
            Grids_2D_ID_int cellID,
            double noDataValue) {
        return Value;
    }

    /**
     * Returns the value at position given by: chunk cell row chunkRow; chunk
     * cell row chunkCol and sets it to valueToSet
     *
     * @param chunkRow the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCol the column index of the cell w.r.t. the origin of this
     * chunk
     * @param valueToSet the value the cell is to be set to
     * @param noDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */
    @Override
    protected double setCell(
            int chunkRow,
            int chunkCol,
            double valueToSet,
            double noDataValue) {
        if (valueToSet == Value) {
            return Value;
        } else {
            // @TODO
            throw new Error("Unable to set value as this chunk is supposed "
                    + "to all contain the same value. What is needed is to "
                    + "transform the chunk to use a richer data structure to "
                    + "store valueToSet in ust this cell.");
        }
    }

    /**
     * Returns a Grids_GridChunkDoubleArrayIterator for iterating over the cells
     * in this.
     *
     * @return
     */
    @Override
    protected Grids_AbstractIterator iterator() {
        return new Grids_GridChunkDoubleIterator(this);
    }

    @Override
    protected void initCell(
            int chunkRow,
            int chunkCol,
            double noDataValue,
            double valueToInitialise) {
    }

}
