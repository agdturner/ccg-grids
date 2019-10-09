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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_AbstractGridNumberStats;

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
public abstract class Grids_AbstractGridNumber extends Grids_AbstractGrid {

    protected Grids_AbstractGridNumber() {
    }

    protected Grids_AbstractGridNumber(Grids_Environment ge, File dir) {
        super(ge, dir);
    }

    /**
     * @return Grids_AbstractGridChunk cell value at at Point2D.Double point as
     * a double.
     * @param point The Point2D.Double for which the cell value is returned.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(Point2D.Double point, boolean hoome) {
        try {
            double r = getCellDouble(point);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(point.getY()), getChunkCol(point.getX()));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(point, hoome);
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
    public double getCellDouble(Point2D.Double point) {
        return getCellDouble(getChunkRow(point.getY()),
                getChunkCol(point.getX()), getCellRow(point.getY()),
                getCellCol(point.getX()));
    }

    /**
     * @return Grids_AbstractGridChunk cell value at at point given by
     * x-coordinate x and y-coordinate y as a double.
     * @param x The x coordinate of the point at which the cell value is
     * returned.
     * @param y The y coordinate of the point at which the cell value is
     * returned.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(double x, double y, boolean hoome) {
        try {
            double r = getCellDouble(x, y);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRow(y), getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(x, y, hoome);
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
    public double getCellDouble(double x, double y) {
        return getCellDouble(getChunkRow(y), getChunkCol(x), getCellRow(y),
                getCellCol(x));
    }

    /**
     * @param row
     * @param col
     * @return Grids_AbstractGridChunk cell value at cell row index equal to
     * _CellRowIndex, cell col index equal to _CellColIndex as a double.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(long row, long col, boolean hoome) {
        try {
            double r = getCellDouble(row, col);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row),
                        getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(row, col, hoome);
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
    public double getCellDouble(long row, long col) {
        return getCellDouble(getChunkRow(row), getChunkCol(col),
                getCellRow(row), getCellCol(col));
    }

    /**
     * @param chunkRow
     * @param chunkCol
     * @return Grids_AbstractGridChunk cell value at cell row index equal to
     * _CellRowIndex, cell col index equal to _CellColIndex as a double.
     * @param cellRow The cell row index of the chunk.
     * @param cellCol The cell column index of the chunk.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(int chunkRow, int chunkCol, int cellRow,
            int cellCol, boolean hoome) {
        try {
            double r = getCellDouble(chunkRow, chunkCol, cellRow, cellCol);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID;
                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(chunkRow, chunkCol, cellRow, cellCol,
                        hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * TODO
     *
     * @param chunkRow
     * @param chunkCol
     * @return Grids_AbstractGridChunk cell value at cell row index equal to
     * _CellRowIndex, cell col index equal to _CellColIndex as a double.
     * @param cellRow The cell row index of the chunk.
     * @param cellCol The cell column index of the chunk.
     */
    public double getCellDouble(int chunkRow, int chunkCol, int cellRow,
            int cellCol) {
        if (!isInGrid(chunkRow, chunkCol, cellRow, cellCol)) {
            if (this instanceof Grids_GridDouble) {
                return ((Grids_GridDouble) this).NoDataValue;
            } else {
                return ((Grids_GridInt) this).NoDataValue;
            }
        }
        Grids_AbstractGridChunk gridChunk = getChunk(chunkRow, chunkCol);
        if (gridChunk == null) {
            if (this instanceof Grids_GridDouble) {
                return ((Grids_GridDouble) this).NoDataValue;
            } else {
                return ((Grids_GridInt) this).NoDataValue;
            }
        }
        return getCellDouble(gridChunk, chunkRow, chunkCol, cellRow, cellCol);
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
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(Grids_AbstractGridChunk chunk,
            int chunkRow, int chunkCol, int cellRow, int cellCol,
            boolean hoome) {
        try {
            double r;
            r = getCellDouble(chunk, chunkRow, chunkCol, cellRow, cellCol);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID;
                chunkID = new Grids_2D_ID_int(chunkRow, chunkCol);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(chunk, chunkRow, chunkCol, cellRow,
                        cellCol, hoome);
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
    public abstract double getCellDouble(Grids_AbstractGridChunk chunk,
            int chunkRow, int chunkCol, int cellRow, int cellCol);

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to point given by x-coordinate x, y-coordinate y.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long[] getNearestValuesCellIDs(double x, double y,
            boolean hoome) {
        try {
            Grids_2D_ID_long[] r = getNearestValuesCellIDs(x, y);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID;
                chunkID = new Grids_2D_ID_int(getChunkRow(y), getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValuesCellIDs(x, y, hoome);
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
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col
     */
    protected abstract Grids_2D_ID_long[] getNearestValuesCellIDs(double x,
            double y, long row, long col);

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values nearest to point with position given by: x-coordinate x,
     * y-coordinate y; and, cell row index _CellRowIndex, cell column index
     * _CellColIndex.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long[] getNearestValuesCellIDs(double x, double y,
            long row, long col, boolean hoome) {
        try {
            Grids_2D_ID_long[] r = getNearestValuesCellIDs(x, y, row, col);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), 
                        getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValuesCellIDs(x, y, row, col, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to position given by row index rowIndex, column index colIndex.
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col
     */
    protected abstract Grids_2D_ID_long[] getNearestValuesCellIDs(long row,
            long col);

    /**
     * @param col
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to position given by row index rowIndex, column index colIndex.
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final Grids_2D_ID_long[] getNearestValuesCellIDs(long row, long col,
            boolean hoome) {
        try {
            Grids_2D_ID_long[] result = getNearestValuesCellIDs(row, col);
            env.checkAndMaybeFreeMemory(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), 
                        getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValuesCellIDs(row, col, hoome);
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
    protected abstract Grids_2D_ID_long[] getNearestValuesCellIDs(double x, 
            double y);

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDouble(double x, double y, 
            boolean hoome) {
        try {
            double r = getNearestValueDouble(x, y);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(y), 
                        getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDouble(x, y, hoome);
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
     * @param row the row index from which average of the nearest data values is
     * returned
     * @param col the column index from which average of the nearest data values
     * is returned
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDouble(double x, double y, long row,
            long col, boolean hoome) {
        try {
            return getNearestValueDouble(x, y, row, col);
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), 
                        getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDouble(x, y, row, col, hoome);
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
     * @param row the row index from which average of the nearest data values is
     * returned
     * @param col the column index from which average of the nearest data values
     * is returned
     */
    protected abstract double getNearestValueDouble(double x, double y,
            long row, long col);

    /**
     * @return the average of the nearest data values to position given by row
     * index rowIndex, column index colIndex.
     * @param row The row index from which average of the nearest data values is
     * returned.
     * @param col The column index from which average of the nearest data values
     * is returned.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDouble(long row, long col, 
            boolean hoome) {
        try {
            double result = getNearestValueDouble(row, col);
            env.checkAndMaybeFreeMemory(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID  = new Grids_2D_ID_int(getChunkRow(row),
                        getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDouble(row, col, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param row The row index from which average of the nearest data values is
     * returned.
     * @param col The column index from which average of the nearest data values
     * is returned.
     * @return the average of the nearest data values to position given by row
     * index rowIndex, column index colIndex
     */
    protected abstract double getNearestValueDouble(long row, long col);

    /**
     * @return the distance to the nearest data value from point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDoubleDistance(double x, double y,
            boolean hoome) {
        try {
            double r = getNearestValueDoubleDistance(x, y);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID;
                chunkID = new Grids_2D_ID_int(getChunkRow(y), getChunkCol(x));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDoubleDistance(x, y, hoome);
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
     * @param row The cell row index of the cell from which the distance nearest
     * to the nearest cell value is returned.
     * @param col The cell column index of the cell from which the distance
     * nearest to the nearest cell value is returned.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDoubleDistance(double x, double y,
            long row, long col, boolean hoome) {
        try {
            double result = getNearestValueDoubleDistance(x, y, row, col);
            env.checkAndMaybeFreeMemory(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID;
                chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDoubleDistance(x, y, row, col, hoome);
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
     * @param row The cell row index of the cell from which the distance nearest
     * to the nearest cell value is returned.
     * @param col The cell column index of the cell from which the distance
     * nearest to the nearest cell value is returned.
     */
    protected abstract double getNearestValueDoubleDistance(double x, double y,
            long row, long col);

    /**
     * @return the distance to the nearest data value from position given by row
     * index rowIndex, column index colIndex as a double.
     * @param row The cell row index of the cell from which the distance nearest
     * to the nearest cell value is returned.
     * @param col The cell column index of the cell from which the distance
     * nearest to the nearest cell value is returned.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDoubleDistance(long row, long col,
            boolean hoome) {
        try {
            double result = getNearestValueDoubleDistance(row, col);
            env.checkAndMaybeFreeMemory(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID;
                chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getNearestValueDoubleDistance(row, col, hoome);
            } else {
                throw e;
            }
        }
    }

    @Override
    public Grids_AbstractGridNumberStats getStats(boolean hoome) {
        return (Grids_AbstractGridNumberStats) super.getStats(hoome);
    }
}
