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
import io.github.agdturner.grids.d2.grid.b.Grids_GridBinary;
import io.github.agdturner.grids.d2.chunk.Grids_Chunk;

/**
 * Stores the data in a {@code Boolean[][]}.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkBinary extends Grids_Chunk {

    private static final long serialVersionUID = 1L;
    
    boolean[][] data;

    protected Grids_ChunkBinary() {}

    public Grids_ChunkBinary(Grids_GridBinary g, Grids_2D_ID_int chunkID) {
        super();
        this.Grid = g;
        ChunkID = chunkID;
        initData();
    }

    @Override
    protected final void initData() {
        Grids_GridBinary g = getGrid();
        int chunkNrows = g.getChunkNRows(ChunkID);
        int chunkNcols = g.getChunkNCols(ChunkID);
        data = new boolean[chunkNrows][chunkNcols];
    }

    /**
     * @return (Grids_GridBoolean) Grid;
     */
    @Override
    public Grids_GridBinary getGrid() {
        return (Grids_GridBinary) Grid;
    }

    /**
     * Returns the value at row, col.
     *
     * @param row The row of the cell w.r.t. the origin of this chunk.
     * @param col The column of the cell w.r.t. the origin of this chunk.
     * @return
     */
    public boolean getCell(int row, int col) {
        return this.data[row][col];
    }

    /**
     * Returns the value at position given by: row, col and sets it to value.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param v the value the cell is to be set to
     * @return
     */
    public boolean setCell(int row, int col, boolean v) {
        boolean v0 = this.data[row][col];
        this.data[row][col] = v;
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
        this.data[row][col] = value;
    }

    /**
     * For clearing the data associated with this.
     */
    @Override
    protected void clearData() {
        data = null;
    }

    protected boolean[][] getData() {
        return data;
    }

    public Grids_ChunkIteratorBinary iterator() {
        return new Grids_ChunkIteratorBinary(this);
    }

    @Override
    public Long getN() {
        long n = 0;
        Grids_ChunkIteratorBinary ite = iterator();
        while (ite.hasNext()) {
            if (ite.next()) {
                n++;
            }
        }
        return n;
    }

}
