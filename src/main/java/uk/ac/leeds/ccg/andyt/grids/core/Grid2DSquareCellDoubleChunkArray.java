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
 * AbstractGrid2DSquareCellDoubleChunk extension that stores cell values in a 
 * double[][].
 */
public class Grid2DSquareCellDoubleChunkArray 
        extends AbstractGrid2DSquareCellDoubleChunk 
        implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * For storing values arranged in rows and columns.
     */
    private double[][] data;
    
    /**
     * Creates a new Grid2DSquareCellDoubleChunkArray.
     */
    public Grid2DSquareCellDoubleChunkArray() {
        this._ChunkID = new ChunkID();
        this.data = new double[ 1 ][ 1 ];
        //this._Grid2DSquareCell = new Grid2DSquareCellDouble(_AbstractGrid2DSquareCell_HashSet, handleOutOfMemoryError);
        this.isSwapUpToDate = false;
    }
 
    /**
     * Creates a new Grid2DSquareCellDoubleChunkArray for 
     * grid2DSquareCellDouble containing all no data values.
     * 
     * @param grid2DSquareCellDouble The Grid2DSquareCellDouble this is 
     *   to be a chunk of.
     * @param _ChunkID The ChunkID to be this._ChunkID.
     */
    protected Grid2DSquareCellDoubleChunkArray( 
            Grid2DSquareCellDouble grid2DSquareCellDouble,
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        initGrid2DSquareCell( grid2DSquareCellDouble );
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
        int chunkNrows = grid2DSquareCellDouble.getChunkNRows( 
                _ChunkID, 
                Grids_Environment.HandleOutOfMemoryErrorFalse );
        this.data = new double[ chunkNrows ][ grid2DSquareCellDouble.getChunkNCols(
                _ChunkID, 
                Grids_Environment.HandleOutOfMemoryErrorFalse ) ];
        int row;
        for ( row = 0; row < chunkNrows; row ++ ) {
            Arrays.fill( data[ row ], _NoDataValue );
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * Creates a new Grid2DSquareCellDoubleChunkArray from 
     * grid2DSquareCellDoubleChunk.
     * 
     * 
     * @param grid2DSquareCellDoubleChunk The AbstractGrid2DSquareCellDoubleChunk this 
     *   values are taken from.
     * @param _ChunkID The ChunkID to be this._ChunkID.
     * TODO:
     * A fast toArray() method in Grid2DSquareCellDoubleChunkMap could be coded 
     * then a constructor based on an double[] or double[][] might be faster?
     */
    protected Grid2DSquareCellDoubleChunkArray( 
            AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellDoubleChunk, 
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        Grid2DSquareCellDouble grid2DSquareCellDouble = 
                grid2DSquareCellDoubleChunk.getGrid2DSquareCellDouble();
        initGrid2DSquareCell( grid2DSquareCellDouble );
        int chunkNrows = grid2DSquareCellDouble.getChunkNRows( 
                _ChunkID, 
                Grids_Environment.HandleOutOfMemoryErrorFalse );
        int chunkNcols = grid2DSquareCellDouble.getChunkNCols( 
                _ChunkID, 
                Grids_Environment.HandleOutOfMemoryErrorFalse );
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
        initData();
        int row;
        int col;
        boolean handleOutOfMemoryError = true;
        for ( row = 0; row < chunkNrows; row ++ ) {
            for ( col = 0; col < chunkNcols; col ++ ) {
                this.data[ row ][ col ] = grid2DSquareCellDoubleChunk.getCell( 
                        row, 
                        col, 
                        _NoDataValue, 
                        handleOutOfMemoryError );
                //initCell( row, col, grid2DSquareCellDoubleChunk.getCell( row, col ) );
            }
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * Initialises the data associated with this.
     */
    protected @Override void initData() {
        Grid2DSquareCellDouble grid2DSquareCellDouble = 
                this.getGrid2DSquareCellDouble();
        int chunkNrows = grid2DSquareCellDouble._ChunkNRows;
        int chunkNcols = grid2DSquareCellDouble._ChunkNCols;
        this.data = new double[ chunkNrows ][ chunkNcols ];
    }
    
    /**
     * Returns this.data.
     * TODO:
     * Should the array be copied and the copy returned?
     * @return 
     */
    protected double[][] getData(){
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
     * 
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin of this chunk
     * @param _NoDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return 
     */
    protected @Override double getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            double _NoDataValue ) {
        try {
            return this.data[ chunkCellRowIndex ][ chunkCellColIndex ];
        } catch ( Exception e0 ) {
            return _NoDataValue;
        }
    }
    
    /**
     * Initialises the value at position given by: chunk cell row chunkCellRowIndex; 
     * chunk cell column chunkCellColIndex. Utility method for constructor.
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     */
    protected @Override void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex, 
            double valueToInitialise) {
        this.data[ chunkCellRowIndex ][ chunkCellColIndex ] = valueToInitialise;
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex; 
     * chunk cell row chunkCellColIndex and sets it to valueToSet
     * 
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin of this chunk
     * @param valueToSet the value the cell is to be set to
     * @param _NoDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return 
     */
    protected @Override double setCell( 
            int chunkCellRowIndex, 
            int chunkCellColIndex, 
            double valueToSet, 
            double _NoDataValue ) {
        try {
            double oldValue = this.data[ chunkCellRowIndex ][ chunkCellColIndex ];
            this.data[ chunkCellRowIndex ][ chunkCellColIndex ] = valueToSet;
            if ( getIsSwapUpToDate() ) { 
                // Optimisation? Want a setCellFast method closer to initCell? 
                // What about an unmodifiable readOnly type chunk?
                if ( valueToSet != oldValue ) {
                    setIsSwapUpToDate( false );
                }
            }
            return oldValue;
        } catch ( Exception e0 ) { // Should not happen! 
            return _NoDataValue;
        }
    }
    
    /**
     * Returns a Grid2DSquareCellDoubleChunkArrayIterator for iterating over 
     * the cells in this.
     * @return 
     */
    protected @Override AbstractIterator iterator() {
        return new Grid2DSquareCellDoubleChunkArrayIterator( this );
    }
    
}