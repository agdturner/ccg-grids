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
package uk.ac.leeds.ccg.agdt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridDouble;
import java.io.Serializable;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;

/**
 * Grids_AbstractGridChunkDouble extension for which all values are the same.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridChunkDouble extends Grids_AbstractGridChunkDouble
        implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the value of every cell in this grid.
     */
    public double Value;

    protected Grids_GridChunkDouble() {
    }

    /**
     * Creates a new Grids_GridChunkDouble with value set to value.
     *
     * @param g The Grids_GridDouble Grids is set to.
     * @param chunkID The Grids_2D_ID_int ChunkID is set to.
     * @param value To be the value of all cells in this chunk.
     */
    public Grids_GridChunkDouble(Grids_GridDouble g, Grids_2D_ID_int chunkID,
            double value) {
        super(g, chunkID);
        Value = value;
    }

    @Override
    protected final void initData() {
    }

    protected double getValue() {
        return Value;
    }

    @Override
    protected void clearData() {
    }

    /**
     * Beware OutOfMemoryErrors being thrown if calling this method.
     *
     * @param row
     * @param col
     * @return
     */
    @Override
    public double getCell(
            int row,
            int col) {
        return Value;
    }

    /**
     * Returns the value at position given by: row, col and sets it to
     * valueToSet.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param v the value the cell is to be set to.
     * @return
     */
    @Override
    public double setCell(
            int row,
            int col,
            double v) {
        if (v == Value) {
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
    public Grids_GridChunkDoubleIterator iterator() {
        return new Grids_GridChunkDoubleIterator(this);
    }

    @Override
    public void initCell(int row, int col, double valueToInitialise) {
    }

    public double getSumDouble() {
        return getN() * Value;
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
    public BigDecimal getArithmeticMean(int numberOfDecimalPlaces) {
        return BigDecimal.valueOf(Value);
    }

    @Override
    public double getArithmeticMeanDouble() {
        return Value;
    }

}
