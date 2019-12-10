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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkDouble;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_GridChunkInt;
import uk.ac.leeds.ccg.agdt.grids.core.grid.stats.Grids_AbstractGridStats;
import uk.ac.leeds.ccg.agdt.grids.io.Grids_ESRIAsciiGridImporter.Grids_ESRIAsciiGridHeader;
import uk.ac.leeds.ccg.agdt.grids.utilities.Grids_Utilities;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;

/**
 * Grids_AbstractGrid
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_AbstractGrid extends Grids_Object
        implements Grids_InterfaceGrid {

    private static final long serialVersionUID = 1L;

    /**
     * Path to dir used for storing grid data.
     */
    protected Generic_Path dir;
    
    /**
     * The Grids_AbstractGridChunk data cache.
     */
    protected TreeMap<Grids_2D_ID_int, Grids_AbstractGridChunk> chunkIDChunkMap;
    
    /**
     * The Grids_AbstractGridChunk data cache.
     */
    protected HashSet<Grids_2D_ID_int> ChunkIDsOfChunksWorthCaching;
    
    /**
     * For storing the number of chunk rows.
     */
    protected int NChunkRows;
    
    /**
     * For storing the number of chunk columns.
     */
    protected int NChunkCols;
    
    /**
     * For storing the (usual) number of rows of cells in a chunk. The number of
     * rows in the final chunk row may be less.
     */
    protected int ChunkNRows;
    
    /**
     * For storing the (usual) number of columns of cells in a chunk. The number
     * of columns in the final chunk column may be less.
     */
    protected int ChunkNCols;
    
    /**
     * For storing the number of rows in the grid.
     */
    protected long NRows;
    
    /**
     * For storing the number of columns in the grid.
     */
    protected long NCols;
    
    /**
     * For storing the Name of the grid.
     */
    protected String Name;
    
    /**
     * @see {@link Grids_Dimensions}
     */
    protected Grids_Dimensions Dimensions;

    /**
     * A reference to the grid stats.
     */
    protected Grids_AbstractGridStats stats;

    protected Grids_AbstractGrid() {
    }

    protected Grids_AbstractGrid(Grids_Environment ge, Generic_Path dir) {
        super(ge);
        this.dir = dir;
    }

    /**
     * Checks to see if {@link #dir} already exists and contains a lock file. If
     * it does then an error is thrown likely aborting the program.
     * @throws IOException If a lock file could not be created.
     */
    protected final void checkDir() throws IOException {
        Path p = dir.getPath();
        if (Files.exists(p)) {
            if (Files.isDirectory(p)) {
                Path lock = Paths.get(dir.toString(), "lock");
                if (!Files.exists(lock)) {
                    Files.createFile(lock);
                } else {
                    throw new IOException("Lock file " + lock + " already exists!");
                }
            } else {
                throw new IOException(dir.toString() + " already exists as a file!");
            }
        }
    }

    protected void init() throws IOException {
        Files.createDirectory(dir.getPath());
        env.setDataToCache(true);
        env.addGrid(this);
    }

    /**
     * Initialises non transient Grids_AbstractGrid fields from g.
     *
     * @param g The Grids_AbstractGrid from which the non transient
     * Grids_AbstractGrid fields of this are set.
     * @throws java.io.IOException If encountered.
     */
    protected void init(Grids_AbstractGrid g) throws IOException {
        ChunkNCols = g.ChunkNCols;
        ChunkNRows = g.ChunkNRows;
        Dimensions = g.Dimensions;
        Name = g.Name;
        NChunkCols = g.NChunkCols;
        NChunkRows = g.NChunkRows;
        NCols = g.NCols;
        NRows = g.NRows;
        init();
    }

    /**
     * Sets the references to this in the chunks
     */
    protected void setReferenceInChunkIDChunkMap() {
        Iterator<Grids_2D_ID_int> ite = chunkIDChunkMap.keySet().iterator();
        while (ite.hasNext()) {
            Grids_2D_ID_int chunkID = ite.next();
            Grids_AbstractGridChunk chunk = chunkIDChunkMap.get(chunkID);
            chunk.setGrid(this);
        }
    }

    /**
     * @return HashSet containing all ChunkIDs.
     */
    public HashSet<Grids_2D_ID_int> getChunkIDs() {
        HashSet<Grids_2D_ID_int> result = new HashSet<>();
        result.addAll(chunkIDChunkMap.keySet());
        return result;
    }

    /**
     * Override to provide a more detailed fields description.
     *
     * @return
     */
    public String getFieldsDescription() {
        String r = "ChunkNcols=" + ChunkNCols + ", ";
        r += "ChunkNrows=" + ChunkNRows + ", ";
        r += "NChunkCols=" + NChunkCols + ", ";
        r += "NChunkRows=" + NChunkRows + ", ";
        r += "NCols=" + NCols + ", ";
        r += "NRows=" + NRows + ", ";
        r += "Directory=" + dir + ", ";
        r += "Name=" + Name + ", ";
        r += "Dimensions=" + getDimensions().toString() + ", ";
        if (chunkIDChunkMap == null) {
            r += "ChunkIDChunkMap=null, ";
        } else {
            r += "ChunkIDChunkMap.size()=" + chunkIDChunkMap.size() + ", ";
        }
        HashSet<Grids_AbstractGrid> grids = env.getGrids();
        if (grids == null) {
            r += "Grids=null, ";
        } else {
            r += "Grids.size()=" + grids.size() + ", ";
        }
        r += getStats().toString();
        return r;
    }

    /**
     * @return a string description of the Abstract fields of this instance.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getFieldsDescription() + "]";
    }

    /**
     * @return dir.
     */
    public Generic_Path getDirectory() {
        return new Generic_Path(dir);
    }

    /**
     *
     * @return Name
     */
    public String getName() {
        return Name;
    }

    /**
     * Sets Name to name.
     *
     * @param name The String Name is set to.
     */
    public void setName(String name) {
        Name = name;
    }

    /**
     * Beware OutOfMemoryErrors calling this method.
     *
     * @return ncols.
     */
    public final long getNCols() {
        return NCols;
    }

    /**
     * Beware OutOfMemoryErrors calling this method.
     *
     * @return nrows.
     */
    public final long getNRows() {
        return NRows;
    }

    /**
     * @return NChunkRows.
     */
    public final int getNChunkRows() {
        return NChunkRows;
    }

    /**
     * Initialises NChunkRows.
     */
    protected final void initNChunkRows() {
        long chunkNrows = (long) ChunkNRows;
        if ((NRows % chunkNrows) != 0) {
            NChunkRows = (int) (NRows / chunkNrows) + 1;
        } else {
            NChunkRows = (int) (NRows / chunkNrows);
        }
    }

    /**
     * @return NChunkCols.
     */
    public final int getNChunkCols() {
        return NChunkCols;
    }

    /**
     * @return the number of chunks in this as a long.
     */
    public final long getNChunks() {
        long nChunks = (long) NChunkRows * (long) NChunkCols;
        if (nChunks > Integer.MAX_VALUE) {
            System.err.println("Error nChunks > Integer.MAX_VALUE in "
                    + this.getClass().getName() + ".getNChunks()");
            throw new Error();
        }
        return nChunks;
    }

    /**
     * Initialises NChunkCols.
     */
    protected final void initNChunkCols() {
        long chunkNCols = (long) ChunkNCols;
        if ((NCols % chunkNCols) != 0) {
            NChunkCols = (int) (NCols / chunkNCols) + 1;
        } else {
            NChunkCols = (int) (NCols / chunkNCols);
        }
    }

    public final int getChunkNRows() {
        return ChunkNRows;
    }

    /**
     * @param chunkRow
     * @return The number of rows in the chunks in the chunk row chunkRow.
     */
    public final int getChunkNRows(int chunkRow) {
        if (chunkRow > -1 && chunkRow < NChunkRows) {
            if (chunkRow == (NChunkRows - 1)) {
                return getChunkNRowsFinalRowChunk();
            } else {
                return ChunkNRows;
            }
        } else {
            return 0;
        }
    }

    public final int getChunkNCols() {
        return ChunkNCols;
    }

    /**
     * @param chunkCol
     * @return The number of columns in the chunks in the chunk column chunkCol.
     */
    public final int getChunkNCols(int chunkCol) {
        if (chunkCol > -1 && chunkCol < NChunkCols) {
            if (chunkCol == (NChunkCols - 1)) {
                return getChunkNColsFinalColChunk();
            } else {
                return ChunkNCols;
            }
        } else {
            return 0;
        }
    }

    /**
     * @return the number of rows in the chunks in the final row.
     */
    public final int getChunkNRowsFinalRowChunk() {
        long nChunkRowsMinusOne = (long) (NChunkRows - 1);
        long chunkNRows = (long) ChunkNRows;
        return (int) (NRows - (nChunkRowsMinusOne * chunkNRows));
    }

    /**
     * @return the number of columns in the chunks in the final column.
     */
    public final int getChunkNColsFinalColChunk() {
        long nChunkColsMinusOne = (long) (NChunkCols - 1);
        long chunkNCols = (long) ChunkNCols;
        return (int) (NCols - (nChunkColsMinusOne * chunkNCols));
    }

    /**
     * @return _ChunkNRows, the number of rows in Grids_AbstractGridChunk with
     * Grids_2D_ID_int equal to _ChunkID
     * @param chunkID The Grids_2D_ID_int of the Grids_AbstractGridChunk thats
     * number of rows is returned.
     */
    public final int getChunkNRows(Grids_2D_ID_int chunkID) {
        if (chunkID.getRow() < (NChunkRows - 1)) {
            return ChunkNRows;
        } else {
            return getChunkNRowsFinalRowChunk();
        }
    }

    /**
     * @return _ChunkNCols, the number of columns in Grids_AbstractGridChunk
     * with Grids_2D_ID_int equal to _ChunkID
     * @param chunkID The Grids_2D_ID_int of the Grids_AbstractGridChunk thats
     * number of columns is returned.
     */
    public final int getChunkNCols(Grids_2D_ID_int chunkID) {
        if (chunkID.getCol() < (NChunkCols - 1)) {
            return ChunkNCols;
        } else {
            return getChunkNColsFinalColChunk();
        }
    }

    /**
     * @return the Dimensions
     */
    public Grids_Dimensions getDimensions() {
        return Dimensions;
    }

    /**
     * This method is for convenience.
     *
     * @return BigDecimal equal to this.Dimensions.getCellsize().
     */
    public final BigDecimal getCellsize() {
        return getDimensions().getCellsize();
    }

    /**
     * This method is for convenience.
     *
     * @return double equal to this.Dimensions[0].doubleValue.
     */
    public final double getCellsizeDouble() {
        return getCellsize().doubleValue();
    }

    /**
     * @return HashSet of ChunkIDs for cells thats centroids are intersected by
     * circle with centre at x-coordinate x, y-coordinate y, and radius
     * distance.
     * @param x The x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     * returned.
     * @param row The row index at y.
     * @param col The col index at x.
     * @param distance The radius of the circle for which intersected cell
     * values are returned.
     */
    public HashSet<Grids_2D_ID_int> getChunkIDs(
            double distance, double x, double y, long row, long col) {
        HashSet<Grids_2D_ID_int> result = new HashSet<>();
        long p;
        long q;
        long cellDistance = (long) Math.ceil(distance / getCellsizeDouble());
        double thisCellX;
        double thisCellY;
        double thisDistance;
        for (p = -cellDistance; p <= cellDistance; p++) {
            thisCellY = getCellYDouble(row + p);
            for (q = -cellDistance; q <= cellDistance; q++) {
                thisCellX = getCellXDouble(col + q);
                thisDistance = Grids_Utilities.distance(thisCellX, thisCellY,
                        x, y);
                if (thisDistance < distance) {
                    Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                            getChunkRow((long) row + p),
                            getChunkCol((long) col + q));
                    result.add(chunkID);
                }
            }
        }
        return result;
    }

    /**
     * @return Chunk column index for the Grids_AbstractGridChunk intersecting
     * the x-coordinate x.
     * @param x The x-coordinate of the line intersecting the chunk column index
     * returned.
     */
    public final int getChunkCol(double x) {
        return getChunkCol(getCol(x));
    }

    /**
     * @return Chunk column index for the Grids_AbstractGridChunk intersecting
     * the cell column index _CellColIndex.
     *
     * @param col
     */
    public final int getChunkCol(long col) {
        return (int) (col / (long) ChunkNCols);
    }

    /**
     * @return Cell column of the cells that intersect the x axis coordinate x.
     * @param x The x-coordinate of the line intersecting the cell column index
     * returned.
     */
    public final long getCol(double x) {
        return getCol(BigDecimal.valueOf(x));
    }

    /**
     * @param x
     * @return Cell column of the cells that intersect the x axis coordinate x.
     */
    public final long getCol(BigDecimal x) {
        Grids_Dimensions gd;
        gd = getDimensions();
        BigDecimal xMinusXMin;
        xMinusXMin = x.subtract(gd.getXMin());
        BigDecimal div;
        div = Math_BigDecimal.divideRoundIfNecessary(xMinusXMin,
                gd.getCellsize(), 0, RoundingMode.DOWN);
        return div.toBigInteger().longValue();
    }

    /**
     * @param chunkCol
     * @param cellCol
     * @return ((long) chunkCol * (long) ChunkNCols) + (long) cellCol
     */
    public final long getCol(int chunkCol, int cellCol) {
        return ((long) chunkCol * (long) ChunkNCols) + (long) cellCol;
    }

    /**
     * @return Chunk cell column Index of the cells that intersect the
     * x-coordinate x.
     * @param x The x-coordinate of the line intersecting the chunk cell column
     * index returned.
     */
    public final int getCellCol(double x) {
        return getCellCol(getCol(x));
    }

    /**
     * @return Chunk cell column index of the cells in the cell column index
     * _CellColIndex.
     * @param col The cell column index of the cell thats chunk cell column
     * index is returned.
     */
    public final int getCellCol(long col) {
        long chunkCol = getChunkCol(col);
        return (int) (col - (chunkCol * ChunkNCols));
    }

    /**
     * @param random
     * @return A Random CellColIndex.
     */
    public final long getCol(Random random) {
        if (NCols < Integer.MAX_VALUE) {
            return random.nextInt((int) NCols);
        } else {
            long col = 0;
            long colMax = 0;
            while (colMax < NCols) {
                colMax += Integer.MAX_VALUE;
                if (colMax < NCols) {
                    col += random.nextInt();
                } else {
                    int colInt = (int) (colMax - NCols);
                    if (colInt > 0) {
                        col += random.nextInt();
                    }
                }
            }
            return col;
        }
    }

    /**
     * @return Chunk row index for the chunks intersecting the line given by
     * y-coordinate y.
     * @param y The y-coordinate of the line for which the chunk row index is
     * returned.
     */
    public final int getChunkRow(double y) {
        return getChunkRow(getRow(y));
    }

    /**
     * @return Chunk row index for the chunk intersecting the cells with cell
     * row index _CellRowIndex.
     * @param row The cell row index of the cells thats chunk row index is
     * returned.
     */
    public final int getChunkRow(long row) {
        return (int) (row / (long) ChunkNRows);
    }

    /**
     * @return Cell row Index for the cells that intersect the line with
     * y-coordinate y.
     * @param y The y-coordinate of the line thats cell row index is returned.
     */
    public final Long getRow(double y) {
        return getRow(BigDecimal.valueOf(y));
    }

    /**
     * @param y
     * @return Cell row of the cells that intersect the y axis coordinate y.
     */
    public final Long getRow(BigDecimal y) {
        Grids_Dimensions gd = getDimensions();
        BigDecimal yMinusYMin = y.subtract(gd.getYMin());
        BigDecimal div = Math_BigDecimal.divideRoundIfNecessary(yMinusYMin,
                gd.getCellsize(), 0, RoundingMode.DOWN);
        return div.toBigInteger().longValue();
    }

    /**
     * @param chunkRow
     * @param cellRow
     * @return ((long) chunkRow * (long) ChunkNRows) + (long) cellRow;
     * chunkCellRowIndex.
     */
    public final Long getRow(int chunkRow, int cellRow) {
        return ((long) chunkRow * (long) ChunkNRows) + (long) cellRow;
    }

    /**
     * @param random
     * @return A Random CellRowIndex.
     */
    public final Long getRow(Random random) {
        if (NRows < Integer.MAX_VALUE) {
            return (long) random.nextInt((int) NRows);
        } else {
            long row = 0;
            long rowMax = 0;
            while (rowMax < NRows) {
                rowMax += Integer.MAX_VALUE;
                if (rowMax < NRows) {
                    row += random.nextInt();
                } else {
                    int rowInt = (int) (rowMax - NRows);
                    if (rowInt > 0) {
                        row += random.nextInt();
                    }
                }
            }
            return row;
        }
    }

    /**
     * @return Cell row of the cells that intersects y axis coordinate y.
     * @param y The y-coordinate of the line for which the chunk cell row index
     * is returned.
     */
    public final int getCellRow(double y) {
        return getCellRow(getRow(y));
    }

    /**
     * @return Chunk cell row index of the cells in row.
     * @param row
     */
    public final int getCellRow(long row) {
        long chunkRow = getChunkRow(row);
        return (int) (row - (chunkRow * ChunkNRows));
    }

    /**
     * @param row
     * @param col
     * @return Grids_2D_ID_long of the cell given by cell row index
     * _CellRowIndex, cell column index _CellColIndex. A Grids_2D_ID_long is
     * returned even if that cell would not be in the grid.
     */
    public final Grids_2D_ID_long getCellID(long row, long col) {
        return new Grids_2D_ID_long(row, col);
    }

    /**
     * @return a Grids_2D_ID_long of the cell given by x-coordinate x,
     * y-coordinate y even if that cell would not be in the grid.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public final Grids_2D_ID_long getCellID(double x, double y) {
        return new Grids_2D_ID_long(getRow(y), getCol(x));
    }

    /**
     * @return a Grids_2D_ID_long of the cell given by x-coordinate x,
     * y-coordinate y even if that cell would not be in the grid.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public final Grids_2D_ID_long getCellID(BigDecimal x, BigDecimal y) {
        return new Grids_2D_ID_long(getRow(y), getCol(x));
    }

    /**
     * Attempts to write this instance to files located in the dir returned by
     * getDirectory().
     */
    public void writeToFile() {
        File f = new File(dir, "thisFile");
        Generic_IO.writeObject(this, Paths.f);
    }

    /**
     * Attempts to write this instance to Files located in the _Directory
     * returned by getDirectory(). Chunks are all cacheped to file.
     */
    public void writeToFileCaching() {
        cacheChunks();
        writeToFile();
    }

    /**
     * Attempts to write to file a chunk in ChunkIDChunkMap. The chunks are
     * iterated through in row major order. Only chunks that are not single
     * values chunks are written out. This method is low level and does not
     * consider ge,NotToCache. Other handling is required prior to calling this
     * method in order to first prefer to not to cache those chunks in
     * ge.NotToCache.
     *
     * @return Grids_2D_ID_int of the chunk which was cacheped or null if there
     * are no suitable chunks to cache.
     */
    public final Grids_2D_ID_int writeToFileChunk() {

        if (ChunkIDsOfChunksWorthCaching == null) {
            int debug = 1;
        }

        if (ChunkIDsOfChunksWorthCaching.isEmpty()) {
            return null;
        }
        Grids_2D_ID_int chunkID;
        Iterator<Grids_2D_ID_int> ite;
        ite = ChunkIDsOfChunksWorthCaching.iterator();
        while (ite.hasNext()) {
            chunkID = ite.next();
            writeToFileChunk(chunkID);
            return chunkID;
        }
//        if (chunkIDChunkMap.isEmpty()) {
//            return null;
//        }
//        Grids_2D_ID_int chunkID;
//        Iterator<Grids_2D_ID_int> ite;
//        ite = chunkIDChunkMap.keySet().iterator();
//        while (ite.hasNext()) {
//            chunkID = ite.next();
//            if (!isChunkSingleValueChunk(chunkID)) {
//                writeToFileChunk(chunkID);
//                return chunkID;
//            }
//        }
        return null;
    }

    /**
     * Attempts to write to File a serialised version of the chunk with chunkID.
     *
     * @param chunkID The ID of the Chunk. to be written.
     * @return True if Grids_AbstractGridChunk on file is up to date.
     */
    public final boolean writeToFileChunk(
            Grids_2D_ID_int chunkID) {
        boolean result = true;
        Grids_AbstractGridChunk gridChunk;
        gridChunk = chunkIDChunkMap.get(chunkID);
        if (gridChunk != null) {
            if (!gridChunk.isCacheUpToDate()) {
                File file = new File(getDirectory(),
                        chunkID.getRow() + "_" + chunkID.getCol());
                file.getParentFile().mkdirs();
                env.env.io.writeObject(gridChunk, file);
                //System.gc();
                gridChunk.setCacheUpToDate(true);
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Attempts to write to File serialised versions of all chunks in
     * chunkIDChunkMap that are not single valued chunks.
     */
    public final void writeToFileChunks() {
        Iterator ite;
        ite = ChunkIDsOfChunksWorthCaching.iterator();
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            chunkID = (Grids_2D_ID_int) ite.next();
            writeToFileChunk(chunkID);
        }
//        ite = chunkIDChunkMap.keySet().iterator();
//        Grids_2D_ID_int chunkID;
//        while (ite.hasNext()) {
//            chunkID = (Grids_2D_ID_int) ite.next();
//            if (isWorthCaching(chunkID)) {
//                writeToFileChunk(chunkID);
//            }
//        }
    }

    /**
     * Attempts to write to File chunks that have a chunkID in chunkIDs.
     *
     * @param chunkIDs A HashSet containing the Grids_2D_ID_int of the
     * Grids_AbstractGridChunk to be written to file.
     */
    public final void writeToFileChunks(
            HashSet<Grids_2D_ID_int> chunkIDs) {
        Iterator<Grids_2D_ID_int> ite;
        ite = chunkIDs.iterator();
        Grids_2D_ID_int id;
        while (ite.hasNext()) {
            id = ite.next();
            if (isWorthCaching(id)) {
                writeToFileChunk(id);
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache a chunk in this. This
     * is one of the lowest level memory handling operation of this class.
     *
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return An account of what was cacheped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> cacheChunk_AccountDetail(
            boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = cacheChunk_AccountDetail();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
            partResult = env.checkAndMaybeFreeMemory_AccountDetail(hoome);
            env.combine(result, partResult);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunk_AccountDetail();
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = env.initMemoryReserve_AccountDetail(hoome);
                env.combine(result, partResult);
                partResult = cacheChunk_AccountDetail(hoome);
                env.combine(result, partResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> cacheChunk_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Grids_2D_ID_int chunkID = writeToFileChunk();
        if (chunkID != null) {
            HashSet<Grids_2D_ID_int> chunks = new HashSet<>(1);
            clearChunk(chunkID);
            chunks.add(chunkID);
            result.put(this, chunks);
        }
        return result;
    }

    /**
     * @param checkAndMaybeFreeMemory
     * @param hoome
     * @return
     */
    public Grids_2D_ID_int cacheChunk_AccountChunk(
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            Grids_2D_ID_int result;
            result = cacheChunk_AccountChunk();
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int result;
                result = cacheChunk_AccountChunk();
                if (result == null) {
                    if (!env.cacheChunk(env.HOOMEF)) {
                        throw e;
                    }
                }
                env.initMemoryReserve();
                return cacheChunk_AccountChunk(
                        checkAndMaybeFreeMemory, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to cache a chunk and return true if cacheped and false otherwise.
     * This will first try to cache a chunk not in ge.NotToCache.
     *
     * @return
     */
    public Grids_2D_ID_int cacheChunk_AccountChunk() {
        Grids_2D_ID_int id;
        id = writeToFileChunk();
        if (id != null) {
            clearChunk(id);
        }
        return id;
    }

    public long cacheChunks_Account() {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int id;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                id = new Grids_2D_ID_int(cri, cci);
                if (writeToFileChunk(id)) {
                    clearChunk(id);
                    result++;
                }
            }
        }
        return result;
    }

    public long cacheChunks_Account(Set<Grids_2D_ID_int> chunkIDs) {
        long result = 0L;
        Iterator<Grids_2D_ID_int> ite = chunkIDs.iterator();
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            chunkID = ite.next();
            if (writeToFileChunk(chunkID)) {
                clearChunk(chunkID);
                result++;
            }
        }
        return result;
    }

    public Grids_2D_ID_int cacheChunkExcept_AccountChunk(
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            Grids_2D_ID_int result;
            result = cacheChunkExcept_AccountChunk(chunkIDs);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int result;
                result = cacheChunkExcept_AccountChunk(chunkIDs);
                if (result == null) {
                    if (!env.cacheChunk(env.HOOMEF)) {
                        throw e;
                    }
                }
                env.initMemoryReserve();
                return result;
            } else {
                throw e;
            }
        }
    }

    public Grids_2D_ID_int cacheChunkExcept_AccountChunk(
            HashSet<Grids_2D_ID_int> chunkIDs) {
        Grids_2D_ID_int chunkID = null;
//        int chunkRow;
//        int chunkCol;
        Iterator<Grids_2D_ID_int> ite;
        ite = ChunkIDsOfChunksWorthCaching.iterator();
        while (ite.hasNext()) {
            chunkID = ite.next();
            if (!chunkIDs.contains(chunkID)) {
                writeToFileChunk(chunkID);
                clearChunk(chunkID);
                return chunkID;
            }
        }
//        for (chunkRow = 0; chunkRow < NChunkRows; chunkRow++) {
//            for (chunkCol = 0; chunkCol < NChunkCols; chunkCol++) {
//                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
//                if (!chunkIDs.contains(chunkID)) {
//                    if (isWorthCaching(chunkID)) {
//                        writeToFileChunk(chunkID);
//                        clearChunk(chunkID);
//                        return chunkID;
//                    }
//                }
//            }
//        }
        return chunkID;
    }

    /**
     * Returns true if chunk is a single value (which tends not to be worth
     * caching).
     *
     * @param chunkID
     * @return
     */
    public boolean isChunkSingleValueChunk(Grids_2D_ID_int chunkID) {
        return chunkIDChunkMap.get(chunkID) instanceof Grids_GridChunkDouble
                || chunkIDChunkMap.get(chunkID) instanceof Grids_GridChunkInt;
    }

    /**
     * Caches the chunk with chunkID to file.
     *
     * @param chunkID
     * @param checkAndMaybeFreeMemory
     * @param hoome
     */
    public void cacheChunk(
            Grids_2D_ID_int chunkID,
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            cacheChunk(chunkID);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(hoome, e);
                cacheChunk(chunkID, checkAndMaybeFreeMemory,
                        hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Caches the chunk with chunkID to file. This will return true if the chunk
     * is cacheped and false otherwise. It is not sensible to cache some types of
     * chunk and in these cases false is returned.
     *
     * @param chunkID
     * @return
     */
    public boolean cacheChunk(Grids_2D_ID_int chunkID) {
        if (writeToFileChunk(chunkID)) {
            clearChunk(chunkID);
            return true;
        }
        return false;
    }

    /**
     * Attempts to write to file and clear from the cache any chunk in this.
     * This is one of the lowest level memory handling operation of this class.
     *
     * @param checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public boolean cacheChunk(
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            boolean result = cacheChunk();
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(hoome, e);
                return cacheChunk(checkAndMaybeFreeMemory, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to cache a chunk and return true if cacheped and false otherwise.
     * This will first try to cache a chunk not in ge.NotToCache.
     *
     * @return
     */
    public boolean cacheChunk() {
        Grids_2D_ID_int chunkID;
        chunkID = writeToFileChunk();
        if (chunkID != null) {
            clearChunk(chunkID);
            return true;
        }
        return false;
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param chunkID A Grids_2D_ID_int not to be cacheped
     * @param checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The Grids_2D_ID_int of Grids_AbstractGridChunk cacheped or null.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(
                    Grids_2D_ID_int chunkID,
                    boolean checkAndMaybeFreeMemory,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = cacheChunkExcept_AccountDetail(chunkID);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunkExcept_AccountDetail(chunkID);
                if (result.isEmpty()) {
                    result = env.cacheChunkExcept_AccountDetail(
                            this, chunkID, false);
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = env.initMemoryReserve_AccountDetail(this, chunkID, hoome);
                env.combine(result, partResult);
                partResult = cacheChunkExcept_AccountDetail(chunkID);
                env.combine(result, partResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache a chunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param chunkID
     * @return The Grids_2D_ID_int of Grids_AbstractGridChunk cacheped or null.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        int cri;
        int cci;
        Grids_2D_ID_int bid;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                bid = new Grids_2D_ID_int(cri, cci);
                if (!bid.equals(chunkID)) {
                    if (isWorthCaching(bid)) {
                        writeToFileChunk(bid);
                        clearChunk(bid);
                        HashSet<Grids_2D_ID_int> chunks;
                        chunks = new HashSet<>(1);
                        chunks.add(bid);
                        result.put(this, chunks);
                        return result;
                    }
                }
            }
        }
        return result;
    }

    public Grids_2D_ID_int cacheChunkExcept_AccountChunk(
            Grids_2D_ID_int chunkID,
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            Grids_2D_ID_int result = cacheChunkExcept_AccountChunk(chunkID);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int result;
                result = cacheChunkExcept_AccountChunk(chunkID);
                if (result == null) {
                    if (env.cacheChunkExcept_Account(this, chunkID, false) < 1L) {
                        throw e;
                    }
                }
                env.initMemoryReserve(this, chunkID, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    public Grids_2D_ID_int cacheChunkExcept_AccountChunk(
            Grids_2D_ID_int chunkID) {
        Grids_2D_ID_int result = null;
        int cri;
        int cci;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                result = new Grids_2D_ID_int(cri, cci);
                if (!result.equals(chunkID)) {
                    if (isWorthCaching(result)) {
                        writeToFileChunk(result);
                        clearChunk(result);
                        return result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Attempts to write to file and clear from the cache all
     * Grid2DSquareCellChunkAbstracts in this except that with ID a_ChunkID.
     *
     * @param chunkID
     * @param checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk cacheped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(
                    Grids_2D_ID_int chunkID,
                    boolean checkAndMaybeFreeMemory,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = cacheChunksExcept_AccountDetail(chunkID);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunkExcept_AccountDetail(chunkID);
                if (result.isEmpty()) {
                    result = env.cacheChunkExcept_AccountDetail(
                            this, chunkID, false);
                    if (result.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = env.initMemoryReserve_AccountDetail(
                        this, chunkID, hoome);
                env.combine(result, partResult);
                partResult = cacheChunksExcept_AccountDetail(
                        chunkID, checkAndMaybeFreeMemory, hoome);
                env.combine(result, partResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache all chunks in this
     * except that with chunkID.
     *
     * @param chunkID
     * @return A HashSet with the ChunkIDs of those Grids_AbstractGridChunk
     * cacheped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> chunksCacheped = new HashSet<>();
        int chunkRow;
        int chunkCol;
        Grids_2D_ID_int chunkIDToCache;
        for (chunkRow = 0; chunkRow < NChunkRows; chunkRow++) {
            for (chunkCol = 0; chunkCol < NChunkCols; chunkCol++) {
                chunkIDToCache = new Grids_2D_ID_int(chunkRow, chunkCol);
                if (!chunkID.equals(chunkIDToCache)) {
                    if (isWorthCaching(chunkIDToCache)) {
                        writeToFileChunk(chunkIDToCache);
                        clearChunk(chunkIDToCache);
                        chunksCacheped.add(chunkIDToCache);
                    }
                }
            }
        }
        result.put(this, chunksCacheped);
        return result;
    }

    public void cacheChunks(boolean hoome) {
        try {
            cacheChunks();
            env.checkAndMaybeFreeMemory(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(hoome, e);
                cacheChunks(hoome);
            } else {
                throw e;
            }
        }
    }

    public void cacheChunks() {
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (isWorthCaching(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearChunk(chunkID);
                }
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache all
     * Grid2DSquareCellChunkAbstracts in this except that with ID _ChunkIDs.
     *
     * @param chunks
     * @param checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk cacheped.
     */
    public long cacheChunksExcept_Account(
            HashSet<Grids_2D_ID_int> chunks,
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            long result = cacheChunksExcept_Account(chunks);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                long result = env.cacheChunkExcept_Account(this, chunks, false);
                if (result < 1L) {
                    result = env.cacheChunkExcept_Account(this, chunks, false);
                    if (result < 1L) {
                        throw e;
                    }
                }
                result += env.initMemoryReserve_Account(
                        this, chunks, hoome);
                result += cacheChunksExcept_Account(chunks,
                        checkAndMaybeFreeMemory, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache all
     * Grid2DSquareCellChunkAbstracts in this except that with ID a_ChunkID.
     *
     * @param chunkIDs
     * @param checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk cacheped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(
                    HashSet<Grids_2D_ID_int> chunkIDs,
                    boolean checkAndMaybeFreeMemory,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = cacheChunksExcept_AccountDetail(chunkIDs);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunkExcept_AccountDetail(chunkIDs);
                if (result.isEmpty()) {
                    env.addToNotToCache(this, chunkIDs);
                    result = env.cacheChunk_AccountDetail(env.HOOMEF);
                    if (result.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = env.initMemoryReserve_AccountDetail(
                        this, chunkIDs, hoome);
                env.combine(result, partResult);
                partResult = cacheChunksExcept_AccountDetail(
                        chunkIDs, checkAndMaybeFreeMemory,
                        hoome);
                env.combine(result, partResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache all chunks in this
     * except that with chunk IDs in chunkIDs.
     *
     * @param chunkIDs HashSet of Grids_AbstractGridChunk.ChunkIDs not to be
     * cacheped.
     * @return A map of those chunks cacheped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(
                    HashSet<Grids_2D_ID_int> chunkIDs) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> chunkIDs2 = new HashSet<>();
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (!chunkIDs.contains(chunkID)) {
                    if (isWorthCaching(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearChunk(chunkID);
                        chunkIDs2.add(chunkID);
                    }
                }
            }
        }
        result.put(this, chunkIDs2);
        return result;
    }

    /**
     * Attempt to cache to file a chunk in this grid except the chunk with
     * chunkID.
     *
     * @param chunkID
     * @param checkAndMaybeFreeMemory
     * @param hoome
     * @return 1L if a chunk was cacheped and 0 otherwise.
     */
    public final long cacheChunkExcept_Account(
            Grids_2D_ID_int chunkID,
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            long result = cacheChunkExcept_Account(chunkID);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                long result = env.cacheChunkExcept_Account(this, chunkID, false);
                if (result < 1L) {
                    result = env.cacheChunkExcept_Account(this, chunkID, false);
                    if (result < 1L) {
                        throw e;
                    }
                }
                result += env.initMemoryReserve_Account(this, chunkID, hoome);
                result += cacheChunksExcept_Account(chunkID, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to cache to file a chunk in this grid except the chunk with
     * chunkID.
     *
     * @param chunkID
     * @return 1L if a chunk was cacheped and 0 otherwise.
     */
    public final long cacheChunkExcept_Account(
            Grids_2D_ID_int chunkID) {
        int cri;
        int cci;
        Grids_2D_ID_int id2;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                id2 = new Grids_2D_ID_int(cri, cci);
                if (!chunkID.equals(id2)) {
                    if (isWorthCaching(id2)) {
                        writeToFileChunk(id2);
                        clearChunk(id2);
                        return 1L;
                    }
                }
            }
        }
        return 0L;
    }

    /**
     * Attempt to cache to file all chunks in this grid except the chunk with
     * chunkID.
     *
     * @param chunkID
     * @param hoome
     * @return A count of the number of chunks cacheped.
     */
    public final long cacheChunksExcept_Account(
            Grids_2D_ID_int chunkID,
            boolean hoome) {
        try {
            long result = cacheChunksExcept_Account(chunkID);
            env.checkAndMaybeFreeMemory(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                long result = env.cacheChunkExcept_Account(this, chunkID, false);
                if (result < 1L) {
                    result = env.cacheChunkExcept_Account(this, chunkID, false);
                    if (result < 1L) {
                        throw e;
                    }
                }
                result += env.initMemoryReserve_Account(this, chunkID, hoome);
                result += cacheChunksExcept_Account(chunkID, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to cache to file all chunks in this grid except the chunk with
     * chunkID.
     *
     * @param chunkID
     * @return A count of the number of chunks cacheped.
     */
    public final long cacheChunksExcept_Account(Grids_2D_ID_int chunkID) {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int id2;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                id2 = new Grids_2D_ID_int(cri, cci);
                if (chunkID != id2) {
                    if (isWorthCaching(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearChunk(chunkID);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    public final long cacheChunksExcept_Account(HashSet<Grids_2D_ID_int> chunkIDs) {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (!chunkIDs.contains(chunkID)) {
                    if (isWorthCaching(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearChunk(chunkID);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(
                    HashSet<Grids_2D_ID_int> chunkIDs,
                    boolean checkAndMaybeFreeMemory,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = cacheChunkExcept_AccountDetail(chunkIDs);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = cacheChunkExcept_AccountDetail(chunkIDs);
                if (result == null) {
                    if (!env.cacheChunk(env.HOOMEF)) {
                        throw e;
                    }
                }
                env.initMemoryReserve();
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param chunks
     * @return The Grids_2D_ID_int of the Grids_AbstractGridChunk cacheped or
     * null.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(
                    HashSet<Grids_2D_ID_int> chunks) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        //HashSet<ChunkID> result_ChunkID_HashSet = new HashSet<ChunkID>(1);
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (!chunks.contains(chunkID)) {
                    if (isWorthCaching(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearChunk(chunkID);
                        //result_ChunkID_HashSet.add(a_ChunkID);
                        result.put(this, chunks);
                        return result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Attempts to cache seriailsed version of all Grid2DSquareCellChunks in this
     * only. To cache seriailsed version of all Grid2DSquareCellChunks in
     * grid2DsquareCells use
     * <code>cacheToFileGrid2DSquareCellGrid2DSquareCellChunks(boolean)</code>
     * Caching involves writing to files and then clearing them from the cache.
     *
     * @param checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk cacheped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunks_AccountDetail(boolean checkAndMaybeFreeMemory,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
            r = cacheChunks_AccountDetail();
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
                r = cacheChunks_AccountDetail();
                if (r.isEmpty()) {
                    r = env.cacheChunk_AccountDetail(false);
                    if (r.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> pr;
                pr = env.initMemoryReserve_AccountDetail(hoome);
                env.combine(r, pr);
                pr = cacheChunks_AccountDetail(checkAndMaybeFreeMemory, hoome);
                env.combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache serialised version of all chunks. This involves writing
     * them to files and then clearing them from the cache.
     *
     * @return The number of Grids_AbstractGridChunk cacheped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            cacheChunks_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> r;
        r = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> chunkIDs = new HashSet<>();
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (isWorthCaching(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearChunk(chunkID);
                    chunkIDs.add(chunkID);
                }
            }
        }
        if (chunkIDs.isEmpty()) {
            return r;
        }
        r.put(this, chunkIDs);
        return r;
    }

    /**
     * Attempts to cache serialised version of all Grids_AbstractGridChunk from
     * (cri0, cci0) to (cri1, cci1) in row major order. This involves writing
     * them to files and then clearing them from the cache.
     *
     * @param cri0 The chunk row index of the first Grids_AbstractGridChunk to
     * be cacheped.
     * @param cci0 The chunk column index of the first Grids_AbstractGridChunk
     * to be cacheped.
     * @param cri1 The chunk row index of the last Grids_AbstractGridChunk to be
     * cacheped.
     * @param cci1 The chunk column index of the last Grids_AbstractGridChunk to
     * be cacheped.
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk cacheped.
     */
    public final long cacheChunks_Account(
            int cri0, int cci0, int cri1, int cci1,
            boolean hoome) {
        try {
            long r = cacheChunks_Account(cri0, cci0, cri1, cci1);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (!cacheChunk()) {
                    throw e;
                }
                long result = 1;
                env.initMemoryReserve();
                result += cacheChunks_Account(cri0, cci0, cri1, cci1, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache seriailsed version of all Grids_AbstractGridChunk from
     * (cri0, cci0) to (cri1, cci1) in row major order. This involves writing
     * them to files and then clearing them from the cache.
     *
     * @param cri0 The chunk row index of the first Grid2DSquareCellChunks to be
     * cacheped.
     * @param cci0 The chunk column index of the first Grid2DSquareCellChunks to
     * be cacheped.
     * @param cri1 The chunk row index of the last Grid2DSquareCellChunks to be
     * cacheped.
     * @param cci1 The chunk column index of the last Grid2DSquareCellChunks to
     * be cacheped.
     * @return The number of Grids_AbstractGridChunk cacheped.
     */
    public final long cacheChunks_Account(int cri0, int cci0, int cri1,
            int cci1) {
        long result = 0L;
        if (cri0 != cri1) {
            for (int cci = cci0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(cri0, cci);
                if (isWorthCaching(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearChunk(chunkID);
                    result++;
                }
            }
            for (int cri = cri0 + 1; cri < cri1; cri++) {
                for (int cci = 0; cci < NChunkCols; cci++) {
                    Grids_2D_ID_int chunkID = new Grids_2D_ID_int(cri, cci);
                    if (isWorthCaching(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearChunk(chunkID);
                        result++;
                    }
                }
            }
            for (int cci = 0; cci < cci1; cci++) {
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(cri1, cci);
                if (isWorthCaching(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearChunk(chunkID);
                    result++;
                }
            }
        } else {
            for (int cci = cci0; cci < cci1 + 1; cci++) {
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(cri0, cci);
                if (isWorthCaching(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearChunk(chunkID);
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * @return true iff chunk given by chunkID is in the cache.
     * @param chunkID The ID of the chunk tested as to whether it is in the
     * cache.
     */
    public final boolean isInCache(Grids_2D_ID_int chunkID) {
        return chunkIDChunkMap.get(chunkID) != null;
//        return chunkIDChunkMap.containsKey(chunkID);
    }

    /**
     * @return true iff the chunk given by chunkID is worth caching.
     * @param chunkID The ID of the chunk tested as to whether it is worth
     * caching.
     */
    public final boolean isWorthCaching(Grids_2D_ID_int chunkID) {
        if (isInCache(chunkID)) {
            return !isChunkSingleValueChunk(chunkID);
        }
        return false;
    }

    /**
     * For releasing a grid2DSquareCellChunk stored in memory. This is usually
     * only done after the equivalent of cacheToFileChunk(ID) has been called.
     *
     * @param chunkID The Grids_2D_ID_int of the grid2DSquareCellChunk to be
     * cleared.
     */
    public final void clearChunk(Grids_2D_ID_int chunkID) {
        chunkIDChunkMap.replace(chunkID, null);
        ChunkIDsOfChunksWorthCaching.remove(chunkID);
        //System.gc();
    }

    /**
     * For releasing all Grids_AbstractGridChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.
     */
    public final void clearChunks() {
        Iterator<Grids_2D_ID_int> ite;
        ite = chunkIDChunkMap.keySet().iterator();
        while (ite.hasNext()) {
            chunkIDChunkMap.replace(ite.next(), null);
        }
        ChunkIDsOfChunksWorthCaching = new HashSet<>();
        //System.gc();
    }

    /**
     * Attempts to load into the memory cache the chunk with chunk ID chunkID.
     *
     * @param chunkID The chunk ID of the chunk to be restored.
     */
    //public abstract void loadIntoCacheChunk(Grids_2D_ID_int chunkID);
    /**
     * @return a Grids_2D_ID_long[] - the cell IDs for cells thats centroids are
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    public final Grids_2D_ID_long[] getCellIDs(double x, double y,
            double distance) {
        return getCellIDs(x, y, getRow(y), getCol(x), distance);
    }

    /**
     * @return a Grids_2D_ID_long[] - the cell IDs for cells thats centroids
     * would be intersected by circle with centre at centroid of cell given by
     * cell row index _CellRowIndex, cell column index _CellColIndex, and radius
     * distance.
     * @param row the row index for the cell thats centroid is the circle centre
     * from which cell values are returned.
     * @param col the column index for the cell thats centroid is the circle
     * centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    public final Grids_2D_ID_long[] getCellIDs(long row, long col,
            double distance) {
        return getCellIDs(getCellXDouble(col), getCellYDouble(row), row, col,
                distance);
    }

    /**
     * TODO: remove need for copy to new array.
     *
     * @return double[] cells - the values for cells thats centroids would be
     * intersected by circle with centre at x-coordinate x, y-coordinate y, and
     * radius distance.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param row the row index at y.
     * @param col the col index at x.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     */
    public Grids_2D_ID_long[] getCellIDs(
            double x, double y, long row, long col, double distance) {
        Grids_2D_ID_long[] a_CellIDs0;
        int cellDistance = (int) Math.ceil(distance / getCellsizeDouble());
        int limit = ((2 * cellDistance) + 1) * ((2 * cellDistance) + 1);
        a_CellIDs0 = new Grids_2D_ID_long[limit];
        long p;
        long q;
        double thisX;
        double thisY;
        int count = 0;
        //if ( limit > 0 ) {
        for (p = row - cellDistance; p <= row + cellDistance; p++) {
            thisY = getCellYDouble(p);
            for (q = col - cellDistance; q <= col + cellDistance; q++) {
                thisX = getCellXDouble(q);
                if (Grids_Utilities.distance(x, y, thisX, thisY) <= distance) {
                    a_CellIDs0[count] = new Grids_2D_ID_long(p, q);
                    //cellIDs0[ count ] = new Grids_2D_ID_long( row, col );
                    count++;
                }
            }
        }
        Grids_2D_ID_long[] a_CellIDs = new Grids_2D_ID_long[count];
        System.arraycopy(a_CellIDs0, 0, a_CellIDs, 0, count);
        return a_CellIDs;
        //}
        //return _CellIDs0;
    }

    /**
     * @return Nearest cells _CellRowIndex and _CellColIndex as a long[] from ID
     * to point given by x-coordinate x, y-coordinate y.
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     */
    public Grids_2D_ID_long getNearestCellID(double x, double y) {
        return getNearestCellID(x, y, getRow(y), getCol(x));
    }

    /**
     * @return Nearest cells _CellRowIndex and _CellColIndex as a long[] from ID
     * to point given by cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param row the row index from which nearest cell Grids_2D_ID_int is
     * returned.
     * @param col the column index from which nearest cell Grids_2D_ID_int is
     * returned. TODO: return Grids_2D_ID_long[] as could be more than one
     * nearest CellID
     */
    public Grids_2D_ID_long getNearestCellID(long row, long col) {
        return getNearestCellID(getCellXDouble(col), getCellYDouble(row), row,
                col);
    }

    /**
     * @return Nearest Grids_2D_ID_long to point given by x-coordinate x,
     * y-coordinate y in position given by _CellRowIndex, _CellColIndex.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param row The cell row index of cell containing point.
     * @param col The cell column index of cell containing point.
     */
    public Grids_2D_ID_long getNearestCellID(double x, double y, long row,
            long col) {
        Grids_2D_ID_long cellID;
        boolean isInGrid = isInGrid(x, y);
        if (!isInGrid) {
            long p;
            long q;
            if (x >= Dimensions.getXMax().doubleValue()) {
                q = NCols - 1;
                if (y > Dimensions.getYMax().doubleValue()) {
                    p = 0;
                } else {
                    if (y < Dimensions.getYMin().doubleValue()) {
                        p = NRows - 1;
                    } else {
                        p = getRow(y);
                    }
                }
            } else {
                if (x < Dimensions.getXMin().doubleValue()) {
                    q = 0;
                    if (y >= Dimensions.getYMax().doubleValue()) {
                        p = 0;
                    } else {
                        if (y < Dimensions.getYMin().doubleValue()) {
                            p = NRows - 1;
                        } else {
                            p = getRow(y);
                        }
                    }
                } else {
                    q = getCol(x);
                    if (y >= Dimensions.getYMax().doubleValue()) {
                        p = 0;
                    } else {
                        p = NRows - 1;
                    }
                }
            }
            cellID = new Grids_2D_ID_long(p, q);
        } else {
            cellID = new Grids_2D_ID_long(row, col);
        }
        return cellID;
    }

    /**
     * @return Height of the grid.
     */
    public final double getHeightDouble() {
        return getHeightBigDecimal().doubleValue();
    }

    /**
     * @return Height of the grid.
     */
    public final BigDecimal getHeightBigDecimal() {
        return Dimensions.getYMax().subtract(Dimensions.getYMin());
    }

    /**
     * @return Width of the grid as a double.
     */
    public final double getWidthDouble() {
        return getWidthBigDecimal().doubleValue();
    }

    /**
     * @return Width of the grid as a BigDecimal.
     */
    public final BigDecimal getWidthBigDecimal() {
        return Dimensions.getXMax().subtract(Dimensions.getXMin());
    }

    /**
     * @return true iff point given by x-coordinate x, y-coordinate y is in the
     * Grid. Anything on the boundary is considered to be in.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public final boolean isInGrid(BigDecimal x, BigDecimal y) {
        return x.compareTo(Dimensions.getXMin()) != -1
                && y.compareTo(Dimensions.getYMin()) != -1
                && x.compareTo(Dimensions.getXMax()) != 1
                && y.compareTo(Dimensions.getYMax()) != 1;
    }

    /**
     * @return true iff point given by x-coordinate x, y-coordinate y is in the
     * Grid. Anything on the boundary is considered to be in.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public final boolean isInGrid(double x, double y) {
        return isInGrid(BigDecimal.valueOf(x), BigDecimal.valueOf(y));
    }

    /**
     * @return true iff position given by cell row index _CellRowIndex, cell
     * column index _CellColIndex is in the Grid.
     * @param row The cell row index to test.
     * @param col The cell column index to test.
     */
    public final boolean isInGrid(long row, long col) {
        return row >= 0 && row < NRows && col >= 0 && col < NCols;
//        if (row < 0) {
//            return false;
//        }
//        if (col < 0) {
//            return false;
//        }
//        if (row >= nrows) {
//            return false;
//        }
//        return col < ncols;
    }

    /**
     * @param chunkRow
     * @param chunkCol
     * @return true iff position given by chunk row index _Row, chunk column
     * index _Col is in the Grid.
     */
    public final boolean isInGrid(int chunkRow, int chunkCol) {
        return chunkRow >= 0 && chunkRow < NChunkRows && chunkCol >= 0 && chunkCol < NChunkCols;
//        if (chunkRow < 0) {
//            return false;
//        }
//        if (chunkCol < 0) {
//            return false;
//        }
//        if (chunkRow >= NChunkRows) {
//            return false;
//        }
//        return chunkCol < NChunkCols;
    }

    /**
     * @return true iff cell given by _CellID is in the Grid.
     * @param i The Grids_2D_ID_long of a cell to test.
     */
    public final boolean isInGrid(Grids_2D_ID_long i) {
        return isInGrid(i.getRow(), i.getCol());
    }

    /**
     * @return true iff Grids_2D_ID_int chunkID is in the Grid.
     * @param chunkID The Grids_2D_ID_int of a cell to test.
     */
    public final boolean isInGrid(Grids_2D_ID_int chunkID) {
        int chunkRow = chunkID.getRow();
        int chunkCol = chunkID.getCol();
        return isInGrid(chunkRow, chunkCol);
    }

    /**
     * @param chunkRow
     * @param chunkCol
     * @param cellRow
     * @param cellCol
     * @return true iff cell is in the Grid.
     */
    public final boolean isInGrid(
            int chunkRow,
            int chunkCol,
            int cellRow,
            int cellCol) {
        return isInGrid(
                getRow(chunkRow, cellRow),
                getCol(chunkCol, cellCol));
//        return isInGrid(
//                ((long) chunkRow * (long) ChunkNRows) + (long) cellRow,
//                ((long) chunkCol * (long) ChunkNCols) + (long) cellCol);
    }

    /**
     * @param col
     * @return the x-coordinate of the centroid of col as a BigDecimal.
     */
    public final BigDecimal getCellXBigDecimal(long col) {
        BigDecimal offSetFromOrigin;
        offSetFromOrigin = Dimensions.getXMin().add(
                Dimensions.getCellsize().multiply(BigDecimal.valueOf(col)));
        BigDecimal halfCellsize = Dimensions.getHalfCellsize();
        return offSetFromOrigin.add(halfCellsize);
    }

    /**
     * @param chunkCol
     * @return x-coordinate of the centroid for cells with column index
     * _CellColIndex as a double.
     * @param cellCol The cell column index thats centroid x-coordinate is
     * returned.
     */
    public final double getCellXDouble(int cellCol, int chunkCol) {
        return getCellXDouble(getCol(cellCol, chunkCol));
    }

    /**
     * @return x-coordinate of the centroid for cells with column index
     * _CellColIndex as a double.
     * @param col The cell column index thats centroid x-coordinate is returned.
     */
    public final double getCellXDouble(long col) {
        return getCellXBigDecimal(col).doubleValue();
    }

    /**
     * @return x-coordinate of the centroid for cell with cell Grids_2D_ID_int
     * _CellID as a BigDecimal.
     * @param chunkID
     */
    public final BigDecimal getCellXBigDecimal(Grids_2D_ID_long chunkID) {
        return getCellXBigDecimal(chunkID.getCol());
    }

    /**
     * @param cellID
     * @return x-coordinate of the centroid for cell with cell Grids_2D_ID_int
     * _CellID as a double
     */
    public final double getCellXDouble(Grids_2D_ID_long cellID) {
        return getCellXBigDecimal(cellID).doubleValue();
    }

    /**
     * @param row
     * @return y-coordinate of the centroid for row as a BigDecimal.
     */
    public final BigDecimal getCellYBigDecimal(long row) {
        BigDecimal offSetFromOrigin;
        offSetFromOrigin = Dimensions.getYMin().add(
                Dimensions.getCellsize().multiply(BigDecimal.valueOf(row)));
        BigDecimal halfCellsize = Dimensions.getHalfCellsize();
        return offSetFromOrigin.add(halfCellsize);
    }

    /**
     * @param chunkRow
     * @return y-coordinate of the centroid for cell given by cellRow, chunkRow.
     * @param cellRow the chunk cell column index thats centroid y-coordinate is
     * returned.
     */
    public final double getCellYDouble(int cellRow, int chunkRow) {
        return getCellYDouble(getRow(cellRow, chunkRow));
    }

    /**
     * @param row
     * @return y-coordinate of the centroid for cells with row index
     * _CellRowIndex as a double.
     */
    public final double getCellYDouble(long row) {
        return getCellYBigDecimal(row).doubleValue();
    }

    /**
     * @param chunkID
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long
     * _CellID as a BigDecimal.
     */
    public final BigDecimal getCellYBigDecimal(Grids_2D_ID_long chunkID) {
        return getCellYBigDecimal(chunkID.getRow());
    }

    /**
     * @param chunkID
     * @return the y-coordinate of the centroid of cell with chunkID
     * Grids_2D_ID_long as a double.
     */
    public final double getCellYDouble(Grids_2D_ID_long chunkID) {
        return getCellYBigDecimal(chunkID).doubleValue();
    }

    /**
     * @return gridBounds (the bounding box of the grid) as a double[] where;
     * gridBounds[0] xmin, left most x-coordinate of this gridBounds[1] ymin,
     * lowest y-coordinate of this gridBounds[2] xmax, right most x-coordinate
     * of this gridBounds[3] ymax, highest y-coordinate of this TODO: Are bounds
     * in double range? Is there more than cellsize difference with precision?
     * Throw appropriate exceptions.
     */
    public final double[] getGridBounds() {
        double[] result = new double[4];
        result[0] = Dimensions.getXMin().doubleValue();
        result[1] = Dimensions.getYMin().doubleValue();
        result[2] = Dimensions.getXMax().doubleValue();
        result[3] = Dimensions.getYMax().doubleValue();
        return result;
    }

    /**
     * @param halfCellsize
     * @param col
     * @return double[] where; double[0] xmin, left most x-coordinate of cell at
     * (rowIndex,colIndex) double[1] ymin, lowest y-coordinate of cell at
     * (rowIndex,colIndex) double[2] xmax, right most x-coordinate of cell at
     * (rowIndex,colIndex) double[3] ymax, highest y-coordinate of cell at
     * (rowIndex,colIndex)
     * @param row the row index of the cell for which the bounds are returned
     */
    public final double[] getCellBoundsDoubleArray(double halfCellsize,
            long row, long col) {
        return getCellBoundsDoubleArray(halfCellsize, getCellXDouble(col),
                getCellYDouble(row));
    }

    /**
     * Precision may compromise result. More precision is available via
     * BigDecimal arithmetic.
     *
     * @param halfCellsize
     * @return double[] where; double[0] xmin, left most x-coordinate of cell
     * that intersects point at (x,y) double[1] ymin, lowest y-coordinate of
     * cell that intersects point at (x,y) double[2] xmax, right most
     * x-coordinate of cell that intersects point at (x,y) double[3] ymax,
     * highest y-coordinate of cell that intersects point at (x,y)
     * @param x the x-coordinate in the cell for which the bounds are returned
     * @param y the y-coordinate in the cell for which the bounds are returned
     */
    public final double[] getCellBoundsDoubleArray(double halfCellsize,
            double x, double y) {
        double[] cellBounds = new double[4];
        cellBounds[0] = x - halfCellsize;
        cellBounds[1] = y - halfCellsize;
        cellBounds[2] = x + halfCellsize;
        cellBounds[3] = y + halfCellsize;
        return cellBounds;
    }

    /**
     * @param halfCellsize
     * @return BigDecimal[] cellBounds_BigDecimalArray;
     * cellBounds_BigDecimalArray[0] xmin, left most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[1] ymin, lowest
     * y-coordinate of cell that intersects point at (x,y)
     * cellBounds_BigDecimalArray[2] xmax, right most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[3] ymax, highest
     * y-coordinate of cell that intersects point at (x,y)
     * @param x the centroid x-coordinate of the cell for which the bounds are
     * returned.
     * @param y the centroid y-coordinate of the cell for which the bounds are
     * returned.
     */
    public final Grids_Dimensions getCellDimensions(
            BigDecimal halfCellsize, BigDecimal x, BigDecimal y) {
        Grids_Dimensions result;
        result = new Grids_Dimensions(x.subtract(halfCellsize),
                x.add(halfCellsize), y.subtract(halfCellsize),
                y.add(halfCellsize), getCellsize());
        return result;
    }

    /**
     * @param halfCellsize
     * @param row
     * @param col
     * @return BigDecimal[] cellBounds_BigDecimalArray;
     * cellBounds_BigDecimalArray[0] xmin, left most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[1] ymin, lowest
     * y-coordinate of cell that intersects point at (x,y)
     * cellBounds_BigDecimalArray[2] xmax, right most x-coordinate of cell that
     * intersects point at (x,y) cellBounds_BigDecimalArray[3] ymax, highest
     * y-coordinate of cell that intersects point at (x,y)
     */
    public final Grids_Dimensions getCellDimensions(
            BigDecimal halfCellsize, long row, long col) {
        return getCellDimensions(halfCellsize, getCellXBigDecimal(col),
                getCellYBigDecimal(row));
    }

    /**
     * @return the next Grids_2D_ID_int in row major order from _ChunkID, or
     * null.
     * @param chunkID
     * @param nChunkRows
     * @param nChunkCols
     */
    public Grids_2D_ID_int getNextChunk(Grids_2D_ID_int chunkID, int nChunkRows,
            int nChunkCols) {
        int chunkRow = chunkID.getRow();
        int chunkCol = chunkID.getCol();
        if (chunkCol < nChunkCols - 1) {
            return new Grids_2D_ID_int(chunkRow, chunkCol + 1);
        } else {
            if (chunkRow < nChunkRows - 1) {
                return new Grids_2D_ID_int(chunkRow + 1, 0);
            }
        }
        return null;
    }

    /**
     * @param chunkID
     * @param nChunkCols
     * @param nChunkRows
     * @return the next Grids_2D_ID_int in row major order from this, or null.
     */
    public Grids_2D_ID_int getPreviousChunk(Grids_2D_ID_int chunkID,
            int nChunkRows, int nChunkCols) {
        int chunkRow = chunkID.getRow();
        int chunkCol = chunkID.getCol();
        if (chunkCol > 0) {
            return new Grids_2D_ID_int(chunkRow, chunkCol - 1);
        } else {
            if (chunkRow > 0) {
                return new Grids_2D_ID_int(chunkRow - 1, 0);
            }
        }
        return null;
    }

    /**
     *
     * @param chunksNotToCacheToFile
     * @param e
     */
    public void freeSomeMemoryAndResetReserve(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> chunksNotToCacheToFile,
            OutOfMemoryError e) {
        Grids_AbstractGrid g;
        HashSet<Grids_2D_ID_int> chunkIDs;
        Iterator<Grids_AbstractGrid> ite;
        ite = chunksNotToCacheToFile.keySet().iterator();
        while (ite.hasNext()) {
            g = ite.next();
            chunkIDs = chunksNotToCacheToFile.get(g);
            if (env.cacheChunkExcept_Account(g, chunkIDs, false) > 0) {
                env.initMemoryReserve(chunksNotToCacheToFile, env.HOOMET);
                return;
            }
        }
        throw e;
    }

    public void freeSomeMemoryAndResetReserve(
            int chunkRow, int chunkCol, OutOfMemoryError e) {
        //env.clearMemoryReserve();
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
        freeSomeMemoryAndResetReserve(chunkID, e);
    }

    public void freeSomeMemoryAndResetReserve(
            HashSet<Grids_2D_ID_int> chunkIDs, OutOfMemoryError e) {
        if (env.cacheChunkExcept_Account(this, chunkIDs, false) < 1L) {
            throw e;
        }
        env.initMemoryReserve(this, chunkIDs, env.HOOMET);
    }

    public void freeSomeMemoryAndResetReserve(
            Grids_2D_ID_int chunkID, OutOfMemoryError e) {
        if (env.cacheChunkExcept_Account(this, false) < 1L) {
            if (env.cacheChunkExcept_Account(this, chunkID, false) < 1L) {
                throw e;
            }
        }
        env.initMemoryReserve(this, chunkID, env.HOOMET);
    }

    public void freeSomeMemoryAndResetReserve(
            boolean hoome, OutOfMemoryError e) {
        if (!env.cacheChunk(env.HOOMEF)) {
            throw e;
        }
        env.initMemoryReserve();
    }

    public void freeSomeMemoryAndResetReserve(OutOfMemoryError e) {
        if (env.cacheChunkExcept_Account(this, false) < 1L) {
            throw e;
        }
        env.initMemoryReserve(this, env.HOOMET);
    }

    /**
     * @return the chunkIDChunkMap
     */
    public TreeMap<Grids_2D_ID_int, Grids_AbstractGridChunk> getChunkIDChunkMap() {
        return chunkIDChunkMap;
    }

    public void initDimensions(Grids_ESRIAsciiGridHeader header,
            long startRowIndex, long startColIndex) {
        BigDecimal xMin;
        BigDecimal yMin;
        BigDecimal xMax;
        BigDecimal yMax;
        BigDecimal cellsize;
        cellsize = header.cellsize;
        xMin = ((BigDecimal) header.xll).add(cellsize.multiply(new BigDecimal(startColIndex)));
        yMin = ((BigDecimal) header.yll).add(cellsize.multiply(new BigDecimal(startRowIndex)));
        xMax = xMin.add(new BigDecimal(Long.toString(NCols)).multiply(cellsize));
        yMax = yMin.add(new BigDecimal(Long.toString(NRows)).multiply(cellsize));
        Dimensions = new Grids_Dimensions(xMin, xMax, yMin, yMax, cellsize);
    }

    /**
     * Assumes nrows and ncols are already initialised.
     *
     * @param g
     * @param startRowIndex
     * @param startColIndex
     */
    public void initDimensions(Grids_AbstractGrid g, long startRowIndex,
            long startColIndex) {
        Dimensions = g.getDimensions(); // temporary assignment
        BigDecimal startColIndexBigDecimal = new BigDecimal((long) startColIndex);
        BigDecimal startRowIndexBigDecimal = new BigDecimal((long) startRowIndex);
        BigDecimal nRowsBigDecimal = new BigDecimal(NRows);
        BigDecimal nColsBigDecimal = new BigDecimal(NCols);
        BigDecimal xMin;
        BigDecimal yMin;
        BigDecimal xMax;
        BigDecimal yMax;
        BigDecimal cellsize;
        if (Dimensions == null) {
            cellsize = BigDecimal.ONE;
            xMin = startColIndexBigDecimal;
            yMin = startRowIndexBigDecimal;
            xMax = xMin.add(nColsBigDecimal);
            yMax = yMin.add(nRowsBigDecimal);
        } else {
            cellsize = Dimensions.getCellsize();
            xMin = Dimensions.getXMin().add(startColIndexBigDecimal.multiply(cellsize));
            yMin = Dimensions.getYMin().add(startRowIndexBigDecimal.multiply(cellsize));
            xMax = Dimensions.getXMin().add(nColsBigDecimal.multiply(cellsize));
            yMax = Dimensions.getYMin().add(nRowsBigDecimal.multiply(cellsize));
        }
        Dimensions = new Grids_Dimensions(xMin, xMax, yMin, yMax, cellsize);
    }

    /**
     * @return Grids_AbstractGridChunk for the given chunkID.
     * @param chunkID
     */
    public abstract Grids_AbstractGridChunk getChunk(Grids_2D_ID_int chunkID);

    /**
     * Attempts to load into the memory cache the chunk with chunk ID chunkID.
     *
     * @param chunkID The chunk ID of the chunk to be restored.
     */
    public void loadIntoCacheChunk(Grids_2D_ID_int chunkID) {
        boolean isInCache = isInCache(chunkID);
        if (!isInCache) {
            File f = new File(getDirectory(),
                    "" + chunkID.getRow() + "_" + chunkID.getCol());
            if (f.exists()) {
                //System.out.println("Loading chunk from file" + f);
                Object o = env.env.io.readObject(f);
                Grids_AbstractGridChunk chunk = (Grids_AbstractGridChunk) o;
                chunk.env = env;
                chunk.initGrid(this);
                chunk.initChunkID(chunkID);
                chunkIDChunkMap.put(chunkID, chunk);
            } else {
                /**
                 * It is assumed that the chunk is all noDataValues so if this
                 * is called in a process which is attempting to set a value,
                 * then the chunk and value should be created without trying to
                 * load from the file.
                 */
            }
        }
    }
//    /**
//     * @param chunkRow
//     * @param chunkCol
//     * @return Grids_AbstractGridChunk.
//     */
//    public abstract Grids_AbstractGridChunk getChunk(int chunkRow, int chunkCol);

    /**
     * @param chunkRow
     * @param chunkCol
     * @return Grids_AbstractGridChunkDouble.
     */
    public final Grids_AbstractGridChunk getChunk(int chunkRow,            int chunkCol) {
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
        return getChunk(chunkID);
    }

    /**
     * @param chunkID
     * @param chunkRow
     * @param chunkCol
     * @return Grids_AbstractGridChunk.
     */
    public abstract Grids_AbstractGridChunk getChunk(Grids_2D_ID_int chunkID, 
            int chunkRow, int chunkCol);

    public abstract Grids_AbstractGridStats getStats();

    /**
     * @return An Iterator for iterating over the cell values in this.
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final Iterator iterator(
            boolean hoome) {
        try {
            Iterator r = iterator();
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(hoome, e);
                return iterator(hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param hoome
     * @return this._GridStatistics TODO: For safety, this method should either
     * be removed and this class be made implement GridStatisticsInterface. This
     * done the methods introduced would be made to call the relevant ones in
     * this._GridStatistics. Or the _GridStatistics need to be made safe in that
     * only copies of fields are passed.
     */
    public Grids_AbstractGridStats getStats(boolean hoome) {
        try {
            Grids_AbstractGridStats r = getStats();
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(hoome, e);
                return getStats(hoome);
            } else {
                throw e;
            }
        }
    }

}
