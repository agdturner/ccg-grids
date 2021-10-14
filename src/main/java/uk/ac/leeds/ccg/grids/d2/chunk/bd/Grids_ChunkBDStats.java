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

import uk.ac.leeds.ccg.grids.d2.chunk.d.*;
import uk.ac.leeds.ccg.grids.d2.stats.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDouble;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleArrayOrMap;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleSinglet;
import uk.ac.leeds.ccg.grids.d2.chunk.d.Grids_ChunkDoubleIteratorArrayOrMap;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.grids.d2.grid.d.Grids_GridDoubleIterator;
import uk.ac.leeds.ccg.math.number.Math_BigRational;

/**
 * For statistics of chunks of type double. Some statistics are kept up to date
 * as the values are changed.
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_ChunkBDStats extends Grids_StatsDouble {

    private static final long serialVersionUID = 1L;

    /**
     * A reference to the chunk
     */
    protected Grids_ChunkDouble c;

    /**
     * Creates a new Grids_ChunkStatsDouble instance.
     *
     * @param ge The GRids Environment.
     * @param c What this.c is set to.
     */
    public Grids_ChunkBDStats(Grids_Environment ge, Grids_ChunkDouble c) {
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
        if (c instanceof Grids_ChunkDoubleSinglet) {
            double v = ((Grids_ChunkDoubleSinglet) c).getV();
            if (Double.isFinite(v)) {
                Grids_GridDouble g = c.getGrid();
                if (v != g.getNoDataValue()) {
                    max = v;
                    min = v;
                    n = (long) g.getChunkNCols(c.getId())
                            * (long) g.getChunkNRows(c.getId());
                    sum = Math_BigRational.valueOf(v).multiply(Math_BigRational.valueOf(n));
                    nMax = n;
                    nMin = n;
                }
            }
        } else {
            Grids_ChunkDoubleIteratorArrayOrMap ite;
            ite = new Grids_ChunkDoubleIteratorArrayOrMap(
                    (Grids_ChunkDoubleArrayOrMap) c);
            double ndv = c.getGrid().getNoDataValue();
            while (ite.hasNext()) {
                double v = ite.next();
                if (Double.isFinite(v)) {
                    if (v != ndv) {
                        update(v);
                    }
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
        if (c instanceof Grids_ChunkDoubleSinglet) {
            double v = ((Grids_ChunkDoubleSinglet) c).getV();
            if (Double.isFinite(v)) {
                Grids_GridDouble g = c.getGrid();
                if (v != g.getNoDataValue()) {
                    if (v == 0) {
                        return n;
                    }
                }
            }
            return 0;
        } else {
            Grids_ChunkDoubleIteratorArrayOrMap ite;
            ite = new Grids_ChunkDoubleIteratorArrayOrMap(
                    (Grids_ChunkDoubleArrayOrMap) c);
            double ndv = c.getGrid().getNoDataValue();
            while (ite.hasNext()) {
                double v = ite.next();
                if (Double.isFinite(v)) {
                    if (v != ndv) {
                        if (v == 0) {
                            r++;
                        }
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
    public Math_BigRational getSum(boolean update) throws IOException, Exception,
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
    public Math_BigRational getSum() throws IOException, Exception,
            ClassNotFoundException {
        return sum;
    }

    /**
     *
     * @param nClasses The number of classes to divide the data into.
     * @return Object[] r where r[0] is the min, r[1] is the max; r[2] is a
     * {@code TreeMap<Integer, TreeMap<Double, Long>>*} where the key is the
     * class index and the value is a map indexed by the number and the count.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Object[] getQuantileClassMap(int nClasses) throws IOException,
            Exception, ClassNotFoundException {
        Object[] r = new Object[3];
        Grids_GridDouble g = getGrid();
        TreeMap<Integer, BigDecimal> mins = new TreeMap<>();
        TreeMap<Integer, BigDecimal> maxs = new TreeMap<>();
        for (int i = 1; i < nClasses; i++) {
            mins.put(i, BigDecimal.valueOf(Double.MAX_VALUE));
            maxs.put(i, BigDecimal.valueOf(-Double.MAX_VALUE));
        }
        r[0] = mins;
        r[1] = maxs;
        long nonZeroN = getNonZeroN();
        long nInClass = nonZeroN / nClasses;
        if (nonZeroN % nClasses != 0) {
            nInClass += 1;
        }
        double noDataValue = g.getNoDataValue();
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
        Grids_GridDoubleIterator ite = g.iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            BigDecimal vbd = BigDecimal.valueOf(v);
            if (!(v == 0.0d || v == noDataValue)) {
                if (count % nInClass == 0) {
                    System.out.println(count + " out of " + nonZeroN);
                }
                count++;
                if (firstValue) {
                    mins.put(0, vbd);
                    maxs.put(0, vbd);
                    classCounts.put(0, 1L);
                    classMap.get(0).put(vbd, 1L);
                    if (nInClass < 2) {
                        classToFill = 1;
                    }
                    firstValue = false;
                } else {
                    int[] valueClass;
                    if (classToFill == nClasses) {
                        classToFill--;
                    }
                    valueClass = getValueClass(vbd, classMap, mins, maxs,
                            classCounts, nInClass, classToFill);
                    classToFill = valueClass[1];
                }
            }
        }
        return r;
    }
}
