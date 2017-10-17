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
import gnu.trove.TIntLongHashMap;
import gnu.trove.TIntLongIterator;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_UnsignedLong;
/**
 * Grids_AbstractGrid2DSquareCellIntChunk extension that stores cell values in a 
 TIntLongHashMap. The maximum size of this Grids_AbstractGrid2DSquareCellIntChunk is 
 64 cells.
 TODO:
 Adjudicate when this is useful. What type of data?
 */
public class Grids_Grid2DSquareCellIntChunk64CellMap
        extends Grids_AbstractGrid2DSquareCellIntChunk {
    
    /**
     * For storing values mapped to a binary encoded long. The long is a key
     * which indicates if the value is that at a given location. Both keys and
     * values are unique.
     */
    private TIntLongHashMap data;
    
    /**
     * Creates a new Grid2DSquareCellIntChunk64CellMap.
     */
    protected Grids_Grid2DSquareCellIntChunk64CellMap() {}
    
    public Grids_Grid2DSquareCellIntChunk64CellMap(Grids_Environment ge) {
        super(ge);
        this.ChunkID = new Grids_2D_ID_int();
        this.data = new TIntLongHashMap();
        //this._Grid2DSquareCell = new Grid2DSquareCellDouble(_AbstractGrid2DSquareCell_HashSet, _HandleOutOfMemoryError);
        this.isSwapUpToDate = false;
    }
    
    /**
     * Creates a new Grid2DSquareCellIntChunk64CellMap.
     * 
     * @param grid2DSquareCellInt The Grid2DSquareCellDouble this is a chunk 
     *   of.
     * @param _ChunkID The ID this will have.
     */
    protected Grids_Grid2DSquareCellIntChunk64CellMap(
            Grids_Grid2DSquareCellInt grid2DSquareCellInt,
            Grids_2D_ID_int _ChunkID ) {
        super(grid2DSquareCellInt.ge);
        //initChunkID( _ChunkID );
        this.ChunkID = _ChunkID;
        initGrid2DSquareCell( grid2DSquareCellInt );
        long nChunkCells =
                ( long ) grid2DSquareCellInt.getChunkNRows() *
                ( long ) grid2DSquareCellInt.getChunkNCols();
        if ( nChunkCells <= 64 ) {
            initData();
        } else {
            throw new Error(
                    "grid2DSquareCellIntChunk.chunkNrows * " +
                    "grid2DSquareCellIntChunk.chunkNcols = " +
                    nChunkCells + " > 64" );
            // Sample? What to do? Hmmmmmmm!
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * Creates a new Grid2DSquareCellIntChunk64CellMap.
     * 
     * 
     * @param grid2DSquareCellIntChunk The 
 Grids_AbstractGrid2DSquareCellIntChunk this is constructed from.
     * @param _ChunkID The ID this will have.
     */
    protected Grids_Grid2DSquareCellIntChunk64CellMap(
            Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk,
            Grids_2D_ID_int _ChunkID ) {
        super(grid2DSquareCellIntChunk.ge);
        this.ChunkID = _ChunkID;
        Grids_Grid2DSquareCellInt grid2DSquareCellInt =
                grid2DSquareCellIntChunk.getGrid2DSquareCellInt();
        initGrid2DSquareCell( grid2DSquareCellInt );
        int chunkNrows = grid2DSquareCellInt.getChunkNRows();
        int chunkNcols = grid2DSquareCellInt.getChunkNCols();
        long nChunkCells =
                ( long ) chunkNrows *
                ( long ) chunkNcols;
        if ( nChunkCells <= 64 ) {
            int noDataValue = grid2DSquareCellInt.getNoDataValue(
                    Grid.ge.HandleOutOfMemoryErrorFalse );
            initData();
            int value;
                    boolean handleOutOfMemoryError = true;
            for ( int row = 0; row < chunkNrows; row ++ ) {
                for ( int col = 0; col < chunkNcols; col ++ ) {
                    value = grid2DSquareCellIntChunk.getCell(
                            row,
                            col,
                            noDataValue, 
                            handleOutOfMemoryError );
                    if ( value != noDataValue ) {
                        initCell(
                                row,
                                col,
                                value );
                    }
                }
            }
        } else {
            throw new Error(
                    "grid2DSquareCellIntChunk.chunkNrows * " +
                    "grid2DSquareCellIntChunk.chunkNcols = " +
                    nChunkCells + " > 64" );
            // Sample? What to do? Hmmmmmmm!
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * Initialises the data associated with this.
     */
    protected @Override void initData() {
        this.data = new TIntLongHashMap();
    }
    
    /**
     * Clears the data associated with this.
     */
    protected @Override void clearData() {
        this.data = null;
        System.gc();
    }
    
    /**
     * Returns this.data
     * TODO:
     * This could be made public if a copy is returned!
     * @return 
     */
    protected TIntLongHashMap getData() {
        return this.data;
    }
    
    /**
     * Returns all the values in row major order as a double[].
     * @return 
     */
    protected @Override int[] toArrayIncludingNoDataValues() {
        Grids_Grid2DSquareCellInt grid2DSquareCellInt = getGrid2DSquareCellInt();
        int noDataValue = grid2DSquareCellInt.getNoDataValue(
                getGrid().ge.HandleOutOfMemoryErrorFalse );
        int chunkNrows = grid2DSquareCellInt.getChunkNRows();
        int chunkNcols = grid2DSquareCellInt.getChunkNCols();
        int length = chunkNrows * chunkNcols;
        int[] array = new int[ length ];
        Arrays.fill( array, noDataValue );
        TIntLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong valueMap = new Grids_UnsignedLong();
        int ite;
        boolean[] valueMapped;
        int position;
        for ( ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            valueMap.setLong( iterator.value() );
            valueMapped = valueMap.toBooleanArray();
            length = valueMapped.length;
            for ( position = 0; position < length; position ++ ) {
                if ( valueMapped[ position ] ) {
                    array[ position ] = iterator.key();
                }
            }
        }
        return array;
    }
    
    /**
     * Returns all the values (not including noDataValues) in row major order 
     * as a double[].
     * @return 
     */
    protected @Override int[] toArrayNotIncludingNoDataValues() {
        int[] array = new int[ getNonNoDataValueCountInt() ];
        TIntLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong valueMap = new Grids_UnsignedLong();
        int ite;
        boolean[] valueMapped;
        int position;
        int index = 0;
        for ( ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            valueMap.setLong( iterator.value() );
            valueMapped = valueMap.toBooleanArray();
            for ( position = 0; position < valueMapped.length; position ++ ) {
                if ( valueMapped[ position ] ) {
                    array[ index ] = iterator.key();
                    index ++;
                }
            }
        }
        return array;
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex.
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of 
     *   this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin 
     *   of this chunk.
     * @param noDataValue The noDataValue of this.grid2DSquareCellInt.
     * @return 
     */
    protected @Override int getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int noDataValue ) {
        TIntLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong valueMap = new Grids_UnsignedLong();
        Grids_Grid2DSquareCellInt grid2DSquareCellInt = getGrid2DSquareCellInt();
        int chunkNcols = grid2DSquareCellInt.getChunkNCols();
        int ite;
        int position;
        for ( ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            valueMap.setLong( iterator.value() );
            position = ( chunkCellRowIndex * chunkNcols ) + chunkCellColIndex;
            if ( valueMap.isAtPosition( position ) ) {
                return iterator.key();
            }
        }
        return noDataValue;
    }
    
    /**
     * Initialises the value at position given by: chunk cell row 
     * chunkCellRowIndex; chunk cell column chunkCellColIndex.
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of 
     *   this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin 
     *   of this chunk.
     * @param valueToInitialise The value with which the cell is initialised.
     */
    protected @Override void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int valueToInitialise ) {
        Grids_Grid2DSquareCellInt grid2DSquareCellInt = getGrid2DSquareCellInt();
        int chunkNcols = grid2DSquareCellInt.getChunkNCols();
        long value = powerOf2( ( chunkCellRowIndex * chunkNcols ) + chunkCellColIndex );
        if ( this.data.containsKey( valueToInitialise ) ) {
            value += data.get( valueToInitialise );
        }
        this.data.put(
                valueToInitialise,
                value );
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell column chunkCellColIndex and sets it to valueToSet
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of 
     *   this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin 
     *   of this chunk.
     * @param valueToSet The value the cell is to be set to
     * @param noDataValue the noDataValue of this.grid2DSquareCellDouble
     * @return 
     */
    protected @Override int setCell( 
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int valueToSet,
            int noDataValue ) {
        Grids_Grid2DSquareCellInt grid2DSquareCellInt = getGrid2DSquareCellInt();
        int result = noDataValue;
        TIntLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong valueMap = new Grids_UnsignedLong();
        int value;
        boolean gotValue = false;
        boolean setValue;
        setValue = valueToSet == result;
        int position = 
                ( chunkCellRowIndex * grid2DSquareCellInt.getChunkNCols() ) + 
                chunkCellColIndex;
        long powerOf2 = powerOf2( position );
        int ite;
        for ( ite = 0; ite < data.size(); ite ++ ) {
            if ( ! ( gotValue && setValue ) ) {
                try{
                    iterator.advance();
                } catch ( ConcurrentModificationException why ) {}
                valueMap.setLong( iterator.value() );
                value = iterator.key();
                //if ( ! gotValue ) { // Probably slows things down!?
                if ( valueMap.isAtPosition( position ) ) {
                    // May be better to keep the value in data and tidy if
                    // necessary. This would save evaluating the if, but would
                    // mean that the chunk is likely to get heavier. However it
                    // is difficult to know what is best, it probably depends on
                    // the data and what is being done with it!
                    if ( iterator.value() == powerOf2 ) {
                        if ( value == valueToSet ) {
                            return value;
                        } else {
                            //try {
                            this.data.remove( value ); 
                            // Removal causes ConcurrentModificationException in iterator.advance()!
                            //} catch ( ConcurrentModificationException e ) {}
                        }
                    } else {
                        iterator.setValue( iterator.value() - powerOf2 );
                    }
                    result = value;
                    gotValue = true;
                }
                //}
                //if ( ! setValue ) { // Probably slows things down!?
                if ( value == valueToSet ) {
                    iterator.setValue( iterator.value() + powerOf2 );
                    setValue = true;
                }
                //}
            } else {
                setIsSwapUpToDate( false );
                return result;
            }
        }
        if ( ! setValue ) {
            this.data.put( valueToSet, powerOf2 );
        }
        this.setIsSwapUpToDate( false );
        return result;
    }
    
    /**
     * Returns 2 raised to the power of value as a long.
     * @param value
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public long powerOf2( 
            int value,
            boolean handleOutOfMemoryError ) {
        try {
            long result = powerOf2( value );
            getGrid().ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch ( OutOfMemoryError a_OutOfMemoryError ) {
            this.getGrid().ge.clear_MemoryReserve();
                if (this.getGrid().ge.swapToFile_Grid2DSquareCellChunkExcept_Account(
                        this.getGrid(),
                        this.ChunkID,
                        false) < 1L){
                        throw a_OutOfMemoryError;
                }
            this.getGrid().ge.init_MemoryReserve(this.getGrid(),
                        this.ChunkID,
                        handleOutOfMemoryError );
            return powerOf2( 
                    value,
                    handleOutOfMemoryError );
        }
    }
    
    /**
     * Returns 2 raised to the power of value as a long.
     * @param value
     * @return 
     */
    protected long powerOf2( 
            int value ){
        Grids_Grid2DSquareCellInt grid2DSquareCellInt = this.getGrid2DSquareCellInt();
        return grid2DSquareCellInt.getUnsignedLongPowersOf2().powersOf2[ value ];
    }
    
    /**
     * Returns the number of cells with noDataValues as a BigInteger
     * @return 
     */
    protected @Override BigInteger getNonNoDataValueCountBigInteger() {
        TIntLongIterator iterator = this.data.iterator();
        long cellIDMap = 0L;
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            cellIDMap += iterator.value();
        }
        return new BigInteger( "" + new Grids_UnsignedLong( cellIDMap ).getCount() );
    }
    
    /**
     * Returns the number of cells with noDataValues as an int
     * @return 
     */
    protected @Override int getNonNoDataValueCountInt() {
        TIntLongIterator iterator = this.data.iterator();
        long cellIDMap = 0L;
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            cellIDMap += iterator.value();
        }
        return new Grids_UnsignedLong( cellIDMap ).getCount();
    }
    
    /**
     * For returning the sum of all non noDataValues as a BigDecimal
     * @return 
     */
    protected @Override BigDecimal getSumBigDecimal() {
        BigDecimal sum = new BigDecimal( 0.0d );
        BigDecimal thisCount;
        TIntLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            value.setLong( iterator.value() );
            thisCount = BigDecimal.valueOf( ( long ) value.getCount() );
            sum = sum.add( new BigDecimal( iterator.key() ).multiply( thisCount ) );
        }
        return sum;
    }
    
    /**
     * For returning the minimum of all non noDataValues as a int.
     * @return 
     */
    protected @Override int getMinInt() {
        int min = Integer.MAX_VALUE;
        TIntLongIterator iterator = this.data.iterator();
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            min = Math.min( min, iterator.key() );
        }
        return min;
    }
    
    /**
     * For returning the maximum of all non noDataValues as a int.
     * @return 
     */
    protected @Override int getMaxInt() {
        int max = Integer.MIN_VALUE;
        TIntLongIterator iterator = this.data.iterator();
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            max = Math.max( max, iterator.key() );
        }
        return max;
    }
    
    /**
     * For returning the Arithmetic Mean of all non noDataValues as a BigDecimal.
     * Using BigDecimal this should be as precise as possible with doubles.
     * @param numberOfDecimalPlaces The number of decimal places to which the
     *   result is precise.
     * @return 
     */
    protected @Override BigDecimal getArithmeticMeanBigDecimal(
            int numberOfDecimalPlaces ) {
        BigDecimal mean = new BigDecimal("0");
        BigDecimal count = new BigDecimal("0");
        BigDecimal thisCount;
        //double mean = 0.0d;
        //double count = 0.0d;
        //byte thisCount = 0;
        TIntLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            value.setLong( iterator.value() );
            thisCount = BigDecimal.valueOf( ( long ) value.getCount() );
            count = count.add( thisCount );
            mean = mean.add( 
                    new BigDecimal( iterator.key() ).multiply( thisCount ) );
            //count += ( double ) thisCount;
            //mean += iterator.key() * ( double ) thisCount;
        }
        try {
            return mean.divide( 
                    count, 
                    numberOfDecimalPlaces, 
                    BigDecimal.ROUND_HALF_EVEN );
        } catch ( ArithmeticException e ) {
            System.out.println(
                    "Divide by zero in " + this.getClass().getName() + 
                    ".getArithmeticMean(). Returning null!" );
            return null;
            //return Double.NaN;
            //return 0.0d;
        }
        //return mean /= count;
    }
    
    /**
     * For returning the Geometric Mean of all non noDataValues as a BigDecimal
     * Warning! This is imprecise and it can happen that Math.pow does not
     * return what might be expected! (For example, negative powers in the range
     * (0,1) for negative numbers.)
     * TODO:
     * Develop a pow function such as com.ibm.icu.math.BigDecimal.pow
     * This resource is not used here due to licensing, but it could be...
     * @return 
     */
    protected BigDecimal getGeometricMeanBigDecimal() {
        BigDecimal mean = new BigDecimal(1.0d);
        BigDecimal count = new BigDecimal(0.0d);
        BigDecimal thisCount;
        //double mean = 1.0d;
        //double count = 0.0d;
        //byte thisCount = 0;
        TIntLongIterator iterator = this.data.iterator();
        double key;
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            key = iterator.key();
            value.setLong( iterator.value() );
            thisCount = new BigDecimal( ( double ) value.getCount() );
            count = count.add( thisCount );
            mean = mean.multiply( new BigDecimal( key ).multiply( thisCount ) );
            //thisCount = value.getCount();
            //count += thisCount;
            //mean *= key * ( double ) thisCount;
        }
        //return ( mean.pow( new BigDecimal( 1.0d ).
        //divide( count, BigDecimal.ROUND_HALF_EVEN ) ) ).doubleValue();
        return new BigDecimal( 
                Math.pow( mean.doubleValue(), 1.0d / count.doubleValue() ) );
        //return Math.pow( mean, 1.0d / count );
    }
    
    /**
     * Returns the Harmonic Mean of all non noDataValues as a BigDecimal. Zero
     * values are ignored in the calculation.
     * @return 
     */
    protected BigDecimal getHarmonicMeanBigDecimal() {
        BigDecimal mean = new BigDecimal(1.0d);
        BigDecimal count = new BigDecimal(0.0d);
        BigDecimal thisCount;
        BigDecimal one = new BigDecimal(1.0d);
        BigDecimal bigDecimal0;
        BigDecimal bigDecimal1;
        TIntLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            value.setLong( iterator.value() );
            thisCount = BigDecimal.valueOf( ( long ) value.getCount() );
            if ( iterator.key() != 0.0d ) {
                count = count.add( thisCount );
                bigDecimal0 = new BigDecimal( iterator.key() );
                bigDecimal1 = one.divide( 
                        bigDecimal0, 
                        325, 
                        BigDecimal.ROUND_HALF_EVEN );
                mean = mean.add( thisCount.multiply( bigDecimal1 ) );
            } else {
                System.out.println(
                        "Warning: Value of 0.0d encountered in " + 
                        this.getClass().getName() + "!" );
            }
        }
        if ( mean.compareTo( new BigDecimal( 0.0d ) ) != 0 ) {
            return count.divide( 
                    mean, 
                    325, 
                    BigDecimal.ROUND_HALF_EVEN );
        } else {
            return mean;
        }
    }
    
    /**
     * For returning the mode of all non noDataValues as a TDoubleHashSet
     * @return 
     */
    protected @Override TIntHashSet getModeTIntHashSet() {
        TIntHashSet mode = new TIntHashSet();
        TIntLongIterator iterator = this.data.iterator();
        byte modeCount = 0;
        byte thisCount;
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            value.setLong( iterator.value() );
            thisCount = value.getCount();
            if ( thisCount > modeCount ) {
                mode.clear();
                mode.add( iterator.key() );
                modeCount = thisCount;
            } else {
                if ( thisCount == modeCount ) {
                    mode.add( iterator.key() );
                }
            }
        }
        return mode;
    }
    
    /**
     * For returning the median of all non noDataValues as a double
     * @return 
     */
    protected @Override double getMedianDouble() {
        int scale = 325;
        int[] keys = this.data.keys();
        sort1( keys, 0, keys.length );
        int nonNoDataValueCount = getNonNoDataValueCountInt();
        Grids_UnsignedLong unsignedLong = new Grids_UnsignedLong();
        int index = -1;
        BigDecimal bigDecimal0;
        BigDecimal bigDecimal1;
        if ( nonNoDataValueCount % 2 == 0 ) {
            // Need arithmetic mean of ( ( nonNoDataValueCount / 2 ) - 1 )th
            // and ( nonNoDataValueCount / 2 )th values
            int requiredIndex = ( nonNoDataValueCount / 2 ) - 1;
            boolean got = false;
            //double median = 0.0d;
            BigDecimal medianBigDecimal = null;
            for ( int keyIndex = 0; keyIndex < keys.length; keyIndex ++ ) {
                unsignedLong.setLong( this.data.get( keys[ keyIndex ] ) );
                index += unsignedLong.getCount();
                if ( ! got && index >= requiredIndex ) {
                    //median = keys[ keyIndex ];
                    medianBigDecimal = new BigDecimal( keys[ keyIndex ] );
                    got = true;
                }
                if ( index >= requiredIndex + 1 ) {
                    //median += keys[ keyIndex ];
                    //median /= 2.0d;
                    //return ( median + keys[ keyIndex ] ) / 2.0d;
                    bigDecimal0 = new BigDecimal( keys[ keyIndex ] );
                    bigDecimal1 = medianBigDecimal.add( bigDecimal0 );
                    BigDecimal bigDecimalTwo = new BigDecimal( 2.0d );
                    bigDecimal0 = bigDecimal1.divide( 
                            bigDecimalTwo, 
                            scale, 
                            BigDecimal.ROUND_HALF_EVEN );
                    return bigDecimal0.doubleValue();
                }
            }
        } else {
            // Need ( ( nonNoDataValueCount ) / 2 )th value
            int requiredIndex = nonNoDataValueCount / 2;
            for ( int keyIndex = 0; keyIndex < keys.length; keyIndex ++ ) {
                unsignedLong.setLong( this.data.get( keys[ keyIndex ] ) );
                index += unsignedLong.getCount();
                if ( index >= requiredIndex ) {
                    return keys[ keyIndex ];
                }
            }
        }
        return getGrid2DSquareCell().getNoDataValueBigDecimal( true ).doubleValue();
    }
    
    /**
     * For returning the standard deviation of all non noDataValues as a double
     * @return 
     */
    protected @Override double getStandardDeviationDouble() {
        double standardDeviation = 0.0d;
        double mean = getArithmeticMeanDouble();
        TIntLongIterator iterator = this.data.iterator();
        Grids_UnsignedLong value = new Grids_UnsignedLong();
        double count = 0.0d;
        double thisCount;
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            value.setLong( iterator.value() );
            thisCount = ( double ) value.getCount();
            count += thisCount;
            standardDeviation += 
                    ( ( iterator.key() - mean ) * ( iterator.key() - mean ) )
                    * thisCount;
        }
        if ( ( count - 1.0d ) > 0 ) {
            return Math.sqrt( standardDeviation / ( count - 1.0d ) );
        } else {
            return standardDeviation;
        }
    }
    
    /**
     * For returning the number of different values.
     * @return 
     */
    protected BigInteger getDiversity() {
        return new BigInteger( Integer.toString( this.data.size() ) );
    }
    
    /**
     * Returns a Grids_Grid2DSquareCellIntChunk64CellMapIterator for iterating over
 the cells in this.
     * @return 
     */
    protected @Override Grids_AbstractIterator iterator() {
        return new Grids_Grid2DSquareCellIntChunk64CellMapIterator( this );
    }
    
}