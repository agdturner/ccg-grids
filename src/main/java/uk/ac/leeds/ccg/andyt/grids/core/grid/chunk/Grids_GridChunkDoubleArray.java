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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;

/**
 * Grids_AbstractGridChunkDouble extension that stores cell values in a
 * double[][].
 */
public class Grids_GridChunkDoubleArray
        extends Grids_AbstractGridChunkDouble
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    /**
     * For storing values arranged in rows and columns.
     */
    private double[][] Data;

    /**
     * Creates a new Grid2DSquareCellDoubleChunkArray.
     */
    protected Grids_GridChunkDoubleArray() {
    }

    /**
     * Creates a new Grids_GridChunkDoubleArray for g containing all no Data
     * values.
     *
     * @param g The Grid2DSquareCellDouble this is to be a chunk of.
     * @param chunkID The ID to be _ChunkID.
     */
    protected Grids_GridChunkDoubleArray(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID) {
        super(g.ge);
        ChunkID = chunkID;
        initGrid(g);
        double noDataValue = g.getNoDataValue(false);
        int chunkNRows = g.getChunkNRows(
                chunkID,
                Grid.ge.HandleOutOfMemoryErrorFalse);
        int chunkNCols = g.getChunkNCols(
                chunkID,
                Grid.ge.HandleOutOfMemoryErrorFalse);
        
        if (chunkNRows < 0 || chunkNCols < 0) {
            int debug = 1;
        }
        
        Data = new double[chunkNRows][chunkNCols];
        int row;
        for (row = 0; row < chunkNRows; row++) {
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
     * @param chunkID The ID to be ChunkID. TODO: A fast toArray() method in
     * Grid2DSquareCellDoubleChunkMap could be coded then a constructor based on
     * an double[] or double[][] might be faster?
     */
    protected Grids_GridChunkDoubleArray(
            Grids_AbstractGridChunkDouble chunk,
            Grids_2D_ID_int chunkID) {
        super(chunk.ge);
        ChunkID = chunkID;
        Grids_GridDouble g = chunk.getGrid();
        initGrid(g);
        int chunkNrows = g.getChunkNRows(
                chunkID,
                Grid.ge.HandleOutOfMemoryErrorFalse);
        int chunkNcols = g.getChunkNCols(
                chunkID,
                Grid.ge.HandleOutOfMemoryErrorFalse);
        double noDataValue = g.getNoDataValue(false);
        initData();
        int row;
        int col;
        boolean handleOutOfMemoryError = true;
        for (row = 0; row < chunkNrows; row++) {
            for (col = 0; col < chunkNcols; col++) {
                Data[row][col] = chunk.getCell(
                        row,
                        col,
                        noDataValue,
                        handleOutOfMemoryError);
                //initCell( row, col, grid2DSquareCellDoubleChunk.getCell( row, col ) );
            }
        }
        SwapUpToDate = false;
    }

    /**
     * Initialises the Data associated with this.
     */
    @Override
    protected final void initData() {
        boolean handleOutOfMemoryError = false;
        Grids_GridDouble g = getGrid();
        int chunkNrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int chunkNcols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        Data = new double[chunkNrows][chunkNcols];
    }

    /**
     * Returns Data. TODO: Should the array be copied and the copy returned?
     *
     * @return
     */
    protected double[][] getData() {
        return Data;
    }

    /**
     * Clears the Data associated with
     */
    protected @Override
    void clearData() {
        Data = null;
        System.gc();
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param noDataValue the _NoDataValue of grid2DSquareCellDouble
     * @return
     */
    protected @Override
    double getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double noDataValue) {
//        try {
        return Data[chunkCellRowIndex][chunkCellColIndex];
//        } catch (Exception e0) {
//            return noDataValue;
//        }
    }

    /**
     * Initialises the value at position given by: chunk cell row
     * chunkCellRowIndex; chunk cell column chunkCellColIndex. Utility method
     * for constructor.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     */
    protected @Override
    void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double valueToInitialise) {
        Data[chunkCellRowIndex][chunkCellColIndex] = valueToInitialise;
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
     * @param noDataValue the _NoDataValue of grid2DSquareCellDouble
     * @return
     */
    protected @Override
    double setCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double valueToSet,
            double noDataValue) {
        if (inChunk(chunkCellRowIndex, chunkCellColIndex)) {
            double oldValue = Data[chunkCellRowIndex][chunkCellColIndex];
            Data[chunkCellRowIndex][chunkCellColIndex] = valueToSet;
            if (isSwapUpToDate()) {
                // Optimisation? Want a setCellFast method closer to initCell? 
                // What about an unmodifiable readOnly type chunk?
                if (valueToSet != oldValue) {
                    setSwapUpToDate(false);
                }
            }
            return oldValue;
        }
        return noDataValue;
    }

    /**
     * Returns a Grids_GridChunkDoubleArrayIterator for iterating over the cells
     * in this.
     *
     * @return
     */
    @Override
    //protected Grids_AbstractGridChunkIterator iterator() {
    protected Grids_GridChunkDoubleArrayIterator iterator() {
        return new Grids_GridChunkDoubleArrayIterator(this);
    }

}
