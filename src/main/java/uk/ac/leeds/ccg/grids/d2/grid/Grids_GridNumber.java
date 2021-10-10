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
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.math.number.Math_BigRational;

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
    public BigDecimal ndv;

    protected Grids_GridNumber(Grids_Environment ge, IO_Cache fs,
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
    public BigDecimal getCellBigDecimal(Math_BigRational x, Math_BigRational y)
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
    public Number setCell(Math_BigRational x, Math_BigRational y, BigDecimal v)
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
    public void addToCell(Math_BigRational x, Math_BigRational y, BigDecimal v)
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
     * @param oom The Order of Magnitude for calculating the distance.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected abstract NearestValuesCellIDsAndDistance
            getNearestValuesCellIDsAndDistance(Math_BigRational x, 
                    Math_BigRational y,                    long row, long col, int oom)
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
    protected NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(
            Math_BigRational x, Math_BigRational y, int oom) throws IOException,
            Exception, ClassNotFoundException {
        return getNearestValuesCellIDsAndDistance(x, y, getRow(y), getCol(x), oom);
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
        String s = " " + getStringValue(Math_BigRational.valueOf(row)) + " | ";
        if (ncols < c) {
            long col;
            for (col = 0; col < ncols - 1; col++) {
                s += getStringValue(getCellBigDecimal(row, col), ndv) + " | ";
            }
            s += getStringValue(getCellBigDecimal(row, col), ndv) + " | ";
            env.env.log(s);
        } else {
            for (long col = 0; col < c - 1; col++) {
                s += getStringValue(getCellBigDecimal(row, col), ndv) + " | ";
            }
            s += "  |";
            s += " " + getStringValue(getCellBigDecimal(row, ncols - 1), ndv) + " |";
            env.env.log(s);
        }
    }

        /**
     * Used to help log a view of the grid.
     *
     * @param v The value to represent as a String.
     * @param ndv The no data value.
     * @return a String representation of {@code v}.
     */
    public String getStringValue(BigDecimal v, BigDecimal ndv) {
        if (v.compareTo(ndv) == 0) {
            return "     *    ";
        }
        return getStringValue(v);
    }

}
