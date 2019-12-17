/*
 * Copyright 2019 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.agdturner.grids.d2.grid.i;

import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkFactoryInt;
import java.io.IOException;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import io.github.agdturner.grids.core.Grids_Dimensions;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.d2.grid.Grids_Grid;
import io.github.agdturner.grids.d2.grid.Grids_GridFactory;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkFactoryIntSinglet;
import io.github.agdturner.grids.d2.stats.Grids_StatsInt;
import io.github.agdturner.grids.d2.stats.Grids_StatsNotUpdatedInt;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_FileStore;

/**
 * A factory for constructing Grids_GridInt instances.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridFactoryInt extends Grids_GridFactory {

    /**
     * The NoDataValue for creating chunks.
     */
    protected int NoDataValue;

    public Grids_ChunkFactoryIntSinglet GridChunkIntFactory;
//    public Grids_GridChunkIntMapFactory ChunkIntMapFactory;
//    public Grids_GridChunkIntArrayFactory ChunkIntArrayFactory;
    public Grids_ChunkFactoryInt DefaultGridChunkIntFactory;

    public Grids_StatsInt Stats;

    /**
     * Creates a new Grids_GridIntFactory.
     *
     * @param ge
     * @param gcif gridChunkIntFactory
     * @param dgcif default GridChunkIntFactory
     * @param chunkNRows The number of rows chunks have by default.
     * @param chunkNCols The number of columns chunks have by default.
     */
    public Grids_GridFactoryInt(Grids_Environment ge, Generic_FileStore store,
            Grids_ChunkFactoryIntSinglet gcif,
            Grids_ChunkFactoryInt dgcif, int chunkNRows,
            int chunkNCols) {
        super(ge, store, chunkNRows, chunkNCols, null);
        GridChunkIntFactory = gcif;
        DefaultGridChunkIntFactory = dgcif;
        Stats = new Grids_StatsNotUpdatedInt(ge);
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
    public Grids_GridFactoryInt(Grids_Environment ge, Generic_FileStore store,
            Grids_ChunkFactoryIntSinglet gridChunkIntFactory,
            Grids_ChunkFactoryInt defaultGridChunkIntFactory,
            int noDataValue, int chunkNRows, int chunkNCols,
            Grids_Dimensions dimensions, Grids_StatsInt stats) {
        super(ge, store, chunkNRows, chunkNCols, dimensions);
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
            Grids_ChunkFactoryInt cf) {
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

    /**
     * Returns A new Grids_GridInt with all values as NoDataValues.
     *
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @return
     */
    @Override
    public Grids_GridInt create(long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException, ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedInt(env),
                GridChunkIntFactory, nRows, nCols, dimensions);
    }

    /**
     * @param stats The type of Grids_StatsInt to accompany the returned grid.
     * @param dir The Directory to be used for storing grid.
     * @param cf The preferred Grids_ChunkFactoryInt for creating chunks that
     * the constructed Grid is to be made of.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @return A new Grids_GridInt grid with all values as NoDataValues.
     */
    public Grids_GridInt create(Grids_StatsInt stats, 
            Grids_ChunkFactoryInt cf, long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException, 
            ClassNotFoundException, Exception {
        Grids_GridInt r = new Grids_GridInt(getStats(stats), store,
                store.getNextID(), cf, ChunkNRows,
                ChunkNCols, nRows, nCols, dimensions, NoDataValue, env);
        store.addDir();
        return r;
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
    public Grids_GridInt create(Grids_Grid g,
            long startRow, long startCol, long endRow, long endCol) 
            throws IOException, ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedInt(env), g,
                DefaultGridChunkIntFactory, startRow, startCol, endRow,
                endCol);
    }

    /**
     * @param stats The type of Grids_StatsInt to accompany the returned grid.
     * @param dir The directory to be used for storing the grid.
     * @param cf The preferred Grids_ChunkFactoryInt for creating chunks that
     * the constructed Grid is to be made of.
     * @param g The Grids_AbstractGridNumber from which grid values are used.
     * @param startRow The topmost row index of g.
     * @param startCol The leftmost column index of g.
     * @param endRow The bottom row index of g.
     * @param endCol The rightmost column index of g.
     * @return A new Grids_GridInt with all values taken from g.
     */
    public Grids_GridInt create(Grids_StatsInt stats, Grids_Grid g,
            Grids_ChunkFactoryInt cf, long startRow, long startCol, long endRow,
            long endCol) throws IOException, ClassNotFoundException, Exception {
        Grids_GridInt r = new Grids_GridInt(getStats(stats), store,
                store.getNextID(), g, cf, ChunkNRows, ChunkNCols, startRow, 
                startCol, endRow, endCol, NoDataValue);
        store.addDir();
        return r;
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
    public Grids_GridInt create(Generic_Path gridFile, long startRow,
            long startCol, long endRow, long endCol) throws IOException,
            ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedInt(env),
                gridFile, DefaultGridChunkIntFactory, startRow, startCol,
                endRow, endCol);
    }

    /**
     * @param stats The type of Grids_StatsInt to accompany the returned grid.
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
    public Grids_GridInt create(Grids_StatsInt stats,
            Generic_Path gridFile, Grids_ChunkFactoryInt cf,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        return new Grids_GridInt(getStats(stats), store, store.getNextID(), gridFile, cf,
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
    public Grids_GridInt create(Generic_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridInt r = new Grids_GridInt(env, store, store.getNextID(), gridFile);
        store.addDir();
        return r;
    }

    /**
     * @param stats
     * @return A new Grids_StatsInt of the same type for use.
     */
    private Grids_StatsInt getStats(Grids_StatsInt stats) {
        if (stats instanceof Grids_StatsNotUpdatedInt) {
            return new Grids_StatsNotUpdatedInt(env);
        } else {
            return new Grids_StatsInt(env);
        }
    }
}
