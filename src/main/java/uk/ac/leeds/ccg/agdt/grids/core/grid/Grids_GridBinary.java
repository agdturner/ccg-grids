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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkBinary;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkBinaryFactory;
import uk.ac.leeds.ccg.agdt.grids.core.grid.stats.Grids_GridBinaryStats;
import uk.ac.leeds.ccg.agdt.grids.core.grid.stats.Grids_GridBinaryStatsNotUpdated;
import uk.ac.leeds.ccg.agdt.grids.core.grid.stats.Grids_GridIntStatsNotUpdated;
import uk.ac.leeds.ccg.agdt.grids.io.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.agdt.grids.io.Grids_ESRIAsciiGridImporter.Grids_ESRIAsciiGridHeader;
import uk.ac.leeds.ccg.agdt.grids.process.Grids_Processor;
import uk.ac.leeds.ccg.agdt.grids.utilities.Grids_Utilities;

/**
 * A class for representing a binary grid.
 *
 * @see Grids_AbstractGrid
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridBinary extends Grids_AbstractGrid {

    private static final long serialVersionUID = 1L;

    public static double DefaultValue = 1.0d;

    protected Grids_GridBinary() {
    }

    /**
     * Creates a new Grids_GridBinary.
     *
     * @param dir What {@link #dir} is set to.
     * @param gridFile The directory containing a file named "thisFile" that
     * {@code ois} was constructed from.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param ge
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridBinary(Generic_Path dir, Generic_Path gridFile,
            ObjectInputStream ois, Grids_Environment ge) throws IOException,
            ClassNotFoundException {
        super(ge, dir);
        init(gridFile, ois);
    }

    /**
     * Creates a new Grids_GridBinary with each cell value equal to {@code ndv}
     * and all chunks of the same type.
     *
     * @param stats The Grids_GridBinaryStats to accompany this.
     * @param dir The directory for this.
     * @param cf The factory preferred for creating chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param ndv The noDataValue. WHY IS THIS IGNORED?
     * @param ge
     * @throws java.io.IOException If encountered.
     */
    protected Grids_GridBinary(Grids_GridBinaryStats stats, Generic_Path dir,
            Grids_GridChunkBinaryFactory cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions,
            double ndv, Grids_Environment ge) throws IOException {
        super(ge, dir);
        init(stats, dir, cf, chunkNRows, chunkNCols, nRows, nCols, dimensions);
    }

    /**
     * Creates a new Grids_GridBinary based on values in grid.
     *
     * @param stats The Grids_AbstractStats to accompany this.
     * @param dir The directory for this.
     * @param g The Grids_AbstractGridNumber from which this is to be
     * constructed.
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
     * @param v
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridBinary(Grids_GridBinaryStats stats, Generic_Path dir,
            Grids_AbstractGrid g, Grids_GridChunkBinaryFactory cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, double v) throws IOException, ClassNotFoundException {
        super(g.env, dir);
        checkDir();
        init(stats, g, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, v);
    }

    /**
     * Creates a new Grids_GridBinary with values obtained from
     * gridFile.Currently gridFile must be a directory of a Grids_GridDouble or
     * Grids_GridInt or an ESRI Asciigrid format file with a filename ending in
     * ".asc" or ".txt".
     *
     * @param stats The Grids_GridBinaryStats to accompany this.
     * @param dir The directory for this.
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
     * @param ge
     * @param v
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridBinary(Grids_GridBinaryStats stats, Generic_Path dir,
            Generic_Path gridFile, Grids_GridChunkBinaryFactory cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, Grids_Environment ge, double v) throws IOException, ClassNotFoundException {
        super(ge, dir);
        init(stats, gridFile, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, v);
    }

    /**
     * Creates a new Grids_GridBinary with values obtained from
     * gridFile.Currently gridFile must be a directory of a Grids_GridDouble or
     * Grids_GridInt or an ESRI Asciigrid format file with a filename ending in
     * ".asc" or ".txt".
     *
     * @param ge
     * @param dir The directory for this.
     * @param gridFile Either a directory, or a formatted File with a specific
     * extension containing the data for this.
     * @param v
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected Grids_GridBinary(Grids_Environment ge, Generic_Path dir,
            Generic_Path gridFile, double v) throws IOException, ClassNotFoundException {
        super(ge, dir);
        init(new Grids_GridBinaryStatsNotUpdated(ge), gridFile, v);
    }

    /**
     * Creates a new Grids_GridDouble with each cell value equal to ndv and all
     * chunks of the same type.
     *
     * @param stats The Grids_GridDoubleStats to accompany this.
     * @param dir The directory for this.
     * @param cf The factory preferred for creating chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dimensions The cellsize, xmin, ymin, xmax and ymax.
     * @param ge
     * @throws java.io.IOException If encountered.
     */
    protected Grids_GridBinary(Grids_GridBinaryStats stats, Generic_Path dir,
            Grids_GridChunkBinaryFactory cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions,
            Grids_Environment ge) throws IOException {
        super(ge, dir);
        init(stats, dir, cf, chunkNRows, chunkNCols, nRows, nCols, dimensions);
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridBinary from which the fields of this are set. with
     * those of g.
     * @throws java.io.IOException If encountered.
     */
    private void init(Grids_GridBinary g) throws IOException {
//        Grids_GridBinaryStats gStats;
//        gStats = g.getStats();
//        if (gStats instanceof Grids_GridBinaryStatsNotUpdated) {
//            stats = new Grids_GridBinaryStatsNotUpdated(this);
//        } else {
//            stats = new Grids_GridStatisticsNotUpdatedAsDataChanged(this);
//        }
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
            ((Grids_GridBinaryStatsNotUpdated) stats).setUpToDate(false);
        }
        stats.grid = this;
    }

    /**
     *
     * @param file
     * @param ois
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    private void init(Generic_Path file, ObjectInputStream ois) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        Path thisFile = getPathThisFile(file);
        try {
            init((Grids_GridBinary) ois.readObject());
            ois.close();
            // Set the reference to this in the grid Chunks
            Iterator<Grids_AbstractGridChunk> chunkIterator;
            chunkIterator = chunkIDChunkMap.values().iterator();
            while (chunkIterator.hasNext()) {
                Grids_AbstractGridChunk chunk = chunkIterator.next();
                chunk.setGrid(this);
            }
        } catch (ClassCastException e) {
            try {
                ois.close();
                ois = Generic_IO.getObjectInputStream(thisFile);
                // If the object is a Grids_GridInt
                Grids_Processor gp = env.getProcessor();
                Grids_GridIntFactory gif = new Grids_GridIntFactory(env,
                        gp.GridChunkIntFactory,
                        gp.DefaultGridChunkIntFactory, Integer.MIN_VALUE,
                        ChunkNRows, ChunkNCols, Dimensions,
                        new Grids_GridIntStatsNotUpdated(env));
                Generic_Path gid = env.files.getGeneratedGridIntDir();
                Grids_GridInt gi = (Grids_GridInt) gif.create(gid, file, ois);
                Grids_GridBinaryFactory gbf = new Grids_GridBinaryFactory(env,
                        gp.GridChunkBinaryFactory, gi.ChunkNRows, gi.ChunkNCols,
                        gi.Dimensions, new Grids_GridBinaryStatsNotUpdated(env),
                        DefaultValue);
                Grids_GridBinary gd = (Grids_GridBinary) gbf.create(this.dir, gi);
                init(gd);
                // Delete gi
                Files.delete(gi.dir);
            } catch (IOException ioe) {
                //ioe.printStackTrace();
                System.err.println(ioe.getLocalizedMessage());
            }
        } catch (ClassNotFoundException | IOException e) {
            //ioe.printStackTrace();
            System.err.println(e.getLocalizedMessage());
        }
        //ioe.printStackTrace();
        // Set the reference to this in the grid stats
        if (getStats() == null) {
            stats = new Grids_GridBinaryStatsNotUpdated(env);
        }
        stats.setGrid(this);
        init();
    }

    /**
     *
     * @param stats
     * @param directory
     * @param chunkFactory
     * @param chunkNRows
     * @param chunkNCols
     * @param nRows
     * @param nCols
     * @param dimensions
     * @throws java.io.IOException If encountered.
     */
    private void init(Grids_GridBinaryStats stats, Generic_Path directory,
            Grids_GridChunkBinaryFactory chunkFactory, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dimensions)
            throws IOException {
        env.checkAndMaybeFreeMemory();
        dir = directory;
        this.stats = stats;
        this.stats.setGrid(this);
        ChunkNRows = chunkNRows;
        ChunkNCols = chunkNCols;
        NRows = nRows;
        NCols = nCols;
        Dimensions = dimensions;
        Name = directory.getFileName().toString();
        initNChunkRows();
        initNChunkCols();
        chunkIDChunkMap = new TreeMap<>();
        ChunkIDsOfChunksWorthCaching = new HashSet<>();
        Grids_2D_ID_int chunkID;
        Grids_GridChunkBinary chunk;
        for (int r = 0; r < NChunkRows; r++) {
            for (int c = 0; c < NChunkCols; c++) {
                env.checkAndMaybeFreeMemory();
                // Try to load chunk.
                chunkID = new Grids_2D_ID_int(r, c);
                chunk = chunkFactory.create(this, chunkID);
                chunkIDChunkMap.put(chunkID, chunk);
            }
            System.out.println("Done chunkRow " + r + " out of "
                    + NChunkRows);
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    private void init(Grids_GridBinaryStats stats, Grids_AbstractGrid g,
            Grids_GridChunkBinaryFactory cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, double v) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        ChunkNRows = chunkNRows;
        ChunkNCols = chunkNCols;
        NRows = endRow - startRow + 1L;
        NCols = endCol - startCol + 1L;
        Name = dir.getFileName().toString();
        initNChunkRows();
        initNChunkCols();
        chunkIDChunkMap = new TreeMap<>();
        ChunkIDsOfChunksWorthCaching = new HashSet<>();
        initDimensions(g, startRow, startCol);
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
        Grids_GridChunkBinary chunk;
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
        if (g instanceof Grids_GridBinary) {
            Grids_GridBinary gd = (Grids_GridBinary) g;
            Grids_GridChunkBinary c;
            boolean gValue;
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
                                                //ge.addToNotToCache(this, chunkID);
                                                if (!chunkIDChunkMap.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    chunkIDChunkMap.put(chunkID, chunk);
                                                } else {
                                                    chunk = (Grids_GridChunkBinary) chunkIDChunkMap.get(chunkID);
                                                }
                                                gValue = gd.getCell(c, cellRow, cellCol);
                                                if (gValue) {
                                                    initCell(chunk, chunkID, row, col, gValue);
                                                }
                                                //ge.removeFromNotToCache(this, chunkID);
                                            }
                                        }
                                    }
                                }
                            }
                            isLoadedChunk = true;
                            env.removeFromNotToCache(g, gChunkID);
                        } catch (OutOfMemoryError e) {
                            if (env.HOOME) {
                                env.clearMemoryReserve();
                                freeSomeMemoryAndResetReserve(e);
                                chunkID = new Grids_2D_ID_int(gcr, gcc);
                                if (env.cacheChunksExcept_Account(this, chunkID, false) < 1L) { // Should also not cache out the chunk of grid thats values are being used to initialise this.
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
//            Grids_GridInt gi = (Grids_GridInt) g;
//            Grids_AbstractGridChunkInt c;
//            int gndv = gi.getNoDataValue();
//            int gValue;
//            for (gcr = startChunkRow; gcr <= endChunkRow; gcr++) {
//                gChunkNRows = g.getChunkNRows(gcr);
//                for (gcc = startChunkCol; gcc <= endChunkCol; gcc++) {
//                    do {
//                        try {
//                            // Try to load chunk.
//                            gChunkID = new Grids_2D_ID_int(gcr, gcc);
//                            env.addToNotToCache(g, gChunkID);
//                            env.checkAndMaybeFreeMemory();
//                            c = gi.getChunk(gChunkID);
//                            gChunkNCols = g.getChunkNCols(gcc);
//                            for (cellRow = 0; cellRow < gChunkNRows; cellRow++) {
//                                gRow = g.getRow(gcr, cellRow);
//                                row = gRow - startRow;
//                                chunkRow = getChunkRow(row);
//                                if (gRow >= startRow && gRow <= endRow) {
//                                    for (cellCol = 0; cellCol < gChunkNCols; cellCol++) {
//                                        gCol = g.getCol(gcc, cellCol);
//                                        col = gCol - startCol;
//                                        chunkCol = getChunkCol(col);
//                                        if (gCol >= startCol && gCol <= endCol) {
//                                            /**
//                                             * Initialise chunk if it does not
//                                             * exist This is here rather than
//                                             * where chunkID is initialised as
//                                             * there may not be a chunk for the
//                                             * chunkID.
//                                             */
//                                            if (isInGrid(row, col)) {
//                                                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
//                                                env.addToNotToCache(this, chunkID);
//                                                if (!chunkIDChunkMap.containsKey(chunkID)) {
//                                                    chunk = cf.create(this, chunkID);
//                                                    chunkIDChunkMap.put(chunkID, chunk);
//                                                    if (!(chunk instanceof Grids_GridChunkDouble)) {
//                                                        ChunkIDsOfChunksWorthCaching.add(chunkID);
//                                                    }
//                                                } else {
//                                                    chunk = (Grids_GridChunkBinary) chunkIDChunkMap.get(chunkID);
//                                                }
//                                                gValue = gi.getCell(c, cellRow, cellCol);
//                                                // Initialise value
//                                                if (gValue == gndv) {
//                                                    initCell(chunk, chunkID, row, col, ndv);
//                                                } else {
//                                                    if (!Double.isNaN(gValue) && Double.isFinite(gValue)) {
//                                                        initCell(chunk, chunkID, row, col, gValue);
//                                                    } else {
//                                                        initCell(chunk, chunkID, row, col, ndv);
//                                                    }
//                                                }
//                                                env.removeFromNotToCache(this, chunkID);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            isLoadedChunk = true;
//                            env.removeFromNotToCache(g, gChunkID);
//                            env.checkAndMaybeFreeMemory();
//                        } catch (OutOfMemoryError e) {
//                            if (env.HOOME) {
//                                env.clearMemoryReserve();
//                                chunkID = new Grids_2D_ID_int(gcr, gcc);
//                                if (env.cacheChunksExcept_Account(this, chunkID, false) < 1L) {
//                                    /**
//                                     * TODO: Should also not cache out the chunk
//                                     * of grid thats values are being used to
//                                     * initialise this.
//                                     */
//                                    throw e;
//                                }
//                                env.initMemoryReserve(this, chunkID, env.HOOME);
//                            } else {
//                                throw e;
//                            }
//                        }
//                    } while (!isLoadedChunk);
//                    isLoadedChunk = false;
//                }
//                System.out.println("Done chunkRow " + gcr + " out of " + nChunkRows);
//            }
        }
        init();
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
     * @param v
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    private void init(Grids_GridBinaryStats stats, Generic_Path gridFile,
            Grids_GridChunkBinaryFactory cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, double v) throws IOException, ClassNotFoundException {
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
                Grids_GridBinaryFactory gf = new Grids_GridBinaryFactory(env,
                        cf, chunkNRows, chunkNCols, null, stats, DefaultValue);
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                ObjectInputStream ois = Generic_IO.getObjectInputStream(thisFile);
                Grids_GridBinary g = gf.create(dir, thisFile, ois);
                Grids_GridBinary g2 = gf.create(dir, g, startRow, startCol,
                        endRow, endCol);
                init(g2);
            }
        } else {
            // Assume ESRI AsciiFile
            ChunkNRows = chunkNRows;
            ChunkNCols = chunkNCols;
            NRows = endRow - startRow;
            NCols = endCol - startCol;
            Name = dir.getFileName().toString();
            initNChunkRows();
            initNChunkCols();
            chunkIDChunkMap = new TreeMap<>();
            ChunkIDsOfChunksWorthCaching = new HashSet<>();
            this.stats = stats;
            this.stats.grid = this;
            String filename = gridFile.getFileName().toString();
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Grids_ESRIAsciiGridHeader header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                initDimensions(header, startRow, startCol);
                double gridFileNoDataValue = header.ndv.doubleValue();
                long row;
                long col;
//                Grids_GridChunkBinary chunk;
//                Grids_GridChunkDouble gridChunk;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                for (row = (NRows - 1); row > -1; row--) {
                    env.checkAndMaybeFreeMemory();
                    env.initNotToCache();
                    for (col = 0; col < NCols; col++) {
                        double value = eagi.readDouble();
                        if (value != gridFileNoDataValue) {
                            if (value == v) {
                                initCell(row, col, true, true);
                            }
                        }
                    }
                    if (row % reportN == 0) {
                        System.out.println("Done row " + row);
                    }
                    env.checkAndMaybeFreeMemory();
                }
            }
        }
        init();
    }

    private void init(Grids_GridBinaryStats stats, Generic_Path gridFile,
            double v) throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        this.stats = stats;
        this.stats.setGrid(this);
        // Set to report every 10%
        int reportN;
        Grids_Processor gp = env.getProcessor();
        if (Files.isDirectory(gridFile)) {
            if (true) {
                Grids_GridBinaryFactory gf = new Grids_GridBinaryFactory(env,
                        gp.GridChunkBinaryFactory,
                        gp.GridBinaryFactory.ChunkNRows,
                        gp.GridBinaryFactory.ChunkNCols, null, stats,
                        DefaultValue);
                Generic_Path thisFile = new Generic_Path(getPathThisFile(gridFile));
                ObjectInputStream ois = Generic_IO.getObjectInputStream(thisFile);
                Grids_GridBinary g = gf.create(dir, thisFile, ois);
                init(g);
                //this.chunkIDChunkMap = g.chunkIDChunkMap;
                this.ChunkIDsOfChunksWorthCaching = g.ChunkIDsOfChunksWorthCaching;
                this.Dimensions = g.Dimensions;
                this.dir = g.dir;
                this.stats = stats;
                this.stats.grid = this;
            }
        } else {
            // Assume ESRI AsciiFile
            checkDir();
            Name = dir.getFileName().toString();
            chunkIDChunkMap = new TreeMap<>();
            ChunkIDsOfChunksWorthCaching = new HashSet<>();
            this.stats = stats;
            this.stats.setGrid(this);
            String filename = gridFile.getFileName().toString();
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(env, gridFile);
                Grids_ESRIAsciiGridHeader header = eagi.getHeader();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                NCols = header.ncols;
                NRows = header.nrows;
                ChunkNRows = gp.GridDoubleFactory.ChunkNRows;
                ChunkNCols = gp.GridDoubleFactory.ChunkNCols;
                initNChunkRows();
                initNChunkCols();
                initDimensions(header, 0, 0);
                reportN = (int) (NRows - 1) / 10;
                if (reportN == 0) {
                    reportN = 1;
                }
                double gridFileNoDataValue = header.ndv.doubleValue();
                long row;
                long col;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                for (row = (NRows - 1); row > -1; row--) {
                    env.checkAndMaybeFreeMemory();
                    env.initNotToCache();
                    for (col = 0; col < NCols; col++) {
                        double value = eagi.readDouble();
                        if (value != gridFileNoDataValue) {
                            if (value == v) {
                                initCell(row, col, true, true);
                            } else {
                                initCell(row, col, false, true);
                            }
                            /**
                             * chunks do not get initialised at all if they are
                             * completely made up of gridFileNoDataValue.
                             */
//                        } else {
//                            initCell(row, col, false, true);

//                        }
                        }
                    }
                    if (row % reportN == 0) {
                        System.out.println("Done row " + row);
                    }
                    env.checkAndMaybeFreeMemory();
                }
            }
        }
        init();
    }

    /**
     *
     * @param row
     * @param col
     * @param value
     * @param fast
     */
    private void initCell(long row, long col, boolean value, boolean fast)
            throws IOException, ClassNotFoundException {
        Grids_GridChunkBinary chunk;
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
        /**
         * Ensure this chunkID is not cacheped and initialise it if it does not
         * already exist.
         */
        env.addToNotToCache(this, chunkID);
        if (!chunkIDChunkMap.containsKey(chunkID)) {
            Grids_GridChunkBinary gc = new Grids_GridChunkBinary(this, chunkID);
            chunkIDChunkMap.put(chunkID, gc);
        } else {
            Grids_AbstractGridChunk c = chunkIDChunkMap.get(chunkID);
            if (c == null) {
                loadIntoCacheChunk(chunkID);
            }
            chunk = (Grids_GridChunkBinary) chunkIDChunkMap.get(chunkID);
            if (fast) {
                initCellFast(chunk, row, col, value);
            } else {
                initCell(chunk, chunkID, row, col, value);
            }
        }
    }

    /**
     * @return Grids_GridChunkBinary for the given chunkID.
     * @param chunkID
     * @throws java.io.IOException
     */
    @Override
    public Grids_GridChunkBinary getChunk(Grids_2D_ID_int chunkID)
            throws IOException, ClassNotFoundException {
        if (isInGrid(chunkID)) {
            if (chunkIDChunkMap.get(chunkID) == null) {
                loadIntoCacheChunk(chunkID);
            }
            return (Grids_GridChunkBinary) chunkIDChunkMap.get(chunkID);
        }
        return null;
    }

    /**
     * @return Grids_GridChunkBinary for the given chunkID.
     * @param chunkID
     */
    @Override
    public Grids_GridChunkBinary getChunk(Grids_2D_ID_int chunkID, int chunkRow,
            int chunkCol) throws IOException, ClassNotFoundException {
        if (isInGrid(chunkRow, chunkCol)) {
            if (chunkIDChunkMap.get(chunkID) == null) {
                loadIntoCacheChunk(chunkID);
            }
            return (Grids_GridChunkBinary) chunkIDChunkMap.get(chunkID);
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
     */
    protected void upDateGridStatistics(boolean newValue, boolean oldValue)
            throws IOException, ClassNotFoundException {
        if (stats.getClass() == Grids_GridBinaryStats.class) {
            if (newValue == false) {
                if (oldValue == false) {
                    stats.setN(stats.getN() - 1L);
                }
                stats.setN(stats.getN() + 1);
            }
        } else {
            if (newValue != oldValue) {
                ((Grids_GridBinaryStatsNotUpdated) stats).setUpToDate(false);
            }
        }
    }

    /**
     * For getting the value at row, col.
     *
     * @param row
     * @param col
     * @return
     */
    public boolean getCell(long row, long col) throws IOException, ClassNotFoundException {
//        boolean isInGrid = isInGrid(row, col);
//        if (isInGrid) {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        Grids_GridChunkBinary c = (Grids_GridChunkBinary) getChunk(chunkRow, chunkCol);
        int cellRow = getCellRow(row);
        int cellCol = getCellCol(col);
        return getCell(c, cellRow, cellCol);
//        }
//        return false;
    }

    /**
     * For getting the value in chunk at cellRow, cellCol.
     *
     * @param chunk
     * @param cellRow The chunk cell row index of the cell thats value is
     * returned.
     * @param cellCol The chunk cell column index of the cell thats value is
     * returned.
     * @return Value at position given by chunk row index _ChunkRowIndex, chunk
     * column index _ChunkColIndex, chunk cell row index cellRow, chunk cell
     * column index cellCol.
     *
     */
    public boolean getCell(Grids_GridChunkBinary chunk, int cellRow, int cellCol) {
        return chunk.getCell(cellRow, cellCol);
    }

    /**
     * For getting the value at x-coordinate x, y-coordinate y.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @return
     */
    public final boolean getCell(double x, double y) throws IOException,
            ClassNotFoundException {
        long row = getRow(y);
        long col = getCol(x);
        boolean isInGrid = isInGrid(row, col);
        if (isInGrid) {
            return getCell(row, col);
        }
        return false;
    }

    /**
     * For returning the value of the cell with cellID.
     *
     * @param cellID the Grids_2D_ID_long of the cell.
     * @return
     */
    public final boolean getCell(Grids_2D_ID_long cellID) throws IOException,
            ClassNotFoundException {
        return getCell(cellID.getRow(), cellID.getCol());
    }

    /**
     * For setting the value at x-coordinate x, y-coordinate y.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param value
     */
    public final void setCell(Double x, Double y, boolean value) throws IOException, ClassNotFoundException {
        setCell(getRow(x), getCol(y), value);
    }

    /**
     * For setting the value at row, col.
     *
     * @param row
     * @param col
     * @param value
     */
    public void setCell(Long row, Long col, boolean value) throws IOException,
            ClassNotFoundException {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        int cellRow = getCellRow(row);
        int cellCol = getCellCol(col);
        Grids_GridChunkBinary chunk;
        chunk = (Grids_GridChunkBinary) getChunk(chunkRow, chunkCol);
        setCell(chunk, cellRow, cellCol, value);
    }

    /**
     * For setting the value at chunkRow, chunkCol, cellRow, cellCol.
     *
     * @param chunkRow
     * @param chunkCol
     * @param cellRow
     * @param cellCol
     * @param value
     */
    public void setCell(int chunkRow, int chunkCol, int cellRow, int cellCol,
            boolean value) throws IOException, ClassNotFoundException {
        Grids_GridChunkBinary chunk;
        chunk = (Grids_GridChunkBinary) getChunk(chunkRow, chunkCol);
        setCell(chunk, cellRow, cellCol, value);
    }

    /**
     * @param chunk
     * @param cellCol
     * @param cellRow
     * @param value
     */
    public void setCell(Grids_GridChunkBinary chunk, int cellRow, int cellCol, 
            boolean value) throws IOException, ClassNotFoundException {
        boolean v = chunk.setCell(cellRow, cellCol, value);
        // Update stats
        upDateGridStatistics(value, v);
    }

    /**
     * Initialises the value in chunk at row, col.
     *
     * @param chunk
     * @param chunkID
     * @param row
     * @param col
     * @param value
     */
    protected void initCell(Grids_GridChunkBinary chunk,
            Grids_2D_ID_int chunkID, long row, long col, boolean value) {
        chunk.initCell(getCellRow(row), getCellCol(col), value);
    }

    protected void updateStatistics(boolean value) throws IOException,
            ClassNotFoundException {
        if (value) {
            stats.setN(stats.getN() + 1);
        }
    }

    /**
     * Initialises the value at _CellRowIndex, _CellColIndex and does nothing
     * about stats
     *
     * @param chunk
     * @param row
     * @param col
     * @param value
     */
    protected void initCellFast(Grids_GridChunkBinary chunk, long row, long col,
            boolean value) {
        chunk.initCell(getCellRow(row), getCellCol(col), value);
    }

    /**
     * @return double[] of all cell values for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    protected boolean[] getCells(double x, double y, double distance) throws IOException, ClassNotFoundException {
        return getCells(x, y, getRow(y), getCol(x), distance);
    }

    /**
     * @return double[] of all cell values for cells thats centroids are
     * intersected by circle with centre at centroid of cell given by cell row
     * index row, cell column index col, and radius distance.
     * @param row the row index for the cell that'stats centroid is the circle
     * centre from which cell values are returned.
     * @param col the column index for the cell that'stats centroid is the
     * circle centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    public boolean[] getCells(long row, long col, double distance) throws IOException, ClassNotFoundException {
        return getCells(getCellXDouble(col), getCellYDouble(row), row, col,
                distance);
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
     */
    protected boolean[] getCells(double x, double y, long row, long col,
            double distance) throws IOException, ClassNotFoundException {
        boolean[] cells;
        int cellDistance = (int) Math.ceil(distance / getCellsizeDouble());
        cells = new boolean[((2 * cellDistance) + 1) * ((2 * cellDistance) + 1)];
        int count = 0;
        for (long p = row - cellDistance; p <= row + cellDistance; p++) {
            double thisY = getCellYDouble(row);
            for (long q = col - cellDistance; q <= col + cellDistance; q++) {
                double thisX = getCellXDouble(col);
                if (Grids_Utilities.distance(x, y, thisX, thisY) <= distance) {
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
     *
     * @param value
     */
    public void initCells(boolean value) throws IOException {
        Iterator<Grids_2D_ID_int> ite = chunkIDChunkMap.keySet().iterator();
        int nChunks = chunkIDChunkMap.size();
        int counter = 0;
        while (ite.hasNext()) {
            env.checkAndMaybeFreeMemory();
            System.out.println("Initialising Chunk " + counter + " out of " + nChunks);
            counter++;
            Grids_2D_ID_int chunkID = ite.next();
            Grids_GridChunkBinary chunk = (Grids_GridChunkBinary) chunkIDChunkMap.get(chunkID);
            int chunkNRows = getChunkNRows(chunkID);
            int chunkNCols = getChunkNCols(chunkID);
            for (int row = 0; row <= chunkNRows; row++) {
                for (int col = 0; col <= chunkNCols; col++) {
                    chunk.initCell(row, col, value);
                }
            }
        }
    }

    /**
     * @return A Grids_GridBinaryIterator for iterating over the cell values in
     * this.
     */
    public Grids_GridBinaryIterator iterator() throws IOException, ClassNotFoundException {
        return new Grids_GridBinaryIterator(this);
    }

    @Override
    public Grids_GridBinaryStats getStats() {
        return (Grids_GridBinaryStats) stats;
    }

}
