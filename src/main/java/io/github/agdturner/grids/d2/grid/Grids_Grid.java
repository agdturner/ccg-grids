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
package io.github.agdturner.grids.d2.grid;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_FileStore;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_IO;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.core.Grids_2D_ID_long;
import io.github.agdturner.grids.core.Grids_Dimensions;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.core.Grids_Object;
import io.github.agdturner.grids.d2.chunk.Grids_Chunk;
import io.github.agdturner.grids.d2.chunk.d.Grids_ChunkDoubleSinglet;
import io.github.agdturner.grids.d2.chunk.i.Grids_ChunkIntSinglet;
import io.github.agdturner.grids.d2.stats.Grids_Stats;
import io.github.agdturner.grids.io.Grids_ESRIAsciiGridImporter.Grids_ESRIAsciiGridHeader;
import io.github.agdturner.grids.util.Grids_Utilities;

/**
 * For two dimensional (d2) grid instances - grids representing a regular
 * lattice like square celled raster arrangement of values in a plane aligned in
 * rows and columns matching up with orthogonal axes X and Y. All values in a
 * grid are of the same type and all "cells" are effectively the same size. The
 * cellsize along with the bounding box dimensions of the grid are stored as
 * {@link java.math.BigDecimal} values. The supported types of values are:
 * {@code boolean}, {@link Boolean}; or a specific type of
 * {@link java.lang.Number}. With the exception of the {@code boolean} type each
 * type has a specific no data value (noDataValue). The rows and columns of
 * cells in a grid are indexed by positive {@code long} values and these can
 * provide a unique {@link Grids_2D_ID_long} identifier for each cell - though
 * typically these are not used as grids are subdivided into chunks and it is
 * the rows and columns of cells in these rows and columns of chunks what are
 * primarily used.
 *
 * Chunks are organised into a map with each chunk indexed by a unique
 * {@link Grids_2D_ID_int} identifier which gives the chunk row and chunk
 * column. Each grid has a minimum of one chunk and a maximum of
 * {@link Integer#MAX_VALUE}.
 *
 * The first row of chunks and first column of chunks start in row 0, column 0.
 * Cells in row 0 have the smallest X axis bounds. Cells in column 0 have the
 * smallest Y axis bounds of all cells in the grid. The number of chunk rows
 * (nChunkRows) and the number of chunk columns (nChunkCols) is determined by
 * the overall number of rows and columns in the grid and some parameters which
 * set the typical number of rows (chunkNRows) and number of columns
 * (chunkNCols) for the chunks in the grid.
 *
 * In the general case (where there are more than two rows and columns of
 * chunks) - most chunks have the same number of rows and columns of cells. The
 * number of rows (chunkNRows) multiplied by the number of columns (chunkNCols)
 * in these chunks cannot exceed {@link Integer#MAX_VALUE}. But chunkNRows and
 * chunkNCols can be different and so chunks represent rectangular sections of
 * the grid.
 *
 * The number of rows in the final row of chunks and the number of columns in
 * the final column of chunks may have a smaller chunkNRows and chunkNCols than
 * chunks in the rest of the grid.
 *
 * The are a few different types of chunk distinguished by how they store and
 * provide access to the cell values. Singlet chunks are those chunks for which
 * all cell values in the chunk are the same. Singlet chunks data is represented
 * with a single value. As soon as there are more than two values in the cells
 * of a chunk, then an alternative storage is needed. Chunks may also store the
 * values in a map or in an array.
 *
 * The grid and the chunks individually can be cached to a file store for
 * persistence and to make available fast access memory for other processing.
 * Caching a chunk and then setting it to null in the map of chunks allows for
 * the memory that chunk was using to be garbage collected and made available
 * for other purposes. When required chunks that are not currently loaded can be
 * reloaded from the cache. Whether a cache effectively represents the same set
 * of values as a chunk in memory will all depend on whether and how the values
 * of the chunk have changed and if they are any different to those in any
 * cache.
 *
 * What the most appropriate type of chunk is for storing values will depend on
 * whether the data are changing, what the variety and distribution of values
 * is, and which values are going to be retrieved and stored and in what order.
 * Some types of chunk perform better than others depending on the density and
 * variety of values in the chunk.
 *
 * It requires effort to change the in memory chunk from one type to another, so
 * whether this is worth it all depends on: the memory demands and available
 * memory; how much the data are changing; and the relative importance of the
 * speed of computation over the memory footprint.
 *
 * Both a grid and each chunk has a {@link Grid_Stats}. These provide access to
 * summary statistics about the grid and each chunk. These are broadly of two
 * types: those which are kept more up-to-date as the underlying data are
 * changed; those where there is no effort to keep them up-to-date as the
 * underlying data changes. Having ready access to summary statistics about the
 * values in a grid or a chunk can be very useful, but there are costs
 * associated with keeping them more up to date if they are not going to be
 * used.
 *
 * It may require experiments and some understanding of the data and the
 * processing that will be done in order to decide what types of chunk and what
 * type of statistics to employ. In general, as the variety and density of the
 * of data increase, an array of values becomes more likely to be better than
 * anything else.
 *
 * There is a trade off between efficient storage, speed and flexibility. This
 * may further vary depending on the architecture and capabilities of the
 * machines being used.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_Grid extends Grids_Object {

    private static final long serialVersionUID = 1L;

    /**
     * The file store in which this is stored.
     */
    protected final Generic_FileStore store;

    /**
     * The file store id for this grid.
     */
    protected final long id;

    /**
     * The Grids_Chunk data cache.
     */
    protected TreeMap<Grids_2D_ID_int, Grids_Chunk> chunkIDChunkMap;

    /**
     * The Grids_Chunk data cache.
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
    protected Grids_Stats stats;

    /**
     * @param e The grids environment.
     * @param fs The file store in which this grid is stored.
     * @param id The id of the directory for this grid in the file store.
     * @throws java.lang.Exception If encountered.
     */
    protected Grids_Grid(Grids_Environment e, Generic_FileStore fs, long id)
            throws Exception {
        super(e);
        this.store = fs;
        this.id = id;
    }

    protected void init() throws IOException {
        env.setDataToCache(true);
        env.addGrid(this);
    }

    /**
     * Initialises non transient Grids_Grid fields from g.
     *
     * @param g The Grids_Grid from which the non transient Grids_Grid fields of
     * this are set.
     * @throws java.io.IOException If encountered.
     */
    protected void init(Grids_Grid g) throws IOException {
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

    protected void init(Grids_Stats stats, int chunkNRows, int chunkNCols) {
        this.stats = stats;
        this.stats.setGrid(this);
        ChunkNRows = chunkNRows;
        ChunkNCols = chunkNCols;
        Name = store.getBaseDir().getFileName().toString() + id;
        initNChunkRows();
        initNChunkCols();
        chunkIDChunkMap = new TreeMap<>();
        ChunkIDsOfChunksWorthCaching = new HashSet<>();
    }

    protected void init(Grids_Stats stats, int chunkNRows, int chunkNCols,
            long nRows, long nCols, Grids_Dimensions dimensions) {
        init(stats, chunkNRows, chunkNCols);
        NRows = nRows;
        NCols = nCols;
        Dimensions = dimensions;
    }

    protected void init(Grids_Grid g, Grids_Stats stats, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol) {
        init(stats, chunkNRows, chunkNCols);
        NRows = endRow - startRow;
        NCols = endCol - startCol;
        initDimensions(g, startRow, startCol);
    }

    /**
     * Sets the references to this in the chunks.
     */
    protected void setReferenceInChunkIDChunkMap() {
        Iterator<Grids_2D_ID_int> ite = chunkIDChunkMap.keySet().iterator();
        while (ite.hasNext()) {
            chunkIDChunkMap.get(ite.next()).initGrid(this);
        }
    }

    /**
     * @return A HashSet containing all chunk IDs.
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
        r += "store=" + store.toString() + ", ";
        r += "id=" + id + ", ";
        r += "Name=" + Name + ", ";
        r += "Dimensions=" + getDimensions().toString() + ", ";
        if (chunkIDChunkMap == null) {
            r += "ChunkIDChunkMap=null, ";
        } else {
            r += "ChunkIDChunkMap.size()=" + chunkIDChunkMap.size() + ", ";
        }
        HashSet<Grids_Grid> grids = env.getGrids();
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
     * @return The path to the directory where this is currently stored.
     */
    public Generic_Path getDirectory() {
        return new Generic_Path(store.getPath(id));
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
     * @return _ChunkNRows, the number of rows in Grids_Chunk with
     * Grids_2D_ID_int equal to _ChunkID
     * @param chunkID The Grids_2D_ID_int of the Grids_Chunk thats number of
     * rows is returned.
     */
    public final int getChunkNRows(Grids_2D_ID_int chunkID) {
        if (chunkID.getRow() < (NChunkRows - 1)) {
            return ChunkNRows;
        } else {
            return getChunkNRowsFinalRowChunk();
        }
    }

    /**
     * @return _ChunkNCols, the number of columns in Grids_Chunk with
     * Grids_2D_ID_int equal to _ChunkID
     * @param chunkID The Grids_2D_ID_int of the Grids_Chunk thats number of
     * columns is returned.
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
     * @return Set of chunk IDs for cells that's centroids are intersected by
     * circle with centre at x-coordinate x, y-coordinate y, and radius
     * distance.
     * @param distance The radius of the circle for which intersecting chunk IDs
     * are returned.
     * @param x The x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y The y-coordinate of the circle centre from which cell values are
     * returned.
     * @param row The row index at y.
     * @param col The col index at x.
     * @param dp The decimal places for the distance calculations.
     * @param rm The RoundingMode for the distance calculations.
     */
    public HashSet<Grids_2D_ID_int> getChunkIDs(BigDecimal distance,
            BigDecimal x, BigDecimal y, long row, long col, int dp,
            RoundingMode rm) {
        HashSet<Grids_2D_ID_int> r = new HashSet<>();
        long delta = distance.divideToIntegralValue(getCellsize()).longValueExact();
        for (long p = -delta; p <= delta; p++) {
            BigDecimal cellY = getCellYBigDecimal(row + p);
            for (long q = -delta; q <= delta; q++) {
                BigDecimal cellX = getCellXBigDecimal(col + q);
                BigDecimal d2 = Grids_Utilities.distance(cellX, cellY, x, y,
                        dp, rm);
                if (d2.compareTo(distance) == -1) {
                    r.add(new Grids_2D_ID_int(getChunkRow(row + p),
                            getChunkCol(col + q)));
                }
            }
        }
        return r;
    }

    /**
     * @return A set of chunk identifiers for all chunks in the range given by
     * rowMin, rowMax, colMin, colMax.
     *
     * @param rowMin The minimum end of the row range.
     * @param rowMax The maximum end of the row range.
     * @param colMin The minimum end of the column range.
     * @param colMax The maximum end of the column range.
     */
    public HashSet<Grids_2D_ID_int> getChunkIDs(long rowMin, long rowMax,
            long colMin, long colMax) {
        HashSet<Grids_2D_ID_int> r = new HashSet<>();
        int cnr = getChunkNRows();
        int cnc = getChunkNCols();
        for (long row = rowMin; row <= rowMax; row += cnr) {
            int cri = getChunkRow(row);
            for (long col = colMin; col <= colMax; col += cnc) {
                int cci = getChunkCol(col);
                r.add(new Grids_2D_ID_int(cri, cci));
            }
        }
        return r;
    }

    /**
     * @return Chunk column index for the Grids_Chunk intersecting the
     * x-coordinate x.
     * @param x The x-coordinate of the line intersecting the chunk column index
     * returned.
     */
    public final int getChunkCol(BigDecimal x) {
        return getChunkCol(getCol(x));
    }

    /**
     * @return Chunk column index for the Grids_Chunk intersecting the cell
     * column index _CellColIndex.
     *
     * @param col
     */
    public final int getChunkCol(long col) {
        return (int) (col / (long) ChunkNCols);
    }

    /**
     * @return Chunk cell column Index of the cells that intersect the
     * x-coordinate x.
     * @param x The x-coordinate of the line intersecting the chunk cell column
     * index returned.
     */
    public final int getCellCol(BigDecimal x) {
        return getCellCol(getCol(x));
    }

    /**
     * @return Chunk cell column index of the cells in the cell column index
     * _CellColIndex.
     * @param col The cell column index of the cell thats chunk cell column
     * index is returned.
     */
    public final int getCellCol(long col) {
        return (int) (col - (getChunkCol(col) * ChunkNCols));
    }

    /**
     * @return Cell row of the cells that intersects y axis coordinate y.
     * @param y The y-coordinate of the line for which the chunk cell row index
     * is returned.
     */
    public final int getCellRow(BigDecimal y) {
        return getCellRow(getRow(y));
    }

    /**
     * @return Chunk cell row index of the cells in row.
     * @param row
     */
    public final int getCellRow(long row) {
        return (int) (row - (getChunkRow(row) * ChunkNRows));
    }

    /**
     * @param x
     * @return Cell column of the cells that intersect the x axis coordinate x.
     */
    public final long getCol(BigDecimal x) {
        Grids_Dimensions d = getDimensions();
        return x.subtract(d.getXMin()).divideToIntegralValue(d.getCellsize())
                .longValueExact();
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
     * @return Chunk row index for the chunks intersecting the line given by
     * y-coordinate y.
     * @param y The y-coordinate of the line for which the chunk row index is
     * returned.
     */
    public final int getChunkRow(BigDecimal y) {
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
     * @param y
     * @return Cell row of the cells that intersect the y axis coordinate y.
     */
    public final long getRow(BigDecimal y) {
        Grids_Dimensions d = getDimensions();
        return y.subtract(d.getYMin()).divideToIntegralValue(d.getCellsize())
                .longValueExact();
    }

    /**
     * @param chunkRow
     * @param cellRow
     * @return ((long) chunkRow * (long) ChunkNRows) + (long) cellRow;
     * chunkCellRowIndex.
     */
    public final long getRow(int chunkRow, int cellRow) {
        return ((long) chunkRow * (long) ChunkNRows) + (long) cellRow;
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
    public final Grids_2D_ID_long getCellID(BigDecimal x, BigDecimal y) {
        return new Grids_2D_ID_long(getRow(y), getCol(x));
    }

    /**
     * Attempts to write this instance to a file.
     *
     * @throws java.io.IOException If encountered.
     */
    public void writeToFile() throws IOException, Exception {
        Generic_IO.writeObject(this, Paths.get(getDirectory().toString(),
                "thisFile"));
    }

    /**
     * Attempts to write this instance to Files located in the _Directory
     * returned by getDirectory().Chunks are all cached to file.
     *
     * @throws java.io.IOException If encountered.
     */
    public void writeToFileCaching() throws IOException, Exception {
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
     * @return Grids_2D_ID_int of the chunk which was cached or null if there
     * are no suitable chunks to cache.
     * @throws java.io.IOException If encountered.
     */
    public final Grids_2D_ID_int writeToFileChunk() throws IOException, Exception {
        if (ChunkIDsOfChunksWorthCaching.isEmpty()) {
            return null;
        }
        Iterator<Grids_2D_ID_int> ite = ChunkIDsOfChunksWorthCaching.iterator();
        while (ite.hasNext()) {
            Grids_2D_ID_int chunkID = ite.next();
            writeToFileChunk(chunkID);
            return chunkID;
        }
//        if (chunkIDChunkMap.isEmpty()) {
//            return null;
//        }
//        Grids_2D_ID_int chunkID;
//        Iterator<Grids_2D_ID_int> ite = chunkIDChunkMap.keySet().iterator();
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
     * @return True if Grids_Chunk on file is up to date.
     * @throws java.io.IOException If encountered.
     */
    public final boolean writeToFileChunk(Grids_2D_ID_int chunkID)
            throws IOException, Exception {
        boolean r = true;
        Grids_Chunk gridChunk = chunkIDChunkMap.get(chunkID);
        if (gridChunk != null) {
            if (!gridChunk.isCacheUpToDate()) {
                Path file = Paths.get(getDirectory().toString(),
                        chunkID.getRow() + "_" + chunkID.getCol());
                //Files.createDirectory(file.getParent());
                Generic_IO.writeObject(gridChunk, file);
                //System.gc();
                gridChunk.setCacheUpToDate(true);
            }
        } else {
            r = false;
        }
        return r;
    }

    /**
     * Attempts to write to File serialised versions of all chunks in
     * chunkIDChunkMap that are not single valued chunks.
     *
     * @throws java.io.IOException If encountered.
     */
    public final void writeToFileChunks() throws IOException, Exception {
        Iterator<Grids_2D_ID_int> ite = ChunkIDsOfChunksWorthCaching.iterator();
        while (ite.hasNext()) {
            writeToFileChunk(ite.next());
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
     * Grids_Chunk to be written to file.
     * @throws java.io.IOException If encountered.
     */
    public final void writeToFileChunks(HashSet<Grids_2D_ID_int> chunkIDs)
            throws IOException, Exception {
        Iterator<Grids_2D_ID_int> ite = chunkIDs.iterator();
        while (ite.hasNext()) {
            Grids_2D_ID_int cid = ite.next();
            if (isWorthCaching(cid)) {
                writeToFileChunk(cid);
            }
        }
    }

    /**
     * Attempts to cache a chunk and return the details of any caching done.
     * This is one of the lowest level memory handling operation of this class.
     *
     * @param hoome If true then if in the initial attempt to cache a chunk and
     * return the details of any caching done results in an OutOfMemoryError
     * being thrown, then an attempt to handle this is made by: clearing the
     * memory reserve, cache a chunk from this grid, and re-initialising the
     * memory reserve (which may involve swapping out chunks from other grids
     * and perhaps also swapping out other data). If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return A detailed account of what was cached.
     * @throws java.io.IOException If encountered.
     */
    public HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunk_AccountDetail(boolean hoome) throws IOException, Exception {
        try {
            HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                    = cacheChunk_AccountDetail();
            HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> pr
                    = env.checkAndMaybeFreeMemory_AccountDetail(hoome);
            env.combine(r, pr);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                        = cacheChunk_AccountDetail();
                if (r.isEmpty()) {
                    throw e;
                }
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> pr
                        = env.initMemoryReserve_AccountDetail(hoome);
                env.combine(r, pr);
                pr = cacheChunk_AccountDetail(hoome);
                env.combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache a chunk and return the details of any caching done.
     * This is one of the lowest level memory handling operation of this class.
     *
     * @return A detailed account of what was cached.
     * @throws java.io.IOException If encountered.
     */
    public HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunk_AccountDetail() throws IOException, Exception {
        HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                = new HashMap<>(1);
        Grids_2D_ID_int chunkID = writeToFileChunk();
        if (chunkID != null) {
            HashSet<Grids_2D_ID_int> chunks = new HashSet<>(1);
            clearChunk(chunkID);
            chunks.add(chunkID);
            r.put(this, chunks);
        }
        return r;
    }

    /**
     * Attempts to cache a chunk and return the id of the chunk cached. This is
     * one of the lowest level memory handling operation of this class.
     *
     * @param hoome If true then if in the initial attempt to cache a chunk and
     * return the id of the chunk cached throws an OutOfMemoryError, then an
     * attempt to handle this is made by: clearing the memory reserve, caching a
     * chunk from this grid, and re-initialising the memory reserve (which may
     * involve swapping out chunks from other grids and perhaps also swapping
     * out other data). If false then OutOfMemoryErrors are caught and thrown.
     * @return An account of what was cached.
     * @throws java.io.IOException If encountered.
     * @param camfm checkAndMaybeFreeMemory
     */
    public Grids_2D_ID_int cacheChunk_AccountChunk(boolean camfm, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_2D_ID_int r = cacheChunk_AccountChunk();
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int r = cacheChunk_AccountChunk();
                if (r == null) {
                    if (!env.cacheChunk(env.HOOMEF)) {
                        throw e;
                    }
                }
                env.initMemoryReserve();
                return cacheChunk_AccountChunk(camfm, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to cache a chunk and return true if cached and false otherwise.
     * This will first try to cache a chunk not in ge.NotToCache.
     *
     * @return An account of what was cached.
     * @throws java.io.IOException If encountered.
     */
    public Grids_2D_ID_int cacheChunk_AccountChunk() throws IOException, Exception {
        Grids_2D_ID_int cid = writeToFileChunk();
        if (cid != null) {
            clearChunk(cid);
        }
        return cid;
    }

    public long cacheChunks_Account() throws IOException, Exception {
        long r = 0L;
        for (int cri = 0; cri < NChunkRows; cri++) {
            for (int cci = 0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int cid = new Grids_2D_ID_int(cri, cci);
                if (writeToFileChunk(cid)) {
                    clearChunk(cid);
                    r++;
                }
            }
        }
        return r;
    }

    public long cacheChunks_Account(Set<Grids_2D_ID_int> chunkIDs) throws IOException, Exception {
        long r = 0L;
        Iterator<Grids_2D_ID_int> ite = chunkIDs.iterator();
        while (ite.hasNext()) {
            Grids_2D_ID_int cid = ite.next();
            if (writeToFileChunk(cid)) {
                clearChunk(cid);
                r++;
            }
        }
        return r;
    }

    /**
     *
     * @param chunkIDs
     * @param camfm checkAndMaybeFreeMemory
     * @param hoome
     * @return
     * @throws IOException
     */
    public Grids_2D_ID_int cacheChunkExcept_AccountChunk(
            HashSet<Grids_2D_ID_int> chunkIDs, boolean camfm,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_2D_ID_int r = cacheChunkExcept_AccountChunk(chunkIDs);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int r = cacheChunkExcept_AccountChunk(chunkIDs);
                if (r == null) {
                    if (!env.cacheChunk(env.HOOMEF)) {
                        throw e;
                    }
                }
                env.initMemoryReserve();
                return r;
            } else {
                throw e;
            }
        }
    }

    public Grids_2D_ID_int cacheChunkExcept_AccountChunk(
            HashSet<Grids_2D_ID_int> chunkIDs) throws IOException, Exception {
        Iterator<Grids_2D_ID_int> ite = ChunkIDsOfChunksWorthCaching.iterator();
        while (ite.hasNext()) {
            Grids_2D_ID_int cid = ite.next();
            if (!chunkIDs.contains(cid)) {
                writeToFileChunk(cid);
                clearChunk(cid);
                return cid;
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
        return null;
    }

    /**
     * @param i The chunk ID of the chunk to test if it is of a singlet type.
     * @return {@code true} if chunk is of a singlet type.
     */
    public boolean isChunkSingleValueChunk(Grids_2D_ID_int i) {
        return chunkIDChunkMap.get(i) instanceof Grids_ChunkDoubleSinglet
                || chunkIDChunkMap.get(i) instanceof Grids_ChunkIntSinglet;
    }

    /**
     * Caches the chunk with chunkID to file.
     *
     * @param chunkID
     * @param camfm checkAndMaybeFreeMemory
     * @param hoome
     * @throws java.io.IOException
     */
    public void cacheChunk(Grids_2D_ID_int chunkID, boolean camfm,
            boolean hoome) throws IOException, Exception {
        try {
            cacheChunk(chunkID);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(hoome, e);
                cacheChunk(chunkID, camfm, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Caches the chunk with chunkID to file. This will return true if the chunk
     * is cached and false otherwise.TIt is not sensible to cache some types of
     * chunk and in these cases false is returned.
     *
     * @param chunkID
     * @return
     * @throws java.io.IOException
     */
    public boolean cacheChunk(Grids_2D_ID_int chunkID) throws IOException,
            Exception {
        if (writeToFileChunk(chunkID)) {
            clearChunk(chunkID);
            return true;
        }
        return false;
    }

    /**
     * Attempts to write to file and clear from the cache any chunk in this.This
     * is one of the lowest level memory handling operation of this class.
     *
     * @param camfm checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return True if a chunk is cached.
     * @throws java.io.IOException
     */
    public boolean cacheChunk(boolean camfm, boolean hoome) throws IOException,
            Exception {
        try {
            boolean r = cacheChunk();
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(hoome, e);
                return cacheChunk(camfm, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to cache a chunk and return true if cached and false otherwise.
     * This will first try to cache a chunk not in ge.NotToCache.
     *
     * @return
     * @throws java.io.IOException
     */
    public boolean cacheChunk() throws IOException, Exception {
        Grids_2D_ID_int cid = writeToFileChunk();
        if (cid != null) {
            clearChunk(cid);
            return true;
        }
        return false;
    }

    /**
     * Attempts to write to file and clear from the cache a Grids_Chunk in
     * this._AbstractGrid2DSquareCell_HashSet.
     *
     * @param cid A chunk id of a chunk not to be cached
     * @param camfm checkAndMaybeFreeMemory
     * @param hoome If true then OutOfMemoryErrors are caught, cache operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return The Grids_2D_ID_int of Grids_Chunk cached or null.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_2D_ID_int cid, boolean camfm,
                    boolean hoome) throws IOException, Exception {
        try {
            HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                    = cacheChunkExcept_AccountDetail(cid);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                        = cacheChunkExcept_AccountDetail(cid);
                if (r.isEmpty()) {
                    r = env.cacheChunkExcept_AccountDetail(this, cid,
                            false);
                }
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> pr
                        = env.initMemoryReserve_AccountDetail(this, cid,
                                hoome);
                env.combine(r, pr);
                pr = cacheChunkExcept_AccountDetail(cid);
                env.combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to cache a chunk and make it available for garbage collection.
     *
     * @param chunkID The id of a chunk not to swap out.
     * @return The chunk ids of chunks swapped out or {@code null} if no chunks
     * were made available for garbage collection.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(Grids_2D_ID_int chunkID)
            throws IOException, Exception {
        HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r = new HashMap<>(1);
        for (int cri = 0; cri < NChunkRows; cri++) {
            for (int cci = 0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int bid = new Grids_2D_ID_int(cri, cci);
                if (!bid.equals(chunkID)) {
                    if (isWorthCaching(bid)) {
                        writeToFileChunk(bid);
                        clearChunk(bid);
                        HashSet<Grids_2D_ID_int> chunks;
                        chunks = new HashSet<>(1);
                        chunks.add(bid);
                        r.put(this, chunks);
                        return r;
                    }
                }
            }
        }
        return r;
    }

    /**
     * @param cid The id of the chunk not to swap out.
     * @param camfm If true then there is a further attempt to check and maybe
     * free memory once a chunk is swapped out or otherwise cleared from memory.
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @return The chunk id of the chunk swapped out.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public Grids_2D_ID_int cacheChunkExcept_AccountChunk(
            Grids_2D_ID_int cid, boolean camfm, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_2D_ID_int r = cacheChunkExcept_AccountChunk(cid);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int r = cacheChunkExcept_AccountChunk(cid);
                if (r == null) {
                    if (env.cacheChunkExcept_Account(this, cid, false) < 1L) {
                        throw e;
                    }
                }
                env.initMemoryReserve(this, cid, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * @param cid The id of the chunk not to swap out.
     * @return The chunk id of the chunk swapped out.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public Grids_2D_ID_int cacheChunkExcept_AccountChunk(
            Grids_2D_ID_int cid) throws IOException, Exception {
        for (int cri = 0; cri < NChunkRows; cri++) {
            for (int cci = 0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int r = new Grids_2D_ID_int(cri, cci);
                if (!r.equals(cid)) {
                    if (isWorthCaching(r)) {
                        writeToFileChunk(r);
                        clearChunk(r);
                        return r;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except that
     * with ID {@code i}.
     *
     * @param i The ID of a chunk not to clear from memory.
     * @param camfm If true then there is a further attempt to check and maybe
     * free memory once a chunk is swapped out or otherwise cleared from memory.
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @return The number of chunks cached.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_2D_ID_int i, boolean camfm,
                    boolean hoome) throws IOException, Exception {
        try {
            HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                    = cacheChunksExcept_AccountDetail(i);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                        = cacheChunkExcept_AccountDetail(i);
                if (r.isEmpty()) {
                    r = env.cacheChunkExcept_AccountDetail(this, i, false);
                    if (r.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> pr
                        = env.initMemoryReserve_AccountDetail(this, i, hoome);
                env.combine(r, pr);
                pr = cacheChunksExcept_AccountDetail(i, camfm, hoome);
                env.combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except that
     * with ID {@code i}.
     *
     * @param i The id of a chunk not to clear from memory.
     * @return Details of which chunks have been cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public final HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(Grids_2D_ID_int i)
            throws IOException, Exception {
        HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> s = new HashSet<>();
        for (int chunkRow = 0; chunkRow < NChunkRows; chunkRow++) {
            for (int chunkCol = 0; chunkCol < NChunkCols; chunkCol++) {
                Grids_2D_ID_int i2 = new Grids_2D_ID_int(chunkRow, chunkCol);
                if (!i.equals(i2)) {
                    if (isWorthCaching(i2)) {
                        writeToFileChunk(i2);
                        clearChunk(i2);
                        s.add(i2);
                    }
                }
            }
        }
        r.put(this, s);
        return r;
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this.
     *
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public void cacheChunks(boolean hoome) throws IOException, Exception {
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

    /**
     * Attempts to swap out or clear from memory all chunks in this.
     *
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public void cacheChunks() throws IOException, Exception {
        for (int cri = 0; cri < NChunkRows; cri++) {
            for (int cci = 0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cri, cci);
                if (isWorthCaching(i)) {
                    writeToFileChunk(i);
                    clearChunk(i);
                }
            }
        }
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except those
     * with IDs in {@code s}.
     *
     * @param s The set of IDs of chunks not to clear from memory.
     * @param camfm If true then there is a further attempt to check and maybe
     * free memory once a chunk is swapped out or otherwise cleared from memory.
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @return The number of chunks cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public long cacheChunksExcept_Account(HashSet<Grids_2D_ID_int> s,
            boolean camfm, boolean hoome) throws IOException, Exception {
        try {
            long r = cacheChunksExcept_Account(s);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                long r = env.cacheChunkExcept_Account(this, s, false);
                if (r < 1L) {
                    r = env.cacheChunkExcept_Account(this, s, false);
                    if (r < 1L) {
                        throw e;
                    }
                }
                r += env.initMemoryReserve_Account(this, s, hoome);
                r += cacheChunksExcept_Account(s, camfm, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except those
     * with IDs in {@code s}.
     *
     * @param s The set of IDs of chunks not to clear from memory.
     * @param camfm If true then there is a further attempt to check and maybe
     * free memory once a chunk is swapped out or otherwise cleared from memory.
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @return Details of which chunks have been cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(HashSet<Grids_2D_ID_int> s,
                    boolean camfm, boolean hoome) throws IOException, Exception {
        try {
            HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                    = cacheChunksExcept_AccountDetail(s);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                        = cacheChunkExcept_AccountDetail(s);
                if (r.isEmpty()) {
                    env.addToNotToCache(this, s);
                    r = env.cacheChunk_AccountDetail(env.HOOMEF);
                    if (r.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> pr
                        = env.initMemoryReserve_AccountDetail(this, s, hoome);
                env.combine(r, pr);
                pr = cacheChunksExcept_AccountDetail(s, camfm, hoome);
                env.combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except those
     * with IDs in {@code s}.
     *
     * @param s The set of IDs of chunks not to clear from memory.
     * @return Details of which chunks have been cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public final HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunksExcept_AccountDetail(HashSet<Grids_2D_ID_int> s)
            throws IOException, Exception {
        HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> s2 = new HashSet<>();
        for (int cri = 0; cri < NChunkRows; cri++) {
            for (int cci = 0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cri, cci);
                if (!s.contains(i)) {
                    if (isWorthCaching(i)) {
                        writeToFileChunk(i);
                        clearChunk(i);
                        s2.add(i);
                    }
                }
            }
        }
        r.put(this, s2);
        return r;
    }

    /**
     * Attempts to swap out or clear from memory a chunk in this except that
     * with ID {@code i}.
     *
     * @param i The ID of the chunk not to clear from memory.
     * @param camfm If true then there is a further attempt to check and maybe
     * free memory once a chunk is swapped out or otherwise cleared from memory.
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @return The number of chunks cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final long cacheChunkExcept_Account(Grids_2D_ID_int i, boolean camfm,
            boolean hoome) throws IOException, Exception {
        try {
            long r = cacheChunkExcept_Account(i);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                long r = env.cacheChunkExcept_Account(this, i, false);
                if (r < 1L) {
                    r = env.cacheChunkExcept_Account(this, i, false);
                    if (r < 1L) {
                        throw e;
                    }
                }
                r += env.initMemoryReserve_Account(this, i, hoome);
                r += cacheChunksExcept_Account(i, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap out or clear from memory a chunk in this except that
     * with ID {@code i}.
     *
     * @param i The ID of the chunk not to clear from memory.
     * @return 1L if a chunk was cleared from memory and 0L otherwise.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public final long cacheChunkExcept_Account(Grids_2D_ID_int i)
            throws IOException, Exception {
        for (int cri = 0; cri < NChunkRows; cri++) {
            for (int cci = 0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int i2 = new Grids_2D_ID_int(cri, cci);
                if (!i.equals(i2)) {
                    if (isWorthCaching(i2)) {
                        writeToFileChunk(i2);
                        clearChunk(i2);
                        return 1L;
                    }
                }
            }
        }
        return 0L;
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except that
     * with ID {@code i}.
     *
     * @param i The ID of the chunk not to clear from memory.
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @return The number of chunks cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final long cacheChunksExcept_Account(Grids_2D_ID_int i,
            boolean hoome) throws IOException, Exception {
        try {
            long r = cacheChunksExcept_Account(i);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                long r = env.cacheChunkExcept_Account(this, i, false);
                if (r < 1L) {
                    r = env.cacheChunkExcept_Account(this, i, false);
                    if (r < 1L) {
                        throw e;
                    }
                }
                r += env.initMemoryReserve_Account(this, i, hoome);
                r += cacheChunksExcept_Account(i, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except that
     * with ID {@code i}.
     *
     * @param i The ID of the chunk not to clear from memory.
     * @return The number of chunks cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final long cacheChunksExcept_Account(Grids_2D_ID_int i)
            throws IOException, Exception {
        long r = 0L;
        for (int cri = 0; cri < NChunkRows; cri++) {
            for (int cci = 0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int id2 = new Grids_2D_ID_int(cri, cci);
                if (i != id2) {
                    if (isWorthCaching(i)) {
                        writeToFileChunk(i);
                        clearChunk(i);
                        r++;
                    }
                }
            }
        }
        return r;
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except those
     * with IDs in {@code s}.
     *
     * @param s The set of chunk IDs not to clear from memory.
     * @return The number of chunks cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final long cacheChunksExcept_Account(HashSet<Grids_2D_ID_int> s)
            throws IOException, Exception {
        long r = 0L;
        for (int cri = 0; cri < NChunkRows; cri++) {
            for (int cci = 0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(cri, cci);
                if (!s.contains(chunkID)) {
                    if (isWorthCaching(chunkID)) {
                        writeToFileChunk(chunkID);
                        clearChunk(chunkID);
                        r++;
                    }
                }
            }
        }
        return r;
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except those
     * with IDs in {@code s}.
     *
     * @param s The set of chunk IDs not to clear from memory.
     * @param camfm If true then there is a further attempt to check and maybe
     * free memory once a chunk is swapped out or otherwise cleared from memory.
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @return Details of which chunks have been cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(HashSet<Grids_2D_ID_int> s,
                    boolean camfm, boolean hoome) throws IOException, Exception {
        try {
            HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                    = cacheChunkExcept_AccountDetail(s);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                        = cacheChunkExcept_AccountDetail(s);
                if (r == null) {
                    if (!env.cacheChunk(env.HOOMEF)) {
                        throw e;
                    }
                }
                env.initMemoryReserve();
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except those
     * with IDs in {@code s}.
     *
     * @param s The set of chunk IDs not to clear from memory.
     * @return Details of which chunks have been cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunkExcept_AccountDetail(HashSet<Grids_2D_ID_int> s)
            throws IOException, Exception {
        HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r = new HashMap<>(1);
        for (int cri = 0; cri < NChunkRows; cri++) {
            for (int cci = 0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cri, cci);
                if (!s.contains(i)) {
                    if (isWorthCaching(i)) {
                        writeToFileChunk(i);
                        clearChunk(i);
                        r.put(this, s);
                        return r;
                    }
                }
            }
        }
        return r;
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this.
     *
     * @param camfm If true then there is a further attempt to check and maybe
     * free memory once a chunk is swapped out or otherwise cleared from memory.
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @return The number of chunks cleared.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public final HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunks_AccountDetail(boolean camfm, boolean hoome)
            throws IOException, Exception {
        try {
            HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                    = cacheChunks_AccountDetail();
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                        = cacheChunks_AccountDetail();
                if (r.isEmpty()) {
                    r = env.cacheChunk_AccountDetail(false);
                    if (r.isEmpty()) {
                        throw e;
                    }
                }
                HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> pr
                        = env.initMemoryReserve_AccountDetail(hoome);
                env.combine(r, pr);
                pr = cacheChunks_AccountDetail(camfm, hoome);
                env.combine(r, pr);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this.
     *
     * @return Details of the chunks cleared.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public final HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>>
            cacheChunks_AccountDetail() throws IOException, Exception {
        HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> r
                = new HashMap<>(1);
        HashSet<Grids_2D_ID_int> s = new HashSet<>();
        for (int cri = 0; cri < NChunkRows; cri++) {
            for (int cci = 0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cri, cci);
                if (isWorthCaching(i)) {
                    writeToFileChunk(i);
                    clearChunk(i);
                    s.add(i);
                }
            }
        }
        if (s.isEmpty()) {
            return r;
        }
        r.put(this, s);
        return r;
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this from (cri0,
     * cci0) to (cri1, cci1) in row major order.
     *
     * @param cri0 The chunk row index of the first chunk to be cleared.
     * @param cci0 The chunk column index of the first chunk to be cleared.
     * @param cri1 The chunk row index of the last chunk to be cleared.
     * @param cci1 The chunk column index of the last chunk to be cleared.
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @return The number of chunks cleared.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final long cacheChunks_Account(int cri0, int cci0, int cri1,
            int cci1, boolean hoome) throws IOException, Exception {
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
                long r = 1;
                env.initMemoryReserve();
                r += cacheChunks_Account(cri0, cci0, cri1, cci1, hoome);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this from (cri0,
     * cci0) to (cri1, cci1) in row major order.
     *
     * @param cri0 The chunk row index of the first chunk to be cleared.
     * @param cci0 The chunk column index of the first chunk to be cleared.
     * @param cri1 The chunk row index of the last chunk to be cleared.
     * @param cci1 The chunk column index of the last chunk to be cleared.
     * @return The number of chunks cached.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final long cacheChunks_Account(int cri0, int cci0, int cri1,
            int cci1) throws IOException, Exception {
        long r = 0L;
        if (cri0 != cri1) {
            for (int cci = cci0; cci < NChunkCols; cci++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cri0, cci);
                if (isWorthCaching(i)) {
                    writeToFileChunk(i);
                    clearChunk(i);
                    r++;
                }
            }
            for (int cri = cri0 + 1; cri < cri1; cri++) {
                for (int cci = 0; cci < NChunkCols; cci++) {
                    Grids_2D_ID_int i = new Grids_2D_ID_int(cri, cci);
                    if (isWorthCaching(i)) {
                        writeToFileChunk(i);
                        clearChunk(i);
                        r++;
                    }
                }
            }
            for (int cci = 0; cci < cci1; cci++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cri1, cci);
                if (isWorthCaching(i)) {
                    writeToFileChunk(i);
                    clearChunk(i);
                    r++;
                }
            }
        } else {
            for (int cci = cci0; cci < cci1 + 1; cci++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cri0, cci);
                if (isWorthCaching(i)) {
                    writeToFileChunk(i);
                    clearChunk(i);
                    r++;
                }
            }
        }
        return r;
    }

    /**
     * @return {@code true} if the chunk with the chunk ID {@code i} is in the
     * fast access memory.
     * @param i The ID of the chunk tested as to whether it is in the fast
     * access memory.
     */
    public final boolean isInCache(Grids_2D_ID_int i) {
        return chunkIDChunkMap.get(i) != null;
//        return chunkIDChunkMap.containsKey(chunkID);
    }

    /**
     * @return {@code true} if the chunk given by chunk ID {@code i} is worth 
     * caching - as determined by whether it is a .
     * @param i The ID of the chunk tested as to whether it is worth
     * caching.
     */
    public final boolean isWorthCaching(Grids_2D_ID_int i) {
        if (isInCache(i)) {
            return !isChunkSingleValueChunk(i);
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
     * For releasing all Grids_Chunk in
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
     * @param dp The decimal places for the distance calculations.
     * @param rm The RoundingMode for the distance calculations.
     */
    public final Grids_2D_ID_long[] getCellIDs(BigDecimal x, BigDecimal y,
            BigDecimal distance, int dp, RoundingMode rm) {
        return getCellIDs(x, y, getRow(y), getCol(x), distance, dp, rm);
    }

    /**
     * @return The cell IDs that's centroids would be within a circle with
     * radius distance centred at the centroid of the cell at row, col.
     * @param row the row index for the cell that's centroid is the circle
     * centre from which cell values are returned.
     * @param col the column index for the cell that's centroid is the circle
     * centre from which cell values are returned.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @param dp The decimal places for the distance calculations.
     * @param rm The RoundingMode for the distance calculations.
     */
    public final Grids_2D_ID_long[] getCellIDs(long row, long col,
            BigDecimal distance, int dp, RoundingMode rm) {
        return getCellIDs(getCellXBigDecimal(col), getCellYBigDecimal(row), row, col, distance, dp, rm);
    }

    /**
     * @return The cell IDs that's centroids would be within a circle with
     * radius distance centred at x-coordinate x, y-coordinate y.
     * @param x the x-coordinate of the circle centre from which cell values are
     * returned.
     * @param y the y-coordinate of the circle centre from which cell values are
     * returned.
     * @param row the row index at y.
     * @param col the col index at x.
     * @param distance the radius of the circle for which intersected cell
     * values are returned.
     * @param dp The decimal places for the distance calculations.
     * @param rm The RoundingMode for the distance calculations.
     */
    public Grids_2D_ID_long[] getCellIDs(BigDecimal x, BigDecimal y, long row,
            long col, BigDecimal distance, int dp, RoundingMode rm) {
        Grids_2D_ID_long[] r;
        HashSet<Grids_2D_ID_long> r2 = new HashSet<>();
        long delta = distance.divideToIntegralValue(getCellsize()).longValueExact();
        for (long p = -delta; p <= delta; p++) {
            BigDecimal cellY = getCellYBigDecimal(row + p);
            for (long q = -delta; q <= delta; q++) {
                BigDecimal cellX = getCellXBigDecimal(col + q);
                BigDecimal d2 = Grids_Utilities.distance(cellX, cellY, x, y,
                        dp, rm);
                if (d2.compareTo(distance) == -1) {
                    r2.add(new Grids_2D_ID_long(row + p, col + q));
                }
            }
        }
        r = new Grids_2D_ID_long[r2.size()];
        return r2.toArray(r);
    }

    /**
     * @return Nearest cells _CellRowIndex and _CellColIndex as a long[] from ID
     * to point given by x-coordinate x, y-coordinate y.
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     */
    public Grids_2D_ID_long getNearestCellID(BigDecimal x, BigDecimal y) {
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
        return getNearestCellID(getCellXBigDecimal(col), getCellYBigDecimal(row),
                row, col);
    }

    /**
     * @return Nearest Grids_2D_ID_long to point given by x-coordinate x,
     * y-coordinate y in position given by _CellRowIndex, _CellColIndex.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param row The cell row index of cell containing point.
     * @param col The cell column index of cell containing point.
     */
    public Grids_2D_ID_long getNearestCellID(BigDecimal x, BigDecimal y,
            long row, long col) {
        Grids_2D_ID_long cellID;
        boolean isInGrid = isInGrid(x, y);
        if (!isInGrid) {
            long p;
            long q;
            if (x.compareTo(Dimensions.getXMax()) >= 0) {
                q = NCols - 1;
                if (y.compareTo(Dimensions.getYMax()) == 1) {
                    p = 0;
                } else {
                    if (y.compareTo(Dimensions.getYMin()) == -1) {
                        p = NRows - 1;
                    } else {
                        p = getRow(y);
                    }
                }
            } else {
                if (x.compareTo(Dimensions.getXMin()) == -1) {
                    q = 0;
                    if (y.compareTo(Dimensions.getYMax()) >= 0) {
                        p = 0;
                    } else {
                        if (y.compareTo(Dimensions.getYMin()) == -1) {
                            p = NRows - 1;
                        } else {
                            p = getRow(y);
                        }
                    }
                } else {
                    q = getCol(x);
                    if (y.compareTo(Dimensions.getYMax()) >= 0) {
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
    public final boolean isInGrid(int chunkRow, int chunkCol, int cellRow,
            int cellCol) {
        return isInGrid(getRow(chunkRow, cellRow), getCol(chunkCol, cellCol));
    }

    /**
     * @param col
     * @return the x-coordinate of the centroid of col as a BigDecimal.
     */
    public final BigDecimal getCellXBigDecimal(long col) {
        return Dimensions.getXMin().add(Dimensions.getCellsize().multiply(
                BigDecimal.valueOf(col))).add(Dimensions.getHalfCellsize());
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
     * @param chunkID
     * @return y-coordinate of the centroid of cell with Grids_2D_ID_long
     * _CellID as a BigDecimal.
     */
    public final BigDecimal getCellYBigDecimal(Grids_2D_ID_long chunkID) {
        return getCellYBigDecimal(chunkID.getRow());
    }

    /**
     * @param halfCellsize Half the grid cellsize.
     * @return BigDecimal[] r where; r[0] xmin, left most x-coordinate of cell
     * that intersects point at (x,y) r[1] ymin, lowest y-coordinate of cell
     * that intersects point at (x,y) r[2] xmax, right most x-coordinate of cell
     * that intersects point at (x,y) r[3] ymax, highest y-coordinate of cell
     * that intersects point at (x,y).
     * @param row The row index of the cell for which the bounds are returned.
     * @param col The cloumn index of the cell for which the bounds are
     * returned.
     */
    public final BigDecimal[] getCellBounds(BigDecimal halfCellsize,
            long row, long col) {
        BigDecimal[] r = new BigDecimal[4];
        BigDecimal x = getCellXBigDecimal(col);
        BigDecimal y = getCellYBigDecimal(row);
        r[0] = x.subtract(halfCellsize);
        r[1] = y.subtract(halfCellsize);
        r[2] = x.add(halfCellsize);
        r[3] = y.add(halfCellsize);
        return r;
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
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public void freeSomeMemoryAndResetReserve(
            HashMap<Grids_Grid, HashSet<Grids_2D_ID_int>> chunksNotToCacheToFile,
            OutOfMemoryError e) throws IOException, Exception {
        Iterator<Grids_Grid> ite = chunksNotToCacheToFile.keySet().iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            HashSet<Grids_2D_ID_int> chunkIDs = chunksNotToCacheToFile.get(g);
            if (env.cacheChunkExcept_Account(g, chunkIDs, false) > 0) {
                env.initMemoryReserve(chunksNotToCacheToFile, env.HOOMET);
                return;
            }
        }
        throw e;
    }

    public void freeSomeMemoryAndResetReserve(
            int chunkRow, int chunkCol, OutOfMemoryError e) throws IOException, Exception {
        //env.clearMemoryReserve();
        Grids_2D_ID_int chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
        freeSomeMemoryAndResetReserve(chunkID, e);
    }

    public void freeSomeMemoryAndResetReserve(
            HashSet<Grids_2D_ID_int> chunkIDs, OutOfMemoryError e) throws IOException, Exception {
        if (env.cacheChunkExcept_Account(this, chunkIDs, false) < 1L) {
            throw e;
        }
        env.initMemoryReserve(this, chunkIDs, env.HOOMET);
    }

    public void freeSomeMemoryAndResetReserve(
            Grids_2D_ID_int chunkID, OutOfMemoryError e) throws IOException, Exception {
        if (env.cacheChunkExcept_Account(this, false) < 1L) {
            if (env.cacheChunkExcept_Account(this, chunkID, false) < 1L) {
                throw e;
            }
        }
        env.initMemoryReserve(this, chunkID, env.HOOMET);
    }

    public void freeSomeMemoryAndResetReserve(
            boolean hoome, OutOfMemoryError e) throws IOException, Exception {
        if (!env.cacheChunk(env.HOOMEF)) {
            throw e;
        }
        env.initMemoryReserve();
    }

    public void freeSomeMemoryAndResetReserve(OutOfMemoryError e) throws IOException, Exception {
        if (env.cacheChunkExcept_Account(this, false) < 1L) {
            throw e;
        }
        env.initMemoryReserve(this, env.HOOMET);
    }

    /**
     * @return the chunkIDChunkMap
     */
    public TreeMap<Grids_2D_ID_int, Grids_Chunk> getChunkIDChunkMap() {
        return chunkIDChunkMap;
    }

    /**
     * Initialises {@link #Dimensions} from {@code header}.
     *
     * @param header
     * @param startRowIndex
     * @param startColIndex
     */
    public void initDimensions(Grids_ESRIAsciiGridHeader header,
            long startRowIndex, long startColIndex) {
        BigDecimal cellsize = header.cellsize;
        BigDecimal xMin = header.xll.add(cellsize.multiply(new BigDecimal(startColIndex)));
        BigDecimal yMin = header.yll.add(cellsize.multiply(new BigDecimal(startRowIndex)));
        BigDecimal xMax = xMin.add(new BigDecimal(Long.toString(NCols)).multiply(cellsize));
        BigDecimal yMax = yMin.add(new BigDecimal(Long.toString(NRows)).multiply(cellsize));
        Dimensions = new Grids_Dimensions(xMin, xMax, yMin, yMax, cellsize);
    }

    /**
     * Assumes nrows and ncols are already initialised.
     *
     * @param g
     * @param startRowIndex
     * @param startColIndex
     */
    public void initDimensions(Grids_Grid g, long startRowIndex,
            long startColIndex) {
        Dimensions = g.getDimensions(); // temporary assignment
        BigDecimal startColIndexBigDecimal = new BigDecimal(startColIndex);
        BigDecimal startRowIndexBigDecimal = new BigDecimal(startRowIndex);
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
     * @return Grids_Chunk for the given chunkID.
     * @param chunkID
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public abstract Grids_Chunk getChunk(Grids_2D_ID_int chunkID)
            throws IOException, ClassNotFoundException, Exception;

    /**
     * Attempts to load into the memory cache the chunk with chunk ID chunkID.
     *
     * @param chunkID The chunk ID of the chunk to be restored.
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public void loadIntoCacheChunk(Grids_2D_ID_int chunkID) throws IOException,
            ClassNotFoundException,
            Exception {
        boolean isInCache = isInCache(chunkID);
        if (!isInCache) {
            Path f = Paths.get(getDirectory().toString(),
                    "" + chunkID.getRow() + "_" + chunkID.getCol());
            if (Files.exists(f)) {
                //System.out.println("Loading chunk from file" + f);
                Object o = Generic_IO.readObject(f);
                Grids_Chunk chunk = (Grids_Chunk) o;
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
//     * @return Grids_Chunk.
//     */
//    public abstract Grids_Chunk getChunk(int chunkRow, int chunkCol);

    /**
     * @param chunkRow
     * @param chunkCol
     * @return Grids_AbstractGridChunkDouble.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public final Grids_Chunk getChunk(int chunkRow, int chunkCol)
            throws IOException, ClassNotFoundException, Exception {
        return getChunk(new Grids_2D_ID_int(chunkRow, chunkCol));
    }

    /**
     * @param chunkID
     * @param chunkRow
     * @param chunkCol
     * @return Grids_Chunk.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public abstract Grids_Chunk getChunk(Grids_2D_ID_int chunkID,
            int chunkRow, int chunkCol) throws IOException,
            ClassNotFoundException, Exception;

    public abstract Grids_Stats getStats();

    /**
     * @param hoome
     * @return this._GridStatistics TODO: For safety, this method should either
     * be removed and this class be made implement GridStatisticsInterface. This
     * done the methods introduced would be made to call the relevant ones in
     * this._GridStatistics. Or the _GridStatistics need to be made safe in that
     * only copies of fields are passed.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public Grids_Stats getStats(boolean hoome) throws IOException, Exception {
        try {
            Grids_Stats r = getStats();
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

    public Path getPathThisFile(Generic_Path p) {
        return Paths.get(p.toString(), "thisFile");
    }
    
    /**
     * POJO for getting nearest value cell IDs and distance.
     */
    public class NearestValuesCellIDsAndDistance {

        public Grids_2D_ID_long[] cellIDs;
        public BigDecimal distance;

        public NearestValuesCellIDsAndDistance() {
        }
    }

    
}
