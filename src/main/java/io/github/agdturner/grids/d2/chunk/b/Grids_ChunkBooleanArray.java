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
package io.github.agdturner.grids.d2.chunk.b;

import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.d2.grid.b.Grids_GridBoolean;

/**
 * Stores the data in a {@code Boolean[][]}.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkBooleanArray extends Grids_ChunkBooleanArrayOrMap {

    private static final long serialVersionUID = 1L;
    
    Boolean[][] data;

    public Grids_ChunkBooleanArray(Grids_GridBoolean g, Grids_2D_ID_int i) {
        super(g, i);
        initData();
    }

    @Override
    protected final void initData() {
        Grids_GridBoolean g = getGrid();
        int chunkNrows = g.getChunkNRows(ChunkID);
        int chunkNcols = g.getChunkNCols(ChunkID);
        data = new Boolean[chunkNrows][chunkNcols];
    }

    /**
     * @return (Grids_GridBoolean) Grid;
     */
    @Override
    public Grids_GridBoolean getGrid() {
        return (Grids_GridBoolean) Grid;
    }

    /**
     * Returns the value at row, col.
     *
     * @param row The row of the cell w.r.t. the origin of this chunk.
     * @param col The column of the cell w.r.t. the origin of this chunk.
     * @return
     */
    public Boolean getCell(int row, int col) {
        return data[row][col];
    }

    /**
     * Returns the value at position given by: row, col and sets it to value.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param v the value the cell is to be set to
     * @return
     */
    public Boolean setCell(int row, int col, boolean v) {
        Boolean v0 = data[row][col];
        data[row][col] = v;
        if (isCacheUpToDate()) {
            if (v != v0) {
                setCacheUpToDate(false);
            }
        }
        return v0;
    }

    /**
     * Initialises the value at row, col to v.
     *
     * @param row The row of the cell w.r.t. the origin of this chunk.
     * @param col The column of the cell w.r.t. the origin of this chunk.
     * @param value
     */
    public void initCell(int row, int col, Boolean value) {
        data[row][col] = value;
    }

    /**
     * For clearing the data associated with this.
     */
    @Override
    protected void clearData() {
        data = null;
    }

    protected Boolean[][] getData() {
        return data;
    }

    public Grids_ChunkIteratorBooleanArray iterator() {
        return new Grids_ChunkIteratorBooleanArray(this);
    }

    @Override
    public Long getN() {
        long n = 0;
        Grids_ChunkIteratorBooleanArray ite = iterator();
        while (ite.hasNext()) {
            if (ite.next()) {
                n++;
            }
        }
        return n;
    }

}
