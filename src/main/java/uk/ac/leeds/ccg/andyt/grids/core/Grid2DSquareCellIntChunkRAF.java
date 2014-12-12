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
import uk.ac.leeds.ccg.andyt.grids.core.AbstractGrid2DSquareCell.ChunkID;
import uk.ac.leeds.ccg.andyt.grids.utilities.FileCreator;
import uk.ac.leeds.ccg.andyt.grids.utilities.AbstractIterator;
/**
 * AbstractGrid2DSquareCellIntChunk extension that stores cell values in a
 * RandomAccessFile.
 */
public class Grid2DSquareCellIntChunkRAF
        extends AbstractGrid2DSquareCellIntChunk
        implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * The File used to store the data.
     */
    private File file;
    
    /**
     * The RandomAccessFile for accessing file.
     */
    public RandomAccessFile randomAccessFile;
    
    /**
     * Default constructor
     */
    public Grid2DSquareCellIntChunkRAF() {
        this._ChunkID = new ChunkID();
        this.file = FileCreator.createNewFile();
        //this._Grid2DSquareCell = new Grid2DSquareCellInt(_AbstractGrid2DSquareCell_HashSet);
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
     * Creates a new Grid2DSquareCellInt grid containing all no data values.
     * 
     * @param file
     * @param grid2DSquareCellInt
     * @param _ChunkID
     */
    protected Grid2DSquareCellIntChunkRAF(
            File file,
            Grid2DSquareCellInt grid2DSquareCellInt,
            ChunkID _ChunkID ) {
        this.file = file;
        this._ChunkID = _ChunkID;
        initGrid2DSquareCell( grid2DSquareCellInt );
        try {
            this.randomAccessFile = new RandomAccessFile( file, "rw" );
            int noDataValue = grid2DSquareCellInt.getNoDataValue(
                    _Grid2DSquareCell._Grids_Environment.HandleOutOfMemoryErrorFalse );
            int chunkNrows = grid2DSquareCellInt._ChunkNRows;
            int chunkNcols = grid2DSquareCellInt._ChunkNCols;
            // Calculate noDataValueAsByteArray
            this.randomAccessFile.seek( 0 );
            this.randomAccessFile.writeInt( noDataValue );
            this.randomAccessFile.seek( 0 );
            byte[] noDataValueAsByteArray = new byte[ 4 ];
            for ( int i = 0; i < 4; i ++ ) {
                noDataValueAsByteArray[ i ] =
                        this.randomAccessFile.readByte();
            }
            this.randomAccessFile.close();
            BufferedOutputStream bis = new BufferedOutputStream(
                    new FileOutputStream( file ) );
            // Populate file
            for ( long i = 0; i < ( long ) chunkNrows * ( long ) chunkNcols; i ++ ) {
                bis.write( noDataValueAsByteArray );
            }
            bis.flush();
            bis.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * TODO:
     * docs
     * Optimise for this type (see commented code for clues)
     * @param file
     * @param _ChunkID
     * @param grid2DSquareCellIntChunk
     */
    protected Grid2DSquareCellIntChunkRAF(
            File file,
            AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk,
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        this.file = file;
        Grid2DSquareCellInt grid2DSquareCellInt =
                grid2DSquareCellIntChunk.getGrid2DSquareCellInt();
        initGrid2DSquareCell( grid2DSquareCellInt );
        if ( grid2DSquareCellIntChunk instanceof Grid2DSquareCellIntChunkRAF ) {
            Grid2DSquareCellIntChunkRAF grid2DSquareCellIntChunkRAF =
                    ( Grid2DSquareCellIntChunkRAF ) grid2DSquareCellIntChunk;
            initData();
            File grid2DSquareCellIntChunkFile = grid2DSquareCellIntChunkRAF.file;
            // Copy inputGridFile into gridFile
            // This is an optimisation for SunOS so the file is copied directly by the operating system.
            // NB. For other platforms similar optimisations can be inserted here.
            try {
                String os = System.getProperty( "os.name" );
                if ( os.equalsIgnoreCase( "SunOS" ) ) {
                    Runtime runtime = java.lang.Runtime.getRuntime();
                    runtime.exec(
                            "cp " + grid2DSquareCellIntChunkFile.toString() +
                            " " + this.file.toString() );
                    // Failed optimsiation for Windows XP OS
                    //} else if ( os.equalsIgnoreCase( "Windows XP" ) ) {
                    //    Runtime runtime = java.lang.Runtime.getRuntime();
                    //    //runtime.exec( "copy d:/" + inputGridFile.toString() + " /B d:/" + gridFile.toString() + " /B " );
                } else {
                    //System.out.println( os );
                    BufferedInputStream bis = new BufferedInputStream(
                            new FileInputStream( grid2DSquareCellIntChunkFile ) );
                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream( this.file ) );
                    for ( int i = 0; i < grid2DSquareCellIntChunkFile.length(); i ++ ) {
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
            try {
                this.randomAccessFile = new RandomAccessFile(
                        file,
                        "rw" );
                int chunkNrows = grid2DSquareCellInt._ChunkNRows;
                int chunkNcols = grid2DSquareCellInt._ChunkNCols;
                int noDataValue = grid2DSquareCellInt.getNoDataValue(
                        _Grid2DSquareCell._Grids_Environment.HandleOutOfMemoryErrorFalse );
                int row;
                int col;
                boolean handleOutOfMemoryError = true;
                int value = Integer.MIN_VALUE;
                for ( row = 0; row < chunkNrows; row ++ ) {
                    for ( col = 0; col < chunkNcols; col ++ ) {
                        value = grid2DSquareCellIntChunk.getCell(
                                row,
                                col,
                                noDataValue, 
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
     * Returns a description of this.
     * @return 
     */
    @Override
    protected String getDescription() {
        return  "Grid2DSquareCellIntChunkRAF( \n" +
                "ChunkID( " + this._ChunkID.toString() + " ) )";
    }
    
    /**
     * Initialises the data associated with this.
     */
    @Override
    public void initData() {
        try {
            this.randomAccessFile = new RandomAccessFile(
                    file,
                    "rw" );
        } catch ( IOException ioe0 ) {
            ioe0.printStackTrace();
        }
    }
    
    /**
     * TODO: docs
     * @return 
     */
    public RandomAccessFile getData(){
        return this.randomAccessFile;
    }
    
    /**
     * Clears the data associated with this.
     */
    @Override
    public void clearData() {
        this.randomAccessFile = null;
        System.gc();
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex.
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin of this chunk
     * @param noDataValue the noDataValue of this.grid2DSquareCellInt
     * @return 
     */
    @Override
    public int getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int noDataValue ) {
        try {
            this.randomAccessFile.seek(
                    ( ( ( long ) chunkCellRowIndex * ( long ) this._Grid2DSquareCell._ChunkNCols )
                    + ( long ) chunkCellColIndex ) * 4L );
            return this.randomAccessFile.readInt();
        } catch ( Exception e0 ) {
            return noDataValue;
        }
    }
    
    //    /**
    //     * Returns the values from startRow, startCol to endRow, endCol as a double[]
    //     * This is generally faster than using getCell() for each cell as usually only one seek is involved at the
    //     * start of each row (rather than seeking ).
    //     */
    //    public double[] getCells( int startchunkCellRowIndex, int startchunkCellColIndex, int endchunkCellRowIndex, int endchunkCellColIndex ) {
    //        double[] result = new double[ ( endchunkCellRowIndex - startchunkCellRowIndex + 1 ) * ( endchunkCellColIndex - startchunkCellColIndex + 1 ) ];
    //        double noDataValue = getNoDataValue();
    //        RandomAccessFile raf = getRandomAccessFile();
    //        int index = 0;
    //        if ( inGrid( startchunkCellRowIndex, startchunkCellColIndex ) && inGrid( endchunkCellRowIndex, endchunkCellColIndex ) ) {
    //            try {
    //                for ( int i = startchunkCellRowIndex; i <= endchunkCellRowIndex; i ++ ) {
    //                    raf.seek( ( ( i * getNcols() ) + startchunkCellColIndex ) * 8 );
    //                    for ( int j = startchunkCellColIndex; j <= endchunkCellColIndex; j ++ ) {
    //                        index ++;
    //                        result[ index ] = raf.readInt();
    //                    }
    //                }
    //            } catch (java.io.IOException e ) {
    //                System.out.println( e.toString() );
    //                System.out.println( "in uk.ac.leeds.ccg.Grid2DSquareCellIntFile.getCells( startchunkCellRowIndex(" + startchunkCellRowIndex + "), startchunkCellColIndex(" + startchunkCellColIndex + "), endchunkCellRowIndex(" + endchunkCellRowIndex + "), endchunkCellColIndex (" + endchunkCellColIndex + ") )" );
    //                e.printStackTrace();
    //            }
    //        } else {
    //            Grid2DSquareCellIntJAIFactory jf = new Grid2DSquareCellIntJAIFactory();
    //            int gridStatisticsType = 1;
    //            AbstractGrid2DSquareCellInt grid = jf.createGrid2DSquareCellInt( this, startchunkCellRowIndex, startchunkCellColIndex, endchunkCellRowIndex, endchunkCellColIndex, noDataValue, gridStatisticsType );
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
    @Override
    protected void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int valueToInitialise) {
        try {
            //this.randomAccessFile.seek( )
            this.randomAccessFile.writeInt( valueToInitialise );
        } catch ( Exception e0 ) {
            e0.printStackTrace();
        }
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell column chunkCellColIndex and sets it to valueToSet
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin of this chunk
     * @param valueToSet the value the cell is to be set to
     * @param noDataValue the noDataValue of this.grid2DSquareCellInt
     * @return 
     */
    @Override
    public int setCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            int valueToSet,
            int noDataValue ) {
        //if ( inGrid( chunkCellRowIndex, chunkCellColIndex ) ) {
        try {
            long pos = ( ( ( long ) chunkCellRowIndex * ( long ) this.getGrid2DSquareCellInt()._ChunkNCols ) + ( long ) chunkCellColIndex ) * 8L;
            this.randomAccessFile.seek( pos );
            int oldValue = this.randomAccessFile.readInt();
            if ( valueToSet != oldValue ) {
                this.randomAccessFile.seek( pos );
                this.randomAccessFile.writeInt( valueToSet );
                return oldValue;
            }
            return oldValue;
        } catch ( Exception e0 ) {
            return noDataValue;
        }
        //}
    }
    
    /**
     * Returns a Grid2DSquareCellIntChunkRAFIterator for iterating over
     * the cells in this.
     * @return 
     */
    @Override
    public AbstractIterator iterator() {
        return new Grid2DSquareCellIntChunkRAFIterator( this );
    }
    
}