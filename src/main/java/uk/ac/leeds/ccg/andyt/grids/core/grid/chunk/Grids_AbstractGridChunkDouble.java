/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import gnu.trove.TDoubleHashSet;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;

/**
 * Provides general methods and controls what methods extended classes must
 * implement acting as an interface.
 */
public abstract class Grids_AbstractGridChunkDouble
        extends Grids_AbstractGridChunk
        implements Serializable {
    //implements Serializable, GridDoubleStatisticsInterface {

    //private static final long serialVersionUID = 1L;
    protected Grids_AbstractGridChunkDouble() {
    }

    protected Grids_AbstractGridChunkDouble(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID) {
        super(g, chunkID);
    }

    /**
     *
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return (Grids_GridDouble) Grid.
     */
    @Override
    public Grids_GridDouble getGrid(
            boolean handleOutOfMemoryError) {
        try {
            Grids_GridDouble result = getGrid();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(
                        Grid,
                        ChunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid,
                        ChunkID,
                        handleOutOfMemoryError);
                return getGrid(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return (Grids_GridDouble) Grid;
     */
    @Override
    protected final Grids_GridDouble getGrid() {
        return (Grids_GridDouble) Grid;
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex, as a double.
     *
     * @param chunkRow the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCol the column index of the cell w.r.t. the origin of this
     * chunk
     * @param noDataValue Grid.NoDataValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getCell(
            int chunkRow,
            int chunkCol,
            double noDataValue,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCell(
                    chunkRow,
                    chunkCol,
                    noDataValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getCell(
                        chunkRow,
                        chunkCol,
                        noDataValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex, as a double.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param noDataValue
     * @return
     */
    protected abstract double getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double noDataValue);

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex, as a BigDecimal. If the value is
     * Grid.NoDataValue then null is returned!
     *
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of
     * this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin
     * of this chunk.
     * @param noDataValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public BigDecimal getCellBigDecimal(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double noDataValue,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getCellBigDecimal(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    noDataValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(
                        Grid,
                        ChunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid,
                        ChunkID,
                        handleOutOfMemoryError);
                return getCellBigDecimal(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex, as a BigDecimal. If the value is
     * Grid.NoDataValue then null is returned!
     *
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of
     * this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin
     * of this chunk.
     * @param noDataValue The Grid.NoDataValue.
     */
    private BigDecimal getCellBigDecimal(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double noDataValue) {
        double value = getCell(
                chunkCellRowIndex,
                chunkCellColIndex,
                noDataValue);
        try {
            return new BigDecimal(value);
        } catch (ArithmeticException ae0) {
            return null;
        }
    }

    /**
     * Initialises the value at position given by: chunk cell row
     * chunkCellRowIndex; chunk cell column chunkCellColIndex. Utility method
     * for constructors of extending classes.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double valueToInitialise,
            boolean handleOutOfMemoryError) {
        try {
            initCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    valueToInitialise);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(
                        Grid,
                        ChunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid,
                        ChunkID,
                        handleOutOfMemoryError);
                initCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        valueToInitialise,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises the value at position given by: chunkRow, chunkCol.
     *
     * @param chunkRow the row of the chunk.
     * @param chunkCol the column of the chunk.
     * @param valueToInitialise the value to initialise the cell with.
     */
    protected abstract void initCell(
            int chunkRow,
            int chunkCol,
            double valueToInitialise);

    /**
     * Returns the value at position given by: chunkRow, chunkCol. sets it to
     * valueToSet
     *
     * @param chunkRow the row of the chunk.
     * @param chunkCol the column of the chunk.
     * @param valueToSet the value the cell is to be set to.
     * @param noDataValue
     * @param handleOutOfMemoryError If true then if OutOfMemoryError is thrown
     * then there is an attempt to manage memory. If false then if an
     * OutOfMemoryError is thrown then there is no attempt to manage memory and
     * the method throws the OutOfMemoryError.
     * @return
     */
    public double setCell(
            int chunkRow,
            int chunkCol,
            double valueToSet,
            double noDataValue,
            boolean handleOutOfMemoryError) {
        try {
            double result = setCell(
                    chunkRow,
                    chunkCol,
                    valueToSet,
                    noDataValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(
                        Grid,
                        ChunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid,
                        ChunkID,
                        handleOutOfMemoryError);
                return setCell(
                        chunkRow,
                        chunkCol,
                        valueToSet,
                        noDataValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the value at position given by: chunkRow, chunkCol and sets it to
     * valueToSet. The noDataValue is passed in for convenience.
     *
     * @param chunkRow the row of the chunk.
     * @param chunkCol the column of the chunk.
     * @param valueToSet the value the cell is to be set to
     * @param noDataValue the Grid.NoDataValue.
     * @return
     */
    protected abstract double setCell(
            int chunkRow,
            int chunkCol,
            double valueToSet,
            double noDataValue);

    /**
     * For clearing the data associated with this.
     */
    protected @Override
    abstract void clearData();

    /**
     * For initialising the data associated with this.
     */
    protected @Override
    abstract void initData();

    /**
     * Returns all the values in row major order as a double[].
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double[] toArrayIncludingNoDataValues(
            boolean handleOutOfMemoryError) {
        try {
            double[] result = toArrayIncludingNoDataValues();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(
                        Grid,
                        ChunkID,
                        false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid,
                        ChunkID,
                        handleOutOfMemoryError);
                return toArrayIncludingNoDataValues(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns all the values in row major order as a double[].
     *
     * @return
     */
    protected double[] toArrayIncludingNoDataValues() {
        boolean handleOutOfMemoryError = false;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int ncols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        double[] array;
        if (((long) nrows * (long) ncols) > Integer.MAX_VALUE) {
            //throw new PrecisionExcpetion
            System.out.println(
                    "PrecisionException in " + getClass().getName()
                    + ".toArray()!");
            System.out.println(
                    "Warning! The returned array size is only "
                    + Integer.MAX_VALUE + " instead of "
                    + ((long) nrows * (long) ncols));
        }
        double _NoDataValue = getGrid().getNoDataValue(handleOutOfMemoryError);
        array = new double[nrows * ncols];
        int row;
        int col;
        int count = 0;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                array[count] = getCell(
                        row,
                        col,
                        _NoDataValue);
                count++;
            }
        }
        return array;
    }

    /**
     * Returns all the values (not including _NoDataVAlues) in row major order
     * as a double[].
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double[] toArrayNotIncludingNoDataValues(
            boolean handleOutOfMemoryError) {
        try {
            double[] result = toArrayNotIncludingNoDataValues();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return toArrayNotIncludingNoDataValues(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns all the values in row major order as a double[].
     *
     * @return
     */
    protected double[] toArrayNotIncludingNoDataValues() {
        boolean handleOutOfMemoryError = false;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int ncols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        double noDataValue = g.getNoDataValue(handleOutOfMemoryError);
        double[] array = new double[getNonNoDataValueCountInt()];
        int row;
        int col;
        int count = 0;
        double value;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(
                        row,
                        col,
                        noDataValue);
                if (value != noDataValue) {
                    array[count] = value;
                    count++;
                }
            }
        }
        return array;
    }

    /**
     * Returns the number of cells with _NoDataValues as a BigInteger.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public BigInteger getNonNoDataValueCountBigInteger(
            boolean handleOutOfMemoryError) {
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
     * Returns the number of cells with _NoDataValues as a BigInteger.
     *
     * @return
     */
    protected BigInteger getNonNoDataValueCountBigInteger() {
        boolean handleOutOfMemoryError = false;
        BigInteger nonNoDataValueCount = BigInteger.ZERO;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int ncols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        double _NoDataValue = g.getNoDataValue(false);
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                if (getCell(row, col, _NoDataValue) != _NoDataValue) {
                    nonNoDataValueCount = nonNoDataValueCount.add(BigInteger.ONE);
                }
            }
        }
        return nonNoDataValueCount;
    }

    /**
     * Returns the number of cells with _NoDataValues as an int.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public int getNonNoDataValueCountInt(
            boolean handleOutOfMemoryError) {
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
     * Returns the number of cells with _NoDataValues as an int.
     *
     * @return
     */
    protected int getNonNoDataValueCountInt() {
        return getNonNoDataValueCountBigInteger().intValue();
    }

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
     * Returns the sum of all non _NoDataValues as a BigDecimal.
     *
     * @return
     */
    protected BigDecimal getSumBigDecimal() {
        boolean handleOutOfMemoryError = false;
        BigDecimal sum = BigDecimal.ZERO;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int ncols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        double noDataValue = g.getNoDataValue(false);
        double value;
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(row, col, noDataValue);
                if (value != noDataValue) {
                    sum = sum.add(new BigDecimal(value));
                }
            }
        }
        return sum;
    }

    /**
     * Returns the minimum of all non _NoDataValues as a double.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getMinDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getMinDouble();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getMinDouble(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the minimum of all non _NoDataValues as a double.
     *
     * @return
     */
    protected double getMinDouble() {
        boolean handleOutOfMemoryError = false;
        double min = Double.POSITIVE_INFINITY;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int ncols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        double noDataValue = g.getNoDataValue(false);
        double value;
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(
                        row,
                        col,
                        noDataValue);
                if (value != noDataValue) {
                    min = Math.min(
                            min,
                            value);
                }
            }
        }
        return min;
    }

    /**
     * Returns the maximum of all non _NoDataValues as a double.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getMaxDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getMaxDouble();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getMaxDouble(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the maximum of all non _NoDataValues as a double.
     *
     * @return
     */
    protected double getMaxDouble() {
        boolean handleOutOfMemoryError = false;
        double max = Double.NEGATIVE_INFINITY;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int ncols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        double noDataValue = g.getNoDataValue(handleOutOfMemoryError);
        double value;
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(row, col, noDataValue);
                if (value != noDataValue) {
                    max = Math.max(max, value);
                }
            }
        }
        return max;
    }

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
    public BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces,
            boolean handleOutOfMemoryError) {
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
                return getArithmeticMeanBigDecimal(
                        numberOfDecimalPlaces,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the Arithmetic Mean of all non _NoDataValues as a BigDecimal. If
     * all cells are _NoDataValues, then null is returned.
     *
     * @param numberOfDecimalPlaces The number of decimal places to which the
     * result is precise.
     * @return
     */
    protected BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces) {
        boolean handleOutOfMemoryError = false;
        BigDecimal mean = BigDecimal.ZERO;
        BigDecimal count = BigDecimal.ZERO;
        BigDecimal one = BigDecimal.ONE;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int ncols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        double _NoDataValue = g.getNoDataValue(handleOutOfMemoryError);
        double value;
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(row, col, _NoDataValue);
                if (value != _NoDataValue) {
                    mean = mean.add(new BigDecimal(value));
                    count = count.add(one);
                }
            }
        }
        if (count.compareTo(BigDecimal.ZERO) != 0) {
            return mean.divide(
                    count,
                    numberOfDecimalPlaces,
                    BigDecimal.ROUND_HALF_EVEN);
        }
        return null;
    }

    /**
     * Returns the Arithmetic Mean of all non _NoDataValues as a double. If all
     * cells are _NoDataValues, then Grid.NoDataValue is returned.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getArithmeticMeanDouble(
            boolean handleOutOfMemoryError) {
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
     * Returns the Arithmetic Mean of all non _NoDataValues as a double. If all
     * cells are _NoDataValues, then Grid.NoDataValue is returned. Using
     * BigDecimal this should be as precise as possible with doubles.
     *
     * @return
     */
    protected double getArithmeticMeanDouble() {
        BigDecimal arithmeticMeanBigDecimal = getArithmeticMeanBigDecimal(324); //Why 324???
        try {
            return arithmeticMeanBigDecimal.doubleValue();
        } catch (NullPointerException npe0) {
            return getGrid().getNoDataValue(false);
        }
    }

    /**
     * Returns the mode of all non _NoDataValues as a TDoubleHashSet.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public TDoubleHashSet getModeTDoubleHashSet(
            boolean handleOutOfMemoryError) {
        try {
            TDoubleHashSet result = getModeTDoubleHashSet();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getModeTDoubleHashSet(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the mode of all non _NoDataValues as a TDoubleHashSet. TODO:
     * Better to use toArray and go through a sorted version?
     *
     * @return
     */
    protected TDoubleHashSet getModeTDoubleHashSet() {
        boolean handleOutOfMemoryError = false;
        TDoubleHashSet mode = new TDoubleHashSet();
        BigInteger nonNoDataValueCountBigInteger
                = getNonNoDataValueCountBigInteger();
        if (nonNoDataValueCountBigInteger.compareTo(BigInteger.ZERO) == 1) {
            //TDoubleObjectHashMap modes = new TDoubleObjectHashMap();
            Grids_GridDouble g = getGrid();
            int nrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
            int ncols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
            double _noDataValue = g.getNoDataValue(false);
            boolean calculated = false;
            int row = 0;
            int col = 0;
            int p;
            int q;
            Object[] tmode = initMode(nrows, ncols, _noDataValue);
            if (tmode[0] == null) {
                return mode;
            } else {
                double value;
                long count;
                long modeCount = (Long) tmode[0];
                mode.add((Double) tmode[1]);
                Grids_2D_ID_int chunkCellID = (Grids_2D_ID_int) tmode[2];
                // Do remainder of the row
                p = chunkCellID.getRow();
                for (q = chunkCellID.getCol() + 1; q < ncols; q++) {
                    value = getCell(p, q, _noDataValue);
                    if (value != _noDataValue) {
                        count = count(p, q, nrows, ncols, value);
                        if (count > modeCount) {
                            mode.clear();
                            mode.add(value);
                            modeCount = count;
                        } else {
                            if (count == modeCount) {
                                mode.add(value);
                            }
                        }
                    }
                }
                // Do remainder of the grid
                for (p++; p < nrows; p++) {
                    for (q = 0; q < ncols; q++) {
                        value = getCell(p, q, _noDataValue);
                        if (value != _noDataValue) {
                            count = count(p, q, nrows, ncols, value);
                            if (count > modeCount) {
                                mode.clear();
                                mode.add(value);
                                modeCount = count;
                            } else {
                                if (count == modeCount) {
                                    mode.add(value);
                                }
                            }
                        }
                    }
                }
            }
        }
        return mode;
    }

    /**
     * Initialises the mode.
     *
     * @see getModeTDoubleHashSet()
     */
    private Object[] initMode(
            int nrows,
            int ncols,
            double noDataValue) {
        Object[] initMode = new Object[3];
        long modeCount;
        int p;
        int q;
        int row;
        int col;
        double value;
        double thisValue;
        for (p = 0; p < nrows; p++) {
            for (q = 0; q < ncols; q++) {
                value = getCell(p, q, noDataValue);
                if (value != noDataValue) {
                    modeCount = 0L;
                    for (row = 0; row < nrows; row++) {
                        for (col = 0; col < ncols; col++) {
                            thisValue = getCell(row, col, noDataValue);
                            if (thisValue == value) {
                                modeCount++;
                            }
                        }
                    }
                    initMode[0] = modeCount;
                    initMode[1] = value;
                    initMode[2] = new Grids_2D_ID_int(p, q);
                    return initMode;
                }
            }
        }
        return initMode;
    }

    /**
     * TODO: docs
     *
     * @param p the row index of the cell from which counting starts
     * @param q the column index of the cell from which counting starts
     * @param nrows
     * @param ncols
     * @param value the value to be counted
     */
    private long count(
            int p,
            int q,
            int nrows,
            int ncols,
            double value) {
        long count = 1L;
        double thisValue;
        double noDataValue = getGrid().getNoDataValue(false);
        // Do remainder of the row
        for (q++; q < ncols; q++) {
            thisValue = getCell(p, q, noDataValue);
            if (thisValue == value) {
                count++;
            }
        }
        // Do remainder of the grid
        for (p++; p < nrows; p++) {
            for (q = 0; q < ncols; q++) {
                thisValue = getCell(p, q, noDataValue);
                if (thisValue == value) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns the median of all non _NoDataValues as a double.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getMedianDouble(
            boolean handleOutOfMemoryError) {
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
     * Returns the median of all non _NoDataValues as a double. This method
     * requires that all data in chunk can be stored as a new array.
     *
     * @return
     */
    protected double getMedianDouble() {
        Grids_GridDouble g = getGrid();
        int scale = 20;
        double median = g.getNoDataValue(false);
        BigInteger nonNoDataValueCountBigInteger
                = getNonNoDataValueCountBigInteger();
        if (nonNoDataValueCountBigInteger.compareTo(BigInteger.ZERO) == 1) {
            double[] array = toArrayNotIncludingNoDataValues();
            sort1(array, 0, array.length);
            BigInteger[] nonNoDataValueCountBigIntegerDivideAndRemainder2
                    = nonNoDataValueCountBigInteger.divideAndRemainder(new BigInteger("2"));
            if (nonNoDataValueCountBigIntegerDivideAndRemainder2[1].compareTo(BigInteger.ZERO) == 0) {
                int index = nonNoDataValueCountBigIntegerDivideAndRemainder2[0].intValue();
                //median = array[ index ];
                //median += array[ index - 1 ];
                //median /= 2.0d;
                //return median;
                BigDecimal medianBigDecimal = new BigDecimal(array[index - 1]);
                return (medianBigDecimal.add(new BigDecimal(array[index]))).divide(new BigDecimal(2.0d), scale, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                //return ( medianBigDecimal.add( new BigDecimal( array[ index ] ) ) ).divide( new BigDecimal( 2.0d ), scale, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
            } else {
                int index = nonNoDataValueCountBigIntegerDivideAndRemainder2[0].intValue();
                return array[index];
            }
        } else {
            return median;
        }
    }

    /**
     * Sorts the specified sub-array of doubles into ascending order. Source
     * copied from java.util.Arrays and method changed so not static for
     * performance reasons.
     *
     * @param x
     * @param len
     * @param off
     */
    protected void sort1(double x[], int off, int len) {
        // Insertion sort on smallest arrays
        if (len < 7) {
            for (int i = off; i < len + off; i++) {
                for (int j = i; j > off && x[j - 1] > x[j]; j--) {
                    swap(x, j, j - 1);
                }
            }
            return;
        }

        // Choose a partition element, v
        int m = off + (len >> 1);       // Small arrays, middle element
        if (len > 7) {
            int l = off;
            int n = off + len - 1;
            if (len > 40) {        // Big arrays, pseudomedian of 9
                int s = len / 8;
                l = med3(x, l, l + s, l + 2 * s);
                m = med3(x, m - s, m, m + s);
                n = med3(x, n - 2 * s, n - s, n);
            }
            m = med3(x, l, m, n); // Mid-size, med of 3
        }
        double v = x[m];

        // Establish Invariant: v* (<v)* (>v)* v*
        int a = off, b = a, c = off + len - 1, d = c;
        while (true) {
            while (b <= c && x[b] <= v) {
                if (x[b] == v) {
                    swap(x, a++, b);
                }
                b++;
            }
            while (c >= b && x[c] >= v) {
                if (x[c] == v) {
                    swap(x, c, d--);
                }
                c--;
            }
            if (b > c) {
                break;
            }
            swap(x, b++, c--);
        }

        // Swap partition elements back to middle
        int s, n = off + len;
        s = Math.min(a - off, b - a);
        vecswap(x, off, b - s, s);
        s = Math.min(d - c, n - d - 1);
        vecswap(x, b, n - s, s);

        // Recursively sort non-partition-elements
        if ((s = b - a) > 1) {
            sort1(x, off, s);
        }
        if ((s = d - c) > 1) {
            sort1(x, n - s, s);
        }
    }

    /**
     * Swaps x[a] with x[b]. Source copied from java.util.Arrays and method
     * changed so not static for performance reasons.
     */
    private void swap(double x[], int a, int b) {
        double t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    /**
     * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)]. Source copied from
     * java.util.Arrays and method changed so not static for performance
     * reasons.
     */
    private void vecswap(double x[], int a, int b, int n) {
        for (int i = 0; i < n; i++, a++, b++) {
            swap(x, a, b);
        }
    }

    /**
     * Returns the index of the median of the three indexed doubles. Source
     * copied from java.util.Arrays and method changed so not static for
     * performance reasons.
     */
    private int med3(double x[], int a, int b, int c) {
        return (x[a] < x[b]
                ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a)
                : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
    }

    /**
     * Returns the standard deviation of all non _NoDataValues as a double.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getStandardDeviationDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getStandardDeviationDouble();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(
                        Grid,
                        ChunkID,
                        handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid,
                        ChunkID,
                        handleOutOfMemoryError);
                return getStandardDeviationDouble(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the standard deviation of all non _NoDataValues as a double.
     *
     * @return
     */
    protected double getStandardDeviationDouble() {
        boolean handleOutOfMemoryError = false;
        double standardDeviation = 0.0d;
        double mean = getArithmeticMeanDouble();
        Grids_GridDouble g
                = getGrid();
        int nrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int ncols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        double noDataValue = g.getNoDataValue(handleOutOfMemoryError);
        double value;
        double count = 0.0d;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                value = getCell(row, col, noDataValue);
                if (value != noDataValue) {
                    standardDeviation
                            += (value - mean)
                            * (value - mean);
                    count += 1.0d;
                }
            }
        }
        if ((count - 1.0d) > 0.0d) {
            return Math.sqrt(standardDeviation / (count - 1.0d));
        } else {
            return standardDeviation;
        }
    }

}
