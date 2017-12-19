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

import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDoubleFactory;
import java.io.File;
import java.io.ObjectInputStream;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStats;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStatsNotUpdated;

/**
 * A factory for constructing Grids_GridDouble instances.
 */
public class Grids_GridDoubleFactory
        extends Grids_AbstractGridNumberFactory {

    /**
     * The NoDataValue for creating chunks.
     */
    protected double NoDataValue;

    public Grids_GridChunkDoubleFactory GridChunkDoubleFactory;
//    public Grids_GridChunkDoubleMapFactory ChunkDoubleMapFactory;
//    public Grids_GridChunkDoubleArrayFactory ChunkDoubleArrayFactory;
    public Grids_AbstractGridChunkDoubleFactory DefaultGridChunkDoubleFactory;

    public Grids_GridDoubleStats Stats;

    protected Grids_GridDoubleFactory() {
    }

    /**
     * Creates a new Grids_GridDoubleFactory. Directory is defaulted to
     * ge.getFiles().getGeneratedGridDoubleFactoryDir(). Dimensions is defaulted
     * to new Grids_Dimensions(chunkNRows, chunkNCols). Stats is defaulted to
     * new Grids_GridStatisticsNotUpdated(ge). NoDataValue is defaulted to
     * -Double.MAX_VALUE. GridChunkDoubleFactory is defaulted to new
     * Grids_GridChunkDoubleFactory. DefaultGridChunkDoubleFactory is defaulted
     * to defaultGridChunkDoubleFactory.
     *
     * @param ge
     * @param directory
     * @param gridChunkDoubleFactory
     * @param defaultGridChunkDoubleFactory
     * @param chunkNRows The number of rows chunks have by default.
     * @param chunkNCols The number of columns chunks have by default.
     */
    public Grids_GridDoubleFactory(
            Grids_Environment ge,
            File directory,
            Grids_GridChunkDoubleFactory gridChunkDoubleFactory,
            Grids_AbstractGridChunkDoubleFactory defaultGridChunkDoubleFactory,
            int chunkNRows,
            int chunkNCols) {
        super(ge, directory, chunkNRows, chunkNCols, null);
        GridChunkDoubleFactory = gridChunkDoubleFactory;
        DefaultGridChunkDoubleFactory = defaultGridChunkDoubleFactory;
        Stats = new Grids_GridDoubleStatsNotUpdated(ge);
        NoDataValue = -Double.MAX_VALUE;
    }

    /**
     * Creates a new Grids_GridDoubleFactory.
     *
     * @param ge
     * @param directory A directory for storing temporary files and caching Grid
     * data.
     * @param gridChunkDoubleFactory
     * @param defaultGridChunkDoubleFactory
     * @param noDataValue
     * @param chunkNRows The number of rows chunks have by default.
     * @param chunkNCols The number of columns chunks have by default.
     * @param dimensions
     * @param stats
     */
    public Grids_GridDoubleFactory(
            Grids_Environment ge,
            File directory,
            Grids_GridChunkDoubleFactory gridChunkDoubleFactory,
            Grids_AbstractGridChunkDoubleFactory defaultGridChunkDoubleFactory,
            double noDataValue,
            int chunkNRows,
            int chunkNCols,
            Grids_Dimensions dimensions,
            Grids_GridDoubleStats stats) {
        super(ge, directory, chunkNRows, chunkNCols, dimensions);
        GridChunkDoubleFactory = gridChunkDoubleFactory;
        DefaultGridChunkDoubleFactory = defaultGridChunkDoubleFactory;
        Stats = stats;
        NoDataValue = noDataValue;
    }

    /**
     * Set DefaultGridChunkDoubleFactory to defaultChunkFactory.
     *
     * @param defaultChunkFactory
     */
    public void setDefaultChunkFactory(
            Grids_AbstractGridChunkDoubleFactory defaultChunkFactory) {
        DefaultGridChunkDoubleFactory = defaultChunkFactory;
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
            double noDataValue) {
        NoDataValue = noDataValue;
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * Returns a new Grids_GridDouble with all values as NoDataValues.
     *
     * @param directory The Directory to be used for storing cached data.
     * @param nRows The NRows.
     * @param nCols The NCols.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public Grids_GridDouble create(
            File directory,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            boolean hoome) {
        return create(new Grids_GridDoubleStatsNotUpdated(ge), directory,
                GridChunkDoubleFactory, nRows, nCols, dimensions, hoome);
    }

    /**
     * Returns a new Grids_GridDouble grid with all values as NoDataValues.
     *
     * @param stats The type of Grids_GridDoubleStats to accompany the returned
     * grid.
     * @param directory The directory to be used for caching grid data.
     * @param chunkFactory The preferred Grids_AbstractGridChunkDoubleFactory
     * for creating chunks that the constructed Grid is to be made of.
     * @param nRows The NRows.
     * @param nCols The NCols.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then swap operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_GridDouble create(
            Grids_GridDoubleStats stats,
            File directory,
            Grids_AbstractGridChunkDoubleFactory chunkFactory,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            boolean hoome) {
        return new Grids_GridDouble(getStats(stats), directory, chunkFactory,
                ChunkNRows, ChunkNCols, nRows, nCols, dimensions, NoDataValue,
                ge, hoome);
    }

    //////////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGridNumber //
    //////////////////////////////////////////////////////
    /**
     * Returns a new Grids_GridDouble with all values taken from g.
     *
     * @param directory The Directory to be used for caching data.
     * @param g The Grids_AbstractGridNumber from which values are used.
     * @param startRow The topmost row index of g.
     * @param startCol The leftmost column index of g.
     * @param endRow The bottom row index of g.
     * @param endCol The rightmost column index of g.
     * @param hoome
     * @return
     */
    @Override
    public Grids_GridDouble create(
            File directory,
            Grids_AbstractGridNumber g,
            long startRow,
            long startCol,
            long endRow,
            long endCol,
            boolean hoome) {
        return create(new Grids_GridDoubleStatsNotUpdated(ge), directory,
                g, DefaultGridChunkDoubleFactory, startRow, startCol, endRow,
                endCol, hoome);
    }

    /**
     * Returns a new Grids_GridDouble with all values taken from g.
     *
     * @param stats The type of Grids_GridDoubleStats to accompany the returned
     * grid.
     * @param directory The directory to be used for caching data.
     * @param chunkFactory The preferred Grids_AbstractGridChunkDoubleFactory
     * for creating chunks that the constructed Grid is to be made of.
     * @param g The Grids_AbstractGridNumber from which grid values are used.
     * @param startRow The topmost row index of g.
     * @param startCol The leftmost column index of g.
     * @param endRow The bottom row index of g.
     * @param endCol The rightmost column index of g.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_GridDouble create(
            Grids_GridDoubleStats stats,
            File directory,
            Grids_AbstractGridNumber g,
            Grids_AbstractGridChunkDoubleFactory chunkFactory,
            long startRow,
            long startCol,
            long endRow,
            long endCol,
            boolean hoome) {
        return new Grids_GridDouble(getStats(stats), directory, g, chunkFactory,
                ChunkNRows, ChunkNCols, startRow, startCol, endRow, endCol,
                NoDataValue, hoome);
    }

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * Returns a new Grids_GridDouble with values obtained from gridFile.
     *
     * @param directory The Directory to be used for storing cached Grid
     * information.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * returned.
     * @param startRow The topmost row index of the grid stored as gridFile.
     * @param startCol The leftmost column index of the grid stored as gridFile.
     * @param endRow The bottom row index of the grid stored as gridFile.
     * @param endCol The rightmost column index of the grid stored as gridFile.
     * @param hoome
     * @return
     */
    @Override
    public Grids_GridDouble create(
            File directory,
            File gridFile,
            long startRow,
            long startCol,
            long endRow,
            long endCol,
            boolean hoome) {
        return create(new Grids_GridDoubleStatsNotUpdated(ge), directory,
                gridFile, DefaultGridChunkDoubleFactory, startRow, startCol,
                endRow, endCol, hoome);
    }

    /**
     * Returns a new Grids_GridDouble with values obtained from gridFile.
     *
     * @param stats The type of Grids_GridDoubleStats to accompany the returned
     * grid.
     * @param directory The directory to be used for storing cached Grid
     * information.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * returned.
     * @param chunkFactory The preferred factory for creating chunks that the
     * constructed Grid is to be made of.
     * @param startRow The topmost row index of the grid stored as gridFile.
     * @param startCol The leftmost column index of the grid stored as gridFile.
     * @param endRow The bottom row index of the grid stored as gridFile.
     * @param endCol The rightmost column index of the grid stored as gridFile.
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then swap operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_GridDouble create(
            Grids_GridDoubleStats stats,
            File directory,
            File gridFile,
            Grids_AbstractGridChunkDoubleFactory chunkFactory,
            long startRow,
            long startCol,
            long endRow,
            long endCol,
            boolean hoome) {
        return new Grids_GridDouble(getStats(stats), directory, gridFile,
                chunkFactory, ChunkNRows, ChunkNCols, startRow, startCol,
                endRow, endCol, NoDataValue, ge, hoome);
    }

    /**
     * Returns a new Grids_GridDouble with values obtained from gridFile.
     *
     * @param directory The directory to be used for storing cached Grid
     * information.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * returned.
     * @param hoome If true then OutOfMemoryErrors are caught in this method
     * then swap operations are initiated prior to retrying. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public Grids_GridDouble create(
            File directory,
            File gridFile,
            boolean hoome) {
        return new Grids_GridDouble(ge, directory, gridFile, hoome);
    }

    /////////////////////////
    // Create from a cache //
    /////////////////////////
    /**
     * Returns a new Grids_GridDouble with values obtained from gridFile.
     *
     * @param directory The Directory to be used for storing cached Grid
     * information.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     * @param hoome
     * @return
     */
    public @Override
    Grids_GridDouble create(
            File directory,
            File gridFile,
            ObjectInputStream ois,
            boolean hoome) {
        return new Grids_GridDouble(directory, gridFile, ois, ge, hoome);
    }
    
    /**
     *
     * @param stats
     * @return a new stats of the same type for use.
     */
    private Grids_GridDoubleStats getStats(Grids_GridDoubleStats stats) {
        if (stats instanceof Grids_GridDoubleStatsNotUpdated) {
            return new Grids_GridDoubleStatsNotUpdated(ge);
        } else {
            return new Grids_GridDoubleStats(ge);
        }
    }
}
