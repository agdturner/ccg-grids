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
 */
package io.github.agdturner.grids.d2.grid;

import io.github.agdturner.grids.d2.grid.d.Grids_GridDouble;
import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;
import java.io.IOException;
import io.github.agdturner.grids.core.Grids_2D_ID_long;
import io.github.agdturner.grids.core.Grids_Environment;
import io.github.agdturner.grids.d2.chunk.Grids_Chunk;
import io.github.agdturner.grids.d2.stats.Grids_StatsNumber;
import java.math.BigDecimal;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.agdt.generic.io.Generic_FileStore;

/**
 * For grids containing Numerical values.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public abstract class Grids_GridNumber extends Grids_Grid {

    private static final long serialVersionUID = 1L;

    protected Grids_GridNumber(Grids_Environment ge, Generic_FileStore fs,
            long id) throws Exception {
        super(ge, fs, id);
    }

    /**
     * @return Grids_Chunk cell value at at point given by x-coordinate x and
     * y-coordinate y as a double.
     * @param x The x coordinate of the point at which the cell value is
     * returned.
     * @param y The y coordinate of the point at which the cell value is
     * returned.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Number getCell(BigDecimal x, BigDecimal y) throws IOException,
            Exception, ClassNotFoundException {
        return getCell(getChunkRow(y), getChunkCol(x), getCellRow(y),
                getCellCol(x));
    }

    /**
     * @param row
     * @param col
     * @return Grids_Chunk cell value at cell row index equal to _CellRowIndex,
     * cell col index equal to _CellColIndex as a double.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Number getCell(long row, long col) throws IOException, Exception,
            ClassNotFoundException {
        return getCell(getChunkRow(row), getChunkCol(col), getCellRow(row),
                getCellCol(col));
    }

    /**
     * TODO
     *
     * @param chunkRow
     * @param chunkCol
     * @return Grids_Chunk cell value at cell row index equal to _CellRowIndex,
     * cell col index equal to _CellColIndex as a double.
     * @param cellRow The cell row index of the chunk.
     * @param cellCol The cell column index of the chunk.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Number getCell(int chunkRow, int chunkCol, int cellRow,
            int cellCol) throws IOException, Exception, ClassNotFoundException {
        if (!isInGrid(chunkRow, chunkCol, cellRow, cellCol)) {
            if (this instanceof Grids_GridDouble) {
                return ((Grids_GridDouble) this).getNoDataValue();
            } else {
                return ((Grids_GridInt) this).getNoDataValue();
            }
        }
        Grids_Chunk gridChunk = getChunk(chunkRow, chunkCol);
        if (gridChunk == null) {
            if (this instanceof Grids_GridDouble) {
                return ((Grids_GridDouble) this).getNoDataValue();
            } else {
                return ((Grids_GridInt) this).getNoDataValue();
            }
        }
        return getCell(gridChunk, chunkRow, chunkCol, cellRow, cellCol);
    }

    /**
     * @param chunkCol
     * @param chunkRow
     * @return Cell value at chunk cell row index cellRow, chunk cell col index
     * cellCol of Grids_Chunk given by chunk row chunkRow, chunk col chunkCol.
     * @param chunk The Grids_Chunk containing the cell.
     * @param cellRow The cell row index of the chunk.
     * @param cellCol The cell column index of the chunk.
     */
    public abstract Number getCell(Grids_Chunk chunk, int chunkRow,
            int chunkCol, int cellRow, int cellCol);

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values nearest to point with position given by: x-coordinate x,
     * y-coordinate y; and, cell row index row, cell column index col.
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col The column index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param dp The number of decimal places the result is to be accurate to.
     * @param rm The {@link RoundingMode} to use when rounding the result.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected abstract NearestValuesCellIDsAndDistance
         getNearestValuesCellIDsAndDistance(BigDecimal x, BigDecimal y, 
                 long row, long col, int dp, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException;

    /**
     * @return a Grids_2D_ID_long[] - The CellIDs of the nearest cells with data
     * values to position given by row index rowIndex, column index colIndex.
     * @param row The row index from which the cell IDs of the nearest cells
     * with data values are returned.
     * @param col
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected abstract NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(long row,
            long col, int dp, RoundingMode rm) throws IOException, Exception,
            ClassNotFoundException;

    /**
     * @return a Grids_2D_ID_long[] The CellIDs of the nearest cells with data
     * values to point given by x-coordinate x, y-coordinate y.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    protected NearestValuesCellIDsAndDistance getNearestValuesCellIDsAndDistance(BigDecimal x,
            BigDecimal y, int dp, RoundingMode rm) throws IOException,
            Exception, ClassNotFoundException {
        return getNearestValuesCellIDsAndDistance(x, y, getRow(y), getCol(x), dp, rm);
    }

    @Override
    public Grids_StatsNumber getStats(boolean hoome) throws IOException,
            Exception {
        return (Grids_StatsNumber) super.getStats(hoome);
    }
}
