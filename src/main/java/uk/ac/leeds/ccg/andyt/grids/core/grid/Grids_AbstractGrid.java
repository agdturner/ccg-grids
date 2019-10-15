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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.leeds.ccg.andyt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_AbstractGridStats;
import uk.ac.leeds.ccg.andyt.grids.io.Grids_ESRIAsciiGridImporter.Grids_ESRIAsciiGridHeader;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGrid extends Grids_Object
        implements Grids_InterfaceGrid {

    //    /**
    //     * A version number for confidence in reloading serialised instances.
    //     */
    //    private static final long serialVersionUID = 1L;
    /**
     * Directory used for storing grid data.
     */
    protected File Directory;
    /**
     * The Grids_AbstractGridChunk data cache.
     */
    protected TreeMap<Grids_2D_ID_int, Grids_AbstractGridChunk> ChunkIDChunkMap;
    /**
     * The Grids_AbstractGridChunk data cache.
     */
    protected HashSet<Grids_2D_ID_int> ChunkIDsOfChunksWorthSwapping;
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
     * Stores the cellsize, minx, miny, maxx, maxy.
     */
    protected Grids_Dimensions Dimensions;

    /**
     * A reference to the grid stats.
     */
    protected Grids_AbstractGridStats stats;

    protected Grids_AbstractGrid() {
    }

    protected Grids_AbstractGrid(Grids_Environment ge, File dir) {
        super(ge);
        Directory = dir;
    }

    protected final void checkDir() {
        if (Directory.exists()) {
            if (Directory.isDirectory()) {
                File lock;
                lock = new File(Directory, "lock");
                if (!lock.exists()) {
                    try {
                        lock.createNewFile();
                    } catch (IOException ex) {
                        Logger.getLogger(Grids_AbstractGrid.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    throw new Error("Lock file " + lock + " already exists. "
                            + "Exiting program to prevent data getting overwritten!");
                }
            } else {
                throw new Error("Directory " + Directory + " already exists as a file. "
                        + "Exiting program to prevent data getting overwritten!");
            }
        }
    }

    protected void init() {
        Directory.mkdir();
        env.setDataToSwap(true);
        env.addGrid(this);
    }

    /**
     * Initialises non transient Grids_AbstractGrid fields from g.
     *
     * @param g The Grids_AbstractGrid from which the non transient
     * Grids_AbstractGrid fields of this are set.
     */
    protected void init(Grids_AbstractGrid g) {
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
     *
     */
    protected void setReferenceInChunkIDChunkMap() {
        Iterator<Grids_2D_ID_int> ite = ChunkIDChunkMap.keySet().iterator();
        Grids_2D_ID_int chunkID;
        Grids_AbstractGridChunk chunk;
        while (ite.hasNext()) {
            chunkID = ite.next();
            chunk = ChunkIDChunkMap.get(chunkID);
            chunk.setGrid(this);
        }
    }

    /**
     * @return HashSet containing all ChunkIDs.
     */
    public HashSet<Grids_2D_ID_int> getChunkIDs() {
        HashSet<Grids_2D_ID_int> result = new HashSet<>();
        result.addAll(ChunkIDChunkMap.keySet());
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
        r += "Directory=" + Directory + ", ";
        r += "Name=" + Name + ", ";
        r += "Dimensions=" + getDimensions().toString() + ", ";
        if (ChunkIDChunkMap == null) {
            r += "ChunkIDChunkMap=null, ";
        } else {
            r += "ChunkIDChunkMap.size()=" + ChunkIDChunkMap.size() + ", ";
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
     * @return Directory.
     */
    public File getDirectory() {
        return new File(Directory.getPath());
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
     * @return NCols.
     */
    public final long getNCols() {
        return NCols;
    }

    /**
     * Beware OutOfMemoryErrors calling this method.
     *
     * @return NRows.
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
     * Attempts to write this instance to files located in the Directory
 returned by getDirectory().
     */
    public void writeToFile() {
        File dir = getDirectory();
        dir.mkdirs();
        File f = new File(dir, "thisFile");
        env.env.io.writeObject(this, f);
    }

    /**
     * Attempts to write this instance to Files located in the _Directory
     * returned by getDirectory(). Chunks are all swapped to file.
     */
    public void writeToFileSwapping() {
        swapChunks();
        writeToFile();
    }

    /**
     * Attempts to write to file a chunk in ChunkIDChunkMap. The chunks are
     * iterated through in row major order. Only chunks that are not single
     * values chunks are written out. This method is low level and does not
     * consider ge,NotToSwap. Other handling is required prior to calling this
     * method in order to first prefer to not to swap those chunks in
     * ge.NotToSwap.
     *
     * @return Grids_2D_ID_int of the chunk which was swapped or null if there
     * are no suitable chunks to swap.
     */
    public final Grids_2D_ID_int writeToFileChunk() {

        if (ChunkIDsOfChunksWorthSwapping == null) {
            int debug = 1;
        }

        if (ChunkIDsOfChunksWorthSwapping.isEmpty()) {
            return null;
        }
        Grids_2D_ID_int chunkID;
        Iterator<Grids_2D_ID_int> ite;
        ite = ChunkIDsOfChunksWorthSwapping.iterator();
        while (ite.hasNext()) {
            chunkID = ite.next();
            writeToFileChunk(chunkID);
            return chunkID;
        }
//        if (ChunkIDChunkMap.isEmpty()) {
//            return null;
//        }
//        Grids_2D_ID_int chunkID;
//        Iterator<Grids_2D_ID_int> ite;
//        ite = ChunkIDChunkMap.keySet().iterator();
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
        gridChunk = ChunkIDChunkMap.get(chunkID);
        if (gridChunk != null) {
            if (!gridChunk.isSwapUpToDate()) {
                File file = new File(getDirectory(),
                        chunkID.getRow() + "_" + chunkID.getCol());
                file.getParentFile().mkdirs();
                env.env.io.writeObject(gridChunk, file);
                //System.gc();
                gridChunk.setSwapUpToDate(true);
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Attempts to write to File serialised versions of all chunks in
     * ChunkIDChunkMap that are not single valued chunks.
     */
    public final void writeToFileChunks() {
        Iterator ite;
        ite = ChunkIDsOfChunksWorthSwapping.iterator();
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            chunkID = (Grids_2D_ID_int) ite.next();
            writeToFileChunk(chunkID);
        }
//        ite = ChunkIDChunkMap.keySet().iterator();
//        Grids_2D_ID_int chunkID;
//        while (ite.hasNext()) {
//            chunkID = (Grids_2D_ID_int) ite.next();
//            if (isWorthSwapping(chunkID)) {
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
            if (isWorthSwapping(id)) {
                writeToFileChunk(id);
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache a chunk in this. This
     * is one of the lowest level memory handling operation of this class.
     *
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return An account of what was swapped.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunk_AccountDetail(
            boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunk_AccountDetail();
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
            partResult = env.checkAndMaybeFreeMemory_AccountDetail(hoome);
            env.combine(result, partResult);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunk_AccountDetail();
                if (result.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = env.initMemoryReserve_AccountDetail(hoome);
                env.combine(result, partResult);
                partResult = swapChunk_AccountDetail(hoome);
                env.combine(result, partResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> swapChunk_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        Grids_2D_ID_int chunkID = writeToFileChunk();
        if (chunkID != null) {
            HashSet<Grids_2D_ID_int> chunks = new HashSet<>(1);
            clearFromCacheChunk(chunkID);
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
    public Grids_2D_ID_int swapChunk_AccountChunk(
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            Grids_2D_ID_int result;
            result = swapChunk_AccountChunk();
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int result;
                result = swapChunk_AccountChunk();
                if (result == null) {
                    if (!env.swapChunk(env.HOOMEF)) {
                        throw e;
                    }
                }
                env.initMemoryReserve();
                return swapChunk_AccountChunk(
                        checkAndMaybeFreeMemory, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to swap a chunk and return true if swapped and false otherwise.
     * This will first try to swap a chunk not in ge.NotToSwap.
     *
     * @return
     */
    public Grids_2D_ID_int swapChunk_AccountChunk() {
        Grids_2D_ID_int id;
        id = writeToFileChunk();
        if (id != null) {
            clearFromCacheChunk(id);
        }
        return id;
    }

    public long swapChunks_Account() {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int id;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                id = new Grids_2D_ID_int(cri, cci);
                if (writeToFileChunk(id)) {
                    clearFromCacheChunk(id);
                    result++;
                }
            }
        }
        return result;
    }

    public long swapChunks_Account(Set<Grids_2D_ID_int> chunkIDs) {
        long result = 0L;
        Iterator<Grids_2D_ID_int> ite = chunkIDs.iterator();
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            chunkID = ite.next();
            if (writeToFileChunk(chunkID)) {
                clearFromCacheChunk(chunkID);
                result++;
            }
        }
        return result;
    }

    public Grids_2D_ID_int swapChunkExcept_AccountChunk(
            HashSet<Grids_2D_ID_int> chunkIDs,
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            Grids_2D_ID_int result;
            result = swapChunkExcept_AccountChunk(chunkIDs);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int result;
                result = swapChunkExcept_AccountChunk(chunkIDs);
                if (result == null) {
                    if (!env.swapChunk(env.HOOMEF)) {
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

    public Grids_2D_ID_int swapChunkExcept_AccountChunk(
            HashSet<Grids_2D_ID_int> chunkIDs) {
        Grids_2D_ID_int chunkID = null;
//        int chunkRow;
//        int chunkCol;
        Iterator<Grids_2D_ID_int> ite;
        ite = ChunkIDsOfChunksWorthSwapping.iterator();
        while (ite.hasNext()) {
            chunkID = ite.next();
            if (!chunkIDs.contains(chunkID)) {
                writeToFileChunk(chunkID);
                clearFromCacheChunk(chunkID);
                return chunkID;
            }
        }
//        for (chunkRow = 0; chunkRow < NChunkRows; chunkRow++) {
//            for (chunkCol = 0; chunkCol < NChunkCols; chunkCol++) {
//                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
//                if (!chunkIDs.contains(chunkID)) {
//                    if (isWorthSwapping(chunkID)) {
//                        writeToFileChunk(chunkID);
//                        clearFromCacheChunk(chunkID);
//                        return chunkID;
//                    }
//                }
//            }
//        }
        return chunkID;
    }

    /**
     * Returns true if chunk is a single value (which tends not to be worth
     * swapping).
     *
     * @param chunkID
     * @return
     */
    public boolean isChunkSingleValueChunk(Grids_2D_ID_int chunkID) {
        return ChunkIDChunkMap.get(chunkID) instanceof Grids_GridChunkDouble
                || ChunkIDChunkMap.get(chunkID) instanceof Grids_GridChunkInt;
    }

    /**
     * Swaps the chunk with chunkID to file.
     *
     * @param chunkID
     * @param checkAndMaybeFreeMemory
     * @param hoome
     */
    public void swapChunk(
            Grids_2D_ID_int chunkID,
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            swapChunk(chunkID);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(hoome, e);
                swapChunk(chunkID, checkAndMaybeFreeMemory,
                        hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Swaps the chunk with chunkID to file. This will return true if the chunk
     * is swapped and false otherwise. It is not sensible to swap some types of
     * chunk and in these cases false is returned.
     *
     * @param chunkID
     * @return
     */
    public boolean swapChunk(Grids_2D_ID_int chunkID) {
        if (writeToFileChunk(chunkID)) {
            clearFromCacheChunk(chunkID);
            return true;
        }
        return false;
    }

    /**
     * Attempts to write to file and clear from the cache any chunk in this.
     * This is one of the lowest level memory handling operation of this class.
     *
     * @param checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public boolean swapChunk(
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            boolean result = swapChunk();
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(hoome, e);
                return swapChunk(checkAndMaybeFreeMemory, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to swap a chunk and return true if swapped and false otherwise.
     * This will first try to swap a chunk not in ge.NotToSwap.
     *
     * @return
     */
    public boolean swapChunk() {
        Grids_2D_ID_int chunkID;
        chunkID = writeToFileChunk();
        if (chunkID != null) {
            clearFromCacheChunk(chunkID);
            return true;
        }
        return false;
    }

    /**
     * Attempts to write to file and clear from the cache a
     * Grids_AbstractGridChunk in this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param chunkID A Grids_2D_ID_int not to be swapped
     * @param checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The Grids_2D_ID_int of Grids_AbstractGridChunk swapped or null.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(
                    Grids_2D_ID_int chunkID,
                    boolean checkAndMaybeFreeMemory,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunkExcept_AccountDetail(chunkID);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(chunkID);
                if (result.isEmpty()) {
                    result = env.swapChunkExcept_AccountDetail(
                            this, chunkID, false);
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = env.initMemoryReserve_AccountDetail(this, chunkID, hoome);
                env.combine(result, partResult);
                partResult = swapChunkExcept_AccountDetail(chunkID);
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
     * @return The Grids_2D_ID_int of Grids_AbstractGridChunk swapped or null.
     */
    public HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        int cri;
        int cci;
        Grids_2D_ID_int bid;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                bid = new Grids_2D_ID_int(cri, cci);
                if (!bid.equals(chunkID)) {
                    if (isWorthSwapping(bid)) {
                        writeToFileChunk(bid);
                        clearFromCacheChunk(bid);
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

    public Grids_2D_ID_int swapChunkExcept_AccountChunk(
            Grids_2D_ID_int chunkID,
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            Grids_2D_ID_int result = swapChunkExcept_AccountChunk(chunkID);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int result;
                result = swapChunkExcept_AccountChunk(chunkID);
                if (result == null) {
                    if (env.swapChunkExcept_Account(this, chunkID, false) < 1L) {
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

    public Grids_2D_ID_int swapChunkExcept_AccountChunk(
            Grids_2D_ID_int chunkID) {
        Grids_2D_ID_int result = null;
        int cri;
        int cci;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                result = new Grids_2D_ID_int(cri, cci);
                if (!result.equals(chunkID)) {
                    if (isWorthSwapping(result)) {
                        writeToFileChunk(result);
                        clearFromCacheChunk(result);
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
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(
                    Grids_2D_ID_int chunkID,
                    boolean checkAndMaybeFreeMemory,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunksExcept_AccountDetail(chunkID);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(chunkID);
                if (result.isEmpty()) {
                    result = env.swapChunkExcept_AccountDetail(
                            this, chunkID, false);
                    if (result.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = env.initMemoryReserve_AccountDetail(
                        this, chunkID, hoome);
                env.combine(result, partResult);
                partResult = swapChunksExcept_AccountDetail(
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
     * swapped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(Grids_2D_ID_int chunkID) {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> chunksSwapped = new HashSet<>();
        int chunkRow;
        int chunkCol;
        Grids_2D_ID_int chunkIDToSwap;
        for (chunkRow = 0; chunkRow < NChunkRows; chunkRow++) {
            for (chunkCol = 0; chunkCol < NChunkCols; chunkCol++) {
                chunkIDToSwap = new Grids_2D_ID_int(chunkRow, chunkCol);
                if (!chunkID.equals(chunkIDToSwap)) {
                    if (isWorthSwapping(chunkIDToSwap)) {
                        writeToFileChunk(chunkIDToSwap);
                        clearFromCacheChunk(chunkIDToSwap);
                        chunksSwapped.add(chunkIDToSwap);
                    }
                }
            }
        }
        result.put(this, chunksSwapped);
        return result;
    }

    public void swapChunks(boolean hoome) {
        try {
            swapChunks();
            env.checkAndMaybeFreeMemory(hoome);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(hoome, e);
                swapChunks(hoome);
            } else {
                throw e;
            }
        }
    }

    public void swapChunks() {
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (isWorthSwapping(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearFromCacheChunk(chunkID);
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
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public long swapChunksExcept_Account(
            HashSet<Grids_2D_ID_int> chunks,
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            long result = swapChunksExcept_Account(chunks);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                long result = env.swapChunkExcept_Account(this, chunks, false);
                if (result < 1L) {
                    result = env.swapChunkExcept_Account(this, chunks, false);
                    if (result < 1L) {
                        throw e;
                    }
                }
                result += env.initMemoryReserve_Account(
                        this, chunks, hoome);
                result += swapChunksExcept_Account(chunks,
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
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(
                    HashSet<Grids_2D_ID_int> chunkIDs,
                    boolean checkAndMaybeFreeMemory,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunksExcept_AccountDetail(chunkIDs);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(chunkIDs);
                if (result.isEmpty()) {
                    env.addToNotToSwap(this, chunkIDs);
                    result = env.swapChunk_AccountDetail(env.HOOMEF);
                    if (result.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = env.initMemoryReserve_AccountDetail(
                        this, chunkIDs, hoome);
                env.combine(result, partResult);
                partResult = swapChunksExcept_AccountDetail(
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
     * swapped.
     * @return A map of those chunks swapped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunksExcept_AccountDetail(
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
                    if (isWorthSwapping(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearFromCacheChunk(chunkID);
                        chunkIDs2.add(chunkID);
                    }
                }
            }
        }
        result.put(this, chunkIDs2);
        return result;
    }

    /**
     * Attempt to swap to file a chunk in this grid except the chunk with
     * chunkID.
     *
     * @param chunkID
     * @param checkAndMaybeFreeMemory
     * @param hoome
     * @return 1L if a chunk was swapped and 0 otherwise.
     */
    public final long swapChunkExcept_Account(
            Grids_2D_ID_int chunkID,
            boolean checkAndMaybeFreeMemory,
            boolean hoome) {
        try {
            long result = swapChunkExcept_Account(chunkID);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                long result = env.swapChunkExcept_Account(this, chunkID, false);
                if (result < 1L) {
                    result = env.swapChunkExcept_Account(this, chunkID, false);
                    if (result < 1L) {
                        throw e;
                    }
                }
                result += env.initMemoryReserve_Account(this, chunkID, hoome);
                result += swapChunksExcept_Account(chunkID, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to swap to file a chunk in this grid except the chunk with
     * chunkID.
     *
     * @param chunkID
     * @return 1L if a chunk was swapped and 0 otherwise.
     */
    public final long swapChunkExcept_Account(
            Grids_2D_ID_int chunkID) {
        int cri;
        int cci;
        Grids_2D_ID_int id2;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                id2 = new Grids_2D_ID_int(cri, cci);
                if (!chunkID.equals(id2)) {
                    if (isWorthSwapping(id2)) {
                        writeToFileChunk(id2);
                        clearFromCacheChunk(id2);
                        return 1L;
                    }
                }
            }
        }
        return 0L;
    }

    /**
     * Attempt to swap to file all chunks in this grid except the chunk with
     * chunkID.
     *
     * @param chunkID
     * @param hoome
     * @return A count of the number of chunks swapped.
     */
    public final long swapChunksExcept_Account(
            Grids_2D_ID_int chunkID,
            boolean hoome) {
        try {
            long result = swapChunksExcept_Account(chunkID);
            env.checkAndMaybeFreeMemory(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                long result = env.swapChunkExcept_Account(this, chunkID, false);
                if (result < 1L) {
                    result = env.swapChunkExcept_Account(this, chunkID, false);
                    if (result < 1L) {
                        throw e;
                    }
                }
                result += env.initMemoryReserve_Account(this, chunkID, hoome);
                result += swapChunksExcept_Account(chunkID, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to swap to file all chunks in this grid except the chunk with
     * chunkID.
     *
     * @param chunkID
     * @return A count of the number of chunks swapped.
     */
    public final long swapChunksExcept_Account(Grids_2D_ID_int chunkID) {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int id2;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                id2 = new Grids_2D_ID_int(cri, cci);
                if (chunkID != id2) {
                    if (isWorthSwapping(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearFromCacheChunk(chunkID);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    public final long swapChunksExcept_Account(HashSet<Grids_2D_ID_int> chunkIDs) {
        long result = 0L;
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (!chunkIDs.contains(chunkID)) {
                    if (isWorthSwapping(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearFromCacheChunk(chunkID);
                        result++;
                    }
                }
            }
        }
        return result;
    }

    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(
                    HashSet<Grids_2D_ID_int> chunkIDs,
                    boolean checkAndMaybeFreeMemory,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunkExcept_AccountDetail(chunkIDs);
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunkExcept_AccountDetail(chunkIDs);
                if (result == null) {
                    if (!env.swapChunk(env.HOOMEF)) {
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
     * @return The Grids_2D_ID_int of the Grids_AbstractGridChunk swapped or
     * null.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunkExcept_AccountDetail(
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
                    if (isWorthSwapping(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearFromCacheChunk(chunkID);
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
     * Attempts to swap seriailsed version of all Grid2DSquareCellChunks in this
     * only. To swap seriailsed version of all Grid2DSquareCellChunks in
     * grid2DsquareCells use
     * <code>swapToFileGrid2DSquareCellGrid2DSquareCellChunks(boolean)</code>
     * Swapping involves writing to files and then clearing them from the cache.
     *
     * @param checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunks_AccountDetail(
                    boolean checkAndMaybeFreeMemory,
                    boolean hoome) {
        try {
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
            result = swapChunks_AccountDetail();
            if (checkAndMaybeFreeMemory) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
                result = swapChunks_AccountDetail();
                if (result.isEmpty()) {
                    result = env.swapChunk_AccountDetail(false);
                    if (result.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> partResult;
                partResult = env.initMemoryReserve_AccountDetail(hoome);
                env.combine(result, partResult);
                partResult = swapChunks_AccountDetail(checkAndMaybeFreeMemory, hoome);
                env.combine(result, partResult);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap serialised version of all chunks. This involves writing
     * them to files and then clearing them from the cache.
     *
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public final HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>>
            swapChunks_AccountDetail() {
        HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> result;
        result = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> chunkIDs = new HashSet<>();
        int cri;
        int cci;
        Grids_2D_ID_int chunkID;
        for (cri = 0; cri < NChunkRows; cri++) {
            for (cci = 0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri, cci);
                if (isWorthSwapping(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearFromCacheChunk(chunkID);
                    chunkIDs.add(chunkID);
                }
            }
        }
        if (chunkIDs.isEmpty()) {
            return result;
        }
        result.put(this, chunkIDs);
        return result;
    }

    /**
     * Attempts to swap serialised version of all Grids_AbstractGridChunk from
     * (cri0, cci0) to (cri1, cci1) in row major order. This involves writing
     * them to files and then clearing them from the cache.
     *
     * @param cri0 The chunk row index of the first Grids_AbstractGridChunk to
     * be swapped.
     * @param cci0 The chunk column index of the first Grids_AbstractGridChunk
     * to be swapped.
     * @param cri1 The chunk row index of the last Grids_AbstractGridChunk to be
     * swapped.
     * @param cci1 The chunk column index of the last Grids_AbstractGridChunk to
     * be swapped.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public final long swapChunks_Account(
            int cri0, int cci0, int cri1, int cci1,
            boolean hoome) {
        try {
            long result = swapChunks_Account(cri0, cci0, cri1, cci1);
            env.checkAndMaybeFreeMemory(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                if (!swapChunk()) {
                    throw e;
                }
                long result = 1;
                env.initMemoryReserve();
                result += swapChunks_Account(cri0, cci0, cri1, cci1, hoome);
                return result;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap seriailsed version of all Grids_AbstractGridChunk from
     * (cri0, cci0) to (cri1, cci1) in row major order. This involves writing
     * them to files and then clearing them from the cache.
     *
     * @param cri0 The chunk row index of the first Grid2DSquareCellChunks to be
     * swapped.
     * @param cci0 The chunk column index of the first Grid2DSquareCellChunks to
     * be swapped.
     * @param cri1 The chunk row index of the last Grid2DSquareCellChunks to be
     * swapped.
     * @param cci1 The chunk column index of the last Grid2DSquareCellChunks to
     * be swapped.
     * @return The number of Grids_AbstractGridChunk swapped.
     */
    public final long swapChunks_Account(
            int cri0, int cci0, int cri1, int cci1) {
        Grids_2D_ID_int chunkID;
        long result = 0L;
        if (cri0 != cri1) {
            for (int cci = cci0; cci < NChunkCols; cci++) {
                chunkID = new Grids_2D_ID_int(cri0, cci);
                if (isWorthSwapping(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearFromCacheChunk(chunkID);
                    result++;
                }
            }
            for (int cri = cri0 + 1; cri < cri1; cri++) {
                for (int cci = 0; cci < NChunkCols; cci++) {
                    chunkID = new Grids_2D_ID_int(cri, cci);
                    if (isWorthSwapping(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearFromCacheChunk(chunkID);
                        result++;
                    }
                }
            }
            for (int cci = 0; cci < cci1; cci++) {
                chunkID = new Grids_2D_ID_int(cri1, cci);
                if (isWorthSwapping(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearFromCacheChunk(chunkID);
                    result++;
                }
            }
        } else {
            for (int cci = cci0; cci < cci1 + 1; cci++) {
                chunkID = new Grids_2D_ID_int(cri0, cci);
                if (isWorthSwapping(chunkID)) {
                    writeToFileChunk(chunkID);
                    clearFromCacheChunk(chunkID);
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
        return ChunkIDChunkMap.get(chunkID) != null;
//        return ChunkIDChunkMap.containsKey(chunkID);
    }

    /**
     * @return true iff the chunk given by chunkID is worth swapping.
     * @param chunkID The ID of the chunk tested as to whether it is worth
     * swapping.
     */
    public final boolean isWorthSwapping(Grids_2D_ID_int chunkID) {
        if (isInCache(chunkID)) {
            return !isChunkSingleValueChunk(chunkID);
        }
        return false;
    }

    /**
     * For releasing a grid2DSquareCellChunk stored in memory. This is usually
     * only done after the equivalent of swapToFileChunk(ID) has been called.
     *
     * @param chunkID The Grids_2D_ID_int of the grid2DSquareCellChunk to be
     * cleared.
     */
    public final void clearFromCacheChunk(Grids_2D_ID_int chunkID) {
        ChunkIDChunkMap.replace(chunkID, null);
        ChunkIDsOfChunksWorthSwapping.remove(chunkID);
        //System.gc();
    }

    /**
     * For releasing all Grids_AbstractGridChunk in
     * this._ChunkID_AbstractGrid2DSquareCellChunk_HashMap.
     */
    public final void clearFromCacheChunks() {
        Iterator<Grids_2D_ID_int> ite;
        ite = ChunkIDChunkMap.keySet().iterator();
        while (ite.hasNext()) {
            ChunkIDChunkMap.replace(ite.next(), null);
        }
        ChunkIDsOfChunksWorthSwapping = new HashSet<>();
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
//        if (row >= NRows) {
//            return false;
//        }
//        return col < NCols;
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
     * @param chunksNotToSwapToFile
     * @param e
     */
    public void freeSomeMemoryAndResetReserve(
            HashMap<Grids_AbstractGrid, HashSet<Grids_2D_ID_int>> chunksNotToSwapToFile,
            OutOfMemoryError e) {
        Grids_AbstractGrid g;
        HashSet<Grids_2D_ID_int> chunkIDs;
        Iterator<Grids_AbstractGrid> ite;
        ite = chunksNotToSwapToFile.keySet().iterator();
        while (ite.hasNext()) {
            g = ite.next();
            chunkIDs = chunksNotToSwapToFile.get(g);
            if (env.swapChunkExcept_Account(g, chunkIDs, false) > 0) {
                env.initMemoryReserve(chunksNotToSwapToFile, env.HOOMET);
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
        if (env.swapChunkExcept_Account(this, chunkIDs, false) < 1L) {
            throw e;
        }
        env.initMemoryReserve(this, chunkIDs, env.HOOMET);
    }

    public void freeSomeMemoryAndResetReserve(
            Grids_2D_ID_int chunkID, OutOfMemoryError e) {
        if (env.swapChunkExcept_Account(this, false) < 1L) {
            if (env.swapChunkExcept_Account(this, chunkID, false) < 1L) {
                throw e;
            }
        }
        env.initMemoryReserve(this, chunkID, env.HOOMET);
    }

    public void freeSomeMemoryAndResetReserve(
            boolean hoome, OutOfMemoryError e) {
        if (!env.swapChunk(env.HOOMEF)) {
            throw e;
        }
        env.initMemoryReserve();
    }

    public void freeSomeMemoryAndResetReserve(OutOfMemoryError e) {
        if (env.swapChunkExcept_Account(this, false) < 1L) {
            throw e;
        }
        env.initMemoryReserve(this, env.HOOMET);
    }

    /**
     * @return the ChunkIDChunkMap
     */
    public TreeMap<Grids_2D_ID_int, Grids_AbstractGridChunk> getChunkIDChunkMap() {
        return ChunkIDChunkMap;
    }

    public void initDimensions(Grids_ESRIAsciiGridHeader header,
            long startRowIndex, long startColIndex) {
        BigDecimal xMin;
        BigDecimal yMin;
        BigDecimal xMax;
        BigDecimal yMax;
        BigDecimal cellsize;
        cellsize = header.cellsize;
        xMin = ((BigDecimal) header.xllcorner).add(cellsize.multiply(new BigDecimal(startColIndex)));
        yMin = ((BigDecimal) header.yllcorner).add(cellsize.multiply(new BigDecimal(startRowIndex)));
        xMax = xMin.add(new BigDecimal(Long.toString(NCols)).multiply(cellsize));
        yMax = yMin.add(new BigDecimal(Long.toString(NRows)).multiply(cellsize));
        Dimensions = new Grids_Dimensions(xMin, xMax, yMin, yMax, cellsize);
    }

    /**
     * Assumes NRows and NCols are already initialised.
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
                ChunkIDChunkMap.put(chunkID, chunk);
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
    public final Grids_AbstractGridChunk getChunk(int chunkRow,
            int chunkCol) {
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
        return getChunk(chunkID);
    }

    /**
     * @param chunkID
     * @param chunkRow
     * @param chunkCol
     * @return Grids_AbstractGridChunk.
     */
    public abstract Grids_AbstractGridChunk getChunk(
            Grids_2D_ID_int chunkID, int chunkRow, int chunkCol);

    public abstract Grids_AbstractGridStats getStats();

    /**
     * @return An Iterator for iterating over the cell values in this.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final Iterator iterator(
            boolean hoome) {
        try {
            Iterator result = iterator();
            env.checkAndMaybeFreeMemory(hoome);
            return result;
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
