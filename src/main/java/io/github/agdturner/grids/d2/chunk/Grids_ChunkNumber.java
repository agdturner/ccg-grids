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

import java.io.IOException;
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
     * @param g What {@link #Grid} is set to.
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
     * Returns the value at row, col.
     *
     * @param row the row of the cell w.r.t. the origin of this chunk.
     * @param col the column of the cell w.r.t. the origin of this chunk.
     * @return
     */
    public abstract BigDecimal getCellBigDecimal(int row, int col);

//    /**
//     * Returns the number of cells with data values.
//     *
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * cache operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    @Override
//    public Long getN(boolean hoome) {
//        try {
//            long result = getN();
//            env.checkAndMaybeFreeMemory(hoome);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                if (env.cacheChunkExcept_Account(Grid, ChunkID, false) < 1L) {
//                    throw e;
//                }
//                env.initMemoryReserve(Grid, ChunkID, hoome);
//                return Grids_ChunkNumber.this.getN(hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
//
//    /**
//     * Returns the sum of all data values.
//     *
//     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
//     * cache operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    @Override
//    public BigDecimal getSum(boolean handleOutOfMemoryError) {
//        try {
//            BigDecimal result = getSum();
//            env.checkAndMaybeFreeMemory(handleOutOfMemoryError);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (handleOutOfMemoryError) {
//                env.clearMemoryReserve();
//                if (env.cacheChunkExcept_Account(Grid, ChunkID, false) < 1L) {
//                    throw e;
//                }
//                env.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
//                return Grids_ChunkNumber.this.getSum(handleOutOfMemoryError);
//            } else {
//                throw e;
//            }
//        }
//    }
//
    /**
     * Returns the sum of all data values.
     *
     * @return
     */
    @Override
    public abstract BigDecimal getSum();

//    /**
//     * Returns the Arithmetic Mean of all data values. If
//     * all cells are NoDataValues, then null is returned.
//     *
//     * @param numberOfDecimalPlaces The number of decimal places to which the
//     * result is precise.
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * cache operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    @Override
//    public BigDecimal getArithmeticMean(
//            int numberOfDecimalPlaces,
//            boolean hoome) {
//        try {
//            BigDecimal result = getArithmeticMean(numberOfDecimalPlaces);
//            env.checkAndMaybeFreeMemory(hoome);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                if (env.cacheChunkExcept_Account(Grid, ChunkID, false) < 1L) {
//                    throw e;
//                }
//                env.initMemoryReserve(Grid, ChunkID, hoome);
//                return getArithmeticMean(numberOfDecimalPlaces, hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
    /**
     * Returns the Arithmetic Mean of all non _NoDataValues as a BigDecimal. If
     * all cells are _NoDataValues, then null is returned.
     *
     * @param dp The number of decimal places to which the result is precise.
     * @return
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

//    /**
//     * Returns the Arithmetic Mean of all data values as a double. If all
//     * cells are NoDataValues, then Grid.NoDataValue is returned.
//     *
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * cache operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    public double getArithmeticMeanDouble(boolean hoome) {
//        try {
//            double result = getArithmeticMeanDouble();
//            env.checkAndMaybeFreeMemory(hoome);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                if (env.cacheChunkExcept_Account(Grid, ChunkID, false) < 1L) {
//                    throw e;
//                }
//                env.initMemoryReserve(Grid, ChunkID, hoome);
//                return getArithmeticMeanDouble(hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
    /**
     * Returns the Arithmetic Mean of all data values as a double. If all cells
     * are NoDataValues, then Grid.NoDataValue is returned.
     *
     * @return
     */
    public double getArithmeticMeanDouble() {
        double result;
        long n = getN();
        double sum = getSum().doubleValue();
        result = sum / (double) n;
        return result;
    }

    /**
     * Returns the median of all data values as a double.
     *
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getMedianDouble(boolean hoome)
            throws IOException, ClassNotFoundException, Exception {
        try {
            double r = getMedianDouble();
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (env.cacheChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                env.initMemoryReserve(Grid, ChunkID, hoome);
                return getMedianDouble(hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the median of all data values as a double. This method requires
     * that all data in chunk can be stored as a new array.
     *
     * @return
     */
    public abstract double getMedianDouble();

    /**
     * Returns the standard deviation of all data values as a double.
     *
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getStandardDeviationDouble(boolean hoome) throws IOException,
            ClassNotFoundException, Exception {
        try {
            double result = getStandardDeviationDouble();
            env.checkAndMaybeFreeMemory(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (env.cacheChunkExcept_Account(Grid, ChunkID, hoome) < 1L) {
                    throw e;
                }
                env.initMemoryReserve(Grid, ChunkID, hoome);
                return getStandardDeviationDouble(hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the standard deviation of all data values as a double.
     *
     * @return
     */
    protected abstract double getStandardDeviationDouble();

}
