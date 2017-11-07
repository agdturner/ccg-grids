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
package uk.ac.leeds.ccg.andyt.grids.core.grid.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;
import java.util.Iterator;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDouble;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_GridDoubleIterator;

/**
 * Used by Grids_GridDouble instances to access statistics. This class is to be
 * instantiated for Grids_GridDouble that keep statistic fields up to date as
 * the underlying data is changed. (Keeping statistic fields up to date as the
 * underlying data is changed can be expensive! Second order statistics like the
 * standard deviation would always require going through all the data again if
 * the values have changed.)
 */
public class Grids_GridDoubleStatistics
        extends Grids_AbstractGridDoubleStatistics
        implements Serializable {

    /**
     * For storing the minimum value.
     */
    /**
     * For storing the maximum value.
     */
    protected Grids_GridDoubleStatistics() {
    }

    public Grids_GridDoubleStatistics(Grids_Environment ge) {
        super(ge);
    }

    /**
     * Creates a new instance of GridStatistics0
     *
     * @param g
     */
    public Grids_GridDoubleStatistics(
            Grids_AbstractGridNumber g) {
        super(g);
    }

    /**
     * Updates fields (statistics) by going through all values in Grid if they
     * might not be up to date.
     */
    @Override
    public void update() {
        ge.tryToEnsureThereIsEnoughMemoryToContinue(ge.HandleOutOfMemoryError);
        Grids_GridDouble g = getGrid();
        BigDecimal valueBD;
        double value;
        double noDataValue = g.getNoDataValue(ge.HandleOutOfMemoryError);
        Grids_GridDoubleIterator ite;
        ite = new Grids_GridDoubleIterator(g);
        while (ite.hasNext()) {
            value = (Double) ite.next();
            if (!Double.isNaN(value)) {
                if (value != noDataValue) {
                    valueBD = new BigDecimal(value);
                    update(value, valueBD);
                }
            }
        }
    }

    protected void update(double value, BigDecimal valueBD) {
        if (Double.isFinite(value)) {
            setN(N.add(BigInteger.ONE));
            setSum(Sum.add(valueBD));
        }
        if (value < Min) {
            setNMin(BigInteger.ONE);
            Min = value;
        } else {
            if (value == Min) {
                setNMin(getNMin().add(BigInteger.ONE));
            }
        }
        if (value > Max) {
            setNMax(BigInteger.ONE);
            Max = value;
        } else {
            if (value == Max) {
                setNMax(getNMax().add(BigInteger.ONE));
            }
        }
    }

    /**
     * For returning the minimum of all data values.
     *
     * @return
     */
    @Override
    protected Double getMin(boolean update) {
        if (getNMin().compareTo(BigInteger.ONE) == -1) {
            if (update) {
                update();
            }
        }
        return Min;
    }

    /**
     * For returning the maximum of all data values.
     *
     * @return
     */
    @Override
    protected Double getMax(boolean update) {
        if (getNMax().compareTo(BigInteger.ONE) == -1) {
            if (update) {
                update();
            }
        }
        return Max;
    }

    @Override
    protected String getName() {
        return getClass().getName();
    }

    /**
     * @TODO Take advantage of the data structures of some types of chunk to
     * optimise this. Probably the best way to do this is to iterate over the
     * chunks and sum all the N from each chunk.
     * @return
     */
    @Override
    protected BigInteger getN() {
        BigInteger result = BigInteger.ZERO;
        Grids_GridDouble g = getGrid();
        double noDataValue;
        noDataValue = g.getNoDataValue(ge.HandleOutOfMemoryError);
//        
//        g.getChunkIDChunkMap();
//        
//        
//        Grids_GridIntIterator ite;
//        ite = g.iterator();
//        ite.GridIterator.
//        
//        ite.getChunkIterator()
//        
//        
        Iterator<Integer> ite;
        ite = g.iterator(ge.HandleOutOfMemoryError);
        while (ite.hasNext()) {
            int value = ite.next();
            if (value != noDataValue) {
                result = result.add(BigInteger.ONE);
            }
        }
        return result;
    }

    /**
     * @TODO Take advantage of the data structures of some types of chunk to
     * optimise this. Probably the best way to do this is to iterate over the
     * chunks and sum all the N from each chunk.
     * @return
     */
    @Override
    protected BigInteger getNonZeroN() {
        BigInteger result = BigInteger.ZERO;
        Grids_GridDouble g = getGrid();
        double noDataValue;
        noDataValue = g.getNoDataValue(ge.HandleOutOfMemoryError);
        Iterator<Double> ite;
        ite = g.iterator(ge.HandleOutOfMemoryError);
        while (ite.hasNext()) {
            double value = ite.next();
            if (!(value == noDataValue || value == 0)) {
                if (!Double.isNaN(value)) {
                    if (Double.isFinite(value)) {
                        result = result.add(BigInteger.ONE);
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected BigDecimal getSum() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected BigDecimal getStandardDeviation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public Object[] getQuantileClassMap(int nClasses) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
