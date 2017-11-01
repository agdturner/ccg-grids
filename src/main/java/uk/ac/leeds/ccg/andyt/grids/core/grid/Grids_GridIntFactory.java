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
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArrayFactory;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_AbstractGridStatistics;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics1;
import java.io.File;
import java.io.ObjectInputStream;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics0;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;

/**
 * A factory for constructing Grid2DSquareCellInt instances.
 */
public class Grids_GridIntFactory
        extends Grids_AbstractGridFactory {

    /**
     * The Grid2DSquareCellChunkAbstractFactory for creating chunks.
     */
    protected Grids_AbstractGridChunkIntFactory ChunkFactory;

    /**
     * Creates a new Grid2DSquareCellDoubleFactory Defaults: Directory to a new
     * one in System.getProperties( "java.io.tmpdir" );
     *
     * @param ge
     * @param handleOutOfMemoryError
     */
    public Grids_GridIntFactory(
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        this(Grids_Files.createTempFile(),
                ge,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory
     *
     *
     *
     * @param directory A "workspace Directory" for storing temporary files and
     * swapping Grid2DSquareCellDouble data to. Defaults: ChunkNRows to 64
     * _ChunkNColss to 64 Grid2DSquareCellDoubleChunkFactory to
     * grid2DSquareCellDoubleChunkArray
     * @param ge
     * @param handleOutOfMemoryError
     */
    public Grids_GridIntFactory(
            File directory,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        this(directory,
                64,
                64,
                new Grids_GridChunkIntArrayFactory(),
                ge,
                handleOutOfMemoryError);
    }

    /**
     * Creates a new Grid2DSquareCellDoubleFactory
     *
     * @param directory A "workspace Directory" for storing temporary files and
     * caching Grid2DSquareCellIntAbstract data.
     * @param chunkNRows The number of rows chunks have by default.
     * @param gcf The Grids_AbstractGridChunkIntFactory for creating
     * Grid2DSquareCellDoubleChunks
     * @param chunkNCols The number of columns chunks have by default.
     * @param ge
     * @param handleOutOfMemoryError
     */
    public Grids_GridIntFactory(
            File directory,
            int chunkNRows,
            int chunkNCols,
            Grids_AbstractGridChunkIntFactory gcf,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        super(ge);
        this.Directory = directory;
        this.ChunkNRows = chunkNRows;
        this.ChunkNCols = chunkNCols;
        initDimensions(chunkNCols, chunkNRows);
        this.ChunkFactory = gcf;
        this.ge = ge;
        this.setGridStatistics(new Grids_GridStatistics1(ge));
        this.HandleOutOfMemoryError = handleOutOfMemoryError;
    }

    /**
     * Returns a reference to this.ChunkFactory.
     *
     * @return
     */
    public Grids_AbstractGridChunkIntFactory getChunkFactory() {
        return this.ChunkFactory;
    }

    /**
     * Sets this.ChunkFactory to ChunkFactory.
     *
     * @param chunkFactory
     */
    public void setChunkFactory(
            Grids_AbstractGridChunkIntFactory chunkFactory) {
        this.ChunkFactory = chunkFactory;
    }

    /////////////////////////
    // Create from scratch //
    /////////////////////////
    /**
     * Returns a new Grid2DSquareCellInt grid with all values as noDataValues.
     *
     *
     *
     *
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param nrows the Grid2DSquareCellInt nrows.
     * @param ncols the Grid2DSquareCellInt ncols.
     * @param dimensions
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public Grids_GridInt create(
            File directory,
            long nrows,
            long ncols,
            Grids_Dimensions dimensions,
            boolean handleOutOfMemoryError) {
        return create(
                getGridStatistics(),
                directory,
                this.ChunkFactory,
                nrows,
                ncols,
                dimensions,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grid2DSquareCellInt grid with all values as noDataValues.
     *
     * @param gridStatistics The AbstractGridStatistics to accompany the
     * returned grid.
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param grid2DSquareCellIntChunkFactory The
     * Grids_AbstractGridChunkIntFactory for creating chunks.
     * @param nrows The Grid2DSquareCellInt nrows.
     * @param ncols The Grid2DSquareCellInt ncols.
     * @param dimensions
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_GridInt create(
            Grids_AbstractGridStatistics gridStatistics,
            File directory,
            Grids_AbstractGridChunkIntFactory grid2DSquareCellIntChunkFactory,
            long nrows,
            long ncols,
            Grids_Dimensions dimensions,
            boolean handleOutOfMemoryError) {
        return new Grids_GridInt(
                gridStatistics,
                directory,
                grid2DSquareCellIntChunkFactory,
                this.ChunkNRows,
                this.ChunkNCols,
                nrows,
                ncols,
                dimensions,
                ge,
                handleOutOfMemoryError);
    }

    //////////////////////////////////////////////////////
    // Create from an existing Grids_AbstractGridNumber //
    //////////////////////////////////////////////////////
    /**
     * Returns a new Grid2DSquareCellInt with values obtained from
     * grid2DSquareCell.
     *
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param g The Grids_AbstractGridNumber from which values are obtained.
     * @param startRowIndex The topmost row index of grid2DSquareCell thats
     * values are used.
     * @param startColIndex The leftmost column index of grid2DSquareCell thats
     * values are used.
     * @param endRowIndex The bottom row index of the grid2DSquareCell thats
     * values are used.
     * @param endColIndex The rightmost column index of grid2DSquareCell thats
     * values are used.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public Grids_GridInt create(
            File directory,
            Grids_AbstractGridNumber g,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        return create(getGridStatistics(),
                directory,
                g,
                this.ChunkFactory,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grid2DSquareCellInt with values obtained from
     * grid2DSquareCell.
     *
     * @param gridStatistics The AbstractGridStatistics for the returned
     * Grid2DSquareCellInt.
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param g The Grids_AbstractGridNumber from which values are obtained.
     * @param gcf The Grids_AbstractGridChunkIntFactory used to construct the
     * chunks.
     * @param startRowIndex The topmost row index of grid2DSquareCell thats
     * values are used.
     * @param startColIndex The leftmost column index of grid2DSquareCell thats
     * values are used.
     * @param endRowIndex The bottom row index of the grid2DSquareCell thats
     * values are used.
     * @param endColIndex The rightmost column index of grid2DSquareCell thats
     * values are used.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_GridInt create(
            Grids_AbstractGridStatistics gridStatistics,
            File directory,
            Grids_AbstractGridNumber g,
            Grids_AbstractGridChunkIntFactory gcf,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        return new Grids_GridInt(
                gridStatistics,
                directory,
                g,
                gcf,
                this.ChunkNRows,
                this.ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                ge,
                handleOutOfMemoryError);
    }

    ////////////////////////
    // Create from a File //
    ////////////////////////
    /**
     * Returns a new Grid2DSquareCellInt with values obtained from gridFile.
     *
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param gridFile either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grid2DSquareCellInt to be returned.
     * @param startRowIndex The topmost row index of the grid represented in
     * gridFile thats values are used.
     * @param startColIndex The leftmost column index of the grid represented in
     * gridFile thats values are used.
     * @param endRowIndex The bottom row index of the grid represented in
     * gridFile thats values are used.
     * @param endColIndex The rightmost column index of the grid represented in
     * gridFile thats values are used.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public Grids_GridInt create(
            File directory,
            File gridFile,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        return create(
                new Grids_GridStatistics0(ge),
                directory,
                gridFile,
                this.ChunkFactory,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                handleOutOfMemoryError);
    }

    /**
     * Returns a new Grids_AbstractGridNumber with values obtained from
     * gridFile.
     *
     * @param gridStatistics
     * @param directory The Directory to be used for storing cached
     * Grid2DSquareCellInt information.
     * @param gridFile either a Directory, or a formatted File with a specific
     * extension containing the data and information about the
     * Grids_AbstractGridNumber to be returned.
     * @param gcf The Grids_AbstractGridChunkIntFactory used to construct the
     * chunks.
     * @param startRowIndex The topmost row index of the grid represented in
     * gridFile thats values are used.
     * @param startColIndex The leftmost column index of the grid represented in
     * gridFile thats values are used.
     * @param endRowIndex The bottom row index of the grid represented in
     * gridFile thats values are used.
     * @param endColIndex The rightmost column index of the grid represented in
     * gridFile thats values are used.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_GridInt create(
            Grids_AbstractGridStatistics gridStatistics,
            File directory,
            File gridFile,
            Grids_AbstractGridChunkIntFactory gcf,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            boolean handleOutOfMemoryError) {
        return new Grids_GridInt(
                gridStatistics,
                directory,
                gridFile,
                gcf,
                this.ChunkNRows,
                this.ChunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                ge,
                handleOutOfMemoryError);
    }

    /////////////////////////
    // Create from a cache //
    /////////////////////////
    /**
     * Returns a new Grid2DSquareCellInt with values obtained from gridFile.
     *
     * @param Directory The Directory for swapping to file.
     * @param gridFile A file containing the data to be used in construction.
     * @param ois The ObjectInputStream to construct from.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught
     * in this method then swap operations are initiated prior to retrying. If
     * false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    @Override
    public Grids_GridInt create(
            File Directory,
            File gridFile,
            ObjectInputStream ois,
            boolean handleOutOfMemoryError) {
        return new Grids_GridInt(
                Directory,
                gridFile,
                ois,
                ge,
                handleOutOfMemoryError);
    }
}
