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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * Grids_AbstractGridChunkDouble extension for which all values are the same.
 */
public class Grids_GridChunkDouble
        extends Grids_AbstractGridChunkDouble
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    /**
     * For storing values arranged in rows and columns.
     */
    private double Value;

    protected Grids_GridChunkDouble() {
    }

    /**
     * By default value is set to 0d.
     *
     * @param ge
     */
    public Grids_GridChunkDouble(Grids_Environment ge) {
        super(ge);
        this.ChunkID = new Grids_2D_ID_int();
        this.Value = 0d;
        this.SwapUpToDate = false;
    }

    /**
     * By default value is set to 0d.
     *
     * @param ge
     * @param value
     */
    public Grids_GridChunkDouble(Grids_Environment ge, double value) {
        super(ge);
        ChunkID = new Grids_2D_ID_int();
        Value = value;
        SwapUpToDate = false;
    }

    /**
     * Creates a new Grids_GridChunkDouble with value set to g.NoDataValue.
     *
     * @param g The Grids_GridDouble Grids is set to.
     * @param chunkID The Grids_2D_ID_int ChunkID is set to.
     */
    protected Grids_GridChunkDouble(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID) {
        super(g.ge);
        ChunkID = chunkID;
        initGrid(g);
        Value = g.getNoDataValue(false);
        SwapUpToDate = false;
    }

    /**
     * Initialises the data associated with this.
     */
    @Override
    protected final void initData() {
        boolean handleOutOfMemoryError = false;
        Grids_GridDouble g = getGrid();
        Value = g.getNoDataValue(handleOutOfMemoryError);
    }

    /**
     * Returns Value.
     * @return 
     */
    protected double getValue() {
        return Value;
    }

    /**
     * Clears the data associated with this.
     */
    @Override
    protected void clearData() {
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param noDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */
    protected @Override
    double getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double noDataValue) {
        return Value;
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex and sets it to valueToSet
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param valueToSet the value the cell is to be set to
     * @param noDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */
    protected @Override
    double setCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double valueToSet,
            double noDataValue) {
        if (valueToSet == Value) {
            return Value;
        } else {
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
    protected @Override
    Grids_AbstractIterator iterator() {
        return new Grids_GridChunkDoubleIterator(this);
    }

    @Override
    protected void initCell(int chunkCellRowIndex, int chunkCellColIndex, double valueToInitialise) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
