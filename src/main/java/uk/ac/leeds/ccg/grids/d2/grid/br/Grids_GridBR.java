/*
 * Copyright 2025 Andy Turner, University of Leeds.
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
package uk.ac.leeds.ccg.grids.d2.grid.br;

import ch.obermuhlner.math.big.BigRational;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.chunk.br.Grids_ChunkBR;
import uk.ac.leeds.ccg.grids.d2.chunk.br.Grids_ChunkBRFactory;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkInt;
import uk.ac.leeds.ccg.grids.d2.chunk.br.Grids_ChunkBRSinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.br.Grids_ChunkBRArray;
import uk.ac.leeds.ccg.grids.d2.chunk.br.Grids_ChunkBRMap;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsBR;
import uk.ac.leeds.ccg.grids.d2.util.Grids_Utilities;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter.Header;
import uk.ac.leeds.ccg.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.io.IO_Utilities;
import uk.ac.leeds.ccg.io.IO_Path;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.math.number.Math_BigRationalSqrt;

/**
 * Grids with {@code BigDecimal} values.
 *
 * @author Andy Turner
 * @version 1.1
 */
public class Grids_GridBR extends Grids_GridNumber {

    private static final long serialVersionUID = 1L;

    /**
     * Each cell v equal to {@code ndv} and all chunks of the same type created
     * via {@code cf}.
     *
     * @param stats What {@link #stats} is set to.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @param cf The factory preferred for creating chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param ndv The noDataValue.
     * @param ge The grids environment.
     * @throws java.io.IOException If encountered.
     */
    protected Grids_GridBR(Grids_StatsBR stats, IO_Cache fs,
            long id, Grids_ChunkBRFactory cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions,
            BigRational ndv, Grids_Environment ge) throws IOException, Exception {
        super(ge, fs, id, ndv);
        init(stats, cf, chunkNRows, chunkNCols, nRows, nCols, dimensions);
    }

    /**
     * Creates a new Grids_GridBR based on values in grid.
     *
     * @param stats What {@link #stats} is set to.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @param g The grid from which this is to be constructed.
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
     * @param ndv The noDataValue for this.
     * @throws java.io.IOException If encountered.
     */
    protected Grids_GridBR(Grids_StatsBR stats, IO_Cache fs,
            long id, Grids_Grid g, Grids_ChunkBRFactory cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, BigRational ndv) throws IOException,
            Exception {
        super(g.env, fs, id, ndv);
        init(stats, g, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, ndv);
    }

    /**
     * Creates a new Grids_GridBR with values obtained from gridFile.
     * {@code gridFile} must be a directory containing a cached instance of a
     * Grids_Number or an ESRI Asciigrid format file with a filename ending in
     * ".asc" or ".txt".
     *
     * @param stats What {@link #stats} is set to.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data for this.
     * @param cf The factory preferred to construct chunks of this.
     * @param chunkNRows The chunk NRows.
     * @param chunkNCols The chunk NCols
     * @param startRow The row of the input that will be the bottom most row of
     * this.
     * @param startCol The column of the input that will be the left most column
     * of this.
     * @param endRow The row of the input that will be the top most row of this.
     * @param endCol The column of the input that will be the right most column
     * of this.
     * @param ndv The noDataValue for this.
     * @param ge The grids environment.
     * @throws java.io.IOException If encountered.
     */
    protected Grids_GridBR(Grids_StatsBR stats, IO_Cache fs,
            long id, IO_Path gridFile, Grids_ChunkBRFactory cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, BigRational ndv, Grids_Environment ge)
            throws IOException, Exception {
        super(ge, fs, id, ndv);
        init(stats, gridFile, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, ndv);
    }

