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
package uk.ac.leeds.ccg.grids.d2.stats;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.grids.d2.Grids_2D_ID_int;
import uk.ac.leeds.ccg.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridInt;
import uk.ac.leeds.ccg.grids.d2.grid.i.Grids_GridIteratorInt;

/**
 * Grids_GridInt statistics. Some of the statistic are kept up to date as the
 * underlying data is changed (which can be computationally expensive). Some
 * statistics like the standard deviation always require going through all the
 * data again if the values have changed in order to recalculate.)
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_StatsInt extends Grids_StatsNumber {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the minimum value.
     */
    protected int min;

    /**
     * For storing the maximum value.
     */
    protected int max;

    public Grids_StatsInt(Grids_Environment ge) {
        super(ge);
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        sum = BigDecimal.ZERO;
    }

    @Override
    protected void init() {
        super.init();
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        sum = BigDecimal.ZERO;
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
     *
     * @return (Grids_GridInt) grid.
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
        Grids_GridIteratorInt ite = g.iterator();
        while (ite.hasNext()) {
            int value = ite.next();
            if (value != ndv) {
                BigDecimal valueBD = new BigDecimal(value);
                update(value, valueBD);
            }
        }
    }

    protected void update(int value, BigDecimal valueBD) {
        n = n.add(BigInteger.ZERO);
        setSum(sum.add(valueBD));
        if (value < min) {
            nMin++;
            min = value;
        } else {
            if (value == min) {
                nMin = 1;
            }
        }
        if (value > max) {
            nMax++;
            max = value;
        } else {
            if (value == max) {
                nMax++;
            }
        }
    }

    /**
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

    public void setMin(int min) {
        this.min = min;
    }

    /**
     * @return The maximum of all data values in {@link #grid}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Integer getMax(boolean update) throws IOException, Exception, ClassNotFoundException {
        if (nMax < 1) {
            if (update) {
                update();
            }
        }
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    /**
     * @return The Number of data values in {@link #grid}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public BigInteger getN() throws IOException, Exception, ClassNotFoundException {
        BigInteger r = BigInteger.ZERO;
        Grids_GridInt g = getGrid();
        Iterator<Grids_2D_ID_int> ite = g.iterator().getGridIterator();
        while (ite.hasNext()) {
            r = r.add(BigInteger.valueOf(g.getChunk(ite.next()).getN()));
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
    public long getNonZeroN() throws IOException, Exception, 
            ClassNotFoundException {
        long r = 0;
        Grids_GridInt g = getGrid();
        double ndv = g.getNoDataValue();
        Grids_GridIteratorInt ite = g.iterator();
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
    public BigDecimal getSum(boolean update) throws IOException, Exception,
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
    public BigDecimal getSum() throws IOException, Exception, ClassNotFoundException {
        BigDecimal r = BigDecimal.ZERO;
        Grids_GridInt g = getGrid();
        Iterator<Grids_2D_ID_int> ite = g.iterator().getGridIterator();
        while (ite.hasNext()) {
            r = r.add(g.getChunk(ite.next()).getSum());
            env.checkAndMaybeFreeMemory();
        }
        return r;
    }

    /**
     * @param dp The number of decimal places the result will be accurate to.
         * @param rm The RoundingMode used in BigDecimal arithmetic.
 * @return The standard deviation correct to {@code dp} decimal places.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigDecimal getStandardDeviation(int dp, RoundingMode rm)
            throws IOException, Exception, ClassNotFoundException {
        BigDecimal stdev = BigDecimal.ZERO;
        BigDecimal mean = getArithmeticMean(dp * 2, rm);
        BigDecimal dataValueCount = BigDecimal.ZERO;
        Grids_GridInt g = (Grids_GridInt) grid;
        double ndv = g.getNoDataValue();
        Grids_GridIteratorInt ite = g.iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            if (v != ndv) {
                if (Double.isFinite(v)) {
                    BigDecimal diffFromMean = new BigDecimal(v).subtract(mean);
                    stdev = stdev.add(diffFromMean.multiply(diffFromMean));
                    dataValueCount = dataValueCount.add(BigDecimal.ONE);
                }
            }
        }
        if (dataValueCount.compareTo(BigDecimal.ONE) != 1) {
            return stdev;
        }
        stdev = stdev.divide(dataValueCount, dp * 2, rm);
        return Math_BigDecimal.sqrt(stdev, dp, rm);
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
    public Object[] getQuantileClassMap(int nClasses) throws IOException, Exception,
            ClassNotFoundException {
        Object[] r = new Object[3];
        Grids_GridInt g = getGrid();
        TreeMap<Integer, BigDecimal> mins = new TreeMap<>();
        TreeMap<Integer, BigDecimal> maxs = new TreeMap<>();
        for (int i = 1; i < nClasses; i++) {
            mins.put(i, BigDecimal.valueOf(Integer.MAX_VALUE));
            maxs.put(i, BigDecimal.valueOf(-Integer.MAX_VALUE));
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
        Grids_GridIteratorInt ite = g.iterator();
        while (ite.hasNext()) {
            int v = ite.next();
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
