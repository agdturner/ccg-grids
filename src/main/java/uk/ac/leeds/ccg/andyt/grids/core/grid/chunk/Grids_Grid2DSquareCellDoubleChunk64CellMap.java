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

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_Grid2DSquareCellDouble;
import gnu.trove.TDoubleHashSet;
import gnu.trove.TDoubleLongHashMap;
import gnu.trove.TDoubleLongIterator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_UnsignedLong;

/**
 * Grids_AbstractGrid2DSquareCellDoubleChunk extension that stores cell values
 * in a TDoubleLongHashMap. The maximum size of this
 * Grids_AbstractGrid2DSquareCellDoubleChunk is 64 cells. TODO: Adjudicate when
 * this is useful. What type of data?
 */
public class Grids_Grid2DSquareCellDoubleChunk64CellMap
        extends Grids_AbstractGrid2DSquareCellDoubleChunk
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    /**
     * For storing values mapped to a binary encoded long. The long is a key
     * which indicates if the value is that at a given location. Both keys and
     * values are unique.
     */
    private TDoubleLongHashMap data;

    protected Grids_Grid2DSquareCellDoubleChunk64CellMap() {
    }

    /**
     * Creates a new Grid2DSquareCellDoubleChunk64CellMap.
     *
     * @param ge
     */
    public Grids_Grid2DSquareCellDoubleChunk64CellMap(Grids_Environment ge) {
        super(ge);
        this.ChunkID = new Grids_2D_ID_int();
        this.data = new TDoubleLongHashMap();
        //this._Grid2DSquareCell = new Grid2DSquareCellDouble(_AbstractGrid2DSquareCell_HashSet, handleOutOfMemoryError);
        this.isSwapUpToDate = false;
    }

    /**
     * Creates a new Grid2DSquareCellDoubleChunk64CellMap.
     *
     * @param g The Grid2DSquareCellDouble this is a chunk of.
     * @param chunkID The ID this will have. TODO: deal with case: (
     * grid2DSquareCellDouble.getChunkNrows() *
     * grid2DSquareCellDouble.getChunkNcols() > 64 )
     */
    protected Grids_Grid2DSquareCellDoubleChunk64CellMap(
            Grids_Grid2DSquareCellDouble g,
            Grids_2D_ID_int chunkID) {
        super(g.ge);
        boolean handleOutOfMemoryError = false;
        this.ChunkID = chunkID;
        initGrid2DSquareCell(g);
        Long nCellsInChunk
                = (long) g.getChunkNRows(handleOutOfMemoryError)
                * (long) g.getChunkNCols(handleOutOfMemoryError);
        if (nCellsInChunk <= 64) {
            initData();
        } else {
            System.err.println(
                    "grid2DSquareCellDoubleChunk.chunkNrows * "
                    + "grid2DSquareCellDoubleChunk.chunkNcols > 64 "
                    + "in \n"
                    + this.getClass().getName()
                    + ".Grid2DSquareCellDoubleChunk64CellMap( \n"
                    + "    Grid2DSquareCellDouble ( "
                    + g.getBasicDescription(false) + " ), \n"
                    + "    ChunkID ( " + chunkID + " ) );");
            // Sample? What to do? Hmmmmmmm!
        }
        this.isSwapUpToDate = false;
    }

    /**
     * Creates a new Grid2DSquareCellDoubleChunk64CellMap.
     *
     *
     *
     * @param gridChunk The AbstractGrid2DSquareCellDoubleChunkk this is
     * constructed from.
     * @param _ChunkID The ID this will have.
     */
    protected Grids_Grid2DSquareCellDoubleChunk64CellMap(
            Grids_AbstractGrid2DSquareCellDoubleChunk gridChunk,
            Grids_2D_ID_int _ChunkID) {
        super(gridChunk.ge);
        boolean handleOutOfMemoryError = false;
        this.ChunkID = _ChunkID;
        Grids_Grid2DSquareCellDouble g
                = gridChunk.getGrid2DSquareCellDouble();
        initGrid2DSquareCell(g);
        int chunkNrows = g.getChunkNRows(handleOutOfMemoryError);
        int chunkNcols = g.getChunkNCols(handleOutOfMemoryError);
        if (chunkNrows * chunkNcols <= 64) {
            double _NoDataValue = g.getNoDataValue(handleOutOfMemoryError);
            initData();
            double value;
            for (int row = 0; row < chunkNrows; row++) {
                for (int col = 0; col < chunkNcols; col++) {
                    value = gridChunk.getCell(
                            row,
                            col,
                            _NoDataValue,
                            handleOutOfMemoryError);
                    if (value != _NoDataValue) {
                        initCell(
                                row,
                                col,
                                value);
                    }
                }
            }
        } else {
            System.err.println(
                    "grid2DSquareCellDoubleChunk.chunkNrows * "
                    + "grid2DSquareCellDoubleChunk.chunkNcols > 64 "
                    + "in \n"
                    + this.getClass().getName()
                    + ".Grid2DSquareCellDoubleChunk64CellMap( \n"
                    + "    Grid2DSquareCellDouble ( "
                    + g.getBasicDescription(handleOutOfMemoryError) + " ), \n"
                    + "    ChunkID ( " + _ChunkID + " ) );");
            // Sample? What to do? Hmmmmmmm!
        }
        this.isSwapUpToDate = false;
    }

    /**
     * Initialises the data associated with this.
     */
    @Override
    protected final void initData() {
        this.data = new TDoubleLongHashMap();
    }

    /**
     * Clears the data associated with this.
     */
    @Override
    protected void clearData() {
        this.data = null;
        System.gc();
    }

    /**
     * Returns this.data TODO: This could be made public if a copy is returned!
     *
     * @return
     */
    protected TDoubleLongHashMap getData() {
        return this.data;
    }

    /**
     * Returns all the values in row major order as a double[].
     *
     * @return
     */
    protected @Override
    double[] toArrayIncludingNoDataValues() {
        boolean handleOutOfMemoryError = false;
        Grids_Grid2DSquareCellDouble g = getGrid2DSquareCellDouble();
        double noDataValue = g.getNoDataValue(handleOutOfMemoryError);
        int nrows = g.getChunkNRows(handleOutOfMemoryError);
        int ncols = g.getChunkNCols(handleOutOfMemoryError);
        double[] array = new double[nrows * ncols];
        Arrays.fill(array, noDataValue);
        TDoubleLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong valueMap = new Grids_UnsignedLong();
        int ite;
        boolean[] valueMapped;
        int position;
        for (ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            valueMap.setLong(iterator.value());
            valueMapped = valueMap.toBooleanArray();
            for (position = 0; position < valueMapped.length; position++) {
                if (valueMapped[position]) {
                    array[position] = iterator.key();
                }
            }
        }
        return array;
    }

    /**
     * Returns all the values (not including _NoDataValues) in row major order
     * as a double[].
     *
     * @return
     */
    protected @Override
    double[] toArrayNotIncludingNoDataValues() {
        double[] array = new double[getNonNoDataValueCountInt()];
        TDoubleLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong valueMap = new Grids_UnsignedLong();
        int ite;
        boolean[] valueMapped;
        int position;
        int index = 0;
        for (ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            valueMap.setLong(iterator.value());
            valueMapped = valueMap.toBooleanArray();
            for (position = 0; position < valueMapped.length; position++) {
                if (valueMapped[position]) {
                    array[index] = iterator.key();
                    index++;
                }
            }
        }
        return array;
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex.
     *
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of
     * this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin
     * of this chunk.
     * @param _NoDataValue The _NoDataValue of this.grid2DSquareCellDouble.
     * @return
     */
    protected @Override
    double getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double _NoDataValue) {
        boolean handleOutOfMemoryError = false;
        TDoubleLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong valueMap = new Grids_UnsignedLong();
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
        int chunkNcols = grid2DSquareCellDouble.getChunkNCols(handleOutOfMemoryError);
        int ite;
        int position;
        for (ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            valueMap.setLong(iterator.value());
            position = (chunkCellRowIndex * chunkNcols) + chunkCellColIndex;
            if (valueMap.isAtPosition(position)) {
                return iterator.key();
            }
        }
        return _NoDataValue;
    }

    /**
     * Initialises the value at position given by: chunk cell row
     * chunkCellRowIndex; chunk cell column chunkCellColIndex.
     *
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of
     * this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin
     * of this chunk.
     * @param valueToInitialise The value with which the cell is initialised.
     */
    @Override
    protected final void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double valueToInitialise) {
        boolean handleOutOfMemoryError = false;
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
        int chunkNcols = grid2DSquareCellDouble.getChunkNCols(handleOutOfMemoryError);
        long long0;
        if (this.data.containsKey(valueToInitialise)) {
            long0 = data.get(valueToInitialise)
                    + powerOf2(((chunkCellRowIndex * chunkNcols) + chunkCellColIndex));
            this.data.put(
                    valueToInitialise,
                    long0);
        } else {
            long0 = powerOf2(((chunkCellRowIndex * chunkNcols) + chunkCellColIndex));
            this.data.put(
                    valueToInitialise,
                    long0);
        }
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell column chunkCellColIndex and sets it to valueToSet
     *
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of
     * this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin
     * of this chunk.
     * @param valueToSet The value the cell is to be set to
     * @param noDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */@Override
    protected 
    double setCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double valueToSet,
            double noDataValue) {
        boolean handleOutOfMemoryError = false;
        Grids_Grid2DSquareCellDouble g = getGrid2DSquareCellDouble();
        double result = noDataValue;
        TDoubleLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong valueMap = new Grids_UnsignedLong();
        double value;
        boolean gotValue = false;
        boolean setValue;
        setValue = valueToSet == result;
        int position = (chunkCellRowIndex * g.getChunkNCols(handleOutOfMemoryError)) + chunkCellColIndex;
        long powerOf2 = powerOf2(position);
        int ite;
        for (ite = 0; ite < data.size(); ite++) {
            if (!(gotValue && setValue)) {
                try {
                    iterator.advance();
                } catch (ConcurrentModificationException why) {
                    ite = ite; // debug?
                }
                valueMap.setLong(iterator.value());
                value = iterator.key();
                //if ( ! gotValue ) { // Probably slows things down!?
                if (valueMap.isAtPosition(position)) {
                    // May be better to keep the value in data and tidy if
                    // necessary. This would save evaluating the if, but would
                    // mean that the chunk is likely to get heavier. However it
                    // is difficult to know what is best, it probably depends on
                    // the data and what is being done with it!
                    if (iterator.value() == powerOf2) {
                        if (value == valueToSet) {
                            return value;
                        } else {
                            //try {
                            this.data.remove(value); // Removal causes ConcurrentModificationException in iterator.advance()!
                            //} catch ( ConcurrentModificationException e ) {}
                        }
                    } else {
                        iterator.setValue(iterator.value() - powerOf2);
                    }
                    result = value;
                    gotValue = true;
                }
                //}
                //if ( ! setValue ) { // Probably slows things down!?
                if (value == valueToSet) {
                    iterator.setValue(iterator.value() + powerOf2);
                    setValue = true;
                }
                //}
            } else {
                setIsSwapUpToDate(false);
                return result;
            }
        }
        if (!setValue) {
            this.data.put(
                    valueToSet,
                    powerOf2);
        }
        setIsSwapUpToDate(false);
        return result;
    }

    /**
     * Returns 2 raised to the power of value as a long.
     *
     * @param value
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public long powerOf2(
            int value,
            boolean handleOutOfMemoryError) {
        try {
            long result = powerOf2(value);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
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
            return powerOf2(
                    value,
                    handleOutOfMemoryError);
        }
    }

    /**
     * Returns 2 raised to the power of value as a long.
     *
     * @param value
     * @return
     */
    protected long powerOf2(
            int value) {
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = this.getGrid2DSquareCellDouble();
        return grid2DSquareCellDouble.getUnsignedLongPowersOf2().powersOf2[value];
    }

    /**
     * Returns the number of cells with _NoDataValues as a BigInteger
     *
     * @return
     */
    protected @Override
    BigInteger getNonNoDataValueCountBigInteger() {
        TDoubleLongIterator iterator = this.data.iterator();
        long cellIDMap = 0L;
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            cellIDMap += iterator.value();
        }
        return new BigInteger("" + new Grids_UnsignedLong(cellIDMap).getCount());
    }

    /**
     * Returns the number of cells with _NoDataValues as an int
     *
     * @return
     */
    protected @Override
    int getNonNoDataValueCountInt() {
        TDoubleLongIterator iterator = this.data.iterator();
        long cellIDMap = 0L;
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            cellIDMap += iterator.value();
        }
        return new Grids_UnsignedLong(cellIDMap).getCount();
    }

    /**
     * For returning the sum of all non _NoDataValues as a BigDecimal
     *
     * @return
     */
    protected @Override
    BigDecimal getSumBigDecimal() {
        BigDecimal sum = new BigDecimal(0.0d);
        BigDecimal thisCount;
        TDoubleLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            value.setLong(iterator.value());
            thisCount = BigDecimal.valueOf((long) value.getCount());
            sum = sum.add(new BigDecimal(iterator.key()).multiply(thisCount));
        }
        return sum;
    }

    /**
     * For returning the minimum of all non _NoDataValues as a double
     *
     * @return
     */
    protected @Override
    double getMinDouble() {
        double min = Double.POSITIVE_INFINITY;
        TDoubleLongIterator iterator = this.data.iterator();
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            min = Math.min(min, iterator.key());
        }
        return min;
    }

    /**
     * For returning the maximum of all non _NoDataValues as a double
     *
     * @return
     */
    protected @Override
    double getMaxDouble() {
        double max = Double.NEGATIVE_INFINITY;
        TDoubleLongIterator iterator = this.data.iterator();
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            max = Math.max(max, iterator.key());
        }
        return max;
    }

    /**
     * For returning the Arithmetic Mean of all non _NoDataValues as a
     * BigDecimal. Using BigDecimal this should be as precise as possible with
     * doubles.
     *
     * @param numberOfDecimalPlaces The number of decimal places for precision.
     * @return
     */
    protected @Override
    BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces) {
        BigDecimal mean = new BigDecimal("0");
        BigDecimal count = new BigDecimal("0");
        BigDecimal thisCount;
        TDoubleLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            value.setLong(iterator.value());
            thisCount = BigDecimal.valueOf((long) value.getCount());
            count = count.add(thisCount);
            mean = mean.add(new BigDecimal(iterator.key()).multiply(thisCount));
        }
        // Might cause a divide by zero problem!
        try {
            return mean.divide(count, numberOfDecimalPlaces, BigDecimal.ROUND_HALF_EVEN);
        } catch (ArithmeticException e) {
            System.out.println(
                    "Divide by zero in \n"
                    + this.getClass().getName() + ".getArithmeticMeanBigDecimal(). "
                    + "Returning null!");
            return null;
        }
    }

    /**
     * For returning the Arithmetic Mean of all non _NoDataValues as a double.
     * Using BigDecimal this should be as precise as possible with doubles.
     *
     * @return
     */
    protected @Override
    double getArithmeticMeanDouble() {
        int numberOfDecimalPlaces = Double.toString(Double.MIN_VALUE).length();
        //int numberOfDecimalPlaces = 324;
        BigDecimal arithmeticMeanBigDecimal = getArithmeticMeanBigDecimal(numberOfDecimalPlaces);
        if (arithmeticMeanBigDecimal == null) {
            return Double.NaN;
        } else {
            return arithmeticMeanBigDecimal.doubleValue();
        }
    }

    /**
     * For returning the Geometric Mean of all non _NoDataValues as a double
     * Warning! This is imprecise and it can happen that Math.pow does not
     * return what might be expected! (For example, negative powers in the range
     * (0,1) for negative numbers.) TODO: Develop a pow function such as
     * com.ibm.icu.math.BigDecimal.pow This resource is not used here due to
     * licensing, but it could be...
     *
     * @return
     */
    //protected @Override double getGeometricMeanDouble() {
    protected double getGeometricMeanDouble() {
        BigDecimal mean = new BigDecimal(1.0d);
        BigDecimal count = new BigDecimal(0.0d);
        BigDecimal thisCount;
        //double mean = 1.0d;
        //double count = 0.0d;
        //byte thisCount = 0;
        TDoubleLongIterator iterator = this.data.iterator();
        double key;
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            key = iterator.key();
            value.setLong(iterator.value());
            thisCount = new BigDecimal((double) value.getCount());
            count = count.add(thisCount);
            mean = mean.multiply(new BigDecimal(key).multiply(thisCount));
            //thisCount = value.getCount();
            //count += thisCount;
            //mean *= key * ( double ) thisCount;
        }
        //return ( mean.pow( new BigDecimal( 1.0d ).divide( count, BigDecimal.ROUND_HALF_EVEN ) ) ).doubleValue();
        return Math.pow(mean.doubleValue(), 1.0d / count.doubleValue());
        //return Math.pow( mean, 1.0d / count );
    }

    /**
     * Returns the Harmonic Mean of all non _NoDataValues as a double. Zero
     * values are ignored in the calculation.
     *
     * @return
     */
    //protected @Override double getHarmonicMeanDouble() {
    protected double getHarmonicMeanDouble() {
        BigDecimal mean = new BigDecimal(1.0d);
        BigDecimal count = new BigDecimal(0.0d);
        BigDecimal thisCount;
        BigDecimal one = new BigDecimal(1.0d);
        TDoubleLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        BigDecimal bigDecimal0;
        BigDecimal bigDecimal1;
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            value.setLong(iterator.value());
            thisCount = BigDecimal.valueOf((long) value.getCount());
            if (iterator.key() != 0.0d) {
                count = count.add(thisCount);
                bigDecimal0 = new BigDecimal(iterator.key());
                bigDecimal1 = one.divide(
                        bigDecimal0,
                        325,
                        BigDecimal.ROUND_HALF_EVEN);
                mean = mean.add(thisCount.multiply(bigDecimal1));
            } else {
                System.out.println(
                        "Warning: Value of 0.0d encountered in "
                        + this.getClass().getName() + ".getHarmonicMeanDouble()!");
            }
        }
        if (mean.compareTo(BigDecimal.ZERO) != 0) {
            bigDecimal0 = count.divide(
                    mean,
                    325,
                    BigDecimal.ROUND_HALF_EVEN);
            return bigDecimal0.doubleValue();
        } else {
            return mean.doubleValue();
        }
    }

    /**
     * For returning the mode of all non _NoDataValues as a TDoubleHashSet
     *
     * @return
     */
    protected @Override
    TDoubleHashSet getModeTDoubleHashSet() {
        TDoubleHashSet mode = new TDoubleHashSet();
        TDoubleLongIterator iterator = this.data.iterator();
        byte modeCount = 0;
        byte thisCount;
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            value.setLong(iterator.value());
            thisCount = value.getCount();
            if (thisCount > modeCount) {
                mode.clear();
                mode.add(iterator.key());
                modeCount = thisCount;
            } else {
                if (thisCount == modeCount) {
                    mode.add(iterator.key());
                }
            }
        }
        return mode;
    }

    /**
     * For returning the median of all non _NoDataValues as a double
     *
     * @return
     */
    protected @Override
    double getMedianDouble() {
        int scale = 325;
        double[] keys = this.data.keys();
        sort1(keys, 0, keys.length);
        int nonNoDataValueCount = getNonNoDataValueCountInt();
        Grids_UnsignedLong unsignedLong = new Grids_UnsignedLong();
        int index = -1;
        if (nonNoDataValueCount % 2 == 0) {
            // Need arithmetic mean of ( ( nonNoDataValueCount / 2 ) - 1 )th
            // and ( nonNoDataValueCount / 2 )th values
            int requiredIndex = (nonNoDataValueCount / 2) - 1;
            boolean got = false;
            //double median = 0.0d;
            BigDecimal medianBigDecimal = null;
            for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                unsignedLong.setLong(this.data.get(keys[keyIndex]));
                index += unsignedLong.getCount();
                if (!got && index >= requiredIndex) {
                    //median = keys[ keyIndex ];
                    medianBigDecimal = new BigDecimal(keys[keyIndex]);
                    got = true;
                }
                if (index >= requiredIndex + 1) {
                    //median += keys[ keyIndex ];
                    //median /= 2.0d;
                    //return ( median + keys[ keyIndex ] ) / 2.0d;
                    BigDecimal bigDecimal0 = new BigDecimal(keys[keyIndex]);
                    BigDecimal bigDecimal1 = medianBigDecimal.add(bigDecimal0);
                    BigDecimal bigDecimalTwo = new BigDecimal(2.0d);
                    bigDecimal0 = (bigDecimal1).divide(
                            bigDecimalTwo,
                            scale,
                            BigDecimal.ROUND_HALF_EVEN);
                    return bigDecimal0.doubleValue();
                }
            }
        } else {
            // Need ( ( nonNoDataValueCount ) / 2 )th value
            int requiredIndex = nonNoDataValueCount / 2;
            for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                unsignedLong.setLong(this.data.get(keys[keyIndex]));
                index += unsignedLong.getCount();
                if (index >= requiredIndex) {
                    return keys[keyIndex];
                }
            }
        }
        return getGrid2DSquareCellDouble().getNoDataValue(false);
    }

    /**
     * For returning the standard deviation of all non _NoDataValues as a double
     *
     * @return
     */
    protected @Override
    double getStandardDeviationDouble() {
        double standardDeviation = 0.0d;
        double mean = getArithmeticMeanDouble();
        TDoubleLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        double count = 0.0d;
        double thisCount;
        for (int ite = 0; ite < data.size(); ite++) {
            iterator.advance();
            value.setLong(iterator.value());
            thisCount = (double) value.getCount();
            count += thisCount;
            standardDeviation
                    += ((iterator.key() - mean) * (iterator.key() - mean))
                    * thisCount;
        }
        if ((count - 1.0d) > 0) {
            return Math.sqrt(standardDeviation / (count - 1.0d));
        } else {
            return standardDeviation;
        }
    }

    /**
     * For returning the number of different values.
     *
     * @return
     */
    //protected @Override BigInteger getDiversity() {
    protected BigInteger getDiversity() {
        return new BigInteger(Integer.toString(this.data.size()));
    }

    /**
     * Returns a Grids_Grid2DSquareCellDoubleChunk64CellMapIterator for
     * iterating over the cells in this.
     *
     * @return
     */
    protected @Override
    Grids_AbstractIterator iterator() {
        return new Grids_Grid2DSquareCellDoubleChunk64CellMapIterator(this);
    }

}
