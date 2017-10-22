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

import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGrid2DSquareCellDoubleChunkFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellDoubleChunkArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellDoubleChunk64CellMapFactory;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_AbstractGridStatistics;
import java.io.File;
import java.io.ObjectInputStream;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics0;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_FileCreator;

/**
 * A factory for constructing Grid2DSquareCellDouble instances.
 */
public class Grids_Grid2DSquareCellDoubleFactory
        extends Grids_AbstractGrid2DSquareCellFactory {

    /**
     * The Grid2DSquareCellChunkAbstractFactory for creating chunks.
     */
    protected Grids_AbstractGrid2DSquareCellDoubleChunkFactory ChunkFactory;
    /**
     * The NoDataValue for creating chunks.
     */
    protected double NoDataValue;

    /**
     * Creates a new Grid2DSquareCellDoubleFactory. Default: NoDataValue to
     * Double.NEGATIVE_INFINITY
     *
     * @param ge
     * @param handleOutOfMemoryError
     */
    public Grids_Grid2DSquareCellDoubleFactory(
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        this(Double.NEGATIVE_INFINITY,
                ge,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     *
     *
     * @param noDataValue The NoDataValue initially set for construction.
     * Defaults: Directory to a new one in System.getProperties(
     * "java.io.tmpdir" );
     * @param ge
     * @param handleOutOfMemoryError
     */
    public Grids_Grid2DSquareCellDoubleFactory(
            double noDataValue,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        this(Grids_FileCreator.createTempFile(),
                noDataValue,
                ge,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     *
     * @param directory A "workspace Directory" for storing temporary files and
     * swapping Grid2DSquareCellDouble data to. Defaults: ChunkNRows to 64
     * _ChunkNColss to 64 Grids_AbstractGrid2DSquareCellDoubleChunkFactory to
     * _Grid2DSquareCellDoubleChunkArray
     * @param ge
     * @param handleOutOfMemoryError
     */
    public Grids_Grid2DSquareCellDoubleFactory(
            File directory,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        this(directory,
                Double.NEGATIVE_INFINITY,
                ge,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     *
     * @param directory A "workspace Directory" for storing temporary files and
     * swapping Grid2DSquareCellDouble data to.
     * @param noDataValue The NoDataValue initially set for construction.
     * Default: ChunkNRows to 64; _ChunkNColss to 64;
     * Grids_AbstractGrid2DSquareCellDoubleChunkFactory to
     * _Grid2DSquareCellDoubleChunkArray.
     * @param ge
     * @param handleOutOfMemoryError
     */
    public Grids_Grid2DSquareCellDoubleFactory(
            File directory,
            double noDataValue,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        this(directory,
                64,
                64,
                new Grids_Grid2DSquareCellDoubleChunkArrayFactory(),
                noDataValue,
                ge,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     *
     * @param directory A "workspace Directory" for storing temporary files and
     * caching Grid2DSquareCellDoubleAbstract data.
     * @param chunkNRows The number of rows chunks have by default.
     * @param gcf
     * TheGrid2DSquareCellDoubleChunkFactoryy for creating
     * Grid2DSquareCellDoubleChunks Default: NoDataValue to
     * Double.NEGATIVE_INFINITY.
     * @param chunkNCols The number of columns chunks have by default.
     * @param ge
     * @param handleOutOfMemoryError
     */
    public Grids_Grid2DSquareCellDoubleFactory(
            File directory,
            int chunkNRows,
            int chunkNCols,
            Grids_AbstractGrid2DSquareCellDoubleChunkFactory gcf,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        this(directory,
                chunkNRows,
                chunkNCols,
                gcf,
                Double.NEGATIVE_INFINITY,
                ge,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory.
     *
     * @param directory A "workspace Directory" for storing temporary files and
     * caching Grid2DSquareCellDoubleAbstract data.
     * @param chunkNRows The number of rows chunks have by default.
     * @param gcf TheGrid2DSquareCellDoubleChunkFactoryy for creating
     * Grid2DSquareCellDoubleChunks
     * @param noDataValue The NoDataValue initially set for construction.
     * @param chunkNCols The number of columns chunks have by default.
     * @param ge
     * @param handleOutOfMemoryError
     */
    public Grids_Grid2DSquareCellDoubleFactory(
            File directory,
            int chunkNRows,
            int chunkNCols,
            Grids_AbstractGrid2DSquareCellDoubleChunkFactory gcf,
            double noDataValue,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        super(ge);
        boolean isGrid2DSquareCellDoubleChunk64CellMapFactory
                = (gcf.getClass()
                == Grids_Grid2DSquareCellDoubleChunk64CellMapFactory.class);
        if (isGrid2DSquareCellDoubleChunk64CellMapFactory) {
            if (chunkNRows * chunkNCols > 64) {
                chunkNRows = 8;
                chunkNCols = 8;
            }
        }
        this.Directory = directory;
        this.ChunkNRows = chunkNRows;
        this.ChunkNCols = chunkNCols;
        initDimensions(chunkNCols, chunkNRows);
        this.ChunkFactory = gcf;
        this.setGridStatistics(new Grids_GridStatistics0(ge));
        this.HandleOutOfMemoryError = handleOutOfMemoryError;
        this.NoDataValue = noDataValue;
    }

    /**
     * Returns a reference to this.ChunkFactory.
     *
     * @return
     */
    public Grids_AbstractGrid2DSquareCellDoubleChunkFactory getChunkFactory() {
        return this.ChunkFactory;
    }

    /**
     * Sets this.ChunkFactory to
 gcf.
     *
     * @param gcf
     */
    public void setChunkFactory(
            Grids_AbstractGrid2DSquareCellDoubleChunkFactory gcf) {
        this.ChunkFactory = gcf;
    }

    /**
     * Returns this.NoDataValue.
     *
     * @return
     */
    public double get_NoDataValue() {
        return this.NoDataValue;
    }

    /**
     * Sets this.NoDataValue.
     *
     * @param noDataValue
     */
    public void set_NoDataValue(
            double noDataValue) {
        this.NoDataValue = noDataValue;
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * Returns a new Grid2DSquareCellDouble grid with all values as
     * NoDataValues.
     *
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellDouble information.
     * @param nRows The Grid2DSquareCellDouble _NRows.
     * @param nCols The Grid2DSquareCellDouble _NCols.
     * @param dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public Grids_Grid2DSquareCellDouble create(
            File directory,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            boolean handleOutOfMemoryError) {
        return create(getGridStatistics(),
                directory,
                this.ChunkFactory,
                nRows,
                nCols,
                dimensions,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grid2DSquareCellDouble grid with all values as
     * NoDataValues.
     *
     * @param gridStatistics The AbstractGridStatistics to accompany the
     * returned grid.
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellDouble information.
     * @param gcf The prefered Grid2DSquareCellDoubleChunkFactoryy for creating
     * chunks that the constructed Grid2DSquareCellDouble is to be made of.
     * @param nRows The Grid2DSquareCellDouble _NRows.
     * @param nCols The Grid2DSquareCellDouble _NCols.
     * @param dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_Grid2DSquareCellDouble create(
            Grids_AbstractGridStatistics gridStatistics,
            File directory,
            Grids_AbstractGrid2DSquareCellDoubleChunkFactory gcf,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            boolean handleOutOfMemoryError) {
        return new Grids_Grid2DSquareCellDouble(
                gridStatistics,
                directory,
                gcf,
                this.ChunkNRows,
                this.ChunkNCols,
                nRows,
                nCols,
                dimensions,
                this.NoDataValue,
                ge,
                handleOutOfMemoryError);
    }

    //////////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGrid2DSquareCell //
    //////////////////////////////////////////////////////
    /**
     * Returns a new Grid2DSquareCellDouble with all values taken from
     * _Grid2DSquareCell.
     *
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellDouble information.
     * @param g TheGrid2DSquareCellt from which grid values are used.
     * @param startRowIndex The topmost row index of _Grid2DSquareCell.
     * @param startColIndex The leftmost column index of _Grid2DSquareCell.
     * @param endRowIndex The bottom row index of _Grid2DSquareCell.
     * @param endColIndex The rightmost column index of _Grid2DSquareCell.
     * @return
     */
    @Override
    public Grids_Grid2DSquareCellDouble create(
            File directory,
            Grids_AbstractGrid2DSquareCell g,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        return create(getGridStatistics(),
                directory,
                g,
                this.ChunkFactory,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grid2DSquareCellDouble with all values taken from
     * _Grid2DSquareCell.
     *
     * @param gridStatistics The AbstractGridStatistics to accompany the
     * returned grid.
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellDouble information.
     * @param chunkFactory The preferred Grid2DSquareCellDoubleChunkFactoryy for
     * creating chunks that the constructed Grid2DSquareCellDouble is to be made
     * of.
     * @param grid TheGrid2DSquareCellt from which grid values are used.
     * @param startRowIndex The topmost row index of _Grid2DSquareCell.
     * @param startColIndex The leftmost column index of _Grid2DSquareCell.
     * @param endRowIndex The bottom row index of _Grid2DSquareCell.
     * @param endColIndex The rightmost column index of _Grid2DSquareCell.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_Grid2DSquareCellDouble create(
            Grids_AbstractGridStatistics gridStatistics,
            File directory,
            Grids_AbstractGrid2DSquareCell grid,
            Grids_AbstractGrid2DSquareCellDoubleChunkFactory chunkFactory,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        return new Grids_Grid2DSquareCellDouble(
                gridStatistics,
                directory,
                grid,
                chunkFactory,
                this.ChunkNRows,
                this.ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                this.NoDataValue,
                ge,
                handleOutOfMemoryError);
    }

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * Returns a new Grid2DSquareCellDouble with values obtained from gridFile.
     *
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellDouble information.
     * @param gridFile Either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grid2DSquareCellDouble to be returned.
     * @param startRowIndex The topmost row index of the grid stored as
     * gridFile.
     * @param startColIndex The leftmost column index of the grid stored as
     * gridFile.
     * @param endRowIndex The bottom row index of the grid stored as gridFile.
     * @param endColIndex The rightmost column index of the grid stored as
     * gridFile.
     * @return
     */
    @Override
    public Grids_Grid2DSquareCellDouble create(
            File directory,
            File gridFile,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        return create(getGridStatistics(),
                directory,
                gridFile,
                this.ChunkFactory,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                ge,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grid2DSquareCellDouble with values obtained from gridFile.
     *
     * @param gridStatistics The AbstractGridStatistics to accompany the
     * returned grid.
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellDouble information.
     * @param gridFile Either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grid2DSquareCellDouble to be returned.
     * @param gcf The prefered Grid2DSquareCellDoubleChunkFactoryy for creating
     * chunks that the constructed Grid2DSquareCellDouble is to be made of.
     * @param startRowIndex The topmost row index of the grid stored as
     * gridFile.
     * @param startColIndex The leftmost column index of the grid stored as
     * gridFile.
     * @param endRowIndex The bottom row index of the grid stored as gridFile.
     * @param endColIndex The rightmost column index of the grid stored as
     * gridFile.
     * @param ge
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_Grid2DSquareCellDouble create(
            Grids_AbstractGridStatistics gridStatistics,
            File directory,
            File gridFile,
            Grids_AbstractGrid2DSquareCellDoubleChunkFactory gcf,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        return new Grids_Grid2DSquareCellDouble(
                gridStatistics,
                directory,
                gridFile,
                gcf,
                this.ChunkNRows,
                this.ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                this.NoDataValue,
                ge,
                handleOutOfMemoryError);
    }

    /////////////////////////
    // Create from a cache //
    /////////////////////////
    /**
     * Returns a new Grid2DSquareCellDouble with values obtained from gridFile.
     *
     * @param directory The Directory for swapping to file.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     * @return
     */
    public @Override
    Grids_Grid2DSquareCellDouble create(
            File directory,
            File gridFile,
            ObjectInputStream ois,
            boolean handleOutOfMemoryError) {
        return new Grids_Grid2DSquareCellDouble(
                directory,
                gridFile,
                ois,
                ge,
                handleOutOfMemoryError);
    }
}
