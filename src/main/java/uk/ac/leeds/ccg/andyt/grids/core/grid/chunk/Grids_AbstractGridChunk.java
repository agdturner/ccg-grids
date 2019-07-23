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
public abstract class Grids_AbstractGridChunk extends Grids_Object 
        implements Serializable {

    /**
     * A reference to the Grid.
     */
    protected transient Grids_AbstractGrid Grid;

    /**
     * For storing the Grids_2D_ID_int of this.
     */
    protected Grids_2D_ID_int ChunkID;

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

    protected Grids_AbstractGridChunk(Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        super(g.env);
        Grid = g;
        ChunkID = chunkID;
        ChunkNRows = Grid.getChunkNRows(ChunkID);
        ChunkNCols = Grid.getChunkNCols(ChunkID);
        SwapUpToDate = false;
    }

    /**
     * Returns Grid.
     *
     * @return
     */
    public abstract Grids_AbstractGrid getGrid();

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
     * Returns a copy of ChunkID.
     *
     * @return
     */
    public Grids_2D_ID_int getChunkID() {
        return new Grids_2D_ID_int(ChunkID);
        //return this._ChunkID;
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
     * Returns this.SwapUpToDate
     *
     * @param swapUpToDate
     */
    public void setSwapUpToDate(boolean swapUpToDate) {
        SwapUpToDate = swapUpToDate;
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
     * @return
     */
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * Returns an iterator over the cell values. These are not guaranteed to be
     * in any particular order.
     *
     * @return
     */
    public abstract Grids_AbstractIterator iterator();

    /**
     * Returns true if row, col is in this.
     *
     * @param row
     * @param col
     * @return
     */
    public boolean inChunk(int row, int col) {
        if (row >= 0 && row < ChunkNRows) {
            if (col >= 0 && col < ChunkNCols) {
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
