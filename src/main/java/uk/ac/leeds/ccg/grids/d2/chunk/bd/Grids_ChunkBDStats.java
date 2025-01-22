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
package uk.ac.leeds.ccg.grids.d2.chunk.bd;

import ch.obermuhlner.math.big.BigRational;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.grid.bd.Grids_GridBD;
import uk.ac.leeds.ccg.grids.d2.grid.bd.Grids_GridBDIterator;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsBD;

/**
 * For statistics of chunks of type double. Some statistics are kept up to date
 * as the values are changed.
 *
 * @author Andy Turner
 * @version 1.0
 */
public class Grids_ChunkBDStats extends Grids_StatsBD {

    private static final long serialVersionUID = 1L;

    /**
     * A reference to the chunk
     */
    protected Grids_ChunkBD c;

    /**
     * Creates a new instance.
     *
     * @param ge The Grids Environment.
     * @param c What this.c is set to.
     */
    public Grids_ChunkBDStats(Grids_Environment ge, Grids_ChunkBD c) {
        super(ge);
        this.c = c;
    }

    /**
     * Updates by going through all values in the chunk.
     *
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public void update() throws IOException, Exception, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        init();
        if (c instanceof Grids_ChunkBDSinglet) {
            BigDecimal v = ((Grids_ChunkBDSinglet) c).getV();
                Grids_GridBD g = c.getGrid();
                if (v != g.getNoDataValue()) {
                    max = v;
                    min = v;
                    n = (long) g.getChunkNCols(c.getId())
                            * (long) g.getChunkNRows(c.getId());
                    sum = BigRational.valueOf(v).multiply(BigRational.valueOf(n));
                    nMax = n;
                    nMin = n;
                }
        } else {
            Grids_ChunkBDIteratorArrayOrMap ite;
            ite = new Grids_ChunkBDIteratorArrayOrMap(
                    (Grids_ChunkBDArrayOrMap) c);
            BigDecimal ndv = c.getGrid().getNoDataValue();
            while (ite.hasNext()) {
                BigDecimal v = ite.next();
                    if (v.compareTo(ndv) != 0) {
                        update(v);
                    }
            }
        }
    }

    /**
     * @return The Number of data values in {@link #c}.
     *
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public long getN() throws IOException, Exception, ClassNotFoundException {
        return n;
    }

    /**
     * @return The Number of non zero data values in {@link #grid}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public long getNonZeroN() throws IOException, Exception,
            ClassNotFoundException {
        long r = 0L;
        if (c instanceof Grids_ChunkBDSinglet) {
            BigDecimal v = ((Grids_ChunkBDSinglet) c).getV();
                Grids_GridBD g = c.getGrid();
                if (v.compareTo(g.getNoDataValue()) != 0) {
                    if (v.compareTo(BigDecimal.ZERO) != 0) {
                        return n;
                    }
                }
            return 0;
        } else {
            Grids_ChunkBDIteratorArrayOrMap ite;
            ite = new Grids_ChunkBDIteratorArrayOrMap(
                    (Grids_ChunkBDArrayOrMap) c);
            BigDecimal ndv = c.getGrid().getNoDataValue();
            while (ite.hasNext()) {
                BigDecimal v = ite.next();
                if (v.compareTo(ndv) != 0) {
                    if (v.compareTo(BigDecimal.ZERO) != 0) {
                            r++;
                        }
                    }
            }
            return r;
        }
    }

    /**
     * @param update Is ignored.
     * @return The sum of all data values.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigRational getSum(boolean update) throws IOException, Exception,
            ClassNotFoundException {
        if (update) {
            update();
        }
        return getSum();
    }

    /**
     * @return The sum of all data values.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigRational getSum() throws IOException, Exception,
            ClassNotFoundException {
        return sum;
    }

    /**
     *
     * @param nClasses The number of classes to divide the data into.
     * @return Object[] r where r[0] is the min, r[1] is the max; r[2] is a
     * {@code TreeMap<Integer, TreeMap<Int, Long>>*} where the key is the
     * class index and the value is a map indexed by the number and the count.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Object[] getQuantileClassMap(int nClasses) throws IOException,
            Exception, ClassNotFoundException {
        Object[] r = new Object[3];
        Grids_GridBD g = getGrid();
        TreeMap<Integer, BigDecimal> mins = new TreeMap<>();
        TreeMap<Integer, BigDecimal> maxs = new TreeMap<>();
        for (int i = 1; i < nClasses; i++) {
            mins.put(i, BigDecimal.valueOf(Integer.MAX_VALUE));
            maxs.put(i, BigDecimal.valueOf(Integer.MIN_VALUE));
        }
        r[0] = mins;
        r[1] = maxs;
        long nonZeroN = getNonZeroN();
        long nInClass = nonZeroN / nClasses;
        if (nonZeroN % nClasses != 0) {
            nInClass += 1;
        }
        BigDecimal noDataValue = g.getNoDataValue();
        TreeMap<Integer, Long> classCounts = new TreeMap<>();
        for (int i = 1; i < nClasses; i++) {
            classCounts.put(i, 0L);
        }
        int classToFill = 0;
        boolean firstValue = true;
        TreeMap<Integer, TreeMap<BigDecimal, Long>> classMap = new TreeMap<>();
        for (int i = 0; i < nClasses; i++) {
            classMap.put(i, new TreeMap<>());
        }
        r[2] = classMap;
        int count = 0;
        //long valueID = 0;
        Grids_GridBDIterator ite = g.iterator();
        while (ite.hasNext()) {
            BigDecimal v = ite.next();
            if (!(v.compareTo(BigDecimal.ZERO) == 0 || v.compareTo(noDataValue) == 0)) {
                if (count % nInClass == 0) {
                    System.out.println(count + " out of " + nonZeroN);
                }
                count++;
                if (firstValue) {
                    mins.put(0, v);
                    maxs.put(0, v);
                    classCounts.put(0, 1L);
                    classMap.get(0).put(v, 1L);
                    if (nInClass < 2) {
                        classToFill = 1;
                    }
                    firstValue = false;
                } else {
                    int[] valueClass;
                    if (classToFill == nClasses) {
                        classToFill--;
                    }
                    valueClass = getValueClass(v, classMap, mins, maxs,
                            classCounts, nInClass, classToFill);
                    classToFill = valueClass[1];
                }
            }
        }
        return r;
    }
}
