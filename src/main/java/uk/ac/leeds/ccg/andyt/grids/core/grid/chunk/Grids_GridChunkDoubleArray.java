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
import java.util.Arrays;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;

/**
 * Grids_AbstractGridChunkDouble extension that stores cell values in a
 * double[][].
 */
public class Grids_GridChunkDoubleArray
        extends Grids_AbstractGridChunkDoubleArrayOrMap
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    /**
     * For storing values arranged in rows and columns.
     */
    private double[][] Data;

    protected Grids_GridChunkDoubleArray() {
    }

    /**
     * Creates a new Grids_GridChunkDoubleArray for g containing all no Data
     * values.
     *
     * @param g The Grids_GridDouble this is to be a chunk of.
     * @param chunkID The ID to be ChunkID.
     */
    protected Grids_GridChunkDoubleArray(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID) {
        super(g, chunkID);
        double noDataValue = g.getNoDataValue(false);
        Data = new double[ChunkNRows][ChunkNCols];
        int row;
        for (row = 0; row < ChunkNRows; row++) {
            Arrays.fill(Data[row], noDataValue);
        }
        SwapUpToDate = false;
    }

    /**
     * Creates a new Grids_GridChunkDoubleArray from chunk.
     *
     *
     * @param chunk The Grids_AbstractGridChunkDouble this values are taken
     * from.
     * @param chunkID The ID to be ChunkID.
     * @TODO optimise for different types of chunk.
     */
    protected Grids_GridChunkDoubleArray(
            Grids_AbstractGridChunkDouble chunk,
            Grids_2D_ID_int chunkID) {
        super(chunk.getGrid(), chunkID);
        initData();
        int row;
        int col;
        boolean handleOutOfMemoryError = true;
        for (row = 0; row < ChunkNRows; row++) {
            for (col = 0; col < ChunkNCols; col++) {
                Data[row][col] = chunk.getCell(
                        row,
                        col,
                        handleOutOfMemoryError);
            }
        }
        SwapUpToDate = false;
    }

    /**
     * Initialises the Data associated with this.
     */
    @Override
    protected final void initData() {
        Data = new double[ChunkNRows][ChunkNCols];
    }

    protected double[][] getData() {
        return Data;
    }

    protected @Override
    void clearData() {
        Data = null;
        System.gc();
    }

    @Override
    protected double getCell(
            int row,
            int col) {
        return Data[row][col];
    }

    @Override
    public void initCell(
            int row,
            int col,
            double valueToInitialise) {
        Data[row][col] = valueToInitialise;
    }

    /**
     * Returns the value at position given by: row, col and sets it to
     * valueToSet
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param valueToSet the value the cell is to be set to
     * @return
     */
    protected @Override
    double setCell(
            int row,
            int col,
            double valueToSet) {
        double oldValue;

        oldValue = 0;
        try {
            oldValue = Data[row][col];
        } catch (Exception e) {
            int debug = 1;
        }

        try {
            Data[row][col] = valueToSet;
        } catch (Exception e) {
            int debug = 1;
        }
        
        if (isSwapUpToDate()) {
            // Optimisation? Want a setCellFast method closer to initCell? 
            // What about an unmodifiable readOnly type chunk?
            if (valueToSet != oldValue) {
                setSwapUpToDate(false);
            }
        }
        return oldValue;
    }

    /**
     * Returns a Grids_GridChunkDoubleArrayOrMapIterator for iterating over the
     * cells in this.
     *
     * @return
     */
    @Override
    protected Grids_GridChunkDoubleArrayOrMapIterator iterator() {
        return new Grids_GridChunkDoubleArrayOrMapIterator(this);
    }

    @Override
    protected double getArithmeticMeanDouble() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number getMin(boolean update, boolean handleOutOfMemoryError) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number getMax(boolean update, boolean handleOutOfMemoryError) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
