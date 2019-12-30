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
package uk.ac.leeds.ccg.grids.d2.chunk.d;

import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import java.util.Arrays;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;

/**
 * Grids_ChunkDouble extension that stores cell values in a double[][].
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkDoubleArray extends Grids_ChunkDoubleArrayOrMap {

    private static final long serialVersionUID = 1L;

    /**
     * For storing values arranged in rows and columns.
     */
    private double[][] data;

    /**
     * Creates a new Grids_GridChunkDoubleArray for g containing all no data
     * values.
     *
     * @param g The Grids_GridDouble this is to be a chunk of.
     * @param i The ID to be id.
     */
    protected Grids_ChunkDoubleArray(Grids_GridDouble g, Grids_2D_ID_int i) {
        super(g, i);
        double noDataValue = g.getNoDataValue();
        data = new double[chunkNRows][chunkNCols];
        int row;
        for (row = 0; row < chunkNRows; row++) {
            Arrays.fill(data[row], noDataValue);
        }
        cacheUpToDate = false;
    }

    /**
     * TODO: Optimise for different types of chunk. A fast toArray() could be
     * coded then a constructor based on an double[] or double[][] might be
     * faster?
     *
     * @param c The chunk that's values will be duplicated.
     * @param i The chunkID.
     */
    protected Grids_ChunkDoubleArray(Grids_ChunkDouble c, Grids_2D_ID_int i) {
        super(c.getGrid(), i);
        initData();
        for (int row = 0; row < chunkNRows; row++) {
            for (int col = 0; col < chunkNCols; col++) {
                data[row][col] = c.getCell(row, col);
            }
        }
        cacheUpToDate = false;
    }

    /**
     * Initialises {@link #data}.
     */
    @Override
    protected final void initData() {
        data = new double[chunkNRows][chunkNCols];
    }

    /**
     * TODO: Should the array be copied and the copy returned?
     *
     * @return {@link #data}.
     */
    protected double[][] getData() {
        return data;
    }

    /**
     * Sets {@link #data} to {@code null}.
     */
    @Override
    protected void clearData() {
        data = null;
        //System.gc();
    }

    /**
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @return The value at position given by: chunk cell row {@code row}; chunk
     * cell column {@code col}.
     */
    @Override
    public double getCell(int row, int col) {
        return data[row][col];
    }

    /**
     * Initialises the value at position given by: row, col.
     *
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @param v The value initialised.
     */
    @Override
    public void initCell(int row, int col, double v) {
        data[row][col] = v;
    }

    /**
     * Sets the value at position given by: chunk cell row {@code row}; chunk
     * cell row {@code col} to {@code v}.
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @param v The value set.
     * @return The value at position given by: chunk cell row {@code row};
     * chunk cell row {@code col} prior to it being set to {@code v}.
     */
    @Override
    public double setCell(int row, int col, double v) {
        double oldValue = data[row][col];
        data[row][col] = v;
        if (isCacheUpToDate()) {
            if (v != oldValue) {
                setCacheUpToDate(false);
            }
        }
        return oldValue;
    }

    /**
     * @return An iterator for iterating over the cells in this.
     */
    public Grids_ChunkIteratorDoubleArrayOrMap iterator() {
        return new Grids_ChunkIteratorDoubleArrayOrMap(this);
    }

    @Override
    public Double getMin(boolean update) {
        Double r = data[0][0];
        Grids_GridDouble g = getGrid();
        double noDataValue = g.getNoDataValue();
        for (int row = 0; row < chunkNRows; row++) {
            for (int col = 0; col < chunkNCols; col++) {
                double v = data[chunkNRows][chunkNCols];
                if (v != noDataValue) {
                    r = Math.min(r, v);
                }
            }
        }
        return r;
    }

    @Override
    public Number getMax(boolean update) {
        Double r = data[0][0];
        Grids_GridDouble g = getGrid();
        double noDataValue = g.getNoDataValue();
        for (int row = 0; row < chunkNRows; row++) {
            for (int col = 0; col < chunkNCols; col++) {
                double v = data[chunkNRows][chunkNCols];
                if (v != noDataValue) {
                    r = Math.max(r, v);
                }
            }
        }
        return r;
    }

}