    /**
     * Creates a new Grids_GridBR with values obtained from gridFile.
     * {@code gridFile} must be a directory containing a cached instance of a
     * Grids_Number or an ESRI Asciigrid format file with a filename ending in
     * ".asc" or ".txt".
     *
     * @param ge The grids environment.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @param gridFile Either a directory, or a formatted File with a specific
     * @param ndv The noDataValue for this. extension containing the data for
     * this.
     * @throws java.io.IOException If encountered.
     */
    protected Grids_GridBR(Grids_Environment ge, IO_Cache fs, long id,
            IO_Path gridFile, BigRational ndv) throws IOException,
            Exception {
        super(ge, fs, id, ndv);
        init(new Grids_GridBRStatsNotUpdated(ge), gridFile);
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridBR from which the fields of this are set. with
     * those of g.
     * @throws java.io.IOException If encountered. *
     */
    private void init(Grids_GridBR g) throws IOException {
//        Grids_StatsBR gStats;
//        gStats = g.getStats();
//        if (gStats instanceof Grids_StatsNotUpdatedBR) {
//            stats = new Grids_StatsNotUpdatedBR(this);
//        } else {
//            stats = new Grids_GridStatisticsNotUpdatedAsDataChanged(this);
//        }
        stats = g.stats;
        super.init(g);
        data = g.data;
        setReferenceInChunks();
        worthSwapping = g.worthSwapping;
        // Set the reference to this in stats
        stats.setGrid(this);
        super.init();
    }

    @Override
    protected void init() throws IOException {
        super.init();
        if (!stats.isUpdated()) {
            ((Grids_GridBRStatsNotUpdated) stats).setUpToDate(false);
        }
        stats.grid = this;
    }

    private void init(Grids_StatsBR stats, Grids_ChunkBRFactory cf,
            int chunkNRows, int chunkNCols, long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException, Exception {
        //env.checkAndMaybeFreeMemory(this, true);
        init(stats, chunkNRows, chunkNCols, nRows, nCols, dimensions);
        for (int r = 0; r < nChunkRows; r++) {
            for (int c = 0; c < nChunkCols; c++) {
                env.checkAndMaybeFreeMemory();
                // Try to load chunk.
                Grids_2D_ID_int i = new Grids_2D_ID_int(r, c);
                //env.checkAndMaybeFreeMemory();
                Grids_ChunkBR chunk = cf.create(this, i);
                data.put(i, chunk);
                if (!(chunk instanceof Grids_ChunkBRSinglet)) {
                    worthSwapping.add(i);
                }
            }
            env.env.log("Done chunkRow " + r + " out of " + nChunkRows);
        }
        init();
    }

    /**
     *
     * @param stats
     * @param g
     * @param cf
     * @param chunkNRows
     * @param chunkNCols
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol
     * @param ndv
     */
    private void init(Grids_StatsBR stats, Grids_Grid g,
            Grids_ChunkBRFactory cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, BigRational ndv) throws IOException, ClassNotFoundException,
            Exception {
        env.checkAndMaybeFreeMemory();
        init(g, stats, chunkNRows, chunkNCols, startRow, startCol, endRow, endCol);
        boolean isLoadedChunk = false;
        int scr = g.getChunkRow(startRow);
        int ecr = g.getChunkRow(endRow);
        int ncr = ecr - scr + 1;
        int scc = g.getChunkCol(startCol);
        int ecc = g.getChunkCol(endCol);
        if (g instanceof Grids_GridBR) {
            Grids_GridBR gd = (Grids_GridBR) g;
            BigRational gndv = gd.getNoDataValue();
            BigRational gValue;
            for (int gcr = scr; gcr <= ecr; gcr++) {
                int gChunkNRows = g.getChunkNRows(gcr);
                for (int gcc = scc; gcc <= ecc; gcc++) {
                    do {
                        try {
                            // Try to load chunk.
                            Grids_2D_ID_int gi = new Grids_2D_ID_int(gcr, gcc);
                            env.addToNotToClear(g, gi);
                            env.checkAndMaybeFreeMemory();
                            Grids_ChunkBR c = gd.getChunk(gi);
                            int gChunkNCols = g.getChunkNCols(gcc);
                            for (int cr = 0; cr < gChunkNRows; cr++) {
                                long gRow = g.getRow(gcr, cr);
                                long row = gRow - startRow;
                                int chunkRow = getChunkRow(row);
                                if (gRow >= startRow && gRow <= endRow) {
                                    for (int cc = 0; cc < gChunkNCols; cc++) {
                                        long gCol = g.getCol(gcc, cc);
                                        long col = gCol - startCol;
                                        int chunkCol = getChunkCol(col);
                                        if (gCol >= startCol && gCol <= endCol) {
                                            /**
                                             * Initialise chunk if it does not
                                             * exist This is here rather than
                                             * where chunkID is initialised as
                                             * there may not be a chunk for the
                                             * chunkID.
                                             */
                                            if (isInGrid(row, col)) {
                                                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                                                //ge.addToNotToClear(this, chunkID);
                                                Grids_ChunkBR chunk;
                                                if (!data.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    data.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_ChunkBRSinglet)) {
                                                        worthSwapping.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_ChunkBR) data.get(chunkID);
                                                }
                                                gValue = gd.getCell(c, cr, cc);
                                                // Initialise v
                                                if (gValue == gndv) {
                                                    initCell(chunk, chunkID, row, col, ndv);
                                                } else {
                                                    initCell(chunk, chunkID, row, col, gValue);
                                                }
                                                //ge.removeFromNotToClear(this, chunkID);
                                            }
                                        }
                                    }
                                }
                            }
                            isLoadedChunk = true;
                            env.removeFromNotToClear(g, gi);
                        } catch (OutOfMemoryError e) {
                            if (env.HOOME) {
                                env.clearMemoryReserve(env.env);
                                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(gcr, gcc);
                                freeSomeMemoryAndResetReserve(chunkID, e);
                                if (env.swapChunksExcept_Account(this, chunkID, false).detail < 1) { // Should also not cache out the chunk of grid thats values are being used to initialise this.
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
                env.env.log("Done chunkRow " + gcr + " out of " + ncr);
            }
        } else {
            Grids_GridInt gi = (Grids_GridInt) g;
            Grids_ChunkInt c;
            int gndv = gi.getNoDataValue();
            int gValue;
            for (int gcr = scr; gcr <= ecr; gcr++) {
                int gChunkNRows = g.getChunkNRows(gcr);
                for (int gcc = scc; gcc <= ecc; gcc++) {
                    do {
                        try {
                            // Try to load chunk.
                            Grids_2D_ID_int gChunkID = new Grids_2D_ID_int(gcr, gcc);
                            env.addToNotToClear(g, gChunkID);
                            env.checkAndMaybeFreeMemory();
                            c = gi.getChunk(gChunkID);
                            int gChunkNCols = g.getChunkNCols(gcc);
                            for (int cellRow = 0; cellRow < gChunkNRows; cellRow++) {
                                long gRow = g.getRow(gcr, cellRow);
                                long row = gRow - startRow;
                                int chunkRow = getChunkRow(row);
                                if (gRow >= startRow && gRow <= endRow) {
                                    for (int cellCol = 0; cellCol < gChunkNCols; cellCol++) {
                                        long gCol = g.getCol(gcc, cellCol);
                                        long col = gCol - startCol;
                                        int chunkCol = getChunkCol(col);
                                        if (gCol >= startCol && gCol <= endCol) {
                                            /**
                                             * Initialise chunk if it does not
                                             * exist This is here rather than
                                             * where chunkID is initialised as
                                             * there may not be a chunk for the
                                             * chunkID.
                                             */
                                            if (isInGrid(row, col)) {
                                                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                                                env.addToNotToClear(this, chunkID);
                                                Grids_ChunkBR chunk;
                                                if (!data.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    data.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_ChunkBRSinglet)) {
                                                        worthSwapping.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_ChunkBR) data.get(chunkID);
                                                }
                                                gValue = gi.getCell(c, cellRow, cellCol);
                                                // Initialise v
                                                if (gValue == gndv) {
                                                    initCell(chunk, chunkID, row, col, ndv);
                                                } else {
                                                    initCell(chunk, chunkID, row, col, BigRational.valueOf(gValue));
                                                }
                                                env.removeFromNotToClear(this, chunkID);
                                            }
                                        }
                                    }
                                }
                            }
                            isLoadedChunk = true;
                            env.removeFromNotToClear(g, gChunkID);
                            env.checkAndMaybeFreeMemory();
                        } catch (OutOfMemoryError e) {
                            if (env.HOOME) {
                                env.clearMemoryReserve(env.env);
                                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(gcr, gcc);
                                if (env.swapChunksExcept_Account(this, chunkID, false).detail < 1L) {
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
                env.env.log("Done chunkRow " + gcr + " out of " + ncr);
            }
        }
        init();
    }

    private void init(Grids_StatsBR stats, IO_Path gridFile,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, BigRational noDataValue)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        // Set to report every 10%
        int reportN;
        reportN = (int) (endRow - startRow) / 10;
        if (reportN == 0) {
            reportN = 1;
        }
        if (Files.isDirectory(gridFile.getPath())) {
            if (true) {
                Grids_Processor gp = env.getProcessor();
                Grids_GridBRFactory gf = gp.gridFactoryBR;
                IO_Path thisFile = new IO_Path(getPathThisFile(gridFile));
                Grids_GridBR g = (Grids_GridBR) gf.create(
                        (Grids_Grid) IO_Utilities.readObject(thisFile));
                Grids_GridBR g2 = gf.create(g, startRow, startCol, endRow,
                        endCol);
                init(g2);
            }
        } else {
            // Assume ESRI AsciiFile
            this.chunkNRows = chunkNRows;
            this.chunkNCols = chunkNCols;
            nRows = endRow - startRow;
            nCols = endCol - startCol;
            initNoDataValue(noDataValue);
            name = fs.getBaseDir().getFileName().toString() + fsID;
            initNChunkRows();
            initNChunkCols();
            data = new TreeMap<>();
            worthSwapping = new HashSet<>();
            this.stats = stats;
            this.stats.grid = this;
            String filename = gridFile.getFileName().toString();
            BigRational value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Header header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                initDimensions(header, startRow, startCol);
                BigRational gridFileNoDataValue = header.ndv;
                long row;
                long col;
//                Grids_ChunkR chunk;
//                Grids_ChunkBRSinglet gridChunk;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue.compareTo(this.ndv) == 0) {
                    if (stats.isUpdated()) {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readBigRational();
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                env.env.log("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    } else {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readBigRational();
                                if (value == gridFileNoDataValue) {
                                    value = this.ndv;
                                }
                                initCell(row, col, value, true);
                            }
                            if (row % reportN == 0) {
                                env.env.log("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    }
                } else {
                    if (stats.isUpdated()) {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readBigRational();
                                if (value == gridFileNoDataValue) {
                                    value = this.ndv;
                                }
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                env.env.log("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    } else {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readBigRational();
                                initCell(row, col, value, true);
                            }
                            if (row % reportN == 0) {
                                env.env.log("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    }
                }
            }
        }
        init();
    }

    private void init(Grids_GridBRStats stats, IO_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        // Set to report every 10%
        int reportN;
        Grids_Processor gp = env.getProcessor();
        if (Files.isDirectory(gridFile.getPath())) {
            if (true) {
                Grids_GridBRFactory gf = gp.gridFactoryBR;
                IO_Path thisFile = new IO_Path(getPathThisFile(gridFile));
                Grids_GridBR g = (Grids_GridBR) gf.create(
                        (Grids_Grid) IO_Utilities.readObject(thisFile));
                init(g);
                //this.data = g.data;
                this.worthSwapping = g.worthSwapping;
                this.ndv = g.ndv;
                this.dim = g.dim;
                this.stats = stats;
                this.stats.grid = this;
            }
        } else {
            // Assume ESRI AsciiFile
            name = fs.getBaseDir().getFileName().toString() + fsID;
            data = new TreeMap<>();
            worthSwapping = new HashSet<>();
            this.stats = stats;
            this.stats.setGrid(this);
            String filename = gridFile.getFileName().toString();
            BigRational value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Header header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                nCols = header.ncols;
                nRows = header.nrows;
                chunkNRows = gp.gridFactoryBR.getChunkNRows();
                chunkNCols = gp.gridFactoryBR.getChunkNCols();
                initNChunkRows();
                initNChunkCols();
                initDimensions(header, 0, 0);
                reportN = (int) (nRows - 1) / 10;
                if (reportN == 0) {
                    reportN = 1;
                }
                BigRational gridFileNoDataValue = header.ndv;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue.compareTo(ndv) == 0) {
                    if (stats.isUpdated()) {
                        for (long row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (long col = 0; col < nCols; col++) {
                                value = eagi.readBigRational();
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                env.env.log("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    } else {
                        for (long row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (long col = 0; col < nCols; col++) {
                                value = eagi.readBigRational();
                                if (value == gridFileNoDataValue) {
                                    value = ndv;
                                }
                                initCell(row, col, value, true);
                            }
                            if (row % reportN == 0) {
                                env.env.log("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    }
                } else {
                    if (stats.isUpdated()) {
                        for (long row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (long col = 0; col < nCols; col++) {
                                value = eagi.readBigRational();
                                if (value == gridFileNoDataValue) {
                                    value = ndv;
                                }
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                env.env.log("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    } else {
                        for (long row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (long col = 0; col < nCols; col++) {
                                value = eagi.readBigRational();
                                if (value == gridFileNoDataValue) {
                                    value = ndv;
                                }
                                initCell(row, col, value, true);
                            }
                            if (row % reportN == 0) {
                                env.env.log("Done row " + row);
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
//    public void loadChunk(Grids_2D_ID_int chunkID) {
//        boolean isLoaded = isLoaded(chunkID);
//        if (!isLoaded) {
//            File f = new File(getDirectory(),
//                    "" + chunkID.getRow() + "_" + chunkID.getCol());
//            Object o = env.env.io.readObject(f);
//            Grids_ChunkR chunk = null;
//            if (o.getClass() == Grids_ChunkBRArray.class) {
//                Grids_ChunkBRArray c;
//                c = (Grids_ChunkBRArray) o;
//                chunk = c;
//            } else if (o.getClass() == Grids_ChunkBRMap.class) {
//                Grids_ChunkBRMap c;
//                c = (Grids_ChunkBRMap) o;
//                chunk = c;
//            } else if (o.getClass() == Grids_ChunkBRSinglet.class) {
//                Grids_ChunkBRSinglet c;
//                c = (Grids_ChunkBRSinglet) o;
//                chunk = c;
//            } else {
//                throw new Error("Unrecognised type of chunk or null "
//                        + this.getClass().getName()
//                        + ".loadChunk(ChunkID(" + chunkID.toString() + "))");
//            }
//            chunk.env = env;
//            chunk.initGrid(this);
//            chunk.initChunkID(chunkID);
//            data.put(chunkID, chunk);
//            if (!(chunk instanceof Grids_ChunkBRSinglet)) {
//                worthSwapping.add(chunkID);
//            }
//        }
//    }
    /**
     *
     * @param row
     * @param col
     * @param value
     * @param fast
     */
    private void initCell(long row, long col, BigRational value, boolean fast)
            throws IOException, ClassNotFoundException, Exception {
        Grids_2D_ID_int i = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
        env.addToNotToClear(this, i);
        if (!data.containsKey(i)) {
            Grids_ChunkBRSinglet gc = new Grids_ChunkBRSinglet(this, i, value);
            data.put(i, gc);
            if (!(gc instanceof Grids_ChunkBRSinglet)) {
                worthSwapping.add(i);
            }
        } else {
            Grids_Chunk c = data.get(i);
            if (c == null) {
                loadChunk(i);
            }
            Grids_ChunkBR chunk = getChunk(i);
            if (chunk instanceof Grids_ChunkBRSinglet) {
                Grids_ChunkBRSinglet gc = (Grids_ChunkBRSinglet) chunk;
                if (value != gc.v) {
                    // Convert chunk to another type
                    chunk = env.getProcessor().gridFactoryBR.defaultGridChunkBRFactory.create(chunk, i);
                    data.put(i, chunk);
                    if (!(chunk instanceof Grids_ChunkBRSinglet)) {
                        worthSwapping.add(i);
                    }
                    chunk.initCell(getChunkCellRow(row), getChunkCellCol(col), value);
                }
            } else {
                if (fast) {
                    initCellFast(chunk, row, col, value);
                } else {
                    initCell(chunk, i, row, col, value);
                }
            }
        }
    }

    /**
     * @return Grids_ChunkR for chunk ID {@code i}.
     * @param i The chunk ID.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_ChunkBR getChunk(Grids_2D_ID_int i)
            throws IOException, Exception, ClassNotFoundException {
        if (isInGrid(i)) {
            if (data.get(i) == null) {
                loadChunk(i);
            }
            return (Grids_ChunkBR) data.get(i);
        }
        return null;
    }

    /**
     * @param i The chunk ID.
     * @param cr The chunk row.
     * @param cc The chunk col.
     * @return The chunk for the given chunkID {@code i}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_ChunkBR getChunk(Grids_2D_ID_int i, int cr, int cc)
            throws IOException, Exception, ClassNotFoundException {
        if (isInGrid(cr, cc)) {
            if (data.get(i) == null) {
                loadChunk(i);
            }
            return (Grids_ChunkBR) data.get(i);
        }
        return null;
    }

    /**
     * If newValue and oldValue are the same then stats won't change. A test
     * might be appropriate in set cell so that this method is not called.
     *
     * @param newValue The v replacing oldValue.
     * @param oldValue The v being replaced.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected void updateStats(BigRational newValue, BigRational oldValue)
            throws IOException, Exception, ClassNotFoundException {
        Grids_GridBRStats s = getStats();
        if (s instanceof Grids_GridBRStatsNotUpdated) {
            if (newValue.compareTo(oldValue) != 0) {
                ((Grids_GridBRStatsNotUpdated) s).setUpToDate(false);
            }
        } else {
            if (newValue.compareTo(ndv) != 0) {
                if (oldValue.compareTo(ndv) != 0) {
                    s.setN(s.getN() - 1);
                    s.setSum(s.getSum().subtract(oldValue));
                    BigRational min = s.getMin(false);
                    if (oldValue.compareTo(min) == 0) {
                        s.setNMin(s.getNMin() - 1);
                    }
                    BigRational max = s.getMax(false);
                    if (oldValue.compareTo(max) == 0) {
                        s.setNMax(s.getNMax() - 1);
                    }
                }
                s.setN(s.getN() + 1);
                s.setSum(s.getSum().add(newValue));
                updateStats(newValue);
                if (s.getNMin() < 1) {
                    // The stats need recalculating
                    s.update();
                }
                if (s.getNMax() < 1) {
                    // The stats need recalculating
                    s.update();
                }
            }
        }
    }

    /**
     * @return ndv.
     */
    public final BigRational getNoDataValue() {
        return ndv;
    }

    /**
     * Initialises NoDataValue as noDataValue with the following exceptions. If
     * noDataValue is NaN or if noDataValue is Double.NEGATIVE_INFINITY or
     * Double.POSITIVE_INFINITY then NoDataValue is left as the default of
     * Integer.MIN_VALUE and a warning message is written to std.out.
     *
     * @param ndv The v ndv is initialised to.
     */
    protected final void initNoDataValue(BigRational ndv) {
        this.ndv = ndv;
    }

    /**
     * @param r The grid cell row index for which the v is returned.
     * @param c The grid cell column index for which the v is returned
     * @return The v in the grid at grid cell row index {@code r}, grid cell
     * column index {@code c} or {@link #ndv} if there is no such v.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigRational getCell(long r, long c) throws IOException, Exception,
            ClassNotFoundException {
        if (isInGrid(r, c)) {
            return getCell((Grids_ChunkBR) getChunk(getChunkRow(r),
                    getChunkCol(c)), getChunkCellRow(r), getChunkCellCol(c));
        }
        return ndv;
    }

    /**
     * For getting the v in chunk at chunk cell row {@code r}, chunk cell col
     * {@code c}.
     *
     * @param chunk The chunk.
     * @param r The chunk cell row index of the v returned.
     * @param c The chunk cell column index of the v returned.
     * @return v in chunk at chunk cell row {@code r}, chunk cell col {@code c}
     * or {@link #ndv} if there is no such v.
     */
    public BigRational getCell(Grids_ChunkBR chunk, int r, int c) {
        if (chunk.inChunk(r, c)) {
            return chunk.getCell(r, c);
        }
        return ndv;
    }

    /**
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return The v at (x, y) or {@link #ndv} if there is no such v.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final BigRational getCell(BigRational x, BigRational y) throws IOException,
            ClassNotFoundException, Exception {
        return getCell(getRow(y), getCol(x));
    }

    /**
     * @param i The cell ID.
     * @return The v of the cell with cell ID {@code i} or {@link #ndv} if there
     * is no such cell in the grid.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final BigRational getCell(Grids_2D_ID_long i) throws IOException,
            ClassNotFoundException, Exception {
        return getCell(i.getRow(), i.getCol());
    }

    /**
     * For setting the v at x-coordinate {@code x}, y-coordinate {@code y}.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param v The v to set in the cell.
     * @return The v at x-coordinate {@code x}, y-coordinate {@code y} or
     * {@link #ndv} if there is no such v.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public final BigRational setCell(BigRational x, BigRational y, BigRational v)
            throws IOException, Exception, ClassNotFoundException, Exception {
        if (isInGrid(x, y)) {
            return setCell(getRow(y), getCol(x), v);
        }
        return ndv;
    }

    /**
     * For setting the v at cell row index {@code r}, cell column index
     * {@code c}.
     *
     * @param r The cell row index of the v to set.
     * @param c The cell column index of the v to set.
     * @param v The v to set at cell row index {@code r}, cell column index
     * {@code c}.
     * @return The v at cell row index {@code r}, cell column index {@code c} or
     * {@link #ndv} if there is no such v.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigRational setCell(long r, long c, BigRational v)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(r, c)) {
            return setCell((Grids_ChunkBR) getChunk(getChunkRow(r),
                    getChunkCol(c)), getChunkCellRow(r), getChunkCellCol(c), v);
        }
        return ndv;
    }

    /**
     * For setting the v in chunk ({@code cr}, {@code cc}) at chunk cell row
     * {@code ccr}, chunk cell column (@code ccc}.
     *
     * @param cr The chunk row of the chunk in which the v is set.
     * @param cc The chunk column of the chunk in which the v is set.
     * @param ccr The chunk cell row of the v to set.
     * @param ccc The chunk cell column of the v to set.
     * @param v The v to set in chunk ({@code cr}, {@code cc}) at chunk cell row
     * {@code ccr}, chunk cell column (@code ccc}.
     * @return The v in chunk ({@code cr}, {@code cc}) at chunk cell row
     * {@code ccr}, chunk cell column (@code ccc}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigRational setCell(int cr, int cc, int ccr, int ccc, BigRational v)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(cr, cc, ccr, ccc)) {
            return setCell((Grids_ChunkBR) getChunk(cr, cc), ccr, ccc, v);
        }
        return ndv;
    }

    /**
     * For setting the v in chunk at chunk cell row {@code ccr}, chunk cell
     * column (@code ccc}.
     *
     * @param chunk The chunk in which the v is to be set.
     * @param ccr The row in chunk of the v to set.
     * @param ccc The column in chunk of the v to set.
     * @param v The v to set in chunk at chunk cell row {@code ccr}, chunk cell
     * column (@code ccc}.
     * @return The v in chunk at chunk cell row {@code ccr}, chunk cell column
     * (@code ccc}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigRational setCell(Grids_ChunkBR chunk, int ccr, int ccc, BigRational v)
            throws IOException, Exception, ClassNotFoundException {
        BigRational r = ndv;
        if (chunk instanceof Grids_ChunkBRArray) {
            r = ((Grids_ChunkBRArray) chunk).setCell(ccr, ccc, v);
        } else if (chunk instanceof Grids_ChunkBRMap) {
            r = ((Grids_ChunkBRMap) chunk).setCell(ccr, ccc, v);
        } else {
            Grids_ChunkBRSinglet c = (Grids_ChunkBRSinglet) chunk;
            if (c != null) {
                if (v.compareTo(c.v) != 0) {
                    // Convert chunk to another type
                    chunk = convertToAnotherTypeOfChunk(chunk, c.getId());
                    r = chunk.setCell(ccr, ccc, v);
                } else {
                    r = c.v;
                }
            }
        }
        // Update stats
        if (v.compareTo(r) != 0) {
            if (stats.isUpdated()) {
                updateStats(v, r);
            }
        }
        return r;
    }

    /**
     * Convert chunk to another type of chunk.
     */
    private Grids_ChunkBR convertToAnotherTypeOfChunk(
            Grids_ChunkBR chunk, Grids_2D_ID_int chunkID)
            throws IOException, ClassNotFoundException, Exception {
        Grids_ChunkBR r;
        Grids_ChunkBRFactory f = env.getProcessor().gridFactoryBR.defaultGridChunkBRFactory;
        r = f.create(chunk, chunkID);
        data.put(chunkID, r);
        if (!(chunk instanceof Grids_ChunkBRSinglet)) {
            worthSwapping.add(chunkID);
        }
        return r;
    }

    /**
     * Initialises the v in chunk at row, col.
     *
     * @param chunk The chunk.
     * @param i The chunk ID.
     * @param row The row.
     * @param col The col.
     * @param v The v.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected void initCell(Grids_ChunkBR chunk, Grids_2D_ID_int i,
            long row, long col, BigRational v) throws IOException,
            ClassNotFoundException, Exception {
        if (chunk instanceof Grids_ChunkBRSinglet) {
            Grids_ChunkBRSinglet gc = (Grids_ChunkBRSinglet) chunk;
            if (v.compareTo(gc.v) != 0) {
                chunk = convertToAnotherTypeOfChunk(chunk, i);
                chunk.initCell(getChunkCellRow(row), getChunkCellCol(col), v);
            } else {
                return;
            }
        } else {
            if (chunk != null) {
                chunk.initCell(getChunkCellRow(row), getChunkCellCol(col), v);
            }
        }
        // Update stats
        if (v.compareTo(ndv) != 0) {
            if (!(stats instanceof Grids_GridBRStatsNotUpdated)) {
                updateStats(v);
            }
        }
    }

    /**
     * updateStats
     *
     * @param value The value for the update.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    protected void updateStats(BigRational value) throws IOException, Exception,
            ClassNotFoundException {
        Grids_GridBRStats dStats = getStats();
        dStats.setN(dStats.getN() + 1);
        dStats.setSum(dStats.getSum().add(value));
        BigRational min = dStats.getMin(false);
        if (value.compareTo(min) == -1) {
            dStats.setNMin(1);
            dStats.setMin(value);
        } else {
            if (value.compareTo(min) == 0) {
                dStats.setNMin(dStats.getNMin() + 1);
            }
        }
        BigRational max = dStats.getMax(false);
        if (value.compareTo(max) == 1) {
            dStats.setNMax(1);
            dStats.setMax(value);
        } else {
            if (value.compareTo(max) == 0) {
                dStats.setNMax(dStats.getNMax() + 1);
            }
        }
    }

    /**
     * Initialises the v at row, col and does nothing about stats.
     *
     * @param chunk The chunk.
     * @param row The row.
     * @param col The column.
     * @param value The v.
     */
    protected void initCellFast(Grids_ChunkBR chunk, long row,
            long col, BigRational value) {
//        int chunkRow = getChunkRow(row);
//        int chunkCol = getChunkCol(col);
//        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
//        Grids_ChunkR chunk = getChunk(chunkID);
        chunk.initCell(getChunkCellRow(row), getChunkCellCol(col), value);
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
     * @param oom The Order of Magnitude for the precision.
     * @param rm The RoundingMode for any rounding.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    protected BigRational[] getCells(BigRational x, BigRational y,
            Math_BigRationalSqrt distance, int oom, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        return getCells(x, y, getRow(y), getCol(x), distance, oom, rm);
    }

    /**
     * @return BigRational[] of all cell values for cells that's centroids are
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
     * @param oom The Order of Magnitude for the precision.
     * @param rm The RoundingMode for any rounding.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected BigRational[] getCells(BigRational x, BigRational y,
            long row, long col, Math_BigRationalSqrt distance, int oom,
            RoundingMode rm) throws IOException, Exception, ClassNotFoundException {
        int delta = getCellDistance(distance, oom, rm);
        BigRational[] r = new BigRational[((2 * delta) + 1) * ((2 * delta) + 1)];
        int count = 0;
        for (long p = row - delta; p <= row + delta; p++) {
            BigRational thisY = getCellY(row);
            for (long q = col - delta; q <= col + delta; q++) {
                BigRational thisX = getCellX(col);
                if (Grids_Utilities.distance2(x, y, thisX, thisY)
                        .compareTo(distance.getX()) != 1) {
                    r[count] = getCell(p, q);
                    count++;
                }
            }
        }
        // Trim cells
        System.arraycopy(r, 0, r, 0, count);
        return r;
    }

    /**
     * @return NearestValuesCellIDsAndDistance - The cell IDs of the nearest
     * cells with data values nearest to a point with position given by:
     * x-coordinate x, y-coordinate y.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param oom The Order of Magnitude for the precision used in distance
     * calculations.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            BigRational x, BigRational y, int oom, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        BigRational value = getCell(x, y);
        if (value.compareTo(ndv) == 0) {
            return getNearestValuesCellIDsAndDistance(x, y, getRow(y),
                    getCol(x), oom, rm);
        }
        r.cellIDs = new Grids_2D_ID_long[1];
        r.cellIDs[0] = getCellID(x, y);
        r.distance = Math_BigRationalSqrt.ZERO;
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
     * @param oom The Order of Magnitude for the precision used in distance
     * calculations.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            long row, long col, int oom, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        BigRational value = getCell(row, col);
        if (value.compareTo(ndv) == 0) {
            return getNearestValuesCellIDsAndDistance(getCellX(col),
                    getCellY(row), row, col, oom, rm);
        }
        r.cellIDs = new Grids_2D_ID_long[1];
        r.cellIDs[0] = getCellID(row, col);
        r.distance = Math_BigRationalSqrt.ZERO;
        return r;
    }

    /**
     * @return NearestValuesCellIDsAndDistance - The ccll IDs of the nearest
     * cells with data values nearest to a point with position given by:
     * x-coordinate x, y-coordinate y; in cell row index {@code row}, cell
     * column index {@code col}.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col The column index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param oom The Order of Magnitude for the precision used in distance
     * calculations.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            BigRational x, BigRational y, long row, long col, int oom,
            RoundingMode rm) throws IOException, Exception, ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        r.cellIDs = new Grids_2D_ID_long[1];
        r.cellIDs[0] = getNearestCellID(x, y, row, col);
        BigRational nearestCellValue = getCell(row, col);
        if (nearestCellValue.compareTo(ndv) == 0) {
            // Find a v Seeking outwards from nearestCellID
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
            BigRational value;
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
                    if (value.compareTo(ndv) != 0) {
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
            BigRational distance2;
            // Go through values and find the closest
            HashSet<Grids_2D_ID_long> closest = new HashSet<>();
            iterator = values.iterator();
            Grids_2D_ID_long cellID = iterator.next();
            r.distance = Grids_Utilities.distance(x, y, getCellX(cellID),
                    getCellY(cellID), oom, rm);
            while (iterator.hasNext()) {
                cellID = iterator.next();
                distance2 = Grids_Utilities.distance2(x, y, getCellX(cellID),
                        getCellY(cellID));
                if (distance2.compareTo(r.distance.getX()) == -1) {
                    closest.clear();
                    closest.add(cellID);
                } else {
                    if (distance2.compareTo(r.distance.getX()) == 0) {
                        closest.add(cellID);
                    }
                }
                BigRational d2 = r.distance.getX().min(distance2);
                r.distance = new Math_BigRationalSqrt(d2.pow(2), d2);
            }
            // Get cellIDs that are within distance of discovered v
            Grids_2D_ID_long[] cellIDs = getCellIDs(x, y, r.distance, oom, rm);
            for (Grids_2D_ID_long cellID1 : cellIDs) {
                if (!visitedSet.contains(cellID1)) {
                    if (getCell(cellID1).compareTo(ndv) != 0) {
                        distance2 = Grids_Utilities.distance2(x, y,
                                getCellX(cellID1),
                                getCellY(cellID1));
                        if (distance2.compareTo(r.distance.getX()) == -1) {
                            closest.clear();
                            closest.add(cellID1);
                        } else {
                            if (distance2.compareTo(r.distance.getX()) == 0) {
                                closest.add(cellID1);
                            }
                        }
                        r.distance = Math_BigRationalSqrt.min(r.distance,
                                new Math_BigRationalSqrt(distance2, oom, rm));
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
     * @param v The v to be added to the cell containing the point (x, y).
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public void addToCell(BigRational x, BigRational y, BigRational v)
            throws IOException, Exception, ClassNotFoundException {
        addToCell(getRow(y), getCol(x), v);
    }

    /**
     * @param cellID The ID of the cell to add to the v of.
     * @param v The v to be added to the cell.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public void addToCell(Grids_2D_ID_long cellID, BigRational v)
            throws IOException, Exception, ClassNotFoundException {
        addToCell(cellID.getRow(), cellID.getCol(), v);
    }

    /**
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @param v The v to be added to the cell. NB1. If cell is not contained in
     * this then then returns ndv. NB2. Adding to ndv is done as if adding to a
     * cell with v of 0. TODO: Check Arithmetic
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public void addToCell(long row, long col, BigRational v) throws IOException,
            ClassNotFoundException, Exception {
        BigRational currentValue = getCell(row, col);
        if (currentValue.compareTo(ndv) != 0) {
            if (v.compareTo(ndv) != 0) {
                setCell(row, col, currentValue.add(v));
            }
        } else {
            if (v.compareTo(ndv) != 0) {
                setCell(row, col, v);
            }
        }
    }

    /**
     * Initialises all cells with v {@code v}.
     *
     * @param v The v to initialise all the cells with.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected void initCells(BigRational v) throws IOException, Exception,
            ClassNotFoundException {
        Iterator<Grids_2D_ID_int> ite = data.keySet().iterator();
        int nChunks = data.size();
        int counter = 0;
        while (ite.hasNext()) {
            env.checkAndMaybeFreeMemory();
            env.env.log("Initialising Chunk " + counter + " out of " + nChunks);
            counter++;
            Grids_2D_ID_int i = ite.next();
            Grids_ChunkBR chunk = getChunk(i);
            int cnr = getChunkNRows(i);
            int cnc = getChunkNCols(i);
            for (int row = 0; row <= cnr; row++) {
                for (int col = 0; col <= cnc; col++) {
                    chunk.initCell(cnr, cnc, v);
                }
            }
        }
    }

    /**
     * @return A Grids_GridBRIterator for iterating over the cell values in
     * this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridBRIterator iterator() throws IOException, Exception,
            ClassNotFoundException {
        return new Grids_GridBRIterator(this);
    }

    @Override
    public Grids_GridBRStats getStats() {
        return (Grids_GridBRStats) stats;
    }

    /**
     * initStatistics
     *
     * @param stats What {@link #stats} is set to.
     */
    public void initStatistics(Grids_StatsBR stats) {
        this.stats = stats;
    }

    /**
     * getCell
     *
     * @param chunk chunk
     * @param chunkRow chunkRow
     * @param chunkCol chunkCol
     * @param cellRow cellRow
     * @param cellCol cellCol
     * @return The cell.
     */
    public BigRational getCell(Grids_Chunk chunk, int chunkRow, int chunkCol,
            int cellRow, int cellCol) {
        Grids_ChunkBR c = (Grids_ChunkBR) chunk;
        if (chunk.getClass() == Grids_ChunkBRArray.class) {
            return ((Grids_ChunkBRArray) c).getCell(cellRow, cellCol);
        }
        if (chunk.getClass() == Grids_ChunkBRMap.class) {
            return ((Grids_ChunkBRMap) c).getCell(cellRow, cellCol);
        }
        return c.getGrid().ndv;
    }

    @Override
    public BigRational getCellBigRational(Grids_Chunk chunk, int cr, int cc,
            int ccr, int ccc) {
        return getCell(chunk, cr, cc, ccr, ccc);
    }

    /**
     * Test if this and {@code g} have the same dimensions, the same number of
     * rows and columns, and the same values in each cell. The chunks are
     * allowed to be stored differently as are the statistics. The no data value
     * may also be different so long as this is distinct from all other values
     * (currently no check is done on the distinctiveness of no data values).
     *
     * @param g The grid to test if it has the same dimensions and values as
     * this.
     * @return {@code true} if this and {@code g} have the same dimensions, the
     * same number of rows and columns, and the same values in each cell.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public boolean isSameDimensionsAndValues(Grids_Grid g) throws IOException,
            Exception {
        if (!(g instanceof Grids_GridBR)) {
            return false;
        }
        if (!isSameDimensions(g)) {
            return false;
        }
        Grids_GridBR gd = (Grids_GridBR) g;
        BigRational gndv = gd.getNoDataValue();
        for (int cr = 0; cr < this.nChunkRows; cr++) {
            int cnr = gd.getChunkNRows(cr);
            for (int cc = 0; cc < this.nChunkCols; cc++) {
                int cnc = gd.getChunkNCols(cc);
                Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                env.addToNotToClear(gd, i);
                // Add to not to clear a row of this chunks.
                long rowMin = gd.getRow(cr, 0);
                long rowMax = gd.getRow(cr, cnr);
                long colMin = gd.getCol(cc, 0);
                long colMax = gd.getCol(cc, cnc);
                Set<Grids_2D_ID_int> s = getChunkIDs(rowMin, rowMax, colMin,
                        colMax);
                env.addToNotToClear(this, s);
                env.checkAndMaybeFreeMemory();
                Grids_ChunkBR chunk = (Grids_ChunkBR) g.getChunk(i, cr, cc);
                for (int ccr = 0; ccr < cnr; ccr++) {
                    long row = gd.getRow(cr, ccr);
                    for (int ccc = 0; ccc < cnc; ccc++) {
                        long col = gd.getCol(cc, ccc);
                        BigRational v = getCell(row, col);
                        //BigRational gv = getCell(chunk, cr, cc, ccr, ccc);
                        BigRational gv = chunk.getCell(ccr, ccc);
                        if (v.compareTo(ndv) == 0) {
                            if (gv.compareTo(gndv) != 0) {
                                return false;
                            }
                        } else {
                            if (gv.compareTo(gndv) == 0) {
                                return false;
                            } else {
                                if (v.compareTo(gv) != 0) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                env.removeFromNotToClear(gd, i);
                env.removeFromNotToClear(this, s);
            }
        }
        return true;
    }
}
