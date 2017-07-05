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
import gnu.trove.TIntHashSet;
import java.io.Serializable;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCell.CellID;

/**
 * An abstract class to be extended and methods overridden to provide statistics
 about the data in Grids_AbstractGrid2DSquareCell and Grid2DSquareCellChunkAbstract
 more optimally.
 */
public abstract class AbstractGridStatistics
        implements Serializable, GridStatisticsInterface {

    private static final long serialVersionUID = 1L;
    /**
     * A reference to the Grids_AbstractGrid2DSquareCell this is for.
     */
    protected Grids_AbstractGrid2DSquareCell Grid2DSquareCell;
    /**
     * For storing the number of cells with non noDataValues.
     */
    protected BigInteger nonNoDataValueCountBigInteger;
    /**
     * For storing the sum of all non noDataValues as a BigDecimal.
     */
    protected BigDecimal sumBigDecimal;
    /**
     * For storing the minimum of all non noDataValues as a BigDecimal.
     */
    protected BigDecimal minBigDecimal;
    /**
     * For storing the number of min values as a BigInteger.
     */
    protected BigInteger minCountBigInteger;
    /**
     * For storing the maximum of all non noDataValues as a BigDecimal.
     */
    protected BigDecimal maxBigDecimal;
    /**
     * For storing the number of max values as a BigInteger.
     */
    protected BigInteger maxCountBigInteger;

    /**
     * For intitialisation
     */
    protected void init() {
        this.nonNoDataValueCountBigInteger = new BigInteger("0");
        this.sumBigDecimal = new BigDecimal(0.0d);
        //this.minBigDecimal = new BigDecimal( Double.POSITIVE_INFINITY );
        //this.minBigDecimal = new BigDecimal(
        //        "99999999999999999999999999999999999999999" );
        //Integer.MAX_VALUE used instead of Double.MAX_VALUE
        BigDecimal bigDecimalIntegerMAX_VALUE
                = new BigDecimal(Integer.MAX_VALUE);
        this.minBigDecimal
                = bigDecimalIntegerMAX_VALUE.add(bigDecimalIntegerMAX_VALUE);
        this.minCountBigInteger = new BigInteger("0");
        //this.maxBigDecimal = new BigDecimal( Double.NEGATIVE_INFINITY );
        //this.minBigDecimal = new BigDecimal(
        //        "-99999999999999999999999999999999999999999" );
        //Integer.MIN_VALUE used instead of Double.MIN_VALUE
        BigDecimal bigDecimalIntegerMIN_VALUE
                = new BigDecimal(Integer.MIN_VALUE);
        this.maxBigDecimal
                = bigDecimalIntegerMIN_VALUE.add(bigDecimalIntegerMIN_VALUE);
        this.maxCountBigInteger = new BigInteger("0");
    }

    /**
     * For intitialisation
     *
     * @param grid2DSquareCell
     */
    protected void init(
            Grids_AbstractGrid2DSquareCell grid2DSquareCell) {
        this.Grid2DSquareCell = grid2DSquareCell;
        this.Grid2DSquareCell._GridStatistics = this;
        init();
    }

    /**
     * Updates fields from _GridStatistics except this.Grid2DSquareCell
     *
     *
     * @param _GridStatistics the _GridStatistics instance which fields are used
     * to update this.
     */
    protected void update(
            AbstractGridStatistics _GridStatistics) {
        this.nonNoDataValueCountBigInteger
                = _GridStatistics.nonNoDataValueCountBigInteger;
        this.sumBigDecimal = _GridStatistics.sumBigDecimal;
        this.minBigDecimal = _GridStatistics.minBigDecimal;
        this.minCountBigInteger = _GridStatistics.minCountBigInteger;
        this.maxBigDecimal = _GridStatistics.maxBigDecimal;
        this.maxCountBigInteger = _GridStatistics.maxCountBigInteger;
    }

    /**
     * Updates fields (statistics) by going through all values in
     * this.grid2DSquareCellAbstract if they might not be up to date. (NB. After
     * calling this it is inexpensive to convert to GridStatistics0.)
     */
    protected abstract void update();

    /**
     * Updates fields (statistics) by going through all values in
     * this.grid2DSquareCellAbstract if they might not be up to date. (NB. After
     * calling this it is inexpensive to convert to GridStatistics0.) This is
     * called from extending classes activated via update. It is here to avoid
     * duplication in the code. The parameters are specified in order to
     * distinguish this method from update().
     *
     *
     * @param _NRows The number of rows in the grid.
     * @param _NCols The number of columns in the grid.
     */
    protected void update(
            long _NRows,
            long _NCols) {
        long row;
        long col;
//        //DEBUG code
//        System.out.println("this.Grid2DSquareCell.toString()" + this.Grid2DSquareCell.toString() );
        if (this.Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class) {
            //if ( this.Grid2DSquareCell instanceof Grid2DSquareCellInt ) {
            Grid2DSquareCellInt grid2DSquareCellInt
                    = (Grid2DSquareCellInt) Grid2DSquareCell;
//            //DEBUG code
//            System.out.println("grid2DSquareCellInt.toString()" + grid2DSquareCellInt.toString() );
            BigDecimal cellBigDecimal;
            int cellInt;
            boolean handleOutOfMemoryError = true;
            int noDataValueInt = grid2DSquareCellInt.getNoDataValue(
                    handleOutOfMemoryError);
            //BigDecimal oneBigDecimal = BigDecimal.ONE;
            for (row = 0; row < _NRows; row++) {
                for (col = 0; col < _NCols; col++) {
                    cellInt = grid2DSquareCellInt.getCell(
                            row,
                            col,
                            handleOutOfMemoryError);
//                    cellInt = grid2DSquareCellInt.getCellInt(
//                            row,
//                            col,
//                            handleOutOfMemoryError );
                    if (cellInt != noDataValueInt) {
                        cellBigDecimal = new BigDecimal(cellInt);
                        this.nonNoDataValueCountBigInteger
                                = this.nonNoDataValueCountBigInteger.add(BigInteger.ONE);
                        this.sumBigDecimal = this.sumBigDecimal.add(cellBigDecimal);
                        if (cellBigDecimal.compareTo(this.minBigDecimal) == -1) {
                            this.minCountBigInteger = BigInteger.ONE;
                            this.minBigDecimal = cellBigDecimal;
                        } else {
                            if (cellBigDecimal.compareTo(this.minBigDecimal) == 0) {
                                this.minCountBigInteger
                                        = this.minCountBigInteger.add(BigInteger.ONE);
                            }
                        }
                        if (cellBigDecimal.compareTo(this.maxBigDecimal) == 1) {
                            this.maxCountBigInteger = BigInteger.ONE;
                            this.maxBigDecimal = cellBigDecimal;
                        } else {
                            if (cellBigDecimal.compareTo(this.maxBigDecimal) == 0) {
                                this.maxCountBigInteger
                                        = this.maxCountBigInteger.add(BigInteger.ONE);
                            }
                        }
                    }
                }
            }
        } else {
            //if ( this.Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class ) {
            //if ( this.Grid2DSquareCell instanceof Grid2DSquareCellDouble ) {
            Grid2DSquareCellDouble grid2DSquareCellDouble
                    = (Grid2DSquareCellDouble) Grid2DSquareCell;
            BigDecimal cellBigDecimal;
            double cellDouble;
            boolean handleOutOfMemoryError = true;
            double noDataValueDouble = grid2DSquareCellDouble._NoDataValue;
            // BigDecimal oneBigDecimal = BigDecimal.ONE;
            for (row = 0; row < _NRows; row++) {
                for (col = 0; col < _NCols; col++) {
                    //System.out.println("row " + row + ", col " + col );
                    cellDouble = grid2DSquareCellDouble.getCell(
                            row,
                            col,
                            handleOutOfMemoryError);
//                    cellDouble = grid2DSquareCellDouble.getCellDouble(
//                            row,
//                            col,
//                            handleOutOfMemoryError );
                    if (cellDouble != noDataValueDouble) {
                        try {
                            // Debug
                            if (cellDouble == Double.NEGATIVE_INFINITY
                                    || cellDouble == Double.NaN
                                    || cellDouble == Double.POSITIVE_INFINITY) {
                                System.out.println("debug row " + row + " col " + col);
                            }

                            cellBigDecimal = new BigDecimal(cellDouble);
                            this.nonNoDataValueCountBigInteger
                                    = this.nonNoDataValueCountBigInteger.add(BigInteger.ONE);
                            this.sumBigDecimal = this.sumBigDecimal.add(cellBigDecimal);
                            if (cellBigDecimal.compareTo(this.minBigDecimal) == -1) {
                                this.minCountBigInteger = BigInteger.ONE;
                                this.minBigDecimal = cellBigDecimal;
                            } else {
                                if (cellBigDecimal.compareTo(this.minBigDecimal) == 0) {
                                    this.minCountBigInteger
                                            = this.minCountBigInteger.add(BigInteger.ONE);
                                }
                            }
                            if (cellBigDecimal.compareTo(this.maxBigDecimal) == 1) {
                                this.maxCountBigInteger = BigInteger.ONE;
                                this.maxBigDecimal = cellBigDecimal;
                            } else {
                                if (cellBigDecimal.compareTo(this.maxBigDecimal) == 0) {
                                    this.maxCountBigInteger
                                            = this.maxCountBigInteger.add(BigInteger.ONE);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.err.println(e.getMessage() + " in AbstractGridStatistics.update(" + _NRows + ", " + _NCols + ") value at row " + row + ", col " + col + " = " + cellDouble + ");");
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a String describing this instance
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public String toString(
            boolean handleOutOfMemoryError) {
        try {
            String result = getDescription();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(false);
                return toString(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the name of the this class
     *
     * @return
     */
    protected String getName() {
        return this.getClass().getName();
        //return "GridStatistics0";
        //return "GridStatistics1";
    }

    /**
     * Returns a string describing this instance
     *
     * @return
     */
    protected String getDescription() {
        String result = this.getName();
        result
                += "( nonNoDataValueCountBigInteger( "
                + this.nonNoDataValueCountBigInteger.toString() + " ), "
                + "maxBigDecimal( " + this.maxBigDecimal.toString() + " ), "
                + "minBigDecimal( " + this.minBigDecimal.toString() + " ), "
                + "maxCountBigInteger( " + this.maxCountBigInteger.toString() + " ), "
                + "minCountBigInteger( " + this.minCountBigInteger.toString() + " ), "
                + "sumBigDecimal( " + this.sumBigDecimal.toString() + " ) )";
        return result;
    }

    /**
     * For returning the number of cells with noDataValues as a BigInteger.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final BigInteger getNonNoDataValueCountBigInteger(
            boolean handleOutOfMemoryError) {
        try {
            BigInteger result = getNonNoDataValueCountBigInteger();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(false);
                return getNonNoDataValueCountBigInteger(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the number of cells with noDataValues as a BigInteger.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final BigInteger getNonZeroAndNonNoDataValueCountBigInteger(
            boolean handleOutOfMemoryError) {
        try {
            BigInteger result = getNonZeroAndNonNoDataValueCountBigInteger();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(false);
                return getNonZeroAndNonNoDataValueCountBigInteger(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return The number of cells with non noDataValues as a BigInteger.
     */
    protected abstract BigInteger getNonNoDataValueCountBigInteger();

    /**
     * @return The number of cells with non zero and non noDataValues as a
     * BigInteger.
     */
    //protected abstract BigInteger getNonZeroAndNonNoDataValueCountBigInteger(
    protected BigInteger getNonZeroAndNonNoDataValueCountBigInteger() {
        BigInteger result;
        result = BigInteger.ZERO;
        boolean handleOutOfMemoryError = true;
        if (this.Grid2DSquareCell instanceof Grid2DSquareCellDouble) {
            Grid2DSquareCellDouble g;
            g = (Grid2DSquareCellDouble) this.Grid2DSquareCell;
            double noDataValue;
            noDataValue = g.get_NoDataValue(handleOutOfMemoryError);
            Iterator<Double> ite;
            ite = g.iterator(handleOutOfMemoryError);
            while (ite.hasNext()) {
                double value = (Double) ite.next();
                if (!(value == noDataValue || value == 0)) {
                    result = result.add(BigInteger.ONE);
                }
            }
//            // Go through all values in the grid and work it out
//            int nChunkRows = g.get_NChunkRows();
//            int nChunkCols = g.get_NChunkCols();
//            for (int chunkRowIndex = 0; chunkRowIndex < nChunkRows; chunkRowIndex++) {
//                for (int chunkColIndex = 0; chunkColIndex < nChunkCols; chunkColIndex++) {
//                    int chunkNcols;
//                    int chunkNrows;
//                    chunkNcols = g.get_ChunkNCols(
//                            chunkColIndex, handleOutOfMemoryError);
//                    chunkNrows = g.get_ChunkNRows(
//                            chunkRowIndex, handleOutOfMemoryError);
//                    AbstractGrid2DSquareCellDoubleChunk chunk;
//                    chunk = (AbstractGrid2DSquareCellDoubleChunk) g.getGrid2DSquareCellChunk(
//                            chunkRowIndex, chunkColIndex, handleOutOfMemoryError);
//                    for (int chunkCellRowIndex = 0; chunkCellRowIndex < chunkNrows; chunkCellRowIndex++) {
//                        for (int chunkCellColIndex = 0; chunkCellColIndex < chunkNcols; chunkCellColIndex++) {
//                            double value;
//                            value = g.getCell(
//                                    chunk, chunkRowIndex, chunkColIndex,
//                                    chunkCellRowIndex, chunkCellColIndex,
//                                    handleOutOfMemoryError);
//                            if (value != noDataValue || value != 0) {
//                                result = result.add(BigInteger.ONE);
//                            }
//                        }
//                    }
//                }
//            }
            return result;
        } else {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    /**
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of cells with non noDataValues (DataValues) as a long.
     */
    @Override
    public final long getNonNoDataValueCountLong(
            boolean handleOutOfMemoryError) {
        try {
            long result = getNonNoDataValueCountLong();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getNonNoDataValueCountLong(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return The number of cells with non noDataValues (DataValues) as a long.
     */
    protected final long getNonNoDataValueCountLong() {
        boolean handleOutOfMemoryError = true;
        return getNonNoDataValueCountBigInteger(handleOutOfMemoryError).longValue();
        //BigInteger value;
        //if ( value.compareTo( BigInteger.valueOf( Long.MAX_VALUE ) ) == -1 &&
        //     value.compareTo( BigInteger.valueOf( Long.MIN_VALUE ) ) == 1 ) {
        //    return value.longValue();
        //} else {
        //    if ( valuel.compareTo( BigInteger.valueOf( Long.MAX_VALUE ) ) == 1 ) {
        //        return Long.MAX_VALUE;
        //    } else {
        //        return Long.MIN_VALUE;
        //    }
        //}
    }

    /**
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return The number of cells with non zero and non noDataValues (non zero
     * DataValues) as a long.
     */
    public final long getNonZeroAndNonNoDataValueCountLong(
            boolean handleOutOfMemoryError) {
        try {
            long result = getNonZeroAndNonNoDataValueCountLong();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(false);
                return getNonZeroAndNonNoDataValueCountLong(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * @return The number of cells with non zero and non noDataValues (non zero
     * DataValues) as a long.
     */
    protected final long getNonZeroAndNonNoDataValueCountLong() {
        boolean handleOutOfMemoryError = true;
        return getNonZeroAndNonNoDataValueCountBigInteger(handleOutOfMemoryError).longValue();
        //BigInteger value;
        //if ( value.compareTo( BigInteger.valueOf( Long.MAX_VALUE ) ) == -1 &&
        //     value.compareTo( BigInteger.valueOf( Long.MIN_VALUE ) ) == 1 ) {
        //    return value.longValue();
        //} else {
        //    if ( valuel.compareTo( BigInteger.valueOf( Long.MAX_VALUE ) ) == 1 ) {
        //        return Long.MAX_VALUE;
        //    } else {
        //        return Long.MIN_VALUE;
        //    }
        //}
    }

    /**
     * For returning the number of cells with noDataValues as a int.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final int getNonNoDataValueCountInt(
            boolean handleOutOfMemoryError) {
        try {
            int result = getNonNoDataValueCountInt();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getNonNoDataValueCountInt(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the number of cells with noDataValues as a int.
     *
     * @return
     */
    protected final int getNonNoDataValueCountInt() {
        boolean handleOutOfMemoryError = true;
        return getNonNoDataValueCountBigInteger(handleOutOfMemoryError).intValue();
        //BigInteger value;
        //if ( value.compareTo( BigInteger.valueOf( Integer.MAX_VALUE ) ) == -1 &&
        //     value.compareTo( BigInteger.valueOf( Integer.MIN_VALUE ) ) == 1 ) {
        //    return value.intValue();
        //} else {
        //    if ( valuel.compareTo( BigInteger.valueOf( Integer.MAX_VALUE ) ) == 1 ) {
        //        return Integer.MAX_VALUE;
        //    } else {
        //        return Integer.MIN_VALUE;
        //    }
        //}
    }

    /**
     * For returning the sum of all non noDataValues as a BigDecimal.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final BigDecimal getSumBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getSumBigDecimal();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getSumBigDecimal(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a BigDecimal.
     *
     * @return
     */
    protected abstract BigDecimal getSumBigDecimal();

    /**
     * For returning the sum of all non noDataValues as a BigInteger.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final BigInteger getSumBigInteger(
            boolean handleOutOfMemoryError) {
        try {
            BigInteger result = getSumBigInteger();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getSumBigInteger(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a BigInteger.
     *
     * @return
     */
    protected final BigInteger getSumBigInteger() {
        boolean handleOutOfMemoryError = true;
        return getSumBigDecimal(handleOutOfMemoryError).toBigIntegerExact();
    }

    /**
     * For returning the sum of all non noDataValues as a double.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final double getSumDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getSumDouble();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getSumDouble(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a double. If
     * this.sumBigDecimal is to big/small to represent as a double then an
     * Arithmetic Exception is thrown and Double.POSITIVE_INFINITY or
     * Double.NEGATIVE_INFINITY are returned as appropriate.
     *
     * @return
     */
    protected final double getSumDouble() {
        boolean handleOutOfMemoryError = true;
        double result = getSumBigDecimal(handleOutOfMemoryError).doubleValue();
        if (Double.isInfinite(result)) {
            throw new ArithmeticException();
        }
        return result;
    }

    /**
     * For returning the sum of all non noDataValues as a long.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final long getSumLong(
            boolean handleOutOfMemoryError) {
        try {
            long result = getSumLong();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getSumLong(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a long.
     *
     * @return
     */
    protected final long getSumLong() {
        boolean handleOutOfMemoryError = true;
        return getSumBigDecimal(handleOutOfMemoryError).longValueExact();
        //BigDecimal value;
        //if ( value.compareTo( BigDecimal.valueOf( Long.MAX_VALUE ) ) == -1 &&
        //     value.compareTo( BigDecimal.valueOf( Long.MIN_VALUE ) ) == 1 ) {
        //    return value.longValue();
        //} else {
        //    if ( value.compareTo( BigDecimal.valueOf( Long.MAX_VALUE ) ) == 1 ) {
        //        return Long.MAX_VALUE;
        //    } else {
        //        return Long.MIN_VALUE;
        //    }
        //}
    }

    /**
     * For returning the sum of all non noDataValues as a int.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final int getSumInt(
            boolean handleOutOfMemoryError) {
        try {
            int result = getSumInt();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getSumInt(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a int.
     *
     * @return
     */
    protected int getSumInt() {
        update();
        boolean handleOutOfMemoryError = true;
        return getSumBigDecimal(handleOutOfMemoryError).intValueExact();
        //BigDecimal value;
        //if ( value.compareTo( BigDecimal.valueOf( Integer.MAX_VALUE ) ) == -1 &&
        //     value.compareTo( BigDecimal.valueOf( Integer.MIN_VALUE ) ) == 1 ) {
        //    return value.intValue();
        //} else {
        //    if ( valuel.compareTo( BigDecimal.valueOf( Integer.MAX_VALUE ) ) == 1 ) {
        //        return Integer.MAX_VALUE;
        //    } else {
        //        return Integer.MIN_VALUE;
        //    }
        //}
    }

    /**
     * For returning the minimum of all non noDataValues as a BigDecimal.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final BigDecimal getMinBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getMinBigDecimal();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getMinBigDecimal(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a BigInteger.
     *
     * @return
     */
    protected abstract BigDecimal getMinBigDecimal();

    /**
     * For returning the minimum of all non noDataValues as a BigInteger.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final BigInteger getMinBigInteger(
            boolean handleOutOfMemoryError) {
        try {
            BigInteger result = getMinBigInteger();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getMinBigInteger(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a BigInteger.
     *
     * @return
     */
    protected final BigInteger getMinBigInteger() {
        return getMinBigDecimal().toBigIntegerExact();
    }

    /**
     * For returning the minimum of all non noDataValues as a double.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final double getMinDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getMinDouble();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getMinDouble(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a double. If
     * this.minBigDecimal is to big/small to represent as a double then an
     * Arithmetic Exception is thrown and Double.POSITIVE_INFINITY or
     * Double.NEGATIVE_INFINITY are returned as appropriate.
     *
     * @return
     */
    protected final double getMinDouble() {
        //update();
        double result = getMinBigDecimal().doubleValue();
        //double result = this.minBigDecimal.doubleValue();
        if (Double.isInfinite(result)) {
            throw new ArithmeticException();
        }
        return result;
    }

    /**
     * For returning the minimum of all non noDataValues as a long.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final long getMinLong(
            boolean handleOutOfMemoryError) {
        try {
            long result = getMinLong();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getMinLong(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a long
     *
     * @return
     */
    protected final long getMinLong() {
        return getMinBigInteger().longValue();
        //BigDecimal value;
        //if ( value.compareTo( BigDecimal.valueOf( Long.MAX_VALUE ) ) == -1 &&
        //     value.compareTo( BigDecimal.valueOf( Long.MIN_VALUE ) ) == 1 ) {
        //    return value.longValue();
        //} else {
        //    if ( value.compareTo( BigDecimal.valueOf( Long.MAX_VALUE ) ) == 1 ) {
        //        return Long.MAX_VALUE;
        //    } else {
        //        return Long.MIN_VALUE;
        //    }
        //}
    }

    /**
     * For returning the minimum of all non noDataValues as a int.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final int getMinInt(
            boolean handleOutOfMemoryError) {
        try {
            int result = getMinInt();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getMinInt(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a int.
     *
     * @return
     */
    protected final int getMinInt() {
        return getMinBigInteger().intValue();
    }

    /**
     * For returning the maximum of all non noDataValues as a BigInteger.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final BigInteger getMaxBigInteger(
            boolean handleOutOfMemoryError) {
        try {
            BigInteger result = getMaxBigInteger();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getMaxBigInteger(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the maximum of all non noDataValues as a BigInteger.
     *
     * @return
     */
    protected final BigInteger getMaxBigInteger() {
        return getMaxBigDecimal().toBigIntegerExact();
    }

    /**
     * For returning the maximum of all non noDataValues as a BigDecimal.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final BigDecimal getMaxBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getMaxBigDecimal();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getMaxBigDecimal(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the maximum of all non noDataValues as a BigInteger.
     *
     * @return
     */
    protected abstract BigDecimal getMaxBigDecimal();

    /**
     * For returning the maximum of all non noDataValues as a double.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final double getMaxDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getMaxDouble();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getMaxDouble(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the maximum of all non noDataValues as a double. If
     * this.minBigDecimal is to big/small to represent as a double then an
     * Arithmetic Exception is thrown and Double.POSITIVE_INFINITY or
     * Double.NEGATIVE_INFINITY are returned as appropriate.
     *
     * @return
     */
    protected final double getMaxDouble() {
        double result = getMaxBigDecimal().doubleValue();
        if (Double.isInfinite(result)) {
            throw new ArithmeticException();
        }
        return result;
    }

    /**
     * For returning the maximum of all non noDataValues as a long.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final long getMaxLong(
            boolean handleOutOfMemoryError) {
        try {
            long result = getMaxLong();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getMaxLong(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a long.
     *
     * @return
     */
    protected final long getMaxLong() {
        //return getMaxBigInteger().longValue();
        //boolean handleOutOfMemoryError = true;
        //return getMaxBigDecimal( handleOutOfMemoryError ).longValueExact();
        return getMaxBigDecimal().longValueExact();
        //BigDecimal value;
        //if ( value.compareTo( BigDecimal.valueOf( Long.MAX_VALUE ) ) == -1 &&
        //     value.compareTo( BigDecimal.valueOf( Long.MIN_VALUE ) ) == 1 ) {
        //    return value.longValue();
        //} else {
        //    if ( value.compareTo( BigDecimal.valueOf( Long.MAX_VALUE ) ) == 1 ) {
        //        return Long.MAX_VALUE;
        //    } else {
        //        return Long.MIN_VALUE;
        //    }
        //}
    }

    /**
     * For returning the maximum of all non noDataValues as a int.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public final int getMaxInt(
            boolean handleOutOfMemoryError) {
        try {
            int result = getMaxInt();
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getMaxInt(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a int.
     *
     * @return
     */
    protected final int getMaxInt() {
        boolean handleOutOfMemoryError = true;
        return getMaxBigDecimal(handleOutOfMemoryError).intValueExact();
    }

    /**
     * For returning the mode of all non noDataValues either as a TDoubleHashSet
 or as a TIntHashSet respectively depending on if (
 this.Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class ) or if (
 this.Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class ).
     * TODO: Change for loops so as to look through each chunk in turn. Is it
     * better to use toArray and go through a sorted version?
     *
     * @return
     */
    protected Object getMode() {
        long _NRows = this.Grid2DSquareCell._NRows;
        long _NCols = this.Grid2DSquareCell._NCols;
        long row;
        long col;
        boolean calculated = false;
        if (this.Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class) {
            Grid2DSquareCellInt grid2DSquareCellInt
                    = (Grid2DSquareCellInt) this.Grid2DSquareCell;
            TIntHashSet mode = new TIntHashSet();
            boolean handleOutOfMemoryError = true;
            int noDataValue = grid2DSquareCellInt.getNoDataValue(
                    handleOutOfMemoryError);
            Object[] tmode = initMode(
                    grid2DSquareCellInt,
                    _NRows,
                    _NCols,
                    noDataValue);
            if (tmode[0] == null) {
                return mode;
            } else {
                //double value;
                int value;
                long count;
                long modeCount = ((Long) tmode[0]);
                mode.add(((Integer) tmode[1]));
                CellID cellID = (CellID) tmode[2];
                // Do remainder of the row
                row = cellID._CellRowIndex;
                for (col = cellID._CellColIndex + 1; col < _NCols; col++) {
                    value = grid2DSquareCellInt.getCell(
                            row,
                            col,
                            handleOutOfMemoryError);
                    if (value != noDataValue) {
                        count = count(
                                grid2DSquareCellInt,
                                row,
                                col,
                                _NRows,
                                _NCols,
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
                for (row++; row < _NRows; row++) {
                    for (col = 0; col < _NCols; col++) {
                        value = grid2DSquareCellInt.getCell(
                                row,
                                col,
                                handleOutOfMemoryError);
                        if (value != noDataValue) {
                            count = count(
                                    grid2DSquareCellInt,
                                    row,
                                    col,
                                    _NRows,
                                    _NCols,
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
            return mode;
        } else {
            //this.Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class
            Grid2DSquareCellDouble grid2DSquareCellDouble
                    = (Grid2DSquareCellDouble) this.Grid2DSquareCell;
            TDoubleHashSet mode = new TDoubleHashSet();
            boolean handleOutOfMemoryError = true;
            double noDataValue = grid2DSquareCellDouble._NoDataValue;
            Object[] tmode = initMode(
                    grid2DSquareCellDouble,
                    _NRows,
                    _NCols,
                    noDataValue);
            if (tmode[0] == null) {
                return mode;
            } else {
                double value;
                long count;
                long modeCount = ((Long) tmode[0]).longValue();
                mode.add(((Double) tmode[1]).doubleValue());
                CellID cellID = (CellID) tmode[2];
                // Do remainder of the row
                row = cellID._CellRowIndex;
                for (col = cellID._CellColIndex + 1; col < _NCols; col++) {
                    value = grid2DSquareCellDouble.getCell(
                            row,
                            col,
                            handleOutOfMemoryError);
                    if (value != noDataValue) {
                        count = count(
                                grid2DSquareCellDouble,
                                row,
                                col,
                                _NRows,
                                _NCols,
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
                for (row++; row < _NRows; row++) {
                    for (col = 0; col < _NCols; col++) {
                        value = grid2DSquareCellDouble.getCell(
                                row,
                                col,
                                handleOutOfMemoryError);
                        if (value != noDataValue) {
                            count = count(
                                    grid2DSquareCellDouble,
                                    row,
                                    col,
                                    _NRows,
                                    _NCols,
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
            return mode;
        }
    }

    /**
     * For initialising mode calculation in getMode().
     */
    private Object[] initMode(
            Grid2DSquareCellInt grid2DSquareCellInt,
            long _NRows,
            long _NCols,
            int noDataValue) {
        Object[] initMode = new Object[3];
        long modeCount;
        long p;
        long q;
        long row;
        long col;
        int value;
        int thisValue;
        boolean handleOutOfMemoryError = true;
        for (p = 0; p < _NRows; p++) {
            for (q = 0; q < _NCols; q++) {
                value = grid2DSquareCellInt.getCell(
                        p,
                        q,
                        handleOutOfMemoryError);
                if (value != noDataValue) {
                    modeCount = 0L;
                    for (row = 0; row < _NRows; row++) {
                        for (col = 0; col < _NCols; col++) {
                            thisValue = grid2DSquareCellInt.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            if (thisValue == value) {
                                modeCount++;
                            }
                        }
                    }
                    initMode[0] = new Long(modeCount);
                    initMode[1] = new Integer(value);
                    initMode[2] = grid2DSquareCellInt.getCellID(
                            p,
                            q,
                            handleOutOfMemoryError);
                    //initMode[2] = new CellID( p, q );
                    return initMode;
                }
            }
        }
        return initMode;
    }

    /**
     * For initialising mode calculation in getMode().
     */
    private Object[] initMode(
            Grid2DSquareCellDouble grid2DSquareCellDouble,
            long _NRows,
            long _NCols,
            double noDataValue) {
        Object[] initMode = new Object[3];
        long modeCount;
        long p;
        long q;
        long row;
        long col;
        double value;
        double thisValue;
        boolean handleOutOfMemoryError = true;
        for (p = 0; p < _NRows; p++) {
            for (q = 0; q < _NCols; q++) {
                value = grid2DSquareCellDouble.getCell(
                        p,
                        q,
                        handleOutOfMemoryError);
                if (value != noDataValue) {
                    modeCount = 0L;
                    for (row = 0; row < _NRows; row++) {
                        for (col = 0; col < _NCols; col++) {
                            thisValue = grid2DSquareCellDouble.getCell(
                                    row,
                                    col,
                                    handleOutOfMemoryError);
                            if (thisValue == value) {
                                modeCount++;
                            }
                        }
                    }
                    initMode[0] = new Long(modeCount);
                    initMode[1] = new Double(value);
                    initMode[2] = grid2DSquareCellDouble.getCellID(
                            p,
                            q,
                            handleOutOfMemoryError);
                    //initMode[2] = new CellID( p, q );
                    return initMode;
                }
            }
        }
        return initMode;
    }

    /**
     * Counts the remaining number of values in grid2DSquareCellInt equal to
     * value from cell given by row p and column q counting in row major order.
     *
     *
     * @param grid2DSquareCellInt
     * @param row The row index of the cell from which counting starts
     * @param col The column index of the cell from which counting starts
     * @param _NRows The number of rows in grid2DSquareCellInt.
     * @param _NCols The number of columns in grid2DSquareCellInt.
     * @param value The value to be counted.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public long count(
            Grid2DSquareCellInt grid2DSquareCellInt,
            long row,
            long col,
            long _NRows,
            long _NCols,
            int value,
            boolean handleOutOfMemoryError) {
        try {
            long result = count(
                    grid2DSquareCellInt,
                    row,
                    col,
                    _NRows,
                    _NCols,
                    value);
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return count(
                        grid2DSquareCellInt,
                        row,
                        col,
                        _NRows,
                        _NCols,
                        value,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Counts the remaining number of values in grid2DSquareCellInt equal to
     * value from cell given by row p and column q counting in row major order.
     *
     *
     * @param grid2DSquareCellInt
     * @param row The row index of the cell from which counting starts
     * @param col The column index of the cell from which counting starts
     * @param _NRows The number of rows in grid2DSquareCellInt.
     * @param _NCols The number of columns in grid2DSquareCellInt.
     * @param value The value to be counted.
     * @return
     */
    protected static long count(
            Grid2DSquareCellInt grid2DSquareCellInt,
            long row,
            long col,
            long _NRows,
            long _NCols,
            int value) {
        long count = 1L;
        int thisValue;
        boolean handleOutOfMemoryError = true;
        // Do remainder of the row of grid2DSquareCellInt
        for (col++; col < _NCols; col++) {
            thisValue = grid2DSquareCellInt.getCell(
                    row,
                    col,
                    handleOutOfMemoryError);
            if (thisValue == value) {
                count++;
            }
        }
        // Do remainder of the grid2DSquareCellInt
        for (row++; row < _NRows; row++) {
            for (col = 0; col < _NCols; col++) {
                thisValue = grid2DSquareCellInt.getCell(
                        row,
                        col,
                        handleOutOfMemoryError);
                if (thisValue == value) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Counts the remaining number of values in grid2DSquareCellDouble equal to
     * value from cell given by row p and column q counting in row major order.
     *
     *
     * @param grid2DSquareCellDouble
     * @param row The row index of the cell from which counting starts
     * @param col The column index of the cell from which counting starts
     * @param _NRows The number of rows in grid2DSquareCellInt.
     * @param _NCols The number of columns in grid2DSquareCellInt.
     * @param value The value to be counted.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public long count(
            Grid2DSquareCellDouble grid2DSquareCellDouble,
            long row,
            long col,
            long _NRows,
            long _NCols,
            double value,
            boolean handleOutOfMemoryError) {
        try {
            long result = count(
                    grid2DSquareCellDouble,
                    row,
                    col,
                    _NRows,
                    _NCols,
                    value);
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return count(
                        grid2DSquareCellDouble,
                        row,
                        col,
                        _NRows,
                        _NCols,
                        value,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Counts the remaining number of values in grid2DSquareCellDouble equal to
     * value from cell given by row p and column q counting in row major order.
     *
     *
     * @param grid2DSquareCellDouble
     * @param row The row index of the cell from which counting starts
     * @param col The column index of the cell from which counting starts
     * @param _NRows The number of rows in grid2DSquareCellInt.
     * @param _NCols The number of columns in grid2DSquareCellInt.
     * @param value The value to be counted.
     * @return
     */
    protected static long count(
            Grid2DSquareCellDouble grid2DSquareCellDouble,
            long row,
            long col,
            long _NRows,
            long _NCols,
            double value) {
        long count = 1L;
        double thisValue;
        boolean handleOutOfMemoryError = true;
        // Do remainder of the row of grid2DSquareCellDouble
        for (col++; col < _NCols; col++) {
            thisValue = grid2DSquareCellDouble.getCell(
                    row,
                    col,
                    handleOutOfMemoryError);
            if (thisValue == value) {
                count++;
            }
        }
        // Do remainder of the grid2DSquareCellDouble
        for (row++; row < _NRows; row++) {
            for (col = 0; col < _NCols; col++) {
                thisValue = grid2DSquareCellDouble.getCell(
                        row,
                        col,
                        handleOutOfMemoryError);
                if (thisValue == value) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * For returning the arithmetic mean of all non noDataValues as a BigDecimal
     *
     * @param numberOfDecimalPlaces The number of places for which the standard
     * deviation will be correct.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getArithmeticMeanBigDecimal(numberOfDecimalPlaces);
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getArithmeticMeanBigDecimal(
                        numberOfDecimalPlaces,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the arithmetic mean of all non noDataValues as a BigDecimal
     *
     * @param numberOfDecimalPlaces The number of decimal places to which the
     * result is precise.
     * @return
     */
    protected abstract BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces);

    /**
     * Returns the standard deviation of all non noDataValues as a BigDecimal.
     *
     * @param numberOfDecimalPlaces The number of places for which the standard
     * deviation will be correct.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public BigDecimal getStandardDeviationBigDecimal(
            int numberOfDecimalPlaces,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getStandardDeviationBigDecimal(numberOfDecimalPlaces);
            Grid2DSquareCell.env.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.Grid2DSquareCell.env.clear_MemoryReserve();
                if (this.Grid2DSquareCell.env.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this.Grid2DSquareCell.env.init_MemoryReserve(
                        Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse);
                return getStandardDeviationBigDecimal(
                        numberOfDecimalPlaces,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the standard deviation of all non noDataValues as a double.
     *
     * @param numberOfDecimalPlaces The number of places for which the standard
     * deviation will be correct. TODO: test
     * @return
     */
    protected BigDecimal getStandardDeviationBigDecimal(
            int numberOfDecimalPlaces) {
        boolean handleOutOfMemoryError = true;
        BigDecimal stdev = new BigDecimal("0");
        // ( 2 * numberOfDecimalPlaces ) is a guess.
        BigDecimal mean = getArithmeticMeanBigDecimal(
                2 * numberOfDecimalPlaces,
                handleOutOfMemoryError);
        int chunkNrows = this.Grid2DSquareCell._ChunkNRows;
        int chunkNcols = this.Grid2DSquareCell._ChunkNCols;
        int nChunkRows = this.Grid2DSquareCell.get_NChunkRows(
                handleOutOfMemoryError);
        int nChunkCols = this.Grid2DSquareCell.get_NChunkCols(
                handleOutOfMemoryError);
        int chunkRowIndex;
        int chunkColIndex;
        int chunkRow;
        int chunkCol;
        boolean isInGrid;
        if (this.Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class) {
            Grid2DSquareCellInt grid2DSquareCellInt
                    = (Grid2DSquareCellInt) this.Grid2DSquareCell;
            int value;
            int noDataValue = grid2DSquareCellInt.getNoDataValue(
                    handleOutOfMemoryError);
            BigDecimal nonNoDataValueCountBigDecimal = new BigDecimal("-1");
            BigDecimal differenceFromMean;
            for (chunkRowIndex = 0; chunkRowIndex < nChunkRows; chunkRowIndex++) {
                for (chunkColIndex = 0; chunkColIndex < nChunkCols; chunkColIndex++) {
                    AbstractGrid2DSquareCellIntChunk chunk
                            = grid2DSquareCellInt.getGrid2DSquareCellIntChunk(
                                    chunkRowIndex,
                                    chunkColIndex,
                                    handleOutOfMemoryError);
                    for (chunkRow = 0; chunkRow < chunkNrows; chunkRow++) {
                        for (chunkCol = 0; chunkCol < chunkNcols; chunkCol++) {
                            isInGrid = grid2DSquareCellInt.isInGrid(
                                    ((long) chunkRowIndex * (long) chunkNrows) + (long) chunkRow,
                                    ((long) chunkColIndex * (long) chunkNcols) + (long) chunkCol,
                                    handleOutOfMemoryError);
                            if (isInGrid) {
                                value = chunk.getCell(
                                        chunkRowIndex,
                                        chunkColIndex,
                                        noDataValue,
                                        handleOutOfMemoryError);
                                if (value != noDataValue) {
                                    differenceFromMean = new BigDecimal(value).subtract(mean);
                                    stdev = stdev.add(differenceFromMean.multiply(differenceFromMean));
                                    nonNoDataValueCountBigDecimal
                                            = nonNoDataValueCountBigDecimal.add(BigDecimal.ONE);
                                }
                            }
                        }
                    }
                }
            }
            if (nonNoDataValueCountBigDecimal.compareTo(BigDecimal.ONE) != 1) {
                return stdev;
            }
            BigDecimal bigDecimal0 = stdev.divide(
                    nonNoDataValueCountBigDecimal,
                    numberOfDecimalPlaces,
                    BigDecimal.ROUND_HALF_EVEN);
            Generic_BigDecimal t_Generic_BigDecimal
                    = Grid2DSquareCell.env.get_Generic_BigDecimal();
            return Generic_BigDecimal.sqrt(
                    bigDecimal0,
                    numberOfDecimalPlaces,
                    t_Generic_BigDecimal.get_RoundingMode());
        } else {
            //this.Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class
            Grid2DSquareCellDouble grid2DSquareCellDouble
                    = (Grid2DSquareCellDouble) this.Grid2DSquareCell;
            double value;
            double noDataValue = grid2DSquareCellDouble._NoDataValue;
            BigDecimal nonNoDataValueCountBigDecimal = new BigDecimal("-1");
            BigDecimal differenceFromMean;
            for (chunkRowIndex = 0; chunkRowIndex < nChunkRows; chunkRowIndex++) {
                for (chunkColIndex = 0; chunkColIndex < nChunkCols; chunkColIndex++) {
                    AbstractGrid2DSquareCellDoubleChunk chunk
                            = grid2DSquareCellDouble.getGrid2DSquareCellDoubleChunk(
                                    chunkRowIndex,
                                    chunkColIndex,
                                    handleOutOfMemoryError);
                    for (chunkRow = 0; chunkRow < chunkNrows; chunkRow++) {
                        for (chunkCol = 0; chunkCol < chunkNcols; chunkCol++) {
                            isInGrid = grid2DSquareCellDouble.isInGrid(
                                    ((long) chunkRowIndex * (long) chunkNrows) + (long) chunkRow,
                                    ((long) chunkColIndex * (long) chunkNcols) + (long) chunkCol,
                                    handleOutOfMemoryError);
                            if (isInGrid) {
                                value = chunk.getCell(
                                        chunkRowIndex,
                                        chunkColIndex,
                                        noDataValue,
                                        handleOutOfMemoryError);
                                if (value != noDataValue) {
                                    differenceFromMean = new BigDecimal(value).subtract(mean);
                                    stdev = stdev.add(differenceFromMean.multiply(differenceFromMean));
                                    nonNoDataValueCountBigDecimal
                                            = nonNoDataValueCountBigDecimal.add(BigDecimal.ONE);
                                }
                            }
                        }
                    }
                }
            }
            if (nonNoDataValueCountBigDecimal.compareTo(BigDecimal.ONE) != 1) {
                return stdev;
            }
            BigDecimal bigDecimal0 = stdev.divide(
                    nonNoDataValueCountBigDecimal,
                    numberOfDecimalPlaces,
                    BigDecimal.ROUND_HALF_EVEN);
            Generic_BigDecimal t_Generic_BigDecimal
                    = Grid2DSquareCell.env.get_Generic_BigDecimal();
            return Generic_BigDecimal.sqrt(
                    bigDecimal0,
                    numberOfDecimalPlaces,
                    t_Generic_BigDecimal.get_RoundingMode());
        }
    }

    public Object[] getQuantileClassMap(
            int nClasses,
            boolean handleOutOfMemoryError) {
        Object[] result;
        result = new Object[3];
        if (this.Grid2DSquareCell instanceof Grid2DSquareCellDouble) {
            Grid2DSquareCellDouble g;
            g = (Grid2DSquareCellDouble) this.Grid2DSquareCell;
            TreeMap<Integer, Double> minDouble;
            TreeMap<Integer, Double> maxDouble;
            minDouble = new TreeMap<Integer, Double>();
            maxDouble = new TreeMap<Integer, Double>();
            for (int i = 1; i < nClasses; i++) {
                minDouble.put(i, Double.POSITIVE_INFINITY);
                maxDouble.put(i, Double.NEGATIVE_INFINITY);
            }
            result[0] = minDouble;
            result[1] = maxDouble;
            long nonZeroAndNonNoDataValueCount;
            nonZeroAndNonNoDataValueCount = getNonZeroAndNonNoDataValueCountLong(
                    handleOutOfMemoryError);
            System.out.println("nonZeroAndNonNoDataValueCount " + nonZeroAndNonNoDataValueCount);
            long numberOfValuesInEachClass;
            numberOfValuesInEachClass = nonZeroAndNonNoDataValueCount / nClasses;
            if (nonZeroAndNonNoDataValueCount % nClasses != 0) {
                numberOfValuesInEachClass += 1;
            }
            double noDataValue;
            noDataValue = g.get_NoDataValue(handleOutOfMemoryError);
            TreeMap<Integer, Long> classCounts;
            classCounts = new TreeMap<Integer, Long>();
            for (int i = 1; i < nClasses; i++) {
                classCounts.put(i, 0L);
            }
            int classToFill = 0;
            boolean firstValue = true;
//        TreeMap<Integer,TreeMap<Double,HashSet<AbstractGrid2DSquareCell.CellID>>> classMap;
//        classMap = new TreeMap<Integer,TreeMap<Double,HashSet<AbstractGrid2DSquareCell.CellID>>>();
            TreeMap<Integer, TreeMap<Double, Long>> classMap;
            classMap = new TreeMap<Integer, TreeMap<Double, Long>>();
            for (int i = 0; i < nClasses; i++) {
                classMap.put(i, new TreeMap<Double, Long>());
            }
            result[2] = classMap;
            int count = 0;
            //long valueID = 0;
            Iterator<Double> ite;
            ite = g.iterator(handleOutOfMemoryError);
            while (ite.hasNext()) {
                double value;
                value = ite.next();

//                if (valueID == 27782) {
//                    //0.0087890625
//                    int debug = 1;
//                }
                if (!(value == 0.0d || value == noDataValue)) {
                    if (count % numberOfValuesInEachClass == 0) {
                        System.out.println(count + " out of " + nonZeroAndNonNoDataValueCount);
                    }
                    count++;
                    if (firstValue) {
                        minDouble.put(0, value);
                        maxDouble.put(0, value);
                        classCounts.put(0, 1L);
                        classMap.get(0).put(value, 1L);
                        if (numberOfValuesInEachClass < 2) {
                            classToFill = 1;
                        }
                        firstValue = false;
                    } else {
                        int[] valueClass;
                        if (classToFill == nClasses) {
                            // Strange, but this can happen!?
                            classToFill--;
                        }
                        valueClass = getValueClass(
                                value,
                                classMap,
                                minDouble,
                                maxDouble,
                                classCounts,
                                numberOfValuesInEachClass,
                                classToFill);
                        classToFill = valueClass[1];
//                        boolean passed;
//                        passed = checkMaps(
//                                classMap,
//                                minDouble,
//                                maxDouble,
//                                classCounts,
//                                classToFill,
//                                numberOfValuesInEachClass);
//                        if (!passed) {
//                            int debug = 1;
//                            System.out.println("valueID " + valueID);
//                        }
                    }
                }
//                valueID ++;
            }
            return result;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private boolean checkMaps(
            TreeMap<Integer, TreeMap<Double, Long>> classMap,
            TreeMap<Integer, Double> minDouble,
            TreeMap<Integer, Double> maxDouble,
            TreeMap<Integer, Long> classCounts,
            int classToFill,
            long numberOfValuesInEachClass) {
        boolean result = true;
        Iterator<Integer> ite;
        ite = classMap.keySet().iterator();
        while (ite.hasNext()) {
            Integer key;
            key = ite.next();
            if (key <= classToFill) {
                double minFromMinDouble;
                double maxFromMaxDouble;
                long countFromClassCounts;
                double minFromClassMap;
                double maxFromClassMap;
                long countFromClassMap;
                minFromMinDouble = minDouble.get(key);
                maxFromMaxDouble = maxDouble.get(key);
                countFromClassCounts = classCounts.get(key);
                TreeMap<Double, Long> classMapClassMap;
                classMapClassMap = classMap.get(key);
                minFromClassMap = classMapClassMap.firstKey();
                maxFromClassMap = classMapClassMap.lastKey();
                Collection<Long> values;
                values = classMapClassMap.values();
                countFromClassMap = getSum(values);
                if (!(minFromMinDouble == minFromClassMap)) {
                    int debug = 0;
                    result = false;
                }
                if (!(maxFromMaxDouble == maxFromClassMap)) {
                    int debug = 1;
                    result = false;
                }
                if (!(countFromClassCounts == countFromClassMap)) {
                    int debug = 2;
                    result = false;
                }
                if (countFromClassCounts > numberOfValuesInEachClass) {
                    if (!(countFromClassCounts - classMapClassMap.lastEntry().getValue() < numberOfValuesInEachClass)) {
                        int debug = 3;
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Move this to generic utilities.
     *
     * @param c
     * @return
     */
    public long getSum(Collection<Long> c) {
        long result = 0L;
        Iterator<Long> ite;
        ite = c.iterator();
        while (ite.hasNext()) {
            long v = ite.next();
            result += v;
        }
        return result;
    }

    /**
     * @param value
     * @param minDouble
     * @param maxDouble
     * @param classCounts
     * @param desiredNumberOfValuesInEachClass
     * @param classToFill
     * @return result[0] is the class, result[1] is the classToFill which may or
     * may not change from what is passed in.
     */
    private int[] getValueClass(
            double value,
            TreeMap<Integer, TreeMap<Double, Long>> classMap,
            TreeMap<Integer, Double> minDouble,
            TreeMap<Integer, Double> maxDouble,
            TreeMap<Integer, Long> classCounts,
            long desiredNumberOfValuesInEachClass,
            int classToFill) {
        int[] result;
        result = new int[2];
        long classToFillCount;
        classToFillCount = classCounts.get(classToFill);
        double maxValueOfClassToFill;
        maxValueOfClassToFill = maxDouble.get(classToFill);
//        if (maxDouble.get(classToFill) != null) {
//            maxValueOfClassToFill = maxDouble.get(classToFill);
//        } else {
//            maxValueOfClassToFill = Double.NEGATIVE_INFINITY;
//        }
        // Special cases
        // Case 1:
        if (value > maxValueOfClassToFill) {
            maxDouble.put(classToFill, value);
            classToFillCount += 1;
            addToMapCounts(value, classToFill, classMap);
            addToCount(classToFill, classCounts);
            result[0] = classToFill;
            //if (classToFillCount >= desiredNumberOfValuesInEachClass) {
            result[1] = checkClassToFillAndPropagation(
                    result,
                    classToFill,
                    classToFillCount,
                    classMap,
                    minDouble,
                    maxDouble,
                    classCounts,
                    desiredNumberOfValuesInEachClass,
                    classToFill);
//            } else {
//                result[1] = classToFill;
//            }
            return result;
        }
        // Case 2:
        if (value == maxValueOfClassToFill) {
            classToFillCount += 1;
            addToMapCounts(value, classToFill, classMap);
            addToCount(classToFill, classCounts);
            result[0] = classToFill;
            //if (classToFillCount >= desiredNumberOfValuesInEachClass) {
            result[1] = checkClassToFillAndPropagation(
                    result,
                    classToFill,
                    classToFillCount,
                    classMap,
                    minDouble,
                    maxDouble,
                    classCounts,
                    desiredNumberOfValuesInEachClass,
                    classToFill);
//            } else {
//                result[1] = classToFill;
//            }
            return result;
        }
//        // Case 3:
//        double minValueOfClass0;
//        minValueOfClass0 = minDouble.get(0);
//        if (value < minValueOfClass0) {
//            minDouble.put(0, value);
//            long class0Count;
//            class0Count = classCounts.get(0);
//            if (class0Count < desiredNumberOfValuesInEachClass) {
//                result[0] = classToFill; // Which should be 0
//                addToMapCounts(value, classToFill, classMap);
//                addToCount(classToFill, classCounts);
//                classToFillCount += 1;
//                if (classToFillCount >= desiredNumberOfValuesInEachClass) {
//                    result[1] = classToFill + 1;
//                } else {
//                    result[1] = classToFill;
//                }
//                return result;
//            } else {
//                classToFillCount += 1;
//                result[0] = 0;
//                checkValueCounts(
//                        result,
//                        0,
//                        classToFillCount,
//                        classMap,
//                        minDouble,
//                        maxDouble,
//                        classCounts,
//                        desiredNumberOfValuesInEachClass,
//                        classToFill);
//                return result;
//            }
//        }
        // General Case
        // 1. Find which class the value sits in.
        // 2. If the value already exists, add to the count, else add to the map
        // 3. Check the top of the class value counts. If by moving these up the 
        //    class would not contain enough values finish, otherwise do the following:
        //    a) move the top values up to the bottom of the next class.
        //      Modify the max value in this class
        //      Modify the min value in the next class
        //      Repeat Step 3 for the next class

        // General Case
        // 1. Find which class the value sits in.
        int classToCheck = classToFill;
//        double maxToCheck0;
//        double minToCheck0;
//        maxToCheck0 = maxDouble.get(classToCheck);
//        minToCheck0 = minDouble.get(classToCheck);
        double maxToCheck;
        double minToCheck;
        maxToCheck = maxDouble.get(classToCheck);
        minToCheck = minDouble.get(classToCheck);
        boolean foundClass = false;
        while (!foundClass) {
            if (value >= minToCheck && value <= maxToCheck) {
                result[0] = classToCheck;
                foundClass = true;
            } else {
                classToCheck--;
                if (classToCheck < 1) {
                    if (classToCheck < 0) {
                        // This means that value is less than min value so set min.
                        minDouble.put(0, value);
                    }
                    result[0] = 0;
                    classToCheck = 0;
                    foundClass = true;
                } else {
                    maxToCheck = minToCheck; // This way ensures there are no gaps.
                    minToCheck = minDouble.get(classToCheck);
                }
            }
        }
        long classToCheckCount;
        classToCheckCount = classCounts.get(classToCheck);
        // 2. If the value already exists, add to the count, else add to the map
        // and counts and ensure maxDouble and minDouble are correct (which has 
        // to be done first)
        maxToCheck = maxDouble.get(classToCheck);
        if (value > maxToCheck) {
            maxDouble.put(classToCheck, value);
        }
        minToCheck = minDouble.get(classToCheck);
        if (value < minToCheck) {
            minDouble.put(classToCheck, value);
        }
        addToMapCounts(value, classToCheck, classMap);
        addToCount(classToCheck, classCounts);
        classToCheckCount = classCounts.get(classToCheck);
        // 3. Check the top of the class value counts. If by moving these up the 
        //    class would not contain enough values finish, otherwise do the following:
        //    a) move the top values up to the bottom of the next class.
        //      Modify the max value in this class
        //      Modify the min value in the next class
        //      Repeat Step 3 for the next class
        //result[1] = checkValueCounts(
        checkClassToFillAndPropagation(
                result,
                classToCheck,
                classToCheckCount,
                classMap,
                minDouble,
                maxDouble,
                classCounts,
                desiredNumberOfValuesInEachClass,
                classToFill);
        //classCounts.put(classToCheck, classToFillCount + 1);
        return result;
    }

    private void addToCount(
            int index,
            TreeMap<Integer, Long> classCounts) {
        long count;
        count = classCounts.get(index);
        count++;
        classCounts.put(index, count);
    }

    private void addToMapCounts(
            double value,
            int classToCount,
            TreeMap<Integer, TreeMap<Double, Long>> classMap) {
        TreeMap<Double, Long> classToCheckMap;
        classToCheckMap = classMap.get(classToCount);
        if (classToCheckMap.containsKey(value)) {
            long count;
            count = classToCheckMap.get(value);
            count++;
            classToCheckMap.put(value, count);
        } else {
            classToCheckMap.put(value, 1L);
        }
    }

    /**
     *
     * @param result
     * @param classToCheck
     * @param classToCheckCount
     * @param classMap
     * @param minDouble
     * @param maxDouble
     * @param classCounts
     * @param desiredNumberOfValuesInEachClass
     * @param classToFill
     * @return Value for classToFill (may be the same as what is passed in).
     */
    private int checkClassToFillAndPropagation(
            int[] result,
            int classToCheck,
            long classToCheckCount,
            TreeMap<Integer, TreeMap<Double, Long>> classMap,
            TreeMap<Integer, Double> minDouble,
            TreeMap<Integer, Double> maxDouble,
            TreeMap<Integer, Long> classCounts,
            long desiredNumberOfValuesInEachClass,
            int classToFill) {
        long classToCheckCountOfMaxValue;
        double classToCheckMaxValue;
        classToCheckMaxValue = maxDouble.get(classToCheck);
        TreeMap<Double, Long> classToCheckMap;
        classToCheckMap = classMap.get(classToCheck);
        classToCheckCountOfMaxValue = classToCheckMap.get(classToCheckMaxValue);
        if (classToCheckCount - classToCheckCountOfMaxValue < desiredNumberOfValuesInEachClass) {
            result[1] = classToFill;
        } else {
            int nextClassToCheck;
            nextClassToCheck = classToCheck + 1;
            // Push the values up into the next class, adjust the min and max values, checkValueCounts again.
            // Push the values up into the next class
            // --------------------------------------
            // 1. Remove
            classCounts.put(classToCheck, classToCheckCount - classToCheckCountOfMaxValue);
            classToCheckMap.remove(classToCheckMaxValue);
            // 2. Add
            TreeMap<Double, Long> nextClassToCheckMap;
            nextClassToCheckMap = classMap.get(nextClassToCheck);
            nextClassToCheckMap.put(classToCheckMaxValue, classToCheckCountOfMaxValue);
            // 2.1 Adjust min and max values
            maxDouble.put(classToCheck, classToCheckMap.lastKey());
//            try {
//            maxDouble.put(classToCheck, classToCheckMap.lastKey());
//            } catch (NoSuchElementException e) {
//                int debug = 1;
//            }
            minDouble.put(nextClassToCheck, classToCheckMaxValue);
            long nextClassToCheckCount;
            nextClassToCheckCount = classCounts.get(nextClassToCheck);
            if (nextClassToCheckCount == 0) {
                maxDouble.put(nextClassToCheck, classToCheckMaxValue);
                // There should not be any value bigger in nextClasstoCheck.
            }
            // 2.2 Add to classCounts
            nextClassToCheckCount += classToCheckCountOfMaxValue;
            classCounts.put(nextClassToCheck, nextClassToCheckCount);
            if (classToFill < nextClassToCheck) {
                classToFill = nextClassToCheck;
            }
            // 2.3. Check this class again then check the next class
            classToCheckCount = classCounts.get(classToCheck);
            result[1] = checkClassToFillAndPropagation(
                    result,
                    classToCheck,
                    classToCheckCount,
                    classMap,
                    minDouble,
                    maxDouble,
                    classCounts,
                    desiredNumberOfValuesInEachClass,
                    classToFill);
            // nextClassToCheckCount needs to be got again as it may have changed!
            nextClassToCheckCount = classCounts.get(nextClassToCheck);
            result[1] = checkClassToFillAndPropagation(
                    result,
                    nextClassToCheck,
                    nextClassToCheckCount,
                    classMap,
                    minDouble,
                    maxDouble,
                    classCounts,
                    desiredNumberOfValuesInEachClass,
                    classToFill);
        }
        return result[1];
    }
}
