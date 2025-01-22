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
package uk.ac.leeds.ccg.grids.d2.grid;

import ch.obermuhlner.math.big.BigRational;
import java.io.IOException;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Object;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.io.IO_Path;

/**
 * Grids_GridFactory.
 *
 * @author Andy Turner
 * @version 1.0
 */
public abstract class Grids_GridFactory extends Grids_Object {

    private static final long serialVersionUID = 1L;

    /**
     * The file store in which grids can be cached.
     */
    protected final IO_Cache store;

    /**
     * The number of rows in a chunk.
     */
    protected int chunkNRows;

    /**
     * The number of columns in a chunk.
     */
    protected int chunkNCols;

    /**
     * The dimensions of the grid.
     */
    protected Grids_Dimensions dim;

    /**
     * Creates a new grid factory.
     *
     * @param e What {@link #env} is set to.
     * @param fs What {@link #store} is set to.
     */
    public Grids_GridFactory(Grids_Environment e, IO_Cache fs) {
        super(e);
        this.store = fs;
    }

    /**
     * Creates a new grid factory.
     *
     * @param e What {@link #env} is set to.
     * @param fs What {@link #store} is set to.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     * @param dim What {@link #dim} is set to.
     */
    public Grids_GridFactory(Grids_Environment e, IO_Cache fs,
            int chunkNRows, int chunkNCols, Grids_Dimensions dim) {
        super(e);
        this.store = fs;
        this.chunkNRows = chunkNRows;
        this.chunkNCols = chunkNCols;
        this.dim = dim;
    }

    /**
     * Set dimensions.
     *
     * @param d What {@link #dim} is set to.
     */
    public void setDimensions(Grids_Dimensions d) {
        dim = d;
    }

    /**
     * @return Grid with all values as false or NoDataValues.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_Grid create(long nRows, long nCols)
            throws IOException, ClassNotFoundException, Exception {
        setDimensions(nRows, nCols);
        return create(nRows, nCols, dim);
    }

    /**
     * @return Grid with all values as false or NoDataValues.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param d The dimensions for the grid created.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public abstract Grids_Grid create(long nRows,
            long nCols, Grids_Dimensions d) throws IOException,
            ClassNotFoundException, Exception;

    /**
     * @return Grid with all values from g.
     * @param g The grid from which values are obtained.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_Grid create(Grids_Grid g)
            throws IOException, ClassNotFoundException, Exception {
        return create(g, 0L, 0L, g.getNRows() - 1, g.getNCols() - 1);
    }

    /**
     * @return Grid with all values from g.
     * @param g The grid from which values are obtained.
     * @param startRow The topmost row index of {@code g} to get values from.
     * @param startCol The leftmost column index of {@code g} to get values
     * from.
     * @param endRow The bottommost row index of {@code g} to get values from.
     * @param endCol The rightmost column index of {@code g} to get values from.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public abstract Grids_Grid create(Grids_Grid g,
            long startRow, long startCol, long endRow, long endCol) 
            throws IOException, ClassNotFoundException, Exception;

    /**
     * @return Grid with values obtained from gridFile. If {@code gf} is a
     * directory then there will be an attempt to load a grid from a file
     * therein.
     * @param gridFile either a directory, or a formatted file used to
     * initialise the grid returned.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public abstract Grids_Grid create(IO_Path gridFile)
            throws IOException, ClassNotFoundException, Exception;

    /**
     * @return A grid with values obtained from gridFile.
     * @param gridFile either a directory, or a formatted file used to
     * initialise the grid returned.
     * @param startRow The start row of the grid in {@code gridFile} to
     * get values from.
     * @param startCol The start column of the grid in {@code gridFile} to
     * get values from.
     * @param endRow The end row of the grid in {@code gridFile} to
     * get values from.
     * @param endCol The end column of the grid in {@code gridFile}
     * to get values from.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public abstract Grids_Grid create(IO_Path gridFile,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException, ClassNotFoundException, Exception;

    /**
     * @return A copy of {@link #chunkNRows}.
     */
    public int getChunkNRows() {
        return chunkNRows;
    }

    /**
     * Sets {@link #chunkNRows}.
     *
     * @param chunkNRows The value to set {@link #chunkNRows} to.
     */
    public void setChunkNRows(int chunkNRows) {
        this.chunkNRows = chunkNRows;
    }

    /**
     * @return A copy of {@link #getChunkNCols}.
     */
    public int getChunkNCols() {
        return chunkNCols;
    }

    /**
     * Sets {@link #chunkNCols}.
     *
     * @param chunkNCols The value to set {@link #chunkNCols} to.
     */
    public void setChunkNCols(int chunkNCols) {
        this.chunkNCols = chunkNCols;
    }

    /**
     * @return {@link #dim}
     */
    public Grids_Dimensions getDimensions() {
        return dim;
    }

    /**
     * Initialises {@link #dim}.
     * 
     * @param nRows The number of rows in the grids to be created.
     * @param nCols The number of columns in the grids to be created.
     */
    protected void setDimensions(long nRows, long nCols) {
        dim = new Grids_Dimensions(BigRational.ZERO,
                BigRational.valueOf(nCols), BigRational.ZERO,
                BigRational.valueOf(nRows), BigRational.ONE);
    }
}
