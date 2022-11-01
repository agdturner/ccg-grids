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
package uk.ac.leeds.ccg.grids.d2.grid.i;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.io.IO_Utilities;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkInt;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntFactory;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_GridNumber;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDouble;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntSinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntArray;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntMap;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsInt;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter.Header;
import uk.ac.leeds.ccg.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.grids.d2.util.Grids_Utilities;
import java.util.Set;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.io.IO_Path;
import uk.ac.leeds.ccg.math.number.Math_BigRational;
import uk.ac.leeds.ccg.math.number.Math_BigRationalSqrt;

/**
 * Grids with {@code int} values.
 *
 * @author Andy Turner
 * @version 1.0
 */
public class Grids_GridInt extends Grids_GridNumber {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the NODATA value of the grid, which by default is
     * Integer.MIN_VALUE. Care should be taken so that NoDataValue is not a data
     * value.
     */
    protected int ndv = Integer.MIN_VALUE;

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
     * @param dims The grid dimensions (cellsize, xmin, ymin, xmax and ymax).
     * @param ndv The ndv for this.
     * @param ge The grids environment.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridInt(Grids_StatsInt stats, IO_Cache fs,
            long id, Grids_ChunkIntFactory cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dims,
            int ndv, Grids_Environment ge) throws IOException, Exception,
            ClassNotFoundException {
        super(ge, fs, id, BigDecimal.valueOf(ndv));
        init(stats, cf, chunkNRows, chunkNCols, nRows, nCols, dims);
    }

    /**
     * Creates a new Grids_GridInt based on values in grid.
     *
     * @param stats What {@link #stats} is set to.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridInt(Grids_StatsInt stats, IO_Cache fs, long id,
            Grids_Grid g, Grids_ChunkIntFactory cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, int ndv) throws IOException, ClassNotFoundException,
            Exception {
        super(g.env, fs, id, BigDecimal.valueOf(ndv));
        init(stats, g, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, ndv);
    }

    /**
     * Creates a new Grids_GridInt with values obtained from gridFile.
     * {@code gridFile} must be a directory containing a cached instance of a
     * Grids_Number or an ESRI Asciigrid format file with a filename ending in
     * ".asc" or ".txt".
     *
     * @param stats What {@link #stats} is set to.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data and information about the Grids_GridInt to
     * be returned.
     * @param cf The factory preferred to construct chunks of this.
     * @param cnr The chunk nRows
     * @param cnc The chunk nCols.
     * @param startRow The start row.
     * @param startCol The start col.
     * @param endRow The end row.
     * @param endCol The end column.
     * @param ndv The ndv for this.
     * @param ge The grids environment.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridInt(Grids_StatsInt stats, IO_Cache fs,
            long id, IO_Path gridFile,
            Grids_ChunkIntFactory cf, int cnr,
            int cnc, long startRow, long startCol, long endRow,
            long endCol, int ndv, Grids_Environment ge)
            throws IOException, ClassNotFoundException, Exception {
        super(ge, fs, id, BigDecimal.valueOf(ndv));
        init(stats, gridFile, cnr, cnc, startRow, startCol, endRow, endCol);
    }

    /**
     * Creates a new Grids_GridInt with values obtained from gridFile.
     * {@code gridFile} must be a directory containing a cached instance of a
     * Grids_Number or an ESRI Asciigrid format file with a filename ending in
     * ".asc" or ".txt".
     *
     * @param ge The grids environment.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data for this.
     * @param ndv The ndv for this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridInt(Grids_Environment ge, IO_Cache fs,
            long id, IO_Path gridFile, int ndv)
            throws IOException, ClassNotFoundException, Exception {
        super(ge, fs, id, BigDecimal.valueOf(ndv));
        init(new Grids_GridIntStatsNotUpdated(ge), gridFile);
    }

    @Override
    public String getFieldsDescription() {
        return "NoDataValue=" + ndv + ", "
                + super.getFieldsDescription();
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridInt from which the fields of this are set.
     */
    private void init(Grids_GridInt g) throws IOException {
        ndv = g.ndv;
        stats = g.stats;
        super.init(g);
        data = g.data;
        // Set the reference to this in data chunks
        setReferenceInChunks();
        worthSwapping = g.worthSwapping;
        // Set the reference to this in the grid stats
        stats.setGrid(this);
        super.init();
        //Stats.grid = this;
    }

