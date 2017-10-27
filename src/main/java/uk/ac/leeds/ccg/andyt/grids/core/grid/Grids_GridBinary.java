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

import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_AbstractGridStatistics;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics1;
import uk.ac.leeds.ccg.andyt.grids.core.statistics.Grids_GridStatistics0;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_long;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDoubleFactory;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunk;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDouble64CellMap;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleArray;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_GridChunkDoubleMap;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ESRIAsciiGridImporter;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_Utilities;

/**
 * A class for representing grids of double precision values.
 *
 * @see Grids_AbstractGrid2DSquareCell
 */
public abstract class Grids_GridBinary
        extends Grids_AbstractGrid
        implements Serializable {

    BitSet data;

    protected Grids_GridBinary() {
    }

    /**
     * Creates a new Grid2DSquareCellDouble
     *
     * @param ge
     */
    public Grids_GridBinary(
            Grids_Environment ge) {
        super(ge);
        init();
    }

    /**
     * Creates a new Grid2DSquareCellDouble. Warning!! Concurrent modification
     * may occur if _Directory is in use. If a completely new instance is wanted
     * then use: Grid2DSquareCellDouble( File, Grid2DSquareCellDoubleAbstract,
     * Grids_AbstractGridChunkDoubleFactory, int, int, long, long, long, long,
     * double, HashSet) which can be accessed via a
     * Grids_Grid2DSquareCellDoubleFactory.
     *
     * @param directory The File _Directory to be used for swapping.
     * @param gridFile The File _Directory containing the File names thisFile
     * that the ois was constructed from.
     * @param ois The ObjectInputStream used in first attempt to construct this.
     * @param ge
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    protected Grids_GridBinary(
            File directory,
            File gridFile,
            ObjectInputStream ois,
            Grids_Environment ge,
            boolean handleOutOfMemoryError) {
        super(ge);
//        init(
//                directory,
//                gridFile,
//                ois,
//                handleOutOfMemoryError);
    }

    /**
     * @return a string description of the instance. Basically the values of
     * each field.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    @Override
    public String toString(
            boolean handleOutOfMemoryError) {
        try {
            String result = "GridBinary( "
                    + super.toString(0, handleOutOfMemoryError) + " )";
            ge.tryToEnsureThereIsEnoughMemoryToContinue(handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (ge.swapChunk_Account(handleOutOfMemoryError) < 1L) {
                    throw e;
                }
                ge.initMemoryReserve(handleOutOfMemoryError);
                return toString(
                        handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Initialises this.
     *
     * @see Grid2DSquareCellDouble()
     */
    private void init() {
    }

    /**
     * @param valueToSet
     * @return the value at _CellRowIndex, _CellColIndex as a double and sets it
     * to valueToSet.
     * @param cellRowIndex The cell row index.
     * @param cellColIndex The cell column index.
     * @param handleOutOfMemoryError If true then OutOfMemoryErrors are caught,
     * swap operations are initiated, then the method is re-called. If false
     * then OutOfMemoryErrors are caught and thrown.
     */
    public double setCell(long cellRowIndex, long cellColIndex, double valueToSet, boolean handleOutOfMemoryError) {
        try {
            double result = setCell(cellRowIndex, cellColIndex, valueToSet);
            Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRowIndex(cellRowIndex), getChunkColIndex(cellColIndex));
            ge.tryToEnsureThereIsEnoughMemoryToContinue(this, chunkID, handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError e) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                Grids_2D_ID_int chunkID = new Grids_2D_ID_int(getChunkRowIndex(cellRowIndex), getChunkColIndex(cellColIndex));
                freeSomeMemoryAndResetReserve(chunkID, e);
                return setCell(cellRowIndex, cellColIndex, valueToSet, handleOutOfMemoryError);
            } else {
                throw e;
            }
        }
    }

    /**
     * Sets the value at _CellRowIndex, _CellColIndex to valueToSet.
     *
     * @param cellRowIndex The cell row index.
     * @param cellColIndex The cell column index.
     * @param valueToSet The value set.
     * @return
     */
    protected abstract double setCell(long cellRowIndex, long cellColIndex, double valueToSet);

}
