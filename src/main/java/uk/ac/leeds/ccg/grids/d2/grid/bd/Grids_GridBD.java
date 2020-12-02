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
package uk.ac.leeds.ccg.grids.d2.grid.bd;

import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.TreeMap;
import uk.ac.leeds.ccg.generic.io.Generic_IO;
import uk.ac.leeds.ccg.generic.io.Generic_Path;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.chunk.bd.Grids_ChunkBD;
import uk.ac.leeds.ccg.grids.d2.chunk.bd.Grids_ChunkFactoryBD;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkInt;
import uk.ac.leeds.ccg.grids.d2.chunk.bd.Grids_ChunkBDSinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.bd.Grids_ChunkBDArray;
import uk.ac.leeds.ccg.grids.d2.chunk.bd.Grids_ChunkBDMap;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsBD;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsNotUpdatedBD;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter.Header;
import uk.ac.leeds.ccg.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.grids.d2.util.Grids_Utilities;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Set;
import uk.ac.leeds.ccg.generic.io.Generic_FileStore;

/**
 * Grids with {@code BigDecimal} values.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridBD extends Grids_GridNumber {

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
    protected Grids_GridBD(Grids_StatsBD stats, Generic_FileStore fs,
            long id, Grids_ChunkFactoryBD cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions,
            BigDecimal ndv, Grids_Environment ge) throws IOException, Exception {
        super(ge, fs, id, ndv);
        init(stats, cf, chunkNRows, chunkNCols, nRows, nCols, dimensions, ndv);
    }

    /**
     * Creates a new Grids_GridBD based on values in grid.
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
    protected Grids_GridBD(Grids_StatsBD stats, Generic_FileStore fs,
            long id, Grids_Grid g, Grids_ChunkFactoryBD cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, BigDecimal ndv) throws IOException,
            Exception {
        super(g.env, fs, id, ndv);
        init(stats, g, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, ndv);
    }

    /**
     * Creates a new Grids_GridBD with values obtained from gridFile.
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
    protected Grids_GridBD(Grids_StatsBD stats, Generic_FileStore fs,
            long id, Generic_Path gridFile, Grids_ChunkFactoryBD cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, BigDecimal ndv, Grids_Environment ge)
            throws IOException, Exception {
        super(ge, fs, id, ndv);
        init(stats, gridFile, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, ndv);
    }

    /**
     * Creates a new Grids_GridBD with values obtained from gridFile.
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
    protected Grids_GridBD(Grids_Environment ge, Generic_FileStore fs, long id,
            Generic_Path gridFile, BigDecimal ndv) throws IOException,
            Exception {
        super(ge, fs, id, ndv);
        init(new Grids_StatsNotUpdatedBD(ge), gridFile);
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridBD from which the fields of this are set. with
     * those of g.
     * @throws java.io.IOException If encountered. *
     */
    private void init(Grids_GridBD g) throws IOException {
//        Grids_StatsBD gStats;
//        gStats = g.getStats();
//        if (gStats instanceof Grids_StatsNotUpdatedBD) {
//            stats = new Grids_StatsNotUpdatedBD(this);
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
            ((Grids_StatsNotUpdatedBD) stats).setUpToDate(false);
        }
        stats.grid = this;
    }

    private void init(Grids_StatsBD stats,
            Grids_ChunkFactoryBD chunkFactory, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions,
            BigDecimal noDataValue) throws IOException, Exception {
        //env.checkAndMaybeFreeMemory(this, true);
        init(stats, chunkNRows, chunkNCols, nRows, nCols, dimensions);
        for (int r = 0; r < nChunkRows; r++) {
            for (int c = 0; c < nChunkCols; c++) {
                env.checkAndMaybeFreeMemory();
                // Try to load chunk.
                Grids_2D_ID_int i = new Grids_2D_ID_int(r, c);
                //env.checkAndMaybeFreeMemory();
                Grids_ChunkBD chunk = chunkFactory.create(this, i);
                data.put(i, chunk);
                if (!(chunk instanceof Grids_ChunkBDSinglet)) {
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
    private void init(Grids_StatsBD stats, Grids_Grid g,
            Grids_ChunkFactoryBD cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, BigDecimal ndv) throws IOException, ClassNotFoundException,
            Exception {
        env.checkAndMaybeFreeMemory();
        init(g, stats, chunkNRows, chunkNCols, startRow, startCol, endRow, endCol);
        boolean isLoadedChunk = false;
        int scr = g.getChunkRow(startRow);
        int ecr = g.getChunkRow(endRow);
        int ncr = ecr - scr + 1;
        int scc = g.getChunkCol(startCol);
        int ecc = g.getChunkCol(endCol);
        if (g instanceof Grids_GridBD) {
            Grids_GridBD gd = (Grids_GridBD) g;
            BigDecimal gndv = gd.getNoDataValue();
            BigDecimal gValue;
            for (int gcr = scr; gcr <= ecr; gcr++) {
                int gChunkNRows = g.getChunkNRows(gcr);
                for (int gcc = scc; gcc <= ecc; gcc++) {
                    do {
                        try {
                            // Try to load chunk.
                            Grids_2D_ID_int gi = new Grids_2D_ID_int(gcr, gcc);
                            env.addToNotToClear(g, gi);
                            env.checkAndMaybeFreeMemory();
                            Grids_ChunkBD c = gd.getChunk(gi);
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
                                                Grids_ChunkBD chunk;
                                                if (!data.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    data.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_ChunkBDSinglet)) {
                                                        worthSwapping.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_ChunkBD) data.get(chunkID);
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
                                                Grids_ChunkBD chunk;
                                                if (!data.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    data.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_ChunkBDSinglet)) {
                                                        worthSwapping.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_ChunkBD) data.get(chunkID);
                                                }
                                                gValue = gi.getCell(c, cellRow, cellCol);
                                                // Initialise v
                                                if (gValue == gndv) {
                                                    initCell(chunk, chunkID, row, col, ndv);
                                                } else {
                                                    initCell(chunk, chunkID, row, col, BigDecimal.valueOf(gValue));
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

    private void init(Grids_StatsBD stats, Generic_Path gridFile,
            Grids_ChunkFactoryBD cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, BigDecimal noDataValue) throws IOException,
            ClassNotFoundException, Exception {
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
                Grids_GridFactoryBD gf = gp.gridFactoryBD;
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                Grids_GridBD g = (Grids_GridBD) gf.create(
                        (Grids_Grid) Generic_IO.readObject(thisFile));
                Grids_GridBD g2 = gf.create(g, startRow, startCol, endRow,
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
            BigDecimal value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Header header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                initDimensions(header, startRow, startCol);
                BigDecimal gridFileNoDataValue = header.ndv;
                long row;
                long col;
//                Grids_ChunkBD chunk;
//                Grids_ChunkBDSinglet gridChunk;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue.compareTo(this.ndv) == 0) {
                    if (stats.isUpdated()) {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readBigDecimal();
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
                                value = eagi.readBigDecimal();
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
                                value = eagi.readBigDecimal();
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
                                value = eagi.readBigDecimal();
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

    private void init(Grids_StatsBD stats, Generic_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        // Set to report every 10%
        int reportN;
        Grids_Processor gp = env.getProcessor();
        if (Files.isDirectory(gridFile.getPath())) {
            if (true) {
                Grids_GridFactoryBD gf = gp.gridFactoryBD;
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                Grids_GridBD g = (Grids_GridBD) gf.create(
                        (Grids_Grid) Generic_IO.readObject(thisFile));
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
            BigDecimal value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Header header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                nCols = header.ncols;
                nRows = header.nrows;
                chunkNRows = gp.gridFactoryBD.getChunkNRows();
                chunkNCols = gp.gridFactoryBD.getChunkNCols();
                initNChunkRows();
                initNChunkCols();
                initDimensions(header, 0, 0);
                reportN = (int) (nRows - 1) / 10;
                if (reportN == 0) {
                    reportN = 1;
                }
                BigDecimal gridFileNoDataValue = header.ndv;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue.compareTo(ndv) == 0) {
                    if (stats.isUpdated()) {
                        for (long row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (long col = 0; col < nCols; col++) {
                                value = eagi.readBigDecimal();
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
                                value = eagi.readBigDecimal();
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
                                value = eagi.readBigDecimal();
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
                                value = eagi.readBigDecimal();
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
//            Grids_ChunkBD chunk = null;
//            if (o.getClass() == Grids_ChunkBDArray.class) {
//                Grids_ChunkBDArray c;
//                c = (Grids_ChunkBDArray) o;
//                chunk = c;
//            } else if (o.getClass() == Grids_ChunkBDMap.class) {
//                Grids_ChunkBDMap c;
//                c = (Grids_ChunkBDMap) o;
//                chunk = c;
//            } else if (o.getClass() == Grids_ChunkBDSinglet.class) {
//                Grids_ChunkBDSinglet c;
//                c = (Grids_ChunkBDSinglet) o;
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
//            if (!(chunk instanceof Grids_ChunkBDSinglet)) {
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
    private void initCell(long row, long col, BigDecimal value, boolean fast)
            throws IOException, ClassNotFoundException, Exception {
        Grids_2D_ID_int i = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
        env.addToNotToClear(this, i);
        if (!data.containsKey(i)) {
            Grids_ChunkBDSinglet gc = new Grids_ChunkBDSinglet(this, i, value);
            data.put(i, gc);
            if (!(gc instanceof Grids_ChunkBDSinglet)) {
                worthSwapping.add(i);
            }
        } else {
            Grids_Chunk c = data.get(i);
            if (c == null) {
                loadChunk(i);
            }
            Grids_ChunkBD chunk = getChunk(i);
            if (chunk instanceof Grids_ChunkBDSinglet) {
                Grids_ChunkBDSinglet gc = (Grids_ChunkBDSinglet) chunk;
                if (value != gc.v) {
                    // Convert chunk to another type
                    chunk = env.getProcessor().gridFactoryBD.defaultGridChunkBDFactory.create(chunk, i);
                    data.put(i, chunk);
                    if (!(chunk instanceof Grids_ChunkBDSinglet)) {
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
     * @return Grids_ChunkBD for chunk ID {@code i}.
     * @param i The chunk ID.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_ChunkBD getChunk(Grids_2D_ID_int i)
            throws IOException, Exception, ClassNotFoundException {
        if (isInGrid(i)) {
            if (data.get(i) == null) {
                loadChunk(i);
            }
            return (Grids_ChunkBD) data.get(i);
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
    public Grids_ChunkBD getChunk(Grids_2D_ID_int i, int cr, int cc)
            throws IOException, Exception, ClassNotFoundException {
        if (isInGrid(cr, cc)) {
            if (data.get(i) == null) {
                loadChunk(i);
            }
            return (Grids_ChunkBD) data.get(i);
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
    protected void updateStats(BigDecimal newValue, BigDecimal oldValue)
            throws IOException, Exception, ClassNotFoundException {
        Grids_StatsBD dStats = getStats();
        if (dStats.getClass() == Grids_StatsBD.class) {
            if (newValue.compareTo(ndv) != 0) {
                if (oldValue.compareTo(ndv) != 0) {
                    dStats.setN(dStats.getN() - 1);
                    dStats.setSum(dStats.getSum().subtract(oldValue));
                    BigDecimal min = dStats.getMin(false);
                    if (oldValue.compareTo(min) == 0) {
                        dStats.setNMin(dStats.getNMin() - 1);
                    }
                    BigDecimal max = dStats.getMax(false);
                    if (oldValue.compareTo(max) == 0) {
                        dStats.setNMax(dStats.getNMax() - 1);
                    }
                }
                dStats.setN(dStats.getN() + 1);
                dStats.setSum(dStats.getSum().add(newValue));
                updateStats(newValue);
                if (dStats.getNMin() < 1) {
                    // The stats need recalculating
                    dStats.update();
                }
                if (dStats.getNMax() < 1) {
                    // The stats need recalculating
                    dStats.update();
                }
            }
        } else {
            if (newValue.compareTo(oldValue) != 0) {
                ((Grids_StatsNotUpdatedBD) dStats).setUpToDate(false);
            }
        }
    }

    /**
     * @return ndv.
     */
    public final BigDecimal getNoDataValue() {
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
    protected final void initNoDataValue(BigDecimal ndv) {
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
    public BigDecimal getCell(long r, long c) throws IOException, Exception,
            ClassNotFoundException {
        if (isInGrid(r, c)) {
            return getCell((Grids_ChunkBD) getChunk(getChunkRow(r),
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
    public BigDecimal getCell(Grids_ChunkBD chunk, int r, int c) {
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
    public final BigDecimal getCell(BigDecimal x, BigDecimal y) throws IOException,
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
    public final BigDecimal getCell(Grids_2D_ID_long i) throws IOException,
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
    public final BigDecimal setCell(BigDecimal x, BigDecimal y, BigDecimal v)
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
    public BigDecimal setCell(long r, long c, BigDecimal v)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(r, c)) {
            return setCell((Grids_ChunkBD) getChunk(getChunkRow(r),
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
    public BigDecimal setCell(int cr, int cc, int ccr, int ccc, BigDecimal v)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(cr, cc, ccr, ccc)) {
            return setCell((Grids_ChunkBD) getChunk(cr, cc), ccr, ccc, v);
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
    public BigDecimal setCell(Grids_ChunkBD chunk, int ccr, int ccc, BigDecimal v)
            throws IOException, Exception, ClassNotFoundException {
        BigDecimal r = ndv;
        if (chunk instanceof Grids_ChunkBDArray) {
            r = ((Grids_ChunkBDArray) chunk).setCell(ccr, ccc, v);
        } else if (chunk instanceof Grids_ChunkBDMap) {
            r = ((Grids_ChunkBDMap) chunk).setCell(ccr, ccc, v);
        } else {
            Grids_ChunkBDSinglet c = (Grids_ChunkBDSinglet) chunk;
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
    private Grids_ChunkBD convertToAnotherTypeOfChunk(
            Grids_ChunkBD chunk, Grids_2D_ID_int chunkID)
            throws IOException, ClassNotFoundException, Exception {
        Grids_ChunkBD r;
        Grids_ChunkFactoryBD f = env.getProcessor().gridFactoryBD.defaultGridChunkBDFactory;
        r = f.create(chunk, chunkID);
        data.put(chunkID, r);
        if (!(chunk instanceof Grids_ChunkBDSinglet)) {
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
    protected void initCell(Grids_ChunkBD chunk, Grids_2D_ID_int i,
            long row, long col, BigDecimal v) throws IOException,
            ClassNotFoundException, Exception {
        if (chunk instanceof Grids_ChunkBDSinglet) {
            Grids_ChunkBDSinglet gc = (Grids_ChunkBDSinglet) chunk;
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
            if (!(stats instanceof Grids_StatsNotUpdatedBD)) {
                updateStats(v);
            }
        }
    }

    protected void updateStats(BigDecimal value) throws IOException, Exception,
            ClassNotFoundException {
        Grids_StatsBD dStats = getStats();
        dStats.setN(dStats.getN() + 1);
        dStats.setSum(dStats.getSum().add(value));
        BigDecimal min = dStats.getMin(false);
        if (value.compareTo(min) == -1) {
            dStats.setNMin(1);
            dStats.setMin(value);
        } else {
            if (value.compareTo(min) == 0) {
                dStats.setNMin(dStats.getNMin() + 1);
            }
        }
        BigDecimal max = dStats.getMax(false);
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
    protected void initCellFast(Grids_ChunkBD chunk, long row,
            long col, BigDecimal value) {
//        int chunkRow = getChunkRow(row);
//        int chunkCol = getChunkCol(col);
//        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
//        Grids_ChunkBD chunk = getChunk(chunkID);
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
     * @param dp The number of decimal places used in distance calculations.
     * @param rm The {@link RoundingMode} to use when rounding distance
     * calculations.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    protected BigDecimal[] getCells(BigDecimal x, BigDecimal y,
            BigDecimal distance, int dp, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        return getCells(x, y, getRow(y), getCol(x), distance, dp, rm);
    }

    /**
     * @return BigDecimal[] of all cell values for cells thats centroids are
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
     * @param dp The number of decimal places used in distance calculations.
     * @param rm The {@link RoundingMode} to use when rounding distance
     * calculations.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected BigDecimal[] getCells(BigDecimal x, BigDecimal y, long row, long col,
            BigDecimal distance, int dp, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        BigDecimal[] cells;
        BigDecimal[] dar = distance.divideAndRemainder(getCellsize());
        int delta = dar[0].intValueExact();
        if (dar[1].compareTo(BigDecimal.ZERO) == 1) {
            delta += 1;
        }
        cells = new BigDecimal[((2 * delta) + 1) * ((2 * delta) + 1)];
        int count = 0;
        for (long p = row - delta; p <= row + delta; p++) {
            BigDecimal thisY = getCellY(row);
            for (long q = col - delta; q <= col + delta; q++) {
                BigDecimal thisX = getCellX(col);
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            BigDecimal x, BigDecimal y, int dp, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        BigDecimal value = getCell(x, y);
        if (value.compareTo(ndv) == 0) {
            return getNearestValuesCellIDsAndDistance(x, y, getRow(y),
                    getCol(x), dp, rm);
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            long row, long col, int dp, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        BigDecimal value = getCell(row, col);
        if (value.compareTo(ndv) == 0) {
            return getNearestValuesCellIDsAndDistance(getCellX(col),
                    getCellY(row), row, col, dp, rm);
        }
        r.cellIDs = new Grids_2D_ID_long[1];
        r.cellIDs[0] = getCellID(row, col);
        r.distance = BigDecimal.ZERO;
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
     * @param dp The number of decimal places used in distance calculations.
     * @param rm The {@link RoundingMode} to use when rounding distance
     * calculations.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            BigDecimal x, BigDecimal y, long row, long col, int dp,
            RoundingMode rm) throws IOException, Exception,
            ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        r.cellIDs = new Grids_2D_ID_long[1];
        r.cellIDs[0] = getNearestCellID(x, y, row, col);
        BigDecimal nearestCellValue = getCell(row, col);
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
            BigDecimal value;
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
            BigDecimal distance;
            // Go through values and find the closest
            HashSet<Grids_2D_ID_long> closest = new HashSet<>();
            iterator = values.iterator();
            Grids_2D_ID_long cellID = iterator.next();
            r.distance = Grids_Utilities.distance(x, y,
                    getCellX(cellID), getCellY(cellID),
                    dp, rm);
            while (iterator.hasNext()) {
                cellID = iterator.next();
                distance = Grids_Utilities.distance(x, y,
                        getCellX(cellID), getCellY(cellID),
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
            // Get cellIDs that are within distance of discovered v
            Grids_2D_ID_long[] cellIDs = getCellIDs(x, y, r.distance, dp, rm);
            for (Grids_2D_ID_long cellID1 : cellIDs) {
                if (!visitedSet.contains(cellID1)) {
                    if (getCell(cellID1).compareTo(ndv) != 0) {
                        distance = Grids_Utilities.distance(x, y,
                                getCellX(cellID1),
                                getCellY(cellID1), dp, rm);
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
     * @param v The v to be added to the cell containing the point (x, y).
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public void addToCell(BigDecimal x, BigDecimal y, BigDecimal v)
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
    public void addToCell(Grids_2D_ID_long cellID, BigDecimal v)
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
    public void addToCell(long row, long col, BigDecimal v) throws IOException,
            ClassNotFoundException, Exception {
        BigDecimal currentValue = getCell(row, col);
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
    protected void initCells(BigDecimal v) throws IOException, Exception,
            ClassNotFoundException {
        Iterator<Grids_2D_ID_int> ite = data.keySet().iterator();
        int nChunks = data.size();
        int counter = 0;
        while (ite.hasNext()) {
            env.checkAndMaybeFreeMemory();
            env.env.log("Initialising Chunk " + counter + " out of " + nChunks);
            counter++;
            Grids_2D_ID_int i = ite.next();
            Grids_ChunkBD chunk = getChunk(i);
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
     * @return A Grids_GridIteratorBD for iterating over the cell values in
     * this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridIteratorBD iterator() throws IOException, Exception,
            ClassNotFoundException {
        return new Grids_GridIteratorBD(this);
    }

    @Override
    public Grids_StatsBD getStats() {
        return (Grids_StatsBD) stats;
    }

    public void initStatistics(Grids_StatsBD stats) {
        this.stats = stats;
    }

    public BigDecimal getCell(Grids_Chunk chunk, int chunkRow, int chunkCol,
            int cellRow, int cellCol) {
        Grids_ChunkBD c = (Grids_ChunkBD) chunk;
        if (chunk.getClass() == Grids_ChunkBDArray.class) {
            return ((Grids_ChunkBDArray) c).getCell(cellRow, cellCol);
        }
        if (chunk.getClass() == Grids_ChunkBDMap.class) {
            return ((Grids_ChunkBDMap) c).getCell(cellRow, cellCol);
        }
        return c.getGrid().ndv;
    }

    @Override
    public BigDecimal getCellBigDecimal(Grids_Chunk chunk, int cr, int cc,
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
     * @return {code true} if this and {@code g} have the same dimensions, the
     * same number of rows and columns, and the same values in each cell.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    @Override
    public boolean isSameDimensionsAndValues(Grids_Grid g) throws IOException,
            Exception {
        if (!(g instanceof Grids_GridBD)) {
            return false;
        }
        if (!isSameDimensions(g)) {
            return false;
        }
        Grids_GridBD gd = (Grids_GridBD) g;
        BigDecimal gndv = gd.getNoDataValue();
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
                Grids_ChunkBD chunk = (Grids_ChunkBD) g.getChunk(i, cr, cc);
                for (int ccr = 0; ccr < cnr; ccr++) {
                    long row = gd.getRow(cr, ccr);
                    for (int ccc = 0; ccc < cnc; ccc++) {
                        long col = gd.getCol(cc, ccc);
                        BigDecimal v = getCell(row, col);
                        //BigDecimal gv = getCell(chunk, cr, cc, ccr, ccc);
                        BigDecimal gv = chunk.getCell(ccr, ccc);
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
