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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Object;

/**
 * Grids_AbstractGridFactory.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_AbstractGridFactory extends Grids_Object {

    private static final long serialVersionUID = 1L;

    /**
     * The number of rows in a chunk.
     */
    protected int ChunkNRows;

    /**
     * The number of columns in a chunk.
     */
    protected int ChunkNCols;

    /**
     * The dimensions of the grid.
     */
    protected Grids_Dimensions Dimensions;

    /**
     * Creates a new Grids_AbstractGridFactory.
     */
    public Grids_AbstractGridFactory() {
    }

    /**
     * Creates a new Grids_AbstractGridFactory.
     *
     * @param e What {@link #env} is set to.
     */
    public Grids_AbstractGridFactory(Grids_Environment e) {
        super(e);
    }

    /**
     * Creates a new Grids_AbstractGridFactory.
     *
     * @param e What {@link #env} is set to.
     * @param chunkNRows What {@link #ChunkNRows} is set to.
     * @param chunkNCols What {@link #ChunkNCols} is set to.
     * @param dimensions What {@link #Dimensions} is set to.
     */
    public Grids_AbstractGridFactory(Grids_Environment e, int chunkNRows,
            int chunkNCols, Grids_Dimensions dimensions) {
        super(e);
        ChunkNRows = chunkNRows;
        ChunkNCols = chunkNCols;
        Dimensions = dimensions;
    }

    /**
     * Set Dimensions.
     *
     * @param d What {@link #Dimensions} is set to.
     */
    public void setDimensions(Grids_Dimensions d) {
        Dimensions = d;
    }

    /**
     * @return Grid with all values as false or NoDataValues.
     * @param dir The directory for storing the grid.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @throws java.io.IOException If encountered.
     */
    public Grids_AbstractGrid create(Generic_Path dir, long nRows, long nCols)
            throws IOException {
        setDimensions(nRows, nCols);
        return create(dir, nRows, nCols, Dimensions);
    }

    /**
     * @return Grid with all values as false or NoDataValues.
     * @param dir The Directory for storing the grid.
     * @param nRows The number of rows in the grid.
     * @param nCols The number of columns in the grid.
     * @param d The dimensions for the grid created.
     * @throws java.io.IOException If encountered.
     */
    public abstract Grids_AbstractGrid create(Generic_Path dir, long nRows,
            long nCols, Grids_Dimensions d) throws IOException;

    ////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGrid //
    ////////////////////////////////////////////////
    /**
     * @return Grid with all values from g.
     * @param dir The directory for storing the grid.
     * @param g The grid from which values are obtained.
     * @throws java.io.IOException If encountered.
     */
    public Grids_AbstractGrid create(Generic_Path dir, Grids_AbstractGrid g)
            throws IOException {
        return create(dir, g, 0L, 0L, g.getNRows() - 1L, g.getNCols() - 1L);
    }

    /**
     * @return Grid with all values from g.
     * @param dir The directory for storing the grid.
     * @param g The grid from which values are obtained.
     * @param startRow The topmost row index of {@code g} to get values from.
     * @param startCol The leftmost column index of {@code g} to get values
     * from.
     * @param endRow The bottommost row index of {@code g} to get values from.
     * @param endCol The rightmost column index of {@code g} to get values from.
     * @throws java.io.IOException If encountered.
     */
    public abstract Grids_AbstractGrid create(Generic_Path dir,
            Grids_AbstractGrid g, long startRow, long startCol, long endRow,
            long endCol) throws IOException;

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * @param dir The directory for storing the grid.
     * @return Grid with values obtained from gridFile. If {@code gf} is a
     * directory then there will be an attempt to load a grid from a file
     * therein.
     * @param gridFile either a directory, or a formatted file used to
     * initialise the grid returned.
     * @throws java.io.IOException If encountered.
     */
    public abstract Grids_AbstractGrid create(Generic_Path dir,
            Generic_Path gridFile) throws IOException;

    /**
     * @return A grid with values obtained from gridFile.
     * @param dir The directory to be used for storing the grid.
     * @param gridFile either a directory, or a formatted file used to
     * initialise the grid returned.
     * @param startRow The topmost row index of the grid in {@code gridFile} to
     * get values from.
     * @param startCol The leftmost column index of grid in {@code gridFile} to
     * get values from.
     * @param endRow The bottommost row index of the grid in {@code gridFile} to
     * get values from.
     * @param endCol The rightmost column index of the grid in {@code gridFile}
     * to get values from.
     * @throws java.io.IOException If encountered.
     */
    public abstract Grids_AbstractGrid create(File dir, File gridFile,
            long startRow, long startCol, long endRow, long endCol)
            throws IOException;

    /**
     * @return Grids_AbstractGridNumber with values obtained from gridFile.
     * @param dir The Directory to be used for storing the grid.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     */
    public abstract Grids_AbstractGrid create(File dir, File gridFile,
            ObjectInputStream ois);

    /**
     * @return A copy of {@link #ChunkNRows}.
     */
    public int getChunkNRows() {
        return ChunkNRows;
    }

    /**
     * Sets {@link #ChunkNRows}.
     *
     * @param chunkNRows The value to set {@link #ChunkNRows} to.
     */
    public void setChunkNRows(int chunkNRows) {
        ChunkNRows = chunkNRows;
    }

    /**
     * @return A copy of {@link #getChunkNCols}.
     */
    public int getChunkNCols() {
        return ChunkNCols;
    }

    /**
     * Sets {@link #ChunkNCols}.
     *
     * @param chunkNCols The value to set {@link #ChunkNCols} to.
     */
    public void setChunkNCols(int chunkNCols) {
        ChunkNCols = chunkNCols;
    }

    /**
     * @return {@link #Dimensions}
     */
    public Grids_Dimensions getDimensions() {
        return Dimensions;
    }

    /**
     * Initialises {@link #Dimensions}. {@link Grids_Dimensions#XMin} and
     * {@link Grids_Dimensions#XMin} are set to {@link BigDecimal#ZERO};
     * {@link Grids_Dimensions#Cellsize} is set to {@link BigDecimal#ONE};
     * {@link Grids_Dimensions#XMax} is set to {@code nCols} using
     * {@link BigDecimal#BigDecimal(int)}; {@link Grids_Dimensions#YMax} is set
     * to {@code nRows} using {@link BigDecimal#BigDecimal(int)}
     *
     * @param nRows The number of rows in the grids to be created.
     * @param nCols The number of columns in the grids to be created.
     */
    protected void setDimensions(long nRows, long nCols) {
        Dimensions = new Grids_Dimensions(BigDecimal.ZERO,
                BigDecimal.valueOf(nRows), BigDecimal.ZERO,
                BigDecimal.valueOf(nCols), BigDecimal.ONE);
    }
}
