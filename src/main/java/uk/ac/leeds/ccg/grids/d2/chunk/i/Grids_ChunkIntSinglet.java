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

import ch.obermuhlner.math.big.BigRational;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;

/**
 * Grids_AbstractGridChunkDouble extension for which all values are the same.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIntSinglet extends Grids_ChunkInt {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the v of every cell in this grid.
     */
    public int v;

    /**
     * Creates a chunk with {@link #v} set to {@code v}.
     *
     * @param g What {@link #grid} is set to.
     * @param i What {@link #id} is set to.
     * @param v What {@link #v} is set to.
     */
    public Grids_ChunkIntSinglet(Grids_GridInt g, Grids_2D_ID_int i, int v) {
        super(g, i, false);
        initGrid(g);
        this.v = v;
    }

    @Override
    protected final void initData() {
    }

    protected int getValue() {
        return v;
    }

    @Override
    protected void clearData() {
    }

    @Override
    public int getCell(int row, int col) {
        return v;
    }

    /**
     * @param row The row index of the cell w.r.t. the origin of this chunk
     * @param col The column index of the cell w.r.t. the origin of this chunk
     * @param v The v the cell is to be set to.
     * @return The v at position given by: row, col and sets it to
 valueToSet.
     */
    @Override
    public int setCell(int row, int col, int v) {
        if (v == this.v) {
            return this.v;
        } else {
            throw new Error("Unable to set value as this chunk is supposed "
                    + "to all contain the same value. What is needed is to "
                    + "transform the chunk to use a richer data structure to "
                    + "store valueToSet in ust this cell.");
        }
    }

    /**
     * @return An iterator for iterating over the cells
     * in this.

     */
    public Grids_ChunkIteratorIntSinglet iterator() {
        return new Grids_ChunkIteratorIntSinglet(this);
    }

    @Override
    public void initCell(int row, int col, int v) {
        if (v != this.v) {
            throw new Error("valueToInitialise != Value in "
                    + this.getClass().getName() + ".initCell(int,int,int)");
        }
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
        return BigRational.valueOf(v);
    }
}
