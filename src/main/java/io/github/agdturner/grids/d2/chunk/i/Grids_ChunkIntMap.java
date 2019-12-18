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
package io.github.agdturner.grids.d2.chunk.i;

import io.github.agdturner.grids.d2.grid.i.Grids_GridInt;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import io.github.agdturner.grids.core.Grids_2D_ID_int;

/**
 * Stores cell values in: a TreeMap with keys as cell values and values as
 * BitSets giving their locations; a TreeMap with keys as cell values and values
 * as a HashSet&LT;Grids_2D_ID_int&GT; giving the locations of these values.
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
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkIntMap extends Grids_ChunkIntArrayOrMap {

    private static final long serialVersionUID = 1L;

    /**
     * A value initialised with grid that can be used to optimise storage.
     * Storage is optimised with the defaultValue set to the most common value.
     * The location of all DefaultValues can be calculated from the converse of
     * the intersection of NoData, InDataMapHashSet and InDataMapBitSet.
     */
    public int DefaultValue;

    /**
     * This is a copy of getGrid().getNoDataValue(boolean) for convenience.
     */
    private final int NoDataValue;

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
    private GridChunkIntMapData Data;

    /**
     * {@link #DefaultValue} is set to {@code 0}.
     *
     * @param g What {@link #Grid} is set to.
     * @param i What {@link #id} is set to.
     */
    protected Grids_ChunkIntMap(Grids_GridInt g, Grids_2D_ID_int i) {
        this(g, i, 0);
    }

    /**
     * Usually it is best if the defaultValue is the most common value.
     *
     * @param g What {@link #Grid} is set to.
     * @param i What {@link #id} is set to.
     * @param dv What {@link #DefaultValue} is set to.
     */
    protected Grids_ChunkIntMap(Grids_GridInt g, Grids_2D_ID_int i, int dv) {
        super(g, i);
        DefaultValue = dv;
        NoDataValue = g.getNoDataValue();
        initData();
        CacheUpToDate = false;
    }

    /**
     * Usually it is best if the defaultValue is the most common value. The
     * chunk created will have the same cell values as {@code c}.
     *
     * @param c The chunk from which the values in this are set.
     * @param i The chunkID.
     * @param dv The default value.
     */
    protected Grids_ChunkIntMap(Grids_ChunkInt c, Grids_2D_ID_int i, int dv) {
        super(c.getGrid(), i);
        DefaultValue = dv;
        NoDataValue = getGrid().getNoDataValue();
        initData();
        for (int row = 0; row < ChunkNRows; row++) {
            for (int col = 0; col < ChunkNCols; col++) {
                int value = c.getCell(row, col);
                initCell(row, col, value);
            }
        }
        this.CacheUpToDate = false;
    }

    /**
     * Initialises {@link #Data}.
     */
    @Override
    protected final void initData() {
        Data = new GridChunkIntMapData(new TreeMap<>(), new TreeMap<>());
        NoData = new BitSet(ChunkNCols * ChunkNRows);
        InDataMapHashSet = new BitSet(ChunkNCols * ChunkNRows);
        InDataMapBitSet = new BitSet(ChunkNCols * ChunkNRows);
    }

    /**
     * @return {@link #Data}.
     */
    protected GridChunkIntMapData getData() {
        return Data;
    }

    /**
     * Sets {@link #Data} to {@code null}.
     */
    @Override
    protected void clearData() {
        Data = null;
        //System.gc();
    }

    /**
     * @return Values as a double[][] indexed by row and column.
     */
    int[][] to2DIntArray() {
        Grids_GridInt grid = getGrid();
        int nrows = grid.getChunkNRows(ChunkID);
        int ncols = grid.getChunkNCols(ChunkID);
        int noDataValue = grid.getNoDataValue();
        int[][] r = new int[nrows][ncols];
        Arrays.fill(r, DefaultValue);
        /**
         * Mask
         */
        int i = 0;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                if (NoData.get(i)) {
                    r[row][col] = noDataValue;
                }
                i++;
            }
        }
        Iterator<Integer> ite;
        /**
         * Populate result with all mappings from data.DataMapBitSet.
         */
        TreeMap<Integer, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        int row = 0;
        int col = 0;
        while (ite.hasNext()) {
            Integer value = ite.next();
            OffsetBitSet offsetBitSet = dataMapBitSet.get(value);
            BitSet bitSet = offsetBitSet.bitSet;
            int offset = offsetBitSet.offset;
            int bitSetLength = bitSet.length();
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
                    r[row][col] = value;
                }
            }
        }
        /**
         * Populate result with all mappings from data.DataMapHashSet.
         */
        TreeMap<Integer, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            Integer value = ite.next();
            HashSet<Grids_2D_ID_int> cellIDs = dataMapHashSet.get(value);
            Iterator<Grids_2D_ID_int> ite2 = cellIDs.iterator();
            while (ite2.hasNext()) {
                Grids_2D_ID_int cellID = ite2.next();
                row = cellID.getRow();
                col = cellID.getCol();
                r[row][col] = value;
            }
        }
        return r;
    }

    /**
     * @return Values in row major order as an int[].
     */
    @Override
    protected int[] toArrayIncludingNoDataValues() {
        Grids_GridInt grid = getGrid();
        int nrows = grid.getChunkNRows(ChunkID);
        int ncols = grid.getChunkNCols(ChunkID);
        int[] r = new int[nrows * ncols];
        Arrays.fill(r, grid.getNoDataValue());
        Iterator<Integer> ite;
        /**
         * Populate result with all mappings from data.DataMapBitSet.
         */
        TreeMap<Integer, OffsetBitSet> dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        while (ite.hasNext()) {
            Integer v = ite.next();
            OffsetBitSet offsetBitSet = dataMapBitSet.get(v);
            int offset = offsetBitSet.offset;
            BitSet bitSet = offsetBitSet.bitSet;
            int bitSetLength = bitSet.length();
            for (int i = 0; i < bitSetLength; i++) {
                if (bitSet.get(i)) {
                    r[i + offset] = v;
                }
            }
        }
        /**
         * Populate result with all mappings from data.DataMapHashSet.
         */
        TreeMap<Integer, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            Integer v = ite.next();
            HashSet<Grids_2D_ID_int> cellIDs = dataMapHashSet.get(v);
            Iterator<Grids_2D_ID_int> ite2 = cellIDs.iterator();
            while (ite2.hasNext()) {
                Grids_2D_ID_int cellID = ite2.next();
                r[(cellID.getRow() * ChunkNCols) + cellID.getCol()] = v;
            }
        }
        return r;
    }

    /**
     * @return Values excluding noDataValues in row major order as an int[].
     */
    @Override
    protected int[] toArrayNotIncludingNoDataValues() {
        int[] r;
        Iterator<Integer> ite;
        TreeMap<Integer, OffsetBitSet> dataMapBitSet;
        TreeMap<Integer, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        /**
         * Count all mappings and initialise result;
         */
        int n = 0;
        // Count from Data.DataMapBitSet.
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        while (ite.hasNext()) {
            n += dataMapBitSet.get(ite.next()).bitSet.cardinality();
        }
        // Count from Data.DataMapBitSet.
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            n += dataMapHashSet.get(ite.next()).size();
        }
        r = new int[n];
        /**
         * Populate result with all mappings from data.DataMapBitSet.
         */
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        int i;
        n = 0;
        while (ite.hasNext()) {
            Integer value = ite.next();
            OffsetBitSet offsetBitSet = dataMapBitSet.get(value);
            for (i = 0; i < offsetBitSet.bitSet.cardinality(); i++) {
                n++;
                r[n] = value;
            }
        }
        /**
         * Populate result with all mappings from data.DataMapHashSet.
         */
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            Integer value = ite.next();
            HashSet<Grids_2D_ID_int> cellIDs = dataMapHashSet.get(value);
            for (i = 0; i < cellIDs.size(); i++) {
                n++;
                r[n] = value;
            }
        }
        return r;
    }

    /**
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @return The value at position given by: chunk cell row {@code row}; chunk
     * cell column {@code col}.
     */
    @Override
    public int getCell(int row, int col) {
        int position = (row * ChunkNCols) + col;
        if (NoData.get(position)) {
            return NoDataValue;
        } else if (InDataMapBitSet.get(position)) {
            int r = getCell(position);
            if (r != NoDataValue) {
                return r;
            }
        } else if (InDataMapHashSet.get(position)) {
            int r = getCell(new Grids_2D_ID_int(row, col));
            if (r != NoDataValue) {
                return r;
            }
        }
        return DefaultValue;
    }

    /**
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @param i The cell ID.
     * @return The value at position given by: row, col.
     */
    protected int getCell(int row, int col, Grids_2D_ID_int i) {
        int pos = (row * ChunkNCols) + col;
        if (NoData.get(pos)) {
            return NoDataValue;
        } else if (InDataMapBitSet.get(pos)) {
            int r = getCell(pos);
            if (r != NoDataValue) {
                return r;
            }
        } else if (InDataMapHashSet.get(pos)) {
            int r = getCell(i);
            if (r != NoDataValue) {
                return r;
            }
        }
        return DefaultValue;
    }

    /**
     * Look in data.DataMapBitSet.
     */
    private int getCell(int position) {
        TreeMap<Integer, OffsetBitSet> m = Data.DataMapBitSet;
        Iterator<Integer> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            int v = ite.next();
            OffsetBitSet offsetBitSet = m.get(v);
            BitSet bitSet = offsetBitSet.bitSet;
            int pos = position - offsetBitSet.offset;
            if (pos > 0 && pos < bitSet.length()) {
                if (bitSet.get(pos)) {
                    return v;
                }
            }
        }
        return NoDataValue;
    }

    /**
     * Look in data.DataMapHashSet.
     */
    private int getCell(Grids_2D_ID_int cellID) {
        TreeMap<Integer, HashSet<Grids_2D_ID_int>> m = Data.DataMapHashSet;
        Iterator<Integer> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            int v = ite.next();
            if (m.get(v).contains(cellID)) {
                return v;
            }
        }
        return NoDataValue;
    }

    /**
     * Initialises the value at position given by: chunk cell row {@code row};
     * chunk cell column {@code col}. Utility method for constructor.
     *
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @param v The value with which the cell is initialised.
     */
    @Override
    public final void initCell(int row, int col, int v) {
        initCell(row, col, new Grids_2D_ID_int(row, col), v);
    }

    /**
     * Initialises the value of the chunk referred to by {@code i} to {@code v}.
     * Utility method for constructor.
     *
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @param i The cell ID of the cell to be initialised.
     * @param v The value with which the cell is initialised.
     */
    protected void initCell(int row, int col, Grids_2D_ID_int i, int v) {
        if (v != DefaultValue) {
            int noDataValue = getGrid().getNoDataValue();
            int pos = (row * ChunkNCols) + col;
            if (v == noDataValue) {
                NoData.set(pos);
            } else {
                /**
                 * Look in data.DataMapBitSet;
                 */
                TreeMap<Integer, OffsetBitSet> m = Data.DataMapBitSet;
                if (m.containsKey(v)) {
                    OffsetBitSet offsetBitSet = m.get(v);
                    BitSet bitSet = offsetBitSet.bitSet;
                    bitSet.set(pos);
                    InDataMapBitSet.set(pos);
                } else {
                    TreeMap<Integer, HashSet<Grids_2D_ID_int>> m2
                            = Data.DataMapHashSet;
                    if (m2.containsKey(v)) {
                        m2.get(v).add(i);
                        InDataMapHashSet.set(pos);
                    } else {
                        /**
                         * If the chunk is looking sparse so far then add to
                         * dataMapHashSet, otherwise add to dataMapBitSet
                         */
                        if ((pos - NoData.cardinality()) / (double) pos < 0.5) {
                            HashSet<Grids_2D_ID_int> s = new HashSet<>();
                            s.add(i);
                            m2.put(v, s);
                            InDataMapHashSet.set(pos);
                        } else {
                            OffsetBitSet offsetBitSet = new OffsetBitSet(pos);
                            offsetBitSet.bitSet.set(0);
                            m.put(v, offsetBitSet);
                            InDataMapBitSet.set(pos);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the value at position given by: row, col and sets it to
     * {@code v}.
     *
     * @param row The chunk row.
     * @param col The chunk column.
     * @param v The value the cell is to be set to.
     * @return The value at position given by: row, col before it is set to
     * {@code v}.
     */
    @Override
    public int setCell(int row, int col, int v) {
        return setCell(row, col, new Grids_2D_ID_int(row, col), v);
    }

    /**
     * Returns the value at position given by: chunk cell row row; chunk cell
     * column col and sets it to value.
     *
     * @param row The chunk row.
     * @param col The chunk column.
     * @param i The chunk cell ID of the cell to be initialised.
     * @param v The value the cell is to be set to.
     * @return The value at position given by: row, col before it is set to
     * {@code v}.
     */
    protected int setCell(int row, int col, Grids_2D_ID_int i, int v) {
        int r = getCell(row, col, i);
        if (r == v) {
            return r;
        }
        if (v != DefaultValue) {
            int pos = (row * ChunkNCols) + col;
            if (v == NoDataValue) {
                NoData.set(pos);
                if (r == DefaultValue) {
                    return DefaultValue;
                } else if (InDataMapBitSet.get(pos)) {
                    TreeMap<Integer, OffsetBitSet> m = Data.DataMapBitSet;
                    OffsetBitSet offsetBitSet = m.get(r);
                    BitSet bitSet = offsetBitSet.bitSet;
                    bitSet.flip(pos);
                    if (bitSet.cardinality() == 0) {
                        m.remove(r);
                    }
                    return r;
                } else {
                    TreeMap<Integer, HashSet<Grids_2D_ID_int>> m2
                            = Data.DataMapHashSet;
                    HashSet<Grids_2D_ID_int> s = m2.get(r);
                    s.remove(i);
                    if (s.isEmpty()) {
                        m2.remove(r);
                    }
                    return r;
                }
            } else {
                if (r == DefaultValue) {
                    TreeMap<Integer, OffsetBitSet> m2 = Data.DataMapBitSet;
                    if (m2.containsKey(v)) {
                        OffsetBitSet offsetBitSet = m2.get(v);
                        BitSet bitSet = offsetBitSet.bitSet;
                        bitSet.set(pos);
                        InDataMapHashSet.set(pos);
                        return r;
                    } else {
                        TreeMap<Integer, HashSet<Grids_2D_ID_int>> m3
                                = Data.DataMapHashSet;
                        if (m3.containsKey(v)) {
                            HashSet<Grids_2D_ID_int> s = m3.get(v);
                            s.add(i);
                            InDataMapHashSet.set(pos);
                            return r;
                        } else {
                            OffsetBitSet offsetBitSet = new OffsetBitSet(pos);
                            offsetBitSet.bitSet.set(0);
                            m2.put(v, offsetBitSet);
                            InDataMapBitSet.set(pos);
                            return r;
                        }
                    }
                } else {
                    // result is a value
                    TreeMap<Integer, OffsetBitSet> m2 = Data.DataMapBitSet;
                    if (m2.containsKey(v)) {
                        OffsetBitSet offsetBitSet;
                        BitSet bitSet;
                        // Remove result.
                        if (m2.containsKey(r)) {
                            offsetBitSet = m2.get(v);
                            bitSet = offsetBitSet.bitSet;
                            bitSet.flip(pos);
                            if (bitSet.cardinality() == 0) {
                                m2.remove(r);
                            }
                        }
                        // Add valueToSet.
                        offsetBitSet = m2.get(v);
                        bitSet = offsetBitSet.bitSet;
                        bitSet.set(pos);
                        return r;
                    } else {
                        TreeMap<Integer, HashSet<Grids_2D_ID_int>> m3
                                = Data.DataMapHashSet;
                        if (m3.containsKey(v)) {
                            HashSet<Grids_2D_ID_int> s;
                            // Remove result.
                            s = m3.get(r);
                            s.remove(i);
                            if (s.isEmpty()) {
                                m3.remove(r);
                            }
                            // Add valueToSet
                            s = m3.get(v);
                            s.add(i);
                            return r;
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
//                                offsetBitSet.bitSet.set(0);
//                                dataMapBitSet.put(valueToSet, offsetBitSet);
//                                InDataMapBitSet.set(position);
//                            }
                            // Regardless of sparseness add to dataMapBitSet
                            OffsetBitSet offsetBitSet;
                            offsetBitSet = new OffsetBitSet(pos);
                            offsetBitSet.bitSet.set(0);
                            m2.put(v, offsetBitSet);
                            InDataMapBitSet.set(pos);
                            return r;
                        }
                    }
                }
            }
        }
        if (isCacheUpToDate()) {
            setCacheUpToDate(false);
        }
        return r;
    }

    /**
     * @return The number of cells with values that are not noDataValues.
     */
    @Override
    public Long getN() {
        return ((long) ChunkNRows * (long) ChunkNCols) - NoData.cardinality();
    }

    /**
     * @param n The number of cells in the chunk.
     * @return The number of cell values equal to {@link #DefaultValue}.
     */
    public int getNumberOfDefaultValues(int n) {
        BitSet s = new BitSet(n);
        s.flip(0, n - 1);
        s.and(InDataMapHashSet);
        s.or(InDataMapBitSet);
        s.xor(NoData);
        return n - s.cardinality();
    }

    /**
     * @return The sum of all data values as a BigDecimal.
     */
    @Override
    public BigDecimal getSum() {
        int n = ChunkNRows * ChunkNCols;
        return getSumBigDecimal(n, getNumberOfDefaultValues(n));
    }

    protected BigDecimal getSumBigDecimal(int n, int numberOfDefaultValues) {
        BigDecimal r = BigDecimal.ZERO;
        r = r.add(BigDecimal.valueOf(DefaultValue)
                .multiply(BigDecimal.valueOf(numberOfDefaultValues)));
        Iterator<Integer> ite;
        /**
         * Add from data.DataMapBitSet;
         */
        TreeMap<Integer, OffsetBitSet> m = Data.DataMapBitSet;
        ite = m.keySet().iterator();
        while (ite.hasNext()) {
            int v = ite.next();
            OffsetBitSet offsetBitSet = m.get(v);
            n = offsetBitSet.bitSet.size();
            r = r.add(BigDecimal.valueOf(v).multiply(BigDecimal.valueOf(n)));
        }
        /**
         * Add from data.DataMapHashSet.
         */
        TreeMap<Integer, HashSet<Grids_2D_ID_int>> m2 = Data.DataMapHashSet;
        ite = m2.keySet().iterator();
        while (ite.hasNext()) {
            int v = ite.next();
            n = m2.get(v).size();
            r = r.add(BigDecimal.valueOf(v).multiply(BigDecimal.valueOf(n)));
        }
        return r;
    }

    /**
     * Returns the minimum of all data values.
     *
     * @return
     */
    @Override
    public Integer getMin() {
        int min;
        int n = ChunkNRows * ChunkNCols;
        if (getNumberOfDefaultValues(n) > 0) {
            min = DefaultValue;
        } else {
            min = Integer.MIN_VALUE;
        }
        min = Math.min(min, Data.DataMapBitSet.firstKey());
        min = Math.min(min, Data.DataMapHashSet.firstKey());
        return min;
    }

    /**
     * Returns the maximum of all data values.
     *
     * @return
     */
    @Override
    public Integer getMax() {
        int max;
        int n = ChunkNRows * ChunkNCols;
        if (getNumberOfDefaultValues(n) > 0) {
            max = DefaultValue;
        } else {
            max = Integer.MIN_VALUE;
        }
        max = Math.max(max, Data.DataMapBitSet.lastKey());
        max = Math.max(max, Data.DataMapHashSet.lastKey());
        return max;
    }

    /**
     * For returning the mode of all data values.
     *
     * @return
     */
    @Override
    protected HashSet<Integer> getMode() {
        HashSet<Integer> mode = new HashSet<>();
        int n = ChunkNCols * ChunkNRows;
        int numberOfDefaultValues = getNumberOfDefaultValues(n);
        int numberOfMostCommonValue = numberOfDefaultValues;
        mode.add(DefaultValue);
        Iterator<Integer> ite;
        ite = Data.DataMapBitSet.keySet().iterator();
        OffsetBitSet offsetBitSet;
        while (ite.hasNext()) {
            int v = ite.next();
            offsetBitSet = Data.DataMapBitSet.get(v);
            int numberOfValues = offsetBitSet.bitSet.cardinality();
            if (numberOfValues < numberOfMostCommonValue) {
                mode = new HashSet<>();
                mode.add(v);
            } else if (numberOfValues == numberOfMostCommonValue) {
                mode.add(v);
            }
        }
        ite = Data.DataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            int v = ite.next();
            int numberOfValues = Data.DataMapHashSet.get(v).size();
            if (numberOfValues < numberOfMostCommonValue) {
                mode = new HashSet<>();
                mode.add(v);
            } else if (numberOfValues == numberOfMostCommonValue) {
                mode.add(v);
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
        TreeMap<Integer, Integer> valueCount = new TreeMap<>();
        int n = ChunkNCols * ChunkNRows;
        int numberOfDefaultValues = getNumberOfDefaultValues(n);
        valueCount.put(DefaultValue, numberOfDefaultValues);
        Iterator<Integer> ite;
        ite = Data.DataMapBitSet.keySet().iterator();
        while (ite.hasNext()) {
           int v = ite.next();
            valueCount.put(v, Data.DataMapBitSet.get(v).bitSet.cardinality());
        }
        ite = Data.DataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
           int v = ite.next();
            valueCount.put(v, Data.DataMapHashSet.get(v).size());
        }
        long nonNoDataValueCount = getN();
        if (nonNoDataValueCount > 0) {
            if (nonNoDataValueCount % 2L == 0L) {
                // Need arithmetic mean of ( ( nonNoDataValueCount / 2 ) - 1 )th
                // and ( nonNoDataValueCount / 2 )th values
                long requiredIndex = (nonNoDataValueCount / 2L) - 1L;
                int i = 0;
                ite = valueCount.keySet().iterator();
                while (ite.hasNext()) {
                   int v = ite.next();
                    i += valueCount.get(v);
                    if (i > requiredIndex && i > requiredIndex + 1) {
                        return v;
                    } else {
                        return (v + ite.next()) / 2.0d;
                    }
                }
            } else {
                // Need ( ( nonNoDataValueCount ) / 2 )th value
                long requiredIndex = nonNoDataValueCount / 2L;
                int i = 0;
                ite = valueCount.keySet().iterator();
                while (ite.hasNext()) {
                   int v = ite.next();
                    i += valueCount.get(v);
                    if (i > requiredIndex) {
                        return v;
                    }
                }
            }
        }
        return getGrid().getNoDataValue();
    }

    /**
     * For returning the standard deviation of all data values as a double.
     *
     * @return
     */
    @Override
    protected double getStandardDeviationDouble() {
        double r = 0.0d;
        double mean = getArithmeticMeanDouble();
        // Calculate the number of default values
        int n = ChunkNRows * ChunkNCols;
        int nValues = getNumberOfDefaultValues(n);
        r += ((DefaultValue - mean) * (DefaultValue - mean)) * nValues;
        Iterator<Integer> ite;
        /**
         * Add from data.DataMapBitSet;
         */
        ite = Data.DataMapBitSet.keySet().iterator();
        OffsetBitSet offsetBitSet;
        while (ite.hasNext()) {
           int v = ite.next();
            offsetBitSet = Data.DataMapBitSet.get(v);
            n = offsetBitSet.bitSet.size();
            nValues += n;
            r += ((v - mean) * (v - mean)) * n;
        }
        /**
         * Add from data.DataMapHashSet.
         */
        ite = Data.DataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
           int v = ite.next();
            n = Data.DataMapHashSet.get(v).size();
            nValues += n;
            r += ((v - mean) * (v - mean)) * n;
        }
        if ((nValues - 1.0d) > 0) {
            return Math.sqrt(r / (double) (nValues - 1L));
        } else {
            return r;
        }
    }

    /**
     * @return The number of different values.
     */
    protected BigInteger getDiversityBigInteger() {
        return BigInteger.valueOf(Data.DataMapBitSet.size()
                + Data.DataMapHashSet.size() + 1);
    }

    /**
     * @return An iterator for iterating over the values in this chunk.
     */
    public Grids_ChunkIteratorIntArrayOrMap iterator() {
        return new Grids_ChunkIteratorIntArrayOrMap(this);
    }

    @Override
    public Integer getMin(boolean update) {
        int min = Integer.MIN_VALUE;
        if (DefaultValue != NoDataValue) {
            min = Math.min(min, DefaultValue);
        }
        min = Math.min(min, Data.DataMapBitSet.firstKey());
        min = Math.min(min, Data.DataMapHashSet.firstKey());
        return min;
    }

    @Override
    public Integer getMax(boolean update) {
        int max = Integer.MIN_VALUE;
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

        public int offset;
        public BitSet bitSet;

        public OffsetBitSet(int offset) {
            this.offset = offset;
            bitSet = new BitSet();
        }
    }

    /**
     * Simple inner class for wrapping an int and a BitSet.
     */
    public class GridChunkIntMapData {

        /**
         * For more common values.
         */
        public final TreeMap<Integer, OffsetBitSet> DataMapBitSet;

        /**
         * For less common and more distributed values.
         */
        public final TreeMap<Integer, HashSet<Grids_2D_ID_int>> DataMapHashSet;

        public GridChunkIntMapData(
                TreeMap<Integer, OffsetBitSet> dataMapBitSet,
                TreeMap<Integer, HashSet<Grids_2D_ID_int>> dataMapHashSet) {
            DataMapBitSet = dataMapBitSet;
            DataMapHashSet = dataMapHashSet;
        }
    }

}
