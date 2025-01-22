/*
 * Copyright 2025 Andy Turner, University of Leeds.
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
import uk.ac.leeds.ccg.grids.d2.grid.br.Grids_GridBR;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;

/**
 * Grids_ChunkR extension for which all values are the same.
 *
 * @author Andy Turner
 * @version 1.1
 */
public class Grids_ChunkBRSinglet extends Grids_ChunkBR {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the v of every cell in this grid.
     */
    public BigRational v;

    /**
     * Creates a new Grids_GridChunkBR with {@link #v} set to {@code v}.
     *
     * @param g What {@link #grid} is set to.
     * @param i What {@link #id} is set to.
     * @param v What {@link #v} is set to.
     */
    public Grids_ChunkBRSinglet(Grids_GridBR g, Grids_2D_ID_int i,
            BigRational v) {
        super(g, i, false);
        this.v = v;
    }

    @Override
    protected final void initData() {
    }

    /**
     * @return {@link #v} 
     */
    protected BigRational getV() {
        return v;
    }

    @Override
    protected void clearData() {
    }

    /**
     * Beware OutOfMemoryErrors being thrown if calling this method.
     *
     * @param row The row.
     * @param col The column.
     * @return The value at (row, col).
     */
    @Override
    public BigRational getCell(int row, int col) {
        return v;
    }

    /**
     * Returns the v at position given by: row, col and sets it to valueToSet.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param v the v the cell is to be set to.
     * @return The value v at row, col.
     * @throws java.lang.Exception If encountered.
     */
    @Override
    public BigRational setCell(int row, int col, BigRational v) throws Exception {
        if (v == this.v) {
            return this.v;
        } else {
            throw new Exception("Unable to set value as this chunk is supposed "
                    + "to all contain the same value. Convert to another type "
                    + "of chunk?");
        }
    }

    /**
     * @return An iterator for iterating over the values in this.
     */
    public Grids_ChunkBRIteratorSinglet iterator() {
        return new Grids_ChunkBRIteratorSinglet(this);
    }

    @Override
    public void initCell(int r, int c, BigRational v) {
    }

    @Override
    public BigRational getSum() {
        if (v == getGrid().getNoDataValue()) {
            return BigRational.ZERO;
        }
        return BigRational.valueOf(getN()).multiply(v);
    }

    @Override
    public Number getMin(boolean update) {
        return v;
    }

    @Override
    public Number getMax(boolean update) {
        return v;
    }

    @Override
    public BigRational getArithmeticMean() {
        return v;
    }
}
