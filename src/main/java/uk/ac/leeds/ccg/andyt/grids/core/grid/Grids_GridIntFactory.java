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
import java.io.File;
import java.io.ObjectInputStream;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridIntStats;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridIntStatsNotUpdated;

/**
 * A factory for constructing Grids_GridInt instances.
 */
public class Grids_GridIntFactory extends Grids_AbstractGridFactory {

    /**
     * The NoDataValue for creating chunks.
     */
    protected int NoDataValue;

    public Grids_GridChunkIntFactory GridChunkIntFactory;
//    public Grids_GridChunkIntMapFactory ChunkIntMapFactory;
//    public Grids_GridChunkIntArrayFactory ChunkIntArrayFactory;
    public Grids_AbstractGridChunkIntFactory DefaultGridChunkIntFactory;

    public Grids_GridIntStats Stats;

    protected Grids_GridIntFactory() {
    }

    /**
     * Creates a new Grids_GridIntFactory.
     *
     * @param ge
     * @param gridChunkIntFactory
     * @param defaultGridChunkIntFactory
     * @param chunkNRows The number of rows chunks have by default.
     * @param chunkNCols The number of columns chunks have by default.
     */
    public Grids_GridIntFactory(Grids_Environment ge,
            Grids_GridChunkIntFactory gridChunkIntFactory,
            Grids_AbstractGridChunkIntFactory defaultGridChunkIntFactory,
            int chunkNRows, int chunkNCols) {
        super(ge, chunkNRows, chunkNCols, null);
        GridChunkIntFactory = gridChunkIntFactory;
        DefaultGridChunkIntFactory = defaultGridChunkIntFactory;
        Stats = new Grids_GridIntStatsNotUpdated(ge);
        NoDataValue = Integer.MIN_VALUE;
    }

    /**
     * Creates a new Grids_GridIntFactory.
     *
     * @param ge
     * @param gridChunkIntFactory
     * @param defaultGridChunkIntFactory
     * @param noDataValue
     * @param chunkNRows The number of rows chunks have by default.
     * @param chunkNCols The number of columns chunks have by default.
     * @param dimensions
     * @param stats
     */
    public Grids_GridIntFactory(Grids_Environment ge,
            Grids_GridChunkIntFactory gridChunkIntFactory,
            Grids_AbstractGridChunkIntFactory defaultGridChunkIntFactory,
            int noDataValue, int chunkNRows, int chunkNCols,
            Grids_Dimensions dimensions, Grids_GridIntStats stats) {
        super(ge, chunkNRows, chunkNCols, dimensions);
        GridChunkIntFactory = gridChunkIntFactory;
        DefaultGridChunkIntFactory = defaultGridChunkIntFactory;
        Stats = stats;
        NoDataValue = noDataValue;
    }

