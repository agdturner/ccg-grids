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
package uk.ac.leeds.ccg.grids.d2.grid;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import uk.ac.leeds.ccg.generic.io.Generic_FileStore;
import uk.ac.leeds.ccg.generic.io.Generic_Path;
import uk.ac.leeds.ccg.generic.io.Generic_IO;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import uk.ac.leeds.ccg.grids.memory.Grids_Account;
import uk.ac.leeds.ccg.grids.memory.Grids_AccountDetail;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.core.Grids_Object;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleSinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.i.Grids_ChunkIntSinglet;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_Stats;
import uk.ac.leeds.ccg.grids.io.Grids_ESRIAsciiGridImporter.Header;
import uk.ac.leeds.ccg.grids.d2.util.Grids_Utilities;
import java.util.HashSet;
import java.util.stream.Collectors;

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
 * the rows and columns of cells in these rows and columns of chunks that are
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
 * of the chunk have changed.
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
 * Each grid has a statistics instance associated with it which provides access
 * to summary statistics about the grid. The statistics are broadly of two
 * types: those which are more or less updated as the underlying data are
 * changed; and, those where no effort is made to keep them up-to-date as the
 * underlying data changes. Having ready access to summary statistics about the
 * values in a grid can be very useful, but there are costs associated with
 * keeping them more up to date if they are not going to be used.
 *
 * It may require experiments and some understanding of the data and the
 * processing that will be done in order to make a good decision about what
 * types of statistic and chunks to use. In general, as the variety and density
 * of data values increases the more likely it becomes that an array of values
 * will be best. For sparse data where the majority of a chunk is noDataValues
 * or is of the same value, then the better the map type storage is.
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
    protected final Generic_FileStore fs;

    /**
     * The file store ID for this grid.
     */
    protected final long fsID;

    /**
     * The data are stored in a map with chunk ID keys and values that are
     * either chunks, or {@code null} - if the chunk is not stored in the fast
     * access memory, but in the file store {@link #fs}.
     */
    protected TreeMap<Grids_2D_ID_int, Grids_Chunk> data;

    /**
     * A set of chunks worth swapping. These do not include singlet type chunks.
     */
    protected HashSet<Grids_2D_ID_int> worthSwapping;

    /**
     * For storing the number of chunk rows.
     */
    protected int nChunkRows;

    /**
     * For storing the number of chunk columns.
     */
    protected int nChunkCols;

    /**
     * For storing the (usual) number of rows of cells in a chunk. The number of
     * rows in the final chunk row may be less.
     */
    protected int chunkNRows;

    /**
     * For storing the (usual) number of columns of cells in a chunk. The number
     * of columns in the final chunk column may be less.
     */
    protected int chunkNCols;

    /**
     * For storing the number of rows in the grid.
     */
    protected long nRows;

    /**
     * For storing the number of columns in the grid.
     */
    protected long nCols;

    /**
     * For storing the Name of the grid.
     */
    protected String name;

    /**
     * The dimensions of the grid.
     */
    protected Grids_Dimensions dim;

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
        this.fs = fs;
        this.fsID = id;
    }

    protected void init() throws IOException {
        env.setDataToClear(true);
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
        chunkNCols = g.chunkNCols;
        chunkNRows = g.chunkNRows;
        dim = g.dim;
        name = g.name;
        nChunkCols = g.nChunkCols;
        nChunkRows = g.nChunkRows;
        nCols = g.nCols;
        nRows = g.nRows;
        init();
    }

    protected void init(Grids_Stats stats, int chunkNRows, int chunkNCols) {
        this.stats = stats;
        this.stats.setGrid(this);
        this.chunkNRows = chunkNRows;
        this.chunkNCols = chunkNCols;
        name = fs.getBaseDir().getFileName().toString() + fsID;
        initNChunkRows();
        initNChunkCols();
        data = new TreeMap<>();
        worthSwapping = new HashSet<>();
    }

    protected void init(Grids_Stats stats, int chunkNRows, int chunkNCols,
            long nRows, long nCols, Grids_Dimensions dimensions) {
        this.nRows = nRows;
        this.nCols = nCols;
        dim = dimensions;
        init(stats, chunkNRows, chunkNCols);
    }

    protected void init(Grids_Grid g, Grids_Stats stats, int chunkNRows,
            int chunkNCols, long startRow, long startCol, long endRow,
            long endCol) {
        nRows = endRow - startRow + 1;
        nCols = endCol - startCol + 1;
        initDimensions(g, startRow, startCol);
        init(stats, chunkNRows, chunkNCols);
    }

    /**
     * Sets the references to this in the chunks.
     */
    protected void setReferenceInChunks() {
        Iterator<Grids_2D_ID_int> ite = data.keySet().iterator();
        while (ite.hasNext()) {
            data.get(ite.next()).initGrid(this);
        }
    }

    /**
     * @return A set of all chunk IDs.
     */
    public Set<Grids_2D_ID_int> getChunkIDs() {
        return data.keySet();
    }

    /**
     * Override to provide a more detailed fields description.
     *
     * @return A text description of the fields of this.
     */
    public String getFieldsDescription() {
        String r = "chunkNcols=" + chunkNCols + ", ";
        r += "chunkNrows=" + chunkNRows + ", ";
        r += "nChunkCols=" + nChunkCols + ", ";
        r += "nChunkRows=" + nChunkRows + ", ";
        r += "nCols=" + nCols + ", ";
        r += "nRows=" + nRows + ", ";
        r += "fs=" + fs.toString() + ", ";
        r += "id=" + fsID + ", ";
        r += "name=" + name + ", ";
        r += "dimensions=" + getDimensions().toString() + ", ";
        if (data == null) {
            r += "data=null, ";
        } else {
            r += "data.size()=" + data.size() + ", ";
        }
        Set<Grids_Grid> grids = env.getGrids();
        if (grids == null) {
            r += "grids=null, ";
        } else {
            r += "grids.size()=" + grids.size() + ", ";
        }
        r += getStats().toString();
        return r;
    }

    /**
     * @return A text description of this.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getFieldsDescription() + "]";
    }

    /**
     * @return The path to the directory in {@link #fs} where this is currently
     * stored.
     */
    public Generic_Path getDirectory() {
        return new Generic_Path(fs.getPath(fsID));
    }

    /**
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets {@link #name} to {@code name}.
     *
     * @param name What {@link #name} is set to.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return {@link #nCols}
     */
    public final long getNCols() {
        return nCols;
    }

    /**
     * @return {@link #nRows}.
     */
    public final long getNRows() {
        return nRows;
    }

    /**
     * @return {@link #nChunkRows}
     */
    public final int getNChunkRows() {
        return nChunkRows;
    }

    /**
     * Initialises NChunkRows.
     */
    protected final void initNChunkRows() {
        long cnr = (long) chunkNRows;
        if ((nRows % cnr) != 0) {
            nChunkRows = (int) (nRows / cnr) + 1;
        } else {
            nChunkRows = (int) (nRows / cnr);
        }
    }

    /**
     * @return {@link #nChunkCols}.
     */
    public final int getNChunkCols() {
        return nChunkCols;
    }

    /**
     * @return the number of chunks in this as a long.
     * @throws java.lang.Exception If the number of chunks is greater than
     * {@link Integer#MAX_VALUE}.
     */
    public final long getNChunks() throws Exception {
        long nChunks = (long) nChunkRows * (long) nChunkCols;
        if (nChunks > Integer.MAX_VALUE) {
            throw new Exception("Error nChunks > Integer.MAX_VALUE");
        }
        return nChunks;
    }

    /**
     * Initialises NChunkCols.
     */
    protected final void initNChunkCols() {
        long cnc = (long) this.chunkNCols;
        if ((nCols % cnc) != 0) {
            nChunkCols = (int) (nCols / cnc) + 1;
        } else {
            nChunkCols = (int) (nCols / cnc);
        }
    }

    /**
     * @return {@link #chunkNRows}
     */
    public final int getChunkNRows() {
        return chunkNRows;
    }

    /**
     * @param cr The chunk row for which the number of rows of cells in the
     * chunk is returned.
     * @return The number of rows of cells in the chunks in the chunk row
     * indexed by {@code cr}.
     */
    public final int getChunkNRows(int cr) {
        if (cr > -1 && cr < nChunkRows) {
            if (cr == (nChunkRows - 1)) {
                return getChunkNRowsFinalRowChunk();
            } else {
                return chunkNRows;
            }
        } else {
            return 0;
        }
    }

    /**
     * @return {@link #chunkNCols}
     */
    public final int getChunkNCols() {
        return chunkNCols;
    }

    /**
     * @param cc The chunk column for which the number of columns of cells in
     * the chunks is returned.
     * @return The number of columns of cells in the chunks in the chunk column
     * indexed by {@code cc}.
     */
    public final int getChunkNCols(int cc) {
        if (cc > -1 && cc < nChunkCols) {
            if (cc == (nChunkCols - 1)) {
                return getChunkNColsFinalColChunk();
            } else {
                return chunkNCols;
            }
        } else {
            return 0;
        }
    }

    /**
     * @return The number of rows of cells in the chunks in the final row of
     * chunks.
     */
    public final int getChunkNRowsFinalRowChunk() {
        return (int) (nRows - ((long) (nChunkRows - 1) * (long) chunkNRows));
    }

    /**
     * @return The number of columns of cells in the chunks in the final column
     * of chunks.
     */
    public final int getChunkNColsFinalColChunk() {
        return (int) (nCols - ((long) (nChunkCols - 1) * (long) chunkNCols));
    }

    /**
     * @return The number of rows in chunk with ID {@code i}.
     * @param i The ID of the chunk for which the number of rows in that chunk
     * is returned.
     */
    public final int getChunkNRows(Grids_2D_ID_int i) {
        if (i.getRow() < (nChunkRows - 1)) {
            return chunkNRows;
        } else {
            return getChunkNRowsFinalRowChunk();
        }
    }

    /**
     * @return The number of columns in chunk with ID {@code i}.
     * @param i The ID of the chunk for which the number of columns in that
     * chunk is returned.
     */
    public final int getChunkNCols(Grids_2D_ID_int i) {
        if (i.getCol() < (nChunkCols - 1)) {
            return chunkNCols;
        } else {
            return getChunkNColsFinalColChunk();
        }
    }

    /**
     * @return the Dimensions
     */
    public Grids_Dimensions getDimensions() {
        return dim;
    }

    /**
     * This method is for convenience.
     *
     * @return {@code dim.getCellsize()}.
     */
    public final BigDecimal getCellsize() {
        return dim.getCellsize();
    }

    /**
     * @param distance The length of a straight line for which the cell distance
     * is returned.
     * @return An integer value equal to the number of cells in a vertical or
     * horizontal direction that a line of distance would intersect. On the
     * boundary is in.
     */
    public final BigDecimal getCellDistance(BigDecimal distance) {
        BigDecimal[] dar = distance.divideAndRemainder(getCellsize());
        BigDecimal r = dar[0];
        if (dar[1].compareTo(BigDecimal.ZERO) == 1) {
            r = r.add(BigDecimal.ONE);
        }
        return r;
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
    public Set<Grids_2D_ID_int> getChunkIDs(BigDecimal distance,
            BigDecimal x, BigDecimal y, long row, long col, int dp,
            RoundingMode rm) {
        Set<Grids_2D_ID_int> r = new HashSet<>();
        long delta = distance.divideToIntegralValue(getCellsize())
                .longValueExact();
        for (long p = -delta; p <= delta; p++) {
            BigDecimal cellY = getCellY(row + p);
            for (long q = -delta; q <= delta; q++) {
                BigDecimal cellX = getCellX(col + q);
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
     * This is for getting all the chunk IDs between (rowMin, colMin) and
     * (rowMax, colMax). This way returns a subset of keys from {@link #data}.
     *
     * @return A set of chunk identifiers for all chunks in the range given by
     * rowMin, rowMax, colMin, colMax.
     *
     * @param rowMin The minimum end of the row range.
     * @param rowMax The maximum end of the row range.
     * @param colMin The minimum end of the column range.
     * @param colMax The maximum end of the column range.
     */
    public Set<Grids_2D_ID_int> getChunkIDs(long rowMin, long rowMax,
            long colMin, long colMax) {
        return getChunkIDs(getChunkRow(rowMin), getChunkRow(rowMax),
                getChunkCol(colMin), getChunkCol(colMax));
//        This implementation creates new Grids_2D_ID_int instances.
//        Set<Grids_2D_ID_int> r = new HashSet<>();
//        int cnr = getChunkNRows();
//        int cnc = getChunkNCols();
//        for (long row = rowMin; row <= rowMax; row += cnr) {
//            int cr = getChunkRow(row);
//            for (long col = colMin; col <= colMax; col += cnc) {
//                int cc = getChunkCol(col);
//                r.add(new Grids_2D_ID_int(cr, cc));
//            }
//        }
//        return r;
    }

    /**
     * This is for getting all the chunk IDs between (crMin, ccMin) and (crMax,
     * ccMax). This way returns a subset of keys from {@link #data}.
     *
     * @return A set of chunk identifiers for all chunks in the range given by
     * crMin, crMax, ccMin, ccMax.
     *
     * @param crMin The minimum chunk row.
     * @param crMax The maximum chunk row.
     * @param ccMin The minimum chunk column.
     * @param ccMax The maximum chunk column.
     */
    public Set<Grids_2D_ID_int> getChunkIDs(int crMin, int crMax, int ccMin,
            int ccMax) {
        return data.keySet().parallelStream()
                .filter(i -> i.getRow() >= crMin)
                .filter(i -> i.getRow() <= crMax)
                .filter(i -> i.getCol() >= ccMin)
                .filter(i -> i.getCol() <= ccMax).collect(Collectors.toSet());
//        // This implementation creates new Grids_2D_ID_int instances. 
//        Set<Grids_2D_ID_int> r = new HashSet<>();
//        for (int cr = crMin; cr <= crMax; cr++) {
//            for (int cc = ccMin; cc <= ccMax; cc++) {
//                r.add(new Grids_2D_ID_int(cr, cc));
//            }
//        }
//        return r;
    }

    /**
     * @return Chunk column index for the chunks intersecting the line
     * x-coordinate x.
     * @param x The x-coordinate of the line intersecting the chunk column index
     * returned.
     */
    public final int getChunkCol(BigDecimal x) {
        return getChunkCol(getCol(x));
    }

    /**
     * @return Chunk column index for the Grids_Chunk intersecting the cell
     * column index {@code col}.
     *
     * @param col The cell column index.
     */
    public final int getChunkCol(long col) {
        return (int) (col / (long) chunkNCols);
    }

    /**
     * @return Chunk cell column index of the cells that intersect the line
     * {@code x}.
     * @param x The x-coordinate of the line intersecting the chunk cell column
     * index returned.
     */
    public final int getChunkCellCol(BigDecimal x) {
        return getChunkCellCol(getCol(x));
    }

    /**
     * @return Chunk cell column index of the cells in the grid column
     * {@code col}.
     * @param col The column in the grid for which the chunk cell row index is
     * returned.
     */
    public final int getChunkCellCol(long col) {
        return (int) (col - (getChunkCol(col) * chunkNCols));
    }

    /**
     * @return Chunk cell row index of the cells that intersect the line y.
     * @param y The y-coordinate of the line for which the chunk cell row index
     * is returned.
     */
    public final int getChunkCellRow(BigDecimal y) {
        return getChunkCellRow(getRow(y));
    }

    /**
     * @return Chunk cell row index of the cells in the grid row {@code row}.
     * @param row The row in the grid for which the chunk cell row index is
     * returned.
     */
    public final int getChunkCellRow(long row) {
        return (int) (row - (getChunkRow(row) * chunkNRows));
    }

    /**
     * @param x The x-coordinate.
     * @return Cell column of the cells that intersect line {@code x}.
     */
    public final long getCol(BigDecimal x) {
        Grids_Dimensions d = getDimensions();
        BigDecimal dx = x.subtract(d.getXMin());
        long r = dx.divideToIntegralValue(d.getCellsize()).longValueExact();
        if (dx.compareTo(BigDecimal.ZERO) == -1) {
            return r - 1L;
        } else {
            return r;
        }
    }

    /**
     * Get the cell column.
     *
     * @param cc The chunk column.
     * @param ccc The chunk cell column.
     * @return {@code ((long) cc * (long) chunkNCols) + (long) ccc}
     */
    public final long getCol(int cc, int ccc) {
        return ((long) cc * (long) chunkNCols) + (long) ccc;
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
     * row index {@code row}.
     * @param row The cell row of the cells that's chunk row index is returned.
     */
    public final int getChunkRow(long row) {
        return (int) (row / (long) chunkNRows);
    }

    /**
     * @param y The y axis coordinate.
     * @return Cell row of the cells that intersect the y axis coordinate y.
     */
    public final long getRow(BigDecimal y) {
        Grids_Dimensions d = getDimensions();
        BigDecimal dy = y.subtract(d.getYMin());
        long r = dy.divideToIntegralValue(d.getCellsize()).longValueExact();
        if (dy.compareTo(BigDecimal.ZERO) == -1) {
            return r - 1L;
        } else {
            return r;
        }
    }

    /**
     * Get the cell row.
     *
     * @param cr The chunk row.
     * @param ccr The cell row in the chunk.
     * @return {@code ((long) cr * (long) chunkNRows) + (long) ccr}
     */
    public final long getRow(int cr, int ccr) {
        return ((long) cr * (long) chunkNRows) + (long) ccr;
    }

    /**
     * A new cell ID for the cell given by cell row index {@code row}, cell
     * column index {@code col}. An ID is returned even if that cell would not
     * be in the grid.
     *
     * @param row The cell row index.
     * @param col The cell column index.
     * @return A new cell ID for the cell given by cell row index {@code row}.
     */
    public final Grids_2D_ID_long getCellID(long row, long col) {
        return new Grids_2D_ID_long(row, col);
    }

    /**
     * A new cell ID for the x-coordinate {@code x}, y-coordinate {@code y}. An
     * ID is returned even if that cell would not be in the grid.
     *
     * @return A new cell ID for the x-coordinate {@code x}, y-coordinate
     * {@code y}.
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
    public void cache() throws IOException, Exception {
        swapChunks();
        Generic_IO.writeObject(this, getPathThisFile(getDirectory()));
    }

    /**
     * Attempts to swap a chunk in {@link #data} from {@link #worthSwapping}.
     * This method does not consider those chunks in {@link #env}.notToClear.
     *
     * @return The chunk ID of the chunk that was cleared or {@code null} if no
     * chunk was cleared.
     * @throws java.io.IOException If encountered.
     */
    public final Grids_2D_ID_int swapChunk() throws IOException, Exception {
        if (worthSwapping.isEmpty()) {
            return null;
        }
        Grids_2D_ID_int i = worthSwapping.stream().findAny().get();
        swapChunk(i); // This removes i from worthSwapping.
        return i;
    }

    /**
     * Attempts to swap a chunk in {@link #data} that is in
     * {@link #worthSwapping} and {@code s}.This method does not consider those
     * chunks in {@link #env}.notToClear.
     *
     * @param s The set of chunk IDs for which one chunk may be cleared.
     * @return The chunk ID of the chunk that was cleared or {@code null} if no
     * chunk was cleared.
     * @throws java.io.IOException If encountered.
     */
    public final Grids_2D_ID_int swapChunk(Set<Grids_2D_ID_int> s)
            throws IOException, Exception {
        if (worthSwapping.isEmpty()) {
            return null;
        }
        Set<Grids_2D_ID_int> s2 = worthSwapping.stream().filter(i
                -> s.contains(i)).collect(Collectors.toSet());
        Iterator<Grids_2D_ID_int> ite = s2.iterator();
        while (ite.hasNext()) {
            Grids_2D_ID_int i = ite.next();
            if (swapChunk(i)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Attempts to cache the chunk with chunk ID {@code i} if there is not
     * already an up-to-date cache.
     *
     * @param i The chunk ID of the chunk to be cached.
     * @return True if Grids_Chunk on file is up to date.
     * @throws java.io.IOException If encountered.
     */
    public final boolean cache(Grids_2D_ID_int i) throws IOException,
            Exception {
        boolean r = true;
        Grids_Chunk c = data.get(i);
        if (c != null) {
            if (!c.isCacheUpToDate()) {
                Path file = Paths.get(getDirectory().toString(),
                        i.getRow() + "_" + i.getCol());
                //Files.createDirectory(file.getParent());
                Generic_IO.writeObject(c, file);
                //System.gc();
                c.setCacheUpToDate(true);
            }
        } else {
            r = false;
        }
        return r;
    }

    /**
     * Attempts to swap chunks that have a chunk ID in {@code s}.
     *
     * @param s A Set containing the chunk IDs of the chunks to swap.
     * @return The number of chunks swapped.
     */
    public final int swapChunks(Set<Grids_2D_ID_int> s) {
        return worthSwapping.parallelStream().filter(i -> s.contains(i))
                .collect(Collectors.summingInt((Grids_2D_ID_int i) -> {
                    try {
                        if (swapChunk(i)) {
                            return 1;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                        env.env.log(ex.getMessage());
                    }
                    return 0;
                }));
//        int r = 0;
//        Iterator<Grids_2D_ID_int> ite = s.iterator();
//        while (ite.hasNext()) {
//            Grids_2D_ID_int cid = ite.next();
//            if (isWorthCaching(cid)) {
//                if (cache(cid)) {
//                    r ++;
//                }
//            }
//        }
//        return r;
    }

    /**
     * Attempts to swap a chunk and return the details of any swapping done.
     *
     * @param hoome If {@code true} then if in the initial attempt to swap a
     * chunk an OutOfMemoryError is thrown, then an attempt to handle this is
     * made by: clearing the memory reserve, caching a chunk from this grid, and
     * re-initialising the memory reserve (which may involve swapping out chunks
     * from other grids and perhaps also swapping out other data). If false then
     * OutOfMemoryErrors are caught and thrown.
     * @return A detailed account of what was swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_AccountDetail swapChunk_AccountDetail(boolean hoome)
            throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunk_AccountDetail();
            r.add(env.checkAndMaybeFreeMemory_AccountDetail(hoome));
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_AccountDetail r = swapChunk_AccountDetail();
                if (r.detail.isEmpty()) {
                    throw e;
                }
                r.add(env.initMemoryReserve_AccountDetail(hoome));
                r.add(swapChunk_AccountDetail(hoome));
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to swap a chunk and return the details of any swapping done.
     *
     * @return A detailed account of what was swapped.
     * @throws java.io.IOException If encountered.
     */
    public Grids_AccountDetail swapChunk_AccountDetail() throws IOException,
            Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Grids_2D_ID_int i = swapChunk();
        if (i != null) {
            Set<Grids_2D_ID_int> s = new HashSet<>(1);
            clearChunk(i);
            s.add(i);
            r.detail.put(this, s);
        }
        return r;
    }

    /**
     * Attempts to cache a chunk and return the id of the chunk cached. This is
     * one of the lowest level memory handling operation of this class.
     *
     * @param camfm If {@code true} check and maybe free memory.
     * @param hoome If {@code true} then an attempt is made to handle an
     * OutOfMemoryErrors encountered by swapping data.
     * @return An account of what was cached.
     * @throws java.io.IOException If encountered.
     */
    public Grids_2D_ID_int swapChunk_AccountChunk(boolean camfm, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_2D_ID_int r = swapChunk_AccountChunk();
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_2D_ID_int r = swapChunk_AccountChunk();
                if (r == null) {
                    if (!env.swapChunk(env.HOOMEF)) {
                        throw e;
                    }
                }
                env.initMemoryReserve(env.env);
                return swapChunk_AccountChunk(camfm, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempt to swap a chunk and return it's ID.
     *
     * @return An account of what was swapped.
     * @throws java.io.IOException If encountered.
     */
    public Grids_2D_ID_int swapChunk_AccountChunk() throws IOException, Exception {
        Grids_2D_ID_int i = swapChunk();
        if (i != null) {
            clearChunk(i);
        }
        return i;
    }

    /**
     * Swap all chunks.
     *
     * @return An account of what was cached.
     * @throws Exception If encountered.
     * @throws IOException If encountered.
     */
    public Grids_Account swapChunks_Account() throws IOException, Exception {
        Grids_Account r = new Grids_Account();
        for (int cri = 0; cri < nChunkRows; cri++) {
            for (int cci = 0; cci < nChunkCols; cci++) {
                if (swapChunk(new Grids_2D_ID_int(cri, cci))) {
                    r.add();
                }
            }
        }
        return r;
    }

    /**
     * Swap chunks in {@code s}.
     *
     * @param s A set of chunk IDs of chunks to swap.
     * @return An account of any swapping
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_Account swapChunks_Account(Set<Grids_2D_ID_int> s)
            throws IOException, Exception {
        Grids_Account r = new Grids_Account();
        Iterator<Grids_2D_ID_int> ite = s.iterator();
        while (ite.hasNext()) {
            if (swapChunk(ite.next())) {
                r.add();
            }
        }
        return r;
    }

    /**
     * @param s A set of chunk IDs of chunks no to swap.
     * @param camfm If {@code true} check and maybe free memory.
     * @param hoome If {@code true} then an attempt is made to handle an
     * OutOfMemoryErrors encountered by swapping data.
     * @return Chunk ID of any swapped chunk or {@code null}.
     * @throws IOException If encountered.
     */
    public Grids_2D_ID_int swapChunkExcept_AccountChunk(
            Set<Grids_2D_ID_int> s, boolean camfm,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_2D_ID_int r = swapChunkExcept_AccountChunk(s);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_2D_ID_int r = swapChunkExcept_AccountChunk(s);
                if (r == null) {
                    if (!env.swapChunk(env.HOOMEF)) {
                        throw e;
                    }
                }
                env.initMemoryReserve(env.env);
                return r;
            } else {
                throw e;
            }
        }
    }

    /**
     * Swap a chunk except a chunk with chunk ID in {@code s}.
     *
     * @param s The set with chunk IDs not to swap.
     * @return The chunk ID of any chunk swapped.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public Grids_2D_ID_int swapChunkExcept_AccountChunk(
            Set<Grids_2D_ID_int> s) throws IOException, Exception {
        Iterator<Grids_2D_ID_int> ite = worthSwapping.iterator();
        while (ite.hasNext()) {
            Grids_2D_ID_int i = ite.next();
            if (!s.contains(i)) {
                cache(i);
                clearChunk(i);
                return i;
            }
        }
//        for (chunkRow = 0; chunkRow < NChunkRows; chunkRow++) {
//            for (chunkCol = 0; chunkCol < NChunkCols; chunkCol++) {
//                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
//                if (!chunkIDs.contains(chunkID)) {
//                    if (isWorthCaching(chunkID)) {
//                        swapChunk(chunkID);
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
        return data.get(i) instanceof Grids_ChunkDoubleSinglet
                || data.get(i) instanceof Grids_ChunkIntSinglet;
    }

    /**
     * Caches the chunk with chunk ID {@code i} to file.
     *
     * @param i The chunk ID.
     * @param camfm If {@code true} check and maybe free memory.
     * @param hoome If {@code true} then an attempt is made to handle an
     * OutOfMemoryErrors encountered by swapping data.
     * @throws java.io.IOException If encountered.
     */
    public void swap(Grids_2D_ID_int i, boolean camfm, boolean hoome)
            throws IOException, Exception {
        try {
            swapChunk(i);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                freeSomeMemoryAndResetReserve(i, e);
                swap(i, camfm, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Checks the chunk with chunk ID {@code i}. If there is not already an up
     * to date cache then it is cached. If the chunk is cached, then it is
     * cleared from memory.
     *
     * @param i The chunk ID of the chunk to cache (if the cache is not already
     * up to date) and anyway clear.
     * @return {@code 1} if a chunk is cleared and {@code 0} otherwise.
     * @throws java.io.IOException If encountered.
     */
    public boolean swapChunk(Grids_2D_ID_int i) throws IOException,
            Exception {
        if (cache(i)) {
            clearChunk(i);
            return true;
        }
        return false;
    }

    /**
     * Attempts to write to file and clear from the cache any chunk in this.This
     * is one of the lowest level memory handling operation of this class.
     *
     * @param camfm If {@code true} check and maybe free memory.
     * @param hoome If {@code true} then an attempt is made to handle an
     * OutOfMemoryErrors encountered by swapping data.
     * @return Chunk ID of a swapped chunk.
     * @throws java.io.IOException If encountered.
     */
    public Grids_2D_ID_int swapChunk(boolean camfm, boolean hoome) throws IOException,
            Exception {
        try {
            Grids_2D_ID_int r = swapChunk();
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                freeSomeMemoryAndResetReserve(e);
                return swapChunk(camfm, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Attempts to write to file and clear from the cache a Grids_Chunk in
     * this._AbstractGrid2DSquareCell_Set.
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
    public Grids_AccountDetail swapChunkExcept_AccountDetail(Grids_2D_ID_int cid, boolean camfm,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunkExcept_AccountDetail(cid);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(cid);
                if (r.detail.isEmpty()) {
                    r.add(env.swapChunkExcept_AccountDetail(this, cid, false));
                }
                r.add(env.initMemoryReserve_AccountDetail(this, cid, hoome));
                r.add(swapChunkExcept_AccountDetail(cid));
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
    public Grids_AccountDetail swapChunkExcept_AccountDetail(Grids_2D_ID_int chunkID)
            throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        for (int cri = 0; cri < nChunkRows; cri++) {
            for (int cci = 0; cci < nChunkCols; cci++) {
                Grids_2D_ID_int bid = new Grids_2D_ID_int(cri, cci);
                if (!bid.equals(chunkID)) {
                    if (isWorthCaching(bid)) {
                        cache(bid);
                        clearChunk(bid);
                        Set<Grids_2D_ID_int> chunks;
                        chunks = new HashSet<>(1);
                        chunks.add(bid);
                        r.detail.put(this, chunks);
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
    public Grids_2D_ID_int swapChunkExcept_AccountChunk(
            Grids_2D_ID_int cid, boolean camfm, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_2D_ID_int r = swapChunkExcept_AccountChunk(cid);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_2D_ID_int r = swapChunkExcept_AccountChunk(cid);
                if (r == null) {
                    if (env.swapChunkExcept_Account(this, cid, false, false).detail < 1L) {
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
    public Grids_2D_ID_int swapChunkExcept_AccountChunk(
            Grids_2D_ID_int cid) throws IOException, Exception {
        for (int cri = 0; cri < nChunkRows; cri++) {
            for (int cci = 0; cci < nChunkCols; cci++) {
                Grids_2D_ID_int r = new Grids_2D_ID_int(cri, cci);
                if (!r.equals(cid)) {
                    if (isWorthCaching(r)) {
                        cache(r);
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
    public final Grids_AccountDetail swapChunksExcept_AccountDetail(Grids_2D_ID_int i, boolean camfm,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunksExcept_AccountDetail(i);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(i);
                if (r.detail.isEmpty()) {
                    r = env.swapChunkExcept_AccountDetail(this, i, false);
                    if (r.detail.isEmpty()) {
                        throw e;
                    }
                }
                r.add(env.initMemoryReserve_AccountDetail(this, i, hoome));
                r.add(swapChunksExcept_AccountDetail(i, camfm, hoome));
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
    public final Grids_AccountDetail swapChunksExcept_AccountDetail(
            Grids_2D_ID_int i) throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Set<Grids_2D_ID_int> s = new HashSet<>();
        for (int chunkRow = 0; chunkRow < nChunkRows; chunkRow++) {
            for (int chunkCol = 0; chunkCol < nChunkCols; chunkCol++) {
                Grids_2D_ID_int i2 = new Grids_2D_ID_int(chunkRow, chunkCol);
                if (!i.equals(i2)) {
                    if (isWorthCaching(i2)) {
                        cache(i2);
                        clearChunk(i2);
                        s.add(i2);
                    }
                }
            }
        }
        r.detail.put(this, s);
        return r;
    }

    /**
     * Swap out or clear from memory all chunks in this that are in
     * {@link #worthSwapping}.
     */
    public void swapChunks() {
        worthSwapping.parallelStream().forEach(i -> {
            try {
                swapChunk(i);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                env.env.log(ex.getMessage());
            }
        });
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
    public Grids_Account swapChunksExcept_Account(Set<Grids_2D_ID_int> s,
            boolean camfm, boolean hoome) throws IOException, Exception {
        try {
            Grids_Account r = new Grids_Account();
            r.add(swapChunksExcept(s));
            if (camfm) {
                r.add(env.checkAndMaybeFreeMemory_Account(hoome));
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_Account r = env.swapChunkExcept_Account(this, s, false);
                if (r.detail == 0) {
                    throw e;
                }
                r.add(env.initMemoryReserve_Account(this, s, hoome));
                r.add(swapChunksExcept_Account(s, camfm, hoome));
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
    public final Grids_AccountDetail swapChunksExcept_AccountDetail(
            Set<Grids_2D_ID_int> s,
            boolean camfm, boolean hoome) throws IOException,
            Exception {
        try {
            Grids_AccountDetail r = new Grids_AccountDetail();
            swapChunksExcept(s, r);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(s);
                if (r.detail.isEmpty()) {
                    env.addToNotToClear(this, s);
                    r = env.swapChunk_AccountDetail(env.HOOMEF);
                    if (r.detail.isEmpty()) {
                        throw e;
                    }
                }
                r.add(env.initMemoryReserve_AccountDetail(this, s, hoome));
                r.add(swapChunksExcept_AccountDetail(s, camfm, hoome));
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
     * @param a An account for adding details of which chunks have been cleared.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     */
    public final void swapChunksExcept(Set<Grids_2D_ID_int> s, Grids_AccountDetail a)
            throws IOException, Exception {
        Set<Grids_2D_ID_int> s2 = new HashSet<>();
        for (int cri = 0; cri < nChunkRows; cri++) {
            for (int cci = 0; cci < nChunkCols; cci++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cri, cci);
                if (!s.contains(i)) {
                    if (isWorthCaching(i)) {
                        cache(i);
                        clearChunk(i);
                        s2.add(i);
                    }
                }
            }
        }
        a.detail.put(this, s2);
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
    public final Grids_Account swapChunkExcept_Account(Grids_2D_ID_int i,
            boolean camfm, boolean hoome) throws IOException, Exception {
        try {
            Grids_Account r = new Grids_Account();
            if (swapChunkExcept(i)) {
                r.add();
            }
            if (camfm) {
                r.add(env.checkAndMaybeFreeMemory_Account(hoome));
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_Account r = env.swapChunkExcept_Account(this, i, false, false);
                if (r.detail < 1) {
                    r = env.swapChunkExcept_Account(this, i, false, false);
                    if (r.detail < 1) {
                        throw e;
                    }
                }
                r.add(env.initMemoryReserve_Account(this, i, hoome));
                r.add(swapChunksExcept_Account(i, camfm, hoome));
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
    public final boolean swapChunkExcept(Grids_2D_ID_int i)
            throws IOException, Exception {
        return swapChunk(worthSwapping.stream().filter(i2 -> i2 != i).findAny()
                .get());
//        for (int cri = 0; cri < nChunkRows; cri++) {
//            for (int cci = 0; cci < nChunkCols; cci++) {
//                Grids_2D_ID_int i2 = new Grids_2D_ID_int(cri, cci);
//                if (!i.equals(i2)) {
//                    if (isWorthCaching(i2)) {
//                        cache(i2);
//                        clearChunk(i2);
//                        return 1;
//                    }
//                }
//            }
//        }
//        return 0;
    }

    /**
     * Attempts to swap out or clear from memory all chunks in this except that
     * with ID {@code i}.
     *
     * @param i The ID of the chunk not to clear from memory.
     * @param camfm If {@code true} check and maybe free memory.
     * @param hoome If true then an attempt is made to handle any encountered
     * {@link OutOfMemoryError}.
     * @return The number of chunks cleared from memory.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.Exception If encountered.
     * @throws OutOfMemoryError If this cannot be handled.
     */
    public final Grids_Account swapChunksExcept_Account(Grids_2D_ID_int i,
            boolean camfm, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_Account r = new Grids_Account();
            r.add(Grids_Grid.this.swapChunksExcept(i));
            if (camfm) {
                r.add(env.checkAndMaybeFreeMemory_Account(i, hoome));
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_Account r = env.swapChunkExcept_Account(this, i, false, false);
                if (r.detail < 1) {
                    r = env.swapChunkExcept_Account(this, i, false, false);
                    if (r.detail < 1) {
                        throw e;
                    }
                }
                r.add(env.initMemoryReserve_Account(this, i, hoome));
                r.add(swapChunksExcept_Account(i, camfm, hoome));
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
    public final int swapChunksExcept(Grids_2D_ID_int i)
            throws IOException, Exception {
        return worthSwapping.parallelStream().filter(i2 -> i2 != i)
                .collect(Collectors.summingInt((Grids_2D_ID_int i2) -> {
                    try {
                        if (swapChunk(i2)) {
                            return 1;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                        env.env.log(ex.getMessage());
                    }
                    return 0;
                }));
//        int r = 0;
//        for (int cri = 0; cri < nChunkRows; cri++) {
//            for (int cci = 0; cci < nChunkCols; cci++) {
//                Grids_2D_ID_int id2 = new Grids_2D_ID_int(cri, cci);
//                r += swapChunkExcept(i);
//                if (i != id2) {
//                    if (isWorthCaching(i)) {
//                        cache(i);
//                        clearChunk(i);
//                        r++;
//                    }
//                }
//            }
//        }
//        return r;
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
    public final int swapChunksExcept(Set<Grids_2D_ID_int> s)
            throws IOException, Exception {
        return worthSwapping.parallelStream().filter(i2 -> !s.contains(i2))
                .collect(Collectors.summingInt((Grids_2D_ID_int i2) -> {
                    try {
                        if (swapChunk(i2)) {
                            return 1;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace(System.err);
                        env.env.log(ex.getMessage());
                    }
                    return 0;
                }));
//        long r = 0L;
//        for (int cri = 0; cri < nChunkRows; cri++) {
//            for (int cci = 0; cci < nChunkCols; cci++) {
//                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(cri, cci);
//                if (!s.contains(chunkID)) {
//                    if (isWorthCaching(chunkID)) {
//                        cache(chunkID);
//                        clearChunk(chunkID);
//                        r++;
//                    }
//                }
//            }
//        }
//        return r;
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
    public final Grids_AccountDetail swapChunkExcept_AccountDetail(Set<Grids_2D_ID_int> s, boolean camfm,
            boolean hoome) throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunkExcept_AccountDetail(s);
            if (camfm) {
                env.checkAndMaybeFreeMemory(hoome);
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_AccountDetail r = swapChunkExcept_AccountDetail(s);
                if (r == null) {
                    if (!env.swapChunk(env.HOOMEF)) {
                        throw e;
                    }
                }
                env.initMemoryReserve(env.env);
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
    public final Grids_AccountDetail swapChunkExcept_AccountDetail(
            Set<Grids_2D_ID_int> s) throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        for (int cri = 0; cri < nChunkRows; cri++) {
            for (int cci = 0; cci < nChunkCols; cci++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cri, cci);
                if (!s.contains(i)) {
                    if (isWorthCaching(i)) {
                        cache(i);
                        clearChunk(i);
                        r.detail.put(this, s);
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
    public final Grids_AccountDetail swapChunks_AccountDetail(boolean camfm, boolean hoome)
            throws IOException, Exception {
        try {
            Grids_AccountDetail r = swapChunks_AccountDetail();
            if (camfm) {
                r.add(env.checkAndMaybeFreeMemory_AccountDetail(hoome));
            }
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                Grids_AccountDetail r = swapChunks_AccountDetail();
                if (r.detail.isEmpty()) {
                    r = env.swapChunk_AccountDetail(false);
                    if (r.detail.isEmpty()) {
                        throw e;
                    }
                }
                r.add(env.initMemoryReserve_AccountDetail(hoome));
                r.add(swapChunks_AccountDetail(camfm, hoome));
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
    public final Grids_AccountDetail swapChunks_AccountDetail()
            throws IOException, Exception {
        Grids_AccountDetail r = new Grids_AccountDetail();
        Set<Grids_2D_ID_int> s = new HashSet<>();
        for (int cri = 0; cri < nChunkRows; cri++) {
            for (int cci = 0; cci < nChunkCols; cci++) {
                Grids_2D_ID_int i = new Grids_2D_ID_int(cri, cci);
                if (isWorthCaching(i)) {
                    cache(i);
                    clearChunk(i);
                    s.add(i);
                }
            }
        }
        if (s.isEmpty()) {
            return r;
        }
        r.detail.put(this, s);
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
    public final Grids_Account swapChunks_Account(int cri0, int cci0, int cri1,
            int cci1, boolean hoome) throws IOException, Exception {
        try {
            Grids_Account r = swapChunks_Account(cri0, cci0, cri1, cci1);
            r.add(env.checkAndMaybeFreeMemory_Account(hoome));
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve(env.env);
                if (swapChunk() == null) {
                    throw e;
                }
                Grids_Account r = new Grids_Account();
                r.detail = 1L;
                r.add(env.initMemoryReserve_Account(env.HOOMEF));
                r.add(swapChunks_Account(cri0, cci0, cri1, cci1, hoome));
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
    public final Grids_Account swapChunks_Account(int cri0, int cci0, int cri1,
            int cci1) throws IOException, Exception {
        Grids_Account r = new Grids_Account();
        if (cri0 != cri1) {
            for (int cci = cci0; cci < nChunkCols; cci++) {
                if (swapChunk(new Grids_2D_ID_int(cri0, cci))) {
                    r.add();
                }
            }
            for (int cri = cri0 + 1; cri < cri1; cri++) {
                for (int cci = 0; cci < nChunkCols; cci++) {
                    if (swapChunk(new Grids_2D_ID_int(cri0, cci))) {
                        r.add();
                    }
                }
            }
            for (int cci = 0; cci < cci1; cci++) {
                if (swapChunk(new Grids_2D_ID_int(cri0, cci))) {
                    r.add();
                }
            }
        } else {
            for (int cci = cci0; cci < cci1 + 1; cci++) {
                if (swapChunk(new Grids_2D_ID_int(cri0, cci))) {
                    r.add();
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
    public final boolean isLoaded(Grids_2D_ID_int i) {
        return data.get(i) != null;
//        return data.containsKey(chunkID);
    }

    /**
     * @return {@code true} if the chunk given by chunk ID {@code i} is worth
     * caching - as determined by whether it is a single .
     * @param i The ID of the chunk tested as to whether it is worth caching.
     */
    public final boolean isWorthCaching(Grids_2D_ID_int i) {
        if (isLoaded(i)) {
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
        data.replace(chunkID, null);
        worthSwapping.remove(chunkID);
        //System.gc();
    }

    /**
     * Clear all chunks by setting them to null in {@link #data}.
     */
    public final void clearChunks() {
        data.keySet().parallelStream().forEach(i -> data.replace(i, null));
        worthSwapping = new HashSet<>();
        //System.gc();
    }

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
        return getCellIDs(getCellX(col), getCellY(row), row, col, distance, dp,
                rm);
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
        Set<Grids_2D_ID_long> r2 = new HashSet<>();
        long delta = distance.divideToIntegralValue(getCellsize()).longValueExact();
        for (long p = -delta; p <= delta; p++) {
            BigDecimal cellY = getCellY(row + p);
            for (long q = -delta; q <= delta; q++) {
                BigDecimal cellX = getCellX(col + q);
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
     * @return Nearest cell ID to point at x-coordinate {@code x}, y-coordinate
     * {@code y}.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public Grids_2D_ID_long getNearestCellID(BigDecimal x, BigDecimal y) {
        return getNearestCellID(x, y, getRow(y), getCol(x));
    }

    /**
     * @return Nearest cell ID to cell row {@code r}, cell column {@code c}.
     * @param r The cell row.
     * @param c The cell column.
     */
    public Grids_2D_ID_long getNearestCellID(long r, long c) {
        return getNearestCellID(getCellX(c), getCellY(r), r, c);
    }

    /**
     * @return Nearest cell ID to point given by x-coordinate x, y-coordinate y
     * in cell row {@code r}, cell column {@code c}.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param row The cell row.
     * @param col The cell column.
     */
    public Grids_2D_ID_long getNearestCellID(BigDecimal x, BigDecimal y,
            long row, long col) {
        Grids_2D_ID_long cellID;
        boolean isInGrid = isInGrid(x, y);
        if (!isInGrid) {
            long p;
            long q;
            if (x.compareTo(dim.getXMax()) >= 0) {
                q = nCols - 1;
                if (y.compareTo(dim.getYMax()) == 1) {
                    p = 0;
                } else {
                    if (y.compareTo(dim.getYMin()) == -1) {
                        p = nRows - 1;
                    } else {
                        p = getRow(y);
                    }
                }
            } else {
                if (x.compareTo(dim.getXMin()) == -1) {
                    q = 0;
                    if (y.compareTo(dim.getYMax()) >= 0) {
                        p = 0;
                    } else {
                        if (y.compareTo(dim.getYMin()) == -1) {
                            p = nRows - 1;
                        } else {
                            p = getRow(y);
                        }
                    }
                } else {
                    q = getCol(x);
                    if (y.compareTo(dim.getYMax()) >= 0) {
                        p = 0;
                    } else {
                        p = nRows - 1;
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
    public final BigDecimal getHeight() {
        return dim.getYMax().subtract(dim.getYMin());
    }

    /**
     * @return Width of the grid.
     */
    public final BigDecimal getWidth() {
        return dim.getXMax().subtract(dim.getXMin());
    }

    /**
     * For testing if two grids have the same dimensions and values.
     *
     * @param g The grid to test against.
     * @return {@code true} if this is the same as {@code g} and false
     * otherwise.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public abstract boolean isSameDimensionsAndValues(Grids_Grid g) throws
            IOException, Exception;

    /**
     * If the dimensions are the same, then so are the numbers of rows and
     * columns.
     *
     * @param g The grid to compare with.
     * @return {@code true} if this has the same dimensions as g.
     */
    public boolean isSameDimensions(Grids_Grid g) {
        return dim.equals(g.dim);
    }

    /**
     * For finding out if grid {@code g} and this have the same dimensions and
     * alignment of chunks.
     *
     * @param g A grid to compare.
     * @return {@code true} if grid {@code g} and this have the same dimensions
     * and alignment of chunks.
     */
    public boolean isSameDimensionsAndChunks(Grids_Grid g) {
        Grids_Dimensions gDim = g.getDimensions();
        if (dim.equals(gDim)) {
            if (chunkNRows == g.getChunkNRows()) {
                if (chunkNCols == g.getChunkNCols()) {
                    if (nChunkRows == g.getNChunkRows()) {
                        if (nChunkCols == g.getNChunkCols()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Two grids are coincident if they overlap and have cells that are the same
     * size and for those that overlap they have the same centroids.
     *
     * @param g The grid to test for coincidence.
     * @return {@code true} if the two grids are coincident.
     */
    public boolean isCoincident(Grids_Grid g) {
        if (dim.getCellsize().compareTo(g.dim.getCellsize()) != 0) {
            return false;
        }
        if (dim.equals(g.dim)) {
            return true;
        }
        if (!dim.intersects(g.dim)) {
            return false;
        }
        BigDecimal gXMin = g.dim.getXMin();
        BigDecimal xMin = dim.getXMin();
        if (xMin.compareTo(gXMin) == -1) {
            BigDecimal x = xMin;
            BigDecimal cs = dim.getCellsize();
            do {
                x = x.add(cs);
            } while (x.compareTo(gXMin) == -1);
            if (x.compareTo(gXMin) != 0) {
                return false;
            }
        } else if (xMin.compareTo(gXMin) == 1) {
            BigDecimal x = gXMin;
            BigDecimal cs = dim.getCellsize();
            do {
                x = x.add(cs);
            } while (x.compareTo(xMin) == -1);
            if (x.compareTo(xMin) != 0) {
                return false;
            }
        }
        BigDecimal gYMin = g.dim.getYMin();
        BigDecimal yMin = dim.getYMin();
        if (yMin.compareTo(gYMin) == -1) {
            BigDecimal y = yMin;
            BigDecimal cs = dim.getCellsize();
            do {
                y = y.add(cs);
            } while (y.compareTo(gYMin) == -1);
            if (y.compareTo(gYMin) != 0) {
                return false;
            }
        } else if (yMin.compareTo(gYMin) == 1) {
            BigDecimal y = gYMin;
            BigDecimal cs = dim.getCellsize();
            do {
                y = y.add(cs);
            } while (y.compareTo(yMin) == -1);
            if (y.compareTo(yMin) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * For finding out if point given by x-coordinate {@code x}, y-coordinate
     * {@code y} is in this grid.
     *
     * @return {@code true} if point given by x-coordinate {@code x},
     * y-coordinate {@code y} is in this grid. Anything on the boundary is
     * considered to be in.
     * @param x The x-coordinate of the point to test.
     * @param y The y-coordinate of the point to test.
     */
    public final boolean isInGrid(BigDecimal x, BigDecimal y) {
        return x.compareTo(dim.getXMin()) != -1
                && y.compareTo(dim.getYMin()) != -1
                && x.compareTo(dim.getXMax()) != 1
                && y.compareTo(dim.getYMax()) != 1;
    }

    /**
     * For finding out if the cell in row {@code r} and column {@code c} is in
     * this grid.
     *
     * @param r The cell row to test.
     * @param c The cell column to test.
     * @return True if (row, col) in the Grid.
     */
    public final boolean isInGrid(long r, long c) {
        return r >= 0 && r < nRows && c >= 0 && c < nCols;
    }

    /**
     * For finding out if the cell with cell ID {@code i} is in this grid.
     *
     * @param i The cell ID to test.
     * @return {@code true} if cell with cell ID {@code i} is in the Grid.
     */
    public final boolean isInGrid(Grids_2D_ID_long i) {
        return isInGrid(i.getRow(), i.getCol());
    }

    /**
     * For finding out if the chunk with chunk ID {@code i} is in this grid.
     *
     * @param i The chunk ID to test.
     * @return True if chunk with ID {@code i} is in the Grid.
     */
    public final boolean isInGrid(Grids_2D_ID_int i) {
        return isInGrid(i.getRow(), i.getCol());
    }

    /**
     * For finding out if the cell with chunk cell row {@code ccr} and chunk
     * cell column {@code ccc} in chunk in chunk row {@code chunkRow} and chunk
     * column {@code cc} is in the dimensions of this grid. This does not
     * necessitate loading the chunk.
     *
     * @param cr The chunk row index to test.
     * @param cc The chunk column index to test.
     * @param ccr The row index in the chunk to test.
     * @param ccc The column index in the chunk to test.
     * @return {@code true} if cell with chunk cell row {@code ccr} and chunk
     * cell column {@code ccc} in chunk in chunk row {@code chunkRow} and chunk
     * column {@code cc} is in the dimensions of this grid.
     */
    public final boolean isInGrid(int cr, int cc, int ccr, int ccc) {
        return isInGrid(getRow(cr, ccr), getCol(cc, ccc));
    }

    /**
     * @param col The cell col index for which the centroid x-coordinate is
     * returned.
     * @return The x-coordinate of the centroid of col as a BigDecimal.
     */
    public final BigDecimal getCellX(long col) {
        return dim.getXMin().add(dim.getCellsize().multiply(
                BigDecimal.valueOf(col))).add(dim.getHalfCellsize());
    }

    /**
     * @return The x-coordinate of the centroid for cell with cell with ID
     * {@code i}.
     * @param i The chunk ID.
     */
    public final BigDecimal getCellX(Grids_2D_ID_long i) {
        return getCellX(i.getCol());
    }

    /**
     * @param row The cell row index for which the centroid y-coordinate is
     * returned.
     * @return The y-coordinate of the centroid for row as a BigDecimal.
     */
    public final BigDecimal getCellY(long row) {
        return dim.getYMin().add(dim.getCellsize().multiply(
                BigDecimal.valueOf(row))).add(dim.getHalfCellsize());
    }

    /**
     * @param i The cell ID for which the centroid x-coordinate is returned.
     * @return The y-coordinate of the centroid of cell with cell ID {@code i}.
     */
    public final BigDecimal getCellY(Grids_2D_ID_long i) {
        return getCellY(i.getRow());
    }

    /**
     * @param halfCellsize Half the grid cellsize.
     * @return BigDecimal[] r where;
     * <ul>
     * <li>r[0] xmin, left most x-coordinate of cell that intersects point at
     * (x,y)</li>
     * <li>r[1] ymin, lowest y-coordinate of cell that intersects point at
     * (x,y)</li>
     * <li>r[2] xmax, right most x-coordinate of cell that intersects point at
     * (x,y)</li>
     * <li>r[3] ymax, highest y-coordinate of cell that intersects point at
     * (x,y).</li>
     * </ul>
     * @param row The row index of the cell for which the bounds are returned.
     * @param col The column index of the cell for which the bounds are
     * returned.
     */
    public final BigDecimal[] getCellBounds(BigDecimal halfCellsize,
            long row, long col) {
        BigDecimal[] r = new BigDecimal[4];
        BigDecimal x = getCellX(col);
        BigDecimal y = getCellY(row);
        r[0] = x.subtract(halfCellsize);
        r[1] = y.subtract(halfCellsize);
        r[2] = x.add(halfCellsize);
        r[3] = y.add(halfCellsize);
        return r;
    }

    /**
     * @return The next chunk ID to {@code i} in a row major order of chunk IDs,
     * or {@code null}.
     * @param i The chunkID of the current chunk in a row major order of chunks.
     * @param ncr The number of chunk rows.
     * @param ncc The number of chunk columns.
     */
    public Grids_2D_ID_int getNextChunkID(Grids_2D_ID_int i, int ncr, int ncc) {
        int r = i.getRow();
        int c = i.getCol();
        if (c < ncc - 1) {
            return new Grids_2D_ID_int(r, c + 1);
        } else {
            if (r < ncr - 1) {
                return new Grids_2D_ID_int(r + 1, 0);
            }
        }
        return null;
    }

    /**
     * @return The previous chunk ID to {@code i} in a row major order of chunk
     * IDs, or {@code null}.
     * @param i The chunkID of the current chunk in a row major order of chunks.
     * @param ncr The number of chunk rows.
     * @param ncc The number of chunk columns.
     */
    public Grids_2D_ID_int getPreviousChunkID(Grids_2D_ID_int i, int ncr,
            int ncc) {
        int r = i.getRow();
        int c = i.getCol();
        if (c > 0) {
            return new Grids_2D_ID_int(r, c - 1);
        } else {
            if (r > 0) {
                return new Grids_2D_ID_int(r - 1, 0);
            }
        }
        return null;
    }

    /**
     * For freeing memory and resetting the memory reserve.
     *
     * @param notToClear Chunks not to clear in memory management.
     * @param e An OutOfMemoryError that may get thrown.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public void freeSomeMemoryAndResetReserve(
            HashMap<Grids_Grid, Set<Grids_2D_ID_int>> notToClear,
            OutOfMemoryError e) throws IOException, Exception {
        Iterator<Grids_Grid> ite = notToClear.keySet().iterator();
        while (ite.hasNext()) {
            Grids_Grid g = ite.next();
            if (env.swapChunkExcept_Account(g, notToClear.get(g), false).detail > 0) {
                env.initMemoryReserve(notToClear, env.HOOMET);
                return;
            }
        }
        throw e;
    }

    /**
     * For freeing memory and resetting the memory reserve.
     *
     * @param cr Chunk row index of a chunk not to swap.
     * @param cc Chunk column index of a chunk not to swap.
     * @param e An OutOfMemoryError that may get thrown.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public void freeSomeMemoryAndResetReserve(int cr, int cc,
            OutOfMemoryError e) throws IOException, Exception {
        //env.clearMemoryReserve(env.env);
        freeSomeMemoryAndResetReserve(new Grids_2D_ID_int(cr, cc), e);
    }

    /**
     * For freeing memory and resetting the memory reserve.
     *
     * @param s A set containing chunk IDs not to swap.
     * @param e An OutOfMemoryError that may get thrown.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public void freeSomeMemoryAndResetReserve(Set<Grids_2D_ID_int> s,
            OutOfMemoryError e) throws IOException, Exception {
        if (env.swapChunkExcept_Account(this, s, false).detail < 1L) {
            throw e;
        }
        env.initMemoryReserve(this, s, env.HOOMET);
    }

    /**
     * For freeing memory and resetting the memory reserve.
     *
     * @param i Chunk ID of a chunk not to clear.
     * @param e An OutOfMemoryError that may get thrown.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public void freeSomeMemoryAndResetReserve(Grids_2D_ID_int i,
            OutOfMemoryError e) throws IOException, Exception {
        if (env.swapChunkExcept_Account(this, i, false, false).detail == 0) {
            throw e;
        }
        env.initMemoryReserve(this, i, env.HOOMET);
    }

    /**
     * For freeing memory and resetting the memory reserve.
     *
     * @param e An OutOfMemoryError that may get thrown.
     * @throws IOException If encountered.
     * @throws Exception If encountered.
     */
    public void freeSomeMemoryAndResetReserve(OutOfMemoryError e)
            throws IOException, Exception {
        if (!env.swapChunk(env.HOOMEF)) {
            throw e;
        }
        env.initMemoryReserve(env.env);
    }

    /**
     * @return {@link #data}
     */
    public TreeMap<Grids_2D_ID_int, Grids_Chunk> getData() {
        return data;
    }

    /**
     * Initialises {@link #dim} from {@code header}. This assumes that
     * {@link #nRows} and {@link #nCols} are already initialised.
     *
     * @param header The header.
     * @param r The start row index.
     * @param c The start column index.
     */
    public void initDimensions(Header header, long r, long c) {
        BigDecimal cellsize = header.cellsize;
        BigDecimal xMin = header.xll.add(cellsize.multiply(new BigDecimal(c)));
        BigDecimal yMin = header.yll.add(cellsize.multiply(new BigDecimal(r)));
        BigDecimal xMax = xMin.add(new BigDecimal(Long.toString(nCols))
                .multiply(cellsize));
        BigDecimal yMax = yMin.add(new BigDecimal(Long.toString(nRows))
                .multiply(cellsize));
        dim = new Grids_Dimensions(xMin, xMax, yMin, yMax, cellsize);
    }

    /**
     * For initialising {@link #dim}. This assumes that {@link #nRows} and
     * {@link #nCols} are already initialised.
     *
     * @param g The grid.
     * @param r The start row index in {@code g}.
     * @param c The start column index in {@code g}.
     */
    public void initDimensions(Grids_Grid g, long r, long c) {
        dim = g.getDimensions(); // temporary assignment
        BigDecimal startColIndexBigDecimal = new BigDecimal(c);
        BigDecimal startRowIndexBigDecimal = new BigDecimal(r);
        BigDecimal nRowsBigDecimal = new BigDecimal(nRows);
        BigDecimal nColsBigDecimal = new BigDecimal(nCols);
        BigDecimal xMin;
        BigDecimal yMin;
        BigDecimal xMax;
        BigDecimal yMax;
        BigDecimal cellsize;
        if (dim == null) {
            cellsize = BigDecimal.ONE;
            xMin = startColIndexBigDecimal;
            yMin = startRowIndexBigDecimal;
            xMax = xMin.add(nColsBigDecimal);
            yMax = yMin.add(nRowsBigDecimal);
        } else {
            cellsize = dim.getCellsize();
            xMin = dim.getXMin().add(startColIndexBigDecimal.multiply(cellsize));
            yMin = dim.getYMin().add(startRowIndexBigDecimal.multiply(cellsize));
            xMax = dim.getXMin().add(nColsBigDecimal.multiply(cellsize));
            yMax = dim.getYMin().add(nRowsBigDecimal.multiply(cellsize));
        }
        dim = new Grids_Dimensions(xMin, xMax, yMin, yMax, cellsize);
    }

    /**
     * For getting the chunk with chunk ID {@code i}.
     *
     * @return The chunk with chunk ID {@code i}.
     * @param i The chunk ID.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     * @throws Exception If encountered.
     */
    public abstract Grids_Chunk getChunk(Grids_2D_ID_int i)
            throws IOException, ClassNotFoundException, Exception;

    /**
     * If not loaded, this attempts to load into memory the chunk with chunk ID
     * {@code i}. If it was not loaded then this means that the chunk perhaps
     * contained only no data values.
     *
     * @param i The chunk ID of the chunk to be loaded.
     * @return {@code true} if the chunk was loaded and {@code false} otherwise.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     * @throws Exception If encountered.
     */
    public boolean loadChunk(Grids_2D_ID_int i) throws IOException,
            ClassNotFoundException, Exception {
        if (!isLoaded(i)) {
            Path f = Paths.get(getDirectory().toString(),
                    "" + i.getRow() + "_" + i.getCol());
            if (Files.exists(f)) {
                //env.env.log("Loading chunk from file" + f);
                Object o = Generic_IO.readObject(f);
                Grids_Chunk chunk = (Grids_Chunk) o;
                chunk.env = env;
                chunk.initGrid(this);
                chunk.initChunkID(i);
                data.put(i, chunk);
                return true;
            } else {
                /**
                 * It is assumed that the chunk is all noDataValues so if this
                 * is called in a process which is attempting to set a value,
                 * then the chunk and value should be created without trying to
                 * load from the file.
                 */
                return false;
            }
        }
        return false;
    }

    /**
     * For getting the chunk at chunk row index {@code r}, chunk col index
     * {@code c}.
     *
     * @param cr The chunk row index.
     * @param cc The chunk column index.
     * @return Grids_AbstractGridChunkDouble.
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     * @throws Exception If encountered.
     */
    public final Grids_Chunk getChunk(int cr, int cc)
            throws IOException, ClassNotFoundException, Exception {
        return getChunk(new Grids_2D_ID_int(cr, cc));
    }

    /**
     * For getting the chunk with chunk ID {@code i} at chunk row index
     * {@code r}, chunk col index {@code c}.
     *
     * @param i The chunk ID.
     * @param cr The chunk row index.
     * @param cc The chunk column index.
     * @return The specific chunk
     * @throws IOException If encountered.
     * @throws ClassNotFoundException If encountered.
     * @throws Exception If encountered.
     */
    public abstract Grids_Chunk getChunk(Grids_2D_ID_int i, int cr, int cc)
            throws IOException, ClassNotFoundException, Exception;

    /**
     * For getting the grid statistics.
     *
     * @return The {@link #stats} cast appropriately.
     */
    public abstract Grids_Stats getStats();

    /**
     * For getting the path for the serialization of this.
     *
     * @param p The directory path that is to contain the file.
     * @return The path for the serialization of this.
     */
    public Path getPathThisFile(Generic_Path p) {
        return Paths.get(p.toString(), "grid.dat");
    }

    /**
     * For printing out some or all of the values in row major order. There is
     * no good memory handling for this yet. It is best if r and c are small. It
     * might be good to develop a new version that allows for a set of
     * rows/columns to include in the print out.
     *
     * @param r The number of rows to print.
     * @param c The number of columns to print.
     * @throws Exception If encountered.
     */
    public void log(long r, long c) throws Exception {
        env.env.log("name=" + getName());
        env.env.log(toString());
        env.env.log("dimensions=" + dim.toString());
        long nrows = getNRows();
        long ncols = getNCols();
        int cols = (int) Math.min(ncols, c);
        //int rows = (int) Math.min(nrows, r);
        //String dashes = getDashes((cols * 13) + 1);
        String dashes = getDashes(ncols, cols);
        env.env.log("  yMax       " + getColMarkers(ncols, cols));
        env.env.log(getStringValue(dim.getYMax()) + "  " + dashes);
        String bars = getBars(ncols, cols);
        String barsAndDashes = getBarsAndDashes(ncols, cols);
        env.env.log(bars);
        if (nrows < r) {
            // Print out the top rows
            long row;
            for (row = nrows - 1; row > 0; row--) {
                logRow(ncols, c, row);
                logBars(bars, barsAndDashes);
            }
            // Print out the bottom row.
            row = 0;
            logRow(ncols, c, row);
            env.env.log(bars);
        } else {
            // Print out the top row
            long row = nrows - 1;
            logRow(ncols, c, row);
            env.env.log(bars);
            // Print a break
            env.env.log(barsAndDashes);
            env.env.log("");
            env.env.log(bars);
            env.env.log("");
            env.env.log(barsAndDashes);
            env.env.log(bars);
            // Print out the bottom
            for (row = r - 2; row > 0; row--) {
                logRow(ncols, c, row);
                logBars(bars, barsAndDashes);
            }
            logRow(ncols, c, row);
            env.env.log(bars);
        }
        env.env.log(getStringValue(dim.getYMin()) + "  " + dashes);
        env.env.log("  Ymin " + getStringValue(dim.getXMin()) + getSpaces((cols * 13) - 5) + getStringValue(dim.getXMax()));
        env.env.log("          Xmin" + getSpaces(cols * 13) + "Xmax");
    }

    /**
     * Used to help log a view of the grid.
     *
     * @param bars Spacers to make a cell appear more cell like.
     * @param barsAndDashes Dividers that divide between rows of the grid.
     */
    protected void logBars(String bars, String barsAndDashes) {
        env.env.log(bars);
        env.env.log(barsAndDashes);
        env.env.log(bars);
    }

    /**
     * @param ncols The number of columns in the grid.
     * @param cols The number of columns that to be printed out.
     * @return A String for a gap to make the cells in the grid printed out to
     * appear more cell like.
     */
    public String getBars(long ncols, int cols) {
        String s = getSpaces(12);
        String r = s + "|";
        if (ncols < cols) {
            for (int i = 0; i < cols; i++) {
                r += s + "|";
            }
        } else {
            for (int i = 0; i < cols - 1; i++) {
                r += s + "|";
            }
            r += "   |" + s + "|";
        }
        return r;
    }

    /**
     * @param ncols The number of columns in the grid.
     * @param cols The number of columns that to be printed out.
     * @return A String to represent a break between two rows of the grid.
     */
    public String getBarsAndDashes(long ncols, int cols) {
        String s = getSpaces(12);
        String d = getDashes2(12);
        String r = s + "|";
        if (ncols < cols) {
            for (int i = 0; i < cols; i++) {
                r += d + "|";
            }
        } else {
            for (int i = 0; i < cols - 1; i++) {
                r += d + "|";
            }
            r += " - |" + d + "|";
        }
        return r;
    }

    /**
     * @param ncols The number of columns in the grid.
     * @param cols The number of columns that to be printed out.
     * @return A string for indicating the columns of the grid.
     */
    protected String getColMarkers(long ncols, int cols) {
        String s = "";
        if (ncols < cols) {
            for (int i = 0; i < cols; i++) {
                s += " " + getStringValue(BigDecimal.valueOf(i)) + "  ";
            }
        } else {
            for (int i = 0; i < cols - 1; i++) {
                s += " " + getStringValue(BigDecimal.valueOf(i)) + "  ";
            }
            s += "    " + getStringValue(BigDecimal.valueOf(ncols - 1L)) + "  ";
        }
        return s;
    }

    /**
     * @param ncols The number of columns in the grid.
     * @param cols The number of columns to be printed out.
     * @return A string for representing the top and bottom edge of the grid.
     */
    public String getDashes(long ncols, int cols) {
        String r = "";
        if (ncols < cols) {
            int n = (cols * 13) + 1;
            return getDashes2(n);
        } else {
            int n = ((cols - 1) * 13) + 1;
            r += getDashes2(n);
            r += " - ";
            r += getDashes2(14);
        }
        return r;
    }

    /**
     * @param n The number of dashes in the result.
     * @return A String with {@code n} dashes "-"
     */
    public String getDashes2(int n) {
        String r = "";
        for (int i = 0; i < n; i++) {
            r += "-";
        }
        return r;
    }

    /**
     * Used to help log a view of the grid.
     *
     * @param ncols The number of columns in the grid.
     * @param c The number of columns to be printed out.
     * @param row The row of the grid to be logged.
     * @throws java.lang.Exception If encountered.
     */
    protected abstract void logRow(long ncols, long c, long row) throws Exception;

    /**
     * Used to help log a view of the grid. This aims to present numerical
     * values in 10 characters which may involve rounding. If the number has
     * fewer than 10 characters it is padded with spaces. The returned String is
     * always of length 10.
     *
     * @param v The value to return as a String.
     * @return {@code v} as a string rounded if necessary using
     * {@code RoundingMode.HALF_UP}.
     */
    public String getStringValue(BigDecimal v) {
        String r = v.toEngineeringString();
        if (r.length() > 10) {
            BigDecimal v2 = v.setScale(v.scale() - (v.precision() - 3),
                    RoundingMode.HALF_UP);
            r = v2.toEngineeringString();
        }
        while (r.length() < 9) {
            r = " " + r + " ";
        }
        if (r.length() < 10) {
            r = " " + r;
        }
        //System.out.println(r.length());
        return r;
    }

    /**
     * @param n The length of the String returned.
     * @return A String comprising of {@code n} spaces " ". 
     */
    public String getSpaces(int n) {
        String r = "";
        for (int i = 0; i < n; i++) {
            r += " ";
        }
        return r;
    }

    /**
     * POJO for nearest values cell IDs and distance.
     */
    public class NearestValuesCellIDsAndDistance {

        public Grids_2D_ID_long[] cellIDs;
        public BigDecimal distance;

        public NearestValuesCellIDsAndDistance() {
        }
    }

}
