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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkIntFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridDoubleStatsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridIntStats;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_GridIntStatsNotUpdated;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridImporter.Grids_ESRIAsciiGridHeader;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_Files;
import uk.ac.leeds.ccg.andyt.grids.process.Grids_Processor;
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
     * A reference to the grid Stats.
     */
    protected Grids_GridIntStats Stats;

    protected Grids_GridInt() {
    }

    /**
     * Creates a new Grids_GridInt.
     *
     * @param dir The directory for this.
     * @param gridFile The directory containing the file named "thisFile" that
     * the ois was constructed from.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param ge
     */
    protected Grids_GridInt(File dir, File gridFile, ObjectInputStream ois,
            Grids_Environment ge) {
        this.env = ge;
        this.Directory = dir;
        init(gridFile, ois);
    }

    /**
     * Creates a new Grids_GridInt with each cell value equal to NoDataValue and
     * all chunks of the same type.
     *
     * @param stats The Grids_GridIntStats to accompany this.
     * @param dir The File _Directory to be used for swapping.
     * @param cf The factory preferred for creating chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dims The grid dimensions (cellsize, xmin, ymin, xmax and ymax).
     * @param ndv The NoDataValue.
     * @param ge
     */
    protected Grids_GridInt(Grids_GridIntStats stats, File dir,
            Grids_AbstractGridChunkIntFactory cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dims,
            int ndv, Grids_Environment ge) {
        super(ge, dir);
        checkDir();
        init(stats, dir, cf, chunkNRows, chunkNCols, nRows, nCols, dims, ndv);
    }

    /**
     * Creates a new Grids_GridInt based on values in grid.
     *
     * @param stats The Grids_GridIntStats to accompany this.
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
     * @param ndv The NoDataValue for this.
     */
    protected Grids_GridInt(Grids_GridIntStats stats, File dir,
            Grids_AbstractGridNumber g, Grids_AbstractGridChunkIntFactory cf,
            int chunkNRows, int chunkNCols, long startRow, long startCol,
            long endRow, long endCol, int ndv) {
        super(g.env, dir);
        checkDir();
        init(stats, g, cf, chunkNRows, chunkNCols, startRow, startCol,
                endRow, endCol, ndv);
    }

    /**
     * Creates a new Grids_GridInt with values obtained from gridFile. Currently
     * gridFile must be a directory of a Grids_GridDouble or Grids_GridInt or a
     * ESRI Asciigrid format file with a filename ending ".asc" or ".txt".
     *
     * @param stats The Grids_GridIntStats to accompany this.
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
     * @param noDataValue The NoDataValue for this.
     * @param ge
     */
    protected Grids_GridInt(Grids_GridIntStats stats, File dir, File gridFile,
            Grids_AbstractGridChunkIntFactory cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, int noDataValue, Grids_Environment ge) {
        super(ge, dir);
        checkDir();
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
    protected Grids_GridInt(Grids_Environment ge, File dir, File gridFile) {
        super(ge, dir);
        init(new Grids_GridIntStatsNotUpdated(ge), gridFile);
    }

    /**
     * @return a string description of the instance. Basically the values of
     * each field.
     */
    @Override
    public String toString() {
        return getClass().getName() + "(NoDataValue(" + NoDataValue + "), "
                + super.toString() + ")";
    }

    /**
     * Initialises this.
     *
     * @param g The Grids_GridInt from which the fields of this are set.
     */
    private void init(Grids_GridInt g) {
        NoDataValue = g.NoDataValue;
        Stats = g.Stats;
        super.init(g);
        ChunkIDChunkMap = g.ChunkIDChunkMap;
        // Set the reference to this in ChunkIDChunkMap chunks
        setReferenceInChunkIDChunkMap();
        ChunkIDsOfChunksWorthSwapping = g.ChunkIDsOfChunksWorthSwapping;
        // Set the reference to this in the Grid Stats
        Stats.init(this);
        super.init();
        //Stats.Grid = this;
    }

    @Override
    protected void init() {
        super.init();
        if (!Stats.isUpdated()) {
            ((Grids_GridIntStatsNotUpdated) Stats).setUpToDate(false);
        }
        Stats.Grid = this;
    }

    /**
     * Initialises this.
     *
     * @param file The File the ois was constructed from.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    private void init(File file, ObjectInputStream ois) {
        env.checkAndMaybeFreeMemory();
        File thisFile = new File(file, "thisFile");
        try {
            init((Grids_GridInt) ois.readObject());
            ois.close();
            // Set the reference to this in the Grid Chunks
            Iterator<Grids_AbstractGridChunk> chunkIterator;
            chunkIterator = ChunkIDChunkMap.values().iterator();
            while (chunkIterator.hasNext()) {
                Grids_AbstractGridChunk chunk = chunkIterator.next();
                chunk.setGrid(this);
            }
        } catch (ClassCastException e) {
            try {
                ois.close();
                ois = env.env.io.getObjectInputStream(thisFile);
                checkDir();
                // If the object is a Grids_GridDouble
                Grids_Processor gp;
                gp = env.getProcessor();
                Grids_GridDoubleFactory gdf;
                gdf = new Grids_GridDoubleFactory(env, gp.GridChunkDoubleFactory,
                        gp.DefaultGridChunkDoubleFactory, -Double.MAX_VALUE,
                        ChunkNRows, ChunkNCols, null,
                        new Grids_GridDoubleStatsNotUpdated(env));
                Grids_Files files = env.getFiles();
                File dir;
                dir = files.createNewFile(files.getGeneratedGridDoubleDir());
                Grids_GridDouble gd;
                gd = (Grids_GridDouble) gdf.create(dir, file, ois);
                Grids_GridIntFactory gif;
                gif = new Grids_GridIntFactory(env, gp.GridChunkIntFactory,
                        gp.DefaultGridChunkIntFactory, Integer.MIN_VALUE,
                        gd.ChunkNRows, gd.ChunkNCols, null,
                        new Grids_GridIntStatsNotUpdated(env));
                Grids_GridInt gi;
                gi = (Grids_GridInt) gif.create(Directory, gd);
                init(gi);
                // delete gd
                gd.Directory.delete();
            } catch (IOException ioe) {
                //ioe.printStackTrace();
                System.err.println(ioe.getLocalizedMessage());
            }
        } catch (ClassNotFoundException | IOException e) {
            //ioe.printStackTrace();
            System.err.println(e.getLocalizedMessage());
        }
        //ioe.printStackTrace();
        // Set the reference to this in the Grid Stats
        if (getStats() == null) {
            Stats = new Grids_GridIntStatsNotUpdated(env);
        }
        Stats.init(this);
        init();
    }

    /**
     * Initialises this.
     *
     * @param stats The AbstractGridStatistics to accompany this.
     * @param dir The directory for this.
     * @param cf The Grids_AbstractGridChunkIntFactory preferred for creating
     * chunks.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param nRows The number of rows of cells.
     * @param nCols The number of columns of cells.
     * @param dim The cellsize, xmin, ymin, xmax and ymax.
     * @param ndv The NoDataValue.
     */
    private void init(Grids_GridIntStats stats, File dir,
            Grids_AbstractGridChunkIntFactory cf, int chunkNRows,
            int chunkNCols, long nRows, long nCols, Grids_Dimensions dim,
            int ndv) {
        env.checkAndMaybeFreeMemory();
        Stats = stats;
        Stats.init(this);
        Directory = dir;
        ChunkNRows = chunkNRows;
        ChunkNCols = chunkNCols;
        NRows = nRows;
        NCols = nCols;
        Dimensions = dim;
        initNoDataValue(ndv);
        Name = dir.getName();
        initNChunkRows();
        initNChunkCols();
        ChunkIDChunkMap = new TreeMap<>();
        ChunkIDsOfChunksWorthSwapping = new HashSet<>();
        int r;
        int c;
        Grids_2D_ID_int chunkID;
        Grids_AbstractGridChunkInt chunk;
        for (r = 0; r < NChunkRows; r++) {
            for (c = 0; c < NChunkCols; c++) {
                env.checkAndMaybeFreeMemory();
                // Try to load chunk.
                chunkID = new Grids_2D_ID_int(r, c);
                chunk = cf.create(this, chunkID);
                ChunkIDChunkMap.put(chunkID, chunk);
                if (!(chunk instanceof Grids_GridChunkInt)) {
                    ChunkIDsOfChunksWorthSwapping.add(chunkID);
                }
            }
            System.out.println("Done chunkRow " + r + " out of "
                    + NChunkRows);
        }
        init();
    }

    /**
     * Initialises this.
     *
     * @param stats The AbstractGridStatistics to accompany this.
     * @param dir The directory for this.
     * @param g The Grids_AbstractGridNumber from which this is to be
     * constructed.
     * @param cf The factory preferred to construct chunks of this.
     * @param chunkNRows The number of rows of cells in any chunk.
     * @param chunkNCols The number of columns of cells in any chunk.
     * @param startRow The row of g which is the bottom most row of this.
     * @param startCol The column of g which is the left most column of this.
     * @param endRow The row of g which is the top most row of this.
     * @param endCol The column of g which is the right most column of this.
     * @param ndv The NoDataValue for this.
     */
    private void init(Grids_GridIntStats stats, Grids_AbstractGridNumber g,
            Grids_AbstractGridChunkIntFactory cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, int ndv) {
        env.checkAndMaybeFreeMemory();
        Stats = stats;
        Stats.init(this);
        ChunkNRows = chunkNRows;
        ChunkNCols = chunkNCols;
        NRows = endRow - startRow + 1L;
        NCols = endCol - startCol + 1L;
        NoDataValue = ndv;
        Name = Directory.getName();
        initNChunkRows();
        initNChunkCols();
        ChunkIDChunkMap = new TreeMap<>();
        ChunkIDsOfChunksWorthSwapping = new HashSet<>();
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
        Grids_AbstractGridChunkInt chunk;
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
            Grids_AbstractGridChunkDouble c;
            double gndv = gd.getNoDataValue();
            double gValue;
            for (gcr = startChunkRow; gcr <= endChunkRow; gcr++) {
                gChunkNRows = g.getChunkNRows(gcr);
                for (gcc = startChunkCol; gcc <= endChunkCol; gcc++) {
                    do {
                        try {
                            // Try to load chunk.
                            gChunkID = new Grids_2D_ID_int(gcr, gcc);
                            env.addToNotToSwap(g, gChunkID);
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
                                                env.addToNotToSwap(this, chunkID);
                                                if (!ChunkIDChunkMap.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    ChunkIDChunkMap.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_GridChunkInt)) {
                                                        ChunkIDsOfChunksWorthSwapping.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_AbstractGridChunkInt) ChunkIDChunkMap.get(chunkID);
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
                                                //ge.removeFromNotToSwap(this, chunkID);
                                            }
                                        }
                                    }
                                }
                            }
                            isLoadedChunk = true;
                            env.removeFromNotToSwap(g, gChunkID);
                            env.checkAndMaybeFreeMemory();
                        } catch (OutOfMemoryError e) {
                            if (env.HOOME) {
                                env.clearMemoryReserve();
                                freeSomeMemoryAndResetReserve(e);
                                chunkID = new Grids_2D_ID_int(gcr, gcc);
                                if (env.swapChunksExcept_Account(this, chunkID, false) < 1L) {
                                    /**
                                     * TODO: Should also not swap out the chunk
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
                    //loadedChunkCount++;
                    //cci1 = _ChunkColIndex;
                }
                System.out.println("Done chunkRow " + gcr + " out of " + nChunkRows);
            }
        } else {
            Grids_GridInt gi = (Grids_GridInt) g;
            Grids_AbstractGridChunkInt c;
            int gndv = gi.getNoDataValue();
            int gValue;
            for (gcr = startChunkRow; gcr <= endChunkRow; gcr++) {
                gChunkNRows = g.getChunkNRows(gcr);
                for (gcc = startChunkCol; gcc <= endChunkCol; gcc++) {
                    do {
                        try {
                            // Try to load chunk.
                            gChunkID = new Grids_2D_ID_int(gcr, gcc);
                            env.addToNotToSwap(g, gChunkID);
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
                                                env.addToNotToSwap(this, chunkID);
                                                if (!ChunkIDChunkMap.containsKey(chunkID)) {
                                                    chunk = cf.create(this, chunkID);
                                                    ChunkIDChunkMap.put(chunkID, chunk);
                                                    if (!(chunk instanceof Grids_GridChunkInt)) {
                                                        ChunkIDsOfChunksWorthSwapping.add(chunkID);
                                                    }
                                                } else {
                                                    chunk = (Grids_AbstractGridChunkInt) ChunkIDChunkMap.get(chunkID);
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
                                                env.removeFromNotToSwap(this, chunkID);
                                            }
                                        }
                                    }
                                }
                            }
                            isLoadedChunk = true;
                            env.removeFromNotToSwap(g, gChunkID);
                            env.checkAndMaybeFreeMemory();
                        } catch (OutOfMemoryError e) {
                            if (env.HOOME) {
                                env.clearMemoryReserve();
                                chunkID = new Grids_2D_ID_int(gcr, gcc);
                                if (env.swapChunksExcept_Account(this, chunkID, false) < 1L) {
                                    /**
                                     * TODO: Should also not swap out the chunk
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
     * @param dir The File _Directory to be used for swapping.
     * @param gridFile Either a _Directory, or a formatted File with a specific
     * extension containing the data and information about the Grids_GridInt to
     * be returned.
     * @param cf The Grids_AbstractGridChunkIntFactory preferred to construct
     * chunks of this.
     * @param chunkNRows The Grids_GridInt _ChunkNRows.
     * @param chunkNCols The Grids_GridInt _ChunkNCols.
     * @param startRow The topmost row index of the grid stored as gridFile.
     * @param startCol The leftmost column index of the grid stored as gridFile.
     * @param endRow The bottom row index of the grid stored as gridFile.
     * @param endCol The rightmost column index of the grid stored as gridFile.
     * @param ndv The NoDataValue for this.
     */
    private void init(Grids_GridIntStats stats, File gridFile,
            Grids_AbstractGridChunkIntFactory cf, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol, int ndv) {
        env.checkAndMaybeFreeMemory();
        Stats = stats;
        Stats.init(this);
        // Set to report every 10%
        int reportN;
        reportN = (int) (endRow - startRow) / 10;
        if (reportN == 0) {
            reportN = 1;
        }
        if (gridFile.isDirectory()) {
            if (true) {
                Grids_Processor gp;
                gp = env.getProcessor();
                Grids_GridIntFactory gf;
                gf = new Grids_GridIntFactory(env, gp.GridChunkIntFactory, cf,
                        ndv, chunkNRows, chunkNCols, null, stats);
                File thisFile = new File(gridFile, "thisFile");
                ObjectInputStream ois;
                ois = env.env.io.getObjectInputStream(thisFile);
                Grids_GridInt g;
                g = (Grids_GridInt) gf.create(Directory, thisFile, ois);
                Grids_GridInt g2;
                g2 = gf.create(Directory, g, startRow, startCol, endRow, endCol);
                init(g2);
            }
        } else {
            // Assume ESRI AsciiFile
            ChunkNRows = chunkNRows;
            ChunkNCols = chunkNCols;
            NRows = endRow - startRow + 1L;
            NCols = endCol - startCol + 1L;
            initNoDataValue(ndv);
            Name = Directory.getName();
            initNChunkRows();
            initNChunkCols();
            ChunkIDChunkMap = new TreeMap<>();
            ChunkIDsOfChunksWorthSwapping = new HashSet<>();
            Stats = stats;
            Stats.init(this);
            String filename = gridFile.getName();
            int value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(gridFile, env);
                Grids_ESRIAsciiGridHeader header = eagi.readHeaderObject();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                initDimensions(header, startRow, startCol);
                int gridFileNoDataValue = header.NoDataValue.intValueExact();
                long row;
                long col;
//                Grids_AbstractGridChunkInt chunk;
//                Grids_GridChunkInt gridChunk;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if ((int) gridFileNoDataValue == NoDataValue) {
                    if (stats.isUpdated()) {
                        for (row = (NRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToSwap();
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
                            env.initNotToSwap();
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
                            env.initNotToSwap();
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
                            env.initNotToSwap();
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

    private void init(Grids_GridIntStats stats, File gridFile) {
        env.checkAndMaybeFreeMemory();
        Stats = stats;
        Stats.init(this);
        // For reporting
        int reportN;
        Grids_Processor gp;
        gp = env.getProcessor();
        if (gridFile.isDirectory()) {
            if (true) {
                Grids_GridIntFactory gf;
                gf = new Grids_GridIntFactory(env, gp.GridChunkIntFactory,
                        gp.DefaultGridChunkIntFactory,
                        gp.GridIntFactory.NoDataValue,
                        gp.GridIntFactory.ChunkNRows,
                        gp.GridIntFactory.ChunkNCols, null, stats);
                File thisFile = new File(gridFile, "thisFile");
                ObjectInputStream ois;
                ois = env.env.io.getObjectInputStream(thisFile);
                Grids_GridInt g;
                g = (Grids_GridInt) gf.create(Directory, thisFile, ois);
                init(g);
                this.ChunkIDChunkMap = g.ChunkIDChunkMap;
                this.ChunkIDsOfChunksWorthSwapping = g.ChunkIDsOfChunksWorthSwapping;
                this.NoDataValue = g.NoDataValue;
                this.Dimensions = g.Dimensions;
                this.Directory = g.Directory;
                this.Stats = g.Stats;
                this.Stats.Grid = this;
            }
        } else {
            // Assume ESRI AsciiFile
            checkDir();
            Name = Directory.getName();
            ChunkIDChunkMap = new TreeMap<>();
            ChunkIDsOfChunksWorthSwapping = new HashSet<>();
            Stats = stats;
            Stats.init(this);
            String filename = gridFile.getName();
            int value;
            if (filename.endsWith("asc") || filename.endsWith("txt")) {
                Grids_ESRIAsciiGridImporter eagi;
                eagi = new Grids_ESRIAsciiGridImporter(gridFile, env);
                Grids_ESRIAsciiGridHeader header = eagi.readHeaderObject();
                //long inputNcols = ( Long ) header[ 0 ];
                //long inputNrows = ( Long ) header[ 1 ];
                NCols = header.NRows;
                NRows = header.NCols;
                ChunkNRows = gp.GridDoubleFactory.ChunkNRows;
                ChunkNCols = gp.GridDoubleFactory.ChunkNCols;
                initNChunkRows();
                initNChunkCols();
                initDimensions(header, 0, 0);
                // Set to report every 10%
                reportN = (int) (NRows - 1) / 10;
                if (reportN == 0) {
                    reportN = 1;
                }
                double gridFileNoDataValue = header.NoDataValue.doubleValue();
                long row;
                long col;
                // Read Data into Chunks. This starts with the last row and ends with the first.
                if (gridFileNoDataValue == NoDataValue) {
                    if (stats.isUpdated()) {
                        for (row = (NRows - 1); row > -1; row--) {
                            env.checkAndMaybeFreeMemory();
                            env.initNotToSwap();
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
                            env.initNotToSwap();
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
                            env.initNotToSwap();
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
                            env.initNotToSwap();
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

    /**
     * @param chunkRow
     * @param chunkCol
     * @return Grids_AbstractGridChunkInt.
     */
    @Override
    public final Grids_AbstractGridChunkInt getChunk(int chunkRow,
            int chunkCol) {
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
        return getChunk(chunkID);
    }

    /**
     * Attempts to load into the memory cache the chunk with chunk ID chunkID.
     *
     * @param chunkID The chunk ID of the chunk to be restored.
     */
    @Override
    public void loadIntoCacheChunk(Grids_2D_ID_int chunkID) {
        boolean isInCache = isInCache(chunkID);
        if (!isInCache) {
            File f = new File(getDirectory(),
                    "" + chunkID.getRow() + "_" + chunkID.getCol());
            Object o = env.env.io.readObject(f);
            Grids_AbstractGridChunkInt chunk = null;
            if (o.getClass() == Grids_GridChunkIntArray.class) {
                Grids_GridChunkIntArray c;
                c = (Grids_GridChunkIntArray) o;
                chunk = c;
            } else if (o.getClass() == Grids_GridChunkIntMap.class) {
                Grids_GridChunkIntMap c;
                c = (Grids_GridChunkIntMap) o;
                chunk = c;
            } else if (o.getClass() == Grids_GridChunkInt.class) {
                Grids_GridChunkInt c;
                c = (Grids_GridChunkInt) o;
                chunk = c;
            } else {
                throw new Error("Unrecognised type of chunk or null "
                        + this.getClass().getName()
                        + ".loadIntoCacheChunk(ChunkID(" + chunkID.toString()
                        + "))");
            }
            chunk.env = env;
            chunk.initGrid(this);
            chunk.initChunkID(chunkID);
            ChunkIDChunkMap.put(chunkID, chunk);
            if (!(chunk instanceof Grids_GridChunkInt)) {
                ChunkIDsOfChunksWorthSwapping.add(chunkID);
            }
            env.setDataToSwap(true);
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
        env.addToNotToSwap(this, chunkID);
        if (!ChunkIDChunkMap.containsKey(chunkID)) {
            Grids_GridChunkInt gc = new Grids_GridChunkInt(this, chunkID,
                    value);
            ChunkIDChunkMap.put(chunkID, gc);
            if (!(gc instanceof Grids_GridChunkInt)) {
                ChunkIDsOfChunksWorthSwapping.add(chunkID);
            }
        } else {
            Grids_AbstractGridChunk c;
            c = ChunkIDChunkMap.get(chunkID);
            if (c == null) {
                loadIntoCacheChunk(chunkID);
            }
            chunk = (Grids_AbstractGridChunkInt) ChunkIDChunkMap.get(chunkID);
            if (chunk instanceof Grids_GridChunkInt) {
                Grids_GridChunkInt gc = (Grids_GridChunkInt) chunk;
                if (value != gc.Value) {
                    // Convert chunk to another type
                    chunk = env.getProcessor().DefaultGridChunkIntFactory.create(
                            chunk, chunkID);
                    chunk.initCell(getCellRow(row), getCellCol(col), value);
                    ChunkIDChunkMap.put(chunkID, chunk);
                    if (!(chunk instanceof Grids_GridChunkInt)) {
                        ChunkIDsOfChunksWorthSwapping.add(chunkID);
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
     * @return Grids_AbstractGridChunkInt for the given chunkID.
     * @param chunkID
     */
    @Override
    public Grids_AbstractGridChunkInt getChunk(
            Grids_2D_ID_int chunkID) {
        if (isInGrid(chunkID)) {
            if (ChunkIDChunkMap.get(chunkID) == null) {
                loadIntoCacheChunk(chunkID);
            }
            return (Grids_AbstractGridChunkInt) ChunkIDChunkMap.get(chunkID);
        }
        return null;
    }

    /**
     * @return Grids_AbstractGridChunkInt for the given chunkID.
     * @param chunkID
     */
    @Override
    public Grids_AbstractGridChunkInt getChunk(
            Grids_2D_ID_int chunkID,
            int chunkRow,
            int chunkCol) {
        if (isInGrid(chunkRow, chunkCol)) {
            if (ChunkIDChunkMap.get(chunkID) == null) {
                loadIntoCacheChunk(chunkID);
            }
            return (Grids_AbstractGridChunkInt) ChunkIDChunkMap.get(chunkID);
        }
        return null;
    }

    /**
     * If newValue and oldValue are the same then stats won't change.
     *
     * @param newValue The value replacing oldValue.
     * @param oldValue The value being replaced.
     */
    public void updateStats(
            int newValue,
            int oldValue) {
        if (Stats.isUpdated()) {
            if (newValue != NoDataValue) {
                if (oldValue != NoDataValue) {
                    BigDecimal oldValueBD = new BigDecimal(oldValue);
                    Stats.setN(Stats.getN() - 1);
                    Stats.setSum(Stats.getSum().subtract(oldValueBD));
                    int min = Stats.getMin(false);
                    if (oldValue == min) {
                        Stats.setNMin(Stats.getNMin() - 1);
                    }
                    int max = Stats.getMax(false);
                    if (oldValue == max) {
                        Stats.setNMax(Stats.getNMax() - 1);
                    }
                }
                if (newValue != NoDataValue) {
                    BigDecimal newValueBD = new BigDecimal(newValue);
                    Stats.setN(Stats.getN() + 1);
                    Stats.setSum(Stats.getSum().add(newValueBD));
                    updateStats(newValue);
                    if (Stats.getNMin() < 1) {
                        // The Stats need recalculating
                        Stats.update();
                    }
                    if (Stats.getNMax() < 1) {
                        // The Stats need recalculating
                        Stats.update();
                    }
                }
            }
        } else {
            if (newValue != oldValue) {
                ((Grids_GridIntStatsNotUpdated) Stats).setUpToDate(false);
            }
        }
    }

    public final int getNoDataValue() {
        return NoDataValue;
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
     * For getting the value at row, col.
     *
     * @param row
     * @param col
     * @return
     */
    public int getCell(
            long row,
            long col) {
//        boolean isInGrid = isInGrid(row, col);
//        if (isInGrid) {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        Grids_AbstractGridChunkInt c;
        c = getChunk(chunkRow, chunkCol);
        int cellRow = getCellRow(row);
        int cellCol = getCellCol(col);
        return getCell(c, cellRow, cellCol);
//        }
//        return NoDataValue;
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
    public int getCell(
            Grids_AbstractGridChunkInt chunk,
            int cellRow,
            int cellCol) {
        if (chunk.getClass() == Grids_GridChunkIntArray.class) {
            return ((Grids_GridChunkIntArray) chunk).getCell(cellRow, cellCol);
        } else if (chunk.getClass() == Grids_GridChunkIntMap.class) {
            return ((Grids_GridChunkIntMap) chunk).getCell(cellRow, cellCol);
        } else {
            return ((Grids_GridChunkInt) chunk).getCell(cellRow, cellCol);
        }
    }

    /**
     * For getting the value at x-coordinate x, y-coordinate y.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @return
     */
    public final int getCell(
            double x,
            double y) {
        long row = getRow(y);
        long col = getCol(x);
        boolean isInGrid = isInGrid(row, col);
        if (isInGrid) {
            return getCell(row, col);
        }
        return NoDataValue;
    }

    /**
     * For returning the value of the cell with cellID.
     *
     * @param cellID the Grids_2D_ID_long of the cell.
     * @return
     */
    public final int getCell(
            Grids_2D_ID_long cellID) {
        return getCell(cellID.getRow(), cellID.getCol());
    }

    /**
     * For setting the value at x-coordinate x, y-coordinate y.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     * @param value
     */
    public final void setCell(
            double x,
            double y,
            int value) {
        setCell(getRow(x), getCol(y), value);
    }

    /**
     * For setting the value at row, col.
     *
     * @param row
     * @param col
     * @param value
     */
    public void setCell(long row, long col, int value) {
        int chunkRow = getChunkRow(row);
        int chunkCol = getChunkCol(col);
        int cellRow = getCellRow(row);
        int cellCol = getCellCol(col);
        Grids_AbstractGridChunkInt chunk;
        chunk = (Grids_AbstractGridChunkInt) Grids_GridInt.this.getChunk(
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
    public void setCell(
            int chunkRow,
            int chunkCol,
            int cellRow,
            int cellCol,
            int newValue) {
        Grids_AbstractGridChunkInt chunk;
        chunk = (Grids_AbstractGridChunkInt) Grids_GridInt.this.getChunk(
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
            Grids_AbstractGridChunkInt chunk,
            int cellRow,
            int cellCol,
            int value) {
        int v;
        if (chunk instanceof Grids_GridChunkIntArray) {
            v = ((Grids_GridChunkIntArray) chunk).setCell(cellRow, cellCol,
                    value);
        } else if (chunk instanceof Grids_GridChunkIntMap) {
            v = ((Grids_GridChunkIntMap) chunk).setCell(cellRow, cellCol,
                    value);
        } else {
            Grids_GridChunkInt c;
            c = (Grids_GridChunkInt) chunk;
            if (value != c.Value) {
                // Convert chunk to another type
                Grids_2D_ID_int chunkID;
                chunkID = chunk.getChunkID();
                chunk = convertToAnotherTypeOfChunk(chunk, chunkID);
                v = chunk.setCell(cellRow, cellCol, value);
            } else {
                v = c.Value;
            }
        }
        // Update Stats
        if (value != v) {
            if (Stats.isUpdated()) {
                Grids_GridInt.this.updateStats(value, v);
            }
        }
    }

    /**
     * Convert chunk to another type of chunk.
     */
    private Grids_AbstractGridChunkInt convertToAnotherTypeOfChunk(
            Grids_AbstractGridChunkInt chunk,
            Grids_2D_ID_int chunkID) {
        Grids_AbstractGridChunkInt result;
        Grids_AbstractGridChunkIntFactory f;
        f = env.getProcessor().DefaultGridChunkIntFactory;
        result = f.create(chunk, chunkID);
        ChunkIDChunkMap.put(chunkID, result);
        return result;
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
    protected void initCell(
            Grids_AbstractGridChunkInt chunk,
            Grids_2D_ID_int chunkID,
            long row,
            long col,
            int value) {
        if (chunk instanceof Grids_GridChunkInt) {
            Grids_GridChunkInt gridChunk = (Grids_GridChunkInt) chunk;
            if (value != gridChunk.Value) {
                // Convert chunk to another type
                chunk = convertToAnotherTypeOfChunk(chunk, chunkID);
                chunk.initCell(getCellRow(row), getCellCol(col), value);
            } else {
                return;
            }
        }
        chunk.initCell(getCellRow(row), getCellCol(col), value);
        // Update Stats
        if (value != NoDataValue) {
            if (Stats.isUpdated()) {
                Grids_GridInt.this.updateStats(value);
            }
        }
    }

    public void updateStats(int value) {
        BigDecimal valueBD = new BigDecimal(value);
        Stats.setN(Stats.getN() + 1);
        Stats.setSum(Stats.getSum().add(valueBD));
        int min = Stats.getMin(false);
        if (value < min) {
            Stats.setNMin(1);
            Stats.setMin(value);
        } else {
            if (value == min) {
                Stats.setNMin(Stats.getNMin() + 1);
            }
        }
        int max = Stats.getMax(false);
        if (value > max) {
            Stats.setNMax(1);
            Stats.setMax(value);
        } else {
            if (value == max) {
                Stats.setNMax(Stats.getNMax() + 1);
            }
        }
    }

    /**
     * Initialises the value at row, col and does nothing about Stats
     *
     * @param chunk
     * @param row
     * @param col
     * @param value
     */
    protected void initCellFast(
            Grids_AbstractGridChunkInt chunk,
            long row,
            long col,
            int value) {
//        int chunkRow = getChunkRow(row);
//        int chunkCol = getChunkCol(col);
//        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
//                chunkRow,
//                chunkCol);
//        Grids_AbstractGridChunkInt chunk = getChunk(chunkID);
        chunk.initCell(getCellRow(row), getCellCol(col), value);
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
        return getCells(x, y, getRow(y), getCol(x), distance);
    }

    /**
     * @return int[] of all cell values for cells thats centroids are
     * intersected by circle with centre at centroid of cell given by cell row
     * index row, cell column index col, and radius distance.
     * @param row the row index for the cell that'Stats centroid is the circle
     * centre from which cell values are returned.
     * @param col the column index for the cell that'Stats centroid is the
     * circle centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    public int[] getCells(
            long row,
            long col,
            double distance) {
        return getCells(getCellXDouble(col), getCellYDouble(row), row, col,
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
     */
    public int[] getCells(
            double x,
            double y,
            long row,
            long col,
            double distance) {
        int[] cells;
        int cellDistance = (int) Math.ceil(distance / getCellsizeDouble());
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
        System.arraycopy(cells, 0, cells, 0, count);
        return cells;
    }

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    @Override
    public double getNearestValueDouble(
            double x,
            double y) {
        double result = getCell(x, y);
        if (result == NoDataValue) {
            result = getNearestValueDouble(x, y, getRow(y), getCol(x));
        }
        return result;
    }

    /**
     * @param row The row index from which average of the nearest data values is
     * returned.
     * @param col The column index from which average of the nearest data values
     * is returned.
     * @return the average of the nearest data values to position given by row
     * index rowIndex, column index colIndex
     */
    @Override
    public double getNearestValueDouble(
            long row,
            long col) {
        double result = getCell(row, col);
        if (result == NoDataValue) {
            result = getNearestValueDouble(getCellXDouble(col),
                    getCellYDouble(row), row, col);
        }
        return result;
    }

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row the row index from which average of the nearest data values is
     * returned
     * @param col the column index from which average of the nearest data values
     * is returned
     */
    @Override
    public double getNearestValueDouble(
            double x,
            double y,
            long row,
            long col) {
        Grids_2D_ID_long nearestCellID = getNearestCellID(x, y, row, col);
        double nearestValue = getCell(row, col);
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
                        isInGrid = isInGrid(row + p, col + q);
                        if (isInGrid) {
                            cellID0 = new Grids_2D_ID_long(row + p, col + q);
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
                    value = getCell(cellID0);
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
                distance = Grids_Utilities.distance(x, y,
                        getCellXDouble(cellID0), getCellYDouble(cellID0));
                if (distance < minDistance) {
                    closest.clear();
                    closest.add(cellID0);
                } else {
                    if (distance == minDistance) {
                        closest.add(cellID0);
                    }
                }
                minDistance = Math.min(minDistance, distance);
            }
            // Get cellIDs that are within distance of discovered value
            Grids_2D_ID_long[] cellIDs = getCellIDs(x, y, minDistance);
            for (Grids_2D_ID_long cellID : cellIDs) {
                if (!visitedSet.contains(cellID)) {
                    if (getCell(cellID) != NoDataValue) {
                        distance = Grids_Utilities.distance(x, y,
                                getCellXDouble(cellID), getCellYDouble(cellID));
                        if (distance < minDistance) {
                            closest.clear();
                            closest.add(cellID);
                        } else {
                            if (distance == minDistance) {
                                closest.add(cellID);
                            }
                        }
                        minDistance = Math.min(minDistance, distance);
                    }
                }
            }
            // Go through the closest and calculate the average.
            value = 0;
            iterator = closest.iterator();
            while (iterator.hasNext()) {
                cellID0 = (Grids_2D_ID_long) iterator.next();
                value += getCell(cellID0);
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
    public Grids_2D_ID_long[] getNearestValuesCellIDs(double x, double y) {
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
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col
     */
    @Override
    public Grids_2D_ID_long[] getNearestValuesCellIDs(
            long row,
            long col) {
        double value = getCell(row, col);
        if (value == NoDataValue) {
            return getNearestValuesCellIDs(getCellXDouble(col),
                    getCellYDouble(row), row, col);
        }
        Grids_2D_ID_long[] cellIDs = new Grids_2D_ID_long[1];
        cellIDs[0] = getCellID(row, col);
        return cellIDs;
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values nearest to point with position given by: x-coordinate x,
     * y-coordinate y; and, cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col The column index from which the cell IDs of the nearest cells
     * with data values are returned.
     */
    @Override
    public Grids_2D_ID_long[] getNearestValuesCellIDs(
            double x,
            double y,
            long row,
            long col) {
        Grids_2D_ID_long[] nearestCellIDs = new Grids_2D_ID_long[1];
        nearestCellIDs[0] = getNearestCellID(x, y, row, col);
        double nearestCellValue = getCell(row, col);
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
                        isInGrid = isInGrid(row + p, col + q);
                        if (isInGrid) {
                            cellID = getCellID(row + p, col + q);
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
                    value = getCell(cellID);
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
                distance = Grids_Utilities.distance(x, y,
                        getCellXDouble(cellID), getCellYDouble(cellID));
                if (distance < minDistance) {
                    closest.clear();
                    closest.add(cellID);
                } else {
                    if (distance == minDistance) {
                        closest.add(cellID);
                    }
                }
                minDistance = Math.min(minDistance, distance);
            }
            // Get cellIDs that are within distance of discovered value
            Grids_2D_ID_long[] cellIDs = getCellIDs(
                    x,
                    y,
                    minDistance);
            for (Grids_2D_ID_long cellID1 : cellIDs) {
                if (!visitedSet.contains(cellID1)) {
                    if (getCell(cellID1) != NoDataValue) {
                        distance = Grids_Utilities.distance(x, y,
                                getCellXDouble(cellID1),
                                getCellYDouble(cellID1));
                        if (distance < minDistance) {
                            closest.clear();
                            closest.add(cellID1);
                        } else {
                            if (distance == minDistance) {
                                closest.add(cellID1);
                            }
                        }
                        minDistance = Math.min(minDistance, distance);
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
    public double getNearestValueDoubleDistance(double x, double y) {
        double result = getCell(x, y);
        if (result == NoDataValue) {
            result = getNearestValueDoubleDistance(x, y, getRow(y), getCol(x));
        }
        return result;
    }

    /**
     * @return the distance to the nearest data value from position given by row
     * index rowIndex, column index colIndex as a double.
     * @param row The cell row index of the cell from which the distance nearest
     * to the nearest cell value is returned.
     * @param col The cell column index of the cell from which the distance
     * nearest to the nearest cell value is returned.
     */
    public double getNearestValueDoubleDistance(
            long row,
            long col) {
        double result = getCell(row, col);
        if (result == NoDataValue) {
            result = getNearestValueDoubleDistance(getCellXDouble(col),
                    getCellYDouble(row), row, col);
        }
        return result;
    }

    /**
     * @return the distance to the nearest data value from: point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex as a double.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param row The cell row index of the cell from which the distance nearest
     * to the nearest cell value is returned.
     * @param col The cell column index of the cell from which the distance
     * nearest to the nearest cell value is returned.
     */
    @Override
    public double getNearestValueDoubleDistance(
            double x,
            double y,
            long row,
            long col) {
        double result = getCell(row, col);
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
            Grids_2D_ID_long nearestCellID = getNearestCellID(x, y, row, col);
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
                        isInGrid = isInGrid(long0, long1);
                        if (isInGrid) {
                            cellID = getCellID(long0, long1);
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
                    value = getCell(cellID);
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
                                    isInGrid = isInGrid(long0, long1);
                                    if (isInGrid) {
                                        cellID = getCellID(long0, long1);
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
                double0 = getCellXDouble(cellID);
                double1 = getCellYDouble(cellID);
                distance = Grids_Utilities.distance(x, y, double0, double1);
                if (distance < minDistance) {
                    closest.clear();
                    closest.add(cellID);
                } else {
                    if (distance == minDistance) {
                        closest.add(cellID);
                    }
                }
                minDistance = Math.min(minDistance, distance);
            }
            // Get cellIDs that are within distance of discovered value
            Grids_2D_ID_long[] cellIDs = getCellIDs(x, y, minDistance);
            for (Grids_2D_ID_long cellID1 : cellIDs) {
                if (!visitedSet.contains(cellID1)) {
                    if (getCell(cellID1) != NoDataValue) {
                        distance = Grids_Utilities.distance(x, y,
                                getCellXDouble(cellID1),
                                getCellYDouble(cellID1));
                        minDistance = Math.min(minDistance, distance);
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
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param v the value to be added to the cell containing the point
     */
    public void addToCell(
            double x,
            double y,
            int v) {
        addToCell(getRow(y), getCol(x), v);
    }

    /**
     * @param cellID the Grids_2D_ID_long of the cell.
     * @param v the value to be added to the cell containing the point
     */
    public void addToCell(
            Grids_2D_ID_long cellID,
            int v) {
        addToCell(cellID.getRow(), cellID.getCol(), v);
    }

    /**
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @param v the value to be added to the cell. NB1. If cell is not contained
     * in this then then returns NoDataValue. NB2. Adding to NoDataValue is done
     * as if adding to a cell with value of 0. TODO: Check Arithmetic
     */
    public void addToCell(
            long row,
            long col,
            int v) {
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
        while (ite.hasNext()) {
            env.checkAndMaybeFreeMemory();
            System.out.println("Initialising Chunk " + counter + " out of "
                    + nChunks);
            counter++;
            chunkID = ite.next();
            chunk = (Grids_AbstractGridChunkInt) ChunkIDChunkMap.get(chunkID);
            chunkNRows = getChunkNRows(chunkID);
            chunkNCols = getChunkNCols(chunkID);
            for (row = 0; row <= chunkNRows; row++) {
                for (col = 0; col <= chunkNCols; col++) {
                    chunk.initCell(chunkNRows, chunkNCols, value);
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
    public Grids_GridIntStats getStats() {
        return Stats;
    }

    public void initStats(Grids_GridIntStats stats) {
        Stats = stats;
    }

    @Override
    public double getCellDouble(Grids_AbstractGridChunk chunk, int chunkRow,
            int chunkCol, int cellRow, int cellCol) {
        Grids_AbstractGridChunkInt c;
        c = (Grids_AbstractGridChunkInt) chunk;
        Grids_GridInt g;
        g = (Grids_GridInt) c.getGrid();
        if (chunk.getClass() == Grids_GridChunkIntArray.class) {
            Grids_GridChunkIntArray gridChunkArray;
            gridChunkArray = (Grids_GridChunkIntArray) c;
            return gridChunkArray.getCell(cellRow, cellCol);
        }
        if (chunk.getClass() == Grids_GridChunkIntMap.class) {
            Grids_GridChunkIntMap gridChunkMap;
            gridChunkMap = (Grids_GridChunkIntMap) c;
            return gridChunkMap.getCell(cellRow, cellCol);
        }
        double noDataValue = g.NoDataValue;
        return noDataValue;
    }

}
