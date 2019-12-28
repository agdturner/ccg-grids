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
package io.github.agdturner.grids.d2.chunk;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.d2.grid.Grids_Grid;
import io.github.agdturner.grids.d2.grid.Grids_GridNumber;
import io.github.agdturner.grids.d2.stats.Grids_StatsInterface;

/**
 * A wrapper for numerical chunks.
 *
 * @author Andy Turner
 * @version 1.0.0
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
     * @return The value at row, col as a BigDecimal.
     */
    public abstract BigDecimal getCellBigDecimal(int row, int col);

    /**
     * @return The sum of all data values.
     */
    @Override
    public abstract BigDecimal getSum();

    /**
     * @param dp The number of decimal places to which the result is precise.
     * @param rm The RoundingMode used if necessary for rounding.
     * @return The Arithmetic Mean of all non no data values as a BigDecimal. If
     * all cells are no data values, then {@code null} is returned.
     */
    @Override
    public BigDecimal getArithmeticMean(int dp, RoundingMode rm) {
        BigDecimal sum = getSum();
        long n = getN();
        if (n != 0) {
            return Math_BigDecimal.divideRoundIfNecessary(sum,
                    BigInteger.valueOf(n), dp, rm);
        }
        return null;
    }
}
