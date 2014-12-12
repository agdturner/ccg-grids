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
package uk.ac.leeds.ccg.andyt.grids.core;

import gnu.trove.TDoubleHashSet;
import gnu.trove.TIntHashSet;
import java.io.Serializable;
import java.math.BigInteger;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell.CellID;

/**
 * An abstract class to be extended and methods overridden to provide
 * statistics about the data in AbstractGrid2DSquareCell and
 * Grid2DSquareCellChunkAbstract more optimally.
 */
public abstract class AbstractGridStatistics
        implements Serializable, GridStatisticsInterface {

    private static final long serialVersionUID = 1L;
    /**
     * A reference to the AbstractGrid2DSquareCell this is for.
     */
    protected AbstractGrid2DSquareCell _Grid2DSquareCell;
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
        BigDecimal bigDecimalIntegerMAX_VALUE =
                new BigDecimal(Integer.MAX_VALUE);
        this.minBigDecimal =
                bigDecimalIntegerMAX_VALUE.add(bigDecimalIntegerMAX_VALUE);
        this.minCountBigInteger = new BigInteger("0");
        //this.maxBigDecimal = new BigDecimal( Double.NEGATIVE_INFINITY );
        //this.minBigDecimal = new BigDecimal(
        //        "-99999999999999999999999999999999999999999" );
        //Integer.MIN_VALUE used instead of Double.MIN_VALUE
        BigDecimal bigDecimalIntegerMIN_VALUE =
                new BigDecimal(Integer.MIN_VALUE);
        this.maxBigDecimal =
                bigDecimalIntegerMIN_VALUE.add(bigDecimalIntegerMIN_VALUE);
        this.maxCountBigInteger = new BigInteger("0");
    }

    /**
     * For intitialisation
     * @param grid2DSquareCell
     */
    protected void init(
            AbstractGrid2DSquareCell grid2DSquareCell) {
        this._Grid2DSquareCell = grid2DSquareCell;
        this._Grid2DSquareCell._GridStatistics = this;
        init();
    }

    /**
     * Updates fields from _GridStatistics except this._Grid2DSquareCell
     * 
     * 
     * @param _GridStatistics the _GridStatistics instance which fields are used
     *   to update this.
     */
    protected void update(
            AbstractGridStatistics _GridStatistics) {
        this.nonNoDataValueCountBigInteger =
                _GridStatistics.nonNoDataValueCountBigInteger;
        this.sumBigDecimal = _GridStatistics.sumBigDecimal;
        this.minBigDecimal = _GridStatistics.minBigDecimal;
        this.minCountBigInteger = _GridStatistics.minCountBigInteger;
        this.maxBigDecimal = _GridStatistics.maxBigDecimal;
        this.maxCountBigInteger = _GridStatistics.maxCountBigInteger;
    }

    /**
     * Updates fields (statistics) by going through all values in
     * this.grid2DSquareCellAbstract if they might not be up to date.
     * (NB. After calling this it is inexpensive to convert to GridStatistics0.)
     */
    protected abstract void update();

    /**
     * Updates fields (statistics) by going through all values in
     * this.grid2DSquareCellAbstract if they might not be up to date.
     * (NB. After calling this it is inexpensive to convert to GridStatistics0.)
     * This is called from extending classes activated via update. It is here
     * to avoid duplication in the code. The parameters are specified in order
     * to distinguish this method from update().
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
//        System.out.println("this._Grid2DSquareCell.toString()" + this._Grid2DSquareCell.toString() );
        if (this._Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class) {
            //if ( this._Grid2DSquareCell instanceof Grid2DSquareCellInt ) {
            Grid2DSquareCellInt grid2DSquareCellInt =
                    (Grid2DSquareCellInt) _Grid2DSquareCell;
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
                        this.nonNoDataValueCountBigInteger =
                                this.nonNoDataValueCountBigInteger.add(BigInteger.ONE);
                        this.sumBigDecimal = this.sumBigDecimal.add(cellBigDecimal);
                        if (cellBigDecimal.compareTo(this.minBigDecimal) == -1) {
                            this.minCountBigInteger = BigInteger.ONE;
                            this.minBigDecimal = cellBigDecimal;
                        } else {
                            if (cellBigDecimal.compareTo(this.minBigDecimal) == 0) {
                                this.minCountBigInteger =
                                        this.minCountBigInteger.add(BigInteger.ONE);
                            }
                        }
                        if (cellBigDecimal.compareTo(this.maxBigDecimal) == 1) {
                            this.maxCountBigInteger = BigInteger.ONE;
                            this.maxBigDecimal = cellBigDecimal;
                        } else {
                            if (cellBigDecimal.compareTo(this.maxBigDecimal) == 0) {
                                this.maxCountBigInteger =
                                        this.maxCountBigInteger.add(BigInteger.ONE);
                            }
                        }
                    }
                }
            }
        } else {
            //if ( this._Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class ) {
            //if ( this._Grid2DSquareCell instanceof Grid2DSquareCellDouble ) {
            Grid2DSquareCellDouble grid2DSquareCellDouble =
                    (Grid2DSquareCellDouble) _Grid2DSquareCell;
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
                        
                        // Debug
                        if (cellDouble == Double.NEGATIVE_INFINITY ||
                                cellDouble == Double.NaN || 
                                cellDouble == Double.POSITIVE_INFINITY) {
                            System.out.println("debug row " + row  + " col " + col);
                        }
                        
                        cellBigDecimal = new BigDecimal(cellDouble);
                        this.nonNoDataValueCountBigInteger =
                                this.nonNoDataValueCountBigInteger.add(BigInteger.ONE);
                        this.sumBigDecimal = this.sumBigDecimal.add(cellBigDecimal);
                        if (cellBigDecimal.compareTo(this.minBigDecimal) == -1) {
                            this.minCountBigInteger = BigInteger.ONE;
                            this.minBigDecimal = cellBigDecimal;
                        } else {
                            if (cellBigDecimal.compareTo(this.minBigDecimal) == 0) {
                                this.minCountBigInteger =
                                        this.minCountBigInteger.add(BigInteger.ONE);
                            }
                        }
                        if (cellBigDecimal.compareTo(this.maxBigDecimal) == 1) {
                            this.maxCountBigInteger = BigInteger.ONE;
                            this.maxBigDecimal = cellBigDecimal;
                        } else {
                            if (cellBigDecimal.compareTo(this.maxBigDecimal) == 0) {
                                this.maxCountBigInteger =
                                        this.maxCountBigInteger.add(BigInteger.ONE);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a String describing this instance
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public String toString(
            boolean handleOutOfMemoryError) {
        try {
            String result = getDescription();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return toString(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the name of the this class
     * @return 
     */
    protected String getName() {
        return this.getClass().getName();
        //return "GridStatistics0";
        //return "GridStatistics1";
    }

    /**
     * Returns a string describing this instance
     * @return 
     */
    protected String getDescription() {
        String result = this.getName();
        result +=
                "( nonNoDataValueCountBigInteger( "
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final BigInteger getNonNoDataValueCountBigInteger(
            boolean handleOutOfMemoryError) {
        try {
            BigInteger result = getNonNoDataValueCountBigInteger();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getNonNoDataValueCountBigInteger(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the number of cells with noDataValues as a BigInteger.
     * @return 
     */
    protected abstract BigInteger getNonNoDataValueCountBigInteger();

    /**
     * For returning the number of cells with noDataValues as a long.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final long getNonNoDataValueCountLong(
            boolean handleOutOfMemoryError) {
        try {
            long result = getNonNoDataValueCountLong();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getNonNoDataValueCountLong(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the number of cells with noDataValues as a long.
     * @return 
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
     * For returning the number of cells with noDataValues as a int.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final int getNonNoDataValueCountInt(
            boolean handleOutOfMemoryError) {
        try {
            int result = getNonNoDataValueCountInt();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getNonNoDataValueCountInt(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the number of cells with noDataValues as a int.
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final BigDecimal getSumBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getSumBigDecimal();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getSumBigDecimal(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a BigDecimal.
     * @return 
     */
    protected abstract BigDecimal getSumBigDecimal();

    /**
     * For returning the sum of all non noDataValues as a BigInteger.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final BigInteger getSumBigInteger(
            boolean handleOutOfMemoryError) {
        try {
            BigInteger result = getSumBigInteger();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getSumBigInteger(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a BigInteger.
     * @return 
     */
    protected final BigInteger getSumBigInteger() {
        boolean handleOutOfMemoryError = true;
        return getSumBigDecimal(handleOutOfMemoryError).toBigIntegerExact();
    }

    /**
     * For returning the sum of all non noDataValues as a double.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final double getSumDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getSumDouble();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getSumDouble(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a double.
     * If this.sumBigDecimal is to big/small to represent as a double then an
     * Arithmetic Exception is thrown and Double.POSITIVE_INFINITY or
     * Double.NEGATIVE_INFINITY are returned as appropriate.
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final long getSumLong(
            boolean handleOutOfMemoryError) {
        try {
            long result = getSumLong();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getSumLong(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a long.
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final int getSumInt(
            boolean handleOutOfMemoryError) {
        try {
            int result = getSumInt();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getSumInt(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the sum of all non noDataValues as a int.
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final BigDecimal getMinBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getMinBigDecimal();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getMinBigDecimal(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a BigInteger.
     * @return 
     */
    protected abstract BigDecimal getMinBigDecimal();

    /**
     * For returning the minimum of all non noDataValues as a BigInteger.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final BigInteger getMinBigInteger(
            boolean handleOutOfMemoryError) {
        try {
            BigInteger result = getMinBigInteger();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getMinBigInteger(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a BigInteger.
     * @return 
     */
    protected final BigInteger getMinBigInteger() {
        return getMinBigDecimal().toBigIntegerExact();
    }

    /**
     * For returning the minimum of all non noDataValues as a double.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final double getMinDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getMinDouble();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getMinDouble(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a double.
     * If this.minBigDecimal is to big/small to represent as a double then an
     * Arithmetic Exception is thrown and Double.POSITIVE_INFINITY or
     * Double.NEGATIVE_INFINITY are returned as appropriate.
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final long getMinLong(
            boolean handleOutOfMemoryError) {
        try {
            long result = getMinLong();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getMinLong(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a long
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final int getMinInt(
            boolean handleOutOfMemoryError) {
        try {
            int result = getMinInt();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getMinInt(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a int.
     * @return 
     */
    protected final int getMinInt() {
        return getMinBigInteger().intValue();
    }

    /**
     * For returning the maximum of all non noDataValues as a BigInteger.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final BigInteger getMaxBigInteger(
            boolean handleOutOfMemoryError) {
        try {
            BigInteger result = getMaxBigInteger();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getMaxBigInteger(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the maximum of all non noDataValues as a BigInteger.
     * @return 
     */
    protected final BigInteger getMaxBigInteger() {
        return getMaxBigDecimal().toBigIntegerExact();
    }

    /**
     * For returning the maximum of all non noDataValues as a BigDecimal.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final BigDecimal getMaxBigDecimal(
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getMaxBigDecimal();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getMaxBigDecimal(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the maximum of all non noDataValues as a BigInteger.
     * @return 
     */
    protected abstract BigDecimal getMaxBigDecimal();

    /**
     * For returning the maximum of all non noDataValues as a double.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final double getMaxDouble(
            boolean handleOutOfMemoryError) {
        try {
            double result = getMaxDouble();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getMaxDouble(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the maximum of all non noDataValues as a double.
     * If this.minBigDecimal is to big/small to represent as a double then an
     * Arithmetic Exception is thrown and Double.POSITIVE_INFINITY or
     * Double.NEGATIVE_INFINITY are returned as appropriate.
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final long getMaxLong(
            boolean handleOutOfMemoryError) {
        try {
            long result = getMaxLong();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getMaxLong(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a long.
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public final int getMaxInt(
            boolean handleOutOfMemoryError) {
        try {
            int result = getMaxInt();
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
                return getMaxInt(handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * For returning the minimum of all non noDataValues as a int.
     * @return 
     */
    protected final int getMaxInt() {
        boolean handleOutOfMemoryError = true;
        return getMaxBigDecimal(handleOutOfMemoryError).intValueExact();
    }

    /**
     * For returning the mode of all non noDataValues either as a
     * TDoubleHashSet or as a TIntHashSet respectively depending on
     * if ( this._Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class ) or
     * if ( this._Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class ).
     * TODO:
     * Change for loops so as to look through each chunk in turn.
     * Is it better to use toArray and go through a sorted version?
     * @return 
     */
    protected Object getMode() {
        long _NRows = this._Grid2DSquareCell._NRows;
        long _NCols = this._Grid2DSquareCell._NCols;
        long row;
        long col;
        boolean calculated = false;
        if (this._Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class) {
            Grid2DSquareCellInt grid2DSquareCellInt =
                    (Grid2DSquareCellInt) this._Grid2DSquareCell;
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
                long modeCount = ((Long) tmode[ 0]).longValue();
                mode.add(((Integer) tmode[ 1]).intValue());
                CellID cellID = (CellID) tmode[ 2];
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
            //this._Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class
            Grid2DSquareCellDouble grid2DSquareCellDouble =
                    (Grid2DSquareCellDouble) this._Grid2DSquareCell;
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
                long modeCount = ((Long) tmode[ 0]).longValue();
                mode.add(((Double) tmode[ 1]).doubleValue());
                CellID cellID = (CellID) tmode[ 2];
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
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
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
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
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
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
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
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
     * @param numberOfDecimalPlaces The number of places for which the standard
     *   deviation will be correct.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getArithmeticMeanBigDecimal(numberOfDecimalPlaces);
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
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
     * @param numberOfDecimalPlaces The number of decimal places to which 
     *   the result is precise.
     * @return 
     */
    protected abstract BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces);

    /**
     * Returns the standard deviation of all non noDataValues as a BigDecimal.
     * @param numberOfDecimalPlaces The number of places for which the standard
     *   deviation will be correct.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public BigDecimal getStandardDeviationBigDecimal(
            int numberOfDecimalPlaces,
            boolean handleOutOfMemoryError) {
        try {
            BigDecimal result = getStandardDeviationBigDecimal(numberOfDecimalPlaces);
            _Grid2DSquareCell._Grids_Environment.tryToEnsureThereIsEnoughMemoryToContinue();
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this._Grid2DSquareCell._Grids_Environment.clear_MemoryReserve();
                if (this._Grid2DSquareCell._Grids_Environment.swapToFile_Grid2DSquareCellChunk_Account() < 1L) {
                    throw a_OutOfMemoryError;
                }
                this._Grid2DSquareCell._Grids_Environment.init_MemoryReserve(
                        Grids_Environment.HandleOutOfMemoryErrorFalse);
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
     * @param numberOfDecimalPlaces The number of places for which the standard
     *   deviation will be correct.
     * TODO:
     * test
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
        int chunkNrows = this._Grid2DSquareCell._ChunkNRows;
        int chunkNcols = this._Grid2DSquareCell._ChunkNCols;
        int nChunkRows = this._Grid2DSquareCell.get_NChunkRows(
                handleOutOfMemoryError);
        int nChunkCols = this._Grid2DSquareCell.get_NChunkCols(
                handleOutOfMemoryError);
        int chunkRowIndex;
        int chunkColIndex;
        int chunkRow;
        int chunkCol;
        boolean isInGrid;
        if (this._Grid2DSquareCell.getClass() == Grid2DSquareCellInt.class) {
            Grid2DSquareCellInt grid2DSquareCellInt =
                    (Grid2DSquareCellInt) this._Grid2DSquareCell;
            int value;
            int noDataValue = grid2DSquareCellInt.getNoDataValue(
                    handleOutOfMemoryError);
            BigDecimal nonNoDataValueCountBigDecimal = new BigDecimal("-1");
            BigDecimal differenceFromMean;
            for (chunkRowIndex = 0; chunkRowIndex < nChunkRows; chunkRowIndex++) {
                for (chunkColIndex = 0; chunkColIndex < nChunkCols; chunkColIndex++) {
                    AbstractGrid2DSquareCellIntChunk chunk =
                            grid2DSquareCellInt.getGrid2DSquareCellIntChunk(
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
                                    nonNoDataValueCountBigDecimal =
                                            nonNoDataValueCountBigDecimal.add(BigDecimal.ONE);
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
            Generic_BigDecimal t_Generic_BigDecimal =
                    _Grid2DSquareCell._Grids_Environment.get_Generic_BigDecimal();
            return Generic_BigDecimal.sqrt(
                    bigDecimal0,
                    numberOfDecimalPlaces,
                    t_Generic_BigDecimal.get_RoundingMode());
        } else {
            //this._Grid2DSquareCell.getClass() == Grid2DSquareCellDouble.class
            Grid2DSquareCellDouble grid2DSquareCellDouble =
                    (Grid2DSquareCellDouble) this._Grid2DSquareCell;
            double value;
            double noDataValue = grid2DSquareCellDouble._NoDataValue;
            BigDecimal nonNoDataValueCountBigDecimal = new BigDecimal("-1");
            BigDecimal differenceFromMean;
            for (chunkRowIndex = 0; chunkRowIndex < nChunkRows; chunkRowIndex++) {
                for (chunkColIndex = 0; chunkColIndex < nChunkCols; chunkColIndex++) {
                    AbstractGrid2DSquareCellDoubleChunk chunk =
                            grid2DSquareCellDouble.getGrid2DSquareCellDoubleChunk(
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
                                    nonNoDataValueCountBigDecimal =
                                            nonNoDataValueCountBigDecimal.add(BigDecimal.ONE);
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
            Generic_BigDecimal t_Generic_BigDecimal =
                    _Grid2DSquareCell._Grids_Environment.get_Generic_BigDecimal();
            return Generic_BigDecimal.sqrt(
                    bigDecimal0,
                    numberOfDecimalPlaces,
                    t_Generic_BigDecimal.get_RoundingMode());
        }
    }
}
