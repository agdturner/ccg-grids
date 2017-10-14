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
package uk.ac.leeds.ccg.andyt.grids.core;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_FileCreator;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_UnsignedLongPowersOf2;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

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
     * @param cellRowIndex The cell row index of the .
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
     * @param grid2DSquareCellChunk The Grids_AbstractGridChunk
     * containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected double getCellDouble(
            Grids_AbstractGridChunk grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        if (grid2DSquareCellChunk instanceof Grids_AbstractGrid2DSquareCellDoubleChunk) {
            Grids_AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellDoubleChunk
                    = (Grids_AbstractGrid2DSquareCellDoubleChunk) grid2DSquareCellChunk;
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble
                    = grid2DSquareCellDoubleChunk.getGrid2DSquareCellDouble();
            double noDataValue = grid2DSquareCellDouble._NoDataValue;
            if (grid2DSquareCellChunk.getClass()
                    == Grids_Grid2DSquareCellDoubleChunk64CellMap.class) {
                Grids_Grid2DSquareCellDoubleChunk64CellMap grid2DSquareCellDoubleChunk64CellMap
                        = (Grids_Grid2DSquareCellDoubleChunk64CellMap) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunk.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkArray.class) {
                Grids_Grid2DSquareCellDoubleChunkArray grid2DSquareCellDoubleChunkArray
                        = (Grids_Grid2DSquareCellDoubleChunkArray) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkJAI.class) {
                Grids_Grid2DSquareCellDoubleChunkJAI grid2DSquareCellDoubleChunkJAI
                        = (Grids_Grid2DSquareCellDoubleChunkJAI) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunkJAI.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkMap.class) {
                Grids_Grid2DSquareCellDoubleChunkMap grid2DSquareCellDoubleChunkMap
                        = (Grids_Grid2DSquareCellDoubleChunkMap) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkRAF.class) {
                Grids_Grid2DSquareCellDoubleChunkRAF grid2DSquareCellDoubleChunkRAF
                        = (Grids_Grid2DSquareCellDoubleChunkRAF) grid2DSquareCellDoubleChunk;
                return grid2DSquareCellDoubleChunkRAF.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            return noDataValue;
        } else {
            //( grid2DSquareCellChunk instanceof Grids_AbstractGridIntChunk )
            Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk
                    = (Grids_AbstractGrid2DSquareCellIntChunk) grid2DSquareCellChunk;
            Grids_Grid2DSquareCellInt grid2DSquareCellInt
                    = grid2DSquareCellIntChunk.getGrid2DSquareCellInt();
            int noDataValue = grid2DSquareCellInt.getNoDataValue(true);
            if (grid2DSquareCellChunk.getClass()
                    == Grids_Grid2DSquareCellIntChunk64CellMap.class) {
                Grids_Grid2DSquareCellIntChunk64CellMap grid2DSquareCellIntChunk64CellMap
                        = (Grids_Grid2DSquareCellIntChunk64CellMap) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunk.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkArray.class) {
                Grids_Grid2DSquareCellIntChunkArray grid2DSquareCellIntChunkArray
                        = (Grids_Grid2DSquareCellIntChunkArray) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkJAI.class) {
                Grids_Grid2DSquareCellIntChunkJAI grid2DSquareCellIntChunkJAI
                        = (Grids_Grid2DSquareCellIntChunkJAI) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunkJAI.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkMap.class) {
                Grids_Grid2DSquareCellIntChunkMap grid2DSquareCellIntChunkMap
                        = (Grids_Grid2DSquareCellIntChunkMap) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkRAF.class) {
                Grids_Grid2DSquareCellIntChunkRAF grid2DSquareCellIntChunkRAF
                        = (Grids_Grid2DSquareCellIntChunkRAF) grid2DSquareCellIntChunk;
                return (double) grid2DSquareCellIntChunkRAF.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
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
     * @param grid2DSquareCellChunk The Grids_AbstractGridChunk
     * containing the cell.
     * @param chunkCellRowIndex The cell row index of the chunk.
     * @param chunkCellColIndex The cell column index of the chunk.
     */
    protected int getCellInt(
            Grids_AbstractGridChunk grid2DSquareCellChunk,
            int chunkRowIndex,
            int chunkColIndex,
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        if (grid2DSquareCellChunk instanceof Grids_AbstractGrid2DSquareCellDoubleChunk) {
            Grids_AbstractGrid2DSquareCellDoubleChunk grid2DSquareCellDoubleChunk
                    = (Grids_AbstractGrid2DSquareCellDoubleChunk) grid2DSquareCellChunk;
            Grids_Grid2DSquareCellDouble grid2DSquareCellDouble
                    = grid2DSquareCellDoubleChunk.getGrid2DSquareCellDouble();
            double noDataValue = grid2DSquareCellDouble._NoDataValue;
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunk64CellMap.class) {
                Grids_Grid2DSquareCellDoubleChunk64CellMap grid2DSquareCellDoubleChunk64CellMap
                        = (Grids_Grid2DSquareCellDoubleChunk64CellMap) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunk64CellMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkArray.class) {
                Grids_Grid2DSquareCellDoubleChunkArray grid2DSquareCellDoubleChunkArray
                        = (Grids_Grid2DSquareCellDoubleChunkArray) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkJAI.class) {
                Grids_Grid2DSquareCellDoubleChunkJAI grid2DSquareCellDoubleChunkJAI
                        = (Grids_Grid2DSquareCellDoubleChunkJAI) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunkJAI.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkMap.class) {
                Grids_Grid2DSquareCellDoubleChunkMap grid2DSquareCellDoubleChunkMap
                        = (Grids_Grid2DSquareCellDoubleChunkMap) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellDoubleChunkRAF.class) {
                Grids_Grid2DSquareCellDoubleChunkRAF grid2DSquareCellDoubleChunkRAF
                        = (Grids_Grid2DSquareCellDoubleChunkRAF) grid2DSquareCellDoubleChunk;
                return (int) grid2DSquareCellDoubleChunkRAF.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            return (int) noDataValue;
        } else {
            //if ( grid2DSquareCellChunk instanceof Grids_AbstractGrid2DSquareCellIntChunk ) {
            Grids_AbstractGrid2DSquareCellIntChunk grid2DSquareCellIntChunk
                    = (Grids_AbstractGrid2DSquareCellIntChunk) grid2DSquareCellChunk;
            Grids_Grid2DSquareCellInt grid2DSquareCellInt
                    = grid2DSquareCellIntChunk.getGrid2DSquareCellInt();
            int noDataValue = grid2DSquareCellInt.getNoDataValue();
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunk64CellMap.class) {
                Grids_Grid2DSquareCellIntChunk64CellMap grid2DSquareCellIntChunk64CellMap
                        = (Grids_Grid2DSquareCellIntChunk64CellMap) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunk64CellMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkArray.class) {
                Grids_Grid2DSquareCellIntChunkArray grid2DSquareCellIntChunkArray
                        = (Grids_Grid2DSquareCellIntChunkArray) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunkArray.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkJAI.class) {
                Grids_Grid2DSquareCellIntChunkJAI grid2DSquareCellIntChunkJAI
                        = (Grids_Grid2DSquareCellIntChunkJAI) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunkJAI.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkMap.class) {
                Grids_Grid2DSquareCellIntChunkMap grid2DSquareCellIntChunkMap
                        = (Grids_Grid2DSquareCellIntChunkMap) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunkMap.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            if (grid2DSquareCellChunk.getClass() == Grids_Grid2DSquareCellIntChunkRAF.class) {
                Grids_Grid2DSquareCellIntChunkRAF grid2DSquareCellIntChunkRAF
                        = (Grids_Grid2DSquareCellIntChunkRAF) grid2DSquareCellIntChunk;
                return grid2DSquareCellIntChunkRAF.getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        noDataValue);
            }
            return noDataValue;
        }
    }


}
