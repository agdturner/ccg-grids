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
import uk.ac.leeds.ccg.generic.io.Generic_Path;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridFactory;
import uk.ac.leeds.ccg.grids.d2.chunk.b.Grids_ChunkFactoryBoolean;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsBoolean;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsNotUpdatedBoolean;
import uk.ac.leeds.ccg.generic.io.Generic_FileStore;

/**
 * A factory for constructing
 * {{@link uk.ac.leeds.ccg.grids.d2.grid.b.Grids_GridBoolean} instances.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridFactoryBoolean extends Grids_GridFactory {

    private static final long serialVersionUID = 1L;

    public Grids_ChunkFactoryBoolean factory;

    public Grids_StatsBoolean stats;

    /**
     * {@link #dim} is set to {@code null}. {@link #stats} is set to
     * {@code new Grids_StatsNotUpdatedBoolean(e)}.
     *
     * @param e What {@link #env} is set to.
     * @param fs What {@link #store} is set to.
     * @param factory What {@link #factory} is set to.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     */
    public Grids_GridFactoryBoolean(Grids_Environment e, Generic_FileStore fs,
            Grids_ChunkFactoryBoolean factory, int chunkNRows, int chunkNCols) {
        this(e, fs, factory, chunkNRows, chunkNCols, null,
                new Grids_StatsNotUpdatedBoolean(e));
    }

    /**
     * @param e What {@link #env} is set to.
     * @param fs What {@link #store} is set to.
     * @param factory What {@link #factory} is set to.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     * @param dimensions What {@link #dim} is set to.
     * @param stats What {@link #stats} is set to.
     */
    public Grids_GridFactoryBoolean(Grids_Environment e, Generic_FileStore fs,
            Grids_ChunkFactoryBoolean factory, int chunkNRows, int chunkNCols,
            Grids_Dimensions dimensions, Grids_StatsBoolean stats) {
        super(e, fs, chunkNRows, chunkNCols, dimensions);
        this.factory = factory;
        this.stats = stats;
    }

    /**
     * Create a grid with all values set to {@code null}.
     *
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The dimensions of the grid.
     * @return A grid with all values set to {@code null}.
     * @throws java.io.IOException If encountered.
     */
    @Override
    public Grids_GridBoolean create(long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException, Exception {
        return create(new Grids_StatsNotUpdatedBoolean(env), factory, nRows,
                nCols, dimensions);
    }

    /**
     * Create a grid with all values set to {@code null}.
     *
     * @param stats The type of Grids_StatsBoolean to accompany the returned
     * grid.
     * @param cf The preferred factory for creating chunks in the grid.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The dimensions of the grid.
     * @return A grid with all values set to {@code null}.
     * @throws java.io.IOException If encountered.
     */
    public Grids_GridBoolean create(Grids_StatsBoolean stats,
            Grids_ChunkFactoryBoolean cf, long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException, Exception {
        Grids_GridBoolean r = new Grids_GridBoolean(getStats(stats),
                store, store.getNextID(), cf, chunkNRows,
                chunkNCols, nRows, nCols, dimensions, env);
        store.addDir();
        return r;
    }

    /**
     * Create a Grids_GridBoolean from {@code g}. {@link #stats} is set to
     * {@code new Grids_StatsNotUpdatedBoolean(env)}.
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
     * @return A new Grids_GridBoolean with all values taken from {@code g}.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridBoolean create(Grids_Grid g,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedBoolean(env), g,
                new Grids_ChunkFactoryBoolean(), startRow, startCol, endRow,
                endCol);
    }

    /**
     * Create a {@link Grids_GridBoolean} from {@code g}.
     *
     * @param stats The type of Grids_StatsBoolean to accompany the returned
     * grid.
     * @param cf The preferred {@link Grids_ChunkFactoryBoolean} for creating
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
     * @return A new {@link Grids_GridBoolean} with all values taken from
     * {@code g}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridBoolean create(Grids_StatsBoolean stats,
            Grids_Grid g, Grids_ChunkFactoryBoolean cf, long startRow,
            long startCol, long endRow, long endCol) throws IOException,
            ClassNotFoundException, Exception {
        Grids_GridBoolean r = new Grids_GridBoolean(getStats(stats),
                store, store.getNextID(), g, cf, chunkNRows,
                chunkNCols, startRow, startCol, endRow, endCol);
        store.addDir();
        return r;
    }

    /**
     * {@link #stats} is set to {@code null} {@link #factory} is set to
     * {@code new Grids_ChunkFactoryBoolean()}.
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
     * @return A new {@link Grids_GridBoolean} with all values taken from the
     * cached grid in {@code gridFile}.
     *
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridBoolean create(Generic_Path gridFile,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedBoolean(env), gridFile,
                new Grids_ChunkFactoryBoolean(), startRow, startCol, endRow,
                endCol);
    }

    /**
     * @param stats The type of stats in the returned grid.
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
     * @return A new {@link Grids_GridBoolean} with all values taken from the
     * cached grid in {@code gridFile}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridBoolean create(Grids_StatsBoolean stats,
            Generic_Path gridFile, Grids_ChunkFactoryBoolean cf,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridBoolean r = new Grids_GridBoolean(getStats(stats),
                store, store.getNextID(), gridFile, cf,
                chunkNRows, chunkNCols, startRow, startCol, endRow, endCol,
                env);
        store.addDir();
        return r;
    }

    /**
     * @param gridFile The directory containing a cached grid that will be used
     * to create the grid returned.
     * @return A new Grids_GridBoolean with values obtained from gridFile.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridBoolean create(Generic_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridBoolean r = new Grids_GridBoolean(env, store,
                store.getNextID(), gridFile);
        store.addDir();
        return r;
    }

    /**
     * @param stats
     * @return A new Grids_StatsBoolean of the same type for use.
     */
    private Grids_StatsBoolean getStats(Grids_StatsBoolean stats) {
        if (stats instanceof Grids_StatsNotUpdatedBoolean) {
            return new Grids_StatsNotUpdatedBoolean(env);
        } else {
            return new Grids_StatsBoolean(env);
        }
    }
}
