/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_Grid2DSquareCellInt;
import gnu.trove.TIntHashSet;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;

/**
 * Provides general methods and controls what methods extended classes must
 * implement acting as an interface.
 */
public abstract class Grids_AbstractGrid2DSquareCellIntChunk
        extends Grids_AbstractGridChunk
        implements Serializable {
    //implements Serializable, GridIntStatisticsInterface {

    private static final long serialVersionUID = 1L;

    /**
     *
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return ( Grids_Grid2DSquareCellInt ) this._Grid2DSquareCell.
     */
    public Grids_Grid2DSquareCellInt getGrid2DSquareCellInt(
            boolean handleOutOfMemoryError) {
        try {
            Grids_Grid2DSquareCellInt result = getGrid2DSquareCellInt();
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getGrid2DSquareCellInt(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     *
     *
     * @return ( Grids_Grid2DSquareCellInt ) this._Grid2DSquareCell.
     */
    protected Grids_Grid2DSquareCellInt getGrid2DSquareCellInt() {
        return (Grids_Grid2DSquareCellInt) this.getGrid();
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex, as a double.
     *
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of
     * this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin
     * of this chunk.
     * @param noDataValue The noDataValue of this.grid2DSquareCellDouble.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    protected int getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int noDataValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    noDataValue);
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getCell(
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
     * chunk cell row chunkCellColIndex, as a double.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param _NoDataValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param chunkID This is a Grids_2D_ID_int for those
     * AbstractGrid2DSquareCells not to be swapped if possible when an
     * OutOfMemoryError is encountered.
     * @return
     */
    public int getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int _NoDataValue,
            boolean handleOutOfMemoryError,
            Grids_2D_ID_int chunkID) {
        try {
            int result = getCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    _NoDataValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(chunkID, false) == null) {
                    this.getGrid().ge.swapToFile_Grid2DSquareCellChunk_AccountDetail(false);
                }
                this.getGrid().ge.init_MemoryReserve(this.ChunkID,
                        handleOutOfMemoryError);
                return getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        _NoDataValue,
                        handleOutOfMemoryError,
                        chunkID);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell col chunkCellColIndex as a int.
     *
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin
     * of this chunk
     * @param noDataValue The noDataValue of this.grid2DSquareCellInt
     * @return
     */
    protected abstract int getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int noDataValue);

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell col chunkCellColIndex as a double.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param noDataValue the noDataValue of this.grid2DSquareCellInt
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown. TODO: Ensure the int can be
     * represented exactly as a double. If not throw Exception of some kind.
     * @return
     */
    public double getCellDouble(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int noDataValue,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    noDataValue);
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getCellDouble(
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
     * chunk cell col chunkCellColIndex as a double.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param noDataValue the noDataValue of this.grid2DSquareCellInt TODO:
     * Ensure the int can be represented exactly as a double. If not throw
     * Exception of some kind.
     */
    private double getCellDouble(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int noDataValue) {
        return (double) getCell(
                chunkCellRowIndex,
                chunkCellColIndex,
                noDataValue);
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
            int valueToInitialise,
            boolean handleOutOfMemoryError) {
        try {
            initCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    valueToInitialise);
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
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
     * Initialises the value at position given by: chunk cell row
     * chunkCellRowIndex; chunk cell column chunkCellColIndex. Utility method
     * for constructors of extending classes.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     */
    protected abstract void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int valueToInitialise);

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex and sets it to valueToSet
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param valueToSet the value the cell is to be set to
     * @param noDataValue the noDataValue of this.grid2DSquareCellDouble
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public int setCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int valueToSet,
            int noDataValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    valueToSet,
                    noDataValue);
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return setCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        valueToSet,
                        noDataValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex and sets it to valueToSet
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param valueToSet the value the cell is to be set to
     * @param noDataValue the noDataValue of this.grid2DSquareCellDouble
     * @return
     */
    protected abstract int setCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int valueToSet,
            int noDataValue);

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
     * Returns values in row major order as an int[].
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public int[] toArrayIncludingNoDataValues(
            boolean handleOutOfMemoryError) {
        try {
            int[] result = toArrayIncludingNoDataValues();
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return toArrayIncludingNoDataValues(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns values in row major order as an int[].
     *
     * @return
     */
    protected int[] toArrayIncludingNoDataValues() {
        Grids_Grid2DSquareCellInt grid2DSquareCellInt = getGrid2DSquareCellInt();
        int chunkNrows = grid2DSquareCellInt.getChunkNRows();
        int chunkNcols = grid2DSquareCellInt.getChunkNCols();
        long nChunkCells
                = (long) chunkNrows
                * (long) chunkNcols;
        int[] array;
        if (nChunkCells > Integer.MAX_VALUE) {
            //throw new PrecisionExcpetion
            System.out.println(
                    "PrecisionException in "
                    + this.getClass().getName() + ".toArray()!");
            System.out.println(
                    "Warning! The returned array size is only "
                    + Integer.MAX_VALUE + " instead of " + nChunkCells);
        }
        int noDataValue = getGrid2DSquareCellInt().getNoDataValue(false);
        array = new int[chunkNrows * chunkNcols];
        int row;
        int col;
        int count = 0;
        boolean handleOutOfMemoryError = true;
        for (row = 0; row < chunkNrows; row++) {
            for (col = 0; col < chunkNcols; col++) {
                array[count] = getCell(
                        row,
                        col,
                        noDataValue,
                        handleOutOfMemoryError);
                count++;
            }
        }
        return array;
    }

    /**
     * Returns values (except those that are noDataValues) in row major order as
     * an int[].
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public int[] toArrayNotIncludingNoDataValues(
            boolean handleOutOfMemoryError) {
        try {
            int[] result = toArrayNotIncludingNoDataValues();
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return toArrayNotIncludingNoDataValues(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns values (except those that are noDataValues) in row major order as
     * an int[].
     *
     * @return
     */
    protected int[] toArrayNotIncludingNoDataValues() {
        Grids_Grid2DSquareCellInt grid2DSquareCellInt = getGrid2DSquareCellInt();
        int chunkNrows = grid2DSquareCellInt.getChunkNRows();
        int chunkNcols = grid2DSquareCellInt.getChunkNCols();
        int noDataValue = grid2DSquareCellInt.getNoDataValue(false);
        int[] array = new int[getNonNoDataValueCountInt()];
        int row;
        int col;
        int count = 0;
        int value;
        boolean handleOutOfMemoryError = true;
        for (row = 0; row < chunkNrows; row++) {
            for (col = 0; col < chunkNcols; col++) {
                value = getCell(
                        row,
                        col,
                        noDataValue,
                        handleOutOfMemoryError);
                if (value != noDataValue) {
                    array[count] = value;
                    count++;
                }
            }
        }
        return array;
    }

    /**
     * For returning the number of cells with noDataValues as a BigInteger.
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
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getNonNoDataValueCountBigInteger(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the number of cells with noDataValues as a BigInteger.
     *
     * @return
     */
    protected BigInteger getNonNoDataValueCountBigInteger() {
        BigInteger nonNoDataValueCount = BigInteger.ZERO;
        Grids_Grid2DSquareCellInt grid2DSquareCellInt = getGrid2DSquareCellInt();
        int nrows = grid2DSquareCellInt.getChunkNRows();
        int ncols = grid2DSquareCellInt.getChunkNCols();
        int noDataValue = grid2DSquareCellInt.getNoDataValue(false);
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                if (getCell(row, col, noDataValue) != noDataValue) {
                    nonNoDataValueCount
                            = nonNoDataValueCount.add(BigInteger.ONE);
                }
            }
        }
        return nonNoDataValueCount;
    }

    /**
     * Returns the number of cells with noDataValues as an int.
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
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getNonNoDataValueCountInt(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the number of cells with noDataValues as an int.
     *
     * @return
     */
    protected int getNonNoDataValueCountInt() {
        int nonNoDataCount = 0;
        Grids_Grid2DSquareCellInt grid2DSquareCellInt
                = (Grids_Grid2DSquareCellInt) getGrid2DSquareCell();
        int nrows = grid2DSquareCellInt.getChunkNRows();
        int ncols = grid2DSquareCellInt.getChunkNCols();
        int noDataValue = grid2DSquareCellInt.getNoDataValue(false);
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                if (getCell(row, col, noDataValue) != noDataValue) {
                    nonNoDataCount++;
                }
            }
        }
        return nonNoDataCount;
    }

    /**
     * For returning the sum of all non noDataValues as a BigDecimal.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public BigDecimal getSumBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getSumBigDecimal();
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getSumBigDecimal(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a BigDecimal.
     *
     * @return
     */
    protected BigDecimal getSumBigDecimal() {
        BigDecimal sum = new BigDecimal(0.0d);
        Grids_Grid2DSquareCellInt grid2DSquareCellInt
                = (Grids_Grid2DSquareCellInt) getGrid2DSquareCell();
        int nrows = grid2DSquareCellInt.getChunkNRows();
        int ncols = grid2DSquareCellInt.getChunkNCols();
        int noDataValue = grid2DSquareCellInt.getNoDataValue(false);
        int value;
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
     * For returning the minimum of all non noDataValues as a int.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public int getMinInt(
            boolean handleOutOfMemoryError) {
        try {
            int result = getMinInt();
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getMinInt(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a int
     *
     * @return
     */
    protected int getMinInt() {
        int min = Integer.MAX_VALUE;
        Grids_Grid2DSquareCellInt grid2DSquareCellInt
                = (Grids_Grid2DSquareCellInt) getGrid2DSquareCell();
        int nrows = grid2DSquareCellInt.getChunkNRows();
        int ncols = grid2DSquareCellInt.getChunkNCols();
        int noDataValue = grid2DSquareCellInt.getNoDataValue(false);
        int value;
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(row, col, noDataValue);
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
     * For returning the maximum of all non noDataValues as a double.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public int getMaxInt(
            boolean handleOutOfMemoryError) {
        try {
            int result = getMaxInt();
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getMaxInt(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the maximum of all non noDataValues as a double.
     *
     * @return
     */
    protected int getMaxInt() {
        int max = Integer.MIN_VALUE;
        Grids_Grid2DSquareCellInt grid2DSquareCellInt
                = (Grids_Grid2DSquareCellInt) getGrid2DSquareCell();
        int nrows = grid2DSquareCellInt.getChunkNRows();
        int ncols = grid2DSquareCellInt.getChunkNCols();
        int noDataValue = grid2DSquareCellInt.getNoDataValue(false);
        int value;
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
     * Returns the Arithmetic Mean of all non noDataValues as a BigDecimal. If
     * all cells are noDataValues, then null is returned.
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
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError0) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw _OutOfMemoryError0;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getArithmeticMeanBigDecimal(
                        numberOfDecimalPlaces,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError0;
            }
        }
    }

    /**
     * Returns the Arithmetic Mean of all non noDataValues as a BigDecimal. If
     * all cells are noDataValues, then null is returned.
     *
     * @param numberOfDecimalPlaces The number of decimal places to which the
     * result is precise.
     * @return
     */
    protected BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces) {
        BigDecimal mean = BigDecimal.ZERO;
        BigDecimal count = BigDecimal.ZERO;
        BigDecimal one = BigDecimal.ONE;
        Grids_Grid2DSquareCellInt grid2DSquareCellInt
                = getGrid2DSquareCellInt();
        int nrows = grid2DSquareCellInt.getChunkNRows();
        int ncols = grid2DSquareCellInt.getChunkNCols();
        int noDataValue = Integer.MIN_VALUE;
        int value;
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(row, col, noDataValue);
                if (value != noDataValue) {
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
     * For returning the Arithmetic Mean of all non noDataValues as a double.
     * Using BigDecimal this should be as precise as possible with doubles.
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
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getArithmeticMeanDouble(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the Arithmetic Mean of all non noDataValues as a double.
     * Using BigDecimal this should be as precise as possible with doubles.
     *
     * @return
     */
    protected double getArithmeticMeanDouble() {
        BigDecimal mean = new BigDecimal(0.0d);
        BigDecimal count = new BigDecimal(0.0d);
        BigDecimal one = new BigDecimal(1.0d);
        Grids_Grid2DSquareCellInt grid2DSquareCellInt
                = (Grids_Grid2DSquareCellInt) getGrid2DSquareCell();
        int nrows = grid2DSquareCellInt.getChunkNRows();
        int ncols = grid2DSquareCellInt.getChunkNCols();
        int noDataValue = grid2DSquareCellInt.getNoDataValue(false);
        int value;
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(row, col, noDataValue);
                if (value != noDataValue) {
                    mean = mean.add(new BigDecimal(value));
                    count = count.add(one);
                }
            }
        }
        if (count.compareTo(new BigDecimal(0.0d)) == 1) {
            BigDecimal bigDecimal0 = mean.divide(
                    count,
                    325,
                    BigDecimal.ROUND_HALF_EVEN);
            return bigDecimal0.doubleValue();
            //return mean.divide( count, mean.scale() + 10, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
        } else {
            return noDataValue;
        }
    }

    /**
     * For returning the mode of all non noDataValues as a TDoubleHashSet.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public TIntHashSet getModeTIntHashSet(
            boolean handleOutOfMemoryError) {
        try {
            TIntHashSet result = getModeTIntHashSet();
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getModeTIntHashSet(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the mode of all non noDataValues as a TDoubleHashSet. TODO:
     * Better to use toArray and go through a sorted version?
     *
     * @return
     */
    protected TIntHashSet getModeTIntHashSet() {
        TIntHashSet mode = new TIntHashSet();
        BigInteger nonNoDataValueCount = getNonNoDataValueCountBigInteger();
        if (nonNoDataValueCount.compareTo(BigInteger.ZERO) == 1) {
            //TDoubleObjectHashMap modes = new TDoubleObjectHashMap();
            Grids_Grid2DSquareCellInt grid2DSquareCellInt
                    = (Grids_Grid2DSquareCellInt) getGrid2DSquareCell();
            int nrows = grid2DSquareCellInt.getChunkNRows();
            int ncols = grid2DSquareCellInt.getChunkNCols();
            int noDataValue = grid2DSquareCellInt.getNoDataValue(false);
            boolean calculated = false;
            int row = 0;
            int col = 0;
            int p;
            int q;
            Object[] tmode = initMode(nrows, ncols, noDataValue);
            if (tmode[0] == null) {
                return mode;
            } else {
                int value;
                long count;
                long modeCount = (Long) tmode[0];
                mode.add((Integer) tmode[1]);
                Grids_2D_ID_int chunkCellID = (Grids_2D_ID_int) tmode[2];
                // Do remainder of the row
                p = chunkCellID.getRow();
                for (q = chunkCellID.getCol() + 1; q < ncols; q++) {
                    value = getCell(
                            p,
                            q,
                            noDataValue);
                    if (value != noDataValue) {
                        count = count(
                                p,
                                q,
                                nrows,
                                ncols,
                                value);
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
                        value = getCell(
                                p,
                                q,
                                noDataValue);
                        if (value != noDataValue) {
                            count = count(
                                    p,
                                    q,
                                    nrows,
                                    ncols,
                                    value);
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
     * @see getModeTIntHashSet()
     */
    private Object[] initMode(
            int nrows,
            int ncols,
            int noDataValue) {
        Object[] initMode = new Object[3];
        long modeCount;
        int p;
        int q;
        int row;
        int col;
        int value;
        int thisValue;
        for (p = 0; p < nrows; p++) {
            for (q = 0; q < ncols; q++) {
                value = getCell(p, q, noDataValue);
                if (value != noDataValue) {
                    modeCount = 0L;
                    for (row = 0; row < nrows; row++) {
                        for (col = 0; col < ncols; col++) {
                            thisValue = getCell(
                                    row,
                                    col,
                                    noDataValue);
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
        int thisValue;
        int noDataValue = getGrid2DSquareCellInt().getNoDataValue(false);
        // Do remainder of the row
        for (q++; q < ncols; q++) {
            thisValue = getCell(
                    p,
                    q,
                    noDataValue);
            if (thisValue == value) {
                count++;
            }
        }
        // Do remainder of the grid
        for (p++; p < nrows; p++) {
            for (q = 0; q < ncols; q++) {
                thisValue = getCell(
                        p,
                        q,
                        noDataValue);
                if (thisValue == value) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns the median of all non noDataValues as a double.
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
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getMedianDouble(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the median of all non noDataValues as a double. This method
     * requires that all data in chunk can be stored as a new array.
     *
     * @return
     */
    protected double getMedianDouble() {
        Grids_Grid2DSquareCellInt grid2DSquareCellInt
                = getGrid2DSquareCellInt();
        int scale = 20;
        double median = Double.NEGATIVE_INFINITY;
        BigInteger nonNoDataValueCountBigInteger
                = getNonNoDataValueCountBigInteger();
        if (nonNoDataValueCountBigInteger.compareTo(BigInteger.ZERO) == 1) {
            int[] array = toArrayNotIncludingNoDataValues();
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
    protected void sort1(int x[], int off, int len) {
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
    private void swap(int x[], int a, int b) {
        int t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    /**
     * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)]. Source copied from
     * java.util.Arrays and method changed so not static for performance
     * reasons.
     */
    private void vecswap(int x[], int a, int b, int n) {
        for (int i = 0; i < n; i++, a++, b++) {
            swap(x, a, b);
        }
    }

    /**
     * Returns the index of the median of the three indexed doubles. Source
     * copied from java.util.Arrays and method changed so not static for
     * performance reasons.
     */
    private int med3(int x[], int a, int b, int c) {
        return (x[a] < x[b]
                ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a)
                : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
    }

    /**
     * Returns the standard deviation of all non noDataValues as a double.
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
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L) {
                    throw e;
                }
                this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError);
                return getStandardDeviationDouble(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the standard deviation of all non noDataValues as a double.
     *
     * @return
     */
    protected double getStandardDeviationDouble() {
        double standardDeviation = 0.0d;
        double mean = getArithmeticMeanDouble();
        Grids_Grid2DSquareCellInt grid2DSquareCellInt
                = getGrid2DSquareCellInt();
        int nrows = grid2DSquareCellInt.getChunkNRows();
        int ncols = grid2DSquareCellInt.getChunkNCols();
        int noDataValue = Integer.MIN_VALUE;
        int value;
        double count = 0.0d;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                value = getCell(row, col, noDataValue);
                if (value != noDataValue) {
                    standardDeviation
                            += ((double) value - mean)
                            * ((double) value - mean);
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
