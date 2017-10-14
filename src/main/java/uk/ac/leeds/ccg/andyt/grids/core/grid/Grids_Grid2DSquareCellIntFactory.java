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
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGrid2DSquareCellIntChunkFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellIntChunkArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellIntChunk64CellMapFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_Grid2DSquareCellInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_AbstractGridStatistics;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics1;
import java.io.File;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_FileCreator;

/**
 * A factory for constructing Grid2DSquareCellInt instances.
 */
public class Grids_Grid2DSquareCellIntFactory
        extends Grids_AbstractGrid2DSquareCellFactory {

    /**
     * The Grid2DSquareCellChunkAbstractFactory for creating chunks.
     */
    protected Grids_AbstractGrid2DSquareCellIntChunkFactory grid2DSquareCellIntChunkFactory;

    /**
     * Creates a new Grid2DSquareCellDoubleFactory
     * Defaults:
     * _Directory to a new one in System.getProperties( "java.io.tmpdir" );
     * @param a_Grids_Environment
     * @param _HandleOutOfMemoryError
     */
    public Grids_Grid2DSquareCellIntFactory(
            Grids_Environment a_Grids_Environment,
            boolean _HandleOutOfMemoryError) {
        this(Grids_FileCreator.createTempFile(),
            a_Grids_Environment,
                _HandleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory
     * 
     * 
     * 
     * @param _Directory A "workspace _Directory" for storing temporary files and
     *   swapping Grid2DSquareCellDouble data to.
     * Defaults:
     * _ChunkNRows to 64
     * _ChunkNColss to 64
     * Grid2DSquareCellDoubleChunkFactory to grid2DSquareCellDoubleChunkArray
     * @param _Grids_Environment
     * @param _HandleOutOfMemoryError
     */
    public Grids_Grid2DSquareCellIntFactory(
            File _Directory,
            Grids_Environment _Grids_Environment,
            boolean _HandleOutOfMemoryError) {
        this(_Directory,
                64,
                64,
                new Grids_Grid2DSquareCellIntChunkArrayFactory(),
                _Grids_Environment,
                _HandleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory
* @param _Directory A "workspace _Directory" for storing temporary files and
     *   caching Grid2DSquareCellIntAbstract data.
     * @param _ChunkNRows The number of rows chunks have by default.
     * @param _Grid2DSquareCellIntChunkFactory The Grids_AbstractGrid2DSquareCellIntChunkFactory
   for creating Grid2DSquareCellDoubleChunks
     * @param _ChunkNCols The number of columns chunks have by default.
     * @param ge
     * @param _HandleOutOfMemoryError
     */
    public Grids_Grid2DSquareCellIntFactory(
            File _Directory,
            int _ChunkNRows,
            int _ChunkNCols,
            Grids_AbstractGrid2DSquareCellIntChunkFactory _Grid2DSquareCellIntChunkFactory,
            Grids_Environment ge,
            boolean _HandleOutOfMemoryError) {
        super(ge);
        this._Directory = _Directory;
        if (_Grid2DSquareCellIntChunkFactory.getClass()
                == Grids_Grid2DSquareCellIntChunk64CellMapFactory.class) {
            if (_ChunkNRows * _ChunkNCols > 64) {
                _ChunkNRows = 8;
                _ChunkNCols = 8;
            }
        }
        this._ChunkNRows = _ChunkNRows;
        this._ChunkNCols = _ChunkNCols;
        this._Dimensions = new BigDecimal[5];
        this._Dimensions[ 0] = new BigDecimal(1L);
        this._Dimensions[ 1] = new BigDecimal(0L);
        this._Dimensions[ 2] = new BigDecimal(0L);
        this._Dimensions[ 3] = new BigDecimal(_ChunkNCols);
        this._Dimensions[ 4] = new BigDecimal(_ChunkNRows);
        this.grid2DSquareCellIntChunkFactory = _Grid2DSquareCellIntChunkFactory;
        this.ge = ge;
        this.set_GridStatistics(new Grids_GridStatistics1());
        this._HandleOutOfMemoryError = true;
    }

    /**
     * Returns a reference to this.grid2DSquareCellIntChunkFactory.
     * @return 
     */
    public Grids_AbstractGrid2DSquareCellIntChunkFactory getGrid2DSquareCellIntChunkFactory() {
        return this.grid2DSquareCellIntChunkFactory;
    }

    /**
     * Sets this.grid2DSquareCellIntChunkFactory to grid2DSquareCellIntChunkFactory.
     * @param grid2DSquareCellIntChunkFactory
     */
    public void setGrid2DSquareCellIntChunkFactory(
            Grids_AbstractGrid2DSquareCellIntChunkFactory grid2DSquareCellIntChunkFactory) {
        this.grid2DSquareCellIntChunkFactory = grid2DSquareCellIntChunkFactory;
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * Returns a new Grid2DSquareCellInt grid with all values as
     * noDataValues.
     * 
     * 
     * 
     * 
     * @param _Directory The _Directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param nrows the Grid2DSquareCellInt nrows.
     * @param ncols the Grid2DSquareCellInt ncols.
     * @param _Dimensions
     * @param _Grids_Environment
     * @param _HandleOutOfMemoryError If true then OutOfMemoryErrors are caught
     *   in this method then swap operations are initiated prior to retrying.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public Grids_Grid2DSquareCellInt create(
            File _Directory,
            long nrows,
            long ncols,
            BigDecimal[] _Dimensions,
            Grids_Environment _Grids_Environment,
            boolean _HandleOutOfMemoryError) {
        return create(
                get_GridStatistics(),
                _Directory,
                this.grid2DSquareCellIntChunkFactory,
                nrows,
                ncols,
                _Dimensions,
                _Grids_Environment,
                _HandleOutOfMemoryError);
    }

    /**
     * Returns a new Grid2DSquareCellInt grid with all values as noDataValues.
     * @param gridStatistics The AbstractGridStatistics to accompany the
     *   returned grid.
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellInt information.
     * @param grid2DSquareCellIntChunkFactory The 
   Grids_AbstractGrid2DSquareCellIntChunkFactory for creating chunks.
     * @param nrows The Grid2DSquareCellInt nrows.
     * @param ncols The Grid2DSquareCellInt ncols.
     * @param _Dimensions
     * @param _Grids_Environment
     * @param _HandleOutOfMemoryError If true then OutOfMemoryErrors are caught
     *   in this method then swap operations are initiated prior to retrying.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public Grids_Grid2DSquareCellInt create(
            Grids_AbstractGridStatistics gridStatistics,
            File _Directory,
            Grids_AbstractGrid2DSquareCellIntChunkFactory grid2DSquareCellIntChunkFactory,
            long nrows,
            long ncols,
            BigDecimal[] _Dimensions,
            Grids_Environment _Grids_Environment,
            boolean _HandleOutOfMemoryError) {
        return new Grids_Grid2DSquareCellInt(
                gridStatistics,
                _Directory,
                grid2DSquareCellIntChunkFactory,
                this._ChunkNRows,
                this._ChunkNCols,
                nrows,
                ncols,
                _Dimensions,
                _Grids_Environment,
                _HandleOutOfMemoryError);
    }

    //////////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGrid2DSquareCell //
    //////////////////////////////////////////////////////
    /**
     * Returns a new Grid2DSquareCellInt with values obtained from
     * grid2DSquareCell.
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellInt information.
     * @param grid2DSquareCell The Grids_AbstractGrid2DSquareCell from which values
   are obtained.
     * @param startRowIndex The topmost row index of grid2DSquareCell thats
     *   values are used.
     * @param startColIndex The leftmost column index of grid2DSquareCell thats
     *   values are used.
     * @param endRowIndex The bottom row index of the grid2DSquareCell thats
     *   values are used.
     * @param endColIndex The rightmost column index of grid2DSquareCell thats
     *   values are used.
     * @param _Grids_Environment
     * @param _HandleOutOfMemoryError If true then OutOfMemoryErrors are caught
     *   in this method then swap operations are initiated prior to retrying.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public Grids_Grid2DSquareCellInt create(
            File _Directory,
            Grids_AbstractGrid2DSquareCell grid2DSquareCell,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment _Grids_Environment,
            boolean _HandleOutOfMemoryError) {
        return create(
                get_GridStatistics(),
                _Directory,
                grid2DSquareCell,
                this.grid2DSquareCellIntChunkFactory,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                _Grids_Environment,
                _HandleOutOfMemoryError);
    }

    /**
     * Returns a new Grid2DSquareCellInt with values obtained from
     * grid2DSquareCell.
     * @param gridStatistics The AbstractGridStatistics for the returned
     * Grid2DSquareCellInt.
     * @param _Directory The _Directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param grid2DSquareCell The Grids_AbstractGrid2DSquareCell from which values
 are obtained.
     * @param grid2DSquareCellIntChunkAbstractFactory The
 Grids_AbstractGrid2DSquareCellIntChunkFactory used to construct the chunks.
     * @param startRowIndex The topmost row index of grid2DSquareCell thats
     * values are used.
     * @param startColIndex The leftmost column index of grid2DSquareCell thats
     * values are used.
     * @param endRowIndex The bottom row index of the grid2DSquareCell thats
     * values are used.
     * @param endColIndex The rightmost column index of grid2DSquareCell thats
     * values are used.
     * @param _Grids_Environment
     * @param _HandleOutOfMemoryError If true then OutOfMemoryErrors are caught
     *   in this method then swap operations are initiated prior to retrying.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public Grids_Grid2DSquareCellInt create(
            Grids_AbstractGridStatistics gridStatistics,
            File _Directory,
            Grids_AbstractGrid2DSquareCell grid2DSquareCell,
            Grids_AbstractGrid2DSquareCellIntChunkFactory grid2DSquareCellIntChunkAbstractFactory,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment _Grids_Environment,
            boolean _HandleOutOfMemoryError) {
        return new Grids_Grid2DSquareCellInt(
                gridStatistics, _Directory,
                grid2DSquareCell,
                grid2DSquareCellIntChunkAbstractFactory,
                this._ChunkNRows,
                this._ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                _Grids_Environment,
                _HandleOutOfMemoryError);
    }

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * Returns a new Grid2DSquareCellInt with values obtained from
     * gridFile.
     * 
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellInt information.
     * @param gridFile either a _Directory, or a formatted File with a specific
     *   extension containing the data and information about the
     *   Grid2DSquareCellInt to be returned.
     * @param startRowIndex The topmost row index of the grid represented in
     *   gridFile thats values are used.
     * @param startColIndex The leftmost column index of the grid represented in
     *   gridFile thats values are used.
     * @param endRowIndex The bottom row index of the grid represented in
     *   gridFile thats values are used.
     * @param endColIndex The rightmost column index of the grid represented in
     *   gridFile thats values are used.
     * @param _Grids_Environment
     * @param _HandleOutOfMemoryError If true then OutOfMemoryErrors are caught
     *   in this method then swap operations are initiated prior to retrying.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public Grids_Grid2DSquareCellInt create(
            File _Directory,
            File gridFile,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment _Grids_Environment,
            boolean _HandleOutOfMemoryError) {
        return create(
                new Grids_GridStatistics1(),
                _Directory, gridFile,
                this.grid2DSquareCellIntChunkFactory,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                _Grids_Environment,
                _HandleOutOfMemoryError);
    }

    /**
     * Returns a new Grids_AbstractGrid2DSquareCell with values obtained from
 gridFile.
     * 
     * @param gridStatistics
     * @param _Directory The _Directory to be used for storing cached
     *   Grid2DSquareCellInt information.
     * @param gridFile either a _Directory, or a formatted File with a specific
   extension containing the data and information about the
   Grids_AbstractGrid2DSquareCell to be returned.
     * @param grid2DSquareCellIntChunkFactory The
   Grids_AbstractGrid2DSquareCellIntChunkFactory used to construct the chunks.
     * @param startRowIndex The topmost row index of the grid represented in
     *   gridFile thats values are used.
     * @param startColIndex The leftmost column index of the grid represented in
     *   gridFile thats values are used.
     * @param endRowIndex The bottom row index of the grid represented in
     *   gridFile thats values are used.
     * @param endColIndex The rightmost column index of the grid represented in
     *   gridFile thats values are used.
     * @param _AbstractGrid2DSquareCell_HashSet A HashSet of swappable Grids_AbstractGrid2DSquareCell
   instances.
     * @param a_Grids_Environment
     * @param _HandleOutOfMemoryError If true then OutOfMemoryErrors are caught
     *   in this method then swap operations are initiated prior to retrying.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    public Grids_Grid2DSquareCellInt create(
            Grids_AbstractGridStatistics gridStatistics,
            File _Directory,
            File gridFile,
            Grids_AbstractGrid2DSquareCellIntChunkFactory grid2DSquareCellIntChunkFactory,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment a_Grids_Environment,
            boolean _HandleOutOfMemoryError) {
        return new Grids_Grid2DSquareCellInt(
                gridStatistics,
                _Directory,
                gridFile,
                grid2DSquareCellIntChunkFactory,
                this._ChunkNRows,
                this._ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
            a_Grids_Environment,
            _HandleOutOfMemoryError);
    }

    /////////////////////////
    // Create from a cache //
    /////////////////////////
    /**
     * Returns a new Grid2DSquareCellInt with values obtained from gridFile.
     * @param _Directory The _Directory for swapping to file.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     * @param _Grids_Environment
     * @param _HandleOutOfMemoryError If true then OutOfMemoryErrors are caught
     *   in this method then swap operations are initiated prior to retrying.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return 
     */
    @Override
    public Grids_Grid2DSquareCellInt create(
            File _Directory,
            File gridFile,
            ObjectInputStream ois,
Grids_Environment _Grids_Environment,
            boolean _HandleOutOfMemoryError) {
        return new Grids_Grid2DSquareCellInt(
                _Directory,
                gridFile,
                ois,
            _Grids_Environment,
                _HandleOutOfMemoryError);
    }
}
