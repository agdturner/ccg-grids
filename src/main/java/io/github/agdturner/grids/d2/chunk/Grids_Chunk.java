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
     * A reference to the grid.
     */
    protected transient Grids_Grid grid;

    /**
     * For storing the ID of this.
     */
    protected Grids_2D_ID_int id;

    /**
     * The number of rows in the chunk.
     */
    protected int chunkNRows;

    /**
     * The number of columns in the chunk.
     */
    protected int chunkNCols;

    /**
     * Indicator for whether the cache of this chunk is upToDate.
     */
    protected transient boolean cacheUpToDate;

    /**
     * Indicator for whether the cache of this chunk is upToDate.
     */
    protected final boolean worthClearing;

    /**
     * @param g What {@link #grid} is set to.
     * @param i What {@link #id} is set to.
     * @param worthClearing What {@link #worthClearing} is set to.
     */
    protected Grids_Chunk(Grids_Grid g, Grids_2D_ID_int i,
            boolean worthClearing) {
        super(g.env);
        grid = g;
        id = i;
        chunkNRows = grid.getChunkNRows(id);
        chunkNCols = grid.getChunkNCols(id);
        cacheUpToDate = false;
        this.worthClearing = worthClearing;
    }

    /**
     * @return {@link #grid}
     */
    public abstract Grids_Grid getGrid();

    /**
     * @param g What {@link #grid} is set to.
     */
    public final void initGrid(Grids_Grid g) {
        grid = g;
    }

    /**
     * @param i What {@link id} is set to.
     */
    public void initChunkID(Grids_2D_ID_int i) {
        id = i;
    }

    /**
     * @return A copy of {@link #id}.
     */
    public Grids_2D_ID_int getId() {
        return new Grids_2D_ID_int(id);
        //return id;
    }

    /**
     * This method is public so that it can be accessed in memory management
     * without checking there is enough memory to continue.
     *
     * @return {@link #cacheUpToDate}
     */
    public boolean isCacheUpToDate() {
        return cacheUpToDate;
    }

    /**
     * Sets {@link #cacheUpToDate} to {@code b}.
     *
     * @param b What {@link #cacheUpToDate} is set to.
     */
    public void setCacheUpToDate(boolean b) {
        cacheUpToDate = b;
    }

    /**
     * @return A text description of this.
     */
    protected String getDescription() {
        return getName() + ", id=" + id.toString();
    }

    /**
     * @return The name of this.
     */
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * @param row The chunk row.
     * @param col The chunk column.
     * @return {@code true} if row, col is in this.
     */
    public boolean inChunk(int row, int col) {
        if (row >= 0 && row < chunkNRows) {
            if (col >= 0 && col < chunkNCols) {
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
     * @return The number of cells with data values or values that are
     * {@code true}.
     */
    public abstract Long getN();
}