    @Override
    protected void init() throws IOException {
        super.init();
        if (!stats.isUpdated()) {
            ((Grids_GridIntStatsNotUpdated) stats).setUpToDate(false);
        }
        stats.grid = this;
    }

    /**
     * Initialises this.
     *
     * @param stats The AbstractGridStatistics to accompany this.
     * @param dir The directory for this.
     * @param cf The Grids_ChunkIntFactory preferred for creating chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param ndv The ndv.
     */
    private void init(Grids_StatsInt stats,
            Grids_ChunkIntFactory cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions)
            throws IOException, Exception {
        env.checkAndMaybeFreeMemory();
        init(stats, chunkNRows, chunkNCols, nRows, nCols, dimensions);
        for (int r = 0; r < nChunkRows; r++) {
            for (int c = 0; c < nChunkCols; c++) {
                env.checkAndMaybeFreeMemory();
                // Try to load chunk.
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(r, c);
                Grids_ChunkInt chunk = cf.create(this, chunkID);
                data.put(chunkID, chunk);
                if (!(chunk instanceof Grids_ChunkIntSinglet)) {
                    worthSwapping.add(chunkID);
                }
            }
            env.env.log("Done chunkRow " + r + " out of " + nChunkRows);
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
            Grids_ChunkIntFactory cf, int chunkNRows,
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
        int startChunkRow = g.getChunkRow(startRow);
        int endChunkRow = g.getChunkRow(endRow);
        int ncr = endChunkRow - startChunkRow + 1;
        int startChunkCol = g.getChunkCol(startCol);
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
                            env.addToNotToClear(g, gChunkID);
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
                                                env.addToNotToClear(this, chunkID);
                                                if (!data.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    data.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_ChunkIntSinglet)) {
                                                        worthSwapping.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_ChunkInt) data.get(chunkID);
                                                }
                                                gValue = gd.getCell(c, cellRow, cellCol);
                                                // Initialise v
                                                if (gValue == gndv) {
                                                    initCell(chunk, chunkID, row, col, ndv);
                                                } else {
                                                    if (!Double.isNaN(gValue) && Double.isFinite(gValue)) {
                                                        initCell(chunk, chunkID, row, col, (int) gValue);
                                                    } else {
                                                        initCell(chunk, chunkID, row, col, ndv);
                                                    }
                                                }
                                                //ge.removeFromNotToClear(this, chunkID);
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
                                freeSomeMemoryAndResetReserve(e);
                                chunkID = new Grids_2D_ID_int(gcr, gcc);
                                freeSomeMemoryAndResetReserve(chunkID, e);
                                if (env.swapChunksExcept_Account(this, chunkID, false).detail < 1) {
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
                env.env.log("Done chunkRow " + gcr + " out of " + ncr);
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
                            env.addToNotToClear(g, gChunkID);
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
                                                env.addToNotToClear(this, chunkID);
                                                if (!data.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    data.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_ChunkIntSinglet)) {
                                                        worthSwapping.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_ChunkInt) data.get(chunkID);
                                                }
                                                gValue = gi.getCell(c, cellRow, cellCol);
                                                // Initialise v
                                                if (gValue == gndv) {
                                                    initCell(chunk, chunkID, row, col, ndv);
                                                } else {
                                                    if (!Double.isNaN(gValue) && Double.isFinite(gValue)) {
                                                        initCell(chunk, chunkID, row, col, gValue);
                                                    } else {
                                                        initCell(chunk, chunkID, row, col, ndv);
                                                    }
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
                                chunkID = new Grids_2D_ID_int(gcr, gcc);
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

    /**
     * Initialises this.
     *
     * @param stats The AbstractGridStatistics to accompany this.
     * @param dir The File _Directory to be used for cacheping.
     * @param gridFile Either a _Directory, or a formatted File with a specific
     * extension containing the data and information about the Grids_GridInt to
     * be returned.
     * @param cf The Grids_ChunkIntFactory preferred to construct chunks of
     * this.
     * @param chunkNRows The Grids_GridInt _ChunkNRows.
     * @param chunkNCols The Grids_GridInt _ChunkNCols.
     * @param startRow The topmost row index of the grid stored as gridFile.
     * @param startCol The leftmost column index of the grid stored as gridFile.
     * @param endRow The bottom row index of the grid stored as gridFile.
     * @param endCol The rightmost column index of the grid stored as gridFile.
     * @param ndv The ndv for this.
     */
    private void init(Grids_StatsInt stats, IO_Path gridFile,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol)
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
                Grids_GridIntFactory gf = gp.gridFactoryInt;
                IO_Path thisFile = new IO_Path(getPathThisFile(gridFile));
                Grids_Grid g = (Grids_Grid) IO_Utilities.readObject(thisFile);
                Grids_GridInt g2 = gf.create(g, startRow, startCol, endRow, endCol);
                init(g2);
            }
        } else {
            // Assume ESRI AsciiFile
            this.chunkNRows = chunkNRows;
            this.chunkNCols = chunkNCols;
            nRows = endRow - startRow + 1L;
            nCols = endCol - startCol + 1L;
            name = fs.getBaseDir().getFileName().toString() + fsID;
            initNChunkRows();
            initNChunkCols();
            data = new TreeMap<>();
            worthSwapping = new HashSet<>();
            this.stats = stats;
            this.stats.setGrid(this);
            String filename = gridFile.getFileName().toString();
            int value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Header header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                initDimensions(header, startRow, startCol);
                int gridFileNoDataValue = header.ndv.intValueExact();
                long row;
                long col;
//                Grids_ChunkInt chunk;
//                Grids_ChunkIntSinglet gridChunk;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue == ndv) {
                    if (stats.isUpdated()) {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readInt();
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
                                value = eagi.readInt();
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
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readInt();
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
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readInt();
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

    private void init(Grids_StatsInt stats, IO_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        // For reporting
        int reportN;
        Grids_Processor gp;
        gp = env.getProcessor();
        if (Files.isDirectory(gridFile.getPath())) {
            if (true) {
                Grids_GridIntFactory gf = gp.gridFactoryInt;
                IO_Path thisFile = new IO_Path(getPathThisFile(gridFile));
                Grids_GridInt g = (Grids_GridInt) gf.create(
                        (Grids_Grid) IO_Utilities.readObject(thisFile));
                init(g);
                this.data = g.data;
                this.worthSwapping = g.worthSwapping;
                this.ndv = g.ndv;
                this.dim = g.dim;
                this.stats = g.getStats();
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
            int value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Header header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                nCols = header.nrows;
                nRows = header.ncols;
                chunkNRows = gp.gridFactoryDouble.getChunkNRows();
                chunkNCols = gp.gridFactoryDouble.getChunkNCols();
                initNChunkRows();
                initNChunkCols();
                initDimensions(header, 0, 0);
                // Set to report every 10%
                reportN = (int) (nRows - 1) / 10;
                if (reportN == 0) {
                    reportN = 1;
                }
                double gridFileNoDataValue = header.ndv.doubleValue();
                long row;
                long col;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue == ndv) {
                    if (stats.isUpdated()) {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = (int) eagi.readDouble();
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
                                value = (int) eagi.readDouble();
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
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = (int) eagi.readDouble();
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
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = (int) eagi.readDouble();
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
//                        + ".loadChunk(ChunkID(" + chunkID.toString()
//                        + "))");
//            }
//            chunk.env = env;
//            chunk.initGrid(this);
//            chunk.initChunkID(chunkID);
//            data.put(chunkID, chunk);
//            if (!(chunk instanceof Grids_ChunkIntSinglet)) {
//                worthSwapping.add(chunkID);
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
        env.addToNotToClear(this, chunkID);
        if (!data.containsKey(chunkID)) {
            Grids_ChunkIntSinglet gc = new Grids_ChunkIntSinglet(this, chunkID,
                    value);
            data.put(chunkID, gc);
            if (!(gc instanceof Grids_ChunkIntSinglet)) {
                worthSwapping.add(chunkID);
            }
        } else {
            Grids_Chunk c;
            c = data.get(chunkID);
            if (c == null) {
                loadChunk(chunkID);
            }
            chunk = (Grids_ChunkInt) data.get(chunkID);
            if (chunk instanceof Grids_ChunkIntSinglet) {
                Grids_ChunkIntSinglet gc = (Grids_ChunkIntSinglet) chunk;
                if (value != gc.v) {
                    // Convert chunk to another type
                    chunk = env.getProcessor().gridFactoryInt.defaultGridChunkIntFactory.create(
                            chunk, chunkID);
                    chunk.initCell(getChunkCellRow(row), getChunkCellCol(col), value);
                    data.put(chunkID, chunk);
                    if (!(chunk instanceof Grids_ChunkIntSinglet)) {
                        worthSwapping.add(chunkID);
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
     * @return Grids_ChunkInt for chunk ID @code i}.
     * @param i The chunk ID.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_ChunkInt getChunk(Grids_2D_ID_int i) throws IOException,
            ClassNotFoundException, Exception {
        if (isInGrid(i)) {
            if (data.get(i) == null) {
                loadChunk(i);
            }
            return (Grids_ChunkInt) data.get(i);
        }
        return null;
    }

    /**
     * @param i The chunk ID.
     * @param cr The chunk row.
     * @param cc The chunk column.
     * @return The chunk for the given chunkID {@code i}.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_ChunkInt getChunk(Grids_2D_ID_int i, int cr, int cc)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(cr, cc)) {
            if (data.get(i) == null) {
                loadChunk(i);
            }
            return (Grids_ChunkInt) data.get(i);
        }
        return null;
    }

    /**
     * If newValue and oldValue are the same then stats won't change.
     *
     * @param newValue The v replacing oldValue.
     * @param oldValue The v being replaced.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void updateStats(int newValue, int oldValue) throws IOException,
            Exception, ClassNotFoundException {
        Grids_GridIntStats s = getStats();
        if (s instanceof Grids_GridIntStatsNotUpdated) {
            if (newValue != oldValue) {
                ((Grids_GridIntStatsNotUpdated) s).setUpToDate(false);
            }
        } else {
            if (newValue != ndv) {
                if (oldValue != ndv) {
                    s.setN(s.getN() - 1);
                    s.setSum(s.getSum().subtract(Math_BigRational.valueOf(oldValue)));
                    int min = s.getMin(false);
                    if (oldValue == min) {
                        s.setNMin(s.getNMin() - 1);
                    }
                    int max = s.getMax(false);
                    if (oldValue == max) {
                        s.setNMax(s.getNMax() - 1);
                    }
                }
                if (newValue != ndv) {
                    s.setN(s.getN() + 1);
                    s.setSum(s.getSum().add(Math_BigRational.valueOf(newValue)));
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
    }

    /**
     * @return {@link #ndv}
     */
    public final int getNoDataValue() {
        return ndv;
    }

    /**
     * Initialises ndv as ndv.
     *
     * @param ndv The v ndv is initialised to.
     */
    protected final void initNoDataValue(
            int ndv) {
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
    public int getCell(long r, long c) throws IOException, Exception,
            ClassNotFoundException {
        if (isInGrid(r, c)) {
            return getCell((Grids_ChunkInt) getChunk(getChunkRow(r),
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
    public int getCell(Grids_ChunkInt chunk, int r, int c) {
        return chunk.getCell(r, c);
    }

    /**
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return The v at (x, y) or {@link #ndv} if there is no such v.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final int getCell(Math_BigRational x, Math_BigRational y) throws IOException,
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
    public final int getCell(Grids_2D_ID_long i) throws IOException,
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
    public final int setCell(Math_BigRational x, Math_BigRational y, int v)
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
    public int setCell(long r, long c, int v)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(r, c)) {
            return setCell((Grids_ChunkInt) getChunk(getChunkRow(r),
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
    public int setCell(int cr, int cc, int ccr, int ccc, int v)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(cr, cc, ccr, ccc)) {
            return setCell((Grids_ChunkInt) getChunk(cr, cc), ccr, ccc, v);
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
    public int setCell(Grids_ChunkInt chunk, int ccr, int ccc, int v)
            throws IOException, Exception, ClassNotFoundException {
        int r = ndv;
        if (chunk instanceof Grids_ChunkIntArray) {
            r = ((Grids_ChunkIntArray) chunk).setCell(ccr, ccc, v);
        } else if (chunk instanceof Grids_ChunkIntMap) {
            r = ((Grids_ChunkIntMap) chunk).setCell(ccr, ccc, v);
        } else {
            Grids_ChunkIntSinglet c = (Grids_ChunkIntSinglet) chunk;
            if (c != null) {
                if (v != c.v) {
                    // Convert chunk to another type
                    chunk = convertToAnotherTypeOfChunk(chunk, c.getId());
                    r = chunk.setCell(ccr, ccc, v);
                } else {
                    r = c.v;
                }
            }
        }
        // Update stats
        if (v != r) {
            if (stats.isUpdated()) {
                updateStats(v, r);
            }
        }
        return r;
    }

    /**
     * Convert chunk to another type of chunk.
     */
    private Grids_ChunkInt convertToAnotherTypeOfChunk(Grids_ChunkInt chunk,
            Grids_2D_ID_int i) throws IOException, ClassNotFoundException,
            Exception {
        Grids_ChunkInt r = env.getProcessor().gridFactoryInt.defaultGridChunkIntFactory.create(chunk, i);
        data.put(i, r);
        return r;
    }

    /**
     * Initialises the v at row, col.
     *
     * @param chunk The chunk.
     * @param i The chunk ID.
     * @param row The cell row.
     * @param col The cell column.
     * @param v The v.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected void initCell(Grids_ChunkInt chunk, Grids_2D_ID_int i, long row,
            long col, int v) throws IOException, ClassNotFoundException,
            Exception {
        if (chunk instanceof Grids_ChunkIntSinglet) {
            Grids_ChunkIntSinglet gc = (Grids_ChunkIntSinglet) chunk;
            if (v != gc.v) {
                // Convert chunk to another type
                chunk = convertToAnotherTypeOfChunk(chunk, i);
                chunk.initCell(getChunkCellRow(row), getChunkCellCol(col), v);
            } else {
                return;
            }
        }
        if (chunk != null) {
            chunk.initCell(getChunkCellRow(row), getChunkCellCol(col), v);
        }
        // Update stats
        if (v != ndv) {
            updateStats(v);
        }
    }

    /**
     * updateStats
     *
     * @param value The value.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    public void updateStats(int value) throws IOException, Exception,
            ClassNotFoundException {
        Grids_GridIntStats iStats = getStats();
        iStats.setN(iStats.getN() + 1);
        iStats.setSum(iStats.getSum().add(Math_BigRational.valueOf(value)));
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
     * Initialises the v at row, col and does nothing about stats
     *
     * @param chunk The chunk.
     * @param row Cell row.
     * @param col Cell column.
     * @param v Cell v.
     */
    protected void initCellFast(Grids_ChunkInt chunk, long row, long col,
            int v) {
        chunk.initCell(getChunkCellRow(row), getChunkCellCol(col), v);
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected int[] getCells(Math_BigRational x, Math_BigRational y,
            Math_BigRationalSqrt distance, int oom, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        return getCells(x, y, getRow(y), getCol(x), distance, oom, rm);
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
     * @param oom The Order of Magnitude for the precision.
     * @param rm The RoundingMode for any rounding.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public int[] getCells(long row, long col, Math_BigRationalSqrt distance,
            int oom, RoundingMode rm) throws IOException, Exception, ClassNotFoundException {
        return getCells(getCellX(col), getCellY(row), row, col, distance, oom, rm);
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
     * @param oom The Order of Magnitude for the precision.
     * @param rm The RoundingMode for any rounding.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public int[] getCells(Math_BigRational x, Math_BigRational y, long row,
            long col, Math_BigRationalSqrt distance, int oom, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        int delta = getCellDistance(distance, oom, rm);
        int[] r = new int[((2 * delta) + 1) * ((2 * delta) + 1)];
        int count = 0;
        for (long p = row - delta; p <= row + delta; p++) {
            Math_BigRational thisY = getCellY(row);
            for (long q = col - delta; q <= col + delta; q++) {
                Math_BigRational thisX = getCellX(col);
                if (Grids_Utilities.distance(x, y, thisX, thisY, oom, rm)
                        .compareTo(distance) <= 0) {
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
            Math_BigRational x, Math_BigRational y, int oom, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        int value = getCell(x, y);
        if (value == ndv) {
            return getNearestValuesCellIDsAndDistance(x, y, getRow(y), getCol(x), oom, rm);
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
        int value = getCell(row, col);
        if (value == ndv) {
            return getNearestValuesCellIDsAndDistance(getCellX(col),
                    getCellY(row), row, col, oom, rm);
        }
        r.cellIDs = new Grids_2D_ID_long[1];
        r.cellIDs[0] = getCellID(row, col);
        r.distance = Math_BigRationalSqrt.ZERO;
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
     * @param oom The Order of Magnitude for the precision used in distance
     * calculations.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            Math_BigRational x, Math_BigRational y, long row, long col, int oom, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        r.cellIDs = new Grids_2D_ID_long[1];
        r.cellIDs[0] = getNearestCellID(x, y, row, col);
        int nearestCellValue = getCell(row, col);
        if (nearestCellValue == ndv) {
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
                    if (value != ndv) {
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
            Math_BigRationalSqrt distance;
            // Go through values and find the closest
            HashSet<Grids_2D_ID_long> closest = new HashSet<>();
            iterator = values.iterator();
            Grids_2D_ID_long cellID = iterator.next();
            r.distance = Grids_Utilities.distance(x, y,
                    getCellX(cellID), getCellY(cellID), oom, rm);
            while (iterator.hasNext()) {
                cellID = iterator.next();
                distance = Grids_Utilities.distance(x, y,
                        getCellX(cellID), getCellY(cellID), oom, rm);
                if (distance.compareTo(r.distance) == -1) {
                    closest.clear();
                    closest.add(cellID);
                } else {
                    if (distance == r.distance) {
                        closest.add(cellID);
                    }
                }
                r.distance = Math_BigRationalSqrt.min(r.distance, distance);
            }
            // Get cellIDs that are within distance of discovered v
            Grids_2D_ID_long[] cellIDs = getCellIDs(x, y, r.distance, oom, rm);
            for (Grids_2D_ID_long cellID1 : cellIDs) {
                if (!visitedSet.contains(cellID1)) {
                    if (getCell(cellID1) != ndv) {
                        distance = Grids_Utilities.distance(x, y,
                                getCellX(cellID1),
                                getCellY(cellID1), oom, rm);
                        if (distance.compareTo(r.distance) == -1) {
                            closest.clear();
                            closest.add(cellID1);
                        } else {
                            if (distance == r.distance) {
                                closest.add(cellID1);
                            }
                        }
                        r.distance = Math_BigRationalSqrt.min(r.distance, distance);
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
    public void addToCell(Math_BigRational x, Math_BigRational y, int v) throws IOException,
            Exception, ClassNotFoundException {
        addToCell(getRow(y), getCol(x), v);
    }

    /**
     * @param i The ID of the cell to add to the v of.
     * @param v The v to be added to the cell.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToCell(Grids_2D_ID_long i, int v)
            throws IOException, Exception, ClassNotFoundException {
        addToCell(i.getRow(), i.getCol(), v);
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
    public void addToCell(long row, long col, int v) throws IOException,
            ClassNotFoundException, Exception {
        int currentValue = getCell(row, col);
        if (currentValue != ndv) {
            if (v != ndv) {
                setCell(row, col, currentValue + v);
            }
        } else {
            if (v != ndv) {
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
    protected void initCells(int v) throws IOException, Exception,
            ClassNotFoundException {
        Iterator<Grids_2D_ID_int> ite = data.keySet().iterator();
        int nChunks = data.size();
        int counter = 0;
        while (ite.hasNext()) {
            env.checkAndMaybeFreeMemory();
            env.env.log("Initialising Chunk " + counter + " out of " + nChunks);
            counter++;
            Grids_2D_ID_int i = ite.next();
            Grids_ChunkInt chunk = getChunk(i);
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
     * @return A Grids_GridIntIterator for iterating over the cell values in
     * this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridIntIterator iterator() throws IOException, Exception,
            ClassNotFoundException {
        return new Grids_GridIntIterator(this);
    }

    @Override
    public Grids_GridIntStats getStats() {
        return (Grids_GridIntStats) stats;
    }

    /**
     * @param stats What {@link #stats} is set to.
     */
    public void initStats(Grids_StatsInt stats) {
        this.stats = stats;
    }

    /**
     * @param chunk chunk
     * @param cr cs
     * @param cc cc
     * @param ccr ccr
     * @param ccc ccc
     * @return The cell value.
     */
    public int getCell(Grids_Chunk chunk, int cr, int cc, int ccr, int ccc) {
        Grids_ChunkInt c = (Grids_ChunkInt) chunk;
        if (chunk.getClass() == Grids_ChunkIntArray.class) {
            return ((Grids_ChunkIntArray) c).getCell(ccr, ccc);
        }
        if (chunk.getClass() == Grids_ChunkIntMap.class) {
            return ((Grids_ChunkIntMap) c).getCell(ccr, ccc);
        }
        return c.getGrid().ndv;
    }

    @Override
    public BigDecimal getCellBigDecimal(Grids_Chunk chunk, int cr, int cc,
            int ccr, int ccc) {
        return BigDecimal.valueOf(getCell(chunk, cr, cc, ccr, ccc));
    }

    @Override
    public Number setCell(int cr, int cc, int ccr, int ccc, BigDecimal v)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(cr, cc, ccr, ccc)) {
            return setCell(cr, cc, ccr, ccc, v.intValue());
        }
        return ndv;
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
        if (!(g instanceof Grids_GridInt)) {
            return false;
        }
        if (!isSameDimensions(g)) {
            return false;
        }
        Grids_GridInt gi = (Grids_GridInt) g;
        int gndv = gi.getNoDataValue();
        for (int cr = 0; cr < this.nChunkRows; cr++) {
            int cnr = gi.getChunkNRows(cr);
            for (int cc = 0; cc < this.nChunkCols; cc++) {
                int cnc = gi.getChunkNCols(cc);
                Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                env.addToNotToClear(gi, i);
                // Add to not to clear a row of this chunks.
                long rowMin = gi.getRow(cr, 0);
                long rowMax = gi.getRow(cr, cnr);
                long colMin = gi.getCol(cc, 0);
                long colMax = gi.getCol(cc, cnc);
                Set<Grids_2D_ID_int> s = getChunkIDs(rowMin, rowMax, colMin,
                        colMax);
                env.addToNotToClear(this, s);
                env.checkAndMaybeFreeMemory();
                Grids_ChunkInt chunk = getChunk(i, cr, cc);
                for (int ccr = 0; ccr < cnr; ccr++) {
                    long row = gi.getRow(cr, ccr);
                    for (int ccc = 0; ccc < cnc; ccc++) {
                        long col = gi.getCol(cc, ccc);
                        int v = getCell(row, col);
                        //int gv = getCell(chunk, cr, cc, ccr, ccc);
                        int gv = chunk.getCell(ccr, ccc);
                        if (v == ndv) {
                            if (gv != gndv) {
                                return false;
                            }
                        } else {
                            if (gv == gndv) {
                                return false;
                            } else {
                                if (v != gv) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                env.removeFromNotToClear(gi, i);
                env.removeFromNotToClear(this, s);
            }
        }
        return true;
    }
}
