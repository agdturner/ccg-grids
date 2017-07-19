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
package uk.ac.leeds.ccg.andyt.grids.core;

import gnu.trove.TDoubleHashSet;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCell.ChunkID;

/**
 * Provides general methods and controls what methods extended classes must
 * implement acting as an interface.
 */
public abstract class Grids_AbstractGrid2DSquareCellDoubleChunk
        extends Grids_AbstractGrid2DSquareCellChunk
        implements Serializable {
    //implements Serializable, GridDoubleStatisticsInterface {

    private static final long serialVersionUID = 1L;

    /**
     *
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return ( Grids_Grid2DSquareCellDouble )
 this._Grid2DSquareCell.env.
     */
    public Grids_Grid2DSquareCellDouble getGrid2DSquareCellDouble(
            boolean handleOutOfMemoryError) {
        try {
            Grids_Grid2DSquareCellDouble result = getGrid2DSquareCellDouble();
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getGrid2DSquareCellDouble(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     *
     *
     * @return ( Grids_Grid2DSquareCellDouble )
 this._Grid2DSquareCell.env.
     */
    protected Grids_Grid2DSquareCellDouble getGrid2DSquareCellDouble() {
        return (Grids_Grid2DSquareCellDouble) this._Grid2DSquareCell;
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex, as a double.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param noDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double noDataValue,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    noDataValue);
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
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
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param chunkID This is a ChunkID for those AbstractGrid2DSquareCells not
     * to be swapped if possible when an OutOfMemoryError is encountered.
     * @return
     */
    public double getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double noDataValue,
            boolean handleOutOfMemoryError,
            ChunkID chunkID) {
        try {
            double result = getCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    noDataValue);
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_AccountDetail(chunkID, handleOutOfMemoryError) == null) {
                    this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_AccountDetail(handleOutOfMemoryError);
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        handleOutOfMemoryError,
                        chunkID);
            } else {
                throw _OutOfMemoryError;
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
     * this._NoDataValue then null is returned!
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getCellBigDecimal(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex, as a BigDecimal. If the value is
     * this._NoDataValue then null is returned!
     *
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of
     * this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin
     * of this chunk.
     * @param noDataValue The _NoDataValue of this.grid2DSquareCellDouble.
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw e;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
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
            double valueToInitialise);

    /**
     * Initialises the value of this with those in the
 Grids_Grid2DSquareCellDoubleChunkRAF grid2DSquareCellDoubleChunkRAF.
     *
     * @param grid2DSquareCellDoubleChunkRAF The Grids_Grid2DSquareCellDoubleChunkRAF
 used to initialise this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected void initCells(
            Grids_Grid2DSquareCellDoubleChunkRAF grid2DSquareCellDoubleChunkRAF,
            boolean handleOutOfMemoryError) {
        try {
            initCells(grid2DSquareCellDoubleChunkRAF);
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                initCells(
                        grid2DSquareCellDoubleChunkRAF,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Initialises the value of this with those in the
 Grids_Grid2DSquareCellDoubleChunkRAF grid2DSquareCellDoubleChunkRAF.
     *
     * @param grid2DSquareCellDoubleChunkRAF The Grids_Grid2DSquareCellDoubleChunkRAF
 used to initialise this.
     */
    protected void initCells(
            Grids_Grid2DSquareCellDoubleChunkRAF grid2DSquareCellDoubleChunkRAF) {
        try {
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble
                    = getGrid2DSquareCellDouble();
            int chunkNrows = grid2DSquareCellDouble._ChunkNRows;
            int chunkNcols = grid2DSquareCellDouble._ChunkNCols;
            RandomAccessFile randomAccessFile;
            randomAccessFile = grid2DSquareCellDoubleChunkRAF.randomAccessFile;
            randomAccessFile.seek(0L);
            int row;
            int col;
            double noDataValue = grid2DSquareCellDouble._NoDataValue;
            double value = noDataValue;
            boolean handleOutOfMemoryError = true;
            for (row = 0; row < chunkNrows; row++) {
                for (col = 0; col < chunkNcols; col++) {
                    try {
                        value = randomAccessFile.readDouble();
                        initCell(
                                row,
                                col,
                                value,
                                handleOutOfMemoryError);
                    } catch (IOException e) {
                        initCell(
                                row,
                                col,
                                noDataValue,
                                handleOutOfMemoryError);
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println(e.getMessage());
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
     * @param _NoDataValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public double setCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double valueToSet,
            double _NoDataValue,
            boolean handleOutOfMemoryError) {
        try {
            double result = setCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    valueToSet,
                    _NoDataValue);
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return setCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        valueToSet,
                        _NoDataValue,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
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
     * @param _NoDataValue
     * @return
     */
    protected abstract double setCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double valueToSet,
            double _NoDataValue);

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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return toArrayIncludingNoDataValues(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns all the values in row major order as a double[].
     *
     * @return
     */
    protected double[] toArrayIncludingNoDataValues() {
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
        int nrows = grid2DSquareCellDouble._ChunkNRows;
        int ncols = grid2DSquareCellDouble._ChunkNCols;
        double[] array;
        if (((long) nrows * (long) ncols) > Integer.MAX_VALUE) {
            //throw new PrecisionExcpetion
            System.out.println(
                    "PrecisionException in " + this.getClass().getName()
                    + ".toArray()!");
            System.out.println(
                    "Warning! The returned array size is only "
                    + Integer.MAX_VALUE + " instead of "
                    + ((long) nrows * (long) ncols));
            array = new double[Integer.MAX_VALUE];
        }
        double _NoDataValue = getGrid2DSquareCellDouble()._NoDataValue;
        array = new double[nrows * ncols];
        int row;
        int col;
        int count = 0;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                array[ count] = getCell(
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return toArrayNotIncludingNoDataValues(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns all the values in row major order as a double[].
     *
     * @return
     */
    protected double[] toArrayNotIncludingNoDataValues() {
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
        int nrows = grid2DSquareCellDouble._ChunkNRows;
        int ncols = grid2DSquareCellDouble._ChunkNCols;
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
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
                        _NoDataValue);
                if (value != _NoDataValue) {
                    array[ count] = value;
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getNonNoDataValueCountBigInteger(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the number of cells with _NoDataValues as a BigInteger.
     *
     * @return
     */
    protected BigInteger getNonNoDataValueCountBigInteger() {
        BigInteger nonNoDataValueCount = BigInteger.ZERO;
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
        int nrows = grid2DSquareCellDouble._ChunkNRows;
        int ncols = grid2DSquareCellDouble._ChunkNCols;
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getNonNoDataValueCountInt(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
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
    public BigDecimal getSumBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getSumBigDecimal();
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getSumBigDecimal(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the sum of all non _NoDataValues as a BigDecimal.
     *
     * @return
     */
    protected BigDecimal getSumBigDecimal() {
        BigDecimal sum = BigDecimal.ZERO;
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
        int nrows = grid2DSquareCellDouble._ChunkNRows;
        int ncols = grid2DSquareCellDouble._ChunkNCols;
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
        double value;
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(row, col, _NoDataValue);
                if (value != _NoDataValue) {
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getMinDouble(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the minimum of all non _NoDataValues as a double.
     *
     * @return
     */
    protected double getMinDouble() {
        double min = Double.POSITIVE_INFINITY;
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
        int nrows = grid2DSquareCellDouble._ChunkNRows;
        int ncols = grid2DSquareCellDouble._ChunkNCols;
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
        double value;
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(
                        row,
                        col,
                        _NoDataValue);
                if (value != _NoDataValue) {
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getMaxDouble(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the maximum of all non _NoDataValues as a double.
     *
     * @return
     */
    protected double getMaxDouble() {
        double max = Double.NEGATIVE_INFINITY;
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
        int nrows = grid2DSquareCellDouble._ChunkNRows;
        int ncols = grid2DSquareCellDouble._ChunkNCols;
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
        double value;
        int row;
        int col;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                value = getCell(
                        row,
                        col,
                        _NoDataValue);
                if (value != _NoDataValue) {
                    max = Math.max(
                            max,
                            value);
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getArithmeticMeanBigDecimal(
                        numberOfDecimalPlaces,
                        handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
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
        BigDecimal mean = BigDecimal.ZERO;
        BigDecimal count = BigDecimal.ZERO;
        BigDecimal one = BigDecimal.ONE;
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble
                = getGrid2DSquareCellDouble();
        int nrows = grid2DSquareCellDouble._ChunkNRows;
        int ncols = grid2DSquareCellDouble._ChunkNCols;
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
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
     * cells are _NoDataValues, then this.grid2DSquareCellDouble._NoDataValue is
     * returned.
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getArithmeticMeanDouble(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the Arithmetic Mean of all non _NoDataValues as a double. If all
     * cells are _NoDataValues, then this.grid2DSquareCellDouble._NoDataValue is
     * returned. Using BigDecimal this should be as precise as possible with
     * doubles.
     *
     * @return
     */
    protected double getArithmeticMeanDouble() {
        BigDecimal arithmeticMeanBigDecimal = getArithmeticMeanBigDecimal(324);
        try {
            return arithmeticMeanBigDecimal.doubleValue();
        } catch (NullPointerException npe0) {
            return getGrid2DSquareCellDouble()._NoDataValue;
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getModeTDoubleHashSet(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
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
        TDoubleHashSet mode = new TDoubleHashSet();
        BigInteger nonNoDataValueCountBigInteger
                = getNonNoDataValueCountBigInteger();
        if (nonNoDataValueCountBigInteger.compareTo(BigInteger.ZERO) == 1) {
            //TDoubleObjectHashMap modes = new TDoubleObjectHashMap();
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble
                    = getGrid2DSquareCellDouble();
            int nrows = grid2DSquareCellDouble._ChunkNRows;
            int ncols = grid2DSquareCellDouble._ChunkNCols;
            double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
            boolean calculated = false;
            int row = 0;
            int col = 0;
            int p;
            int q;
            Object[] tmode = initMode(
                    nrows,
                    ncols,
                    _NoDataValue);
            if (tmode[0] == null) {
                return mode;
            } else {
                double value;
                long count;
                long modeCount = ((Long) tmode[ 0]).longValue();
                mode.add(((Double) tmode[ 1]).doubleValue());
                ChunkCellID chunkCellID = (ChunkCellID) tmode[ 2];
                // Do remainder of the row
                p = chunkCellID.chunkCellRowIndex;
                for (q = chunkCellID.chunkCellColIndex + 1; q < ncols; q++) {
                    value = getCell(p, q, _NoDataValue);
                    if (value != _NoDataValue) {
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
                        value = getCell(p, q, _NoDataValue);
                        if (value != _NoDataValue) {
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
     * @see getModeTDoubleHashSet()
     */
    private Object[] initMode(
            int nrows,
            int ncols,
            double _NoDataValue) {
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
                value = getCell(p, q, _NoDataValue);
                if (value != _NoDataValue) {
                    modeCount = 0L;
                    for (row = 0; row < nrows; row++) {
                        for (col = 0; col < ncols; col++) {
                            thisValue = getCell(
                                    row,
                                    col,
                                    _NoDataValue);
                            if (thisValue == value) {
                                modeCount++;
                            }
                        }
                    }
                    initMode[0] = new Long(modeCount);
                    initMode[1] = new Double(value);
                    initMode[2] = new ChunkCellID(
                            p,
                            q);
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
        double _NoDataValue
                = getGrid2DSquareCellDouble()._NoDataValue;
        // Do remainder of the row
        for (q++; q < ncols; q++) {
            thisValue = getCell(
                    p,
                    q,
                    _NoDataValue);
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
                        _NoDataValue);
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getMedianDouble(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
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
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble
                = getGrid2DSquareCellDouble();
        int scale = 20;
        double median = grid2DSquareCellDouble._NoDataValue;
        BigInteger nonNoDataValueCountBigInteger
                = getNonNoDataValueCountBigInteger();
        if (nonNoDataValueCountBigInteger.compareTo(BigInteger.ZERO) == 1) {
            double[] array = toArrayNotIncludingNoDataValues();
            sort1(array, 0, array.length);
            BigInteger[] nonNoDataValueCountBigIntegerDivideAndRemainder2
                    = nonNoDataValueCountBigInteger.divideAndRemainder(new BigInteger("2"));
            if (nonNoDataValueCountBigIntegerDivideAndRemainder2[ 1].compareTo(BigInteger.ZERO) == 0) {
                int index = nonNoDataValueCountBigIntegerDivideAndRemainder2[ 0].intValue();
                    //median = array[ index ];
                //median += array[ index - 1 ];
                //median /= 2.0d;
                //return median;
                BigDecimal medianBigDecimal = new BigDecimal(array[ index - 1]);
                return (medianBigDecimal.add(new BigDecimal(array[ index]))).divide(new BigDecimal(2.0d), scale, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                //return ( medianBigDecimal.add( new BigDecimal( array[ index ] ) ) ).divide( new BigDecimal( 2.0d ), scale, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
            } else {
                int index = nonNoDataValueCountBigIntegerDivideAndRemainder2[ 0].intValue();
                return array[ index];
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
            getGrid2DSquareCell(handleOutOfMemoryError).env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell.env.clear_MemoryReserve();
                if (this._Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this._Grid2DSquareCell,
                        this._ChunkID) < 1L) {
                    throw _OutOfMemoryError;
                }
                this._Grid2DSquareCell.env.init_MemoryReserve(
                        this._Grid2DSquareCell,
                        this._ChunkID,
                        handleOutOfMemoryError);
                return getStandardDeviationDouble(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the standard deviation of all non _NoDataValues as a double.
     *
     * @return
     */
    protected double getStandardDeviationDouble() {
        double standardDeviation = 0.0d;
        double mean = getArithmeticMeanDouble();
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble
                = getGrid2DSquareCellDouble();
        int nrows = grid2DSquareCellDouble._ChunkNRows;
        int ncols = grid2DSquareCellDouble._ChunkNCols;
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
        double value;
        double count = 0.0d;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                value = getCell(row, col, _NoDataValue);
                if (value != _NoDataValue) {
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
