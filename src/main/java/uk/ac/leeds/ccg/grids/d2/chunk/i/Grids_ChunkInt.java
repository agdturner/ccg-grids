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
package uk.ac.leeds.ccg.grids.d2.chunk.i;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashSet;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkNumber;
import java.util.Arrays;
import uk.ac.leeds.ccg.math.number.Math_BigRational;
import uk.ac.leeds.ccg.math.number.Math_BigRationalSqrt;

/**
 * For chunks that represent values at cell locations that are {@code int} type
 * numbers.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_ChunkInt extends Grids_ChunkNumber {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     *
     * @param g The grid.
     * @param i The chunk ID
     * @param worthClearing Indicates whether it is likely to be worth clearing
     * in memory handling.
     */
    protected Grids_ChunkInt(Grids_GridInt g, Grids_2D_ID_int i,
            boolean worthClearing) {
        super(g, i, worthClearing);
    }

    /**
     * @return (Grids_GridInt) grid;
     */
    @Override
    public final Grids_GridInt getGrid() {
        return (Grids_GridInt) grid;
    }

    /**
     * @param row The row of the cell w.r.t. the origin of this chunk
     * @param col The column of the cell w.r.t. the origin of this chunk
     * @return The value at row, col.
     */
    public abstract int getCell(int row, int col);

    /**
     * @param row The row of the cell w.r.t. the origin of this chunk.
     * @param col The column of the cell w.r.t. the origin of this chunk.
     * @return The value at row, col as a BigDecimal.
     */
    @Override
    public BigDecimal getCellBigDecimal(int row, int col) {
        return BigDecimal.valueOf(getCell(row, col));
    }

    /**
     * Initialises the value at position given by: row, col.
     *
     * @param row the row of the cell w.r.t. the origin of this chunk
     * @param col the column of the cell w.r.t. the origin of this chunk
     * @param v the value with which the cell is initialised
     */
    public abstract void initCell(int row, int col, int v);

    /**
     * Returns the value at chunk cell row index {@code r}. chunk cell column
     * index {@code c} and sets it to {@code v}.
     *
     * @param r The chunk cell row index. of the cell w.r.t. the origin of this
     * chunk
     * @param c the column index of the cell w.r.t. the origin of this chunk
     * @param v the value the cell is to be set to
     * @return The value at chunk cell row index {@code r}, chunk cell column
     * index {@code c} before it is set.
     * @throws Exception If encountered.
     */
    public abstract int setCell(int r, int c, int v) throws Exception;

    /**
     * @return Values in row major order as an int[].
     */
    protected int[] toArrayIncludingNoDataValues() {
        Grids_GridInt g = getGrid();
        int chunkNrows = g.getChunkNRows();
        int chunkNcols = g.getChunkNCols();
        long nChunkCells = (long) chunkNrows * (long) chunkNcols;
        int[] array;
        if (nChunkCells > Integer.MAX_VALUE) {
            //throw new PrecisionExcpetion
            System.out.println("PrecisionException in "
                    + this.getClass().getName() + ".toArray()!");
            System.out.println("Warning! The returned array size is only "
                    + Integer.MAX_VALUE + " instead of " + nChunkCells);
        }
        //int noDataValue = getGrid().getNoDataValue();
        array = new int[chunkNrows * chunkNcols];
        int count = 0;
        for (int row = 0; row < chunkNrows; row++) {
            for (int col = 0; col < chunkNcols; col++) {
                array[count] = getCell(row, col);
                count++;
            }
        }
        return array;
    }

    /**
     * @return Values (except those that are noDataValues) in row major order as
     * an int[].
     */
    protected int[] toArrayNotIncludingNoDataValues() {
        Grids_GridInt g = getGrid();
        int chunkNrows = g.getChunkNRows(id);
        int chunkNcols = g.getChunkNCols(id);
        int noDataValue = g.getNoDataValue();
        long n = getN();
        if (n != (int) n) {
            throw new Error("n != (int) n");
        }
        int[] array = new int[(int) n];
        int count = 0;
        for (int row = 0; row < chunkNrows; row++) {
            for (int col = 0; col < chunkNcols; col++) {
                int value = getCell(row, col);
                if (value != noDataValue) {
                    array[count] = value;
                    count++;
                }
            }
        }
        return array;
    }

    /**
     * @return The number of cells with no data values as a BigInteger.
     */
    @Override
    public Long getN() {
        long n = 0;
        Grids_GridInt g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        int noDataValue = g.getNoDataValue();
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                if (getCell(row, col) != noDataValue) {
                    n++;
                }
            }
        }
        return n;
    }

    /**
     * @return The sum of all data values as a BigDecimal.
     */
    @Override
    public Math_BigRational getSum() {
        Math_BigRational sum = Math_BigRational.ZERO;
        Grids_GridInt g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        int noDataValue = g.getNoDataValue();
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                int value = getCell(row, col);
                if (value != noDataValue) {
                    sum = sum.add(Math_BigRational.valueOf(value));
                }
            }
        }
        return sum;
    }

    /**
     * @return The minimum of all data values.
     */
    public Integer getMin() {
        int min = Integer.MAX_VALUE;
        Grids_GridInt g = getGrid();
        int nrows = g.getChunkNRows();
        int ncols = g.getChunkNCols();
        int noDataValue = g.getNoDataValue();
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                int value = getCell(row, col);
                if (value != noDataValue) {
                    min = Math.min(min, value);
                }
            }
        }
        return min;
    }

    /**
     * @return The maximum of all data values.
     */
    protected Integer getMax() {
        int max = Integer.MIN_VALUE;
        Grids_GridInt g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        int noDataValue = g.getNoDataValue();
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                int value = getCell(row, col);
                if (value != noDataValue) {
                    max = Math.max(max, value);
                }
            }
        }
        return max;
    }

    /**
     * @return The mode.
     */
    protected HashSet<Integer> getMode() {
        HashSet<Integer> mode = new HashSet<>();
        long n = getN();
        if (n > 0) {
            Grids_GridInt g = getGrid();
            int nrows = g.getChunkNRows(id);
            int ncols = g.getChunkNCols(id);
            int noDataValue = g.getNoDataValue();
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
                    value = getCell(p, q);
                    if (value != noDataValue) {
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
                        value = getCell(p, q);
                        if (value != noDataValue) {
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
     * @see getModeTIntHashSet()
     */
    private Object[] initMode(int nrows, int ncols, int noDataValue) {
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
                value = getCell(p, q);
                if (value != noDataValue) {
                    modeCount = 0L;
                    for (row = 0; row < nrows; row++) {
                        for (col = 0; col < ncols; col++) {
                            thisValue = getCell(row, col);
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
     * @param p The row index of the cell from which counting starts.
     * @param q The column index of the cell from which counting starts.
     * @param nrows The nrows.
     * @param ncols The ncols.
     * @param value The value to be counted.
     * @return A count of cells with value {@code v} starting from row
     * {@code p}, column {@code q}.
     */
    protected long count(int p, int q, int nrows, int ncols, int value) {
        long count = 1L;
        int thisValue;
        // Do remainder of the row
        for (q++; q < ncols; q++) {
            thisValue = getCell(p, q);
            if (thisValue == value) {
                count++;
            }
        }
        // Do remainder of the grid
        for (p++; p < nrows; p++) {
            for (q = 0; q < ncols; q++) {
                thisValue = getCell(p, q);
                if (thisValue == value) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * @return The median of all non noDataValues as a double. This method
     * requires that all data in chunk can be stored as a new array.
     */
    public double getMedian() {
        long n = getN();
        BigInteger n2 = BigInteger.valueOf(n);
        if (n > 0) {
            int[] array = toArrayNotIncludingNoDataValues();
            Arrays.sort(array, 0, array.length);
            BigInteger[] n2DAR2 = n2.divideAndRemainder(new BigInteger("2"));
            if (n2DAR2[1].compareTo(BigInteger.ZERO) == 0) {
                int index = n2DAR2[0].intValue();
                return (array[index] + array[index - 1]) / 2.0d;
            } else {
                int index = n2DAR2[0].intValue();
                return array[index];
            }
        } else {
            return getGrid().getNoDataValue();
        }
    }

    /**
     * @param oom The Order of Magnitude for the precision.
     * @return The standard deviation of all data values.
     */
    protected BigDecimal getStandardDeviation(int oom, RoundingMode rm) {
        Math_BigRational sd = Math_BigRational.ZERO;
        Math_BigRational mean = getArithmeticMean();
        Grids_GridInt g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        int ndv = g.getNoDataValue();
        long count = 0;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                int v = getCell(row, col);
                if (v != ndv) {
                    sd = sd.add(Math_BigRational.valueOf(v).subtract(mean).pow(2));
                    count++;
                }
            }
        }
        if ((count - 1L) > 0L) {
            return new Math_BigRationalSqrt(sd.divide(BigInteger.valueOf(count - 1L)), oom, rm).toBigDecimal(oom, rm);
        } else {
            return BigDecimal.ZERO;
        }
    }
}
