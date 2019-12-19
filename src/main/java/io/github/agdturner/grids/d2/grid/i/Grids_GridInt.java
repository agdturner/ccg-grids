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

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.core.Grids_2D_ID_long;
import io.github.agdturner.grids.core.Grids_Dimensions;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkInt;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkFactoryInt;
import io.github.agdturner.grids.d2.chunk.Grids_Chunk;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.d2.grid.Grids_GridNumber;
import io.github.agdturner.grids.d2.grid.Grids_Grid;
import io.github.agdturner.grids.d2.grid.d.Grids_GridDouble;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDouble;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkIntSinglet;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkIntArray;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkIntMap;
import io.github.agdturner.grids.d2.stats.Grids_StatsInt;
import io.github.agdturner.grids.d2.stats.Grids_StatsNotUpdatedInt;
import io.github.agdturner.grids.io.Grids_ESRIAsciiGridImporter;
import io.github.agdturner.grids.io.Grids_ESRIAsciiGridImporter.Grids_ESRIAsciiGridHeader;
import io.github.agdturner.grids.process.Grids_Processor;
import io.github.agdturner.grids.util.Grids_Utilities;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_FileStore;

/**
 * A class for representing grids of int values.
 *
 * @see Grids_GridNumber
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridInt extends Grids_GridNumber {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the NODATA value of the grid, which by default is
     * Integer.MIN_VALUE. Care should be taken so that NoDataValue is not a data
     * value.
     */
    protected int NoDataValue = Integer.MIN_VALUE;

    /**
     * Creates a new Grids_GridInt with each cell value equal to ndv and all
     * chunks of the same type.
     *
     * @param stats The Grids_StatsInt to accompany this.
     * @param cf The factory preferred for creating chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dims The grid dimensions (cellsize, xmin, ymin, xmax and ymax).
     * @param ndv The ndv.
     * @param ge
     */
    protected Grids_GridInt(Grids_StatsInt stats, Generic_FileStore fs,
            long id, Grids_ChunkFactoryInt cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dims,
            int ndv, Grids_Environment ge) throws IOException, Exception,
            ClassNotFoundException {
        super(ge, fs, id);
        init(stats, cf, chunkNRows, chunkNCols, nRows, nCols, dims, ndv);
    }

    /**
     * Creates a new Grids_GridInt based on values in grid.
     *
     * @param stats The Grids_StatsInt to accompany this.
     * @param g The Grids_GridNumber from which this is to be constructed.
     * @param cf The factory preferred to construct chunks of this.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param startRow The Grid2DSquareCell row which is the bottom most row of
     * this.
     * @param startCol The Grid2DSquareCell column which is the left most column
     * of this.
     * @param endRow The Grid2DSquareCell row which is the top most row of this.
     * @param endCol The Grid2DSquareCell column which is the right most column
     * of this.
     * @param ndv The ndv for this.
     */
    protected Grids_GridInt(Grids_StatsInt stats, Generic_FileStore fs, long id,
            Grids_Grid g, Grids_ChunkFactoryInt cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow, 
            long endCol, int ndv) throws IOException, ClassNotFoundException, 
            Exception {
        super(g.env, fs, id);
        init(stats, g, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, ndv);
    }

    /**
     * Creates a new Grids_GridInt with values obtained from gridFile. Currently
     * gridFile must be a directory of a Grids_GridDouble or Grids_GridInt or a
     * ESRI Asciigrid format file with a filename ending ".asc" or ".txt".
     *
     * @param stats The Grids_StatsInt to accompany this.
     * @param dir The directory for this.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the Grids_GridInt to
     * be returned.
     * @param cf The factory preferred to construct chunks of this.
     * @param chunkNRows
     * @param startRow The Grid2DSquareCell row which is the bottom most row of
     * this.
     * @param chunkNCols
     * @param startCol The Grid2DSquareCell column which is the left most column
     * of this.
     * @param endRow The Grid2DSquareCell row which is the top most row of this.
     * @param endCol The Grid2DSquareCell column which is the right most column
     * of this.
     * @param noDataValue The ndv for this.
     * @param ge
     */
    protected Grids_GridInt(Grids_StatsInt stats, Generic_FileStore fs,
            long id, Generic_Path gridFile,
            Grids_ChunkFactoryInt cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, int noDataValue, Grids_Environment ge)
            throws IOException, ClassNotFoundException, Exception {
        super(ge, fs, id);
        init(stats, gridFile, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, noDataValue);
    }

    /**
     * Creates a new Grids_GridInt with values obtained from gridFile. Currently
     * gridFile must be a directory of a Grids_GridDouble or Grids_GridInt or an
     * ESRI Asciigrid format file with a filename ending in ".asc" or ".txt".
     *
     * @param ge
     * @param dir The directory for this.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data for this.
     */
    protected Grids_GridInt(Grids_Environment ge, Generic_FileStore fs,
            long id, Generic_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        super(ge, fs, id);
        init(new Grids_StatsNotUpdatedInt(ge), gridFile);
    }

    @Override
    public String getFieldsDescription() {
        return "NoDataValue=" + NoDataValue + ", "
                + super.getFieldsDescription();
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridInt from which the fields of this are set.
     */
    private void init(Grids_GridInt g) throws IOException {
        NoDataValue = g.NoDataValue;
        stats = g.stats;
        super.init(g);
        chunkIDChunkMap = g.chunkIDChunkMap;
        // Set the reference to this in chunkIDChunkMap chunks
        setReferenceInChunkIDChunkMap();
        ChunkIDsOfChunksWorthCaching = g.ChunkIDsOfChunksWorthCaching;
        // Set the reference to this in the grid stats
        stats.setGrid(this);
        super.init();
        //Stats.grid = this;
    }

    @Override
    protected void init() throws IOException {
        super.init();
        if (!stats.isUpdated()) {
            ((Grids_StatsNotUpdatedInt) stats).setUpToDate(false);
        }
        stats.grid = this;
    }

    /**
     * Initialises this.
     *
     * @param stats The AbstractGridStatistics to accompany this.
     * @param dir The directory for this.
     * @param cf The Grids_ChunkFactoryInt preferred for creating chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param ndv The ndv.
     */
    private void init(Grids_StatsInt stats,
            Grids_ChunkFactoryInt cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions,
            int ndv) throws IOException, Exception {
        env.checkAndMaybeFreeMemory();
        init(stats, chunkNRows, chunkNCols, nRows, nCols, dimensions);
        for (int r = 0; r < NChunkRows; r++) {
            for (int c = 0; c < NChunkCols; c++) {
                env.checkAndMaybeFreeMemory();
                // Try to load chunk.
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(r, c);
                Grids_ChunkInt chunk = cf.create(this, chunkID);
                chunkIDChunkMap.put(chunkID, chunk);
                if (!(chunk instanceof Grids_ChunkIntSinglet)) {
                    ChunkIDsOfChunksWorthCaching.add(chunkID);
                }
            }
            env.env.log("Done chunkRow " + r + " out of " + NChunkRows);
        }
        init();
    }

    /**
     * Initialises this.
     *
     * @param stats The AbstractGridStatistics to accompany this.
     * @param dir The directory for this.
     * @param g The Grids_Grid from which this is to be constructed.
     * @param cf The factory preferred to construct chunks of this.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param startRow The row of g which is the bottom most row of this.
     * @param startCol The column of g which is the left most column of this.
     * @param endRow The row of g which is the top most row of this.
     * @param endCol The column of g which is the right most column of this.
     * @param ndv The ndv for this.
     */
    private void init(Grids_StatsInt stats, Grids_Grid g,
            Grids_ChunkFactoryInt cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, int ndv) throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        init(g, stats, chunkNRows, chunkNCols, startRow, startCol, endRow, endCol);
        int gcr;
        int gcc;
        int chunkRow;
        int chunkCol;
        boolean isLoadedChunk = false;
        int cellRow;
        int cellCol;
        long row;
        long col;
        long gRow;
        long gCol;
        Grids_2D_ID_int chunkID;
        Grids_2D_ID_int gChunkID;
        Grids_ChunkInt chunk;
        int gChunkNRows;
        int gChunkNCols;
        int startChunkRow;
        startChunkRow = g.getChunkRow(startRow);
        int endChunkRow;
        endChunkRow = g.getChunkRow(endRow);
        int nChunkRows;
        nChunkRows = endChunkRow - startChunkRow + 1;
        int startChunkCol;
        startChunkCol = g.getChunkCol(startCol);
        int endChunkCol;
        endChunkCol = g.getChunkCol(endCol);
        if (g instanceof Grids_GridDouble) {
            Grids_GridDouble gd = (Grids_GridDouble) g;
            Grids_ChunkDouble c;
            double gndv = gd.getNoDataValue();
            double gValue;
            for (gcr = startChunkRow; gcr <= endChunkRow; gcr++) {
                gChunkNRows = g.getChunkNRows(gcr);
                for (gcc = startChunkCol; gcc <= endChunkCol; gcc++) {
                    do {
                        try {
                            // Try to load chunk.
                            gChunkID = new Grids_2D_ID_int(gcr, gcc);
                            env.addToNotToCache(g, gChunkID);
                            env.checkAndMaybeFreeMemory();
                            c = gd.getChunk(gChunkID);
                            gChunkNCols = g.getChunkNCols(gcc);
                            for (cellRow = 0; cellRow < gChunkNRows; cellRow++) {
                                gRow = g.getRow(gcr, cellRow);
                                row = gRow - startRow;
                                chunkRow = getChunkRow(row);
                                if (gRow >= startRow && gRow <= endRow) {
                                    for (cellCol = 0; cellCol < gChunkNCols; cellCol++) {
                                        gCol = g.getCol(gcc, cellCol);
                                        col = gCol - startCol;
                                        chunkCol = getChunkCol(col);
                                        if (gCol >= startCol && gCol <= endCol) {
                                            /**
                                             * Initialise chunk if it does not
                                             * exist This is here rather than
                                             * where chunkID is initialised as
                                             * there may not be a chunk for the
                                             * chunkID.
                                             */
                                            if (isInGrid(row, col)) {
                                                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                                                env.addToNotToCache(this, chunkID);
                                                if (!chunkIDChunkMap.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    chunkIDChunkMap.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_ChunkIntSinglet)) {
                                                        ChunkIDsOfChunksWorthCaching.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_ChunkInt) chunkIDChunkMap.get(chunkID);
                                                }
                                                gValue = gd.getCell(c, cellRow, cellCol);
                                                // Initialise value
                                                if (gValue == gndv) {
                                                    initCell(chunk, chunkID, row, col, ndv);
                                                } else {
                                                    if (!Double.isNaN(gValue) && Double.isFinite(gValue)) {
                                                        initCell(chunk, chunkID, row, col, (int) gValue);
                                                    } else {
                                                        initCell(chunk, chunkID, row, col, ndv);
                                                    }
                                                }
                                                //ge.removeFromNotToCache(this, chunkID);
                                            }
                                        }
                                    }
                                }
                            }
                            isLoadedChunk = true;
                            env.removeFromNotToCache(g, gChunkID);
                            env.checkAndMaybeFreeMemory();
                        } catch (OutOfMemoryError e) {
                            if (env.HOOME) {
                                env.clearMemoryReserve();
                                freeSomeMemoryAndResetReserve(e);
                                chunkID = new Grids_2D_ID_int(gcr, gcc);
                                if (env.cacheChunksExcept_Account(this, chunkID, false) < 1L) {
                                    /**
                                     * TODO: Should also not cache out the chunk
                                     * of grid that's values are being used to
                                     * initialise this.
                                     */
                                    throw e;
                                }
                                env.initMemoryReserve(this, chunkID, env.HOOME);
                            } else {
                                throw e;
                            }
                        }
                    } while (!isLoadedChunk);
                    isLoadedChunk = false;
                    //loadedChunkCount++;
                    //cci1 = _ChunkColIndex;
                }
                System.out.println("Done chunkRow " + gcr + " out of " + nChunkRows);
            }
        } else {
            Grids_GridInt gi = (Grids_GridInt) g;
            Grids_ChunkInt c;
            int gndv = gi.getNoDataValue();
            int gValue;
            for (gcr = startChunkRow; gcr <= endChunkRow; gcr++) {
                gChunkNRows = g.getChunkNRows(gcr);
                for (gcc = startChunkCol; gcc <= endChunkCol; gcc++) {
                    do {
                        try {
                            // Try to load chunk.
                            gChunkID = new Grids_2D_ID_int(gcr, gcc);
                            env.addToNotToCache(g, gChunkID);
                            env.checkAndMaybeFreeMemory();
                            c = gi.getChunk(gChunkID);
                            gChunkNCols = g.getChunkNCols(gcc);
                            for (cellRow = 0; cellRow < gChunkNRows; cellRow++) {
                                gRow = g.getRow(gcr, cellRow);
                                row = gRow - startRow;
                                chunkRow = getChunkRow(row);
                                if (gRow >= startRow && gRow <= endRow) {
                                    for (cellCol = 0; cellCol < gChunkNCols; cellCol++) {
                                        gCol = g.getCol(gcc, cellCol);
                                        col = gCol - startCol;
                                        chunkCol = getChunkCol(col);
                                        if (gCol >= startCol && gCol <= endCol) {
                                            /**
                                             * Initialise chunk if it does not
                                             * exist This is here rather than
                                             * where chunkID is initialised as
                                             * there may not be a chunk for the
                                             * chunkID.
                                             */
                                            if (isInGrid(row, col)) {
                                                chunkID = new Grids_2D_ID_int(
                                                        chunkRow,
                                                        chunkCol);
                                                env.addToNotToCache(this, chunkID);
                                                if (!chunkIDChunkMap.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    chunkIDChunkMap.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_ChunkIntSinglet)) {
                                                        ChunkIDsOfChunksWorthCaching.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_ChunkInt) chunkIDChunkMap.get(chunkID);
                                                }
                                                gValue = gi.getCell(c, cellRow, cellCol);
                                                // Initialise value
                                                if (gValue == gndv) {
                                                    initCell(chunk, chunkID, row, col, ndv);
                                                } else {
                                                    if (!Double.isNaN(gValue) && Double.isFinite(gValue)) {
                                                        initCell(chunk, chunkID, row, col, gValue);
                                                    } else {
                                                        initCell(chunk, chunkID, row, col, ndv);
                                                    }
                                                }
                                                env.removeFromNotToCache(this, chunkID);
                                            }
                                        }
                                    }
                                }
                            }
                            isLoadedChunk = true;
                            env.removeFromNotToCache(g, gChunkID);
                            env.checkAndMaybeFreeMemory();
                        } catch (OutOfMemoryError e) {
                            if (env.HOOME) {
                                env.clearMemoryReserve();
                                chunkID = new Grids_2D_ID_int(gcr, gcc);
                                if (env.cacheChunksExcept_Account(this, chunkID, false) < 1L) {
                                    /**
                                     * TODO: Should also not cache out the chunk
                                     * of grid thats values are being used to
                                     * initialise this.
                                     */
                                    throw e;
                                }
                                env.initMemoryReserve(this, chunkID, env.HOOME);
                            } else {
                                throw e;
                            }
                        }
                    } while (!isLoadedChunk);
                    isLoadedChunk = false;
                }
                System.out.println("Done chunkRow " + gcr + " out of "
                        + nChunkRows);
            }
        }
        init();
    }

    /**
     * Initialises this.
     *
     * @param stats The AbstractGridStatistics to accompany this.
     * @param dir The File _Directory to be used for cacheping.
     * @param gridFile Either a _Directory, or a formatted File with a specific
     * extension containing the data and information about the Grids_GridInt to
     * be returned.
     * @param cf The Grids_ChunkFactoryInt preferred to construct chunks of
     * this.
     * @param chunkNRows The Grids_GridInt _ChunkNRows.
     * @param chunkNCols The Grids_GridInt _ChunkNCols.
     * @param startRow The topmost row index of the grid stored as gridFile.
     * @param startCol The leftmost column index of the grid stored as gridFile.
     * @param endRow The bottom row index of the grid stored as gridFile.
     * @param endCol The rightmost column index of the grid stored as gridFile.
     * @param ndv The ndv for this.
     */
    private void init(Grids_StatsInt stats, Generic_Path gridFile,
            Grids_ChunkFactoryInt cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, int ndv) throws IOException, ClassNotFoundException,
            Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        // Set to report every 10%
        int reportN;
        reportN = (int) (endRow - startRow) / 10;
        if (reportN == 0) {
            reportN = 1;
        }
        if (Files.isDirectory(gridFile)) {
            if (true) {
                Grids_Processor gp = env.getProcessor();
                Grids_GridFactoryInt gf = gp.GridIntFactory;
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                Grids_Grid g = (Grids_Grid) Generic_IO.readObject(thisFile);
                Grids_GridInt g2 = gf.create(g, startRow, startCol, endRow, endCol);
                init(g2);
            }
        } else {
            // Assume ESRI AsciiFile
            ChunkNRows = chunkNRows;
            ChunkNCols = chunkNCols;
            NRows = endRow - startRow + 1L;
            NCols = endCol - startCol + 1L;
            initNoDataValue(ndv);
            Name = store.getBaseDir().getFileName().toString() + id;
            initNChunkRows();
            initNChunkCols();
            chunkIDChunkMap = new TreeMap<>();
            ChunkIDsOfChunksWorthCaching = new HashSet<>();
            this.stats = stats;
            this.stats.setGrid(this);
            String filename = gridFile.getFileName().toString();
            int value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Grids_ESRIAsciiGridHeader header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                initDimensions(header, startRow, startCol);
                int gridFileNoDataValue = header.ndv.intValueExact();
                long row;
                long col;
//                Grids_ChunkInt chunk;
//                Grids_ChunkIntSinglet gridChunk;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue == NoDataValue) {
                    if (stats.isUpdated()) {
                        for (row = (NRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToCache();
                            for (col = 0; col < NCols; col++) {
                                value = eagi.readInt();
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    } else {
                        for (row = (NRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToCache();
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
                            env.checkAndMaybeFreeMemory();
                        }
                    }
                } else {
                    if (stats.isUpdated()) {
                        for (row = (NRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToCache();
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
                            env.checkAndMaybeFreeMemory();
                        }
                    } else {
                        for (row = (NRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToCache();
                            for (col = 0; col < NCols; col++) {
                                value = eagi.readInt();
                                initCell(row, col, value, true);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    }
                }
            }
        }
        init();
    }

    private void init(Grids_StatsInt stats, Generic_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        // For reporting
        int reportN;
        Grids_Processor gp;
        gp = env.getProcessor();
        if (Files.isDirectory(gridFile)) {
            if (true) {
                Grids_GridFactoryInt gf = gp.GridIntFactory;
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                Grids_GridInt g = (Grids_GridInt) gf.create(
                        (Grids_Grid) Generic_IO.readObject(thisFile));
                init(g);
                this.chunkIDChunkMap = g.chunkIDChunkMap;
                this.ChunkIDsOfChunksWorthCaching = g.ChunkIDsOfChunksWorthCaching;
                this.NoDataValue = g.NoDataValue;
                this.Dimensions = g.Dimensions;
                this.stats = g.getStats();
                this.stats.grid = this;
            }
        } else {
            // Assume ESRI AsciiFile
            Name = store.getBaseDir().getFileName().toString() + id;
            chunkIDChunkMap = new TreeMap<>();
            ChunkIDsOfChunksWorthCaching = new HashSet<>();
            this.stats = stats;
            this.stats.setGrid(this);
            String filename = gridFile.getFileName().toString();
            int value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Grids_ESRIAsciiGridHeader header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                NCols = header.nrows;
                NRows = header.ncols;
                ChunkNRows = gp.GridDoubleFactory.getChunkNRows();
                ChunkNCols = gp.GridDoubleFactory.getChunkNCols();
                initNChunkRows();
                initNChunkCols();
                initDimensions(header, 0, 0);
                // Set to report every 10%
                reportN = (int) (NRows - 1) / 10;
                if (reportN == 0) {
                    reportN = 1;
                }
                double gridFileNoDataValue = header.ndv.doubleValue();
                long row;
                long col;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue == NoDataValue) {
                    if (stats.isUpdated()) {
                        for (row = (NRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToCache();
                            for (col = 0; col < NCols; col++) {
                                value = (int) eagi.readDouble();
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    } else {
                        for (row = (NRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToCache();
                            for (col = 0; col < NCols; col++) {
                                value = (int) eagi.readDouble();
                                if (value == gridFileNoDataValue) {
                                    value = NoDataValue;
                                }
                                initCell(row, col, value, true);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    }
                } else {
                    if (stats.isUpdated()) {
                        for (row = (NRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToCache();
                            for (col = 0; col < NCols; col++) {
                                value = (int) eagi.readDouble();
                                if (value == gridFileNoDataValue) {
                                    value = NoDataValue;
                                }
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    } else {
                        for (row = (NRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToCache();
                            for (col = 0; col < NCols; col++) {
                                value = (int) eagi.readDouble();
                                initCell(row, col, value, true);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    }
                }
            }
        }
        init();
    }

//    /**
//     * Attempts to load into the memory cache the chunk with chunk ID chunkID.
//     *
//     * @param chunkID The chunk ID of the chunk to be restored.
//     */
//    @Override
//    public void loadIntoCacheChunk(Grids_2D_ID_int chunkID) {
//        boolean isInCache = isInCache(chunkID);
//        if (!isInCache) {
//            File f = new File(getDirectory(),
//                    "" + chunkID.getRow() + "_" + chunkID.getCol());
//            Object o = env.env.io.readObject(f);
//            Grids_ChunkInt chunk = null;
//            if (o.getClass() == Grids_ChunkIntArray.class) {
//                Grids_ChunkIntArray c;
//                c = (Grids_ChunkIntArray) o;
//                chunk = c;
//            } else if (o.getClass() == Grids_ChunkIntMap.class) {
//                Grids_ChunkIntMap c;
//                c = (Grids_ChunkIntMap) o;
//                chunk = c;
//            } else if (o.getClass() == Grids_ChunkIntSinglet.class) {
//                Grids_ChunkIntSinglet c;
//                c = (Grids_ChunkIntSinglet) o;
//                chunk = c;
//            } else {
//                throw new Error("Unrecognised type of chunk or null "
//                        + this.getClass().getName()
//                        + ".loadIntoCacheChunk(ChunkID(" + chunkID.toString()
//                        + "))");
//            }
//            chunk.env = env;
//            chunk.initGrid(this);
//            chunk.initChunkID(chunkID);
//            chunkIDChunkMap.put(chunkID, chunk);
//            if (!(chunk instanceof Grids_ChunkIntSinglet)) {
//                ChunkIDsOfChunksWorthCaching.add(chunkID);
//            }
//            env.setDataToCache(true);
//        }
//    }
    /**
     *
     * @param row
     * @param col
     * @param value
     * @param fast
     */
    private void initCell(long row, long col, int value, boolean fast)
            throws IOException, ClassNotFoundException, Exception {
        Grids_ChunkInt chunk;
        int chunkRow;
        int chunkCol;
        Grids_2D_ID_int chunkID;
        chunkRow = getChunkRow(row);
        chunkCol = getChunkCol(col);
        chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
        /**
         * Ensure this chunkID is not cacheped and initialise it if it does not
         * already exist.
         */
        env.addToNotToCache(this, chunkID);
        if (!chunkIDChunkMap.containsKey(chunkID)) {
            Grids_ChunkIntSinglet gc = new Grids_ChunkIntSinglet(this, chunkID,
                    value);
            chunkIDChunkMap.put(chunkID, gc);
            if (!(gc instanceof Grids_ChunkIntSinglet)) {
                ChunkIDsOfChunksWorthCaching.add(chunkID);
            }
        } else {
            Grids_Chunk c;
            c = chunkIDChunkMap.get(chunkID);
            if (c == null) {
                loadIntoCacheChunk(chunkID);
            }
            chunk = (Grids_ChunkInt) chunkIDChunkMap.get(chunkID);
            if (chunk instanceof Grids_ChunkIntSinglet) {
                Grids_ChunkIntSinglet gc = (Grids_ChunkIntSinglet) chunk;
                if (value != gc.Value) {
                    // Convert chunk to another type
                    chunk = env.getProcessor().defaultChunkIntFactory.create(
                            chunk, chunkID);
                    chunk.initCell(getCellRow(row), getCellCol(col), value);
                    chunkIDChunkMap.put(chunkID, chunk);
                    if (!(chunk instanceof Grids_ChunkIntSinglet)) {
                        ChunkIDsOfChunksWorthCaching.add(chunkID);
                    }
                }
            } else {
                if (fast) {
                    initCellFast(chunk, row, col, value);
                } else {
                    initCell(chunk, chunkID, row, col, value);
                }
            }
        }
    }

    /**
     * @return Grids_ChunkInt for the given chunkID.
     * @param chunkID
     */
    @Override
    public Grids_ChunkInt getChunk(Grids_2D_ID_int chunkID) throws IOException,
            ClassNotFoundException, Exception {
        if (isInGrid(chunkID)) {
            if (chunkIDChunkMap.get(chunkID) == null) {
                loadIntoCacheChunk(chunkID);
            }
            return (Grids_ChunkInt) chunkIDChunkMap.get(chunkID);
        }
        return null;
    }

    /**
     * @return Grids_ChunkInt for the given chunkID.
     * @param chunkID
     */
    @Override
    public Grids_ChunkInt getChunk(Grids_2D_ID_int chunkID, int chunkRow,
            int chunkCol) throws IOException, ClassNotFoundException,
            Exception {
        if (isInGrid(chunkRow, chunkCol)) {
            if (chunkIDChunkMap.get(chunkID) == null) {
                loadIntoCacheChunk(chunkID);
            }
            return (Grids_ChunkInt) chunkIDChunkMap.get(chunkID);
        }
        return null;
    }

    /**
     * If newValue and oldValue are the same then stats won't change.
     *
     * @param newValue The value replacing oldValue.
     * @param oldValue The value being replaced.
     */
    public void updateStats(int newValue, int oldValue) throws IOException, Exception, ClassNotFoundException {
        Grids_StatsInt iStats = getStats();
        if (iStats.isUpdated()) {
            if (newValue != NoDataValue) {
                if (oldValue != NoDataValue) {
                    BigDecimal oldValueBD = new BigDecimal(oldValue);
                    iStats.setN(iStats.getN() - 1);
                    iStats.setSum(iStats.getSum().subtract(oldValueBD));
                    int min = iStats.getMin(false);
                    if (oldValue == min) {
                        iStats.setNMin(iStats.getNMin() - 1);
                    }
                    int max = iStats.getMax(false);
                    if (oldValue == max) {
                        iStats.setNMax(iStats.getNMax() - 1);
                    }
                }
                if (newValue != NoDataValue) {
                    BigDecimal newValueBD = new BigDecimal(newValue);
                    iStats.setN(iStats.getN() + 1);
                    iStats.setSum(iStats.getSum().add(newValueBD));
                    updateStats(newValue);
                    if (iStats.getNMin() < 1) {
                        // The stats need recalculating
                        iStats.update();
                    }
                    if (iStats.getNMax() < 1) {
                        // The stats need recalculating
                        iStats.update();
                    }
                }
            }
        } else {
            if (newValue != oldValue) {
                ((Grids_StatsNotUpdatedInt) iStats).setUpToDate(false);
            }
        }
    }

    public final int getNoDataValue() {
        return NoDataValue;
    }

    /**
     * Initialises ndv as noDataValue.
     *
     * @param noDataValue The value ndv is initialised to.
     */
    protected final void initNoDataValue(
            int noDataValue) {
        NoDataValue = noDataValue;
    }

    /**
     * For getting the value at row, col.
     *
     * @param row
     * @param col
     * @return
     */
    public int getCell(long row, long col) throws IOException, Exception,
            ClassNotFoundException {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        Grids_ChunkInt c = (Grids_ChunkInt) getChunk(chunkRow, chunkCol);
        int cellRow = getCellRow(row);
        int cellCol = getCellCol(col);
        return getCell(c, cellRow, cellCol);
    }

    /**
     * For getting the value in chunk at cellRow, cellCol.
     *
     * @param chunk
     * @return Value at position given by chunk row index _ChunkRow, chunk
     * column index _ChunkCol, chunk cell row index cellRow, chunk cell column
     * index cellCol.
     * @param cellRow The chunk cell row index of the cell thats value is
     * returned.
     * @param cellCol The chunk cell column index of the cell thats value is
     * returned.
     */
    public int getCell(Grids_ChunkInt chunk, int cellRow, int cellCol) {
        if (chunk.getClass() == Grids_ChunkIntArray.class) {
            return ((Grids_ChunkIntArray) chunk).getCell(cellRow, cellCol);
        } else if (chunk.getClass() == Grids_ChunkIntMap.class) {
            return ((Grids_ChunkIntMap) chunk).getCell(cellRow, cellCol);
        } else {
            return ((Grids_ChunkIntSinglet) chunk).getCell(cellRow, cellCol);
        }
    }

    /**
     * For getting the value at x-coordinate x, y-coordinate y.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @return The value at x, y.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final int getCell(BigDecimal x, BigDecimal y) throws IOException,
            ClassNotFoundException, Exception {
        long row = getRow(y);
        long col = getCol(x);
        boolean isInGrid = isInGrid(row, col);
        if (isInGrid) {
            return getCell(row, col);
        }
        return NoDataValue;
    }

    /**
     * @param cellID the Grids_2D_ID_long of the cell.
     * @return The value of the cell with cellID.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final int getCell(Grids_2D_ID_long cellID) throws IOException,
            Exception, ClassNotFoundException {
        return getCell(cellID.getRow(), cellID.getCol());
    }

    /**
     * For setting the value at x-coordinate x, y-coordinate y.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param v The value to set in the cell.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final void setCell(BigDecimal x, BigDecimal y, int v)
            throws IOException, Exception, ClassNotFoundException, Exception {
        setCell(getRow(x), getCol(y), v);
    }

    /**
     * For setting the value at row, col.
     *
     * @param row
     * @param col
     * @param value
     */
    public void setCell(long row, long col, int value) throws IOException, ClassNotFoundException, Exception {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        int cellRow = getCellRow(row);
        int cellCol = getCellCol(col);
        Grids_ChunkInt chunk;
        chunk = (Grids_ChunkInt) getChunk(
                chunkRow, chunkCol);
        setCell(chunk, cellRow, cellCol, value);
    }

    /**
     * For setting the value of the cell at chunkRow, chunkCol, cellRow,
     * cellCol.
     *
     * @param chunkRow
     * @param chunkCol
     * @param cellRow
     * @param cellCol
     * @param newValue
     */
    public void setCell(int chunkRow, int chunkCol, int cellRow, int cellCol,
            int newValue) throws IOException, ClassNotFoundException, Exception {
        Grids_ChunkInt chunk;
        chunk = (Grids_ChunkInt) getChunk(
                chunkRow, chunkCol);
        setCell(chunk, cellRow, cellCol, newValue);
    }

    /**
     * For setting the value of the chunk at cellRow, cellCol.
     *
     * @param chunk
     * @param cellCol
     * @param cellRow
     * @param value
     */
    public void setCell(
            Grids_ChunkInt chunk,
            int cellRow,
            int cellCol,
            int value) throws IOException, ClassNotFoundException, Exception {
        int v;
        if (chunk instanceof Grids_ChunkIntArray) {
            v = ((Grids_ChunkIntArray) chunk).setCell(cellRow, cellCol,
                    value);
        } else if (chunk instanceof Grids_ChunkIntMap) {
            v = ((Grids_ChunkIntMap) chunk).setCell(cellRow, cellCol,
                    value);
        } else {
            Grids_ChunkIntSinglet c;
            c = (Grids_ChunkIntSinglet) chunk;
            if (value != c.Value) {
                // Convert chunk to another type
                Grids_2D_ID_int chunkID = chunk.getChunkID();
                chunk = convertToAnotherTypeOfChunk(chunk, chunkID);
                v = chunk.setCell(cellRow, cellCol, value);
            } else {
                v = c.Value;
            }
        }
        // Update stats
        if (value != v) {
            if (stats.isUpdated()) {
                updateStats(value, v);
            }
        }
    }

    /**
     * Convert chunk to another type of chunk.
     */
    private Grids_ChunkInt convertToAnotherTypeOfChunk(
            Grids_ChunkInt chunk,
            Grids_2D_ID_int chunkID) throws IOException, ClassNotFoundException, Exception {
        Grids_ChunkInt r;
        Grids_ChunkFactoryInt f = env.getProcessor().defaultChunkIntFactory;
        r = f.create(chunk, chunkID);
        chunkIDChunkMap.put(chunkID, r);
        return r;
    }

    /**
     * Initialises the value at row, col.
     *
     * @param chunk
     * @param chunkID
     * @param row
     * @param col
     * @param value
     */
    protected void initCell(Grids_ChunkInt chunk, Grids_2D_ID_int chunkID,
            long row, long col, int value) throws IOException,
            ClassNotFoundException, Exception {
        if (chunk instanceof Grids_ChunkIntSinglet) {
            Grids_ChunkIntSinglet gridChunk = (Grids_ChunkIntSinglet) chunk;
            if (value != gridChunk.Value) {
                // Convert chunk to another type
                chunk = convertToAnotherTypeOfChunk(chunk, chunkID);
                chunk.initCell(getCellRow(row), getCellCol(col), value);
            } else {
                return;
            }
        }
        chunk.initCell(getCellRow(row), getCellCol(col), value);
        // Update stats
        if (value != NoDataValue) {
            if (stats.isUpdated()) {
                updateStats(value);
            }
        }
    }

    public void updateStats(int value) throws IOException, Exception,
            ClassNotFoundException {
        Grids_StatsInt iStats = getStats();
        BigDecimal valueBD = new BigDecimal(value);
        iStats.setN(iStats.getN() + 1);
        iStats.setSum(iStats.getSum().add(valueBD));
        int min = iStats.getMin(false);
        if (value < min) {
            iStats.setNMin(1);
            iStats.setMin(value);
        } else {
            if (value == min) {
                iStats.setNMin(iStats.getNMin() + 1);
            }
        }
        int max = iStats.getMax(false);
        if (value > max) {
            iStats.setNMax(1);
            iStats.setMax(value);
        } else {
            if (value == max) {
                iStats.setNMax(iStats.getNMax() + 1);
            }
        }
    }

    /**
     * Initialises the value at row, col and does nothing about stats
     *
     * @param chunk
     * @param row
     * @param col
     * @param value
     */
    protected void initCellFast(Grids_ChunkInt chunk, long row, long col,
            int value) {
//        int chunkRow = getChunkRow(row);
//        int chunkCol = getChunkCol(col);
//        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
//                chunkRow,
//                chunkCol);
//        Grids_ChunkInt chunk = getChunk(chunkID);
        chunk.initCell(getCellRow(row), getCellCol(col), value);
    }

    /**
     * @return int[] of all cell values for cells that's centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    protected int[] getCells(BigDecimal x, BigDecimal y,
            BigDecimal distance, int dp, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        return getCells(x, y, getRow(y), getCol(x), distance, dp, rm);
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at centroid of cell given by cell row
     * index row, cell column index col, and radius distance.
     * @param row the row index for the cell that'stats centroid is the circle
     * centre from which cell values are returned.
     * @param col the column index for the cell that'stats centroid is the
     * circle centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    public int[] getCells(long row, long col, BigDecimal distance, int dp,
            RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        return getCells(getCellXBigDecimal(col), getCellYBigDecimal(row), row,
                col, distance, dp, rm);
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
    public int[] getCells(BigDecimal x, BigDecimal y, long row, long col,
            BigDecimal distance, int dp, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        int[] cells;
        BigDecimal[] dar = distance.divideAndRemainder(getCellsize());
        int delta = dar[0].intValueExact();
        if (dar[1].compareTo(BigDecimal.ZERO) == 1) {
            delta += 1;
        }
        cells = new int[((2 * delta) + 1) * ((2 * delta) + 1)];
        int count = 0;
        for (long p = row - delta; p <= row + delta; p++) {
            BigDecimal thisY = getCellYBigDecimal(row);
            for (long q = col - delta; q <= col + delta; q++) {
                BigDecimal thisX = getCellXBigDecimal(col);
                if (Grids_Utilities.distance(x, y, thisX, thisY, dp, rm)
                        .compareTo(distance) <= 0) {
                    cells[count] = getCell(p, q);
                    count++;
                }
            }
        }
        // Trim cells
        System.arraycopy(cells, 0, cells, 0, count);
        return cells;
    }

    /**
     * @return NearestValuesCellIDsAndDistance - The cell IDs of the nearest
     * cells with data values nearest to a point with position given by:
     * x-coordinate x, y-coordinate y.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param dp The number of decimal places used in distance calculations.
     * @param rm The {@link RoundingMode} to use when rounding distance
     * calculations.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            BigDecimal x, BigDecimal y, int dp, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        int value = getCell(x, y);
        if (value == NoDataValue) {
            return getNearestValuesCellIDsAndDistance(x, y, getRow(y), getCol(x), dp, rm);
        }
        r.cellIDs = new Grids_2D_ID_long[1];
        r.cellIDs[0] = getCellID(x, y);
        r.distance = BigDecimal.ZERO;
        return r;
    }

    /**
     * @return NearestValuesCellIDsAndDistance - The cell IDs of the nearest
     * cells with data values nearest to cell row index {@code row}, cell column
     * index {@code col}.
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col The column index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param dp The number of decimal places used in distance calculations.
     * @param rm The {@link RoundingMode} to use when rounding distance
     * calculations.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            long row, long col, int dp, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        int value = getCell(row, col);
        if (value == NoDataValue) {
            return getNearestValuesCellIDsAndDistance(getCellXBigDecimal(col),
                    getCellYBigDecimal(row), row, col, dp, rm);
        }
        r.cellIDs = new Grids_2D_ID_long[1];
        r.cellIDs[0] = getCellID(row, col);
        r.distance = BigDecimal.ZERO;
        return r;
    }

    /**
     * @return NearestValuesCellIDsAndDistance - The cell IDs of the nearest
     * cells with data values nearest to a point with position given by:
     * x-coordinate x, y-coordinate y; in cell row index {@code row}, cell
     * column index {@code col}.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col The column index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param dp The number of decimal places used in distance calculations.
     * @param rm The {@link RoundingMode} to use when rounding distance
     * calculations.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            BigDecimal x, BigDecimal y, long row, long col, int dp,
            RoundingMode rm) throws IOException, Exception,
            ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        r.cellIDs = new Grids_2D_ID_long[1];
        r.cellIDs[0] = getNearestCellID(x, y, row, col);
        int nearestCellValue = getCell(row, col);
        if (nearestCellValue == NoDataValue) {
            // Find a value Seeking outwards from nearestCellID
            // Initialise visitedSet1
            HashSet<Grids_2D_ID_long> visitedSet = new HashSet<>();
            HashSet<Grids_2D_ID_long> visitedSet1 = new HashSet<>();
            visitedSet.add(r.cellIDs[0]);
            visitedSet1.add(r.cellIDs[0]);
            // Initialise toVisitSet1
            HashSet<Grids_2D_ID_long> toVisitSet1 = new HashSet<>();
            for (long p = -1; p < 2; p++) {
                for (long q = -1; q < 2; q++) {
                    if (!(p == 0 && q == 0)) {
                        if (isInGrid(row + p, col + q)) {
                            toVisitSet1.add(getCellID(row + p, col + q));
                        }
                    }
                }
            }
            // Seek
            boolean foundValue = false;
            int value;
            HashSet<Grids_2D_ID_long> values = new HashSet<>();
            Iterator<Grids_2D_ID_long> iterator;
            while (!foundValue) {
                HashSet<Grids_2D_ID_long> visitedSet2 = new HashSet<>();
                HashSet<Grids_2D_ID_long> toVisitSet2 = new HashSet<>();
                iterator = toVisitSet1.iterator();
                while (iterator.hasNext()) {
                    Grids_2D_ID_long cellID = iterator.next();
                    visitedSet2.add(cellID);
                    value = getCell(cellID);
                    if (value != NoDataValue) {
                        foundValue = true;
                        values.add(cellID);
                    } else {
                        // Add neighbours to toVisitSet2
                        for (long p = -1; p < 2; p++) {
                            for (long q = -1; q < 2; q++) {
                                if (!(p == 0 && q == 0)) {
                                    long r0 = cellID.getRow() + p;
                                    long c0 = cellID.getCol() + q;
                                    if (isInGrid(r0, c0)) {
                                        toVisitSet2.add(getCellID(r0, c0));
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
            BigDecimal distance;
            // Go through values and find the closest
            HashSet<Grids_2D_ID_long> closest = new HashSet<>();
            iterator = values.iterator();
            Grids_2D_ID_long cellID = iterator.next();
            r.distance = Grids_Utilities.distance(x, y,
                    getCellXBigDecimal(cellID), getCellYBigDecimal(cellID),
                    dp, rm);
            while (iterator.hasNext()) {
                cellID = iterator.next();
                distance = Grids_Utilities.distance(x, y,
                        getCellXBigDecimal(cellID), getCellYBigDecimal(cellID),
                        dp, rm);
                if (distance.compareTo(r.distance) == -1) {
                    closest.clear();
                    closest.add(cellID);
                } else {
                    if (distance == r.distance) {
                        closest.add(cellID);
                    }
                }
                r.distance = r.distance.min(distance);
            }
            // Get cellIDs that are within distance of discovered value
            Grids_2D_ID_long[] cellIDs = getCellIDs(x, y, r.distance, dp, rm);
            for (Grids_2D_ID_long cellID1 : cellIDs) {
                if (!visitedSet.contains(cellID1)) {
                    if (getCell(cellID1) != NoDataValue) {
                        distance = Grids_Utilities.distance(x, y,
                                getCellXBigDecimal(cellID1),
                                getCellYBigDecimal(cellID1), dp, rm);
                        if (distance.compareTo(r.distance) == -1) {
                            closest.clear();
                            closest.add(cellID1);
                        } else {
                            if (distance == r.distance) {
                                closest.add(cellID1);
                            }
                        }
                        r.distance = r.distance.min(distance);
                    }
                }
            }
            // Go through the closest and put into an array
            r.cellIDs = new Grids_2D_ID_long[closest.size()];
            iterator = closest.iterator();
            int counter = 0;
            while (iterator.hasNext()) {
                r.cellIDs[counter] = iterator.next();
                counter++;
            }
        }
        return r;
    }

    /**
     * @param x The x-coordinate of a point.
     * @param y The y-coordinate of a point.
     * @param v The value to be added to the cell containing the point (x, y).
     */
    public void addToCell(BigDecimal x, BigDecimal y, int v) throws IOException,
            Exception, ClassNotFoundException {
        addToCell(getRow(y), getCol(x), v);
    }

    /**
     * @param i The ID of the cell to add to the value of.
     * @param v The value to be added to the cell.
     */
    public void addToCell(Grids_2D_ID_long i, int v)
            throws IOException, Exception, ClassNotFoundException {
        addToCell(i.getRow(), i.getCol(), v);
    }

    /**
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @param v The value to be added to the cell. NB1. If cell is not contained
     * in this then then returns ndv. NB2. Adding to ndv is done as if adding to
     * a cell with value of 0. TODO: Check Arithmetic
     */
    public void addToCell(long row, long col, int v) throws IOException,
            ClassNotFoundException, Exception {
        int currentValue = getCell(row, col);
        if (currentValue != NoDataValue) {
            if (v != NoDataValue) {
                setCell(row, col, currentValue + v);
            }
        } else {
            if (v != NoDataValue) {
                setCell(row, col, v);
            }
        }
    }

    /**
     * Initialises all cells with value {@code v}.
     *
     * @param v The value to initialise all the cells with.
     */
    protected void initCells(int v) throws IOException, Exception,
            ClassNotFoundException {
        Iterator<Grids_2D_ID_int> ite = chunkIDChunkMap.keySet().iterator();
        int nChunks = chunkIDChunkMap.size();
        int counter = 0;
        while (ite.hasNext()) {
            env.checkAndMaybeFreeMemory();
            System.out.println("Initialising Chunk " + counter + " out of "
                    + nChunks);
            counter++;
            Grids_2D_ID_int i = ite.next();
            Grids_ChunkInt chunk = getChunk(i);
            int chunkNRows = getChunkNRows(i);
            int chunkNCols = getChunkNCols(i);
            for (int row = 0; row <= chunkNRows; row++) {
                for (int col = 0; col <= chunkNCols; col++) {
                    chunk.initCell(chunkNRows, chunkNCols, v);
                }
            }
        }
    }

    /**
     * @return A Grids_GridIteratorInt for iterating over the cell values in
     * this.
     */
    public Grids_GridIteratorInt iterator() throws IOException, Exception,
            ClassNotFoundException {
        return new Grids_GridIteratorInt(this);
    }

    @Override
    public Grids_StatsInt getStats() {
        return (Grids_StatsInt) stats;
    }

    public void initStats(Grids_StatsInt stats) {
        this.stats = stats;
    }

    public int getCell(Grids_Chunk chunk, int chunkRow, int chunkCol,
            int cellRow, int cellCol) {
        Grids_ChunkInt c = (Grids_ChunkInt) chunk;
        if (chunk.getClass() == Grids_ChunkIntArray.class) {
            return ((Grids_ChunkIntArray) c).getCell(cellRow, cellCol);
        }
        if (chunk.getClass() == Grids_ChunkIntMap.class) {
            return ((Grids_ChunkIntMap) c).getCell(cellRow, cellCol);
        }
        return c.getGrid().NoDataValue;
    }

    @Override
    public BigDecimal getCellBigDecimal(Grids_Chunk chunk, int chunkRow,
            int chunkCol, int cellRow, int cellCol) {
        return BigDecimal.valueOf(getCell(chunk, chunkRow, chunkCol, cellRow,
                cellCol));
    }

}
