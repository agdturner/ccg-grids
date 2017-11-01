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
import gnu.trove.TDoubleHashSet;
import gnu.trove.TDoubleObjectHashMap;
import gnu.trove.TDoubleObjectIterator;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
//import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.utilities.Grids_AbstractIterator;

/**
 * Grids_AbstractGridChunkDouble extension that stores cell values in a
 * TDoubleObjectHashMap.
 */
public class Grids_GridChunkDoubleMap
        extends Grids_AbstractGridChunkDouble
        implements Serializable {

    //private static final long serialVersionUID = 1L;
    /**
     * A value initialised with grid that can be used to optimise storage.
     * Storage is optimised with the defaultValue set to the most common value.
     * The location of all DefaultValues can be calculated from the converse of
     * the intersection of NoData, InDataMapHashSet and InDataMapBitSet.
     */
    private double DefaultValue;

    /**
     * Identifies the locations of all noDataValues.
     */
    private BitSet NoData;

    /**
     * Identifies which cells are stored in DataMapHashSet.
     */
    private BitSet InDataMapHashSet;

    /**
     * Identifies which cells are stored in DataMapBitSet.
     */
    private BitSet InDataMapBitSet;

    /**
     * For storing values mapped to a Grids_2D_ID_int HashSet or an individual
     * Grids_2D_ID_int.
     */
    private GridChunkDoubleMapData Data;

    /**
     * Default constructor.
     */
    protected Grids_GridChunkDoubleMap() {
    }

    /**
     * Creates a new Grid2DSquareCellDoubleChunkMap
     *
     * @param g
     * @param chunkID Default: default value to
     * grid2DSquareCellDouble.getNoDataValue()
     */
    protected Grids_GridChunkDoubleMap(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID) {
        this(
                g,
                chunkID,
                g.getNoDataValue(false));
    }

    /**
     * Creates a new Grid2DSquareCellDoubleChunkMap
     *
     * @param g
     * @param chunkID
     * @param defaultValue
     */
    protected Grids_GridChunkDoubleMap(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID,
            double defaultValue) {
        super(g, chunkID);
        DefaultValue = defaultValue;
        initData();
        SwapUpToDate = false;
    }

    /**
     * Creates a new Grid2DSquareCellDoubleChunkMap
     *
     * @param gridChunk
     * @param chunkID Default: default value to
     * grid2DSquareCellDouble.getNoDataValue() TODO: Optimise for different
     * types.
     * @param defaultValue
     */
    protected Grids_GridChunkDoubleMap(
            Grids_AbstractGridChunkDouble gridChunk,
            Grids_2D_ID_int chunkID,
            double defaultValue) {
        super(gridChunk.getGrid(), chunkID);
        boolean handleOutOfMemoryError = gridChunk.ge.HandleOutOfMemoryErrorFalse;
        DefaultValue = defaultValue;
        initData();
        double gridChunkNoDataValue;
        gridChunkNoDataValue = gridChunk.getGrid().getNoDataValue(handleOutOfMemoryError);
        double value;
        for (int row = 0; row < ChunkNRows; row++) {
            for (int col = 0; col < ChunkNCols; col++) {
                value = gridChunk.getCell(
                        row,
                        col,
                        defaultValue,
                        handleOutOfMemoryError);
                if (value == gridChunkNoDataValue) {
                    NoData.set((row * ChunkNCols) + col);
                } else {
                    if (value != defaultValue) {
                        initCell(
                                row,
                                col,
                                value);
                    }
                }
            }
        }
        this.SwapUpToDate = false;
    }

    /**
     * Initialises the Data associated with this.
     */
    @Override
    protected final void initData() {
        Data = new GridChunkDoubleMapData(
                new TreeMap<>(),
                new TreeMap<>());
        NoData = new BitSet(ChunkNCols * ChunkNRows);
        InDataMapHashSet = new BitSet(ChunkNCols * ChunkNRows);
        InDataMapBitSet = new BitSet(ChunkNCols * ChunkNRows);
    }

    /**
     * Returns this.Data TODO: This could be made public if a copy is returned!
     *
     * @return
     */
    protected GridChunkDoubleMapData getData() {
        return Data;
    }

    /**
     * Clears the Data associated with this.
     */
    protected @Override
    void clearData() {
        Data = null;
        System.gc();
    }

    /**
     * Returns values in row major order as a double[].
     *
     * @return
     */
    double[][] to2DDoubleArray() {
        Grids_GridDouble grid = getGrid();
        int nrows = grid.getChunkNRows(ChunkID, Grid.ge.HandleOutOfMemoryErrorFalse);
        int ncols = grid.getChunkNCols(ChunkID, Grid.ge.HandleOutOfMemoryErrorFalse);
        double[][] result;
        result = new double[nrows][ncols];
        Arrays.fill(result, grid.getNoDataValue(Grid.ge.HandleOutOfMemoryErrorFalse));
        Iterator<Double> ite;
        /**
         * Populate result with all mappings from data.DataMapBitSet.
         */
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        Double value;
        OffsetBitSet offsetBitSet;
        BitSet bitSet;
        int offset;
        int bitSetLength;
        int i;
        int row = 0;
        int col = 0;
        while (ite.hasNext()) {
            value = ite.next();
            offsetBitSet = dataMapBitSet.get(value);
            bitSet = offsetBitSet._BitSet;
            offset = offsetBitSet.Offset;
            bitSetLength = bitSet.length();
            for (i = 0; i < offset; i++) {
                col++;
                if (col == ncols - 1) {
                    row++;
                    col = 0;
                }
            }
            for (i = offset; i < bitSetLength + offset; i++) {
                col++;
                if (col == ncols - 1) {
                    row++;
                    col = 0;
                }
                if (bitSet.get(i)) {
                    result[row][col] = value;
                }
            }
        }
        /**
         * Populate result with all mappings from data.DataMapHashSet.
         */
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        HashSet<Grids_2D_ID_int> cellIDs;
        Iterator<Grids_2D_ID_int> ite2;
        Grids_2D_ID_int cellID;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            cellIDs = dataMapHashSet.get(value);
            ite2 = cellIDs.iterator();
            while (ite2.hasNext()) {
                cellID = ite2.next();
                row = cellID.getRow();
                col = cellID.getCol();
                result[row][col] = value;
            }
        }
        return result;
    }

    /**
     * Returns values in row major order as a double[].
     *
     * @return
     */
    protected @Override
    double[] toArrayIncludingNoDataValues() {
        Grids_GridDouble grid = getGrid();
        int nrows = grid.getChunkNRows(ChunkID, Grid.ge.HandleOutOfMemoryErrorFalse);
        int ncols = grid.getChunkNCols(ChunkID, Grid.ge.HandleOutOfMemoryErrorFalse);
        double[] result;
        result = new double[nrows * ncols];
        Arrays.fill(result, grid.getNoDataValue(Grid.ge.HandleOutOfMemoryErrorFalse));
        Iterator<Double> ite;
        /**
         * Populate result with all mappings from data.DataMapBitSet.
         */
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        Double value;
        OffsetBitSet offsetBitSet;
        BitSet bitSet;
        int offset;
        int bitSetLength;
        int i;
        while (ite.hasNext()) {
            value = ite.next();
            offsetBitSet = dataMapBitSet.get(value);
            offset = offsetBitSet.Offset;
            bitSet = offsetBitSet._BitSet;
            bitSetLength = bitSet.length();
            for (i = 0; i < bitSetLength; i++) {
                if (bitSet.get(i)) {
                    result[i + offset] = value;
                }
            }
        }
        /**
         * Populate result with all mappings from data.DataMapHashSet.
         */
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        HashSet<Grids_2D_ID_int> cellIDs;
        Iterator<Grids_2D_ID_int> ite2;
        Grids_2D_ID_int cellID;
        int row;
        int col;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            cellIDs = dataMapHashSet.get(value);
            ite2 = cellIDs.iterator();
            while (ite2.hasNext()) {
                cellID = ite2.next();
                row = cellID.getRow();
                col = cellID.getCol();
                result[(row * ChunkNCols) + col] = value;
            }
        }
        return result;
    }

    /**
     * @return
     */
    protected @Override
    double[] toArrayNotIncludingNoDataValues() {
        double[] result;
        Iterator<Double> ite;
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        Double value;
        OffsetBitSet offsetBitSet;
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        HashSet<Grids_2D_ID_int> cellIDs;
        /**
         * Count all mappings and initialise result;
         */
        int n = 0;
        // Count from Data.DataMapBitSet.
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            offsetBitSet = dataMapBitSet.get(value);
            n += offsetBitSet._BitSet.cardinality();
        }
        // Count from Data.DataMapBitSet.
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            cellIDs = dataMapHashSet.get(value);
            n += cellIDs.size();
        }
        result = new double[n];
        /**
         * Populate result with all mappings from data.DataMapBitSet.
         */
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        int i;
        n = 0;
        while (ite.hasNext()) {
            value = ite.next();
            offsetBitSet = dataMapBitSet.get(value);
            for (i = 0; i < offsetBitSet._BitSet.cardinality(); i++) {
                n++;
                result[n] = value;
            }
        }
        /**
         * Populate result with all mappings from data.DataMapHashSet.
         */
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            cellIDs = dataMapHashSet.get(value);
            for (i = 0; i < cellIDs.size(); i++) {
                n++;
                result[n] = value;
            }
        }
        return result;
    }

    /**
     * Returns the value at position given by: chunk cell row chunkRow; chunk
     * cell row chunkCol.
     *
     * @param chunkRow the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCol the column index of the cell w.r.t. the origin of this
     * chunk
     * @param noDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */
    protected @Override
    double getCell(
            int chunkRow,
            int chunkCol,
            double noDataValue) {
        Iterator<Double> ite;
        /**
         * Look in data.DataMapBitSet;
         */
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        double value;
        int position = (chunkRow * ChunkNCols) + chunkCol;
        OffsetBitSet offsetBitSet;
        BitSet bitSet;
        int positionMinusOffset;
        while (ite.hasNext()) {
            value = ite.next();
            offsetBitSet = dataMapBitSet.get(value);
            bitSet = offsetBitSet._BitSet;
            positionMinusOffset = position - offsetBitSet.Offset;
            if (positionMinusOffset > 0 && positionMinusOffset < bitSet.length()) {
                if (bitSet.get(positionMinusOffset)) {
                    return value;
                }
            }
        }
        /**
         * Look in data.DataMapHashSet.
         */
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        HashSet<Grids_2D_ID_int> cellIDs;
//        Iterator<Grids_2D_ID_int> ite2;
        Grids_2D_ID_int inputCellID;
        inputCellID = new Grids_2D_ID_int(chunkRow, chunkCol);
//        Grids_2D_ID_int cellID;
//        int row;
//        int col;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            cellIDs = dataMapHashSet.get(value);
            if (cellIDs.contains(inputCellID)) {
                return value;
            }
//            ite2 = cellIDs.iterator();
//            while (ite2.hasNext()) {
//                cellID = ite2.next();
//                row = cellID.getRow();
//                if (row == chunkRow) {
//                    col = cellID.getCol();
//                   if (col == chunkCol) {
//                       return value;
//                   }
//                }
//            }
        }
        return noDataValue;
    }

    /**
     * Returns the value at position given by: chunk cell row chunkRow; chunk
     * cell row chunkCol.
     *
     * @param chunkRow the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCol the column index of the cell w.r.t. the origin of this
     * chunk
     * @param cellID
     * @param noDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */
    protected @Override
    double getCell(
            int chunkRow,
            int chunkCol,
            Grids_2D_ID_int cellID,
            double noDataValue) {
        Iterator<Double> ite;
        int position = (chunkRow * ChunkNCols) + chunkCol;
        double value;
        if (NoData.get(position)) {
            return noDataValue;
        } else if (InDataMapBitSet.get(position)) {
            /**
             * Look in data.DataMapBitSet;
             */
            TreeMap<Double, OffsetBitSet> dataMapBitSet;
            dataMapBitSet = Data.DataMapBitSet;
            ite = dataMapBitSet.keySet().iterator();
            OffsetBitSet offsetBitSet;
            BitSet bitSet;
            int positionMinusOffset;
            while (ite.hasNext()) {
                value = ite.next();
                offsetBitSet = dataMapBitSet.get(value);
                bitSet = offsetBitSet._BitSet;
                positionMinusOffset = position - offsetBitSet.Offset;
                if (positionMinusOffset > 0 && positionMinusOffset < bitSet.length()) {
                    if (bitSet.get(positionMinusOffset)) {
                        return value;
                    }
                }
            }
        } else if (InDataMapHashSet.get(position)) {
            /**
             * Look in data.DataMapHashSet.
             */
            TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
            dataMapHashSet = Data.DataMapHashSet;
            HashSet<Grids_2D_ID_int> cellIDs;
//           Iterator<Grids_2D_ID_int> ite2;
            cellID = new Grids_2D_ID_int(chunkRow, chunkCol);
//           Grids_2D_ID_int cellID;
//           int row;
//           int col;
            ite = dataMapHashSet.keySet().iterator();
            while (ite.hasNext()) {
                value = ite.next();
                cellIDs = dataMapHashSet.get(value);
                if (cellIDs.contains(cellID)) {
                    return value;
                }
//                ite2 = cellIDs.iterator();
//                while (ite2.hasNext()) {
//                    cellID = ite2.next();
//                    row = cellID.getRow();
//                    if (row == chunkRow) {
//                        col = cellID.getCol();
//                        if (col == chunkCol) {
//                            return value;
//                        }
//                    }
//                }
            }
        }
        return DefaultValue;
    }

    /**
     * Returns the value of cell with CellID given by chunkCellID.
     *
     * @param chunkCellID The chunk CellID of cell thats value is returned.
     * @param noDataValue The Grid.NoDataValue.
     * @return
     */
    protected double getCell(
            Grids_2D_ID_int chunkCellID,
            double noDataValue) {
        return getCell(chunkCellID.getRow(), chunkCellID.getCol(), noDataValue);
    }

    /**
     * Initialises the value at position given by: chunk cell row chunkRow;
     * chunk cell column chunkCol. Utility method for constructor.
     *
     * @param chunkRow the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCol the column index of the cell w.r.t. the origin of this
     * chunk
     * @param valueToInitialise the value with which the cell is initialised
     */
    @Override
    protected final void initCell(
            int chunkRow,
            int chunkCol,
            double noDataValue,
            double valueToInitialise) {
        Grids_2D_ID_int chunkCellID = new Grids_2D_ID_int(
                chunkRow,
                chunkCol);
        initCell(
                chunkRow,
                chunkCol,
                chunkCellID,
                noDataValue,
                valueToInitialise);
    }

    /**
     * Initialises the value of the chunk referred to by chunkCellID to
     * valueToInitialise. Utility method for constructor.
     *
     * @param chunkRow
     * @param chunkCol
     * @param chunkCellID the Grids_AbstractGridChunkDouble.Grids_2D_ID_int of
     * the cell to be initialised
     * @param valueToInitialise the value with which the cell is initialised
     * @param noDataValue
     */
    protected void initCell(
            int chunkRow,
            int chunkCol,
            Grids_2D_ID_int chunkCellID,
            double valueToInitialise,
            double noDataValue) {
        if (valueToInitialise != DefaultValue) {
            int position = (chunkRow * ChunkNCols) + chunkCol;
            if (valueToInitialise == noDataValue) {
                NoData.set(position);
            } else {
                /**
                 * Look in data.DataMapBitSet;
                 */
                TreeMap<Double, OffsetBitSet> dataMapBitSet;
                dataMapBitSet = Data.DataMapBitSet;
                if (dataMapBitSet.containsKey(valueToInitialise)) {
                    OffsetBitSet offsetBitSet;
                    offsetBitSet = dataMapBitSet.get(valueToInitialise);
                    BitSet bitSet = offsetBitSet._BitSet;
                    bitSet.set(position);
                    InDataMapBitSet.set(position);
                } else {
                    TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
                    dataMapHashSet = Data.DataMapHashSet;
                    if (dataMapHashSet.containsKey(valueToInitialise)) {
                        dataMapHashSet.get(valueToInitialise).add(chunkCellID);
                        InDataMapHashSet.set(position);
                    } else {
                        /**
                         * If the chunk is looking sparse so far then add to
                         * dataMapHashSet, otherwise add to dataMapBitSet
                         */
                        if ((position - NoData.cardinality()) / (double) position < 0.5) {
                            HashSet<Grids_2D_ID_int> s = new HashSet<>();
                            s.add(chunkCellID);
                            dataMapHashSet.put(valueToInitialise, s);
                        } else {
                            OffsetBitSet offsetBitSet;
                            offsetBitSet = new OffsetBitSet(position);
                            offsetBitSet._BitSet.set(0);
                            dataMapBitSet.put(valueToInitialise, offsetBitSet);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the value at position given by: chunk cell row chunkRow; chunk
     * cell column chunkCol and sets it to valueToSet
     *
     * @param chunkRow the row index of the cell w.r.t. the origin of this chunk
     * @param chunkCol the column index of the cell w.r.t. the origin of this
     * chunk
     * @param valueToSet the value the cell is to be set to
     * @param noDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */
    protected @Override
    double setCell(
            int chunkRow,
            int chunkCol,
            double valueToSet,
            double noDataValue) {
        Grids_2D_ID_int chunkCellID = new Grids_2D_ID_int(
                chunkRow,
                chunkCol);
        return setCell(
                chunkCellID,
                valueToSet,
                noDataValue);
    }

    /**
     * Returns the value at position given by: chunk cell row chunkRow; chunk
     * cell column chunkCol and sets it to valueToSet
     *
     *
     *
     * @param chunkCellID the Grids_AbstractGridChunkDouble.Grids_2D_ID_int of
     * the cell to be initialised
     * @param valueToSet the value the cell is to be set to
     * @param noDataValue the _NoDataValue of this.grid2DSquareCellDouble
     * @return
     */
    protected double setCell(
            Grids_2D_ID_int chunkCellID,
            double valueToSet,
            double noDataValue) {
        double result = this.DefaultValue;
        TDoubleObjectIterator iterator = this.Data.iterator();
        double value = noDataValue;
        boolean gotValue = false;
        boolean setValue;
        setValue = valueToSet == result;
        int ite;
        int maxIte = this.Data.size();
        HashSet set;
        Grids_2D_ID_int individual;
        boolean remove = false;
        double removeValue = value;
        for (ite = 0; ite < maxIte; ite++) {
            if (!(gotValue && setValue)) {
                try {
                    iterator.advance();
                    value = iterator.key();
                    //if ( ! gotValue ) { // Slows things down!? Probably...
                    /* Is this better than:
                     * if ( iterator.value().getClass() == HashSet.class  ) {
                     * } else {
                     *     // ( iterator.value().getClass() == Grids_2D_ID_int.class )
                     * }
                     * ?
                     */
                    try {
                        //if ( iterator.value() == null ) { // Debugging code
                        //    int null0Debug = 0; // Debugging code
                        //} // Debugging code
                        set = (HashSet) iterator.value();
                        if (set.contains(chunkCellID)) {
                            if (value == valueToSet) {
                                return value;
                            }
                            if (set.size() == 2) {
                                // Convert other entry to a Grids_2D_ID_int
                                Iterator setIterator = set.iterator();
                                for (int setIteratorIndex = 0; setIteratorIndex < 2; setIteratorIndex++) {
                                    individual = (Grids_2D_ID_int) setIterator.next();
                                    if (!individual.equals(chunkCellID)) {
                                        iterator.setValue(individual);
                                    }
                                }
                            } else {
                                set.remove(chunkCellID);
                            }
                            result = value;
                            gotValue = true;
                        }
                    } catch (java.lang.ClassCastException e) {
                        individual = (Grids_2D_ID_int) iterator.value();
                        if (individual.equals(chunkCellID)) {
                            if (value == valueToSet) {
                                return value;
                            }
                            // Need to set a value to be removed and remove it once stopped iterating to avoid ConcurrentModificationException.
                            remove = true;
                            removeValue = value;
                            result = value;
                            gotValue = true;
                        }
                    }
                    //if ( ! setValue ) { // Slows things down!? Probably...
                    if (value == valueToSet) {
                        try {
                            set = (HashSet) iterator.value();
                            set.add(chunkCellID);
                        } catch (java.lang.ClassCastException cce) {
                            individual = (Grids_2D_ID_int) iterator.value();
                            set = new HashSet();
                            set.add(individual);
                            set.add(chunkCellID);
                            iterator.setValue(set);
                        } catch (Exception e1) {
                            // What is happening?
                            e1.toString();
                            int e1Debug = 0;
                        }
                        setValue = true;
                    }
                    //}
                    //} catch ( ConcurrentModificationException cme0 ) {
                    //    int cme0Debug = 0;
                    //} catch ( NoSuchElementException nsee0 ) {
                    //    int nsee0Debug = 0;
                    //} catch ( NullPointerException npe ) {
                    //    int npe0Debug = 0;
                } catch (Exception e0) {
                    e0.printStackTrace(System.err);
                }
            }
        }
        if (remove) {
            this.Data.remove(removeValue);
        }
        if (!setValue) {
            this.Data.put(valueToSet, chunkCellID);
        }
        if (isSwapUpToDate()) { // Optimisation? Want a setCellFast method closer to initCell? What about an unmodifiable readOnly type chunk?
            if (valueToSet != value) {
                setSwapUpToDate(false);
            }
        }
        return result;
    }

    /**
     * Returns the number of cells with non _NoDataValues as a BigInteger.
     *
     * @return
     */
    protected @Override
    BigInteger getNonNoDataValueCountBigInteger() {
        double _NoDataValue = getGrid().getNoDataValue(false);
        TDoubleObjectIterator iterator = this.Data.iterator();
        BigInteger nonNoDataCountBigInteger = BigInteger.ZERO;
        if (this.DefaultValue == _NoDataValue) {
            for (int ite = 0; ite < this.Data.size(); ite++) {
                iterator.advance();
                try {
                    nonNoDataCountBigInteger = nonNoDataCountBigInteger.add(
                            new BigInteger(Integer.toString(((HashSet) iterator.value()).size())));
                } catch (java.lang.ClassCastException e) {
                    nonNoDataCountBigInteger = nonNoDataCountBigInteger.add(BigInteger.ONE);
                }
            }
        } else {
            for (int ite = 0; ite < this.Data.size(); ite++) {
                iterator.advance();
                if (iterator.key() == _NoDataValue) {
                    try {
                        long chunkNrows = (long) this.getGrid().getChunkNRows(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse);
                        long chunkNcols = (long) this.getGrid().getChunkNCols(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse);
                        return new BigInteger(Long.toString(
                                (chunkNrows * chunkNcols)
                                - ((HashSet) iterator.value()).size()));
                    } catch (java.lang.ClassCastException e) {
                        long chunkNrows = (long) this.getGrid().getChunkNRows(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse);
                        long chunkNcols = (long) this.getGrid().getChunkNCols(this.ChunkID,
                                getGrid().ge.HandleOutOfMemoryErrorFalse);
                        return new BigInteger(Long.toString(
                                (chunkNrows * chunkNcols) - 1L));
                    }
                }
            }
        }
        return nonNoDataCountBigInteger;
    }

    /**
     * Returns the sum of all non _NoDataValues as a BigDecimal.
     *
     * @return
     */
    protected @Override
    BigDecimal getSumBigDecimal() {
        BigDecimal sum = new BigDecimal(0.0d);
        BigDecimal thisCount;
        TDoubleObjectIterator iterator = this.Data.iterator();
        HashSet set;
        Grids_2D_ID_int individual;
        for (int ite = 0; ite < Data.size(); ite++) {
            iterator.advance();
            try {
                thisCount = new BigDecimal(Integer.toString(
                        ((HashSet) iterator.value()).size()));
                sum = sum.add(new BigDecimal(iterator.key()).multiply(thisCount));
            } catch (java.lang.ClassCastException e) {
                sum = sum.add(new BigDecimal(iterator.key()));
            }
        }
        return sum;
    }

    /**
     * Returns the minimum of all non _NoDataValues as a double.
     *
     * @return
     */
    protected @Override
    double getMinDouble() {
        double min = Double.POSITIVE_INFINITY;
        TDoubleObjectIterator iterator = this.Data.iterator();
        for (int ite = 0; ite < Data.size(); ite++) {
            iterator.advance();
            min = Math.min(min, iterator.key());
        }
        return min;
    }

    /**
     * Returns the maximum of all non _NoDataValues as a double
     *
     * @return
     */
    protected @Override
    double getMaxDouble() {
        double max = Double.NEGATIVE_INFINITY;
        TDoubleObjectIterator iterator = this.Data.iterator();
        for (int ite = 0; ite < Data.size(); ite++) {
            iterator.advance();
            max = Math.max(max, iterator.key());
        }
        return max;
    }

    /**
     * Returns the Arithmetic Mean of all non _NoDataValues as a double. Using
     * BigDecimal this should be as precise as possible with doubles.
     *
     * @return
     */
    protected @Override
    double getArithmeticMeanDouble() {
        BigDecimal sum = new BigDecimal(0.0d);
        BigDecimal thisCount;
        BigDecimal totalCount = new BigDecimal(0.0d);
        BigDecimal oneBigDecimal = new BigDecimal(1.0d);
        TDoubleObjectIterator iterator = this.Data.iterator();
        for (int ite = 0; ite < Data.size(); ite++) {
            iterator.advance();
            try {
                thisCount = new BigDecimal(Integer.toString(
                        ((HashSet) iterator.value()).size()));
                totalCount = totalCount.add(thisCount);
                sum = sum.add(new BigDecimal(iterator.key()).multiply(thisCount));
            } catch (java.lang.ClassCastException e) {
                sum = sum.add(new BigDecimal(iterator.key()));
                totalCount = totalCount.add(oneBigDecimal);
            }
        }
        if (totalCount.compareTo(new BigDecimal(0.0d)) == 1) {
            return sum.divide(
                    totalCount,
                    325,
                    BigDecimal.ROUND_HALF_EVEN).doubleValue();
        } else {
            return getGrid().getNoDataValue(false);
        }
    }

    /**
     * For returning the mode of all non _NoDataValues as a TDoubleHashSet
     *
     * @return
     */
    protected @Override
    TDoubleHashSet getModeTDoubleHashSet() {
        TDoubleHashSet mode = new TDoubleHashSet();
        TDoubleObjectIterator iterator = this.Data.iterator();
        int modeCount = 0;
        int thisCount;
        for (int ite = 0; ite < Data.size(); ite++) {
            iterator.advance();
            try {
                thisCount = ((HashSet) iterator.value()).size();
            } catch (java.lang.ClassCastException e) {
                thisCount = 1;
            }
            if (thisCount > modeCount) {
                mode.clear();
                mode.add(iterator.key());
                modeCount = thisCount;
            } else {
                if (thisCount == modeCount) {
                    mode.add(iterator.key());
                }
            }
        }
        return mode;
    }

    /**
     * For returning the median of all non _NoDataValues as a double
     *
     * @return
     */
    protected @Override
    double getMedianDouble() {
        int scale = 325;
        double[] keys = this.Data.keys();
        sort1(keys, 0, keys.length);
        long nonNoDataValueCountLong = getNonNoDataValueCountBigInteger().longValue();
        if (nonNoDataValueCountLong > 0) {
            long index = -1L;
            if (nonNoDataValueCountLong % 2L == 0L) {
                // Need arithmetic mean of ( ( nonNoDataValueCount / 2 ) - 1 )th
                // and ( nonNoDataValueCount / 2 )th values
                long requiredIndex = (nonNoDataValueCountLong / 2L) - 1L;
                boolean got = false;
                BigDecimal medianBigDecimal = null;
                for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                    try {
                        index += (long) ((HashSet) this.Data.get(keys[keyIndex])).size();
                    } catch (java.lang.ClassCastException e) {
                        index++;
                    }
                    if (!got && index >= requiredIndex) {
                        medianBigDecimal = new BigDecimal(keys[keyIndex]);
                        got = true;
                    }
                    if (index >= requiredIndex + 1L) {
                        BigDecimal bigDecimalTwo = new BigDecimal(2.0d);
                        BigDecimal bigDecimal0 = new BigDecimal(keys[keyIndex]);
                        BigDecimal bigDecimal1 = medianBigDecimal.add(bigDecimal0);
                        bigDecimal0 = bigDecimal1.divide(
                                bigDecimalTwo,
                                scale,
                                BigDecimal.ROUND_HALF_EVEN);
                        return bigDecimal0.doubleValue();
                    }
                }
            } else {
                // Need ( ( nonNoDataValueCount ) / 2 )th value
                long requiredIndex = nonNoDataValueCountLong / 2L;
                for (int keyIndex = 0; keyIndex < keys.length; keyIndex++) {
                    try {
                        index += ((HashSet) this.Data.get(keys[keyIndex])).size();
                    } catch (java.lang.ClassCastException e) {
                        index++;
                    }
                    if (index >= requiredIndex) {
                        return keys[keyIndex];
                    }
                }
            }
        }
        return getGrid().getNoDataValue(false);
    }

    /**
     * For returning the standard deviation of all non _NoDataValues as a double
     *
     * @return
     */
    protected @Override
    double getStandardDeviationDouble() {
        double standardDeviation = 0.0d;
        double mean = getArithmeticMeanDouble();
        // Calculate the number of default values
        int n = ChunkNRows * ChunkNCols;
        BitSet defaultValues;
        defaultValues = new BitSet(n);
        defaultValues.flip(0, n - 1);
        defaultValues.and(InDataMapHashSet);
        defaultValues.or(InDataMapBitSet);
        defaultValues.xor(NoData);
        int nValues = n - defaultValues.cardinality();
        standardDeviation
                += ((DefaultValue - mean) * (DefaultValue - mean))
                * nValues;
        Iterator<Double> ite;
        double value;
        /**
         * Add from data.DataMapBitSet;
         */
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        OffsetBitSet offsetBitSet;
        while (ite.hasNext()) {
            value = ite.next();
            offsetBitSet = dataMapBitSet.get(value);
            n = offsetBitSet._BitSet.size();
            nValues += n;
            standardDeviation
                    += ((value - mean) * (value - mean))
                    * n;
        }
        /**
         * Add from data.DataMapHashSet.
         */
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        HashSet<Grids_2D_ID_int> cellIDs;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            n = dataMapHashSet.get(value).size();
            nValues += n;
            standardDeviation
                    += ((value - mean) * (value - mean))
                    * n;
        }
        if ((nValues - 1.0d) > 0) {
            return Math.sqrt(standardDeviation / (double) (nValues - 1L));
        } else {
            return standardDeviation;
        }
    }

    /**
     * For returning the number of different values.
     *
     * @return
     */
    protected BigInteger getDiversityBigInteger() {
        BigInteger result;
        result = BigInteger.valueOf(Data.DataMapBitSet.size() + Data.DataMapHashSet.size() + 1);
        return result;
    }

    /**
     * Returns a Grids_GridChunkDoubleMapIterator for iterating over the cells
     * in this.
     *
     * @return
     */
    protected @Override
    Grids_AbstractIterator iterator() {
        return new Grids_GridChunkDoubleMapIterator(this);
    }

    /**
     * Simple inner class for wrapping an int and a BitSet.
     */
    public class OffsetBitSet {

        public int Offset;
        public BitSet _BitSet;

        public OffsetBitSet(int offset) {
            Offset = offset;
            _BitSet = new BitSet();
        }
    }

    /**
     * Simple inner class for wrapping an int and a BitSet.
     */
    public class GridChunkDoubleMapData {

        // For more common values.
        private final TreeMap<Double, OffsetBitSet> DataMapBitSet;

        // For less common and more distributed values.
        private final TreeMap<Double, HashSet<Grids_2D_ID_int>> DataMapHashSet;

        public GridChunkDoubleMapData(
                TreeMap<Double, OffsetBitSet> dataMapBitSet,
                TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet) {
            DataMapBitSet = dataMapBitSet;
            DataMapHashSet = dataMapHashSet;
        }
    }

}
