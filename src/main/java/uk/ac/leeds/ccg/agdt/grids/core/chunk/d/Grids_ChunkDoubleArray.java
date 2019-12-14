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
package uk.ac.leeds.ccg.agdt.grids.core.chunk.d;

import uk.ac.leeds.ccg.agdt.grids.core.grid.d.Grids_GridDouble;
import java.io.Serializable;
import java.util.Arrays;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;

/**
 * Grids_ChunkDouble extension that stores cell values in a
 double[][].
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkDoubleArray
        extends Grids_ChunkDoubleArrayOrMap
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    /**
     * For storing values arranged in rows and columns.
     */
    private double[][] Data;

    protected Grids_ChunkDoubleArray() {
    }

    /**
     * Creates a new Grids_GridChunkDoubleArray for g containing all no Data
     * values.
     *
     * @param g The Grids_GridDouble this is to be a chunk of.
     * @param chunkID The ID to be ChunkID.
     */
    protected Grids_ChunkDoubleArray(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID) {
        super(g, chunkID);
        double noDataValue = g.getNoDataValue();
        Data = new double[ChunkNRows][ChunkNCols];
        int row;
        for (row = 0; row < ChunkNRows; row++) {
            Arrays.fill(Data[row], noDataValue);
        }
        CacheUpToDate = false;
    }

    /**
     * Creates a new Grids_GridChunkDoubleArray from chunk.
     *
     *
     * @param chunk The Grids_ChunkDouble this values are taken
 from.
     * @param chunkID The ID to be ChunkID.
     * @TODO optimise for different types of chunk.
     */
    protected Grids_ChunkDoubleArray(
            Grids_ChunkDouble chunk,
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
        Data = new double[ChunkNRows][ChunkNCols];
    }

    protected double[][] getData() {
        return Data;
    }

    protected @Override
    void clearData() {
        Data = null;
        //System.gc();
    }

    /**
     * Beware OutOfMemoryErrors being thrown if calling this method.
     *
     * @param row
     * @param col
     * @return
     */
    @Override
    public double getCell(
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
     * Returns the value at position given by: row, col and sets it to value.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param v the value the cell is to be set to
     * @return
     */
    @Override
    public double setCell(
            int row,
            int col,
            double v) {
        double oldValue;
        oldValue = Data[row][col];
        Data[row][col] = v;
        if (isCacheUpToDate()) {
            if (v != oldValue) {
                setCacheUpToDate(false);
            }
        }
        return oldValue;
    }

    /**
     * Returns a Grids_ChunkIteratorDoubleArrayOrMap for iterating over the
 cells in this.
     *
     * @return
     */
    public Grids_ChunkIteratorDoubleArrayOrMap iterator() {
        return new Grids_ChunkIteratorDoubleArrayOrMap(this);
    }

    @Override
    public Double getMin(boolean update) {
        Double result = Data[0][0];
        Grids_GridDouble g = getGrid();
        double noDataValue = g.getNoDataValue();
        double v;
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
    public Number getMax(boolean update) {
        Double result = Data[0][0];
        Grids_GridDouble g = getGrid();
        double noDataValue = g.getNoDataValue();
        double v;
        int row;
        int col;
        for (row = 0; row < ChunkNRows; row++) {
            for (col = 0; col < ChunkNCols; col++) {
                v = Data[ChunkNRows][ChunkNCols];
                if (v != noDataValue) {
                    result = Math.max(result, v);
                }
            }
        }
        return result;
    }

}
