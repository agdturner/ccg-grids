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
package uk.ac.leeds.ccg.agdt.grids.core.grid.stats;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;
import java.util.Iterator;
import uk.ac.leeds.ccg.agdt.math.Math_BigDecimal;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_2D_ID_int;
import uk.ac.leeds.ccg.agdt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridInt;
import uk.ac.leeds.ccg.agdt.grids.core.grid.Grids_GridIntIterator;
import uk.ac.leeds.ccg.agdt.grids.core.grid.chunk.Grids_AbstractGridChunkInt;

/**
 * Used by Grids_GridInt instances to access statistics. This class is to be
 * instantiated for Grids_GridInt that keep statistic fields up to date as the
 * underlying data is changed. (Keeping statistic fields up to date as the
 * underlying data is changed can be expensive! Second order statistics like the
 * standard deviation would always require going through all the data again if
 * the values have changed.)
*
 * @author Andy Turner
 * @version 1.0.0
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

    public Grids_GridIntStats(Grids_Environment ge) {
        super(ge);
        Min = Integer.MAX_VALUE;
        Max = Integer.MIN_VALUE;
        Sum = BigDecimal.ZERO;
    }

    @Override
    protected void init(){
        super.init();
        Min = Integer.MAX_VALUE;
        Max = Integer.MIN_VALUE;
        Sum = BigDecimal.ZERO;
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
     * @return (Grids_GridInt) grid
     */
    @Override
    public Grids_GridInt getGrid() {
        return (Grids_GridInt) grid;
    }

    /**
     * Updates by going through all values in grid.
     */
    @Override
    public void update() {
        env.checkAndMaybeFreeMemory();
        init();
        Grids_GridInt g = getGrid();
        BigDecimal valueBD;
        int value;
        int ndv = g.getNoDataValue();
        Grids_GridIntIterator ite;
        ite = g.iterator();
        while (ite.hasNext()) {
            value = (Integer) ite.next();
            if (value != ndv) {
                valueBD = new BigDecimal(value);
                update(value, valueBD);
            }
        }
    }

    protected void update(int value, BigDecimal valueBD) {
        n++;
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
        if (NMin < 1) {
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
        if (NMax < 1) {
            if (update) {
                update();
            }
        }
        return Max;
    }

    public void setMax(int max) {
        Max = max;
    }

    /**
     * @TODO Take advantage of the data structures of some types of chunk to
     * optimise this. Probably the best way to do this is to iterate over the
     * chunks and sum all the N from each chunk.
     * @return
     */
    @Override
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
//        noDataValue = g.getNoDataValue(env.HOOME);
//        Iterator<Integer> ite;
//        ite = g.iterator(env.HOOME);
//        while (ite.hasNext()) {
//            int value = ite.next();
//            if (value != noDataValue) {
//                result += 1;
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
        Grids_GridInt g = getGrid();
        int ndv = g.getNoDataValue();
        Iterator<Integer> ite;
        ite = g.iterator(env.HOOME);
        while (ite.hasNext()) {
            int value = ite.next();
            if (!(value == ndv || value == 0)) {
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
        return result;
    }

    protected BigDecimal getStandardDeviation(int numberOfDecimalPlaces) {
        BigDecimal stdev = BigDecimal.ZERO;
        BigDecimal mean = getArithmeticMean(numberOfDecimalPlaces * 2);
        BigDecimal dataValueCount = BigDecimal.ZERO;
        BigDecimal diffFromMean;
        Grids_GridInt g = (Grids_GridInt) grid;
        int value;
        int noDataValue = g.getNoDataValue();
        Grids_GridIntIterator ite;
        ite = g.iterator();
        while (ite.hasNext()) {
            value = (Integer) ite.next();
            if (value != noDataValue) {
                diffFromMean = new BigDecimal(value).subtract(mean);
                stdev = stdev.add(diffFromMean.multiply(diffFromMean));
                dataValueCount = dataValueCount.add(BigDecimal.ONE);
            }
        }
        if (dataValueCount.compareTo(BigDecimal.ONE) != 1) {
            return stdev;
        }
        stdev = stdev.divide(dataValueCount, numberOfDecimalPlaces,
                BigDecimal.ROUND_HALF_EVEN);
        return Math_BigDecimal.sqrt(stdev, numberOfDecimalPlaces,
                env.bd.getRoundingMode());
    }

    @Override
    public Object[] getQuantileClassMap(int nClasses) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
