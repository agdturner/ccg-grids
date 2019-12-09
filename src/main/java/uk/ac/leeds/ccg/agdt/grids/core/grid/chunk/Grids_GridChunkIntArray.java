/*
 * Copyright 2019 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.agdt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridInt;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridDouble;

/**
 * Grids_AbstractGridChunkInt extension that stores cell values in a int[][].
*
 * @author Andy Turner
 * @version 1.0.0
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
        CacheUpToDate = false;
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
        CacheUpToDate = false;
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
        if (isCacheUpToDate()) {
            if (v != oldValue) {
                setCacheUpToDate(false);
            }
        }
        return oldValue;
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
    
    @Override
    public Number getMin(boolean update) {
        Integer result = Data[0][0];
        Grids_GridInt g = getGrid();
        int noDataValue = g.getNoDataValue();
        int v;
        int row;
        int col;
        for (row = 0; row < ChunkNRows; row++) {
            for (col = 0; col < ChunkNCols; col++) {
                v = Data[ChunkNRows][ChunkNCols];
                if (v != noDataValue) {
                    result = Math.min(result, v);
                }
            }
        }
        return result;
    }

    @Override
    public Integer getMax(boolean update) {
        Integer result = Data[0][0];
        Grids_GridInt g = getGrid();
        int noDataValue = g.getNoDataValue();
        int v;
        int row;
        int col;
        for (row = 0; row < ChunkNRows; row++) {
            for (col = 0; col < ChunkNCols; col++) {
                v = Data[ChunkNRows][ChunkNCols];
                if (v != noDataValue) {
                    result = Math.min(result, v);
                }
            }
        }
        return result;
    }

}
