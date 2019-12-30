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
package uk.ac.leeds.ccg.grids.d2.chunk.b;

import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.grid.b.Grids_GridBinary;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;

/**
 * Stores the data in a {@code boolean[][]}.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkBinaryArray extends Grids_Chunk {

    private static final long serialVersionUID = 1L;

    boolean[][] data;

    public Grids_ChunkBinaryArray(Grids_GridBinary g, Grids_2D_ID_int i) {
        super(g, i, true);
        initData();
    }

    @Override
    protected final void initData() {
        Grids_GridBinary g = getGrid();
        int chunkNrows = g.getChunkNRows(id);
        int chunkNcols = g.getChunkNCols(id);
        data = new boolean[chunkNrows][chunkNcols];
    }

    /**
     * @return (Grids_GridBinary) grid;
     */
    @Override
    public Grids_GridBinary getGrid() {
        return (Grids_GridBinary) grid;
    }

    /**
     * Returns the value at {@code row}, {@code col}.
     *
     * @param row The row of the cell w.r.t. the origin of this chunk.
     * @param col The column of the cell w.r.t. the origin of this chunk.
     * @return The value at {@code row}, {@code col}.
     */
    public boolean getCell(int row, int col) {
        return this.data[row][col];
    }

    /**
     * Returns the value at {@code row}, {@code col} and sets it to value
     * {@code v}.
     *
     * @param row The chunk cell row index.
     * @param col The chunk cell column index.
     * @param v The value the cell is to be set to.
     * @return The value at {@code row}, {@code col} before it is set to
     * {@code v}.
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
     * Initialises the value at {@code row}, {@code col} to {@code v}.
     *
     * @param row The row of the cell w.r.t. the origin of this chunk.
     * @param col The column of the cell w.r.t. the origin of this chunk.
     * @param v The value to initialise.
     */
    public void initCell(int row, int col, Boolean v) {
        this.data[row][col] = v;
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

    public Grids_ChunkIteratorBinaryArray iterator() {
        return new Grids_ChunkIteratorBinaryArray(this);
    }

    @Override
    public Long getN() {
        long n = 0;
        Grids_ChunkIteratorBinaryArray ite = iterator();
        while (ite.hasNext()) {
            if (ite.next()) {
                n++;
            }
        }
        return n;
    }

}
