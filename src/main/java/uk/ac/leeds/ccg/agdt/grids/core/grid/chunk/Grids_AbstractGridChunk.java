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
 */package uk.ac.leeds.ccg.agdt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_AbstractGrid;
import java.io.Serializable;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Object;

/**
 *
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_AbstractGridChunk extends Grids_Object {

    private static final long serialVersionUID = 1L;

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
    protected transient boolean CacheUpToDate;
    //protected boolean CacheUpToDate;

    protected Grids_AbstractGridChunk() {
    }

    protected Grids_AbstractGridChunk(Grids_AbstractGrid g,
            Grids_2D_ID_int chunkID) {
        super(g.env);
        Grid = g;
        ChunkID = chunkID;
        ChunkNRows = Grid.getChunkNRows(ChunkID);
        ChunkNCols = Grid.getChunkNCols(ChunkID);
        CacheUpToDate = false;
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
     * Returns CacheUpToDate. This method is public so that it can be accessed in
     * memory management without checking there is enough memory to continue.
     *
     * @return
     */
    public boolean isCacheUpToDate() {
        return CacheUpToDate;
    }

    /**
     * Sets {@link #CacheUpToDate} to b. 
     *
     * @param b
     */
    public void setCacheUpToDate(boolean b) {
        CacheUpToDate = b;
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

    /**
     * Returns the number of cells with data values or values of true.
     *
     * @return
     */
    public abstract Long getN();
}
