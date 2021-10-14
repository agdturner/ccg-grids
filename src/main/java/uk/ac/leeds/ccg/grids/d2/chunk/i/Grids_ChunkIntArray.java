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
package uk.ac.leeds.ccg.grids.d2.chunk.i;

import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;
import java.util.Arrays;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;

/**
 * Grids_ChunkInt that stores cell values in a int[][].
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIntArray extends Grids_ChunkIntArrayOrMap {

    private static final long serialVersionUID = 1L;

    /**
     * For storing values arranged in rows and columns.
     */
    private int[][] data;

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
        for (row = 0; row < chunkNRows; row++) {
            Arrays.fill(data[row], noDataValue);
        }
        cacheUpToDate = false;
    }

    /**
     * @param c The chunk that's values will be duplicated.
     * @param i The chunk ID.
     */
    protected Grids_ChunkIntArray(Grids_ChunkInt c, Grids_2D_ID_int i) {
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
        data = new int[chunkNRows][chunkNCols];
    }

    /**
     * @return {@link #data}.
     */
    protected int[][] getData() {
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
     * @param row The row.
     * @param col The column.
     * @return The value at position given by: chunk cell row {@code row}; chunk
     * cell column {@code col}.
     */
    @Override
    public int getCell(int row, int col) {
        return data[row][col];
    }

    /**
     * Initialises the value at position given by: row, col.
     *
     * @param row The row.
     * @param col The column.
     * @param v The value to initialise.
     */
    @Override
    public void initCell(int row, int col, int v) {
        data[row][col] = v;
    }

    /**
     * Sets the value at position given by: chunk cell row {@code row};
     * chunk cell row {@code col} to {@code v}.
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @param v The value set.
     * @return The value at position given by: chunk cell row {@code row};
     * chunk cell row {@code col} prior to it being set to {@code v}.
     */
    @Override
    public int setCell(int row, int col, int v) {
        int oldValue = data[row][col];
        data[row][col] = v;
        if (isCacheUpToDate()) {
            if (v != oldValue) {
                setCacheUpToDate(false);
            }
        }
        return oldValue;
    }

    /**
     * @return A {@link Grids_ChunkIntIteratorArrayOrMap} for iterating over the 
     * cells in this.
     */
    public Grids_ChunkIntIteratorArrayOrMap iterator() {
        return new Grids_ChunkIntIteratorArrayOrMap(this);
    }

    @Override
    public Integer getMin(boolean update) {
        Integer r = data[0][0];
        Grids_GridInt g = getGrid();
        int noDataValue = g.getNoDataValue();
        for (int row = 0; row < chunkNRows; row++) {
            for (int col = 0; col < chunkNCols; col++) {
                int v = data[chunkNRows][chunkNCols];
                if (v != noDataValue) {
                    r = Math.min(r, v);
                }
            }
        }
        return r;
    }

    @Override
    public Integer getMax(boolean update) {
        Integer r = data[0][0];
        Grids_GridInt g = getGrid();
        int noDataValue = g.getNoDataValue();
        for (int row = 0; row < chunkNRows; row++) {
            for (int col = 0; col < chunkNCols; col++) {
                int v = data[chunkNRows][chunkNCols];
                if (v != noDataValue) {
                    r = Math.min(r, v);
                }
            }
        }
        return r;
    }

}
