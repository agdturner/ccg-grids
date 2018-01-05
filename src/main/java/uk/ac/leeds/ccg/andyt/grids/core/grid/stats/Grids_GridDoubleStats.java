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
package uk.ac.leeds.ccg.andyt.grids.core.grid.stats;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeMap;
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkDouble;

/**
 * Used by Grids_GridDouble instances to access statistics. This class is to be
 * instantiated for Grids_GridDouble that keep statistic fields up to date as
 * the underlying data is changed. (Keeping statistic fields up to date as the
 * underlying data is changed can be expensive! Second order statistics like the
 * standard deviation would always require going through all the data again if
 * the values have changed.)
 */
public class Grids_GridDoubleStats
        extends Grids_AbstractGridNumberStats
        implements Serializable {

    /**
     * For storing the minimum value.
     */
    protected double Min;

    /**
     * For storing the maximum value.
     */
    protected double Max;

    protected Grids_GridDoubleStats() {
    }

    public Grids_GridDoubleStats(Grids_Environment ge) {
        super(ge);
        init();
    }

    public Grids_GridDoubleStats(Grids_GridDouble g) {
        super(g);
        init();
    }

    /**
     * For initialisation.
     */
    private void init() {
        Min = Double.MAX_VALUE;
        Max = -Double.MAX_VALUE;
        N = 0;
        Sum = BigDecimal.ZERO;
        NMin = 0;
        NMax = 0;
    }

    /**
     * @return true iff the stats are kept up to date as the underlying data
     * change.
     */
    @Override
    public boolean isUpdated() {
        return true;
    }

    /**
     *
     * @return (Grids_GridDouble) Grid
     */
    @Override
    public Grids_GridDouble getGrid() {
        return (Grids_GridDouble) Grid;
    }

    /**
     * Updates by going through all values in Grid.
     */
    @Override
    public void update() {
        ge.checkAndMaybeFreeMemory();
        init();
        Grids_GridDouble g = getGrid();
        BigDecimal valueBD;
        double value;
        double ndv = g.getNoDataValue();
        Grids_GridDoubleIterator ite;
        ite = g.iterator();
        while (ite.hasNext()) {
            value = (Double) ite.next();
            if (Double.isFinite(value)) {
                if (value != ndv) {
                    valueBD = new BigDecimal(value);
                    update(value, valueBD);
                }
            }
        }
    }

    protected void update(double value, BigDecimal valueBD) {
        N++;
        setSum(Sum.add(valueBD));
        if (value < Min) {
            NMin++;
            Min = value;
        } else {
            if (value == Min) {
                NMin = 1;
            }
        }
        if (value > Max) {
            NMax++;
            Max = value;
        } else {
            if (value == Max) {
                NMax++;
            }
        }
    }

    /**
     * For returning the minimum of all data values.
     *
     * @return
     */
    @Override
    public Double getMin(boolean update) {
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
     * For returning the maximum of all data values.
     *
     * @return
     */
    @Override
    public Double getMax(boolean update) {
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

    @Override
    public String getName() {
        return getClass().getName();
    }

    /**
     * @TODO Take advantage of the data structures of some types of chunk to
     * optimise this. Probably the best way to do this is to iterate over the
     * chunks and sum all the N from each chunk.
     * @return
     */
    public long getN() {
        long result = 0;
        Grids_GridDouble g = getGrid();
        Grids_GridDoubleIterator gIte;
        gIte = g.iterator();
        Iterator<Grids_2D_ID_int> ite;
        ite = gIte.getGridIterator();
        Grids_AbstractGridChunkDouble chunk;
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            chunkID = (Grids_2D_ID_int) ite.next();
            chunk = (Grids_AbstractGridChunkDouble) g.getChunk(chunkID);
            result += chunk.getN();
        }
//        double noDataValue;
//        noDataValue = g.getNoDataValue(ge.HOOME);
//        Iterator<Double> ite;
//        ite = g.iterator(ge.HOOME);
//        while (ite.hasNext()) {
//            double value = ite.next();
//            if (value != noDataValue) {
//                result ++;
//            }
//        }
        return result;
    }

    /**
     * @TODO Take advantage of the data structures of some types of chunk to
     * optimise this. Probably the best way to do this is to iterate over the
     * chunks and sum all the N from each chunk.
     * @return
     */
    @Override
    public BigInteger getNonZeroN() {
        BigInteger result = BigInteger.ZERO;
        Grids_GridDouble g = getGrid();
        double ndv = g.getNoDataValue();
        Iterator<Double> ite;
        ite = g.iterator(ge.HOOME);
        while (ite.hasNext()) {
            double value = ite.next();
            if (!(value == ndv || value == 0)) {
                if (Double.isFinite(value)) {
                    result = result.add(BigInteger.ONE);
                }
            }
        }
        return result;
    }

    /**
     *
     * @return
     */
    public BigDecimal getSum() {
        BigDecimal result = BigDecimal.ZERO;
        Grids_GridDouble g = getGrid();
        Grids_GridDoubleIterator gIte;
        gIte = g.iterator();
        Iterator<Grids_2D_ID_int> ite;
        ite = gIte.getGridIterator();
        Grids_AbstractGridChunkDouble chunk;
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            chunkID = (Grids_2D_ID_int) ite.next();
            chunk = (Grids_AbstractGridChunkDouble) g.getChunk(chunkID);
            result = result.add(chunk.getSum());
        }
        return result;
    }

    public BigDecimal getStandardDeviation(int numberOfDecimalPlaces) {
        BigDecimal stdev = BigDecimal.ZERO;
        BigDecimal mean = getArithmeticMean(numberOfDecimalPlaces * 2);
        BigDecimal dataValueCount = BigDecimal.ZERO;
        BigDecimal diffFromMean;
        Grids_GridDouble g = (Grids_GridDouble) Grid;
        double value;
        double ndv = g.getNoDataValue();
        Grids_GridDoubleIterator ite;
        ite = g.iterator();
        while (ite.hasNext()) {
            value = (Double) ite.next();
            if (value != ndv) {
                if (Double.isFinite(value)) {
                    diffFromMean = new BigDecimal(value).subtract(mean);
                    stdev = stdev.add(diffFromMean.multiply(diffFromMean));
                    dataValueCount = dataValueCount.add(BigDecimal.ONE);
                }
            }
        }
        if (dataValueCount.compareTo(BigDecimal.ONE) != 1) {
            return stdev;
        }
        stdev = stdev.divide(dataValueCount, numberOfDecimalPlaces, 
                BigDecimal.ROUND_HALF_EVEN);
        return Generic_BigDecimal.sqrt(stdev, numberOfDecimalPlaces,
                ge.get_Generic_BigDecimal().get_RoundingMode());
    }

    @Override
    public Object[] getQuantileClassMap(int nClasses) {
        Object[] result;
        result = new Object[3];
        Grids_GridDouble g = getGrid();
        TreeMap<Integer, Double> minDouble;
        TreeMap<Integer, Double> maxDouble;
        minDouble = new TreeMap<>();
        maxDouble = new TreeMap<>();
        for (int i = 1; i < nClasses; i++) {
            minDouble.put(i, Double.MAX_VALUE);
            maxDouble.put(i, -Double.MAX_VALUE);
        }
        result[0] = minDouble;
        result[1] = maxDouble;
        BigInteger nonZeroN;
        nonZeroN = getNonZeroN();
        long nonZeroNLong = nonZeroN.longValueExact();
        System.out.println("nonZeroAndNonNoDataValueCount " + nonZeroN);
        long numberOfValuesInEachClass;
        numberOfValuesInEachClass = nonZeroNLong / nClasses;
        if (nonZeroNLong % nClasses != 0) {
            numberOfValuesInEachClass += 1;
        }
        double noDataValue;
        noDataValue = g.getNoDataValue();
        TreeMap<Integer, Long> classCounts;
        classCounts = new TreeMap<>();
        for (int i = 1; i < nClasses; i++) {
            classCounts.put(i, 0L);
        }
        int classToFill = 0;
        boolean firstValue = true;
        TreeMap<Integer, TreeMap<Double, Long>> classMap;
        classMap = new TreeMap<>();
        for (int i = 0; i < nClasses; i++) {
            classMap.put(i, new TreeMap<>());
        }
        result[2] = classMap;
        int count = 0;
        //long valueID = 0;
        Iterator<Double> ite;
        ite = g.iterator(ge.HOOME);
        while (ite.hasNext()) {
            double value;
            value = ite.next();
            if (!(value == 0.0d || value == noDataValue)) {
                if (count % numberOfValuesInEachClass == 0) {
                    System.out.println(count + " out of " + nonZeroN);
                }
                count++;
                if (firstValue) {
                    minDouble.put(0, value);
                    maxDouble.put(0, value);
                    classCounts.put(0, 1L);
                    classMap.get(0).put(value, 1L);
                    if (numberOfValuesInEachClass < 2) {
                        classToFill = 1;
                    }
                    firstValue = false;
                } else {
                    int[] valueClass;
                    if (classToFill == nClasses) {
                        classToFill--;
                    }
                    valueClass = getValueClass(
                            value,
                            classMap,
                            minDouble,
                            maxDouble,
                            classCounts,
                            numberOfValuesInEachClass,
                            classToFill);
                    classToFill = valueClass[1];
                }
            }
        }
        return result;
    }
}
