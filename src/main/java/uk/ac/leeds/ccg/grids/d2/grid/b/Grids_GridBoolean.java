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
package uk.ac.leeds.ccg.grids.d2.grid.b;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Objects;
import uk.ac.leeds.ccg.generic.io.Generic_IO;
import uk.ac.leeds.ccg.generic.io.Generic_Path;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Dimensions;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.grid.Grids_Grid;
import uk.ac.leeds.ccg.grids.d2.chunk.b.Grids_ChunkBooleanArray;
import uk.ac.leeds.ccg.grids.d2.chunk.b.Grids_ChunkFactoryBoolean;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsBoolean;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsNotUpdatedBoolean;
import uk.ac.leeds.ccg.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.grids.d2.util.Grids_Utilities;
import java.util.HashSet;
import java.util.TreeMap;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.grids.d2.chunk.b.Grids_ChunkBoolean;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter.Header;
import uk.ac.leeds.ccg.math.number.Math_BigRational;

/**
 * Grids with {@code Boolean} values. The noDataValue is {@code null}.
 *
 * @author Andy Turner
 * @version 1.0
 */
public class Grids_GridBoolean extends Grids_GridB {

    private static final long serialVersionUID = 1L;

    /**
     * The default value.
     */
    public static Boolean DefaultValue = null;

    /**
     * Creates a new grid with each cell value equal to {@code ndv} and all
     * chunks of the same type.
     *
     * @param stats What {@link #stats}T is set to.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @param cf The factory used to create chunks.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     * @param nRows What {@link #nRows} is set to.
     * @param nCols What {@link #nCols} is set to.
     * @param dimensions What {@link #dim} is set to.
     * @param e The grids environment.
     * @throws java.io.IOException If encountered.
     */
    protected Grids_GridBoolean(Grids_StatsBoolean stats, IO_Cache fs,
            long id, Grids_ChunkFactoryBoolean cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions,
            Grids_Environment e) throws IOException, Exception {
        super(e, fs, id);
        init(stats, cf, chunkNRows, chunkNCols, nRows, nCols, dimensions);
    }

    /**
     * Creates a new grid based on values in grid {@code g}.
     *
     * @param stats What {@link #stats}T is set to.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @param g The {@link Grids_Grid} used to construct this.
     * @param cf The factory used to create chunks.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     * @param startRow The row of {@code g} which is the bottom most row of
     * this.
     * @param startCol The column of {@code g} which is the left most column of
     * this.
     * @param endRow The row of {@code g} which is the top most row of this.
     * @param endCol The column of {@code g} which is the right most column of
     * this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridBoolean(Grids_StatsBoolean stats, IO_Cache fs,
            long id, Grids_Grid g, Grids_ChunkFactoryBoolean cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol) throws IOException, Exception, ClassNotFoundException {
        super(g.env, fs, id);
        init(stats, g, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol);
    }

    /**
     * Creates a new grid with values obtained from a grid cached in gridFile.
     *
     * @param stats What {@link #stats}T is set to.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @param gridFile The directory containing a grid that is to be loaded to
     * initialise this.
     * @param cf The factory used to create chunks.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     * @param startRow The row of {@code g} which is the bottom most row of
     * this.
     * @param startCol The column of {@code g} which is the left most column of
     * this.
     * @param endRow The row of {@code g} which is the top most row of this.
     * @param endCol The column of {@code g} which is the right most column of
     * this.
     * @param e The grids environment.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     */
    protected Grids_GridBoolean(Grids_StatsBoolean stats, IO_Cache fs,
            long id, Generic_Path gridFile, Grids_ChunkFactoryBoolean cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, Grids_Environment e)
            throws IOException, ClassNotFoundException, Exception {
        super(e, fs, id);
        init(stats, gridFile, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol);
    }

