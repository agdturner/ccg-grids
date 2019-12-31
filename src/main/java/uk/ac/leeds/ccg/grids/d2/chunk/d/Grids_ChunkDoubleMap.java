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
package uk.ac.leeds.ccg.grids.d2.chunk.d;

import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import java.math.RoundingMode;
import uk.ac.leeds.ccg.math.Math_BigDecimal;

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
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkDoubleMap extends Grids_ChunkDoubleArrayOrMap {

    private static final long serialVersionUID = 1L;

    /**
     * A value initialised with grid that can be used to optimise storage.
     * Storage is optimised with the defaultValue set to the most common value.
     * The location of all DefaultValues can be calculated from the converse of
     * the intersection of NoData, InDataMapHashSet and InDataMapBitSet.
     */
    public double defaultValue;

    /**
     * This is a copy of getGrid().getNoDataValue(boolean) for convenience.
     */
    private final double noDataValue;

    /**
     * Identifies the locations of all noDataValues.
     */
    private BitSet noData;

    /**
     * Identifies which cells are stored in DataMapHashSet.
     */
    private BitSet inDataMapHashSet;

    /**
     * Identifies which cells are stored in DataMapBitSet.
     */
    private BitSet inDataMapBitSet;

    /**
     * For storing the data of this chunk.
     */
    private GridChunkDoubleMapData Data;

    /**
     * {@link #defaultValue} is set to {@code 0.0d}.
     *
     * @param g What {@link #grid} is set to.
     * @param i What {@link #id} is set to.
     */
    protected Grids_ChunkDoubleMap(Grids_GridDouble g, Grids_2D_ID_int i) {
        this(g, i, 0.0d);
    }

    /**
     * Usually it is best if the defaultValue is the most common value.
     *
     * @param g What {@link #grid} is set to.
     * @param i What {@link #id} is set to.
     * @param dv What {@link #defaultValue} is set to.
     */
    protected Grids_ChunkDoubleMap(Grids_GridDouble g, Grids_2D_ID_int i, double dv) {
        super(g, i);
        defaultValue = dv;
        noDataValue = g.getNoDataValue();
        initData();
        cacheUpToDate = false;
    }

    /**
     * Usually it is best if the defaultValue is the most common value. The
     * chunk created will have the same cell values as {@code c}.
     *
     * @param c The chunk from which the values in this are set.
     * @param i The chunkID.
     * @param dv The default value.
     */
    protected Grids_ChunkDoubleMap(Grids_ChunkDouble c, Grids_2D_ID_int i,
            double dv) {
        super(c.getGrid(), i);
        defaultValue = dv;
        noDataValue = getGrid().getNoDataValue();
        initData();
        for (int row = 0; row < chunkNRows; row++) {
            for (int col = 0; col < chunkNCols; col++) {
                double value = c.getCell(row, col);
                initCell(row, col, value);
            }
        }
        cacheUpToDate = false;
    }

    /**
     * Initialises {@link #Data}.
     */
    @Override
    protected final void initData() {
        Data = new GridChunkDoubleMapData(new TreeMap<>(), new TreeMap<>());
        noData = new BitSet(chunkNCols * chunkNRows);
        inDataMapHashSet = new BitSet(chunkNCols * chunkNRows);
        inDataMapBitSet = new BitSet(chunkNCols * chunkNRows);
    }

    /**
     * @return {@link #Data}.
     */
    protected GridChunkDoubleMapData getData() {
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
    double[][] to2DDoubleArray() {
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        double ndv = g.getNoDataValue();
        double[][] r = new double[nrows][ncols];
        Arrays.fill(r, defaultValue);
        /**
         * Mask
         */
        int i = 0;
        for (int row = 0; row < nrows; row++) {
            for (int col = 0; col < ncols; col++) {
                if (noData.get(i)) {
                    r[row][col] = ndv;
                }
                i++;
            }
        }
        Iterator<Double> ite;
        /**
         * Populate result with all mappings from data.DataMapBitSet.
         */
        TreeMap<Double, OffsetBitSet> dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        int col = 0;
        int row = 0;
        while (ite.hasNext()) {
            Double value = ite.next();
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
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            Double value = ite.next();
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
     * @return Values in row major order as a double[].
     */
    @Override
    public double[] toArrayIncludingNoDataValues() {
        Grids_GridDouble g = getGrid();
        int nrows = g.getChunkNRows(id);
        int ncols = g.getChunkNCols(id);
        double[] r = new double[nrows * ncols];
        Arrays.fill(r, g.getNoDataValue());
        Iterator<Double> ite;
        /**
         * Populate result with all mappings from data.DataMapBitSet.
         */
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        while (ite.hasNext()) {
            Double v = ite.next();
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
        TreeMap<Double, HashSet<Grids_2D_ID_int>> dataMapHashSet;
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            Double v = ite.next();
            HashSet<Grids_2D_ID_int> cellIDs = dataMapHashSet.get(v);
            Iterator<Grids_2D_ID_int> ite2 = cellIDs.iterator();
            while (ite2.hasNext()) {
                Grids_2D_ID_int cellID = ite2.next();
                r[(cellID.getRow() * chunkNCols) + cellID.getCol()] = v;
            }
        }
        return r;
    }

    /**
     * @return Values excluding noDataValues in row major order as a double[].
     */
    @Override
    public double[] toArrayNotIncludingNoDataValues() {
        double[] r;
        Iterator<Double> ite;
        TreeMap<Double, OffsetBitSet> dataMapBitSet;
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
            n += dataMapBitSet.get(ite.next()).bitSet.cardinality();
        }
        // Count from Data.DataMapBitSet.
        dataMapHashSet = Data.DataMapHashSet;
        ite = dataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            n += dataMapHashSet.get(ite.next()).size();
        }
        r = new double[n];
        /**
         * Populate result with all mappings from data.DataMapBitSet.
         */
        dataMapBitSet = Data.DataMapBitSet;
        ite = dataMapBitSet.keySet().iterator();
        int i;
        n = 0;
        while (ite.hasNext()) {
            Double value = ite.next();
            offsetBitSet = dataMapBitSet.get(value);
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
            Double value = ite.next();
            cellIDs = dataMapHashSet.get(value);
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
    public double getCell(int row, int col) {
        int pos = (row * chunkNCols) + col;
        if (noData.get(pos)) {
            return noDataValue;
        } else if (inDataMapBitSet.get(pos)) {
            double r = getCell(pos);
            if (r != noDataValue) {
                return r;
            }
        } else if (inDataMapHashSet.get(pos)) {
            double r = getCell(new Grids_2D_ID_int(row, col));
            if (r != noDataValue) {
                return r;
            }
        }
        return defaultValue;
    }

    /**
     * @param row The row index of the cell w.r.t. the origin of this chunk.
     * @param col The column index of the cell w.r.t. the origin of this chunk.
     * @param i The cell ID.
     * @return The value at position given by: row, col.
     */
    protected double getCell(int row, int col, Grids_2D_ID_int i) {
        int pos = (row * chunkNCols) + col;
        if (noData.get(pos)) {
            return noDataValue;
        } else if (inDataMapBitSet.get(pos)) {
            double r = getCell(pos);
            if (r != noDataValue) {
                return r;
            }
        } else if (inDataMapHashSet.get(pos)) {
            double r = getCell(i);
            if (r != noDataValue) {
                return r;
            }
        }
        return defaultValue;
    }

    /**
     * Look in data.DataMapBitSet.
     */
    private double getCell(int position) {
        TreeMap<Double, OffsetBitSet> m = Data.DataMapBitSet;
        Iterator<Double> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            OffsetBitSet offsetBitSet = m.get(v);
            BitSet bitSet = offsetBitSet.bitSet;
            int pos = position - offsetBitSet.offset;
            if (pos > 0 && pos < bitSet.length()) {
                if (bitSet.get(pos)) {
                    return v;
                }
            }
        }
        return noDataValue;
    }

    /**
     * Look in data.DataMapHashSet.
     */
    private double getCell(Grids_2D_ID_int cellID) {
        TreeMap<Double, HashSet<Grids_2D_ID_int>> m = Data.DataMapHashSet;
        Iterator<Double> ite = m.keySet().iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            if (m.get(v).contains(cellID)) {
                return v;
            }
        }
        return noDataValue;
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
    public final void initCell(int row, int col, double v) {
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
    protected void initCell(int row, int col, Grids_2D_ID_int i, double v) {
        if (v != defaultValue) {
            int pos = (row * chunkNCols) + col;
            if (v == noDataValue) {
                noData.set(pos);
            } else {
                /**
                 * Look in data.DataMapBitSet or dataMapHashSet
                 */
                TreeMap<Double, OffsetBitSet> m = Data.DataMapBitSet;
                if (m.containsKey(v)) {
                    OffsetBitSet offsetBitSet = m.get(v);
                    BitSet bitSet = offsetBitSet.bitSet;
                    bitSet.set(pos);
                    inDataMapBitSet.set(pos);
                } else {
                    TreeMap<Double, HashSet<Grids_2D_ID_int>> m2
                            = Data.DataMapHashSet;
                    if (m2.containsKey(v)) {
                        m2.get(v).add(i);
                        inDataMapHashSet.set(pos);
                    } else {
                        /**
                         * If the chunk is looking sparse so far then add to
                         * dataMapHashSet, otherwise add to dataMapBitSet
                         */
                        if ((pos - noData.cardinality()) / (double) pos < 0.5) {
                            HashSet<Grids_2D_ID_int> s = new HashSet<>();
                            s.add(i);
                            m2.put(v, s);
                            inDataMapHashSet.set(pos);
                        } else {
                            OffsetBitSet offsetBitSet;
                            offsetBitSet = new OffsetBitSet(pos);
                            offsetBitSet.bitSet.set(0);
                            m.put(v, offsetBitSet);
                            inDataMapBitSet.set(pos);
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
    public double setCell(int row, int col, double v) {
        return setCell(row, col, new Grids_2D_ID_int(row, col), v);
    }

    /**
     * Returns the value at position given by: row, col and sets it to
     * {@code v}.
     *
     * @param row The chunk row.
     * @param col The chunk column.
     * @param i The chunk cell ID of the cell to be initialised.
     * @param v The value the cell is to be set to.
     * @return The value at position given by: row, col before it is set to
     * {@code v}.
     */
    public double setCell(int row, int col, Grids_2D_ID_int i, double v) {
        double r = getCell(row, col, i);
        if (r == v) {
            return r;
        }
        if (v != defaultValue) {
            int pos = (row * chunkNCols) + col;
            if (v == noDataValue) {
                noData.set(pos);
                if (r == defaultValue) {
                    return defaultValue;
                } else if (inDataMapBitSet.get(pos)) {
                    TreeMap<Double, OffsetBitSet> m = Data.DataMapBitSet;
                    OffsetBitSet offsetBitSet = m.get(r);
                    BitSet bitSet = offsetBitSet.bitSet;
                    bitSet.flip(pos);
                    if (bitSet.cardinality() == 0) {
                        m.remove(r);
                    }
                    return r;
                } else {
                    TreeMap<Double, HashSet<Grids_2D_ID_int>> m2
                            = Data.DataMapHashSet;
                    HashSet<Grids_2D_ID_int> s = m2.get(r);
                    s.remove(i);
                    if (s.isEmpty()) {
                        m2.remove(r);
                    }
                    return r;
                }
            } else {
                if (r == defaultValue) {
                    TreeMap<Double, OffsetBitSet> m2 = Data.DataMapBitSet;
                    if (m2.containsKey(v)) {
                        OffsetBitSet offsetBitSet = m2.get(v);
                        BitSet bitSet = offsetBitSet.bitSet;
                        bitSet.set(pos);
                        inDataMapHashSet.set(pos);
                        return r;
                    } else {
                        TreeMap<Double, HashSet<Grids_2D_ID_int>> m3
                                = Data.DataMapHashSet;
                        if (m3.containsKey(v)) {
                            HashSet<Grids_2D_ID_int> s = m3.get(v);
                            s.add(i);
                            inDataMapHashSet.set(pos);
                            return r;
                        } else {
                            OffsetBitSet offsetBitSet = new OffsetBitSet(pos);
                            offsetBitSet.bitSet.set(0);
                            m2.put(v, offsetBitSet);
                            inDataMapBitSet.set(pos);
                            return r;
                        }
                    }
                } else {
                    // result is a value
                    TreeMap<Double, OffsetBitSet> m2 = Data.DataMapBitSet;
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
                        TreeMap<Double, HashSet<Grids_2D_ID_int>> m3
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
//                                    / (double) (chunkNRows * chunkNCols);
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
                            inDataMapBitSet.set(pos);
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
        return ((long) chunkNRows * (long) chunkNCols) - noData.cardinality();
    }

    /**
     * @param n The number of cells in the chunk.
     * @return The number of cell values equal to {@link #defaultValue}.
     */
    public int getNumberOfDefaultValues(int n) {
        BitSet s = new BitSet(n);
        s.flip(0, n - 1);
        s.and(inDataMapHashSet);
        s.or(inDataMapBitSet);
        s.xor(noData);
        return n - s.cardinality();
    }

    /**
     * @return The sum of all data values as a BigDecimal.
     */
    @Override
    public BigDecimal getSum() {
        int n = chunkNRows * chunkNCols;
        int numberOfDefaultValues = getNumberOfDefaultValues(n);
        return getSumBigDecimal(n, numberOfDefaultValues);
    }

    protected BigDecimal getSumBigDecimal(int n, int numberOfDefaultValues) {
        BigDecimal r = BigDecimal.ZERO;
        r = r.add(BigDecimal.valueOf(defaultValue)
                .multiply(BigDecimal.valueOf(numberOfDefaultValues)));
        Iterator<Double> ite;
        /**
         * Add from data.DataMapBitSet;
         */
        TreeMap<Double, OffsetBitSet> m = Data.DataMapBitSet;
        ite = m.keySet().iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            OffsetBitSet offsetBitSet = m.get(v);
            n = offsetBitSet.bitSet.size();
            r = r.add(BigDecimal.valueOf(v).multiply(BigDecimal.valueOf(n)));
        }
        /**
         * Add from data.DataMapHashSet.
         */
        TreeMap<Double, HashSet<Grids_2D_ID_int>> m2 = Data.DataMapHashSet;
        ite = m2.keySet().iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            n = m2.get(v).size();
            r = r.add(BigDecimal.valueOf(v).multiply(BigDecimal.valueOf(n)));
        }
        return r;
    }

    /**
     * @return The minimum of all data values.
     */
    @Override
    public Double getMin() {
        double min;
        int n = chunkNRows * chunkNCols;
        if (getNumberOfDefaultValues(n) > 0) {
            min = defaultValue;
        } else {
            min = Double.POSITIVE_INFINITY;
        }
        min = Math.min(min, Data.DataMapBitSet.firstKey());
        min = Math.min(min, Data.DataMapHashSet.firstKey());
        return min;
    }

    /**
     * @return The maximum of all data values.
     */
    @Override
    public Double getMax() {
        double max;
        int n = chunkNRows * chunkNCols;
        if (getNumberOfDefaultValues(n) > 0) {
            max = defaultValue;
        } else {
            max = Double.NEGATIVE_INFINITY;
        }
        max = Math.max(max, Data.DataMapBitSet.lastKey());
        max = Math.max(max, Data.DataMapHashSet.lastKey());
        return max;
    }

    /**
     * @return The mode.
     */
    @Override
    protected HashSet<Double> getMode() {
        HashSet<Double> mode = new HashSet<>();
        int n = chunkNCols * chunkNRows;
        int numberOfDefaultValues = getNumberOfDefaultValues(n);
        int numberOfMostCommonValue = numberOfDefaultValues;
        mode.add(defaultValue);
        Iterator<Double> ite;
        ite = Data.DataMapBitSet.keySet().iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            OffsetBitSet offsetBitSet = Data.DataMapBitSet.get(v);
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
            double v = ite.next();
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
     * @return The median.
     */
    @Override
    public double getMedian() {
        TreeMap<Double, Integer> valueCount = new TreeMap<>();
        int nCells = chunkNCols * chunkNRows;
        int numberOfDefaultValues = getNumberOfDefaultValues(nCells);
        valueCount.put(defaultValue, numberOfDefaultValues);
        Iterator<Double> ite = Data.DataMapBitSet.keySet().iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            valueCount.put(v, Data.DataMapBitSet.get(v).bitSet.cardinality());
        }
        ite = Data.DataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            valueCount.put(v, Data.DataMapHashSet.get(v).size());
        }
        long n = getN();
        if (n > 0) {
            if (n % 2L == 0L) {
                // Need arithmetic mean of ( ( nonNoDataValueCount / 2 ) - 1 )th
                // and ( nonNoDataValueCount / 2 )th values
                long requiredIndex = (n / 2L) - 1L;
                int i = 0;
                ite = valueCount.keySet().iterator();
                while (ite.hasNext()) {
                    double v = ite.next();
                    i += valueCount.get(v);
                    if (i > requiredIndex && i > requiredIndex + 1) {
                        return v;
                    } else {
                        return (v + ite.next()) / 2.0d;
                    }
                }
            } else {
                // Need ( ( nonNoDataValueCount ) / 2 )th value
                long requiredIndex = n / 2L;
                int i = 0;
                ite = valueCount.keySet().iterator();
                while (ite.hasNext()) {
                    double v = ite.next();
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
     * @return The standard deviation.
     */
    @Override
    protected BigDecimal getStandardDeviation(int dp, RoundingMode rm) {
        BigDecimal r = BigDecimal.ZERO;
        BigDecimal mean = getArithmeticMean(dp, rm);
        // Calculate the number of default values
        int n = chunkNRows * chunkNCols;
        int nValues = getNumberOfDefaultValues(n);
        r = r.add((BigDecimal.valueOf(defaultValue).subtract(mean).pow(2))
                .multiply(BigDecimal.valueOf(nValues)));
        Iterator<Double> ite;
        /**
         * Add from data.DataMapBitSet;
         */
        ite = Data.DataMapBitSet.keySet().iterator();
        OffsetBitSet offsetBitSet;
        while (ite.hasNext()) {
            double v = ite.next();
            offsetBitSet = Data.DataMapBitSet.get(v);
            n = offsetBitSet.bitSet.size();
            nValues += n;
            r = r.add((BigDecimal.valueOf(v).subtract(mean).pow(2))
                    .multiply(BigDecimal.valueOf(n)));
        }
        /**
         * Add from data.DataMapHashSet.
         */
        ite = Data.DataMapHashSet.keySet().iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            n = Data.DataMapHashSet.get(v).size();
            nValues += n;
            r = r.add((BigDecimal.valueOf(v).subtract(mean).pow(2))
                    .multiply(BigDecimal.valueOf(n)));
        }
        if ((nValues - 1L) > 0L) {
            return Math_BigDecimal.sqrt(Math_BigDecimal.divideRoundIfNecessary(
                    r, BigInteger.valueOf(nValues - 1L), dp * 2, rm), dp, rm);
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
    public Grids_ChunkIteratorDoubleArrayOrMap iterator() {
        return new Grids_ChunkIteratorDoubleArrayOrMap(this);
    }

    @Override
    public Double getMin(boolean update) {
        double min = Integer.MIN_VALUE;
        if (defaultValue != noDataValue) {
            min = Math.min(min, defaultValue);
        }
        min = Math.min(min, Data.DataMapBitSet.firstKey());
        min = Math.min(min, Data.DataMapHashSet.firstKey());
        return min;
    }

    @Override
    public Double getMax(boolean update) {
        double max = Integer.MIN_VALUE;
        if (defaultValue != noDataValue) {
            max = Math.max(max, defaultValue);
        }
        max = Math.max(max, Data.DataMapBitSet.lastKey());
        max = Math.max(max, Data.DataMapHashSet.lastKey());
        return max;
    }

    /**
     * Simple inner class for wrapping an int and a bitSet.
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
     * Simple inner class for wrapping an int and a bitSet.
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
