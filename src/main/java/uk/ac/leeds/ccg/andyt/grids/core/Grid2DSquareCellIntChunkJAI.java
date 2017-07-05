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
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.media.jai.remote.SerializableRenderedImage;
import javax.media.jai.RasterFactory;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCell.ChunkID;
import uk.ac.leeds.ccg.andyt.grids.utilities.AbstractIterator;
/**
 * AbstractGrid2DSquareCellIntChunk extension that stores cell values in a
 * javax.media.jai.TiledImage.
 */
public class Grid2DSquareCellIntChunkJAI
        extends AbstractGrid2DSquareCellIntChunk
        implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Image for storing the grid data
     */
    private transient TiledImage data;
    
    /**
     * Creates a new Grid2DSquareCellIntChunkJAI.
     */
    public Grid2DSquareCellIntChunkJAI() {
        this._ChunkID = new ChunkID();
        int numBands = 1;
        int dataType = DataBuffer.TYPE_INT;
        int minX = 0;
        int minY = 0;
        int ncols = 1;
        int nrows = 1;
        int tileGridXOffset = 0;
        int tileGridYOffset = 0;
        SampleModel sampleModel0 = RasterFactory.createBandedSampleModel(
                dataType,
                ncols,
                nrows,
                numBands );
        ColorModel colorModel0 = PlanarImage.createColorModel( sampleModel0 );
        this.data = new TiledImage(
                minX,
                minY,
                ncols,
                nrows,
                tileGridXOffset,
                tileGridYOffset,
                sampleModel0,
                colorModel0 );
        //this._Grid2DSquareCell = new Grid2DSquareCellDouble(_AbstractGrid2DSquareCell_HashSet, _HandleOutOfMemoryError);
        this.isSwapUpToDate = false;
    }
    
    /**
     * Creates a new Grid2DSquareCellIntChunkJAI from a
     * SerializableRenderedImage.
     * 
     * @param serializableRenderedImage The SerializableRenderedImage from which
     *   to create this.
     * @param _ChunkID The ChunkID of this.
     */
    protected Grid2DSquareCellIntChunkJAI(
            SerializableRenderedImage serializableRenderedImage,
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        int numBands = 1;
        //int dataType = DataBuffer.TYPE_DOUBLE;
        int dataType = DataBuffer.TYPE_INT;
        int minX = 0;
        int minY = 0;
        int tileGridXOffset = 0;
        int tileGridYOffset = 0;
        SampleModel sampleModel0 = serializableRenderedImage.getSampleModel();
        ColorModel colorModel0 = serializableRenderedImage.getColorModel();
        int ncols = serializableRenderedImage.getWidth();
        int nrows = serializableRenderedImage.getHeight();
        this.data = new TiledImage(
                minX,
                minY,
                ncols,
                nrows,
                tileGridXOffset,
                tileGridYOffset,
                sampleModel0,
                colorModel0 );
        this.isSwapUpToDate = false;
    }
    
    /**
     * Creates a new Grid2DSquareCellIntChunkJAI for
     * Grid2DSquareCellDouble with _ChunkID ChunkID
     * 
     * @param grid2DSquareCellInt The Grid2DSquareCellInt for which this is
     *   created.
     * @param _ChunkID The ChunkID of this.
     */
    protected Grid2DSquareCellIntChunkJAI(
            Grid2DSquareCellInt grid2DSquareCellInt,
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        initGrid2DSquareCell( grid2DSquareCellInt );
        int chunkNrows = grid2DSquareCellInt.getChunkNRows(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        int chunkNcols = grid2DSquareCellInt.getChunkNCols(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        int noDataValue = grid2DSquareCellInt.getNoDataValue(
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        initData();
        for ( int row = 0; row < chunkNrows; row ++ ) {
            for ( int col = 0; col < chunkNcols; col ++ ) {
                initCell( row, col, noDataValue );
            }
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * Creates a new Grid2DSquareCellIntChunkJAI from a
     * AbstractGrid2DSquareCellIntChunk with _ChunkID ChunkID
     * 
     * 
     * @param grid2DSquareCellIntChunk The
     *   AbstractGrid2DSquareCellIntChunk from which this is
     *   created.
     * @param _ChunkID The ChunkID of this.
     */
    protected Grid2DSquareCellIntChunkJAI(
            AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk,
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        Grid2DSquareCellInt grid2DSquareCellInt =
                grid2DSquareCellIntChunk.getGrid2DSquareCellInt();
        initGrid2DSquareCell( grid2DSquareCellInt );
        int chunkNrows = grid2DSquareCellInt.getChunkNRows(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        int chunkNcols = grid2DSquareCellInt.getChunkNCols(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        int noDataValue = grid2DSquareCellInt.getNoDataValue(
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        initData( chunkNrows, chunkNcols );
        int value;
        boolean handleOutOfMemoryError = true;
        for ( int row = 0; row < chunkNrows; row ++ ) {
            for ( int col = 0; col < chunkNcols; col ++ ) {
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
        this.isSwapUpToDate = false;
    }
    
    /**
     * Initialises the data associated with this.
     */
    protected @Override void initData() {
        Grid2DSquareCellInt grid2DSquareCellInt = getGrid2DSquareCellInt();
        int chunkNrows = grid2DSquareCellInt.getChunkNRows(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        int chunkNcols = grid2DSquareCellInt.getChunkNCols(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        initData(
                chunkNrows,
                chunkNcols );
    }
    
    /**
     * Initialises the data associated with this.
     * @param ncols
     * @param nrows
     */
    protected void initData(
            int ncols,
            int nrows ) {
        int numBands = 1;
        //int dataType = DataBuffer.TYPE_DOUBLE;
        int dataType = DataBuffer.TYPE_INT;
        int minX = 0;
        int minY = 0;
        int tileGridXOffset = 0;
        int tileGridYOffset = 0;
        SampleModel sampleModel0 = RasterFactory.createBandedSampleModel(
                dataType,
                ncols,
                nrows,
                numBands );
        ColorModel colorModel0 = PlanarImage.createColorModel( sampleModel0 );
        this.data = new TiledImage(
                minX,
                minY,
                ncols,
                nrows,
                tileGridXOffset,
                tileGridYOffset,
                sampleModel0,
                colorModel0 );
        //this.data = new SerializableRenderedImage( new TiledImage (
        //        minX,
        //        minY,
        //        ncols,
        //        nrows,
        //        tileGridXOffset,
        //        tileGridYOffset,
        //        sampleModel0,
        //        colorModel0 ) );
    }
    
    /**
     * For returning data
     * @return 
     */
    protected TiledImage getData() {
        return this.data;
    }
    
    /**
     * Clears the data associated with this.
     */
    protected @Override void clearData() {
        this.data = null;
        System.gc();
    }
    
    // Serialization method.
    private void writeObject(
            ObjectOutputStream oos ) {
        try {
            oos.defaultWriteObject();
            //oos.writeObject( new SerializableRenderedImage( this.data ) );
            oos.writeObject( new SerializableRenderedImage( this.data, true ) );
        } catch ( IOException e0 ) {
            e0.printStackTrace();
        }
    }
    
    // Deserialization method.
    private void readObject(
            ObjectInputStream ois ) {
        try {
            ois.defaultReadObject();
            SerializableRenderedImage serializableRenderedImage =
                    ( SerializableRenderedImage ) ois.readObject();
            this.data = new TiledImage( serializableRenderedImage, true );
        } catch ( Exception e0 ) {
            e0.printStackTrace();
        }
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
            return this.data.getSample(
                    chunkCellColIndex,
                    chunkCellRowIndex,
                    0 );
        } catch ( Exception e0 ) {
            return noDataValue;
        }
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell column chunkCellColIndex and sets it to valueToSet
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
            int oldValue = getCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    noDataValue );
            //this.data.setSampleDouble(
            //        chunkCellColIndex,
            //        chunkCellRowIndex,
            //        0,
            //        valueToSet );
            this.data.setSample(
                    chunkCellColIndex,
                    chunkCellRowIndex,
                    0,
                    valueToSet );
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
        //this.data.getData().setSampleDouble(
        //        chunkCellColIndex,
        //        chunkCellRowIndex,
        //        0,
        //        valueToInitialise );
        this.data.setSample(
                chunkCellColIndex,
                chunkCellRowIndex,
                0,
                valueToInitialise );
    }
    
    /**
     * Returns a Grid2DSquareCellIntChunkJAIIterator for iterating over
     * the cells in this.
     * @return 
     */
    protected @Override AbstractIterator iterator() {
        return new Grid2DSquareCellIntChunkJAIIterator( this );
    }
    
}