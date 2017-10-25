/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid;
import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
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
     * For storing the Grids_2D_ID_int of this.
     * TODO:
     * Is this transient for caching?
     */
    protected transient Grids_2D_ID_int ChunkID;
    //protected Grids_2D_ID_int _ChunkID;
    /**
     * Indicator for whether the swapped version of this chunk is upToDate.
     * TODO:
     * This adds a small amount of weight, so for 64CellMap implementations
     * it may be undesirable?
     */
    protected transient boolean isSwapUpToDate;
    //protected boolean isSwapUpToDate;

    public Grids_AbstractGridChunk(){}
    
    protected Grids_AbstractGridChunk(Grids_Environment ge){
        super(ge);
    }
    
    /**
     * Returns this._Grid2DSquareCell.
     *
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_AbstractGrid getGrid(boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractGrid result = getGrid();
            result.ge.tryToEnsureThereIsEnoughMemoryToContinue(result, handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunkExcept_Account(this.getGrid(), this.ChunkID, handleOutOfMemoryError)< 1L) {
                    throw _OutOfMemoryError;
                }
                this.getGrid().ge.initMemoryReserve(this.getGrid(), this.ChunkID, handleOutOfMemoryError);
                return Grids_AbstractGridChunk.this.getGrid(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns Grid.
     * @return
     */
    protected Grids_AbstractGrid getGrid() {
        return Grid;
    }

    /**
     * Initialises _Grid2DSquareCell.
     *
     * @param g
     */
    public final void initGrid(Grids_AbstractGrid g) {
        this.setGrid(g);
    }

    /**
     * Initialises _ChunkID.
     *
     * @param chunkID
     */
    public void initChunkID(Grids_2D_ID_int chunkID) {
        this.ChunkID = chunkID;
    }

    /**
     * Returns a copy of this._ChunkID.
     *
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_2D_ID_int getChunkID(boolean handleOutOfMemoryError) {
        try {
            Grids_2D_ID_int result = getChunkID();
            Grids_AbstractGridChunk.this.getGrid(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clearMemoryReserve();
                if (this.getGrid().ge.swapChunkExcept_Account(this.getGrid(), this.ChunkID, handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                this.getGrid().ge.initMemoryReserve(this.getGrid(), this.ChunkID, handleOutOfMemoryError);
                return getChunkID(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns a copy of this._ChunkID.
     * @return
     */
    protected Grids_2D_ID_int getChunkID() {
        return new Grids_2D_ID_int(this.ChunkID);
        //return this._ChunkID;
    }

    /**
     * Returns this.isSwapUpToDate
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public boolean getIsSwapUpToDate(boolean handleOutOfMemoryError) {
        try {
            boolean result = getIsSwapUpToDate();
            Grids_AbstractGridChunk.this.getGrid(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clearMemoryReserve();
                if (this.getGrid().ge.swapChunkExcept_Account(this.getGrid(), this.ChunkID, handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                this.getGrid().ge.initMemoryReserve(this.getGrid(), this.ChunkID, handleOutOfMemoryError);
                return getIsSwapUpToDate(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns this.isSwapUpToDate
     * @return
     */
    protected boolean getIsSwapUpToDate() {
        return this.isSwapUpToDate;
    }

    /**
     * Sets this.isSwapUpToDate to isSwapUpToDate
     * @param isSwapUpToDate
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     */
    public void setIsSwapUpToDate(boolean isSwapUpToDate, boolean handleOutOfMemoryError) {
        try {
            setIsSwapUpToDate(isSwapUpToDate);
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clearMemoryReserve();
                if (this.getGrid().ge.swapChunkExcept_Account(this.getGrid(), this.ChunkID, handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                this.getGrid().ge.initMemoryReserve(this.getGrid(), this.ChunkID, handleOutOfMemoryError);
                setIsSwapUpToDate(isSwapUpToDate, handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns this.isSwapUpToDate
     * @param isSwapUpToDate
     */
    protected void setIsSwapUpToDate(boolean isSwapUpToDate) {
        this.isSwapUpToDate = isSwapUpToDate;
    }

    /**
     * For returning a description of this.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public String toString(boolean handleOutOfMemoryError) {
        try {
            String result = getDescription();
            Grids_AbstractGridChunk.this.getGrid(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clearMemoryReserve();
                if (this.getGrid().ge.swapChunkExcept_Account(this.getGrid(), this.ChunkID, handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                this.getGrid().ge.initMemoryReserve(this.getGrid(), this.ChunkID, handleOutOfMemoryError);
                return toString(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * For returning a description of this.
     * @return
     */
    protected String getDescription() {
        return this.getClass().toString() + "( ChunkID ( " + this.ChunkID.toString() + " ) )";
    }

    /**
     * Returns the name of this.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public String getName(boolean handleOutOfMemoryError) {
        try {
            String result = getName();
            Grids_AbstractGridChunk.this.getGrid(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clearMemoryReserve();
                if (this.getGrid().ge.swapChunkExcept_Account(this.getGrid(), this.ChunkID, handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                this.getGrid().ge.initMemoryReserve(this.getGrid(), this.ChunkID, handleOutOfMemoryError);
                return getName(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns the name of this.
     * @return
     */
    protected String getName() {
        return this.getClass().getName();
    }

    /**
     * Returns an iterator over the cell values. These are not guaranteed
     * to be in any particular order.
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public Grids_AbstractIterator iterator(boolean handleOutOfMemoryError) {
        try {
            Grids_AbstractIterator result = iterator();
            Grids_AbstractGridChunk.this.getGrid(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clearMemoryReserve();
                if (this.getGrid().ge.swapChunkExcept_Account(this.getGrid(), this.ChunkID, handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                this.getGrid().ge.initMemoryReserve(this.getGrid(), this.ChunkID, handleOutOfMemoryError);
                return iterator(handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns an iterator over the cell values. These are not guaranteed
     * to be in any particular order.
     * @return
     */
    protected abstract Grids_AbstractIterator iterator();

    /**
     * Returns true if the cell given by chunk cell row index
     * chunkCellRowIndex, chunk cell col index chunkCellColIndex is in this.
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @param handleOutOfMemoryError
     *   If true then OutOfMemoryErrors are caught, swap operations are
     *     initiated, then the method is re-called.
     *   If false then OutOfMemoryErrors are caught and thrown.
     * @return
     */
    public boolean inChunk(int chunkCellRowIndex, int chunkCellColIndex, boolean handleOutOfMemoryError) {
        try {
            boolean result = inChunk(chunkCellRowIndex, chunkCellColIndex);
            Grids_AbstractGridChunk.this.getGrid(handleOutOfMemoryError).ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError _OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                this.getGrid().ge.clearMemoryReserve();
                if (this.getGrid().ge.swapChunkExcept_Account(this.getGrid(), this.ChunkID, handleOutOfMemoryError) < 1L) {
                    throw _OutOfMemoryError;
                }
                this.getGrid().ge.initMemoryReserve(this.getGrid(), this.ChunkID, handleOutOfMemoryError);
                return inChunk(chunkCellRowIndex, chunkCellColIndex, handleOutOfMemoryError);
            } else {
                throw _OutOfMemoryError;
            }
        }
    }

    /**
     * Returns true if the cell given by chunk cell row index
     * chunkCellRowIndex, chunk cell col index chunkCellColIndex is in this.
     * @param chunkCellRowIndex
     * @param chunkCellColIndex
     * @return
     */
    protected boolean inChunk(int chunkCellRowIndex, int chunkCellColIndex) {
        int chunkNrows = this.getGrid().getChunkNRows(this.ChunkID, false);
        int chunkNcols = this.getGrid().getChunkNCols(this.ChunkID, false);
        return chunkCellRowIndex > -1 && chunkCellRowIndex < chunkNrows && chunkCellColIndex > -1 && chunkCellColIndex < chunkNcols;
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
        this.Grid = grid;
    }
    
}
