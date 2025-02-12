/*
 * Copyright 2025 Andy Turner, University of Leeds.
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
package uk.ac.leeds.ccg.grids.d2.chunk.br;

import ch.obermuhlner.math.big.BigRational;
import uk.ac.leeds.ccg.grids.d2.grid.br.Grids_GridBR;
import java.math.BigInteger;
import java.util.HashSet;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_ChunkNumber;
import java.math.RoundingMode;
import java.util.Arrays;
import uk.ac.leeds.ccg.math.number.Math_BigRationalSqrt;

/**
 * For chunks that represent values at cell locations that are
 * {@code BigDecimal} type numbers.
 *
 * @author Andy Turner
 * @version 1.1
 */
public abstract class Grids_ChunkBR extends Grids_ChunkNumber {

    private static final long serialVersionUID = 1L;

    /**
     * @param g What {@link #grid} is set to.
     * @param i What {@link #id} is set to.
     * @param worthClearing What {@link #worthClearing} is set to.
     */
    protected Grids_ChunkBR(Grids_GridBR g, Grids_2D_ID_int i,
            boolean worthClearing) {
        super(g, i, worthClearing);
    }

    /**
     * @return (Grids_GridBR) grid;
     */
    @Override
    public final Grids_GridBR getGrid() {
        return (Grids_GridBR) grid;
    }

    /**
     * @param r The chunk cell row index.
     * @param c The chunk cell column index.
     * @return The value at chunk cell row {@code r}, chunk cell column index
     * {@code c}.
     */
    public abstract BigRational getCell(int r, int c);

    /**
     * @param r The chunk cell row index.
     * @param c The chunk cell column index.
     * @return The value at chunk cell row {@code r}, chunk cell column index
     * {@code c} as a BigDecimal.
     */
    @Override
    public BigRational getCellBigRational(int r, int c) {
        return getCell(r, c);
    }

    /**
     * Initialises the value at chunk cell row {@code r}, chunk cell column
     * {@code c} to {@code v}.
     *
     * @param r The chunk cell row.
     * @param c The chunk cell column.
     * @param v The value to initialise.
     */
    public abstract void initCell(int r, int c, BigRational v);

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
    public abstract BigRational setCell(int r, int c, BigRational v) throws Exception;

    /**
     * @return All the values including noDataValue's in row major order as a
     * double[].
     */
    public BigRational[] toArrayIncludingNoDataValues() {
        Grids_GridBR g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        BigRational[] array = new BigRational[nrows * ncols];
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
    public BigRational[] toArrayNotIncludingNoDataValues() {
        Grids_GridBR g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        long n = getN();
        BigRational[] array = new BigRational[(int) n];
        int count = 0;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                BigRational v = getCell(row, col);
                if (v.compareTo(g.ndv) == 0) {
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
        Grids_GridBR g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                BigRational v = getCell(row, col);
                if (v.compareTo(g.ndv) != 0) {
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
    public BigRational getSum() {
        BigRational sum = BigRational.ZERO;
        Grids_GridBR g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                BigRational v = getCell(row, col);
                if (v.compareTo(g.ndv) != 0) {
                    sum = sum.add(v);
                }
            }
        }
        return sum;
    }

    /**
     * @return The minimum of all data values.
     */
    protected BigRational getMin() {
        BigRational min = getCell(0, 0);
        Grids_GridBR g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                BigRational v = getCell(row, col);
                if (v.compareTo(g.ndv) != 0) {
                    min = min.min(v);
                }
            }
        }
        return min;
    }

