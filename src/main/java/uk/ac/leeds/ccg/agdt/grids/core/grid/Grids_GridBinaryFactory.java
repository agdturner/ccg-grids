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
package uk.ac.leeds.ccg.agdt.grids.core.grid;

import java.io.IOException;
import java.io.ObjectInputStream;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkBinaryFactory;
import uk.ac.leeds.ccg.agdt.grids.core.grid.stats.Grids_GridBinaryStats;
import uk.ac.leeds.ccg.agdt.grids.core.grid.stats.Grids_GridBinaryStatsNotUpdated;

/**
 * A factory for constructing Grids_GridBinary instances.
*
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridBinaryFactory extends Grids_AbstractGridFactory {

    public double v;
    
    public Grids_GridChunkBinaryFactory factory;

    public Grids_GridBinaryStats Stats;

    protected Grids_GridBinaryFactory(double v) {
        this.v = v;
    }

    /**
     * Creates a new Grids_GridBinaryFactory.
     *
     * @param ge
     * @param factory
     * @param chunkNRows The number of rows chunks have by default.
     * @param chunkNCols The number of columns chunks have by default.
     * @param v
     */
    public Grids_GridBinaryFactory(Grids_Environment ge,
            Grids_GridChunkBinaryFactory factory,
            int chunkNRows, int chunkNCols, double v) {
        this(ge,factory, chunkNRows, chunkNCols, null, 
                new Grids_GridBinaryStatsNotUpdated(ge), v);
//        super(ge, chunkNRows, chunkNCols, null);
//        this.factory = factory;
//        Stats = new Grids_GridBinaryStatsNotUpdated(ge);
//        this.v = v;
    }

    /**
     * Creates a new Grids_GridBinaryFactory.
     *
     * @param ge
     * @param factory
     * @param chunkNRows The number of rows chunks have by default.
     * @param chunkNCols The number of columns chunks have by default.
     * @param dimensions
     * @param stats
     * @param v
     */
    public Grids_GridBinaryFactory(Grids_Environment ge,
            Grids_GridChunkBinaryFactory factory, int chunkNRows, int chunkNCols,
            Grids_Dimensions dimensions, Grids_GridBinaryStats stats, double v) {
        super(ge, chunkNRows, chunkNCols, dimensions);
        this.factory = factory;
        Stats = stats;
        this.v = v;
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * Returns A new Grids_GridBinary with all values as NoDataValues.
     *
     * @param dir The Directory to be used for storing grid.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @return
     */
    @Override
    public Grids_GridBinary create(Generic_Path dir, long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException {
        return create(new Grids_GridBinaryStatsNotUpdated(env), dir,
                factory, nRows, nCols, dimensions);
    }

    /**
     * @param stats The type of Grids_GridBinaryStats to accompany the returned
     * grid.
     * @param dir The Directory to be used for storing grid.
     * @param cf The preferred Grids_AbstractGridChunkDoubleFactory for creating
     * chunks that the constructed Grid is to be made of.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param dimensions The xmin, ymin, xmax, ymax, cellsize.
     * @return A new Grids_GridBinary grid with all values as NoDataValues.
     */
    public Grids_GridBinary create(Grids_GridBinaryStats stats, Generic_Path dir,
            Grids_GridChunkBinaryFactory cf, long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException {
        return new Grids_GridBinary(getStats(stats), dir, cf, ChunkNRows,
                ChunkNCols, nRows, nCols, dimensions, env);
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
     * @return A new Grids_GridBinary with all values taken from g.
     */
    @Override
    public Grids_GridBinary create(Generic_Path dir, Grids_AbstractGrid g,
            long startRow, long startCol, long endRow, long endCol) throws IOException {
        return create(new Grids_GridBinaryStatsNotUpdated(env), dir, g,
                new Grids_GridChunkBinaryFactory(), startRow, startCol, endRow,
                endCol, v);
    }

    /**
     * @param stats The type of Grids_GridBinaryStats to accompany the returned
     * grid.
     * @param dir The directory to be used for storing the grid.
     * @param cf The preferred Grids_AbstractGridChunkDoubleFactory for creating
     * chunks that the constructed Grid is to be made of.
     * @param g The Grids_AbstractGridNumber from which grid values are used.
     * @param startRow The topmost row index of g.
     * @param startCol The leftmost column index of g.
     * @param endRow The bottom row index of g.
     * @param endCol The rightmost column index of g.
     * @return A new Grids_GridBinary with all values taken from g.
     */
    public Grids_GridBinary create(Grids_GridBinaryStats stats, Generic_Path dir,
            Grids_AbstractGrid g, Grids_GridChunkBinaryFactory cf,
            long startRow, long startCol, long endRow, long endCol, double v) throws IOException {
        return new Grids_GridBinary(getStats(stats), dir, g, cf, ChunkNRows,
                ChunkNCols, startRow, startCol, endRow, endCol, v);
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
     * @return A new Grids_GridBinary with values obtained from gridFile.
     */
    @Override
    public Grids_GridBinary create(Generic_Path dir, Generic_Path gridFile, long startRow,
            long startCol, long endRow, long endCol) throws IOException {
        return create(new Grids_GridBinaryStatsNotUpdated(env), dir, gridFile, 
                new Grids_GridChunkBinaryFactory(), startRow, startCol, endRow,
                endCol, v);
    }

    /**
     * @param stats The type of Grids_GridBinaryStats to accompany the returned
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
     * @return A new Grids_GridBinary with values obtained from gridFile.
     */
    public Grids_GridBinary create(Grids_GridBinaryStats stats, Generic_Path dir,
            Generic_Path gridFile, Grids_GridChunkBinaryFactory cf,
            long startRow, long startCol, long endRow, long endCol, double v) throws IOException {
        return new Grids_GridBinary(getStats(stats), dir, gridFile, cf,
                ChunkNRows, ChunkNCols, startRow, startCol, endRow, endCol,
                env, v);
    }

    /**
     * @param dir The directory to be used for storing the grid.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the grid to be
     * returned.
     * @return A new Grids_GridBinary with values obtained from gridFile.
     */
    @Override
    public Grids_GridBinary create(Generic_Path dir, Generic_Path gridFile) throws IOException {
        return new Grids_GridBinary(env, dir, gridFile, v);
    }

    /////////////////////////
    // Create from a cache //
    /////////////////////////
    /**
     * @param dir The Directory to be used for storing the grid.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     * @return A new Grids_GridBinary with values obtained from gridFile.
     */
    @Override
    public Grids_GridBinary create(Generic_Path dir, Generic_Path gridFile, 
            ObjectInputStream ois) throws IOException {
        return new Grids_GridBinary(dir, gridFile, ois, env);
    }

    /**
     * @param stats
     * @return A new Grids_GridBinaryStats of the same type for use.
     */
    private Grids_GridBinaryStats getStats(Grids_GridBinaryStats stats) {
        if (stats instanceof Grids_GridBinaryStatsNotUpdated) {
            return new Grids_GridBinaryStatsNotUpdated(env);
        } else {
            return new Grids_GridBinaryStats(env);
        }
    }
}
