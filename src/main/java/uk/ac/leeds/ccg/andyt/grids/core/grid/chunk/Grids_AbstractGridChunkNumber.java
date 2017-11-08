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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_InterfaceStatistics;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridChunkNumber 
        extends Grids_AbstractGridChunk 
        implements Serializable, Grids_InterfaceStatistics {
    
    protected Grids_AbstractGridChunkNumber() {
    }

    protected Grids_AbstractGridChunkNumber(Grids_AbstractGrid g, Grids_2D_ID_int chunkID) {
        super(g, chunkID);
    }

    /**
     * Returns Grid.
     *
     * @return
     */
    @Override
    protected abstract Grids_AbstractGridNumber getGrid();
    
    /**
     * Returns the number of cells with data values.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public Long getN(boolean handleOutOfMemoryError) {
        try {
            long result = getN();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return Grids_AbstractGridChunkNumber.this.getN(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Returns the number of cells with data values.
     *
     * @return
     */
    protected abstract long getN();
    
    /**
     * Returns the sum of all data values.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public BigDecimal getSum(boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getSum();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return Grids_AbstractGridChunkNumber.this.getSum(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the sum of all data values.
     *
     * @return
     */
    protected abstract BigDecimal getSum();
    
    /**
     * Returns the Arithmetic Mean of all data values. If
     * all cells are NoDataValues, then null is returned.
     *
     * @param numberOfDecimalPlaces The number of decimal places to which the
     * result is precise.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public BigDecimal getArithmeticMean(int numberOfDecimalPlaces,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getArithmeticMean(numberOfDecimalPlaces);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getArithmeticMean(numberOfDecimalPlaces, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the Arithmetic Mean of all data values as a BigDecimal. If
     * all cells are NoDataValues, then null is returned.
     *
     * @param numberOfDecimalPlaces The number of decimal places to which the
     * result is precise.
     * @return
     */
    protected abstract BigDecimal getArithmeticMean(int numberOfDecimalPlaces);

    /**
     * Returns the Arithmetic Mean of all data values as a double. If all
     * cells are NoDataValues, then Grid.NoDataValue is returned.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getArithmeticMeanDouble(boolean handleOutOfMemoryError) {
        try {
            double result = getArithmeticMeanDouble();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getArithmeticMeanDouble(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the Arithmetic Mean of all data values as a double calculated using double
     * precision rounding. If all
     * cells are NoDataValues, then Grid.NoDataValue is returned. Using
     * BigDecimal this should be as precise as is possible with doubles.
     *
     * @return
     */
    protected abstract double getArithmeticMeanDouble();

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
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
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
    protected abstract double getMedianDouble();

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
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
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
