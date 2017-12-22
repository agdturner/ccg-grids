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
package uk.ac.leeds.ccg.andyt.grids.core.grid.chunk;

import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import java.io.Serializable;
import java.util.Arrays;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;

/**
 * Grids_AbstractGridChunkDouble extension that stores cell values in a
 * double[][].
 */
public class Grids_GridChunkDoubleArray
        extends Grids_AbstractGridChunkDoubleArrayOrMap
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    /**
     * For storing values arranged in rows and columns.
     */
    private double[][] Data;

    protected Grids_GridChunkDoubleArray() {
    }

    /**
     * Creates a new Grids_GridChunkDoubleArray for g containing all no Data
     * values.
     *
     * @param g The Grids_GridDouble this is to be a chunk of.
     * @param chunkID The ID to be ChunkID.
     */
    protected Grids_GridChunkDoubleArray(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID) {
        super(g, chunkID);
        double noDataValue = g.getNoDataValue();
        Data = new double[ChunkNRows][ChunkNCols];
        int row;
        for (row = 0; row < ChunkNRows; row++) {
            Arrays.fill(Data[row], noDataValue);
        }
        SwapUpToDate = false;
    }

    /**
     * Creates a new Grids_GridChunkDoubleArray from chunk.
     *
     *
     * @param chunk The Grids_AbstractGridChunkDouble this values are taken
     * from.
     * @param chunkID The ID to be ChunkID.
     * @TODO optimise for different types of chunk.
     */
    protected Grids_GridChunkDoubleArray(
            Grids_AbstractGridChunkDouble chunk,
            Grids_2D_ID_int chunkID) {
        super(chunk.getGrid(), chunkID);
        initData();
        int row;
        int col;
        for (row = 0; row < ChunkNRows; row++) {
            for (col = 0; col < ChunkNCols; col++) {
                Data[row][col] = chunk.getCell(row, col);
            }
        }
        SwapUpToDate = false;
    }

    /**
     * Initialises the Data associated with this.
     */
    @Override
    protected final void initData() {
        Data = new double[ChunkNRows][ChunkNCols];
    }

    protected double[][] getData() {
        return Data;
    }

    protected @Override
    void clearData() {
        Data = null;
        //System.gc();
    }

    /**
     * Beware OutOfMemoryErrors being thrown if calling this method.
     *
     * @param row
     * @param col
     * @return
     */
    @Override
    public double getCell(
            int row,
            int col) {
        return Data[row][col];
    }

    @Override
    public void initCell(
            int row,
            int col,
            double valueToInitialise) {
        Data[row][col] = valueToInitialise;
    }

    /**
     * Returns the value at position given by: row, col and sets it to value.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param v the value the cell is to be set to
     * @return
     */
    @Override
    public double setCell(
            int row,
            int col,
            double v) {
        double oldValue;
        oldValue = Data[row][col];
        Data[row][col] = v;
        if (isSwapUpToDate()) {
            if (v != oldValue) {
                setSwapUpToDate(false);
            }
        }
        return oldValue;
    }

    /**
     * Returns a Grids_GridChunkDoubleArrayOrMapIterator for iterating over the
     * cells in this.
     *
     * @return
     */
    @Override
    public Grids_GridChunkDoubleArrayOrMapIterator iterator() {
        return new Grids_GridChunkDoubleArrayOrMapIterator(this);
    }

    @Override
    public Double getMin(boolean update) {
        Double result = Data[0][0];
        Grids_GridDouble g = getGrid();
        double noDataValue = g.getNoDataValue();
        double v;
        int row;
        int col;
        for (row = 0; row < ChunkNRows; row++) {
            for (col = 0; col < ChunkNCols; col++) {
                v = Data[ChunkNRows][ChunkNCols];
                if (v != noDataValue) {
                    result = Math.min(result, v);
                }
            }
        }
        return result;
    }

    @Override
    public Number getMax(boolean update) {
        Double result = Data[0][0];
        Grids_GridDouble g = getGrid();
        double noDataValue = g.getNoDataValue();
        double v;
        int row;
        int col;
        for (row = 0; row < ChunkNRows; row++) {
            for (col = 0; col < ChunkNCols; col++) {
                v = Data[ChunkNRows][ChunkNCols];
                if (v != noDataValue) {
                    result = Math.max(result, v);
                }
            }
        }
        return result;
    }

}
