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

import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_long;
import java.io.IOException;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.chunk.Grids_Chunk;
import java.math.BigDecimal;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.generic.io.Generic_FileStore;

/**
 * For grids containing Numerical values.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_GridNumber extends Grids_Grid {

    private static final long serialVersionUID = 1L;

    /**
     * The noDataValue for the grid.
     */
    public final BigDecimal ndv;

    protected Grids_GridNumber(Grids_Environment ge, Generic_FileStore fs,
            long id, BigDecimal ndv) throws Exception {
        super(ge, fs, id);
        this.ndv = ndv;
    }

    /**
     * @return The value at x-coordinate {@code x} and y-coordinate {@code y} as
     * a BigDecimal.
     * @param x The x-coordinate of the point at which the cell value is
     * returned.
     * @param y The y-coordinate of the point at which the cell value is
     * returned.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigDecimal getCellBigDecimal(BigDecimal x, BigDecimal y)
            throws IOException, Exception, ClassNotFoundException {
        return getCellBigDecimal(getChunkRow(y), getChunkCol(x),
                getChunkCellRow(y), getChunkCellCol(x));
    }

    /**
     * @param row The chunk cell row index.
     * @param col The chunk cell column index.
     * @return Value at cell row index {@code row}, cell col index {@code col}
     * as a BigDecimal.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigDecimal getCellBigDecimal(long row, long col) throws IOException,
            Exception, ClassNotFoundException {
        return getCellBigDecimal(getChunkRow(row), getChunkCol(col),
                getChunkCellRow(row), getChunkCellCol(col));
    }

    /**
     * For getting the value of chunk cell row {@code ccr} and chunk cell column
     * {@code ccc} in chunk in chunk row {@code cr}, chunk column {@code cc} as
     * a BigDecimal.
     *
     * @param cr The chunk row.
     * @param cc The chunk col.
     * @param ccr The cell row index of the chunk.
     * @param ccc The cell column index of the chunk.
     * @return The value of chunk cell row {@code ccr}, chunk cell column
     * {@code ccc} in chunk in chunk row {@code cr}, chunk column {@code cc} as
     * a BigDecimal.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigDecimal getCellBigDecimal(int cr, int cc, int ccr, int ccc)
            throws IOException, Exception, ClassNotFoundException {
        if (!isInGrid(cr, cc, ccr, ccc)) {
            return ndv;
        }
        Grids_Chunk gc = getChunk(cr, cc);
        if (gc == null) {
            return ndv;
        }
        return getCellBigDecimal(gc, cr, cc, ccr, ccc);
    }

    /**
     * For getting the value of chunk cell row {@code ccr} and chunk cell column
     * {@code ccc} in chunk in chunk row {@code cr}, chunk column {@code cc} as
     * a BigDecimal.
     *
     * @param chunk The Grids_Chunk containing the cell.
     * @param cr The chunk row.
     * @param cc The chunk col.
     * @param ccr The cell row index of the chunk.
     * @param ccc The cell column index of the chunk.
     * @return The value of chunk cell row {@code ccr}, chunk cell column
     * {@code ccc} in chunk in chunk row {@code cr}, chunk column {@code cc} as
     * a BigDecimal.
     */
    public abstract BigDecimal getCellBigDecimal(Grids_Chunk chunk, int cr,
            int cc, int ccr, int ccc);

    /**
     * For setting the value of chunk cell row {@code ccr} and chunk cell column
     * {@code ccc} in chunk in chunk row {@code cr}, chunk column {@code cc} to
     * {@code v}.
     *
     * @param cr The chunk row.
     * @param cc The chunk col.
     * @param ccr The cell row index of the chunk.
     * @param ccc The cell column index of the chunk.
     * @param v The value to set.
     * @return The value of chunk cell row {@code ccr}, chunk cell column
     * {@code ccc} in chunk in chunk row {@code cr}, chunk column {@code cc} as
     * a BigDecimal.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public abstract Number setCell(int cr, int cc, int ccr, int ccc,
            BigDecimal v) throws IOException, ClassNotFoundException,
            Exception;

    /**
     * For setting the value at cell row index {@code r}, cell column index
     * {@code c} to v.
     *
     * @param r The cell row.
     * @param c The cell column.
     * @param v The value to add.
     * @return The value of at cell row index {@code r}, cell column index
     * {@code c} before it is set to {@code v}.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Number setCell(long r, long c, BigDecimal v)
            throws IOException, Exception, ClassNotFoundException {
        int cr = getChunkRow(r);
        int cc = getChunkCol(c);
        int ccr = getChunkCellRow(r);
        int ccc = getChunkCellCol(c);
        if (isInGrid(cr, cc, ccr, ccc)) {
            return setCell(cr, cc, ccr, ccc, v);
        }
        return ndv;
    }

    /**
     * For setting the value at cell with cell ID {@code cellID}.
     *
     * @param cellID The cell ID.
     * @param v The value to add.
     * @return The value of at cell row index {@code r}, cell column index
     * {@code c} before it is set to {@code v}.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Number setCell(Grids_2D_ID_long cellID, BigDecimal v)
            throws IOException, Exception, ClassNotFoundException {
        return setCell(cellID.getRow(), cellID.getCol(), v);
    }

    /**
     * For setting the value at x-coordinate {@code x}, y-coordinate {@code y}
     * to {@code v}.
     *
     * @param x The x-coordinate of the point at which the cell value is
     * returned.
     * @param y The y-coordinate of the point at which the cell value is
     * returned.
     * @param v The value to set.
     * @return The value of at cell row index {@code r}, cell column index
     * {@code c} before it is set to {@code v}.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Number setCell(BigDecimal x, BigDecimal y, BigDecimal v)
            throws IOException, Exception, ClassNotFoundException {
        if (isInGrid(x, y)) {
            return setCell(getChunkRow(y), getChunkCol(x),
                    getChunkCellRow(y), getChunkCellCol(x), v);
        }
        return ndv;
    }

    /**
     * For adding {@code v} to the value of chunk cell row {@code ccr}, chunk
     * cell column {@code ccc} in chunk in chunk row {@code cr}, chunk column
     * {@code cc} to {@code v}.
     *
     * @param cr The chunk row.
     * @param cc The chunk col.
     * @param ccr The cell row index of the chunk.
     * @param ccc The cell column index of the chunk.
     * @param v The value to add.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToCell(int cr, int cc, int ccr, int ccc, BigDecimal v)
            throws IOException, ClassNotFoundException, Exception {
        if (v.compareTo(ndv) != 0) {
            if (isInGrid(cr, cc, ccr, ccc)) {
                BigDecimal v2 = getCellBigDecimal(cr, cc, ccr, ccc);
                if (v2.compareTo(ndv) == 0) {
                    setCell(cr, cc, ccr, ccc, v);
                } else {
                    setCell(cr, cc, ccr, ccc, v.add(v2));
                }
            }
        }
    }

    /**
     * For adding {@code v} to the cell value at cell row index {@code r}, cell
     * column index {@code c}.
     *
     * @param r The cell row.
     * @param c The cell column.
     * @param v The value to add.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToCell(long r, long c, BigDecimal v)
            throws IOException, Exception, ClassNotFoundException {
        addToCell(getChunkRow(r), getChunkCol(c), getChunkCellRow(r),
                getChunkCellCol(c), v);
    }

    /**
     * For adding {@code v} to the cell with cell ID {@code cellID}.
     *
     * @param cellID The cell ID.
     * @param v The value to add.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToCell(Grids_2D_ID_long cellID, BigDecimal v)
            throws IOException, Exception, ClassNotFoundException {
        addToCell(cellID.getRow(), cellID.getCol(), v);
    }

    /**
     * For setting the value at x-coordinate {@code x}, y-coordinate {@code y}
     * to {@code v}.
     *
     * @param x The x-coordinate of the point at which the cell value is
     * returned.
     * @param y The y-coordinate of the point at which the cell value is
     * returned.
     * @param v The value to add.
     * @throws java.lang.Exception If encountered.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public void addToCell(BigDecimal x, BigDecimal y, BigDecimal v)
            throws IOException, Exception, ClassNotFoundException {
        addToCell(getChunkRow(y), getChunkCol(x), getChunkCellRow(y),
                getChunkCellCol(x), v);
    }

    /**
     * @return The CellIDs of the nearest cells with data values nearest to
     * point with position given by: x-coordinate x, y-coordinate y; and, cell
     * row index row, cell column index col.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col The column index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param dp The number of decimal places the result is to be accurate to.
     * @param rm The {@link RoundingMode} to use when rounding the result.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected abstract NearestValuesCellIDsAndDistance
            getNearestValuesCellIDsAndDistance(BigDecimal x, BigDecimal y,
                    long row, long col, int dp, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException;

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to position given by row index rowIndex, column index colIndex.
     * @param row The row from which the cell IDs of the nearest cells with data
     * values are returned.
     * @param col The column from which the cell IDs of the nearest cells with
     * data values are returned.
     * @param dp The number of decimal places the result is to be accurate to.
     * @param rm The {@link RoundingMode} to use when rounding the result.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected abstract NearestValuesCellIDsAndDistance
            getNearestValuesCellIDsAndDistance(long row, long col, int dp,
                    RoundingMode rm) throws IOException, Exception,
            ClassNotFoundException;

    /**
     * @return a Grids_2D_ID_long[] The CellIDs of the nearest cells with data
     * values to point given by x-coordinate x, y-coordinate y.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param dp The number of decimal places the result is to be accurate to.
     * @param rm The {@link RoundingMode} to use when rounding the result.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(BigDecimal x,
            BigDecimal y, int dp, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        return getNearestValuesCellIDsAndDistance(x, y, getRow(y), getCol(x), dp, rm);
    }

    /**
     * For writing out some or all of the values in row major order. There is no
     * good memory handling for this yet. It is best if r and c are small.
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
        String dashes = getDashes((cols * 13) + 1);
        env.env.log("  yMax       " + getColMarkers(cols));
        env.env.log(getFormattedNumber(dim.getYMax()) + "  " + dashes);
        String bars = getBars(cols);
        String barsAndDashes = getBarsAndDashes(cols);
        env.env.log(bars);
        if (nrows < r) {
            long row;
            for (row = nrows - 1; row > 0; row--) {
                logRow(ncols, c, row);
                logBars(bars, barsAndDashes);
            }
            row = 0;
            logRow(ncols, c, row);
            env.env.log(bars);
        } else {
            long row = nrows - 1;
            logRow(ncols, c, row);
            env.env.log(bars);
            env.env.log("...");
            env.env.log(bars);
            for (row = r - 2; row > -1; row--) {
                logRow(ncols, c, row);
                logBars(bars, barsAndDashes);
            }
            logRow(ncols, c, row);
        }
        env.env.log(getFormattedNumber(dim.getYMin()) + "  " + dashes);
        env.env.log("  Ymin " + getFormattedNumber(dim.getXMin())
                + getSpaces((cols * 10) - 1) + getFormattedNumber(dim.getXMax()));
        env.env.log("          Xmin" + getSpaces((cols * 11) + 2) + "Xmax");
    }

    protected void logRow(long ncols, long c, long row) throws Exception {
        String s = " " + getFormattedNumber(BigDecimal.valueOf(row)) + " | ";
        if (ncols < c) {
            long col;
            for (col = 0; col < ncols - 1; col++) {
                s += getFormattedNumber(getCellBigDecimal(row, col), ndv) + " | ";
            }
            s += getFormattedNumber(getCellBigDecimal(row, col), ndv) + " | ";
            env.env.log(s);
        } else {
            for (long col = 0; col < c - 1; col++) {
                s += getFormattedNumber(getCellBigDecimal(row, col), ndv) + " | ";
            }
            s += "... ";
            s += getFormattedNumber(getCellBigDecimal(row, ncols - 1), ndv) + " |";
            env.env.log(s);
        }
    }
    
    protected String getColMarkers(int cols) {
        String s = "";
        for (int i = 0; i < cols; i ++) {
            s += " " + getFormattedNumber(BigDecimal.valueOf(i)) + "  ";
        }
        return s;
    }

    protected void logBars(String bars, String barsAndDashes) {
        env.env.log(bars);
        env.env.log(barsAndDashes);
        env.env.log(bars);
    }

    public String getFormattedNumber(BigDecimal v, BigDecimal ndv) {
        if (v.compareTo(ndv) == 0) {
            return "     *    ";
        }
        return getFormattedNumber(v);
    }

    public String getFormattedNumber(BigDecimal v) {
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

    public String getSpaces(int n) {
        String r = "";
        for (int i = 0; i < n; i++) {
            r += " ";
        }
        return r;
    }

    public String getDashes(int n) {
        String r = "";
        for (int i = 0; i < n; i++) {
            r += "-";
        }
        return r;
    }

    public String getBars(int n) {
        String s = getSpaces(12);
        String r = s + "|";
        for (int i = 0; i < n; i++) {
            r += s + "|";
        }
        return r;
    }

    public String getBarsAndDashes(int n) {
        String s = getSpaces(12);
        String d = getDashes(12);
        String r = s + "|";
        for (int i = 0; i < n; i++) {
            r += d + "|";
        }
        return r;
    }
}
