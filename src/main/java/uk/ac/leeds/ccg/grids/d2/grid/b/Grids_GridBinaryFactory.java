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
package uk.ac.leeds.ccg.grids.d2.grid.b;

import java.io.IOException;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridFactory;
import uk.ac.leeds.ccg.grids.d2.chunk.b.Grids_ChunkBinaryFactory;
import uk.ac.leeds.ccg.grids.d2.chunk.b.Grids_ChunkBinaryFactorySinglet;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.io.IO_Path;

/**
 * A factory for constructing
 * {{@link uk.ac.leeds.ccg.grids.d2.grid.b.Grids_GridBinary} instances.
 *
 * @author Andy Turner
 * @version 1.0
 */
public class Grids_GridBinaryFactory extends Grids_GridFactory {

    private static final long serialVersionUID = 1L;

    /**
     * Chunk factory.
     */
    public Grids_ChunkBinaryFactory factory;

    /**
     * Stats
     */
    public Grids_GridBinaryStats stats;

    /**
     * gridChunkBinaryFactory
     */
    public Grids_ChunkBinaryFactorySinglet gridChunkBinaryFactory;
//    public Grids_GridChunkBinaryMapFactory ChunkBinaryMapFactory;
//    public Grids_GridChunkBinaryArrayFactory ChunkBinaryArrayFactory;
    
    /**
     * defaultGridChunkBinaryFactory
     */
    public Grids_ChunkBinaryFactory defaultGridChunkBinaryFactory;
    
    /**
     * {@link #dim} is set to {@code null}. {@link #stats} is set to
     * {@code new Grids_GridBinaryStatsNotUpdated(e)}.
     *
     * @param e What {@link #env} is set to.
     * @param store What {@link #store} is set to.
     * @param factory What {@link #factory} is set to.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     */
    public Grids_GridBinaryFactory(Grids_Environment e, IO_Cache store,
            Grids_ChunkBinaryFactory factory, int chunkNRows, int chunkNCols) {
        this(e, store, factory, chunkNRows, chunkNCols, null,
                new Grids_GridBinaryStatsNotUpdated(e));
    }

    /**
     * @param e What {@link #env} is set to.
     * @param store What {@link #store} is set to.
     * @param cf What {@link #factory} is set to.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     * @param dimensions What {@link #dim} is set to.
     * @param stats What {@link #stats} is set to.
     */
    public Grids_GridBinaryFactory(Grids_Environment e, IO_Cache store,
            Grids_ChunkBinaryFactory cf, int chunkNRows, int chunkNCols,
            Grids_Dimensions dimensions, Grids_GridBinaryStats stats) {
        super(e, store, chunkNRows, chunkNCols, dimensions);
        this.factory = cf;
        this.stats = stats;
    }

