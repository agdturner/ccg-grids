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
package uk.ac.leeds.ccg.grids.d2.grid.d;

import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkFactoryDouble;
import java.io.IOException;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import uk.ac.leeds.ccg.grids.d2.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkFactoryDoubleSinglet;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridFactory;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsDouble;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsNotUpdatedDouble;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_FileStore;

/**
 * A factory for constructing Grids_GridDouble instances.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridFactoryDouble extends Grids_GridFactory {

    private static final long serialVersionUID = 1L;

    /**
     * The noDataValue for creating chunks.
     */
    protected double noDataValue;

    public Grids_ChunkFactoryDoubleSinglet gridChunkDoubleFactory;
//    public Grids_GridChunkDoubleMapFactory ChunkDoubleMapFactory;
//    public Grids_GridChunkDoubleArrayFactory ChunkDoubleArrayFactory;
    public Grids_ChunkFactoryDouble defaultGridChunkDoubleFactory;

    public Grids_StatsDouble stats;

    /**
     * Creates a new Grids_GridDoubleFactory. {@link #noDataValue} is set to
     * {@code -Double.MAX_VALUE}; {@link #dim} is set to {@code null};
     * {@link #stats} is set to {@code  new Grids_StatsNotUpdatedDouble(e)}.
     *
     *
     * @param e What {@link #env} is set to.
     * @param fs What {@link #store} is set to.
     * @param gcdf What {@link #gridChunkDoubleFactory} is set to.
     * @param dgcdf What {@link #defaultGridChunkDoubleFactory} is set to.
     * @param cnr What {@link #chunkNRows} is set to.
     * @param cnc What {@link #chunkNCols} is set to.
     */
    public Grids_GridFactoryDouble(Grids_Environment e, Generic_FileStore fs,
            Grids_ChunkFactoryDoubleSinglet gcdf,
            Grids_ChunkFactoryDouble dgcdf, int cnr, int cnc) {
        this(e, fs, gcdf, dgcdf, -Double.MAX_VALUE, cnr, cnc,
                null, new Grids_StatsNotUpdatedDouble(e));
    }

    /**
     * Creates a new Grids_GridDoubleFactory.
     *
     * @param ge What {@link #env} is set to.
     * @param fs What {@link #store} is set to.
     * @param gcdf What {@link #gridChunkDoubleFactory} is set to.
     * @param dgcdf What {@link #defaultGridChunkDoubleFactory} is set to.
     * @param ndv What {@link #noDataValue} is set to.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     * @param dim What {@link #dim} is set to.
     * @param stats What {@link #stats} is set to.
     */
    public Grids_GridFactoryDouble(Grids_Environment ge, Generic_FileStore fs,
            Grids_ChunkFactoryDoubleSinglet gcdf,
            Grids_ChunkFactoryDouble dgcdf, double ndv, int chunkNRows,
            int chunkNCols, Grids_Dimensions dim, Grids_StatsDouble stats) {
        super(ge, fs, chunkNRows, chunkNCols, dim);
        gridChunkDoubleFactory = gcdf;
        defaultGridChunkDoubleFactory = dgcdf;
        this.stats = stats;
        this.noDataValue = ndv;
    }

    /**
     * For setting {@link #defaultGridChunkDoubleFactory}.
     *
     * @param cf What {@link #defaultGridChunkDoubleFactory} is set to.
     */
    public void setDefaultChunkFactory(Grids_ChunkFactoryDouble cf) {
        defaultGridChunkDoubleFactory = cf;
    }

    /**
     * @return {@link #noDataValue}
     */
    public double getNoDataValue() {
        return noDataValue;
    }

    /**
     * Sets {@link #noDataValue}.
     *
     * @param ndv What {@link #noDataValue} is set to.
     */
    public void setNoDataValue(double ndv) {
        this.noDataValue = ndv;
    }

    /**
     * Creates a new Grids_GridDouble with all values set to
     * {@link #noDataValue} and with {@link #stats} that are not updated and
     * with chunks made using {@link #gridChunkDoubleFactory}.
     *
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The dimensions (xmin, ymin, xmax, ymax, cellsize) of
     * the grid to be created.
     * @return A new Grids_GridDouble with all values set to
     * {@link #noDataValue} and with {@link #stats} that are not updated and
     * with chunks made using {@link #gridChunkDoubleFactory}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridDouble create(long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException,
            ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedDouble(env),
                gridChunkDoubleFactory, nRows, nCols, dimensions);
    }

    /**
     * Creates a new Grids_GridDouble with all values set to
     * {@link #noDataValue}.
     *
     * @param stats The type of Grids_StatsDouble to accompany the returned
     * grid.
     * @param cf The Grids_ChunkFactoryDouble for creating chunks that the
     * constructed Grid is to be made of.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @return A new Grids_GridDouble with all values set to
     * {@link #noDataValue}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble create(Grids_StatsDouble stats,
            Grids_ChunkFactoryDouble cf, long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException,
            ClassNotFoundException, Exception {
        Grids_GridDouble r = new Grids_GridDouble(getStats(stats), store,
                store.getNextID(), cf, chunkNRows,
                chunkNCols, nRows, nCols, dimensions, noDataValue, env);
        store.addDir();
        return r;
    }

    /**
     * Creates a new Grids_GridDouble with values set from {@code #g}. The stats
     * for the grid are not updated and the
     * {@link #defaultGridChunkDoubleFactory} is used to create chunks.
     *
     * @param g The grid used to set the values of the grid created.
     * @param startRow The start row index of {@code #g}.
     * @param startCol The start column index of {@code #g}.
     * @param endRow The end row index of {@code #g}.
     * @param endCol The end column index of {@code #g}.
     * @return A new Grids_GridDouble with all values set from {@code #g}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridDouble create(Grids_Grid g,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedDouble(env), g,
                defaultGridChunkDoubleFactory, startRow, startCol, endRow,
                endCol);
    }

    /**
     * Creates a new Grids_GridDouble with values set from {@code #g}.
     *
     * @param stats The type of Grids_StatsDouble to accompany the created grid.
     * @param g The grid used to set the values of the grid created.
     * @param cf The chunk factory for creating chunks.
     * @param startRow The start row index of {@code #g}.
     * @param startCol The start column index of {@code #g}.
     * @param endRow The end row index of {@code #g}.
     * @param endCol The end column index of {@code #g}.
     * @return A new Grids_GridDouble with all values set from {@code #g}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble create(Grids_StatsDouble stats,
            Grids_Grid g, Grids_ChunkFactoryDouble cf,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridDouble r = new Grids_GridDouble(getStats(stats), store,
                store.getNextID(), g, cf, chunkNRows,
                chunkNCols, startRow, startCol, endRow, endCol, noDataValue);
        store.addDir();
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
     * @return A new Grids_GridDouble with values obtained from gridFile.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridDouble create(Generic_Path gridFile, long startRow,
            long startCol, long endRow, long endCol) throws IOException,
            ClassNotFoundException, Exception {
        return create(new Grids_StatsNotUpdatedDouble(env),
                gridFile, defaultGridChunkDoubleFactory, startRow, startCol,
                endRow, endCol);
    }

    /**
     * @param stats The type of Grids_StatsDouble to accompany the returned
     * grid.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * constructed.
     * @param cf The preferred factory for creating chunks that the constructed
     * Grid is to be made of.
     * @param startRow The topmost row index of the grid stored as gridFile.
     * @param startCol The leftmost column index of the grid stored as gridFile.
     * @param endRow The bottom row index of the grid stored as gridFile.
     * @param endCol The rightmost column index of the grid stored as gridFile.
     * @return A new Grids_GridDouble with values obtained from gridFile.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridDouble create(Grids_StatsDouble stats,
            Generic_Path gridFile, Grids_ChunkFactoryDouble cf,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridDouble r = new Grids_GridDouble(getStats(stats), store,
                store.getNextID(), gridFile, cf, chunkNRows, chunkNCols,
                startRow, startCol, endRow, endCol, noDataValue, env);
        store.addDir();
        return r;
    }

    /**
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * returned.
     * @return A new Grids_GridDouble with values obtained from gridFile.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_GridDouble create(Generic_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        Grids_GridDouble r = new Grids_GridDouble(env, store, store.getNextID(),
                gridFile, noDataValue);
        store.addDir();
        return r;
    }

    /**
     * For duplicating stats.
     * 
     * @param stats What is to be duplicated.
     * @return A new Grids_StatsDouble of the same type for use.
     */
    private Grids_StatsDouble getStats(Grids_StatsDouble stats) {
        if (stats instanceof Grids_StatsNotUpdatedDouble) {
            return new Grids_StatsNotUpdatedDouble(env);
        } else {
            return new Grids_StatsDouble(env);
        }
    }
}
