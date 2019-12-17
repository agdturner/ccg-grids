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
package io.github.agdturner.grids.d2.grid.b;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.core.Grids_2D_ID_long;
import io.github.agdturner.grids.core.Grids_Dimensions;
import io.github.agdturner.grids.d2.chunk.Grids_Chunk;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.d2.grid.Grids_Grid;
import io.github.agdturner.grids.d2.chunk.b.Grids_ChunkBinaryArray;
import io.github.agdturner.grids.d2.chunk.b.Grids_ChunkFactoryBinary;
import io.github.agdturner.grids.d2.stats.Grids_StatsBinary;
import io.github.agdturner.grids.d2.stats.Grids_StatsNotUpdatedBinary;
import io.github.agdturner.grids.process.Grids_Processor;
import io.github.agdturner.grids.util.Grids_Utilities;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_FileStore;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;

/**
 * A binary grid. This is a grid containing boolean values. There is no
 * noDataValue for this type of grid. For this type of grid the N in the
 * {@link #stats} is the umber of true cells.
 *
 * @see uk.ac.leeds.ccg.agdt.grids.d2.grids.Grids_Grid
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridBinary extends Grids_Grid {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new Grids_GridBinary with each cell value equal to {@code ndv}
     * and all chunks of the same type.
     *
     * @param stats What {@link #stats}T is set to.
     * @param baseDir The baseDir of the file store in which this grid may be
     * stored.
     * @param id The id of the grid in the file store.
     * @param cf The factory used to create chunks.
     * @param chunkNRows What {@link #ChunkNRows} is set to.
     * @param chunkNCols What {@link #ChunkNCols} is set to.
     * @param nRows What {@link #NRows} is set to.
     * @param nCols What {@link #NCols} is set to.
     * @param dimensions What {@link #Dimensions} is set to.
     * @param e The grids environment.
     * @throws java.io.IOException If encountered.
     */
    protected Grids_GridBinary(Grids_StatsBinary stats,
            Generic_FileStore fs, long id, Grids_ChunkFactoryBinary cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions,
            Grids_Environment e) throws IOException, Exception {
        super(e, fs, id);
        init(stats, cf, chunkNRows, chunkNCols, nRows, nCols, dimensions);
    }

    /**
     * Creates a new Grids_GridBinary based on values in grid.
     *
     * @param stats What {@link #stats}T is set to.
     * @param dir What {@link #dir} is set to.
     * @param baseDir The baseDir of the file store in which this grid may be
     * stored.
     * @param id The id of the grid in the file store.
     * @param g The {@link Grids_Grid} used to construct this.
     * @param cf The factory used to create chunks.
     * @param chunkNRows What {@link #ChunkNRows} is set to.
     * @param chunkNCols What {@link #ChunkNCols} is set to.
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
    protected Grids_GridBinary(Grids_StatsBinary stats, Generic_FileStore fs,
            long id, Grids_Grid g, Grids_ChunkFactoryBinary cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol) throws IOException, ClassNotFoundException, Exception {
        super(g.env, fs, id);
        init(stats, g, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol);
    }

    /**
     * Creates a new Grids_GridBinary with values obtained from a grid cached in
     * gridFile.
     *
     * @param stats What {@link #stats}T is set to.
     * @param dir What {@link #dir} is set to.
     * @param baseDir The baseDir of the file store in which this grid may be
     * stored.
     * @param id The id of the grid in the file store.
     * @param gridFile The directory containing a grid that is to be loaded to
     * initialise this.
     * @param cf The factory used to create chunks.
     * @param chunkNRows What {@link #ChunkNRows} is set to.
     * @param chunkNCols What {@link #ChunkNCols} is set to.
     * @param startRow The row of {@code g} which is the bottom most row of
     * this.
     * @param startCol The column of {@code g} which is the left most column of
     * this.
     * @param endRow The row of {@code g} which is the top most row of this.
     * @param endCol The column of {@code g} which is the right most column of
     * this.
     * @param e The grids environment.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridBinary(Grids_StatsBinary stats, Generic_FileStore fs,
            long id, Generic_Path gridFile, Grids_ChunkFactoryBinary cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, Grids_Environment e)
            throws IOException, ClassNotFoundException, Exception {
        super(e, fs, id);
        init(stats, gridFile, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol);
    }

    /**
     * Creates a new Grids_GridBinary with values obtained from a grid cached in
     * {@code gridFile}.
     *
     * @param dir What {@link #dir} is set to.
     * @param baseDir The baseDir of the file store in which this grid may be
     * stored.
     * @param id The id of the grid in the file store.
     * @param gridFile The directory containing a grid that is to be loaded to
     * initialise this.
     * @param e The grids environment.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridBinary(Grids_Environment e, Generic_FileStore fs,
            long id, Generic_Path gridFile) throws IOException,
            ClassNotFoundException, Exception {
        super(e, fs, id);
        init(new Grids_StatsNotUpdatedBinary(e), gridFile);
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridBinary from which the fields of this are set.
     * @throws java.io.IOException If encountered.
     */
    private void init(Grids_GridBinary g) throws IOException {
        stats = g.stats;
        super.init(g);
        chunkIDChunkMap = g.chunkIDChunkMap;
        setReferenceInChunkIDChunkMap();
        ChunkIDsOfChunksWorthCaching = g.ChunkIDsOfChunksWorthCaching;
        // Set the reference to this in stats
        stats.setGrid(this);
        super.init();
    }

    @Override
    protected void init() throws IOException {
        super.init();
        if (!stats.isUpdated()) {
            ((Grids_StatsNotUpdatedBinary) stats).setUpToDate(false);
        }
        stats.grid = this;
    }

    /**
     * @param stats What {@link #stats} is set to.
     * @param cf The factory used to create chunks.
     * @param chunkNRows What {@link #ChunkNRows} is set to.
     * @param chunkNCols What {@link #ChunkNCols} is set to.
     * @param nRows What {@link #NRows} is set to.
     * @param nCols What {@link #NCols} is set to.
     * @param dimensions What {@link #Dimensions} is set to.
     * @throws java.io.IOException If encountered.
     */
    private void init(Grids_StatsBinary stats,
            Grids_ChunkFactoryBinary cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions)
            throws IOException, Exception {
        env.checkAndMaybeFreeMemory();
        init(stats, chunkNRows, chunkNCols, nRows, nCols, dimensions);
        for (int r = 0; r < NChunkRows; r++) {
            for (int c = 0; c < NChunkCols; c++) {
                env.checkAndMaybeFreeMemory();
                // Try to load chunk.
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(r, c);
                Grids_ChunkBinaryArray chunk = cf.create(this, chunkID);
                chunkIDChunkMap.put(chunkID, chunk);
            }
            env.env.log("Done chunkRow " + r + " out of " + NChunkRows);
        }
        init();
    }

    /**
     * @param stats What {@link #stats} is set to.
     * @param g The grid to initialise the values in this from.
     * @param cf The factory used to create chunks.
     * @param chunkNRows What {@link #ChunkNRows} is set to.
     * @param chunkNCols What {@link #ChunkNCols} is set to.
     * @param nRows What {@link #NRows} is set to.
     * @param nCols What {@link #NCols} is set to.
     * @param dimensions What {@link #Dimensions} is set to.
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
    private void init(Grids_StatsBinary stats, Grids_Grid g,
            Grids_ChunkFactoryBinary cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol) throws IOException, ClassNotFoundException,
            Exception {
        env.checkAndMaybeFreeMemory();
        init(g, stats, chunkNRows, chunkNCols, startRow, startCol, endRow, endCol);
        boolean isLoadedChunk = false;
        int startChunkRow = g.getChunkRow(startRow);
        int endChunkRow = g.getChunkRow(endRow);
        int nChunkRows = endChunkRow - startChunkRow + 1;
        int startChunkCol = g.getChunkCol(startCol);
        int endChunkCol = g.getChunkCol(endCol);
        if (g instanceof Grids_GridBinary) {
            Grids_GridBinary gb = (Grids_GridBinary) g;
            for (int gcr = startChunkRow; gcr <= endChunkRow; gcr++) {
                int gChunkNRows = g.getChunkNRows(gcr);
                for (int gcc = startChunkCol; gcc <= endChunkCol; gcc++) {
                    do {
                        try {
                            // Try to load chunk.
                            Grids_2D_ID_int gChunkID = new Grids_2D_ID_int(gcr,
                                    gcc);
                            loadChunk(gChunkID, g, gb, gcc, gcr, cf,
                                    gChunkNRows, startRow, endRow, startCol,
                                    endCol);
                            isLoadedChunk = true;
                            env.removeFromNotToCache(g, gChunkID);
                        } catch (OutOfMemoryError e) {
                            if (env.HOOME) {
                                env.clearMemoryReserve();
                                freeSomeMemoryAndResetReserve(e);
                                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                                        gcr, gcc);
                                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> notToSwapOut = new HashMap<>();
                                notToSwapOut.put(g, g.getChunkIDs(startRow,
                                        endRow, startCol, endCol));
                                HashSet<Grids_2D_ID_int> s = new HashSet<>();
                                s.add(chunkID);
                                notToSwapOut.put(this, s);
                                if (env.cacheChunksExcept_Account(notToSwapOut,
                                        false) < 1L) {
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
        } else if (g instanceof Grids_GridBinary) {
            // Implementation needed...
        } else {
            //(g instanceof Grids_GridNumber) {
            // Implementation needed...
        }
        init();
    }

    public void loadChunk(Grids_2D_ID_int gChunkID, Grids_Grid g,
            Grids_GridBinary gb, int gcc, int gcr,
            Grids_ChunkFactoryBinary cf, int gChunkNRows, long startRow,
            long endRow, long startCol, long endCol) throws IOException,
            java.lang.ClassNotFoundException, Exception {
        env.addToNotToCache(g, gChunkID);
        env.checkAndMaybeFreeMemory();
        Grids_ChunkBinaryArray c = gb.getChunk(gChunkID);
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
                         * Initialise chunk if it does not exist This is here
                         * rather than where chunkID is initialised as there may
                         * not be a chunk for the chunkID.
                         */
                        if (isInGrid(row, col)) {
                            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                            //ge.addToNotToCache(this, chunkID);
                            Grids_ChunkBinaryArray chunk;
                            if (!chunkIDChunkMap.containsKey(chunkID)) {
                                chunk = cf.create(this, chunkID);
                                chunkIDChunkMap.put(chunkID, chunk);
                            } else {
                                chunk = (Grids_ChunkBinaryArray) chunkIDChunkMap.get(chunkID);
                            }
                            boolean gValue = gb.getCell(c, cellRow, cellCol);
                            if (gValue) {
                                initCell(chunk, row, col, gValue);
                            }
                            //ge.removeFromNotToCache(this, chunkID);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param stats
     * @param gridFile
     * @param cf
     * @param chunkNRows
     * @param chunkNCols
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol
     * @throws java.io.IOException If encountered or if gridFile is not a
     * directory.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    private void init(Grids_StatsBinary stats, Generic_Path gridFile,
            Grids_ChunkFactoryBinary cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol) throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        if (Files.isDirectory(gridFile)) {
            if (true) {
                Grids_Processor gp = env.getProcessor();
                Grids_GridFactoryBinary gf = gp.GridBinaryFactory;
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                Grids_GridBinary g = (Grids_GridBinary) gf.create(
                        (Grids_Grid) Generic_IO.readObject(thisFile));
                Grids_GridBinary g2 = gf.create(g, startRow, startCol,
                        endRow, endCol);
                init(g2);
            }
        } else {
            throw new IOException(gridFile.toString() + " is not a directory.");
        }
    }

    private void init(Grids_StatsBinary stats, Generic_Path gridFile)
            throws IOException, ClassNotFoundException, Exception {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        Grids_Processor gp = env.getProcessor();
        if (Files.isDirectory(gridFile)) {
            if (true) {
                Grids_GridFactoryBinary gf = gp.GridBinaryFactory;
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                Grids_GridBinary g = (Grids_GridBinary) gf.create(
                        (Grids_Grid) Generic_IO.readObject(thisFile));
                init(g);
                //this.chunkIDChunkMap = g.chunkIDChunkMap;
                this.ChunkIDsOfChunksWorthCaching = g.ChunkIDsOfChunksWorthCaching;
                this.Dimensions = g.Dimensions;
                this.stats = stats;
                this.stats.grid = this;
            }
        } else {
            throw new IOException(gridFile.toString() + " is not a directory.");
        }
    }

    /**
     *
     * @param row The row to set the value in.
     * @param col The col to set the value in.
     * @param value The value to set.
     * @param fast If true then the cell value setting is done faster, but the
     * statistics are not kept current.
     */
    private void initCell(long row, long col, boolean value, boolean fast)
            throws IOException, ClassNotFoundException, Exception {
        Grids_ChunkBinaryArray chunk;
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
        /**
         * Ensure the chunk with chunkID is not swapped out and initialise it if
         * it does not already exist.
         */
        env.addToNotToCache(this, chunkID);
        if (!chunkIDChunkMap.containsKey(chunkID)) {
            Grids_ChunkBinaryArray gc = new Grids_ChunkBinaryArray(this, chunkID);
            chunkIDChunkMap.put(chunkID, gc);
        } else {
            Grids_Chunk c = chunkIDChunkMap.get(chunkID);
            if (c == null) {
                loadIntoCacheChunk(chunkID);
            }
            chunk = (Grids_ChunkBinaryArray) chunkIDChunkMap.get(chunkID);
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
    public Grids_ChunkBinaryArray getChunk(Grids_2D_ID_int chunkID)
            throws IOException, ClassNotFoundException, Exception {
        if (isInGrid(chunkID)) {
            if (chunkIDChunkMap.get(chunkID) == null) {
                loadIntoCacheChunk(chunkID);
            }
            return (Grids_ChunkBinaryArray) chunkIDChunkMap.get(chunkID);
        }
        return null;
    }

    /**
     * @return Grids_ChunkBinaryArray for the given chunkID.
     * @param chunkID The identifier for the chunk to return.
     * @param chunkRow The chunk row.
     * @param chunkCol The chunk col.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Grids_ChunkBinaryArray getChunk(Grids_2D_ID_int chunkID, int chunkRow,
            int chunkCol) throws IOException, ClassNotFoundException,
            Exception {
        if (isInGrid(chunkRow, chunkCol)) {
            if (chunkIDChunkMap.get(chunkID) == null) {
                loadIntoCacheChunk(chunkID);
            }
            return (Grids_ChunkBinaryArray) chunkIDChunkMap.get(chunkID);
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
    protected void upDateGridStatistics(boolean newValue, boolean oldValue)
            throws IOException, Exception, ClassNotFoundException {
        if (stats.getClass() == Grids_StatsBinary.class) {
            if (newValue == false) {
                if (oldValue == false) {
                    stats.setN(stats.getN() - 1L);
                }
                stats.setN(stats.getN() + 1);
            }
        } else {
            if (newValue != oldValue) {
                ((Grids_StatsNotUpdatedBinary) stats).setUpToDate(false);
            }
        }
    }

    /**
     * @param row The row for which the value is returned.
     * @param col The column for which the value is returned
     * @return The value at (row, col).
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public boolean getCell(long row, long col) throws IOException,
            ClassNotFoundException, Exception {
//        boolean isInGrid = isInGrid(row, col);
//        if (isInGrid) {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        Grids_ChunkBinaryArray c = (Grids_ChunkBinaryArray) getChunk(chunkRow, chunkCol);
        int cellRow = getCellRow(row);
        int cellCol = getCellCol(col);
        return getCell(c, cellRow, cellCol);
//        }
//        return null;
    }

    /**
     * For getting the value in chunk at cellRow, cellCol.
     *
     * @param chunk The chunk in which the value is returned.
     * @param cellRow The chunk cell row index of the cell that's value is
     * returned.
     * @param cellCol The chunk cell column index of the cell that's value is
     * returned.
     * @return Value at position given by chunk row index _ChunkRowIndex, chunk
     * column index _ChunkColIndex, chunk cell row index cellRow, chunk cell
     * column index cellCol.
     *
     */
    public boolean getCell(Grids_ChunkBinaryArray chunk, int cellRow, int cellCol) {
        return chunk.getCell(cellRow, cellCol);
    }

    /**
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @return The value at (x, y).
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final boolean getCell(BigDecimal x, BigDecimal y) throws IOException,
            ClassNotFoundException, Exception {
        long row = getRow(y);
        long col = getCol(x);
        boolean isInGrid = isInGrid(row, col);
        if (isInGrid) {
            return getCell(row, col);
        }
        return false;
    }

    /**
     * @param cellID the Grids_2D_ID_long of the cell.
     * @return The value of the cell with cellID.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final boolean getCell(Grids_2D_ID_long cellID) throws IOException,
            ClassNotFoundException, Exception {
        return getCell(cellID.getRow(), cellID.getCol());
    }

    /**
     * For setting the value at x-coordinate x, y-coordinate y.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param value The value to set at (x, y).
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public final void setCell(BigDecimal x, BigDecimal y, boolean value)
            throws IOException, ClassNotFoundException, Exception {
        setCell(getRow(y), getCol(x), value);
    }

    /**
     * For setting the value at row, col.
     *
     * @param row The cell row of the value to set.
     * @param col The cell column of the value to set.
     * @param value The value to set at (x, y).
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void setCell(long row, long col, boolean value) throws IOException,
            ClassNotFoundException, Exception {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        int cellRow = getCellRow(row);
        int cellCol = getCellCol(col);
        setCell((Grids_ChunkBinaryArray) getChunk(chunkRow, chunkCol), cellRow,
                cellCol, value);
    }

    /**
     * For setting the value at chunkRow, chunkCol, cellRow, cellCol.
     *
     * @param chunkRow The chunkRow of the value to set.
     * @param chunkCol The chunkCol of the value to set.
     * @param cellRow The cellRow of the value to set.
     * @param cellCol The cellCol of the value to set.
     * @param value The value to set at chunkRow, chunkCol, cellRow, cellCol.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void setCell(int chunkRow, int chunkCol, int cellRow, int cellCol,
            boolean value) throws IOException, ClassNotFoundException,
            Exception {
        Grids_ChunkBinaryArray chunk;
        chunk = (Grids_ChunkBinaryArray) getChunk(chunkRow, chunkCol);
        setCell(chunk, cellRow, cellCol, value);
    }

    /**
     * For setting the value in chunk at cellRow, cellCol.
     *
     * @param chunk The chunk in which the value is to be set.
     * @param cellRow The row in chunk of the value to set.
     * @param cellCol The col in chunk of the value to set.
     * @param value The value to set in chunk at cellRow, cellCol.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void setCell(Grids_ChunkBinaryArray chunk, int cellRow, int cellCol,
            boolean value) throws IOException, Exception, ClassNotFoundException {
        boolean v = chunk.setCell(cellRow, cellCol, value);
        // Update stats
        upDateGridStatistics(value, v);
    }

    /**
     * For initialising the value in chunk at row, col of this grid.
     * {@link #stats} are updated.
     *
     * @param chunk The chunk in which the cell value is initialised.
     * @param row The row in the grid containing the cell which value is to be
     * set.
     * @param col The column in the grid containing the cell which value is to
     * be set.
     * @param value The value to initialise.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected void initCell(Grids_ChunkBinaryArray chunk, long row, long col,
            boolean value) throws IOException, Exception, ClassNotFoundException {
        chunk.initCell(getCellRow(row), getCellCol(col), value);
        if (value) {
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
    protected void initCellFast(Grids_ChunkBinaryArray chunk, long row, long col,
            boolean value) {
        chunk.initCell(getCellRow(row), getCellCol(col), value);
    }

    /**
     * @param dp The number of decimal places for the precision of distance
     * calculations.
     * @param rm The RoundingMode for distance calculations.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @return An array of all cell values for cells that's centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected boolean[] getCells(BigDecimal x, BigDecimal y,
            BigDecimal distance, int dp, RoundingMode rm) throws IOException,
            ClassNotFoundException, Exception {
        return getCells(x, y, getRow(y), getCol(x), distance, dp, rm);
    }

    /**
     * @param dp The number of decimal places for the precision of distance
     * calculations.
     * @param rm The RoundingMode for distance calculations.
     * @param row The row index for the cell that's centroid is the circle
     * centre from which cell values are returned.
     * @param col The column index for the cell that's centroid is the circle
     * centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @return An array of all cell values for cells that's centroids are
     * intersected by circle with centre at centroid of cell given by cell row
     * index row, cell column index col, and radius distance.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public boolean[] getCells(long row, long col, BigDecimal distance, int dp,
            RoundingMode rm) throws IOException, ClassNotFoundException,
            Exception {
        return getCells(getCellXBigDecimal(col), getCellYBigDecimal(row), row,
                col, distance, dp, rm);
    }

    /**
     * @param dp The number of decimal places for the precision of distance
     * calculations.
     * @param rm The RoundingMode for distance calculations.
     * @param x The x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     * returned.
     * @param row The row index at y.
     * @param col The column index at x.
     * @param distance The radius of the circle for which intersected cell
     * values are returned.
     * @return An array of all cell values for cells that's centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected boolean[] getCells(BigDecimal x, BigDecimal y, long row, long col,
            BigDecimal distance, int dp, RoundingMode rm) throws IOException,
            ClassNotFoundException, Exception {
        int delta = Math_BigDecimal.ceilingSignificantDigit(
                Math_BigDecimal.divideRoundIfNecessary(x, y, 1,
                        RoundingMode.UP)).intValueExact();
        boolean[] cells = new boolean[((2 * delta) + 1) * ((2 * delta) + 1)];
        int count = 0;
        for (long p = row - delta; p <= row + delta; p++) {
            BigDecimal thisY = getCellYBigDecimal(row);
            for (long q = col - delta; q <= col + delta; q++) {
                BigDecimal thisX = getCellXBigDecimal(col);
                if (Grids_Utilities.distance(x, y, thisX, thisY, dp, rm)
                        .compareTo(distance) == -1) {
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
     * Initialises all cell values to {@code v}.
     *
     * @param v The value to set.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void initCells(boolean v) throws IOException, ClassNotFoundException,
            Exception {
        Iterator<Grids_2D_ID_int> ite = chunkIDChunkMap.keySet().iterator();
        int nChunks = chunkIDChunkMap.size();
        int counter = 0;
        while (ite.hasNext()) {
            env.checkAndMaybeFreeMemory();
            env.env.log("Initialising Chunk " + counter + " out of " + nChunks);
            counter++;
            Grids_2D_ID_int chunkID = ite.next();
            Grids_ChunkBinaryArray chunk = getChunk(chunkID);
            int chunkNRows = getChunkNRows(chunkID);
            int chunkNCols = getChunkNCols(chunkID);
            for (int row = 0; row < chunkNRows; row++) {
                for (int col = 0; col < chunkNCols; col++) {
                    chunk.initCell(row, col, v);
                }
            }
        }
    }

    /**
     * @return A {@link Grids_GridIteratorBinary} for iterating over the cell
     * values in this.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Grids_GridIteratorBinary iterator() throws IOException,
            ClassNotFoundException, Exception {
        return new Grids_GridIteratorBinary(this);
    }

    /**
     * @return {@code (Grids_StatsBinary) stats}
     */
    @Override
    public Grids_StatsBinary getStats() {
        return (Grids_StatsBinary) stats;
    }

}
