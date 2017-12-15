/**
 * Version 1.0 is to handle single variable 2DSquareCelled raster data.
 * Copyright (C) 2017 Andy Turner, CCG, University of Leeds, UK.
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
package uk.ac.leeds.ccg.andyt.grids.core.grid;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.BitSet;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.statistics.Grids_AbstractGridNumberStatistics;

/**
 * A class for representing grids of double precision values.
 *
 * @see Grids_AbstractGrid2DSquareCell
 */
public abstract class Grids_GridBinary
        extends Grids_AbstractGrid
        implements Serializable {

    BitSet data;
    /**
     * A reference to the grid Statistics Object.
     */
    protected Grids_AbstractGridNumberStatistics GridStatistics;

    protected Grids_GridBinary() {
    }

    /**
     * Creates a new Grids_GridBinary.
     *
     * @param ge
     * @param directory
     */
    public Grids_GridBinary(Grids_Environment ge, File directory) {
        super(ge, directory);
    }

    /**
     * @param directory The directory to be used for swapping.
     * @param gridFile The directory containing the file "thisFile"
     * from which the ois was constructed.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param ge
     * @param hoome If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected Grids_GridBinary(
            File directory,
            File gridFile,
            ObjectInputStream ois,
            Grids_Environment ge,
            boolean hoome) {
        super(ge, directory);
        // @TODO Code
    }

    /**
     * @return a string description of the instance. Basically the values of
     * each field.
     * @param hoome If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public String toString(
            boolean hoome) {
        try {
            String result = "GridBinary( "
                    + super.toString(hoome) + " )";
            ge.checkAndMaybeFreeMemory(hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                ge.clearMemoryReserve();
                if (!ge.swapChunk(ge.HOOMEF)) {
                        throw e;
                }
                ge.initMemoryReserve(hoome);
                return toString(
                        hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * @param valueToSet
     * @return the value at _CellRowIndex, _CellColIndex as a double and sets it
     * to valueToSet.
     * @param row The cell row index.
     * @param col The cell column index.
     * @param hoome If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double setCell(long row, long col, double valueToSet, boolean hoome) {
        try {
            double result = setCell(row, col, valueToSet);
            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
            ge.checkAndMaybeFreeMemory(this, chunkID, hoome);
            return result;
        } catch (OutOfMemoryError e) {
            if (hoome) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRow(row), getChunkCol(col));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return setCell(row, col, valueToSet, hoome);
            } else {
                throw e;
            }
        }
    }

    /**
     * Sets the value at _CellRowIndex, _CellColIndex to valueToSet.
     *
     * @param row The cell row index.
     * @param col The cell column index.
     * @param valueToSet The value set.
     * @return
     */
    protected abstract double setCell(long row, long col, double valueToSet);

}
