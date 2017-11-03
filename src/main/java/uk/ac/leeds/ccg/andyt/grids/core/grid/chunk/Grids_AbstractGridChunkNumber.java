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

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridChunkNumber extends Grids_AbstractGridChunk implements Serializable {
    
    protected Grids_AbstractGridChunkNumber() {
    }

    protected Grids_AbstractGridChunkNumber(Grids_AbstractGrid g, Grids_2D_ID_int chunkID) {
        super(g, chunkID);
    }

    /**
     * For clearing the data associated with this.
     */
    @Override
    protected abstract void clearData();

    /**
     * Returns Grid.
     *
     * @return
     */
    @Override
    protected abstract Grids_AbstractGridNumber getGrid();
    
    /**
     * Returns the number of cells with _NoDataValues as an int.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public int getNonNoDataValueCountInt(boolean handleOutOfMemoryError) {
        try {
            int result = getNonNoDataValueCountInt();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getNonNoDataValueCountInt(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the number of cells with no data values as an int.
     *
     * @return
     */
    protected abstract int getNonNoDataValueCountInt();

    /**
     * Returns the number of cells with no data values as a BigInteger.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public BigInteger getNonNoDataValueCountBigInteger(boolean handleOutOfMemoryError) {
        try {
            BigInteger result = getNonNoDataValueCountBigInteger();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getNonNoDataValueCountBigInteger(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Returns the number of cells with no data values as a BigInteger.
     *
     * @return
     */
    protected abstract BigInteger getNonNoDataValueCountBigInteger();
    
    /**
     * Returns the sum of all non _NoDataValues as a BigDecimal.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public BigDecimal getSumBigDecimal(boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getSumBigDecimal();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getSumBigDecimal(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the sum of all data values as a BigDecimal.
     *
     * @return
     */
    protected abstract BigDecimal getSumBigDecimal();
    
    /**
     * Returns the Arithmetic Mean of all non _NoDataValues as a BigDecimal. If
     * all cells are _NoDataValues, then null is returned.
     *
     * @param numberOfDecimalPlaces The number of decimal places to which the
     * result is precise.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public BigDecimal getArithmeticMeanBigDecimal(int numberOfDecimalPlaces, boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getArithmeticMeanBigDecimal(numberOfDecimalPlaces);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getArithmeticMeanBigDecimal(numberOfDecimalPlaces, handleOutOfMemoryError);
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
    protected abstract BigDecimal getArithmeticMeanBigDecimal(int numberOfDecimalPlaces);

    /**
     * Returns the Arithmetic Mean of all data values as a double. If all
     * cells are _NoDataValues, then Grid.NoDataValue is returned.
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
     * Returns the Arithmetic Mean of all data values as a double. If all
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
