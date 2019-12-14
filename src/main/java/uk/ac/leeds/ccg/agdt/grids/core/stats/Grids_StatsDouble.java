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
package uk.ac.leeds.ccg.agdt.grids.core.stats;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.agdt.grids.core.grid.d.Grids_GridDouble;
import uk.ac.leeds.ccg.agdt.grids.core.grid.d.Grids_GridDoubleIterator;

/**
 * Grids_GridDouble statistics. Some of the statistic are kept up to date as the
 * underlying data is changed (which can be computationally expensive). Some
 * statistics like the standard deviation always require going through all the
 * data again if the values have changed in order to recalculate.)
 *
 * @author Andy Turner
 * @version 1.0.0
 */
public class Grids_StatsDouble extends Grids_StatsNumber {

    private static final long serialVersionUID = 1L;

    /**
     * For storing the minimum value.
     */
    protected double Min;

    /**
     * For storing the maximum value.
     */
    protected double Max;

    public Grids_StatsDouble(Grids_Environment ge) {
        super(ge);
        Min = Double.MAX_VALUE;
        Max = -Double.MAX_VALUE;
        Sum = BigDecimal.ZERO;
    }

    @Override
    protected void init() {
        super.init();
        Min = Double.MAX_VALUE;
        Max = -Double.MAX_VALUE;
        Sum = BigDecimal.ZERO;
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
    public void update() throws IOException, ClassNotFoundException {
        env.checkAndMaybeFreeMemory();
        init();
        Grids_GridDouble g = getGrid();
        double ndv = g.getNoDataValue();
        Grids_GridDoubleIterator ite = g.iterator();
        while (ite.hasNext()) {
            double v = ite.next();
            if (Double.isFinite(v)) {
                if (v != ndv) {
                    BigDecimal vBD = new BigDecimal(v);
                    update(v, vBD);
                }
            }
        }
    }

    protected void update(double v, BigDecimal vBD) {
        n++;
        setSum(Sum.add(vBD));
        if (v < Min) {
            NMin = 1;
            Min = v;
        } else {
            if (v == Min) {
                NMin++;
            }
        }
        if (v > Max) {
            NMax = 1;
            Max = v;
        } else {
            if (v == Max) {
                NMax++;
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
    public Double getMin(boolean update) throws IOException, ClassNotFoundException {
        if (NMin < 1) {
            if (update) {
                update();
            }
        }
        return Min;
    }

    public void setMin(double min) {
        Min = min;
    }

    /**
     * @return The maximum of all data values in {@link #grid}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public Double getMax(boolean update) throws IOException, ClassNotFoundException {
        if (NMax < 1) {
            if (update) {
                update();
            }
        }
        return Max;
    }

    public void setMax(double max) {
        Max = max;
    }

    /**
     * @return The Number of data values in {@link #grid}.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    @Override
    public long getN() throws IOException, ClassNotFoundException {
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
    public BigInteger getNonZeroN() throws IOException, ClassNotFoundException {
        BigInteger r = BigInteger.ZERO;
        Grids_GridDouble g = getGrid();
        double ndv = g.getNoDataValue();
        Grids_GridDoubleIterator ite = g.iterator();
        while (ite.hasNext()) {
            double value = ite.next();
            if (!(value == ndv || value == 0)) {
                if (Double.isFinite(value)) {
                    r = r.add(BigInteger.ONE);
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
    public BigDecimal getSum(boolean update) throws IOException,
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
    public BigDecimal getSum() throws IOException, ClassNotFoundException {
        BigDecimal r = BigDecimal.ZERO;
        Grids_GridDouble g = getGrid();
        Iterator<Grids_2D_ID_int> ite = g.iterator().getGridIterator();
        while (ite.hasNext()) {
            r = r.add(g.getChunk(ite.next()).getSum());
            env.checkAndMaybeFreeMemory();
        }
        return r;
    }

    /**
     * @param dp The number of decimal places the result will be accurate to.
     * @return The standard deviation correct to {@code dp} decimal places.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If encountered.
     */
    public BigDecimal getStandardDeviation(int dp)
            throws IOException, ClassNotFoundException {
        BigDecimal stdev = BigDecimal.ZERO;
        BigDecimal mean = getArithmeticMean(dp * 2);
        BigDecimal dataValueCount = BigDecimal.ZERO;
        Grids_GridDouble g = (Grids_GridDouble) grid;
        double ndv = g.getNoDataValue();
        Grids_GridDoubleIterator ite = g.iterator();
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
        stdev = stdev.divide(dataValueCount, dp, RoundingMode.HALF_EVEN);
        return Math_BigDecimal.sqrt(stdev, dp, env.bd.getRoundingMode());
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
            ClassNotFoundException {
        Object[] r = new Object[3];
        Grids_GridDouble g = getGrid();
        TreeMap<Integer, Double> mins = new TreeMap<>();
        TreeMap<Integer, Double> maxs = new TreeMap<>();
        for (int i = 1; i < nClasses; i++) {
            mins.put(i, Double.MAX_VALUE);
            maxs.put(i, -Double.MAX_VALUE);
        }
        r[0] = mins;
        r[1] = maxs;
        BigInteger nonZeroN = getNonZeroN();
        long nonZeroNLong = nonZeroN.longValueExact();
        System.out.println("nonZeroAndNonNoDataValueCount " + nonZeroN);
        long nInClass = nonZeroNLong / nClasses;
        if (nonZeroNLong % nClasses != 0) {
            nInClass += 1;
        }
        double noDataValue = g.getNoDataValue();
        TreeMap<Integer, Long> classCounts = new TreeMap<>();
        for (int i = 1; i < nClasses; i++) {
            classCounts.put(i, 0L);
        }
        int classToFill = 0;
        boolean firstValue = true;
        TreeMap<Integer, TreeMap<Double, Long>> classMap = new TreeMap<>();
        for (int i = 0; i < nClasses; i++) {
            classMap.put(i, new TreeMap<>());
        }
        r[2] = classMap;
        int count = 0;
        //long valueID = 0;
        Grids_GridDoubleIterator ite = g.iterator();
        while (ite.hasNext()) {
            double value = ite.next();
            if (!(value == 0.0d || value == noDataValue)) {
                if (count % nInClass == 0) {
                    System.out.println(count + " out of " + nonZeroN);
                }
                count++;
                if (firstValue) {
                    mins.put(0, value);
                    maxs.put(0, value);
                    classCounts.put(0, 1L);
                    classMap.get(0).put(value, 1L);
                    if (nInClass < 2) {
                        classToFill = 1;
                    }
                    firstValue = false;
                } else {
                    int[] valueClass;
                    if (classToFill == nClasses) {
                        classToFill--;
                    }
                    valueClass = getValueClass(value, classMap, mins, maxs, 
                            classCounts, nInClass, classToFill);
                    classToFill = valueClass[1];
                }
            }
        }
        return r;
    }
}
