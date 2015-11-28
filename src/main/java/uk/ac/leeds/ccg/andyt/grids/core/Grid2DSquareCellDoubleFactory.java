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
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.utilities.FileCreator;

/**
 * A factory for constructing Grid2DSquareCellDouble instances.
 */
public class Grid2DSquareCellDoubleFactory
        extends AbstractGrid2DSquareCellFactory {

    /**
     * The Grid2DSquareCellChunkAbstractFactory for creating chunks.
     */
    protected AbstractGrid2DSquareCellDoubleChunkFactory _Grid2DSquareCellDoubleChunkFactory;
    /**
     * The _NoDataValue for creating chunks.
     */
    protected double _NoDataValue;

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     * Default:
     * _NoDataValue to Double.NEGATIVE_INFINITY
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     */
    public Grid2DSquareCellDoubleFactory(
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        this(Double.NEGATIVE_INFINITY,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     * 
     * 
     * @param _NoDataValue The _NoDataValue initially set for construction.
     * Defaults:
     * _Directory to a new one in System.getProperties( "java.io.tmpdir" );
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     */
    public Grid2DSquareCellDoubleFactory(
            double _NoDataValue,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        this(FileCreator.createTempFile(),
                _NoDataValue,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     * @param _Directory A "workspace _Directory" for storing temporary files and
     *   swapping Grid2DSquareCellDouble data to.
     * Defaults:
     * _ChunkNRows to 64
     * _ChunkNColss to 64
     * AbstractGrid2DSquareCellDoubleChunkFactory to _Grid2DSquareCellDoubleChunkArray
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     */
    public Grid2DSquareCellDoubleFactory(
            File _Directory,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        this(_Directory,
                Double.NEGATIVE_INFINITY,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     * @param _Directory A "workspace _Directory" for storing temporary files and
     *   swapping Grid2DSquareCellDouble data to.
     * @param _NoDataValue The _NoDataValue initially set for construction.
     * Default:
     * _ChunkNRows to 64;
     * _ChunkNColss to 64;
     * AbstractGrid2DSquareCellDoubleChunkFactory to _Grid2DSquareCellDoubleChunkArray.
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     */
    public Grid2DSquareCellDoubleFactory(
            File _Directory,
            double _NoDataValue,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        this(_Directory,
                64,
                64,
                new Grid2DSquareCellDoubleChunkArrayFactory(),
                _NoDataValue,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     * @param _Directory A "workspace _Directory" for storing temporary files and
     *   caching Grid2DSquareCellDoubleAbstract data.
     * @param _ChunkNRows The number of rows chunks have by default.
     * @param _Grid2DSquareCellDoubleChunkFactory TheGrid2DSquareCellDoubleChunkFactoryy
     *   for creating Grid2DSquareCellDoubleChunks
     * Default:
     * _NoDataValue to Double.NEGATIVE_INFINITY.
     * @param _ChunkNCols The number of columns chunks have by default.
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     */
    public Grid2DSquareCellDoubleFactory(
            File _Directory,
            int _ChunkNRows,
            int _ChunkNCols,
            AbstractGrid2DSquareCellDoubleChunkFactory _Grid2DSquareCellDoubleChunkFactory,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        this(_Directory,
                _ChunkNRows,
                _ChunkNCols,
                _Grid2DSquareCellDoubleChunkFactory,
                Double.NEGATIVE_INFINITY,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     * 
     * @param _Directory A "workspace _Directory" for storing temporary files and
     *   caching Grid2DSquareCellDoubleAbstract data.
     * @param _ChunkNRows The number of rows chunks have by default.
     * @param _Grid2DSquareCellDoubleChunkFactory TheGrid2DSquareCellDoubleChunkFactoryy
     *   for creating Grid2DSquareCellDoubleChunks
     * @param _NoDataValue The _NoDataValue initially set for construction.
     * @param _ChunkNCols The number of columns chunks have by default.
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     */
    public Grid2DSquareCellDoubleFactory(
            File _Directory,
            int _ChunkNRows,
            int _ChunkNCols,
            AbstractGrid2DSquareCellDoubleChunkFactory _Grid2DSquareCellDoubleChunkFactory,
            double _NoDataValue,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        _Grids_Environment = a_Grids_Environment;
        boolean isGrid2DSquareCellDoubleChunk64CellMapFactory =
                (_Grid2DSquareCellDoubleChunkFactory.getClass()
                == Grid2DSquareCellDoubleChunk64CellMapFactory.class);
        if (isGrid2DSquareCellDoubleChunk64CellMapFactory) {
            if (_ChunkNRows * _ChunkNCols > 64) {
                _ChunkNRows = 8;
                _ChunkNCols = 8;
            }
        }
        this._Directory = _Directory;
        this._ChunkNRows = _ChunkNRows;
        this._ChunkNCols = _ChunkNCols;
        this._Dimensions = new BigDecimal[5];
        this._Dimensions[ 0] = new BigDecimal(1L);
        this._Dimensions[ 1] = new BigDecimal(0L);
        this._Dimensions[ 2] = new BigDecimal(0L);
        this._Dimensions[ 3] = new BigDecimal(_ChunkNCols);
        this._Dimensions[ 4] = new BigDecimal(_ChunkNRows);
        this._Grid2DSquareCellDoubleChunkFactory = _Grid2DSquareCellDoubleChunkFactory;
        this.set_GridStatistics(new GridStatistics1());
        this._HandleOutOfMemoryError = handleOutOfMemoryError;
        this._NoDataValue = _NoDataValue;
    }

    /**
     * Returns a reference to this._Grid2DSquareCellDoubleChunkFactory.
     * @return 
     */
    public AbstractGrid2DSquareCellDoubleChunkFactory getGrid2DSquareCellDoubleChunkFactory() {
        return this._Grid2DSquareCellDoubleChunkFactory;
    }

    /**
     * Sets this._Grid2DSquareCellDoubleChunkFactory to _Grid2DSquareCellDoubleChunkFactory.
     * @param _Grid2DSquareCellDoubleChunkFactory
     */
    public void setGrid2DSquareCellDoubleChunkFactory(
            AbstractGrid2DSquareCellDoubleChunkFactory _Grid2DSquareCellDoubleChunkFactory) {
        this._Grid2DSquareCellDoubleChunkFactory = _Grid2DSquareCellDoubleChunkFactory;
    }

    /**
     * Returns this._NoDataValue.
     * @return 
     */
    public double get_NoDataValue() {
        return this._NoDataValue;
    }

    /**
     * Sets this._NoDataValue.
     * @param noDataValue
     */
    public void set_NoDataValue(
            double noDataValue) {
        this._NoDataValue = noDataValue;
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * Returns a new Grid2DSquareCellDouble grid with all values as
     * NoDataValues.
     * 
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellDouble information.
     * @param _NRows The Grid2DSquareCellDouble _NRows.
     * @param _NCols The Grid2DSquareCellDouble _NCols.
     * @param _Dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public Grid2DSquareCellDouble create(
            File _Directory,
            long _NRows,
            long _NCols,
            BigDecimal[] _Dimensions,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        return create(
                get_GridStatistics(),
                _Directory,
                this._Grid2DSquareCellDoubleChunkFactory,
                _NRows,
                _NCols,
                _Dimensions,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grid2DSquareCellDouble grid with all values as
     * NoDataValues.
     * 
     * @param _GridStatistics The AbstractGridStatistics to accompany the
     *   returned grid.
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellDouble information.
     * @param _Grid2DSquareCellDoubleChunkFactory The prefered
     *  Grid2DSquareCellDoubleChunkFactoryy for creating chunks that
     *   the constructed Grid2DSquareCellDouble is to be made of.
     * @param _NRows The Grid2DSquareCellDouble _NRows.
     * @param _NCols The Grid2DSquareCellDouble _NCols.
     * @param _Dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     *   in this method then swap operations are initiated prior to retrying.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public Grid2DSquareCellDouble create(
            AbstractGridStatistics _GridStatistics,
            File _Directory,
            AbstractGrid2DSquareCellDoubleChunkFactory _Grid2DSquareCellDoubleChunkFactory,
            long _NRows,
            long _NCols,
            BigDecimal[] _Dimensions,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        return new Grid2DSquareCellDouble(
                _GridStatistics,
                _Directory,
                _Grid2DSquareCellDoubleChunkFactory,
                this._ChunkNRows,
                this._ChunkNCols,
                _NRows,
                _NCols,
                _Dimensions,
                this._NoDataValue,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    //////////////////////////////////////////////////////
    // Create from an existing AbstractGrid2DSquareCell //
    //////////////////////////////////////////////////////
    /**
     * Returns a new Grid2DSquareCellDouble with all values taken from
     * _Grid2DSquareCell.
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellDouble information.
     * @param _Grid2DSquareCell TheGrid2DSquareCellt from which grid
     *   values are used.
     * @param startRowIndex The topmost row index of _Grid2DSquareCell.
     * @param startColIndex The leftmost column index of _Grid2DSquareCell.
     * @param endRowIndex The bottom row index of _Grid2DSquareCell.
     * @param endColIndex The rightmost column index of _Grid2DSquareCell.
     * @param a_Grids_Environment
     * @return 
     */
    @Override
    public Grid2DSquareCellDouble create(
            File _Directory,
            AbstractGrid2DSquareCell _Grid2DSquareCell,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        return create(
                get_GridStatistics(),
                _Directory,
                _Grid2DSquareCell,
                this._Grid2DSquareCellDoubleChunkFactory,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grid2DSquareCellDouble with all values taken from
     * _Grid2DSquareCell.
     * 
     * @param gridStatistics The AbstractGridStatistics to accompany the
     *   returned grid.
     * @param directory The _Directory to be used for storing cached
     *   Grid2DSquareCellDouble information.
     * @param chunkFactory The preferred
     *  Grid2DSquareCellDoubleChunkFactoryy for creating chunks that
     *   the constructed Grid2DSquareCellDouble is to be made of.
     * @param grid TheGrid2DSquareCellt from which grid
     *   values are used.
     * @param startRowIndex The topmost row index of _Grid2DSquareCell.
     * @param startColIndex The leftmost column index of _Grid2DSquareCell.
     * @param endRowIndex The bottom row index of _Grid2DSquareCell.
     * @param endColIndex The rightmost column index of _Grid2DSquareCell.
     * @param grids_Environment
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are initiated,
     *     then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public Grid2DSquareCellDouble create(
            AbstractGridStatistics gridStatistics,
            File directory,
            AbstractGrid2DSquareCell grid,
            AbstractGrid2DSquareCellDoubleChunkFactory chunkFactory,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment grids_Environment,
            boolean handleOutOfMemoryError) {
        return new Grid2DSquareCellDouble(
                gridStatistics,
                directory,
                grid,
                chunkFactory,
                this._ChunkNRows,
                this._ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                this._NoDataValue,
                grids_Environment,
                handleOutOfMemoryError);
    }

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * Returns a new Grid2DSquareCellDouble with values obtained from gridFile.
     * 
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellDouble information.
     * @param gridFile Either a _Directory, or a formatted File with a specific
     *   extension containing the data and information about the
     *   Grid2DSquareCellDouble to be returned.
     * @param startRowIndex The topmost row index of the grid stored as gridFile.
     * @param startColIndex The leftmost column index of the grid stored as gridFile.
     * @param endRowIndex The bottom row index of the grid stored as gridFile.
     * @param endColIndex The rightmost column index of the grid stored as gridFile.
     * @param a_Grids_Environment
     * @return 
     */
    @Override
    public Grid2DSquareCellDouble create(
            File _Directory,
            File gridFile,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        return create(
                get_GridStatistics(),
                _Directory,
                gridFile,
                this._Grid2DSquareCellDoubleChunkFactory,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grid2DSquareCellDouble with values obtained from gridFile.
     * 
     * @param _GridStatistics The AbstractGridStatistics to accompany the
     *   returned grid.
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellDouble information.
     * @param gridFile Either a _Directory, or a formatted File with a specific
     *   extension containing the data and information about the
     *   Grid2DSquareCellDouble to be returned.
     * @param _Grid2DSquareCellDoubleChunkFactory The prefered
     *  Grid2DSquareCellDoubleChunkFactoryy for creating chunks that
     *   the constructed Grid2DSquareCellDouble is to be made of.
     * @param startRowIndex The topmost row index of the grid stored as gridFile.
     * @param startColIndex The leftmost column index of the grid stored as gridFile.
     * @param endRowIndex The bottom row index of the grid stored as gridFile.
     * @param endColIndex The rightmost column index of the grid stored as gridFile.
     * @param a_Grids_Environment
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     *   in this method then swap operations are initiated prior to retrying.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public Grid2DSquareCellDouble create(
            AbstractGridStatistics _GridStatistics,
            File _Directory,
            File gridFile,
            AbstractGrid2DSquareCellDoubleChunkFactory _Grid2DSquareCellDoubleChunkFactory,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        return new Grid2DSquareCellDouble(
                _GridStatistics,
                _Directory,
                gridFile,
                _Grid2DSquareCellDoubleChunkFactory,
                this._ChunkNRows,
                this._ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                this._NoDataValue,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }

    /////////////////////////
    // Create from a cache //
    /////////////////////////
    /**
     * Returns a new Grid2DSquareCellDouble with values obtained from gridFile.
     * 
     * @param _Directory The _Directory for swapping to file.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     * @param a_Grids_Environment
     * @return 
     */
    public 
    @Override
    Grid2DSquareCellDouble create(
            File _Directory,
            File gridFile,
            ObjectInputStream ois,
            Grids_Environment a_Grids_Environment,
            boolean handleOutOfMemoryError) {
        return new Grid2DSquareCellDouble(
                _Directory,
                gridFile,
                ois,
                a_Grids_Environment,
                handleOutOfMemoryError);
    }
}
