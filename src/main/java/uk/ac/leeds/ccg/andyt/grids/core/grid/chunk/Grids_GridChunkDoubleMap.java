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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.grid.stats.Grids_InterfaceStats;

/**
 * Stores cell values in: a TreeMap with keys as cell values and values as
 * BitSets giving their locations; a TreeMap with keys as cell values and values
 * as a TreeSet&LT;Grids_2D_ID_int&GT; giving the locations of these values.
 * There is a default value for all values that are not in these maps and that
 * are not no data values. The locations of no data values are given in a
 * BitSet. The complexity of this data store allows for some efficiencies in
 * statistical calculations and storage all depending on the distribution and
 * commonalities in the data values. Until all the data is read in and processed
 * it is not known how is the best way to store it for speed and efficiency. If
 * the chunk values are mutable and do not change it is perhaps worth changing
 * into an efficient data storage in terms of what is stored in each map and
 * what the default value is. It may also be worth considering changing to a
 * different chunk altogether. The class might be improved with the use of more
 * efficient and lightweight collections that might be available from third
 * parties.
 *
 * In the past GNU Trove was used as it provided a stable lightweight
 * collections framework that was appropriate for storing primitive maps in this
 * and associated classes. The Eclipse Collections Framework was considered as a
 * replacement for GNU Trove. GNU Trove worked well, but I decided to remove
 * this dependency at a time of rationalising the Grids library in 2017. The
 * rationalisation involved reducing dependencies generally. This
 * rationalisation also removed a dependency on JAI which was used to provide an
 * alternative storage for chunks.
 */
