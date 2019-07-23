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
package uk.ac.leeds.ccg.andyt.grids.core.grid.stats;

import java.io.Serializable;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;

/**
 * To be extended to provide statistics about the data in Grids and GridChunks
 * more optimally.
 */
public abstract class Grids_AbstractGridNumberStats extends Grids_Object
        implements Serializable {

    //private long final long serialVersionUID = 1L;
    /**
     * For storing the number of cells with data values.
     */
    protected long N;

    /**
     * For storing the sum of all non data values.
     */
    protected BigDecimal Sum;

    /**
     * For storing the number of minimum data values.
     */
    protected long NMin;

    /**
     * For storing the number of maximum data values.
     */
    protected long NMax;

    protected Grids_AbstractGridNumberStats() {
    }

    public Grids_AbstractGridNumberStats(Grids_Environment ge) {
        super(ge);
        init();
    }

    /**
     * For initialisation.
     */
    private void init() {
        N = 0;
        Sum = BigDecimal.ZERO;
        NMin = 0;
        NMax = 0;
    }

    /**
     * @return true iff the stats are kept up to date as the underlying data
     * change.
     */
    public abstract boolean isUpdated();

    /**
     * Updates from stats.
     *
     * @param stats the Grids_AbstractGridNumberStats instance which fields are
     * used to update this.
     */
    public void update(
            Grids_AbstractGridNumberStats stats) {
        N = stats.N;
        Sum = stats.Sum;
        NMin = stats.NMin;
        NMax = stats.NMax;
    }

    /**
     * Updates by going through all values in Grid if the fields are likely not
     * be up to date.
     */
    protected abstract void update();

    /**
     * Returns a String describing this instance
     *
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public String toString(
            boolean hoome) {
        try {
            String result = toString();
            env.checkAndMaybeFreeMemory();
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                    if (!env.swapChunk(env.HOOMEF)) {
                        throw e;
                    }
                env.initMemoryReserve();
                return toString(hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the name of the this class.
     *
     * @return
     */
    public abstract String getName();
//    {
//        return getClass().getName();
//    }

    /**
     * Returns a string describing this instance.
     *
     * @return
     */
    @Override
    public String toString() {
        String result = getName();
        result += "(N(" + N + "),"
                + "Max(" + getMax(false) + "),"
                + "Min(" + getMin(false) + "),"
                + "NMax(" + NMax + "),"
                + "NMin(" + NMin + "),"
                + "Sum(" + Sum.toString() + "))";
        return result;
    }

//    /**
//     * For returning the number of cells with data values as a BigInteger.
//     *
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * swap operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    @Override
//    public final Long getN(boolean hoome) {
//        try {
//            long result = getN();
//            env.checkAndMaybeFreeMemory(hoome);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                if (env.swapChunkExcept_Account(Grid, hoome) < 1L) {
//                    if (!env.swapChunk(env.HOOMEF)) {
//                        throw e;
//                    }
//                }
//                env.initMemoryReserve(false);
//                return getN(hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
//    
//    /**
//     * @return The number of cells with finite data values as a BigInteger.
//     */
//    protected abstract long getN();
    /**
     * @return The number of cells with finite data values as a BigInteger.
     */
    public abstract Grids_AbstractGridNumber getGrid();

    /**
     * @return The number of cells with finite non zero data values.
     */
    public abstract BigInteger getNonZeroN();

//    /**
//     * For returning the sum of all finite data values.
//     *
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * swap operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    @Override
//    public final BigDecimal getSum(boolean hoome) {
//        try {
//            BigDecimal result = getSum();
//            env.checkAndMaybeFreeMemory(hoome);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                if (env.swapChunkExcept_Account(Grid, hoome) < 1L) {
//                    if (!env.swapChunk(env.HOOMEF)) {
//                        throw e;
//                    }
//                }
//                env.initMemoryReserve(env.HOOMEF);
//                return getSum(hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
//
//    /**
//     * For returning the sum of all finite data values.
//     *
//     * @return
//     */
//    protected abstract BigDecimal getSum();
//    /**
//     * For returning the minimum of all data values.
//     *
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * swap operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    @Override
//    public Number getMin(
//            boolean update,
//            boolean hoome) {
//        try {
//            Number result = getMin(update);
//            env.checkAndMaybeFreeMemory(hoome);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                if (env.swapChunkExcept_Account(Grid, hoome) < 1L) {
//                    if (!env.swapChunk(env.HOOMEF)) {
//                        throw e;
//                    }
//                }
//                env.initMemoryReserve(env.HOOMEF);
//                return getMin(update, hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
//
    /**
     * For returning the minimum of all data values.
     *
     * @param update If true then update() is called.
     * @return
     */
    public abstract Number getMin(boolean update);

//    /**
//     * For returning the maximum of all data values.
//     *
//     * @param update If true then an update of the statistics is made.
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * swap operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    @Override
//    public final Number getMax(
//            boolean update,
//            boolean hoome) {
//        try {
//            Number result = getMax(update);
//            env.checkAndMaybeFreeMemory(hoome);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                if (env.swapChunkExcept_Account(Grid, hoome) < 1L) {
//                    if (!env.swapChunk(env.HOOMEF)) {
//                        throw e;
//                    }
//                }
//                env.initMemoryReserve(env.HOOMEF);
//                return getMax(
//                        update,
//                        hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
//
    /**
     * For returning the maximum of all data values.
     *
     * @param update If true then an update of the statistics is made.
     * @return
     */
    public abstract Number getMax(boolean update);

//    /**
//     * For returning the mode of all data values.
//     *
//     * @return
//     */
//    protected Object getMode() {
//        long row;
//        long col;
//        boolean calculated = false;
//        if (Grid instanceof Grids_GridInt) {
//            Grids_GridInt g = (Grids_GridInt) Grid;
//            HashSet<Integer> mode = new HashSet<>();
//            int noDataValue = g.getNoDataValue(env.HOOME);
//            Object[] tmode = initMode(
//                    g,
//                    noDataValue);
//            if (tmode[0] == null) {
//                return mode;
//            } else {
//                //double value;
//                int value;
//                long count;
//                long modeCount = ((Long) tmode[0]);
//                mode.add(((Integer) tmode[1]));
//
//                Grids_2D_ID_long cellID = (Grids_2D_ID_long) tmode[2];
//                // Do remainder of the row
//                row = cellID.getRow();
//                for (col = cellID.getCol() + 1; col < nCols; col++) {
//                    value = g.getCell(row, col, env.HOOME);
//                    if (value != noDataValue) {
//                        count = count(g, row, col, nRows, nCols, value);
//                        if (count > modeCount) {
//                            mode.clear();
//                            mode.add(value);
//                            modeCount = count;
//                        } else {
//                            if (count == modeCount) {
//                                mode.add(value);
//                            }
//                        }
//                    }
//                }
//                // Do remainder of the grid
//                for (row++; row < nRows; row++) {
//                    for (col = 0; col < nCols; col++) {
//                        value = g.getCell(row, col, env.HOOME);
//                        if (value != noDataValue) {
//                            count = count(
//                                    g,
//                                    row,
//                                    col,
//                                    nRows,
//                                    nCols,
//                                    value);
//                            if (count > modeCount) {
//                                mode.clear();
//                                mode.add(value);
//                                modeCount = count;
//                            } else {
//                                if (count == modeCount) {
//                                    mode.add(value);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            return mode;
//        } else {
//            //getClass() == Grids_GridDouble.class
//            Grids_GridDouble g = (Grids_GridDouble) Grid;
//            //TDoubleHashSet mode = new TDoubleHashSet();
//            HashSet<Double> mode = new HashSet<>();
//            double noDataValue = g.getNoDataValue(env.HOOME);
//            Object[] tmode = initMode(g, nRows, nCols, noDataValue);
//            if (tmode[0] == null) {
//                return mode;
//            } else {
//                double value;
//                long count;
//                long modeCount = ((Long) tmode[0]);
//                mode.add(((Double) tmode[1]));
//                Grids_2D_ID_long cellID = (Grids_2D_ID_long) tmode[2];
//                // Do remainder of the row
//                row = cellID.getRow();
//                for (col = cellID.getCol() + 1; col < nCols; col++) {
//                    value = g.getCell(row, col, env.HOOME);
//                    if (value != noDataValue) {
//                        count = count(g, row, col, nRows, nCols, value);
//                        if (count > modeCount) {
//                            mode.clear();
//                            mode.add(value);
//                            modeCount = count;
//                        } else {
//                            if (count == modeCount) {
//                                mode.add(value);
//                            }
//                        }
//                    }
//                }
//                // Do remainder of the grid
//                for (row++; row < nRows; row++) {
//                    for (col = 0; col < nCols; col++) {
//                        value = g.getCell(row, col, env.HOOME);
//                        if (value != noDataValue) {
//                            count = count(g, row, col, nRows, nCols, value);
//                            if (count > modeCount) {
//                                mode.clear();
//                                mode.add(value);
//                                modeCount = count;
//                            } else {
//                                if (count == modeCount) {
//                                    mode.add(value);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            return mode;
//        }
//    }
//
//    /**
//     * For initialising mode calculation in getMode().
//     */
//    private Object[] initMode(
//            Grids_GridInt g,
//            int noDataValue) {
//        Object[] initMode = new Object[3];
//        long modeCount;
//        long p;
//        long q;
//        long row;
//        long col;
//        int value;
//        int thisValue;
//        boolean hoome = true;
//        Grids_GridIntIterator ite;
//        ite = g.iterator();
//        while (ite.hasNext()) {
//            value = (Integer) ite.next();
//            if (value != noDataValue) {
//                modeCount = 0L;
//                Grids_GridIntIterator ite2;
//                ite2 = g.iterator();
//                while (ite2.hasNext()) {
//                    thisValue = (Integer) ite2.next();
//                    if (thisValue == value) {
//                        modeCount++;
//                    }
//                }
//                initMode[0] = modeCount;
//                initMode[1] = value;
//                initMode[2] = ite.getChunkID();
//                g.getCellID(
//                        p,
//                        q,
//                        hoome);
//                //initMode[2] = new CellID( p, q );
//                return initMode;
//            }
//            return initMode;
//        }
//        /**
//         * For initialising mode calculation in getMode().
//         */
//    private Object[] initMode(
//            Grids_GridDouble grid2DSquareCellDouble,
//            long _NRows,
//            long _NCols,
//            double noDataValue) {
//        Object[] initMode = new Object[3];
//        long modeCount;
//        long p;
//        long q;
//        long row;
//        long col;
//        double value;
//        double thisValue;
//        boolean hoome = true;
//        for (p = 0; p < _NRows; p++) {
//            for (q = 0; q < _NCols; q++) {
//                value = grid2DSquareCellDouble.getCell(
//                        p,
//                        q,
//                        hoome);
//                if (value != noDataValue) {
//                    modeCount = 0L;
//                    for (row = 0; row < _NRows; row++) {
//                        for (col = 0; col < _NCols; col++) {
//                            thisValue = grid2DSquareCellDouble.getCell(
//                                    row,
//                                    col,
//                                    hoome);
//                            if (thisValue == value) {
//                                modeCount++;
//                            }
//                        }
//                    }
//                    initMode[0] = modeCount;
//                    initMode[1] = value;
//                    initMode[2] = grid2DSquareCellDouble.getCellID(
//                            p,
//                            q,
//                            hoome);
//                    //initMode[2] = new CellID( p, q );
//                    return initMode;
//                }
//            }
//        }
//        return initMode;
//    }
//
//    /**
//     * Counts the remaining number of values in grid2DSquareCellInt equal to
//     * value from cell given by row p and column q counting in row major order.
//     *
//     *
//     * @param g
//     * @param row The row index of the cell from which counting starts
//     * @param col The column index of the cell from which counting starts
//     * @param nRows The number of rows in grid2DSquareCellInt.
//     * @param nCols The number of columns in grid2DSquareCellInt.
//     * @param value The value to be counted.
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * swap operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    public long count(
//            Grids_GridInt g,
//            long row,
//            long col,
//            long nRows,
//            long nCols,
//            int value,
//            boolean hoome) {
//        try {
//            long result = count(
//                    g,
//                    row,
//                    col,
//                    nRows,
//                    nCols,
//                    value);
//            env.checkAndMaybeFreeMemory(hoome);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                if (env.swapChunkExcept_Account(Grid, hoome) < 1L) {
//                    if (env.swapChunk_Account(hoome) < 1L) {
//                        throw e;
//                    }
//                }
//                env.initMemoryReserve(env.HOOMEF);
//                return count(
//                        g,
//                        row,
//                        col,
//                        nRows,
//                        nCols,
//                        value,
//                        hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
//
//    /**
//     * Counts the remaining number of values in grid2DSquareCellInt equal to
//     * value from cell given by row p and column q counting in row major order.
//     *
//     * @param g
//     * @param row The row index of the cell from which counting starts
//     * @param col The column index of the cell from which counting starts
//     * @param nRows The number of rows in grid2DSquareCellInt.
//     * @param nCols The number of columns in grid2DSquareCellInt.
//     * @param value The value to be counted.
//     * @return
//     */
//    protected static long count(
//            Grids_GridInt g,
//            long row,
//            long col,
//            long nRows,
//            long nCols,
//            int value) {
//        long count = 1L;
//        int thisValue;
//        boolean hoome = true;
//        // Do remainder of the row of grid2DSquareCellInt
//        for (col++; col < nCols; col++) {
//            thisValue = g.getCell(row, col, hoome);
//            if (thisValue == value) {
//                count++;
//            }
//        }
//        // Do remainder of the grid2DSquareCellInt
//        for (row++; row < nRows; row++) {
//            for (col = 0; col < nCols; col++) {
//                thisValue = g.getCell(row, col, hoome);
//                if (thisValue == value) {
//                    count++;
//                }
//            }
//        }
//        return count;
//    }
//
//    /**
//     * Counts the remaining number of values in grid2DSquareCellDouble equal to
//     * value from cell given by row p and column q counting in row major order.
//     *
//     * @param g
//     * @param row The row index of the cell from which counting starts
//     * @param col The column index of the cell from which counting starts
//     * @param nRows The number of rows in grid2DSquareCellInt.
//     * @param nCols The number of columns in grid2DSquareCellInt.
//     * @param value The value to be counted.
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * swap operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    public long count(
//            Grids_GridDouble g,
//            long row,
//            long col,
//            long nRows,
//            long nCols,
//            double value,
//            boolean hoome) {
//        try {
//            long result = count(g, row, col, nRows, nCols, value);
//            env.checkAndMaybeFreeMemory(hoome);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                if (env.swapChunkExcept_Account(Grid, hoome) < 1L) {
//                    if (env.swapChunk_Account(hoome) < 1L) {
//                        throw e;
//                    }
//                }
//                env.initMemoryReserve(env.HOOMEF);
//                return count(g, row, col, nRows, nCols, value, hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
//
//    /**
//     * Counts the remaining number of values in grid2DSquareCellDouble equal to
//     * value from cell given by row p and column q counting in row major order.
//     *
//     *
//     * @param g
//     * @param row The row index of the cell from which counting starts
//     * @param col The column index of the cell from which counting starts
//     * @param nRows The number of rows in grid2DSquareCellInt.
//     * @param nCols The number of columns in grid2DSquareCellInt.
//     * @param value The value to be counted.
//     * @return
//     */
//    protected static long count(
//            Grids_GridDouble g,
//            long row,
//            long col,
//            long nRows,
//            long nCols,
//            double value) {
//        long count = 1L;
//        double thisValue;
//        boolean hoome = true;
//        // Do remainder of the row of g
//        for (col++; col < nCols; col++) {
//            thisValue = g.getCell(row, col, hoome);
//            if (thisValue == value) {
//                count++;
//            }
//        }
//        // Do remainder of the grid2DSquareCellDouble
//        for (row++; row < nRows; row++) {
//            for (col = 0; col < nCols; col++) {
//                thisValue = g.getCell(row, col, hoome);
//                if (thisValue == value) {
//                    count++;
//                }
//            }
//        }
//        return count;
//    }
//    /**
//     * For returning the arithmetic mean of all data values.
//     *
//     * @param numberOfDecimalPlaces The result returned uses BigDecimal
//     * arithmetic to ensure the result is correct given a round scheme to this
//     * many decimal places.
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
//                if (env.swapChunkExcept_Account(Grid, hoome) < 1L) {
//                    if (!env.swapChunk(env.HOOMEF)) {
//                        throw e;
//                    }
//                }
//                env.initMemoryReserve(env.HOOMEF);
//                return getArithmeticMean(numberOfDecimalPlaces, hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
    /**
     * For returning the arithmetic mean of all data values. Throws an
     * ArithmeticException if N is equal to zero.
     *
     * @param numberOfDecimalPlaces The result returned uses BigDecimal
     * arithmetic to ensure the result is correct given a round scheme to this
     * many decimal places.
     *
     * @return
     */
    public BigDecimal getArithmeticMean(int numberOfDecimalPlaces) {
        return Sum.divide(new BigDecimal(N), numberOfDecimalPlaces, 
                BigDecimal.ROUND_HALF_EVEN);
    }

//    /**
//     * Returns the standard deviation of all data values.
//     *
//     * @param numberOfDecimalPlaces
//     * @param hoome If true then OutOfMemoryErrors are caught,
//     * swap operations are initiated, then the method is re-called. If false
//     * then OutOfMemoryErrors are caught and thrown.
//     * @return
//     */
//    public BigDecimal getStandardDeviation(
//            int numberOfDecimalPlaces,
//            boolean hoome) {
//        try {
//            BigDecimal result = getStandardDeviation(numberOfDecimalPlaces);
//            env.checkAndMaybeFreeMemory(hoome);
//            return result;
//        } catch (OutOfMemoryError e) {
//            if (hoome) {
//                env.clearMemoryReserve();
//                if (env.swapChunkExcept_Account(Grid, hoome) < 1L) {
//                    if (!env.swapChunk(env.HOOMEF)) {
//                        throw e;
//                    }
//                }
//                env.initMemoryReserve(env.HOOMEF);
//                return getStandardDeviation(
//                        numberOfDecimalPlaces,
//                        hoome);
//            } else {
//                throw e;
//            }
//        }
//    }
//    /**
//     * Returns the standard deviation of all data values.
//     *
//     * @param numberOfDecimalPlaces
//     * @return
//     */
//    protected abstract BigDecimal getStandardDeviation(int numberOfDecimalPlaces);
    public abstract Object[] getQuantileClassMap(int nClasses);

//    private boolean checkMaps(
//            TreeMap<Integer, TreeMap<Double, Long>> classMap,
//            TreeMap<Integer, Double> minDouble,
//            TreeMap<Integer, Double> maxDouble,
//            TreeMap<Integer, Long> classCounts,
//            int classToFill,
//            long numberOfValuesInEachClass) {
//        boolean result = true;
//        Iterator<Integer> ite;
//        ite = classMap.keySet().iterator();
//        while (ite.hasNext()) {
//            Integer key;
//            key = ite.next();
//            if (key <= classToFill) {
//                double minFromMinDouble;
//                double maxFromMaxDouble;
//                long countFromClassCounts;
//                double minFromClassMap;
//                double maxFromClassMap;
//                long countFromClassMap;
//                minFromMinDouble = minDouble.get(key);
//                maxFromMaxDouble = maxDouble.get(key);
//                countFromClassCounts = classCounts.get(key);
//                TreeMap<Double, Long> classMapClassMap;
//                classMapClassMap = classMap.get(key);
//                minFromClassMap = classMapClassMap.firstKey();
//                maxFromClassMap = classMapClassMap.lastKey();
//                Collection<Long> values;
//                values = classMapClassMap.values();
//                countFromClassMap = getSum(values);
//                if (!(minFromMinDouble == minFromClassMap)) {
//                    int debug = 0;
//                    result = false;
//                }
//                if (!(maxFromMaxDouble == maxFromClassMap)) {
//                    int debug = 1;
//                    result = false;
//                }
//                if (!(countFromClassCounts == countFromClassMap)) {
//                    int debug = 2;
//                    result = false;
//                }
//                if (countFromClassCounts > numberOfValuesInEachClass) {
//                    if (!(countFromClassCounts - classMapClassMap.lastEntry().getValue() < numberOfValuesInEachClass)) {
//                        int debug = 3;
//                        result = false;
//                    }
//                }
//            }
//        }
//        return result;
//    }
    /**
     * Move this to generic utilities.
     *
     * @param c
     * @return
     */
    public BigInteger getSum(Collection<Long> c) {
        BigInteger result = BigInteger.ZERO;
        Iterator<Long> ite;
        ite = c.iterator();
        while (ite.hasNext()) {
            long v = ite.next();
            result = result.add(BigInteger.valueOf(v));
        }
        return result;
    }

    /**
     * @param value
     * @param classMap
     * @param minDouble
     * @param maxDouble
     * @param classCounts
     * @param desiredNumberOfValuesInEachClass
     * @param classToFill
     * @return result[0] is the class, result[1] is the classToFill which may or
     * may not change from what is passed in.
     */
    protected int[] getValueClass(
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

    /**
     * @param n to set N to.
     */
    public void setN(long n) {
        N = n;
    }

    /**
     * @param sum to set Sum to.
     */
    public void setSum(BigDecimal sum) {
        Sum = sum;
    }

    /**
     * @param nMin to set NMin to.
     */
    public void setNMin(long nMin) {
        NMin = nMin;
    }

    /**
     * @param nMax to set NMax to.
     */
    public void setNMax(long nMax) {
        NMax = nMax;
    }

    /**
     * @return the NMin
     */
    public long getNMin() {
        return NMin;
    }

    /**
     * @return the NMax
     */
    public long getNMax() {
        return NMax;
    }
}
