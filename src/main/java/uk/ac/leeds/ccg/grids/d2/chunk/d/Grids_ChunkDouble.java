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
package uk.ac.leeds.ccg.grids.d2.chunk.d;

import ch.obermuhlner.math.big.BigRational;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkNumber;
import java.math.RoundingMode;
import java.util.Arrays;
import uk.ac.leeds.ccg.math.Math_BigRationalSqrt;

/**
 * For chunks that represent values at cell locations that are {@code double}
 * type numbers.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_ChunkDouble extends Grids_ChunkNumber {

    private static final long serialVersionUID = 1L;

    /**
     * @param g What {@link #grid} is set to.
     * @param i What {@link #id} is set to.
     * @param worthClearing What {@link #worthClearing} is set to.
     */
    protected Grids_ChunkDouble(Grids_GridDouble g, Grids_2D_ID_int i,
            boolean worthClearing) {
        super(g, i, worthClearing);
    }

    /**
     * @return (Grids_GridDouble) grid;
     */
    @Override
    public final Grids_GridDouble getGrid() {
        return (Grids_GridDouble) grid;
    }

    /**
     * @param r The chunk cell row index.
     * @param c The chunk cell column index.
     * @return The value at chunk cell row {@code r}, chunk cell column index
     * {@code c}.
     */
    public abstract double getCell(int r, int c);

    /**
     * @param r The chunk cell row index.
     * @param c The chunk cell column index.
     * @return The value at chunk cell row {@code r}, chunk cell column index
     * {@code c} as a BigDecimal.
     */
    @Override
    public BigDecimal getCellBigDecimal(int r, int c) {
        return BigDecimal.valueOf(getCell(r, c));
    }

    /**
     * Initialises the value at chunk cell row {@code r}, chunk cell column
     * {@code c} to {@code v}.
     *
     * @param r The chunk cell row.
     * @param c The chunk cell column.
     * @param v The value to initialise.
     */
    public abstract void initCell(int r, int c, double v);

    /**
     * Returns the value at chunk cell row {@code r}, chunk cell column
     * {@code c} and sets it to {@code v}.
     *
     * @param r The chunk cell row.
     * @param c The chunk cell column.
     * @param v The value the cell is to be set to.
     * @return The value at chunk cell row {@code r}, chunk cell column
     * {@code c} before it is set.
     * @throws Exception If encountered.
     */
    public abstract double setCell(int r, int c, double v) throws Exception;

    /**
     * @return All the values including noDataValue's in row major order as a
     * double[].
     */
    public double[] toArrayIncludingNoDataValues() {
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        double[] array = new double[nrows * ncols];
        int count = 0;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                array[count] = getCell(row, col);
                count++;
            }
        }
        return array;
    }

    /**
     * @return All the values excluding noDataValues in row major order as a
     * double[].
     */
    public double[] toArrayNotIncludingNoDataValues() {
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        double noDataValue = g.getNoDataValue();
        long n = getN();
        double[] array = new double[(int) n];
        int count = 0;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                double v = getCell(row, col);
                if (v != noDataValue) {
                    array[count] = v;
                    count++;
                }
            }
        }
        return array;
    }

    /**
     * @return The number of cells with data values.
     */
    @Override
    public Long getN() {
        long n = 0;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        double noDataValue = g.getNoDataValue();
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                double value = getCell(row, col);
                if (Double.isNaN(value) && Double.isFinite(value)) {
                    if (value != noDataValue) {
                        n++;
                    }
                }
            }
        }
        return n;
    }

    /**
     * @return The sum of all data values as a BigDecimal.
     */
    @Override
    public BigDecimal getSum() {
        BigDecimal sum = BigDecimal.ZERO;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        double noDataValue = g.getNoDataValue();
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                double v = getCell(row, col);
                if (Double.isNaN(v) && Double.isFinite(v)) {
                    if (v != noDataValue) {
                        sum = sum.add(new BigDecimal(v));
                    }
                }
            }
        }
        return sum;
    }

    /**
     * @return The minimum of all data values.
     */
    protected Double getMin() {
        double min = Double.POSITIVE_INFINITY;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        double noDataValue = g.getNoDataValue();
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                double v = getCell(row, col);
                if (v != noDataValue) {
                    min = Math.min(min, v);
                }
            }
        }
        return min;
    }

    /**
     * @return The maximum of all data values.
     */
    protected Double getMax() {
        double max = Double.NEGATIVE_INFINITY;
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        double noDataValue = g.getNoDataValue();
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                double v = getCell(row, col);
                if (v != noDataValue) {
                    max = Math.max(max, v);
                }
            }
        }
        return max;
    }

    /**
     * @return The mode of all data values.
     */
    protected HashSet<Double> getMode() {
        HashSet<Double> mode = new HashSet<>();
        long n = getN();
        if (n > 0) {
            Grids_GridDouble g = getGrid();
            int nrows = g.getChunkNRows(id);
            int ncols = g.getChunkNCols(id);
            double noDataValue = g.getNoDataValue();
            Object[] tmode = initMode(nrows, ncols, noDataValue);
            if (tmode[0] == null) {
                return mode;
            } else {
                double value;
                long count;
                long modeCount = (Long) tmode[0];
                mode.add((Double) tmode[1]);
                Grids_2D_ID_int chunkCellID = (Grids_2D_ID_int) tmode[2];
                // Do remainder of the row
                int p = chunkCellID.getRow();
                for (int q = chunkCellID.getCol() + 1; q < ncols; q++) {
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
                    for (int q = 0; q < ncols; q++) {
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
     * @see #getMode()
     */
    private Object[] initMode(int nrows, int ncols, double noDataValue) {
        Object[] initMode = new Object[3];
        long modeCount;
        double thisValue;
        for (int p = 0; p < nrows; p++) {
            for (int q = 0; q < ncols; q++) {
               double v = getCell(p, q);
                if (v != noDataValue) {
                    modeCount = 0L;
                    for (int row = 0; row < nrows; row++) {
                        for (int col = 0; col < ncols; col++) {
                            thisValue = getCell(row, col);
                            if (thisValue == v) {
                                modeCount++;
                            }
                        }
                    }
                    initMode[0] = modeCount;
                    initMode[1] = v;
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
     * @param nrows The number of rows in the chunk.
     * @param ncols The number of columns in the chunk.
     * @param v The value to be counted.
     * @return A count of the remaining cells with value {@code v} starting from
     * p, q and going in row major order.
     */
    private long count(int p, int q, int nrows, int ncols, double v) {
        long count = 1L;
        double thisValue;
        // Do remainder of the row
        for (q++; q < ncols; q++) {
            thisValue = getCell(p, q);
            if (thisValue == v) {
                count++;
            }
        }
        // Do remainder of the grid
        for (p++; p < nrows; p++) {
            for (q = 0; q < ncols; q++) {
                thisValue = getCell(p, q);
                if (thisValue == v) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * @return The median of all data values as a double. This method requires
     * that all data in chunk can be stored as a new array.
     */
    public double getMedian() {
        long n = getN();
        BigInteger n2 = BigInteger.valueOf(n);
        if (n > 0) {
            double[] array = toArrayNotIncludingNoDataValues();
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
     * @param dp The number of decimal places the result is to be accurate to.
     * @param rm The rounding mode.
     * @return The standard deviation of all data values.
     */
    protected BigDecimal getStandardDeviation(int oom, RoundingMode rm) {
        BigRational sd = BigRational.ZERO;
        BigRational mean = getArithmeticMean();
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        double noDataValue = g.getNoDataValue();
        long count = 0;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                double v = getCell(row, col);
                if (v != noDataValue) {
                    sd = sd.add((BigRational.valueOf(v).subtract(mean)).pow(2));
                    count++;
                }
            }
        }
        if ((count - 1L) > 0L) {
//            return Math_BigDecimal.sqrt(Math_BigDecimal.divideRoundIfNecessary(
//                    sd, BigInteger.valueOf(count - 1L), dp * 2, rm), dp, rm);
            return new Math_BigRationalSqrt(sd.divide(BigInteger.valueOf(count - 1L))).toBigDecimal(oom);
        } else {
            return BigDecimal.ZERO;
        }
    }
}