    /**
     * Creates a grid with values obtained from a grid cached in
     * {@code gridFile}.
     *
     * @param e The grids environment.
     * @param fs What {@link #fs} is set to.
     * @param id What {@link #fsID} is set to.
     * @param gridFile The directory containing a grid that is to be loaded to
     * initialise this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridBoolean(Grids_Environment e, IO_Cache fs,
            long id, Generic_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        super(e, fs, id);
        init(new Grids_StatsNotUpdatedBoolean(e), gridFile);
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridBoolean from which the fields of this are set.
     * @throws java.io.IOException If encountered.
     */
    private void init(Grids_GridBoolean g) throws IOException, Exception {
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
            ((Grids_StatsNotUpdatedBoolean) stats).setUpToDate(false);
        }
        stats.grid = this;
    }

    /**
     * @param stats What {@link #stats} is set to.
     * @param cf The factory used to create chunks.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     * @param nRows What {@link #nRows} is set to.
     * @param nCols What {@link #nCols} is set to.
     * @param dimensions What {@link #dim} is set to.
     * @throws java.io.IOException If encountered.
     */
    private void init(Grids_StatsBoolean stats, Grids_ChunkFactoryBoolean cf,
            int chunkNRows, int chunkNCols, long nRows, long nCols,
            Grids_Dimensions dimensions) throws IOException, Exception {
        env.checkAndMaybeFreeMemory();
        init(stats, chunkNRows, chunkNCols, nRows, nCols, dimensions);
        for (int r = 0; r < nChunkRows; r++) {
            for (int c = 0; c < nChunkCols; c++) {
                env.checkAndMaybeFreeMemory();
                // Try to load chunk.
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(r, c);
                Grids_ChunkBoolean chunk = cf.create(this, chunkID);
                data.put(chunkID, chunk);
            }
            env.env.log("Done chunkRow " + r + " out of " + nChunkRows);
        }
        init();
    }

    /**
     * @param stats What {@link #stats} is set to.
     * @param g The grid to initialise the values in this from.
     * @param cf The factory used to create chunks.
     * @param chunkNRows What {@link #chunkNRows} is set to.
     * @param chunkNCols What {@link #chunkNCols} is set to.
     * @param nRows What {@link #nRows} is set to.
     * @param nCols What {@link #nCols} is set to.
     * @param dimensions What {@link #dim} is set to.
     * @throws java.io.IOException If encountered.
     * @param startRow The row of {@code g} which is the bottom most row of
     * this.
     * @param startCol The column of {@code g} which is the left most column of
     * this.
     * @param endRow The row of {@code g} which is the top most row of this.
     * @param endCol The column of {@code g} which is the right most column of
     * this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    private void init(Grids_StatsBoolean stats, Grids_Grid g,
            Grids_ChunkFactoryBoolean cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol) throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        init(g, stats, chunkNRows, chunkNCols, startRow, startCol, endRow,
                endCol);
        int startChunkRow = g.getChunkRow(startRow);
        int endChunkRow = g.getChunkRow(endRow);
        //int ncr = endChunkRow - startChunkRow + 1;
        int startChunkCol = g.getChunkCol(startCol);
        int endChunkCol = g.getChunkCol(endCol);
        if (g instanceof Grids_GridBoolean) {
            Grids_GridBoolean gb = (Grids_GridBoolean) g;
            for (int gcr = startChunkRow; gcr <= endChunkRow; gcr++) {
                int gChunkNRows = g.getChunkNRows(gcr);
                for (int gcc = startChunkCol; gcc <= endChunkCol; gcc++) {
                    boolean isLoadedChunk;
                    do {
                        isLoadedChunk = loadChunk(g, cf, chunkNRows, chunkNCols,
                                startRow, startCol, endRow, endCol,
                                startChunkRow, endChunkRow,
                                startChunkCol, endChunkCol, gb, gcr,
                                gChunkNRows, gcc);
                    } while (!isLoadedChunk);
                    //loadedChunkCount++;
                    //cci1 = _ChunkColIndex;
                }
                env.env.log("Done chunkRow " + gcr + " out of " + nChunkRows);
            }
        } else if (g instanceof Grids_GridBinary) {
            // Implementation needed...
        } else {
            //(g instanceof Grids_GridNumber) {
            // Implementation needed...
        }
        init();
    }

    /**
     * For loading a chunk.
     *
     * @param g The grid.
     * @param cf Chunk factory.
     * @param chunkNRows Chunk NRows
     * @param chunkNCols Chunk NCols
     * @param startRow Start row.
     * @param startCol Start column.
     * @param endRow End row.
     * @param endCol End column.
     * @param startChunkRow Start chunk row.
     * @param endChunkRow End chunk row.
     * @param startChunkCol Start chunk column.
     * @param endChunkCol End chunk column.
     * @param gb Grid boolean.
     * @param gcr Grid chunk row.
     * @param gChunkNRows Chunk nRows
     * @param gcc Grid chunk column.
     * @return {@code true} if chunk was loaded.
     * @throws ClassNotFoundException If encountered.
     * @throws Exception If encountered.
     */
    protected boolean loadChunk(Grids_Grid g, Grids_ChunkFactoryBoolean cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, int startChunkRow, int endChunkRow,
            int startChunkCol, int endChunkCol,
            Grids_GridBoolean gb, int gcr, int gChunkNRows, int gcc)
            throws ClassNotFoundException, Exception {
        boolean isLoadedChunk = false;
        try {
            // Try to load chunk.
            Grids_2D_ID_int gChunkID = new Grids_2D_ID_int(gcr, gcc);
            loadChunk(gChunkID, g, gb, gcc, gcr, cf, gChunkNRows, startRow,
                    endRow, startCol, endCol);
            isLoadedChunk = true;
            env.removeFromNotToClear(g, gChunkID);
        } catch (OutOfMemoryError e) {
            if (env.HOOME) {
                env.clearMemoryReserve(env.env);
                Grids_2D_ID_int i = new Grids_2D_ID_int(gcr, gcc);
                freeSomeMemoryAndResetReserve(i, e);
                HashMap<Grids_Grid, Set<Grids_2D_ID_int>> notToClear
                        = new HashMap<>();
                notToClear.put(g, g.getChunkIDs(startRow, endRow, startCol,
                        endCol));
                Set<Grids_2D_ID_int> s = new HashSet<>();
                s.add(i);
                notToClear.put(this, s);
                if (env.swapChunksExcept_Account(notToClear, false).detail < 1) {
                    throw e;
                }
                env.initMemoryReserve(this, i, env.HOOME);
            } else {
                throw e;
            }
        }
        return isLoadedChunk;
    }

    /**
     * Load a chunk.
     *
     * @param gChunkID gChunkID
     * @param g g
     * @param gb gb
     * @param gcc gcc
     * @param gcr gcr
     * @param cf cf
     * @param gChunkNRows gChunkNRows
     * @param startRow startRow
     * @param endRow endRow
     * @param startCol startCol
     * @param endCol endCol
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     * @throws Exception If encountered.
     */
    protected void loadChunk(Grids_2D_ID_int gChunkID, Grids_Grid g,
            Grids_GridBoolean gb, int gcc, int gcr,
            Grids_ChunkFactoryBoolean cf, int gChunkNRows, long startRow,
            long endRow, long startCol, long endCol) throws IOException,
            ClassNotFoundException, Exception {
        env.addToNotToClear(g, gChunkID);
        env.checkAndMaybeFreeMemory();
        Grids_ChunkBooleanArray c = gb.getChunk(gChunkID);
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
                         * Initialise chunk if it does not exist This is here
                         * rather than where chunkID is initialised as there may
                         * not be a chunk for the chunkID.
                         */
                        if (isInGrid(row, col)) {
                            Grids_2D_ID_int i = new Grids_2D_ID_int(
                                    chunkRow, chunkCol);
                            //ge.addToNotToClear(this, chunkID);
                            Grids_ChunkBoolean chunk;
                            if (!data.containsKey(i)) {
                                chunk = cf.create(this, i);
                                data.put(i, chunk);
                            } else {
                                chunk = (Grids_ChunkBooleanArray) data.get(i);
                            }
                            boolean gValue = gb.getCell(c, cr, cc);
                            if (gValue) {
                                initCell(chunk, row, col, gValue);
                            }
                            //ge.removeFromNotToClear(this, chunkID);
                        }
                    }
                }
            }
        }
    }

    /**
     * For initialising.
     *
     * @param stats stats
     * @param gridFile gridFile
     * @param cf cf
     * @param chunkNRows chunkNRows
     * @param chunkNCols chunkNCols
     * @param startRow startRow
     * @param startCol startCol
     * @param endRow endRow
     * @param endCol endCol
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     * @throws Exception If encountered.
     */
    protected final void init(Grids_StatsBoolean stats, Generic_Path gridFile,
            Grids_ChunkFactoryBoolean cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol) throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        if (Files.isDirectory(gridFile.getPath())) {
            if (true) {
                Grids_Processor gp = env.getProcessor();
                Grids_GridFactoryBoolean gf = gp.gridFactoryBoolean;
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                Grids_GridBoolean g = (Grids_GridBoolean) gf.create(
                        (Grids_Grid) Generic_IO.readObject(thisFile));
                Grids_GridBoolean g2 = gf.create(g, startRow, startCol,
                        endRow, endCol);
                init(g2);
            }
        } else {
            throw new IOException(gridFile.toString() + " is not a directory.");
        }
    }

    private void init(Grids_StatsBoolean stats, Generic_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        // Set to report every 10%
        int reportN;
        Grids_Processor gp = env.getProcessor();
        if (Files.isDirectory(gridFile.getPath())) {
            if (true) {
                Grids_GridFactoryBoolean gf = gp.gridFactoryBoolean;
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                Grids_GridBoolean g = (Grids_GridBoolean) gf.create(
                        (Grids_Grid) Generic_IO.readObject(thisFile));
                init(g);
                //this.data = g.data;
                this.worthSwapping = g.worthSwapping;
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
                chunkNRows = gp.gridFactoryBoolean.getChunkNRows();
                chunkNCols = gp.gridFactoryBoolean.getChunkNCols();
                initNChunkRows();
                initNChunkCols();
                initDimensions(header, 0, 0);
                reportN = (int) (nRows - 1) / 10;
                if (reportN == 0) {
                    reportN = 1;
                }
                BigDecimal gridFileNoDataValue = header.ndv;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (stats.isUpdated()) {
                    for (long row = (nRows - 1); row > -1; row--) {
                        env.checkAndMaybeFreeMemory();
                        env.initNotToClear();
                        for (long col = 0; col < nCols; col++) {
                            value = eagi.readBigDecimal();
                            if (value != gridFileNoDataValue) {
                                if (value.compareTo(BigDecimal.ZERO) == 0) {
                                    initCell(row, col, false, false);
                                } else {
                                    initCell(row, col, true, false);
                                }
                            }
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
                            if (value != gridFileNoDataValue) {
                                if (value.compareTo(BigDecimal.ZERO) == 0) {
                                    initCell(row, col, false, true);
                                } else {
                                    initCell(row, col, true, true);
                                }
                            }
                        }
                        if (row % reportN == 0) {
                            env.env.log("Done row " + row);
                        }
                        env.checkAndMaybeFreeMemory();
                    }
                }
            }
        }
        init();
    }

    /**
     * @param row The row to set the value in.
     * @param col The col to set the value in.
     * @param value The value to set.
     * @param fast If true then the cell value setting is done faster, but the
     * statistics are not kept current.
     */
    private void initCell(long row, long col, Boolean value, boolean fast)
            throws IOException, ClassNotFoundException, Exception {
        Grids_ChunkBooleanArray chunk;
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
        /**
         * Ensure the chunk with chunkID is not swapped out and initialise it if
         * it does not already exist.
         */
        env.addToNotToClear(this, chunkID);
        if (!data.containsKey(chunkID)) {
            Grids_ChunkBooleanArray gc = new Grids_ChunkBooleanArray(this, chunkID);
            data.put(chunkID, gc);
        } else {
            Grids_Chunk c = data.get(chunkID);
            if (c == null) {
                loadChunk(chunkID);
            }
            chunk = (Grids_ChunkBooleanArray) data.get(chunkID);
            if (fast) {
                initCellFast(chunk, row, col, value);
            } else {
                initCell(chunk, row, col, value);
            }
        }
    }

    /**
     * @return A chunk for the given chunkID.
     * @param chunkID The identifier for the chunk to return.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_ChunkBooleanArray getChunk(Grids_2D_ID_int chunkID)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(chunkID)) {
            if (data.get(chunkID) == null) {
                loadChunk(chunkID);
            }
            return (Grids_ChunkBooleanArray) data.get(chunkID);
        }
        return null;
    }

    /**
     * @return Grids_ChunkBooleanArray for the given chunkID.
     * @param chunkID The identifier for the chunk to return.
     * @param chunkRow The chunk row.
     * @param chunkCol The chunk col.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_ChunkBooleanArray getChunk(Grids_2D_ID_int chunkID, int chunkRow,
            int chunkCol) throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(chunkRow, chunkCol)) {
            if (data.get(chunkID) == null) {
                loadChunk(chunkID);
            }
            return (Grids_ChunkBooleanArray) data.get(chunkID);
        }
        return null;
    }

    /**
     * If newValue and oldValue are the same then stats won't change. A test
     * might be appropriate in set cell so that this method is not called.
     *
     * WARNING! This should not be public, please don't use it. The reason it
     * has been made public is to allow access from chunk setCell methods which
     * may be accessed directly instead of via setCell in this class.
     *
     * @param newValue The value replacing oldValue.
     * @param oldValue The value being replaced.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected void upDateGridStatistics(Boolean newValue, Boolean oldValue)
            throws IOException, Exception, ClassNotFoundException {
        if (!(newValue == null && oldValue == null)) {
            if (stats.getClass() == Grids_StatsBoolean.class) {
                if (newValue
                        == false) {
                    if (oldValue == false) {
                        //stats.setN(stats.getN().subtract(BigInteger.ONE));
                        stats.setN(stats.getN() - 1);
                    }
                    //stats.setN(stats.getN().add(BigInteger.ONE));
                    stats.setN(stats.getN() + 1);
                }
            } else {
                if (newValue == null) {
                    ((Grids_StatsNotUpdatedBoolean) stats).setUpToDate(false);
                } else if (oldValue == null) {
                    ((Grids_StatsNotUpdatedBoolean) stats).setUpToDate(false);
                } else {
                    if (!Objects.equals(newValue, oldValue)) {
                        ((Grids_StatsNotUpdatedBoolean) stats).setUpToDate(false);
                    }
                }
            }

        }
    }

    /**
     * @param r The grid cell row index for which the value is returned.
     * @param c The grid cell column index for which the value is returned
     * @return The value in the grid at grid cell row index {@code r}, grid cell
     * column index {@code c} or {@code null} if there is no such value.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Boolean getCell(long r, long c) throws IOException,
            ClassNotFoundException, Exception {
        if (isInGrid(r, c)) {
            return getCell((Grids_ChunkBooleanArray) getChunk(getChunkRow(r),
                    getChunkCol(c)), getChunkCellRow(r), getChunkCellCol(c));
        }
        return null;
    }

    /**
     * For getting the value in chunk at chunk cell row {@code r}, chunk cell
     * col {@code c}.
     *
     * @param chunk The chunk.
     * @param r The chunk cell row index of the value returned.
     * @param c The chunk cell column index of the value returned.
     * @return Value in chunk at chunk cell row {@code r}, chunk cell col
     * {@code c} or {@code null} if there is no such value.
     */
    public Boolean getCell(Grids_ChunkBooleanArray chunk, int r, int c) {
        if (chunk.inChunk(r, c)) {
            return chunk.getCell(r, c);
        }
        return null;
    }

    /**
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return The value at x-coordinate {@code x}, y-coordinate {@code y} or
     * {@code null} if there is no such value.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final boolean getCell(Math_BigRational x, Math_BigRational y) throws IOException,
            ClassNotFoundException, Exception {
        return getCell(getRow(y), getCol(x));
    }

    /**
     * @param i The cell ID.
     * @return The value of the cell with cell ID {@code i} or {@code null} if
     * there is no such cell in the grid.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final Boolean getCell(Grids_2D_ID_long i) throws IOException,
            ClassNotFoundException, Exception {
        return getCell(i.getRow(), i.getCol());
    }

    /**
     * For setting value at x-coordinate {@code x}, y-coordinate {@code y}.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param v The value to set at (x, y).
     * @return The value at x-coordinate {@code x}, y-coordinate {@code y} or
     * {@code null} if there is no such value.
     *
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Boolean setCell(Math_BigRational x, Math_BigRational y, Boolean v)
            throws IOException, ClassNotFoundException, Exception {
        return setCell(getRow(y), getCol(x), v);
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
     * {@code c} or {@code null} if there is no such value.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Boolean setCell(long r, long c, Boolean v)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(r, c)) {
            return setCell((Grids_ChunkBooleanArray) getChunk(getChunkRow(r),
                    getChunkCol(c)), getChunkCellRow(r), getChunkCellCol(c), v);
        }
        return null;
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
    public Boolean setCell(int cr, int cc, int ccr, int ccc, boolean v)
            throws IOException, ClassNotFoundException, Exception {
        return setCell((Grids_ChunkBooleanArray) getChunk(cr, cc), ccr, ccc, v);
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
    public Boolean setCell(Grids_ChunkBooleanArray chunk, int ccr, int ccc,
            Boolean v) throws IOException, Exception, ClassNotFoundException {
        Boolean r = chunk.setCell(ccr, ccc, v);
        // Update stats
        upDateGridStatistics(v, r);
        return r;
    }

    /**
     * For initialising the value in chunk at row, col of this grid.
     * {@link #stats} are updated.
     *
     * @param chunk The chunk in which the cell value is initialised.
     * @param r The row in the grid containing the cell which value is to be
     * set.
     * @param c The column in the grid containing the cell which value is to be
     * set.
     * @param value The value to initialise.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected void initCell(Grids_ChunkBoolean chunk, long r, long c,
            Boolean value) throws IOException, Exception, ClassNotFoundException {
        chunk.initCell(getChunkCellRow(r), getChunkCellCol(c), value);
        if (value != null) {
            //stats.setN(stats.getN().add(BigInteger.ONE));
            stats.setN(stats.getN() + 1);
        }
    }

    /**
     * Initialises the value at row, col not updating {@link #stats}.
     *
     * @param chunk The chunk in which the value is to by initialised.
     * @param row The row in the grid containing the cell which value is to be
     * set.
     * @param col The column in the grid containing the cell which value is to
     * be set.
     * @param value The value to initialise.
     */
    protected void initCellFast(Grids_ChunkBooleanArray chunk, long row, long col,
            Boolean value) {
        chunk.initCell(getChunkCellRow(row), getChunkCellCol(col), value);
    }

    /**
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param distance2 The radius of the circle squared for which intersected
     * cell values are returned.
     * @return An array of all cell values for cells that's centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Boolean[] getCells(Math_BigRational x, Math_BigRational y,
            Math_BigRational distance2) throws IOException,
            ClassNotFoundException, Exception {
        return getCells(x, y, getRow(y), getCol(x), distance2);
    }

    /**
     * @param row The row index for the cell that's centroid is the circle
     * centre from which cell values are returned.
     * @param col The column index for the cell that's centroid is the circle
     * centre from which cell values are returned.
     * @param distance2 the radius of the circle for which intersected cell
     * values are returned squared.
     * @return An array of all cell values for cells that's centroids are
     * intersected by circle with centre at centroid of cell given by cell row
     * index row, cell column index col, and radius distance.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Boolean[] getCells(long row, long col, Math_BigRational distance2)
            throws IOException, ClassNotFoundException, Exception {
        return getCells(getCellX(col), getCellY(row), row, col, distance2);
    }

    /**
     * @param x The x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     * returned.
     * @param row The row index at y.
     * @param col The column index at x.
     * @param distance2 The radius of the circle squared for which intersected
     * cell values are returned squared.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     * @return The cells.
     */
    protected Boolean[] getCells(Math_BigRational x, Math_BigRational y,
            long row, long col, Math_BigRational distance2) throws IOException,
            ClassNotFoundException, Exception {
        int delta = x.divide(y).ceil().intValue();
        Boolean[] r = new Boolean[((2 * delta) + 1) * ((2 * delta) + 1)];
        int count = 0;
        for (long p = row - delta; p <= row + delta; p++) {
            Math_BigRational thisY = getCellY(row);
            for (long q = col - delta; q <= col + delta; q++) {
                Math_BigRational thisX = getCellX(col);
                if (Grids_Utilities.distance2(x, y, thisX, thisY)
                        .compareTo(distance2) == -1) {
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
     * Initialises all cell values to {@code v}.
     *
     * @param v The value to set.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void initCells(Boolean v) throws IOException, ClassNotFoundException, Exception {
        Iterator<Grids_2D_ID_int> ite = data.keySet().iterator();
        int nChunks = data.size();
        int counter = 0;
        while (ite.hasNext()) {
            env.checkAndMaybeFreeMemory();
            env.env.log("Initialising Chunk " + counter + " out of " + nChunks);
            counter++;
            Grids_2D_ID_int chunkID = ite.next();
            Grids_ChunkBooleanArray chunk = getChunk(chunkID);
            int cnr = getChunkNRows(chunkID);
            int cnc = getChunkNCols(chunkID);
            for (int row = 0; row < cnr; row++) {
                for (int col = 0; col < cnc; col++) {
                    chunk.initCell(row, col, v);
                }
            }
        }
    }

    /**
     * @return A {@link Grids_GridIteratorBoolean} for iterating over the cell
     * values in this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridIteratorBoolean iterator() throws IOException,
            ClassNotFoundException, Exception {
        return new Grids_GridIteratorBoolean(this);
    }

    /**
     * @return {@code (Grids_StatsBoolean) stats}
     */
    @Override
    public Grids_StatsBoolean getStats() {
        return (Grids_StatsBoolean) stats;
    }

    /**
     * Test if this and {@code g} have the same dimensions, the same number of
     * rows and columns, and the same values in each cell. The chunks are
     * allowed to be stored differently as are the statistics.
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
        if (!(g instanceof Grids_GridBoolean)) {
            return false;
        }
        if (!isSameDimensions(g)) {
            return false;
        }
        Grids_GridBoolean gb = (Grids_GridBoolean) g;
        for (int cr = 0; cr < this.nChunkRows; cr++) {
            int cnr = gb.getChunkNRows(cr);
            for (int cc = 0; cc < this.nChunkCols; cc++) {
                int cnc = gb.getChunkNCols(cc);
                Grids_2D_ID_int i = new Grids_2D_ID_int(cr, cc);
                env.addToNotToClear(gb, i);
                // Add to not to clear a row of this chunks.
                long rowMin = gb.getRow(cr, 0);
                long rowMax = gb.getRow(cr, cnr);
                long colMin = gb.getCol(cc, 0);
                long colMax = gb.getCol(cc, cnc);
                Set<Grids_2D_ID_int> s = getChunkIDs(rowMin, rowMax, colMin,
                        colMax);
                env.addToNotToClear(this, s);
                env.checkAndMaybeFreeMemory();
                Grids_ChunkBooleanArray chunk = getChunk(i, cr, cc);
                for (int ccr = 0; ccr < cnr; ccr++) {
                    long row = gb.getRow(cr, ccr);
                    for (int ccc = 0; ccc < cnc; ccc++) {
                        long col = gb.getCol(cc, ccc);
                        Boolean v = getCell(row, col);
                        Boolean gv = chunk.getCell(cr, cc);
                        if (v == null) {
                            if (gv != null) {
                                return false;
                            }
                        } else {
                            if (gv == null) {
                                return false;
                            } else {
                                if (!Objects.equals(v, gv)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                env.removeFromNotToClear(gb, i);
                env.removeFromNotToClear(this, s);
            }
        }
        return true;
    }

    /**
     * Used to help log a view of the grid.
     *
     * @param ncols The number of columns in the grid.
     * @param c The number of columns to write out.
     * @param row The row of the grid to write out.
     * @throws Exception If encountered.
     */
    @Override
    protected void logRow(long ncols, long c, long row) throws Exception {
        String s = " " + getStringValue(BigDecimal.valueOf(row)) + " | ";
        if (ncols < c) {
            long col;
            for (col = 0; col < ncols - 1; col++) {
                s += getStringValue(getCell(row, col)) + " | ";
            }
            s += getStringValue(getCell(row, col)) + " | ";
            env.env.log(s);
        } else {
            for (long col = 0; col < c - 1; col++) {
                s += getStringValue(getCell(row, col)) + " | ";
            }
            s += "  |";
            s += " " + getStringValue(getCell(row, ncols - 1)) + " |";
            env.env.log(s);
        }
    }

    /**
     * Used to help log a view of the grid.
     *
     * @param v The value to represent as a String.
     * @return a String representation of {@code v}.
     */
    protected String getStringValue(Boolean v) {
        if (v == null) {
            return "     *    ";
        }
        if (v) {
            return "   true   ";
        }
        return "   false  ";
    }

}
