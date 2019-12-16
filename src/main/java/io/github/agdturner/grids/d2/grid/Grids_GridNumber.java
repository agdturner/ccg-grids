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

import io.github.agdturner.grids.d2.grid.d.Grids_GridDouble;
import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;
import java.awt.geom.Point2D;
import java.io.IOException;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_Path;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.core.Grids_2D_ID_long;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.d2.chunk.Grids_Chunk;
import io.github.agdturner.grids.d2.stats.Grids_StatsNumber;

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
*
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_GridNumber extends Grids_Grid {

    private static final long serialVersionUID = 1L;

    protected Grids_GridNumber(Grids_Environment ge, Generic_Path dir,
            Generic_Path baseDir) throws Exception {
        super(ge, dir, baseDir);
    }

    /**
     * @return Grids_Chunk cell value at at Point2D.Double point as
 a double.
     * @param point The Point2D.Double for which the cell value is returned.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(Point2D.Double point, boolean hoome) 
            throws IOException, Exception, ClassNotFoundException {
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
     * @return Grids_Chunk cell value at at Point2D.Double point as
 a double.
     * @param point The Point2D.Double for which the cell value is returned.
     */
    public double getCellDouble(Point2D.Double point) throws IOException, Exception, ClassNotFoundException {
        return getCellDouble(getChunkRow(point.getY()),
                getChunkCol(point.getX()), getCellRow(point.getY()),
                getCellCol(point.getX()));
    }

    /**
     * @return Grids_Chunk cell value at at point given by
 x-coordinate x and y-coordinate y as a double.
     * @param x The x coordinate of the point at which the cell value is
     * returned.
     * @param y The y coordinate of the point at which the cell value is
     * returned.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(double x, double y, boolean hoome) throws IOException, Exception, ClassNotFoundException {
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
     * @return Grids_Chunk cell value at at point given by
 x-coordinate x and y-coordinate y as a double.
     * @param x The x coordinate of the point at which the cell value is
     * returned.
     * @param y The y coordinate of the point at which the cell value is
     * returned.
     */
    public double getCellDouble(double x, double y) throws IOException, Exception, ClassNotFoundException {
        return getCellDouble(getChunkRow(y), getChunkCol(x), getCellRow(y),
                getCellCol(x));
    }

    /**
     * @param row
     * @param col
     * @return Grids_Chunk cell value at cell row index equal to
 _CellRowIndex, cell col index equal to _CellColIndex as a double.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(long row, long col, boolean hoome) throws IOException, Exception, ClassNotFoundException {
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
     * @return Grids_Chunk cell value at cell row index equal to
 _CellRowIndex, cell col index equal to _CellColIndex as a double.
     */
    public double getCellDouble(long row, long col) throws IOException, Exception, ClassNotFoundException {
        return getCellDouble(getChunkRow(row), getChunkCol(col),
                getCellRow(row), getCellCol(col));
    }

    /**
     * @param chunkRow
     * @param chunkCol
     * @return Grids_Chunk cell value at cell row index equal to
 _CellRowIndex, cell col index equal to _CellColIndex as a double.
     * @param cellRow The cell row index of the chunk.
     * @param cellCol The cell column index of the chunk.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(int chunkRow, int chunkCol, int cellRow,
            int cellCol, boolean hoome) throws IOException, Exception, ClassNotFoundException {
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
     * @return Grids_Chunk cell value at cell row index equal to
 _CellRowIndex, cell col index equal to _CellColIndex as a double.
     * @param cellRow The cell row index of the chunk.
     * @param cellCol The cell column index of the chunk.
     */
    public double getCellDouble(int chunkRow, int chunkCol, int cellRow,
            int cellCol) throws IOException, Exception, ClassNotFoundException {
        if (!isInGrid(chunkRow, chunkCol, cellRow, cellCol)) {
            if (this instanceof Grids_GridDouble) {
                return ((Grids_GridDouble) this).getNoDataValue();
            } else {
                return ((Grids_GridInt) this).getNoDataValue();
            }
        }
        Grids_Chunk gridChunk = getChunk(chunkRow, chunkCol);
        if (gridChunk == null) {
            if (this instanceof Grids_GridDouble) {
                return ((Grids_GridDouble) this).getNoDataValue();
            } else {
                return ((Grids_GridInt) this).getNoDataValue();
            }
        }
        return getCellDouble(gridChunk, chunkRow, chunkCol, cellRow, cellCol);
    }

    /**
     * @param chunkCol
     * @param chunkRow
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
 col index chunkCellColIndex of Grids_Chunk given by chunk row
 index _Row, chunk col index _Col as a double.
     * @param chunk The Grids_Chunk containing the cell.
     * @param cellRow The cell row index of the chunk.
     * @param cellCol The cell column index of the chunk.
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getCellDouble(Grids_Chunk chunk,
            int chunkRow, int chunkCol, int cellRow, int cellCol,
            boolean hoome) throws IOException, Exception {
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
 col index chunkCellColIndex of Grids_Chunk given by chunk row
 index _Row, chunk col index _Col as a double.
     * @param chunk The Grids_Chunk containing the cell.
     * @param cellRow The cell row index of the chunk.
     * @param cellCol The cell column index of the chunk.
     */
    public abstract double getCellDouble(Grids_Chunk chunk,
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
            boolean hoome) 
            throws IOException, Exception, ClassNotFoundException {
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
            double y, long row, long col)
            throws IOException, Exception, ClassNotFoundException;

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
            long row, long col, boolean hoome) 
            throws IOException, Exception, ClassNotFoundException {
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
            long col)
            throws IOException, Exception, ClassNotFoundException;

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
            boolean hoome) 
            throws IOException, Exception, ClassNotFoundException {
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
            double y)
            throws IOException, Exception, ClassNotFoundException;

    /**
     * @return the average of the nearest data values to point given by
     * x-coordinate x, y-coordinate y as a double.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param hoome If true then OutOfMemoryErrors are caught, swap operations
     * are initiated, then the method is re-called. If false then
     * OutOfMemoryErrors are caught and thrown.
     */
    public final double getNearestValueDouble(double x, double y, boolean hoome)
            throws IOException, Exception, ClassNotFoundException {
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
    protected abstract <T extends Number> double getNearestValueDouble(double x, double y) 
            throws IOException, Exception, ClassNotFoundException;

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
            long col, boolean hoome) 
            throws IOException, Exception, ClassNotFoundException {
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
            long row, long col)            throws IOException, Exception, ClassNotFoundException;

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
    public final double getNearestValueDouble(long row, long col, boolean hoome) 
            throws IOException, Exception, ClassNotFoundException {
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
    protected abstract double getNearestValueDouble(long row, long col)
            throws IOException, Exception, ClassNotFoundException;

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
            boolean hoome) throws IOException, Exception, ClassNotFoundException {
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
    protected abstract double getNearestValueDoubleDistance(double x, double y)throws IOException, Exception, ClassNotFoundException;

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
            long row, long col, boolean hoome) throws IOException, Exception, ClassNotFoundException {
        try {
            double r = getNearestValueDoubleDistance(x, y, row, col);
            env.checkAndMaybeFreeMemory(hoome);
            return r;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                env.clearMemoryReserve();
                Grids_2D_ID_int chunkID  = new Grids_2D_ID_int(getChunkRow(row), 
                        getChunkCol(col));
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
            long row, long col)throws IOException, Exception, ClassNotFoundException;

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
            boolean hoome) throws IOException, Exception, ClassNotFoundException {
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
    public Grids_StatsNumber getStats(boolean hoome) throws IOException, Exception {
        return (Grids_StatsNumber) super.getStats(hoome);
    }
}
