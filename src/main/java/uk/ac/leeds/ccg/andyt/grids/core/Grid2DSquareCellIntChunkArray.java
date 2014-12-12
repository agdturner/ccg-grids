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
import java.io.Serializable;
import java.util.Arrays;
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell.ChunkID;
import uk.ac.leeds.ccg.andyt.grids.utilities.AbstractIterator;
/**
 * AbstractGrid2DSquareCellIntChunk extension that stores cell values in a 
 * int[][].
 */
public class Grid2DSquareCellIntChunkArray 
        extends AbstractGrid2DSquareCellIntChunk 
        implements Serializable {
    
    private static final long serialVersionUID = 1L; 
    
    /**
     * For storing values arranged in rows and columns.
     */
    private int[][] data;
    
    /**
     * Default constructor
     */
    public Grid2DSquareCellIntChunkArray() {
        this._ChunkID = new ChunkID();
        this.data = new int[ 1 ][ 1 ];
        //this._Grid2DSquareCell = new Grid2DSquareCellInt(_AbstractGrid2DSquareCell_HashSet);
        this.isSwapUpToDate = false;
    }
 
    /**
     * Creates a new Grid2DSquareCellInt grid containing all no data values.
     * 
     * @param grid2DSquareCellInt
     * @param _ChunkID
     */
    protected Grid2DSquareCellIntChunkArray( 
            Grid2DSquareCellInt grid2DSquareCellInt, 
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        initGrid2DSquareCell( grid2DSquareCellInt );
        int noDataValue = grid2DSquareCellInt.getNoDataValue( 
                Grids_Environment.HandleOutOfMemoryErrorFalse );
        int chunkNrows = grid2DSquareCellInt.getChunkNRows( 
                _ChunkID, 
                Grids_Environment.HandleOutOfMemoryErrorFalse );
        int chunkNcols = grid2DSquareCellInt.getChunkNCols( 
                _ChunkID, 
                Grids_Environment.HandleOutOfMemoryErrorFalse );
        this.data = new int[ chunkNrows ][ chunkNcols ];
        int row;
        for ( row = 0; row < chunkNrows; row ++ ) {
            Arrays.fill( data[ row ], noDataValue );
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * TODO:
     * 1. docs
     * 2. A fast toArray() method in Grid2DSquareCellIntChunkMap could be coded 
     * then a constructor based on an int[] or int[][] might be faster?
     * @param grid2DSquareCellIntChunk
     * @param _ChunkID
     */
    protected Grid2DSquareCellIntChunkArray( 
            AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk, 
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        Grid2DSquareCellInt grid2DSquareCellInt = 
                grid2DSquareCellIntChunk.getGrid2DSquareCellInt();
        initGrid2DSquareCell( grid2DSquareCellInt );
        int chunkNrows = grid2DSquareCellInt.getChunkNRows( 
                _ChunkID, 
                Grids_Environment.HandleOutOfMemoryErrorFalse );
        int chunkNcols = grid2DSquareCellInt.getChunkNCols( 
                _ChunkID, 
                Grids_Environment.HandleOutOfMemoryErrorFalse );
        int noDataValue = grid2DSquareCellInt.getNoDataValue( 
                Grids_Environment.HandleOutOfMemoryErrorFalse );
        initData();
        int row;
        int col;
        boolean handleOutOfMemoryError = true;
        for ( row = 0; row < chunkNrows; row ++ ) {
            for ( col = 0; col < chunkNcols; col ++ ) {
                this.data[ row ][ col ] = grid2DSquareCellIntChunk.getCell( 
                        row, 
                        col, 
                        noDataValue, 
                        handleOutOfMemoryError );
//initCell( 
//        row, 
//        col, 
//        grid2DSquareCellIntChunk.getCell( row, col ) );
            }
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * Initialises the data associated with this.
     */
    protected @Override void initData() {
        Grid2DSquareCellInt grid2DSquareCellInt = this.getGrid2DSquareCellInt();
        int chunkNcols = grid2DSquareCellInt._ChunkNCols;
        int chunkNrows = grid2DSquareCellInt._ChunkNRows;
        this.data = new int[ chunkNrows ][ chunkNcols ];
    }
    
    /**
     * Returns this.data.
     * TODO:
     * Should the array be copied and the copy returned?
     * @return 
     */
    protected int[][] getData(){
        return this.data;
    }
    
    /**
     * Clears the data associated with this.
     */
    protected @Override void clearData() {
        this.data = null;
        System.gc();
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex; 
     * chunk cell row chunkCellColIndex.
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of 
     *   this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin 
     *   of this chunk
     * @param noDataValue the noDataValue of this.grid2DSquareCellInt
     * @return 
     */
    protected @Override int getCell( 
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int noDataValue ) {
        try {
            return this.data[ chunkCellRowIndex ][ chunkCellColIndex ];
        } catch ( Exception e0 ) {
            return noDataValue;
        }
    }
    
    /**
     * Initialises the value at position given by: chunk cell row 
     * chunkCellRowIndex; chunk cell column chunkCellColIndex. Utility method 
     * for constructor.
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of 
     *   this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin 
     *   of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     */
    protected @Override void initCell(
            int chunkCellRowIndex, 
            int chunkCellColIndex, 
            int valueToInitialise) {
        this.data[ chunkCellRowIndex ][ chunkCellColIndex ] = valueToInitialise;
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex; 
     * chunk cell row chunkCellColIndex and sets it to valueToSet
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of 
     *   this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin 
     *   of this chunk
     * @param valueToSet the value the cell is to be set to
     * @param noDataValue the noDataValue of this.grid2DSquareCellInt
     * @return 
     */
    protected @Override int setCell( 
            int chunkCellRowIndex, 
            int chunkCellColIndex, 
            int valueToSet, 
            int noDataValue ) {
        try {
            int oldValue = this.data[ chunkCellRowIndex ][ chunkCellColIndex ];
            this.data[ chunkCellRowIndex ][ chunkCellColIndex ] = valueToSet;
            if ( getIsSwapUpToDate() ) { 
                // Optimisation? Want a setCellFast method closer to initCell? 
                // What about an unmodifiable readOnly type chunk?
                if ( valueToSet != oldValue ) {
                    setIsSwapUpToDate( false );
                }
            }
            return oldValue;
        } catch ( Exception e0 ) {
            return noDataValue;
        }
    }
    
    /**
     * Returns a Grid2DSquareCellIntChunkArrayIterator for iterating over 
     * the cells in this.
     * @return 
     */
    protected @Override AbstractIterator iterator() {
        return new Grid2DSquareCellIntChunkArrayIterator( this );
    }
    
}