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

import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_AbstractStatisticsBigDecimal;
import java.io.File;
import java.io.ObjectInputStream;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_GridStatisticsNotUpdatedAsDataChanged;

/**
 * A factory for constructing Grids_GridInt instances.
 */
public class Grids_GridIntFactory
        extends Grids_AbstractGridNumberFactory {

    /**
     * The NoDataValue for creating chunks.
     */
    protected int NoDataValue;

    public Grids_GridChunkIntFactory GridChunkIntFactory;
//    public Grids_GridChunkIntMapFactory ChunkIntMapFactory;
//    public Grids_GridChunkIntArrayFactory ChunkIntArrayFactory;
    public Grids_AbstractGridChunkIntFactory DefaultGridChunkFactory;

    protected Grids_GridIntFactory() {
    }

    /**
     * Creates a new Grids_GridIntFactory. Directory is defaulted to
     * ge.getFiles().getGeneratedGridIntFactoryDir(). Dimensions is defaulted
     * to new Grids_Dimensions(chunkNRows, chunkNCols). Statistics is defaulted
     * to new Grids_GridStatisticsNotUpdatedAsDataChanged(ge). NoDataValue is
     * defaulted to Integer.MIN_VALUE. GridChunkIntFactory is defaulted to new
     * Grids_GridChunkIntFactory(). DefaultGridChunkFactory is defaulted to
     * GridChunkIntFactory.
     *
     * @param chunkNRows The number of rows chunks have by default.
     * @param chunkNCols The number of columns chunks have by default.
     * @param ge
     */
    public Grids_GridIntFactory(
            Grids_Environment ge,
            int chunkNRows,
            int chunkNCols) {
        super(ge, ge.getFiles().getGeneratedGridIntFactoryDir(),
                chunkNRows, chunkNCols,
                new Grids_Dimensions(chunkNRows, chunkNCols),
                new Grids_GridStatisticsNotUpdatedAsDataChanged(ge));
        NoDataValue = Integer.MIN_VALUE;
        GridChunkIntFactory = new Grids_GridChunkIntFactory(NoDataValue);
        DefaultGridChunkFactory = GridChunkIntFactory;
    }

    /**
     * Creates a new Grids_GridIntFactory.
     *
     * @param directory A directory for storing temporary files and caching Grid
     * data.
     * @param chunkNRows The number of rows chunks have by default.
     * @param noDataValue
     * @param defaultGridChunkFactory
     * @param dimensions
     * @param gridStatistics
     * @param chunkNCols The number of columns chunks have by default.
     * @param ge
     */
    public Grids_GridIntFactory(
            Grids_Environment ge,
            File directory,
            int noDataValue,
            int chunkNRows,
            int chunkNCols,
            Grids_Dimensions dimensions,
            Grids_AbstractStatisticsBigDecimal gridStatistics,
            Grids_AbstractGridChunkIntFactory defaultGridChunkFactory) {
        super(ge, directory, chunkNRows, chunkNCols, dimensions, gridStatistics);
        NoDataValue = noDataValue;
        GridChunkIntFactory = new Grids_GridChunkIntFactory(NoDataValue);
        DefaultGridChunkFactory = defaultGridChunkFactory;
    }

    /**
     * Set DefaultGridChunkFactory to defaultChunkFactory.
     *
     * @param defaultChunkFactory
     */
    public void setDefaultChunkFactory(
            Grids_AbstractGridChunkIntFactory defaultChunkFactory) {
        DefaultGridChunkFactory = defaultChunkFactory;
    }

    /**
     * Returns NoDataValue.
     *
     * @return
     */
    public double getNoDataValue() {
        return NoDataValue;
    }

    /**
     * Sets NoDataValue to noDataValue.
     *
     * @param noDataValue
     */
    public void setNoDataValue(
            int noDataValue) {
        NoDataValue = noDataValue;
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * Returns a new Grids_GridInt with all values as NoDataValues.
     *
     * @param directory The Directory to be used for storing cached data.
     * @param nRows The NRows.
     * @param nCols The NCols.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public Grids_GridInt create(
            File directory,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            boolean handleOutOfMemoryError) {
        return create(getGridStatistics(),
                directory,
                DefaultGridChunkFactory,
                nRows,
                nCols,
                dimensions,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grids_GridInt grid with all values as NoDataValues.
     *
     * @param gridStatistics The GridStatistics to accompany the returned grid.
     * @param directory The directory to be used for storing cached grid
     * information.
     * @param chunkFactory The preferred Grids_AbstractGridChunkIntFactory
     * for creating chunks that the constructed Grid is to be made of.
     * @param nRows The NRows.
     * @param nCols The NCols.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_GridInt create(
            Grids_AbstractStatisticsBigDecimal gridStatistics,
            File directory,
            Grids_AbstractGridChunkIntFactory chunkFactory,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            boolean handleOutOfMemoryError) {
        return new Grids_GridInt(
                gridStatistics,
                directory,
                chunkFactory,
                ChunkNRows,
                ChunkNCols,
                nRows,
                nCols,
                dimensions,
                NoDataValue,
                ge,
                handleOutOfMemoryError);
    }

    //////////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGridNumber //
    //////////////////////////////////////////////////////
    /**
     * Returns a new Grids_GridInt with all values taken from g.
     *
     * @param directory The Directory to be used for storing cached data.
     * @param g The Grids_AbstractGridNumber from which values are used.
     * @param startRowIndex The topmost row index of g.
     * @param startColIndex The leftmost column index of g.
     * @param endRowIndex The bottom row index of g.
     * @param endColIndex The rightmost column index of g.
     * @return
     */
    @Override
    public Grids_GridInt create(
            File directory,
            Grids_AbstractGridNumber g,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        return create(getGridStatistics(),
                directory,
                g,
                DefaultGridChunkFactory,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grids_GridInt with all values taken from g.
     *
     * @param gridStatistics The GridStatistics to accompany the returned grid.
     * @param directory The directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param chunkFactory The preferred Grids_AbstractGridChunkIntFactory
     * for creating chunks that the constructed Grid is to be made of.
     * @param g The Grids_AbstractGridNumber from which grid values are used.
     * @param startRowIndex The topmost row index of g.
     * @param startColIndex The leftmost column index of g.
     * @param endRowIndex The bottom row index of g.
     * @param endColIndex The rightmost column index of g.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_GridInt create(
            Grids_AbstractStatisticsBigDecimal gridStatistics,
            File directory,
            Grids_AbstractGridNumber g,
            Grids_AbstractGridChunkIntFactory chunkFactory,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        return new Grids_GridInt(
                gridStatistics,
                directory,
                g,
                chunkFactory,
                ChunkNRows,
                ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                NoDataValue,
                handleOutOfMemoryError);
    }

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * Returns a new Grids_GridInt with values obtained from gridFile.
     *
     * @param directory The Directory to be used for storing cached Grid
     * information.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * returned.
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
    public Grids_GridInt create(
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
                DefaultGridChunkFactory,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grids_GridInt with values obtained from gridFile.
     *
     * @param gridStatistics The GridStatistics to accompany the returned grid.
     * @param directory The Directory to be used for storing cached Grid
     * information.
     * @param gridFile Either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grid2DSquareCellInt to be returned.
     * @param chunkFactory The preferred Grids_AbstractGridChunkIntFactory
     * for creating chunks that the constructed Grid is to be made of.
     * @param startRowIndex The topmost row index of the grid stored as
     * gridFile.
     * @param startColIndex The leftmost column index of the grid stored as
     * gridFile.
     * @param endRowIndex The bottom row index of the grid stored as gridFile.
     * @param endColIndex The rightmost column index of the grid stored as
     * gridFile.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_GridInt create(
            Grids_AbstractStatisticsBigDecimal gridStatistics,
            File directory,
            File gridFile,
            Grids_AbstractGridChunkIntFactory chunkFactory,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        return new Grids_GridInt(
                gridStatistics,
                directory,
                gridFile,
                chunkFactory,
                ChunkNRows,
                ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                NoDataValue,
                ge,
                handleOutOfMemoryError);
    }

    /////////////////////////
    // Create from a cache //
    /////////////////////////
    /**
     * Returns a new Grids_GridInt with values obtained from gridFile.
     *
     * @param directory The Directory to be used for storing cached Grid
     * information.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     * @return
     */
    public @Override
    Grids_GridInt create(
            File directory,
            File gridFile,
            ObjectInputStream ois,
            boolean handleOutOfMemoryError) {
        return new Grids_GridInt(
                directory,
                gridFile,
                ois,
                ge,
                handleOutOfMemoryError);
    }
}
