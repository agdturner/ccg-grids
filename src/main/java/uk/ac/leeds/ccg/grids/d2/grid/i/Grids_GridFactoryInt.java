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
package uk.ac.leeds.ccg.grids.d2.grid.i;

import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkFactoryInt;
import java.io.IOException;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridFactory;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkFactoryIntSinglet;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsInt;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsNotUpdatedInt;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.io.IO_Path;

/**
 * A factory for constructing Grids_GridInt instances.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridFactoryInt extends Grids_GridFactory {

    private static final long serialVersionUID = 1L;

    /**
     * The noDataValue for creating chunks.
     */
    protected int noDataValue;

    /**
     * gridChunkIntFactory
     */
    public Grids_ChunkFactoryIntSinglet gridChunkIntFactory;
//    public Grids_GridChunkIntMapFactory ChunkIntMapFactory;
//    public Grids_GridChunkIntArrayFactory ChunkIntArrayFactory;
    
    /**
     * defaultGridChunkIntFactory
     */
    public Grids_ChunkFactoryInt defaultGridChunkIntFactory;

    /**
     * stats
     */
    public Grids_StatsInt stats;

    /**
     * Creates a new Grids_GridIntFactory.
     *
     * @param e The grids environment.
     * @param fs The file store.
     * @param gcif gridChunkIntFactory
     * @param dgcif default gridChunkIntFactory
     * @param chunkNRows The number of rows chunks have by default.
     * @param chunkNCols The number of columns chunks have by default.
     */
    public Grids_GridFactoryInt(Grids_Environment e, IO_Cache fs,
            Grids_ChunkFactoryIntSinglet gcif,
            Grids_ChunkFactoryInt dgcif, int chunkNRows,
            int chunkNCols) {
        super(e, fs, chunkNRows, chunkNCols, null);
        gridChunkIntFactory = gcif;
        defaultGridChunkIntFactory = dgcif;
        stats = new Grids_StatsNotUpdatedInt(e);
        noDataValue = Integer.MIN_VALUE;
    }

    /**
     * Creates a new Grids_GridIntFactory.
     *
     * @param e The grids environment.
     * @param fs The file store.
     * @param gcif gridChunkIntFactory
     * @param dgcif defaultGridChunkIntFactory
     * @param ndv noDataValue
     * @param chunkNRows The number of rows chunks have by default.
     * @param chunkNCols The number of columns chunks have by default.
     * @param dim dimensions
     * @param stats stats
     */
    public Grids_GridFactoryInt(Grids_Environment e, IO_Cache fs,
            Grids_ChunkFactoryIntSinglet gcif,
            Grids_ChunkFactoryInt dgcif,
            int ndv, int chunkNRows, int chunkNCols,
            Grids_Dimensions dim, Grids_StatsInt stats) {
        super(e, fs, chunkNRows, chunkNCols, dim);
        gridChunkIntFactory = gcif;
        defaultGridChunkIntFactory = dgcif;
        this.stats = stats;
        noDataValue = ndv;
    }

    /**
     * Set defaultGridChunkIntFactory to cf.
     *
     * @param cf What {@link #defaultGridChunkIntFactory} is set to.
     */
    public void setDefaultChunkFactory(Grids_ChunkFactoryInt cf) {
        defaultGridChunkIntFactory = cf;
    }

    /**
     * @return {@link #noDataValue}.
      */
    public double getNoDataValue() {
        return noDataValue;
    }

    /**
     * Sets {@link #noDataValue} to {@code ndv}.
     *
     * @param ndv What {@link #noDataValue} is set to.
     */
    public void setNoDataValue(int ndv) {
        this.noDataValue = ndv;
    }

    /**
     * Returns A new Grids_GridInt with all values as noDataValues.
     *
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dim The xmin, ymin, xmax, ymax, cellsize.
     * @return A new Grids_GridInt with all values as noDataValues.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridInt create(long nRows, long nCols, Grids_Dimensions dim)
            throws IOException, ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedInt(env),
                gridChunkIntFactory, nRows, nCols, dim);
    }

    /**
     * @param stats The type of Grids_StatsInt to accompany the returned grid.
     * @param cf The preferred Grids_ChunkFactoryInt for creating chunks that
     * the constructed Grid is to be made of.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @return A new Grids_GridInt grid with all values as NoDataValues.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridInt create(Grids_StatsInt stats,
            Grids_ChunkFactoryInt cf, long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException,
            ClassNotFoundException, Exception {
        Grids_GridInt r = new Grids_GridInt(getStats(stats), store,
                store.getNextID(), cf, chunkNRows,
                chunkNCols, nRows, nCols, dimensions, noDataValue, env);
        //store.addDir();
        return r;
    }

    /**
     * @param g The Grids_AbstractGridNumber from which values are used.
     * @param startRow The topmost row index of g.
     * @param startCol The leftmost column index of g.
     * @param endRow The bottom row index of g.
     * @param endCol The rightmost column index of g.
     * @return A new Grids_GridInt with all values taken from g.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridInt create(Grids_Grid g, long startRow, long startCol,
            long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedInt(env), g,
                defaultGridChunkIntFactory, startRow, startCol, endRow,
                endCol);
    }

    /**
     * @param stats The type of Grids_StatsInt to accompany the returned grid.
     * @param cf The preferred Grids_ChunkFactoryInt for creating chunks that
     * the constructed Grid is to be made of.
     * @param g The Grids_AbstractGridNumber from which grid values are used.
     * @param startRow The topmost row index of g.
     * @param startCol The leftmost column index of g.
     * @param endRow The bottom row index of g.
     * @param endCol The rightmost column index of g.
     * @return A new Grids_GridInt with all values taken from g.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridInt create(Grids_StatsInt stats, Grids_Grid g,
            Grids_ChunkFactoryInt cf, long startRow, long startCol, long endRow,
            long endCol) throws IOException, ClassNotFoundException, Exception {
        Grids_GridInt r = new Grids_GridInt(getStats(stats), store,
                store.getNextID(), g, cf, chunkNRows, chunkNCols, startRow,
                startCol, endRow, endCol, noDataValue);
        //store.addDir();
        return r;
    }

    /**
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * constructed.
     * @param startRow The topmost row index of the grid stored as gridFile.
     * @param startCol The leftmost column index of the grid stored as gridFile.
     * @param endRow The bottom row index of the grid stored as gridFile.
     * @param endCol The rightmost column index of the grid stored as gridFile.
     * @return A new Grids_GridInt with values obtained from gridFile.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridInt create(IO_Path gridFile, long startRow,
            long startCol, long endRow, long endCol) throws IOException,
            ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedInt(env),
                gridFile, defaultGridChunkIntFactory, startRow, startCol,
                endRow, endCol);
    }

    /**
     * @param stats The type of Grids_StatsInt to accompany the returned grid.
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridInt create(Grids_StatsInt stats,
            IO_Path gridFile, Grids_ChunkFactoryInt cf,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        return new Grids_GridInt(getStats(stats), store, store.getNextID(), gridFile, cf,
                chunkNRows, chunkNCols, startRow, startCol, endRow, endCol,
                noDataValue, env);
    }

    /**
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * returned.
     * @return A new Grids_GridInt with values obtained from gridFile.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridInt create(IO_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridInt r = new Grids_GridInt(env, store, store.getNextID(),
                gridFile, noDataValue);
        //store.addDir();
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
