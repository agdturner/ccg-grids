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
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Environment;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGridNumber;

/**
 * Used by Grids_AbstractGridNumber instances to access statistics. This class
 * is to be instantiated for Grids_AbstractGridNumber that keep statistic fields
 * up to date as the underlying data is changed. (Keeping statistic fields up to
 * date as the underlying data is changed can be expensive! Second order
 * statistics like the standard deviation would always require going through all
 * the data again if the values have changed.)
 */
public class Grids_GridStatistics
        extends Grids_AbstractStatisticsBigDecimal
        implements Serializable {

    //private static final long serialVersionUID = 1L; 
    /**
     * Creates a new instance of GridStatistics0
     */
    protected Grids_GridStatistics() {
    }

    public Grids_GridStatistics(Grids_Environment ge) {
        super(ge);
    }

    /**
     * Creates a new instance of GridStatistics0
     *
     * @param g
     */
    public Grids_GridStatistics(
            Grids_AbstractGridNumber g) {
        super(g);
    }

    /**
     * Updates fields (statistics) by going through all values in Grid if they
     * might not be up to date.
     */
    public @Override
    void update() {
        init();
        super.update();
    }

    /**
     * For returning the number of cells with noDataValues as a BigInteger
     *
     * @return
     */
    @Override
    protected BigInteger getNonNoDataValueCount() {
        return NonNoDataValueCount;
    }

    /**
     * For returning the sum of all non noDataValues as a BigDecimal
     *
     * @return
     */
    @Override
    protected BigDecimal getSum() {
        return Sum;
    }

    /**
     * For returning the minimum of all non noDataValues as a BigDecimal
     *
     * @return
     */
    @Override
    protected BigDecimal getMin(boolean update) {
        if (getMinCount().compareTo(BigInteger.ONE) == -1) {
            if (update) {
                update();
            }
        }
        return Min;
    }

    /**
     * For returning the maximum of all non noDataValues as a BigDecimal.
     *
     * @return
     */
    @Override
    protected BigDecimal getMax(boolean update) {
        if (getMaxCount().compareTo(BigInteger.ONE) == -1) {
            if (update) {
                update();
            }
        }
        return Max;
    }

    /**
     * For returning the arithmetic mean of all non noDataValues as a BigDecimal
     * Throws an ArithmeticException if NonNoDataValueCount is equal to zero.
     *
     * @return
     */
    protected @Override
    BigDecimal getArithmeticMean() {
        return Sum.divide(new BigDecimal(NonNoDataValueCount),
                NumberOfDecimalPlacesForArithmeticMean,
                BigDecimal.ROUND_HALF_EVEN);
    }

}
