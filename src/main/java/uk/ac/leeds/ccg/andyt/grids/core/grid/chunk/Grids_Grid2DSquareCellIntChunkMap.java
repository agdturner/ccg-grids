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
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;
/**
 * Grids_AbstractGrid2DSquareCellIntChunk extension that stores cell values in a
 TIntObjectHashMap.
 * TODO:
 * Enable default value to be values other than noDataValue.
 */
public  class Grids_Grid2DSquareCellIntChunkMap
        extends Grids_AbstractGrid2DSquareCellIntChunk
        implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * A value initialised with grid that can be used to optimise storage.
     * Storage is optimised with the defaultValue set to the most common value.
     * By default the defaultValue is set to
     * this._Grid2DSquareCell.getNoDataValue().
     */
    private int defaultValue;
    
    /**
     * For storing values mapped to a Grids_2D_ID_int HashSet or an individual
     * Grids_2D_ID_int.
     */
    private TIntObjectHashMap data;
    
    /**
     * Default constructor.
     */
    public Grids_Grid2DSquareCellIntChunkMap() {
        this.ChunkID = new Grids_2D_ID_int();
        this.data = new TIntObjectHashMap();
        this.defaultValue = Integer.MIN_VALUE;
        //this._Grid2DSquareCell = new Grid2DSquareCellInt(_AbstractGrid2DSquareCell_HashSet);
        this.isSwapUpToDate = false;
    }
    
    /**
     * Creates a new Grid2DSquareCellIntChunkMap
     * 
     * @param grid2DSquareCellInt
     * @param _ChunkID
     * Default:
     * default value to grid2DSquareCellInt.getNoDataValue()
     */
    protected Grids_Grid2DSquareCellIntChunkMap(
            Grids_Grid2DSquareCellInt grid2DSquareCellInt,
            Grids_2D_ID_int _ChunkID ) {
        this(   grid2DSquareCellInt,
                _ChunkID,
                grid2DSquareCellInt.getNoDataValue(
                grid2DSquareCellInt.ge.HandleOutOfMemoryErrorFalse ) );
    }
    
    /**
     * Creates a new Grid2DSquareCellIntChunkMap
     * 
     * @param grid2DSquareCellInt
     * @param _ChunkID
     * @param defaultValue
     */
    protected Grids_Grid2DSquareCellIntChunkMap(
            Grids_Grid2DSquareCellInt grid2DSquareCellInt,
            Grids_2D_ID_int _ChunkID,
            int defaultValue ) {
        this.ChunkID = _ChunkID;
        initGrid2DSquareCell( grid2DSquareCellInt );
        this.defaultValue = defaultValue;
        initData();
        this.isSwapUpToDate = false;
    }
    
    /**
     * Creates a new Grid2DSquareCellIntChunkMap
     * 
     * @param grid2DSquareCellIntChunk
     * @param _ChunkID
     * Default:
     * default value to grid2DSquareCellInt.getNoDataValue()
     * TODO:
     * Optimise for different types.
     */
    protected Grids_Grid2DSquareCellIntChunkMap(
            Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk,
            Grids_2D_ID_int _ChunkID ) {
        this(   grid2DSquareCellIntChunk,
                _ChunkID,
                grid2DSquareCellIntChunk.getGrid2DSquareCellInt().getNoDataValue(
                grid2DSquareCellIntChunk.Grid.ge.HandleOutOfMemoryErrorFalse ) );
    }
    
    /**
     * Creates a new Grid2DSquareCellIntChunkMap
     * 
     * @param grid2DSquareCellIntChunk
     * @param chunkID
     * Default:
     * default value to grid2DSquareCellInt.getNoDataValue()
     * TODO:
     * Optimise for different types.
     * @param defaultValue
     */
    protected Grids_Grid2DSquareCellIntChunkMap(
            Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk,
            Grids_2D_ID_int chunkID,
            int defaultValue ) {
        this.ChunkID = chunkID;
        Grids_Grid2DSquareCellInt grid2DSquareCellInt =
                grid2DSquareCellIntChunk.getGrid2DSquareCellInt();
        initGrid2DSquareCell( grid2DSquareCellInt );
        int chunkNrows = grid2DSquareCellInt.getChunkNRows();
        int chunkNcols = grid2DSquareCellInt.getChunkNCols();
        initData();
        int value;
        boolean handleOutOfMemoryError = true;
        for ( int row = 0; row < chunkNrows; row ++ ) {
            for ( int col = 0; col < chunkNcols; col ++ ) {
                value = grid2DSquareCellIntChunk.getCell(
                        row,
                        col,
                        this.defaultValue,
                        handleOutOfMemoryError );
                if ( value != defaultValue ) {
                    initCell(
                            row,
                            col,
                            value );
                }
            }
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * Returns a description of this.
     * @return 
     */
    @Override
    protected String getDescription() {
        return  "Grid2DSquareCellIntChunkMap( \n" +
                "ChunkID( " + this.ChunkID.toString() + " ) )";
    }
    
    /**
     * Initialises the data associated with this.
     */
    @Override
    public void initData() {
        this.data = new TIntObjectHashMap();
    }
    
    /**
     * Returns this.data.
     * @return 
     */
    public TIntObjectHashMap getData(){
        return this.data;
    }
    
    /**
     * Clears the data associated with this.
     */
    @Override
    public void clearData() {
        this.data = null;
        System.gc();
    }
    
    /**
     * Sets this.defaultValue to defaultValue.
     */
    private void initDefaultValue( int defaultValue ) {
        this.defaultValue = defaultValue;
    }
    
    /**
     * Returns values in row major order as a int[].
     * @return 
     */
    @Override
    public int[] toArrayIncludingNoDataValues() {
        Grids_Grid2DSquareCellInt grid2DSquareCellInt = getGrid2DSquareCellInt();
        int nrows = grid2DSquareCellInt.getChunkNRows();
        int ncols = grid2DSquareCellInt.getChunkNCols();
        int[] array;
        long ncells = ( long ) nrows * ( long ) ncols;
        if ( ncells > Integer.MAX_VALUE ) {
            //throw new PrecisionExcpetion
            System.out.println(
                    "PrecisionExcpetion in " +
                    this.getClass().getName() + ".toArray()!" );
            System.out.println(
                    "Warning! The returned array size is only " +
                    Integer.MAX_VALUE + " instead of " + ncells );
            array = new int[ Integer.MAX_VALUE ];
        }
        array = new int[ nrows * ncols ];
        Arrays.fill( array, this.defaultValue );
        TIntObjectHashMap data = getData();
        TIntObjectIterator iterator = data.iterator();
        HashSet set;
        Grids_2D_ID_int chunkCellID;
        int ite;
        int position;
        for ( ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            try {
                set = ( HashSet ) iterator.value();
                Iterator setIterator = set.iterator();
                while ( setIterator.hasNext() ) {
                    chunkCellID = ( Grids_2D_ID_int ) setIterator.next();
                    position =
                            ( chunkCellID.getRow() * ncols ) +
                            chunkCellID.getCol();
                    array[ position ] = iterator.key();
                }
            } catch ( java.lang.ClassCastException e ) {
                chunkCellID = ( Grids_2D_ID_int ) iterator.value();
                position =
                        ( chunkCellID.getRow() * ncols ) +
                        chunkCellID.getCol();
                array[ position ] = iterator.key();
            }
        }
        return array;
    }
    
    /**
     * Returns non noDataValue in row major order as a int[].
     * @return 
     */
    @Override
    public int[] toArrayNotIncludingNoDataValues() {
        int noDataValue = getGrid2DSquareCellInt().getNoDataValue(
                getGrid().ge.HandleOutOfMemoryErrorFalse );
        BigInteger nonNoDataValueCountBigInteger =
                getNonNoDataValueCountBigInteger();
        int[] array;
        BigInteger bigInteger0 = nonNoDataValueCountBigInteger.add(
                BigInteger.ONE );
        BigInteger bigInteger1 = new BigInteger(
                Integer.toString( Integer.MAX_VALUE ) );
        if ( bigInteger0.compareTo( bigInteger1 ) > 0 ) {
            //throw new PrecisionExcpetion
            System.out.println(
                    "PrecisionExcpetion in " + this.getClass().getName() +
                    ".toArrayNotIncludingNoDataValues()!" );
            System.out.println(
                    "Warning! The returned array size is only " +
                    Integer.MAX_VALUE + " instead of " +
                    nonNoDataValueCountBigInteger.toString() );
            array = new int[ Integer.MAX_VALUE ];
        }
        array = new int[ nonNoDataValueCountBigInteger.intValue() ];
        TIntObjectIterator iterator = this.data.iterator();
        TIntObjectHashMap data = getData();
        HashSet set;
        Grids_2D_ID_int chunkCellID;
        int ite;
        int position = 0;
        int value;
        try {
            for ( ite = 0; ite < data.size(); ite ++ ) {
                iterator.advance();
                try {
                    set = ( HashSet ) iterator.value();
                    Iterator setIterator = set.iterator();
                    while ( setIterator.hasNext() ) {
                        chunkCellID = ( Grids_2D_ID_int ) setIterator.next();
                        value = iterator.key();
                        if ( value != noDataValue ) {
                            array[ position ] = iterator.key();
                        }
                        position ++;
                    }
                } catch ( java.lang.ClassCastException e ) {
                    chunkCellID = ( Grids_2D_ID_int ) iterator.value();
                    value = iterator.key();
                    if ( value != noDataValue ) {
                        array[ position ] = iterator.key();
                    }
                    position ++;
                }
            }
        } catch ( java.lang.ArrayIndexOutOfBoundsException e ) {
        } catch ( java.lang.NegativeArraySizeException e ) {}
        return array;
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex as a int.
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     *   this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     *   of this chunk
     * @param noDataValue the noDataValue of this.grid2DSquareCellInt
     * @return 
     */
    @Override
    public int getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int noDataValue ) {
        return getCell(
                new Grids_2D_ID_int( chunkCellRowIndex, chunkCellColIndex ),
                noDataValue );
    }
    
    /**
     * Returns the value of cell with CellID given by chunkCellID
     * @param chunkCellID the chunk CellID of cell thats value is to be returned
     * @param noDataValue the noDataValue of this.grid2DSquareCellInt
     * @return 
     */
    public int getCell(
            Grids_2D_ID_int chunkCellID,
            int noDataValue ) {
        TIntObjectIterator iterator = this.data.iterator();
        HashSet set;
        Grids_2D_ID_int individual;
        int ite;
        for ( ite = 0; ite < this.data.size(); ite ++ ) {
            iterator.advance();
            try {
                set = ( HashSet ) iterator.value();
                if ( set.contains( chunkCellID ) ) {
                    return iterator.key();
                }
            } catch ( java.lang.ClassCastException e ) {
                individual = ( Grids_2D_ID_int ) iterator.value();
                if ( individual.equals( chunkCellID ) ) {
                    return iterator.key();
                }
            }
        }
        return this.defaultValue;
    }
    
    /**
     * Initialises the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell column chunkCellColIndex. Utility method for constructor.
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     *   this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     *   of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     */
    @Override
    protected void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int valueToInitialise) {
        initCell(
                new Grids_2D_ID_int( chunkCellRowIndex, chunkCellColIndex ),
                valueToInitialise );
    }
    
    /**
     * Initialises the value of the chunk referred to by chunkCellID to
     * valueToInitialise. Utility method for constructor.
     * 
     * @param chunkCellID the Grids_AbstractGrid2DSquareCellIntChunk.Grids_2D_ID_int of
   the cell to be initialised
     * @param valueToInitialise the value with which the cell is initialised
     */
    protected void initCell(
            Grids_2D_ID_int chunkCellID,
            int valueToInitialise ) {
        if ( this.data.containsKey( valueToInitialise ) ) {
            HashSet set;
            try {
                set = ( HashSet ) this.data.get( valueToInitialise );
                set.add( chunkCellID );
            } catch ( java.lang.ClassCastException e ) {
                Grids_2D_ID_int individual =
                        ( Grids_2D_ID_int ) this.data.get( valueToInitialise );
                set = new HashSet();
                set.add( individual );
                set.add( chunkCellID );
                this.data.put( valueToInitialise, set );
            }
        } else {
            this.data.put( valueToInitialise, chunkCellID );
        }
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell column chunkCellColIndex and sets it to valueToSet
     * @param chunkCellRowIndex The row index of the cell w.r.t. the origin of
     *   this chunk.
     * @param chunkCellColIndex The column index of the cell w.r.t. the origin
     *   of this chunk.
     * @param valueToSet The value the cell is to be set to.
     * @param noDataValue The noDataValue of this.grid2DSquareCellInt.
     * @return 
     */
    @Override
    public int setCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int valueToSet,
            int noDataValue ) {
        return setCell(
                new Grids_2D_ID_int( chunkCellRowIndex, chunkCellColIndex ),
                valueToSet );
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell column chunkCellColIndex and sets it to valueToSet
     * 
     * @param chunkCellID the Grids_AbstractGrid2DSquareCellIntChunk.Grids_2D_ID_int of
   the cell to be initialised
     * @param valueToSet the value the cell is to be set to
     * @return 
     */
    public int setCell(
            Grids_2D_ID_int chunkCellID,
            int valueToSet ) {
        int result = this.defaultValue;
        TIntObjectIterator iterator = this.data.iterator();
        int value = result;
        boolean gotValue = false;
        boolean setValue = false;
        if ( valueToSet == result ) {
            setValue = true;
        } else {
            setValue = false;
        }
        int ite;
        int maxIte = this.data.size();
        HashSet set;
        Grids_2D_ID_int individual;
        boolean remove = false;
        int removeValue = value;
        for ( ite = 0; ite < maxIte; ite ++ ) {
            if ( ! ( gotValue && setValue ) ) {
                try {
                    iterator.advance();
                    value = iterator.key();
                    //if ( ! gotValue ) { // Probably slows things down!?
                    /* Is this better than:
                     * if ( iterator.value().getClass() == HashSet.class  ) {
                     * } else {
                     *     // ( iterator.value().getClass() == Grids_2D_ID_int.class )
                     * }
                     * ?
                     */
                    try {
                        //if ( iterator.value() == null ) { // Debugging code
                        //    int null0Debug = 0; // Debugging code
                        //} // Debugging code
                        set = ( HashSet ) iterator.value();
                        if ( set.contains( chunkCellID ) ) {
                            if ( value == valueToSet ) {
                                return value;
                            }
                            if ( set.size() == 2 ) {
                                // Convert other entry to a Grids_2D_ID_int
                                Iterator setIterator = set.iterator();
                                for ( int setIteratorIndex = 0; setIteratorIndex < 2; setIteratorIndex ++ ) {
                                    individual = ( Grids_2D_ID_int ) setIterator.next();
                                    if ( ! individual.equals( chunkCellID ) ) {
                                        iterator.setValue( individual );
                                    }
                                }
                            } else {
                                set.remove( chunkCellID );
                            }
                            result = value;
                            gotValue = true;
                        }
                    } catch ( java.lang.ClassCastException e ) {
                        individual = ( Grids_2D_ID_int ) iterator.value();
                        if ( individual.equals( chunkCellID ) ) {
                            if ( value == valueToSet ) {
                                return value;
                            }
                            // Need to set a value to be removed and remove it once stopped iterating to avoid ConcurrentModificationException.
                            remove = true;
                            removeValue = value;
                            result = value;
                            gotValue = true;
                        }
                    }
                    //if ( ! setValue ) { // Slows things down!? Probably...
                    if ( value == valueToSet ) {
                        try {
                            set = ( HashSet ) iterator.value();
                            set.add( chunkCellID );
                        } catch ( java.lang.ClassCastException cce ) {
                            individual = ( Grids_2D_ID_int ) iterator.value();
                            set = new HashSet();
                            set.add( individual );
                            set.add( chunkCellID );
                            iterator.setValue( set );
                        } catch ( Exception e1 ) {
                            // What is happening?
                            e1.toString();
                            int e1Debug = 0;
                        }
                        setValue = true;
                    }
                    //}
                    //} catch ( ConcurrentModificationException cme0 ) {
                    //    int cme0Debug = 0;
                    //} catch ( NoSuchElementException nsee0 ) {
                    //    int nsee0Debug = 0;
                    //} catch ( NullPointerException npe ) {
                    //    int npe0Debug = 0;
                } catch ( Exception e0 ) {
                    e0.printStackTrace();
                    int e0Debug = 0;
                }
            }
        }
        if ( remove ) {
            try {
                this.data.remove( removeValue );
            } catch ( ConcurrentModificationException cme0 ) {
                int cme0Debug = 0;
            } catch ( Exception e0 ) {
                // What is happening?
                e0.toString();
                int e0Debug = 0;
            }
        }
        if ( ! setValue ) {
            this.data.put( valueToSet, chunkCellID );
        }
        if ( getIsSwapUpToDate() ) { // Optimisation? Want a setCellFast method closer to initCell? What about an unmodifiable readOnly type chunk?
            if ( valueToSet != value ) {
                setIsSwapUpToDate( false );
            }
        }
        return result;
    }
    
    /**
     * Returns the number of cells with non noDataValues as a BigInteger.
     * @return 
     */
    public @Override BigInteger getNonNoDataValueCountBigInteger() {
        int noDataValue = getGrid2DSquareCellInt().getNoDataValue(
                getGrid().ge.HandleOutOfMemoryErrorFalse);
        TIntObjectIterator iterator = this.data.iterator();
        BigInteger nonNoDataCountBigInteger = BigInteger.ZERO;
        if ( this.defaultValue == noDataValue ) {
            for ( int ite = 0; ite < this.data.size(); ite ++ ) {
                iterator.advance();
                try {
                    nonNoDataCountBigInteger = nonNoDataCountBigInteger.add(
                            new BigInteger( Integer.toString( ( ( HashSet ) iterator.value() ).size() ) ) );
                } catch ( java.lang.ClassCastException e ) {
                    nonNoDataCountBigInteger = nonNoDataCountBigInteger.add( BigInteger.ONE );
                }
            }
        } else {
            for ( int ite = 0; ite < this.data.size(); ite ++ ) {
                iterator.advance();
                if ( iterator.key() == noDataValue ) {
                    try {
                        return new BigInteger( Long.toString( ( 
                                ( long ) this.getGrid().getChunkNRows(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse ) *
                                ( long ) this.getGrid().getChunkNCols(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse ) ) - ( ( HashSet ) iterator.value() ).size() ) );
                    } catch ( java.lang.ClassCastException e ) {
                        return new BigInteger( Long.toString( ( 
                                ( long ) this.getGrid().getChunkNRows(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse ) *
                                ( long ) this.getGrid().getChunkNCols(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse ) ) - 1L ) );
                    }
                }
            }
        }
        return nonNoDataCountBigInteger;
    }
    
    /**
     * Returns the sum of all non noDataValues as a BigDecimal.
     * @return 
     */
    protected @Override BigDecimal getSumBigDecimal() {
        BigDecimal sum = new BigDecimal( 0.0d );
        BigDecimal thisCount;
        TIntObjectIterator iterator = this.data.iterator();
        HashSet set;
        Grids_2D_ID_int individual;
        for ( int ite = 0; ite < data.size(); ite ++ ) {
            iterator.advance();
            try {
                thisCount = new BigDecimal( Integer.toString( ( ( HashSet ) iterator.value() ).size() ) );
                sum = sum.add( new BigDecimal( iterator.key() ).multiply( thisCount ) );
            } catch ( java.lang.ClassCastException e ) {
                sum = sum.add( new BigDecimal( iterator.key() ) );
            }
        }
        return sum;
    }
    
    //    /**
    //     * Returns the minimum of all non noDataValues as a double.
    //     */
    //    public double getMin() {
    //        double min = Double.POSITIVE_INFINITY;
    //        TDoubleObjectIterator iterator = this.data.iterator();
    //        for ( int ite = 0; ite < data.size(); ite ++ ) {
    //            iterator.advance();
    //            min = Math.min( min, iterator.key() );
    //        }
    //        return min;
    //    }
    //
    //    /**
    //     * Returns the maximum of all non noDataValues as a double
    //     */
    //    public double getMax() {
    //        double max = Double.NEGATIVE_INFINITY;
    //        TDoubleObjectIterator iterator = this.data.iterator();
    //        for ( int ite = 0; ite < data.size(); ite ++ ) {
    //            iterator.advance();
    //            max = Math.max( max, iterator.key() );
    //        }
    //        return max;
    //    }
    //
    //    /**
    //     * Returns the Arithmetic Mean of all non noDataValues as a double.
    //     * Using BigDecimal this should be as precise as possible with doubles.
    //     */
    //    public double getArithmeticMean() {
    //        BigDecimal sum = new BigDecimal( 0.0d );
    //        BigDecimal thisCount;
    //        BigDecimal totalCount = new BigDecimal( 0.0d );
    //        BigDecimal oneBigDecimal = new BigDecimal( 1.0d );
    //        TDoubleObjectIterator iterator = this.data.iterator();
    //        for ( int ite = 0; ite < data.size(); ite ++ ) {
    //            iterator.advance();
    //            try {
    //                thisCount = new BigDecimal( Integer.toString( ( ( HashSet ) iterator.value() ).size() ) );
    //                totalCount = totalCount.add( thisCount );
    //                sum = sum.add( new BigDecimal( iterator.key() ).multiply( thisCount ) );
    //            } catch ( java.lang.ClassCastException e ) {
    //                sum = sum.add( new BigDecimal( iterator.key() ) );
    //                totalCount = totalCount.add( oneBigDecimal );
    //            }
    //        }
    //        if ( totalCount.compareTo( new BigDecimal( 0.0d ) ) == 1 ) {
    //            return sum.divide( totalCount, 325, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
    //        } else {
    //            return getGrid2DSquareCellDouble().getNoDataValue();
    //        }
    //    }
    //
    //    /**
    //     * For returning the mode of all non noDataValues as a TDoubleHashSet
    //     */
    //    public TIntHashSet getMode() {
    //        TIntHashSet mode = new TIntHashSet();
    //        TIntObjectIterator iterator = this.data.iterator();
    //        int modeCount = 0;
    //        int thisCount;
    //        for ( int ite = 0; ite < data.size(); ite ++ ) {
    //            iterator.advance();
    //            try {
    //                thisCount = ( ( HashSet ) iterator.value() ).size();
    //            } catch ( java.lang.ClassCastException e ) {
    //                thisCount = 1;
    //            }
    //            if ( thisCount > modeCount ) {
    //                mode.clear();
    //                mode.add( iterator.key() );
    //                modeCount = thisCount;
    //            } else {
    //                if ( thisCount == modeCount ) {
    //                    mode.add( iterator.key() );
    //                }
    //            }
    //        }
    //        return mode;
    //    }
    //
    //    /**
    //     * For returning the median of all non noDataValues as a double
    //     */
    //    public double getMedian() {
    //        int scale = 325;
    //        double[] keys = this.data.keys();
    //        sort1( keys, 0, keys.length );
    //        long nonNoDataValueCount = getNonNoDataValueCount().longValue();
    //        if ( nonNoDataValueCount > 0 ) {
    //            long index = -1L;
    //            if ( nonNoDataValueCount % 2L == 0L ) {
    //                // Need arithmetic mean of ( ( nonNoDataValueCount / 2 ) - 1 )th
    //                // and ( nonNoDataValueCount / 2 )th values
    //                long requiredIndex = ( nonNoDataValueCount / 2L ) - 1L;
    //                boolean got = false;
    //                BigDecimal medianBigDecimal = null;
    //                for ( int keyIndex = 0; keyIndex < keys.length; keyIndex ++ ) {
    //                    try {
    //                        index += ( long ) ( ( HashSet ) this.data.get( keys[ keyIndex ] ) ).size();
    //                    } catch ( java.lang.ClassCastException e ) {
    //                        index ++;
    //                    }
    //                    if ( ! got && index >= requiredIndex ) {
    //                        medianBigDecimal = new BigDecimal( keys[ keyIndex ] );
    //                        got = true;
    //                    }
    //                    if ( index >= requiredIndex + 1L ) {
    //                        return ( medianBigDecimal.add( new BigDecimal( keys[ keyIndex ] ) ) ).divide( new BigDecimal( 2.0d ), scale, BigDecimal.ROUND_HALF_EVEN ).doubleValue();
    //                    }
    //                }
    //            } else {
    //                // Need ( ( nonNoDataValueCount ) / 2 )th value
    //                long requiredIndex = nonNoDataValueCount / 2L;
    //                for ( int keyIndex = 0; keyIndex < keys.length; keyIndex ++ ) {
    //                    try {
    //                        index += ( ( HashSet ) this.data.get( keys[ keyIndex ] ) ).size();
    //                    } catch ( java.lang.ClassCastException e ) {
    //                        index ++;
    //                    }
    //                    if ( index >= requiredIndex ) {
    //                        return keys[ keyIndex ];
    //                    }
    //                }
    //            }
    //        }
    //        return getGrid2DSquareCellInt().getNoDataValue();
    //    }
    //
    //    /**
    //     * For returning the standard deviation of all non noDataValues as a double
    //     */
    //    public double getStandardDeviation() {
    //        double standardDeviation = 0.0d;
    //        double mean = getArithmeticMean();
    //        TIntObjectIterator iterator = this.data.iterator();
    //        HashSet set;
    //        Grids_2D_ID_int individual;
    //        int ite;
    //        long count = 0L;
    //        long thisCount;
    //        for ( ite = 0; ite < this.data.size(); ite ++ ) {
    //            iterator.advance();
    //            try {
    //                thisCount = ( ( ( HashSet ) iterator.value() ).size() );
    //                count += thisCount;
    //                standardDeviation += ( ( iterator.key() - mean ) * ( iterator.key() - mean ) ) * thisCount;
    //            } catch ( java.lang.ClassCastException e ) {
    //                standardDeviation += ( iterator.key() - mean ) * ( iterator.key() - mean );
    //                count ++;
    //            }
    //        }
    //        if ( ( count - 1.0d ) > 0 ) {
    //            return Math.sqrt( standardDeviation / ( double ) ( count - 1L ) );
    //        } else {
    //            return standardDeviation;
    //        }
    //    }
    //
    //    /**
    //     * For returning the number of different values.
    //     */
    //    public BigInteger getDiversity() {
    //        return new BigInteger( Integer.toString( this.data.size() ) );
    //    }
    
    /**
     * Returns an Grids_AbstractIterator for iterating over the cells in this.
     * @return 
     */
    protected @Override Grids_AbstractIterator iterator() {
        return new Grids_Grid2DSquareCellIntChunkMapIterator( this );
    }
    
}