    /**
     * Set DefaultGridChunkIntFactory to cf.
     *
     * @param cf
     */
    public void setDefaultChunkFactory(
            Grids_AbstractGridChunkIntFactory cf) {
        DefaultGridChunkIntFactory = cf;
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
    public void setNoDataValue(int noDataValue) {
        NoDataValue = noDataValue;
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * Returns A new Grids_GridInt with all values as NoDataValues.
     *
     * @param dir The Directory to be used for storing grid.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @return
     */
    @Override
    public Grids_GridInt create(File dir, long nRows, long nCols,
            Grids_Dimensions dimensions) {
        return create(new Grids_GridIntStatsNotUpdated(env), dir,
                GridChunkIntFactory, nRows, nCols, dimensions);
    }

    /**
     * @param stats The type of Grids_GridIntStats to accompany the returned
     * grid.
     * @param dir The Directory to be used for storing grid.
     * @param cf The preferred Grids_AbstractGridChunkIntFactory for creating
     * chunks that the constructed Grid is to be made of.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @return A new Grids_GridInt grid with all values as NoDataValues.
     */
    public Grids_GridInt create(Grids_GridIntStats stats, File dir,
            Grids_AbstractGridChunkIntFactory cf, long nRows, long nCols,
            Grids_Dimensions dimensions) {
        return new Grids_GridInt(getStats(stats), dir, cf, ChunkNRows,
                ChunkNCols, nRows, nCols, dimensions, NoDataValue, env);
    }

    //////////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGridNumber //
    //////////////////////////////////////////////////////
    /**
     * @param dir The Directory to be used for storing the grid.
     * @param g The Grids_AbstractGridNumber from which values are used.
     * @param startRow The topmost row index of g.
     * @param startCol The leftmost column index of g.
     * @param endRow The bottom row index of g.
     * @param endCol The rightmost column index of g.
     * @return A new Grids_GridInt with all values taken from g.
     */
    @Override
    public Grids_GridInt create(File dir, Grids_AbstractGrid g,
            long startRow, long startCol, long endRow, long endCol) {
        return create(new Grids_GridIntStatsNotUpdated(env), dir, g,
                DefaultGridChunkIntFactory, startRow, startCol, endRow,
                endCol);
    }

    /**
     * @param stats The type of Grids_GridIntStats to accompany the returned
     * grid.
     * @param dir The directory to be used for storing the grid.
     * @param cf The preferred Grids_AbstractGridChunkIntFactory for creating
     * chunks that the constructed Grid is to be made of.
     * @param g The Grids_AbstractGridNumber from which grid values are used.
     * @param startRow The topmost row index of g.
     * @param startCol The leftmost column index of g.
     * @param endRow The bottom row index of g.
     * @param endCol The rightmost column index of g.
     * @return A new Grids_GridInt with all values taken from g.
     */
    public Grids_GridInt create(Grids_GridIntStats stats, File dir,
            Grids_AbstractGrid g, Grids_AbstractGridChunkIntFactory cf,
            long startRow, long startCol, long endRow, long endCol) {
        return new Grids_GridInt(getStats(stats), dir, g, cf, ChunkNRows,
                ChunkNCols, startRow, startCol, endRow, endCol, NoDataValue);
    }

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * @param dir The Directory to be used for storing the grid.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * constructed.
     * @param startRow The topmost row index of the grid stored as gridFile.
     * @param startCol The leftmost column index of the grid stored as gridFile.
     * @param endRow The bottom row index of the grid stored as gridFile.
     * @param endCol The rightmost column index of the grid stored as gridFile.
     * @return A new Grids_GridInt with values obtained from gridFile.
     */
    @Override
    public Grids_GridInt create(File dir, File gridFile, long startRow,
            long startCol, long endRow, long endCol) {
        return create(new Grids_GridIntStatsNotUpdated(env), dir,
                gridFile, DefaultGridChunkIntFactory, startRow, startCol,
                endRow, endCol);
    }

    /**
     * @param stats The type of Grids_GridIntStats to accompany the returned
     * grid.
     * @param dir The directory to be used for storing the grid.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * constructed.
     * @param cf The preferred factory for creating chunks that the constructed
     * Grid is to be made of.
     * @param startRow The topmost row index of the grid stored as gridFile.
     * @param startCol The leftmost column index of the grid stored as gridFile.
     * @param endRow The bottom row index of the grid stored as gridFile.
     * @param endCol The rightmost column index of the grid stored as gridFile.
     * @return A new Grids_GridInt with values obtained from gridFile.
     */
    public Grids_GridInt create(Grids_GridIntStats stats, File dir,
            File gridFile, Grids_AbstractGridChunkIntFactory cf,
            long startRow, long startCol, long endRow, long endCol) {
        return new Grids_GridInt(getStats(stats), dir, gridFile, cf,
                ChunkNRows, ChunkNCols, startRow, startCol, endRow, endCol,
                NoDataValue, env);
    }

    /**
     * @param dir The directory to be used for storing the grid.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * returned.
     * @return A new Grids_GridInt with values obtained from gridFile.
     */
    @Override
    public Grids_GridInt create(File dir, File gridFile) {
        return new Grids_GridInt(env, dir, gridFile);
    }

    /////////////////////////
    // Create from a cache //
    /////////////////////////
    /**
     * @param dir The Directory to be used for storing the grid.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     * @return A new Grids_GridInt with values obtained from gridFile.
     */
    public @Override
    Grids_GridInt create(File dir, File gridFile, ObjectInputStream ois) {
        return new Grids_GridInt(dir, gridFile, ois, env);
    }

    /**
     * @param stats
     * @return A new Grids_GridIntStats of the same type for use.
     */
    private Grids_GridIntStats getStats(Grids_GridIntStats stats) {
        if (stats instanceof Grids_GridIntStatsNotUpdated) {
            return new Grids_GridIntStatsNotUpdated(env);
        } else {
            return new Grids_GridIntStats(env);
        }
    }
}
