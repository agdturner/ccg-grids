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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGrid2DSquareCellDoubleChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGrid2DSquareCellIntChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellDoubleChunk64CellMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellDoubleChunkArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellDoubleChunkMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellIntChunk64CellMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellIntChunkArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_Grid2DSquareCellIntChunkMap;

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
public abstract class Grids_AbstractGrid2DSquareCell
        extends Grids_AbstractGrid {

    protected Grids_AbstractGrid2DSquareCell(){}
    
    public Grids_AbstractGrid2DSquareCell(Grids_Environment ge) {
        super(ge);
    }
    
    /**
     * @return Grids_AbstractGridChunk cell value at at
     * Point2D.Double point as a double.
     * @param point The Point2D.Double for which the cell value is returned.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(
            Point2D.Double point,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(point);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(point.getY()),
                        getChunkColIndex(point.getX()));
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
     * @return Grids_AbstractGridChunk cell value at at
     * Point2D.Double point as a double.
     * @param point The Point2D.Double for which the cell value is returned.
     */
    protected double getCellDouble(
            Point2D.Double point) {
        return getCellDouble(
                getChunkRowIndex(point.getY()),
                getChunkColIndex(point.getX()),
                getChunkCellRowIndex(point.getY()),
                getChunkCellColIndex(point.getX()));
    }

    /**
     * @return Grids_AbstractGridChunk cell value at at point given
     * by x-coordinate x and y-coordinate y as a double.
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
                ge.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(y),
                        getChunkColIndex(x));
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
     * @return Grids_AbstractGridChunk cell value at at point given
     * by x-coordinate x and y-coordinate y as a double.
     * @param x The x coordinate of the point at which the cell value is
     * returned.
     * @param y The y coordinate of the point at which the cell value is
     * returned.
     */
    protected double getCellDouble(
            double x,
            double y) {
        return getCellDouble(
                getChunkRowIndex(y),
                getChunkColIndex(x),
                getChunkCellRowIndex(y),
                getChunkCellColIndex(x));
    }

    /**
     * @param cellRowIndex
     * @param cellColIndex
     * @return Grids_AbstractGridChunk cell value at cell row index
     * equal to _CellRowIndex, cell col index equal to _CellColIndex as a
     * double.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(
                    cellRowIndex,
                    cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;

            }
        }
    }

    /**
     * @param cellRowIndex
     * @param cellColIndex
     * @return Grids_AbstractGridChunk cell value at cell row index
     * equal to _CellRowIndex, cell col index equal to _CellColIndex as a
     * double.
     */
    protected double getCellDouble(
            long cellRowIndex,
            long cellColIndex) {
        return getCellDouble(
                getChunkRowIndex(cellRowIndex),
                getChunkColIndex(cellColIndex),
                getChunkCellRowIndex(cellRowIndex),
                getChunkCellColIndex(cellColIndex));
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Grids_AbstractGridChunk cell value at cell row index
     * equal to _CellRowIndex, cell col index equal to _CellColIndex as a
     * double.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(
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
                ge.clear_MemoryReserve();
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
     * @return Grids_AbstractGridChunk cell value at cell row index
     * equal to _CellRowIndex, cell col index equal to _CellColIndex as a
     * double.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected double getCellDouble(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        if (!isInGrid(chunkRowIndex, chunkColIndex)) {
            if (this instanceof Grids_Grid2DSquareCellDouble) {
                return ((Grids_Grid2DSquareCellDouble) this)._NoDataValue;
            } else {
                //this instanceof Grids_Grid2DSquareCellInt
                return ((Grids_Grid2DSquareCellInt) this).getNoDataValue();
            }
        }
        Grids_AbstractGridChunk grid2DSquareCellChunk
                = getGrid2DSquareCellChunk(
                        chunkRowIndex,
                        chunkColIndex);
        if (grid2DSquareCellChunk == null) {
            return this.getNoDataValueBigDecimal(false).doubleValue();
        }
        return getCellDouble(
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
     * col index chunkCellColIndex of Grids_AbstractGridChunk given
     * by chunk row index _Row, chunk col index _Col as a double.
     * @param grid2DSquareCellChunk The Grids_AbstractGridChunk
     * containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double getCellDouble(
            Grids_AbstractGridChunk grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            double result = getCellDouble(
                    grid2DSquareCellChunk,
                    chunkRowIndex,
                    chunkColIndex,
                    chunkCellRowIndex,
                    chunkCellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        chunkRowIndex,
                        chunkColIndex);
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellDouble(
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
     * TODO
     *
     * @param chunkColIndex
     * @param chunkRowIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGridChunk given
     * by chunk row index _Row, chunk col index _Col as a double.
     * @param chunk The Grids_AbstractGridChunk
     * containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected double getCellDouble(
            Grids_AbstractGridChunk chunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        if (chunk instanceof Grids_AbstractGrid2DSquareCellDoubleChunk) {
            Grids_AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellDoubleChunk
                    = (Grids_AbstractGrid2DSquareCellDoubleChunk) chunk;
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble
                    = grid2DSquareCellDoubleChunk.getGrid2DSquareCellDouble(false);
            double noDataValue = grid2DSquareCellDouble._NoDataValue;
            if (chunk.getClass()
                    == Grids_Grid2DSquareCellDoubleChunk64CellMap.class) {
                Grids_Grid2DSquareCellDoubleChunk64CellMap grid2DSquareCellDoubleChunk64CellMap
                        = (Grids_Grid2DSquareCellDoubleChunk64CellMap) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunk.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false);
            }
            if (chunk.getClass() == Grids_Grid2DSquareCellDoubleChunkArray.class) {
                Grids_Grid2DSquareCellDoubleChunkArray grid2DSquareCellDoubleChunkArray
                        = (Grids_Grid2DSquareCellDoubleChunkArray) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false);
            }
            if (chunk.getClass() == Grids_Grid2DSquareCellDoubleChunkMap.class) {
                Grids_Grid2DSquareCellDoubleChunkMap grid2DSquareCellDoubleChunkMap
                        = (Grids_Grid2DSquareCellDoubleChunkMap) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false);
            }
            return noDataValue;
        } else {
            //( grid2DSquareCellChunk instanceof Grids_AbstractGridIntChunk )
            Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk
                    = (Grids_AbstractGrid2DSquareCellIntChunk) chunk;
            Grids_Grid2DSquareCellInt grid2DSquareCellInt
                    = grid2DSquareCellIntChunk.getGrid2DSquareCellInt(false);
            int noDataValue = grid2DSquareCellInt.getNoDataValue(true);
            if (chunk.getClass()
                    == Grids_Grid2DSquareCellIntChunk64CellMap.class) {
                Grids_Grid2DSquareCellIntChunk64CellMap grid2DSquareCellIntChunk64CellMap
                        = (Grids_Grid2DSquareCellIntChunk64CellMap) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunk.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false,
                        chunk.getChunkID(false));
            }
            if (chunk.getClass() == Grids_Grid2DSquareCellIntChunkArray.class) {
                Grids_Grid2DSquareCellIntChunkArray grid2DSquareCellIntChunkArray
                        = (Grids_Grid2DSquareCellIntChunkArray) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false,
                        chunk.getChunkID(false));
            }
            if (chunk.getClass() == Grids_Grid2DSquareCellIntChunkMap.class) {
                Grids_Grid2DSquareCellIntChunkMap grid2DSquareCellIntChunkMap
                        = (Grids_Grid2DSquareCellIntChunkMap) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false,
                        chunk.getChunkID(false));
            }
            return (double) noDataValue;
        }
    }

    /**
     * @param cellRowIndex
     * @param cellColIndex
     * @return Grids_AbstractGridChunk cell value at cell row index
     * equal to cellRowIndex, cell col index equal to cellColIndex as a int.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int getCellInt(
            long cellRowIndex,
            long cellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            int result = getCellInt(
                    cellRowIndex,
                    cellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clear_MemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(
                        getChunkRowIndex(cellRowIndex),
                        getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return getCellInt(
                        cellRowIndex,
                        cellColIndex,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return Grids_AbstractGridChunk cell value at cell row index
     * equal to cellRowIndex, cell col index equal to cellColIndex as a int.
     * @param cellRowIndex The cell row index.
     * @param cellColIndex The cell column index.
     */
    protected int getCellInt(
            long cellRowIndex,
            long cellColIndex) {
        return getCellInt(
                getChunkRowIndex(cellRowIndex),
                getChunkColIndex(cellColIndex),
                getChunkCellRowIndex(cellRowIndex),
                getChunkCellColIndex(cellColIndex));
    }

    /**
     * @param chunkRowIndex
     * @param chunkColIndex
     * @return Cell value at chunk cell row index chunkCellRowIndex, chunk cell
     * col index chunkCellColIndex of Grids_AbstractGridChunk given
     * by chunk row index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int getCellInt(
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
                ge.clear_MemoryReserve();
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
     * col index chunkCellColIndex of Grids_AbstractGridChunk given
     * by chunk row index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected int getCellInt(
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        Grids_AbstractGridChunk grid2DSquareCellChunk = getGrid2DSquareCellChunk(
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
     * col index chunkCellColIndex of Grids_AbstractGridChunk given
     * by chunk row index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param grid2DSquareCellChunk The Grids_AbstractGridChunk
     * containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public int getCellInt(
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
                ge.clear_MemoryReserve();
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
     * col index chunkCellColIndex of Grids_AbstractGridChunk given
     * by chunk row index chunkRowIndex, chunk col index chunkColIndex as a int.
     * @param chunk The Grids_AbstractGridChunk
     * containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected int getCellInt(
            Grids_AbstractGridChunk chunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        if (chunk instanceof Grids_AbstractGrid2DSquareCellDoubleChunk) {
            Grids_AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellDoubleChunk
                    = (Grids_AbstractGrid2DSquareCellDoubleChunk) chunk;
            Grids_Grid2DSquareCellDouble g
                    = grid2DSquareCellDoubleChunk.getGrid2DSquareCellDouble(false);
            double noDataValue = g._NoDataValue;
            if (chunk.getClass() == Grids_Grid2DSquareCellDoubleChunk64CellMap.class) {
                Grids_Grid2DSquareCellDoubleChunk64CellMap grid2DSquareCellDoubleChunk64CellMap
                        = (Grids_Grid2DSquareCellDoubleChunk64CellMap) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunk64CellMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false);
            }
            if (chunk.getClass() == Grids_Grid2DSquareCellDoubleChunkArray.class) {
                Grids_Grid2DSquareCellDoubleChunkArray grid2DSquareCellDoubleChunkArray
                        = (Grids_Grid2DSquareCellDoubleChunkArray) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false);
            }
            if (chunk.getClass() == Grids_Grid2DSquareCellDoubleChunkMap.class) {
                Grids_Grid2DSquareCellDoubleChunkMap grid2DSquareCellDoubleChunkMap
                        = (Grids_Grid2DSquareCellDoubleChunkMap) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false);
            }
            return (int) noDataValue;
        } else {
            //if ( grid2DSquareCellChunk instanceof Grids_AbstractGrid2DSquareCellIntChunk ) {
            Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk
                    = (Grids_AbstractGrid2DSquareCellIntChunk) chunk;
            Grids_Grid2DSquareCellInt grid2DSquareCellInt
                    = grid2DSquareCellIntChunk.getGrid2DSquareCellInt(false);
            int noDataValue = grid2DSquareCellInt.getNoDataValue();
            if (chunk.getClass() == Grids_Grid2DSquareCellIntChunk64CellMap.class) {
                Grids_Grid2DSquareCellIntChunk64CellMap grid2DSquareCellIntChunk64CellMap
                        = (Grids_Grid2DSquareCellIntChunk64CellMap) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunk64CellMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false,
                        chunk.getChunkID(false));
            }
            if (chunk.getClass() == Grids_Grid2DSquareCellIntChunkArray.class) {
                Grids_Grid2DSquareCellIntChunkArray grid2DSquareCellIntChunkArray
                        = (Grids_Grid2DSquareCellIntChunkArray) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false,
                        chunk.getChunkID(false));
            }
            if (chunk.getClass() == Grids_Grid2DSquareCellIntChunkMap.class) {
                Grids_Grid2DSquareCellIntChunkMap grid2DSquareCellIntChunkMap
                        = (Grids_Grid2DSquareCellIntChunkMap) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue,
                        false,
                        chunk.getChunkID(false));
            }
            return noDataValue;
        }
    }


}
