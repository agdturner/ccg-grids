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
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.NotActiveException;
import java.io.Serializable;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.media.jai.remote.SerializableRenderedImage;
import javax.media.jai.RasterFactory;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_AbstractGrid2DSquareCell.ChunkID;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;
/**
 * Grids_AbstractGrid2DSquareCellDoubleChunk extension that stores cell values in a
 javax.media.jai.TiledImage.
 */
public class Grids_Grid2DSquareCellDoubleChunkJAI
        extends Grids_AbstractGrid2DSquareCellDoubleChunk
        implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Image for storing the grid data
     */
    private transient TiledImage data;
    
    /**
     * Creates a new Grid2DSquareCellDoubleChunkJAI.
     */
    public Grids_Grid2DSquareCellDoubleChunkJAI() {
        this._ChunkID = new ChunkID();
        int numBands = 1;
        int dataType = DataBuffer.TYPE_DOUBLE;
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
        //this._Grid2DSquareCell = new Grid2DSquareCellDouble(_AbstractGrid2DSquareCell_HashSet, handleOutOfMemoryError);
        this.isSwapUpToDate = false;
    }
    
    /**
     * Creates a new Grid2DSquareCellDoubleChunkJAI from a 
     * SerializableRenderedImage.
     * 
     * @param serializableRenderedImage The SerializableRenderedImage from which 
     *   to create this.
     * @param _ChunkID The ChunkID of this.
     */
    protected Grids_Grid2DSquareCellDoubleChunkJAI(
            SerializableRenderedImage serializableRenderedImage,
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        int numBands = 1;
        int dataType = DataBuffer.TYPE_DOUBLE;
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
     * Creates a new Grid2DSquareCellDoubleChunkJAI for 
     * Grid2DSquareCellDouble with _ChunkID ChunkID
     * 
     * @param grid2DSquareCellDouble The Grid2DSquareCellDouble for which this is 
     *   created.
     * @param _ChunkID The ChunkID of this.
     */
    protected Grids_Grid2DSquareCellDoubleChunkJAI(
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble,
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        initGrid2DSquareCell( grid2DSquareCellDouble );
        int chunkNrows = grid2DSquareCellDouble.getChunkNRows(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        int chunkNcols = grid2DSquareCellDouble.getChunkNCols(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
        initData();
        for ( int row = 0; row < chunkNrows; row ++ ) {
            for ( int col = 0; col < chunkNcols; col ++ ) {
                initCell(
                        row,
                        col,
                        _NoDataValue );
            }
        }
        this.isSwapUpToDate = false;
    }
    
    /**
     * Creates a new Grid2DSquareCellDoubleChunkJAI from a  
 Grids_AbstractGrid2DSquareCellDoubleChunk with _ChunkID ChunkID
     * 
     * 
     * 
     * @param grid2DSquareCellDoubleChunk The 
   Grids_AbstractGrid2DSquareCellDoubleChunk from which this is 
   created.
     * @param _ChunkID The ChunkID of this.
     */
    protected Grids_Grid2DSquareCellDoubleChunkJAI(
            Grids_AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellDoubleChunk,
            ChunkID _ChunkID ) {
        this._ChunkID = _ChunkID;
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble =
                grid2DSquareCellDoubleChunk.getGrid2DSquareCellDouble();
        initGrid2DSquareCell( grid2DSquareCellDouble );
        int chunkNrows = grid2DSquareCellDouble.getChunkNRows(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        int chunkNcols = grid2DSquareCellDouble.getChunkNCols(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        double _NoDataValue = grid2DSquareCellDouble._NoDataValue;
        initData( chunkNrows, chunkNcols );
        boolean handleOutOfMemoryError = true;
        double value = _NoDataValue;
        for ( int row = 0; row < chunkNrows; row ++ ) {
            for ( int col = 0; col < chunkNcols; col ++ ) {
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
        this.isSwapUpToDate = false;
    }
    
    /**
     * Initialises the data associated with this.
     */
    protected @Override void initData() {
        Grids_Grid2DSquareCellDouble grid2DSquareCellDouble = getGrid2DSquareCellDouble();
        int chunkNcols = grid2DSquareCellDouble.getChunkNCols(
                _ChunkID,
                _Grid2DSquareCell.env.HandleOutOfMemoryErrorFalse );
        int chunkNrows = grid2DSquareCellDouble.getChunkNRows(
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
            int nrows  ) {
        int numBands = 1;
        int dataType = DataBuffer.TYPE_DOUBLE;
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
        //TiledImage tiledImage = new TiledImage(
        //        minX,
        //        minY,
        //        ncols,
        //        nrows,
        //        tileGridXOffset,
        //        tileGridYOffset,
        //        sampleModel0,
        //        colorModel0 );
        //this.data = new SerializableRenderedImage( tiledImage );
    }
    
    /**
     * For returning data
     * TODO:
     * Return a copy of this.data and make public?
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
    
    /**
     * Serialization method.
     */
    private void writeObject(
            ObjectOutputStream oos ) {
        try {
            oos.defaultWriteObject();
            //oos.writeObject( new SerializableRenderedImage( this.data ) );
            oos.writeObject( new SerializableRenderedImage(
                    this.data,
                    true ) );
        } catch ( IOException e0 ) {
            e0.printStackTrace();
        }
    }
    
    /**
     * Deserialization method.
     */
    private void readObject(
            ObjectInputStream ois ) {
        try {
            ois.defaultReadObject();
            SerializableRenderedImage serializableRenderedImage =
                    ( SerializableRenderedImage ) ois.readObject();
            this.data = new TiledImage(
                    serializableRenderedImage,
                    true );
        } catch ( NotActiveException nae0 ) {
            nae0.printStackTrace();
        } catch ( IOException ioe0 ) {
            ioe0.printStackTrace();
        } catch ( ClassNotFoundException cnfe0 ) {
            cnfe0.printStackTrace();
        } catch ( ClassCastException cce0 ) {
            cce0.printStackTrace();
        }
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
            return this.data.getSampleDouble(
                    chunkCellColIndex,
                    chunkCellRowIndex,
                    0 );
        } catch ( Exception e0 ) {
            return _NoDataValue;
        }
    }
    
    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell column chunkCellColIndex and sets it to valueToSet
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
            double oldValue = getCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    _NoDataValue );
            //this.data.setSampleDouble( chunkCellColIndex, chunkCellRowIndex, 0, valueToSet );
            this.data.setSample(
                    chunkCellColIndex,
                    chunkCellRowIndex,
                    0,
                    valueToSet );
            if ( getIsSwapUpToDate() ) { // Optimisation? Want a setCellFast method closer to initCell? What about an unmodifiable readOnly type chunk?
                if ( valueToSet != oldValue ) {
                    setIsSwapUpToDate( false );
                }
            }
            return oldValue;
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
            double valueToInitialise ) {
        //this.data.getData().setSampleDouble( chunkCellColIndex, chunkCellRowIndex, 0, valueToInitialise );
        this.data.setSample(
                chunkCellColIndex,
                chunkCellRowIndex,
                0,
                valueToInitialise );
    }
    
    /**
     * Returns a Grids_Grid2DSquareCellDoubleChunkJAIIterator for iterating over
 the cells in this.
     * @return 
     */
    protected @Override Grids_AbstractIterator iterator() {
        return new Grids_Grid2DSquareCellDoubleChunkJAIIterator( this );
    }
    
}