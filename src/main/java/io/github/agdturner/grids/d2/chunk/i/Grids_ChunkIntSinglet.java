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

import java.math.BigDecimal;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;
import java.math.RoundingMode;

/**
 * Grids_AbstractGridChunkDouble extension for which all values are the same.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIntSinglet extends Grids_ChunkInt {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the value of every cell in this grid.
     */
    public int Value;

    /**
     * Creates a chunk with {@link #Value} set to {@code v}.
     *
     * @param g What {@link #Grid} is set to.
     * @param i What {@link #ChunkID} is set to.
     * @param v What {@link #Value} is set to.
     */
    public Grids_ChunkIntSinglet(Grids_GridInt g, Grids_2D_ID_int i, int v) {
        super(g, i, false);
        initGrid(g);
        Value = v;
    }

    @Override
    protected final void initData() {
    }

    protected int getValue() {
        return Value;
    }

    @Override
    protected void clearData() {
    }

    @Override
    public int getCell(int row, int col) {
        return Value;
    }

    /**
     * Returns the value at position given by: row, col and sets it to
     * valueToSet.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param valueToSet the value the cell is to be set to.
     * @return
     */
    @Override
    public int setCell(int row, int col, int valueToSet) {
        if (valueToSet == Value) {
            return Value;
        } else {
            // @TODO
            throw new Error("Unable to set value as this chunk is supposed "
                    + "to all contain the same value. What is needed is to "
                    + "transform the chunk to use a richer data structure to "
                    + "store valueToSet in ust this cell.");
        }
    }

    /**
     * Returns a Grids_GridChunkDoubleArrayIterator for iterating over the cells
     * in this.
     *
     * @return
     */
    public Grids_ChunkIteratorIntSinglet iterator() {
        return new Grids_ChunkIteratorIntSinglet(this);
    }

    @Override
    public void initCell(int row, int col, int valueToInitialise) {
        if (valueToInitialise != Value) {
            throw new Error("valueToInitialise != Value in "
                    + this.getClass().getName() + ".initCell(int,int,int)");
        }
    }

    @Override
    public Number getMin(boolean update) {
        return Value;
    }

    @Override
    public Number getMax(boolean update) {
        return Value;
    }

    @Override
    public BigDecimal getArithmeticMean(int dp, RoundingMode rm) {
        return BigDecimal.valueOf(Value);
    }

    @Override
    public double getArithmeticMeanDouble() {
        return Value;
    }
}
