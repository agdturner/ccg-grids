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
package uk.ac.leeds.ccg.grids.d2.grid.i;

import ch.obermuhlner.math.big.BigRational;
import java.io.IOException;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.stats.Grids_StatsInt;
import uk.ac.leeds.ccg.math.number.Math_BigRationalSqrt;

/**
 * Grids_GridDouble statistics. Some of the statistic are kept up to date as the
 * underlying data is changed (which can be computationally expensive). Some
 * statistics like the standard deviation always require going through all the
 * data again if the values have changed in order to recalculate.)
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_GridIntStats extends Grids_StatsInt {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     *
     * @param ge Grids_Environment
     */
    public Grids_GridIntStats(Grids_Environment ge) {
        super(ge);
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        sum = BigRational.ZERO;
    }

    @Override
    protected void init() {
        super.init();
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        sum = BigRational.ZERO;
    }

    /**
     * @return true - some stats are kept up to date as the underlying data
     * changes.
     */
    @Override
    public boolean isUpdated() {
        return true;
    }

    /**
     * @return (Grids_GridDouble) grid.
     */
    @Override
    public Grids_GridInt getGrid() {
        return (Grids_GridInt) grid;
    }

    /**
     * Updates by going through all values in grid.
     *
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public void update() throws IOException, Exception, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        init();
        Grids_GridInt g = getGrid();
        int ndv = g.getNoDataValue();
        // This is slow!
        Grids_GridIntIterator ite = g.iterator();
        while (ite.hasNext()) {
            int v = ite.next();
            if (v != ndv) {
                update(v);
            }
        }
    }

    /**
     * @param update If true then {@link #update()} is called.
     * @return The minimum of all data values in {@link #grid}.
     *
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Integer getMin(boolean update) throws IOException, Exception, ClassNotFoundException {
        if (nMin < 1) {
            if (update) {
                update();
            }
        }
        return min;
    }

    /**
     * @return The Number of data values in {@link #grid}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public long getN() throws IOException, Exception, ClassNotFoundException {
        long r = 0;
        Grids_GridInt g = getGrid();
        Iterator<Grids_2D_ID_int> ite = g.iterator().getGridIterator();
        while (ite.hasNext()) {
            r += g.getChunk(ite.next()).getN();
            env.checkAndMaybeFreeMemory();
        }
        return r;
    }

    /**
     * @return The Number of non zero data values in {@link #grid}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public long getNonZeroN() throws IOException, Exception, ClassNotFoundException {
        long r = 0L;
        Grids_GridInt g = getGrid();
        int ndv = g.getNoDataValue();
        Grids_GridIntIterator ite = g.iterator();
        while (ite.hasNext()) {
            int v = ite.next();
            if (!(v == ndv || v == 0)) {
                r++;
            }
        }
        return r;
    }

    /**
     * @param update Is ignored.
     * @return The sum of all data values.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigRational getSum(boolean update) throws IOException, Exception,
            ClassNotFoundException {
//        if (update) {
//            update();
//        }
        return getSum();
    }

    /**
     * @return The sum of all data values.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigRational getSum() throws IOException, Exception, ClassNotFoundException {
        BigRational r = BigRational.ZERO;
        Grids_GridInt g = getGrid();
        Iterator<Grids_2D_ID_int> ite = g.iterator().getGridIterator();
        while (ite.hasNext()) {
            r = r.add(g.getChunk(ite.next()).getSum());
            env.checkAndMaybeFreeMemory();
        }
        return r;
    }

    /**
     * @param oom The Order of Magnitude for the precision.
     * @param rm The RoundingMode for any rounding.
     * @return The standard deviation correct to {@code oom}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public Math_BigRationalSqrt getStandardDeviation(int oom, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        BigRational stdev = BigRational.ZERO;
        BigRational mean = getArithmeticMean();
        long dataValueCount = 0;
        Grids_GridInt g = (Grids_GridInt) grid;
        int ndv = g.getNoDataValue();
        Grids_GridIntIterator ite = g.iterator();
        while (ite.hasNext()) {
            int v = ite.next();
            if (v != ndv) {
                BigRational delta = BigRational.valueOf(v).subtract(mean);
                stdev = stdev.add(delta.multiply(delta));
                dataValueCount++;
            }
        }
        if (dataValueCount < 2) {
            return Math_BigRationalSqrt.ZERO;
        }
        stdev = stdev.divide(BigInteger.valueOf(dataValueCount - 1L));
        return new Math_BigRationalSqrt(stdev, oom, rm);
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
        Grids_GridInt g = getGrid();
        TreeMap<Integer, BigRational> mins = new TreeMap<>();
        TreeMap<Integer, BigRational> maxs = new TreeMap<>();
        for (int i = 1; i < nClasses; i++) {
            mins.put(i, BigRational.valueOf(Integer.MAX_VALUE));
            maxs.put(i, BigRational.valueOf(Integer.MIN_VALUE));
        }
        r[0] = mins;
        r[1] = maxs;
        long nonZeroN = getNonZeroN();
        long nInClass = nonZeroN / nClasses;
        if (nonZeroN % nClasses != 0) {
            nInClass += 1;
        }
        int noDataValue = g.getNoDataValue();
        TreeMap<Integer, Long> classCounts = new TreeMap<>();
        for (int i = 1; i < nClasses; i++) {
            classCounts.put(i, 0L);
        }
        int classToFill = 0;
        boolean firstValue = true;
        TreeMap<Integer, TreeMap<BigRational, Long>> classMap = new TreeMap<>();
        for (int i = 0; i < nClasses; i++) {
            classMap.put(i, new TreeMap<>());
        }
        r[2] = classMap;
        int count = 0;
        //long valueID = 0;
        Grids_GridIntIterator ite = g.iterator();
        while (ite.hasNext()) {
            int v = ite.next();
            BigRational vbd = BigRational.valueOf(v);
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
