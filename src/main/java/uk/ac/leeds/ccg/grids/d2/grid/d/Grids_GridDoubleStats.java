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
package uk.ac.leeds.ccg.grids.d2.grid.d;

import uk.ac.leeds.ccg.grids.d2.stats.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.math.number.Math_BigRational;
import uk.ac.leeds.ccg.math.number.Math_BigRationalSqrt;

/**
 * Some of the statistic are kept up to date as the
 * underlying data is changed (which can be computationally expensive). Second 
 * order statistics like the standard deviation always require recalculating if 
 * the data have changed.)
 *
 * @author Andy Turner
 * @version 1.0
 */
public class Grids_GridDoubleStats extends Grids_StatsDouble {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     * 
     * @param ge Grids_Environment
     */
    public Grids_GridDoubleStats(Grids_Environment ge) {
        super(ge);
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
        sum = Math_BigRational.ZERO;
    }

    @Override
    protected void init() {
        super.init();
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
        sum = Math_BigRational.ZERO;
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
    public Grids_GridDouble getGrid() {
        return (Grids_GridDouble) grid;
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
        Grids_GridDouble g = getGrid();
        double ndv = g.getNoDataValue();
        // This is too slow!
        Grids_GridDoubleIterator ite = g.iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            if (Double.isFinite(v)) {
                if (v != ndv) {
                    update(v);
                }
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
    public Double getMin(boolean update) throws IOException, Exception, ClassNotFoundException {
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
        Grids_GridDouble g = getGrid();
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
        Grids_GridDouble g = getGrid();
        double ndv = g.getNoDataValue();
        Grids_GridDoubleIterator ite = g.iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            if (!(v == ndv || v == 0)) {
                if (Double.isFinite(v)) {
                    r++;
                }
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
    public Math_BigRational getSum(boolean update) throws IOException, Exception,
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
    public Math_BigRational getSum() throws IOException, Exception, ClassNotFoundException {
        Math_BigRational r = Math_BigRational.ZERO;
        Grids_GridDouble g = getGrid();
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
        Math_BigRational stdev = Math_BigRational.ZERO;
        Math_BigRational mean = getArithmeticMean();
        long dataValueCount = 0;
        Grids_GridDouble g = (Grids_GridDouble) grid;
        double ndv = g.getNoDataValue();
        Grids_GridDoubleIterator ite = g.iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            if (v != ndv) {
                if (Double.isFinite(v)) {
                    Math_BigRational delta = Math_BigRational.valueOf(v).subtract(mean);
                    stdev = stdev.add(delta.multiply(delta));
                    dataValueCount ++;
                }
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
