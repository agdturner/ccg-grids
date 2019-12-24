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
package io.github.agdturner.grids.d2.grid.d;

import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.core.Grids_2D_ID_long;
import io.github.agdturner.grids.core.Grids_Dimensions;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDouble;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkFactoryDouble;
import io.github.agdturner.grids.d2.chunk.Grids_Chunk;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkInt;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDoubleSinglet;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDoubleArray;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDoubleMap;
import io.github.agdturner.grids.d2.grid.Grids_GridNumber;
import io.github.agdturner.grids.d2.grid.Grids_Grid;
import io.github.agdturner.grids.d2.stats.Grids_StatsDouble;
import io.github.agdturner.grids.d2.stats.Grids_StatsNotUpdatedDouble;
import io.github.agdturner.grids.io.Grids_ESRIAsciiGridImporter;
import io.github.agdturner.grids.io.Grids_ESRIAsciiGridImporter.Header;
import io.github.agdturner.grids.process.Grids_Processor;
import io.github.agdturner.grids.util.Grids_Utilities;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_FileStore;

/**
 * A class for representing grids with {@code double} values.
 *
 * @see Grids_GridNumber
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridDouble extends Grids_GridNumber {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the NODATA value of the grid, which by default is
     * -Double.MAX_VALUE. N.B. Double.NaN, Double.POSITIVE_INFINITY or
     * Double.NEGATIVE_INFINITY should not be used. N.B. Care should be taken so
     * that NoDataValue is not a data value.
     */
    protected double NoDataValue = -Double.MAX_VALUE;

    /**
     * Each cell value equal to {@code ndv} and all chunks of the same type
     * created via {@code cf}.
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
    protected Grids_GridDouble(Grids_StatsDouble stats, Generic_FileStore fs,
            long id, Grids_ChunkFactoryDouble cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions,
            double ndv, Grids_Environment ge) throws IOException, Exception {
        super(ge, fs, id, BigDecimal.valueOf(ndv));
        init(stats, cf, chunkNRows, chunkNCols, nRows, nCols, dimensions, ndv);
    }

    /**
     * Creates a new Grids_GridDouble based on values in grid.
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
    protected Grids_GridDouble(Grids_StatsDouble stats, Generic_FileStore fs,
            long id, Grids_Grid g, Grids_ChunkFactoryDouble cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, double ndv) throws IOException,
            Exception {
        super(g.env, fs, id, BigDecimal.valueOf(ndv));
        init(stats, g, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, ndv);
    }

    /**
     * Creates a new Grids_GridDouble with values obtained from gridFile.
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
     * @param chunkNRows
     * @param chunkNCols
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
    protected Grids_GridDouble(Grids_StatsDouble stats, Generic_FileStore fs,
            long id, Generic_Path gridFile, Grids_ChunkFactoryDouble cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, double ndv, Grids_Environment ge)
            throws IOException, Exception {
        super(ge, fs, id, BigDecimal.valueOf(ndv));
        init(stats, gridFile, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, ndv);
    }

    /**
     * Creates a new Grids_GridDouble with values obtained from gridFile.
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
    protected Grids_GridDouble(Grids_Environment ge, Generic_FileStore fs,
            long id, Generic_Path gridFile, double ndv) throws IOException, Exception {
        super(ge, fs, id, BigDecimal.valueOf(ndv));
        init(new Grids_StatsNotUpdatedDouble(ge), gridFile);
    }

    @Override
    public String getFieldsDescription() {
        return "NoDataValue=" + NoDataValue + ", "
                + super.getFieldsDescription();
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridDouble from which the fields of this are set. with
     * those of g.
     * @throws java.io.IOException If encountered. *
     */
    private void init(Grids_GridDouble g) throws IOException {
        NoDataValue = g.NoDataValue;
//        Grids_StatsDouble gStats;
//        gStats = g.getStats();
//        if (gStats instanceof Grids_StatsNotUpdatedDouble) {
//            stats = new Grids_StatsNotUpdatedDouble(this);
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
        env.setDataToClear(true);
        env.addGrid(this);
        if (!stats.isUpdated()) {
            ((Grids_StatsNotUpdatedDouble) stats).setUpToDate(false);
        }
        stats.grid = this;
    }

    private void init(Grids_StatsDouble stats,
            Grids_ChunkFactoryDouble chunkFactory, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions,
            double noDataValue) throws IOException, Exception {
        //env.checkAndMaybeFreeMemory(this, true);
        init(stats, chunkNRows, chunkNCols, nRows, nCols, dimensions);
        for (int r = 0; r < nChunkRows; r++) {
            for (int c = 0; c < nChunkCols; c++) {
                env.checkAndMaybeFreeMemory();
                // Try to load chunk.
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(r, c);
                //env.checkAndMaybeFreeMemory();
                Grids_ChunkDouble chunk = chunkFactory.create(this, chunkID);
                data.put(chunkID, chunk);
                if (!(chunk instanceof Grids_ChunkDoubleSinglet)) {
                    worthSwapping.add(chunkID);
                }
            }
            System.out.println("Done chunkRow " + r + " out of "
                    + nChunkRows);
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
    private void init(Grids_StatsDouble stats, Grids_Grid g,
            Grids_ChunkFactoryDouble cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, double ndv) throws IOException, ClassNotFoundException,
            Exception {
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
        Grids_ChunkDouble chunk;
        int gChunkNRows;
        int gChunkNCols;
        int startChunkRow = g.getChunkRow(startRow);
        int endChunkRow = g.getChunkRow(endRow);
        int startChunkCol = g.getChunkCol(startCol);
        int endChunkCol = g.getChunkCol(endCol);
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
                                                //ge.addToNotToClear(this, chunkID);
                                                if (!data.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    data.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_ChunkDoubleSinglet)) {
                                                        worthSwapping.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_ChunkDouble) data.get(chunkID);
                                                }
                                                gValue = gd.getCell(c, cellRow, cellCol);
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
                                                //ge.removeFromNotToClear(this, chunkID);
                                            }
                                        }
                                    }
                                }
                            }
                            isLoadedChunk = true;
                            env.removeFromNotToClear(g, gChunkID);
                        } catch (OutOfMemoryError e) {
                            if (env.HOOME) {
                                env.clearMemoryReserve(env.env);
                                freeSomeMemoryAndResetReserve(e);
                                chunkID = new Grids_2D_ID_int(gcr, gcc);
                                if (env.swapChunksExcept_Account(this, chunkID, false) < 1) { // Should also not cache out the chunk of grid thats values are being used to initialise this.
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
                                                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                                                env.addToNotToClear(this, chunkID);
                                                if (!data.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    data.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_ChunkDoubleSinglet)) {
                                                        worthSwapping.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_ChunkDouble) data.get(chunkID);
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
                                if (env.swapChunksExcept_Account(this, chunkID, false) < 1L) {
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
                System.out.println("Done chunkRow " + gcr + " out of " + nChunkRows);
            }
        }
        init();
    }

    private void init(Grids_StatsDouble stats, Generic_Path gridFile,
            Grids_ChunkFactoryDouble cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, double noDataValue) throws IOException,
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
        if (Files.isDirectory(gridFile)) {
            if (true) {
                Grids_Processor gp = env.getProcessor();
                Grids_GridFactoryDouble gf = gp.GridDoubleFactory;
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                Grids_GridDouble g = (Grids_GridDouble) gf.create(
                        (Grids_Grid) Generic_IO.readObject(thisFile));
                Grids_GridDouble g2 = gf.create(g, startRow, startCol, endRow,
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
            double value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Header header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                initDimensions(header, startRow, startCol);
                double gridFileNoDataValue = header.ndv.doubleValue();
                long row;
                long col;
//                Grids_ChunkDouble chunk;
//                Grids_ChunkDoubleSinglet gridChunk;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue == NoDataValue) {
                    if (stats.isUpdated()) {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readDouble();
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    } else {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readDouble();
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
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readDouble();
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
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readDouble();
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

    private void init(Grids_StatsDouble stats, Generic_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        // Set to report every 10%
        int reportN;
        Grids_Processor gp = env.getProcessor();
        if (Files.isDirectory(gridFile)) {
            if (true) {
                Grids_GridFactoryDouble gf = gp.GridDoubleFactory;
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                Grids_GridDouble g = (Grids_GridDouble) gf.create(
                        (Grids_Grid) Generic_IO.readObject(thisFile));
                init(g);
                //this.data = g.data;
                this.worthSwapping = g.worthSwapping;
                this.NoDataValue = g.NoDataValue;
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
            double value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Header header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                nCols = header.ncols;
                nRows = header.nrows;
                chunkNRows = gp.GridDoubleFactory.getChunkNRows();
                chunkNCols = gp.GridDoubleFactory.getChunkNCols();
                initNChunkRows();
                initNChunkCols();
                initDimensions(header, 0, 0);
                reportN = (int) (nRows - 1) / 10;
                if (reportN == 0) {
                    reportN = 1;
                }
                double gridFileNoDataValue = header.ndv.doubleValue();
                Grids_ChunkDouble chunk;
                Grids_ChunkDoubleSinglet gridChunk;
                long row;
                long col;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue == NoDataValue) {
                    if (stats.isUpdated()) {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readDouble();
                                initCell(row, col, value, false);
                            }
                            if (row % reportN == 0) {
                                System.out.println("Done row " + row);
                            }
                            env.checkAndMaybeFreeMemory();
                        }
                    } else {
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readDouble();
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
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readDouble();
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
                        for (row = (nRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToClear();
                            for (col = 0; col < nCols; col++) {
                                value = eagi.readDouble();
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
//            Grids_ChunkDouble chunk = null;
//            if (o.getClass() == Grids_ChunkDoubleArray.class) {
//                Grids_ChunkDoubleArray c;
//                c = (Grids_ChunkDoubleArray) o;
//                chunk = c;
//            } else if (o.getClass() == Grids_ChunkDoubleMap.class) {
//                Grids_ChunkDoubleMap c;
//                c = (Grids_ChunkDoubleMap) o;
//                chunk = c;
//            } else if (o.getClass() == Grids_ChunkDoubleSinglet.class) {
//                Grids_ChunkDoubleSinglet c;
//                c = (Grids_ChunkDoubleSinglet) o;
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
//            if (!(chunk instanceof Grids_ChunkDoubleSinglet)) {
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
    private void initCell(long row, long col, double value, boolean fast)
            throws IOException, ClassNotFoundException, Exception {
        Grids_2D_ID_int i = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
        env.addToNotToClear(this, i);
        if (!data.containsKey(i)) {
            Grids_ChunkDoubleSinglet gc = new Grids_ChunkDoubleSinglet(this, i, value);
            data.put(i, gc);
            if (!(gc instanceof Grids_ChunkDoubleSinglet)) {
                worthSwapping.add(i);
            }
        } else {
            Grids_Chunk c = data.get(i);
            if (c == null) {
                loadChunk(i);
            }
            Grids_ChunkDouble chunk = getChunk(i);
            if (chunk instanceof Grids_ChunkDoubleSinglet) {
                Grids_ChunkDoubleSinglet gc = (Grids_ChunkDoubleSinglet) chunk;
                if (value != gc.Value) {
                    // Convert chunk to another type
                    chunk = env.getProcessor().GridDoubleFactory.DefaultGridChunkDoubleFactory.create(chunk, i);
                    data.put(i, chunk);
                    if (!(chunk instanceof Grids_ChunkDoubleSinglet)) {
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
     * @return Grids_ChunkDouble for the given chunkID.
     * @param chunkID
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_ChunkDouble getChunk(Grids_2D_ID_int chunkID)
            throws IOException, Exception, ClassNotFoundException {
        if (isInGrid(chunkID)) {
            if (data.get(chunkID) == null) {
                loadChunk(chunkID);
            }
            return (Grids_ChunkDouble) data.get(chunkID);
        }
        return null;
    }

    /**
     * @return Grids_ChunkDouble for the given chunkID.
     * @param chunkID
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_ChunkDouble getChunk(Grids_2D_ID_int chunkID, int chunkRow,
            int chunkCol) throws IOException, Exception,
            ClassNotFoundException {
        if (isInGrid(chunkRow, chunkCol)) {
            if (data.get(chunkID) == null) {
                loadChunk(chunkID);
            }
            return (Grids_ChunkDouble) data.get(chunkID);
        }
        return null;
    }

    /**
     * If newValue and oldValue are the same then stats won't change. A test
     * might be appropriate in set cell so that this method is not called.
     *
     * @param newValue The value replacing oldValue.
     * @param oldValue The value being replaced.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected void updateStats(double newValue, double oldValue)
            throws IOException, Exception, ClassNotFoundException {
        Grids_StatsDouble dStats = getStats();
        if (dStats.getClass() == Grids_StatsDouble.class) {
            if (newValue != NoDataValue) {
                if (oldValue != NoDataValue) {
                    BigDecimal oldValueBD = new BigDecimal(oldValue);
                    dStats.setN(dStats.getN().subtract(BigInteger.ONE));
                    dStats.setSum(dStats.getSum().subtract(oldValueBD));
                    double min = dStats.getMin(false);
                    if (oldValue == min) {
                        dStats.setNMin(dStats.getNMin() - 1);
                    }
                    double max = dStats.getMax(false);
                    if (oldValue == max) {
                        dStats.setNMax(dStats.getNMax() - 1);
                    }
                }
                BigDecimal newValueBD = new BigDecimal(newValue);
                dStats.setN(dStats.getN().add(BigInteger.ONE));
                dStats.setSum(dStats.getSum().add(newValueBD));
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
            if (newValue != oldValue) {
                ((Grids_StatsNotUpdatedDouble) dStats).setUpToDate(false);
            }
        }
    }

    /**
     * @return ndv.
     */
    public final double getNoDataValue() {
        return NoDataValue;
    }

    /**
     * Initialises NoDataValue as noDataValue with the following exceptions. If
     * noDataValue is NaN or if noDataValue is Double.NEGATIVE_INFINITY or
     * Double.POSITIVE_INFINITY then NoDataValue is left as the default of
     * Integer.MIN_VALUE and a warning message is written to std.out.
     *
     * @param ndv The value ndv is initialised to.
     */
    protected final void initNoDataValue(double ndv) {
        if (Double.isNaN(ndv)) {
            System.out.println("NoDataValue cannot be set to NaN! NoDataValue "
                    + "remains as " + NoDataValue);
        } else if (Double.isInfinite(ndv)) {
            System.out.println("NoDataValue cannot be infinite! NoDataValue "
                    + "remains as " + NoDataValue);
        } else {
            NoDataValue = ndv;
        }
    }

    /**
     * @param r The grid cell row index for which the value is returned.
     * @param c The grid cell column index for which the value is returned
     * @return The value in the grid at grid cell row index {@code r}, grid cell
     * column index {@code c} or {@link #NoDataValue} if there is no such value.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public double getCell(long r, long c) throws IOException, Exception,
            ClassNotFoundException {
        if (isInGrid(r, c)) {
            return getCell((Grids_ChunkDouble) getChunk(getChunkRow(r),
                    getChunkCol(c)), getChunkCellRow(r), getChunkCellCol(c));
        }
        return NoDataValue;
    }

    /**
     * For getting the value in chunk at chunk cell row {@code r}, chunk cell
     * col {@code c}.
     *
     * @param chunk The chunk.
     * @param r The chunk cell row index of the value returned.
     * @param c The chunk cell column index of the value returned.
     * @return Value in chunk at chunk cell row {@code r}, chunk cell col
     * {@code c} or {@link #NoDataValue} if there is no such value.
     */
    public double getCell(Grids_ChunkDouble chunk, int r, int c) {
        if (chunk.inChunk(r, c)) {
            return chunk.getCell(r, c);
        }
        return NoDataValue;
    }

    /**
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return The value at (x, y) or {@link #NoDataValue} if there is no such
     * value.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final double getCell(BigDecimal x, BigDecimal y) throws IOException,
            ClassNotFoundException, Exception {
        return getCell(getRow(y), getCol(x));
    }

    /**
     * @param i The cell ID.
     * @return The value of the cell with cell ID {@code i} or
     * {@link #NoDataValue} if there is no such cell in the grid.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final double getCell(Grids_2D_ID_long i) throws IOException,
            ClassNotFoundException, Exception {
        return getCell(i.getRow(), i.getCol());
    }

    /**
     * For setting the value at x-coordinate {@code x}, y-coordinate {@code y}.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param v The value to set in the cell.
     * @return The value at x-coordinate {@code x}, y-coordinate {@code y} or
     * {@link #NoDataValue} if there is no such value.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final double setCell(BigDecimal x, BigDecimal y, double v)
            throws IOException, Exception, ClassNotFoundException, Exception {
        return setCell(getRow(x), getCol(y), v);
    }

    /**
     * For setting the value at cell row index {@code r}, cell column index
     * {@code c}.
     *
     * @param r The cell row index of the value to set.
     * @param c The cell column index of the value to set.
     * @param v The value to set at cell row index {@code r}, cell column index
     * {@code c}.
     * @return The value at cell row index {@code r}, cell column index
     * {@code c} or {@link #NoDataValue} if there is no such value.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public double setCell(long r, long c, double v)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(r, c)) {
            return setCell((Grids_ChunkDouble) getChunk(getChunkRow(r),
                    getChunkCol(c)), getChunkCellRow(r), getChunkCellCol(c), v);
        }
        return NoDataValue;
    }

    /**
     * For setting the value in chunk ({@code cr}, {@code cc}) at chunk cell row
     * {@code ccr}, chunk cell column (@code ccc}.
     *
     * @param cr The chunk row of the chunk in which the value is set.
     * @param cc The chunk column of the chunk in which the value is set.
     * @param ccr The chunk cell row of the value to set.
     * @param ccc The chunk cell column of the value to set.
     * @param v The value to set in chunk ({@code cr}, {@code cc}) at chunk cell
     * row {@code ccr}, chunk cell column (@code ccc}.
     * @return The value in chunk ({@code cr}, {@code cc}) at chunk cell row
     * {@code ccr}, chunk cell column (@code ccc}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public double setCell(int cr, int cc, int ccr, int ccc, double v)
            throws IOException, ClassNotFoundException, Exception {
        return setCell((Grids_ChunkDouble) getChunk(cr, cc), ccr, ccc, v);
    }

    /**
     * For setting the value in chunk at chunk cell row {@code ccr}, chunk cell
     * column (@code ccc}.
     *
     * @param chunk The chunk in which the value is to be set.
     * @param ccr The row in chunk of the value to set.
     * @param ccc The column in chunk of the value to set.
     * @param v The value to set in chunk at chunk cell row {@code ccr}, chunk
     * cell column (@code ccc}.
     * @return The value in chunk at chunk cell row {@code ccr}, chunk cell
     * column (@code ccc}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public double setCell(Grids_ChunkDouble chunk, int ccr, int ccc, double v) 
            throws IOException, Exception, ClassNotFoundException {
        double r = NoDataValue;
        if (chunk instanceof Grids_ChunkDoubleArray) {
            r = ((Grids_ChunkDoubleArray) chunk).setCell(ccr, ccc, v);
        } else if (chunk instanceof Grids_ChunkDoubleMap) {
            r = ((Grids_ChunkDoubleMap) chunk).setCell(ccr, ccc, v);
        } else {
            Grids_ChunkDoubleSinglet c = (Grids_ChunkDoubleSinglet) chunk;
            if (c != null) {
                if (v != c.Value) {
                    // Convert chunk to another type
                    chunk = convertToAnotherTypeOfChunk(chunk, c.getChunkID());
                    r = chunk.setCell(ccr, ccc, v);
                } else {
                    r = c.Value;
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
    private Grids_ChunkDouble convertToAnotherTypeOfChunk(
            Grids_ChunkDouble chunk, Grids_2D_ID_int chunkID)
            throws IOException, ClassNotFoundException, Exception {
        Grids_ChunkDouble r;
        Grids_ChunkFactoryDouble f = env.getProcessor().GridDoubleFactory.DefaultGridChunkDoubleFactory;
        r = f.create(chunk, chunkID);
        data.put(chunkID, r);
        if (!(chunk instanceof Grids_ChunkDoubleSinglet)) {
            worthSwapping.add(chunkID);
        }
        return r;
    }

    /**
     * Initialises the value in chunk at row, col.
     *
     * @param chunk The chunk.
     * @param i The chunk ID.
     * @param row The row.
     * @param col The col.
     * @param v The value.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected void initCell(Grids_ChunkDouble chunk, Grids_2D_ID_int i,
            long row, long col, double v) throws IOException,
            ClassNotFoundException, Exception {
        if (chunk instanceof Grids_ChunkDoubleSinglet) {
            Grids_ChunkDoubleSinglet gridChunk = (Grids_ChunkDoubleSinglet) chunk;
            if (v != gridChunk.Value) {
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
        if (v != NoDataValue) {
            if (!(stats instanceof Grids_StatsNotUpdatedDouble)) {
                updateStats(v);
            }
        }
    }

    protected void updateStats(double value) throws IOException, Exception,
            ClassNotFoundException {
        Grids_StatsDouble dStats = getStats();
        if (!Double.isNaN(value) && Double.isFinite(value)) {
            BigDecimal valueBD = new BigDecimal(value);
            dStats.setN(dStats.getN().add(BigInteger.ONE));
            dStats.setSum(dStats.getSum().add(valueBD));
            double min = dStats.getMin(false);
            if (value < min) {
                dStats.setNMin(1);
                dStats.setMin(value);
            } else {
                if (value == min) {
                    dStats.setNMin(dStats.getNMin() + 1);
                }
            }
            double max = dStats.getMax(false);
            if (value > max) {
                dStats.setNMax(1);
                dStats.setMax(value);
            } else {
                if (value == max) {
                    dStats.setNMax(dStats.getNMax() + 1);
                }
            }
        }
    }

    /**
     * Initialises the value at row, col and does nothing about stats.
     *
     * @param chunk The chunk.
     * @param row The row.
     * @param col The column.
     * @param value The value.
     */
    protected void initCellFast(Grids_ChunkDouble chunk, long row,
            long col, double value) {
//        int chunkRow = getChunkRow(row);
//        int chunkCol = getChunkCol(col);
//        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
//        Grids_ChunkDouble chunk = getChunk(chunkID);
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected double[] getCells(BigDecimal x, BigDecimal y,
            BigDecimal distance, int dp, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        return getCells(x, y, getRow(y), getCol(x), distance, dp, rm);
    }

    /**
     * @return double[] of all cell values for cells thats centroids are
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
    protected double[] getCells(BigDecimal x, BigDecimal y, long row, long col,
            BigDecimal distance, int dp, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        double[] cells;
        BigDecimal[] dar = distance.divideAndRemainder(getCellsize());
        int delta = dar[0].intValueExact();
        if (dar[1].compareTo(BigDecimal.ZERO) == 1) {
            delta += 1;
        }
        cells = new double[((2 * delta) + 1) * ((2 * delta) + 1)];
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            BigDecimal x, BigDecimal y, int dp, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        NearestValuesCellIDsAndDistance r = new NearestValuesCellIDsAndDistance();
        double value = getCell(x, y);
        if (value == NoDataValue) {
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
        double value = getCell(row, col);
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
        double nearestCellValue = getCell(row, col);
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
            double value;
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToCell(BigDecimal x, BigDecimal y, double v)
            throws IOException, Exception, ClassNotFoundException {
        addToCell(getRow(y), getCol(x), v);
    }

    /**
     * @param cellID The ID of the cell to add to the value of.
     * @param v The value to be added to the cell.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToCell(Grids_2D_ID_long cellID, double v)
            throws IOException, Exception, ClassNotFoundException {
        addToCell(cellID.getRow(), cellID.getCol(), v);
    }

    /**
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @param v The value to be added to the cell. NB1. If cell is not contained
     * in this then then returns ndv. NB2. Adding to ndv is done as if adding to
     * a cell with value of 0. TODO: Check Arithmetic
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToCell(long row, long col, double v) throws IOException,
            ClassNotFoundException, Exception {
        double currentValue = getCell(row, col);
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected void initCells(double v) throws IOException, Exception,
            ClassNotFoundException {
        Iterator<Grids_2D_ID_int> ite = data.keySet().iterator();
        int nChunks = data.size();
        int counter = 0;
        while (ite.hasNext()) {
            env.checkAndMaybeFreeMemory();
            System.out.println("Initialising Chunk " + counter + " out of "
                    + nChunks);
            counter++;
            Grids_2D_ID_int i = ite.next();
            Grids_ChunkDouble chunk = getChunk(i);
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
     * @return A Grids_GridIteratorDouble for iterating over the cell values in
     * this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridIteratorDouble iterator() throws IOException, Exception,
            ClassNotFoundException {
        return new Grids_GridIteratorDouble(this);
    }

    @Override
    public Grids_StatsDouble getStats() {
        return (Grids_StatsDouble) stats;
    }

    public void initStatistics(Grids_StatsDouble stats) {
        this.stats = stats;
    }

    public double getCell(Grids_Chunk chunk, int chunkRow, int chunkCol,
            int cellRow, int cellCol) {
        Grids_ChunkDouble c = (Grids_ChunkDouble) chunk;
        if (chunk.getClass() == Grids_ChunkDoubleArray.class) {
            return ((Grids_ChunkDoubleArray) c).getCell(cellRow, cellCol);
        }
        if (chunk.getClass() == Grids_ChunkDoubleMap.class) {
            return ((Grids_ChunkDoubleMap) c).getCell(cellRow, cellCol);
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
