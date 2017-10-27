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
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridBinary;

/**
 * Provides general methods and controls what methods extended classes must
 * implement acting as an interface.
 */
public class Grids_GridChunkBinary
        extends Grids_AbstractGridChunk
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    boolean[][] data;

    protected Grids_GridChunkBinary() {
    }

    protected Grids_GridChunkBinary(Grids_Environment ge) {
        super(ge);
    }

    @Override
    protected void initData() {
        boolean handleOutOfMemoryError = false;
        Grids_GridBinary g = getGrid();
        int chunkNrows = g.getChunkNRows(ChunkID, handleOutOfMemoryError);
        int chunkNcols = g.getChunkNCols(ChunkID, handleOutOfMemoryError);
        data = new boolean[chunkNrows][chunkNcols];
    }

    @Override
    public Grids_GridBinary getGrid(
            boolean handleOutOfMemoryError) {
        try {
            Grids_GridBinary result = getGrid();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getGrid(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * @return (Grids_GridBinary) Grid;
     */
    @Override
    protected Grids_GridBinary getGrid() {
        return (Grids_GridBinary) Grid;
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex, as a double.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public boolean getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = getCell(chunkCellRowIndex, chunkCellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getCell(chunkCellRowIndex, chunkCellColIndex, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex, as a double.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @param chunkID This is a Grids_2D_ID_int for those
     * AbstractGrid2DSquareCells not to be swapped if possible when an
     * OutOfMemoryError is encountered.
     * @return
     */
    public boolean getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean handleOutOfMemoryError,
            Grids_2D_ID_int chunkID) {
        try {
            boolean result = getCell(chunkCellRowIndex, chunkCellColIndex);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_AccountDetail(chunkID, false) == null) {
                    ge.swapChunk_AccountDetail(false);
                }
                ge.initMemoryReserve(ChunkID, handleOutOfMemoryError);
                return getCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        handleOutOfMemoryError,
                        chunkID);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the value at position given by: chunk cell row chunkCellRowIndex;
     * chunk cell row chunkCellColIndex, as a double.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @return
     */
    protected boolean getCell(
            int chunkCellRowIndex,
            int chunkCellColIndex) {
        try {
            return this.data[chunkCellRowIndex][chunkCellColIndex];
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Initialises the value at position given by: chunk cell row
     * chunkCellRowIndex; chunk cell column chunkCellColIndex. Utility method
     * for constructors of extending classes.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean valueToInitialise,
            boolean handleOutOfMemoryError) {
        try {
            initCell(
                    chunkCellRowIndex,
                    chunkCellColIndex,
                    valueToInitialise);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, false) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                initCell(
                        chunkCellRowIndex,
                        chunkCellColIndex,
                        valueToInitialise,
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises the value at position given by: chunk cell row
     * chunkCellRowIndex; chunk cell column chunkCellColIndex. Utility method
     * for constructors of extending classes.
     *
     * @param chunkCellRowIndex the row index of the cell w.r.t. the origin of
     * this chunk
     * @param chunkCellColIndex the column index of the cell w.r.t. the origin
     * of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     */
    protected void initCell(
            int chunkCellRowIndex,
            int chunkCellColIndex,
            boolean valueToInitialise) {
        this.data[chunkCellRowIndex][chunkCellColIndex] = valueToInitialise;
    }

    /**
     * For clearing the data associated with this.
     */
    @Override
    protected void clearData() {
        data = null;
    }

    protected boolean[][] getData() {
        return data;
    }

    @Override
    protected Grids_GridChunkBinaryIterator iterator() {
        return new Grids_GridChunkBinaryIterator(this);
    }

}
