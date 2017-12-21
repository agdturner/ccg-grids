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
import uk.ac.leeds.ccg.andyt.generic.math.Generic_BigDecimal;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridInt;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridIntIterator;
import uk.ac.leeds.ccg.andyt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;

/**
 * Used by Grids_GridInt instances to access statistics. This class is to be
 * instantiated for Grids_GridInt that keep statistic fields up to date as the
 * underlying data is changed. (Keeping statistic fields up to date as the
 * underlying data is changed can be expensive! Second order statistics like the
 * standard deviation would always require going through all the data again if
 * the values have changed.)
 */
public class Grids_GridIntStats
        extends Grids_AbstractGridNumberStats
        implements Serializable {

    /**
     * For storing the minimum value.
     */
    protected int Min;
    /**
     * For storing the maximum value.
     */
    protected int Max;

    protected Grids_GridIntStats() {
    }

    public Grids_GridIntStats(Grids_Environment ge) {
        super(ge);
        init();
    }

    public Grids_GridIntStats(Grids_GridInt g) {
        super(g);
        init();
    }

    /**
     * For initialisation.
     */
    private void init() {
        //getGrid().initStats(this);
        Min = Integer.MIN_VALUE;
        Max = Integer.MAX_VALUE;
    }

    /**
     * @return true iff the stats are kept up to date as the underlying
     * data change.
     */
    @Override
    public boolean isUpdated() {
        return true;
    }

    /**
     *
     * @return (Grids_GridInt) Grid
     */
    @Override
    public Grids_GridInt getGrid() {
        return (Grids_GridInt) Grid;
    }

    /**
     * Updates by going through all values in Grid if the fields are likely not
     * be up to date.
     */
    @Override
    public void update() {
        ge.checkAndMaybeFreeMemory();
        Grids_GridInt g = getGrid();
        BigDecimal valueBD;
        int value;
        int noDataValue = g.getNoDataValue();
        Grids_GridIntIterator ite;
        ite = g.iterator();
        while (ite.hasNext()) {
            value = (Integer) ite.next();
            if (value != noDataValue) {
                valueBD = new BigDecimal(value);
                update(value, valueBD);
            }
        }
    }

    protected void update(int value, BigDecimal valueBD) {
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
    public Integer getMin(boolean update) {
        if (getNMin() < 1) {
            if (update) {
                update();
            }
        }
        return Min;
    }

    public void setMin(int min) {
        Min = min;
    }

    /**
     * For returning the maximum of all data values.
     *
     * @return
     */
    @Override
    public Integer getMax(boolean update) {
        if (getNMax() < 1) {
            if (update) {
                update();
            }
        }
        return Max;
    }

    public void setMax(int max) {
        Max = max;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    /**
     * @return
     */
    public long getN() {
        long result = 0;
        Grids_GridInt g = getGrid();
        Grids_GridIntIterator gIte;
        gIte = g.iterator();
        Iterator<Grids_2D_ID_int> ite;
        ite = gIte.getGridIterator();
        Grids_AbstractGridChunkInt chunk;
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            chunkID = (Grids_2D_ID_int) ite.next();
            chunk = (Grids_AbstractGridChunkInt) g.getChunk(chunkID);
            result += chunk.getN();
        }
//        int noDataValue;
//        noDataValue = g.getNoDataValue(ge.HOOME);
//        Iterator<Integer> ite;
//        ite = g.iterator(ge.HOOME);
//        while (ite.hasNext()) {
//            int value = ite.next();
//            if (value != noDataValue) {
//                result += 1;
//            }
//        }
        return result;
    }

    /**
     * @return
     */
    @Override
    protected BigInteger getNonZeroN() {
        BigInteger result = BigInteger.ZERO;
        Grids_GridInt g = getGrid();
//        Grids_GridIntIterator gridIntIterator;
//        gridIntIterator = new Grids_GridIntIterator(g);
//        Grids_AbstractGridChunkNumberRowMajorOrderIterator chunkIterator;
//        chunkIterator = gridIntIterator.getChunkIterator();
//        Grids_AbstractGridChunkInt chunkDouble;
//        while (chunkIterator.hasNext()) {
//            chunkDouble = (Grids_AbstractGridChunkInt) chunkIterator.next();
//            result = result.add(
//                    chunkDouble.getNonZeroN(ge.HOOME));
//        }
        int noDataValue;
        noDataValue = g.getNoDataValue(ge.HOOME);
        Iterator<Integer> ite;
        ite = g.iterator(ge.HOOME);
        while (ite.hasNext()) {
            int value = ite.next();
            if (!(value == noDataValue || value == 0)) {
                result = result.add(BigInteger.ONE);
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
        Grids_GridInt g = getGrid();
        Grids_GridIntIterator gIte;
        gIte = g.iterator();
        Iterator<Grids_2D_ID_int> ite;
        ite = gIte.getGridIterator();
        Grids_AbstractGridChunkInt chunk;
        Grids_2D_ID_int chunkID;
        while (ite.hasNext()) {
            chunkID = (Grids_2D_ID_int) ite.next();
            chunk = (Grids_AbstractGridChunkInt) g.getChunk(chunkID);
            result = result.add(chunk.getSum());
        }
//        int noDataValue;
//        noDataValue = g.getNoDataValue(ge.HOOME);
//        Iterator<Integer> ite;
//        ite = g.iterator(ge.HOOME);
//        while (ite.hasNext()) {
//            int value = ite.next();
//            if (value != noDataValue) {
//                result = result.add(BigDecimal.valueof(value));
//            }
//        }
        return result;
    }

    protected BigDecimal getStandardDeviation(int numberOfDecimalPlaces) {
        BigDecimal stdev = BigDecimal.ZERO;
        BigDecimal mean = getArithmeticMean(numberOfDecimalPlaces * 2);
        BigDecimal dataValueCount = BigDecimal.ZERO;
        BigDecimal differenceFromMean;
        Grids_GridInt g = (Grids_GridInt) Grid;
        int value;
        int noDataValue = g.getNoDataValue(ge.HOOME);
        Grids_GridIntIterator ite;
        ite = g.iterator();
        while (ite.hasNext()) {
            value = (Integer) ite.next();
            if (value != noDataValue) {
                differenceFromMean = new BigDecimal(value).subtract(mean);
                stdev = stdev.add(differenceFromMean.multiply(differenceFromMean));
                dataValueCount = dataValueCount.add(BigDecimal.ONE);
            }
        }
        if (dataValueCount.compareTo(BigDecimal.ONE) != 1) {
            return stdev;
        }
        stdev = stdev.divide(
                dataValueCount,
                numberOfDecimalPlaces,
                BigDecimal.ROUND_HALF_EVEN);
        return Generic_BigDecimal.sqrt(
                stdev,
                numberOfDecimalPlaces,
                ge.get_Generic_BigDecimal().get_RoundingMode());
    }

    @Override
    public Object[] getQuantileClassMap(int nClasses) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
