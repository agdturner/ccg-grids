/*
 * Copyright (C) 2017 geoagdt.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.andyt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_InterfaceStats;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridChunkNumber 
        extends Grids_AbstractGridChunk 
        implements Serializable, Grids_InterfaceStats {
    
    protected Grids_AbstractGridChunkNumber() {
    }

    protected Grids_AbstractGridChunkNumber(Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        super(g, chunkID);
    }

    /**
     * Returns Grid.
     *
     * @return
     */
    @Override
    public abstract Grids_AbstractGridNumber getGrid();
    
    /**
     * Returns the value at row, col.
     *
     * @param row the row of the cell w.r.t. the origin of this chunk.
     * @param col the column of the cell w.r.t. the origin of this chunk.
     * @return
     */
    public abstract double getCellDouble(int row, int col);
    
//    /**
//     * Returns the number of cells with data values.
//     *
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * swap operations are initiated, then the method is re-called. If false
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
//                if (env.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
//                    throw e;
//                }
//                env.initMemoryReserve(Grid, ChunkID, hoome);
//                return Grids_AbstractGridChunkNumber.this.getN(hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
//    
    /**
     * Returns the number of cells with data values.
     *
     * @return
     */
    @Override
    public abstract Long getN();
    
//    /**
//     * Returns the sum of all data values.
//     *
//     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
//     * swap operations are initiated, then the method is re-called. If false
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
//                if (env.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
//                    throw e;
//                }
//                env.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
//                return Grids_AbstractGridChunkNumber.this.getSum(handleOutOfMemoryError);
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
//     * swap operations are initiated, then the method is re-called. If false
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
//                if (env.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
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
     * @param numberOfDecimalPlaces The number of decimal places to which the
     * result is precise.
     * @return
     */
    @Override
    public BigDecimal getArithmeticMean(int numberOfDecimalPlaces) {
        BigDecimal sum = getSum();
        long n = getN();
        BigInteger n2 = BigInteger.valueOf(n);
        if (n != 0) {
            return Math_BigDecimal.divideRoundIfNecessary(sum, n2,
                    numberOfDecimalPlaces, RoundingMode.HALF_EVEN);
        }
        return null;
    }

//    /**
//     * Returns the Arithmetic Mean of all data values as a double. If all
//     * cells are NoDataValues, then Grid.NoDataValue is returned.
//     *
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * swap operations are initiated, then the method is re-called. If false
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
//                if (env.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
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
     * Returns the Arithmetic Mean of all data values as a double. If all
     * cells are NoDataValues, then Grid.NoDataValue is returned. 
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
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getMedianDouble(boolean handleOutOfMemoryError) {
        try {
            double result = getMedianDouble();
            env.checkAndMaybeFreeMemory(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clearMemoryReserve();
                if (env.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                env.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getMedianDouble(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the median of all data values as a double. This method
     * requires that all data in chunk can be stored as a new array.
     *
     * @return
     */
    public abstract double getMedianDouble();

    /**
     * Returns the standard deviation of all data values as a double.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getStandardDeviationDouble(boolean handleOutOfMemoryError) {
        try {
            double result = getStandardDeviationDouble();
            env.checkAndMaybeFreeMemory(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                env.clearMemoryReserve();
                if (env.swapChunkExcept_Account(Grid, ChunkID, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                env.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getStandardDeviationDouble(handleOutOfMemoryError);
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
