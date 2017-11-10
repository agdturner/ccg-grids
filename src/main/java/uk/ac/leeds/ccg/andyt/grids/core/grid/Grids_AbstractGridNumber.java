/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2005 Andy Turner, CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkIntMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_AbstractGridNumberStatistics;

/**
 * Contains Grids_2D_ID_long and Grids_2D_ID_int classes, referencing and
 * general geometry methods. It also controls what methods extended classes must
 * implement acting like an interface.
 *
 * The basic geometries are ordered in set numbers of rows and columns and are
 * arranged sequentially as their base two-dimensional orthogonal coordinate
 * axes. The sequential arrangement goes along the x-axis row by row from the
 * y-axis, then up the y-axis taking each row in turn.
 *
 * TODO: Handling for NumberFormatExceptions and ArithmeticExceptions in
 * calculations
 */
public abstract class Grids_AbstractGridNumber
        extends Grids_AbstractGrid implements Grids_InterfaceGridNumber {

    protected Grids_AbstractGridNumber() {
    }

    public Grids_AbstractGridNumber(Grids_Environment ge, File directory) {
        super(ge, directory);
    }

    @Override
    public String toString() {
        String result;
        result = super.toString();
//        result += "," + Statistics.toString(true);
        return result;
    }

    /**
     * Initialises non transient Grids_AbstractGrid fields from g.
     *
     * @param g The Grids_AbstractGrid from which the non transient
     * Grids_AbstractGrid fields of this are set.
     */
    protected void init(Grids_AbstractGridNumber g) {
        super.init(g);
        Grids_AbstractGridNumberStatistics s;
        s = g.getStatistics();
//        if (Statistics instanceof Grids_GridStatistics) {
//            Statistics = new Grids_GridStatistics(this);
//        } else {
//            Statistics = new Grids_GridStatisticsNotUpdatedAsDataChanged(this);
//        }
    }

    /**
     * @return Grids_AbstractGridChunk cell value at at Point2D.Double point as
     * a double.
     * @param point The Point2D.Double for which the cell value is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(
            Point2D.Double point,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(point);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(point.getY()),
                        getChunkCol(point.getX()));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(
                        point,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Grids_AbstractGridChunk cell value at at Point2D.Double point as
     * a double.
     * @param point The Point2D.Double for which the cell value is returned.
     */
    protected double getCellDouble(
            Point2D.Double point) {
        return getCellDouble(
                getChunkRow(point.getY()),
                getChunkCol(point.getX()),
                getCellRow(point.getY()),
                getCellCol(point.getX()));
    }

    /**
     * @return Grids_AbstractGridChunk cell value at at point given by
     * x-coordinate x and y-coordinate y as a double.
     * @param x The x coordinate of the point at which the cell value is
     * returned.
     * @param y The y coordinate of the point at which the cell value is
     * returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(
            double x,
            double y,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(
                    x,
                    y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(y),
                        getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(
                        x,
                        y,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Grids_AbstractGridChunk cell value at at point given by
     * x-coordinate x and y-coordinate y as a double.
     * @param x The x coordinate of the point at which the cell value is
     * returned.
     * @param y The y coordinate of the point at which the cell value is
     * returned.
     */
    protected double getCellDouble(
            double x,
            double y) {
        return getCellDouble(
                getChunkRow(y),
                getChunkCol(x),
                getCellRow(y),
                getCellCol(x));
    }

    /**
     * @param row
     * @param col
     * @return Grids_AbstractGridChunk cell value at cell row index equal to
     * _CellRowIndex, cell col index equal to _CellColIndex as a double.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(
            long row,
            long col,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(                    row,                    col);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(row),
                        getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(                        row,                        col,                        handleOutOfMemoryError);
            } else {
                throw e;

            }
        }
    }

    /**
     * @param row
     * @param col
     * @return Grids_AbstractGridChunk cell value at cell row index equal to
     * _CellRowIndex, cell col index equal to _CellColIndex as a double.
     */
    protected double getCellDouble(
            long row,
            long col) {
        return getCellDouble(
                getChunkRow(row),
                getChunkCol(col),
                getCellRow(row),
                getCellCol(col));
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Grids_AbstractGridChunk cell value at cell row index equal to
     * _CellRowIndex, cell col index equal to _CellColIndex as a double.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(
                        chunkRowIndex,
                        chunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * TODO
     *
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Grids_AbstractGridChunk cell value at cell row index equal to
     * _CellRowIndex, cell col index equal to _CellColIndex as a double.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected double getCellDouble(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        if (!isInGrid(chunkRowIndex, chunkColIndex)) {
            if (this instanceof Grids_GridDouble) {
                return ((Grids_GridDouble) this).NoDataValue;
            } else {
                return ((Grids_GridInt) this).NoDataValue;
            }
        }
        Grids_AbstractGridChunk gridChunk;
        gridChunk = getChunk(chunkRowIndex, chunkColIndex);
        if (gridChunk == null) {
            if (this instanceof Grids_GridDouble) {
                return ((Grids_GridDouble) this).NoDataValue;
            } else {
                return ((Grids_GridInt) this).NoDataValue;
            }
        }
        return getCellDouble(
                gridChunk,
                chunkRowIndex,
                chunkColIndex,
                chunkCellRowIndex,
                chunkCellColIndex);
    }

    /**
     * @param chunkCol
     * @param chunkRow
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGridChunk given by chunk row
     * index _Row, chunk col index _Col as a double.
     * @param chunk The Grids_AbstractGridChunk containing the
     * cell.
     * @param cellRow The cell row index of the chunk.
     * @param cellCol The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(
            Grids_AbstractGridChunk chunk,
            int chunkRow,
            int chunkCol,
            int cellRow,
            int cellCol,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(
                    chunk,
                    chunkRow,
                    chunkCol,
                    cellRow,
                    cellCol);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRow,
                        chunkCol);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(
                        chunk,
                        chunkRow,
                        chunkCol,
                        cellRow,
                        cellCol,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkCol
     * @param chunkRow
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGridChunk given by chunk row
     * index _Row, chunk col index _Col as a double.
     * @param chunk The Grids_AbstractGridChunk containing the cell.
     * @param cellRow The cell row index of the chunk.
     * @param cellCol The cell column index of the chunk.
     */
    protected abstract double getCellDouble(
            Grids_AbstractGridChunk chunk,
            int chunkRow,
            int chunkCol,
            int cellRow,
            int cellCol);

    /**
     * @param row
     * @param col
     * @return Grids_AbstractGridChunk cell value at cell row index equal to
     * row, cell col index equal to col as a int.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getCellInt(
            long row,
            long col,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCellInt(
                    row,
                    col);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(row),
                        getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellInt(
                        row,
                        col,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Grids_AbstractGridChunk cell value at cell row index equal to
     * row, cell col index equal to col as a int.
     * @param row The cell row index.
     * @param col The cell column index.
     */
    protected int getCellInt(
            long row,
            long col) {
        return getCellInt(
                getChunkRow(row),
                getChunkCol(col),
                getCellRow(row),
                getCellCol(col));
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGridChunk given by chunk row
     * index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getCellInt(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCellInt(
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellInt(
                        chunkRowIndex,
                        chunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGridChunk given by chunk row
     * index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected int getCellInt(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        Grids_AbstractGridChunk grid2DSquareCellChunk = getChunk(
                chunkRowIndex,
                chunkColIndex);
        return getCellInt(
                grid2DSquareCellChunk,
                chunkRowIndex,
                chunkColIndex,
                chunkCellRowIndex,
                chunkCellColIndex);
    }

    /**
     * @param chunkColIndex
     * @param chunkRowIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGridChunk given by chunk row
     * index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param grid2DSquareCellChunk The Grids_AbstractGridChunk containing the
     * cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final int getCellInt(
            Grids_AbstractGridChunk grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCellInt(
                    grid2DSquareCellChunk,
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellInt(
                        grid2DSquareCellChunk,
                        chunkRowIndex,
                        chunkColIndex,
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param chunkColIndex
     * @param chunkRowIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGridChunk given by chunk row
     * index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param chunk The Grids_AbstractGridChunk containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected int getCellInt(
            Grids_AbstractGridChunk chunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        if (chunk instanceof Grids_AbstractGridChunkDouble) {
            Grids_AbstractGridChunkDouble gridChunk;
            gridChunk = (Grids_AbstractGridChunkDouble) chunk;
            Grids_GridDouble g;
            g = (Grids_GridDouble) gridChunk.getGrid(false);
            if (chunk.getClass() == Grids_GridChunkDoubleArray.class) {
                Grids_GridChunkDoubleArray gridChunkArray;
                gridChunkArray = (Grids_GridChunkDoubleArray) gridChunk;
                return (int) gridChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        false);
            }
            if (chunk.getClass() == Grids_GridChunkDoubleMap.class) {
                Grids_GridChunkDoubleMap gridChunkMap;
                gridChunkMap = (Grids_GridChunkDoubleMap) gridChunk;
                return (int) gridChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        false);
            }
            double noDataValue = g.NoDataValue;
            return (int) noDataValue;
        } else {
            //if ( grid2DSquareCellChunk instanceof Grids_AbstractGridChunkInt ) {
            Grids_AbstractGridChunkInt gridChunk
                    = (Grids_AbstractGridChunkInt) chunk;
            Grids_GridInt g;
            g = (Grids_GridInt) gridChunk.getGrid(false);
            if (chunk.getClass() == Grids_GridChunkIntArray.class) {
                Grids_GridChunkIntArray gridChunkArray;
                gridChunkArray = (Grids_GridChunkIntArray) gridChunk;
                return gridChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        false,
                        chunk.getChunkID(false));
            }
            if (chunk.getClass() == Grids_GridChunkIntMap.class) {
                Grids_GridChunkIntMap gridChunkMap;
                gridChunkMap = (Grids_GridChunkIntMap) gridChunk;
                return gridChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        false,
                        chunk.getChunkID(false));
            }
            int noDataValue = g.NoDataValue;
            return noDataValue;
        }
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to point given by x-coordinate x, y-coordinate y.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long[] getNearestValuesCellIDs(double x, double y, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getNearestValuesCellIDs(x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(y), getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValuesCellIDs(x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values nearest to point with position given by: x-coordinate x,
     * y-coordinate y; and, cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row The row index from which the cell IDs of the nearest
     * cells with data values are returned.
     * @param col
     */
    protected abstract Grids_2D_ID_long[] getNearestValuesCellIDs(double x, double y, long row, long col);

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values nearest to point with position given by: x-coordinate x,
     * y-coordinate y; and, cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row The row index from which the cell IDs of the nearest
     * cells with data values are returned.
     * @param col
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long[] getNearestValuesCellIDs(double x, double y, long row, long col, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getNearestValuesCellIDs(x, y, row, col);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValuesCellIDs(x, y, row, col, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to position given by row index rowIndex, column index colIndex.
     * @param row The row index from which the cell IDs of the nearest
     * cells with data values are returned.
     * @param col
     */
    protected abstract Grids_2D_ID_long[] getNearestValuesCellIDs(long row, long col);

    /**
     * @param col
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to position given by row index rowIndex, column index colIndex.
     * @param row The row index from which the cell IDs of the nearest
     * cells with data values are returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long[] getNearestValuesCellIDs(long row, long col, boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_long[] result = getNearestValuesCellIDs(row, col);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValuesCellIDs(row, col, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long[] The CellIDs of the nearest cells with data
     * values to point given by x-coordinate x, y-coordinate y.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    protected abstract Grids_2D_ID_long[] getNearestValuesCellIDs(double x, double y);

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDouble(double x, double y, boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDouble(x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(y), getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDouble(x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     */
    protected abstract double getNearestValueDouble(double x, double y);

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row the row index from which average of the nearest data
     * values is returned
     * @param col the column index from which average of the nearest
     * data values is returned
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDouble(double x, double y, long row, long col, boolean handleOutOfMemoryError) {
        try {
            return getNearestValueDouble(x, y, row, col);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDouble(x, y, row, col, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row the row index from which average of the nearest data
     * values is returned
     * @param col the column index from which average of the nearest
     * data values is returned
     */
    protected abstract double getNearestValueDouble(double x, double y, long row, long col);

    /**
     * @return the average of the nearest data values to position given by row
     * index rowIndex, column index colIndex.
     * @param row The row index from which average of the nearest data
     * values is returned.
     * @param col The column index from which average of the nearest
     * data values is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDouble(long row, long col, boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDouble(row, col);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDouble(row, col, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param row The row index from which average of the nearest data
     * values is returned.
     * @param col The column index from which average of the nearest
     * data values is returned.
     * @return the average of the nearest data values to position given by row
     * index rowIndex, column index colIndex
     */
    protected abstract double getNearestValueDouble(long row, long col);

    /**
     * @return the distance to the nearest data value from point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDoubleDistance(double x, double y, boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDoubleDistance(x, y);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(y), getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDoubleDistance(x, y, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the distance to the nearest data value from point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    protected abstract double getNearestValueDoubleDistance(double x, double y);

    /**
     * @return the distance to the nearest data value from: point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex as a double.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param row The cell row index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param col The cell column index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDoubleDistance(double x, double y, long row, long col, boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDoubleDistance(x, y, row, col);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDoubleDistance(x, y, row, col, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return the distance to the nearest data value from: point given by
     * x-coordinate x, y-coordinate y in position given by row index rowIndex,
     * column index colIndex as a double.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param row The cell row index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param col The cell column index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     */
    protected abstract double getNearestValueDoubleDistance(double x, double y, long row, long col);

    /**
     * @return the distance to the nearest data value from position given by row
     * index rowIndex, column index colIndex as a double.
     * @param row The cell row index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param col The cell column index of the cell from which the
     * distance nearest to the nearest cell value is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDoubleDistance(long row, long col, boolean handleOutOfMemoryError) {
        try {
            double result = getNearestValueDoubleDistance(row, col);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDoubleDistance(row, col, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return An Iterator for iterating over the cell values in this.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public final Iterator iterator(
            boolean handleOutOfMemoryError) {
        try {
            Iterator result = iterator();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return iterator(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param handleOutOfMemoryError
     * @return this._GridStatistics TODO: For safety, this method should either
     * be removed and this class be made implement GridStatisticsInterface. This
     * done the methods introduced would be made to call the relevant ones in
     * this._GridStatistics. Or the _GridStatistics need to be made safe in that
     * only copies of fields are passed.
     */
    public Grids_AbstractGridNumberStatistics getStatistics(boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGridNumberStatistics result;
            result = getStatistics();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                freeSomeMemoryAndResetReserve(handleOutOfMemoryError, e);
                return getStatistics(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    public abstract Grids_AbstractGridNumberStatistics getStatistics();

}
