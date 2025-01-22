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
package uk.ac.leeds.ccg.grids.d2.chunk.br;

import ch.obermuhlner.math.big.BigRational;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.grids.d2.grid.br.Grids_GridBR;
import java.util.Arrays;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;

/**
 * Grids_ChunkR extension that stores cell values in a double[][].
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkBRArray extends Grids_ChunkBRArrayOrMap {

    private static final long serialVersionUID = 1L;

    /**
     * For storing values arranged in rows and columns.
     */
    private BigRational[][] data;

    /**
     * Creates a new Grids_GridChunkBRArray for g containing all no data
     * values.
     *
     * @param g The Grids_GridBR this is to be a chunk of.
     * @param i The ID to be id.
     */
    protected Grids_ChunkBRArray(Grids_GridBR g, Grids_2D_ID_int i) {
        super(g, i);
        data = new BigRational[chunkNRows][chunkNCols];
        for (int row = 0; row < chunkNRows; row++) {
            Arrays.fill(data[row], g.ndv);
        }
        cacheUpToDate = false;
    }

    /**
     * @param c The chunk that's values will be duplicated.
     * @param i The chunkID.
     */
    protected Grids_ChunkBRArray(Grids_ChunkBR c, Grids_2D_ID_int i) {
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
        data = new BigRational[chunkNRows][chunkNCols];
    }

    /**
     * TODO: Should the array be copied and the copy returned?
     *
     * @return {@link #data}.
     */
    protected BigRational[][] getData() {
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
    public BigRational getCell(int row, int col) {
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
    public void initCell(int row, int col, BigRational v) {
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
    public BigRational setCell(int row, int col, BigRational v) {
        BigRational oldValue = data[row][col];
        data[row][col] = v;
        if (isCacheUpToDate()) {
            if (v.compareTo(oldValue) != 0) {
                setCacheUpToDate(false);
            }
        }
        return oldValue;
    }

    /**
     * @return An iterator for iterating over the cells in this.
     */
    public Grids_ChunkBRIteratorArrayOrMap iterator() {
        return new Grids_ChunkBRIteratorArrayOrMap(this);
    }

    @Override
    public BigRational getMin(boolean update) {
        BigRational r = data[0][0];
        Grids_GridBR g = getGrid();
        for (int row = 0; row < chunkNRows; row++) {
            for (int col = 0; col < chunkNCols; col++) {
                BigRational v = data[chunkNRows][chunkNCols];
                if (v.compareTo(g.ndv) != 0) {
                    r = r.min(v);
                }
            }
        }
        return r;
    }

    @Override
    public Number getMax(boolean update) {
        BigRational r = data[0][0];
        Grids_GridBR g = getGrid();
        for (int row = 0; row < chunkNRows; row++) {
            for (int col = 0; col < chunkNCols; col++) {
                BigRational v = data[chunkNRows][chunkNCols];
                if (v.compareTo(g.ndv) != 0) {
                    r = r.max(v);
                }
            }
        }
        return r;
    }

}
