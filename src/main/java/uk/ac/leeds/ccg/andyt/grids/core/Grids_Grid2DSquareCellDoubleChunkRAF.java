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
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_FileCreator;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;
/**
 * Grids_AbstractGrid2DSquareCellDoubleChunk extension that stores cell values in a 
 RandomAccessFile.
 */
public class Grids_Grid2DSquareCellDoubleChunkRAF 
        extends Grids_AbstractGrid2DSquareCellDoubleChunk 
        implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The File used to store the data.
     */
    private File file;
    
    /**
     * The RandomAccessFile for accessing file.
     */
    protected RandomAccessFile randomAccessFile;
    
    /**
     * Default constructor
     */
    public Grids_Grid2DSquareCellDoubleChunkRAF() {
        this._ChunkID = new Grids_2D_ID_int();
        this.file = Grids_FileCreator.createNewFile();
        //this._Grid2DSquareCell = new Grid2DSquareCellInt(_Grid2DSquareCells);
        this.isSwapUpToDate = true;
        try {
            this.randomAccessFile = new RandomAccessFile( 
                    this.file, 
                    "rw" );
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    /**
     * Creates a new Grid2DSquareCellDouble grid containing all no data values.
     * 
     * @param file
     * @param grid2DSquareCellDouble
     * @param _ChunkID
     */
    protected Grids_Grid2DSquareCellDoubleChunkRAF(
            File file, 
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble,
            Grids_2D_ID_int _ChunkID ) {
        this.file = file;
        this._ChunkID = _ChunkID;
        initGrid2DSquareCell( grid2DSquareCellDouble );
        try {
            this.randomAccessFile = new RandomAccessFile( 
                    file, 
                    "rw" );
            double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
            double _NoData = 0.0d;
            int chunkNrows = grid2DSquareCellDouble._ChunkNRows;
            int chunkNcols = grid2DSquareCellDouble._ChunkNCols;
            if ( _NoDataValue == _NoData ) { // There is a fast way to initialise the file!
                this.randomAccessFile.seek( ( ( chunkNrows * chunkNcols ) - 1L ) * 8L );
                this.randomAccessFile.writeDouble( _NoData );
            } else {
                // Calculate _NoDataValueAsByteArray
                this.randomAccessFile.seek( 0 );
                this.randomAccessFile.writeDouble( _NoDataValue );
                this.randomAccessFile.seek( 0 );
                byte[] _NoDataValueAsByteArray = new byte[ 8 ];
                for ( int i = 0; i < 8; i ++ ) {
                    _NoDataValueAsByteArray[ i ] = this.randomAccessFile.readByte();
                }
                this.randomAccessFile.close();
                BufferedOutputStream bis = new BufferedOutputStream( 
                        new FileOutputStream( file ) );
                for ( long i = 0; i < ( long ) chunkNrows * ( long ) chunkNcols; i ++ ) {
                    bis.write( _NoDataValueAsByteArray );
                }
                bis.flush();
                bis.close();
            }
        } catch ( java.io.IOException ioe0 ) {
            ioe0.printStackTrace();
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * TODO:
     * docs
     * Optimise for this type (see commented code for clues)
     * @param file
     * @param chunkID
     * @param grid2DSquareCellDoubleChunk
     */
    protected Grids_Grid2DSquareCellDoubleChunkRAF( 
            File file, 
            Grids_AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellDoubleChunk, 
            Grids_2D_ID_int chunkID ) {
        this._ChunkID = chunkID;
        this.file = file;
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = 
                grid2DSquareCellDoubleChunk.getGrid2DSquareCellDouble();
        initGrid2DSquareCell( grid2DSquareCellDouble );
        if ( grid2DSquareCellDoubleChunk instanceof Grids_Grid2DSquareCellDoubleChunkRAF ) {
            Grids_Grid2DSquareCellDoubleChunkRAF grid2DSquareCellDoubleChunkRAF = 
                    ( Grids_Grid2DSquareCellDoubleChunkRAF ) grid2DSquareCellDoubleChunk;
            initData();
            File grid2DSquareCellDoubleChunkFile = grid2DSquareCellDoubleChunkRAF.file;
            // Copy inputGridFile into gridFile
            // This is an optimisation for SunOS so the file is copied directly by the operating system.
            // NB. For other platforms similar optimisations can be inserted here.
            try {
                String os = System.getProperty( "os.name" );
                if ( os.equalsIgnoreCase( "SunOS" ) ) {
                    Runtime runtime = java.lang.Runtime.getRuntime();
                    runtime.exec(
                            "cp " + grid2DSquareCellDoubleChunkFile.toString() + 
                            " " + this.file.toString() );
                    // Failed optimsiation for Windows XP OS
                    //} else if ( os.equalsIgnoreCase( "Windows XP" ) ) {
                    //    Runtime runtime = java.lang.Runtime.getRuntime();
                    //    //runtime.exec( "copy d:/" + inputGridFile.toString() + " /B d:/" + gridFile.toString() + " /B " );
                } else {
                    //System.out.println( os );
                    BufferedInputStream bis = new BufferedInputStream( 
                            new FileInputStream( grid2DSquareCellDoubleChunkFile ) );
                    BufferedOutputStream bos = new BufferedOutputStream( 
                            new FileOutputStream( this.file ) );
                    for ( int i = 0; i < grid2DSquareCellDoubleChunkFile.length(); i ++ ) {
                        bos.write( bis.read() );
                    }
                    bos.flush();
                    bos.close();
                    bis.close();
                }
            } catch ( java.io.IOException ioe0 ) {
                ioe0.printStackTrace();
            }
        } else {
            boolean handleOutOfMemoryError = true;
            try {
                this.randomAccessFile = new RandomAccessFile( 
                        file, 
                        "rw" );
                int chunkNrows = grid2DSquareCellDouble._ChunkNRows;
                int chunkNcols = grid2DSquareCellDouble._ChunkNCols;
                double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
                double value;
                int row;
                int col;
                for ( row = 0; row < chunkNrows; row ++ ) {
                    for ( col = 0; col < chunkNcols; col ++ ) {
                        //this.randomAccessFile.writeDouble( grid2DSquareCellDoubleChunk.getCell( row, col ) );
                        value = grid2DSquareCellDoubleChunk.getCell( 
                                row, 
                                col, 
                                _NoDataValue, 
                                handleOutOfMemoryError );
                        initCell( 
                                row, 
                                col, 
                                value );
                    }
                }
            } catch ( java.io.IOException ioe0 ) {
                ioe0.printStackTrace();
            }
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * Initialises the data associated with this.
     */
    protected @Override void initData() {
        try {
            this.randomAccessFile = new RandomAccessFile( 
                    file,
                    "rw" );
        } catch ( IOException ioe0 ) {
            ioe0.printStackTrace();
        }
    }
    
    /**
     * For returning this.randomAccessFile.
     * TODO:
     * Return a copy of this.randomAccessFile and make public? 
     * Should the file be copied?
     * @return 
     */
    protected RandomAccessFile getData(){
        return this.randomAccessFile;
    }
    
    /**
     * Clears the data associated with this.
     */
    protected @Override void clearData() {
        this.randomAccessFile = null;
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
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
            long long0 = 
                    ( long ) chunkCellRowIndex * 
                    ( long ) grid2DSquareCellDouble._ChunkNCols;
            long position = 
                    ( long0 + ( long ) chunkCellColIndex ) 
                    * 8L;
            this.randomAccessFile.seek( position );
            return this.randomAccessFile.readDouble();
        } catch ( Exception e0 ) {
            return _NoDataValue;
        }
    }
    
    //    /**
    //     * Returns the values from startRow, startCol to endRow, endCol as a double[]
    //     * This is generally faster than using getCell() for each cell as usually only one seek is involved at the
    //     * start of each row (rather than seeking ).
    //     */
    //    public double[] getCells( int startchunkCellRowIndex, int startchunkCellColIndex, int endchunkCellRowIndex, int endchunkCellColIndex ) {
    //        double[] result = new double[ ( endchunkCellRowIndex - startchunkCellRowIndex + 1 ) * ( endchunkCellColIndex - startchunkCellColIndex + 1 ) ];
    //        double _NoDataValue = getNoDataValue();
    //        RandomAccessFile raf = getRandomAccessFile();
    //        int index = 0;
    //        if ( inGrid( startchunkCellRowIndex, startchunkCellColIndex ) && inGrid( endchunkCellRowIndex, endchunkCellColIndex ) ) {
    //            try {
    //                for ( int i = startchunkCellRowIndex; i <= endchunkCellRowIndex; i ++ ) {
    //                    raf.seek( ( ( i * getNcols() ) + startchunkCellColIndex ) * 8 );
    //                    for ( int j = startchunkCellColIndex; j <= endchunkCellColIndex; j ++ ) {
    //                        index ++;
    //                        result[ index ] = raf.readDouble();
    //                    }
    //                }
    //            } catch (java.io.IOException e ) {
    //                System.out.println( e.toString() );
    //                System.out.println( "in uk.ac.leeds.ccg.Grid2DSquareCellDoubleFile.getCells( startchunkCellRowIndex(" + startchunkCellRowIndex + "), startchunkCellColIndex(" + startchunkCellColIndex + "), endchunkCellRowIndex(" + endchunkCellRowIndex + "), endchunkCellColIndex (" + endchunkCellColIndex + ") )" );
    //                e.printStackTrace();
    //            }
    //        } else {
    //            Grid2DSquareCellDoubleJAIFactory jf = new Grid2DSquareCellDoubleJAIFactory();
    //            int gridStatisticsType = 1;
    //            AbstractGrid2DSquareCellDouble grid = jf.createGrid2DSquareCellDouble( this, startchunkCellRowIndex, startchunkCellColIndex, endchunkCellRowIndex, endchunkCellColIndex, _NoDataValue, gridStatisticsType );
    //            for ( int i = startchunkCellRowIndex; i <= endchunkCellRowIndex; i ++ ) {
    //                for ( int j = startchunkCellColIndex; j <= endchunkCellColIndex; j ++ ) {
    //                    index ++;
    //                    result[ index ] = grid.getCell( index );
    //                }
    //            }
    //            grid.clear();
    //        }
    //        return result;
    //    }
    
    
    /**
     * Initialises the value at position given by: chunk cell row chunkCellRowIndex; 
     * chunk cell column chunkCellColIndex. Utility method for constructor. It is 
     * assumed that the file head is in the correct position 
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     */
    protected @Override void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex, 
            double valueToInitialise) {
        try {
            //this.randomAccessFile.seek( )
            this.randomAccessFile.writeDouble( valueToInitialise );
        } catch ( Exception e0 ) {
            e0.printStackTrace();
        }
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex; 
     * chunk cell column chunkCellColIndex and sets it to valueToSet. The swapped version
     * remains up to date (if it is) since the data resides in filespace.
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
        //if ( inGrid( chunkCellRowIndex, chunkCellColIndex ) ) {
        try {
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
            long long0 = 
                    ( long ) chunkCellRowIndex * 
                    ( long ) grid2DSquareCellDouble._ChunkNCols;
            long position =
                    ( long0 + ( long ) chunkCellColIndex )
                    * 8L;
            this.randomAccessFile.seek( position );
            double oldValue = this.randomAccessFile.readDouble();
            if ( valueToSet != oldValue ) {
                this.randomAccessFile.seek( position );
                this.randomAccessFile.writeDouble( valueToSet );
                return oldValue;
            }
            return oldValue;
        } catch ( Exception e0 ) {
            return _NoDataValue;
        }
        //}
    }
    
    /**
     * Returns a Grids_Grid2DSquareCellDoubleChunkRAFIterator for iterating over 
 the cells in this.
     * @return 
     */
    protected @Override Grids_AbstractIterator iterator() {
        return new Grids_Grid2DSquareCellDoubleChunkRAFIterator( this );
    }
    
}