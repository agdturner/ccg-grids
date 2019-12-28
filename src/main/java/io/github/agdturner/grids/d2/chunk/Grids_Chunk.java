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
 */package io.github.agdturner.grids.d2.chunk;

import io.github.agdturner.grids.d2.grid.Grids_Grid;
import io.github.agdturner.grids.core.Grids_2D_ID_int;
import io.github.agdturner.grids.core.Grids_Object;

/**
 * For chunks.
 * 
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_Chunk extends Grids_Object {

    private static final long serialVersionUID = 1L;

    /**
     * A reference to the Grid.
     */
    protected transient Grids_Grid Grid;

    /**
     * For storing the ID of this.
     */
    protected Grids_2D_ID_int ChunkID;

    /**
     * The number of rows in the chunk.
     */
    protected int ChunkNRows;

    /**
     * The number of columns in the chunk.
     */
    protected int ChunkNCols;

    /**
     * Indicator for whether the cache of this chunk is upToDate.
     */
    protected transient boolean CacheUpToDate;

    /**
     * Indicator for whether the cache of this chunk is upToDate.
     */
    protected final boolean worthClearing;

    /**
     * @param g What {@link #Grid} is set to.
     * @param i What {@link #id} is set to.
     * @param worthClearing What {@link #worthClearing} is set to.
     */
    protected Grids_Chunk(Grids_Grid g, Grids_2D_ID_int i, 
            boolean worthClearing) {
        super(g.env);
        Grid = g;
        ChunkID = i;
        ChunkNRows = Grid.getChunkNRows(ChunkID);
        ChunkNCols = Grid.getChunkNCols(ChunkID);
        CacheUpToDate = false;
        this.worthClearing = worthClearing;
    }

    /**
     * @return {@link #Grid}
     */
    public abstract Grids_Grid getGrid();

    /**
     * Initialises Grid = g.
     *
     * @param g What {}
     */
    public final void initGrid(Grids_Grid g) {
        Grid = g;
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
     * Returns CacheUpToDate. This method is public so that it can be accessed
     * in memory management without checking there is enough memory to continue.
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
     * Returns the number of cells with data values or values of true.
     *
     * @return
     */
    public abstract Long getN();
}
