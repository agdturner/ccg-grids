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

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;

/**
 * Grids_AbstractGridChunkInt extension that stores cell values in a int[][].
 */
public class Grids_GridChunkIntArray
        extends Grids_AbstractGridChunkIntArrayOrMap
        implements Serializable {

    //private static final long serialVersionUID = 1L; 
    /**
     * For storing values arranged in rows and columns.
     */
    private int[][] Data;

    /**
     * Default constructor
     */
    protected Grids_GridChunkIntArray() {
    }

    /**
     * Creates a new Grid2DSquareCellInt grid containing all no Data values.
     *
     * @param g
     * @param chunkID
     */
    protected Grids_GridChunkIntArray(
            Grids_GridInt g,
            Grids_2D_ID_int chunkID) {
        super(g, chunkID);
        initData();
        int noDataValue = g.getNoDataValue();
        int row;
        for (row = 0; row < ChunkNRows; row++) {
            Arrays.fill(Data[row], noDataValue);
        }
        SwapUpToDate = false;
    }

    /**
     * TODO: 1. docs 2. A fast toArray() method in Grid2DSquareCellIntChunkMap
     * could be coded then a constructor based on an int[] or int[][] might be
     * faster?
     *
     * @param chunk
     * @param chunkID
     */
    protected Grids_GridChunkIntArray(
            Grids_AbstractGridChunkInt chunk,
            Grids_2D_ID_int chunkID) {
        super(chunk.getGrid(), chunkID);
        initData();
        int row;
        int col;
        for (row = 0; row < ChunkNRows; row++) {
            for (col = 0; col < ChunkNCols; col++) {
                Data[row][col] = chunk.getCell(row, col);
            }
        }
        SwapUpToDate = false;
    }

    /**
     * Initialises the Data associated with this.
     */
    @Override
    protected final void initData() {
        Data = new int[ChunkNRows][ChunkNCols];
    }

    /**
     * Returns Data. TODO: Should the array be copied and the copy returned?
     *
     * @return
     */
    protected int[][] getData() {
        return Data;
    }

    /**
     * Clears the Data associated with
     */
    protected @Override
    void clearData() {
        Data = null;
        //System.gc();
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @return
     */
    public @Override
    int getCell(
            int row,
            int col) {
        return Data[row][col];
    }

    /**
     * Initialises the value at position given by: row, col.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk.
     * @param col the column index of the cell w.r.t. the origin of this chunk.
     * @param v the value with which the cell is initialised
     */
    @Override
    public void initCell(
            int row,
            int col,
            int v) {
        Data[row][col] = v;
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex and sets it to valueToSet
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param v the value the cell is to be set to
     * @return
     */
    @Override
    public int setCell(
            int row,
            int col,
            int v) {
        int oldValue = Data[row][col];
        Data[row][col] = v;
        if (isSwapUpToDate()) {
            if (v != oldValue) {
                setSwapUpToDate(false);
            }
        }
        return oldValue;
    }

    @Override
    public Number getMin(boolean update) {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number getMax(boolean update) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal getArithmeticMean(int numberOfDecimalPlaces) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Returns a Grids_GridChunkIntArrayOrMapIterator for iterating over the
     * cells in this in row major order.
     *
     * @return
     */
    @Override
    public Grids_GridChunkIntArrayOrMapIterator iterator() {
        return new Grids_GridChunkIntArrayOrMapIterator(this);
    }

}