public class Grids_GridChunkDoubleMap
        extends Grids_AbstractGridChunkDoubleArrayOrMap
        implements Serializable, Grids_InterfaceStats {

    //private static final long serialVersionUID = 1L;
    /**
     * A value initialised with grid that can be used to optimise storage.
     * Storage is optimised with the defaultValue set to the most common value.
     * The location of all DefaultValues can be calculated from the converse of
     * the intersection of NoData, InDataMapHashSet and InDataMapBitSet.
     */
    public double DefaultValue;

    /**
     * This is a copy of getGrid().getNoDataValue(boolean) for convenience.
     */
    private final double NoDataValue;

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
     * For storing the data of this chunk.
     */
    private GridChunkDoubleMapData Data;

    /**
     * Default constructor.
     */
    protected Grids_GridChunkDoubleMap() {
        NoDataValue = -Double.MAX_VALUE;
    }

    /**
     * Creates a new Grids_GridChunkDoubleMap with DefaultValue set to 0.0d.
     *
     * @param g
     * @param chunkID
     */
    protected Grids_GridChunkDoubleMap(
            Grids_GridDouble g,
            Grids_2D_ID_int chunkID) {
        this(
                g,
                chunkID,
                0.0d);
    }

    /**
     * Creates a new Grids_GridChunkDoubleMap with DefaultValue set to
     * defaultValue. Usually it is best if the defaultValue is the most common
     * value.
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
        NoDataValue = g.getNoDataValue();
        initData();
        SwapUpToDate = false;
    }

    /**
     * Creates a new Grids_GridChunkDoubleMap with DefaultValue set to
     * defaultValue. Usually it is best if the defaultValue is the most common
     * value. The chunk created will have the same number of rows and columns as
     * gridChunk.
     *
     * @param gridChunk
     * @param chunkID
     * @param defaultValue
     */
    protected Grids_GridChunkDoubleMap(
            Grids_AbstractGridChunkDouble gridChunk,
            Grids_2D_ID_int chunkID,
            double defaultValue) {
        super(gridChunk.getGrid(), chunkID);
        DefaultValue = defaultValue;
        NoDataValue = getGrid().getNoDataValue();
        initData();
        double value;
        for (int row = 0; row < ChunkNRows; row++) {
            for (int col = 0; col < ChunkNCols; col++) {
                value = gridChunk.getCell(row, col);
                initCell(row, col, value);
            }
        }
        SwapUpToDate = false;
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
        //System.gc();
    }

    /**
     * Returns values in row major order as a double[].
     *
     * @return
     */
    double[][] to2DDoubleArray() {
        Grids_GridDouble grid = getGrid();
        int nrows = grid.getChunkNRows(ChunkID);
        int ncols = grid.getChunkNCols(ChunkID);
        double noDataValue = grid.getNoDataValue();
        double[][] result;
        result = new double[nrows][ncols];
        Arrays.fill(result, DefaultValue);
        /**
         * Mask
         */
        int row;
        int col;
        int i = 0;
        for (row = 0; row < nrows; row++) {
            for (col = 0; col < ncols; col++) {
                if (NoData.get(i)) {
                    result[row][col] = noDataValue;
                }
                i++;
            }
        }
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
        col = 0;
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
    @Override
    public double[] toArrayIncludingNoDataValues() {
        Grids_GridDouble grid = getGrid();
        int nrows = grid.getChunkNRows(ChunkID);
        int ncols = grid.getChunkNCols(ChunkID);
        double[] result;
        result = new double[nrows * ncols];
        Arrays.fill(result, grid.getNoDataValue());
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
    @Override
    public double[] toArrayNotIncludingNoDataValues() {
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
     * Returns the value at position given by: row col.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @return
     */
    @Override
    public double getCell(
            int row,
            int col) {
        int position = (row * ChunkNCols) + col;
        if (NoData.get(position)) {
            return NoDataValue;
        } else if (InDataMapBitSet.get(position)) {
            double result = getCell(position);
            if (result != NoDataValue) {
                return result;
            }
        } else if (InDataMapHashSet.get(position)) {
            Grids_2D_ID_int cellID = new Grids_2D_ID_int(row, col);
            double result = getCell(cellID);
            if (result != NoDataValue) {
                return result;
            }
        }
        return DefaultValue;
    }

    /**
     * Returns the value at position given by: row col.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param cellID
     * @return
     */
    protected double getCell(
            int row,
            int col,
            Grids_2D_ID_int cellID) {
        int position = (row * ChunkNCols) + col;
        if (NoData.get(position)) {
            return NoDataValue;
        } else if (InDataMapBitSet.get(position)) {
            double result = getCell(position);
            if (result != NoDataValue) {
                return result;
            }
        } else if (InDataMapHashSet.get(position)) {
            double result = getCell(cellID);
            if (result != NoDataValue) {
                return result;
            }
        }
        return DefaultValue;
    }

    /**
     * Look in data.DataMapBitSet;
     */
    private double getCell(int position) {
        double value;
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        Iterator<Double> ite;
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
        return NoDataValue;
    }

    /**
     * Look in data.DataMapHashSet.
     */
    private double getCell(Grids_2D_ID_int cellID) {
        double value;
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        HashSet<Grids_2D_ID_int> cellIDs;
        Iterator<Double> ite;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            cellIDs = dataMapHashSet.get(value);
            if (cellIDs.contains(cellID)) {
                return value;
            }
        }
        return NoDataValue;
    }

    /**
     * Initialises the value at position given by: row, col.
     *
     * @param row the row index of the cell w.r.t. the origin of this chunk
     * @param col the column index of the cell w.r.t. the origin of this chunk
     * @param valueToInitialise the value with which the cell is initialised
     */
    @Override
    public final void initCell(
            int row,
            int col,
            double valueToInitialise) {
        Grids_2D_ID_int chunkCellID = new Grids_2D_ID_int(
                row,
                col);
        initCell(
                row,
                col,
                chunkCellID,
                valueToInitialise);
    }

    /**
     * Initialises the value of the chunk referred to by chunkCellID to
     * valueToInitialise. Utility method for constructor.
     *
     * @param row
     * @param col
     * @param chunkCellID the Grids_AbstractGridChunkDouble.Grids_2D_ID_int of
     * the cell to be initialised
     * @param valueToInitialise the value with which the cell is initialised
     */
    protected void initCell(
            int row,
            int col,
            Grids_2D_ID_int chunkCellID,
            double valueToInitialise) {
        if (valueToInitialise != DefaultValue) {
            int position = (row * ChunkNCols) + col;
            if (valueToInitialise == NoDataValue) {
                NoData.set(position);
            } else {
                /**
                 * Look in data.DataMapBitSet or dataMapHashSet
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
                            InDataMapHashSet.set(position);
                        } else {
                            OffsetBitSet offsetBitSet;
                            offsetBitSet = new OffsetBitSet(position);
                            offsetBitSet._BitSet.set(0);
                            dataMapBitSet.put(valueToInitialise, offsetBitSet);
                            InDataMapBitSet.set(position);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the value at position given by: row, col and sets it to
     * valueToSet.
     *
     * @param row the chunk row.
     * @param col the chunk column.
     * @param valueToSet the value the cell is to be set to
     * @return
     */
    @Override
    public double setCell(
            int row,
            int col,
            double valueToSet) {
        double result;
        Grids_2D_ID_int chunkCellID = new Grids_2D_ID_int(row, col);
        result = getCell(row, col, chunkCellID);
        if (result == valueToSet) {
            return result;
        }
        if (valueToSet != DefaultValue) {
            int position = (row * ChunkNCols) + col;
            if (valueToSet == NoDataValue) {
                NoData.set(position);
                if (result == DefaultValue) {
                    return DefaultValue;
                } else if (InDataMapBitSet.get(position)) {
                    TreeMap<Double, OffsetBitSet> dataMapBitSet;
                    dataMapBitSet = Data.DataMapBitSet;
                    OffsetBitSet offsetBitSet;
                    offsetBitSet = dataMapBitSet.get(result);
                    BitSet bitSet = offsetBitSet._BitSet;
                    bitSet.flip(position);
                    if (bitSet.cardinality() == 0) {
                        dataMapBitSet.remove(result);
                    }
                    return result;
                } else {
                    TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
                    dataMapHashSet = Data.DataMapHashSet;
                    HashSet<Grids_2D_ID_int> s = dataMapHashSet.get(result);
                    s.remove(chunkCellID);
                    if (s.isEmpty()) {
                        dataMapHashSet.remove(result);
                    }
                    return result;
                }
            } else {
                if (result == DefaultValue) {
                    TreeMap<Double, OffsetBitSet> dataMapBitSet;
                    dataMapBitSet = Data.DataMapBitSet;
                    if (dataMapBitSet.containsKey(valueToSet)) {
                        OffsetBitSet offsetBitSet;
                        offsetBitSet = dataMapBitSet.get(valueToSet);
                        BitSet bitSet = offsetBitSet._BitSet;
                        bitSet.set(position);
                        InDataMapHashSet.set(position);
                        return result;
                    } else {
                        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
                        dataMapHashSet = Data.DataMapHashSet;
                        if (dataMapHashSet.containsKey(valueToSet)) {
                            HashSet<Grids_2D_ID_int> s = dataMapHashSet.get(valueToSet);
                            s.add(chunkCellID);
                            InDataMapHashSet.set(position);
                            return result;
                        } else {
                            OffsetBitSet offsetBitSet;
                            offsetBitSet = new OffsetBitSet(position);
                            offsetBitSet._BitSet.set(0);
                            dataMapBitSet.put(valueToSet, offsetBitSet);
                            InDataMapBitSet.set(position);
                            return result;
                        }
                    }
                } else {
                    // result is a value
                    TreeMap<Double, OffsetBitSet> dataMapBitSet;
                    dataMapBitSet = Data.DataMapBitSet;
                    if (dataMapBitSet.containsKey(valueToSet)) {
                        OffsetBitSet offsetBitSet;
                        BitSet bitSet;
                        // Remove result.
                        if (dataMapBitSet.containsKey(result)) {
                            offsetBitSet = dataMapBitSet.get(valueToSet);
                            bitSet = offsetBitSet._BitSet;
                            bitSet.flip(position);
                            if (bitSet.cardinality() == 0) {
                                dataMapBitSet.remove(result);
                            }
                        }
                        // Add valueToSet.
                        offsetBitSet = dataMapBitSet.get(valueToSet);
                        bitSet = offsetBitSet._BitSet;
                        bitSet.set(position);
                        return result;
                    } else {
                        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
                        dataMapHashSet = Data.DataMapHashSet;
                        if (dataMapHashSet.containsKey(valueToSet)) {
                            HashSet<Grids_2D_ID_int> s;
                            // Remove result.
                            s = dataMapHashSet.get(result);
                            s.remove(chunkCellID);
                            if (s.isEmpty()) {
                                dataMapHashSet.remove(result);
                            }
                            // Add valueToSet
                            s = dataMapHashSet.get(valueToSet);
                            s.add(chunkCellID);
                            return result;
                        } else {
//                            /**
//                             * If the chunk is sparse then add to
//                             * dataMapHashSet, otherwise add to dataMapBitSet
//                             */
//                            double sparseness;
//                            sparseness = (InDataMapHashSet.cardinality()
//                                    + InDataMapBitSet.cardinality())
//                                    / (double) (ChunkNRows * ChunkNCols);
//                            if (sparseness < 0.5) {
//                                HashSet<Grids_2D_ID_int> s = new HashSet<>();
//                                s.add(chunkCellID);
//                                dataMapHashSet.put(valueToSet, s);
//                                InDataMapHashSet.set(position);
//                            } else {
//                                OffsetBitSet offsetBitSet;
//                                offsetBitSet = new OffsetBitSet(position);
//                                offsetBitSet._BitSet.set(0);
//                                dataMapBitSet.put(valueToSet, offsetBitSet);
//                                InDataMapBitSet.set(position);
//                            }
                            // Regardless of sparseness add to dataMapBitSet
                            OffsetBitSet offsetBitSet;
                            offsetBitSet = new OffsetBitSet(position);
                            offsetBitSet._BitSet.set(0);
                            dataMapBitSet.put(valueToSet, offsetBitSet);
                            InDataMapBitSet.set(position);
                            return result;
                        }
                    }
                }
            }
        }
        if (isSwapUpToDate()) {
            setSwapUpToDate(false);
        }
        return result;
    }

    /**
     * Returns the number of cells with data values.
     *
     * @return
     */
    @Override
    public Long getN() {
        return ((long) ChunkNRows * (long) ChunkNCols) - NoData.cardinality();
    }

    /**
     *
     * @param n The number of cells in the chunk.
     * @return
     */
    public int getNumberOfDefaultValues(int n) {
        BitSet defaultValues;
        defaultValues = new BitSet(n);
        defaultValues.flip(0, n - 1);
        defaultValues.and(InDataMapHashSet);
        defaultValues.or(InDataMapBitSet);
        defaultValues.xor(NoData);
        return n - defaultValues.cardinality();
    }

    /**
     * Returns the sum of all data values as a BigDecimal.
     *
     * @return
     */
    @Override
    public BigDecimal getSum() {
        int n = ChunkNRows * ChunkNCols;
        int numberOfDefaultValues = getNumberOfDefaultValues(n);
        return getSumBigDecimal(n, numberOfDefaultValues);
    }

    protected BigDecimal getSumBigDecimal(
            int n,
            int numberOfDefaultValues) {
        BigDecimal result = BigDecimal.ZERO;
        result = result.add(BigDecimal.valueOf(DefaultValue)
                .multiply(BigDecimal.valueOf(numberOfDefaultValues)));
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
            result = result.add(BigDecimal.valueOf(value)
                    .multiply(BigDecimal.valueOf(n)));
        }
        /**
         * Add from data.DataMapHashSet.
         */
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            n = dataMapHashSet.get(value).size();
            result = result.add(BigDecimal.valueOf(value)
                    .multiply(BigDecimal.valueOf(n)));
        }
        return result;
    }

    /**
     * Returns the minimum of all data values as a double.
     *
     * @return
     */
    @Override
    public Double getMin() {
        double min;
        int n = ChunkNRows * ChunkNCols;
        if (getNumberOfDefaultValues(n) > 0) {
            min = DefaultValue;
        } else {
            min = Double.POSITIVE_INFINITY;
        }
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        min = Math.min(min, dataMapBitSet.firstKey());
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        min = Math.min(min, dataMapHashSet.firstKey());
        return min;
    }

    /**
     * Returns the maximum of all data values as a double.
     *
     * @return
     */
    @Override
    public Double getMax() {
        double max;
        int n = ChunkNRows * ChunkNCols;
        if (getNumberOfDefaultValues(n) > 0) {
            max = DefaultValue;
        } else {
            max = Double.NEGATIVE_INFINITY;
        }
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        max = Math.max(max, dataMapBitSet.lastKey());
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        max = Math.max(max, dataMapHashSet.lastKey());
        return max;
    }

    /**
     * For returning the mode of all non _NoDataValues as a TDoubleHashSet
     *
     * @return
     */
    @Override
    protected HashSet<Double> getMode() {
        HashSet<Double> mode = new HashSet<>();
        int n = ChunkNCols * ChunkNRows;
        int numberOfDefaultValues = getNumberOfDefaultValues(n);
        int numberOfMostCommonValue = numberOfDefaultValues;
        mode.add(DefaultValue);
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        Iterator<Double> ite;
        double value;
        int numberOfValues;
        ite = dataMapBitSet.keySet().iterator();
        OffsetBitSet offsetBitSet;
        while (ite.hasNext()) {
            value = ite.next();
            offsetBitSet = dataMapBitSet.get(value);
            numberOfValues = offsetBitSet._BitSet.cardinality();
            if (numberOfValues < numberOfMostCommonValue) {
                mode = new HashSet<>();
                mode.add(value);
            } else if (numberOfValues == numberOfMostCommonValue) {
                mode.add(value);
            }
        }
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        HashSet<Grids_2D_ID_int> chunkIDs;
        while (ite.hasNext()) {
            value = ite.next();
            chunkIDs = dataMapHashSet.get(value);
            numberOfValues = chunkIDs.size();
            if (numberOfValues < numberOfMostCommonValue) {
                mode = new HashSet<>();
                mode.add(value);
            } else if (numberOfValues == numberOfMostCommonValue) {
                mode.add(value);
            }
        }
        return mode;
    }

    /**
     * For returning the median of all data values as a double.
     *
     * @return
     */
    @Override
    public double getMedianDouble() {
        TreeMap<Double, Integer> valueCount = new TreeMap<>();
        int nCells = ChunkNCols * ChunkNRows;
        int numberOfDefaultValues = getNumberOfDefaultValues(nCells);
        valueCount.put(DefaultValue, numberOfDefaultValues);
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        double value;
        Iterator<Double> ite;
        ite = dataMapBitSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            valueCount.put(value, dataMapBitSet.get(value)._BitSet.cardinality());
        }
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            value = ite.next();
            valueCount.put(value, dataMapHashSet.get(value).size());
        }
        long n = getN();
        if (n > 0) {
            long index = -1L;
            if (n % 2L == 0L) {
                // Need arithmetic mean of ( ( nonNoDataValueCount / 2 ) - 1 )th
                // and ( nonNoDataValueCount / 2 )th values
                long requiredIndex = (n / 2L) - 1L;
                int i = 0;
                ite = valueCount.keySet().iterator();
                while (ite.hasNext()) {
                    value = ite.next();
                    i += valueCount.get(value);
                    if (i > requiredIndex && i > requiredIndex + 1) {
                        return value;
                    } else {
                        double value2;
                        value2 = ite.next();
                        return (value + value2) / 2.0d;
                    }
                }
            } else {
                // Need ( ( nonNoDataValueCount ) / 2 )th value
                long requiredIndex = n / 2L;
                int i = 0;
                ite = valueCount.keySet().iterator();
                while (ite.hasNext()) {
                    value = ite.next();
                    i += valueCount.get(value);
                    if (i > requiredIndex) {
                        return value;
                    }
                }
            }
        }
        return getGrid().getNoDataValue();
    }

    /**
     * For returning the standard deviation of all non _NoDataValues as a double
     *
     * @return
     */
    @Override
    protected double getStandardDeviationDouble() {
        double result = 0.0d;
        double mean = getArithmeticMeanDouble();
        // Calculate the number of default values
        int n = ChunkNRows * ChunkNCols;
        int nValues = getNumberOfDefaultValues(n);
        result += ((DefaultValue - mean) * (DefaultValue - mean)) * nValues;
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
            result += ((value - mean) * (value - mean)) * n;
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
            result += ((value - mean) * (value - mean)) * n;
        }
        if ((nValues - 1.0d) > 0) {
            return Math.sqrt(result / (double) (nValues - 1L));
        } else {
            return result;
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
    @Override
    public Grids_GridChunkDoubleArrayOrMapIterator iterator() {
        return new Grids_GridChunkDoubleArrayOrMapIterator(this);
    }

    @Override
    public Double getMin(boolean update) {
        double min = Integer.MIN_VALUE;
        if (DefaultValue != NoDataValue) {
            min = Math.min(min, DefaultValue);
        }
        min = Math.min(min, Data.DataMapBitSet.firstKey());
        min = Math.min(min, Data.DataMapHashSet.firstKey());
        return min;
    }

    @Override
    public Double getMax(boolean update) {
        double max = Integer.MIN_VALUE;
        if (DefaultValue != NoDataValue) {
            max = Math.max(max, DefaultValue);
        }
        max = Math.max(max, Data.DataMapBitSet.lastKey());
        max = Math.max(max, Data.DataMapHashSet.lastKey());
        return max;
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

        /**
         * For more common values.
         */
        public final TreeMap<Double, OffsetBitSet> DataMapBitSet;

        /**
         * For less common and more distributed values.
         */
        public final TreeMap<Double, HashSet<Grids_2D_ID_int>> DataMapHashSet;

        public GridChunkDoubleMapData(
                TreeMap<Double, OffsetBitSet> dataMapBitSet,
                TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet) {
            DataMapBitSet = dataMapBitSet;
            DataMapHashSet = dataMapHashSet;
        }
    }

}
