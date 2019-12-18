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
package io.github.agdturner.grids.d2.chunk.i;

import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;
import java.util.Arrays;
import io.github.agdturner.grids.core.Grids_2D_ID_int;

/**
 * Grids_ChunkInt extension that stores cell values in a int[][].
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIntArray extends Grids_ChunkIntArrayOrMap {

    private static final long serialVersionUID = 1L;

    /**
     * For storing values arranged in rows and columns.
     */
    private int[][] Data;

    /**
     * Creates a new chunk filled with noDataValues.
     *
     * @param g The grid.
     * @param i The chunkID.
     */
    protected Grids_ChunkIntArray(Grids_GridInt g, Grids_2D_ID_int i) {
        super(g, i);
        initData();
        int noDataValue = g.getNoDataValue();
        int row;
        for (row = 0; row < ChunkNRows; row++) {
            Arrays.fill(Data[row], noDataValue);
        }
        CacheUpToDate = false;
    }

    /**
     * TODO: Optimise for different types of chunk. A fast toArray() could be
     * coded then a constructor based on an int[] or int[][] might be faster?
     *
     * @param c The chunk that's values will be duplicated.
     * @param i The chunkID.
     */
    protected Grids_ChunkIntArray(Grids_ChunkInt c, Grids_2D_ID_int i) {
        super(c.getGrid(), i);
        initData();
        for (int row = 0; row < ChunkNRows; row++) {
            for (int col = 0; col < ChunkNCols; col++) {
                Data[row][col] = c.getCell(row, col);
            }
        }
        CacheUpToDate = false;
    }

    /**
     * Initialises {@link #Data}.
     */
    @Override
    protected final void initData() {
        Data = new int[ChunkNRows][ChunkNCols];
    }

    /**
     * TODO: Should the array be copied and the copy returned?
     *
     * @return {@link #Data}.
     */
    protected int[][] getData() {
        return Data;
    }

    /**
     * Sets {@link #Data} to {@code null}.
     */
    @Override
    protected void clearData() {
        Data = null;
        //System.gc();
    }

    /**
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @return The value at position given by: chunk cell row {@code row}; chunk
     * cell column {@link col}.
     */
    @Override
    public int getCell(int row, int col) {
        return Data[row][col];
    }

    /**
     * Initialises the value at position given by: row, col.
     *
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @param v The value initialised.
     */
    @Override
    public void initCell(int row, int col, int v) {
        Data[row][col] = v;
    }

    /**
     * Sets the value at position given by: chunk cell row {@code row};
     * chunk cell row {@code col} to {@code v).
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @param v The value set.
     * @return The value at position given by: chunk cell row {@code row};
     * chunk cell row {@code col} prior to it being set to {@code v).
     */
    @Override
    public int setCell(int row, int col, int v) {
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
     * @return A {@link Grids_ChunkIteratorIntArrayOrMap} for iterating over the 
     * cells in this.
     */
    public Grids_ChunkIteratorIntArrayOrMap iterator() {
        return new Grids_ChunkIteratorIntArrayOrMap(this);
    }

    @Override
    public Integer getMin(boolean update) {
        Integer r = Data[0][0];
        Grids_GridInt g = getGrid();
        int noDataValue = g.getNoDataValue();
        for (int row = 0; row < ChunkNRows; row++) {
            for (int col = 0; col < ChunkNCols; col++) {
                int v = Data[ChunkNRows][ChunkNCols];
                if (v != noDataValue) {
                    r = Math.min(r, v);
                }
            }
        }
        return r;
    }

    @Override
    public Integer getMax(boolean update) {
        Integer r = Data[0][0];
        Grids_GridInt g = getGrid();
        int noDataValue = g.getNoDataValue();
        for (int row = 0; row < ChunkNRows; row++) {
            for (int col = 0; col < ChunkNCols; col++) {
                int v = Data[ChunkNRows][ChunkNCols];
                if (v != noDataValue) {
                    r = Math.min(r, v);
                }
            }
        }
        return r;
    }

}
