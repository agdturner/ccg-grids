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

import uk.ac.leeds.ccg.grids.d2.grid.b.Grids_GridBoolean;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;

/**
 * For chunks that represent values at cell locations that are {@code Boolean}.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_ChunkBoolean extends Grids_Chunk {

    private static final long serialVersionUID = 1L;

    /**
     * @param g What {@link #grid} is set to.
     * @param i What {@link #id} is set to.
     * @param worthClearing What {@link #worthClearing} is set to.
     */
    protected Grids_ChunkBoolean(Grids_GridBoolean g, Grids_2D_ID_int i,
            boolean worthClearing) {
        super(g, i, worthClearing);
    }

    /**
     * @return (Grids_GridBoolean) grid;
     */
    @Override
    public final Grids_GridBoolean getGrid() {
        return (Grids_GridBoolean) grid;
    }

    /**
     * @param r The chunk cell row index.
     * @param c The chunk cell column index.
     * @return The value at chunk cell row {@code r}, chunk cell column index
     * {@code c}.
     */
    public abstract Boolean getCell(int r, int c);

    /**
     * Initialises the value at chunk cell row {@code r}, chunk cell column
     * {@code c} to {@code v}.
     *
     * @param r The chunk cell row.
     * @param c The chunk cell column.
     * @param v The value to initialise.
     */
    public abstract void initCell(int r, int c, Boolean v);

    /**
     * Returns the value at chunk cell row {@code r}, chunk cell column
     * {@code c} and sets it to {@code v}.
     *
     * @param r The chunk cell row.
     * @param c The chunk cell column.
     * @param v The value the cell is to be set to.
     * @return The value at chunk cell row {@code r}, chunk cell column
     * {@code c} before it is set.
     * @throws Exception If encountered.
     */
    public abstract Boolean setCell(int r, int c, Boolean v) throws Exception;

    /**
     * @return All the values including noDataValue's in row major order as a
     * double[].
     */
    public Boolean[] toArrayIncludingNoDataValues() {
        Grids_GridBoolean g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        Boolean[] array = new Boolean[nrows * ncols];
        int count = 0;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                array[count] = getCell(row, col);
                count++;
            }
        }
        return array;
    }

    /**
     * @return All the values excluding noDataValues in row major order as a
     * double[].
     */
    public Boolean[] toArrayNotIncludingNoDataValues() {
        Grids_GridBoolean g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        long n = getN();
        Boolean[] array = new Boolean[(int) n];
        int count = 0;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                Boolean v = getCell(row, col);
                if (v != null) {
                    array[count] = v;
                    count++;
                }
            }
        }
        return array;
    }

    /**
     * @return The number of cells with data values.
     */
    @Override
    public Long getN() {
        long n = 0;
        Grids_GridBoolean g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                Boolean v = getCell(row, col);
                if (v != null) {
                        n++;
                }
            }
        }
        return n;
    }
}
