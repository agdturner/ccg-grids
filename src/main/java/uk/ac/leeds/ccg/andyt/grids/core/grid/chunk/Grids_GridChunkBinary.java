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

import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridBinary;

/**
 * For binary grid chunks..
 */
public class Grids_GridChunkBinary extends Grids_AbstractGridChunk {

    //private static final long serialVersionUID = 1L;
    boolean[][] data;

    protected Grids_GridChunkBinary() {}

    public Grids_GridChunkBinary(Grids_GridBinary g, Grids_2D_ID_int chunkID) {
        this.Grid = g;
        ChunkID = chunkID;
        initData();
    }

    @Override
    protected final void initData() {
        Grids_GridBinary g = getGrid();
        int chunkNrows = g.getChunkNRows(ChunkID);
        int chunkNcols = g.getChunkNCols(ChunkID);
        data = new boolean[chunkNrows][chunkNcols];
    }

    /**
     * @return (Grids_GridBinary) Grid;
     */
    @Override
    public Grids_GridBinary getGrid() {
        return (Grids_GridBinary) Grid;
    }

    /**
     * Returns the value at row, col.
     *
     * @param row The row of the cell w.r.t. the origin of this chunk.
     * @param col The column of the cell w.r.t. the origin of this chunk.
     * @return
     */
    public boolean getCell(int row, int col) {
        return this.data[row][col];
    }

    /**
     * Returns the value at position given by: row, col and sets it to value.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param v the value the cell is to be set to
     * @return
     */
    public boolean setCell(int row, int col, boolean v) {
        boolean oldValue;
        oldValue = this.data[row][col];
        this.data[row][col] = v;
        if (isCacheUpToDate()) {
            if (v != oldValue) {
                setCacheUpToDate(false);
            }
        }
        return oldValue;
    }

    /**
     * Initialises the value at row, col to v.
     *
     * @param row The row of the cell w.r.t. the origin of this chunk.
     * @param col The column of the cell w.r.t. the origin of this chunk.
     * @param value
     */
    public void initCell(int row, int col, boolean value) {
        this.data[row][col] = value;
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
    public Grids_GridChunkBinaryIterator iterator() {
        return new Grids_GridChunkBinaryIterator(this);
    }

    @Override
    public Long getN() {
        long n = 0;
        Grids_GridChunkBinaryIterator ite = iterator();
        while (ite.hasNext()) {
            if ((Boolean) ite.next()) {
                n++;
            }
        }
        return n;
    }

}