    /**
     * Create a grid with all values set to {@code false}.
     *
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The dimensions of the grid.
     * @return A grid with all values set to {@code false}.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_GridBinary create(long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException, Exception {
        return create(new Grids_GridBinaryStatsNotUpdated(env), factory, nRows,
                nCols, dimensions);
    }

    /**
     * Create a grid with all values set to {@code false}.
     *
     * @param stats The type of Grids_StatsBinary to accompany the returned
     * grid.
     * @param cf The preferred factory for creating chunks in the grid.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The dimensions of the grid.
     * @return A grid with all values set to {@code false}.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     */
    public Grids_GridBinary create(Grids_GridBinaryStats stats,
            Grids_ChunkBinaryFactory cf, long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException, Exception {
        Grids_GridBinary r = new Grids_GridBinary(getStats(stats),
                store, store.getNextID(), cf, chunkNRows,
                chunkNCols, nRows, nCols, dimensions, env);
        //store.addDir();
        return r;
    }

    /**
     * Create a {@link Grids_GridBinary} from {@code g}.
     *
     * {@link #stats} is set to {@code new Grids_GridBinaryStatsNotUpdated(env)}.
     *
     * @param g The grid from which values are used to set the values in the
     * created grid.
     * @param startRow The topmost row index of {@code g} for which the values
     * are used to set the values in the created grid.
     * @param startCol The leftmost column index of {@code g} for which the
     * values are used to set the values in the created grid.
     * @param endRow The bottom row index of {@code g} for which the values are
     * used to set the values in the created grid.
     * @param endCol The rightmost column index of of {@code g} for which the
     * values are used to set the values in the created grid.
     * @return A new {@link Grids_GridBinary} with all values taken from
     * {@code g}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridBinary create(Grids_Grid g,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        return create(new Grids_GridBinaryStatsNotUpdated(env), g,
                defaultGridChunkBinaryFactory, startRow, startCol, endRow,
                endCol);
    }

    /**
     * Create a {@link Grids_GridBinary} from {@code g}.
     *
     * @param stats The type of Grids_StatsBinary to accompany the returned
     * grid.
     * @param cf The preferred {@link Grids_ChunkBinaryFactory} for creating
     * chunks that the constructed grid is to be made of.
     * @param g The grid from which values are used to set the values in the
     * created grid.
     * @param startRow The topmost row index of {@code g} for which the values
     * are used to set the values in the created grid.
     * @param startCol The leftmost column index of {@code g} for which the
     * values are used to set the values in the created grid.
     * @param endRow The bottom row index of {@code g} for which the values are
     * used to set the values in the created grid.
     * @param endCol The rightmost column index of of {@code g} for which the
     * values are used to set the values in the created grid.
     * @return A new {@link Grids_GridBinary} with all values taken from
     * {@code g}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridBinary create(Grids_GridBinaryStats stats,
            Grids_Grid g, Grids_ChunkBinaryFactory cf, long startRow,
            long startCol, long endRow, long endCol) throws IOException,
            ClassNotFoundException, Exception {
        Grids_GridBinary r = new Grids_GridBinary(getStats(stats),
                store, store.getNextID(), g, cf, chunkNRows,
                chunkNCols, startRow, startCol, endRow, endCol);
        //store.addDir();
        return r;
    }

    /**
     * {@link #stats} is set to {@code new Grids_GridBinaryStatsNotUpdated(env)}.
     *
     * @param gridFile The directory containing a cached grid that will be used
     * to create the grid returned.
     * @param startRow The topmost row index of {@code g} for which the values
     * are used to set the values in the created grid.
     * @param startCol The leftmost column index of {@code g} for which the
     * values are used to set the values in the created grid.
     * @param endRow The bottom row index of {@code g} for which the values are
     * used to set the values in the created grid.
     * @param endCol The rightmost column index of of {@code g} for which the
     * values are used to set the values in the created grid.
     * @return A new {@link Grids_GridBinary} with all values taken from the
     * cached grid in {@code gridFile}.
     *
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridBinary create(IO_Path gridFile,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridBinary r = create(new Grids_GridBinaryStatsNotUpdated(env),
                gridFile, defaultGridChunkBinaryFactory, startRow, startCol,
                endRow, endCol);
        //store.addDir();
        return r;
    }

    /**
     * @param stats A {@link Grids_StatsBinary} which is effectively duplicated
     * but becomes the stats in the returned grid.
     * @param gridFile The directory containing a cached grid that will be used
     * to create the grid returned.
     * @param cf The preferred factory for creating chunks that the constructed
     * Grid is to be made of.
     * @param startRow The topmost row index of {@code g} for which the values
     * are used to set the values in the created grid.
     * @param startCol The leftmost column index of {@code g} for which the
     * values are used to set the values in the created grid.
     * @param endRow The bottom row index of {@code g} for which the values are
     * used to set the values in the created grid.
     * @param endCol The rightmost column index of of {@code g} for which the
     * values are used to set the values in the created grid.
     * @return A new {@link Grids_GridBinary} with all values taken from the
     * cached grid in {@code gridFile}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridBinary create(Grids_GridBinaryStats stats,
            IO_Path gridFile, Grids_ChunkBinaryFactory cf,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridBinary r = new Grids_GridBinary(getStats(stats), store,
                store.getNextID(), gridFile, cf, chunkNRows, chunkNCols,
                startRow, startCol, endRow, endCol, env);
        //store.addDir();
        return r;
    }

    /**
     * @param gridFile The directory containing a cached grid that will be used
     * to create the grid returned.
     * @return A new Grids_GridBinary with values obtained from gridFile.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridBinary create(IO_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        return new Grids_GridBinary(env, store, store.getNextID(), gridFile);
    }

    /**
     * @param stats
     * @return A new Grids_StatsBinary of the same type for use.
     */
    private Grids_GridBinaryStats getStats(Grids_GridBinaryStats stats) {
        if (stats instanceof Grids_GridBinaryStatsNotUpdated) {
            return new Grids_GridBinaryStatsNotUpdated(env);
        } else {
            return new Grids_GridBinaryStats(env);
        }
    }
}
