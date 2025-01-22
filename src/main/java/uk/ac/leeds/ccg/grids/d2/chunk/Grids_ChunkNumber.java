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
package uk.ac.leeds.ccg.grids.d2.chunk;

import ch.obermuhlner.math.big.BigRational;
import java.math.BigInteger;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsInterface;

/**
 * A wrapper for numerical chunks.
 *
 * @author Andy Turner
 * @version 1.1
 */
public abstract class Grids_ChunkNumber extends Grids_Chunk
        implements Grids_StatsInterface {

    private static final long serialVersionUID = 1L;

    /**
     * @param g What {@link #grid} is set to.
     * @param i What {@link #id} is set to.
     * @param worthClearing What {@link #worthClearing} is set to.
     */
    protected Grids_ChunkNumber(Grids_Grid g, Grids_2D_ID_int i,
            boolean worthClearing) {
        super(g, i, worthClearing);
    }

    @Override
    public abstract Grids_GridNumber getGrid();

    /**
     * @param row The chunk row index.
     * @param col The chunk column index.
     * @return The value at row, col as a BigRational.
     */
    public abstract BigRational getCellBigRational(int row, int col);

    /**
     * @return The sum of all data values.
     */
    @Override
    public abstract BigRational getSum();

    /**
     * @return The Arithmetic Mean of all data values as a BigRational. If
     * all cells are no data values, then {@code null} is returned.
     */
    @Override
    public BigRational getArithmeticMean() {
        BigRational sum = getSum();
        long n = getN();
        if (n != 0) {
            return sum.divide(BigInteger.valueOf(n));
        }
        return null;
    }
}