    /**
     * @return The maximum of all data values.
     */
    protected BigRational getMax() {
        BigRational max = getCell(0, 0);
        Grids_GridBR g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                BigRational v = getCell(row, col);
                if (v.compareTo(g.ndv) != 0) {
                    max = max.max(v);
                }
            }
        }
        return max;
    }

    /**
     * @return The mode of all data values.
     */
    protected HashSet<BigRational> getMode() {
        HashSet<BigRational> mode = new HashSet<>();
        long n = getN();
        if (n > 0) {
            Grids_GridBR g = getGrid();
            int nrows = g.getChunkNRows(id);
            int ncols = g.getChunkNCols(id);
            Object[] tmode = initMode(nrows, ncols, g.ndv);
            if (tmode[0] == null) {
                return mode;
            } else {
                BigRational v;
                long count;
                long modeCount = (Long) tmode[0];
                mode.add((BigRational) tmode[1]);
                Grids_2D_ID_int chunkCellID = (Grids_2D_ID_int) tmode[2];
                // Do remainder of the row
                int p = chunkCellID.getRow();
                for (int q = chunkCellID.getCol() + 1; q < ncols; q++) {
                    v = getCell(p, q);
                    if (v.compareTo(g.ndv) != 0) {
                        count = count(p, q, nrows, ncols, v);
                        if (count > modeCount) {
                            mode.clear();
                            mode.add(v);
                            modeCount = count;
                        } else {
                            if (count == modeCount) {
                                mode.add(v);
                            }
                        }
                    }
                }
                // Do remainder of the grid
                for (p++; p < nrows; p++) {
                    for (int q = 0; q < ncols; q++) {
                        v = getCell(p, q);
                        if (v.compareTo(g.ndv) != 0) {
                            count = count(p, q, nrows, ncols, v);
                            if (count > modeCount) {
                                mode.clear();
                                mode.add(v);
                                modeCount = count;
                            } else {
                                if (count == modeCount) {
                                    mode.add(v);
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
    private Object[] initMode(int nrows, int ncols, BigRational ndv) {
        Object[] initMode = new Object[3];
        long modeCount;
        BigRational thisValue;
        for (int p = 0; p < nrows; p++) {
            for (int q = 0; q < ncols; q++) {
                BigRational v = getCell(p, q);
                if (v.compareTo(ndv) != 0) {
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
    private long count(int p, int q, int nrows, int ncols, BigRational v) {
        long count = 1L;
        BigRational thisValue;
        // Do remainder of the row
        for (q++; q < ncols; q++) {
            thisValue = getCell(p, q);
            if (thisValue.compareTo(v) == 0) {
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
    public BigRational getMedian() {
        long n = getN();
        BigInteger n2 = BigInteger.valueOf(n);
        if (n > 0) {
            BigRational[] array = toArrayNotIncludingNoDataValues();
            Arrays.sort(array, 0, array.length);
            BigInteger[] n2DAR2 = n2.divideAndRemainder(new BigInteger("2"));
            if (n2DAR2[1].compareTo(BigInteger.ZERO) == 0) {
                int index = n2DAR2[0].intValue();
                return (array[index].add(array[index - 1])).divide(BigRational.valueOf(2));
            } else {
                int index = n2DAR2[0].intValue();
                return array[index];
            }
        } else {
            return getGrid().getNoDataValue();
        }
    }

    /**
     * Calculate and return the standard deviation of all data values. If there
     * is only one data value then BigDecimal.ZERO is returned.
     *
     * @param oom The order of magnitude at which the result is calculated.
     * @param rm The {@link RoundingMode} for any rounding that is necessary.
     * @return The standard deviation of all data values calculated to
     * {@code oom} precision using {@link RoundingMode} {@code rm}.
     */
    protected BigRational getStandardDeviation(int oom, RoundingMode rm) {
        BigRational sd = BigRational.ZERO;
        BigRational mean = getArithmeticMean();
        Grids_GridBR g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        long count = 0;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                BigRational v = getCell(row, col);
                if (v.compareTo(g.ndv) != 0) {
                    sd = sd.add(v.subtract(mean).pow(2));
                    count++;
                }
            }
        }
        long d = count - 1L;
        if (d > 0L) {
            Math_BigRationalSqrt sqrt = new Math_BigRationalSqrt(sd, oom, rm);
            return sqrt.getSqrt(oom, rm);
        } else {
            return BigRational.ZERO;
        }
    }
}
