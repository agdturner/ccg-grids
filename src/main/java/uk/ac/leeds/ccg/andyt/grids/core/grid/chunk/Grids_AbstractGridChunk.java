/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Object;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 *
 * @author geoagdt
 */
public abstract class Grids_AbstractGridChunk extends Grids_Object implements Serializable {

    /**
     * A reference to the Grid.
     */
    protected transient Grids_AbstractGrid Grid;

    /**
     * For storing the Grids_2D_ID_int of this. TODO: Is this transient for
     * caching?
     */
    protected transient Grids_2D_ID_int ChunkID;

    protected int ChunkNRows;

    protected int ChunkNCols;

    //protected Grids_2D_ID_int _ChunkID;
    /**
     * Indicator for whether the swapped version of this chunk is upToDate.
     * TODO: This adds a small amount of weight, so for 64CellMap
     * implementations it may be undesirable?
     */
    protected transient boolean SwapUpToDate;
    //protected boolean SwapUpToDate;

    protected Grids_AbstractGridChunk() {
    }

    protected Grids_AbstractGridChunk(Grids_AbstractGrid g, Grids_2D_ID_int chunkID) {
        super(g.ge);
        Grid = g;
        ChunkID = chunkID;
        ChunkNRows = Grid.getChunkNRows(ChunkID, ge.HandleOutOfMemoryErrorFalse);
        ChunkNCols = Grid.getChunkNCols(ChunkID, ge.HandleOutOfMemoryErrorFalse);
        SwapUpToDate = false;
    }

    /**
     * Returns this._Grid2DSquareCell.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final Grids_AbstractGrid getGrid(boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGrid result = getGrid();
            result.ge.tryToEnsureThereIsEnoughMemoryToContinue(ChunkID, handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return Grids_AbstractGridChunk.this.getGrid(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns Grid.
     *
     * @return
     */
    protected abstract Grids_AbstractGrid getGrid();

    /**
     * Initialises Grid = g.
     *
     * @param g
     */
    public final void initGrid(Grids_AbstractGrid g) {
        setGrid(g);
    }

    /**
     * Initialises _ChunkID.
     *
     * @param chunkID
     */
    public void initChunkID(Grids_2D_ID_int chunkID) {
        ChunkID = chunkID;
    }

    /**
     * Returns a copy of this._ChunkID.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final Grids_2D_ID_int getChunkID(boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = getChunkID();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getChunkID(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns a copy of this._ChunkID.
     *
     * @return
     */
    protected Grids_2D_ID_int getChunkID() {
        return new Grids_2D_ID_int(ChunkID);
        //return this._ChunkID;
    }

    /**
     * Returns this.SwapUpToDate
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final boolean isSwapUpToDate(boolean handleOutOfMemoryError) {
        try {
            boolean result = isSwapUpToDate();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return isSwapUpToDate(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns SwapUpToDate. This method is public so that it can be accessed in
     * memory management without checking there is enough memory to continue.
     *
     * @return
     */
    public boolean isSwapUpToDate() {
        return SwapUpToDate;
    }

    /**
     * Sets this.SwapUpToDate to SwapUpToDate
     *
     * @param swapUpToDate
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public final void setSwapUpToDate(boolean swapUpToDate, boolean handleOutOfMemoryError) {
        try {
            setSwapUpToDate(swapUpToDate);
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                setSwapUpToDate(swapUpToDate, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns this.SwapUpToDate
     *
     * @param swapUpToDate
     */
    protected void setSwapUpToDate(boolean swapUpToDate) {
        SwapUpToDate = swapUpToDate;
    }

    /**
     * For returning a description of this.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final String toString(boolean handleOutOfMemoryError) {
        try {
            String result = getDescription();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return toString(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * For returning a description of this.
     *
     * @return
     */
    protected String getDescription() {
        return getName() + "(ChunkID(" + ChunkID.toString() + "))";
    }

    /**
     * Returns the name of this.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final String getName(boolean handleOutOfMemoryError) {
        try {
            String result = getName();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return getName(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns the name of this.
     *
     * @return
     */
    protected String getName() {
        return this.getClass().getName();
    }

    /**
     * Returns an iterator over the cell values. These are not guaranteed to be
     * in any particular order.
     *
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final Grids_AbstractIterator iterator(boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractIterator result = iterator();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return iterator(handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns an iterator over the cell values. These are not guaranteed to be
     * in any particular order.
     *
     * @return
     */
    protected abstract Grids_AbstractIterator iterator();

    /**
     * Returns true if the cell given by chunk cell row index chunkCellRowIndex,
     * chunk cell col index chunkCellColIndex is in this.
     *
     * @param chunkRow
     * @param chunkCol
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public final boolean inChunk(
            int chunkRow,
            int chunkCol,
            boolean handleOutOfMemoryError) {
        try {
            boolean result = inChunk(chunkRow, chunkCol);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(Grid, ChunkID, handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(Grid, ChunkID, handleOutOfMemoryError);
                return inChunk(chunkRow, chunkCol, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Returns true if the cell given by chunk cell row index chunkCellRowIndex,
     * chunk cell col index chunkCellColIndex is in this.
     *
     * @param chunkRow
     * @param chunkCol
     * @return
     */
    protected boolean inChunk(int chunkRow, int chunkCol) {
        if (chunkRow >= 0 && chunkRow < ChunkNRows) {
            if (chunkCol >= 0 && chunkCol < ChunkNCols) {
                return true;
            }
        }
        return false;
    }

    /**
     * For clearing the data associated with this.
     */
    protected abstract void clearData();

    /**
     * For initialising the data associated with this.
     */
    protected abstract void initData();

    /**
     * @param grid the Grid to set
     */
    public void setGrid(Grids_AbstractGrid grid) {
        Grid = grid;
    }

}
