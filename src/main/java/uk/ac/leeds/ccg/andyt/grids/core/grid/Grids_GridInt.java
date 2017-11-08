/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_GridDoubleStatisticsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_GridIntStatistics;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_GridIntStatisticsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * A class for representing grids of int values.
 *
 * @see Grids_AbstractGridNumber
 */
public class Grids_GridInt
        extends Grids_AbstractGridNumber
        implements Serializable {

    /**
     * For storing the NODATA value of the grid, which by default is
     * Integer.MIN_VALUE. Care should be taken so that NoDataValue is not a data
     * value.
     */
    protected int NoDataValue = Integer.MIN_VALUE;

    /**
     * A reference to the grid Statistics Object.
     */
    protected Grids_GridIntStatistics Statistics;

    protected Grids_GridInt() {
    }

    /**
     * Creates a new Grids_GridInt. Warning!! Concurrent modification may occur
     * if directory is in use.
     *
     * @param directory The directory to be used for swapping.
     * @param gridFile The directory containing the file named "thisFile" that
     * the ois was constructed from.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param ge
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected Grids_GridInt(
            File directory,
            File gridFile,
            ObjectInputStream ois,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        super(ge, directory);
        init(gridFile, ois, handleOutOfMemoryError);
    }

    /**
     * Creates a new Grids_GridInt with each cell value equal to NoDataValue and
     * all chunks of the same type.
     *
     * @param gs The AbstractGridStatistics to accompany this.
     * @param directory The File _Directory to be used for swapping.
     * @param chunkFactory The Grids_AbstractGridChunkIntFactory preferred for
     * creating chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param noDataValue The NoDataValue.
     * @param ge
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected Grids_GridInt(
            Grids_GridIntStatistics gs,
            File directory,
            Grids_AbstractGridChunkIntFactory chunkFactory,
            int chunkNRows,
            int chunkNCols,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            int noDataValue,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        super(ge, directory);
        init(gs, directory, chunkFactory, chunkNRows, chunkNCols,
                nRows, nCols, dimensions, noDataValue, handleOutOfMemoryError);
    }

    /**
     * Creates a new Grids_GridInt based on values in grid.
     *
     * @param gs The AbstractGridStatistics to accompany this.
     * @param directory The File _Directory to be used for swapping.
     * @param grid The Grids_AbstractGridNumber from which this is to be
     * constructed.
     * @param chunkFactory The Grids_AbstractGridChunkIntFactory preferred to
     * construct chunks of this.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param startRowIndex The Grid2DSquareCell row index which is the bottom
     * most row of this.
     * @param startColIndex The Grid2DSquareCell column index which is the left
     * most column of this.
     * @param endRowIndex The Grid2DSquareCell row index which is the top most
     * row of this.
     * @param endColIndex The Grid2DSquareCell column index which is the right
     * most column of this.
     * @param noDataValue The NoDataValue for this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected Grids_GridInt(
            Grids_GridIntStatistics gs,
            File directory,
            Grids_AbstractGridNumber grid,
            Grids_AbstractGridChunkIntFactory chunkFactory,
            int chunkNRows,
            int chunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            int noDataValue,
            boolean handleOutOfMemoryError) {
        super(grid.ge, directory);
        init(gs, grid, chunkFactory, chunkNRows, chunkNCols,
                startRowIndex, startColIndex, endRowIndex, endColIndex,
                noDataValue, handleOutOfMemoryError);
    }

    /**
     * Creates a new Grids_GridInt with values obtained from gridFile. Currently
     * gridFile must be a directory of a Grids_GridDouble or Grids_GridInt or a
     * ESRI Asciigrid format file with a filename ending ".asc" or ".txt".
     *
     * @param gs The AbstractGridStatistics to accompany this.
     * @param directory The directory to be used for swapping.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the Grids_GridInt to
     * be returned.
     * @param chunkFactory The Grids_AbstractGridChunkIntFactory preferred to
     * construct chunks of this.
     * @param chunkNRows
     * @param startRowIndex The Grid2DSquareCell row index which is the bottom
     * most row of this.
     * @param chunkNCols
     * @param startColIndex The Grid2DSquareCell column index which is the left
     * most column of this.
     * @param endRowIndex The Grid2DSquareCell row index which is the top most
     * row of this.
     * @param endColIndex The Grid2DSquareCell column index which is the right
     * most column of this.
     * @param noDataValue The NoDataValue for this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param ge
     */
    protected Grids_GridInt(
            Grids_GridIntStatistics gs,
            File directory,
            File gridFile,
            Grids_AbstractGridChunkIntFactory chunkFactory,
            int chunkNRows,
            int chunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            int noDataValue,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        super(ge, directory);
        init(gs,
                gridFile,
                chunkFactory,
                chunkNRows,
                chunkNCols,
                startRowIndex,
                startColIndex,
                endRowIndex,
                endColIndex,
                noDataValue,
                handleOutOfMemoryError);
    }

    /**
     * @return a string description of the instance. Basically the values of
     * each field.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public String toString(boolean handleOutOfMemoryError) {
        try {
            String result = getClass().getName()
                    + "(NoDataValue(" + NoDataValue + "), "
                    + super.toString(handleOutOfMemoryError) + ")";
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return toString(
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridInt from which the fields of this are set.
     * @param initTransientFields Iff true then transient fields of this are set
     * with those of g.
     */
    private void init(
            Grids_GridInt g,
            boolean initTransientFields) {
        NoDataValue = g.NoDataValue;
        super.init(g);
        if (initTransientFields) {
            ChunkIDChunkMap = g.ChunkIDChunkMap;
            // Set the reference to this in the Grid Statistics
            Statistics.init(this);
        }
        ge.addGrid(this);
    }

    /**
     * Initialises this.
     *
     * @param directory The File _Directory to be used for swapping.
     * @param gridFile The File _Directory containing the File named thisFile
     * that the ois was constructed from.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param _AbstractGrid2DSquareCell_HashSet A HashSet of swappable
     * Grids_AbstractGridNumber instances.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    private void init(
            File file,
            ObjectInputStream ois,
            boolean handleOutOfMemoryError) {
        try {
            File thisFile = new File(
                    file,
                    "thisFile");
            try {
                boolean initTransientFields = false;
                init((Grids_GridInt) ois.readObject(),
                        initTransientFields);
                ois.close();
                // Set the reference to this in the Grid Chunks
                initChunks(file);
                Iterator<Grids_AbstractGridChunk> chunkIterator;
                chunkIterator = ChunkIDChunkMap.values().iterator();
                while (chunkIterator.hasNext()) {
                    Grids_AbstractGridChunk chunk = chunkIterator.next();
                    chunk.setGrid(this);
                }
            } catch (ClassCastException e) {
                try {
                    ois.close();
                    ois = Generic_StaticIO.getObjectInputStream(thisFile);
                    // If the object is a Grids_GridDouble
                    Grids_GridDoubleFactory gdf;
                    gdf = new Grids_GridDoubleFactory(
                            ge,
                            ge.getFiles().getGeneratedGridDoubleFactoryDir(),
                            Integer.MIN_VALUE,
                            ChunkNRows,
                            ChunkNCols,
                            Dimensions,
                            new Grids_GridDoubleStatisticsNotUpdated(ge),
                            ge.getProcessor().GridChunkDoubleArrayFactory);
                    Grids_GridDouble gridDouble;
                    gridDouble = (Grids_GridDouble) gdf.create(
                            Directory,
                            file,
                            ois,
                            handleOutOfMemoryError);
                    Grids_GridIntFactory gif = new Grids_GridIntFactory(
                            ge,
                            Directory,
                            (int) gridDouble.NoDataValue,
                            gridDouble.ChunkNRows,
                            gridDouble.ChunkNCols,
                            gridDouble.Dimensions,
                            Statistics,
                            ge.getProcessor().GridChunkIntArrayFactory);
                    Grids_GridInt gridInt = (Grids_GridInt) gif.create(gridDouble);
                    boolean initTransientFields = false;
                    init(gridInt, initTransientFields);
                    initChunks(file);
                } catch (IOException ioe) {
                    //ioe.printStackTrace();
                    System.err.println(ioe.getLocalizedMessage());
                }
            } catch (ClassNotFoundException | IOException e) {
                //ioe.printStackTrace();
                System.err.println(e.getLocalizedMessage());
            }
            //ioe.printStackTrace();
            // Set the reference to this in the Grid Statistics
            getStatistics().init(this);
            ge.addGrid(this);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunks_Account(false) < 1L) {
                    throw e;
                }
                init(file,
                        ois,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises this.
     *
     * @param statistics The AbstractGridStatistics to accompany this.
     * @param directory The File _Directory to be used for swapping.
     * @param grid2DSquareCellIntChunkFactory The
     * Grids_AbstractGridChunkIntFactory prefered for creating chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param _Dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param noDataValue The NoDataValue.
     * @param _AbstractGrid2DSquareCell_HashSet A HashSet of swappable
     * Grids_AbstractGrid instances.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    private void init(
            Grids_GridIntStatistics statistics,
            File directory,
            Grids_AbstractGridChunkIntFactory chunkFactory,
            int chunkNRows,
            int chunkNCols,
            long nRows,
            long nCols,
            Grids_Dimensions dimensions,
            int noDataValue,
            boolean handleOutOfMemoryError) {
        try {
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            Directory = directory;
            Statistics = statistics;
            Statistics.init(this);
            Directory = directory;
            ChunkNRows = chunkNRows;
            ChunkNCols = chunkNCols;
            NRows = nRows;
            NCols = nCols;
            Dimensions = dimensions;
            initNoDataValue(noDataValue);
            Name = directory.getName();
            initNChunkRows();
            initNChunkCols();
            ChunkIDChunkMap = new TreeMap<>();
            int chunkRow;
            int chunkCol;
            boolean isLoadedChunk = false;
            int int_0 = 0;
            Grids_2D_ID_int chunkID;
            Grids_AbstractGridChunkInt chunk;
            for (chunkRow = int_0; chunkRow < NChunkRows; chunkRow++) {
                for (chunkCol = int_0; chunkCol < NChunkCols; chunkCol++) {
                    do {
                        try {
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                            // Try to load chunk.
                            chunkID = new Grids_2D_ID_int(
                                    chunkRow,
                                    chunkCol);
                            chunk = chunkFactory.createGridChunkInt(
                                    this,
                                    chunkID);
                            ChunkIDChunkMap.put(
                                    chunkID,
                                    chunk);
                            isLoadedChunk = true;
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                        } catch (OutOfMemoryError e) {
                            if (handleOutOfMemoryError) {
                                ge.clearMemoryReserve();
                                freeSomeMemoryAndResetReserve(chunkRow, chunkCol, e);
                            } else {
                                throw e;
                            }
                        }
                    } while (!isLoadedChunk);
                    isLoadedChunk = false;
                    //loadedChunkCount++;
                }
                System.out.println("Done chunkRow " + chunkRow + " out of " + NChunkRows);
            }
            ge.addGrid(this);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(e);
                init(statistics,
                        directory,
                        chunkFactory,
                        chunkNRows,
                        chunkNCols,
                        nRows,
                        nCols,
                        dimensions,
                        noDataValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises this.
     *
     * @param statistics The AbstractGridStatistics to accompany this.
     * @param directory gridStatistics File _Directory to be used for swapping.
     * @param g The Grids_AbstractGridNumber from which this is to be
     * constructed.
     * @param chunkFactory The Grids_AbstractGridChunkIntFactory prefered to
     * construct chunks of this.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param startRowIndex The Grid2DSquareCell row index which is the bottom
     * most row of this.
     * @param startColIndex The Grid2DSquareCell column index which is the left
     * most column of this.
     * @param endRowIndex The Grid2DSquareCell row index which is the top most
     * row of this.
     * @param endColIndex The Grid2DSquareCell column index which is the right
     * most column of this.
     * @param noDataValue The NoDataValue for this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    private void init(
            Grids_GridIntStatistics statistics,
            Grids_AbstractGridNumber g,
            Grids_AbstractGridChunkIntFactory chunkFactory,
            int chunkNRows,
            int chunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            int noDataValue,
            boolean handleOutOfMemoryError) {
        try {
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            Statistics = statistics;
            statistics.init(this);
            ChunkNRows = chunkNRows;
            ChunkNCols = chunkNCols;
            NRows = endRowIndex - startRowIndex + 1L;
            NCols = endColIndex - startColIndex + 1L;
            NoDataValue = noDataValue;
            Name = Directory.getName();
            initNChunkRows();
            initNChunkCols();
            ChunkIDChunkMap = new TreeMap<>();
            initDimensions(g, startRowIndex, startColIndex, handleOutOfMemoryError);
            int chunkRow;
            int chunkCol;
            boolean isLoadedChunk = false;
            int cellRow;
            int cellCol;
            long row;
            long col;
            Grids_2D_ID_int chunkID;
            Grids_AbstractGridChunkInt chunk;
            int gridChunkNRows;
            int gridChunkNCols;
            long rowIndex;
            long colIndex;
            int startChunkRow;
            startChunkRow = g.getChunkRow(startRowIndex);
            int endChunkRow;
            endChunkRow = g.getChunkRow(endRowIndex);
            int nChunkRows;
            nChunkRows = endChunkRow - startChunkRow + 1;
            int startChunkCol;
            startChunkCol = g.getChunkCol(startColIndex);
            int endChunkCol;
            endChunkCol = g.getChunkCol(endColIndex);
            if (g instanceof Grids_GridDouble) {
                Grids_GridDouble grid = (Grids_GridDouble) g;
                double gNoDataValue = grid.getNoDataValue(handleOutOfMemoryError);
                double gValue;
                for (chunkRow = startChunkRow; chunkRow <= endChunkRow; chunkRow++) {
                    for (chunkCol = startChunkCol; chunkCol <= endChunkCol; chunkCol++) {
                        do {
                            try {
                                ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                                // Try to load chunk.
                                chunkID = new Grids_2D_ID_int(
                                        chunkRow,
                                        chunkCol);
                                ge.addToNotToSwapData(g, chunkID);
                                // Initialise chunk if it does not exist
                                if (!ChunkIDChunkMap.containsKey(chunkID)) {
                                    chunk = chunkFactory.createGridChunkInt(
                                            this,
                                            chunkID);
                                    ChunkIDChunkMap.put(
                                            chunkID,
                                            chunk);
                                }
                                gridChunkNRows = g.getChunkNRows(chunkRow, handleOutOfMemoryError);
                                gridChunkNCols = g.getChunkNCols(chunkCol, handleOutOfMemoryError);
                                for (cellRow = 0; cellRow < gridChunkNRows; cellRow++) {
                                    rowIndex = g.getRow(chunkRow, cellRow, chunkID, handleOutOfMemoryError);
                                    row = rowIndex - startRowIndex;
                                    if (rowIndex >= startRowIndex && rowIndex <= endRowIndex) {
                                        for (cellCol = 0; cellCol < gridChunkNCols; cellCol++) {
                                            colIndex = g.getCellCol(chunkCol, cellCol, chunkID, handleOutOfMemoryError);
                                            col = colIndex - startColIndex;
                                            if (colIndex >= startColIndex && colIndex <= endColIndex) {
                                                gValue = grid.getCell(
                                                        rowIndex,
                                                        colIndex);
                                                // Initialise value
                                                if (gValue == gNoDataValue) {
                                                    initCell(
                                                            row,
                                                            col,
                                                            noDataValue);
                                                } else {
                                                    if (!Double.isNaN(gValue) && Double.isFinite(gValue)) {
                                                        initCell(
                                                                row,
                                                                col,
                                                                (int) gValue);
                                                    } else {
                                                        initCell(
                                                                row,
                                                                col,
                                                                noDataValue);
                                                    }
                                                }
                                                col++;
                                            }
                                        }
                                        row++;
                                    }
                                }
                                isLoadedChunk = true;
                                ge.removeFromNotToSwapData(g, chunkID);
                                ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                            } catch (OutOfMemoryError e) {
                                if (handleOutOfMemoryError) {
                                    ge.clearMemoryReserve();
                                    freeSomeMemoryAndResetReserve(e);
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRow,
                                            chunkCol);
                                    if (ge.swapChunksExcept_Account(this, false) < 1L) {
                                        if (ge.swapChunksExcept_Account(this, chunkID, false) < 1L) {
                                            throw e;
                                        }
                                    }
                                    ge.initMemoryReserve(
                                            this,
                                            chunkID,
                                            handleOutOfMemoryError);
                                } else {
                                    throw e;
                                }
                            }
                        } while (!isLoadedChunk);
                        isLoadedChunk = false;
                        //loadedChunkCount++;
                        //cci1 = _ChunkCol;
                    }
                    System.out.println("Done chunkRow " + chunkRow + " out of " + nChunkRows);
                    chunkRow++;
                }
            } else {
                Grids_GridInt grid = (Grids_GridInt) g;
                int gNoDataValue = grid.getNoDataValue(handleOutOfMemoryError);
                int gValue;
                for (chunkRow = startChunkRow; chunkRow <= endChunkRow; chunkRow++) {
                    for (chunkCol = startChunkCol; chunkCol <= endChunkCol; chunkCol++) {
                        do {
                            try {
                                ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                                // Try to load chunk.
                                chunkID = new Grids_2D_ID_int(
                                        chunkRow,
                                        chunkCol);
                                ge.addToNotToSwapData(g, chunkID);
                                gridChunkNRows = g.getChunkNRows(chunkID, handleOutOfMemoryError);
                                gridChunkNCols = g.getChunkNCols(chunkID, handleOutOfMemoryError);
                                for (cellRow = 0; cellRow < gridChunkNRows; cellRow++) {
                                    rowIndex = g.getRow(chunkRow, cellRow, chunkID, handleOutOfMemoryError);
                                    row = rowIndex - startRowIndex;
                                    if (rowIndex >= startRowIndex && rowIndex <= endRowIndex) {
                                        for (cellCol = 0; cellCol < gridChunkNCols; cellCol++) {
                                            colIndex = g.getCellCol(chunkCol, cellCol, chunkID, handleOutOfMemoryError);
                                            col = colIndex - startColIndex;
                                            if (colIndex >= startColIndex && colIndex <= endColIndex) {
                                                // Initialise chunk if it does not exist
                                                if (!ChunkIDChunkMap.containsKey(chunkID)) {
                                                    chunk = chunkFactory.createGridChunkInt(
                                                            this,
                                                            chunkID);
                                                    ChunkIDChunkMap.put(
                                                            chunkID,
                                                            chunk);
                                                }
                                                gValue = grid.getCell(
                                                        rowIndex,
                                                        colIndex);
                                                // Initialise value
                                                if (gValue == gNoDataValue) {
                                                    initCell(
                                                            row,
                                                            col,
                                                            noDataValue);
                                                } else {
                                                    initCell(
                                                            row,
                                                            col,
                                                            gValue);
                                                }
                                                col++;
                                            }
                                        }
                                        row++;
                                    }
                                }
                                ge.removeFromNotToSwapData(g, chunkID);
                                ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                                isLoadedChunk = true;
                            } catch (OutOfMemoryError e) {
                                if (handleOutOfMemoryError) {
                                    ge.clearMemoryReserve();
                                    chunkID = new Grids_2D_ID_int(
                                            chunkRow,
                                            chunkCol);
                                    if (ge.swapChunksExcept_Account(this, chunkID, false) < 1L) { // Should also not swap out the chunk of grid thats values are being used to initialise this.
                                        throw e;
                                    }
                                    ge.initMemoryReserve(this, chunkID, handleOutOfMemoryError);
                                } else {
                                    throw e;
                                }
                            }
                        } while (!isLoadedChunk);
                        isLoadedChunk = false;
                        //loadedChunkCount++;
                        //cci1 = _ChunkCol;
                    }
                    System.out.println("Done chunkRow " + chunkRow + " out of " + nChunkRows);
                    chunkRow++;
                }
            }
            ge.addGrid(this);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunks_Account(false) < 1) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                init(statistics,
                        g,
                        chunkFactory,
                        chunkNRows,
                        chunkNCols,
                        startRowIndex,
                        startColIndex,
                        endRowIndex,
                        endColIndex,
                        noDataValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises this.
     *
     * @param statistics The AbstractGridStatistics to accompany this.
     * @param directory The File _Directory to be used for swapping.
     * @param gridFile Either a _Directory, or a formatted File with a specific
     * extension containing the data and information about the Grids_GridInt to
     * be returned.
     * @param chunkFactory The Grids_AbstractGridChunkIntFactory preferred to
     * construct chunks of this.
     * @param chunkNRows The Grids_GridInt _ChunkNRows.
     * @param chunkNCols The Grids_GridInt _ChunkNCols.
     * @param startRowIndex The topmost row index of the grid stored as
     * gridFile.
     * @param startColIndex The leftmost column index of the grid stored as
     * gridFile.
     * @param endRowIndex The bottom row index of the grid stored as gridFile.
     * @param endColIndex The rightmost column index of the grid stored as
     * gridFile.
     * @param noDataValue The NoDataValue for this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    private void init(
            Grids_GridIntStatistics statistics,
            File gridFile,
            Grids_AbstractGridChunkIntFactory chunkFactory,
            int chunkNRows,
            int chunkNCols,
            long startRowIndex,
            long startColIndex,
            long endRowIndex,
            long endColIndex,
            int noDataValue,
            boolean handleOutOfMemoryError) {
        ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        Statistics = statistics;
        statistics.init(this);
        // Set to report every 10%
        int reportN;
        reportN = (int) (endRowIndex - startRowIndex) / 10;
        if (gridFile.isDirectory()) {
            if (true) {
                Grids_GridIntFactory gf;
                gf = new Grids_GridIntFactory(
                        ge,
                        ge.getFiles().getGeneratedGridIntFactoryDir(),
                        noDataValue,
                        chunkNRows,
                        chunkNCols,
                        new Grids_Dimensions(NChunkRows * chunkNRows, NChunkCols * chunkNRows),
                        statistics,
                        chunkFactory);
                File thisFile = new File(
                        gridFile,
                        "thisFile");
                ObjectInputStream ois;
                ois = Generic_StaticIO.getObjectInputStream(thisFile);
                Grids_GridInt g;
                g = (Grids_GridInt) gf.create(
                        Directory,
                        thisFile,
                        ois,
                        handleOutOfMemoryError);
                Grids_GridInt g2;
                g2 = gf.create(
                        Directory,
                        g,
                        startRowIndex,
                        startColIndex,
                        endRowIndex,
                        endColIndex,
                        handleOutOfMemoryError);
                init(
                        g2,
                        false);

            }
            initChunks(gridFile);
        } else {
            // Assume ESRI AsciiFile
            ChunkNRows = chunkNRows;
            ChunkNCols = chunkNCols;
            NRows = endRowIndex - startRowIndex + 1L;
            NCols = endColIndex - startColIndex + 1L;
            initNoDataValue(noDataValue);
            Name = Directory.getName();
            initNChunkRows();
            initNChunkCols();
            ChunkIDChunkMap = new TreeMap<>();
            Statistics = statistics;
            Statistics.init(this);
            String filename = gridFile.getName();
            int value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(
                        gridFile,
                        ge);
                Object[] header = eagi.readHeaderObject();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                initDimensions(header, startRowIndex, startColIndex);
                int gridFileNoDataValue = (Integer) header[5];
                long row;
                long col;
                Grids_AbstractGridChunkInt chunk;
                Grids_GridChunkInt gridChunk;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if ((int) gridFileNoDataValue == NoDataValue) {
                    if (statistics.getClass().getName().equalsIgnoreCase(Grids_GridIntStatistics.class.getName())) {
                        for (row = (NRows - 1); row > -1; row--) {
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                            ge.initNotToSwapData();
                            for (col = 0; col < NCols; col++) {
                                value = eagi.readInt();
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                        }
                    } else {
                        for (row = (NRows - 1); row > -1; row--) {
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                            ge.initNotToSwapData();
                            for (col = 0; col < NCols; col++) {
                                value = eagi.readInt();
                                if (value == gridFileNoDataValue) {
                                    value = NoDataValue;
                                }
                                initCell(row, col, value, true);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                        }
                    }
                } else {
                    if (statistics.getClass().getName().equalsIgnoreCase(Grids_GridIntStatistics.class.getName())) {
                        for (row = (NRows - 1); row > -1; row--) {
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                            ge.initNotToSwapData();
                            for (col = 0; col < NCols; col++) {
                                value = eagi.readInt();
                                if (value == gridFileNoDataValue) {
                                    value = NoDataValue;
                                }
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                        }
                    } else {
                        for (row = (NRows - 1); row > -1; row--) {
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                            ge.initNotToSwapData();
                            for (col = 0; col < NCols; col++) {
                                value = eagi.readInt();
                                initCell(row, col, value, true);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param row
     * @param col
     * @param value
     * @param fast
     */
    private void initCell(long row, long col, int value, boolean fast) {
        Grids_AbstractGridChunkInt chunk;
        Grids_GridChunkInt gridChunk;
        int chunkRow;
        int chunkCol;
        Grids_2D_ID_int chunkID;
        chunkRow = getChunkRow(row);
        chunkCol = getChunkCol(col);
        chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
        /**
         * Ensure this chunkID is not swapped and initialise it if it does not
         * already exist.
         */
        ge.addToNotToSwapData(this, chunkID);
        if (!ChunkIDChunkMap.containsKey(chunkID)) {
            gridChunk = new Grids_GridChunkInt(this, chunkID, value);
            ChunkIDChunkMap.put(chunkID, gridChunk);
        } else {
            Grids_AbstractGridChunk c;
            c = ChunkIDChunkMap.get(chunkID);
            if (c == null) {
                loadIntoCacheChunk(chunkID, ge.HandleOutOfMemoryError);
            }
            chunk = (Grids_AbstractGridChunkInt) ChunkIDChunkMap.get(chunkID);
            if (chunk instanceof Grids_GridChunkInt) {
                gridChunk = (Grids_GridChunkInt) chunk;
                if (value != gridChunk.Value) {
                    // Convert chunk to another type
                    chunk = ge.getProcessor().GridChunkIntFactory.createGridChunkInt(
                            chunk,
                            chunkID);
                    chunk.setCell(chunkRow, chunkCol, value, ge.HandleOutOfMemoryError);
                    ChunkIDChunkMap.put(chunkID, chunk);
                }
            } else {
                if (fast) {
                    initCellFast(row, col, value);
                } else {
                    initCell(row, col, value);
                }
            }
        }
    }

    /**
     * @return Grids_AbstractGridChunkInt for the given chunkID.
     * @param chunkID
     */
    @Override
    protected Grids_AbstractGridChunkInt getGridChunk(
            Grids_2D_ID_int chunkID
    ) {
        boolean containsKey;
        boolean isInGrid = isInGrid(chunkID);
        if (isInGrid) {
            containsKey = ChunkIDChunkMap.containsKey(chunkID);
            if (!containsKey) {
                loadIntoCacheChunk(chunkID);
            }
            return (Grids_AbstractGridChunkInt) ChunkIDChunkMap.get(chunkID);
        }
        return null;
    }

    /**
     * If newValue and oldValue are the same then statistics won't change. A
     * test might be appropriate in set cell so that this method is not called.
     * Also want to keep track if underlying data has changed for getting
     * statistics of Grids_GridStatisticsNotUpdatedAsDataChanged type.
     *
     * @param newValue The value replacing oldValue.
     * @param oldValue The value being replaced.
     */
    private void upDateGridStatistics(
            int newValue,
            int oldValue) {
        if (Statistics.getClass() == Grids_GridIntStatistics.class) {
            boolean handleOutOfMemoryError;
            handleOutOfMemoryError = ge.HandleOutOfMemoryError;
            if (newValue != NoDataValue) {
                if (oldValue != NoDataValue) {
                    BigDecimal oldValueBD = new BigDecimal(oldValue);
                    Statistics.setN(Statistics.getN(handleOutOfMemoryError) - 1);
                    Statistics.setSum(Statistics.getSum(handleOutOfMemoryError).subtract(oldValueBD));
                    int min = Statistics.getMin(false, handleOutOfMemoryError).intValue();
                    if (oldValue == min) {
                        Statistics.setNMin(Statistics.getNMin() - 1);
                    }
                    int max = Statistics.getMax(false, handleOutOfMemoryError).intValue();
                    if (oldValue == max) {
                        Statistics.setNMax(Statistics.getNMax() - 1);
                    }
                }
                if (newValue != NoDataValue) {
                    BigDecimal newValueBD = new BigDecimal(newValue);
                    Statistics.setN(Statistics.getN(handleOutOfMemoryError) + 1);
                    Statistics.setSum(Statistics.getSum(handleOutOfMemoryError).add(newValueBD));
                    updateStatistics(newValue);
                    if (Statistics.getNMin() < 1) {
                        // The Statistics need recalculating
                        Statistics.update();
                    }
                    if (Statistics.getNMax() < 1) {
                        // The Statistics need recalculating
                        Statistics.update();
                    }
                }
            }
        } else {
            if (newValue != oldValue) {
                ((Grids_GridIntStatisticsNotUpdated) Statistics).setUpToDate(false);
            }
        }
    }

    /**
     * @return NoDataValue.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getNoDataValue(
            boolean handleOutOfMemoryError) {
        try {
            int result = NoDataValue;
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return getNoDataValue(
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises NoDataValue as noDataValue.
     *
     * @param noDataValue The value NoDataValue is initialised to.
     */
    protected final void initNoDataValue(
            int noDataValue) {
        NoDataValue = noDataValue;
    }

    /**
     * @return Value at _CellRowIndex, _CellColIndex else returns NoDataValue.
     * @param row .
     * @param col .
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int getCell(
            long row,
            long col,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    row,
                    col);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(row),
                        getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCell(
                        row,
                        col,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Value at _CellRowIndex, _CellColIndex else returns NoDataValue.
     *
     * @param row .
     * @param col .
     */
    protected int getCell(
            long row,
            long col) {
        boolean isInGrid = isInGrid(
                row,
                col);
        if (isInGrid) {
            int chunkRow = getChunkRow(row);
            int chunkCol = getChunkCol(col);
            long chunkRowLong = chunkRow;
            long chunkColLong = chunkCol;
            int cellRow = (int) (row - (chunkRowLong * ChunkNRows));
            int cellCol = (int) (col - (chunkColLong * ChunkNCols));
            Grids_AbstractGridChunk chunk;
            chunk = getChunk(
                    chunkRow,
                    chunkCol);
            if (chunk.getClass() == Grids_GridChunkIntArray.class) {
                return ((Grids_GridChunkIntArray) chunk).getCell(
                        cellRow,
                        cellCol,
                        false);
            } else if (chunk.getClass() == Grids_GridChunkIntMap.class) {
                return ((Grids_GridChunkIntMap) chunk).getCell(
                        cellRow,
                        cellCol,
                        false);
            }
        }
        return NoDataValue;
    }

    /**
     * @param chunk
     * @return Value at position given by chunk row index _ChunkRow, chunk
     * column index _ChunkCol, chunk cell row index cellRow,
     * chunk cell column index cellCol.
     * @param chunkRow The chunk row index of the cell thats value is
     * returned.
     * @param chunkCol The chunk column index of the cell thats value is
     * returned.
     * @param cellRow The chunk cell row index of the cell thats value
     * is returned.
     * @param cellCol The chunk cell column index of the cell thats
     * value is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int getCell(
            Grids_AbstractGridChunkInt chunk,
            int chunkRow,
            int chunkCol,
            int cellRow,
            int cellCol,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    chunk,
                    chunkRow,
                    chunkCol,
                    cellRow,
                    cellCol);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRow,
                        chunkCol);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCell(
                        chunk,
                        chunkRow,
                        chunkCol,
                        cellRow,
                        cellCol,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunk
     * @return Value at position given by chunk row index _ChunkRow, chunk
     * column index _ChunkCol, chunk cell row index cellRow,
     * chunk cell column index cellCol.
     * @param chunkRow The chunk row index of the cell that'Statistics
     * value is returned.
     * @param chunkCol The chunk column index of the cell that'Statistics
     * value is returned.
     * @param cellRow The chunk cell row index of the cell thats value
     * is returned.
     * @param cellCol The chunk cell column index of the cell thats
     * value is returned.
     */
    protected int getCell(
            Grids_AbstractGridChunkInt chunk,
            int chunkRow,
            int chunkCol,
            int cellRow,
            int cellCol) {
        if (chunk.getClass() == Grids_GridChunkIntArray.class) {
            return ((Grids_GridChunkIntArray) chunk).getCell(cellRow,
                    cellCol,
                    false);
        } else if (chunk.getClass() == Grids_GridChunkIntMap.class) {
            return ((Grids_GridChunkIntMap) chunk).getCell(cellRow,
                    cellCol,
                    false);
        } else {
            return NoDataValue;
        }
    }

    /**
     * For returning the value of the cell containing point given by
     * x-coordinate x, y-coordinate y as a int.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final int getCell(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(y),
                        getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCell(x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value of the cell containing point given by
     * x-coordinate x, y-coordinate y as a int.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @return
     */
    protected final int getCell(
            double x,
            double y) {
        return getCell(
                getRow(y),
                getCol(x));
    }

    /**
     * For returning the value of the cell with cell Grids_2D_ID_int cellID as a
     * int.
     *
     * @param cellID the Grids_2D_ID_long of the cell.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final int getCell(
            Grids_2D_ID_long cellID,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCell(
                    cellID.getRow(),
                    cellID.getCol());
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(cellID.getRow()),
                        getChunkCol(cellID.getCol()));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCell(
                        cellID,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value at x-coordinate x, y-coordinate y and setting it
     * to newValue.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param newValue .
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final int setCell(
            double x,
            double y,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    x,
                    y,
                    newValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(y),
                        getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return setCell(
                        x,
                        y,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value at x-coordinate x, y-coordinate y and setting it
     * to newValue.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param newValue .
     * @return
     */
    protected final int setCell(
            double x,
            double y,
            int newValue) {
        return setCell(
                getRow(x),
                getCol(y),
                newValue);
    }

    /**
     * For returning the value of the cell with cell Grids_2D_ID_int _CellID and
     * setting it to newValue.
     *
     * @param cellID the Grids_2D_ID_long of the cell.
     * @param newValue .
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final int setCell(
            Grids_2D_ID_long cellID,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    cellID.getRow(),
                    cellID.getCol(),
                    newValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(cellID.getRow()),
                        getChunkCol(cellID.getCol()));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return setCell(
                        cellID,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param valueToSet
     * @return the value at row, col as a double and sets it
     * to valueToSet.
     * @param row The cell row index.
     * @param col The cell column index.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int setCell(long row, long col, int valueToSet, boolean handleOutOfMemoryError) {
        try {
            int result = setCell(row, col, valueToSet);
            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
            ge.tryToEnsureThereIsEnoughMemoryToContinue(this, chunkID, handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return setCell(row, col, valueToSet, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value at row, col and setting it to
     * newValue.
     *
     * @param row
     * @param col
     * @param newValue .
     * @return
     */
    protected int setCell(
            long row,
            long col,
            int newValue) {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        int cellRow = getCellRow(row);
        int cellCol = getCellCol(col);
        Grids_AbstractGridChunkInt chunk = (Grids_AbstractGridChunkInt) getGridChunk(
                chunkRow,
                chunkCol);
        return setCell(
                chunk,
                chunkRow,
                chunkCol,
                cellRow,
                cellCol,
                newValue);
    }

    /**
     * For returning the value of the cell in chunk given by _ChunkRow and
     * _ChunkCol and cell in the chunk given by cellCol and
     * cellRow and setting it to newValue.
     *
     * @param chunkRow
     * @param chunkCol
     * @param cellRow
     * @param cellCol
     * @param newValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public int setCell(
            int chunkRow,
            int chunkCol,
            int cellRow,
            int cellCol,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    chunkRow,
                    chunkCol,
                    cellRow,
                    cellCol,
                    newValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRow,
                        chunkCol);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return setCell(
                        chunkRow,
                        chunkCol,
                        cellRow,
                        cellCol,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning the value of the cell in chunk given by _ChunkRow and
     * _ChunkCol and cell in the chunk given by cellCol and
     * cellRow and setting it to newValue.
     *
     * @param chunkRow
     * @param chunkCol
     * @param cellRow
     * @param cellCol
     * @param newValue
     * @return
     */
    protected int setCell(
            int chunkRow,
            int chunkCol,
            int cellRow,
            int cellCol,
            int newValue) {
        Grids_AbstractGridChunkInt chunk;
        chunk = (Grids_AbstractGridChunkInt) getGridChunk(
                chunkRow,
                chunkCol);
        return setCell(
                chunk,
                chunkRow,
                chunkCol,
                cellRow,
                cellCol,
                newValue);
    }

    /**
     * @param chunk
     * @param chunkRow
     * @param cellCol
     * @param cellRow
     * @param chunkCol
     * @param newValue
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return Value at _CellRowIndex, _CellColIndex and sets it to newValue.
     */
    public int setCell(
            Grids_AbstractGridChunkInt chunk,
            int chunkRow,
            int chunkCol,
            int cellRow,
            int cellCol,
            int newValue,
            boolean handleOutOfMemoryError) {
        try {
            int result = setCell(
                    chunk,
                    chunkRow,
                    chunkCol,
                    cellRow,
                    cellCol,
                    newValue);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRow,
                        chunkCol);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return setCell(
                        chunk,
                        chunkRow,
                        chunkCol,
                        cellRow,
                        cellCol,
                        newValue,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunk
     * @param chunkRow
     * @param cellCol
     * @param cellRow
     * @param chunkCol
     * @param newValue
     * @return Value at _CellRowIndex, _CellColIndex and sets it to newValue.
     */
    protected int setCell(
            Grids_AbstractGridChunkInt chunk,
            int chunkRow,
            int chunkCol,
            int cellRow,
            int cellCol,
            int newValue) {
        int result = NoDataValue;
        if (chunk instanceof Grids_GridChunkIntArray) {
            result = ((Grids_GridChunkIntArray) chunk).setCell(
                    cellRow,
                    cellCol,
                    newValue,
                    ge.HandleOutOfMemoryError);
        } else if (chunk instanceof Grids_GridChunkIntMap) {
            result = ((Grids_GridChunkIntMap) chunk).setCell(
                    cellRow,
                    cellCol,
                    newValue,
                    ge.HandleOutOfMemoryError);
        } else {
            Grids_GridChunkInt c;
            c = (Grids_GridChunkInt) chunk;
            if (newValue != c.Value) {
                // Convert chunk to another type
                Grids_2D_ID_int chunkID;
                chunkID = chunk.getChunkID(ge.HandleOutOfMemoryError);
                chunk = ge.getProcessor().GridChunkIntFactory.createGridChunkInt(
                        chunk,
                        chunkID);
                chunk.setCell(chunkRow, chunkCol, newValue, ge.HandleOutOfMemoryError);
                ChunkIDChunkMap.put(chunkID, chunk);
            }
            return result;
        }
        // Update Statistics
        upDateGridStatistics(newValue, result);
        return result;
    }

    /**
     * Initialises the value at row, col.
     *
     * @param row
     * @param col
     * @param value
     */
    protected void initCell(
            long row,
            long col,
            int value) {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                chunkRow,
                chunkCol);
        Grids_AbstractGridChunkInt chunk = getGridChunk(chunkID);
        chunk.initCell(
                (int) (row - ((long) chunkRow * (long) ChunkNRows)),
                (int) (col - ((long) chunkCol * (long) ChunkNCols)),
                value);
        // Update Statistics
        if (value != NoDataValue) {
            updateStatistics(value);
        }
    }

    private void updateStatistics(int value) {
        boolean h = ge.HandleOutOfMemoryError;
        BigDecimal valueBD = new BigDecimal(value);
        Statistics.setN(Statistics.getN(h) + 1);
        Statistics.setSum(Statistics.getSum(h).add(valueBD));
        int min = Statistics.getMin(false, h).intValue();
        if (value < min) {
            Statistics.setNMin(1);
            Statistics.setMin(value);
        } else {
            if (value == min) {
                Statistics.setNMin(Statistics.getNMin() + 1);
            }
        }
        int max = Statistics.getMax(false, h).intValue();
        if (value > max) {
            Statistics.setNMax(1);
            Statistics.setMax(value);
        } else {
            if (value == max) {
                Statistics.setNMax(Statistics.getNMax() + 1);
            }
        }
    }

    /**
     * Initialises the value at row, col and does nothing
     * about Statistics
     *
     * @param row
     * @param col
     * @param valueToInitialise
     */
    protected void initCellFast(
            long row,
            long col,
            int valueToInitialise) {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        int chunkNRows = ChunkNRows;
        int chunkNCols = ChunkNCols;
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                chunkRow,
                chunkCol);
        Grids_AbstractGridChunkInt chunk = getGridChunk(chunkID);
        chunk.initCell(
                (int) (row - ((long) chunkRow * (long) chunkNRows)),
                (int) (col - ((long) chunkCol * (long) chunkNCols)),
                valueToInitialise);
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown. TODO
     */
    public int[] getCells(
            double x,
            double y,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            int[] result = getCells(
                    x,
                    y,
                    distance);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                long row = getRow(y);
                long col = getCol(x);
                HashSet<Grids_2D_ID_int> chunkIDs = getChunkIDs(
                        distance,
                        x,
                        y,
                        row,
                        col);
                freeSomeMemoryAndResetReserve(chunkIDs, e);
                return getCells(
                        x,
                        y,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    protected int[] getCells(
            double x,
            double y,
            double distance) {
        return getCells(
                x,
                y,
                getRow(y),
                getCol(x),
                distance);
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at centroid of cell given by cell row
     * index row, cell column index col, and radius distance.
     * @param row the row index for the cell that'Statistics centroid
     * is the circle centre from which cell values are returned.
     * @param col the column index for the cell that'Statistics
     * centroid is the circle centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int[] getCells(
            long row,
            long col,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            int[] result = getCells(
                    row,
                    col,
                    distance);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                double x = getCellXDouble(col);
                double y = getCellYDouble(row);
                HashSet chunkIDs = getChunkIDs(
                        distance,
                        x,
                        y,
                        row,
                        col);
                freeSomeMemoryAndResetReserve(chunkIDs, e);
                return getCells(
                        row,
                        col,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at centroid of cell given by cell row
     * index row, cell column index col, and radius distance.
     * @param row the row index for the cell that'Statistics centroid
     * is the circle centre from which cell values are returned.
     * @param col the column index for the cell that'Statistics
     * centroid is the circle centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    protected int[] getCells(
            long row,
            long col,
            double distance) {
        return getCells(
                getCellXDouble(col),
                getCellYDouble(row),
                row,
                col,
                distance);
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x The x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     * returned.
     * @param row The row index at y.
     * @param col The column index at x.
     * @param distance The radius of the circle for which intersected cell
     * values are returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown. TODO
     */
    public int[] getCells(
            double x,
            double y,
            long row,
            long col,
            double distance,
            boolean handleOutOfMemoryError) {
        try {
            int[] result = getCells(
                    x,
                    y,
                    row,
                    col,
                    distance);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                HashSet chunkIDs = getChunkIDs(
                        distance,
                        x,
                        y,
                        row,
                        col);
                freeSomeMemoryAndResetReserve(chunkIDs, e);
                return getCells(
                        x,
                        y,
                        row,
                        col,
                        distance,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x The x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     * returned.
     * @param row The row index at y.
     * @param col The column index at x.
     * @param distance The radius of the circle for which intersected cell
     * values are returned.
     */
    protected int[] getCells(
            double x,
            double y,
            long row,
            long col,
            double distance) {
        int[] cells;
        int cellDistance = (int) Math.ceil(distance / getCellsizeDouble(true));
        cells = new int[((2 * cellDistance) + 1) * ((2 * cellDistance) + 1)];
        long p;
        long q;
        double thisX;
        double thisY;
        int count = 0;
        for (p = row - cellDistance; p <= row + cellDistance; p++) {
            thisY = getCellYDouble(row);
            for (q = col - cellDistance; q <= col + cellDistance; q++) {
                thisX = getCellXDouble(col);
                if (Grids_Utilities.distance(x, y, thisX, thisY) <= distance) {
                    cells[count] = getCell(
                            p,
                            q);
                    count++;
                }
            }
        }
        // Trim cells
        System.arraycopy(
                cells,
                0,
                cells,
                0,
                count);
        return cells;
    }

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    @Override
    protected double getNearestValueDouble(
            double x,
            double y) {
        double result = getCell(x, y);
        if (result == NoDataValue) {
            result = getNearestValueDouble(
                    x, y,
                    getRow(y),
                    getCol(x));
        }
        return result;
    }

    /**
     * @param row The row index from which average of the nearest data
     * values is returned.
     * @param col The column index from which average of the nearest
     * data values is returned.
     * @return the average of the nearest data values to position given by row
     * index rowIndex, column index colIndex
     */
    @Override
    protected double getNearestValueDouble(
            long row,
            long col) {
        double result = getCell(
                row,
                col);
        if (result == NoDataValue) {
            result = getNearestValueDouble(
                    getCellXDouble(col),
                    getCellYDouble(row),
                    row,
                    col);
        }
        return result;
    }

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row the row index from which average of the nearest data
     * values is returned
     * @param col the column index from which average of the nearest
     * data values is returned
     */
    @Override
    protected double getNearestValueDouble(
            double x,
            double y,
            long row,
            long col) {
        Grids_2D_ID_long nearestCellID = getNearestCellID(
                x,
                y,
                row,
                col);
        double nearestValue = getCell(
                row,
                col);
        if (nearestValue == NoDataValue) {
            // Find a value Seeking outwards from nearestCellID
            // Initialise visitedSet1
            HashSet visitedSet = new HashSet();
            HashSet visitedSet1 = new HashSet();
            visitedSet.add(nearestCellID);
            visitedSet1.add(nearestCellID);
            // Initialise toVisitSet1
            HashSet toVisitSet1 = new HashSet();
            long p;
            long q;
            Grids_2D_ID_long cellID0;
            boolean isInGrid;
            for (p = -1; p < 2; p++) {
                for (q = -1; q < 2; q++) {
                    if (!(p == 0 && q == 0)) {
                        isInGrid = isInGrid(
                                row + p,
                                col + q);
                        if (isInGrid) {
                            cellID0 = new Grids_2D_ID_long(
                                    row + p,
                                    col + q);
                            toVisitSet1.add(cellID0);
                        }
                    }
                }
            }
            // Seek
            boolean foundValue = false;
            double value;
            HashSet values = new HashSet();
            HashSet visitedSet2;
            HashSet toVisitSet2;
            Iterator iterator;
            Grids_2D_ID_long cellID1;
            while (!foundValue) {
                visitedSet2 = new HashSet();
                toVisitSet2 = new HashSet();
                iterator = toVisitSet1.iterator();
                while (iterator.hasNext()) {
                    cellID0 = (Grids_2D_ID_long) iterator.next();
                    visitedSet2.add(cellID0);
                    value = getCell(cellID0, ge.HandleOutOfMemoryErrorTrue);
                    if (value != NoDataValue) {
                        foundValue = true;
                        values.add(cellID0);
                    } else {
                        // Add neighbours to toVisitSet2
                        for (p = -1; p < 2; p++) {
                            for (q = -1; q < 2; q++) {
                                if (!(p == 0 && q == 0)) {
                                    isInGrid = isInGrid(
                                            cellID0.getRow() + p,
                                            cellID0.getCol() + q);
                                    if (isInGrid) {
                                        cellID1 = new Grids_2D_ID_long(
                                                cellID0.getRow() + p,
                                                cellID0.getCol() + q);
                                        toVisitSet2.add(cellID1);
                                    }
                                }
                            }
                        }
                    }
                }
                toVisitSet2.removeAll(visitedSet1);
                toVisitSet2.removeAll(visitedSet2);
                visitedSet.addAll(visitedSet2);
                visitedSet1 = visitedSet2;
                toVisitSet1 = toVisitSet2;
            }
            double distance;
            double minDistance = Integer.MAX_VALUE;
            // Go through values and find the closest
            HashSet closest = new HashSet();
            iterator = values.iterator();
            while (iterator.hasNext()) {
                cellID0 = (Grids_2D_ID_long) iterator.next();
                distance = Grids_Utilities.distance(
                        x,
                        y,
                        getCellXDouble(cellID0),
                        getCellYDouble(cellID0));
                if (distance < minDistance) {
                    closest.clear();
                    closest.add(cellID0);
                } else {
                    if (distance == minDistance) {
                        closest.add(cellID0);
                    }
                }
                minDistance = Math.min(
                        minDistance,
                        distance);
            }
            // Get cellIDs that are within distance of discovered value
            Grids_2D_ID_long[] cellIDs = getCellIDs(
                    x,
                    y,
                    minDistance);
            for (Grids_2D_ID_long cellID : cellIDs) {
                if (!visitedSet.contains(cellID)) {
                    if (getCell(cellID, ge.HandleOutOfMemoryErrorTrue) != NoDataValue) {
                        distance = Grids_Utilities.distance(x, y, getCellXDouble(cellID), getCellYDouble(cellID));
                        if (distance < minDistance) {
                            closest.clear();
                            closest.add(cellID);
                        } else {
                            if (distance == minDistance) {
                                closest.add(cellID);
                            }
                        }
                        minDistance = Math.min(
                                minDistance,
                                distance);
                    }
                }
            }
            // Go through the closest and calculate the average.
            value = 0;
            iterator = closest.iterator();
            while (iterator.hasNext()) {
                cellID0 = (Grids_2D_ID_long) iterator.next();
                value += getCell(cellID0, ge.HandleOutOfMemoryErrorTrue);
            }
            nearestValue = value / (double) closest.size();
        }
        return nearestValue;
    }

    /**
     * @return a Grids_2D_ID_long[] The CellIDs of the nearest cells with data
     * values to point given by x-coordinate x, y-coordinate y.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    @Override
    protected Grids_2D_ID_long[] getNearestValuesCellIDs(double x, double y) {
        double value = getCell(x, y);
        if (value == NoDataValue) {
            return getNearestValuesCellIDs(x, y, getRow(y), getCol(x));
        }
        Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[1];
        cellIDs[0] = getCellID(x, y);
        return cellIDs;
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to position given by row index rowIndex, column index colIndex.
     * @param row The row index from which the cell IDs of the nearest
     * cells with data values are returned.
     * @param col
     */
    @Override
    protected Grids_2D_ID_long[] getNearestValuesCellIDs(
            long row,
            long col) {
        double value = getCell(
                row,
                col);
        if (value == NoDataValue) {
            return getNearestValuesCellIDs(
                    getCellXDouble(col),
                    getCellYDouble(row),
                    row,
                    col);
        }
        Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[1];
        cellIDs[0] = getCellID(
                row,
                col);
        return cellIDs;
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values nearest to point with position given by: x-coordinate x,
     * y-coordinate y; and, cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row The row index from which the cell IDs of the nearest
     * cells with data values are returned.
     * @param col The column index from which the cell IDs of the
     * nearest cells with data values are returned.
     */
    @Override
    protected Grids_2D_ID_long[] getNearestValuesCellIDs(
            double x,
            double y,
            long row,
            long col) {
        Grids_2D_ID_long[] nearestCellIDs = new Grids_2D_ID_long[1];
        nearestCellIDs[0] = getNearestCellID(
                x,
                y,
                row,
                col);
        double nearestCellValue = getCell(
                row,
                col);
        if (nearestCellValue == NoDataValue) {
            // Find a value Seeking outwards from nearestCellID
            // Initialise visitedSet1
            HashSet visitedSet = new HashSet();
            HashSet visitedSet1 = new HashSet();
            visitedSet.add(nearestCellIDs[0]);
            visitedSet1.add(nearestCellIDs[0]);
            // Initialise toVisitSet1
            HashSet toVisitSet1 = new HashSet();
            long p;
            long q;
            boolean isInGrid;
            Grids_2D_ID_long cellID;
            for (p = -1; p < 2; p++) {
                for (q = -1; q < 2; q++) {
                    if (!(p == 0 && q == 0)) {
                        isInGrid = isInGrid(
                                row + p,
                                col + q);
                        if (isInGrid) {
                            cellID = getCellID(
                                    row + p,
                                    col + q);
                            toVisitSet1.add(cellID);
                        }
                    }
                }
            }
            // Seek
            boolean foundValue = false;
            double value;
            HashSet values = new HashSet();
            HashSet visitedSet2;
            HashSet toVisitSet2;
            Iterator iterator;
            while (!foundValue) {
                visitedSet2 = new HashSet();
                toVisitSet2 = new HashSet();
                iterator = toVisitSet1.iterator();
                while (iterator.hasNext()) {
                    cellID = (Grids_2D_ID_long) iterator.next();
                    visitedSet2.add(cellID);
                    value = getCell(
                            cellID,
                            ge.HandleOutOfMemoryErrorTrue);
                    if (value != NoDataValue) {
                        foundValue = true;
                        values.add(cellID);
                    } else {
                        // Add neighbours to toVisitSet2
                        for (p = -1; p < 2; p++) {
                            for (q = -1; q < 2; q++) {
                                if (!(p == 0 && q == 0)) {
                                    isInGrid = isInGrid(
                                            cellID.getRow() + p,
                                            cellID.getCol() + q);
                                    if (isInGrid) {
                                        cellID = getCellID(
                                                cellID.getRow() + p,
                                                cellID.getCol() + q);
                                        toVisitSet2.add(cellID);
                                    }
                                }
                            }
                        }
                    }
                }
                toVisitSet2.removeAll(visitedSet1);
                toVisitSet2.removeAll(visitedSet2);
                visitedSet.addAll(visitedSet2);
                visitedSet1 = visitedSet2;
                toVisitSet1 = toVisitSet2;
            }
            double distance;
            double minDistance = Double.MAX_VALUE;
            // Go through values and find the closest
            HashSet closest = new HashSet();
            iterator = values.iterator();
            while (iterator.hasNext()) {
                cellID = (Grids_2D_ID_long) iterator.next();
                distance = Grids_Utilities.distance(
                        x,
                        y,
                        getCellXDouble(cellID),
                        getCellYDouble(cellID));
                if (distance < minDistance) {
                    closest.clear();
                    closest.add(cellID);
                } else {
                    if (distance == minDistance) {
                        closest.add(cellID);
                    }
                }
                minDistance = Math.min(
                        minDistance,
                        distance);
            }
            // Get cellIDs that are within distance of discovered value
            Grids_2D_ID_long[] cellIDs = getCellIDs(
                    x,
                    y,
                    minDistance);
            for (Grids_2D_ID_long cellID1 : cellIDs) {
                if (!visitedSet.contains(cellID1)) {
                    if (getCell(cellID1, ge.HandleOutOfMemoryErrorTrue) != NoDataValue) {
                        distance = Grids_Utilities.distance(x, y, getCellXDouble(cellID1), getCellYDouble(cellID1));
                        if (distance < minDistance) {
                            closest.clear();
                            closest.add(cellID1);
                        } else {
                            if (distance == minDistance) {
                                closest.add(cellID1);
                            }
                        }
                        minDistance = Math.min(
                                minDistance,
                                distance);
                    }
                }
            }
            // Go through the closest and put into an array
            nearestCellIDs = new Grids_2D_ID_long[closest.size()];
            iterator = closest.iterator();
            int counter = 0;
            while (iterator.hasNext()) {
                nearestCellIDs[counter] = (Grids_2D_ID_long) iterator.next();
                counter++;
            }
        }
        return nearestCellIDs;
    }

    /**
     * @return the distance to the nearest data value from point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    @Override
    protected double getNearestValueDoubleDistance(double x, double y) {
        double result = getCell(x, y);
        if (result == NoDataValue) {
            result = getNearestValueDoubleDistance(
                    x, y,
                    getRow(y),
                    getCol(x));
        }
        return result;
    }

    /**
     * @return the distance to the nearest data value from position given by row
     * index rowIndex, column index colIndex as a double.
     * @param row The cell row index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param col The cell column index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     */
    protected double getNearestValueDoubleDistance(
            long row,
            long col) {
        double result = getCell(
                row,
                col);
        if (result == NoDataValue) {
            result = getNearestValueDoubleDistance(getCellXDouble(col),
                    getCellYDouble(row),
                    row,
                    col);
        }
        return result;
    }

    /**
     * @return the distance to the nearest data value from: point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex as a double.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param row The cell row index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param col The cell column index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     */
    @Override
    protected double getNearestValueDoubleDistance(
            double x,
            double y,
            long row,
            long col) {
        double result = getCell(
                row,
                col);
        if (result == NoDataValue) {
            // Initialisation
            long long0;
            long long1;
            long longMinus1 = -1;
            long longTwo = 2;
            long longZero = 0;
            boolean boolean0;
            boolean boolean1;
            boolean boolean2;
            double double0;
            double double1;
            Grids_2D_ID_long nearestCellID = getNearestCellID(
                    x,
                    y,
                    row,
                    col);
            HashSet visitedSet = new HashSet();
            HashSet visitedSet1 = new HashSet();
            visitedSet.add(nearestCellID);
            visitedSet1.add(nearestCellID);
            HashSet toVisitSet1 = new HashSet();
            long p;
            long q;
            boolean isInGrid;
            Grids_2D_ID_long cellID;
            boolean foundValue = false;
            double value;
            HashSet values = new HashSet();
            HashSet visitedSet2;
            HashSet toVisitSet2;
            Iterator iterator;
            double distance;
            double minDistance = Double.MAX_VALUE;
            HashSet closest = new HashSet();
            // Find a value Seeking outwards from nearestCellID
            // Initialise toVisitSet1
            for (p = longMinus1; p < longTwo; p++) {
                for (q = longMinus1; q < longTwo; q++) {
                    boolean0 = (p == longZero);
                    boolean1 = (q == longZero);
                    boolean2 = !(boolean0 && boolean1);
                    if (boolean2) {
                        long0 = row + p;
                        long1 = col + q;
                        isInGrid = isInGrid(
                                long0,
                                long1,
                                ge.HandleOutOfMemoryErrorTrue);
                        if (isInGrid) {
                            cellID = getCellID(
                                    long0,
                                    long1,
                                    ge.HandleOutOfMemoryErrorTrue);
                            toVisitSet1.add(cellID);
                        }
                    }
                }
            }
            // Seek
            while (!foundValue) {
                visitedSet2 = new HashSet();
                toVisitSet2 = new HashSet();
                iterator = toVisitSet1.iterator();
                while (iterator.hasNext()) {
                    cellID = (Grids_2D_ID_long) iterator.next();
                    visitedSet2.add(cellID);
                    value = getCell(
                            cellID,
                            ge.HandleOutOfMemoryErrorTrue);
                    if (value != NoDataValue) {
                        foundValue = true;
                        values.add(cellID);
                    } else {
                        // Add neighbours to toVisitSet2
                        for (p = longMinus1; p < longTwo; p++) {
                            for (q = longMinus1; q < longTwo; q++) {
                                boolean0 = (p == longZero);
                                boolean1 = (q == longZero);
                                boolean2 = !(boolean0 && boolean1);
                                if (boolean2) {
                                    long0 = cellID.getRow() + p;
                                    long1 = cellID.getCol() + q;
                                    isInGrid = isInGrid(
                                            long0,
                                            long1,
                                            ge.HandleOutOfMemoryErrorTrue);
                                    if (isInGrid) {
                                        cellID = getCellID(
                                                long0,
                                                long1,
                                                ge.HandleOutOfMemoryErrorTrue);
                                        toVisitSet2.add(cellID);
                                    }
                                }
                            }
                        }
                    }
                }
                toVisitSet2.removeAll(visitedSet1);
                toVisitSet2.removeAll(visitedSet2);
                visitedSet.addAll(visitedSet2);
                visitedSet1 = visitedSet2;
                toVisitSet1 = toVisitSet2;
            }
            // Go through values and find the closest
            iterator = values.iterator();
            while (iterator.hasNext()) {
                cellID = (Grids_2D_ID_long) iterator.next();
                double0 = getCellXDouble(
                        cellID,
                        ge.HandleOutOfMemoryErrorTrue);
                double1 = getCellYDouble(
                        cellID,
                        ge.HandleOutOfMemoryErrorTrue);
                distance = Grids_Utilities.distance(
                        x,
                        y,
                        double0,
                        double1);
                if (distance < minDistance) {
                    closest.clear();
                    closest.add(cellID);
                } else {
                    if (distance == minDistance) {
                        closest.add(cellID);
                    }
                }
                minDistance = Math.min(
                        minDistance,
                        distance);
            }
            // Get cellIDs that are within distance of discovered value
            Grids_2D_ID_long[] cellIDs = getCellIDs(
                    x,
                    y,
                    minDistance);
            for (Grids_2D_ID_long cellID1 : cellIDs) {
                if (!visitedSet.contains(cellID1)) {
                    if (getCell(cellID1, ge.HandleOutOfMemoryErrorTrue) != NoDataValue) {
                        distance = Grids_Utilities.distance(x, y, getCellXDouble(cellID1), getCellYDouble(cellID1));
                        minDistance = Math.min(
                                minDistance,
                                distance);
                    }
                }
            }
            result = minDistance;
        } else {
            result = 0.0d;
        }
        return result;
    }

    /**
     * @return current value of cell containing the point given by x-coordinate
     * x, y-coordinate y, and adds valueToAdd to that cell.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param valueToAdd the value to be added to the cell containing the point
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double addToCell(
            double x,
            double y,
            int valueToAdd,
            boolean handleOutOfMemoryError) {
        try {
            int result = addToCell(
                    x,
                    y,
                    valueToAdd);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(y),
                        getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return addToCell(
                        x,
                        y,
                        valueToAdd,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return current value of cell containing the point given by x-coordinate
     * x, y-coordinate y, and adds valueToAdd to that cell.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param valueToAdd the value to be added to the cell containing the point
     */
    protected int addToCell(
            double x,
            double y,
            int valueToAdd) {
        return addToCell(
                getRow(y),
                getCol(x),
                valueToAdd);
    }

    /**
     * @return Value of the cell with cell Grids_2D_ID_int cellID and adds
     * valueToAdd to that cell.
     * @param cellID the Grids_2D_ID_long of the cell.
     * @param valueToAdd the value to be added to the cell containing the point
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int addToCell(
            Grids_2D_ID_long cellID,
            int valueToAdd,
            boolean handleOutOfMemoryError) {
        try {
            int result = addToCell(
                    cellID,
                    valueToAdd);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(cellID.getRow()),
                        getChunkCol(cellID.getCol()));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return addToCell(
                        cellID,
                        valueToAdd,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Value of the cell with cell Grids_2D_ID_int cellID and adds
     * valueToAdd to that cell.
     * @param cellID the Grids_2D_ID_long of the cell.
     * @param valueToAdd the value to be added to the cell containing the point
     */
    protected int addToCell(
            Grids_2D_ID_long cellID,
            int valueToAdd) {
        return addToCell(
                cellID.getRow(),
                cellID.getCol(),
                valueToAdd);
    }

    /**
     * @return current value of cell with row index rowIndex and column index
     * colIndex and adds valueToAdd to that cell.
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @param valueToAdd the value to be added to the cell.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown. NB1. If cell is not
     * contained in this then then returns NoDataValue. NB2. Adding to
     * NoDataValue is done as if adding to a cell with value of 0. TODO: Check
     * Arithmetic
     */
    public double addToCell(
            long row,
            long col,
            int valueToAdd,
            boolean handleOutOfMemoryError) {
        try {
            double result = addToCell(
                    row,
                    col,
                    valueToAdd);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(row),
                        getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return addToCell(
                        row,
                        col,
                        valueToAdd,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return current value of cell with row index rowIndex and column index
     * colIndex and adds valueToAdd to that cell.
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @param valueToAdd the value to be added to the cell. NB1. If cell is not
     * contained in this then then returns NoDataValue. NB2. Adding to
     * NoDataValue is done as if adding to a cell with value of 0. TODO: Check
     * Arithmetic
     */
    protected int addToCell(
            long row,
            long col,
            int valueToAdd) {
        boolean isInGrid = isInGrid(
                row,
                col);
        if (isInGrid) {
            int currentValue = getCell(
                    row,
                    col);
            if (currentValue != NoDataValue) {
                if (valueToAdd != NoDataValue) {
                    return setCell(
                            row,
                            col,
                            currentValue + valueToAdd);
                }
            } else {
                if (valueToAdd != NoDataValue) {
                    return setCell(
                            row,
                            col,
                            valueToAdd);
                }
            }
        }
        return NoDataValue;
    }

    /**
     *
     * @param value
     * @param handleOutOfMemoryError
     */
    public void initCells(
            int value,
            boolean handleOutOfMemoryError) {
        try {
            initCells(value);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                initCells(
                        value,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     *
     * @param value
     */
    protected void initCells(int value) {
        Iterator<Grids_2D_ID_int> ite = ChunkIDChunkMap.keySet().iterator();
        int nChunks = ChunkIDChunkMap.size();
        Grids_AbstractGridChunkInt chunk;
        int chunkNRows;
        int chunkNCols;
        int row;
        int col;
        Grids_2D_ID_int chunkID;
        int counter = 0;
        boolean handleOutOfMemoryError = true;
        while (ite.hasNext()) {
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            System.out.println(
                    "Initialising Chunk " + counter
                    + " out of " + nChunks);
            counter++;
            chunkID = ite.next();
            chunk = (Grids_AbstractGridChunkInt) ChunkIDChunkMap.get(chunkID);
            chunkNRows = getChunkNRows(chunkID, handleOutOfMemoryError);
            chunkNCols = getChunkNCols(chunkID, handleOutOfMemoryError);
            for (row = 0; row <= chunkNRows; row++) {
                for (col = 0; col <= chunkNCols; col++) {
                    chunk.setCell(
                            chunkNRows,
                            chunkNCols,
                            value,
                            handleOutOfMemoryError);
                }
            }
        }
    }

    /**
     * @return A Grids_GridIntIterator for iterating over the cell values in
     * this.
     */
    @Override
    public Grids_GridIntIterator iterator() {
        return new Grids_GridIntIterator(this);
    }

    @Override
    public Grids_GridIntStatistics getStatistics() {
        return Statistics;
    }

    public void initStatistics(Grids_GridIntStatistics statistics) {
        Statistics = statistics;
    }

    @Override
    protected double getCellDouble(Grids_AbstractGridChunk chunk, int chunkRow, int chunkCol, int cellRow, int cellCol) {
        Grids_AbstractGridChunkInt gridChunk
                = (Grids_AbstractGridChunkInt) chunk;
        Grids_GridInt gridInt;
        gridInt = (Grids_GridInt) gridChunk.getGrid(false);
        int noDataValue = gridInt.getNoDataValue(true);
        if (chunk.getClass() == Grids_GridChunkIntArray.class) {
            Grids_GridChunkIntArray gridChunkArray;
            gridChunkArray = (Grids_GridChunkIntArray) gridChunk;
            return (double) gridChunkArray.getCell(
                    cellRow,
                    cellCol,
                    false,
                    chunk.getChunkID(false));
        }
        if (chunk.getClass() == Grids_GridChunkIntMap.class) {
            Grids_GridChunkIntMap gridChunkMap
                    = (Grids_GridChunkIntMap) gridChunk;
            return (double) gridChunkMap.getCell(
                    cellRow,
                    cellCol,
                    false,
                    chunk.getChunkID(false));
        }
        return (double) noDataValue;
    }

}
